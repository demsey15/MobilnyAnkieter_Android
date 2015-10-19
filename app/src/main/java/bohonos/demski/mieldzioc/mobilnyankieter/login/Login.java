package bohonos.demski.mieldzioc.mobilnyankieter.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewAnimator;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.application.ApplicationState;
import bohonos.demski.mieldzioc.mobilnyankieter.client.MainActivity;

public class Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ViewAnimator animator = (ViewAnimator) findViewById(R.id.login_animator);
        animator.setDisplayedChild(1);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.setDisplayedChild(0);

                EditText passwordTxt = (EditText) findViewById(R.id.user_password);
                final String password = passwordTxt.getText().toString();

                if (password.trim().isEmpty()){
                    passwordTxt.setError("Hasło nie może być puste!");
                }
                else{
                    if(isPasswordCorrect(password)){
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        animator.setDisplayedChild(1);
                        Toast.makeText(getApplicationContext(), "Podano błędne hasło!", Toast.LENGTH_LONG);
                    }
                }
            }
        });

    }

    private boolean isPasswordCorrect(String password){
        return ApplicationState.getInstance(getApplicationContext()).logIn(password);
    }
}

