package bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.database.DataBaseAdapter;
import bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.creatingsurveysfiles.SurveyFileCreator;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;

public class SendSurveysTemplateActivity extends ActionBarActivity {
    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                devices.add(device.getName() + "\n" + device.getAddress());

                Log.d("BLUETOOTH_DD", Arrays.toString(devices.toArray()));
            }
        }
    };

    private List<String> devices = new ArrayList<>();



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

                        SurveyFileCreator surveyFileCreator = new SurveyFileCreator();
                        surveyFileCreator.saveSurveyTemplate(survey, getApplicationContext());

                        return 1;
                    }

                    @Override
                    protected void onPostExecute(Integer i) {
                        boolean result = i == 1;

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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_send_surveys_template, menu);
        return true;
    }

}
