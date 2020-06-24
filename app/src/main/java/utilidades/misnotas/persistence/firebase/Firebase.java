package utilidades.misnotas.persistence.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import utilidades.misnotas.model.Nota;

public abstract class Firebase {

    private static String DATABASE = "notas";
    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(DATABASE);
    public static void update(String id,Nota nota) {
        databaseReference.child(id).setValue(nota);
    }
    public static Task<Void> removeId(int id) {return databaseReference.child(String.valueOf(id)).removeValue();}

}
