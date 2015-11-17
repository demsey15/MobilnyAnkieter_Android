package bohonos.demski.mieldzioc.mobilnyankieter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import bohonos.demski.mieldzioc.mobilnyankieter.application.UserPreferences;
import bohonos.demski.mieldzioc.mobilnyankieter.constraints.IConstraint;
import bohonos.demski.mieldzioc.mobilnyankieter.constraints.NumberConstraint;
import bohonos.demski.mieldzioc.mobilnyankieter.constraints.TextConstraint;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.DateTimeQuestion;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.GridQuestion;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.MultipleChoiceQuestion;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.OneChoiceQuestion;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.ScaleQuestion;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.TextQuestion;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import bohonos.demski.mieldzioc.mobilnyankieter.utilities.DateAndTimeService;

/**
 * Created by Dominik Demski on 2015-05-02.
 * Adapter zajmujący się szablonami ankiet.
 */
public class DataBaseAdapter {

    private static final String DEBUG_TAG = "SqLiteSurveyDB";

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    public DataBaseAdapter(Context context) {
        this.context = context;
    }

    /**
     * Tę metodę wywołać przed wszystkimi innymi.
     * @return
     */
    public DataBaseAdapter open(){
        dbHelper = new DatabaseHelper(context);
        Log.d("Otwieram", "Otwieram połączenie z bazą!");
            db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public boolean ifSurveyTemplateInDB(String idOfSurveys){
        Cursor cursor = db.query(DatabaseHelper.SURVEY_TEMPLATE_TABLE, new String[]
                {DatabaseHelper.KEY_ID}, DatabaseHelper.KEY_ID + " = '" + idOfSurveys  + "' ",
                null, null, null, null);

        return cursor.moveToFirst();
    }

    public long getNumberOfFilledSurveysAtThisDevice(String idOfSurveys){
        open();

        Cursor cursor = db.query(DatabaseHelper.NUMBER_OF_FILLED_SURVEYS_TABLE, new String[] {DatabaseHelper.KEY_NUMBER_OF_FILLED_SURVEYS_NFSDB},
                DatabaseHelper.KEY_SURVEY_NFSDB + " = '" + idOfSurveys + "'", null, null, null, null);

        if(cursor.moveToFirst()){
            close();

            return cursor.getLong(0);
        }
        else{
            close();

            return -1;
        }
    }

    public void incrementNumberOfFilledSurveys(String idOfSurveys){
        long previousNumber = getNumberOfFilledSurveysAtThisDevice(idOfSurveys);
        previousNumber++;

        open();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.KEY_NUMBER_OF_FILLED_SURVEYS_NFSDB, previousNumber);

        db.update(DatabaseHelper.NUMBER_OF_FILLED_SURVEYS_TABLE, contentValues, DatabaseHelper.KEY_SURVEY_NFSDB + " = '" + idOfSurveys + "'", null);

        close();
    }

