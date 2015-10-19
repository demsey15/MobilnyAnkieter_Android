package bohonos.demski.mieldzioc.mobilnyankieter.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ViewAnimator;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.creatingandeditingsurvey.CreateNewSurvey;
import bohonos.demski.mieldzioc.mobilnyankieter.fillingSurvey.ChooseSurveyToFillActivity;

/**
 * Aktywność z różnymi akcjami do wyboru.
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareSendFilledSurveysButton();

        Button newSurveyButt = (Button) findViewById(R.id.new_survey_button);

        newSurveyButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNewSurvey.class);
                startActivity(intent);
            }
        });


        Button sendSurveyButton = (Button) findViewById(R.id.send_survey_button);

        sendSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button fillSurveyButton = (Button) findViewById(R.id.fill_survey_button);
        fillSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseSurveyToFillActivity.class);
                startActivity(intent);
            }
        });
    }

    private void prepareSendFilledSurveysButton() {
        Button sendFilledSurveysButton = (Button) findViewById(R.id.send_filled_survey_button);
        final ViewAnimator animator = (ViewAnimator) findViewById(R.id.main_animator);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.sending_survey_progress);
    }
}
