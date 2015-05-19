package bohonos.demski.mieldzioc.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.GregorianCalendar;

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

    /**
     * Zwraca ankietera albo null, jeœli nie ma w bazie takiego ankietera.
     * @param id
     * @return
     */
    public Interviewer getInterviewer(String id){
        Cursor cursor = db.query(DatabaseHelper.INTERVIEWERS_TABLE, new String[]
                {DatabaseHelper.KEY_ID_INTERVIEWER_IDB, DatabaseHelper.KEY_CAN_CREATE_IDB},
                DatabaseHelper.KEY_ID_INTERVIEWER_IDB + " = " + id,
                null, null, null, null);
        if(cursor.moveToFirst()){
            boolean canCreate = cursor.getInt(1) == 1;
            Interviewer interviewer = new Interviewer("", "", id, new GregorianCalendar());
            interviewer.setInterviewerPrivileges(canCreate);
            return interviewer;
        }
        else return null;
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
}
