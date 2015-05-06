package bohonos.demski.mieldzioc.creatingAndEditingSurvey;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditChoiceAnswersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditChoiceAnswersFragment extends Fragment {

    private List<EditText> editTexts = new ArrayList<>();
    private LinearLayout linearLayout;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param answersList lista odpowiedzi do danego pytania
     * @return A new instance of fragment EditChoiceAnswersFragment.
     */
    public static EditChoiceAnswersFragment newInstance(ArrayList<String> answersList) {
        EditChoiceAnswersFragment fragment = new EditChoiceAnswersFragment();
        Bundle args = new Bundle();
        args.putSerializable("ANSWERS_LIST", answersList);
        fragment.setArguments(args);
        return fragment;
    }

    public EditChoiceAnswersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        List<String> answersTemp = (List<String>) getArguments().getSerializable("ANSWERS_LIST");
            if(answersTemp != null){
                for(String answer : answersTemp){
                    createNewEditText(answer);
                }
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_choice_answers, container, false);

        linearLayout = (LinearLayout) view.findViewById(R.id.answers_choice_list);

        for(EditText editTxt : editTexts){
            linearLayout.addView(editTxt);
        }

        //po kliknięciu na przycisk, dodaj nowe pole do dodawania pytań
        EditText clickToAddAnswerEdit = (EditText) view.findViewById(R.id.click_to_add_answer_edit);
        clickToAddAnswerEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText answer = createNewEditText("");
                linearLayout.addView(answer);
            }
        });
        return view;
    }

    /**
     * Tworzy nowy editText i dodaje go do listy editTexts.
     * @param text tekst
     * @return utworzony editText
     */
    private EditText createNewEditText(String text){
        EditText answ = new EditText(getActivity());
        answ.setText(text);
        answ.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        answ.setId(GenerateId.generateViewId());
        editTexts.add(answ);
        return answ;
    }

    public List<String> getAnswers(){
        List<String> answers = new ArrayList<>(editTexts.size());
        for(EditText editText : editTexts){
            answers.add(editText.getText().toString());
        }
        return answers;
    }
}
