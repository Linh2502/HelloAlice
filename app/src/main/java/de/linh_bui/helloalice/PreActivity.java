package de.linh_bui.helloalice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
        ttsInit();
        checkConnectionToInternet();
    }

    protected void checkConnectionToInternet(){
        if(isOnline()){
            downloadZip();
        } else {
            callOfflineActivity();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void downloadZip(){
        Intent syncData = new Intent(this, ContentSynchronization.class);
        startActivityForResult(syncData, 1);
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

    public void callOnlineActivity(){
        Intent startOnlineActivity = new Intent(this, OnlineActivity.class);
        startActivity(startOnlineActivity);
    }

    public void callOfflineActivity(){
        Intent startOfflineActivity = new Intent(this, OfflineActivity.class);
        startActivity(startOfflineActivity);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    extractZipFile();
                    callOnlineActivity();
                }
                break;
            }
        }
    }
}
