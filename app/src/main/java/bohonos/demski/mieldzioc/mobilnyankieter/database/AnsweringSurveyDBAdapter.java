package bohonos.demski.mieldzioc.mobilnyankieter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.application.DateAndTimeService;
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
                + survey.getNumberOfSurvey(), null);
        db.delete(DatabaseHelper.FILLED_SURVEYS_TABLE, DatabaseHelper.KEY_SURVEY_FSDB + " = " +
                survey.getIdOfSurveys() + " AND " + DatabaseHelper.KEY_NO_FILLED_SURVEY_FSDB + " = "
                + survey.getNumberOfSurvey(), null);
        close();
    }

    public boolean addAnswers(Survey survey){
        open();
        ContentValues filledSurveyValues = new ContentValues();
        filledSurveyValues.put(DatabaseHelper.KEY_SURVEY_FSDB, survey.getIdOfSurveys());
        filledSurveyValues.put(DatabaseHelper.KEY_NO_FILLED_SURVEY_FSDB, survey.getNumberOfSurvey());
        filledSurveyValues.put(DatabaseHelper.KEY_FILLED_BY_DEVICE_ID_FSDB, survey.getDeviceId());
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