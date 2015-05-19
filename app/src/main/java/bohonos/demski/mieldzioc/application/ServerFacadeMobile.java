package bohonos.demski.mieldzioc.application;

import android.app.ActionBar;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import bohonos.demski.mieldzioc.networkConnection.ServerConnectionFacade;

/**
 * Created by Dominik on 2015-05-19.
 */
public class ServerFacadeMobile   {


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
