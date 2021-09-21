package com.gvm.diy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gvm.diy.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    private RecyclerView recycler_view;
    private TextView textViewTitle;
    private ImageButton imageButtonBack;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
        View itemView = inflater.inflate(R.layout.fragment_chat, container, false);
        recycler_view = itemView.findViewById(R.id.recycler_view);
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        imageButtonBack = itemView.findViewById(R.id.imageButtonBack);


        return itemView;
    }
}