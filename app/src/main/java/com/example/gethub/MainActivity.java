package com.example.gethub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat; // NEW IMPORT
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View; // NEW IMPORT

import com.example.gethub.auth.LoginActivity;
import com.example.gethub.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Use a Handler to delay the transition to the Login Activity
        // This simulates a splash screen loading or initial setup
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);

            // --- START: Transition Code ---

            // 1. Get a reference to the logo view from the binding object
            View sharedView = binding.imgGethub;

            // 2. Create the Bundle for the shared element transition
            // The third parameter must match the transitionName in the XML ("app_logo_transition")
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    MainActivity.this,
                    sharedView,
                    "app_logo_transition"
            );

            // 3. Start the next activity using the options bundle
            startActivity(intent, options.toBundle());

            // --- END: Transition Code ---

            // Finish this activity so the user cannot navigate back to the splash screen
            finish();
        }, 1500); // Wait for 1.5 seconds
    }
}