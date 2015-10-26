package bohonos.demski.mieldzioc.mobilnyankieter.surveytemplates;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.database.AnsweringSurveyDBAdapter;
import bohonos.demski.mieldzioc.mobilnyankieter.filledsurveys.fillingSurvey.ChooseSurveyAdapter;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.SurveyHandler;

public class DeleteSurveyTemplateActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_survey_to_fill);

        prepareChooseSurveyList();
    }

    private void prepareChooseSurveyList() {
        final ChooseSurveyAdapter adapter =
                new ChooseSurveyAdapter(DeleteSurveyTemplateActivity.this, SurveyHandler.ACTIVE);

        ListView listView = (ListView) findViewById(R.id.choose_survey_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Survey survey = (Survey) adapter.getItem(i);

                (new AlertDialog.Builder(DeleteSurveyTemplateActivity.this)
                    .setMessage("Czy na pewno chcesz usunąć wybrany szablon ankiety? Tej operacji nie będzie można cofnąć!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ApplicationState.getInstance(getApplicationContext()).getSurveyHandler().deleteSurvey(survey);

                            adapter.notifyDataSetChanged();

                            showDeleteAnswersAlertDialog(survey);
                        }
                    })
                    .setNegativeButton("Nie", null)).show();
            }
        });
    }

    private void showDeleteAnswersAlertDialog(final Survey survey) {
        (new AlertDialog.Builder(DeleteSurveyTemplateActivity.this)
            .setMessage("Czy usunąć też wszystkie zebrane odpowiedzi do tej ankiety? Tej operacji nie będzie można cofnąć!")
            .setIcon(android.R.drawable.ic_delete)
            .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    AnsweringSurveyDBAdapter db = new AnsweringSurveyDBAdapter(getApplicationContext());
                    db.deleteAnswers(survey.getIdOfSurveys(), false, false, true);

                    Toast.makeText(getApplicationContext(), "Usunięto wybraną ankietę i wszystkie zebrane do niej odpowiedzi.", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), "Usunięto wybrany szablon ankiet.", Toast.LENGTH_SHORT).show();
                }
            })).show();
    }

}
