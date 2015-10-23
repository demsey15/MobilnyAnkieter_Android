package bohonos.demski.mieldzioc.mobilnyankieter.surveytemplates;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey.SendSurveysTemplateActivity;

public class SurveyTemplateActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_template);

        prepareSendSurveyTemplatesButton();
    }

    private void prepareSendSurveyTemplatesButton() {
        Button sendSurveyTemplatesButton = (Button) findViewById(R.id.send_surveys_template_button);
        sendSurveyTemplatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SurveyTemplateActivity.this, SendSurveysTemplateActivity.class);

                startActivity(intent);
            }
        });
    }

}
