package bohonos.demski.mieldzioc.mobilnyankieter.filledsurveys.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.constraints.IConstraint;
import bohonos.demski.mieldzioc.mobilnyankieter.constraints.NumberConstraint;
import bohonos.demski.mieldzioc.mobilnyankieter.constraints.TextConstraint;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.TextQuestion;

public class AnswerTextQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private EditText answer;
    private int myQuestionNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_text_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(answeringSurveyControl.getSurveysTitle());

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        question = answeringSurveyControl.getQuestion(myQuestionNumber);
        Log.d("WYPELNIANIE_ANKIETY", AnswerTextQuestionActivity.class + " nr pytania: " +
                myQuestionNumber);
        if(!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_short_text);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_short_text);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_short_text);
        questionHint.setText(question.getHint());

        answer = (EditText) findViewById(R.id.answer_answer_short_text);
        int maxLength = -1;
        TextQuestion txtQuestion = (TextQuestion) question;
        IConstraint constraint = txtQuestion.getConstraint();
        if(constraint != null) {
            if (constraint instanceof NumberConstraint) {
                NumberConstraint numberConstraint = (NumberConstraint) constraint;
                if(numberConstraint.isMustBeInteger()){
                    answer.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                else answer.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            else if (constraint instanceof TextConstraint) {
                TextConstraint textConstraint = (TextConstraint) constraint;
                if (textConstraint.getMaxLength() != null)
                    maxLength = textConstraint.getMaxLength();
            }
        }
        if(maxLength == -1) maxLength = TextQuestion.SHORT_ANSWER_MAX_LENGTH;
        answer.setFilters(new InputFilter[]{new InputFilter.LengthFilter
                (maxLength)});

        Button nextButton = (Button) findViewById(R.id.next_question_button);
        Button finishButton = (Button) findViewById(R.id.end_filling_button);
        Button finishAndStartButton = (Button) findViewById(R.id.end_and_start_filling_button);
        if(answeringSurveyControl.getNumberOfQuestions() - 1 > myQuestionNumber) {  //jeśli to nie jest ostatnie pytanie
            finishButton.setVisibility(View.INVISIBLE);
            finishAndStartButton.setVisibility(View.INVISIBLE);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (setUserAnswer())
                        goToNextActivity();
                }
            });
        }
        else{
            nextButton.setVisibility(View.INVISIBLE);
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (setUserAnswer()) {
                        if (answeringSurveyControl.finishAnswering(ApplicationState.
                                getInstance(getApplicationContext()).getSurveysRepository())) {
                            Intent intent = new Intent(AnswerTextQuestionActivity.this, SurveysSummary.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
                            startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(getApplicationContext(), "Nie można zakończyć ankiety", Toast.LENGTH_SHORT);
                    }
                }
            });
            finishAndStartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (setUserAnswer()) {
                        String idOfSurveys = answeringSurveyControl.getIdOfSurveysFillingSurvey(); //id wypełnianej ankiety
                        if (answeringSurveyControl.finishAnswering(ApplicationState.
                                getInstance(getApplicationContext()).getSurveysRepository())) {
                            Intent intent = new Intent(getApplicationContext(), WelcomeFillingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            answeringSurveyControl.startAnswering(idOfSurveys,          //rozpocznij wypełnianie nowej ankiety
                                    ApplicationState.getInstance(getApplicationContext()).getDeviceId());
                            intent.putExtra("SURVEY_TITLE", answeringSurveyControl.getSurveysTitle());
                            intent.putExtra("SURVEY_DESCRIPTION", answeringSurveyControl.getSurveysDescription());
                            intent.putExtra("SURVEY_SUMMARY", answeringSurveyControl.getSurveysSummary());

                            startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(getApplicationContext(), "Nie można zakończyć ankiety", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }


    private void goToNextActivity() {
        AnsweringSurveyControl control = ApplicationState.
                getInstance(AnswerTextQuestionActivity.this).getAnsweringSurveyControl();
        if(control.getNumberOfQuestions() - 1 > myQuestionNumber){
            Question question = control.getQuestion(myQuestionNumber + 1);
            int questionType = question.getQuestionType();
            Intent intent;
            if(questionType == Question.ONE_CHOICE_QUESTION){
                intent = new Intent(AnswerTextQuestionActivity.this,
                        AnswerOneChoiceQuestionActivity.class);
            }
            else if(questionType == Question.MULTIPLE_CHOICE_QUESTION){
                intent = new Intent(AnswerTextQuestionActivity.this,
                        AnswerMultipleChoiceQuestionActivity.class);
            }
            else if(questionType == Question.DROP_DOWN_QUESTION){
                intent = new Intent(AnswerTextQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
            }
            else if(questionType == Question.SCALE_QUESTION){
                intent = new Intent(AnswerTextQuestionActivity.this, AnswerScaleQuestionActivity.class);
            }
            else if(questionType == Question.DATE_QUESTION){
                intent = new Intent(AnswerTextQuestionActivity.this, AnswerDateQuestionActivity.class);
            }
            else if(questionType == Question.TIME_QUESTION){
                intent = new Intent(AnswerTextQuestionActivity.this, AnswerTimeQuestionActivity.class);
            }
            else if(questionType == Question.GRID_QUESTION){
                intent = new Intent(AnswerTextQuestionActivity.this, AnswerGridQuestionActivity.class);
            }
            else if(questionType == Question.TEXT_QUESTION){
                intent = new Intent(AnswerTextQuestionActivity.this, AnswerTextQuestionActivity.class);
            }
            else{
                intent = new Intent(AnswerTextQuestionActivity.this, SurveysSummary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
            startActivity(intent);
        }
    }

    private boolean setUserAnswer(){
        AnsweringSurveyControl control = answeringSurveyControl;
        String givenAnswer = answer.getText().toString();
        if(question.isObligatory()){
            if(givenAnswer.trim().isEmpty()){ //jeśli pytanie jest obowiązkowe i nic nie dodano
                answer.setError("To pytanie jest obowiązkowe, podaj odpowiedź!");

                return false;
            }
        }
        if(!givenAnswer.trim().isEmpty()){
            if(control.setTextQuestionAnswer(myQuestionNumber, givenAnswer)) {
                return true;
            }
            else{
                TextQuestion textQuestion = (TextQuestion) question;
                if(textQuestion.getConstraint() instanceof  NumberConstraint){
                    NumberConstraint numberConstraint = (NumberConstraint) textQuestion.getConstraint();
                    Double toCheck = 0.0;
                    try{
                        toCheck = Double.valueOf(givenAnswer);
                    }
                    catch(NumberFormatException e){
                        answer.setError("Odpowiedź powinna być liczbą.");
                        return false;
                    }
                    if(numberConstraint.getNotEquals() != null){
                        if(toCheck.equals(numberConstraint.getNotEquals() )){
                            answer.setError("Odpowiedź musi być różna od " + numberConstraint.getNotEquals());
                            return false;
                        }
                    }
                    if(! numberConstraint.isNotBetweenMaxAndMinValue()){
                        if(numberConstraint.getMinValue() != null){
                            if(toCheck.compareTo(numberConstraint.getMinValue()) < 0){
                                answer.setError("Odpowiedź musi być większa od " + numberConstraint.getMinValue());
                                return false;
                            }
                        }
                        if(numberConstraint.getMaxValue() != null){
                            if(toCheck.compareTo(numberConstraint.getMaxValue()) > 0){
                                answer.setError("Odpowiedź musi być mniejsza lub równa " + numberConstraint.getMaxValue());
                                return false;
                            }
                        }
                    }
                    else{
                        if(numberConstraint.getMinValue() != null && numberConstraint.getMaxValue() != null){
                            if(toCheck.compareTo(numberConstraint.getMinValue()) >= 0
                                    && toCheck.compareTo(numberConstraint.getMaxValue()) <= 0){
                                answer.setError("Odpowiedź nie powinna się znajdować pomiędzy " +
                                        numberConstraint.getMinValue() + " i " + numberConstraint.getMaxValue());
                                return false;
                            }
                        }
                        else{
                            if(numberConstraint.getMinValue() != null){
                                if(toCheck.compareTo(numberConstraint.getMinValue()) >= 0){
                                    answer.setError("Odpowiedź powinna być mniejsza od " +
                                            numberConstraint.getMinValue());
                                    return false;
                                }
                            }
                            if(numberConstraint.getMaxValue() != null){
                                if(toCheck.compareTo(numberConstraint.getMaxValue()) <= 0){
                                    answer.setError("Odpowiedź powinna być większa od " +
                                            numberConstraint.getMaxValue());
                                    return false;
                                }
                            }
                        }
                    }
                    if(numberConstraint.isMustBeInteger()){
                        if((toCheck % (toCheck.intValue())) != 0){
                            answer.setError("Odpowiedź powinna być liczbą całkowitą.");
                            return false;
                        }
                    }
                }
                else if(textQuestion.getConstraint() instanceof TextConstraint){
                    TextConstraint textConstraint = (TextConstraint) textQuestion.getConstraint();
                    if(textConstraint.getMinLength() != null){
                        if(givenAnswer.length() < textConstraint.getMinLength()){
                            answer.setError("Odpowiedź powinna mieć co najmniej " + textConstraint.getMinLength()
                            + " znaków");
                            return false;
                        }
                    }
                    if(textConstraint.getRegex() != null){
                        Matcher matcher = textConstraint.getRegex().matcher(givenAnswer);
                        if(!matcher.matches()){
                            answer.setError("Odpowiedź powinna być postaci: " +
                                    textConstraint.getRegex().pattern());
                            return false;
                        }
                    }
                }
                answer.setError("Nie można dodać odpowiedzi - nieznany błąd");
                return false;
            }
        }
        return true;
    }
}
