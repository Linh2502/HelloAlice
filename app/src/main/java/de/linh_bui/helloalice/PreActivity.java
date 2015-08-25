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
    private String path;
    private TextToSpeech tts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        path = getExternalFilesDir(null).getAbsolutePath();
        downloadZip();
        extractZipFile();
        ttsInit();
        callMainActivity();
    }

    private void downloadZip(){
        //Intent syncData = new Intent(this, ContentSynchronization.class);
        //startActivityForResult(syncData, RESULT_CODE);
    }

    private void extractZipFile() {
        File fileExt = new File(path + "/bots");
        Log.d("ZIP", "Check Folder if exists" + path + " and " + fileExt.exists());
        if (!fileExt.exists()) {
            ZipFileExtraction extract = new ZipFileExtraction();
            Log.d("ZIP", "Not existing, extracting Zip");
            try {
                extract.unZipIt(getAssets().open("bots.zip"), getExternalFilesDir(null).getAbsolutePath() + "/");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ttsInit() {
        tts = new TextToSpeech(this, this);
    }

    public void callMainActivity(){
        Intent startMainActivity = new Intent(this, MainActivity.class);
        startActivity(startMainActivity);
    }

    @Override
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
}
