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
 * DownloadingDialog is the dialog that pops up during download
 * Code from Dr. Brian Fraser
 */
public class DownloadingDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Create the view
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.downloading_dialog_layout, null);

        // Create a button Listener
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // cancel
                ((RestaurantListActivity)(DownloadingDialog.this.getActivity())).onCancelDownloadDialogPressed();
            }
        };

        // Build the alert dialog
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setNegativeButton(android.R.string.no, listener)
                .create();

    }
}
