package bohonos.demski.mieldzioc.fillingSurvey;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.GenerateId;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.questions.Question;

public class AnswerOneChoiceQuestionActivity extends ActionBarActivity {

    AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private List<Button> answers = new ArrayList<>();
    private Button chosenAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_one_choice_question);

        int myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        question = answeringSurveyControl.getQuestion(myQuestionNumber);

        if(!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_one_choice);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_one_choice);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_one_choice);
        questionHint.setText(question.getHint());

        List<String> answerList = question.getAnswersAsStringList();

        for(String ans : answerList){
            Button button = new Button(this);
            button.setText(ans);
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setId(GenerateId.generateViewId());
            button.setBackgroundColor(getResources().getColor(R.color.pomaranczowy));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  //po klikniêciu zmieñ kolor odpowiedzi na czarny (
                    // resztê na ponarañczowy)
                    if (chosenAnswer != null && chosenAnswer.equals(v)) { //jeœli klikniêto na zaznaczon¹ ju¿ odpowiedŸ
                        v.setBackgroundColor(getResources().getColor(R.color.pomaranczowy)); //odznacz j¹
                        chosenAnswer = null;
                    }
                    if (chosenAnswer != null) {     //je¿eli jakaœ odpowiedŸ ju¿ jest zaznaczona,
                        for (Button butt : answers) {        //odznacz j¹
                            butt.setBackgroundColor(getResources().getColor(R.color.pomaranczowy));
                        }
                    }
                    v.setBackgroundColor(getResources().getColor(R.color.black)); //zaznacz wybran¹ odpowiedŸ
                    chosenAnswer = (Button) v;
                }
            });
            answers.add(button);
        }
    }

    private String getChosenAnswer(){
        if(chosenAnswer != null) return chosenAnswer.getText().toString(); //jeœli wybrano jak¹œ odpowiedŸ
        else return null;                               //to j¹ zwróæ, a jak nie, to zwróæ null
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_one_choice_question, menu);
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
