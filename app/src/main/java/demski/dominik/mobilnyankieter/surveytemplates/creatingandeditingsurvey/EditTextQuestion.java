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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import demski.dominik.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.constraints.IConstraint;
import bohonos.demski.mieldzioc.mobilnyankieter.constraints.NumberConstraint;
import bohonos.demski.mieldzioc.mobilnyankieter.constraints.TextConstraint;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.CreatingSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.TextQuestion;


public class EditTextQuestion extends ActionBarActivity implements TextConstraintsFragment.OnTextFragmentInteractionListener, NumberConstraintsFragment.OnNumberFragmentInteractionListener {

    private Question question;
    private int questionNumber;
    private TextConstraintsFragment textConstraintsFragment;
    private  NumberConstraintsFragment numberConstraintsFragment;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text_question);

        Intent intent = getIntent();
        question = (Question) intent.getSerializableExtra("QUESTION");
        questionNumber = intent.getIntExtra("QUESTION_NUMBER", 0);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        final EditText titleTxt = (EditText) findViewById(R.id.question_text);
        final EditText hintTxt = (EditText) findViewById(R.id.hint_text);
        final CheckBox obligatory = (CheckBox) findViewById(R.id.obligatory_checkbox);
        Button addButton = (Button) findViewById(R.id.save_question_button);

        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.constraints_radio_group);
        final RadioButton textRadio = (RadioButton) findViewById(R.id.text_radio);
        final RadioButton numberRadio = (RadioButton) findViewById(R.id.number_radio);
        final RadioButton nothingRadio = (RadioButton) findViewById(R.id.nothing_radio);

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.constraints_relative);

        if(question.getQuestion() != null){
            titleTxt.setText(question.getQuestion());
        }

        if(question.getHint() != null){
            hintTxt.setText(question.getHint());
        }

        obligatory.setChecked(question.isObligatory());

        textRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    relativeLayout.removeAllViews();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    textConstraintsFragment = new TextConstraintsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("QUESTION", question);
                    textConstraintsFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.constraints_relative, textConstraintsFragment);

                    fragmentTransaction.commit();
                }
            }
        });

        numberRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    relativeLayout.removeAllViews();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    numberConstraintsFragment = new NumberConstraintsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("QUESTION", question);
                    numberConstraintsFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.constraints_relative, numberConstraintsFragment);
                    fragmentTransaction.commit();
                }
                }
        });

        nothingRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    relativeLayout.removeAllViews();
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatingSurveyControl control = CreatingSurveyControl.getInstance();
                control.setQuestionText(questionNumber, titleTxt.getText().toString());
                control.setQuestionHint(questionNumber, hintTxt.getText().toString());
                control.setQuestionObligatory(questionNumber, obligatory.isChecked());

                int checked = radioGroup.getCheckedRadioButtonId();
                if (checked != -1) {
                    if(checked == R.id.text_radio){
                        Integer minLength = textConstraintsFragment.getMinLength();
                        Integer maxLength = textConstraintsFragment.getMaxLength();
                        String regex = textConstraintsFragment.getRegex();
                        if(control.setTextConstraints(questionNumber, minLength, maxLength, regex)){
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                    else if(checked == R.id.number_radio){
                        Double maxValue = numberConstraintsFragment.getMaxValue();
                        Double minValue = numberConstraintsFragment.getMinValue();

                        Double notEquals = numberConstraintsFragment.getNotEquals();
                        Boolean notBetween = numberConstraintsFragment.isNotBetween();
                        Boolean mustBeInteger = numberConstraintsFragment.isMustBeInteger();
                        if(control.setNumberConstraints(questionNumber, minValue, maxValue,
                                mustBeInteger, notEquals, notBetween)){
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                    else {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
                else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
                });

        TextQuestion txtQuestion = (TextQuestion) question;
        IConstraint constraint = txtQuestion.getConstraint();
        if(constraint != null) {
            if(constraint instanceof TextConstraint){
                textRadio.setChecked(true);
            }
            else if (constraint instanceof NumberConstraint){
                numberRadio.setChecked(true);
            }
        }
        else{
            nothingRadio.setChecked(true);
        }
    }

    @Override
    public void onTextFragmentInteraction(int minLength, int maxLength, String regex) {

    }

    @Override
    public void onNumberFragmentInteraction(double minValue, double maxValue, boolean mustBeInteger, double notEquals, boolean notBetweenMaxAndMinValue) {

    }
}
