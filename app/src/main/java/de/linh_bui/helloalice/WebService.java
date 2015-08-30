package de.linh_bui.helloalice;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Linh on 26.08.15.
 */
public class WebService extends AsyncTask<String, Void, String>{
    private String response = "Ich habe dich nicht verstanden";
    private URL url;
    private HttpURLConnection urlConnection;
    private OutputStreamWriter writer;

    public WebService() { }

    public void setResponse(String response){
        Log.e("Service", "save response");
        this.response = response;
    }

    public String getResponse(){
        Log.e("Service", "get response");
        return response;
    }

    protected String doInBackground(String... params) {
        try{
            Log.e("Service", "call url");
            connectToURL(params);
            postRequest(params);
            return urlResponse();
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

    private void connectToURL(String... params) throws IOException {
        url = new URL("http://194.95.221.229:8080/Hablame-BotBackend/conversation");
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setChunkedStreamingMode(0);
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type", "text/plain;charset=ISO-8859-1");
        urlConnection.setRequestProperty("Content-length", String.valueOf(params[0].length()));
    }

    private void postRequest(String... params) throws IOException {
        ReplaceSpecialCharacter newString = new ReplaceSpecialCharacter();
        writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
        writer.write(newString.replaceSpecialCharacter(params[0]));
        writer.flush();
    }

    private String urlResponse() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        for ( String line; (line = reader.readLine()) != null; )
        {
            response = line;
        }

        writer.close();
        reader.close();
        return response;
    }
}
