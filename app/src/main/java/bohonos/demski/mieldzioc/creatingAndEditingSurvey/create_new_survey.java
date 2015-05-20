package bohonos.demski.mieldzioc.creatingAndEditingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.rits.cloning.Cloner;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.controls.CreatingSurveyControl;


public class create_new_survey extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_survey);
        final CreatingSurveyControl control = CreatingSurveyControl.getInstance();
        Log.d("CREATE_SURVEY", "Ankiete tworzy: " + ApplicationState.getInstance(getApplicationContext()).
                getLoggedInterviewer());
        control.createNewSurvey((new Cloner()).deepClone(ApplicationState.getInstance(getApplicationContext()).
                getLoggedInterviewer()));

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

                Intent intent = new Intent(create_new_survey.this, CreateNewSurveyQuestionsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_new_survey, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
