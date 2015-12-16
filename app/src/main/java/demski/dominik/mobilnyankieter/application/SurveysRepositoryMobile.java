package demski.dominik.mobilnyankieter.application;

import android.content.Context;

import bohonos.demski.mieldzioc.mobilnyankieter.survey.ISurveyRepository;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import demski.dominik.mobilnyankieter.database.AnsweringSurveyDBAdapter;
import demski.dominik.mobilnyankieter.database.DataBaseAdapter;

/**
 * Created by Dominik on 2015-05-10.
 */
public class SurveysRepositoryMobile implements ISurveyRepository {
    private AnsweringSurveyDBAdapter answeringDbAdapter;
    private DataBaseAdapter dbAdapter;

    public SurveysRepositoryMobile(Context context) {
        answeringDbAdapter = new AnsweringSurveyDBAdapter(context);
        dbAdapter = new DataBaseAdapter(context);
    }

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
