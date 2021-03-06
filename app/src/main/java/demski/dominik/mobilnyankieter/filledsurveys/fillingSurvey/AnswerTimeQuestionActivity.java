package demski.dominik.mobilnyankieter.filledsurveys.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import demski.dominik.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import demski.dominik.mobilnyankieter.application.ApplicationState;
import demski.dominik.mobilnyankieter.application.UserPreferences;
import demski.dominik.mobilnyankieter.myControls.MyTwoDigitFormater;

public class AnswerTimeQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private NumberPicker hour;
    private NumberPicker minute;
    private int myQuestionNumber;

    private CheckBox isNoAnsweredCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_time_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(answeringSurveyControl.getSurveysTitle());

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);

        question =  answeringSurveyControl.getQuestion(myQuestionNumber);

        prepareQuestionView();

        Integer hourAns = null;
        Integer minuteAns = null;
        boolean noAnsChecked = false;

        if(savedInstanceState != null){
            hourAns = savedInstanceState.getInt("HOUR");
            minuteAns = savedInstanceState.getInt("MINUTE");
            noAnsChecked = savedInstanceState.getBoolean("NO_ANSWER");
        }

        prepareAnswerView(hourAns, minuteAns, noAnsChecked);

        prepareNextAndFinishButton();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("HOUR", hour.getValue());
        outState.putInt("MINUTE", minute.getValue());
        outState.putBoolean("NO_ANSWER", isNoAnsweredCheckBox.isChecked());
    }

    private void prepareQuestionView() {
        if (!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_time);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_time);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_time);
        questionHint.setText(question.getHint());
    }

    private void prepareAnswerView(Integer hourAns, Integer minuteAns, boolean noAnsChecked) {
        hour = (NumberPicker) findViewById(R.id.answer_time_number_picker_hour);
        minute = (NumberPicker) findViewById(R.id.answer_time_number_picker_minute);

        hour.setMaxValue(23);
        hour.setMinValue(0);
        hour.setFormatter(new MyTwoDigitFormater());
        hour.setBackgroundColor(getResources().getColor(R.color.pomaranczowy));

        minute.setMinValue(0);
        minute.setMaxValue(59);
        minute.setFormatter(new MyTwoDigitFormater());
        minute.setBackgroundColor(getResources().getColor(R.color.pomaranczowy));

        if(hourAns != null){
            hour.setValue(hourAns);
        }

        if(minuteAns != null){
            minute.setValue(minuteAns);
        }

        isNoAnsweredCheckBox = (CheckBox) findViewById(R.id.no_answ_chcBox_time);
        if(question.isObligatory()){
            isNoAnsweredCheckBox.setVisibility(View.INVISIBLE);
        }
        else{
            isNoAnsweredCheckBox.setChecked(noAnsChecked);
        }
    }

    private void prepareNextAndFinishButton() {
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
                            Intent intent = new Intent(AnswerTimeQuestionActivity.this, SurveysSummary.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
                            startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(getApplicationContext(), "Nie można zakończyć ankiety", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "Nie można zakończyć ankiety", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean setUserAnswer(){
        AnsweringSurveyControl control = ApplicationState.
                getInstance(AnswerTimeQuestionActivity.this).getAnsweringSurveyControl();

        if(!isNoAnsweredCheckBox.isChecked()) {
            if (control.setDateAndTimeQuestionAnswer(myQuestionNumber, 1, 1, 1970, //dodano odpowiedź
                    hour.getValue(), minute.getValue(), 0)) {
                return true;
            } else {                           //nie powinno się zdarzyć
                Toast.makeText(AnswerTimeQuestionActivity.this,
                        "Podana odpowiedź zawiera błędy.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else{
            return true;
        }
    }

    public void goToNextActivity(){
        AnsweringSurveyControl control = answeringSurveyControl;
        if (control.getNumberOfQuestions() - 1 > myQuestionNumber) {
            Question question = control.getQuestion(myQuestionNumber + 1);
            int questionType = question.getQuestionType();
            Intent intent;
            if (questionType == Question.ONE_CHOICE_QUESTION) {
                intent = new Intent(AnswerTimeQuestionActivity.this,
                        AnswerOneChoiceQuestionActivity.class);
            } else if (questionType == Question.MULTIPLE_CHOICE_QUESTION) {
                intent = new Intent(AnswerTimeQuestionActivity.this,
                        AnswerMultipleChoiceQuestionActivity.class);
            } else if (questionType == Question.DROP_DOWN_QUESTION) {
                intent = new Intent(AnswerTimeQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
            } else if(questionType == Question.SCALE_QUESTION){
                intent = new Intent(AnswerTimeQuestionActivity.this, AnswerScaleQuestionActivity.class);
            }
            else if(questionType == Question.DATE_QUESTION){
                intent = new Intent(AnswerTimeQuestionActivity.this, AnswerDateQuestionActivity.class);
            }
            else if(questionType == Question.TIME_QUESTION){
                intent = new Intent(AnswerTimeQuestionActivity.this, AnswerTimeQuestionActivity.class);
            }
            else if(questionType == Question.GRID_QUESTION){
                intent = new Intent(AnswerTimeQuestionActivity.this, AnswerGridQuestionActivity.class);
            }
            else if(questionType == Question.TEXT_QUESTION){
                intent = new Intent(AnswerTimeQuestionActivity.this, AnswerTextQuestionActivity.class);
            }
            else{
                intent = new Intent(AnswerTimeQuestionActivity.this, SurveysSummary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
            startActivity(intent);
        }
    }
}
