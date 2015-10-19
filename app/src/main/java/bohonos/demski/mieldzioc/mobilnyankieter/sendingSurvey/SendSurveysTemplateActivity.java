package bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.database.DataBaseAdapter;
import bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.bluetoothconnection.Bluetooth;
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

                        if(Bluetooth.isDeviceSupportingBluetooth()){
                            Bluetooth.requestBluetoothToBeEnabledIfItIsNot(SendSurveysTemplateActivity.this);
                        }

                        return 1;
                    }

                    @Override
                    protected void onPostExecute(Integer i) {
                        boolean result = false;

                        result = i == 1;

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
