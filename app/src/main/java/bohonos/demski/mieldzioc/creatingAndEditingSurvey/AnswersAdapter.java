package bohonos.demski.mieldzioc.creatingAndEditingSurvey;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.List;

/**
 * Adapter zarządzający listą odpowiedzi do pytań wyboru podczas edycji odpowiedzi.
 * Created by Dominik Demski on 2015-04-09.
 */
public class AnswersAdapter extends BaseAdapter {

    private Context context;
    private List<String> answers;
   // private CreatingSurveyControl control = CreatingSurveyControl.getInstance();

    /**
     * Tworzy adapter obsługujący listę odpowiedzi dla pytań wyboru, podczas edycji pytań.
     * @param context
     * @param answers lista odpowiedzi, nie może być równa null!
     */
    public AnswersAdapter(Context context, List<String> answers){
        this.context = context;
        if(answers == null) throw new NullPointerException("Lista odpowiedzi nie może być nullem!");
        this.answers = answers;
        answers.add("Dodaj odpowiedź.");
    }

    @Override
    public int getCount() {
        return answers.size();
    }

    @Override
    public Object getItem(int position) {
        return answers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View answer;

        answer = (convertView == null) ? (new EditText(context)) : convertView;

        EditText answerText = (EditText) answer;
       if(position == getCount() - 1){
           answerText.setHint(R.string.add_answer_hint);
       }
       else {
           answerText.setText((String) getItem(position));
       }
        return answer;
    }
}
