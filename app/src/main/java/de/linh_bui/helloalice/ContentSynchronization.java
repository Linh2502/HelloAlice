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
    private String dropBoxAppKey;
    private String dropBoxAppSecret;
    private String dropBoxAccessToken;
    private String path;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private TextView txtDownload;
    private File localFile;
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
        dropBoxAppKey = config.getDropBoxAppKey();
        dropBoxAppSecret = config.getDropBoxAppSecret();
        dropBoxAccessToken = config.getDropBoxAccessToken();
    }

    private void setupAuthentication(){
        AppKeyPair appKeys = new AppKeyPair(dropBoxAppKey, dropBoxAppSecret);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
        mDBApi.getSession().setOAuth2AccessToken(dropBoxAccessToken);
        checkForUpdates();
    }

    private void checkForUpdates(){
        new AsyncTask<Void, Void, Boolean>(){
            DropboxAPI.Entry fileInfo;
            long dropBoxFileDate;
            protected Boolean doInBackground(Void... params) {
                try {
                    localFile = new File(path + "/bots.zip");
                    fileInfo = mDBApi.metadata("/bots.zip", 1, null, true, null);
                    dropBoxFileDate = new ConvertDate().getDateToTimestamp(fileInfo.modified);
                    if(localFile.lastModified() < dropBoxFileDate){
                        return true;
                    }else{
                        return false;
                    }
                } catch (Exception e){
                    return false;
                }
            }
            protected void onPostExecute(Boolean response){
                if(response){
                    try {
                        downloadFile();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DropboxException e) {
                        e.printStackTrace();
                    }
                }else{
                    callBack(RESULT_CANCELED);
                }
            }
        }.execute();
    }

    private void downloadFile() throws FileNotFoundException, DropboxException {
        txtDownload.setText("Downloading bot data");
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    FileOutputStream outputStream = new FileOutputStream(localFile);
                    DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/bots.zip", null, outputStream, null);
                    Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
                    return true;
                } catch (Exception e){
                    return false;
                }
            }
            protected void onPostExecute(Boolean response){
                if(response){
                    callBack(RESULT_OK);
                }else{
                    shutDown();
                }
            }
        }.execute();
    }

    private void callBack(int result){
        txtDownload.setText("Finished");
        Intent returnIntent = new Intent();
        setResult(result,returnIntent);
        finish();
    }

    private void shutDown(){
        txtDownload.setText("Error downloading bot data," + "\n" + "attempt to shut down app");
        Runnable task = new Runnable() {
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        };
        worker.schedule(task, 2, TimeUnit.SECONDS);
    }
}