    /**
     * Dodaj ankietę do bazy danych.
     * Metoda sama dba o otwarcie i zamknięcie połączenia z bazą danych.
     * @param survey ankieta do dodania.
     * @param isSent czy ankieta została wysłana już na serwer.
     * @return
     */
    public boolean addSurveyTemplate(Survey survey, int status, boolean isSent){
        String idOfSurveys = survey.getIdOfSurveys();

        long numberOfFilledSurveys = getNumberOfFilledSurveysAtThisDevice(idOfSurveys);

        open();

        if(numberOfFilledSurveys == -1){
            addNewSurveyToNumberOfFilledSurveysTable(idOfSurveys);
        }

        ContentValues templateValues = new ContentValues();
        templateValues.put(DatabaseHelper.KEY_ID, idOfSurveys);
        templateValues.put(DatabaseHelper.KEY_STATUS, status);

        templateValues.put(DatabaseHelper.KEY_CREATED_BY_DEVICE_ID, survey.getDeviceId());

        templateValues.put(DatabaseHelper.KEY_CREATED_DATE, DateAndTimeService.getToday());
        templateValues.put(DatabaseHelper.KEY_MODIFICATION_DATE, DateAndTimeService.getToday());
        templateValues.put(DatabaseHelper.KEY_MODIFIED_BY, survey.getDeviceId());

        String title = survey.getTitle();
        if(title == null) {
            templateValues.putNull(DatabaseHelper.KEY_TITLE);
        }
        else {
            templateValues.put(DatabaseHelper.KEY_TITLE, title);
        }

        String description = survey.getDescription();
        if(description == null){
            templateValues.putNull(DatabaseHelper.KEY_DESCRIPTION);
        }
        else{
            templateValues.put(DatabaseHelper.KEY_DESCRIPTION, description);
        }

        String summary = survey.getSummary();
        if(summary == null){
            templateValues.putNull(DatabaseHelper.KEY_SUMMARY);
        }
        else{
            templateValues.put(DatabaseHelper.KEY_SUMMARY, summary);
        }

        templateValues.put(DatabaseHelper.KEY_SENT, (isSent)? 1 : 0);

        db.insert(DatabaseHelper.SURVEY_TEMPLATE_TABLE, null, templateValues);

        int size = survey.questionListSize();

        for(int i = 0; i < size; i++){
            Question question = survey.getQuestion(i);

            if(addQuestion(question, idOfSurveys, i) == -1){
                close();
                return false;
            }

            int questionType = question.getQuestionType();
            if(questionType == Question.DROP_DOWN_QUESTION ||
                    questionType == Question.MULTIPLE_CHOICE_QUESTION || questionType ==
                    Question.ONE_CHOICE_QUESTION){
                if(addChoiceAnswers(question, idOfSurveys, i) == -1) {
                    close();
                    return false;
                }
            }
            else if(questionType == Question.GRID_QUESTION){
                if(addGridAnswers((GridQuestion) question, idOfSurveys, i) == -1) {
                    close();
                    return false;
                }
            }
            else if(questionType == Question.SCALE_QUESTION){
                if(addScaleAnswers((ScaleQuestion) question, idOfSurveys, i) == -1) {
                    close();
                    return false;
                }
            }
            else if(questionType == Question.TEXT_QUESTION){
                TextQuestion textQuestion = (TextQuestion) question;
                IConstraint constraint = textQuestion.getConstraint();
                if(constraint instanceof TextConstraint){
                    if(addTextConstraints((TextConstraint) constraint, idOfSurveys, i)
                        == -1) {
                        close();
                        return false;
                    }
                }
                else if(constraint instanceof  NumberConstraint){
                    if(addNumberConstraints((NumberConstraint) constraint, idOfSurveys,
                            i) == -1) {
                        close();
                        return false;
                    }
                }
            }
        }
        close();
        Log.d(DEBUG_TAG, "Dodano ankietę do bazy danych; id: " + survey.getIdOfSurveys() +
                ", tytuł: " + survey.getTitle());
        return true;
    }

