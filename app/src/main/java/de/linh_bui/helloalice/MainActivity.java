package de.linh_bui.helloalice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private ModBot alice;
    private ModChat chatSession;
    private Intent botData;
    private Intent chatData;
    private ImageButton btnSpeak;
    private TextToSpeech tts;
    private TextView txtSpeechInput;
    private TextView txtChat;
    private ProgressBar progressBar;
    private LinearLayout mainLayout;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                voiceInput();
            }
        });
    }

    public void ttsInit() {
        tts = new TextToSpeech(this, this);
    }

    protected void onStop() {
        tts.stop();
        tts.shutdown();
        super.onStop();
    }

    public void onDestroy() {
        if (getTTS() != null) {
            tts.stop();
        }
        super.onDestroy();
    }

    public TextToSpeech getTTS() {
        return tts;
    }

    public void onInit(int status) { }

    private void setup() {
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        ttsInit();
        initializeBot();
    }

    private void initializeBot(){
        Intent i = new Intent(this, LoadBot.class);
        startActivityForResult(i, 1);
    }

    @Deprecated
    private void initializeChat(){
        Intent i = new Intent(this, LoadChat.class);
        i.putExtra("botName", alice.name);
        i.putExtra("path", alice.root_path);
        startActivityForResult(i, 2);
    }

    private void loadBot(){
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    alice = botData.getParcelableExtra("bot");
                    return true;
                } catch (Exception e){
                    Log.e("Error loading Bot", "error", e);
                    return false;
                }
            }
            protected void onPostExecute(Boolean response){
                if(response){
                    loadChat();
                }else{
                    Log.e("Error loading, attempt to reconnect", "error");
                }
            }
        }.execute();
    }

    private void loadChat(){
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    chatSession = new ModChat(alice);
                    return true;
                } catch (Exception e){
                    Log.e("Error loading Bot", "error", e);
                    return false;
                }
            }
            protected void onPostExecute(Boolean response){
                if(response){
                    Log.e("Chat", "load chat success");
                }else{
                    Log.e("Error loading, attempt to reconnect", "error");
                }
            }
        }.execute();
    }

    public void voiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    this.botData = data;
                    loadBot();
                }
                break;
            }
            case 2: {
                if(resultCode == RESULT_OK){
                    this.chatData = data;
                    loadChat();
                }
                break;
            }
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String response = chatSession.multisentenceRespond(result.get(0));
                    voiceOutput(response);
                    txtSpeechInput.setText("Input: " + result.get(0) + "\n" + "Output: " + response);
                }
                break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void voiceOutput(String outputText) {
        CharSequence outputToSpeech = outputText;
        tts.speak(outputToSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
