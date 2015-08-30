package de.linh_bui.helloalice;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Linh on 13.08.15.
 */
public class ContentSynchronization extends Activity {
    private String dropboxAppKey;
    private String dropboxAppSecret;
    private String dropboxAccessToken;
    private String path;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private TextView txtDownload;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        txtDownload = (TextView) findViewById(R.id.txtDownload);
        path = getExternalFilesDir(null).getAbsolutePath();

        getKeys();
        setupAuthentication();
    }

    public ContentSynchronization(){}

    private void getKeys(){
        DropBoxConfiguration config = new DropBoxConfiguration();
        dropboxAppKey = config.getDropBoxAppKey();
        dropboxAppSecret = config.getDropBoxAppSecret();
        dropboxAccessToken = config.getDropBoxAccessToken();
    }

    private void setupAuthentication(){
        AppKeyPair appKeys = new AppKeyPair(dropboxAppKey, dropboxAppSecret);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        mDBApi.getSession().setOAuth2AccessToken(dropboxAccessToken);
        try {
            downloadFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }
    }

    private void downloadFile() throws FileNotFoundException, DropboxException {
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    File file = new File(path + "/bots.zip");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/bots.zip", null, outputStream, null);
                    Log.e("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
                    return true;
                } catch (Exception e){
                    return false;
                }
            }
            protected void onPostExecute(Boolean response){
                if(response){
                    callBack();
                }else{
                    shutDown();
                }
            }
        }.execute();
    }

    private void callBack(){
        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    private void shutDown(){
        txtDownload.setText("Error downloading bot data, attempt to shut down app...");
        Runnable task = new Runnable() {
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        };
        worker.schedule(task, 3, TimeUnit.SECONDS);
    }
}
