package bohonos.demski.mieldzioc.myControls;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import bohonos.demski.mieldzioc.application.DateAndTimeService;

/**
 * Created by Dominik on 2015-05-07.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Bundle bundle = getArguments();
        if(bundle != null){
            EditText editText = (EditText) getActivity().findViewById((Integer)
                    getArguments().getInt("EDIT_TEXT"));
            editText.setText(DateAndTimeService.addFirstZeros(day) + "-" +
                    DateAndTimeService.addFirstZeros(month + 1) + "-" + year);
        }

    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idEditText id edit texta, do którego ma zostać wpisana data
     * @return A new instance of fragment DatePickerFragment.
     */
    public static DatePickerFragment newInstance(int idEditText) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable("EDIT_TEXT", idEditText);
        fragment.setArguments(args);
        return fragment;
    }

    public DatePickerFragment() {
        // Required empty public constructor
    }

}
