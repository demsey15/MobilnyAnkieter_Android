package demski.dominik.mobilnyankieter.surveytemplates.creatingandeditingsurvey;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import bohonos.demski.mieldzioc.mobilnyankieter.controls.CreatingSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import demski.dominik.mobilnyankieter.R;


public class EditChoiceQuestion extends ActionBarActivity {
    private Question question;
    private int questionNumber;
    private EditChoiceAnswersFragment fragment;

    private int fragmentId;

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

        addAnswersFragment(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("FRAGMENT_ID", fragment.getId());
    }

    private void prepareView(){
        final EditText titleTxt = (EditText) findViewById(R.id.question_choice_text);
        final EditText hintTxt = (EditText) findViewById(R.id.hint_choice_text);
        final CheckBox obligatory = (CheckBox) findViewById(R.id.obligatory_choice_checkbox);
        Button addButton = (Button) findViewById(R.id.save_choice_question_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatingSurveyControl control = CreatingSurveyControl.getInstance();
                control.setQuestionText(questionNumber, titleTxt.getText().toString());
                control.setQuestionHint(questionNumber, hintTxt.getText().toString());
                control.setQuestionObligatory(questionNumber, obligatory.isChecked());

                List<String> answers = fragment.getAnswers();

                control.resetAnswersInChooseQuestion(questionNumber, answers);

                setResult(RESULT_OK);
                finish();
            }
        });

        if(question.getQuestion() != null) titleTxt.setText(question.getQuestion());
        if(question.getHint() != null) hintTxt.setText(question.getHint());
        obligatory.setChecked(question.isObligatory());
    }

    private void addAnswersFragment(Bundle savedInstanceState){
        if(savedInstanceState != null){
            fragmentId = savedInstanceState.getInt("FRAGMENT_ID");

            FragmentManager fragmentManager = getFragmentManager();

            fragment = (EditChoiceAnswersFragment) fragmentManager.findFragmentById(fragmentId);
        }
        else{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragment = EditChoiceAnswersFragment.newInstance((ArrayList<String>) CreatingSurveyControl.
                    getInstance().getAnswersAsStringList(questionNumber));


            fragmentTransaction.add(R.id.choice_question_linear, fragment);
            fragmentTransaction.commit();

            fragmentId = fragment.getId();
        }
    }
}
