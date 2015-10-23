package bohonos.demski.mieldzioc.mobilnyankieter.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewAnimator;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.client.MainActivity;

public class Login extends ActionBarActivity {
    private CheckBox rememberPasswordChkBox;
    private CheckBox shouldNotLogOutChkBox;
    private EditText passwordTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ApplicationState applicationState = ApplicationState.getInstance(getApplicationContext());

        if(applicationState.ifShouldDontLogOut()){
            applicationState.logIn(applicationState.getUserPassword());

            Intent intent = new Intent(Login.this, MainActivity.class);

            startActivity(intent);
            finish();

        }
        else{
            boolean shouldRememberPassword = applicationState.ifShouldRememberPassword();

            rememberPasswordChkBox = (CheckBox) findViewById(R.id.rememberPasswordChcBox);
            rememberPasswordChkBox.setChecked(shouldRememberPassword);

            shouldNotLogOutChkBox = (CheckBox) findViewById(R.id.dontLogOutChcBox);
            shouldNotLogOutChkBox.setChecked(applicationState.ifShouldDontLogOut());

            passwordTxt = (EditText) findViewById(R.id.user_password);

            if(shouldRememberPassword){
                passwordTxt.setText(String.valueOf(applicationState.getUserPassword()));
            }

            final ViewAnimator animator = (ViewAnimator) findViewById(R.id.login_animator);
            animator.setDisplayedChild(0);

            final ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            prepareLoginButton(animator);
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
                        ApplicationState applicationState = ApplicationState.getInstance(getApplicationContext());

                        applicationState.saveRememberPassword(rememberPasswordChkBox.isChecked());
                        applicationState.saveDontLogOut(shouldNotLogOutChkBox.isChecked());

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
        return ApplicationState.getInstance(getApplicationContext()).logIn(password.toCharArray());
    }
}

