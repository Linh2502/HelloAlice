package de.linh_bui.helloalice;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.alicebot.ab.Chat;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Linh on 10.08.15.
 */
public class GoogleService extends Activity implements TextToSpeech.OnInitListener {
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private Chat chatSession;
    private TextToSpeech tts;
    private TextView txtSpeechInput;

    public void setup(Chat session, TextView speechInput) {
        chatSession = session;
        txtSpeechInput = speechInput;
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

    /**
     * Receiving speech input
     */
    @Override
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

    public void ttsInit() {
        tts = new TextToSpeech(this, this);
    }

    public void ttsStop() {
        tts.stop();
        tts.shutdown();
    }

    public TextToSpeech getTTS() {
        return tts;
    }
}
