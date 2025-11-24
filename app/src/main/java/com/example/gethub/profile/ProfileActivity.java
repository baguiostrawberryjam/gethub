// File: com.example.gethub.profile.ProfileActivity.java
package com.example.gethub.profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gethub.R;
import com.example.gethub.auth.LoginActivity;
import com.example.gethub.databinding.ActivityProfileBinding; // NEW BINDING
import com.example.gethub.models.User;

public class ProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "extra_logged_in_user";
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );


        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        User user = getIntent().getParcelableExtra(EXTRA_USER);

        if (user == null) {
            Toast.makeText(this, "Error: User data missing.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupHeader();
        displayProfileData(user);
        setupLogoutButton();

        // FUTURE: Setup click handlers for Edit/Change Password/Settings
    }

    private void setupHeader() {
        // Back Button: Finishes the current activity and returns to the previous screen.
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void displayProfileData(User user) {
        // Full Name, Student ID, Email, Course/Program, Campus Branch, Contact Number, Address

        binding.tvFullName.setText(String.format("%s %s %s", user.getFirstName(), user.getMiddleName(), user.getLastName()));
        binding.tvStudentIDValue.setText(user.getStudentId());
        binding.tvEmailValue.setText(user.getEmail());
        binding.tvCourseValue.setText(user.getCourseProgram());
        binding.tvCampusValue.setText(user.getCampusBranch());
        binding.tvContactValue.setText(user.getContactNumber());
        binding.tvAddressValue.setText(String.format("%s %s, %s, %s, %s",
                user.getAddressNo(), user.getAddressStreet(), user.getAddressBarangay(),
                user.getAddressCity(), user.getAddressProvince()));

        byte[] profileImage = user.getUserImage();
        if (profileImage != null && profileImage.length > 0) {
            Bitmap bitmap = ImageConverter.toBitmap(profileImage);
            binding.ivProfilePicture.setImageBitmap(bitmap);
        } else {
            binding.ivProfilePicture.setImageResource(R.drawable.bg_prof_circular);
        }
        // Assuming binding includes an ImageView for the profile picture (imgProfile)
        // You would load the image based on user.getProfilePictureUrl() from the ProfileSettings Model
        // For now, this just displays the textual data.
    }

    private void setupLogoutButton() {
        binding.btnLogout.setOnClickListener(v -> {
            // Show a confirmation dialog before logging out
            new AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Log Out", (dialog, which) -> {
                        // User confirmed, execute the logout
                        performLogout();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // User canceled, dismiss the dialog
                        dialog.dismiss();
                    })
                    .show();
        });
    }
    private void performLogout() {
        // In a real app, you would clear SharedPreferences, tokens, or database sessions here.
        // For our "training wheels" system, we just navigate and clear the stack.

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

        // --- CRITICAL ---
        // These flags clear the entire "back stack" (Home, Profile, etc.)
        // and make LoginActivity the new root.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish(); // Close the ProfileActivity
    }
}