package bohonos.demski.mieldzioc.application;

import android.content.Context;

import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.survey.SurveyHandler;

/**
 * Created by Dominik Demski on 2015-05-02.
 */
public class ApplicationState {
    private Interviewer loggedInterviewer;
    private Context context;

    public SurveyHandler getSurveyHandler() {
        return surveyHandler;
    }

    private SurveyHandlerMobile surveyHandler;

    private static ApplicationState instance;

    private ApplicationState(Context context){
        this.context = context;
        surveyHandler = new SurveyHandlerMobile(context, 0);   //getLASTSURVEYSTATE!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    public static ApplicationState getInstance(Context context){
        return (instance == null)? (instance = new ApplicationState(context)):instance;
        }
    public Interviewer getLoggedInterviewer() {
        return loggedInterviewer;
    }

    public void setLoggedInterviewer(Interviewer loggedInterviewer) {
        this.loggedInterviewer = loggedInterviewer;
    }
}
