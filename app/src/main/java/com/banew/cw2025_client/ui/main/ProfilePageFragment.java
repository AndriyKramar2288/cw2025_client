package com.banew.cw2025_client.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.banew.cw2025_client.R;
import com.bumptech.glide.Glide;

public class ProfilePageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_page, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        var mainPageModel = new ViewModelProvider(requireActivity()).get(MainPageModel.class);

        mainPageModel.getCurrentUser().observeForever(u -> {
            ((TextView) getActivity().findViewById(R.id.profile_email)).setText(
                    u.getEmail()
            );
            ((TextView) getActivity().findViewById(R.id.profile_username)).setText(
                    u.getUsername()
            );

            if (u.getPhotoSrc() != null) {
                ImageView imageView = getActivity().findViewById(R.id.userAvatarImage);

                String imageUrl = u.getPhotoSrc();

                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.contacts_product_24px)
                        .centerCrop()
                        .into(imageView);
            }
        });
    }
}