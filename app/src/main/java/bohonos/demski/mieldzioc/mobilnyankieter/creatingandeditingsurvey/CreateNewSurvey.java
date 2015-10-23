package bohonos.demski.mieldzioc.mobilnyankieter.creatingandeditingsurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.CreatingSurveyControl;


/**
 * Tworzenie nowej ankiety - podawanie tytuly, opisu i tekstu pozegnania.
 */
public class CreateNewSurvey extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_survey);
        final CreatingSurveyControl control = CreatingSurveyControl.getInstance();

        control.createNewSurvey(ApplicationState.getInstance(getApplicationContext()).getDeviceId());

        ImageButton button = (ImageButton) findViewById(R.id.add_questions_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText) findViewById(R.id.survey_title)).getText().toString();
                String description = ((EditText) findViewById(R.id.survey_description)).getText().toString();
                String summary = ((EditText) findViewById(R.id.survey_summary)).getText().toString();

                if(!title.equals("")) control.setSurveyTitle(title);
                if(!description.equals("")) control.setSurveyDescription(description);
                if(!summary.equals("")) control.setSurveySummary(summary);

                Intent intent = new Intent(CreateNewSurvey.this, CreateNewSurveyQuestionsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

}
