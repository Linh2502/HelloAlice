package de.linh_bui.helloalice;

import android.os.Parcel;
import android.os.Parcelable;

import org.alicebot.ab.Bot;

/**
 * Created by Linh on 18.08.15.
 */
public class ModBot extends Bot implements Parcelable{

    private String botName;
    private String path;

    public ModBot() { }

    public ModBot(Parcel input){
        botName = input.readString();
        path = input.readString();
    }

    public ModBot(String botName, String path){
        super(botName, path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(botName);
        dest.writeString(path);
    }

    public static final Parcelable.Creator<ModBot> CREATOR = new Parcelable.Creator<ModBot>(){
        public ModBot createFromParcel(Parcel input){
            return new ModBot(input);
        }
        public ModBot[] newArray(int size){
            return new ModBot[size];
        }
    };
}
