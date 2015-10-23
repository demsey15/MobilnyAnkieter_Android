package bohonos.demski.mieldzioc.mobilnyankieter.creatingandeditingsurvey;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.CreatingSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;


public class EditChoiceQuestion extends ActionBarActivity {
    private Question question;
    private int questionNumber;
    private EditChoiceAnswersFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_choice_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        question = (Question) intent.getSerializableExtra("QUESTION");
        questionNumber = intent.getIntExtra("QUESTION_NUMBER", 0);

        prepareView();
        addAnswersFragment();

    }

    private void prepareView(){
        final EditText titleTxt = (EditText) findViewById(R.id.question_choice_text);
        final EditText hintTxt = (EditText) findViewById(R.id.hint_choice_text);
        final EditText errorTxt = (EditText) findViewById(R.id.error_choice_text);
        final CheckBox obligatory = (CheckBox) findViewById(R.id.obligatory_choice_checkbox);
        Button addButton = (Button) findViewById(R.id.save_choice_question_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatingSurveyControl control = CreatingSurveyControl.getInstance();
                control.setQuestionText(questionNumber, titleTxt.getText().toString());
                control.setQuestionHint(questionNumber, hintTxt.getText().toString());
                control.setQuestionErrorMessage(questionNumber, errorTxt.getText().toString());
                control.setQuestionObligatory(questionNumber, obligatory.isChecked());

                List<String> answers = fragment.getAnswers();

                for(String answer : answers){
                    control.addAnswerToChooseQuestion(questionNumber, answer);
                }
                setResult(RESULT_OK);
                finish();
            }
        });

        if(question.getQuestion() != null) titleTxt.setText(question.getQuestion());
        if(question.getHint() != null) hintTxt.setText(question.getHint());
        if(question.getErrorMessage() != null) errorTxt.setText(question.getErrorMessage());
        obligatory.setChecked(question.isObligatory());
    }
    private void addAnswersFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment = EditChoiceAnswersFragment.newInstance((ArrayList<String>) CreatingSurveyControl.
                getInstance().getAnswersAsStringList(questionNumber));
        fragmentTransaction.add(R.id.choice_question_linear, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_choice_question, menu);
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
