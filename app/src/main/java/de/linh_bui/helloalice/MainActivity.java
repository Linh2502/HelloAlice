package de.linh_bui.helloalice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.alicebot.ab.Chat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private Chat chatSession;
    private ModBot alice;
    private ImageButton btnSpeak;
    private TextToSpeech tts;
    private TextView txtSpeechInput;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int RESULT_CODE = 1;

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

    private void setup() {
        Intent i = getIntent();
        alice = i.getParcelableExtra("alice");
        chatSession = new ModChat(alice);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        ttsInit();

        //Intent syncData = new Intent(this, ContentSynchronization.class);
        //startActivityForResult(syncData, RESULT_CODE);
    }

    public void ttsInit() {
        tts = new TextToSpeech(this, this);
    }

    protected void onStart() {
        super.onStart();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
