package com.starlms.starlms.admin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.starlms.starlms.R;
import com.starlms.starlms.adapter.AdminSurveyListAdapter;
import com.starlms.starlms.dao.SurveyDao;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.entity.Survey;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminSurveyListFragment extends Fragment implements AdminSurveyListAdapter.OnItemInteractionListener {

    private static final String ARG_STATUS = "status";
    private String status;

    private SurveyDao surveyDao;
    private RecyclerView recyclerView;
    private AdminSurveyListAdapter adapter;
    private ExecutorService executorService;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private SurveyListListener mListener;

    // SỬA Ở ĐÂY: Thêm phương thức mới vào Interface
    public interface SurveyListListener {
        void onSurveyDataChanged();
        void onShowStatistics(int surveyId, String surveyTitle);
    }

    public static AdminSurveyListFragment newInstance(String status) {
        AdminSurveyListFragment fragment = new AdminSurveyListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof SurveyListListener) {
            mListener = (SurveyListListener) getParentFragment();
        } else {
            throw new RuntimeException("The parent fragment must implement SurveyListListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        surveyDao = db.surveyDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_survey_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean isPublishedTab = "Đã đăng".equals(status);

        recyclerView = view.findViewById(R.id.recycler_view_surveys);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new AdminSurveyListAdapter(isPublishedTab);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemInteractionListener(this);

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_survey);
        if (isPublishedTab) {
            fabAdd.setVisibility(View.GONE);
        } else {
            fabAdd.setOnClickListener(v -> showSurveyDialog(null));
        }

        loadSurveys();
    }

    private void loadSurveys() {
        executorService.execute(() -> {
            List<Survey> surveys = surveyDao.getSurveysByStatus(status);
            mainThreadHandler.post(() -> adapter.setSurveys(surveys));
        });
    }
    
    private void showSurveyDialog(@Nullable final Survey survey) {
        boolean isUpdate = (survey != null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isUpdate ? "Cập Nhật Khảo Sát" : "Thêm Khảo Sát Mới");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.admin_dialog_add_update_survey, null);
        final EditText etTitle = dialogView.findViewById(R.id.et_survey_title);
        final EditText etDescription = dialogView.findViewById(R.id.et_survey_description);
        builder.setView(dialogView);

        if (isUpdate) {
            etTitle.setText(survey.getTitle());
            etDescription.setText(survey.getDescription());
        }

        builder.setPositiveButton(isUpdate ? "Cập Nhật" : "Thêm", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getContext(), "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }

            executorService.execute(() -> {
                if (isUpdate) {
                    survey.setTitle(title);
                    survey.setDescription(description);
                    surveyDao.update(survey);
                } else {
                    Survey newSurvey = new Survey(title, description);
                    surveyDao.insert(newSurvey);
                }
                mainThreadHandler.post(() -> {
                    if (mListener != null) mListener.onSurveyDataChanged();
                });
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
    
    private void deleteSurvey(final Survey survey) {
        executorService.execute(() -> {
            surveyDao.delete(survey);
            mainThreadHandler.post(() -> {
                if (mListener != null) mListener.onSurveyDataChanged();
            });
        });
    }

    @Override
    public void onItemLongClick(Survey survey) {
        if ("Đã đăng".equals(status)) return;

        final CharSequence[] options = {"Cập nhật", "Xóa"};
        new AlertDialog.Builder(requireContext())
                .setTitle(survey.getTitle())
                .setItems(options, (dialog, item) -> {
                    if (options[item].equals("Cập nhật")) {
                        showSurveyDialog(survey);
                    } else if (options[item].equals("Xóa")) {
                         new AlertDialog.Builder(requireContext())
                            .setTitle("Xác nhận xóa")
                            .setMessage("Bạn có chắc chắn muốn xóa khảo sát '" + survey.getTitle() + "'?")
                            .setPositiveButton("Xóa", (d, w) -> deleteSurvey(survey))
                            .setNegativeButton("Hủy", null)
                            .show();
                    }
                })
                .show();
    }

    @Override
    public void onPublishClick(Survey survey) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận đăng")
                .setMessage("Bạn có chắc muốn đăng khảo sát này? Sau khi đăng sẽ không thể sửa hoặc xóa.")
                .setPositiveButton("Đăng", (dialog, which) -> {
                    executorService.execute(() -> {
                        survey.setStatus("Đã đăng");
                        surveyDao.update(survey);
                        mainThreadHandler.post(() -> {
                            if (mListener != null) mListener.onSurveyDataChanged();
                        });
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    // SỬA Ở ĐÂY: Thay thế Toast bằng cách gọi ra interface
    @Override
    public void onItemClick(Survey survey) {
        if ("Đã đăng".equals(status)) {
            if (mListener != null) {
                mListener.onShowStatistics(survey.getSurveyId(), survey.getTitle());
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
