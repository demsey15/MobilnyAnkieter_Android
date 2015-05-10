package bohonos.demski.mieldzioc.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.List;

import bohonos.demski.mieldzioc.application.DateAndTimeService;
import bohonos.demski.mieldzioc.questions.Question;
import bohonos.demski.mieldzioc.survey.Survey;

/**
 * Created by Dominik on 2015-05-09.
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
        Log.d("Otwieram", "Otwieram po³¹czenie z baz¹!");
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

        if(db.insert(DatabaseHelper.FILLED_SURVEYS_TABLE, null, filledSurveyValues) == -1)
            return false;
        for(int i = 0; i < survey.questionListSize(); i++) {
            Question question = survey.getQuestion(i);
            List<String> answers = question.getUserAnswersAsStringList();
            if (answers.size() == 0) {
                ContentValues answersValues = new ContentValues();
                answersValues.put(DatabaseHelper.KEY_SURVEY_SADB, survey.getIdOfSurveys());
                answersValues.put(DatabaseHelper.KEY_NO_FILLED_SURVEY_SADB, survey.getNumberOfSurvey());
                answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_SADB, 0);
                answersValues.put(DatabaseHelper.KEY_QUESTION_NUMBER_SADB, survey.getIdOfSurveys() + i);
                if(db.insert(DatabaseHelper.ANSWERS_TABLE, null, answersValues) == -1) return false;
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
                    if(db.insert(DatabaseHelper.ANSWERS_TABLE, null, answersValues) == -1) return false;
                }
            }
        }
        close();
        return true;
    }
}
