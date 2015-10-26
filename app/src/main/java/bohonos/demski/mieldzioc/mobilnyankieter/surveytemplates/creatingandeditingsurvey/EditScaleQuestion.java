package bohonos.demski.mieldzioc.mobilnyankieter.surveytemplates.creatingandeditingsurvey;

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
import android.widget.TextView;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.CreatingSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.ScaleQuestion;


public class EditScaleQuestion extends ActionBarActivity {

    private Question question;
    private int questionNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_scale_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        question = (Question) intent.getSerializableExtra("QUESTION");
        questionNumber = intent.getIntExtra("QUESTION_NUMBER", 0);

        final EditText titleTxt = (EditText) findViewById(R.id.question_scale_text);
        final EditText hintTxt = (EditText) findViewById(R.id.hint_scale_text);
        final CheckBox obligatory = (CheckBox) findViewById(R.id.obligatory_checkbox);
        Button addButton = (Button) findViewById(R.id.save_question);

        final TextView fromTxt = (TextView) findViewById(R.id.label_label_from);
        final TextView toTxt = (TextView) findViewById(R.id.label_label_to);
        final EditText fromEdit = (EditText) findViewById(R.id.from_scale);
        final EditText toEdit = (EditText) findViewById(R.id.to_scale);
        final TextView errorScaleTxt = (TextView) findViewById(R.id.bad_scale_error);
        final EditText fromLabelEdit = (EditText) findViewById(R.id.from_label);
        final EditText toLabelEdit = (EditText) findViewById(R.id.to_label);


        if(question.getQuestion() != null) titleTxt.setText(question.getQuestion());
        if(question.getHint() != null) hintTxt.setText(question.getHint());
        obligatory.setChecked(question.isObligatory());


        ScaleQuestion question2 = (ScaleQuestion) question;
        fromTxt.setText(String.valueOf(question2.getMinValue()) + ":");
        toTxt.setText(String.valueOf(question2.getMaxValue()) + ":");
        fromEdit.setText(String.valueOf(question2.getMinValue()));
        toEdit.setText(String.valueOf(question2.getMaxValue()));
        String minLabel, maxLabel;
        if((minLabel = question2.getMinLabel())!= null) fromLabelEdit.setText(minLabel);
        if((maxLabel = question2.getMaxLabel())!= null) toLabelEdit.setText(maxLabel);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (fromEdit.getText().toString().equals("") || toEdit.getText().toString().equals("")) {
                        errorScaleTxt.setText("Pola skali nie mogą być puste!");
                    } else if (Integer.valueOf(fromEdit.getText().toString()) > Integer.valueOf(toEdit.getText().toString())) {
                        errorScaleTxt.setText("Maksymalna skala nie może być mniejsza od minimalnej!");
                    } else {
                        CreatingSurveyControl control = CreatingSurveyControl.getInstance();
                        control.setQuestionText(questionNumber, titleTxt.getText().toString());
                        control.setQuestionHint(questionNumber, hintTxt.getText().toString());
                        control.setQuestionObligatory(questionNumber, obligatory.isChecked());

                        control.setScaleQuestionMaxValue(questionNumber, Integer.valueOf(toEdit.getText().toString()));
                        control.setScaleQuestionMinValue(questionNumber, Integer.valueOf(fromEdit.getText().toString()));
                        String from, to;
                        if (!(from = fromLabelEdit.getText().toString()).equals(""))
                            control.setScaleQuestionMinLabel(questionNumber, from);
                        if (!(to = toLabelEdit.getText().toString()).equals(""))
                            control.setScaleQuestionMaxLabel(questionNumber, to);
                        setResult(RESULT_OK);
                        finish();
                    }
                }
                catch(NumberFormatException e){
                    errorScaleTxt.setText("Wartości skali muszą być liczbami całkowitymi.");
                }
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_scale_question, menu);
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
