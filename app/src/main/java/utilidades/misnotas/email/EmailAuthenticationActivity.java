package utilidades.misnotas.email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilidades.misnotas.BuildConfig;
import utilidades.misnotas.MainActivity;
import utilidades.misnotas.R;
import utilidades.misnotas.utils.KeyboardUtil;
import utilidades.misnotas.utils.LocalData;
import utilidades.misnotas.utils.NetworkUtil;

import static utilidades.misnotas.utils.LocalData.TEMP_EMAIL_ID;
import static utilidades.misnotas.utils.LocalData.USER_ID;


public class EmailAuthenticationActivity extends AppCompatActivity {


    private static final String TAG = "EmailAuthenticationActivity";
    private FirebaseAuth mAuth;
    private ActionCodeSettings actionCodeSettings;
    private FirebaseUser user;

    private EditText emailEt;
    private Button enviarB;
    private View v;
    Context context;
    Activity activity;
    LocalData localData;

    private String emailID = null, emailLink = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_authentication);
        activity = this;
        localData = new LocalData(getApplicationContext());

        emailEt = findViewById(R.id.emailET);
        enviarB = findViewById(R.id.enviarEmailB);

        //Si hemos cerrado sesion, inicializamos user_id a ''
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
            localData.setString(USER_ID,"");

        //Iniciamos envio email
        initFirebaseAuthentication();
        escuchadorEnviarB();

        //Al regresar despues de validar email, escuchamos
        emailID = TEMP_EMAIL_ID;
        if(emailID != "")
            escuchadorDynamicLink();

    }

    // Initialize Firebase Auth
    private void initFirebaseAuthentication() {
        mAuth = FirebaseAuth.getInstance();
        actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        .setUrl("https://misnotas-b6c56.firebaseapp.com/finishSignUp")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(BuildConfig.APPLICATION_ID, true, null)
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
                    Snackbar.make(v, emailID + ", NO es un email válido.", Snackbar.LENGTH_LONG)
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
                            localData.setString(TEMP_EMAIL_ID,emailID);

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
                        // Get deep link from result (may be null if no link is found)
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
            mAuth.signInWithEmailLink(emailID, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && task.getResult().getUser() != null) {
                                    user = task.getResult().getUser();
                                    if (!TextUtils.isEmpty(user.getUid()))
                                        localData.setString(USER_ID,user.getUid());
                                        Intent intent = new Intent(activity, MainActivity.class);
                                        startActivity(intent);
                                }
                            } else {
                               NetworkUtil.isNetworkAvailable(context,true);
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
