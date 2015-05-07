package bohonos.demski.mieldzioc.fillingSurvey;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.questions.Question;

public class AnswerDropDownListQuestionActivity extends ActionBarActivity {

    AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private int myQuestionNumber;
    private Spinner answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_drop_down_list_question);

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        question = answeringSurveyControl.getQuestion(myQuestionNumber);

        if(!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_drop_down);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_drop_down);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_drop_down);
        questionHint.setText(question.getHint());

        //wstawianie odpowiedzi
        answers = (Spinner) findViewById(R.id.spinner_answer_drop_down);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        List<String> answerList = question.getAnswersAsStringList();
        for(String ans : answerList){
            adapter.add(ans);
        }

        answers.setAdapter(adapter);

        ImageButton nextButton = (ImageButton) findViewById(R.id.next_question_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnsweringSurveyControl control = ApplicationState.
                        getInstance(AnswerDropDownListQuestionActivity.this).getAnsweringSurveyControl();
               // control.setOneChoiceQuestionAnswer(myQuestionNumber, getChosenAnswer());
                if (control.getNumberOfQuestions() - 1 > myQuestionNumber) {
                    Question question = control.getQuestion(myQuestionNumber + 1);
                    int questionType = question.getQuestionType();
                    Intent intent;
                    if (questionType == Question.ONE_CHOICE_QUESTION) {
                        intent = new Intent(AnswerDropDownListQuestionActivity.this,
                                AnswerOneChoiceQuestionActivity.class);
                    } else if (questionType == Question.MULTIPLE_CHOICE_QUESTION) {
                        intent = new Intent(AnswerDropDownListQuestionActivity.this,
                                AnswerMultipleChoiceQuestionActivity.class);
                    } else if (questionType == Question.DROP_DOWN_QUESTION) {
                        intent = new Intent(AnswerDropDownListQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
                    }else if(questionType == Question.SCALE_QUESTION){
                        intent = new Intent(AnswerDropDownListQuestionActivity.this, AnswerScaleQuestionActivity.class);
                    }
                    else if(questionType == Question.DATE_QUESTION){
                        intent = new Intent(AnswerDropDownListQuestionActivity.this, AnswerDateQuestionActivity.class);
                    }
                    else if(questionType == Question.TIME_QUESTION){
                        intent = new Intent(AnswerDropDownListQuestionActivity.this, AnswerTimeQuestionActivity.class);
                    }
                    else if(questionType == Question.GRID_QUESTION){
                        intent = new Intent(AnswerDropDownListQuestionActivity.this, AnswerGridQuestionActivity.class);
                    }
                    else if(questionType == Question.TEXT_QUESTION){
                        intent = new Intent(AnswerDropDownListQuestionActivity.this, AnswerShortTextQuestionActivity.class);
                    }
                    else intent = new Intent(AnswerDropDownListQuestionActivity.this, AnswerLongTextQuestionActivity.class);
                    intent.putExtra("QUESTION_NUMBER", 0);
                    intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
                    startActivity(intent);
                } else {
                    Toast.makeText(AnswerDropDownListQuestionActivity.this,
                            "Ju¿ wiêcej pytañ nie ma.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private String getChosenAnswer(){
        return (String) answers.getSelectedItem();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_drop_down_list_question, menu);
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
