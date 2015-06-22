package bohonos.demski.mieldzioc.application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import bohonos.demski.mieldzioc.dataBase.AnsweringSurveyDBAdapter;
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

    public final static int SERVER_UNAVAILABLE = 38;

    //public static String SERVER_IP = "150.254.79.29";
    public static String SERVER_IP = "95.108.42.87";
  // public static String SERVER_IP = "192.168.145.1";
  //  public static String SERVER_IP = "50.16.43.97";
  //  public static String SERVER_IP = "37.152.19.249";



    private Context context;
    private ServerConnectionFacade serverConnectionFacade = new ServerConnectionFacade(SERVER_IP);


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
        Log.d("LOGIN", "Zaczynam logowanie");
        InterviewerDBAdapter db = new InterviewerDBAdapter(context);
        char[] passwordToSave = password.clone();
        Interviewer interviewer = db.getInterviewer(usersId);

        if (interviewer == null) {   //w bazie danych nie ma takiego ankietera
            if(isNetworkAvailable()) {
                boolean result = serverConnectionFacade.authenticate(usersId, password);
                if (result) {
                    interviewer = new Interviewer("", "", usersId, new GregorianCalendar());
                    interviewer.setInterviewerPrivileges(false);
                    db.addInterviewer(interviewer, passwordToSave); //dodaj ankietera do bazy danych
                    // z brakiem uprawnień
                    //do tworzenia ankiet
                } else {
                        return 0;  //nie zalogowano (nie ważne, czy jest połączenie z serwerem)
                }
            }
            else{
                return NO_NETWORK_CONNECTION;
            }
        }
        else{
            Log.d("LOGIN", "sprawdzam haslo w bazie");
            InterviewerDBAdapter db2 = new InterviewerDBAdapter(context);
            db2.open();
            if(!db2.checkPassword(interviewer.getId(), password)){
                db2.close();
                return 0;
            }
        }
        db.close();
        return (ApplicationState.getInstance(context).logIn(interviewer, passwordToSave)) ? 1 : 0;
        }

    /**
     * Uzgadnia z serwerem uprawnienia użytkownika do tworzenia ankiet. Jeśli okaże się, że nie ma
     * już takiego użytkownika w serwerze (albo został zwolniony), usuwa użytkownika z bazy danych
     * komórki i zwraca BAD_PASSWORD
     * @param interviewerId
     * @return BAD_PASSWORD, UNKNOWN_ERROR_CONNECTION, REQUEST_OUT_OF_TIME, NO_NETWORK_CONNECTION,
     * 1 - jeśli ankieter może tworzyć ankiety, 0, jeśli nie.
     */
    public int updateInterviewerCanCreate(String interviewerId){
        if(isNetworkAvailable()){
                int result = serverConnectionFacade.getInterviewerCreatingPrivileges(interviewerId, interviewerId,
                        ApplicationState.getInstance(context).getPassword());
                if(result == ServerConnectionFacade.BAD_PASSWORD) { //nie ma takiego użytkownika, albo jest zwolniony
                    InterviewerDBAdapter db = new InterviewerDBAdapter(context);
                    db.deleteInterviewer(interviewerId);
                    return ServerConnectionFacade.BAD_PASSWORD;
                }
                else if(result == 0 || result == 1){
                    InterviewerDBAdapter db = new InterviewerDBAdapter(context);
                    db.setInterviewerCreatingPrivileges(interviewerId, result == 1);    //ustaw wartość w bazie danych
                    ApplicationState.getInstance(context).getLoggedInterviewer(). //ustaw wartość w stanie użytkownika
                            setInterviewerPrivileges(result == 1);
                    return result;
                }
                else if(result == SERVER_UNAVAILABLE){
                    InterviewerDBAdapter db = new InterviewerDBAdapter(context);
                    db.setInterviewerCreatingPrivileges(interviewerId, false);    //ustaw wartość w bazie danych
                    ApplicationState.getInstance(context).getLoggedInterviewer(). //ustaw wartość w stanie użytkownika
                            setInterviewerPrivileges(false);
                    return SERVER_UNAVAILABLE;
                }
        }
        else{
            InterviewerDBAdapter db = new InterviewerDBAdapter(context);
            db.setInterviewerCreatingPrivileges(interviewerId, false);    //ustaw wartość w bazie danych
            ApplicationState.getInstance(context).getLoggedInterviewer(). //ustaw wartość w stanie użytkownika
                    setInterviewerPrivileges(false);
            return NO_NETWORK_CONNECTION;
        }
        InterviewerDBAdapter db = new InterviewerDBAdapter(context);
        db.setInterviewerCreatingPrivileges(interviewerId, false);    //ustaw wartość w bazie danych
        ApplicationState.getInstance(context).getLoggedInterviewer(). //ustaw wartość w stanie użytkownika
                setInterviewerPrivileges(false);
        return UNKNOWN_ERROR_CONNECTION;
    }

    /**
     * Wczytaj ankiety, które może wypełniać użytkownik do SurveyHandlerMobile.
     * Jeśli jest połączenie z siecią, pobrane sa dane z serwera, jesli nie,
     * używa sie ostatnio pobranych danych.
     * @return FIRST_LOG_IN, BAD_PASSWORD; //chyba zwolniono ankietera, UNKNOWN_ERROR_CONNECTION;
     * REQUEST_OUT_OF_TIME, OPERATION_OK, NO_NETWORK_CONNECTION
     */
    public int prepareTemplatesToFill(){
        Interviewer interviewer =  ApplicationState.getInstance(context).getLoggedInterviewer();
        if(interviewer == null) return FIRST_LOG_IN;
        InterviewerDBAdapter dbInt = new InterviewerDBAdapter(context);
        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
        if(isNetworkAvailable()){
                List<Survey> surveys = new ArrayList<>();
                List<String> surveysId = serverConnectionFacade.getActiveIdTemplateForInterviewer(interviewer.getId(),
                        interviewer.getId(), ApplicationState.getInstance(context).getPassword());
                if(surveysId == null){
                    if(serverConnectionFacade.tryConnection())
                        return ServerConnectionFacade.BAD_PASSWORD; //chyba zwolniono ankietera.
                    else{
                        List<String> list = dbInt.getSurveysToFillingForInterviewer(interviewer);
                        List<Survey> surveys2 = new ArrayList<>();
                        for(String survey : list){
                            Survey s = dataBaseAdapter.getSurveyTemplate(survey);
                            if(s != null){
                                surveys2.add(s);
                            }
                        }
                        ApplicationState.getInstance(context).prepareSurveyHandler(surveys);
                        return SERVER_UNAVAILABLE;
                    }
                }
                else{
                    Log.d("PREPARE SURVEYS", "POBRALEM ID ANKIET DO WYPELNIANIA: " + Arrays.toString(surveysId.toArray()));
                    dbInt.updateSurveysToFillingForInterviewer(interviewer, surveysId);
                    for(String survId : surveysId){
                        Survey survey = dataBaseAdapter.getSurveyTemplate(survId);
                        if(survey == null){     //jeżeli szablonu nie ma w bazie danych
                            Log.d("PREPARE SURVEYS", "SZABLONU " + survId + "  NIE MA W BAZIE - POBIERAM...");
                            if(isNetworkAvailable()){       //spróbuj go pobrać
                                Survey s = serverConnectionFacade.getSurveyTemplate(survId, interviewer.getId(),
                                        ApplicationState.getInstance(context).getPassword());
                                if(s != null){
                                    Log.d("PREPARE SURVEYS", "POBRANO ANKIETE: " + s.getTitle());
                                    dataBaseAdapter.addSurveyTemplate(s, SurveyHandler.ACTIVE, true); //dodaj uaktualniony szablon
                                    surveys.add(s);
                                }
                                else{
                                    Log.d("PREPARE SURVEYS", "NIE UDALO SIE POBRAC: " + survId + " = " + s);
                                }
                            }
                        }
                        else{                       //jesli w bazie szablon jest jako niekatywny
                            Log.d("PREPARE SURVEYS", "SZABLON " + survId + "  NIEAKTUALNY - POBIERAM...");
                            if(dataBaseAdapter.getSurveyStatus(survId) != SurveyHandler.ACTIVE){
                                if(isNetworkAvailable()){       //spróbuj go pobrać
                                    Survey s = serverConnectionFacade.getSurveyTemplate(survId, interviewer.getId(),
                                            ApplicationState.getInstance(context).getPassword());
                                    if(s != null){
                                      //  Log.d("PREPARE SURVEYS", "POBRANO ANKIETE: " + s.getTitle() + " pyt. 1: " + s.getQuestion(0).getQuestion());
                                        dataBaseAdapter.deleteSurveyTemplate(survId); //usuń stary szablon
                                        dataBaseAdapter.addSurveyTemplate(s, SurveyHandler.ACTIVE, true); //dodaj uaktualniony szablon
                                        surveys.add(s);     //dodaj nowy
                                    }
                                    else{
                                        Log.d("PREPARE SURVEYS", "NIE UDALO SIE POBRAC: " + survId + " = " + s);
                                    }
                                }
                            }
                            else
                                surveys.add(survey);
                        }
                    }
                    ApplicationState.getInstance(context).prepareSurveyHandler(surveys);
                    return ServerConnectionFacade.OPERATION_OK;
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

    /**
     * Wysyła na serwer wypełnioną ankietę, usuwa ją z bazy danych
     * (nawet jeśli serwer jej nie przyjmie -
     * z różnych powodów (może nie można już wypełniać ankiet albo taka ankieta jest już
     * w serwerze.
     * @param survey ankieta do wysłania
     * @return RESULT_OK albo NO_NETWORK_CONNECTION
     */
    public int sendFilledSurvey(Survey survey){
        if(isNetworkAvailable()){
            ApplicationState applicationState =  ApplicationState.getInstance(context);
            Interviewer interviewer =applicationState.getLoggedInterviewer();
            AnsweringSurveyDBAdapter db = new AnsweringSurveyDBAdapter(context);

            Log.d("SEND_FILLED", "Wysyłam: " + survey.getIdOfSurveys() + " : " + survey.getNumberOfSurvey());
            serverConnectionFacade.sendFilledSurvey(survey,
                    interviewer.getId(), applicationState.getPassword());

            db.deleteAnswers(survey);
            return ServerConnectionFacade.OPERATION_OK;
        }
        else return NO_NETWORK_CONNECTION;
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
