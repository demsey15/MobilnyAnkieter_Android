package bohonos.demski.mieldzioc.mobilnyankieter.creatingandeditingsurvey;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import bohonos.demski.mieldzioc.constraints.IConstraint;
import bohonos.demski.mieldzioc.constraints.TextConstraint;
import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.questions.TextQuestion;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TextConstraintsFragment.OnTextFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class TextConstraintsFragment extends Fragment {

    private OnTextFragmentInteractionListener mListener;
    private EditText minLengthEdit;
    private EditText maxLengthEdit;
    private EditText regexEdit;


    public TextConstraintsFragment() {
        // Required empty public constructor
    }

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text_constraints, container, false);

        minLengthEdit = (EditText) view.findViewById(R.id.min_length_edit);
        maxLengthEdit = (EditText) view.findViewById(R.id.max_length_edit);
        regexEdit = (EditText) view.findViewById(R.id.regex_edit);

        TextQuestion question = (TextQuestion) getArguments().getSerializable("QUESTION");
        IConstraint constraint = question.getConstraint();
        if(constraint != null) {
            if(constraint instanceof TextConstraint) {
                TextConstraint txtCon = (TextConstraint) constraint;
                Integer minLength = txtCon.getMinLength();
                Integer maxLength = txtCon.getMaxLength();
                String regex = (txtCon.getRegex() == null)? null : txtCon.getRegex().toString();
                if (minLength != null)
                    minLengthEdit.setText(String.valueOf(minLength));
                if (maxLength != null)
                    maxLengthEdit.setText(String.valueOf(maxLength));
                if (regex != null)
                    regexEdit.setText(regex);
            }
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTextFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public Integer getMinLength(){
        return (minLengthEdit.getText().toString().equals(""))? null : (Integer.valueOf(minLengthEdit
        .getText().toString()));
    }

    public Integer getMaxLength(){
        return (maxLengthEdit.getText().toString().equals(""))? null : (Integer.valueOf(maxLengthEdit
                .getText().toString()));
    }

    public String getRegex(){
        return (regexEdit.getText().toString().equals(""))? null : (regexEdit
                .getText().toString());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTextFragmentInteractionListener {
        void onTextFragmentInteraction(int minLength, int maxLength, String regex);
    }

}
