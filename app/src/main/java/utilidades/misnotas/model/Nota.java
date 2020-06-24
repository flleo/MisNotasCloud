package utilidades.misnotas.model;

import android.content.ContentValues;

import utilidades.misnotas.persistence.sqlite.NotasContract;

public class Nota {
    private int _id ;
    private String user_id ;
    private String titulo , contenido ;

    public Nota() {
    }

    public Nota(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public Nota(String user_id, String titulo, String contenido) {
        this.user_id = user_id;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public Nota(int _id, String user_id, String titulo, String contenido) {
        this._id = _id;
        this.user_id = user_id;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int get_id() { return _id;}

    public String getUser_id() {
        return user_id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    @Override
    public String toString() {  //Lo que mostrara el listview
        return
                titulo +
                "\n" + contenido;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(NotasContract.NotaEntry.USER_ID, user_id);
        values.put(NotasContract.NotaEntry.TITULO, titulo);
        values.put(NotasContract.NotaEntry.CONTENIDO, contenido);

        return values;
    }
}
