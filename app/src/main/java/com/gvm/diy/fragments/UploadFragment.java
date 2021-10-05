package com.gvm.diy.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gvm.diy.R;

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
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }
}