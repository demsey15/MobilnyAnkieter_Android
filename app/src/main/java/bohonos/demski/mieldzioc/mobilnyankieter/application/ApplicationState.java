package bohonos.demski.mieldzioc.mobilnyankieter.application;

import android.content.Context;

import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.SurveysTemplateControl;
import bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.creatingsurveysfiles.FileHandler;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveyHandler;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveysRepository;

/**
 * Created by Dominik Demski on 2015-05-02.
 */
public class ApplicationState {
    private static ApplicationState instance;

    private SurveyHandlerMobile surveyHandler;
    private SurveysRepositoryMobile surveysRepository;
    private SurveysTemplateControl surveysTemplateControl;
    private AnsweringSurveyControl answeringSurveyControl;

    private UsersPreferencesManager preferences;

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

        preferences = new UsersPreferencesManager(context);
    }

    public boolean prepareSurveyHandler(){
        if(instance == null || surveyHandler != null || surveysRepository != null || surveysTemplateControl!= null
                || answeringSurveyControl != null){
            return false;
        }

        surveyHandler = new SurveyHandlerMobile(context, preferences.getLastAddedSurveyTemplateNumber());

        surveysRepository = new SurveysRepositoryMobile(context);
        surveysTemplateControl = new SurveysTemplateControl(surveyHandler);
        answeringSurveyControl = new AnsweringSurveyControl(surveyHandler);

        FileHandler fileHandler = new FileHandler();
        fileHandler.prepareLoadingDirectories();

        return true;
    }

    public void saveLastAddedSurveyTemplateNumber(int lastTemplateNumber){
        preferences.saveLastAddedSurveyTemplateNumber(lastTemplateNumber);
    }
}
