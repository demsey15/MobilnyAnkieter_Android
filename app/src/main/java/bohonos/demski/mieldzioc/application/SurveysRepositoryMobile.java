package bohonos.demski.mieldzioc.application;

import android.content.Context;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bohonos.demski.mieldzioc.dataBase.AnsweringSurveyDBAdapter;
import bohonos.demski.mieldzioc.survey.Survey;
import bohonos.demski.mieldzioc.survey.SurveysRepository;

/**
 * Created by Dominik on 2015-05-10.
 */
public class SurveysRepositoryMobile extends SurveysRepository {
    private AnsweringSurveyDBAdapter dbAdapter;
    private Context context;

    public SurveysRepositoryMobile(Context context) {
        super(new HashMap<String, List<Survey>>());
        dbAdapter = new AnsweringSurveyDBAdapter(context);
        this.context = context;
    }

    @Override
    public int addNewSurvey(Survey survey) {
        int id = super.addNewSurvey(survey);
        if(dbAdapter.addAnswers(survey)){
            if(ApplicationState.getInstance(context).isAutoSending()){
                (new AsyncTask<Survey, Void, Void>() {
                    @Override
                    protected Void doInBackground(Survey... params) {
                        NetworkIssuesControl networkIssuesControl = new NetworkIssuesControl(context);
                        networkIssuesControl.sendFilledSurvey(params[0]);
                        return null;
                    }
                }).execute(survey);
            }
            return id;
        }
        else return -1;
    }
}
