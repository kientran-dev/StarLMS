package com.starlms.starlms.admin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.starlms.starlms.R;
import com.starlms.starlms.adapter.AdminSurveyResponseAdapter;
import com.starlms.starlms.dao.SurveyResponseDao;
import com.starlms.starlms.database.AppDatabase;
import com.starlms.starlms.model.SurveyResponseWithUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminSurveyStatisticsFragment extends Fragment {

    private static final String ARG_SURVEY_ID = "survey_id";
    private static final String ARG_SURVEY_TITLE = "survey_title";

    private int surveyId;
    private String surveyTitle;

    private SurveyResponseDao surveyResponseDao;
    private RecyclerView recyclerView;
    private AdminSurveyResponseAdapter adapter;
    private ExecutorService executorService;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public static AdminSurveyStatisticsFragment newInstance(int surveyId, String surveyTitle) {
        AdminSurveyStatisticsFragment fragment = new AdminSurveyStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SURVEY_ID, surveyId);
        args.putString(ARG_SURVEY_TITLE, surveyTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            surveyId = getArguments().getInt(ARG_SURVEY_ID);
            surveyTitle = getArguments().getString(ARG_SURVEY_TITLE);
        }
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        surveyResponseDao = db.surveyResponseDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment_survey_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_survey_statistics);
        toolbar.setTitle(surveyTitle != null ? surveyTitle : "Thống kê");
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recycler_view_responses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new AdminSurveyResponseAdapter();
        recyclerView.setAdapter(adapter);

        loadResponses();
    }

    private void loadResponses() {
        executorService.execute(() -> {
            List<SurveyResponseWithUser> responses = surveyResponseDao.getResponsesForSurvey(surveyId);
            mainThreadHandler.post(() -> adapter.setResponses(responses));
        });
    }
}
