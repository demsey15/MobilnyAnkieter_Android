package bohonos.demski.mieldzioc.mobilnyankieter.application;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.database.AnsweringSurveyDBAdapter;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveysRepository;

/**
 * Created by Dominik on 2015-05-10.
 */
public class SurveysRepositoryMobile extends SurveysRepository {
    private AnsweringSurveyDBAdapter dbAdapter;

    public SurveysRepositoryMobile(Context context) {
        super(new HashMap<String, List<Survey>>());
        dbAdapter = new AnsweringSurveyDBAdapter(context);
    }

    @Override
    public int addNewSurvey(Survey survey) {
        int id = super.addNewSurvey(survey);
        if(dbAdapter.addAnswers(survey)){
            return id;
        }
        else{
            return -1;
        }
    }
}
