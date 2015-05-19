package bohonos.demski.mieldzioc.fillingSurvey;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.networkConnection.ServerConnectionFacade;
import bohonos.demski.mieldzioc.survey.Survey;
import bohonos.demski.mieldzioc.survey.SurveyHandler;

public class ChooseSurveyToFillActivity extends ActionBarActivity {

    private ChooseSurveyAdapter adapter =
            new ChooseSurveyAdapter(ChooseSurveyToFillActivity.this, SurveyHandler.ACTIVE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_survey_to_fill);

        ListView list = (ListView) findViewById(R.id.choose_survey_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationState applicationState =
                        ApplicationState.getInstance(ChooseSurveyToFillActivity.this);
                AnsweringSurveyControl control = applicationState.getAnsweringSurveyControl();
                final Survey survey = (Survey)adapter.getItem(position);
                control.startAnswering(survey.getIdOfSurveys(), applicationState.getLoggedInterviewer());
                Intent intent = new Intent(ChooseSurveyToFillActivity.this, WelcomeFillingActivity.class);
                intent.putExtra("SURVEY_TITLE", survey.getTitle());
                intent.putExtra("SURVEY_DESCRIPTION", survey.getDescription());
                intent.putExtra("SURVEY_SUMMARY", survey.getSummary());

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_survey_to_fill, menu);
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
