package bohonos.demski.mieldzioc.fillingSurvey;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;

public class SurveysSummary extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surveys_summary);

        TextView summaryTxt = (TextView) findViewById(R.id.summary_text);
        String summary = getIntent().getStringExtra("SURVEY_SUMMARY");
        if(summary != null && !summary.trim().equals(""))
            summaryTxt.setText(summary);
        else summaryTxt.setText("Dziêkujemy za wype³nienie ankiety!");
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
