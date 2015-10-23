package bohonos.demski.mieldzioc.mobilnyankieter.surveytemplates.creatingandeditingsurvey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import bohonos.demski.mieldzioc.mobilnyankieter.R;
import bohonos.demski.mieldzioc.mobilnyankieter.questions.Question;

/**
 * Created by Dominik on 2015-04-09.
 * Dialog służący do wyboru typu pytania.
 */
public class ChoosingQuestionType extends DialogFragment {
    private OnQuestionTypeChosenListener activityListener;



    // Container Activity must implement this interface
    public interface OnQuestionTypeChosenListener{
        void onQuestionTypeChosen(int questionType);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            activityListener = (OnQuestionTypeChosenListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " musi implementować OnQuestionTypeChosenListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_quest_type_dialog_title)
                .setItems(R.array.type_of_questions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                activityListener.onQuestionTypeChosen(Question.ONE_CHOICE_QUESTION);
                                break;
                            case 1:
                                activityListener.onQuestionTypeChosen(Question.MULTIPLE_CHOICE_QUESTION);
                                break;
                            case 2:
                                activityListener.onQuestionTypeChosen(Question.SCALE_QUESTION);
                                break;
                            case 3:
                                activityListener.onQuestionTypeChosen(Question.GRID_QUESTION);
                                break;
                            case 4:
                                activityListener.onQuestionTypeChosen(Question.TEXT_QUESTION);
                                break;
                            case 5:
                                activityListener.onQuestionTypeChosen(Question.TEXT_QUESTION);
                                break;
                            case 6:
                                activityListener.onQuestionTypeChosen(Question.DROP_DOWN_QUESTION);
                                break;
                            case 7:
                                activityListener.onQuestionTypeChosen(Question.DATE_QUESTION);
                                break;
                            case 8:
                                activityListener.onQuestionTypeChosen(Question.TIME_QUESTION);
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
