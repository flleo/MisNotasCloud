package utilidades.misnotas.email;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilidades.misnotas.MainActivity;
import utilidades.misnotas.R;
import utilidades.misnotas.model.Nota;
import utilidades.misnotas.persistence.firebase.Firebase;
import utilidades.misnotas.persistence.sqlite.NotasDbHelper;
import utilidades.misnotas.utils.EncriptaDesencriptaAES;
import utilidades.misnotas.utils.KeyboardUtil;
import utilidades.misnotas.utils.LocalData;
import utilidades.misnotas.utils.NetworkUtil;

import static utilidades.misnotas.utils.LocalData.EMAIL_ID;
import static utilidades.misnotas.utils.LocalData.USER_ID;
import static utilidades.misnotas.utils.LocalData.USER_IDs;


public class EmailAuthenticationActivity extends AppCompatActivity {


    private static final String TAG = "EmailAuthenticationActivity";
    private FirebaseAuth mAuth;
    private ActionCodeSettings actionCodeSettings;
    LocalData localData;
    private EditText emailEt;
    private Button enviarB;
    private String emailID = null, emailLink = null;
    EncriptaDesencriptaAES encriptaDesencriptaAES = new EncriptaDesencriptaAES();
    NotasDbHelper notasDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_authentication);

        emailEt = findViewById(R.id.emailET);
        enviarB = findViewById(R.id.enviarEmailB);

        localData = new LocalData(getApplicationContext());
        notasDbHelper = new NotasDbHelper(this);

        if(USER_ID == "" && EMAIL_ID != null) {
            escuchadorDynamicLink();
        } else if(EMAIL_ID == null){
            //Iniciamos envio email
            initFirebaseAuthentication();
            escuchadorEnviarB();
        }
    }

    // Initialize Firebase Auth
    private void initFirebaseAuthentication() {
        mAuth = FirebaseAuth.getInstance();
        actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://misnotas-b6c56.firebaseapp.com/")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName("utilidades.misnotas", true, null)
                        .build();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //Boton enviar email
    private void escuchadorEnviarB() {
        enviarB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailID = emailEt.getText().toString();
                if (!TextUtils.isEmpty(emailID) && isEmailValid(emailID)) {
                    iniciamosEnvioEmail();
                    KeyboardUtil.hideSoftKeyboard(EmailAuthenticationActivity.this);
                    Snackbar.make(v, "Enviando email...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                } else {
                    Snackbar.make(v, emailID + " NO es un email válido.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    //Envio del email
    private void iniciamosEnvioEmail() {
        mAuth.sendSignInLinkToEmail(emailID, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(getCurrentFocus(), "Compruebe su email!", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                            EMAIL_ID = emailID;
                        } else {
                            Objects.requireNonNull(task.getException()).printStackTrace();
                            Snackbar.make(getCurrentFocus(), emailID + ", NO se envió, compruebe su acceso a internet.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
    }

    private void escuchadorDynamicLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            emailLink = deepLink.toString();
                            escuchadorVerificacionEmailDelCliente();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void escuchadorVerificacionEmailDelCliente() {
        try {
            FirebaseAuth.getInstance().signInWithEmailLink(EMAIL_ID, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && task.getResult().getUser() != null) {
                                    FirebaseUser user = task.getResult().getUser();
                                    if (!TextUtils.isEmpty(user.getUid())){
                                        USER_ID = user.getUid();
                                        localData.setString(USER_IDs, USER_ID);

                                        Intent intent = new Intent(getApplication(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            } else {
                                NetworkUtil.isNetworkAvailable(getApplicationContext(),true);
                            }
                        }
                    });
        } catch (IllegalArgumentException e) {
            Snackbar.make(getCurrentFocus(), "Verifique su correo!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }



    @Override
    public void onBackPressed (){
        //Nothing to do
    }
}
