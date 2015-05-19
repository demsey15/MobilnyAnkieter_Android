package bohonos.demski.mieldzioc.application;

import android.content.Context;

import java.util.List;

import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.controls.SurveysTemplateControl;
import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.survey.Survey;
import bohonos.demski.mieldzioc.survey.SurveyHandler;
import bohonos.demski.mieldzioc.survey.SurveysRepository;

/**
 * Created by Dominik Demski on 2015-05-02.
 */
public class ApplicationState {
    private Interviewer loggedInterviewer;
    private char[] password;
    private Context context;
    private SurveyHandlerMobile surveyHandler;
    private SurveysRepositoryMobile surveysRepository;
    private SurveysTemplateControl surveysTemplateControl;
    private AnsweringSurveyControl answeringSurveyControl;
    private UsersPreferences preferences;

    public SurveyHandler getSurveyHandler() {
        return surveyHandler;
    }

    public SurveysRepository getSurveysRepository() {
        return surveysRepository;
    }

    private static ApplicationState instance;

    public SurveysTemplateControl getSurveysTemplateControl() {
        return surveysTemplateControl;
    }

    public AnsweringSurveyControl getAnsweringSurveyControl() {
        return answeringSurveyControl;
    }

    private ApplicationState(Context context){
        this.context = context;
    }

    public char[] getPassword() {
        return password.clone();
    }

    public static ApplicationState getInstance(Context context){
        return (instance == null)? (instance = new ApplicationState(context)):instance;
        }

   public boolean prepareSurveyHandler(List<Survey> surveys){
       if(instance == null) return false;
       if(surveys == null) {
           surveyHandler = new SurveyHandlerMobile(context, preferences.    //jeœli nie przekazano listy szablonów
                                                                        //pobierz wszystkie
                   getLastAddedSurveyTemplateNumber());
       }
       else{                                                      //jeœli przekazano listê szablonów
           surveyHandler = new SurveyHandlerMobile(context, preferences.
                   getLastAddedSurveyTemplateNumber(), surveys);
       }
       surveysRepository = new SurveysRepositoryMobile(context);
       surveysTemplateControl = new SurveysTemplateControl(surveyHandler);
       answeringSurveyControl = new AnsweringSurveyControl(surveyHandler);

       return true;
   }

    public boolean logIn(Interviewer interviewer, char[] password){
        if(instance == null) return false;
        this.loggedInterviewer = interviewer;
        this.password = password;
        preferences = new UsersPreferences(context, interviewer);
        return true;
    }

    public void saveLastAddedSurveyTemplateNumber(int lastTemplateNumber){
        preferences.saveLastAddedSurveyTemplateNumber(lastTemplateNumber);
    }

    public Interviewer getLoggedInterviewer() {
        return loggedInterviewer;
    }

}