    private void addNewSurveyToNumberOfFilledSurveysTable(String idOfSurveys) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_SURVEY_NFSDB, idOfSurveys);
        values.put(DatabaseHelper.KEY_NUMBER_OF_FILLED_SURVEYS_NFSDB, 0);

        db.insert(DatabaseHelper.NUMBER_OF_FILLED_SURVEYS_TABLE, null, values);
    }

    private long addQuestion(Question question, String idOfSurveys, int questionNumber){
        ContentValues questionValues = new ContentValues();
        questionValues.put(DatabaseHelper.KEY_ID_SURVEY_QDB, idOfSurveys);
        questionValues.put(DatabaseHelper.KEY_QUESTION_NUMBER_QDB, questionNumber);
        questionValues.put(DatabaseHelper.KEY_QUESTION_QDB, question.getQuestion());
        questionValues.put(DatabaseHelper.KEY_OBLIGATORY_QDB, (question.isObligatory())? 1 : 0);
        questionValues.put(DatabaseHelper.KEY_HINT_QDB, question.getHint());
        questionValues.put(DatabaseHelper.KEY_TYPE_QDB, question.getQuestionType());
        questionValues.put(DatabaseHelper.KEY_CREATED_DATE_QDB, DateAndTimeService.getToday());
        questionValues.put(DatabaseHelper.KEY_MODIFICATION_DATE_QDB, DateAndTimeService.getToday());
        questionValues.put(DatabaseHelper.KEY_MODIFIED_BY_QDB, UserPreferences.getInstance(context).getDeviceId());

        Log.d("DB_SAVE_QTYPE", question.getQuestionType() + " ");

        return db.insert(DatabaseHelper.QUESTIONS_TABLE, null, questionValues);
    }
    private int addChoiceAnswers(Question question, String surveyId, int questionNumber){
        List<String> answers = question.getAnswersAsStringList();
        Log.d("ADD_CHOICE_TEST_ANS", Arrays.toString(answers.toArray()));
        int answersSize = answers.size();

        for(int i = 0; i < answersSize; i++){
            ContentValues answersValues = new ContentValues();
            answersValues.put(DatabaseHelper.KEY_SURVEY_CHADB, surveyId);
            answersValues.put(DatabaseHelper.KEY_QUESTION_CHADB, questionNumber);
            answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_CHADB, i);
            answersValues.put(DatabaseHelper.KEY_ANSWER_CHADB, answers.get(i));

            if(db.insert(DatabaseHelper.CHOICE_ANSWERS_TABLE, null, answersValues) == -1) {
                return -1;
            }
        }

        return answersSize;
    }

    private long addScaleAnswers(ScaleQuestion question, String surveyId, int questionNumber) {
        ContentValues answersValues = new ContentValues();
        answersValues.put(DatabaseHelper.KEY_SURVEY_SCDB, surveyId);
        answersValues.put(DatabaseHelper.KEY_QUESTION_SCDB, questionNumber);
        answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_SCDB, 0);
        answersValues.put(DatabaseHelper.KEY_MIN_LAB_SCDB, question.getMinLabel());
        answersValues.put(DatabaseHelper.KEY_MAX_LABEL_SCDB, question.getMaxLabel());
        answersValues.put(DatabaseHelper.KEY_MIN_VALUE_SCDB, question.getMinValue());
        answersValues.put(DatabaseHelper.KEY_MAX_VALUE_SCDB, question.getMaxValue());

        return db.insert(DatabaseHelper.SCALE_ANSWERS_TABLE, null, answersValues);
    }

    private int addGridAnswers(GridQuestion question, String surveyId, int questionNumber) {
        List<String> rows = question.getRowLabels();
        List<String> columns = question.getColumnLabels();
        int rowsSize = rows.size();
        int columnsSize = columns.size();

        for(int i = 0; i < rowsSize; i++){
            ContentValues answersValues = new ContentValues();
            answersValues.put(DatabaseHelper.KEY_SURVEY_GRDB, surveyId);
            answersValues.put(DatabaseHelper.KEY_QUESTION_GRDB, questionNumber);
            answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_GRDB, i);
            answersValues.put(DatabaseHelper.KEY_ANSWER_GRDB, rows.get(i));
            if(db.insert(DatabaseHelper.GRID_ROW_ANSWERS_TABLE, null, answersValues) == -1){
                return -1;
            }
        }

        for(int i = 0; i < columnsSize; i++){
            ContentValues answersValues = new ContentValues();
            answersValues.put(DatabaseHelper.KEY_SURVEY_GCDB, surveyId);
            answersValues.put(DatabaseHelper.KEY_QUESTION_GCDB, questionNumber);
            answersValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_GCDB, i);
            answersValues.put(DatabaseHelper.KEY_ANSWER_GCDB, columns.get(i));
            if(db.insert(DatabaseHelper.GRID_COLUMN_ANSWERS_TABLE, null, answersValues) == -1) {
                return -1;
            }
        }
        return columnsSize + rowsSize;
    }

    private long addNumberConstraints(NumberConstraint constraint, String surveyId, int questionNumber){
        ContentValues constraintsValues = new ContentValues();
        constraintsValues.put(DatabaseHelper.KEY_SURVEY_NCDB, surveyId);
        constraintsValues.put(DatabaseHelper.KEY_QUESTION_NCDB, questionNumber);
        constraintsValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_NCDB, questionNumber);

        if(constraint.getMinValue() == null) constraintsValues.putNull(DatabaseHelper.KEY_MIN_VALUE_NCDB);
            else constraintsValues.put(DatabaseHelper.KEY_MIN_VALUE_NCDB, constraint.getMinValue());

        if(constraint.getMaxValue() == null) constraintsValues.putNull(DatabaseHelper.KEY_MAX_VALUE_NCDB);
            else constraintsValues.put(DatabaseHelper.KEY_MAX_VALUE_NCDB, constraint.getMaxValue());

        constraintsValues.put(DatabaseHelper.KEY_MUST_BE_INTEGER_NCDB, constraint.isMustBeInteger());

        if(constraint.getNotEquals() == null) constraintsValues.putNull(DatabaseHelper.KEY_NOT_EQUALS_NCDB);
            else constraintsValues.put(DatabaseHelper.KEY_NOT_EQUALS_NCDB, constraint.getNotEquals());

        constraintsValues.put(DatabaseHelper.KEY_NOT_BETWEEN_NCDB,
                constraint.isNotBetweenMaxAndMinValue());
        return db.insert(DatabaseHelper.NUMBER_CONSTRAINTS_TABLE, null , constraintsValues);
    }

    private long addTextConstraints(TextConstraint constraint, String surveyId, int questionNumber){
        ContentValues constraintsValues = new ContentValues();
        constraintsValues.put(DatabaseHelper.KEY_SURVEY_TCDB, surveyId);
        constraintsValues.put(DatabaseHelper.KEY_QUESTION_TCDB, questionNumber);
        constraintsValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_TCDB, questionNumber);

        if(constraint.getMaxLength() == null)
            constraintsValues.putNull(DatabaseHelper.KEY_MAX_LENGTH_TCDB);
        else constraintsValues.put(DatabaseHelper.KEY_MAX_LENGTH_TCDB, constraint.getMaxLength());

        if(constraint.getMinLength() == null)
            constraintsValues.putNull(DatabaseHelper.KEY_MIN_LENGTH_TCDB);
        else constraintsValues.put(DatabaseHelper.KEY_MIN_LENGTH_TCDB, constraint.getMinLength());

        if(constraint.getRegex() != null)
            constraintsValues.put(DatabaseHelper.KEY_REGEX_TCDB, constraint.getRegex().pattern());
        else constraintsValues.putNull(DatabaseHelper.KEY_REGEX_TCDB);
        return db.insert(DatabaseHelper.TEXT_CONSTRAINTS_TABLE, null, constraintsValues);
    }

    public Map<Survey, Integer> getAllSurveyTemplates(){
        open();

        HashMap<Survey, Integer> templates = new HashMap<>();
        Cursor cursor = db.query(DatabaseHelper.SURVEY_TEMPLATE_TABLE, new String[]
                {DatabaseHelper.KEY_ID, DatabaseHelper.KEY_STATUS}, null, null, null, null, null);
        while(cursor.moveToNext()){
            Survey survey = getSurveyTemplate(cursor.getString(0));
            templates.put(survey, cursor.getInt(1));
        }

        close();

        return templates;
    }

    public Map<Survey, Boolean> getAllSurveyTemplatesWithSendStatus(){
        open();

        HashMap<Survey, Boolean> templates = new HashMap<>();

        Cursor cursor = db.query(DatabaseHelper.SURVEY_TEMPLATE_TABLE, new String[]
                {DatabaseHelper.KEY_ID, DatabaseHelper.KEY_SENT}, null, null, null, null, null);

        while(cursor.moveToNext()){
            Survey survey = getSurveyTemplate(cursor.getString(0));

            templates.put(survey, cursor.getInt(1) == 1);
        }

        close();
        return templates;
    }

    /**
     *
     * @param idOfSurveys
     * @return null jesli nie ma ankiety o takim id.
     */
    public Survey getSurveyTemplate(String idOfSurveys){
        open();
        Survey survey = null;
        Cursor cursor = db.query(DatabaseHelper.SURVEY_TEMPLATE_TABLE, new String[]{
                DatabaseHelper.KEY_CREATED_BY_DEVICE_ID, DatabaseHelper.KEY_TITLE,
                DatabaseHelper.KEY_DESCRIPTION, DatabaseHelper.KEY_SUMMARY}, DatabaseHelper.KEY_ID +
                " = '" + idOfSurveys + "'", null, null, null, null);
        if(cursor.moveToFirst()) {
            survey = new Survey(null);
            survey.setDescription((cursor.isNull(2)) ? null : cursor.getString(2));
            survey.setIdOfSurveys(idOfSurveys);
            survey.setSummary((cursor.isNull(3)) ? null : cursor.getString(3));
            survey.setTitle((cursor.isNull(1))? "" : cursor.getString(1));

            String deviceId = cursor.getString(0);
            survey.setDeviceId(deviceId);

            List<Question> questions = getSurveysQuestion(idOfSurveys);
            for (Question question : questions) {
                survey.addQuestion(question);
            }
        }
        close();
        return survey;
    }

    private List<Question> getSurveysQuestion(String idOfSurveys) {
        Cursor cursor = db.query(DatabaseHelper.QUESTIONS_TABLE, new String[]{
                        DatabaseHelper.KEY_OBLIGATORY_QDB, DatabaseHelper.KEY_HINT_QDB,
                        DatabaseHelper.KEY_QUESTION_QDB, DatabaseHelper.KEY_TYPE_QDB
                        }, DatabaseHelper.KEY_ID_SURVEY_QDB + " = '"
                        + idOfSurveys + "' ", null, null, null,
                        DatabaseHelper.KEY_QUESTION_NUMBER_QDB + " ASC");
        int i = 0;
        List<Question> questions = new ArrayList<>();
        while (cursor.moveToNext()) {
            Question question = null;
            int questionType = cursor.getInt(3);

            Log.d("QTYPE_DB_READ", "" + questionType);
            if(questionType == Question.DROP_DOWN_QUESTION) {
                question = new OneChoiceQuestion("");
                List<String> list = getChoiceAnswers(idOfSurveys, i);
                OneChoiceQuestion choiceQuestion = (OneChoiceQuestion) question;
                for (String answer : list) {
                    choiceQuestion.addAnswer(answer);
                }
                choiceQuestion.setIsDropDownList(true);
            }
            else if (questionType == Question.ONE_CHOICE_QUESTION) {
                question = new OneChoiceQuestion("");
                List<String> list = getChoiceAnswers(idOfSurveys, i);
                OneChoiceQuestion choiceQuestion = (OneChoiceQuestion) question;
                for (String answer : list) {
                    choiceQuestion.addAnswer(answer);
                }
                choiceQuestion.setIsDropDownList(false);
            }
            else if (questionType == Question.MULTIPLE_CHOICE_QUESTION) {
                question = new MultipleChoiceQuestion("");
                List<String> list = getChoiceAnswers(idOfSurveys, i);
                MultipleChoiceQuestion choiceQuestion = (MultipleChoiceQuestion) question;
                for (String answer : list) {
                    choiceQuestion.addAnswer(answer);
                }
            }
            else if (questionType == Question.TEXT_QUESTION) {
                question = new TextQuestion("");
                TextQuestion textQuestion = (TextQuestion) question;
                TextConstraint textConstraint = getTextConstraints(idOfSurveys, i);
                if (textConstraint == null) {
                    NumberConstraint numberConstraint = getNumberConstraints(idOfSurveys, i);
                    if (numberConstraint != null) {
                        textQuestion.setConstraint(numberConstraint);
                    }
                } else {
                    textQuestion.setConstraint(textConstraint);
                }
            }
            else if (questionType == Question.SCALE_QUESTION) {
                question = getScaleAnswers(idOfSurveys, i);
            }
            else if(questionType == Question.GRID_QUESTION){
                question = new GridQuestion("");
                GridQuestion gridQuestion = (GridQuestion) question;
                gridQuestion.setRowLabels(getGridRowAnswers(idOfSurveys, i));
                gridQuestion.setColumnLabels(getGridColumnAnswers(idOfSurveys, i));
            }
            else if(questionType == Question.DATE_QUESTION){
                question = new DateTimeQuestion("");
                DateTimeQuestion dateQuestion = (DateTimeQuestion) question;
                dateQuestion.setOnlyDate(true);
            }
            else if(questionType == Question.TIME_QUESTION){
                question = new DateTimeQuestion("");
                DateTimeQuestion dateQuestion = (DateTimeQuestion) question;
                dateQuestion.setOnlyTime(true);
            }
            question.setObligatory(cursor.getInt(0) != 0);
            question.setHint(cursor.getString(1));
            question.setQuestion(cursor.getString(2));
            questions.add(question);
            i++;
        }
        return questions;
    }
    /**
     * Zwraca listę odpowiedzi dla danego szablonu ankiety.
     * @param idOfSurveys id szablonu.
     * @param questionNumber numer pytania (cyfra).
     * @return Lista odpowiedzi.
     */
    private List<String> getChoiceAnswers(String idOfSurveys, int questionNumber){
        List<String> answers = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.CHOICE_ANSWERS_TABLE, new String[] {
                DatabaseHelper.KEY_ANSWER_CHADB}, DatabaseHelper.KEY_SURVEY_CHADB + " = '" +
                idOfSurveys + "' AND " + DatabaseHelper.KEY_QUESTION_CHADB + " = " +
                questionNumber, null, null, null, DatabaseHelper.KEY_ANSWER_NUMBER_CHADB + " ASC");
        while(cursor.moveToNext()){
            answers.add(cursor.getString(0));
        }

        Log.d("GET_CHOICE_TEST_ANSW", Arrays.toString(answers.toArray()));
        return answers;
    }

    private List<String> getGridRowAnswers(String idOfSurveys, int questionNumber){
        List<String> answers = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.GRID_ROW_ANSWERS_TABLE, new String[] {
                DatabaseHelper.KEY_ANSWER_GRDB}, DatabaseHelper.KEY_SURVEY_GRDB + " = '" +
                idOfSurveys + "' AND " + DatabaseHelper.KEY_QUESTION_GRDB + " = " +
                questionNumber, null, null, null, DatabaseHelper.KEY_ANSWER_NUMBER_GRDB + " ASC");
        while(cursor.moveToNext()){
            answers.add(cursor.getString(0));
        }
        return answers;
    }

    private List<String> getGridColumnAnswers(String idOfSurveys, int questionNumber){
        List<String> answers = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.GRID_COLUMN_ANSWERS_TABLE, new String[] {
                DatabaseHelper.KEY_ANSWER_GCDB}, DatabaseHelper.KEY_SURVEY_GCDB + " = '" +
                idOfSurveys + "' AND " + DatabaseHelper.KEY_QUESTION_GCDB + " = " +
                questionNumber, null, null, null, DatabaseHelper.KEY_ANSWER_NUMBER_GCDB + " ASC");
        while(cursor.moveToNext()){
            answers.add(cursor.getString(0));
        }
        return answers;
    }

    /**
     * Zwraca ograniczenia liczbowe dla zadanego pytania.
     * @param idOfSurveys
     * @param questionNumber
     * @return null, jesli nie ma ograniczeń tekstowych.
     */
    private NumberConstraint getNumberConstraints(String idOfSurveys, int questionNumber){
        Cursor cursor = db.query(DatabaseHelper.NUMBER_CONSTRAINTS_TABLE, new String[] {
                DatabaseHelper.KEY_MIN_VALUE_NCDB, DatabaseHelper.KEY_MAX_VALUE_NCDB,
                DatabaseHelper.KEY_NOT_BETWEEN_NCDB, DatabaseHelper.KEY_NOT_EQUALS_NCDB,
                DatabaseHelper.KEY_MUST_BE_INTEGER_NCDB}, DatabaseHelper.KEY_SURVEY_NCDB + " = '" +
                idOfSurveys + "' AND " + DatabaseHelper.KEY_QUESTION_NCDB + " = " +
                questionNumber, null, null, null, null, null);
        NumberConstraint numberConstraint = null;
        if(cursor.moveToFirst()){
            Log.d("DB_NUMBER_CONSTRAINT", "" + cursor.isNull(0));
            numberConstraint = new NumberConstraint((cursor.isNull(0))? null : cursor.getDouble(0),
                    (cursor.isNull(1))? null : cursor.getDouble(1),
                    (cursor.getInt(4) != 0), (cursor.isNull(3))? null : cursor.getDouble(3),
                    (cursor.getInt(2) != 0));
        }
        return numberConstraint;
    }

    /**
     * Zwraca ograniczenia liczbowe dla zadanego pytania.
     * @param idOfSurveys
     * @param questionNumber
     * @return null, jesli nie ma ograniczeń tekstowych.
     */
    private TextConstraint getTextConstraints(String idOfSurveys, int questionNumber){
        Cursor cursor = db.query(DatabaseHelper.TEXT_CONSTRAINTS_TABLE, new String[] {
                DatabaseHelper.KEY_MIN_LENGTH_TCDB, DatabaseHelper.KEY_MAX_LENGTH_TCDB,
                DatabaseHelper.KEY_REGEX_TCDB}, DatabaseHelper.KEY_SURVEY_TCDB + " = '" +
                idOfSurveys + "' AND " + DatabaseHelper.KEY_QUESTION_TCDB + " = " +
                questionNumber, null, null, null, null, null);
        TextConstraint textConstraint = null;
        if(cursor.moveToFirst()){
            Pattern pattern = null;
            if(!cursor.isNull(2)) {
                try {
                    pattern = Pattern.compile(cursor.getString(2));
                } catch (Exception e) {
                    pattern = null;
                }
            }
            textConstraint = new TextConstraint((cursor.isNull(0))? null : cursor.getInt(0),
                    (cursor.isNull(1))? null : cursor.getInt(1), pattern);
        }
        return textConstraint;
    }

    /**
     * Zwraca ScaleQuestions z uzupełnionymi tylko polami dotyczącymi rodzaju odpowiedzi (bez treści
     * pytania na przykład!).
     * @param idOfSurveys
     * @param questionNumber
     * @return null, jeśli nie ma takiego pytania.
     */
    private ScaleQuestion getScaleAnswers(String idOfSurveys, int questionNumber){
        ScaleQuestion scaleQuestion = null;
        Cursor cursor = db.query(DatabaseHelper.SCALE_ANSWERS_TABLE, new String[] {
                DatabaseHelper.KEY_MIN_VALUE_SCDB, DatabaseHelper.KEY_MAX_VALUE_SCDB,
                DatabaseHelper.KEY_MIN_LAB_SCDB, DatabaseHelper.KEY_MAX_LABEL_SCDB},
                DatabaseHelper.KEY_SURVEY_SCDB + " = '" +
                        idOfSurveys + "' AND " + DatabaseHelper.KEY_QUESTION_SCDB + " = " +
                questionNumber, null, null, null, null, null);
        Log.d("SCALE_DB_READ", "" + cursor.getCount());
        if(cursor.moveToFirst()){
            Log.d("SCALE_DB_READ", "jestem " + cursor.getInt(0) + " " + cursor.getInt(1)
            + " " +cursor.getString(2) + " " +cursor.getString(3));
            scaleQuestion = new ScaleQuestion("", false, "", cursor.getInt(0),
                    cursor.getInt(1), cursor.getString(2), cursor.getString(3));
        }

        return scaleQuestion;
    }

    public void setSurveySent(Survey survey, boolean isSent){
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_SENT, (isSent)? 1 : 0);
        db.update(DatabaseHelper.SURVEY_TEMPLATE_TABLE, values, DatabaseHelper.KEY_ID + " = '" +
                survey.getIdOfSurveys() + "' ", null);
        close();
    }

    public boolean isSurveySent(Survey survey){
        open();
        Cursor cursor = db.query(DatabaseHelper.SURVEY_TEMPLATE_TABLE, new String[]
                {DatabaseHelper.KEY_SENT}, DatabaseHelper.KEY_ID + " = '" +
                survey.getIdOfSurveys() + "' ", null, null, null, null);
        if(cursor.moveToNext()){
            return cursor.getInt(0) == 1;
        }

        close();

        return false;
    }

    public void deleteSurveyTemplate(String surveyId){
        open();
        db.delete(DatabaseHelper.TEXT_CONSTRAINTS_TABLE, DatabaseHelper.KEY_SURVEY_TCDB + " = '" +
                surveyId + "' ", null);
        db.delete(DatabaseHelper.NUMBER_CONSTRAINTS_TABLE, DatabaseHelper.KEY_SURVEY_NCDB + " = '" +
                surveyId  + "' ", null);
        db.delete(DatabaseHelper.GRID_ROW_ANSWERS_TABLE, DatabaseHelper.KEY_SURVEY_GRDB + " = '" +
                surveyId  + "' ", null);
        db.delete(DatabaseHelper.GRID_COLUMN_ANSWERS_TABLE, DatabaseHelper.KEY_SURVEY_GCDB + " = '" +
                surveyId  + "' ", null);
        db.delete(DatabaseHelper.SCALE_ANSWERS_TABLE, DatabaseHelper.KEY_SURVEY_SCDB + " = '" +
                surveyId  + "' ", null);
        db.delete(DatabaseHelper.CHOICE_ANSWERS_TABLE, DatabaseHelper.KEY_SURVEY_CHADB + " = '" +
                surveyId  + "' ", null);
        db.delete(DatabaseHelper.QUESTIONS_TABLE, DatabaseHelper.KEY_ID_SURVEY_QDB + " = '" +
                surveyId  + "' ", null);
        db.delete(DatabaseHelper.SURVEY_TEMPLATE_TABLE, DatabaseHelper.KEY_ID + " = '" +
                surveyId  + "' ", null);
        close();
    }
}
