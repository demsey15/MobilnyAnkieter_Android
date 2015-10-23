package bohonos.demski.mieldzioc.mobilnyankieter.fillingSurvey;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.application.DateAndTimeService;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.myControls.DatePickerFragment;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;

public class AnswerDateQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private int myQuestionNumber;
    private EditText answerTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bohonos.demski.mieldzioc.mobilnyankieter.R.layout.activity_answer_date_question);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(answeringSurveyControl.getSurveysTitle());

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        Log.d("WYPELNIANIE_ANKIETY", "Date - otrzymalem pytanie nr: " + myQuestionNumber);
        question =  answeringSurveyControl.getQuestion(myQuestionNumber);

        if (!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_date);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_date);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_date);
        questionHint.setText(question.getHint());

        answerTxt = (EditText) findViewById(R.id.answer_date);

        Button chooseDateButt = (Button) findViewById(R.id.answer_choose_date_date);
        chooseDateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
              //  FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                DatePickerFragment fragmentAnswer = DatePickerFragment.newInstance(answerTxt.getId());
                // fragmentTransaction.add(R.id.answer_linear_date, fragmentAnswer);
                fragmentAnswer.show(fragmentManager, "MOJ_PICKER");
            }
        });

        ImageButton nextButton = (ImageButton) findViewById(R.id.next_question_button);
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
                            Intent intent = new Intent(AnswerDateQuestionActivity.this, SurveysSummary.class);
                            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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


    private boolean setUserAnswer(){
        String answer = answerTxt.getText().toString();
        if(question.isObligatory()) {       //jest obowiazkowe
            if (answer.trim().equals("")) {      //i nie ma odpowiedzi
                answerTxt.setError("Pytanie jest obowiązkowe. Proszę podać odpowiedź.");
                return false;
            }
        }
        if(!answer.trim().equals("")){    //jest odpowiedz (nie ważne, czy jest obowiązkowe
            if(DateAndTimeService.getDateFromString(  //jeżeli nie można zrobić z niej daty
                    answer + " 01:00:00") == null) {
                answerTxt.setError("Podaj datę w formacie dd-mm-yyyy");
                return false;
            }
            else{                                                          //jeżeli można zrobić z niej datę
                String day = answer.substring(0, 2);
                String month = answer.substring(3, 5);
                String year = answer.substring(6, 10);
                if(answeringSurveyControl.setDateQuestionAnswer(myQuestionNumber, Integer.valueOf(day), //dodano odpowiedź
                        Integer.valueOf(month), Integer.valueOf(year))){
                        return true;
                }
                else{                           //nie powinno się zdarzyć
                    answerTxt.setError("Podana odpowiedź zawiera błędy, podaj datę w formacie" +
                            " dd-mm-yyyy po 1970 roku");
                    return false;
                }
            }
        }
        return  true;
    }
    private void goToNextActivity(){
        AnsweringSurveyControl control = answeringSurveyControl;
        if (control.getNumberOfQuestions() - 1 > myQuestionNumber) {
            Question question = control.getQuestion(myQuestionNumber + 1);
            int questionType = question.getQuestionType();
            Intent intent;
            if (questionType == Question.ONE_CHOICE_QUESTION) {
                intent = new Intent(AnswerDateQuestionActivity.this,
                        AnswerOneChoiceQuestionActivity.class);
            } else if (questionType == Question.MULTIPLE_CHOICE_QUESTION) {
                intent = new Intent(AnswerDateQuestionActivity.this,
                        AnswerMultipleChoiceQuestionActivity.class);
            } else if (questionType == Question.DROP_DOWN_QUESTION) {
                intent = new Intent(AnswerDateQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
            } else if (questionType == Question.SCALE_QUESTION) {
                intent = new Intent(AnswerDateQuestionActivity.this, AnswerScaleQuestionActivity.class);
            } else if (questionType == Question.DATE_QUESTION) {
                intent = new Intent(AnswerDateQuestionActivity.this, AnswerDateQuestionActivity.class);
            } else if (questionType == Question.TIME_QUESTION) {
                intent = new Intent(AnswerDateQuestionActivity.this, AnswerTimeQuestionActivity.class);
            } else if (questionType == Question.GRID_QUESTION) {
                intent = new Intent(AnswerDateQuestionActivity.this, AnswerGridQuestionActivity.class);
            } else if (questionType == Question.TEXT_QUESTION) {
                intent = new Intent(AnswerDateQuestionActivity.this, AnswerTextQuestionActivity.class);
            } else{
                intent = new Intent(AnswerDateQuestionActivity.this, SurveysSummary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
            startActivity(intent);
        }
    }
}
