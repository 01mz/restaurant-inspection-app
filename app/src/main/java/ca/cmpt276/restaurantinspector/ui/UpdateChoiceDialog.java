package ca.cmpt276.restaurantinspector.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import ca.cmpt276.restaurantinspector.R;

/**
 * UpdateChoiceDialog is the dialog that pops up if an update available to ask if the user wants to update.
 * User can choose yes or no.
 * Code from Dr. Brian Fraser
 */
public class UpdateChoiceDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Create a button Listener
        DialogInterface.OnClickListener listener = (dialog, which) -> {
            switch(which){
                case DialogInterface.BUTTON_POSITIVE:
                    ((RestaurantListActivity)(UpdateChoiceDialog.this.getActivity())).onUpdateDialogPressed(true);
                    dismiss();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    ((RestaurantListActivity)(UpdateChoiceDialog.this.getActivity())).onUpdateDialogPressed(false);
                    dismiss();
                    break;
            }
        };

        // Build the alert dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.update_available)
                .setMessage(R.string.update_question)
                //.setView(v)

                .setNegativeButton(R.string.no, listener)
                .setPositiveButton(R.string.yes, listener)
                .create();

    }
}
