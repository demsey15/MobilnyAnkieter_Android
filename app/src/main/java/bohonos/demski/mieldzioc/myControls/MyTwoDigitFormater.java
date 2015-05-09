package bohonos.demski.mieldzioc.myControls;

import android.widget.NumberPicker;

/**
 * Created by Dominik on 2015-05-07.
 */
public class MyTwoDigitFormater implements NumberPicker.Formatter {

    @Override
    public String format(int value) {
        return String.format("%02d", value);
    }
}
