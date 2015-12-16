package demski.dominik.mobilnyankieter.filledsurveys.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import demski.dominik.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.ScaleQuestion;
import demski.dominik.mobilnyankieter.application.ApplicationState;
import demski.dominik.mobilnyankieter.application.UserPreferences;

public class AnswerScaleQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private ScaleQuestion question;
    private SeekBar chosenAnswer;
    private int myQuestionNumber;

    private boolean isNoAnswerSet = false;

    private int min;
    private int max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_scale_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(answeringSurveyControl.getSurveysTitle());

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);

        question = (ScaleQuestion) answeringSurveyControl.getQuestion(myQuestionNumber);

        prepareQuestionView();

        Integer selectedAnswer = null;

        if(savedInstanceState != null){
            selectedAnswer = savedInstanceState.getInt("CHOSEN_ANSWER");
            isNoAnswerSet = savedInstanceState.getBoolean("NO_ANSWER_STATE");
        }

        prepareAnswerView(selectedAnswer);

        prepareNextAndFinishButtons();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("CHOSEN_ANSWER", chosenAnswer.getProgress());
        outState.putBoolean("NO_ANSWER_STATE", isNoAnswerSet);
    }

    private void prepareQuestionView() {
        if (!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_scale);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_scale);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_scale);
        questionHint.setText(question.getHint());
    }

    private void prepareAnswerView(Integer selectedAnswer) {
        final TextView chosenAnswerMessage = (TextView) findViewById(R.id.scale_chosen_answer_txt);

        prepareNoAnswerCheckBox(chosenAnswerMessage);

        min = question.getMinValue();
        max = question.getMaxValue();

        chosenAnswer = (SeekBar) findViewById(R.id.answer_scale_seekBar);
        chosenAnswer.setMax(max - min);

        if(selectedAnswer != null){
            chosenAnswer.setProgress(selectedAnswer);
        }

        if(!isNoAnswerSet) {
            chosenAnswerMessage.setText("" + (chosenAnswer.getProgress() + min));
        }

        chosenAnswer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(!isNoAnswerSet) {
                    chosenAnswerMessage.setText("" + (seekBar.getProgress() + min));
                }
            }
        });

        TextView leftLabel = (TextView) findViewById(R.id.leftLabel_answer_scale_question);
        TextView rightLabel = (TextView) findViewById(R.id.rightLabel_answer_scale_question);

        leftLabel.setText(question.getMinLabel());
        rightLabel.setText(question.getMaxLabel());
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

    private void prepareNoAnswerCheckBox(final TextView chosenAnswerMessage) {
        CheckBox noAnswerCheckBox = (CheckBox) findViewById(R.id.no_answ_chcBox_scale);

        if(question.isObligatory()){
            noAnswerCheckBox.setVisibility(View.INVISIBLE);
        }else{
            noAnswerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    isNoAnswerSet = b;

                    if(isNoAnswerSet){
                        chosenAnswerMessage.setText("brak");
                    }
                    else{
                        chosenAnswerMessage.setText("" + (chosenAnswer.getProgress() + min));
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

        if(!isNoAnswerSet) {
            if (control.setScaleQuestionAnswer(myQuestionNumber, chosenAnswer.getProgress() + min))
                return true;
            else {
                Toast.makeText(AnswerScaleQuestionActivity.this,
                        "Coś poszło nie tak, nie dodano odpowiedzi.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else{
            return true;
        }
    }
}
