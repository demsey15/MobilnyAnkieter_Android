package bohonos.demski.mieldzioc.mobilnyankieter.application;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;

import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.SurveysTemplateControl;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveyHandler;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveysRepository;

/**
 * Created by Dominik Demski on 2015-05-02.
 */
public class ApplicationState {
    private static ApplicationState instance;

    private String deviceId = "1";

    private SurveyHandlerMobile surveyHandler;
    private SurveysRepositoryMobile surveysRepository;
    private SurveysTemplateControl surveysTemplateControl;
    private AnsweringSurveyControl answeringSurveyControl;

    private UsersPreferences preferences;

    private Context context;

    public static ApplicationState getInstance(Context context){
        return (instance == null)? (instance = new ApplicationState(context)):instance;
    }

    public SurveyHandler getSurveyHandler() {
        return surveyHandler;
    }

    public SurveysRepository getSurveysRepository() {
        return surveysRepository;
    }

    public SurveysTemplateControl getSurveysTemplateControl() {
        return surveysTemplateControl;
    }

    public AnsweringSurveyControl getAnsweringSurveyControl() {
        return answeringSurveyControl;
    }

    private ApplicationState(Context context){
        this.context = context;

        preferences = new UsersPreferences(context);
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

        prepareSurveyHandler();

        Log.d("LOGOWANIE", "HASLO OK");

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

    private boolean prepareSurveyHandler(){
        if(instance == null) return false;

        surveyHandler = new SurveyHandlerMobile(context, preferences.getLastAddedSurveyTemplateNumber());
        //jeśli nie przekazano listy szablonów
        //pobierz wszystkie

        surveysRepository = new SurveysRepositoryMobile(context);
        surveysTemplateControl = new SurveysTemplateControl(surveyHandler);
        answeringSurveyControl = new AnsweringSurveyControl(surveyHandler);

        return true;
    }

    public void saveLastAddedSurveyTemplateNumber(int lastTemplateNumber){
        preferences.saveLastAddedSurveyTemplateNumber(lastTemplateNumber);
    }

    public String getDeviceId() {
        return deviceId;
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
}
