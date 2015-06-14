package bohonos.demski.mieldzioc.creatingAndEditingSurvey;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import bohonos.demski.mieldzioc.constraints.IConstraint;
import bohonos.demski.mieldzioc.constraints.NumberConstraint;
import bohonos.demski.mieldzioc.questions.TextQuestion;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NumberConstraintsFragment.OnNumberFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {} factory method to
 * create an instance of this fragment.
 *
 * Fragment ustalaj¹cy ograniczenia tekstowe w pytaniu typu tekstowego.
 */
public class NumberConstraintsFragment extends Fragment {
    private OnNumberFragmentInteractionListener mListener;
    private EditText minValueEdit;
    private EditText maxValueEdit;
    private EditText notEqualsEdit;
    private CheckBox mustBeIntegerCheckBox;
    private CheckBox notBetweenCheckBox;

    public NumberConstraintsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_constraints, container, false);

        minValueEdit = (EditText) view.findViewById(R.id.min_value_edit);
        maxValueEdit = (EditText) view.findViewById(R.id.max_value_edit);
        notEqualsEdit = (EditText) view.findViewById(R.id.not_equals_edit);
        notBetweenCheckBox = (CheckBox) view.findViewById(R.id.not_between_check_box);
        mustBeIntegerCheckBox = (CheckBox) view.findViewById(R.id.must_be_integer_check_box);

        TextQuestion question = (TextQuestion) getArguments().getSerializable("QUESTION");
        IConstraint constraint = question.getConstraint();
        if(constraint != null) {
            if(constraint instanceof NumberConstraint) {
                NumberConstraint numCon = (NumberConstraint) constraint;
                Double minValue = numCon.getMinValue();
                Double maxValue = numCon.getMaxValue();
                Double notEquals = numCon.getNotEquals();
                Boolean notBetween = numCon.isNotBetweenMaxAndMinValue();
                Boolean mustBeInteger = numCon.isMustBeInteger();
                if (minValue != null)
                    minValueEdit.setText(String.valueOf(minValue));
                if (maxValue != null)
                    maxValueEdit.setText(String.valueOf(maxValue));
                if (notEquals != null)
                   notEqualsEdit.setText(String.valueOf(notEquals));
                if(notBetween != null)
                    notBetweenCheckBox.setChecked(notBetween);
                if(mustBeInteger != null)
                    mustBeIntegerCheckBox.setChecked(mustBeInteger);
            }
        }
        return view;
    }

    public Double getMinValue(){
        String value;
        return ((value = minValueEdit.getText().toString()).equals("")) ? null : Double.valueOf(value);
    }

    public Double getMaxValue(){
        String value;
        return ((value = maxValueEdit.getText().toString()).equals("")) ? null : Double.valueOf(value);
    }

    public Double getNotEquals(){
        String value;
        return ((value = notEqualsEdit.getText().toString()).equals("")) ? null : Double.valueOf(value);
    }

    public boolean isMustBeInteger(){
        return mustBeIntegerCheckBox.isChecked();
    }

    public boolean isNotBetween(){
        return notBetweenCheckBox.isChecked();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNumberFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
    public interface OnNumberFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onNumberFragmentInteraction(double minValue, double maxValue, boolean mustBeInteger, double notEquals,
                                                boolean notBetweenMaxAndMinValue);
    }

}
