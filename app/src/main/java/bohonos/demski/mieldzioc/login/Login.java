package bohonos.demski.mieldzioc.login;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewAnimator;

import bohonos.demski.mieldzioc.application.NetworkIssuesControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.MainActivity;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.dataBase.InterviewerDBAdapter;
import bohonos.demski.mieldzioc.interviewer.Interviewer;

public class Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ViewAnimator animator = (ViewAnimator) findViewById(R.id.login_animator);
        animator.setDisplayedChild(1);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText loginTxt = (EditText) findViewById(R.id.user_pesel);
                String login = loginTxt.getText().toString();
                if (login.trim().equals("")) loginTxt.setError("Podaj PESEL");
                else {
                    EditText passwordTxt = (EditText) findViewById(R.id.user_password);
                    String passw = passwordTxt.getText().toString();
                    if (passw.trim().equals("")) passwordTxt.setError("Hasło nie może być puste!");
                    else {
                        animator.setDisplayedChild(0);
                        char[] password = passw.toCharArray();
                        NetworkIssuesControl networkIssuesControl = new NetworkIssuesControl
                                (getApplicationContext());
                        int result = networkIssuesControl.login(login, password);
                        if(result == NetworkIssuesControl.NO_NETWORK_CONNECTION){
                            Toast.makeText(getApplicationContext(), "Brak połączenia z internetem.",
                                    Toast.LENGTH_LONG).show();
                            animator.setDisplayedChild(1);
                        }
                        else if(result == NetworkIssuesControl.REQUEST_OUT_OF_TIME){
                            Toast.makeText(getApplicationContext(), "Serwer nie odpowiada, " +
                                            "spróbuj ponownie.",
                                    Toast.LENGTH_LONG).show();
                            animator.setDisplayedChild(1);
                        }
                        else if(result == NetworkIssuesControl.UNKNOWN_ERROR_CONNECTION){
                            Toast.makeText(getApplicationContext(), "Błąd połączenia z serwerem." +
                                            " Spróbuj ponownie.",
                                    Toast.LENGTH_LONG).show();
                            animator.setDisplayedChild(1);
                        }
                        else if(result == 0){
                            animator.setDisplayedChild(1);
                            loginTxt.setError("Podano błędne dane");
                        }
                        else if(result == 1){
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                }
            }
        });
    }
}
