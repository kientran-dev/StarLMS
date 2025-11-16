package com.starlms.starlms.admin;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.starlms.starlms.R;
import com.starlms.starlms.admin.fragment.AdminCourseManagementFragment;
import com.starlms.starlms.admin.fragment.AdminDashboardFragment;
import com.starlms.starlms.admin.fragment.AdminFeaturesFragment;
import com.starlms.starlms.admin.fragment.AdminSurveyManagementFragment;
import com.starlms.starlms.admin.fragment.AdminTeacherManagementFragment;

public class AdminDashboardActivity extends AppCompatActivity implements AdminFeaturesFragment.OnFeatureClickListener {

    private BottomNavigationView bottomNavigationView;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        appBarLayout = findViewById(R.id.appbar);

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

        // Lắng nghe sự thay đổi của Back Stack để ẩn/hiện header và bottom menu
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackEntryCount > 0) {
                // Nếu có màn hình chi tiết, ẩn header và bottom menu
                appBarLayout.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                // Nếu quay về màn hình gốc, hiện lại header và bottom menu
                appBarLayout.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.admin_navigation_dashboard);
        }
    }

    private void loadFragment(Fragment fragment, boolean isInitial) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (!isInitial) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onTeacherManagementClicked() {
        loadFragment(new AdminTeacherManagementFragment(), false);
    }

    @Override
    public void onCourseManagementClicked() {
        loadFragment(new AdminCourseManagementFragment(), false);
    }

    @Override
    public void onSurveyManagementClicked() {
        loadFragment(new AdminSurveyManagementFragment(), false);
    }
}
