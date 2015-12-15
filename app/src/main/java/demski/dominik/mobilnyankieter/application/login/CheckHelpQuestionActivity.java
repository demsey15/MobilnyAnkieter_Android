package demski.dominik.mobilnyankieter.application.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import demski.dominik.mobilnyankieter.R;
import demski.dominik.mobilnyankieter.application.UserPreferences;

public class CheckHelpQuestionActivity extends ActionBarActivity {
    private String answer;

    private EditText answerTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_help_question);

        final UserPreferences userPreferences = UserPreferences.getInstance(getApplicationContext());
        answer = userPreferences.getHelpQuestionAnswer().trim();

        TextView questionTxt = (TextView) findViewById(R.id.reset_password_question);
        questionTxt.setText(userPreferences.getHelpQuestion());

        answerTxt = (EditText) findViewById(R.id.reset_password_answer);

        prepareResetButton(userPreferences);
    }

    private void prepareResetButton(final UserPreferences userPreferences) {
        Button resetPassword = (Button) findViewById(R.id.reset_password_button);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String readAnswer = answerTxt.getText().toString();

                if(answer.equalsIgnoreCase(readAnswer.trim())){
                    userPreferences.resetUsersSettings();

                    Intent intent = new Intent(CheckHelpQuestionActivity.this, Register.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);
                    setResult(RESULT_OK);

                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Podano błędną odpowiedź.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
