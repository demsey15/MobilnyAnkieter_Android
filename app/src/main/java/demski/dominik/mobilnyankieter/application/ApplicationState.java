package demski.dominik.mobilnyankieter.application;

import android.content.Context;

import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.SurveysTemplateControl;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.ISurveyRepository;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveyHandler;
import demski.dominik.mobilnyankieter.sendingsurvey.creatingsurveysfiles.FileHandler;

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

    public ISurveyRepository getSurveysRepository() {
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
