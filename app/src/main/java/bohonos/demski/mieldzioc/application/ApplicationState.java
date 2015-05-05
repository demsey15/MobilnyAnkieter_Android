package bohonos.demski.mieldzioc.application;

import android.content.Context;

import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.controls.SurveysTemplateControl;
import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.survey.SurveyHandler;

/**
 * Created by Dominik Demski on 2015-05-02.
 */
public class ApplicationState {
    private Interviewer loggedInterviewer;
    private Context context;
    private SurveyHandlerMobile surveyHandler;
    private SurveysTemplateControl surveysTemplateControl;
    private AnsweringSurveyControl answeringSurveyControl;

    public SurveyHandler getSurveyHandler() {
        return surveyHandler;
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
        surveyHandler = new SurveyHandlerMobile(context, 0);   //getLASTSURVEYSTATE!!!!!!!!!!!!!!!!!!!!!!!!!!!
        surveysTemplateControl = new SurveysTemplateControl(surveyHandler);
        answeringSurveyControl = new AnsweringSurveyControl(surveyHandler);

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
