package ca.cmpt276.restaurantinspector.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * UpdateChoiceDialog is the dialog that pops up if an update available
 * Code from Dr. Brian Fraser
 */
public class UpdateChoiceDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        // Create the view
//        View v = LayoutInflater.from(getActivity())
//                .inflate(R.layout.win_message_layout, null);

        // Create a button Listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
            }
        };

        // Build the alert dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Update available.")
                .setMessage("Would you like to update?")
                //.setView(v)

                .setNegativeButton("No", listener)
                .setPositiveButton("Yes", listener)
                .create();

    }
}
