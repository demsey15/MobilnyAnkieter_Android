package demski.dominik.mobilnyankieter.filledsurveys.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.common.Pair;
import demski.dominik.mobilnyankieter.R;
import demski.dominik.mobilnyankieter.application.ApplicationState;
import demski.dominik.mobilnyankieter.application.UserPreferences;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.GridQuestion;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import demski.dominik.mobilnyankieter.utilities.GenerateId;

public class AnswerGridQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private List<String> rowLabels = new ArrayList<>();
    private List<String> columnLabels = new ArrayList<>();
    private List<List<RadioButton>> radioButtons = new ArrayList<>();
    private int myQuestionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_grid_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(answeringSurveyControl.getSurveysTitle());

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        question = answeringSurveyControl.getQuestion(myQuestionNumber);

        prepareQuestionView();

        ArrayList<Pair<String, String>> answers = new ArrayList<>();

        if(savedInstanceState != null){
            answers = (ArrayList<Pair<String, String>>) savedInstanceState.getSerializable("CHOSEN_ANSWERS");
        }

        createGrid(answers);

        prepareNextAndFinishButtons();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Pair<String, String>> answers = new ArrayList<>();

        for(int i = 0; i < radioButtons.size(); i++){
            for(int j = 0; j < radioButtons.get(0).size(); j++){
                RadioButton button = radioButtons.get(i).get(j);
                if(button.isChecked()){
                    answers.add(new Pair<>(rowLabels.get(i), columnLabels.get(j)));
                }
            }
        }

        outState.putSerializable("CHOSEN_ANSWERS", answers);
    }

    private void prepareQuestionView() {
        if (!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_grid);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_grid);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_grid);
        questionHint.setText(question.getHint());
    }

    private void prepareNextAndFinishButtons() {
        Button nextButton = (Button) findViewById(R.id.next_question_button);
        Button finishButton = (Button) findViewById(R.id.end_filling_button);
        Button finishAndStartButton = (Button) findViewById(R.id.end_and_start_filling_button);
        if(answeringSurveyControl.getNumberOfQuestions() - 1 > myQuestionNumber) {  //jeśli to nie jest ostatnie pytanie
            finishButton.setVisibility(View.INVISIBLE);
            finishAndStartButton.setVisibility(View.INVISIBLE);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (setUserAnswer())
                        goToNextActivity();
                }
            });
        }
        else{
            nextButton.setVisibility(View.INVISIBLE);
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (setUserAnswer()) {
                        if (answeringSurveyControl.finishAnswering(ApplicationState.
                                getInstance(getApplicationContext()).getSurveysRepository())) {
                            Intent intent = new Intent(AnswerGridQuestionActivity.this, SurveysSummary.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
                            startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(getApplicationContext(), "Nie można zakończyć ankiety", Toast.LENGTH_SHORT);
                    }
                }
            });
            finishAndStartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (setUserAnswer()) {
                        String idOfSurveys = answeringSurveyControl.getIdOfSurveysFillingSurvey(); //id wypełnianej ankiety
                        if (answeringSurveyControl.finishAnswering(ApplicationState.
                                getInstance(getApplicationContext()).getSurveysRepository())) {
                            Intent intent = new Intent(getApplicationContext(), WelcomeFillingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            answeringSurveyControl.startAnswering(idOfSurveys,          //rozpocznij wypełnianie nowej ankiety
                                    UserPreferences.getInstance(getApplicationContext()).getDeviceId());
                            intent.putExtra("SURVEY_TITLE", answeringSurveyControl.getSurveysTitle());
                            intent.putExtra("SURVEY_DESCRIPTION", answeringSurveyControl.getSurveysDescription());
                            intent.putExtra("SURVEY_SUMMARY", answeringSurveyControl.getSurveysSummary());
                            startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(getApplicationContext(), "Nie można zakończyć ankiety",
                                    Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void createGrid(ArrayList<Pair<String, String>> chosenAnswers){
        TableLayout tableLayout = (TableLayout) findViewById(R.id.answer_grid_table);
        GridQuestion gridQuestion = (GridQuestion) question;
        rowLabels = gridQuestion.getRowLabels();
        columnLabels = gridQuestion.getColumnLabels();

        TableRow row = new TableRow(this);
        row.addView(new TextView(this));
        for(String label : columnLabels){
            TextView textView = new TextView(this);
            textView.setText(label);
            textView.setPadding(5, 5, 50, 50);
            textView.setTextColor(getResources().getColor(R.color.black));
            row.addView(textView);
        }
        tableLayout.addView(row);

        for(String rowLabel : rowLabels){
            TableRow row2 = new TableRow(this);
            TextView textView = new TextView(this);
            textView.setText(rowLabel);
            textView.setPadding(5, 5, 50, 50);
            textView.setTextColor(getResources().getColor(R.color.black));
            row2.addView(textView);
            List<RadioButton> list = new ArrayList<>(rowLabels.size());
            radioButtons.add(list);
            for(String colLabel : columnLabels){
                RadioButton radioButton = new RadioButton(this){
                    @Override
                    public void toggle() {
                        if(isChecked()) {
                                setChecked(false);

                        } else {
                            setChecked(true);
                        }
                    }
                };
                radioButton.setId(GenerateId.generateViewId());
                radioButton.setPadding(5, 5, 5, 5);

                if(chosenAnswers.contains(new Pair<String, String>(rowLabel, colLabel))){
                    radioButton.setChecked(true);
                }

                list.add(radioButton);
                row2.addView(radioButton);
            }
            tableLayout.addView(row2);
        }
    }

    private void goToNextActivity() {
        AnsweringSurveyControl control = ApplicationState.
                getInstance(AnswerGridQuestionActivity.this).getAnsweringSurveyControl();

        if(control.getNumberOfQuestions() - 1 > myQuestionNumber){
            Question question = control.getQuestion(myQuestionNumber + 1);
            int questionType = question.getQuestionType();
            Intent intent;
            if(questionType == Question.ONE_CHOICE_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this,
                        AnswerOneChoiceQuestionActivity.class);
            }
            else if(questionType == Question.MULTIPLE_CHOICE_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this,
                        AnswerMultipleChoiceQuestionActivity.class);
            }
            else if(questionType == Question.DROP_DOWN_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
            }
            else if(questionType == Question.SCALE_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerScaleQuestionActivity.class);
            }
            else if(questionType == Question.DATE_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerDateQuestionActivity.class);
            }
            else if(questionType == Question.TIME_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerTimeQuestionActivity.class);
            }
            else if(questionType == Question.GRID_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerGridQuestionActivity.class);
            }
            else if(questionType == Question.TEXT_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerTextQuestionActivity.class);
            }
            else{
                intent = new Intent(AnswerGridQuestionActivity.this, SurveysSummary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
            startActivity(intent);
        }
    }

    private boolean setUserAnswer(){
        //Każda odpowiedź powinna być w formacie: #rowLabel# ^columnLabel^
        List<String> answers = new ArrayList<>();
        for(int i = 0; i < radioButtons.size(); i++){
            for(int j = 0; j < radioButtons.get(0).size(); j++){
                RadioButton button = radioButtons.get(i).get(j);
                if(button.isChecked()){
                    String answer = "#" + rowLabels.get(i) + "#" + " ^" + columnLabels.get(j) + "^";
                    answers.add(answer);
                }
            }
        }
        if(question.isObligatory()){
            if(answers.isEmpty()){     //jeśli pytanie jest obowiązkowe i nic nie dodano
                Toast.makeText(AnswerGridQuestionActivity.this,
                        "To pytanie jest obowiązkowe, podaj odpowiedź!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(! answers.isEmpty()){
            if(answeringSurveyControl.setGridQuestionAnswer(myQuestionNumber, answers))
                return true;
            else{
                Toast.makeText(this,
                        "Coś poszło nie tak, nie dodano odpowiedzi.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
