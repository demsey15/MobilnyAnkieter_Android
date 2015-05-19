package bohonos.demski.mieldzioc.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import bohonos.demski.mieldzioc.interviewer.Interviewer;

/**
 * Created by Dominik Demski on 2015-05-04.
 */
public class InterviewerDBAdapter {

    private static final String DEBUG_TAG = "SqLiteSurveyDB";

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    public InterviewerDBAdapter(Context context) {
        this.context = context;
    }

    public InterviewerDBAdapter open(){
        dbHelper = new DatabaseHelper(context);
        Log.d("Otwieram", "Otwieram po³¹czenie z baz¹!");
        try {
            db = dbHelper.getWritableDatabase();
        }
        catch(SQLiteException e){
            db = dbHelper.getReadableDatabase();
            Log.d(DEBUG_TAG, "Nie otrzymalem dostepu do bazy danych - przy operacji na ankieterze");
        }
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public void addInterviewer(Interviewer interviewer, char[] password){
        open();
        ContentValues interviewerValues = new ContentValues();
        interviewerValues.put(DatabaseHelper.KEY_ID_INTERVIEWER_IDB, interviewer.getId());
        interviewerValues.put(DatabaseHelper.KEY_PASSWORD_IDB, new String(password));
        interviewerValues.put(DatabaseHelper.KEY_CAN_CREATE_IDB,
                (interviewer.getInterviewerPrivileges())? 1 : 0);
        if(db.insert(DatabaseHelper.INTERVIEWERS_TABLE, null, interviewerValues) != -1)
        Log.d(DEBUG_TAG, "Dodano ankietera: " + interviewer.getId());
        else Log.d(DEBUG_TAG, "Nie Dodano ankietera: " + interviewer.getId());
        close();
    }

    public void deleteInterviewer(String id){
        open();
        db.delete(DatabaseHelper.INTERVIEWERS_TABLE, DatabaseHelper.KEY_ID_INTERVIEWER_IDB + " = " +
                id, null);
        close();
    }

    public void setInterviewerCreatingPrivileges(String interviewerId, boolean canCreate){
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_CAN_CREATE_IDB, (canCreate) ? 1 : 0);
        db.update(DatabaseHelper.INTERVIEWERS_TABLE, values, DatabaseHelper.KEY_ID_INTERVIEWER_IDB +
                " = " + interviewerId, null);
        close();
    }

    /**
     * Zwraca ankietera albo null, jeœli nie ma w bazie takiego ankietera.
     * @param id
     * @return
     */
    public Interviewer getInterviewer(String id){
       open();
        Cursor cursor = db.query(DatabaseHelper.INTERVIEWERS_TABLE, new String[]
                {DatabaseHelper.KEY_ID_INTERVIEWER_IDB, DatabaseHelper.KEY_CAN_CREATE_IDB},
                DatabaseHelper.KEY_ID_INTERVIEWER_IDB + " = " + id,
                null, null, null, null);
        if(cursor.moveToFirst()){
            boolean canCreate = cursor.getInt(1) == 1;
            Interviewer interviewer = new Interviewer("", "", id, new GregorianCalendar());
            interviewer.setInterviewerPrivileges(canCreate);
            close();
            return interviewer;
        }
        else{
            close();
            return null;
        }
    }

    /**
     * Sprawdza has³o dla danego ankietera, false, jeœli nie ma ankietera w bazie.
     * @param id
     * @param password
     * @return
     */
    public boolean checkPassword(String id, char[] password){
        Interviewer interviewer = getInterviewer(id);
        if(interviewer == null) return false;
        else{
            Cursor cursor = db.query(DatabaseHelper.INTERVIEWERS_TABLE, new String[]
                            {DatabaseHelper.KEY_ID_INTERVIEWER_IDB, DatabaseHelper.KEY_PASSWORD_IDB},
                    DatabaseHelper.KEY_ID_INTERVIEWER_IDB + " = " + id,
                    null, null, null, null);
            if(cursor.moveToFirst()) {
                char[] rightPassword = cursor.getString(1).toCharArray();
                if (rightPassword.length != password.length) {
                    for (int i = 0; i < rightPassword.length; i++) {
                        rightPassword[i] = 'a';
                    }
                    return false;
                }
                for (int i = 0; i < rightPassword.length; i++) {
                    if (rightPassword[i] != password[i]) {
                        for (int j = 0; j < rightPassword.length; j++) {
                            rightPassword[j] = 'a';
                            password[j] = 'a';
                        }
                        return false;
                    }
                }
                for (int j = 0; j < rightPassword.length; j++) {
                    rightPassword[j] = 'a';
                    password[j] = 'a';
                }
                return true;
            }
            else return false;
        }
    }

    /**
     * Pobierz id szablonów ankiet, które mo¿e wype³niaæ dany ankieter. Nie trzeba otwieraæ i zamykaæ
     * po³¹czenia.
     * @param interviewer
     * @return
     */
    public List<String> getSurveysToFillingForInterviewer(Interviewer interviewer){
       open();
        Cursor cursor = db.query(DatabaseHelper.FILLING_PRIVILEGES_TABLE, new String[]
                        {DatabaseHelper.KEY_INTERVIEWER_FPDB, DatabaseHelper.KEY_SURVEY_FPDB},
                DatabaseHelper.KEY_INTERVIEWER_FPDB + " = " + interviewer.getId(), null, null, null, null);
        List<String> result = new ArrayList<>();
        while (cursor.moveToNext()){
            result.add(cursor.getString(1));
        }
        close();
        return result;
    }

    /**
     * Usuwa z bazy stare przywileje i wstawia nowe.
     * @param interviewer
     * @param ids
     */
    public void updateSurveysToFillingForInterviewer(Interviewer interviewer, List<String> ids){
        open();
        db.delete(DatabaseHelper.FILLING_PRIVILEGES_TABLE, DatabaseHelper.KEY_INTERVIEWER_FPDB +
                " = " + interviewer.getId(), null);
        ContentValues values = new ContentValues();
        for(String id : ids){
            values.put(DatabaseHelper.KEY_INTERVIEWER_FPDB, interviewer.getId());
            values.put(DatabaseHelper.KEY_SURVEY_FPDB, id);
        }
        if(ids.size() > 0) {
            db.insert(DatabaseHelper.FILLING_PRIVILEGES_TABLE, null, values);
        }
        close();
    }
}
