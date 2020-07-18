package utilidades.misnotas.model;

import android.content.ContentValues;

import utilidades.misnotas.persistence.sqlite.NotasContract;

public class Nota {
    private String id;
    private String user_id ;
    private String titulo ;
    private String contenido ;

    public Nota() {
    }

 /*   public Nota(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public Nota(String user_id, String titulo, String contenido) {
        this.user_id = user_id;
        this.titulo = titulo;
        this.contenido = contenido;
    }*/

    public Nota(String id, String user_id, String titulo, String contenido) {
        this.id = id;
        this.user_id = user_id;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
        values.put(NotasContract.NotaEntry.ID, id);
        values.put(NotasContract.NotaEntry.USER_ID, user_id);
        values.put(NotasContract.NotaEntry.TITULO, titulo);
        values.put(NotasContract.NotaEntry.CONTENIDO, contenido);

        return values;
    }
}
