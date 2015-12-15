package demski.dominik.mobilnyankieter.filledsurveys.fillingSurvey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import demski.dominik.mobilnyankieter.R;
import demski.dominik.mobilnyankieter.client.MainActivity;

public class SurveysSummary extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveys_summary);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        TextView summaryTxt = (TextView) findViewById(R.id.summary_text);
        String summary = getIntent().getStringExtra("SURVEY_SUMMARY");
        if(summary != null && !summary.trim().equals("") && !summary.equals("null"))
            summaryTxt.setText(summary);
        else summaryTxt.setText("Dziękujemy za wypełnienie ankiety!");

        Button button = (Button) findViewById(R.id.back_to_menu_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SurveysSummary.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_long_text_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
