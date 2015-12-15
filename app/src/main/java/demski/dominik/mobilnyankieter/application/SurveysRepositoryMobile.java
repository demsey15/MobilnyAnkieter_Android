package demski.dominik.mobilnyankieter.application;

import android.content.Context;

import demski.dominik.mobilnyankieter.database.AnsweringSurveyDBAdapter;
import demski.dominik.mobilnyankieter.database.DataBaseAdapter;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveysRepository;

/**
 * Created by Dominik on 2015-05-10.
 */
public class SurveysRepositoryMobile extends SurveysRepository {
    private AnsweringSurveyDBAdapter answeringDbAdapter;
    private DataBaseAdapter dbAdapter;

    public SurveysRepositoryMobile(Context context) {
        answeringDbAdapter = new AnsweringSurveyDBAdapter(context);
        dbAdapter = new DataBaseAdapter(context);
    }

    @Override
    public long addNewSurvey(Survey survey) {
        long numberOfFilledSurveysWithThisId = dbAdapter.getNumberOfFilledSurveysAtThisDevice(survey.getIdOfSurveys());

        if(numberOfFilledSurveysWithThisId == -1){
            return -1;
        }
        else {
            numberOfFilledSurveysWithThisId++;

            survey.setNumberOfSurvey(numberOfFilledSurveysWithThisId);

            if (answeringDbAdapter.addAnswers(survey)) {
                dbAdapter.incrementNumberOfFilledSurveys(survey.getIdOfSurveys());

                return numberOfFilledSurveysWithThisId;
            } else {
                return -1;
            }
        }
    }
}
