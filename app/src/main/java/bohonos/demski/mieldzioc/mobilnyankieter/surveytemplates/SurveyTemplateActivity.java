package bohonos.demski.mieldzioc.mobilnyankieter.surveytemplates;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import java.io.File;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.SendSurveysTemplateActivity;
import bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.creatingsurveysfiles.FileHandler;

public class SurveyTemplateActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_template);

        prepareSendSurveyTemplatesButton();
        prepareLoadSurveyTemplatesButton();

        Button deleteSurveyTemplatesButton = (Button) findViewById(R.id.delete_survey_templates_button);
        deleteSurveyTemplatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SurveyTemplateActivity.this, DeleteSurveyTemplateActivity.class);

                startActivity(intent);
                finish();
            }
        });
    }

    private void prepareSendSurveyTemplatesButton() {
        Button sendSurveyTemplatesButton = (Button) findViewById(R.id.send_surveys_template_button);
        sendSurveyTemplatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SurveyTemplateActivity.this, SendSurveysTemplateActivity.class);

                startActivity(intent);
            }
        });
    }

    private void prepareLoadSurveyTemplatesButton() {
        Button loadSurveyTemplatesButton = (Button) findViewById(R.id.load_survey_templates_button);

        final ViewAnimator animator = (ViewAnimator) findViewById(R.id.survey_templates_animator);

        loadSurveyTemplatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animator.setDisplayedChild(1);

                (new AsyncTask<Void, String, Pair<Integer[], String>>() {
                    @Override
                    protected Pair<Integer[], String> doInBackground(Void... voids) {
                        publishProgress("Trwa odczytywanie plików...");

                        FileHandler fileHandler = new FileHandler();

                        Pair<File[], String> result  = fileHandler.getFilesFromDirectory(fileHandler.getLoadingDir());

                        File[] files = result.first;

                        if(files == null){
                            return new Pair<>(null, result.second);
                        }
                        else{
                            Integer[] loadingStatistics = new Integer[] {0, 0, 0, 0, 0, 0, files.length};

                            for(int i = 0; i < files.length; i++){
                                publishProgress("Wczytywanie szablonów ankiet...", "(" + (i + 1) + "/" + files.length + ")");

                                File file = files[i];

                                LoadingSurveyTemplates loadingSurveyTemplates = new LoadingSurveyTemplates();
                                int loadingResult = loadingSurveyTemplates.loadSurveyTemplate(file, getApplicationContext());

                                loadingStatistics[loadingResult] = loadingStatistics[loadingResult] + 1;
                            }

                            if(loadingStatistics[LoadingSurveyTemplates.SURVEY_ADDED] == files.length){
                                return new Pair<>(null, "Wszystkie ankiety zostały wczytane (" + files.length + ")!");
                            }
                            else{
                                return new Pair<>(loadingStatistics, null);
                            }
                        }
                    }

                    @Override
                    protected void onPostExecute(Pair<Integer[], String> result) {
                        animator.setDisplayedChild(0);

                        if(result.first == null){
                            Toast.makeText(getApplicationContext(), result.second, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Integer[] statistics = result.first;

                            String communicate =  gerErrorLoadingSurveyMessage(statistics);

                            (new AlertDialog.Builder(SurveyTemplateActivity.this)
                                    .setTitle("Podsumowanie")
                                    .setMessage(communicate)
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setPositiveButton(android.R.string.ok, null))
                                    .show();
                        }
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        TextView progressTxt = (TextView) findViewById(R.id.text_loading_templates_progress);

                        progressTxt.setText(values[0]);

                        if(values.length > 1){
                            TextView progressTxt2 = (TextView) findViewById(R.id.text_2_loading_templates_progress);

                            progressTxt2.setText(values[1]);
                        }
                    }
                }).execute();
            }
        });
    }

    @NonNull
    private String gerErrorLoadingSurveyMessage(Integer[] statistics) {
        StringBuilder communicateBuilder = new StringBuilder();

        int amountOfNotLoadedTemplates = statistics[statistics.length - 1] - statistics[LoadingSurveyTemplates.SURVEY_ADDED];

        communicateBuilder.append("Liczba nie załadowanych szablonów: ");
        communicateBuilder.append(amountOfNotLoadedTemplates);
        communicateBuilder.append("\nNapraw błędy i/lub spróbuj ponownie.");
        communicateBuilder.append("\n\nSzczegóły:\n\nLiczba załadowanych ankiet: ");
        communicateBuilder.append(statistics[LoadingSurveyTemplates.SURVEY_ADDED]);
        communicateBuilder.append("\nLiczba plików w złym formacie: ");
        communicateBuilder.append(statistics[LoadingSurveyTemplates.WRONG_FILE_FORMAT]);
        communicateBuilder.append("\nLiczba wypełnionych ankiet zamiast szablonów ankiet: ");
        communicateBuilder.append(statistics[LoadingSurveyTemplates.SURVEY_ANSWER_INSTEAD_OF_SURVEY_TEMPLATE]);
        communicateBuilder.append("\nLiczba istniejących już wczesniej ankiet: ");
        communicateBuilder.append(statistics[LoadingSurveyTemplates.SURVEY_TEMPLATE_ALREADY_EXISTS]);
        communicateBuilder.append("\nLiczba plików, których nie mogę odczytać: ");
        communicateBuilder.append(statistics[LoadingSurveyTemplates.CANNOT_READ_FILE]);
        communicateBuilder.append("\nLiczba błędów podczas dodawania do bazy danych: ");
        communicateBuilder.append(statistics[LoadingSurveyTemplates.ERROR_DURING_ADDING_TO_DB]);

        return communicateBuilder.toString();
    }

}
