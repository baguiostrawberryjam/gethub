package com.example.gethub.auth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adapter for the ViewPager2 to manage the 5 registration pages (fragments).
 */
public class RegistrationPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 5;

    public RegistrationPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RegistrationPage1Fragment();
            case 1:
                return new RegistrationPage2Fragment(); // TODO: Create this fragment
            case 2:
                return new RegistrationPage3Fragment(); // TODO: Create this fragment
            case 3:
                return new RegistrationPage4Fragment(); // TODO: Create this fragment
            case 4:
                return new RegistrationPage5Fragment(); // TODO: Create this fragment
            default:
                // Should not happen, but safe fallback
                return new RegistrationPage1Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}