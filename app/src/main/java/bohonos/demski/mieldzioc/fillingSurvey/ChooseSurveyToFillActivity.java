package bohonos.demski.mieldzioc.fillingSurvey;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
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
import bohonos.demski.mieldzioc.application.NetworkIssuesControl;
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
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem autoSending = menu.findItem(R.id.auto_sending);
        NetworkIssuesControl networkIssuesControl = new NetworkIssuesControl(getApplicationContext());
        if(autoSending == null) Log.d("NULL_DD", "null");
        if(networkIssuesControl.isNetworkAvailable()){
            ApplicationState applicationState = ApplicationState.getInstance(getApplicationContext());
            if(applicationState.isAutoSending()) {
                autoSending.setIcon(R.drawable.send_green);
                autoSending.setTitle("Ustawione automatyczne wysyłanie wypełnionych ankiet.");
            }
            else{
                autoSending.setIcon(R.drawable.send_red);
                autoSending.setTitle("Nie ustawiono automatycznego wysyłania wypełnionych ankiet.");
            }
        }
        else{
            autoSending.setIcon(R.drawable.send_inactive);
            autoSending.setTitle("Automatyczne wysyłanie niemożliwe - brak połączenia z internetem.");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.auto_sending:
                NetworkIssuesControl networkIssuesControl = new NetworkIssuesControl(getApplicationContext());
                ApplicationState applicationState = ApplicationState.getInstance(getApplicationContext());
                if (networkIssuesControl.isNetworkAvailable()) {
                    if (applicationState.isAutoSending()) {
                        applicationState.changeAutoSending();
                        item.setIcon(R.drawable.send_red);
                        item.setTitle("Nie ustawiono automatycznego wysyłania wypełnionych ankiet.");
                    } else {
                        applicationState.changeAutoSending();
                        item.setIcon(R.drawable.send_green);
                        item.setTitle("Ustawione automatyczne wysyłanie wypełnionych ankiet.");
                    }
                } else {
                    item.setIcon(R.drawable.send_inactive);
                    item.setTitle("Automatyczne wysyłanie niemożliwe - brak połączenia z internetem.");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
