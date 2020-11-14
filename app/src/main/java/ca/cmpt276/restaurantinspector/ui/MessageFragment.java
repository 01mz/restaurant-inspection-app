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
 * MessageFragment is the dialog that pops up after winning. After the dialog is clicked the user
 * returns to the Main Menu.
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
        DialogInterface.OnClickListener listener = (dialog, which) -> requireActivity().finish();

        // Build the alert dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Congrats, you win!")
                .setView(v)
                .setPositiveButton(android.R.string.ok, listener)
                .create();

    }
}
