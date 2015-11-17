package bohonos.demski.mieldzioc.mobilnyankieter.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Dominik Demski on 2015-05-06.
 */
public class UsersPreferencesManager {
    private SharedPreferences preferences;

    private static final String LAST_TEMPLATE_NUMBER = "lastTemplateNumber";
    private static final String PASSWORD = "password";
    private static final String HELP_QUESTION = "helpQuestion";
    private static final String HELP_QUESTION_ANSWER = "helpQuestionAnswer";
    private static final String DONT_LOG_OUT = "dontLogOut";
    private static final String REMEMBER_PASSWORD = "rememberPassword";
    private static final String DEVICE_ID = "device_id";

    public UsersPreferencesManager(Context context) {
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public void saveLastAddedSurveyTemplateNumber(int lastTemplateNumber) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(LAST_TEMPLATE_NUMBER, lastTemplateNumber);
        editor.commit();
    }

    public int getLastAddedSurveyTemplateNumber() {
        return preferences.getInt("lastTemplateNumber", 0);
    }

    public void savePassword(char[] password){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(PASSWORD, String.valueOf(password));
        editor.commit();

        Arrays.fill(password, '0');
    }

    public char[] getPassword(){
        return preferences.getString(PASSWORD, "").toCharArray();
    }

    public void saveHelpQuestion(String helpQuestion){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(HELP_QUESTION, helpQuestion);
        editor.commit();
    }

    public String getHelpQuestion(){
        return preferences.getString(HELP_QUESTION, "");
    }

    public void saveHelpQuestionAnswer(String helpQuestionAnswer){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(HELP_QUESTION_ANSWER, helpQuestionAnswer);
        editor.commit();
    }

    public String getHelpQuestionAnswer(){
        return preferences.getString(HELP_QUESTION_ANSWER, "");
    }

    public void saveRememberPassword(boolean shouldRemember){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(REMEMBER_PASSWORD, shouldRemember);
        editor.commit();
    }

    public boolean ifShouldRememberPassword(){
        return preferences.getBoolean(REMEMBER_PASSWORD, false);
    }

    public void saveDontLogOut(boolean shouldDontLogOut){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(DONT_LOG_OUT, shouldDontLogOut);
        editor.commit();
    }

    public boolean ifShouldDontLogOut(){
        return preferences.getBoolean(DONT_LOG_OUT, false);
    }

    public String getDeviceId(Context context){
        String deviceId = preferences.getString(DEVICE_ID, "");

        if(deviceId.isEmpty()){
            long random = (new Random()).nextLong();

            deviceId = getWifiMacAddress(context) + random;

            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(DEVICE_ID, deviceId);
            editor.commit();
        }

        return  deviceId;
    }

    private String getWifiMacAddress(Context context){
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        WifiInfo info = manager.getConnectionInfo();

        return info.getMacAddress();
    }
}
