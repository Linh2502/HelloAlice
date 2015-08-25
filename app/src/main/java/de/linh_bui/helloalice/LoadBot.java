package de.linh_bui.helloalice;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Linh on 24.08.15.
 */
public class LoadBot extends Activity {
    private ModBot alice;
    private String botName = "Alice";
    private String path;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_botprogress);
        path = getExternalFilesDir(null).getAbsolutePath();
        setupBot();
    }

    private void setupBot(){
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Log.e("Bot", "load bot" + path);
                    alice = new ModBot(botName, path);
                    return true;
                } catch (Exception e){
                    Log.e("Error loading Bot", "error", e);
                    return false;
                }
            }
            protected void onPostExecute(Boolean response){
                if(response){
                    Log.e("Bot", "load bot success");
                    callBack();
                }else{
                    Log.e("Error loading, attempt to reconnect", "error");
                    setupBot();
                }
            }
        }.execute();
    }

    private void callBack(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("bot", alice);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
