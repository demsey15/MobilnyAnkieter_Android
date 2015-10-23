package bohonos.demski.mieldzioc.mobilnyankieter.surveytemplates.creatingandeditingsurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.CreatingSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;

/**
 * Klasa odpowiedzialna za dodawanie pytań do ankiety (i edycji pytań podczas tworzenia).
 */
public class CreateNewSurveyQuestionsActivity extends ActionBarActivity
        implements ChoosingQuestionType.OnQuestionTypeChosenListener {

  private QuestionsAdapter questionsAdapter = new QuestionsAdapter(this);
  private CreatingSurveyControl control = CreatingSurveyControl.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_survey_questions);

        initializeQuestionsList();

        Button finishCreating = (Button) findViewById(R.id.finish_creating_survey_button);
        finishCreating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (control.finishCreating(ApplicationState.getInstance(
                        getApplicationContext()).
                        getSurveyHandler()) == null) {           //nie udało się dodać do bazy danych
                    Toast.makeText(CreateNewSurveyQuestionsActivity.this,
                            "Nie udało się dodać ankiety do bazy danych", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(CreateNewSurveyQuestionsActivity.this, "Dodano ankietę",
                            Toast.LENGTH_SHORT).show();
                }

                CreateNewSurveyQuestionsActivity.this.finish();
            }

        });
    }

    private void initializeQuestionsList(){
        ListView list = (ListView) findViewById(R.id.questionsListView);
        list.setAdapter(questionsAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startEditQuestionActivity(position);
            }
        });
    }



    /**
     * Metoda wywołana po wybraniu typu pytania, który chcemy dodać do ankiety.
     * Uruchamia aktywność edycji pytania.
     * @param questionType typ pytania (patrz stałe w klasie Question).
     */
    @Override
    public void onQuestionTypeChosen(int questionType) {
        if(questionType == Question.DATE_QUESTION){
            control.addDateQuestion();
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditDateTimeQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(control.getQuestionsCount() - 1));
            intent.putExtra("QUESTION_NUMBER", control.getQuestionsCount() - 1);
            startActivityForResult(intent, 1);
        }
        else if(questionType == Question.TIME_QUESTION){
            control.addTimeQuestion();
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditDateTimeQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(control.getQuestionsCount() - 1));
            intent.putExtra("QUESTION_NUMBER", control.getQuestionsCount() - 1);
            startActivityForResult(intent, 1);
        }
        else if(questionType == Question.SCALE_QUESTION){
            control.addScaleQuestion();
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditScaleQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(control.getQuestionsCount() - 1));
            intent.putExtra("QUESTION_NUMBER", control.getQuestionsCount() - 1);
            startActivityForResult(intent, 1);
        }
        else if(questionType == Question.TEXT_QUESTION) {
            control.addTextQuestion();
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditTextQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(control.getQuestionsCount() - 1));
            intent.putExtra("QUESTION_NUMBER", control.getQuestionsCount() - 1);
            startActivityForResult(intent, 1);
        }
        else if(questionType == Question.MULTIPLE_CHOICE_QUESTION) {
            control.addMultipleChoiceQuestion();
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditChoiceQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(control.getQuestionsCount() - 1));
            intent.putExtra("QUESTION_NUMBER", control.getQuestionsCount() - 1);
            startActivityForResult(intent, 1);
        }
        else if(questionType == Question.ONE_CHOICE_QUESTION) {
            control.addOneChoiceQuestion();
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditChoiceQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(control.getQuestionsCount() - 1));
            intent.putExtra("QUESTION_NUMBER", control.getQuestionsCount() - 1);
            startActivityForResult(intent, 1);
        }
        else if(questionType == Question.DROP_DOWN_QUESTION) {
            control.addDropDownListQuestion();
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditChoiceQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(control.getQuestionsCount() - 1));
            intent.putExtra("QUESTION_NUMBER", control.getQuestionsCount() - 1);
            startActivityForResult(intent, 1);
        }
        else if(questionType == Question.GRID_QUESTION) {
            control.addGridQuestion();
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditGridQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(control.getQuestionsCount() - 1));
            intent.putExtra("QUESTION_NUMBER", control.getQuestionsCount() - 1);
            startActivityForResult(intent, 1);
        }
    }

    /**
     * Metoda, która dla pytania o zadanym numerze uruchamia odpowiednią aktywność do edycji
     * tego pytania
     * @param questionNumber numer pytania na liście
     */
    private void startEditQuestionActivity(int questionNumber){
        int questionType = control.getQuestion(questionNumber).getQuestionType();
        if(questionType == Question.DATE_QUESTION){
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditDateTimeQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(questionNumber));
            intent.putExtra("QUESTION_NUMBER", questionNumber);
            startActivityForResult(intent, 2);
        }
        else if(questionType == Question.TIME_QUESTION){
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditDateTimeQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(questionNumber));
            intent.putExtra("QUESTION_NUMBER", questionNumber);
            startActivityForResult(intent, 2);
        }
        else if(questionType == Question.SCALE_QUESTION){
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditScaleQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(questionNumber));
            intent.putExtra("QUESTION_NUMBER", questionNumber);
            startActivityForResult(intent, 2);
        }
        else if(questionType == Question.TEXT_QUESTION) {
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditTextQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(questionNumber));
            intent.putExtra("QUESTION_NUMBER", questionNumber);
            startActivityForResult(intent, 2);
        }
        else if(questionType == Question.MULTIPLE_CHOICE_QUESTION ||
                questionType == Question.DROP_DOWN_QUESTION ||
                questionType == Question.ONE_CHOICE_QUESTION) {
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditChoiceQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(questionNumber));
            intent.putExtra("QUESTION_NUMBER", questionNumber);
            startActivityForResult(intent, 2);
        }
        else if(questionType == Question.GRID_QUESTION) {
            Intent intent = new Intent(CreateNewSurveyQuestionsActivity.this, EditGridQuestion.class);
            intent.putExtra("QUESTION", control.getQuestion(questionNumber));
            intent.putExtra("QUESTION_NUMBER", questionNumber);
            startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if (resultCode != RESULT_OK) {
                control.removeQuestion(control.getQuestionsCount() - 1);
            }
            questionsAdapter.notifyDataSetChanged();
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            questionsAdapter.notifyDataSetChanged();
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_new_survey_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.add_question:
                ChoosingQuestionType c = new ChoosingQuestionType();
                c.show(getFragmentManager(), "missilies");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
