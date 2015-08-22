package de.linh_bui.helloalice;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;

/**
 * Created by Linh on 18.08.15.
 */
public class PreActivity extends Activity implements TextToSpeech.OnInitListener{
    private ModBot alice;
    private String botName = "alice2";
    private String path;
    private TextView progressView;
    private TextToSpeech tts;
    private int loadingPercent = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        path = getExternalFilesDir(null).getAbsolutePath();
        downloadZip();
        extractZipFile();
        ttsInit();
        setupBot();
    }

    private void downloadZip(){
        //Intent syncData = new Intent(this, ContentSynchronization.class);
        //startActivityForResult(syncData, RESULT_CODE);
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

    public void ttsInit() {
        tts = new TextToSpeech(this, this);
    }

    private void setupBot(){
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    alice = new ModBot(botName, path);
                    return true;
                } catch (Exception e){
                    Log.e("Error loading Bot", "error", e);
                    return false;
                }
            }
            protected void onPostExecute(Boolean response){
                if(response){
                    callMainActivity();
                }else{
                    Log.e("Error loading, attempt to reconnect", "error");
                    setupBot();
                }
            }
            protected void onProgressUpdate(Void... params){
                progressView = (TextView) findViewById(R.id.txtSpeechInput);
                progressView.setText(progressView.getText() + "" + loadingPercent+1 + "%");
            }
        }.execute();
    }

    public void callMainActivity(){
        Intent startMainActivity = new Intent(this, MainActivity.class);
        startMainActivity.putExtra("alice", alice);
        startActivity(startMainActivity);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.ENGLISH);

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
