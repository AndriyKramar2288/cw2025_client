package com.banew.cw2025_client.ui.grave;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.banew.cw2025_client.R;

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