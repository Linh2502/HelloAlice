package de.linh_bui.helloalice;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

import java.io.File;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File fileExt = new File(getExternalFilesDir(null).getAbsolutePath()+"/bots");

        if(!fileExt.exists())
        {
            ZipFileExtraction extract = new ZipFileExtraction();

            try
            {
                extract.unZipIt(getAssets().open("bots.zip"), getExternalFilesDir(null).getAbsolutePath()+"/");
            } catch (Exception e) { e.printStackTrace(); }
        }

        String botname = "alice2";
        String path = getExternalFilesDir(null).getAbsolutePath();
        Bot alice = new Bot(botname,path);

        Chat chatSession = new Chat(alice);

        String request = "Hello.  Are you alive?  What is your name?";
        String response = chatSession.multisentenceRespond(request);
        System.out.println(response);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
