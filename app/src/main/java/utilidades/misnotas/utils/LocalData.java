package utilidades.misnotas.utils;


import android.content.Context;
import android.content.SharedPreferences;

//Permite leer y escribir pares clave-valor persistentes de tipos de datos de primitivas: booleanos, floats, ints, longs y strings.
 public  class  LocalData {
    //En este caso van a coincidir TEMP_EMAIL_ID y USER_ID
    public static  String EMAIL_ID;
    public static  String USER_ID;
    public static  final String USER_IDs = "";
    private static final String APP_SHARED_PREFS = "FireBaseDemoPref";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public LocalData(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public String getString(String keyName) {
        return appSharedPrefs.getString(keyName, "");
    }

    public void setString(String keyName, String value) {
        prefsEditor.putString(keyName, value);
        prefsEditor.commit();
    }

    public void resetAll() {
        prefsEditor.clear();
        prefsEditor.commit();
    }

}