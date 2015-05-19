package bohonos.demski.mieldzioc.application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import bohonos.demski.mieldzioc.dataBase.DataBaseAdapter;
import bohonos.demski.mieldzioc.dataBase.InterviewerDBAdapter;
import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.networkConnection.ServerConnectionFacade;
import bohonos.demski.mieldzioc.survey.Survey;
import bohonos.demski.mieldzioc.survey.SurveyHandler;

/**
 * Created by Dominik on 2015-05-19.
 */
public class NetworkIssuesControl {
    public static final int NO_NETWORK_CONNECTION = 100;
    public static final int REQUEST_OUT_OF_TIME = 101;
    public static final int UNKNOWN_ERROR_CONNECTION = 102;
    public static final int FIRST_LOG_IN = 103;

    private Context context;
    private ServerFacadeMobile serverFacadeMobile = new ServerFacadeMobile();
    private ServerConnectionFacade serverConnectionFacade = new ServerConnectionFacade();

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
        char[] passwordToSave = password.clone();
        Interviewer interviewer = db.getInterviewer(usersId);

        if (interviewer == null) {   //w bazie danych nie ma takiego ankietera
            if(isNetworkAvailable()){
                try {
                    boolean result = serverFacadeMobile.authenticate(usersId, password);
                    if(result){
                        interviewer = new Interviewer("", "", usersId, new GregorianCalendar());
                        interviewer.setInterviewerPrivileges(false);
                        db.addInterviewer(interviewer, passwordToSave); //dodaj ankietera do bazy danych
                                                                    // z brakiem uprawnieñ
                                                                    //do tworzenia ankiet
                    }
                    else{

                        return 0;  //nie zalogowano
                    }
                } catch (TimeoutException e) {

                    return REQUEST_OUT_OF_TIME;
                } catch (ExecutionException e) {

                   return UNKNOWN_ERROR_CONNECTION;
                } catch (InterruptedException e) {

                    return UNKNOWN_ERROR_CONNECTION;
                }
            }
            else{

                return NO_NETWORK_CONNECTION;
            }
        }
        db.close();
        return (ApplicationState.getInstance(context).logIn(interviewer, passwordToSave)) ? 1 : 0;
        }

    /**
     * Uzgadnia z serwerem uprawnienia u¿ytkownika do tworzenia ankiet. Jeœli oka¿e siê, ¿e nie ma
     * ju¿ takiego u¿ytkownika w serwerze (albo zosta³ zwolniony), usuwa u¿ytkownika z bazy danych
     * komórki i zwraca BAD_PASSWORD
     * @param interviewerId
     * @return BAD_PASSWORD, UNKNOWN_ERROR_CONNECTION, REQUEST_OUT_OF_TIME, NO_NETWORK_CONNECTION,
     * 1 - jeœli ankieter mo¿e tworzyæ ankiety, 0, jeœli nie.
     */
    public int updateInterviewerCanCreate(String interviewerId){
        if(isNetworkAvailable()){
            try {
                int result = serverFacadeMobile.getInterviewerCreatingPrivileges(interviewerId, interviewerId,
                        ApplicationState.getInstance(context).getPassword());
                if(result == ServerConnectionFacade.BAD_PASSWORD){ //nie ma takiego u¿ytkownika, albo jest zwolniony
                    InterviewerDBAdapter db = new InterviewerDBAdapter(context);
                    db.deleteInterviewer(interviewerId);
                    return ServerConnectionFacade.BAD_PASSWORD;
                }
                else if(result == 0 || result == 1){
                    InterviewerDBAdapter db = new InterviewerDBAdapter(context);
                    db.setInterviewerCreatingPrivileges(interviewerId, result == 1);    //ustaw wartoœæ w bazie danych
                    ApplicationState.getInstance(context).getLoggedInterviewer(). //ustaw wartoœæ w stanie u¿ytkownika
                            setInterviewerPrivileges(result == 1);
                    return result;
                }
            } catch (InterruptedException e) {
                InterviewerDBAdapter db = new InterviewerDBAdapter(context);
                db.setInterviewerCreatingPrivileges(interviewerId, false);    //ustaw wartoœæ w bazie danych
                ApplicationState.getInstance(context).getLoggedInterviewer(). //ustaw wartoœæ w stanie u¿ytkownika
                        setInterviewerPrivileges(false);
                return UNKNOWN_ERROR_CONNECTION;
            } catch (ExecutionException e) {
                InterviewerDBAdapter db = new InterviewerDBAdapter(context);
                db.setInterviewerCreatingPrivileges(interviewerId, false);    //ustaw wartoœæ w bazie danych
                ApplicationState.getInstance(context).getLoggedInterviewer(). //ustaw wartoœæ w stanie u¿ytkownika
                        setInterviewerPrivileges(false);
                return UNKNOWN_ERROR_CONNECTION;
            } catch (TimeoutException e) {
                InterviewerDBAdapter db = new InterviewerDBAdapter(context);
                db.setInterviewerCreatingPrivileges(interviewerId, false);    //ustaw wartoœæ w bazie danych
                ApplicationState.getInstance(context).getLoggedInterviewer(). //ustaw wartoœæ w stanie u¿ytkownika
                        setInterviewerPrivileges(false);
                return REQUEST_OUT_OF_TIME;
            }
        }
        else{
            InterviewerDBAdapter db = new InterviewerDBAdapter(context);
            db.setInterviewerCreatingPrivileges(interviewerId, false);    //ustaw wartoœæ w bazie danych
            ApplicationState.getInstance(context).getLoggedInterviewer(). //ustaw wartoœæ w stanie u¿ytkownika
                    setInterviewerPrivileges(false);
            return NO_NETWORK_CONNECTION;
        }
        InterviewerDBAdapter db = new InterviewerDBAdapter(context);
        db.setInterviewerCreatingPrivileges(interviewerId, false);    //ustaw wartoœæ w bazie danych
        ApplicationState.getInstance(context).getLoggedInterviewer(). //ustaw wartoœæ w stanie u¿ytkownika
                setInterviewerPrivileges(false);
        return UNKNOWN_ERROR_CONNECTION;
    }

    /**
     * Wczytaj ankiety, które mo¿e wype³niaæ u¿ytkownik do SurveyHandlerMobile.
     * Jeœli jest po³¹czenie z sieci¹, pobrane sa dane z serwera, jesli nie,
     * u¿ywa sie ostatnio pobranych danych.
     * @return FIRST_LOG_IN, BAD_PASSWORD; //chyba zwolniono ankietera, UNKNOWN_ERROR_CONNECTION;
     * REQUEST_OUT_OF_TIME, OPERATION_OK, NO_NETWORK_CONNECTION
     */
    public int prepareTemplatesToFill(){
        Interviewer interviewer =  ApplicationState.getInstance(context).getLoggedInterviewer();
        if(interviewer == null) return FIRST_LOG_IN;
        InterviewerDBAdapter dbInt = new InterviewerDBAdapter(context);
        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
        if(isNetworkAvailable()){
            ServerFacadeMobile facade = new ServerFacadeMobile();
            try {
                List<Survey> surveys = new ArrayList<>();
                List<String> surveysId = facade.getActiveIdTemplateForInterviewer(interviewer.getId(),
                        interviewer.getId(), ApplicationState.getInstance(context).getPassword());
                if(surveysId == null){
                    return ServerConnectionFacade.BAD_PASSWORD; //chyba zwolniono ankietera.
                }
                else{
                    dbInt.updateSurveysToFillingForInterviewer(interviewer, surveysId);
                    for(String survId : surveysId){
                        Survey survey = dataBaseAdapter.getSurveyTemplate(survId);
                        if(survey == null){     //je¿eli szablonu nie ma w bazie danych
                            if(isNetworkAvailable()){       //spróbuj go pobraæ
                                Survey s = facade.getSurveyTemplate(survId, interviewer.getId(),
                                        ApplicationState.getInstance(context).getPassword());
                                if(s != null){
                                    surveys.add(s);
                                }
                            }
                        }
                        else{
                            surveys.add(survey);
                        }
                    }
                    ApplicationState.getInstance(context).prepareSurveyHandler(surveys);
                    return ServerConnectionFacade.OPERATION_OK;
                }
            } catch (InterruptedException e) {
               return UNKNOWN_ERROR_CONNECTION;
            } catch (ExecutionException e) {
                return UNKNOWN_ERROR_CONNECTION;
            } catch (TimeoutException e) {
                return REQUEST_OUT_OF_TIME;
            }
        }
        else {
            List<String> list = dbInt.getSurveysToFillingForInterviewer(interviewer);
            List<Survey> surveys = new ArrayList<>();
            for(String survey : list){
                Survey s = dataBaseAdapter.getSurveyTemplate(survey);
                if(s != null){
                    surveys.add(s);
                }
            }
            ApplicationState.getInstance(context).prepareSurveyHandler(surveys);
            return NO_NETWORK_CONNECTION;
        }
    }

    public int sendSurveyTemplate(Survey survey){
        if(isNetworkAvailable()) {
            return serverConnectionFacade.sendSurveyTemplate(survey, ApplicationState.getInstance(context).
                    getLoggedInterviewer().getId(), ApplicationState.getInstance(context).getPassword());
        }
        else{
            return NO_NETWORK_CONNECTION;
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
