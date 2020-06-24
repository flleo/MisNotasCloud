package utilidades.misnotas.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import utilidades.misnotas.R;
import utilidades.misnotas.email.EmailAuthenticationActivity;
import utilidades.misnotas.model.Nota;
import utilidades.misnotas.persistence.firebase.Firebase;
import utilidades.misnotas.persistence.sqlite.NotasDbHelper;
import utilidades.misnotas.utils.EncriptaDesencriptaAES;
import utilidades.misnotas.utils.LocalData;

import static utilidades.misnotas.utils.LocalData.USER_ID;
import static utilidades.misnotas.utils.LocalData.TEMP_EMAIL_ID;

public class ListaFragment extends Fragment {
    ArrayAdapter adaptador = null;
    ArrayList<Nota> notas = new ArrayList<>();
    Nota nota = new Nota();
    EncriptaDesencriptaAES encriptaDesencriptaAES = new EncriptaDesencriptaAES();
    LocalData localData;

    ListView notasLV;
    Button nuevaNotaB;
    NotasDbHelper notasDbHelper;
    boolean vacio = false;
    String user_id = "", email_id = "";
    ListaFragment listaFragment;
    private ArrayList<Nota> notasF = new ArrayList<>();
    AlertDialog _dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listaFragment = this;
        notasDbHelper = new NotasDbHelper(getContext());
        localData = new LocalData(getContext());

        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    public void onViewCreated(@NonNull View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Recogemos las vistas
        notasLV = view.findViewById(R.id.notasLV);
        nuevaNotaB = view.findViewById(R.id.nuevaNotaB);

        user_id = localData.getString(USER_ID);
        email_id = localData.getString(TEMP_EMAIL_ID);


        if (user_id.equals("salir")) {
            Intent intent = new Intent(getActivity(), EmailAuthenticationActivity.class);
            startActivity(intent);
        } else {
            nota.setUser_id(user_id);
            upload();
        }

        //Llamamos nueva nota
        nuevaNotaB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    NavHostFragment.findNavController(ListaFragment.this)
                            .navigate(R.id.action_ListaFragment_to_NotaFragment);
                } catch (IllegalStateException e) {
                    Log.e("Error nuevaNota(), ", e.getMessage());
                }
            }
        });

        //lista onclick la mandamos a editar
        notasLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Nota nota = (Nota) notasLV.getItemAtPosition(position);
                    Bundle data = new Bundle();
                    data.putInt("_id", nota.get_id());
                    data.putString("user_id", nota.getUser_id());
                    data.putString("titulo", nota.getTitulo());
                    data.putString("contenido", notas.get(position).getContenido());
                    NavHostFragment.findNavController(ListaFragment.this)
                            .navigate(R.id.action_ListaFragment_to_NotaFragment, data);
                } catch (IllegalStateException e) {
                    Log.e("Error notasLV.listener: ", e.getMessage());
                }
            }
        });

        //Mantenemos presionado para eliminar nota
        notasLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Nota nota = (Nota) notasLV.getItemAtPosition(position);
                borrarCompartir(nota, view);
                return true;
            }
        });
    }

    public void upload() {
        notas = notasDbHelper.getAll(nota.getUser_id());
        adaptador();
        if (notas.size() == 0) {
            notasF.clear();
            activaSaveFirebase();
            int i = 0;
            if(notasF.size() > 0){
                for(Nota n: notasF) {
                    notasDbHelper.update(n);
                    i++;
                }
                if(i == notasF.size())   upload();
            }
        }
    }

    private void activaSaveFirebase() {
        Firebase.databaseReference.orderByChild("user_id").equalTo(nota.getUser_id()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null) {
                    Nota nota = encriptaDesencriptaAES.desencriptaAES(dataSnapshot.getValue(Nota.class));
                    notasF.add(nota);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

    private void borrarCompartir(Nota nota1, final View view) {
        final Nota nota = nota1;
        final String[] campos = {"Borrar", "Compartir"};
        final int[] selected = new int[]{99};
        final boolean[] clickedItems = {false, false, false};

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Envio/Borrado de notas")
                .setMultiChoiceItems(campos, clickedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked){
                            _dialog.dismiss();
                            switch (which) {
                                case 0:
                                    builder.setIcon(android.R.drawable.ic_delete)
                                            .setTitle(R.string.titleborrar)
                                            .setItems(null, null)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (notasDbHelper.delete(nota.get_id()) > 0) {
                                                        Snackbar.make(getView(), "La nota ha sido eliminada", Snackbar.LENGTH_SHORT)
                                                                .setAction("Action", null).show();
                                                        upload();
                                                        Firebase.removeId(nota.get_id());
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                    break;
                                case 1:
                                    Intent intentE = new Intent(Intent.ACTION_SEND);
                                    intentE.setType("text/plain");
                                    intentE.putExtra(Intent.EXTRA_EMAIL, new String[]{email_id}); // recipients
                                    intentE.putExtra(Intent.EXTRA_SUBJECT, "MisNotasCloud - " + nota.getTitulo());
                                    intentE.putExtra(Intent.EXTRA_TEXT, "\n" + nota.getTitulo() + "\n\n" + nota.getContenido());
                                    //Ver si el dispositivo tiene alguna aplicacion de envio
                                    List<ResolveInfo> activities = getContext().getPackageManager().queryIntentActivities(intentE, PackageManager.MATCH_DEFAULT_ONLY);
                                    if (activities.size() > 0) {
                                        startActivity(Intent.createChooser(intentE,"Enviar mediante:"));
                                    } else {
                                        Snackbar.make(getView(), "Debes tener una app, de envio de datos, en tu dispositivo", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                    break;
                            }
                        }
                    }
                });
        _dialog = builder.show();
    }



    private void adaptador() {
        adaptador = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, notas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setTypeface(null, Typeface.BOLD);
                text1.setTextSize(18);

                text1.setText(notas.get(position).getTitulo());
                text2.setText(contenido(position));
                return view;
            }
        };
        notasLV.setAdapter(adaptador);
    }

    private String contenido(int position) {
        String contenido = (notas.get(position).getContenido().trim());
        int size = contenido.length();
        if(size > 100)
            contenido = contenido.substring(0,100);

        return (contenido);
    }


}