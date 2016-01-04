package demski.dominik.mobilnyankieter.filledsurveys;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import demski.dominik.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import demski.dominik.mobilnyankieter.application.MessageWindow;
import demski.dominik.mobilnyankieter.database.AnsweringSurveyDBAdapter;
import demski.dominik.mobilnyankieter.sendingsurvey.SendingSurveyAnswersAdapter;
import demski.dominik.mobilnyankieter.sendingsurvey.creatingsurveysfiles.SurveyFileCreator;

public class FilledSurveysActionsActivity extends ActionBarActivity {
    public static final String LIST_ACTION_MODE = "MODE";
    public static final Integer JSON_MODE = 0;
    public static final Integer DELETING_MODE = 1;
    public static final Integer CSV_MODE = 2;

    private int mode;

    private Map<String, List<Survey>> allSurveys = new HashMap<>();
    private Map<String, List<Survey>> sentSurveys = new HashMap<>();
    private Map<String, List<Survey>> notSentSurveys = new HashMap<>();

    private boolean isAllSurveysFilterSet = false;
    private boolean isSentSurveysFilterSet = false;
    private boolean isNotSentSurveysFilterSet = false;

    private SendingSurveyAnswersAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_survey_to_fill);

        mode = getIntent().getIntExtra(LIST_ACTION_MODE, 0);

        if(savedInstanceState != null){
            boolean[] filter = savedInstanceState.getBooleanArray("FILTER");

            isSentSurveysFilterSet = filter[0];
            isNotSentSurveysFilterSet = filter[1];
            isAllSurveysFilterSet = filter[2];
        }
        else{
            isNotSentSurveysFilterSet = mode == CSV_MODE || mode == JSON_MODE;
            isSentSurveysFilterSet = mode == DELETING_MODE;
        }

        if(mode == DELETING_MODE){
            setTitle("Usuwanie wyników ankiet");
        }
        initSurveysLists();

        prepareListView();

        prepareStartingFilter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBooleanArray("FILTER", new boolean[]{isSentSurveysFilterSet, isNotSentSurveysFilterSet, isAllSurveysFilterSet});

    }

    private void prepareStartingFilter(){
        if(isNotSentSurveysFilterSet){
            adapter = new SendingSurveyAnswersAdapter(notSentSurveys, getApplicationContext());
        }
        else if(isSentSurveysFilterSet){
            adapter = new SendingSurveyAnswersAdapter(sentSurveys, getApplicationContext());
        }
        else{
            adapter = new SendingSurveyAnswersAdapter(allSurveys, getApplicationContext());
        }

        listView.setAdapter(adapter);
    }

    private void prepareListView() {
        listView = (ListView) findViewById(R.id.choose_survey_list);

        if(mode == JSON_MODE) {
            prepareListViewToSending();
        }
        else if(mode == DELETING_MODE){
            prepareListViewToDeleting();
        }
        else if(mode == CSV_MODE){
            prepareListViewToSending();
        }
    }

    private void prepareListViewToDeleting(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Survey survey = (Survey) adapter.getItem(i);
                        (new AlertDialog.Builder(FilledSurveysActionsActivity.this)
                                .setMessage("Czy na pewno chcesz usunąć zebrane wyniki wybranej ankiety? Tej operacji nie będzie można cofnąć!")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String idOfSurveys = survey.getIdOfSurveys();

                                        AnsweringSurveyDBAdapter db = new AnsweringSurveyDBAdapter(getApplicationContext());
                                        db.deleteAnswers(idOfSurveys, isSentSurveysFilterSet, isNotSentSurveysFilterSet, isAllSurveysFilterSet, JSON_MODE);

                                        if(isSentSurveysFilterSet){
                                            List<Survey> removed = sentSurveys.remove(idOfSurveys);
                                            removeSurveysWithTheSameIdFromSurveyMap(allSurveys, removed);

                                            adapter = new SendingSurveyAnswersAdapter(sentSurveys, getApplicationContext());
                                        }
                                        else if(isNotSentSurveysFilterSet){
                                            List<Survey> removed = notSentSurveys.remove(idOfSurveys);
                                            removeSurveysWithTheSameIdFromSurveyMap(allSurveys, removed);

                                            adapter = new SendingSurveyAnswersAdapter(notSentSurveys, getApplicationContext());
                                        }
                                        else if(isAllSurveysFilterSet){
                                            allSurveys.remove(idOfSurveys);
                                            sentSurveys.remove(idOfSurveys);
                                            notSentSurveys.remove(idOfSurveys);

                                            adapter = new SendingSurveyAnswersAdapter(allSurveys, getApplicationContext());
                                        }

                                        listView.setAdapter(adapter);

                                        Toast.makeText(getApplicationContext(), "Wyniki zostały usunięte.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Nie", null)
                ).show();
            }
        });
    }

    private void prepareListViewToSending() {
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
                    List<Survey> list = notSentSurveys.get(idOfSurveys);

                    if(list != null) {
                        toSend = new ArrayList<>(list);
                    }
                    else{
                        toSend = null;
                    }

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
                        if(lists[0] != null) {
                            publishProgress();

                            SurveyFileCreator surveyFileCreator = new SurveyFileCreator();

                            if(mode == JSON_MODE) {
                                return surveyFileCreator.saveSurveyAnswersInJson(lists[0], filterAbbr);
                            } else if(mode == CSV_MODE){
                                return surveyFileCreator.saveSurveyAnswersInCsv(lists[0], filterAbbr);
                            }
                            else{
                                return null;
                            }
                        }
                        else{
                            return null;
                        }
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        button.setBackgroundColor(getResources().getColor(R.color.during_sending_button));
                    }

                    @Override
                    protected void onPostExecute(Pair<Boolean, String> result) {
                        if (result != null) {
                            boolean isSuccessful = result.first;
                            String message = result.second;

                            if (isSuccessful) {
                                button.setBackgroundColor(getResources().getColor(R.color.sent_button));
                                adapter.setSurveyWasClicked(itemNumber);

                                changeSurveysListExisting(isNotSentSet, isAllSet, idOfSurveys);

                                showAlertDialogAboutRemovingSurveyAnswers(message, toSend, isAllSet, isSentSet, isNotSentSet);
                            } else {
                                button.setBackgroundColor(getResources().getColor(R.color.cant_send_button));
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }).execute(toSend);
            }
        });
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
        (new AlertDialog.Builder(FilledSurveysActionsActivity.this)
            .setMessage(message + "\nCzy chcesz usunąć wygenerowane wyniki ankiet z bazy danych " +
                    "(stworzone pliki nie zostaną usunięte)? Usuniętych danych nie będzie" +
                    " można przywrócić!")
            .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(sent != null && !sent.isEmpty()) {
                        AnsweringSurveyDBAdapter db = new AnsweringSurveyDBAdapter(getApplicationContext());

                        String idOfSurveys = sent.get(0).getIdOfSurveys();

                        if (isAllSet) {
                            db.deleteAnswers(idOfSurveys, false, false, true, mode);
                        }
                        else if(isSentSet){
                            db.deleteAnswers(idOfSurveys, true, false, false, mode);
                        } else if(isNotSentSet){
                            db.deleteAnswers(idOfSurveys, false, true, false, mode);
                        }

                        removeSurveysWithTheSameIdFromSurveyMap(sentSurveys, sent);

                        removeSurveysWithTheSameIdFromSurveyMap(allSurveys, sent);

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

                        if(mode == JSON_MODE) {
                            db.setSurveyAnswersAsSent(idOfSurveys);
                        }
                        else if(mode == CSV_MODE){
                            db.setSurveyAnswersWasMadeCSV(idOfSurveys);
                        }
                    }
                }
            })).show();
    }

    private void removeSurveysWithTheSameIdFromSurveyMap(Map<String, List<Survey>> removeFrom, List<Survey> toRemove) {
       if(toRemove != null && !toRemove.isEmpty()) {
           String idOfSurveys = toRemove.get(0).getIdOfSurveys();

           List<Survey> surveysWithIdOfSurveysId = removeFrom.get(idOfSurveys);

           surveysWithIdOfSurveysId.removeAll(toRemove);

           if (surveysWithIdOfSurveysId.isEmpty()) {
               removeFrom.remove(idOfSurveys);
           }
       }
    }

    private void initSurveysLists(){
        AnsweringSurveyDBAdapter dbAdapter = new AnsweringSurveyDBAdapter(getApplicationContext());

        List<Pair<Survey, Boolean>> theWhole;

        if(mode == JSON_MODE || mode == DELETING_MODE) {
            theWhole = dbAdapter.getAllAnswersWithSentStatus();
        }
        else{
            theWhole = dbAdapter.getAllAnswersWithCSVMadeStatus();
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_survey_answers, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if(isNotSentSurveysFilterSet){
            menu.findItem(R.id.filter_not_sent).setChecked(true);
        }
        else if(isSentSurveysFilterSet){
            menu.findItem(R.id.filter_sent).setChecked(true);
        }
        else{
            menu.findItem(R.id.filter_all).setChecked(true);
        }


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
            case R.id.help:
                CharSequence toShow = "";

                if(mode == CSV_MODE){
                    toShow = getResources().getText(R.string.export_survey_to_csv_help);
                }
                else if(mode == JSON_MODE){
                    toShow = getResources().getText(R.string.export_survey_to_json_help);
                }
                else if(mode == DELETING_MODE){
                    toShow = getResources().getText(R.string.delete_survey_answers_help);
                }

                MessageWindow.showHelpMessage(this, getResources().getString(R.string.help_title), toShow);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
