package utilidades.misnotas.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import utilidades.misnotas.R;
import utilidades.misnotas.model.Nota;
import utilidades.misnotas.persistence.firebase.Firebase;
import utilidades.misnotas.persistence.sqlite.NotasDbHelper;
import utilidades.misnotas.utils.EncriptaDesencriptaAES;
import utilidades.misnotas.utils.KeyboardUtil;

import static androidx.core.content.ContextCompat.getSystemService;
import static utilidades.misnotas.R.layout.fragment_nota;
import static utilidades.misnotas.utils.LocalData.USER_ID;

public class NotaFragment extends Fragment {

    //vistas
    Button misNotasB;
    EditText tituloET;
    EditText contenidoET;

    EncriptaDesencriptaAES encriptaDesencriptaAES = new EncriptaDesencriptaAES();
    Bundle bundle;
    NotasDbHelper notasDbHelper;
    Nota nota = new Nota();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        notasDbHelper = new NotasDbHelper(getContext());
        nota.setUser_id(USER_ID);        //Para nueva nota

        return inflater.inflate(fragment_nota, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //CArgamso vistas
        misNotasB = view.findViewById(R.id.misNotasB);
        tituloET = view.findViewById(R.id.tituloET);
        contenidoET = view.findViewById(R.id.contenidoET);


        //Si hay datos los recogemos
        bundle = this.getArguments();
        if (bundle != null) {
            nota.setId(bundle.getString("id"));
            nota.setUser_id(bundle.getString("user_id"));
            tituloET.setText(bundle.getString("titulo"));
            contenidoET.setText(bundle.getString("contenido"));

        }



        misNotasB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                String titulo1 = tituloET.getText().toString().trim();
                String contenido2 = contenidoET.getText().toString().trim();
                if (titulo1.length() > 0 || contenido2.length() > 0) {
                    KeyboardUtil.hideSoftKeyboard(getActivity());
                    nota.setTitulo(primaraMayuscula(titulo1));
                    nota.setContenido(primaraMayuscula(contenido2));
                    Nota notaE = encriptaDesencriptaAES.encriptacionAES(nota);
                    if (bundle != null) {
                        //Actualizamos
                        Log.e("actualizado id", String.valueOf(notaE.getId()));
                        Firebase.update(notaE);
                        if(notasDbHelper.update(nota) > 0){
                            Snackbar.make(view, "La nota se actualizó", Snackbar.LENGTH_SHORT)
                                    .setAction("", null).show();
                        }
                    } else {
                        //Nueva nota
                        String id;
                        id = Firebase.push();
                        Log.e("id", id);
                        notaE.setId(id);
                        Firebase.update(notaE);
                        nota.setId(id);
                        if (notasDbHelper.save(nota) != -1) {
                            Snackbar.make(view, "La nota fue añadida ", Snackbar.LENGTH_SHORT).setAction("", null).show();
                        }
                    }
                }
                //Cambiamos a la vista listado
                NavHostFragment.findNavController(NotaFragment.this)
                        .navigate(R.id.action_NotaFragment_to_ListaFragment, null);




            }
        });

    }



    private String primaraMayuscula(String titulo1) {
        String titulo2 = "";
        try {
            char T = titulo1.charAt(0);
            titulo2 = String.valueOf(T).toUpperCase();
            for (int i = 1; i < titulo1.length(); i++)
                titulo2 += titulo1.charAt(i);
        } catch (StringIndexOutOfBoundsException u) {
        }
        return titulo2;
    }

}
