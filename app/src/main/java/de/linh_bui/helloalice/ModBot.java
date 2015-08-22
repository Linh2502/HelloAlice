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

    public ModBot(String botName, String path){
        super(botName, path);
        this.botName = botName;
        this.path = path;
    }

    public String getBotName(){
        return botName;
    }

    public String getPath(){
        return path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.botName);
        dest.writeString(this.path);
    }

    private ModBot (Parcel in) {
        super(in.readString(), in.readString());
        this.botName = in.readString();
        this.path = in.readString();
    }

    public static final Creator<ModBot> CREATOR = new Creator<ModBot>() {
        public ModBot createFromParcel(Parcel source) {
            return new ModBot(source);
        }

        public ModBot[] newArray(int size) {
            return new ModBot[size];
        }
    };
}
