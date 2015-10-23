package bohonos.demski.mieldzioc.mobilnyankieter.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.client.MainActivity;

public class Register extends ActionBarActivity {
    EditText passwordTxt;
    EditText repeatedPasswordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if(checkIfUserIsAlreadyRegistered()){
            startLoginActivity();
        }
        else {
            passwordTxt = (EditText) findViewById(R.id.user_new_password);
            repeatedPasswordTxt = (EditText) findViewById(R.id.user_new_password_again);

            prepareRepeatedPasswordTxt();

            Button registerButton = (Button) findViewById(R.id.register_button);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (passwordTxt.getText().toString().isEmpty()) {
                        passwordTxt.setError("Hasło nie może być puste!");

                        reportErrorsInForm();
                    } else {
                        if (checkIfRepeatedPasswordEqualsUserPassword()) {
                            ApplicationState applicationState = ApplicationState.getInstance(getApplicationContext());

                            boolean isHelpQuestionEmptyOrValid = establishHelpQuestion(applicationState);

                            if (isHelpQuestionEmptyOrValid) {
                                applicationState.saveUserPassword(passwordTxt.getText().toString().toCharArray());

                                applicationState.logIn(passwordTxt.getText().toString().toCharArray());

                                Intent intent = new Intent(Register.this, MainActivity.class);

                                startActivity(intent);
                                finish();

                                Toast.makeText(getApplicationContext(), "Dane zostały zapisane!", Toast.LENGTH_SHORT).show();
                            } else {
                                reportErrorsInForm();
                            }
                        } else {
                            reportErrorsInForm();
                        }
                    }
                }
            });
        }

    }

    private void reportErrorsInForm() {
        Toast.makeText(getApplicationContext(), "Popraw występujące błędy!", Toast.LENGTH_SHORT).show();
    }

    private boolean establishHelpQuestion(ApplicationState applicationState) {
        EditText helpQuestionTxt = (EditText) findViewById(R.id.user_help_question);
        String helpQuestion = helpQuestionTxt.getText().toString();

        if(!helpQuestion.isEmpty()){
            EditText helpQuestionAnswerTxt = (EditText) findViewById(R.id.user_help_question_answer);
            String helpQuestionAnswer = helpQuestionAnswerTxt.getText().toString();

            if(helpQuestionAnswer.isEmpty()){
                helpQuestionAnswerTxt.setError("Odpowiedź nie może być pusta!");

                return false;
            }
            else{
                applicationState.saveHelpQuestion(helpQuestion);
                applicationState.saveHelpQuestionAnswer(helpQuestionAnswer);

                return true;
            }
        }
        else{
            return true;
        }
    }

    private void prepareRepeatedPasswordTxt() {
        repeatedPasswordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!checkIfRepeatedPasswordEqualsUserPassword()) {
                    repeatedPasswordTxt.setError("Podane hasła muszą się zgadzać!");
                }
            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(Register.this, Login.class);

        startActivity(intent);
        finish();
    }

    private boolean checkIfUserIsAlreadyRegistered(){
        return ApplicationState.getInstance(getApplicationContext()).checkIfUsersPasswordIsSet();
    }

    private boolean checkIfRepeatedPasswordEqualsUserPassword(){
        return passwordTxt.getText().toString().equals(repeatedPasswordTxt.getText().toString());
    }

}
