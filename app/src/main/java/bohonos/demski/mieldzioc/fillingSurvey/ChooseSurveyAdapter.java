package bohonos.demski.mieldzioc.fillingSurvey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

import bohonos.demski.mieldzioc.application.ApplicationState;
import bohonos.demski.mieldzioc.creatingAndEditingSurvey.R;
import bohonos.demski.mieldzioc.survey.Survey;
import bohonos.demski.mieldzioc.survey.SurveyHandler;

/**
 * Created by Dominik on 2015-05-05.
 */
public class ChooseSurveyAdapter extends BaseAdapter {
    private Context context;
    private List<Survey> surveys;

    public ChooseSurveyAdapter(Context context) {
        this.context = context;
        surveys = ApplicationState.getInstance(context).getSurveysTemplateControl().
                getSurveysWithId(SurveyHandler.ACTIVE);
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
