package bohonos.demski.mieldzioc.fillingSurvey;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.GenerateId;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.questions.Question;

public class AnswerMultipleChoiceQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private List<Button> answers = new ArrayList<>();
    private List<Button> chosenAnswer = new ArrayList<>();
    private int myQuestionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_multiple_choice_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        question = answeringSurveyControl.getQuestion(myQuestionNumber);

        if (!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_multiple_choice);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_multiple_choice);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_multiple_choice);
        questionHint.setText(question.getHint());

        List<String> answerList = question.getAnswersAsStringList();
        LinearLayout answersLinear = (LinearLayout) findViewById(R.id.answer_list_answers_multiple_choice);

        for (String ans : answerList) {
            Button button = new Button(this);
            button.setText(ans);
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setId(GenerateId.generateViewId());
            button.setBackgroundColor(getResources().getColor(R.color.odpowiedz_button));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  //po kliknięciu zmień kolor odpowiedzi na czarny
                    if (!chosenAnswer.isEmpty() && chosenAnswer.contains(v)) { //jeśli kliknięto na zaznaczoną już odpowiedź
                        v.setBackgroundColor(getResources().getColor(R.color.odpowiedz_button)); //odznacz ją
                        chosenAnswer.remove(v);
                    } else {
                        v.setBackgroundColor(getResources().getColor(R.color.chosen_answer_button)); //zaznacz wybraną odpowiedź
                        chosenAnswer.add((Button) v);
                    }
                }
            });
            answers.add(button);
            answersLinear.addView(button);
        }

        ImageButton nextButton = (ImageButton) findViewById(R.id.next_question_button);
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
                            Intent intent = new Intent(AnswerMultipleChoiceQuestionActivity.this, SurveysSummary.class);
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
                                    ApplicationState.getInstance(getApplicationContext()).getLoggedInterviewer());
                            intent.putExtra("SURVEY_TITLE", answeringSurveyControl.getSurveysTitle());
                            intent.putExtra("SURVEY_DESCRIPTION", answeringSurveyControl.getSurveysDescription());
                            intent.putExtra("SURVEY_SUMMARY", answeringSurveyControl.getSurveysSummary());
                            startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(getApplicationContext(), "Nie można zakończyć ankiety", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void goToNextActivity() {
        AnsweringSurveyControl control = ApplicationState.
                getInstance(AnswerMultipleChoiceQuestionActivity.this).getAnsweringSurveyControl();

        if(control.getNumberOfQuestions() - 1 > myQuestionNumber){
            Question question = control.getQuestion(myQuestionNumber + 1);
            int questionType = question.getQuestionType();
            Intent intent;
            if(questionType == Question.ONE_CHOICE_QUESTION){
                intent = new Intent(AnswerMultipleChoiceQuestionActivity.this,
                        AnswerOneChoiceQuestionActivity.class);
            }
            else if(questionType == Question.MULTIPLE_CHOICE_QUESTION){
                intent = new Intent(AnswerMultipleChoiceQuestionActivity.this,
                        AnswerMultipleChoiceQuestionActivity.class);
            }
            else if(questionType == Question.DROP_DOWN_QUESTION){
                intent = new Intent(AnswerMultipleChoiceQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
            }
            else if(questionType == Question.SCALE_QUESTION){
                intent = new Intent(AnswerMultipleChoiceQuestionActivity.this, AnswerScaleQuestionActivity.class);
            }
            else if(questionType == Question.DATE_QUESTION){
                intent = new Intent(AnswerMultipleChoiceQuestionActivity.this, AnswerDateQuestionActivity.class);
            }
            else if(questionType == Question.TIME_QUESTION){
                intent = new Intent(AnswerMultipleChoiceQuestionActivity.this, AnswerTimeQuestionActivity.class);
            }
            else if(questionType == Question.GRID_QUESTION){
                intent = new Intent(AnswerMultipleChoiceQuestionActivity.this, AnswerGridQuestionActivity.class);
            }
            else if(questionType == Question.TEXT_QUESTION){
                intent = new Intent(AnswerMultipleChoiceQuestionActivity.this, AnswerTextQuestionActivity.class);
            }
            else intent = new Intent(AnswerMultipleChoiceQuestionActivity.this, SurveysSummary.class);
            intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
            startActivity(intent);
        }
    }

    private boolean setUserAnswer(){
        AnsweringSurveyControl control = ApplicationState.
                getInstance(AnswerMultipleChoiceQuestionActivity.this).getAnsweringSurveyControl();
        if(question.isObligatory()){
            if(chosenAnswer.isEmpty()){     //jeśli pytanie jest obowiązkowe i nic nie dodano
                Toast.makeText(AnswerMultipleChoiceQuestionActivity.this,
                        "To pytanie jest obowiązkowe, podaj odpowiedź!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(! chosenAnswer.isEmpty()){
            List<String> toReturn = new ArrayList<>(chosenAnswer.size());
            for(Button answ : chosenAnswer) {
                toReturn.add(answ.getText().toString());
            }
            if(control.setMultipleChoiceQuestionAnswer(myQuestionNumber, toReturn))
                return true;
            else{
                Toast.makeText(AnswerMultipleChoiceQuestionActivity.this,
                        "Coś poszło nie tak, nie dodano odpowiedzi.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_multiple_choice_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
