package com.starlms.starlms.admin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.starlms.starlms.R;

public class AdminFeaturesFragment extends Fragment {

    private OnFeatureClickListener mListener;

    // Interface để giao tiếp với Activity
    public interface OnFeatureClickListener {
        void onTeacherManagementClicked();
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

        // --- Tùy chỉnh từng ô chức năng tại đây ---

        // 1. Quản lý Giảng viên
        View featureTeacher = view.findViewById(R.id.feature_attendance); // ID của ô đầu tiên
        ((TextView) featureTeacher.findViewById(R.id.feature_name)).setText("QL Giảng viên");
        // ((ImageView) featureTeacher.findViewById(R.id.feature_icon)).setImageResource(R.drawable.ic_admin_teacher); // Ví dụ đổi icon
        featureTeacher.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onTeacherManagementClicked();
            }
        });

        // 2. Quản lý Điểm
        View featureGrades = view.findViewById(R.id.feature_grades);
        ((TextView) featureGrades.findViewById(R.id.feature_name)).setText("QL Điểm");

        // 3. Quản lý Nhiệm vụ
        View featureTasks = view.findViewById(R.id.feature_tasks);
        ((TextView) featureTasks.findViewById(R.id.feature_name)).setText("QL Nhiệm vụ");

        // 4. Quản lý Lịch trình
        View featureSchedule = view.findViewById(R.id.feature_schedule);
        ((TextView) featureSchedule.findViewById(R.id.feature_name)).setText("QL Lịch trình");

        // 5. Thông tin Sinh viên
        View featureStudentInfo = view.findViewById(R.id.feature_student_info);
        ((TextView) featureStudentInfo.findViewById(R.id.feature_name)).setText("TT Sinh viên");

        // 6. Khảo sát
        View featureSurvey = view.findViewById(R.id.feature_survey);
        ((TextView) featureSurvey.findViewById(R.id.feature_name)).setText("Khảo sát");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
