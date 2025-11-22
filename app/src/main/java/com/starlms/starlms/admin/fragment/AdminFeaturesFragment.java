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

    public interface OnFeatureClickListener {
        void onTeacherManagementClicked();
        void onCourseManagementClicked();
        void onSurveyManagementClicked();
        void onLeaveManagementClicked();
        void onScheduleManagementClicked();
        void onAttendanceManagementClicked();
        void onStudentManagementClicked();
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
        setupFeature(view.findViewById(R.id.feature_teacher_management), R.drawable.ic_supervisor_account, "QL Giảng viên", () -> {
            if (mListener != null) mListener.onTeacherManagementClicked();
        });

        // 2. Quản lý Khóa học
        setupFeature(view.findViewById(R.id.feature_course_management), R.drawable.ic_school, "QL Khóa học", () -> {
            if (mListener != null) mListener.onCourseManagementClicked();
        });

        // 3. Quản lý Lịch học
        setupFeature(view.findViewById(R.id.feature_schedule_management), R.drawable.ic_event, "QL Lịch học", () -> {
            if (mListener != null) mListener.onScheduleManagementClicked();
        });

        // 4. Quản lý Đơn nghỉ
        setupFeature(view.findViewById(R.id.feature_leave_management), R.drawable.ic_assignment_late, "QL Đơn nghỉ", () -> {
            if (mListener != null) mListener.onLeaveManagementClicked();
        });

        // 5. Quản lý Chuyên cần
        setupFeature(view.findViewById(R.id.feature_attendance_management), R.drawable.ic_playlist_add_check, "QL Chuyên cần", () -> {
            if (mListener != null) mListener.onAttendanceManagementClicked();
        });

        // 6. Quản lý Khảo sát
        setupFeature(view.findViewById(R.id.feature_survey_management), R.drawable.ic_poll, "QL Khảo sát", () -> {
            if (mListener != null) mListener.onSurveyManagementClicked();
        });

        // 7. Quản lý Học viên
        setupFeature(view.findViewById(R.id.feature_student_management), R.drawable.ic_people, "QL Học viên", () -> {
            if (mListener != null) mListener.onStudentManagementClicked();
        });
    }

    private void setupFeature(View featureView, int iconRes, String name, Runnable onClickAction) {
        ((ImageView) featureView.findViewById(R.id.feature_icon)).setImageResource(iconRes);
        ((TextView) featureView.findViewById(R.id.feature_name)).setText(name);
        featureView.setOnClickListener(v -> onClickAction.run());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
