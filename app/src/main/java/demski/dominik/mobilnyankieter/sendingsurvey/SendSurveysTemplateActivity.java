package demski.dominik.mobilnyankieter.sendingsurvey;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import demski.dominik.mobilnyankieter.R;
import demski.dominik.mobilnyankieter.application.MessageWindow;
import demski.dominik.mobilnyankieter.database.DataBaseAdapter;
import demski.dominik.mobilnyankieter.sendingsurvey.creatingsurveysfiles.SurveyFileCreator;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;

public class SendSurveysTemplateActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_survey_to_fill);

        prepareChooseSurveyList();
    }

    private void prepareChooseSurveyList() {
        ListView chooseSurvey = (ListView) findViewById(R.id.choose_survey_list);

        final SendingNotSentSurveyAdapter adapter = new SendingNotSentSurveyAdapter(getApplicationContext());

        chooseSurvey.setAdapter(adapter);

        chooseSurvey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView button = (TextView) view;
                final Survey survey = (Survey) adapter.getItem(position);

                (new AsyncTask<Survey, Void, Pair<Boolean, String>>() {
                    @Override
                    protected Pair<Boolean, String> doInBackground(Survey... params) {
                        publishProgress();

                        SurveyFileCreator surveyFileCreator = new SurveyFileCreator();

                        return surveyFileCreator.saveSurveyTemplate(survey);
                    }

                    @Override
                    protected void onPostExecute(Pair<Boolean, String> result) {
                        boolean isSuccessful = result.first;
                        String message = result.second;

                        if(isSuccessful) {
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(getApplicationContext());
                            dataBaseAdapter.setSurveySent(survey, true);

                            button.setBackgroundColor(getResources().getColor(R.color.sent_button));
                        }
                        else{
                            button.setBackgroundColor(getResources().getColor(R.color.cant_send_button));
                        }

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.menu_with_only_help_icon, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.help:
                MessageWindow.showHelpMessage(this, getResources().getString(R.string.help_title),
                        getResources().getText(R.string.export_survey_templates_help));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
