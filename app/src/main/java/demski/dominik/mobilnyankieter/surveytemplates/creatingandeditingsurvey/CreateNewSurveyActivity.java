package demski.dominik.mobilnyankieter.surveytemplates.creatingandeditingsurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import bohonos.demski.mieldzioc.mobilnyankieter.controls.CreatingSurveyControl;
import demski.dominik.mobilnyankieter.R;
import demski.dominik.mobilnyankieter.application.MessageWindow;
import demski.dominik.mobilnyankieter.application.UserPreferences;


/**
 * Tworzenie nowej ankiety - podawanie tytuly, opisu i tekstu pozegnania.
 */
public class CreateNewSurveyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_survey);
        final CreatingSurveyControl control = CreatingSurveyControl.getInstance();

        control.createNewSurvey(UserPreferences.getInstance(getApplicationContext()).getDeviceId());

        Button button = (Button) findViewById(R.id.add_questions_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText) findViewById(R.id.survey_title)).getText().toString();
                String description = ((EditText) findViewById(R.id.survey_description)).getText().toString();
                String summary = ((EditText) findViewById(R.id.survey_summary)).getText().toString();

                if(!title.equals("")) control.setSurveyTitle(title);
                if(!description.equals("")) control.setSurveyDescription(description);
                if(!summary.equals("")) control.setSurveySummary(summary);

                Intent intent = new Intent(CreateNewSurveyActivity.this, CreateNewSurveyQuestionsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_only_help_icon, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.help:
                MessageWindow.showHelpMessage(this, getResources().getString(R.string.help_title),
                        getResources().getText(R.string.creating_survey_start));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
