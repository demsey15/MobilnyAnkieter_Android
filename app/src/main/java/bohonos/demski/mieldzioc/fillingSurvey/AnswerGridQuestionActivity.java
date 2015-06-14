package bohonos.demski.mieldzioc.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.controls.AnsweringSurveyControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.GenerateId;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.questions.GridQuestion;
import bohonos.demski.mieldzioc.questions.Question;

public class AnswerGridQuestionActivity extends ActionBarActivity {

    private AnsweringSurveyControl answeringSurveyControl = ApplicationState.getInstance(this).
            getAnsweringSurveyControl();
    private Question question;
    private List<String> rowLabels = new ArrayList<>();
    private List<String> columnLabels = new ArrayList<>();
    private List<List<RadioButton>> radioButtons = new ArrayList<>();
    private int myQuestionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_grid_question);

        myQuestionNumber = getIntent().getIntExtra("QUESTION_NUMBER", 0);
        question = answeringSurveyControl.getQuestion(myQuestionNumber);

        if (!question.isObligatory()) {
            TextView obligatoryText = (TextView) findViewById(R.id.answer_obligatory_grid);
            obligatoryText.setVisibility(View.INVISIBLE);
        }

        TextView questionText = (TextView) findViewById(R.id.answer_question_grid);
        questionText.setText(question.getQuestion());

        TextView questionHint = (TextView) findViewById(R.id.answer_hint_grid);
        questionHint.setText(question.getHint());

        createGrid();

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
                            Intent intent = new Intent(AnswerGridQuestionActivity.this, SurveysSummary.class);
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
                            answeringSurveyControl.startAnswering(idOfSurveys,          //rozpocznij wypełnianie nowej ankiety
                                    ApplicationState.getInstance(getApplicationContext()).getLoggedInterviewer());
                            intent.putExtra("SURVEY_TITLE", answeringSurveyControl.getSurveysTitle());
                            intent.putExtra("SURVEY_DESCRIPTION", answeringSurveyControl.getSurveysDescription());
                            intent.putExtra("SURVEY_SUMMARY", answeringSurveyControl.getSurveysSummary());
                            startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(getApplicationContext(), "Nie można zakończyć ankiety",
                                    Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    private void createGrid(){
        TableLayout tableLayout = (TableLayout) findViewById(R.id.answer_grid_table);
        GridQuestion gridQuestion = (GridQuestion) question;
        rowLabels = gridQuestion.getRowLabels();
        columnLabels = gridQuestion.getColumnLabels();

        TableRow row = new TableRow(this);
        row.addView(new TextView(this));
        for(String label : columnLabels){
            TextView textView = new TextView(this);
            textView.setText(label);
            textView.setPadding(5,5,50,50);
            row.addView(textView);
        }
        tableLayout.addView(row);

        for(String rowLabel : rowLabels){
            TableRow row2 = new TableRow(this);
            TextView textView = new TextView(this);
            textView.setText(rowLabel);
            textView.setPadding(5, 5, 50, 50);
            row2.addView(textView);
            List<RadioButton> list = new ArrayList<>(rowLabels.size());
            radioButtons.add(list);
            for(String colLabel : columnLabels){
                RadioButton radioButton = new RadioButton(this){
                    @Override
                    public void toggle() {
                        if(isChecked()) {
                                setChecked(false);

                        } else {
                            setChecked(true);
                        }
                    }
                };
                radioButton.setId(GenerateId.generateViewId());
                radioButton.setPadding(5, 5, 5, 5);
                list.add(radioButton);
                row2.addView(radioButton);
            }
            tableLayout.addView(row2);
        }


/*
        TableRow row = new TableRow(this);
        RadioButton radioButton = new RadioButton(this);
        radioButton.setId(GenerateId.generateViewId());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        radioButton.setText("Moj przycisk");
        row.addView(radioButton);
        TableRow row2 = new TableRow(this);
        RadioButton radioButton2 = new RadioButton(this);
        radioButton2.setId(GenerateId.generateViewId());
        ViewGroup.LayoutParams layoutParams2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        radioButton2.setText("Moj przycisk");
        row2.addView(radioButton2);
        groupTableLayout.addView(row, layoutParams);
        groupTableLayout.addView(row2, layoutParams); */
                /*
                <TableRow>
        <RadioButton android:id="@+id/rad1" android:text="Button1"
        android:layout_width="105px" android:layout_height="wrap_content"
        android:textSize="13px" />
        <RadioButton android:id="@+id/rad2" android:text="Button2"
        android:layout_width="105px" android:textSize="13px"
        android:layout_height="wrap_content" />
        <RadioButton android:id="@+id/rad3" android:text="Button3"
        android:layout_width="105px" android:textSize="13px"
        android:layout_height="wrap_content" />
        </TableRow > */
    }
    private void goToNextActivity() {
        AnsweringSurveyControl control = ApplicationState.
                getInstance(AnswerGridQuestionActivity.this).getAnsweringSurveyControl();

        if(control.getNumberOfQuestions() - 1 > myQuestionNumber){
            Question question = control.getQuestion(myQuestionNumber + 1);
            int questionType = question.getQuestionType();
            Intent intent;
            if(questionType == Question.ONE_CHOICE_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this,
                        AnswerOneChoiceQuestionActivity.class);
            }
            else if(questionType == Question.MULTIPLE_CHOICE_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this,
                        AnswerMultipleChoiceQuestionActivity.class);
            }
            else if(questionType == Question.DROP_DOWN_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerDropDownListQuestionActivity.class);
            }
            else if(questionType == Question.SCALE_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerScaleQuestionActivity.class);
            }
            else if(questionType == Question.DATE_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerDateQuestionActivity.class);
            }
            else if(questionType == Question.TIME_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerTimeQuestionActivity.class);
            }
            else if(questionType == Question.GRID_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerGridQuestionActivity.class);
            }
            else if(questionType == Question.TEXT_QUESTION){
                intent = new Intent(AnswerGridQuestionActivity.this, AnswerTextQuestionActivity.class);
            }
            else intent = new Intent(AnswerGridQuestionActivity.this, SurveysSummary.class);
            intent.putExtra("QUESTION_NUMBER", myQuestionNumber + 1);
            intent.putExtra("SURVEY_SUMMARY", getIntent().getStringExtra("SURVEY_SUMMARY"));
            startActivity(intent);
        }
    }

    private boolean setUserAnswer(){
        //Każda odpowiedź powinna być w formacie: #rowLabel# ^columnLabel^
        List<String> answers = new ArrayList<>();
        for(int i = 0; i < radioButtons.size(); i++){
            for(int j = 0; j < radioButtons.get(0).size(); j++){
                RadioButton button = radioButtons.get(i).get(j);
                if(button.isChecked()){
                    String answer = "#" + rowLabels.get(i) + "#" + " ^" + columnLabels.get(j) + "^";
                    answers.add(answer);
                }
            }
        }
        if(question.isObligatory()){
            if(answers.isEmpty()){     //jeśli pytanie jest obowiązkowe i nic nie dodano
                Toast.makeText(AnswerGridQuestionActivity.this,
                        "To pytanie jest obowiązkowe, podaj odpowiedź!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(! answers.isEmpty()){
            if(answeringSurveyControl.setGridQuestionAnswer(myQuestionNumber, answers))
                return true;
            else{
                Toast.makeText(this,
                        "Coś poszło nie tak, nie dodano odpowiedzi.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_grid_question, menu);
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
