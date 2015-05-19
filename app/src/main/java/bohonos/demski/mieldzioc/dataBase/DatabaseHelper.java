package bohonos.demski.mieldzioc.dataBase;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dominik Demski on 2015-05-01.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //tabele:

    //tabela Survey_template
    private static final String DB_NAME = "survey_database";
    private static final int DB_VERSION = 11;

    public static final String SURVEY_TEMPLATE_TABLE = "Survey_template";

    public static final String KEY_ID = "id";
    public static final String ID_OPTIONS = "TEXT PRIMARY KEY NOT NULL";
    public static final int ID_COLUMN = 0;
    public static final String KEY_STATUS = "Status";
    public static final String STATUS_OPTIONS = "INT NOT NULL";
    public static final int STATUS_COLUMN = 1;
    public static final String KEY_INTERVIEWER = "Interviewer";
    public static final String INTERVIEWER_OPTIONS = "TEXT CHECK(" + KEY_INTERVIEWER + " " +
            "GLOB '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]')";
    public static final int INTERVIEWER_COLUMN = 2;
    public static final String KEY_CREATED_DATE = "Date_of_creating";
    public static final String CREATED_DATE_OPTIONS = "TEXT NOT NULL CHECK(" + KEY_CREATED_DATE +
             " GLOB '[1-3][0-9][0-9][0-9]-" +
            "[0-1][0-9]-[0-3][0-9]" + " [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]')";
    //TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")
    public static final int CREATED_DATE_COLUMN = 3;
    public static final String KEY_MODIFICATION_DATE = "Date_of_modification";
    public static final String MODIFICATION_DATE_OPTIONS = "TEXT NOT NULL CHECK(" +
            KEY_MODIFICATION_DATE + " GLOB '[1-3][0-9][0-9][0-9]-" +
            "[0-1][0-9]-[0-3][0-9]" + " [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]')";
    //TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")*/
    public static final int MODIFICATION_DATE_COLUMN = 4;
    public static final String KEY_MODIFIED_BY = "Modified_by";
    public static final String MODIFIED_BY_OPTIONS = "TEXT NOT NULL CHECK(" + KEY_MODIFIED_BY  +
            " GLOB '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]')";
    public static final int MODIFIED_BY_COLUMN = 5;
    public static final String KEY_TITLE = "Title";
    public static final String TITLE_OPTIONS = "TEXT";
    public static final int TITLE_COLUMN = 6;
    public static final String KEY_DESCRIPTION = "Description";
    public static final String DESCRIPTION_OPTIONS = "TEXT";
    public static final int DESCRIPTION_COLUMN = 7;
    public static final String KEY_SUMMARY = "Summary";
    public static final String SUMMARY_OPTIONS = "TEXT";
    public static final int SUMMARY_COLUMN = 8;
    public static final String KEY_SENT = "Sent";  //czy wys³ana na serwer
    public static final String SENT_OPTIONS = "INT NOT NULL CHECK(" + KEY_SENT + " IN(0, 1))";
    public static final int SENT_COLUMN = 9;


    //tabela pytania
    public static final String QUESTIONS_TABLE = "Questions";

    public static final String KEY_ID_SURVEY_QDB = "Survey";
    public static final String ID_SURVEY_OPTIONS_QDB = "TEXT NOT NULL";
    public static final String FK_QDB = "FOREIGN KEY (" + KEY_ID_SURVEY_QDB +
            ") REFERENCES " + SURVEY_TEMPLATE_TABLE + "(" + KEY_ID + ")";
    public static final int ID_SURVEY_COLUMN_QDB = 0;
    public static final String KEY_QUESTION_NUMBER_QDB = "Question_number";   //NUMER PYTANIA: NRANKIETYnrpytania
    public static final String QUESTION_NUMBER_OPTIONS_QDB = "TEXT PRIMARY KEY NOT NULL";
    public static final int QUESTION_NUMBER_COLUMN_QDB = 1;
    public static final String KEY_QUESTION_QDB = "Question";
    public static final String QUESTION_OPTIONS_QDB = "TEXT";
    public static final int QUESTION_COLUMN_QDB = 2;
    public static final String KEY_OBLIGATORY_QDB = "Obligatory";
    public static final String OBLIGATORY_OPTIONS_QDB = "INTEGER CHECK(" + KEY_OBLIGATORY_QDB +
            " IN(0, 1))";
    public static final int OBLIGATORY_COLUMN_QDB = 3;
    public static final String KEY_HINT_QDB = "Hint";
    public static final String HINT_OPTIONS_QDB = "TEXT";
    public static final int HINT_COLUMN_QDB = 4;
    public static final String KEY_ERROR_QDB = "Error";
    public static final String ERROR_OPTIONS_QDB = "TEXT";
    public static final int ERROR_COLUMN_QDB = 5;
    public static final String KEY_URL_QDB = "Url";
    public static final String URL_OPTIONS_QDB = "TEXT";
    public static final int URL_COLUMN_QDB = 6;
    public static final String KEY_TYPE_QDB = "Type";
    public static final String TYPE_OPTIONS_QDB = "INTEGER CHECK(" + KEY_TYPE_QDB + " >= 0 AND " +
    KEY_TYPE_QDB + " <= 7)";
    public static final int TYPE_COLUMN_QDB = 7;
    public static final String KEY_INTERVIEWER_QDB = "Interviewer";
    public static final String INTERVIEWER_OPTIONS_QDB = "TEXT CHECK(" + KEY_INTERVIEWER_QDB
    + " GLOB '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]')";
    public static final int INTERVIEWER_COLUMN_QDB = 8;
    public static final String KEY_CREATED_DATE_QDB = "Date_of_creating";
    public static final String CREATED_DATE_OPTIONS_QDB = "TEXT NOT NULL CHECK(" +
            KEY_CREATED_DATE_QDB + " GLOB '[1-3][0-9][0-9][0-9]-" +
            "[0-1][0-9]-[0-3][0-9]" + " [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]')";
    //TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")
    public static final int CREATED_DATE_COLUMN_QDB = 9;
    public static final String KEY_MODIFICATION_DATE_QDB = "Date_of_modification";
    public static final String MODIFICATION_DATE_OPTIONS_QDB = "TEXT NOT NULL CHECK(" +
            KEY_MODIFICATION_DATE_QDB + " GLOB '[1-3][0-9][0-9][0-9]-" +
            "[0-1][0-9]-[0-3][0-9]" + " [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]')";
    //TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")
    public static final int MODIFICATION_DATE_COLUMN_QDB = 10;
    public static final String KEY_MODIFIED_BY_QDB = "Modified_by";
    public static final String MODIFIED_BY_OPTIONS_QDB = "TEXT NOT NULL CHECK(" +
            KEY_MODIFIED_BY_QDB + " GLOB '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]')";
    public static final int MODIFIED_BY_COLUMN_QDB = 11;

    public static final String INTERVIEWERS_TABLE = "Interviewers";

    public static final String KEY_ID_INTERVIEWER_IDB = "PESEL";
    public static final String ID_INTERVIEWER_OPTIONS_IDB = "TEXT NOT NULL CHECK(" +
            KEY_ID_INTERVIEWER_IDB + " GLOB '[0-9][0-9][0-9][0-9]" +
            "[0-9][0-9][0-9][0-9][0-9][0-9][0-9]')";
    public static final int ID_INTERVIEWER_COLUMN_IDB = 0;
    public static final String KEY_PASSWORD_IDB = "Password";
    public static final String PASSWORD_OPTIONS_IDB = "TEXT NOT NULL";
    public static final int PASSWORD_COLUMN_IDB = 1;
    public static final String KEY_CAN_CREATE_IDB = "Can_create";
    public static final String CAN_CREATE_OPTIONS_IDB = "INTEGER NOT NULL CHECK( " +
            KEY_CAN_CREATE_IDB + " IN(0, 1))";
    public static final int CAN_CREATE_COLUMN_IDB = 2;


    public static final String SCALE_ANSWERS_TABLE = "Scale_answers";

    public static final String KEY_SURVEY_SCDB = "Survey";
    public static final String SURVEY_OPTIONS_SCDB = "TEXT NOT NULL REFERENCES "
            + SURVEY_TEMPLATE_TABLE;
    public static final int SURVEY_COLUMN_SCDB = 0;
    public static final String KEY_QUESTION_SCDB = "Question";
    public static final String QUESTION_OPTIONS_SCDB = "TEXT NOT NULL REFERENCES "
            + QUESTIONS_TABLE;
    public static final int QUESTION_COLUMN_SCDB = 1;
    public static final String KEY_MIN_LAB_SCDB = "Min_label";
    public static final String MIN_LAB_OPTIONS_SCDB = "TEXT";
    public static final int MIN_LAB_COLUMN_SCDB = 2;
    public static final String KEY_MAX_LABEL_SCDB = "Max_label";
    public static final String MAX_LABEL_OPTIONS_SCDB = "TEXT";
    public static final int MAX_LABEL_COLUMN_SCDB = 3;
    public static final String KEY_MIN_VALUE_SCDB = "Min_value";
    public static final String MIN_VALUE_SCDB = "INTEGER NOT NULL";
    public static final int MIN_VALUE_COLUMN_SCDB = 4;
    public static final String KEY_MAX_VALUE_SCDB = "Max_value";
    public static final String MAX_VALUE_SCDB = "INTEGER NOT NULL";
    public static final int MAX_VALUE_COLUMN_SCDB = 5;
    public static final String KEY_ANSWER_NUMBER_SCDB = "Number";
    public static final String ANSWER_NUMBER_SCDB = "TEXT NOT NULL PRIMARY KEY";
    public static final int ANSWER_NUMBER_COLUMN_SCDB = 6;

    public static final String CHOICE_ANSWERS_TABLE = "Choice_answers";

    public static final String KEY_SURVEY_CHADB = "Survey";
    public static final String SURVEY_OPTIONS_CHADB = "TEXT NOT NULL REFERENCES "
            + SURVEY_TEMPLATE_TABLE;
    public static final int SURVEY_COLUMN_CHADB = 0;
    public static final String KEY_QUESTION_CHADB = "Question";
    public static final String QUESTION_OPTIONS_CHADB = "TEXT NOT NULL REFERENCES "
            + QUESTIONS_TABLE;
    public static final int QUESTION_COLUMN_CHADB = 1;
    public static final String KEY_ANSWER_NUMBER_CHADB = "Answer_number";
    public static final String ANSWER_NUMBER_OPTIONS_CHADB = "TEXT NOT NULL PRIMARY KEY";
    public static final int ANSWER_NUMBER_COLUMN_CHADB = 2;
    public static final String KEY_ANSWER_CHADB = "Answer";
    public static final String ANSWER_OPTIONS_CHADB = "TEXT NOT NULL";
    public static final int ANSWER_COLUMN_CHADB = 3;

    public static final String GRID_COLUMN_ANSWERS_TABLE = "Grid_column_answers";

    public static final String KEY_SURVEY_GCDB = "Survey";
    public static final String SURVEY_OPTIONS_GCDB = "TEXT NOT NULL REFERENCES "
            + SURVEY_TEMPLATE_TABLE;
    public static final int SURVEY_COLUMN_GCDB = 0;
    public static final String KEY_QUESTION_GCDB = "Question";
    public static final String QUESTION_OPTIONS_GCDB = "TEXT NOT NULL REFERENCES "
            + QUESTIONS_TABLE;
    public static final int QUESTION_COLUMN_GCDB = 1;
    public static final String KEY_ANSWER_NUMBER_GCDB = "Answer_number";
    public static final String ANSWER_NUMBER_OPTIONS_GCDB = "TEXT NOT NULL PRIMARY KEY";
    public static final int ANSWER_NUMBER_COLUMN_GCDB = 2;
    public static final String KEY_ANSWER_GCDB = "Answer";
    public static final String ANSWER_OPTIONS_GCDB = "TEXT NOT NULL";
    public static final int ANSWER_COLUMN_GCDB = 3;

    public static final String GRID_ROW_ANSWERS_TABLE = "Grid_row_answers";

    public static final String KEY_SURVEY_GRDB = "Survey";
    public static final String SURVEY_OPTIONS_GRDB = "TEXT NOT NULL REFERENCES "
            + SURVEY_TEMPLATE_TABLE;
    public static final int SURVEY_COLUMN_GRDB = 0;
    public static final String KEY_QUESTION_GRDB = "Question";
    public static final String QUESTION_OPTIONS_GRDB = "TEXT NOT NULL REFERENCES "
            + QUESTIONS_TABLE;
    public static final int QUESTION_COLUMN_GRDB = 1;
    public static final String KEY_ANSWER_NUMBER_GRDB = "Answer_number";
    public static final String ANSWER_NUMBER_OPTIONS_GRDB = "TEXT NOT NULL PRIMARY KEY";
    public static final int ANSWER_NUMBER_COLUMN_GRDB = 2;
    public static final String KEY_ANSWER_GRDB = "Answer";
    public static final String ANSWER_OPTIONS_GRDB = "TEXT NOT NULL";
    public static final int ANSWER_COLUMN_GRDB = 3;

    public static final String NUMBER_CONSTRAINTS_TABLE = "Number_constraints";

    public static final String KEY_SURVEY_NCDB = "Survey";
    public static final String SURVEY_OPTIONS_NCDB = "TEXT NOT NULL REFERENCES "
            + SURVEY_TEMPLATE_TABLE;
    public static final int SURVEY_COLUMN_NCDB = 0;
    public static final String KEY_QUESTION_NCDB = "Question";
    public static final String QUESTION_OPTIONS_NCDB = "TEXT NOT NULL REFERENCES "
            + QUESTIONS_TABLE;
    public static final int QUESTION_COLUMN_NCDB = 1;
    public static final String KEY_ANSWER_NUMBER_NCDB = "Answer_number";
    public static final String ANSWER_NUMBER_OPTIONS_NCDB = "TEXT NOT NULL PRIMARY KEY";
    public static final int ANSWER_NUMBER_COLUMN_NCDB = 2;
    public static final String KEY_MIN_VALUE_NCDB = "Min_value";
    public static final String MIN_VALUE_OPTIONS_NCDB = "REAL";
    public static final int MIN_VALUE_COLUMN_NCDB = 3;
    public static final String KEY_MAX_VALUE_NCDB = "Max_value";
    public static final String MAX_VALUE_OPTIONS_NCDB = "REAL";
    public static final int MAX_VALUE_COLUMN_NCDB = 4;
    public static final String KEY_MUST_BE_INTEGER_NCDB = "Must_be_integer";
    public static final String MUST_BE_INTEGER_OPTIONS_NCDB = "INTEGER CHECK(" +
            KEY_MUST_BE_INTEGER_NCDB + " IN(0, 1))";
    public static final int MUST_BE_INTEGER_COLUMN_NCDB = 5;
    public static final String KEY_NOT_EQUALS_NCDB = "Not_equals";
    public static final String NOT_EQUALS_OPTIONS_NCDB = "REAL";
    public static final int NOT_EQUALS_COLUMN_NCDB = 6;
    public static final String KEY_NOT_BETWEEN_NCDB = "Not_between";
    public static final String NOT_BETWEEN_OPTIONS_NCDB = "INTEGER CHECK(" +
            KEY_NOT_BETWEEN_NCDB + " IN(0, 1))";
    public static final int NOT_BETWEEN_COLUMN_NCDB = 7;

    public static final String TEXT_CONSTRAINTS_TABLE = "Text_constraints";

    public static final String KEY_SURVEY_TCDB = "Survey";
    public static final String SURVEY_OPTIONS_TCDB = "TEXT NOT NULL REFERENCES "
            + SURVEY_TEMPLATE_TABLE;
    public static final int SURVEY_COLUMN_TCDB = 0;
    public static final String KEY_QUESTION_TCDB = "Question";
    public static final String QUESTION_OPTIONS_TCDB = "TEXT NOT NULL REFERENCES "
            + QUESTIONS_TABLE;
    public static final int QUESTION_COLUMN_TCDB = 1;
    public static final String KEY_ANSWER_NUMBER_TCDB = "Answer_number";
    public static final String ANSWER_NUMBER_OPTIONS_TCDB = "TEXT NOT NULL PRIMARY KEY";
    public static final int ANSWER_NUMBER_COLUMN_TCDB = 2;
    public static final String KEY_MIN_LENGTH_TCDB = "Min_length";
    public static final String MIN_LENGTH_OPTIONS_TCDB = "INTEGER";
    public static final int MIN_VALUE_COLUMN_TCDB = 3;
    public static final String KEY_MAX_LENGTH_TCDB = "Max_length";
    public static final String MAX_LENGTH_OPTIONS_TCDB = "INTEGER";
    public static final int MAX_VALUE_COLUMN_TCDB = 4;
    public static final String KEY_REGEX_TCDB = "Regex";
    public static final String REGEX_OPTIONS_TCDB = "TEXT";
    public static final int REGEX_COLUMN_TCDB = 5;


    public static final String ANSWERS_TABLE = "Answers";

    public static final String KEY_SURVEY_SADB = "Survey";
    public static final String SURVEY_OPTIONS_SADB = "TEXT NOT NULL REFERENCES " +
            SURVEY_TEMPLATE_TABLE;
    public static final int SURVEY_COLUMN_SADB  = 0;
    public static final String KEY_NO_FILLED_SURVEY_SADB = "Filled_survey_number"; //numer wype³nionej ankiety
    public static final String NO_FILLED_SURVEY_OPTIONS_SADB = "INT NOT NULL";
    public static final int NO_FILLED_COLUMN_SADB = 1;
    public static final String KEY_ANSWER_NUMBER_SADB = "Answer_number"; //numer odpowiedzi w danym pytaniu
    public static final String ANSWER_NUMBER_OPTIONS_SADB = "INT NOT NULL";
    public static final int ANSWER_NUMBER_COLUMN_SADB = 2;
    public static final String KEY_QUESTION_NUMBER_SADB = "Question_number";
    public static final String QUESTION_NUMBER_OPTIONS_SADB = "TEXT NOT NULL  REFERENCES " +
             "QUESTIONS_TABLE";
    public static final int QUESTION_NUMBER_COLUMN_SADB = 3;
    public static final String KEY_ANSWER_SADB = "Answer";
    public static final String ANSWER_OPTIONS_SADB = "TEXT";
    public static final int ANSWER_COLUMN_SADB = 4;

    public static final String FILLED_SURVEYS_TABLE = "Filled_surveys";

    public static final String KEY_SURVEY_FSDB = "Survey";
    public static final String SURVEY_OPTIONS_FSDB = "TEXT NOT NULL";
    public static final int SURVEY_COLUMN_FSDB  = 0;
    public static final String KEY_NO_FILLED_SURVEY_FSDB = "Filled_survey_number"; //numer wype³nionej ankiety
    public static final String NO_FILLED_OPTIONS_FSDB = "INT NOT NULL";
    public static final int NO_FILLED_COLUMN_FSDB = 1;
    public static final String KEY_INTERVIEWER_FSDB = "Interviewer";
    public static final String INTERVIEWER_OPTIONS_FSDB = "TEXT CHECK(" + KEY_INTERVIEWER_FSDB + " " +
            "GLOB '[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]')";
    public static final int INTERVIEWER_COLUMN_FSDB = 2;
    public static final String KEY_FROM_DATE_FSDB = "From_date";
    public static final String FROM_DATE_OPTIONS_FSDB = "TEXT NOT NULL CHECK(" +
            KEY_FROM_DATE_FSDB + " GLOB '[1-3][0-9][0-9][0-9]-" +
            "[0-1][0-9]-[0-3][0-9]" + " [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]')";
    //TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")
    public static final int FROM_DATE_COLUMN_FSDB = 3;
    public static final String KEY_TO_DATE_FSDB = "To_date";
    public static final String TO_DATE_OPTIONS_FSDB = "TEXT NOT NULL CHECK(" +
            KEY_FROM_DATE_FSDB + " GLOB '[1-3][0-9][0-9][0-9]-" +
            "[0-1][0-9]-[0-3][0-9]" + " [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9][0-9][0-9]')";
    //TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")
    public static final int TO_DATE_COLUMN_FSDB = 4;


    public static final String FILLING_PRIVILEGES_TABLE = "Filling_privileges";

    public static final String KEY_INTERVIEWER_FPDB = "Interviewer";
    public static final String INTERVIEWER_OPTIONS_FPDB = "TEXT NOT NULL";
    public static final int INTERVIEWER_COLUMN_FPDB = 0;
    public static final String KEY_SURVEY_FPDB = "Survey";
    public static final String SURVEY_OPTIONS_FPDB = "TEXT NOT NULL";
    public static final int SURVEY_COLUMN_FPDB = 1;


    private static final String DB_CREATE_SURVEY_TEMPLATE_TABLE = "CREATE TABLE " +
            SURVEY_TEMPLATE_TABLE + "( " + KEY_ID + " " + ID_OPTIONS + ", " +
            KEY_STATUS + " " + STATUS_OPTIONS + ", " + KEY_INTERVIEWER + " " +
            INTERVIEWER_OPTIONS + ", " + KEY_CREATED_DATE + " " + CREATED_DATE_OPTIONS +
            ", " + KEY_MODIFICATION_DATE + " " + MODIFICATION_DATE_OPTIONS + ", " +
            KEY_MODIFIED_BY + " " + MODIFIED_BY_OPTIONS + ", " + KEY_TITLE + " " + TITLE_OPTIONS +
            ", " + KEY_DESCRIPTION + " " + DESCRIPTION_OPTIONS + ", " + KEY_SUMMARY +
            " " + SUMMARY_OPTIONS + ", " + KEY_SENT + " " + SENT_OPTIONS +  ");";

    private static final String DB_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
            QUESTIONS_TABLE + "( " + KEY_ID_SURVEY_QDB + " " + ID_SURVEY_OPTIONS_QDB +
            ", " + KEY_QUESTION_NUMBER_QDB + " " + QUESTION_NUMBER_OPTIONS_QDB +
            ", " + KEY_QUESTION_QDB + " " + QUESTION_OPTIONS_QDB + ", " +
            KEY_OBLIGATORY_QDB + " " + OBLIGATORY_OPTIONS_QDB + ", " +
            KEY_HINT_QDB + " " + HINT_OPTIONS_QDB + ", " + KEY_ERROR_QDB + " " + ERROR_OPTIONS_QDB +
            ", " + KEY_URL_QDB + " " + URL_OPTIONS_QDB + ", " + KEY_TYPE_QDB + " " + TYPE_OPTIONS_QDB
            + ", " + KEY_CREATED_DATE_QDB + " " + CREATED_DATE_OPTIONS_QDB + ", " +
            KEY_MODIFICATION_DATE_QDB + " " + MODIFICATION_DATE_OPTIONS_QDB + ", " + KEY_INTERVIEWER_QDB
            + " " + INTERVIEWER_OPTIONS_QDB + ", " + KEY_MODIFIED_BY + " " + MODIFIED_BY_OPTIONS_QDB
            + ", " + FK_QDB + ");";

    private static final String DB_CREATE_INTERVIEWERS_TABLE = "CREATE TABLE " + INTERVIEWERS_TABLE +
            "( " + KEY_ID_INTERVIEWER_IDB + " " + ID_INTERVIEWER_OPTIONS_IDB + ", " +
            KEY_PASSWORD_IDB + " " + PASSWORD_OPTIONS_IDB + ", " + KEY_CAN_CREATE_IDB +
            " " + CAN_CREATE_OPTIONS_IDB + ");";

    private static final String DB_CREATE_CHOICE_ANSWERS_TABLE = "CREATE TABLE " +
            CHOICE_ANSWERS_TABLE + "( " + KEY_SURVEY_CHADB + " " + SURVEY_OPTIONS_CHADB +
            ", " + KEY_QUESTION_CHADB + " " + QUESTION_OPTIONS_CHADB + ", " +
            KEY_ANSWER_NUMBER_CHADB + " " + ANSWER_NUMBER_OPTIONS_CHADB + ", " +
            KEY_ANSWER_CHADB + " " + ANSWER_OPTIONS_CHADB + ");";

    private static final String DB_CREATE_SCALE_ANSWERS_TABLE = "CREATE TABLE " +
            SCALE_ANSWERS_TABLE + "( " + KEY_SURVEY_SCDB + " " + SURVEY_OPTIONS_SCDB +
            ", " + KEY_QUESTION_SCDB + " " + QUESTION_OPTIONS_SCDB + ", " +
            KEY_MIN_LAB_SCDB + " " + MIN_LAB_OPTIONS_SCDB + ", " + KEY_MAX_LABEL_SCDB + " "
            + MAX_LABEL_OPTIONS_SCDB + ", " + KEY_MIN_VALUE_SCDB + " " + MIN_VALUE_SCDB +
            ", " + KEY_MAX_VALUE_SCDB + " " + MAX_VALUE_SCDB + ", " + KEY_ANSWER_NUMBER_SCDB +
            " " + ANSWER_NUMBER_SCDB + ");";

    private static final String DB_CREATE_GRID_COLUMN_ANSWERS_TABLE = "CREATE TABLE " +
            GRID_COLUMN_ANSWERS_TABLE + "( " + KEY_SURVEY_GCDB + " " + SURVEY_OPTIONS_GCDB +
            ", " + KEY_QUESTION_GCDB + " " + QUESTION_OPTIONS_GCDB + ", " +
            KEY_ANSWER_NUMBER_GCDB + " " + ANSWER_NUMBER_OPTIONS_GCDB + ", " +
            KEY_ANSWER_GCDB + " " + ANSWER_OPTIONS_GCDB + ");";

    private static final String DB_CREATE_GRID_ROW_ANSWERS_TABLE = "CREATE TABLE " +
            GRID_ROW_ANSWERS_TABLE + "( " + KEY_SURVEY_GRDB + " " + SURVEY_OPTIONS_GRDB +
            ", " + KEY_QUESTION_GRDB + " " + QUESTION_OPTIONS_GRDB + ", " +
            KEY_ANSWER_NUMBER_GRDB + " " + ANSWER_NUMBER_OPTIONS_GRDB + ", " +
            KEY_ANSWER_GRDB + " " + ANSWER_OPTIONS_GRDB + ");";

    private static final String DB_CREATE_NUMBER_CONSTRAINTS_TABLE = "CREATE TABLE " +
            NUMBER_CONSTRAINTS_TABLE + "( " + KEY_SURVEY_NCDB + " " + SURVEY_OPTIONS_NCDB + ", " +
            KEY_QUESTION_NCDB + " " + QUESTION_OPTIONS_NCDB + ", " + KEY_ANSWER_NUMBER_NCDB
            + " " + ANSWER_NUMBER_OPTIONS_NCDB + ", " + KEY_MIN_VALUE_NCDB +
            " " + MIN_VALUE_OPTIONS_NCDB + ", " + KEY_MAX_VALUE_NCDB + " " +
            MAX_VALUE_OPTIONS_NCDB + ", " + KEY_MUST_BE_INTEGER_NCDB + " " +
            MUST_BE_INTEGER_OPTIONS_NCDB + ", " + KEY_NOT_EQUALS_NCDB + " " +
            NOT_EQUALS_OPTIONS_NCDB + ", " + KEY_NOT_BETWEEN_NCDB + " " + NOT_BETWEEN_OPTIONS_NCDB +
            ");";

    private static final String DB_CREATE_TEXT_CONSTRAINTS_TABLE = "CREATE TABLE " +
            TEXT_CONSTRAINTS_TABLE + "( " + KEY_SURVEY_TCDB + " " + SURVEY_OPTIONS_TCDB + ", " +
            KEY_QUESTION_TCDB + " " + QUESTION_OPTIONS_TCDB + ", " + KEY_ANSWER_NUMBER_TCDB
            + " " + ANSWER_NUMBER_OPTIONS_TCDB + ", " + KEY_MIN_LENGTH_TCDB +
            " " + MIN_LENGTH_OPTIONS_TCDB + ", " + KEY_MAX_LENGTH_TCDB + " " +
            MAX_LENGTH_OPTIONS_TCDB + ", " + KEY_REGEX_TCDB + " " +
            REGEX_OPTIONS_TCDB + ");";

    private static final String DB_CREATE_FILLED_SURVEYS_TABLE = "CREATE TABLE " +
            FILLED_SURVEYS_TABLE + "( " + KEY_SURVEY_FSDB + " " + SURVEY_OPTIONS_FSDB + ", " +
           KEY_NO_FILLED_SURVEY_FSDB + " " + NO_FILLED_OPTIONS_FSDB + ", " + KEY_INTERVIEWER_FSDB +
            " " + INTERVIEWER_OPTIONS_FSDB + ", " + KEY_FROM_DATE_FSDB + " " + FROM_DATE_OPTIONS_FSDB
            + ", " + KEY_TO_DATE_FSDB + " " + TO_DATE_OPTIONS_FSDB + ");";

    private static final String DB_CREATE_ANSWERS_TABLE = "CREATE TABLE " +
            ANSWERS_TABLE + "( " + KEY_SURVEY_SADB + " " + SURVEY_OPTIONS_SADB + ", " +
            KEY_NO_FILLED_SURVEY_SADB + " " + NO_FILLED_SURVEY_OPTIONS_SADB + ", " + KEY_ANSWER_NUMBER_SADB +
            " " + ANSWER_NUMBER_OPTIONS_SADB + ", " + KEY_QUESTION_NUMBER_SADB + " "
            + QUESTION_NUMBER_OPTIONS_SADB + ", "  + KEY_ANSWER_SADB + " "
            + ANSWER_OPTIONS_SADB + ");";

    private static final String DB_CREATE_FILLING_PRIVILEGES_TABLE = "CREATE TABLE " +
            FILLING_PRIVILEGES_TABLE + "( " + KEY_INTERVIEWER_FPDB + " " + INTERVIEWER_OPTIONS_FPDB
            + ", " + KEY_SURVEY_FPDB + " " + SURVEY_OPTIONS_FPDB + ");";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_INTERVIEWERS_TABLE);
        db.execSQL(DB_CREATE_SURVEY_TEMPLATE_TABLE);
        db.execSQL(DB_CREATE_QUESTIONS_TABLE);
        db.execSQL(DB_CREATE_CHOICE_ANSWERS_TABLE);
        db.execSQL(DB_CREATE_SCALE_ANSWERS_TABLE);
        db.execSQL(DB_CREATE_GRID_COLUMN_ANSWERS_TABLE);
        db.execSQL(DB_CREATE_GRID_ROW_ANSWERS_TABLE);
        db.execSQL(DB_CREATE_NUMBER_CONSTRAINTS_TABLE);
        db.execSQL(DB_CREATE_TEXT_CONSTRAINTS_TABLE);
        db.execSQL(DB_CREATE_FILLED_SURVEYS_TABLE);
        db.execSQL(DB_CREATE_ANSWERS_TABLE);
        db.execSQL(DB_CREATE_FILLING_PRIVILEGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop = "DROP TABLE IF EXISTS ";
        db.execSQL(drop + FILLING_PRIVILEGES_TABLE);
        db.execSQL(drop + ANSWERS_TABLE);
        db.execSQL(drop + FILLED_SURVEYS_TABLE);
        db.execSQL(drop + TEXT_CONSTRAINTS_TABLE);
        db.execSQL(drop + NUMBER_CONSTRAINTS_TABLE);
        db.execSQL(drop + GRID_ROW_ANSWERS_TABLE);
        db.execSQL(drop + GRID_COLUMN_ANSWERS_TABLE);
        db.execSQL(drop + SCALE_ANSWERS_TABLE);
        db.execSQL(drop + CHOICE_ANSWERS_TABLE);
        db.execSQL(drop + QUESTIONS_TABLE);
        db.execSQL(drop + INTERVIEWERS_TABLE);
        db.execSQL(drop + SURVEY_TEMPLATE_TABLE);

        onCreate(db);
    }
}
