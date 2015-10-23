package bohonos.demski.mieldzioc.mobilnyankieter.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ViewAnimator;

import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.creatingandeditingsurvey.CreateNewSurvey;
import bohonos.demski.mieldzioc.mobilnyankieter.database.AnsweringSurveyDBAdapter;
import bohonos.demski.mieldzioc.mobilnyankieter.fillingSurvey.ChooseSurveyToFillActivity;
import bohonos.demski.mieldzioc.mobilnyankieter.login.Login;
import bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.SendSurveysTemplateActivity;
import bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.creatingsurveysfiles.SurveyFileCreator;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;

/**
 * Aktywność z różnymi akcjami do wyboru.
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareSendFilledSurveysButton();

        Button newSurveyButt = (Button) findViewById(R.id.new_survey_button);

        newSurveyButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNewSurvey.class);
                startActivity(intent);
            }
        });


        Button sendSurveyButton = (Button) findViewById(R.id.survey_template_button);

        sendSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SendSurveysTemplateActivity.class);
                startActivity(intent);
            }
        });

        Button fillSurveyButton = (Button) findViewById(R.id.fill_survey_button);
        fillSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseSurveyToFillActivity.class);
                startActivity(intent);
            }
        });
    }

    private void prepareSendFilledSurveysButton() {
        Button sendFilledSurveysButton = (Button) findViewById(R.id.filled_survey_button);
        final ViewAnimator animator = (ViewAnimator) findViewById(R.id.main_animator);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.sending_survey_progress);

        sendFilledSurveysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnsweringSurveyDBAdapter answeringSurveyDBAdapter = new AnsweringSurveyDBAdapter(getApplicationContext());

                List<Survey> surveys = answeringSurveyDBAdapter.getAllAnswers();

                SurveyFileCreator surveyFileCreator = new SurveyFileCreator();
                surveyFileCreator.saveSurveyAnswers(surveys, getApplicationContext());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.log_out:
                logOut();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logOut() {
        ApplicationState.getInstance(getApplicationContext()).saveDontLogOut(false);

        Intent intent = new Intent(MainActivity.this, Login.class);

        finish();
        startActivity(intent);
    }
}
