package com.banew.cw2025_client.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banew.cw2025_client.CoursePlanAdapter;
import com.banew.cw2025_client.R;

import java.util.ArrayList;

public class MainPageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.coursesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        var adapter = new CoursePlanAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        var mainPageModel = new ViewModelProvider(requireActivity()).get(MainPageModel.class);
        mainPageModel.getCurrentCoursePlans().observe(getViewLifecycleOwner(), adapter::setList);
    }
}