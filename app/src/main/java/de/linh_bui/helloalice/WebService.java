package de.linh_bui.helloalice;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Linh on 26.08.15.
 */
public class WebService extends AsyncTask<String, Void, String>{
    private String response = "Test";
    protected String doInBackground(String... params) {
        try{
            Log.e("Service", "call url");
            URL url = new URL("http://194.95.221.229:8080/Hablame-BotBackend/conversation");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            bw.write(params[0]);
            bw.close();
            Log.e("Input", params[0]);

            Scanner scanner = new Scanner(urlConnection.getInputStream());
            while(scanner.hasNextLine()){
                Log.e("Service", "save response");
                response = scanner.nextLine();
                Log.e("Service", "response is: " + response);
            }
            scanner.close();
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
