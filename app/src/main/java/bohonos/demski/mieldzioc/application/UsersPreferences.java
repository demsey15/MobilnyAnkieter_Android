package bohonos.demski.mieldzioc.application;

import android.content.Context;
import android.content.SharedPreferences;

import bohonos.demski.mieldzioc.interviewer.Interviewer;

/**
 * Created by Dominik Demski on 2015-05-06.
 */
public class UsersPreferences {
    private SharedPreferences preferences;

    public UsersPreferences(Context context, Interviewer interviewer) {
        preferences = context.getSharedPreferences("settings_" + interviewer.getId(),
                Context.MODE_PRIVATE);
    }

    public void saveLastAddedSurveyTemplateNumber(int lastTemplateNumber) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("lastTemplateNumber", lastTemplateNumber);
        editor.commit();
    }

    public int getLastAddedSurveyTemplateNumber() {
        return preferences.getInt("lastTemplateNumber", 0);
    }
}
