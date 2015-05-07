package bohonos.demski.mieldzioc.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.questions.Question;

public class AnswerDateQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private EditText chosenAnswer;
    private int myQuestionNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_date_question);

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        Log.d("WYPELNIANIE_ANKIETY", "Date - otrzymalem pytanie nr: " + myQuestionNumber);
        question =  answeringSurveyControl.getQuestion(myQuestionNumber);

        if (!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_date);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_date);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_date);
        questionHint.setText(question.getHint());

        chosenAnswer = (EditText) findViewById(R.id.answer_txt_date);

        ImageButton nextButton = (ImageButton) findViewById(R.id.next_question_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnsweringSurveyControl control = ApplicationState.
                        getInstance(AnswerDateQuestionActivity.this).getAnsweringSurveyControl();
                //  control.setScaleQuestionAnswer(myQuestionNumber, getChosenAnswer());
                if (control.getNumberOfQuestions() - 1 > myQuestionNumber) {
                    Question question = control.getQuestion(myQuestionNumber + 1);
                    int questionType = question.getQuestionType();
                    Intent intent;
                    if (questionType == Question.ONE_CHOICE_QUESTION) {
                        intent = new Intent(AnswerDateQuestionActivity.this,
                                AnswerOneChoiceQuestionActivity.class);
                    } else if (questionType == Question.MULTIPLE_CHOICE_QUESTION) {
                        intent = new Intent(AnswerDateQuestionActivity.this,
                                AnswerMultipleChoiceQuestionActivity.class);
                    } else if (questionType == Question.DROP_DOWN_QUESTION) {
                        intent = new Intent(AnswerDateQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
                    } else if(questionType == Question.SCALE_QUESTION){
                        intent = new Intent(AnswerDateQuestionActivity.this, AnswerScaleQuestionActivity.class);
                    }
                    else if(questionType == Question.DATE_QUESTION){
                        intent = new Intent(AnswerDateQuestionActivity.this, AnswerDateQuestionActivity.class);
                    }
                    else if(questionType == Question.TIME_QUESTION){
                        intent = new Intent(AnswerDateQuestionActivity.this, AnswerTimeQuestionActivity.class);
                    }
                    else if(questionType == Question.GRID_QUESTION){
                        intent = new Intent(AnswerDateQuestionActivity.this, AnswerGridQuestionActivity.class);
                    }
                    else if(questionType == Question.TEXT_QUESTION){
                        intent = new Intent(AnswerDateQuestionActivity.this, AnswerShortTextQuestionActivity.class);
                    }
                    else intent = new Intent(AnswerDateQuestionActivity.this, AnswerLongTextQuestionActivity.class);
                    intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
                    intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
                    startActivity(intent);
                } else {
                    Toast.makeText(AnswerDateQuestionActivity.this,
                            "Ju¿ wiêcej pytañ nie ma.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private String getChosenAnswer(){
        return chosenAnswer.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_date_question, menu);
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
