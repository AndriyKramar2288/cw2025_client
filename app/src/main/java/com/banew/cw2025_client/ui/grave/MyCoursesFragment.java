package com.banew.cw2025_client.ui.grave;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.banew.cw2025_client.R;

public class MyCoursesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_courses, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}