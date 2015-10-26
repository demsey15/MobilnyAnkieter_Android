package bohonos.demski.mieldzioc.mobilnyankieter.application.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;

public class CheckHelpQuestionActivity extends ActionBarActivity {
    private String answer;

    private EditText answerTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_help_question);

        final ApplicationState applicationState = ApplicationState.getInstance(getApplicationContext());
        answer = applicationState.getHelpQuestionAnswer();

        TextView questionTxt = (TextView) findViewById(R.id.reset_password_question);
        questionTxt.setText(applicationState.getHelpQuestion());

        answerTxt = (EditText) findViewById(R.id.reset_password_answer);

        prepareResetButton(applicationState);
    }

    private void prepareResetButton(final ApplicationState applicationState) {
        Button resetPassword = (Button) findViewById(R.id.reset_password_button);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String readAnswer = answerTxt.getText().toString();

                if(answer.equalsIgnoreCase(readAnswer)){
                    applicationState.resetUsersSettings();

                    Intent intent = new Intent(CheckHelpQuestionActivity.this, Register.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Podano błędną odpowiedź.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
