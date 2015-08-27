package de.linh_bui.helloalice;

/**
 * Created by Linh on 27.08.15.
 */
public class ReplaceSpecialCharacter {
    public String replaceSpecialCharacter(String convertString){
        String result = convertString
                .replaceAll("ä", "ae")
                .replaceAll("ö", "oe")
                .replaceAll("ü", "ue")
                .replaceAll("ß", "ss")
                .replaceAll("Ä", "Ae")
                .replaceAll("Ö", "Oe")
                .replaceAll("Ü", "Ue");
        return result;
    }
}
