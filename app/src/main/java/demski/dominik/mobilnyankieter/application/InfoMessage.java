package demski.dominik.mobilnyankieter.application;

import android.app.AlertDialog;
import android.content.Context;

import demski.dominik.mobilnyankieter.R;

/**
 * Created by Dominik on 2015-12-16.
 */
public class InfoMessage {
    public static void showInfoMessage(Context context){
        (new AlertDialog.Builder(context)
                .setTitle("O aplikacji")
                .setMessage(R.string.app_info)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.ok, null))
                .show();
    }
}
