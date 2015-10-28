package bohonos.demski.mieldzioc.mobilnyankieter.filledsurveys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import bohonos.demski.mieldzioc.mobilnyankieter.R;

public class FilledSurveysActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filled_surveys);

        Button sendSurveyAnswersButton = (Button) findViewById(R.id.send_filled_surveys_button);
        sendSurveyAnswersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilledSurveysActivity.this, FilledSurveysActionsActivity.class);
                intent.putExtra(FilledSurveysActionsActivity.LIST_ACTION_MODE, FilledSurveysActionsActivity.SENDING_MODE);

                startActivity(intent);
            }
        });

        Button deleteSurveyAnswersButton = (Button) findViewById(R.id.delete_survey_answers_button);
        deleteSurveyAnswersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilledSurveysActivity.this, FilledSurveysActionsActivity.class);
                intent.putExtra(FilledSurveysActionsActivity.LIST_ACTION_MODE, FilledSurveysActionsActivity.DELETING_MODE);

                startActivity(intent);
            }
        });

        deleteSurveyAnswersButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((Button) view).setHint("podpowiedź");
                return false;
            }
        });
    }

}
