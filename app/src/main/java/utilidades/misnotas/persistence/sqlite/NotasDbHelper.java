package utilidades.misnotas.persistence.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import utilidades.misnotas.model.Nota;

import static utilidades.misnotas.persistence.sqlite.NotasContract.*;

public  class NotasDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 35;
    public  static final String DATABASE_NAME = "notas.db";

    public NotasDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Table
        db.execSQL("CREATE TABLE " + NotaEntry.TABLE_NAME + " ("
                + NotaEntry._ID + "  INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NotaEntry.ID + " TEXT NOT NULL UNIQUE,"
        + NotaEntry.USER_ID + " TEXT NOT NULL,"
        + NotaEntry.TITULO + " TEXT NOT NULL,"
        + NotaEntry.CONTENIDO + " TEXT NOT NULL"
                +")"
      );

    }



    public  long save(Nota nota) throws SQLiteConstraintException {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(NotaEntry.TABLE_NAME,null, nota.toContentValues());
    }

    public int update(Nota nota) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String[] ids = {nota.getId()};
        return sqLiteDatabase.update(NotaEntry.TABLE_NAME,nota.toContentValues(),"id = ?", ids);
    }

    public int delete(String id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String[] ids = {id};
        return sqLiteDatabase.delete(NotaEntry.TABLE_NAME,"id = ?", ids);
    }

    public ArrayList<Nota> getAll(String user_id) {
        ArrayList<Nota> notas = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] parametros = {NotaEntry._ID,NotaEntry.ID,NotaEntry.USER_ID,NotaEntry.TITULO,NotaEntry.CONTENIDO};
        String whereClause = "user_id = ?";
        String[] tableColumns = new String[] {user_id};
        String orderBy = "titulo";
        try {
            Cursor cursor = sqLiteDatabase.query(
                    NotaEntry.TABLE_NAME,
                    parametros, whereClause, tableColumns ,null, null, orderBy
            );
            if(cursor == null) Log.e("Sqlite:"," Error, al obtener el metodo getAll()");
            if(!cursor.moveToFirst()) Log.d("Sqlite:", " No existen datos");
            else
                do {
                    Nota nota = new Nota(
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4)
                    );
                    notas.add(nota);
                } while (cursor.moveToNext());
            cursor.close();
        } catch (IllegalArgumentException e) {}

        return notas;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotaEntry.TABLE_NAME);
        onCreate(db);
    }
}
