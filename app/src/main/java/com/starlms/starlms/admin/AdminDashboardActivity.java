package com.starlms.starlms.admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.starlms.starlms.R;
import com.starlms.starlms.admin.fragment.AdminDashboardFragment;
import com.starlms.starlms.admin.fragment.AdminFeaturesFragment;
import com.starlms.starlms.admin.fragment.AdminTeacherManagementFragment;

public class AdminDashboardActivity extends AppCompatActivity implements AdminFeaturesFragment.OnFeatureClickListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.admin_navigation_dashboard) {
                selectedFragment = new AdminDashboardFragment();
            } else if (itemId == R.id.admin_navigation_features) {
                selectedFragment = new AdminFeaturesFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, true);
            }
            return true;
        });

        // Load the default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.admin_navigation_dashboard);
        }
    }

    private void loadFragment(Fragment fragment, boolean isInitial) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        // Add to back stack only if it's not the initial fragments loaded by bottom nav
        if (!isInitial) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onTeacherManagementClicked() {
        // Khi nhận được tín hiệu từ AdminFeaturesFragment, chuyển sang AdminTeacherManagementFragment
        loadFragment(new AdminTeacherManagementFragment(), false);
    }
}
