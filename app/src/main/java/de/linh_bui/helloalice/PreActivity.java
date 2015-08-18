package de.linh_bui.helloalice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.io.File;
import java.util.Locale;

/**
 * Created by Linh on 18.08.15.
 */
public class PreActivity extends Activity implements TextToSpeech.OnInitListener{
    private ModBot alice;
    private String botName = "Alice";
    private TextToSpeech tts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        extractZipFile();
        ttsInit();
        setupBot();
    }

    private void extractZipFile() {
        File fileExt = new File(getExternalFilesDir(null).getAbsolutePath() + "/bots");

        if (!fileExt.exists()) {
            ZipFileExtraction extract = new ZipFileExtraction();

            try {
                extract.unZipIt(getAssets().open("bots.zip"), getExternalFilesDir(null).getAbsolutePath() + "/");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ttsInit() {
        tts = new TextToSpeech(this, this);
    }

    private void setupBot(){
        final String path = getExternalFilesDir(null).getAbsolutePath();
        alice = new ModBot(botName, path);
        callMainActivity();
    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.GERMAN);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                Log.e("TTS", "This Language is not supported");
            } else {
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    private void callMainActivity(){
        Intent startMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startMainActivity.putExtra("alice", alice);
        startActivity(startMainActivity);
    }
}
