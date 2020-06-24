package utilidades.misnotas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;


import utilidades.misnotas.email.EmailAuthenticationActivity;
import utilidades.misnotas.persistence.sqlite.NotasContract;
import utilidades.misnotas.utils.LocalData;

import static utilidades.misnotas.utils.LocalData.USER_ID;

public class MainActivity extends AppCompatActivity {



    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*    LocalData localData = new LocalData(getApplicationContext());
            if(!localData.getString(USER_ID).equals("")){}
            else {
                Intent mailIntent = new Intent(MainActivity.this, EmailAuthenticationActivity.class);
                startActivity(mailIntent);
                finish();
            }
*/
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_salir) {
            Intent intent = new Intent(this, EmailAuthenticationActivity.class);
            intent.putExtra("SALIR", "salir");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed (){
        //Nothing to do
    }

}
