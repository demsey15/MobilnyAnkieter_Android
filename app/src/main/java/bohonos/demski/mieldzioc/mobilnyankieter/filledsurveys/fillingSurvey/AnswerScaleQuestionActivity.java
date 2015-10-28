package bohonos.demski.mieldzioc.mobilnyankieter.filledsurveys.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.ScaleQuestion;

public class AnswerScaleQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private ScaleQuestion question;
    private SeekBar chosenAnswer;
    private int myQuestionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_scale_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(answeringSurveyControl.getSurveysTitle());

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        Log.d("WYPELNIANIE_ANKIETY", "Scale - otrzymalem pytanie nr: " + myQuestionNumber);
        question = (ScaleQuestion) answeringSurveyControl.getQuestion(myQuestionNumber);

        if (!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_scale);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_scale);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_scale);
        questionHint.setText(question.getHint());

        chosenAnswer = (SeekBar) findViewById(R.id.answer_scale_seekBar);
        chosenAnswer.setMax(question.getMaxValue());

        TextView leftLabel = (TextView) findViewById(R.id.leftLabel_answer_scale_question);
        TextView rightLabel = (TextView) findViewById(R.id.rightLabel_answer_scale_question);

        leftLabel.setText(question.getMinLabel());
        rightLabel.setText(question.getMaxLabel());

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
                            Intent intent = new Intent(AnswerScaleQuestionActivity.this, SurveysSummary.class);
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
                                    ApplicationState.getInstance(getApplicationContext()).getDeviceId());
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
                getInstance(AnswerScaleQuestionActivity.this).getAnsweringSurveyControl();
        if (control.getNumberOfQuestions() - 1 > myQuestionNumber) {
            Question question = control.getQuestion(myQuestionNumber + 1);
            int questionType = question.getQuestionType();
            Intent intent;
            if (questionType == Question.ONE_CHOICE_QUESTION) {
                intent = new Intent(AnswerScaleQuestionActivity.this,
                        AnswerOneChoiceQuestionActivity.class);
            } else if (questionType == Question.MULTIPLE_CHOICE_QUESTION) {
                intent = new Intent(AnswerScaleQuestionActivity.this,
                        AnswerMultipleChoiceQuestionActivity.class);
            } else if (questionType == Question.DROP_DOWN_QUESTION) {
                intent = new Intent(AnswerScaleQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
            }else if(questionType == Question.SCALE_QUESTION){
                intent = new Intent(AnswerScaleQuestionActivity.this, AnswerScaleQuestionActivity.class);
            }
            else if(questionType == Question.DATE_QUESTION){
                intent = new Intent(AnswerScaleQuestionActivity.this, AnswerDateQuestionActivity.class);
            }
            else if(questionType == Question.TIME_QUESTION){
                intent = new Intent(AnswerScaleQuestionActivity.this, AnswerTimeQuestionActivity.class);
            }
            else if(questionType == Question.GRID_QUESTION){
                intent = new Intent(AnswerScaleQuestionActivity.this, AnswerGridQuestionActivity.class);
            }
            else if(questionType == Question.TEXT_QUESTION){
                intent = new Intent(AnswerScaleQuestionActivity.this, AnswerTextQuestionActivity.class);
            }
            else{
                intent = new Intent(AnswerScaleQuestionActivity.this, SurveysSummary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
            startActivity(intent);
        }
    }
    private boolean setUserAnswer(){
        AnsweringSurveyControl control = ApplicationState.
                getInstance(AnswerScaleQuestionActivity.this).getAnsweringSurveyControl();
       /* if(question.isObligatory()){
            if(chosenAnswer != null){     //jeśli pytanie jest obowiązkowe i nic nie dodano
                Toast.makeText(AnswerOneChoiceQuestionActivity.this,
                        "To pytanie jest obowiązkowe, podaj odpowiedź!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        */
        //if(chosenAnswer != null){
            if(control.setScaleQuestionAnswer(myQuestionNumber, chosenAnswer.getProgress()))
                return true;
            else{
                Toast.makeText(AnswerScaleQuestionActivity.this,
                        "Coś poszło nie tak, nie dodano odpowiedzi.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
}
