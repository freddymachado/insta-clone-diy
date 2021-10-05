package com.gvm.diy.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gvm.diy.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class UploadFragment extends Fragment {
    Uri resultUri;


    public UploadFragment() {
        // Required empty public constructor
    }
    public UploadFragment(Uri resultUri) {
        this.resultUri = resultUri;
    }
/*
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment(resultUri);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_search, container, false);

        TextView textViewPublish, textViewName, textViewUser, textViewDescription;
        ImageButton imageButtonBack;
        RoundedImageView roundedImageViewAvatar;

        textViewPublish = itemView.findViewById(R.id.textViewPublish);
        textViewName = itemView.findViewById(R.id.textViewName);
        textViewUser = itemView.findViewById(R.id.textViewUser);

        imageButtonBack = itemView.findViewById(R.id.imageButtonBack);
        roundedImageViewAvatar = itemView.findViewById(R.id.user_profile_image);
        textViewDescription = itemView.findViewById(R.id.textViewDescription);

        roundedImageViewAvatar.setImageURI(resultUri);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        textViewPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return itemView;
    }
}