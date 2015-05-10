package bohonos.demski.mieldzioc.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.application.DateAndTimeService;
import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.myControls.MyTwoDigitFormater;
import bohonos.demski.mieldzioc.questions.Question;

public class AnswerTimeQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private NumberPicker hour;
    private NumberPicker minute;
    private int myQuestionNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_time_question);

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        Log.d("WYPELNIANIE_ANKIETY", "Time - otrzymalem pytanie nr: " + myQuestionNumber);
        question =  answeringSurveyControl.getQuestion(myQuestionNumber);

        if (!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_time);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_time);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_time);
        questionHint.setText(question.getHint());

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

        ImageButton nextButton = (ImageButton) findViewById(R.id.next_question_button);
        Button finishButton = (Button) findViewById(R.id.end_filling_button);
        if(answeringSurveyControl.getNumberOfQuestions() - 1 > myQuestionNumber) {  //jeœli to nie jest ostatnie pytanie
            finishButton.setVisibility(View.INVISIBLE);
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
                    if (setUserAnswer()){
                        if(answeringSurveyControl.finishAnswering(ApplicationState.
                                getInstance(getApplicationContext()).getSurveysRepository())){
                            Intent intent = new Intent(AnswerTimeQuestionActivity.this, SurveysSummary.class);
                            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
                            startActivity(intent);
                            finish();
                        }
                        else Toast.makeText(getApplicationContext(), "Nie mo¿na zakoñczyæ ankiety", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private boolean setUserAnswer(){
        AnsweringSurveyControl control = ApplicationState.
                getInstance(AnswerTimeQuestionActivity.this).getAnsweringSurveyControl();
       /* if(question.isObligatory()) {       //jest obowiazkowe
            if (answer.trim().equals("")) {      //i nie ma odpowiedzi
                answerTxt.setError("Pytanie jest obowi¹zkowe. Proszê podaæ odpowiedŸ.");
                return false;
            }
        }
        if(!answer.trim().equals("")){    //jest odpowiedz (nie wa¿ne, czy jest obowi¹zkowe
         */
           // else{                                                          //je¿eli mo¿na zrobiæ z niej datê

                if(control.setDateAndTimeQuestionAnswer(myQuestionNumber, 1, 1, 1970, //dodano odpowiedŸ
                        hour.getValue(), minute.getValue(), 0)){
                    return true;
                }
                else{                           //nie powinno siê zdarzyæ
                    Toast.makeText(AnswerTimeQuestionActivity.this,
                            "Podana odpowiedŸ zawiera b³êdy.", Toast.LENGTH_SHORT).show();
                    return false;
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
            else intent = new Intent(AnswerTimeQuestionActivity.this, SurveysSummary.class);
            intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
            startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_time_question, menu);
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
