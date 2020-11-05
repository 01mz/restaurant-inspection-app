package ca.cmpt276.restaurantinspector.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ca.cmpt276.restaurantinspector.R;

public class SingleInspection extends AppCompatActivity {

    public static Intent makeLaunch(Context c) {
        return new Intent(c, ca.cmpt276.restaurantinspector.ui.SingleInspection.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_inspection);
    }
}