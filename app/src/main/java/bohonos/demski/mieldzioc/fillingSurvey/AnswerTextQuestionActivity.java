package bohonos.demski.mieldzioc.fillingSurvey;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.constraints.IConstraint;
import bohonos.demski.mieldzioc.constraints.NumberConstraint;
import bohonos.demski.mieldzioc.constraints.TextConstraint;
import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.questions.Question;
import bohonos.demski.mieldzioc.questions.TextQuestion;

public class AnswerTextQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private EditText answer;
    private int myQuestionNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_text_question);

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        question = answeringSurveyControl.getQuestion(myQuestionNumber);
        Log.d("WYPELNIANIE_ANKIETY", AnswerTextQuestionActivity.class + " nr pytania: " +
                myQuestionNumber);
        if(!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_short_text);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_short_text);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_short_text);
        questionHint.setText(question.getHint());

        answer = (EditText) findViewById(R.id.answer_answer_short_text);
        int maxLength = -1;
        TextQuestion txtQuestion = (TextQuestion) question;
        IConstraint constraint = txtQuestion.getConstraint();
        if(constraint != null) {
            if (constraint instanceof NumberConstraint) {
                NumberConstraint numberConstraint = (NumberConstraint) constraint;
                if(numberConstraint.isMustBeInteger()){
                    answer.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                else answer.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            else if (constraint instanceof TextConstraint) {
                TextConstraint textConstraint = (TextConstraint) constraint;
                if (textConstraint.getMaxLength() != null)
                    maxLength = textConstraint.getMaxLength();
            }
        }
        if(maxLength == -1) maxLength = TextQuestion.SHORT_ANSWER_MAX_LENGTH;
        answer.setFilters(new InputFilter[]{new InputFilter.LengthFilter
                (maxLength)});

        ImageButton nextButton = (ImageButton) findViewById(R.id.next_question_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnsweringSurveyControl control = ApplicationState.
                        getInstance(AnswerTextQuestionActivity.this).getAnsweringSurveyControl();
//                control.setOneChoiceQuestionAnswer(myQuestionNumber, getChosenAnswer());
                if(control.getNumberOfQuestions() - 1 > myQuestionNumber){
                    Question question = control.getQuestion(myQuestionNumber + 1);
                    int questionType = question.getQuestionType();
                    Intent intent;
                    if(questionType == Question.ONE_CHOICE_QUESTION){
                        intent = new Intent(AnswerTextQuestionActivity.this,
                                AnswerOneChoiceQuestionActivity.class);
                    }
                    else if(questionType == Question.MULTIPLE_CHOICE_QUESTION){
                        intent = new Intent(AnswerTextQuestionActivity.this,
                                AnswerMultipleChoiceQuestionActivity.class);
                    }
                    else if(questionType == Question.DROP_DOWN_QUESTION){
                        intent = new Intent(AnswerTextQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
                    }
                    else if(questionType == Question.SCALE_QUESTION){
                        intent = new Intent(AnswerTextQuestionActivity.this, AnswerScaleQuestionActivity.class);
                    }
                    else if(questionType == Question.DATE_QUESTION){
                        intent = new Intent(AnswerTextQuestionActivity.this, AnswerDateQuestionActivity.class);
                    }
                    else if(questionType == Question.TIME_QUESTION){
                        intent = new Intent(AnswerTextQuestionActivity.this, AnswerTimeQuestionActivity.class);
                    }
                    else if(questionType == Question.GRID_QUESTION){
                        intent = new Intent(AnswerTextQuestionActivity.this, AnswerGridQuestionActivity.class);
                    }
                    else if(questionType == Question.TEXT_QUESTION){
                        intent = new Intent(AnswerTextQuestionActivity.this, AnswerTextQuestionActivity.class);
                    }
                    else intent = new Intent(AnswerTextQuestionActivity.this, SurveysSummary.class);
                    intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
                    intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
                    startActivity(intent);
                }
                else{
                    Toast.makeText(AnswerTextQuestionActivity.this,
                            "Ju¿ wiêcej pytañ nie ma.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_short_text_question, menu);
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
