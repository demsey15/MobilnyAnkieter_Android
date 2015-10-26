package bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.database.AnsweringSurveyDBAdapter;
import bohonos.demski.mieldzioc.mobilnyankieter.database.DataBaseAdapter;
import bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.creatingsurveysfiles.SurveyFileCreator;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;

public class SendingSurveyAnswersActivity extends ActionBarActivity {
    private Map<String, List<Survey>> allSurveys = new HashMap<>();
    private Map<String, List<Survey>> sentSurveys = new HashMap<>();
    private Map<String, List<Survey>> notSentSurveys = new HashMap<>();

    private boolean isAllSurveysFilterSet = false;
    private boolean isSentSurveysFilterSet = false;
    private boolean isNotSentSurveysFilterSet = true;

    private SendingSurveyAnswersAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_survey_to_fill);

        initSurveysLists();

        prepareListView();
    }

    private void prepareListView() {
        listView = (ListView) findViewById(R.id.choose_survey_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                final boolean isSentSet = isSentSurveysFilterSet;
                final boolean isNotSentSet = isNotSentSurveysFilterSet;
                final boolean isAllSet = isAllSurveysFilterSet;

                final int itemNumber = i;

                Survey survey = (Survey) adapter.getItem(i);
                final String idOfSurveys = survey.getIdOfSurveys();

                final List<Survey> toSend;
                final String filterAbbr;

                if(isAllSet){
                    toSend = new ArrayList<>(allSurveys.get(idOfSurveys));

                    filterAbbr = "all";
                }
                else if(isNotSentSet){
                    toSend = new ArrayList<>(notSentSurveys.get(idOfSurveys));

                    filterAbbr = "notSent";
                }
                else if(isSentSet){
                    toSend = new ArrayList<>(sentSurveys.get(idOfSurveys));

                    filterAbbr = "sent";
                } else{
                    toSend = new ArrayList<>();

                    filterAbbr = "";
                }

                final TextView button = (TextView) view;

                (new AsyncTask<List<Survey>, Void, Pair<Boolean, String>>() {
                    @Override
                    protected Pair<Boolean, String> doInBackground(List<Survey>... lists) {
                        publishProgress();

                        SurveyFileCreator surveyFileCreator = new SurveyFileCreator();

                        return surveyFileCreator.saveSurveyAnswers(lists[0], getApplicationContext(), filterAbbr);
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        button.setBackgroundColor(getResources().getColor(R.color.during_sending_button));
                    }

                    @Override
                    protected void onPostExecute(Pair<Boolean, String> result) {
                        boolean isSuccessful = result.first;
                        String message = result.second;

                        if(isSuccessful) {
                            button.setBackgroundColor(getResources().getColor(R.color.sent_button));
                            adapter.setSurveyWasClicked(itemNumber);

                            changeSurveysListExisting(isNotSentSet, isAllSet, idOfSurveys);

                            showAlertDialogAboutRemovingSurveyAnswers(message, toSend, isAllSet, isSentSet, isNotSentSet);
                        }
                        else{
                            button.setBackgroundColor(getResources().getColor(R.color.cant_send_button));
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    }
                }).execute(toSend);
            }
        });

        adapter = new SendingSurveyAnswersAdapter(notSentSurveys, getApplicationContext());

        listView.setAdapter(adapter);
    }

    private void changeSurveysListExisting(boolean isNotSentSet, boolean isAllSet, String idOfSurveys) {
        if(isNotSentSet || isAllSet){
            List<Survey> alreadySent = notSentSurveys.remove(idOfSurveys);

            if(alreadySent != null){
                List<Survey> sentWithTheSameId = sentSurveys.get(idOfSurveys);

                if(sentWithTheSameId != null){
                    sentWithTheSameId.addAll(alreadySent);
                }
                else{
                    sentSurveys.put(idOfSurveys, alreadySent);
                }
            }
        }
    }

    private void showAlertDialogAboutRemovingSurveyAnswers(String message, final List<Survey> sent,
                                                           final boolean isAllSet, final boolean isSentSet,
                                                           final boolean isNotSentSet) {
        (new AlertDialog.Builder(SendingSurveyAnswersActivity.this)
            .setMessage(message + "\nCzy chcesz usunąć przesłane wyniki ankiet z bazy danych " +
                    "(wygenerowane pliki nie zostaną usunięte)? Usuniętych danych nie będzie" +
                    " można przywrócić!")
            .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(sent != null && !sent.isEmpty()) {
                        AnsweringSurveyDBAdapter db = new AnsweringSurveyDBAdapter(getApplicationContext());

                        String idOfSurveys = sent.get(0).getIdOfSurveys();

                        if (isAllSet) {
                            db.deleteAnswers(idOfSurveys, false, false, true);
                        }
                        else if(isSentSet){
                            db.deleteAnswers(idOfSurveys, true, false, false);
                        } else if(isNotSentSet){
                            db.deleteAnswers(idOfSurveys, false, true, false);
                        }

                        Log.d("SENT_LIST", "" +sent.size());
                        removeSurveysWithTheSameIdFromSurveyMap(sentSurveys, sent);
                        Log.d("SENT_LIST","" + sent.size());
                        removeSurveysWithTheSameIdFromSurveyMap(allSurveys, sent);
                        Log.d("SENT_LIST","" + sent.size());

                        Toast.makeText(getApplicationContext(), "Liczba usuniętych odpowiedzi: " + sent.size(), Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(sent != null && !sent.isEmpty()) {
                        String idOfSurveys = sent.get(0).getIdOfSurveys();

                        AnsweringSurveyDBAdapter db = new AnsweringSurveyDBAdapter(getApplicationContext());

                        db.setSurveyAnswersAsSent(idOfSurveys);
                    }
                }
            })).show();
    }

    private void removeSurveysWithTheSameIdFromSurveyMap(Map<String, List<Survey>> removeFrom, List<Survey> toRemove) {
       if(toRemove != null && !toRemove.isEmpty()) {
           Log.d("SENT_LIST_1", "" + toRemove.size());
           String idOfSurveys = toRemove.get(0).getIdOfSurveys();
           Log.d("SENT_LIST_1", "" + toRemove.size());
           List<Survey> surveysWithIdOfSurveysId = removeFrom.get(idOfSurveys);
           Log.d("SENT_LIST_1", "" + toRemove.size());
           surveysWithIdOfSurveysId.removeAll(toRemove);
           Log.d("SENT_LIST_1", "" + toRemove.size());
           if (surveysWithIdOfSurveysId.isEmpty()) {
               removeFrom.remove(idOfSurveys);
           }
           Log.d("SENT_LIST_1", "" + toRemove.size());
       }
    }

    private void initSurveysLists(){
        AnsweringSurveyDBAdapter dbAdapter = new AnsweringSurveyDBAdapter(getApplicationContext());

        List<Pair<Survey, Boolean>> theWhole = dbAdapter.getAllAnswersWithSentStatus();

        for(Pair<Survey, Boolean> pair : theWhole){
            Survey survey = pair.first;

            boolean isSent = pair.second;

            if(isSent){
                addToSurveyMap(survey, sentSurveys);
            } else{
                addToSurveyMap(survey, notSentSurveys);
            }

            addToSurveyMap(survey, allSurveys);
        }
    }

    private void addToSurveyMap(Survey survey, Map<String, List<Survey>> mapToAdd) {
        String idOfSurveys = survey.getIdOfSurveys();
        List<Survey> surveys = mapToAdd.get(idOfSurveys);

        if(surveys != null){
            surveys.add(survey);
        }
        else{
            surveys = new ArrayList<>();
            surveys.add(survey);

            mapToAdd.put(idOfSurveys, surveys);
        }
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

                        if (isSuccessful) {
                            DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(getApplicationContext());
                            dataBaseAdapter.setSurveySent(survey, true);

                            button.setBackgroundColor(getResources().getColor(R.color.sent_button));
                        } else {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_survey_answers, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.filter_sent:
                item.setChecked(true);

                isAllSurveysFilterSet = false;
                isNotSentSurveysFilterSet = false;
                isSentSurveysFilterSet = true;

                adapter = new SendingSurveyAnswersAdapter(sentSurveys, getApplicationContext());
                listView.setAdapter(adapter);

                return true;
            case R.id.filter_not_sent:
                item.setChecked(true);

                isAllSurveysFilterSet = false;
                isNotSentSurveysFilterSet = true;
                isSentSurveysFilterSet = false;

                adapter = new SendingSurveyAnswersAdapter(notSentSurveys, getApplicationContext());
                listView.setAdapter(adapter);

                return true;
            case R.id.filter_all:
                item.setChecked(true);

                isAllSurveysFilterSet = true;
                isNotSentSurveysFilterSet = false;
                isSentSurveysFilterSet = false;

                adapter = new SendingSurveyAnswersAdapter(allSurveys, getApplicationContext());
                listView.setAdapter(adapter);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
