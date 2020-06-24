package utilidades.misnotas.persistence.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import utilidades.misnotas.model.Nota;

import static utilidades.misnotas.persistence.sqlite.NotasContract.*;

public  class NotasDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 17;
    public  static final String DATABASE_NAME = "notas.db";

    public NotasDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Table
        db.execSQL("CREATE TABLE " + NotaEntry.TABLE_NAME + " ("
                + NotaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + NotaEntry.USER_ID + " TEXT NOT NULL,"
        + NotaEntry.TITULO + " TEXT NOT NULL,"
        + NotaEntry.CONTENIDO + " TEXT NOT NULL"
                +")"
      );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotaEntry.TABLE_NAME);
        onCreate(db);
    }

    public  long save(Nota nota) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        return sqLiteDatabase.insert(NotaEntry.TABLE_NAME,null, nota.toContentValues());
    }

    public int update(Nota nota) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String[] id_ = {String.valueOf(nota.get_id())};
        return sqLiteDatabase.update(NotaEntry.TABLE_NAME,nota.toContentValues(),"_id = ?", id_);
    }

    public int delete(int _id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String[] id_ = {String.valueOf(_id)};
        return sqLiteDatabase.delete(NotaEntry.TABLE_NAME,"_id = ?", id_);
    }

    public ArrayList<Nota> getAll(String user_id) {
        ArrayList<Nota> notas = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] parametros = {NotaEntry._ID,NotaEntry.USER_ID,NotaEntry.TITULO,NotaEntry.CONTENIDO};
        String whereClause = "user_id = ?";
        String[] tableColumns = new String[] {user_id};
        String orderBy = "titulo";
        Cursor cursor = sqLiteDatabase.query(
                NotaEntry.TABLE_NAME,
                parametros, whereClause, tableColumns ,null, null, orderBy
                );
        if(cursor == null) Log.e("Sqlite:"," Error, al obtener el metodo getAll()");
        if(!cursor.moveToFirst()) Log.d("Sqlite:", " No existen datos");
        else
        do {
            Nota nota = new Nota(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
            notas.add(nota);
        } while (cursor.moveToNext());
        cursor.close();

        return notas;
    }
}
