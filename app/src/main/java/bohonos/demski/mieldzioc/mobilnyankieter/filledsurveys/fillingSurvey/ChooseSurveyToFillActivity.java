package bohonos.demski.mieldzioc.mobilnyankieter.filledsurveys.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveyHandler;

public class ChooseSurveyToFillActivity extends ActionBarActivity {

    private ChooseSurveyAdapter adapter =
            new ChooseSurveyAdapter(ChooseSurveyToFillActivity.this, SurveyHandler.ACTIVE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_survey_to_fill);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Wybierz ankietę");

        ListView list = (ListView) findViewById(R.id.choose_survey_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationState applicationState =
                        ApplicationState.getInstance(ChooseSurveyToFillActivity.this);
                AnsweringSurveyControl control = applicationState.getAnsweringSurveyControl();
                final Survey survey = (Survey)adapter.getItem(position);
                control.startAnswering(survey.getIdOfSurveys(), applicationState.getDeviceId());
                Intent intent = new Intent(ChooseSurveyToFillActivity.this, WelcomeFillingActivity.class);
                intent.putExtra("SURVEY_TITLE", survey.getTitle());
                intent.putExtra("SURVEY_DESCRIPTION", survey.getDescription());
                intent.putExtra("SURVEY_SUMMARY", survey.getSummary());

                startActivity(intent);
            }
        });
    }
}
