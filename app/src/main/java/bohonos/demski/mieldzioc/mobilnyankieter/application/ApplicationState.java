package bohonos.demski.mieldzioc.mobilnyankieter.application;

import android.content.Context;

import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.controls.SurveysTemplateControl;
import bohonos.demski.mieldzioc.survey.SurveyHandler;
import bohonos.demski.mieldzioc.survey.SurveysRepository;

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
    }

    public boolean logIn(String password){
        if(instance == null){
            return false;
        }

        preferences = new UsersPreferences(context);

        prepareSurveyHandler();

        return true;
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
}
