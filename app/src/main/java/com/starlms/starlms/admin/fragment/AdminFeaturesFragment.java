package com.starlms.starlms.admin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.starlms.starlms.R;

public class AdminFeaturesFragment extends Fragment {

    private OnFeatureClickListener mListener;

    public interface OnFeatureClickListener {
        void onTeacherManagementClicked();
        void onCourseManagementClicked();
        void onSurveyManagementClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFeatureClickListener) {
            mListener = (OnFeatureClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFeatureClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_features, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Quản lý Giảng viên
        View featureTeacher = view.findViewById(R.id.feature_attendance);
        ((TextView) featureTeacher.findViewById(R.id.feature_name)).setText("QL Giảng viên");
        featureTeacher.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onTeacherManagementClicked();
            }
        });

        // 2. Quản lý Khóa học
        View featureCourses = view.findViewById(R.id.feature_grades);
        ((TextView) featureCourses.findViewById(R.id.feature_name)).setText("QL Khóa học");
        featureCourses.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onCourseManagementClicked();
            }
        });

        // 6. Quản lý Khảo sát
        View featureSurvey = view.findViewById(R.id.feature_survey);
        ((TextView) featureSurvey.findViewById(R.id.feature_name)).setText("QL Khảo sát");
        featureSurvey.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSurveyManagementClicked();
            }
        });

        // ... Các ô chức năng khác
        View featureTasks = view.findViewById(R.id.feature_tasks);
        ((TextView) featureTasks.findViewById(R.id.feature_name)).setText("QL Nhiệm vụ");

        View featureSchedule = view.findViewById(R.id.feature_schedule);
        ((TextView) featureSchedule.findViewById(R.id.feature_name)).setText("QL Lịch trình");

        View featureStudentInfo = view.findViewById(R.id.feature_student_info);
        ((TextView) featureStudentInfo.findViewById(R.id.feature_name)).setText("TT Sinh viên");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
