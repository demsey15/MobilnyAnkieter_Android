package bohonos.demski.mieldzioc.fillingSurvey;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.questions.Question;
import bohonos.demski.mieldzioc.questions.ScaleQuestion;

public class AnswerScaleQuestionActivity extends ActionBarActivity {

    AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private ScaleQuestion question;
    private SeekBar chosenAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_scale_question);

        int myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
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
    }

    private int getChosenAnswer(){
        return chosenAnswer.getProgress();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_scale_question, menu);
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
