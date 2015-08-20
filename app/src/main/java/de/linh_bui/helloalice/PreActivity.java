package de.linh_bui.helloalice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;

/**
 * Created by Linh on 18.08.15.
 */
public class PreActivity extends Activity{
    private ModBot alice;
    private String botName = "alice2";
    private String path;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        path = getExternalFilesDir(null).getAbsolutePath();
        extractZipFile();
        setupBot();
    }

    private void extractZipFile() {
        File fileExt = new File(path + "/bots");

        if (!fileExt.exists()) {
            ZipFileExtraction extract = new ZipFileExtraction();

            try {
                extract.unZipIt(getAssets().open("bots.zip"), getExternalFilesDir(null).getAbsolutePath() + "/");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupBot(){
        alice = new ModBot(botName, path);
        callMainActivity();
    }

    private void callMainActivity(){
        Intent startMainActivity = new Intent(this, MainActivity.class);
        startMainActivity.putExtra("alice", alice);
        startActivity(startMainActivity);
    }
}
