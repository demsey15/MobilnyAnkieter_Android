package bohonos.demski.mieldzioc.application;

import android.app.ActionBar;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import bohonos.demski.mieldzioc.networkConnection.ServerConnectionFacade;
import bohonos.demski.mieldzioc.survey.Survey;

/**
 * Created by Dominik on 2015-05-19.
 */
public class ServerFacadeMobile{


    ServerConnectionFacade facade = new ServerConnectionFacade();


    public boolean authenticate(String usersId, char[] password) throws TimeoutException,
            ExecutionException, InterruptedException {
        AsyncTask<Object, Void, Boolean> asyncTask = (new AsyncTask<Object, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Object... params) {
                return facade.authenticate((String) params[0], (char[]) params[1]);
            }
        }).execute(usersId, password);

           return asyncTask.get(40, TimeUnit.SECONDS);
    }

    public int getInterviewerCreatingPrivileges(String interviewerId, String usersId, char[] password)
            throws InterruptedException, ExecutionException, TimeoutException {
        AsyncTask<Object, Void, Integer> asyncTask = (new AsyncTask<Object, Void, Integer>() {
            @Override
            protected Integer doInBackground(Object... params) {
                return facade.getInterviewerCreatingPrivileges((String) params[0], (String) params[1],
                        (char[]) params[2]);
            }
        }).execute(interviewerId, usersId, password);

        return asyncTask.get(40, TimeUnit.SECONDS);
    }

    public Survey getSurveyTemplate(String idOfSurveys, String usersId, char[] password) throws InterruptedException,
            ExecutionException, TimeoutException {
        AsyncTask<Object, Void, Survey> asyncTask = (new AsyncTask<Object, Void, Survey>() {
            @Override
            protected Survey doInBackground(Object... params) {
                return facade.getSurveyTemplate((String) params[0], (String) params[1],
                        (char[]) params[2]);
            }
        }).execute(idOfSurveys, usersId, password);
        return asyncTask.get(40, TimeUnit.SECONDS);
    }


    public List<String> getActiveIdTemplateForInterviewer(String interviewerId, String usersId, char[] password) throws InterruptedException, ExecutionException, TimeoutException {
        AsyncTask<Object, Void, List<String>> asyncTask = (new AsyncTask<Object, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Object... params) {
                return facade.getActiveIdTemplateForInterviewer((String) params[0],
                        (String) params[1], (char[]) params[2]);
            }
        }).execute(interviewerId, usersId, password);

        return asyncTask.get(40, TimeUnit.SECONDS);
    }


    /*
    if(!isNetworkAvailable()) Toast.makeText(getApplicationContext(), "Brak sieci", Toast.LENGTH_SHORT).show();
    else Toast.makeText(getApplicationContext(), "Jest siec", Toast.LENGTH_SHORT).show();
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            ServerConnectionFacade ser = new ServerConnectionFacade();
            ser.sendSurveyTemplate(survey, ApplicationState.
                            getInstance(getApplicationContext()).
                            getLoggedInterviewer().getId(),
                    new char[] {'a', 'b', 'c'});
        }
    });
    t.start();
    */

}
