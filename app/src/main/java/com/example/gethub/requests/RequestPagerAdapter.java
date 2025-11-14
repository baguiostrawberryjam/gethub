// File: com.example.gethub.requests.RequestPagerAdapter.java (Updated for 2 pages)
package com.example.gethub.requests;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RequestPagerAdapter extends FragmentStateAdapter {

    public RequestPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new RequestPage1Fragment(); // Delivery & Document
            case 1: return new RequestPage2Fragment(); // Purpose & Delivery Details (NEW LAST PAGE)
            default: throw new IllegalStateException("Invalid Request page position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // REDUCED from 3 to 2
    }
}