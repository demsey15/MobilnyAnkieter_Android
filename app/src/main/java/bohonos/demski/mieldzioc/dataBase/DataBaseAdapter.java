package bohonos.demski.mieldzioc.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.application.DateAndTimeService;
import bohonos.demski.mieldzioc.constraints.IConstraint;
import bohonos.demski.mieldzioc.constraints.NumberConstraint;
import bohonos.demski.mieldzioc.constraints.TextConstraint;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.TextConstraintsFragment;
import bohonos.demski.mieldzioc.questions.GridQuestion;
import bohonos.demski.mieldzioc.questions.OneChoiceQuestion;
import bohonos.demski.mieldzioc.questions.Question;
import bohonos.demski.mieldzioc.questions.ScaleQuestion;
import bohonos.demski.mieldzioc.questions.TextQuestion;
import bohonos.demski.mieldzioc.survey.Survey;

/**
 * Created by Dominik on 2015-05-02.
 */
public class DataBaseAdapter {

    private static final String DEBUG_TAG = "SqLiteSurveyDB";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "survey.db";

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    public DataBaseAdapter(Context context) {
        this.context = context;
    }

    /**
     * Tê metodê wywo³aæ przed wszystkimi innymi.
     * @return
     */
    public DataBaseAdapter open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
            db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    /**
     * Dodaj ankietê do bazy danych.
     * Metoda sama dba o otwarcie i zamkniêcie po³¹czenia z baz¹ danych.
     * @param survey ankieta do dodania.
     * @return
     */
    public boolean addSurvey(Survey survey){
        open();
        int idOfSurveys = survey.getIdOfSurveys();
        int size = survey.questionListSize();

        for(int i = 0; i < size; i++){
            Question question = survey.getQuestion(i);
            String questionNumber = "" + idOfSurveys + i;
            if(addQuestion(question, idOfSurveys, questionNumber) == -1) return false;
            int questionType = question.getQuestionType();
            if(questionType == Question.DROP_DOWN_QUESTION ||
                    questionType == Question.MULTIPLE_CHOICE_QUESTION || questionType ==
                    Question.ONE_CHOICE_QUESTION){
                if(addChoiceAnswers(question, idOfSurveys, questionNumber) == -1)
                    return false;
            }
            else if(questionType == Question.GRID_QUESTION){
                if(addGridAnswers((GridQuestion) question, idOfSurveys, questionNumber) == -1)
                    return false;
            }
            else if(questionType == Question.SCALE_QUESTION){
                if(addScaleAnswers((ScaleQuestion) question, idOfSurveys, questionNumber) == -1)
                    return false;
            }
            else if(questionType == Question.TEXT_QUESTION){
                TextQuestion textQuestion = (TextQuestion) question;
                IConstraint constraint = textQuestion.getConstraint();
                if(constraint instanceof TextConstraint){
                    if(addTextConstraints((TextConstraint) constraint, idOfSurveys, questionNumber)
                        == -1)
                        return false;
                }
                else if(constraint instanceof  NumberConstraint){
                    if(addNumberConstraints((NumberConstraint) constraint, idOfSurveys,
                            questionNumber) == -1)
                        return false;
                }
            }
        }
        close();
        return true;
    }

