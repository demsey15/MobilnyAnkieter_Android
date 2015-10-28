package bohonos.demski.mieldzioc.mobilnyankieter.surveytemplates.creatingandeditingsurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.CreatingSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;


public class EditDateTimeQuestion extends ActionBarActivity {

    private Question question;
    private int questionNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_date_time_question);
        Intent intent = getIntent();
        question = (Question) intent.getSerializableExtra("QUESTION");
        questionNumber = intent.getIntExtra("QUESTION_NUMBER", 0);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        final EditText titleTxt = (EditText) findViewById(R.id.question_date_time_text);
        final EditText hintTxt = (EditText) findViewById(R.id.hint_date_time_text);
        final CheckBox obligatory = (CheckBox) findViewById(R.id.obligatory_checkbox);
        Button addButton = (Button) findViewById(R.id.add_data_time_question);

        if(question.getQuestion() != null) titleTxt.setText(question.getQuestion());
        if(question.getHint() != null) hintTxt.setText(question.getHint());
        obligatory.setChecked(question.isObligatory());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatingSurveyControl control = CreatingSurveyControl.getInstance();
                control.setQuestionText(questionNumber, titleTxt.getText().toString());
                control.setQuestionHint(questionNumber, hintTxt.getText().toString());
                control.setQuestionObligatory(questionNumber, obligatory.isChecked());
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
