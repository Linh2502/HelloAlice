package de.linh_bui.helloalice;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Linh on 26.08.15.
 */
public class WebService extends AsyncTask<String, Void, String>{
    private String response = "Ich habe dich nicht verstanden";
    protected String doInBackground(String... params) {
        try{
            Log.e("Service", "call url");
            URL url = new URL("http://194.95.221.229:8080/Hablame-BotBackend/conversation");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "text/plain;charset=ISO-8859-1");
            urlConnection.setRequestProperty("Content-length", String.valueOf(params[0].length()));

            ReplaceSpecialCharacter newString = new ReplaceSpecialCharacter();
            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
            writer.write(newString.replaceSpecialCharacter(params[0]));
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            for ( String line; (line = reader.readLine()) != null; )
            {
                response = line;
            }

            writer.close();
            reader.close();

            return response;
        } catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    protected void onPostExecute(String result){
        if(result != null){
            Log.e("Service", "save result");
            setResponse(result);
        }else{
            Log.e("Error loading, attempt to reconnect", "error");
            response = "";
        }
    }

    public void setResponse(String response){
        Log.e("Service", "save response");
        this.response = response;
    }

    public String getResponse(){
        Log.e("Service", "get response");
        return response;
    }
}
