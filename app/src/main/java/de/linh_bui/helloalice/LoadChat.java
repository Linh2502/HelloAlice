package de.linh_bui.helloalice;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Linh on 24.08.15.
 */
public class LoadChat extends Activity{
    private ModBot alice;
    private ModChat chatSession;
    private Bundle extras;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatprogress);
        extras = getIntent().getExtras();
        setupChat();
    }

    private void setupChat(){
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Log.e("Chat", "load chat");
                    //alice = extras.getParcelable("bot");
                    String name = extras.getString("botName");
                    String path = extras.getString("path");
                    Log.e("Alice", name + " + " + path);
                    alice = new ModBot(name, path);
                    return true;
                } catch (Exception e){
                    Log.e("Error loading Bot", "error", e);
                    return false;
                }
            }
            protected void onPostExecute(Boolean response){
                if(response){
                    Log.e("Chat", "load chat success");
                    chatSession = new ModChat(alice);
                    callBack();
                }else{
                    Log.e("Error loading, attempt to reconnect", "error");
                    setupChat();
                }
            }
        }.execute();
    }

    private void callBack(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("chatSession", chatSession);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
