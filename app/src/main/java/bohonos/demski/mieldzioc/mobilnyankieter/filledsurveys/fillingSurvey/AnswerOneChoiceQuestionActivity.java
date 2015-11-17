package bohonos.demski.mieldzioc.mobilnyankieter.filledsurveys.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.application.UserPreferences;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import bohonos.demski.mieldzioc.mobilnyankieter.utilities.GenerateId;

public class AnswerOneChoiceQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private List<Button> answers = new ArrayList<>();
    private Button chosenAnswer;
    private int myQuestionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_one_choice_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(answeringSurveyControl.getSurveysTitle());


        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        question = answeringSurveyControl.getQuestion(myQuestionNumber);
        Log.d("WYPELNIANIE_ANKIETY", AnswerOneChoiceQuestionActivity.class + " nr pytania: " +
                myQuestionNumber);
        if(!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_one_choice);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_one_choice);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_one_choice);
        questionHint.setText(question.getHint());

        List<String> answerList = question.getAnswersAsStringList();

        LinearLayout answersLinear = (LinearLayout) findViewById(R.id.answer_list_answers_one_choice);

        for(String ans : answerList){
            Button button = new Button(this);
            button.setText(ans);
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setId(GenerateId.generateViewId());
            button.setBackgroundColor(getResources().getColor(R.color.odpowiedz_button));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  //po kliknięciu zmień kolor odpowiedzi na czarny (
                    // resztę na ponarańczowy)
                    if (chosenAnswer != null && chosenAnswer.getId() == v.getId()) { //jeśli kliknięto na zaznaczoną już odpowiedź
                        v.setBackgroundColor(getResources().getColor(R.color.odpowiedz_button)); //odznacz ją
                        Log.d("WYPELNIANIE_ANKIETY", "Odznaczam odpowiedz jednokrotnego wyboru");
                        chosenAnswer = null;
                    } else {
                        if (chosenAnswer != null) {     //jeżeli jakaś odpowiedź już jest zaznaczona,
                            for (Button butt : answers) {        //odznacz ją
                                butt.setBackgroundColor(getResources().getColor(R.color.odpowiedz_button));
                            }
                        }
                        v.setBackgroundColor(getResources().getColor(R.color.chosen_answer_button)); //zaznacz wybraną odpowiedź
                        chosenAnswer = (Button) v;
                    }
                }
            });
            answers.add(button);
            answersLinear.addView(button);
        }

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
                            Intent intent = new Intent(AnswerOneChoiceQuestionActivity.this, SurveysSummary.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Nie można zakończyć ankiety", Toast.LENGTH_SHORT);
                        }
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
                            Toast.makeText(getApplicationContext(), "Nie można zakończyć ankiety", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void goToNextActivity() {
        AnsweringSurveyControl control = ApplicationState.
                getInstance(AnswerOneChoiceQuestionActivity.this).getAnsweringSurveyControl();
        if(control.getNumberOfQuestions() - 1 > myQuestionNumber){
            Question question = control.getQuestion(myQuestionNumber + 1);
            int questionType = question.getQuestionType();
            Intent intent;
            if(questionType == Question.ONE_CHOICE_QUESTION){
                intent = new Intent(AnswerOneChoiceQuestionActivity.this,
                        AnswerOneChoiceQuestionActivity.class);
            }
            else if(questionType == Question.MULTIPLE_CHOICE_QUESTION){
                intent = new Intent(AnswerOneChoiceQuestionActivity.this,
                        AnswerMultipleChoiceQuestionActivity.class);
            }
            else if(questionType == Question.DROP_DOWN_QUESTION){
                intent = new Intent(AnswerOneChoiceQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
            }
            else if(questionType == Question.SCALE_QUESTION){
                intent = new Intent(AnswerOneChoiceQuestionActivity.this, AnswerScaleQuestionActivity.class);
            }
            else if(questionType == Question.DATE_QUESTION){
                intent = new Intent(AnswerOneChoiceQuestionActivity.this, AnswerDateQuestionActivity.class);
            }
            else if(questionType == Question.TIME_QUESTION){
                intent = new Intent(AnswerOneChoiceQuestionActivity.this, AnswerTimeQuestionActivity.class);
            }
            else if(questionType == Question.GRID_QUESTION){
                intent = new Intent(AnswerOneChoiceQuestionActivity.this, AnswerGridQuestionActivity.class);
            }
            else if(questionType == Question.TEXT_QUESTION){
                intent = new Intent(AnswerOneChoiceQuestionActivity.this, AnswerTextQuestionActivity.class);
            }
            else{
                intent = new Intent(AnswerOneChoiceQuestionActivity.this, SurveysSummary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
            startActivity(intent);
        }
    }

    private boolean setUserAnswer(){
        AnsweringSurveyControl control = ApplicationState.
                getInstance(AnswerOneChoiceQuestionActivity.this).getAnsweringSurveyControl();
        if(question.isObligatory()){
            if(chosenAnswer == null){     //jeśli pytanie jest obowiązkowe i nic nie dodano
                Toast.makeText(AnswerOneChoiceQuestionActivity.this,
                        "To pytanie jest obowiązkowe, podaj odpowiedź!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(chosenAnswer != null){
            if(control.setOneChoiceQuestionAnswer(myQuestionNumber, chosenAnswer.getText().toString())) {
                Log.d("ANSWER", chosenAnswer.getText().toString());
                return true;
            }
            else{
                Toast.makeText(AnswerOneChoiceQuestionActivity.this,
                        "Coś poszło nie tak, nie dodano odpowiedzi.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
