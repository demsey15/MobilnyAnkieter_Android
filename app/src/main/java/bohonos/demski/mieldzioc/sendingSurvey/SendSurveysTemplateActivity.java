package bohonos.demski.mieldzioc.sendingSurvey;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import bohonos.demski.mieldzioc.application.NetworkIssuesControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.dataBase.DataBaseAdapter;
import bohonos.demski.mieldzioc.networkConnection.ServerConnectionFacade;
import bohonos.demski.mieldzioc.survey.Survey;

public class SendSurveysTemplateActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_survey_to_fill);

        ListView chooseSurvey = (ListView) findViewById(R.id.choose_survey_list);
        final SendingNotSentSurveyAdapter adapter = new SendingNotSentSurveyAdapter(getApplicationContext());
        chooseSurvey.setAdapter(adapter);

        chooseSurvey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Wybor ankiety to send", "Wybrano ankiete do przeslania");
                final TextView button = (TextView) view;
                Log.d("Wybor ankiety to send", "Wybrano ankiete do przeslania");
                final Survey survey = (Survey) adapter.getItem(position);
                Log.d("Wybor ankiety to send", "Wybrano ankiete do przeslania");
                (new AsyncTask<Survey, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Survey... params) {
                        publishProgress();
                        NetworkIssuesControl control = new NetworkIssuesControl(getApplicationContext());
                        return control.sendSurveyTemplate(params[0]);
                    }

                    @Override
                    protected void onPostExecute(Integer i) {
                        boolean result = false;
                        if(i == NetworkIssuesControl.NO_NETWORK_CONNECTION){
                            Toast.makeText(getApplicationContext(), "Brak połączenia z internetem",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if(i == ServerConnectionFacade.OPERATION_OK){
                           result = true;
                        }
                        else if(i == ServerConnectionFacade.TEMPLATE_ALREADY_EXISTS){
                            result = true;
                        }
                        if(result) {
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(getApplicationContext());
                            dataBaseAdapter.setSurveySent(survey, true);
                            button.setBackgroundColor(getResources().getColor(R.color.sent_button));
                        }
                        else button.setBackgroundColor(getResources().getColor(R.color.cant_send_button));
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        button.setBackgroundColor(getResources().getColor(R.color.during_sending_button));
                    }
                }).execute(survey);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_send_surveys_template, menu);
        return true;
    }

}
