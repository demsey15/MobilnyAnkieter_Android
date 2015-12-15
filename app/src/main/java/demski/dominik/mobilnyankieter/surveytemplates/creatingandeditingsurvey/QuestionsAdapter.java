package demski.dominik.mobilnyankieter.surveytemplates.creatingandeditingsurvey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import demski.dominik.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.controls.CreatingSurveyControl;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;

/**
 * Created by Dominik Demski on 2015-04-09.
 *
 * Adapter listy pytan podczas tworzenia nowej ankiety.
 */
public class QuestionsAdapter extends BaseAdapter {

    private CreatingSurveyControl control = CreatingSurveyControl.getInstance();
    private Context context;

    public QuestionsAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return control.getQuestionsCount();
    }

    @Override
    public Object getItem(int position) {
        return control.getQuestion(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View questionView;
        if (convertView == null) {
            questionView = LayoutInflater.from(context).inflate(R.layout.creating_questions_row, parent, false);
        } else {
            questionView = convertView;
       }
        bindQuestionToView((Question)getItem(position), questionView);
        return questionView;
    }

    private void bindQuestionToView(Question question, View questionView) {
        ImageView questionPhoto = (ImageView) questionView.findViewById(R.id.question_photo);
        int questionType = question.getQuestionType();
        switch(questionType){
            case Question.DROP_DOWN_QUESTION:
                questionPhoto.setImageResource(R.drawable.dropdownlist);
                break;
            case Question.ONE_CHOICE_QUESTION:
                questionPhoto.setImageResource(R.drawable.onechoice);
                break;
            case Question.SCALE_QUESTION:
                questionPhoto.setImageResource(R.drawable.ruler);
                break;
            case Question.GRID_QUESTION:
                questionPhoto.setImageResource(R.drawable.grid);
                break;
            case Question.TEXT_QUESTION:
                questionPhoto.setImageResource(R.drawable.text);
                break;
            case Question.MULTIPLE_CHOICE_QUESTION:
                questionPhoto.setImageResource(R.drawable.multiplechoice);
                break;
            case Question.DATE_QUESTION:
                questionPhoto.setImageResource(R.drawable.calendar);
                break;
            case Question.TIME_QUESTION:
                questionPhoto.setImageResource(R.drawable.clock);
                break;
        }

            TextView questionLabel = (TextView) questionView.findViewById(R.id.question_text_row);
            questionLabel.setText(question.getQuestion());
        }
    }

