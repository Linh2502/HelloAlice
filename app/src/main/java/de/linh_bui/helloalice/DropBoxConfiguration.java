package de.linh_bui.helloalice;

/**
 * Created by Linh on 30.08.15.
 */

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DropBoxConfiguration {
    private Properties properties;
    private InputStream input;

    public DropBoxConfiguration(){
        properties = new Properties();
        try{
            String filename = "/assets/config.properties";
            input = getClass().getResourceAsStream(filename);
            if(input==null){
                Log.e("Error", "Sorry, unable to find " + filename);
            }
            properties.load(input);
            input.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String getDropBoxAppKey(){
        return properties.getProperty("dropboxappkey");
    }

    public String getDropBoxAppSecret(){
        return properties.getProperty("dropboxappsecret");
    }

    public String getDropBoxAccessToken() {
        return properties.getProperty("dropboxaccesstoken");
    }
}