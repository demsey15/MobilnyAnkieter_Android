package bohonos.demski.mieldzioc.mobilnyankieter.sendingsurvey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.survey.Survey;

/**
 * Created by Dominik on 2015-10-24.
 */
public class SendingSurveyAnswersAdapter extends BaseAdapter {
    private List<Survey> surveys;
    private Map<String, List<Survey>> surveysMap;
    private boolean[] wasClicked;

    private Context context;

    public SendingSurveyAnswersAdapter(Map<String, List<Survey>> surveysMap, Context context) {
        this.surveysMap = surveysMap;

        surveys = new ArrayList<>();

        for(List<Survey> list : surveysMap.values()){
            if(!list.isEmpty()){
                surveys.add(list.get(0));
            }
        }

        this.context = context;

        wasClicked = new boolean[surveys.size()];
    }

    @Override
    public int getCount() {
        return surveys.size();
    }

    @Override
    public Object getItem(int position) {
        return surveys.get(position);
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

        int amountOfSurveysWithThisId = surveysMap.get(survey.getIdOfSurveys()).size();

        surveyTitle.setText(survey.getTitle() + " (" + amountOfSurveysWithThisId + ")");

        if(wasClicked[position]){
            surveyView.setBackgroundColor(context.getResources().getColor(R.color.sent_button));
        }

        return surveyView;
    }

    public void setSurveyWasClicked(int position){
        wasClicked[position] = true;
    }
}
