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

import java.util.ArrayList;
import java.util.List;

import demski.dominik.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.CreatingSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;


public class EditGridQuestion extends ActionBarActivity {

    private Question question;
    private int questionNumber;
    private EditChoiceAnswersFragment rowFragment;
    private EditChoiceAnswersFragment columnFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_grid_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        question = (Question) intent.getSerializableExtra("QUESTION");
        questionNumber = intent.getIntExtra("QUESTION_NUMBER", 0);

        prepareView();
        addAnswersFragment();
    }


    private void prepareView(){
        final EditText titleTxt = (EditText) findViewById(R.id.question_grid_text);
        final EditText hintTxt = (EditText) findViewById(R.id.hint_grid_text);
        final CheckBox obligatory = (CheckBox) findViewById(R.id.obligatory_grid_checkbox);
        Button addButton = (Button) findViewById(R.id.save_grid_question_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatingSurveyControl control = CreatingSurveyControl.getInstance();
                control.setQuestionText(questionNumber, titleTxt.getText().toString());
                control.setQuestionHint(questionNumber, hintTxt.getText().toString());
                control.setQuestionObligatory(questionNumber, obligatory.isChecked());

                List<String> rowLabels = rowFragment.getAnswers();
                List<String> columnLabels = columnFragment.getAnswers();

                control.setGridRowLabels(questionNumber, rowLabels);
                control.setGridColumnLabels(questionNumber, columnLabels);

                setResult(RESULT_OK);
                finish();
            }
        });

        if(question.getQuestion() != null) titleTxt.setText(question.getQuestion());
        if(question.getHint() != null) hintTxt.setText(question.getHint());
        obligatory.setChecked(question.isObligatory());
    }
    private void addAnswersFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        rowFragment = EditChoiceAnswersFragment.newInstance((ArrayList<String>) CreatingSurveyControl.
                getInstance().getGridRowLabels(questionNumber));
        columnFragment = EditChoiceAnswersFragment.newInstance((ArrayList<String>) CreatingSurveyControl.
                getInstance().getGridColumnLabels(questionNumber));
        fragmentTransaction.add(R.id.grid_question_linear_rows, rowFragment);
        fragmentTransaction.add(R.id.grid_question_linear_columns, columnFragment);
        fragmentTransaction.commit();
    }
}
