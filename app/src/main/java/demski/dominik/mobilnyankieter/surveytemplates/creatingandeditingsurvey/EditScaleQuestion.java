package demski.dominik.mobilnyankieter.surveytemplates.creatingandeditingsurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import demski.dominik.mobilnyankieter.R;
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
        final EditText fromLabelEdit = (EditText) findViewById(R.id.from_label);
        final EditText toLabelEdit = (EditText) findViewById(R.id.to_label);


        if(question.getQuestion() != null) titleTxt.setText(question.getQuestion());
        if(question.getHint() != null) hintTxt.setText(question.getHint());
        obligatory.setChecked(question.isObligatory());

        ScaleQuestion question2 = (ScaleQuestion) question;

        fromTxt.setText(String.valueOf(question2.getMinValue()) + ":");
        toTxt.setText(String.valueOf(question2.getMaxValue()) + ":");

        prepareFromEditTxt(fromTxt, fromEdit, question2);

        prepareToEditText(toTxt, toEdit, question2);

        String minLabel, maxLabel;

        if((minLabel = question2.getMinLabel())!= null) {
            fromLabelEdit.setText(minLabel);
        }

        if((maxLabel = question2.getMaxLabel())!= null){
            toLabelEdit.setText(maxLabel);
        }


        prepareAddButton(titleTxt, hintTxt, obligatory, addButton, fromEdit, toEdit, fromLabelEdit, toLabelEdit);
    }

    private void prepareToEditText(final TextView toTxt, EditText toEdit, ScaleQuestion question2) {
        toEdit.setText(String.valueOf(question2.getMaxValue()));
        toEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                toTxt.setText(editable.toString() + ":");
            }
        });
    }

    private void prepareFromEditTxt(final TextView fromTxt, EditText fromEdit, ScaleQuestion question2) {
        fromEdit.setText(String.valueOf(question2.getMinValue()));
        fromEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                fromTxt.setText(editable.toString() + ":");
            }
        });
    }

    private void prepareAddButton(final EditText titleTxt, final EditText hintTxt, final CheckBox obligatory, Button addButton, final EditText fromEdit, final EditText toEdit, final EditText fromLabelEdit, final EditText toLabelEdit) {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (fromEdit.getText().toString().equals("") || toEdit.getText().toString().equals("")) {
                        toEdit.setError("Pola skali nie mogą być puste!");
                    } else if (Integer.valueOf(fromEdit.getText().toString()) > Integer.valueOf(toEdit.getText().toString())) {
                        toEdit.setError("Wartość \"do\" nie może być mniejsza od wartości \"od\"!");
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
                    toEdit.setError("Wartości skali muszą być liczbami całkowitymi.");
                }
            }

        });
    }
}
