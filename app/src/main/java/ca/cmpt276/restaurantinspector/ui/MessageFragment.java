package ca.cmpt276.restaurantinspector.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import ca.cmpt276.restaurantinspector.R;

/**
 * MessageFragment is the dialog that pops up if update available
 * Code from Dr. Brian Fraser
 */
public class MessageFragment extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create the view
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.win_message_layout, null);

        // Create a button Listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:

                        break;
                    case DialogInterface.BUTTON_NEUTRAL:

                        break;
                }
            }
        };

        // Build the alert dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Congrats, you win!")
                .setView(v)

                .setNegativeButton(android.R.string.cancel, listener)
                .create();

    }
}
