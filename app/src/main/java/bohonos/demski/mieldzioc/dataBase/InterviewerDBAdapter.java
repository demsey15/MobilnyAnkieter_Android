package bohonos.demski.mieldzioc.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

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

    public void addInterviewer(Interviewer interviewer){
        open();
        ContentValues interviewerValues = new ContentValues();
        interviewerValues.put(DatabaseHelper.KEY_ID_INTERVIEWER_IDB, interviewer.getId());
        interviewerValues.put(DatabaseHelper.KEY_PASSWORD_IDB, "abdwe");   ///////DODAWANIE HASLA!!!!!!!!!!!
        if(db.insert(DatabaseHelper.INTERVIEWERS_TABLE, null, interviewerValues) != -1)
        Log.d(DEBUG_TAG, "Dodano ankietera: " + interviewer.getId());
        else Log.d(DEBUG_TAG, "Nie Dodano ankietera: " + interviewer.getId());
        close();
    }
}
