package demski.dominik.mobilnyankieter.application;

import android.content.Context;

import java.util.Arrays;

import demski.dominik.mobilnyankieter.sendingsurvey.creatingsurveysfiles.FileHandler;

/**
 * Created by Dominik on 2015-11-16.
 */
public class UserPreferences {
    private static UserPreferences instance;

    private UsersPreferencesManager preferences;

    private Context context;

    public static UserPreferences getInstance(Context context){
        return (instance == null)? (instance = new UserPreferences(context)):instance;
    }

    private UserPreferences(Context context){
        this.context = context;

        preferences = new UsersPreferencesManager(context);
    }

    public boolean logIn(char[] password){
        if(instance == null){
            clearPasswordArray(password);

            return false;
        }

        if(!checkUserPassword(password)){
            clearPasswordArray(password);

            return false;
        }

        clearPasswordArray(password);

        prepareSurveyManagers();

        FileHandler fileHandler = new FileHandler();
        fileHandler.prepareLoadingDirectories();

        return true;
    }

    private boolean checkUserPassword(char[] password){
        char[] userPassword = preferences.getPassword();

        if(checkIfPasswordIsEmpty(password)){
            return false;
        }
        else{
            if(userPassword.length != password.length){
                return false;
            }
            else{
                for(int i = 0; i < userPassword.length; i++){
                    if(password[i] != userPassword[i]) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    private boolean checkIfPasswordIsEmpty(char[] password){
        return password.length == 0;
    }

    private void clearPasswordArray(char[] password){
        Arrays.fill(password, '0');
    }

    private boolean prepareSurveyManagers(){
        if(instance == null){
            return false;
        }

       ApplicationState.getInstance(context).prepareSurveyHandler();

        return true;
    }


    public void saveUserPassword(char[] password){
        preferences.savePassword(password);
    }

    public char[] getUserPassword(){
        return preferences.getPassword();
    }

    public void saveHelpQuestion(String helpQuestion){
        preferences.saveHelpQuestion(helpQuestion);
    }

    public String getHelpQuestion(){
        return preferences.getHelpQuestion();
    }

    public void saveHelpQuestionAnswer(String helpQuestionAnswer){
        preferences.saveHelpQuestionAnswer(helpQuestionAnswer);
    }

    public String getHelpQuestionAnswer(){
        return preferences.getHelpQuestionAnswer();
    }

    public boolean checkIfUsersPasswordIsSet(){
        char[] usersPassword = preferences.getPassword();

        boolean passwordIsSet = !checkIfPasswordIsEmpty(usersPassword);
        clearPasswordArray(usersPassword);

        return  passwordIsSet;
    }

    public void saveRememberPassword(boolean shouldRemember){
        preferences.saveRememberPassword(shouldRemember);
    }

    public boolean ifShouldRememberPassword(){
        return preferences.ifShouldRememberPassword();
    }

    public void saveDontLogOut(boolean shouldDontLogOut){
        preferences.saveDontLogOut(shouldDontLogOut);
    }

    public boolean ifShouldDontLogOut(){
        return preferences.ifShouldDontLogOut();
    }

    public void resetUsersSettings(){
        saveDontLogOut(false);
        saveRememberPassword(false);


        saveHelpQuestionAnswer("");
        saveHelpQuestion("");

        saveUserPassword("".toCharArray());
    }

    public String getDeviceId() {
        return preferences.getDeviceId(context);
    }
}
