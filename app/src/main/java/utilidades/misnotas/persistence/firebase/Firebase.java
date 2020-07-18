package utilidades.misnotas.persistence.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import utilidades.misnotas.model.Nota;

public abstract class Firebase {

    private static String DATABASE = "notas";
    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(DATABASE);
    public static String push() {return  databaseReference.push().getKey();}
    public static void update(Nota nota) {
        databaseReference.child(nota.getUser_id()).child(nota.getId()).setValue(nota);
    }
    public static Task<Void> removeId(Nota nota) {return databaseReference.child(nota.getUser_id()).child(nota.getId()).removeValue();}



}
