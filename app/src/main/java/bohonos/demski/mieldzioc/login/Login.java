package bohonos.demski.mieldzioc.login;

import android.content.Intent;
import android.os.AsyncTask;
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
                final EditText loginTxt = (EditText) findViewById(R.id.user_pesel);
                final String login = loginTxt.getText().toString();
                if (login.trim().equals("")) loginTxt.setError("Podaj PESEL");
                else {
                    final EditText passwordTxt = (EditText) findViewById(R.id.user_password);
                    final String passw = passwordTxt.getText().toString();
                    if (passw.trim().equals("")) passwordTxt.setError("Hasło nie może być puste!");
                    else {
                        final TextView textProgress = (TextView) findViewById(R.id.text_progress);

                        (new AsyncTask<Object, Integer, Integer>() {
                            @Override
                            protected Integer doInBackground(Object... params) {        //zaloguj
                                ServerConnectionFacade s = new ServerConnectionFacade();

                                publishProgress(1);
                                char[] password = passw.toCharArray();
                                NetworkIssuesControl networkIssuesControl = new NetworkIssuesControl
                                        (getApplicationContext());
                                return networkIssuesControl.login(login, password);
                            }

                            @Override
                            protected void onPostExecute(Integer integer) {
                                int result = integer;
                                if (result == NetworkIssuesControl.NO_NETWORK_CONNECTION) {
                                    Toast.makeText(getApplicationContext(), "Brak połączenia z internetem.",
                                            Toast.LENGTH_LONG).show();
                                    animator.setDisplayedChild(1);
                                } else if (result == NetworkIssuesControl.REQUEST_OUT_OF_TIME) {
                                    Toast.makeText(getApplicationContext(), "Serwer nie odpowiada, " +
                                                    "spróbuj ponownie.",
                                            Toast.LENGTH_LONG).show();
                                    animator.setDisplayedChild(1);
                                } else if (result == NetworkIssuesControl.UNKNOWN_ERROR_CONNECTION) {
                                    Toast.makeText(getApplicationContext(), "Błąd połączenia z serwerem." +
                                                    " Spróbuj ponownie.",
                                            Toast.LENGTH_LONG).show();
                                    animator.setDisplayedChild(1);
                                } else if (result == 0) {
                                    animator.setDisplayedChild(1);
                                    loginTxt.setError("Podano błędne dane");
                                } else if (result == 1) {    //udało się zalogować!


                                    (new AsyncTask<Object, Integer, Object[]>() {
                                        @Override
                                        protected Object[] doInBackground(Object... params) {
                                           publishProgress(2);

                                            NetworkIssuesControl networkIssuesControl =
                                                    new NetworkIssuesControl(getApplicationContext());
                                            int canCreate = networkIssuesControl.updateInterviewerCanCreate(login);
                                            boolean goAheadCreate = false;
                                            boolean createEstablish = false;

                                            if (canCreate == NetworkIssuesControl.NO_NETWORK_CONNECTION ||   //przechodzimy dalej, ale nie możemy tworzyć ankiet
                                                    canCreate == NetworkIssuesControl.REQUEST_OUT_OF_TIME ||
                                                    canCreate == NetworkIssuesControl.UNKNOWN_ERROR_CONNECTION) {
                                                goAheadCreate = true;
                                            } else if (canCreate == ServerConnectionFacade.BAD_PASSWORD) {
                                                publishProgress(3);
                                            } else {       //ustalono uprawnienia do tworzenia ankiet
                                                goAheadCreate = true;
                                                createEstablish = true;
                                            }
                                            if (goAheadCreate) {
                                                int fill = networkIssuesControl.prepareTemplatesToFill();
                                                if (fill == NetworkIssuesControl.REQUEST_OUT_OF_TIME ||
                                                        fill == NetworkIssuesControl.UNKNOWN_ERROR_CONNECTION) {
                                                    publishProgress(4);
                                                } else {
                                                    return new Object[] {2, createEstablish};
                                                }
                                            }

                                        return new Object[] {1, createEstablish};
                                        }

                                        @Override
                                        protected void onProgressUpdate(Integer... values) {
                                            if(values[0] == 2){
                                                textProgress.setText("Pobieranie uprawnień...");
                                                textProgress.setVisibility(View.VISIBLE);
                                            }
                                            else if(values[0] == 3){
                                                animator.setDisplayedChild(1);
                                                Toast.makeText(getApplicationContext(), "Wygląda na to, że zostałeś" +
                                                        " zwolniony", Toast.LENGTH_LONG).show();
                                            }
                                            else if(values[0] == 4){
                                                animator.setDisplayedChild(1);
                                                Toast.makeText(getApplicationContext(), "Nie można ustalić listy ankiet do wypełniania." +
                                                        " Spróbuj ponownie.", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        protected void onPostExecute(Object[] objects) {
                                            if(((Integer) objects[0]) == 2) {
                                                Intent intent = new Intent(Login.this, MainActivity.class);
                                                startActivity(intent);
                                                if (!((Boolean) objects[1]))
                                                    Toast.makeText(getApplicationContext(), "Nie można ustalić uprawnień." +
                                                            "\nSpróbuj ponownie później.", Toast.LENGTH_LONG).show();

                                                finish();
                                            }
                                        }
                                    }).execute();
                                }
                            }

                            @Override
                            protected void onProgressUpdate(Integer... values) {
                                if(values[0] == 1){
                                    textProgress.setText("Logowanie...");
                                    animator.setDisplayedChild(0);
                                }
                            }
                        }).execute();
                    }


                }
            }
        });
    }

}

