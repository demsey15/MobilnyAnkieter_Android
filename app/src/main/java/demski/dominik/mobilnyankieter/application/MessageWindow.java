package demski.dominik.mobilnyankieter.application;

import android.app.AlertDialog;
import android.content.Context;

import demski.dominik.mobilnyankieter.R;

/**
 * Created by Dominik on 2015-12-16.
 */
public class MessageWindow {
    public static void showInfoMessage(Context context, String title, String message){
        (new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.ok, null))
                .show();
    }

    public static void showHelpMessage(Context context, String title, CharSequence message){
        (new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_menu_help)
                .setPositiveButton(android.R.string.ok, null))
                .show();
    }
}
