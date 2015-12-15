package demski.dominik.mobilnyankieter.sendingsurvey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import demski.dominik.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;
import demski.dominik.mobilnyankieter.database.DataBaseAdapter;

/**
 * Created by Dominik on 2015-05-19.
 */
public class SendingNotSentSurveyAdapter extends BaseAdapter{
    private Context context;
    private Map<Survey, Boolean> surveys;
    private List<Survey> surveyList;

    public SendingNotSentSurveyAdapter(Context context) {
        this.context = context;
        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);

        surveys = dataBaseAdapter.getAllSurveyTemplatesWithSendStatus();
        surveyList = new ArrayList<>(surveys.keySet());
    }

    @Override
    public int getCount() {
        return surveyList.size();
    }

    @Override
    public Object getItem(int position) {
        return surveyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View surveyView;

        surveyView = (convertView == null) ? (LayoutInflater.from(context).
                inflate(R.layout.survey_row, parent, false)) : convertView;

        Survey survey = (Survey) getItem(position);

        TextView surveyTitle = (TextView) surveyView.findViewById(R.id.survey_row_text);
        surveyTitle.setText(survey.getTitle());

        if(surveys.get(survey)){
            surveyView.setBackgroundColor(context.getResources().getColor(R.color.sent_button));
        }

        return surveyView;
    }
}
