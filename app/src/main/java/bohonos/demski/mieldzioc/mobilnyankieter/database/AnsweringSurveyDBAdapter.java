package bohonos.demski.mieldzioc.mobilnyankieter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.filledsurveys.FilledSurveysActionsActivity;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import bohonos.demski.mieldzioc.mobilnyankieter.utilities.DateAndTimeService;

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

    public void deleteAnswers(String idOfSurveys, boolean sent, boolean notSent, boolean all, int mode){
        open();

        if((sent && notSent) || (sent && all) || (all && notSent)){
            throw new IllegalArgumentException("sent, notSent, all mustn't be true at the same time.");
        }

        String whereAnswers = "";
        String whereSurveys = "";

        String filteredColumn;

        if(mode == FilledSurveysActionsActivity.JSON_MODE){
            filteredColumn =  DatabaseHelper.KEY_IS_SENT_FSDB;
        }
        else{
            filteredColumn = DatabaseHelper.KEY_WAS_CSV_MADE_FSDB;
        }

        if(all) {
            whereAnswers = DatabaseHelper.KEY_SURVEY_SADB + " = '" + idOfSurveys + "'";
            whereSurveys = DatabaseHelper.KEY_SURVEY_FSDB + " = '" + idOfSurveys + "'";
        }
        else if(sent){
            String in = getInExpression(true);

            whereAnswers = DatabaseHelper.KEY_SURVEY_SADB + " = '" + idOfSurveys + "' AND " +
                    DatabaseHelper.KEY_NO_FILLED_SURVEY_SADB + " IN " + in;
            whereSurveys = DatabaseHelper.KEY_SURVEY_FSDB + " = '" + idOfSurveys + "' AND " +
                    filteredColumn + " = " + 1;
        }
        else if(notSent){
            String in = getInExpression(false);

            whereAnswers = DatabaseHelper.KEY_SURVEY_SADB + " = '" + idOfSurveys + "' AND " +
                    DatabaseHelper.KEY_NO_FILLED_SURVEY_SADB + " IN " + in;
            whereSurveys = DatabaseHelper.KEY_SURVEY_FSDB + " = '" + idOfSurveys + "' AND " +
                    filteredColumn + " = " + 0;
        }

        if(sent || notSent || all) {
            db.delete(DatabaseHelper.ANSWERS_TABLE, whereAnswers, null);
            db.delete(DatabaseHelper.FILLED_SURVEYS_TABLE, whereSurveys, null);
        }
        close();
    }

    @NonNull
    private String getInExpression(boolean ifSent) {
        int sentStatus = (ifSent)? 1 : 0;

        Cursor cursor = db.query(DatabaseHelper.FILLED_SURVEYS_TABLE, new String[] {DatabaseHelper.KEY_NO_FILLED_SURVEY_FSDB},
                DatabaseHelper.KEY_IS_SENT_FSDB + " = " + sentStatus, null, null, null, null);

        String in = "";
        while(cursor.moveToNext()){
            if(!cursor.isNull(0)) {
                in += cursor.getInt(0) + ",";
            }
        }

        if(!in.isEmpty()) {
            in = "(" + in.substring(0, in.length() - 1) + ")";
        }
        else{
            in = "()";
        }

        return in;
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
        filledSurveyValues.put(DatabaseHelper.KEY_IS_SENT_FSDB, 0);
        filledSurveyValues.put(DatabaseHelper.KEY_WAS_CSV_MADE_FSDB, 0);

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

    /**
     * Zwraca listę wszystkich odpowiedzi dla ankiet, które przeprowadził dany ankieter.
     * @return
     */
    public List<Pair<Survey, Boolean>> getAllAnswersWithSentStatus(){
        open();
        Cursor cursor = db.query(DatabaseHelper.FILLED_SURVEYS_TABLE, new String[]
                        {DatabaseHelper.KEY_SURVEY_FSDB, DatabaseHelper.KEY_NO_FILLED_SURVEY_FSDB,
                                DatabaseHelper.KEY_FROM_DATE_FSDB,
                                DatabaseHelper.KEY_TO_DATE_FSDB, DatabaseHelper.KEY_FILLED_BY_DEVICE_ID_FSDB,
                                DatabaseHelper.KEY_IS_SENT_FSDB}, null,
                null, null, null, null);

        List<Pair<Survey, Boolean>> surveys = new ArrayList<>();

        while(cursor.moveToNext()){
            DataBaseAdapter db = new DataBaseAdapter(context);
            Survey survey = db.getSurveyTemplate(cursor.getString(0));
            if(survey != null){
                survey.setDeviceId(cursor.getString(4));
                for(int j = 0; j < survey.questionListSize(); j++){
                    List<String> answers = getAnswers(cursor.getString(0), cursor.getLong(1), j); //pobierz odpowiedzi uzytkownika dla j-tego pytania

                    Log.d("DB_GET_ANSWERS_0", Arrays.toString(answers.toArray()));
                    Question question = survey.getQuestion(j);

                    if(question.getQuestionType() == Question.TIME_QUESTION){
                        answers.addAll(0, Arrays.asList(new String[] {"2", "2", "2"}));
                    }

                    Log.d("DB_GET_ANSW_SETSTATUS", "" + question.setUserAnswers(answers));
                }
                survey.setNumberOfSurvey(cursor.getLong(1));
                GregorianCalendar from = DateAndTimeService.getDateFromStringYYYYMMDDHHMMSS(cursor.getString(2));
                GregorianCalendar to = DateAndTimeService.getDateFromStringYYYYMMDDHHMMSS(cursor.getString(3));

                if(from != null && to != null){
                    survey.setStartTime(from);
                    survey.setFinishTime(to);
                    surveys.add(new Pair<>(survey, (cursor.getInt(5) == 1)));
                }
            }
        }
        close();

        return surveys;
    }

    public List<Pair<Survey, Boolean>> getAllAnswersWithCSVMadeStatus(){
        open();
        Cursor cursor = db.query(DatabaseHelper.FILLED_SURVEYS_TABLE, new String[]
                        {DatabaseHelper.KEY_SURVEY_FSDB, DatabaseHelper.KEY_NO_FILLED_SURVEY_FSDB,
                                DatabaseHelper.KEY_FROM_DATE_FSDB,
                                DatabaseHelper.KEY_TO_DATE_FSDB, DatabaseHelper.KEY_FILLED_BY_DEVICE_ID_FSDB,
                                DatabaseHelper.KEY_WAS_CSV_MADE_FSDB}, null,
                null, null, null, null);

        List<Pair<Survey, Boolean>> surveys = new ArrayList<>();

        while(cursor.moveToNext()){
            DataBaseAdapter db = new DataBaseAdapter(context);
            Survey survey = db.getSurveyTemplate(cursor.getString(0));
            if(survey != null){
                survey.setDeviceId(cursor.getString(4));
                for(int j = 0; j < survey.questionListSize(); j++){
                    List<String> answers = getAnswers(cursor.getString(0), cursor.getLong(1), j); //pobierz odpowiedzi uzytkownika dla j-tego pytania
                    Question question = survey.getQuestion(j);

                    if(question.getQuestionType() == Question.TIME_QUESTION){
                        answers.addAll(0, Arrays.asList(new String[] {"2", "2", "2"}));
                    }

                    question.setUserAnswers(answers);
                }
                survey.setNumberOfSurvey(cursor.getLong(1));
                GregorianCalendar from = DateAndTimeService.getDateFromStringYYYYMMDDHHMMSS(cursor.getString(2));
                GregorianCalendar to = DateAndTimeService.getDateFromStringYYYYMMDDHHMMSS(cursor.getString(3));

                if(from != null && to != null){
                    survey.setStartTime(from);
                    survey.setFinishTime(to);
                    surveys.add(new Pair<>(survey, (cursor.getInt(5) == 1)));
                }
            }
        }
        close();

        return surveys;
    }

    private List<String> getAnswers(String idOfSurveys, long filledSurveyNumber, int questionNumber){
        Cursor cursor = db.query(DatabaseHelper.ANSWERS_TABLE, new String[]
                        {DatabaseHelper.KEY_ANSWER_SADB, DatabaseHelper.KEY_ANSWER_NUMBER_SADB},
                DatabaseHelper.KEY_SURVEY_SADB + " = '" + idOfSurveys + "' AND " +
                        DatabaseHelper.KEY_QUESTION_NUMBER_SADB + " = '" + idOfSurveys + questionNumber
                + "' AND " + DatabaseHelper.KEY_NO_FILLED_SURVEY_SADB + " = " + filledSurveyNumber
                , null, null, null, DatabaseHelper.KEY_ANSWER_NUMBER_SADB + " ASC");

        List<String> answers = new ArrayList<>();

        while (cursor.moveToNext()) {
            String answer = (cursor.isNull(0))? null : cursor.getString(0);
            if(answer != null) {
                answers.add(answer);
            }
        }
        Log.d("GET_ANSWERS_DB", "Odczytałem " + answers.size() + " odpowiedzi: " + Arrays.toString(answers.toArray()));
        return answers;
    }

    public void setSurveyAnswersAsSent(String idOfSurveys){
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_IS_SENT_FSDB, 1);

        db.update(DatabaseHelper.FILLED_SURVEYS_TABLE, contentValues,
                DatabaseHelper.KEY_SURVEY_FSDB + " = '" + idOfSurveys + "'", null);
        close();
    }

    public void setSurveyAnswersWasMadeCSV(String idOfSurveys){
        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_WAS_CSV_MADE_FSDB, 1);

        db.update(DatabaseHelper.FILLED_SURVEYS_TABLE, contentValues,
                DatabaseHelper.KEY_SURVEY_FSDB + " = '" + idOfSurveys + "'", null);
        close();
    }
}
