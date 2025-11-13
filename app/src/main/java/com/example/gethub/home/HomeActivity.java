// File: com.example.gethub.home.HomeActivity.java
package com.example.gethub.home;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.gethub.R;
import com.example.gethub.models.User;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "extra_user_data"; // Key for Intent Extra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Assuming you create a layout file named 'activity_home.xml'
        setContentView(R.layout.activity_home);

        // Use R.id.tv_welcome from your layout file
        TextView tvWelcome = findViewById(R.id.tv_welcome);

        // 1. Get the Parcelable User object from the Intent
        if (getIntent().hasExtra(EXTRA_USER)) {
            User loggedInUser = getIntent().getParcelableExtra(EXTRA_USER);

            if (loggedInUser != null && tvWelcome != null) {
                // 2. Display the personalized welcome message
                String firstName = loggedInUser.getFirstName();
                tvWelcome.setText("Welcome, " + firstName + "!");
            }
        } else if (tvWelcome != null) {
            tvWelcome.setText("Welcome to the Dashboard!");
        }
    }
}