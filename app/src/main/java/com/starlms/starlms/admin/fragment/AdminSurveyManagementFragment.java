package com.starlms.starlms.admin.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.starlms.starlms.R;
import com.starlms.starlms.adapter.AdminSurveyListPagerAdapter;

public class AdminSurveyManagementFragment extends Fragment implements AdminSurveyListFragment.SurveyListListener {

    private ViewPager2 viewPager;
    private AdminSurveyListPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_survey_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_survey_management);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        TabLayout tabLayout = view.findViewById(R.id.tab_layout_surveys);
        viewPager = view.findViewById(R.id.view_pager_surveys);

        setupViewPager(tabLayout);
    }

    private void setupViewPager(TabLayout tabLayout) {
        pagerAdapter = new AdminSurveyListPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Chưa đăng");
                            break;
                        case 1:
                            tab.setText("Đã đăng");
                            break;
                    }
                }
        ).attach();
    }

    @Override
    public void onSurveyDataChanged() {
        int currentItem = viewPager.getCurrentItem();
        pagerAdapter = new AdminSurveyListPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentItem, false);
    }

    @Override
    public void onShowStatistics(int surveyId, String surveyTitle) {
        // Khi nhận được tín hiệu, chuyển sang màn hình thống kê
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, AdminSurveyStatisticsFragment.newInstance(surveyId, surveyTitle));
        transaction.addToBackStack(null); // Để có thể nhấn back
        transaction.commit();
    }
}
