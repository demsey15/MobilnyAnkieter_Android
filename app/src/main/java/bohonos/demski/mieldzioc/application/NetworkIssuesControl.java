package bohonos.demski.mieldzioc.application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import bohonos.demski.mieldzioc.dataBase.InterviewerDBAdapter;
import bohonos.demski.mieldzioc.interviewer.Interviewer;

/**
 * Created by Dominik on 2015-05-19.
 */
public class NetworkIssuesControl {
    public static final int NO_NETWORK_CONNECTION = 100;
    public static final int REQUEST_OUT_OF_TIME = 101;
    public static final int UNKNOWN_ERROR_CONNECTION = 102;

    private Context context;
    private ServerFacadeMobile serverFacadeMobile = new ServerFacadeMobile();

    public NetworkIssuesControl(Context context) {
        this.context = context;
    }

    /**
     *
     * @param usersId
     * @param password
     * @return REQUEST_OUT_OF_TIME, UNKNOWN_ERROR_CONNECTION, NO_NETWORK_CONNECTION, 1 - zalogowano,
     * 0 - nie zalogowano.
     */
    public int login(String usersId, char[] password){
        InterviewerDBAdapter db = new InterviewerDBAdapter(context);
        db.open();
        Interviewer interviewer = db.getInterviewer(usersId);

        if (interviewer == null) {   //w bazie danych nie ma takiego ankietera
            if(isNetworkAvailable()){
                try {
                    boolean result = serverFacadeMobile.authenticate(usersId, password);
                    if(result){
                        interviewer = new Interviewer("", "", usersId, new GregorianCalendar());
                        interviewer.setInterviewerPrivileges(false);
                        db.addInterviewer(interviewer, password); //dodaj ankietera do bazy danych
                                                                    // z brakiem uprawnieñ
                                                                    //do tworzenia ankiet
                    }
                    else{
                        db.close();
                        return 0;  //nie zalogowano
                    }
                } catch (TimeoutException e) {
                    db.close();
                    return REQUEST_OUT_OF_TIME;
                } catch (ExecutionException e) {
                    db.close();
                   return UNKNOWN_ERROR_CONNECTION;
                } catch (InterruptedException e) {
                    db.close();
                    return UNKNOWN_ERROR_CONNECTION;
                }
            }
            else{
                db.close();
                return NO_NETWORK_CONNECTION;
            }
        }
        db.close();
        return (ApplicationState.getInstance(context).logIn(interviewer)) ? 1 : 0;
        }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
