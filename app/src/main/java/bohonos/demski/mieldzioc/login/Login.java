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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import bohonos.demski.mieldzioc.application.NetworkIssuesControl;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.MainActivity;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.dataBase.InterviewerDBAdapter;
import bohonos.demski.mieldzioc.interviewer.Interviewer;
import bohonos.demski.mieldzioc.networkConnection.ServerConnectionFacade;

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
                EditText loginTxt = (EditText) findViewById(R.id.user_pesel);
                String login = loginTxt.getText().toString();
                if (login.trim().equals("")) loginTxt.setError("Podaj PESEL");
                else {
                    EditText passwordTxt = (EditText) findViewById(R.id.user_password);
                    String passw = passwordTxt.getText().toString();
                    if (passw.trim().equals("")) passwordTxt.setError("Hasło nie może być puste!");
                    else {
                        TextView textProgress = (TextView) findViewById(R.id.text_progress);
                        textProgress.setVisibility(View.INVISIBLE);
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
                        else if(result == 1){    //udało się zalogować!

                            textProgress.setText("Pobieranie uprawnień...");
                            textProgress.setVisibility(View.VISIBLE);

                            int canCreate = networkIssuesControl.updateInterviewerCanCreate(login);
                            boolean goAheadCreate = false;
                            boolean createEstablish = false;

                            if(canCreate == NetworkIssuesControl.NO_NETWORK_CONNECTION ||   //przechodzimy dalej, ale nie możemy tworzyć ankiet
                                    canCreate == NetworkIssuesControl.REQUEST_OUT_OF_TIME ||
                                    canCreate == NetworkIssuesControl.UNKNOWN_ERROR_CONNECTION){
                                goAheadCreate = true;
                            }
                            else if(canCreate == ServerConnectionFacade.BAD_PASSWORD){
                                    animator.setDisplayedChild(1);
                                    Toast.makeText(getApplicationContext(), "Wygląda na to, że zostałeś" +
                                            " zwolniony", Toast.LENGTH_LONG).show();
                            }
                            else{       //ustalono uprawnienia
                                goAheadCreate = true;
                                createEstablish = true;
                            }
                            if(goAheadCreate){
                                int fill = networkIssuesControl.prepareTemplatesToFill();
                                if(fill == NetworkIssuesControl.REQUEST_OUT_OF_TIME ||
                                        fill == NetworkIssuesControl.UNKNOWN_ERROR_CONNECTION){
                                    animator.setDisplayedChild(1);
                                    Toast.makeText(getApplicationContext(), "Nie można ustalić listy ankiet do wypełniania." +
                                            " Spróbuj ponownie.", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    if(!createEstablish)
                                        Toast.makeText(getApplicationContext(), "Nie można ustalić uprawnień." +
                                            "\nSpróbuj ponownie później.", Toast.LENGTH_LONG).show();

                                    finish();
                                }
                            }
                        }

                    }
                }
            }
        });
    }
}
