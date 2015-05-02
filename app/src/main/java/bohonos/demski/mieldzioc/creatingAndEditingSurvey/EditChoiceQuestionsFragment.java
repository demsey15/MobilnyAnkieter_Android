package bohonos.demski.mieldzioc.creatingAndEditingSurvey;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import bohonos.demski.mieldzioc.controls.CreatingSurveyControl;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditChoiceQuestionsFragment extends Fragment {

    private AnswersAdapter answersAdapter;
    private  List<String> answers = new ArrayList<>();

    public EditChoiceQuestionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_choice_questions, container, false);
        ListView list = (ListView) view.findViewById(R.id.answers_list);
        answers = CreatingSurveyControl.getInstance().getAnswersAsStringList(getArguments().getInt("QUESTION_NUMBER"));
        answersAdapter = new AnswersAdapter(this.getActivity(), answers);
        list.setAdapter(answersAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int length = answersAdapter.getCount();
                if(position == length - 1){
                    Log.i("LISTA_ODPOWIEDZI", "Kliknięto " + position);
                    answers.add(length - 2, new String(""));
                    answersAdapter.notifyDataSetChanged();
                }
                else Log.i("LISTA_ODPOWIEDZI", "Kliknięto nie ostatni, ale " + position);
            }
        });
        return view;
    }
}