    public long addQuestion(Question question, int idOfSurveys, String questionNumber){
        ContentValues questionValues = new ContentValues();
        questionValues.put(DatabaseHelper.KEY_ID_SURVEY_QDB, idOfSurveys);
        questionValues.put(DatabaseHelper.KEY_QUESTION_NUMBER_QDB, questionNumber);
        questionValues.put(DatabaseHelper.KEY_QUESTION_QDB, question.getQuestion());
        questionValues.put(DatabaseHelper.KEY_OBLIGATORY_QDB, question.isObligatory());
        questionValues.put(DatabaseHelper.KEY_HINT_QDB, question.getHint());
        questionValues.put(DatabaseHelper.KEY_ERROR_QDB, question.getErrorMessage());
        questionValues.put(DatabaseHelper.KEY_URL_QDB, question.getPictureURL());
        questionValues.put(DatabaseHelper.KEY_TYPE_QDB, question.getQuestionType());
        questionValues.put(DatabaseHelper.KEY_CREATED_DATE_QDB, DateAndTimeService.getToday());
        questionValues.put(DatabaseHelper.KEY_MODIFICATION_DATE_QDB, DateAndTimeService.getToday());
        questionValues.put(DatabaseHelper.KEY_INTERVIEWER_QDB, String.valueOf(
                ApplicationState.getInstance().getLoggedInterviewer().getId()));
        questionValues.put(DatabaseHelper.KEY_MODIFIED_BY_QDB, String.valueOf(
                ApplicationState.getInstance().getLoggedInterviewer().getId()));
        return db.insert(DatabaseHelper.QUESTIONS_TABLE, null, questionValues);
    }
    public long addChoiceAnswers(Question question, int surveyId, String questionNumber){
        List<String> answers = question.getAnswersAsStringList();

        int answersSize = answers.size();

        for(int i = 0; i < answersSize; i++){
            ContentValues answersValues = new ContentValues();
            answersValues.put(DatabaseHelper.KEY_SURVEY_CHADB, surveyId);
            answersValues.put(DatabaseHelper.KEY_QUESTION_CHADB, questionNumber);
            String answerNo = "" + questionNumber + i;
            answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_CHADB, answerNo);
            answersValues.put(DatabaseHelper.KEY_ANSWER_CHADB, answers.get(i));
            if(db.insert(DatabaseHelper.CHOICE_ANSWERS_TABLE, null, answersValues) == -1)
                return -1;
        }
        return answersSize;
    }

    public long addScaleAnswers(ScaleQuestion question, int surveyId, String questionNumber) {
        ContentValues answersValues = new ContentValues();
        answersValues.put(DatabaseHelper.KEY_SURVEY_SCDB, surveyId);
        answersValues.put(DatabaseHelper.KEY_QUESTION_SCDB, questionNumber);
        String answerNo = "" + questionNumber;
        answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_SCDB, answerNo);
        answersValues.put(DatabaseHelper.KEY_MIN_LAB_SCDB, question.getMinLabel());
        answersValues.put(DatabaseHelper.KEY_MAX_LABEL_SCDB, question.getMaxLabel());
        answersValues.put(DatabaseHelper.KEY_MIN_VALUE_SCDB, question.getMinValue());
        answersValues.put(DatabaseHelper.KEY_MAX_VALUE_SCDB, question.getMaxValue());
        return db.insert(DatabaseHelper.SCALE_ANSWERS_TABLE, null, answersValues);
    }

    public long addGridAnswers(GridQuestion question, int surveyId, String questionNumber) {

        ContentValues columnsValues = new ContentValues();
        List<String> rows = question.getRowLabels();
        List<String> columns = question.getColumnLabels();
        int rowsSize = rows.size();
        int columnsSize = columns.size();

        for(int i = 0; i < rowsSize; i++){
            ContentValues answersValues = new ContentValues();
            answersValues.put(DatabaseHelper.KEY_SURVEY_GRDB, surveyId);
            answersValues.put(DatabaseHelper.KEY_QUESTION_GRDB, questionNumber);
            String answerNo = questionNumber + i;
            answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_GRDB, answerNo);
            answersValues.put(DatabaseHelper.KEY_ANSWER_GRDB, rows.get(i));
            if(db.insert(DatabaseHelper.GRID_ROW_ANSWERS_TABLE, null, answersValues) == -1)
                return -1;
        }

        for(int i = 0; i < columnsSize; i++){
            ContentValues answersValues = new ContentValues();
            answersValues.put(DatabaseHelper.KEY_SURVEY_GCDB, surveyId);
            answersValues.put(DatabaseHelper.KEY_QUESTION_GCDB, questionNumber);
            String answerNo = questionNumber + i;
            answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_GCDB, answerNo);
            answersValues.put(DatabaseHelper.KEY_ANSWER_GCDB, columns.get(i));
            if(db.insert(DatabaseHelper.GRID_COLUMN_ANSWERS_TABLE, null, answersValues) == -1)
                return -1;
        }
        return columnsSize + rowsSize;
    }


    public long addNumberConstraints(NumberConstraint constraint, int surveyId,
                                     String questionNumber){
        ContentValues constraintsValues = new ContentValues();
        constraintsValues.put(DatabaseHelper.KEY_SURVEY_NCDB, surveyId);
        constraintsValues.put(DatabaseHelper.KEY_QUESTION_NCDB, questionNumber);
        constraintsValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_NCDB, questionNumber);
        constraintsValues.put(DatabaseHelper.KEY_MIN_VALUE_NCDB, constraint.getMinValue());
        constraintsValues.put(DatabaseHelper.KEY_MAX_VALUE_NCDB, constraint.getMaxValue());
        constraintsValues.put(DatabaseHelper.KEY_MUST_BE_INTEGER_NCDB, constraint.isMustBeInteger());
        constraintsValues.put(DatabaseHelper.KEY_NOT_EQUALS_NCDB, constraint.getNotEquals());
        constraintsValues.put(DatabaseHelper.KEY_NOT_BETWEEN_NCDB,
                constraint.isNotBetweenMaxAndMinValue());
        return db.insert(DatabaseHelper.NUMBER_CONSTRAINTS_TABLE, null, constraintsValues);
    }

    public long addTextConstraints(TextConstraint constraint, int surveyId,
                                     String questionNumber){
        ContentValues constraintsValues = new ContentValues();
        constraintsValues.put(DatabaseHelper.KEY_SURVEY_TCDB, surveyId);
        constraintsValues.put(DatabaseHelper.KEY_QUESTION_TCDB, questionNumber);
        constraintsValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_TCDB, questionNumber);
        constraintsValues.put(DatabaseHelper.KEY_MIN_LENGTH_TCDB, constraint.getMinLength());
        constraintsValues.put(DatabaseHelper.KEY_MAX_LENGTH_TCDB, constraint.getMaxLength());
        constraintsValues.put(DatabaseHelper.KEY_REGEX_TCDB, constraint.getRegex().pattern());
        return db.insert(DatabaseHelper.TEXT_CONSTRAINTS_TABLE, null, constraintsValues);
    }
}
