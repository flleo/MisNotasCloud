package utilidades.misnotas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import utilidades.misnotas.email.EmailAuthenticationActivity;
import utilidades.misnotas.fragment.ListaFragment;
import utilidades.misnotas.fragment.NotaFragment;
import utilidades.misnotas.utils.LocalData;
import static utilidades.misnotas.utils.LocalData.USER_ID;
import static utilidades.misnotas.utils.LocalData.USER_IDs;

public class MainActivity extends AppCompatActivity  {


    private static final String TAG = "MainActivity : ";
    LocalData localData;
    ListaFragment listaFragment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localData = new LocalData(getApplicationContext());
        USER_ID = localData.getString(USER_IDs);
        if (USER_ID != "") {
            Log.e(USER_ID,"user");
        } else {
            Intent mailIntent = new Intent(MainActivity.this, EmailAuthenticationActivity.class);
            startActivity(mailIntent);
        }


    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        int positionOfMenuItem = 0; // or whatever...
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString(item.getTitle());
        float[] hsv = {36, 100, 100};
        s.setSpan(new ForegroundColorSpan(Color.HSVToColor(hsv)), 0, s.length(), 0);
        item.setTitle(s);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_salir) {
            Intent intent = new Intent(this, EmailAuthenticationActivity.class);
            intent.putExtra("SALIR", "salir");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Nothing to do
    }

}
