package bohonos.demski.mieldzioc.sendingSurvey;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.dataBase.DataBaseAdapter;
import bohonos.demski.mieldzioc.survey.Survey;

/**
 * Created by Dominik on 2015-05-19.
 */
public class SendingNotSentSurveyAdapter extends BaseAdapter{
    private Context context;
    private List<Survey> surveys;

    public SendingNotSentSurveyAdapter(Context context) {
        this.context = context;
        DataBaseAdapter dataBaseAdapter = new DataBaseAdapter(context);
        surveys = dataBaseAdapter.getNotSentSurveysTemplateCreatedByInterviewer
                (ApplicationState.getInstance(context).getLoggedInterviewer());
        Log.d("ADAPTER_WYSYLANIE", "Pobralem: " + surveys.size() + " ankiet");
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
        View survey;

        survey = (convertView == null) ? (LayoutInflater.from(context).
                inflate(R.layout.survey_row, parent, false)) : convertView;

        TextView surveyTitle = (TextView) survey.findViewById(R.id.survey_row_text);
        surveyTitle.setText(((Survey) getItem(position)).getTitle());
        return survey;
    }
}
