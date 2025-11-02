package com.banew.cw2025_client.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        var mainPageModel = new ViewModelProvider(requireActivity()).get(MainPageModel.class);
//
//        Button button = view.findViewById(R.id.main_page_add_course_plan_button);
//        button.setOnClickListener(v -> {
//            NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
//                    .getSupportFragmentManager()
//                    .findFragmentById(R.id.nav_host_fragment);
//
//            if (navHostFragment != null) {
//                NavController navController = navHostFragment.getNavController();
//                navController.navigate(R.id.createCoursePlanFragment);
//            }
//        });
//
//        var adapter = setUpAdapter();
//        mainPageModel.getCurrentCoursePlans().observe(getViewLifecycleOwner(), adapter::setList);
//    }
//
//    private CoursePlanAdapter setUpAdapter() {
//        RecyclerView recyclerView = requireView().findViewById(R.id.coursesRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        var adapter = new CoursePlanAdapter(new ArrayList<>());
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(
//                requireContext(),
//                LinearLayoutManager.HORIZONTAL,
//                false
//        ));
//
//        return adapter;
//    }
}