package bohonos.demski.mieldzioc.fillingSurvey;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.questions.Question;

public class WelcomeFillingActivity extends ActionBarActivity {

    private String summary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_filling);

        String title = getIntent().getStringExtra("SURVEY_TITLE");
        String description = getIntent().getStringExtra("SURVEY_DESCRIPTION");
        summary = getIntent().getStringExtra("SURVEY_SUMMARY");

        TextView titleTxt = (TextView) findViewById(R.id.welcome_survey_title);
        TextView descriptionTxt = (TextView) findViewById(R.id.welcome_survey_description);

        titleTxt.setText(title);
        descriptionTxt.setText(description);

        ImageButton nextButton = (ImageButton) findViewById(R.id.welcome_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnsweringSurveyControl control = ApplicationState.
                        getInstance(WelcomeFillingActivity.this).getAnsweringSurveyControl();
                if(control.getNumberOfQuestions() > 0){
                    Question question = control.getQuestion(0);
                    int questionType = question.getQuestionType();
                    Intent intent;
                    if(questionType == Question.ONE_CHOICE_QUESTION){
                        intent = new Intent(WelcomeFillingActivity.this,
                                AnswerOneChoiceQuestionActivity.class);
                    }
                    else if(questionType == Question.MULTIPLE_CHOICE_QUESTION){
                        intent = new Intent(WelcomeFillingActivity.this,
                                AnswerMultipleChoiceQuestionActivity.class);
                    }
                    else if(questionType == Question.DROP_DOWN_QUESTION){
                        intent = new Intent(WelcomeFillingActivity.this, AnswerDropDownListQuestionActivity.class);
                    }
                    else if(questionType == Question.SCALE_QUESTION){
                        intent = new Intent(WelcomeFillingActivity.this, AnswerScaleQuestionActivity.class);
                    }
                    else if(questionType == Question.DATE_QUESTION){
                        intent = new Intent(WelcomeFillingActivity.this, AnswerDateQuestionActivity.class);
                    }
                    else if(questionType == Question.TIME_QUESTION){
                        intent = new Intent(WelcomeFillingActivity.this, AnswerTimeQuestionActivity.class);
                    }
                    else if(questionType == Question.GRID_QUESTION){
                        intent = new Intent(WelcomeFillingActivity.this, AnswerGridQuestionActivity.class);
                    }
                    else if(questionType == Question.TEXT_QUESTION){
                        intent = new Intent(WelcomeFillingActivity.this, AnswerTextQuestionActivity.class);
                    }
                    else intent = new Intent(WelcomeFillingActivity.this, SurveysSummary.class);
                    intent.putExtra("QUESTION_NUMBER", 0);
                    intent.putExtra("SURVEY_SUMMARY", summary);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(WelcomeFillingActivity.this,
                            "Ankieta nie zawiera żadnych pytań", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_filling, menu);
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
