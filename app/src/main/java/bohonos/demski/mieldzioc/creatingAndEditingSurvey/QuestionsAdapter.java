package bohonos.demski.mieldzioc.creatingAndEditingSurvey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import bohonos.demski.mieldzioc.controls.CreatingSurveyControl;
import bohonos.demski.mieldzioc.questions.Question;

/**
 * Created by Dominik Demski on 2015-04-09.
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
/*
    private int getLayoutId(int position){
        int questionType = control.getQuestionType(position);
        if(questionType == Question.ONE_CHOICE_QUESTION || questionType == Question.DROP_DOWN_QUESTION
            || questionType == Question.MULTIPLE_CHOICE_QUESTION)
            return R.layout.create_new_choice_question;
        else if(questionType == Question.DATE_QUESTION || questionType == Question.TIME_QUESTION)
            return R.layout.create_date_time_question;
        else if(questionType == Question.GRID_QUESTION)
            return R.layout.create_new_grid_question;
        else if(questionType == Question.SCALE_QUESTION)
            return R.layout.create_new_scale_question;
        else if(questionType == Question.TEXT_QUESTION)
            return R.layout.create_new_text_question;
        else return 0;
    }
    */

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
                questionPhoto.setImageResource(R.drawable.lista_rozwijana);
                break;
            case Question.ONE_CHOICE_QUESTION:
                questionPhoto.setImageResource(R.drawable.one_choice);
                break;
            case Question.SCALE_QUESTION:
                questionPhoto.setImageResource(R.drawable.skala);
                break;
            case Question.GRID_QUESTION:
                questionPhoto.setImageResource(R.drawable.siatka);
                break;
            case Question.TEXT_QUESTION:
                questionPhoto.setImageResource(R.drawable.long_text);
                break;
            case Question.MULTIPLE_CHOICE_QUESTION:
                questionPhoto.setImageResource(R.drawable.multiple_choice);
                break;
            case Question.DATE_QUESTION:
                questionPhoto.setImageResource(R.drawable.data);
                break;
            case Question.TIME_QUESTION:
                questionPhoto.setImageResource(R.drawable.godzina);
                break;
        }

            TextView questionLabel = (TextView) questionView.findViewById(R.id.question_text_row);
            questionLabel.setText(question.getQuestion());
        }
    }

