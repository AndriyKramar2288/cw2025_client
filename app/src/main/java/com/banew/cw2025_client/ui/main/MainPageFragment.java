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

import com.banew.cw2025_client.R;

public class MainPageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_page, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        var mainPageModel = new ViewModelProvider(requireActivity()).get(MainPageModel.class);
        mainPageModel.getCurrentUser().observeForever(u -> {
            ((TextView) getActivity().findViewById(R.id.long_text)).setText(
                    u.getUsername()
            );
        });
    }
}