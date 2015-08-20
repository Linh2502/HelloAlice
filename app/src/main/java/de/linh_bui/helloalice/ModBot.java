package de.linh_bui.helloalice;

import android.os.Parcel;
import android.os.Parcelable;

import org.alicebot.ab.Bot;

/**
 * Created by Linh on 18.08.15.
 */
public class ModBot extends Bot implements Parcelable{

    private String bot_path;
    private String bot_name_path;

    public ModBot(String botName, String path){
        super(botName, path);
    }

    public ModBot() {
    }

    public String getBot_path() {
        return bot_path;
    }

    public void setBot_path(String bot_path) {
        this.bot_path = bot_path;
    }

    public String getBot_name_path() {
        return bot_name_path;
    }

    public void setBot_name_path(String bot_name_path) {
        this.bot_name_path = bot_name_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bot_path);
        dest.writeString(this.bot_name_path);
    }

    private ModBot(Parcel in) {
        this.bot_path = in.readString();
        this.bot_name_path = in.readString();
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
