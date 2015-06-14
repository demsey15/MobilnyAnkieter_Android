package bohonos.demski.mieldzioc.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.application.DateAndTimeService;
import bohonos.demski.mieldzioc.constraints.IConstraint;
import bohonos.demski.mieldzioc.constraints.NumberConstraint;
import bohonos.demski.mieldzioc.constraints.TextConstraint;
import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.questions.DateTimeQuestion;
import bohonos.demski.mieldzioc.questions.GridQuestion;
import bohonos.demski.mieldzioc.questions.MultipleChoiceQuestion;
import bohonos.demski.mieldzioc.questions.OneChoiceQuestion;
import bohonos.demski.mieldzioc.questions.Question;
import bohonos.demski.mieldzioc.questions.ScaleQuestion;
import bohonos.demski.mieldzioc.questions.TextQuestion;
import bohonos.demski.mieldzioc.survey.Survey;
import bohonos.demski.mieldzioc.survey.SurveyHandler;

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
    /**
     * Dodaj ankietę do bazy danych.
     * Metoda sama dba o otwarcie i zamknięcie połączenia z bazą danych.
     * @param survey ankieta do dodania.
     * @param isSent czy ankieta została wysłana już na serwer.
     * @return
     */
    public boolean addSurveyTemplate(Survey survey, int status, boolean isSent){
        open();
        String idOfSurveys = survey.getIdOfSurveys();
        int size = survey.questionListSize();

        ContentValues templateValues = new ContentValues();
        templateValues.put(DatabaseHelper.KEY_ID, idOfSurveys);
        templateValues.put(DatabaseHelper.KEY_STATUS, status);
        Interviewer interviewer = survey.getInterviewer();
        if(interviewer != null) {
            templateValues.put(DatabaseHelper.KEY_INTERVIEWER, interviewer.getId());
        }
        templateValues.put(DatabaseHelper.KEY_CREATED_DATE, DateAndTimeService.getToday());
        templateValues.put(DatabaseHelper.KEY_MODIFICATION_DATE, DateAndTimeService.getToday());
        templateValues.put(DatabaseHelper.KEY_MODIFIED_BY, ApplicationState.getInstance(context).
                getLoggedInterviewer().getId());
        templateValues.put(DatabaseHelper.KEY_TITLE, survey.getTitle());
        templateValues.put(DatabaseHelper.KEY_DESCRIPTION, survey.getDescription());
        templateValues.put(DatabaseHelper.KEY_SUMMARY, survey.getSummary());
        templateValues.put(DatabaseHelper.KEY_SENT, (isSent)? 1 : 0);
        db.insert(DatabaseHelper.SURVEY_TEMPLATE_TABLE, null, templateValues);

        for(int i = 0; i < size; i++){
            Question question = survey.getQuestion(i);
            String questionNumber = "" + idOfSurveys + i;
            if(addQuestion(question, idOfSurveys, questionNumber) == -1){
                close();
                return false;
            }
            int questionType = question.getQuestionType();
            if(questionType == Question.DROP_DOWN_QUESTION ||
                    questionType == Question.MULTIPLE_CHOICE_QUESTION || questionType ==
                    Question.ONE_CHOICE_QUESTION){
                if(addChoiceAnswers(question, idOfSurveys, questionNumber) == -1) {
                    close();
                    return false;
                }
            }
            else if(questionType == Question.GRID_QUESTION){
                if(addGridAnswers((GridQuestion) question, idOfSurveys, questionNumber) == -1) {
                    close();
                    return false;
                }
            }
            else if(questionType == Question.SCALE_QUESTION){
                if(addScaleAnswers((ScaleQuestion) question, idOfSurveys, questionNumber) == -1) {
                    close();
                    return false;
                }
            }
            else if(questionType == Question.TEXT_QUESTION){
                TextQuestion textQuestion = (TextQuestion) question;
                IConstraint constraint = textQuestion.getConstraint();
                if(constraint instanceof TextConstraint){
                    if(addTextConstraints((TextConstraint) constraint, idOfSurveys, questionNumber)
                        == -1) {
                        close();
                        return false;
                    }
                }
                else if(constraint instanceof  NumberConstraint){
                    if(addNumberConstraints((NumberConstraint) constraint, idOfSurveys,
                            questionNumber) == -1) {
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

    public long addQuestion(Question question, String idOfSurveys, String questionNumber){
        ContentValues questionValues = new ContentValues();
        questionValues.put(DatabaseHelper.KEY_ID_SURVEY_QDB, idOfSurveys);
        questionValues.put(DatabaseHelper.KEY_QUESTION_NUMBER_QDB, questionNumber);
        questionValues.put(DatabaseHelper.KEY_QUESTION_QDB, question.getQuestion());
        questionValues.put(DatabaseHelper.KEY_OBLIGATORY_QDB, (question.isObligatory())? 1 : 0);
        questionValues.put(DatabaseHelper.KEY_HINT_QDB, question.getHint());
        questionValues.put(DatabaseHelper.KEY_ERROR_QDB, question.getErrorMessage());
        questionValues.put(DatabaseHelper.KEY_URL_QDB, question.getPictureURL());
        questionValues.put(DatabaseHelper.KEY_TYPE_QDB, question.getQuestionType());
        questionValues.put(DatabaseHelper.KEY_CREATED_DATE_QDB, DateAndTimeService.getToday());
        questionValues.put(DatabaseHelper.KEY_MODIFICATION_DATE_QDB, DateAndTimeService.getToday());
        questionValues.put(DatabaseHelper.KEY_INTERVIEWER_QDB,
                ApplicationState.getInstance(context).getLoggedInterviewer().getId());
        questionValues.put(DatabaseHelper.KEY_MODIFIED_BY_QDB, String.valueOf(
                ApplicationState.getInstance(context).getLoggedInterviewer().getId()));
        return db.insert(DatabaseHelper.QUESTIONS_TABLE, null, questionValues);
    }
    public long addChoiceAnswers(Question question, String surveyId, String questionNumber){
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

    public long addScaleAnswers(ScaleQuestion question, String surveyId, String questionNumber) {
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

    public long addGridAnswers(GridQuestion question, String surveyId, String questionNumber) {

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

    public long addNumberConstraints(NumberConstraint constraint, String surveyId,
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

    public long addTextConstraints(TextConstraint constraint, String surveyId,
                                     String questionNumber){
        ContentValues constraintsValues = new ContentValues();
        constraintsValues.put(DatabaseHelper.KEY_SURVEY_TCDB, surveyId);
        constraintsValues.put(DatabaseHelper.KEY_QUESTION_TCDB, questionNumber);
        constraintsValues.put(DatabaseHelper.KEY_ANSWER_NUMBER_TCDB, questionNumber);
        constraintsValues.put(DatabaseHelper.KEY_MIN_LENGTH_TCDB, constraint.getMinLength());
        constraintsValues.put(DatabaseHelper.KEY_MAX_LENGTH_TCDB, constraint.getMaxLength());
        if(constraint.getRegex() != null)
            constraintsValues.put(DatabaseHelper.KEY_REGEX_TCDB, constraint.getRegex().pattern());
        return db.insert(DatabaseHelper.TEXT_CONSTRAINTS_TABLE, null, constraintsValues);
    }

    public HashMap<Survey, Integer> getAllSurveyTemplates(){
        HashMap<Survey, Integer> templates = new HashMap<>();
        Cursor cursor = db.query(DatabaseHelper.SURVEY_TEMPLATE_TABLE, new String[]
                {DatabaseHelper.KEY_ID, DatabaseHelper.KEY_STATUS}, null, null, null, null, null);
        while(cursor.moveToNext()){
            Survey survey = getSurveyTemplate(cursor.getString(0));
            templates.put(survey, cursor.getInt(1));
        }
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
                DatabaseHelper.KEY_INTERVIEWER, DatabaseHelper.KEY_TITLE,
                DatabaseHelper.KEY_DESCRIPTION, DatabaseHelper.KEY_SUMMARY}, DatabaseHelper.KEY_ID +
                " = '" + idOfSurveys + "'", null, null, null, null);
        if(cursor.moveToFirst()) {
            survey = new Survey(null);
            survey.setDescription(cursor.getString(2));
            survey.setIdOfSurveys(idOfSurveys);
            survey.setSummary(cursor.getString(3));
            survey.setTitle((cursor.getString(1) == null) ? "" : cursor.getString(1));
            String interviewerId = cursor.getString(0);     //spróbuj pobrać ankietera
            Cursor cursorInterviewer = db.query(DatabaseHelper.INTERVIEWERS_TABLE, new String[]
                            {DatabaseHelper.KEY_CAN_CREATE_IDB},
                    DatabaseHelper.KEY_ID_INTERVIEWER_IDB + " = '" + interviewerId  + "' ",
                    null, null, null, null);
            if (cursorInterviewer.moveToFirst()) {        //jeśli mam takiego interveiwera w bazie, to
                //dodaj go do ankiety, jesli nie, to nie
                Interviewer interviewer = new Interviewer(null, null, interviewerId, null);
                interviewer.setInterviewerPrivileges((cursorInterviewer.getInt(0) == 0) ? false : true);
            }
            List<Question> questions = getSurveysQuestion(idOfSurveys);
            for (Question question : questions) {
                survey.addQuestion(question);
            }
        }
        close();
        return survey;
    }

    public List<Question> getSurveysQuestion(String idOfSurveys) {
        Cursor cursor = db.query(DatabaseHelper.QUESTIONS_TABLE, new String[]{
                        DatabaseHelper.KEY_OBLIGATORY_QDB, DatabaseHelper.KEY_HINT_QDB,
                        DatabaseHelper.KEY_ERROR_QDB, DatabaseHelper.KEY_URL_QDB,
                        DatabaseHelper.KEY_TYPE_QDB, DatabaseHelper.KEY_QUESTION_QDB
                        }, DatabaseHelper.KEY_ID_SURVEY_QDB + " = '"
                        + idOfSurveys + "' ", null, null, null,
                        DatabaseHelper.KEY_QUESTION_NUMBER_QDB + " ASC");
        int i = 0;
        List<Question> questions = new ArrayList<>();
        while (cursor.moveToNext()) {
            Question question = null;
            int questionType = cursor.getInt(4);
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
            question.setObligatory((cursor.getInt(0) == 0) ? false : true);
            question.setHint(cursor.getString(1));
            question.setErrorMessage(cursor.getString(2));
            question.setPictureURL(cursor.getString(3));
            question.setQuestion(cursor.getString(5));
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
    public List<String> getChoiceAnswers(String idOfSurveys, int questionNumber){
        String questionNo = idOfSurveys + questionNumber;
        List<String> answers = new ArrayList<String>();
        Cursor cursor = db.query(DatabaseHelper.CHOICE_ANSWERS_TABLE, new String[] {
                DatabaseHelper.KEY_ANSWER_CHADB}, DatabaseHelper.KEY_QUESTION_CHADB + " = " +
                questionNo, null, null, null, DatabaseHelper.KEY_ANSWER_NUMBER_CHADB + " ASC");
        while(cursor.moveToNext()){
            answers.add(cursor.getString(0));
        }
        return answers;
    }

    public List<String> getGridRowAnswers(String idOfSurveys, int questionNumber){
        String questionNo = idOfSurveys + questionNumber;
        List<String> answers = new ArrayList<String>();
        Cursor cursor = db.query(DatabaseHelper.GRID_ROW_ANSWERS_TABLE, new String[] {
                DatabaseHelper.KEY_ANSWER_GRDB}, DatabaseHelper.KEY_QUESTION_GRDB + " = " +
                questionNo, null, null, null, DatabaseHelper.KEY_ANSWER_NUMBER_GRDB + " ASC");
        while(cursor.moveToNext()){
            answers.add(cursor.getString(0));
        }
        return answers;
    }

    public List<String> getGridColumnAnswers(String idOfSurveys, int questionNumber){
        String questionNo = idOfSurveys + questionNumber;
        List<String> answers = new ArrayList<String>();
        Cursor cursor = db.query(DatabaseHelper.GRID_COLUMN_ANSWERS_TABLE, new String[] {
                DatabaseHelper.KEY_ANSWER_GCDB}, DatabaseHelper.KEY_QUESTION_GCDB + " = " +
                questionNo, null, null, null, DatabaseHelper.KEY_ANSWER_NUMBER_GCDB + " ASC");
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
    public NumberConstraint getNumberConstraints(String idOfSurveys, int questionNumber){
        String questionNo = idOfSurveys + questionNumber;
        Cursor cursor = db.query(DatabaseHelper.NUMBER_CONSTRAINTS_TABLE, new String[] {
                DatabaseHelper.KEY_MIN_VALUE_NCDB, DatabaseHelper.KEY_MAX_VALUE_NCDB,
                DatabaseHelper.KEY_NOT_BETWEEN_NCDB, DatabaseHelper.KEY_NOT_EQUALS_NCDB,
                DatabaseHelper.KEY_MUST_BE_INTEGER_NCDB}, DatabaseHelper.KEY_QUESTION_GRDB + " = " +
                questionNo, null, null, null, null, null);
        NumberConstraint numberConstraint = null;
        if(cursor.moveToFirst()){
            numberConstraint = new NumberConstraint(cursor.getDouble(0),
                    cursor.getDouble(1), (cursor.getInt(4) == 0)? false : true, cursor.getDouble(3),
                    (cursor.getInt(2) == 0)? false : true);
        }
        return numberConstraint;
    }

    /**
     * Zwraca ograniczenia liczbowe dla zadanego pytania.
     * @param idOfSurveys
     * @param questionNumber
     * @return null, jesli nie ma ograniczeń tekstowych.
     */
    public TextConstraint getTextConstraints(String idOfSurveys, int questionNumber){
        String questionNo = idOfSurveys + questionNumber;
        Cursor cursor = db.query(DatabaseHelper.TEXT_CONSTRAINTS_TABLE, new String[] {
                DatabaseHelper.KEY_MIN_LENGTH_TCDB, DatabaseHelper.KEY_MAX_LENGTH_TCDB,
                DatabaseHelper.KEY_REGEX_TCDB}, DatabaseHelper.KEY_QUESTION_TCDB + " = " +
                questionNo, null, null, null, null, null);
        TextConstraint textConstraint = null;
        if(cursor.moveToFirst()){
            Pattern pattern;
            try{
                pattern = Pattern.compile(cursor.getString(2));
            }
            catch(Exception e){
                pattern = null;
            }
            textConstraint = new TextConstraint(cursor.getInt(0),
                    cursor.getInt(1), pattern);
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
    public ScaleQuestion getScaleAnswers(String idOfSurveys, int questionNumber){
        String questionNo = idOfSurveys + questionNumber;
        ScaleQuestion scaleQuestion = null;
        Cursor cursor = db.query(DatabaseHelper.SCALE_ANSWERS_TABLE, new String[] {
                DatabaseHelper.KEY_MIN_VALUE_SCDB, DatabaseHelper.KEY_MAX_VALUE_SCDB,
                DatabaseHelper.KEY_MIN_LAB_SCDB, DatabaseHelper.KEY_MAX_LABEL_SCDB,},
                DatabaseHelper.KEY_QUESTION_SCDB + " = " +
                questionNo, null, null, null, null, null);
        if(cursor.moveToFirst()){
            scaleQuestion = new ScaleQuestion("", false, "", "", cursor.getInt(0),
                    cursor.getInt(1), cursor.getString(2), cursor.getString(3));
        }
        return scaleQuestion;
    }

    /**
     * Zwraca status wybranej grupy ankiet.
     * @param idOfSurveys
     * @return status grupy ankiet albo -2, jesli w bazie nie ma szablonu o takim id.
     */
    public int getSurveyStatus(String idOfSurveys){
        open();
        Cursor cursor = db.query(DatabaseHelper.SURVEY_TEMPLATE_TABLE, new String[]
                        {DatabaseHelper.KEY_STATUS}, DatabaseHelper.KEY_ID + " = '" + idOfSurveys + "' ",
                        null, null, null, null);
        int toReturn = -2;
        if(cursor.moveToFirst())
            toReturn =  cursor.getInt(0);
        close();
        return toReturn;
    }

    /**
     * Zwraca listę szablonów ankiet stworzonych przez ankietera, niewysłanych jeszcze na serwer.
     * Nie zwraca null.
     * @param interviewer
     * @return
     */
    public List<Survey> getNotSentSurveysTemplateCreatedByInterviewer(Interviewer interviewer){
        List<Survey> list = new ArrayList<>();
        open();
        Cursor cursor = db.query(DatabaseHelper.SURVEY_TEMPLATE_TABLE, new String[]
                {DatabaseHelper.KEY_ID}, DatabaseHelper.KEY_INTERVIEWER + " = '" + interviewer.getId()
                + "' AND " + DatabaseHelper.KEY_SENT + " = " + 0 + " AND " + DatabaseHelper.KEY_STATUS
                + " = " + SurveyHandler.IN_PROGRESS,
                null, null, null, null);
        while(cursor.moveToNext()){
            Log.d("WYSYLANIE_BAZA", "Pobieram ankiete do wyslania o id: " + cursor.getString(0));
            Survey survey = getSurveyTemplate(cursor.getString(0));
            if(survey != null)
                list.add(survey);
        }
        close();
        return list;
    }

    public void setSurveySent(Survey survey, boolean isSent){
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_SENT, (isSent)? 1 : 0);
        db.update(DatabaseHelper.SURVEY_TEMPLATE_TABLE, values, DatabaseHelper.KEY_ID + " = '" +
                survey.getIdOfSurveys() + "' ", null);
        close();
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
