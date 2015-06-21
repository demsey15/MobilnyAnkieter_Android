package bohonos.demski.mieldzioc.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import bohonos.demski.mieldzioc.application.DateAndTimeService;
import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.questions.Question;
import bohonos.demski.mieldzioc.survey.Survey;

/**
 * Created by Dominik on 2015-05-09.
 *
 * Adapter zajmujący się odpowiedziami ankietowanych.
 */
public class AnsweringSurveyDBAdapter {

    private static final String DEBUG_TAG = "SqLiteAnsweringSurveyDB";

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    public AnsweringSurveyDBAdapter(Context context) {
        this.context = context;
    }

    public AnsweringSurveyDBAdapter open(){
        dbHelper = new DatabaseHelper(context);
        Log.d("Otwieram", "Otwieram połączenie z bazą!");
        try {
            db = dbHelper.getWritableDatabase();
        }
        catch(SQLiteException e){
            Log.d(DEBUG_TAG, "Nie otrzymalem dostepu do bazy danych - przy operacji dodawania danych");
        }
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public void deleteAnswers(Survey survey){
        open();
        db.delete(DatabaseHelper.ANSWERS_TABLE, DatabaseHelper.KEY_SURVEY_SADB + " = " +
                survey.getIdOfSurveys() + " AND " + DatabaseHelper.KEY_NO_FILLED_SURVEY_SADB + " = "
                + survey.getNumberOfSurvey() + " AND " + DatabaseHelper.KEY_INTERVIEWER_SADB +
                " = " + survey.getInterviewer().getId(), null);
        db.delete(DatabaseHelper.FILLED_SURVEYS_TABLE, DatabaseHelper.KEY_SURVEY_FSDB + " = " +
                survey.getIdOfSurveys() + " AND " + DatabaseHelper.KEY_NO_FILLED_SURVEY_FSDB + " = "
                + survey.getNumberOfSurvey() + " AND " + DatabaseHelper.KEY_INTERVIEWER_FSDB + " = "
                + survey.getInterviewer().getId(), null);
        close();
    }
    /**
     * Zwraca listę wszystkich odpowiedzi dla ankiet, które przeprowadził dany ankieter.
     * @param interviewer ankieter, który przeprowadził ankiety.
     * @return
     */
    public List<Survey> getAnswersForInterviewer(Interviewer interviewer){
        open();
        Cursor cursor = db.query(DatabaseHelper.FILLED_SURVEYS_TABLE, new String[]
                        {DatabaseHelper.KEY_SURVEY_FSDB, DatabaseHelper.KEY_NO_FILLED_SURVEY_FSDB,
                                DatabaseHelper.KEY_FROM_DATE_FSDB,
                        DatabaseHelper.KEY_TO_DATE_FSDB},
                DatabaseHelper.KEY_INTERVIEWER_FSDB + " = " + interviewer.getId(),
                null, null, null, null);
        List<Survey> surveys = new ArrayList<>();
        while(cursor.moveToNext()){
            DataBaseAdapter db = new DataBaseAdapter(context);
            Survey survey = db.getSurveyTemplate(cursor.getString(0));
            if(survey != null){
                survey.setInterviewer(interviewer);
                for(int j = 0; j < survey.questionListSize(); j++){
                    List<String> answers = getAnswers(cursor.getString(0), j, interviewer.getId()); //pobierz odpowiedzi dla i-tego pytania
                    Question question = survey.getQuestion(j);
                    question.setUserAnswers(answers);
                }
                survey.setNumberOfSurvey(cursor.getInt(1));
                GregorianCalendar from = DateAndTimeService.getDateFromStringYYYYMMDDHHMMSS(cursor.getString(2));
                GregorianCalendar to = DateAndTimeService.getDateFromStringYYYYMMDDHHMMSS(cursor.getString(3));

                if(from != null && to != null){
                    survey.setStartTime(from);
                    survey.setFinishTime(to);
                    surveys.add(survey);
                }
            }
        }
        close();
        return surveys;
    }


    public List<String> getAnswers(String idOfSurveys, int questionNumber, String interviewerId){
        Cursor cursor = db.query(DatabaseHelper.ANSWERS_TABLE, new String[]
                        {DatabaseHelper.KEY_ANSWER_SADB, DatabaseHelper.KEY_ANSWER_NUMBER_SADB},
                DatabaseHelper.KEY_SURVEY_SADB + " = " + idOfSurveys + " AND " +
                        DatabaseHelper.KEY_QUESTION_NUMBER_SADB + " = " + idOfSurveys + questionNumber
                        + " AND " + DatabaseHelper.KEY_INTERVIEWER_SADB + " = " + interviewerId
                        , null, null, null, DatabaseHelper.KEY_ANSWER_NUMBER_SADB + " ASC");

        List<String> answers = new ArrayList<>();

        while (cursor.moveToNext()) {
            String answer = (cursor.isNull(0))? null : cursor.getString(0);
            if(answer != null)
                answers.add(answer);
        }
        Log.d("GET_ANSWERS_DB", "Odczytałem " + answers.size() + " odpowiedzi: " + Arrays.toString(answers.toArray()));
        return answers;
    }

    public boolean addAnswers(Survey survey){
        open();
        ContentValues filledSurveyValues = new ContentValues();
        filledSurveyValues.put(DatabaseHelper.KEY_SURVEY_FSDB, survey.getIdOfSurveys());
        filledSurveyValues.put(DatabaseHelper.KEY_NO_FILLED_SURVEY_FSDB, survey.getNumberOfSurvey());
        filledSurveyValues.put(DatabaseHelper.KEY_INTERVIEWER_FSDB, survey.getInterviewer().getId());
        filledSurveyValues.put(DatabaseHelper.KEY_FROM_DATE_FSDB, DateAndTimeService.
                getDateAsDBString(survey.getStartTime()));
        filledSurveyValues.put(DatabaseHelper.KEY_TO_DATE_FSDB, DateAndTimeService.
                getDateAsDBString(survey.getFinishTime()));

        if(db.insert(DatabaseHelper.FILLED_SURVEYS_TABLE, null, filledSurveyValues) == -1) {
            close();
            return false;
        }
        for(int i = 0; i < survey.questionListSize(); i++) {
            Question question = survey.getQuestion(i);
            List<String> answers = question.getUserAnswersAsStringList();
            Log.d("SAVE_ANSWERS_DB", "powinienem zapisać " + answers.size() + " odpowiedzi");
            if (answers.size() == 0) {                              //brak odpowiedzi użytkownika
                ContentValues answersValues = new ContentValues();
                answersValues.put(DatabaseHelper.KEY_SURVEY_SADB, survey.getIdOfSurveys());
                answersValues.put(DatabaseHelper.KEY_NO_FILLED_SURVEY_SADB, survey.getNumberOfSurvey());
                answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_SADB, 0);
                answersValues.put(DatabaseHelper.KEY_QUESTION_NUMBER_SADB, survey.getIdOfSurveys() + i);
                answersValues.putNull(DatabaseHelper.KEY_ANSWER_SADB);  //jeśli nie ma odpowiedzi wstaw null
                answersValues.put(DatabaseHelper.KEY_INTERVIEWER_SADB, survey.getInterviewer().getId());
                if(db.insert(DatabaseHelper.ANSWERS_TABLE, null, answersValues) == -1){
                    close();
                    return false;
                }
            } else {
                int j = 0;
                for (String answer : answers) {
                    ContentValues answersValues = new ContentValues();
                    answersValues.put(DatabaseHelper.KEY_SURVEY_SADB, survey.getIdOfSurveys());
                    answersValues.put(DatabaseHelper.KEY_NO_FILLED_SURVEY_SADB, survey.getNumberOfSurvey());
                    answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_SADB, j);
                    answersValues.put(DatabaseHelper.KEY_QUESTION_NUMBER_SADB, survey.getIdOfSurveys() + i);
                    answersValues.put(DatabaseHelper.KEY_ANSWER_SADB, answer);
                    answersValues.put(DatabaseHelper.KEY_INTERVIEWER_SADB, survey.getInterviewer().getId());
                    j++;
                    if(db.insert(DatabaseHelper.ANSWERS_TABLE, null, answersValues) == -1){
                        close();
                        return false;
                    }
                    Log.d("SAVE_ANSWERS_DB", "zapisałem " + j + " odpowiedz");
                }
                Log.d("SAVE_ANSWERS_DB", "zapisałem w sumie " + j + " odpowiedzi");
            }
        }
        close();
        return true;
    }
}
