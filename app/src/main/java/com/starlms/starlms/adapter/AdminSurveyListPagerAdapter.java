package com.starlms.starlms.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
// import androidx.fragment.app.FragmentActivity; // Không cần nữa
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.starlms.starlms.admin.fragment.AdminSurveyListFragment;

public class AdminSurveyListPagerAdapter extends FragmentStateAdapter {

    // SỬA Ở ĐÂY: Chấp nhận một Fragment thay vì FragmentActivity
    public AdminSurveyListPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return AdminSurveyListFragment.newInstance("Chưa đăng");
            case 1:
                return AdminSurveyListFragment.newInstance("Đã đăng");
            default:
                // Should not happen
                return AdminSurveyListFragment.newInstance("Chưa đăng");
        }
    }

    @Override
    public int getItemCount() {
        return 2; // We have 2 tabs
    }
}
