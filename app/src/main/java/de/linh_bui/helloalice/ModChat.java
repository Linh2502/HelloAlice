package de.linh_bui.helloalice;

import android.os.Parcel;
import android.os.Parcelable;

import org.alicebot.ab.Chat;

/**
 * Created by Linh on 19.08.15.
 */
public class ModChat extends Chat implements Parcelable{

    private ModBot classBot;

    public ModChat(ModBot bot){
        super(bot);
        this.classBot = bot;
    }

    public ModBot getClassBot(){
        return classBot;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.classBot, flags);
    }

    private ModChat (Parcel in) {
        this.classBot = in.readParcelable(ModBot.class.getClassLoader());
    }

    public static final Creator<ModChat> CREATOR = new Creator<ModChat>() {
        public ModChat createFromParcel(Parcel source) {
            return new ModChat(source);
        }

        public ModChat[] newArray(int size) {
            return new ModChat[size];
        }
    };
}
