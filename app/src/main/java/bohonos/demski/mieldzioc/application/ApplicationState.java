package bohonos.demski.mieldzioc.application;

import bohonos.demski.mieldzioc.interviewer.Interviewer;

/**
 * Created by Dominik on 2015-05-02.
 */
public class ApplicationState {
    private Interviewer loggedInterviewer;

    private static ApplicationState instance;

    private ApplicationState(){

    }

    public static ApplicationState getInstance(){
        return (instance == null)? (instance = new ApplicationState()) : instance;
    }
    public Interviewer getLoggedInterviewer() {
        return loggedInterviewer;
    }

    public void setLoggedInterviewer(Interviewer loggedInterviewer) {
        this.loggedInterviewer = loggedInterviewer;
    }
}
