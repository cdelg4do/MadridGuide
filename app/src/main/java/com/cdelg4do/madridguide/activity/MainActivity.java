package com.cdelg4do.madridguide.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.navigator.Navigator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This class represents the initial screen.
 */
public class MainActivity extends AppCompatActivity {

    // Reference to UI elements (bind with Butterknife)
    @BindView(R.id.activity_main_btn_shops)
    Button btnShops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind all views of this activity (with Butterknife)
        ButterKnife.bind(this);

        setupShopsButton();
    }


    // Auxiliary methods:

    private void setupShopsButton() {

        btnShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigator.navigateFromMainActivityToShopsActivity(MainActivity.this);
            }
        });
    }
}
