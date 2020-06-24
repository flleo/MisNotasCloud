package utilidades.misnotas.persistence.sqlite;

import android.provider.BaseColumns;

public class NotasContract  {

    public static abstract class NotaEntry implements BaseColumns {
        public static final String TABLE_NAME = "nota";
        public static final String USER_ID = "user_id";
        public static final String TITULO = "titulo";
        public static final String CONTENIDO = "contenido";

    }
}
