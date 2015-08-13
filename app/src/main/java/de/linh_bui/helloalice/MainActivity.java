package de.linh_bui.helloalice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

import java.io.File;

public class MainActivity extends Activity{
    private Bot alice;
    private Chat chatSession;
    private GoogleService service;
    private ImageButton btnSpeak;
    private String botName = "alice2";
    private String path;
    private TextView txtSpeechInput;
    static final int RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        extractZipFile();

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.voiceInput();
            }
        });

    }

    @Override
    protected void onStart() {
        service.ttsInit();
        super.onStart();
    }

    @Override
    protected void onStop() {
        service.ttsStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // Destroy tts
        if (service.getTTS() != null) {
            service.ttsStop();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
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

    private void setup() {
        path = getExternalFilesDir(null).getAbsolutePath();
        alice = new Bot(botName, path);
        chatSession = new Chat(alice);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        service = new GoogleService();
        service.setup(chatSession, txtSpeechInput);

        Intent syncData = new Intent(this, ContentSynchronization.class);
        startActivityForResult(syncData, RESULT_CODE);
    }

    private void extractZipFile() {
        File fileExt = new File(getExternalFilesDir(null).getAbsolutePath() + "/bots");

        if (!fileExt.exists()) {
            ZipFileExtraction extract = new ZipFileExtraction();

            try {
                extract.unZipIt(getAssets().open("bots.zip"), getExternalFilesDir(null).getAbsolutePath() + "/");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RESULT_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }else{
                System.out.println("Error ResultOK");
            }
        }else{
            System.out.println("Error ResultCode");
        }
    }
}
