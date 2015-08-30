package de.linh_bui.helloalice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Linh on 18.08.15.
 */
public class PreActivity extends Activity implements TextToSpeech.OnInitListener{
    private String path;
    private TextToSpeech tts;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        path = getExternalFilesDir(null).getAbsolutePath();
        ttsInit();
        Runnable task = new Runnable() {
            public void run() {
                checkConnectionToInternet();
            }
        };
        worker.schedule(task, 3, TimeUnit.SECONDS);
    }

    protected void checkConnectionToInternet(){
        if(isOnline()){
            downloadZip();
        } else {
            checkExistingZipFolder(false);
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

    private void checkExistingZipFolder(Boolean bool){
        File fileExt = new File(path + "/bots");
        if(bool){
            extractZipFile();
        } else if (!fileExt.exists()) {
            extractZipFile();
        }
    }

    private void extractZipFile() {
        ZipFileExtraction extract = new ZipFileExtraction();
        try {
            extract.unZipIt(getAssets().open("bots.zip"), getExternalFilesDir(null).getAbsolutePath() + "/");
        } catch (Exception e) {
            e.printStackTrace();
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
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_CANCELED) {
                    checkExistingZipFolder(false);
                    callOnlineActivity();
                } else if (resultCode == RESULT_OK) {
                    checkExistingZipFolder(true);
                    callOnlineActivity();
                }
                break;
            }
        }
    }
}
