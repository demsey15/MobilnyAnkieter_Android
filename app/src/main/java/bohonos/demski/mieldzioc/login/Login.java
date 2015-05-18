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

import bohonos.demski.mieldzioc.creatingAndEditingSurvey.MainActivity;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.dataBase.InterviewerDBAdapter;
import bohonos.demski.mieldzioc.interviewer.Interviewer;

public class Login extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                        char[] password = passw.toCharArray();
                        InterviewerDBAdapter db = new InterviewerDBAdapter(getApplicationContext());
                        db.open();
                        Interviewer interviewer = db.getInterviewer(login);
                        if (interviewer == null) {
                            loginTxt.setError("Brak ankietera.");
                        } else {
                            if (db.checkPassword(login, password)) {
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                loginTxt.setError("Podano błędne dane.");
                            }
                        }
                        db.close();
                    }
                }
            }
        });
    }
}
