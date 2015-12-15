package demski.dominik.mobilnyankieter.application.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import demski.dominik.mobilnyankieter.R;
import demski.dominik.mobilnyankieter.application.UserPreferences;
import demski.dominik.mobilnyankieter.client.MainActivity;

public class Login extends ActionBarActivity {
    private CheckBox rememberPasswordChkBox;
    private CheckBox shouldNotLogOutChkBox;
    private EditText passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserPreferences userPreferences = UserPreferences.getInstance(getApplicationContext());

        if(userPreferences.ifShouldDontLogOut()){
            userPreferences.logIn(userPreferences.getUserPassword());

            Intent intent = new Intent(Login.this, MainActivity.class);

            startActivity(intent);
            finish();

        }
        else{
            boolean shouldRememberPassword = userPreferences.ifShouldRememberPassword();

            rememberPasswordChkBox = (CheckBox) findViewById(R.id.rememberPasswordChcBox);
            rememberPasswordChkBox.setChecked(shouldRememberPassword);

            shouldNotLogOutChkBox = (CheckBox) findViewById(R.id.dontLogOutChcBox);
            shouldNotLogOutChkBox.setChecked(userPreferences.ifShouldDontLogOut());

            passwordTxt = (EditText) findViewById(R.id.user_password);

            if(shouldRememberPassword){
                passwordTxt.setText(String.valueOf(userPreferences.getUserPassword()));
            }

            final ViewAnimator animator = (ViewAnimator) findViewById(R.id.login_animator);
            animator.setDisplayedChild(0);

            final ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            prepareLoginButton(animator);

            TextView forgotPasswordTxt = (TextView) findViewById(R.id.forgot_password_txt);
            forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String helpQuestion = UserPreferences.getInstance(getApplicationContext()).getHelpQuestion();

                    if(helpQuestion.isEmpty()){
                        (new AlertDialog.Builder(getApplicationContext())
                                .setMessage("Niestety podczas rejestracji nie ustalono pytania pomocniczego, w związku" +
                                        " z tym, nie można zresetować hasła.")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setNeutralButton("Ok", null)).show();
                    }
                    else{
                        Intent intent = new Intent(Login.this, CheckHelpQuestionActivity.class);

                        startActivityForResult(intent, 1);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK){
            finish();
        }
    }

    private void prepareLoginButton(final ViewAnimator animator) {
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.setDisplayedChild(1);

                final String password = passwordTxt.getText().toString();

                if (password.trim().isEmpty()){
                    animator.setDisplayedChild(0);
                    passwordTxt.setError("Hasło nie może być puste!");
                }
                else{
                    if(isPasswordCorrect(password)){
                        UserPreferences userPreferences = UserPreferences.getInstance(getApplicationContext());

                        userPreferences.saveRememberPassword(rememberPasswordChkBox.isChecked());
                        userPreferences.saveDontLogOut(shouldNotLogOutChkBox.isChecked());

                        Intent intent = new Intent(Login.this, MainActivity.class);

                        finish();
                        startActivity(intent);
                    }
                    else{
                        animator.setDisplayedChild(0);
                        Toast.makeText(getApplicationContext(), "Podano błędne hasło!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private boolean isPasswordCorrect(String password){
        return UserPreferences.getInstance(getApplicationContext()).logIn(password.toCharArray());
    }
}

