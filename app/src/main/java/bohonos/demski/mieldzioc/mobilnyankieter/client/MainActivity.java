package bohonos.demski.mieldzioc.mobilnyankieter.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.application.login.Login;
import bohonos.demski.mieldzioc.mobilnyankieter.filledsurveys.FilledSurveysActivity;
import bohonos.demski.mieldzioc.mobilnyankieter.filledsurveys.fillingSurvey.ChooseSurveyToFillActivity;
import bohonos.demski.mieldzioc.mobilnyankieter.surveytemplates.SurveyTemplateActivity;
import bohonos.demski.mieldzioc.mobilnyankieter.surveytemplates.creatingandeditingsurvey.CreateNewSurvey;

/**
 * Aktywność z różnymi akcjami do wyboru.
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareNewSurveyTemplateButton();
        prepareFillSurveyButton();
        prepareSurveyTemplatesButton();
        prepareFilledSurveysButton();
    }

    private void prepareFillSurveyButton() {
        Button fillSurveyButton = (Button) findViewById(R.id.fill_survey_button);
        fillSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseSurveyToFillActivity.class);
                startActivity(intent);
            }
        });
    }

    private void prepareNewSurveyTemplateButton() {
        Button newSurveyButt = (Button) findViewById(R.id.new_survey_button);

        newSurveyButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNewSurvey.class);
                startActivity(intent);
            }
        });
    }

    private void prepareSurveyTemplatesButton() {
        Button surveyTemplateButton = (Button) findViewById(R.id.survey_template_button);

        surveyTemplateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SurveyTemplateActivity.class);
                startActivity(intent);
            }
        });
    }

    private void prepareFilledSurveysButton() {
        Button sendFilledSurveysButton = (Button) findViewById(R.id.filled_survey_button);

        sendFilledSurveysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FilledSurveysActivity.class);
                startActivity(intent);
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
