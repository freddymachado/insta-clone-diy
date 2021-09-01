package com.gvm.diy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gvm.diy.adapter.ExploreAdapter;
import com.gvm.diy.models.ExploreItem;
import com.gvm.diy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchFragment extends Fragment {

    // TODO: Hacer 2do recycler al pulsar la búsqueda (segunda entrega)
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private RecyclerView recycler_view;

    public SearchFragment() {
        // Required empty public constructor
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
        View itemView = inflater.inflate(R.layout.fragment_search, container, false);
        recycler_view = itemView.findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );

        List<ExploreItem> exploreItems = new ArrayList<>();

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("server_key","1539874186")
                .addFormDataPart("username","temi.abi111@gmail.com")
                .addFormDataPart("password","erisport")/*
                .addFormDataPart("access_token","1d1ae6d02edb7d9ef2dbc305b184eb2e08518d251579769480857c0547956829822027d585cce3e5bd")
                .addFormDataPart("offset","633")
                .addFormDataPart("limit","2")
                .addFormDataPart("v","1")
                .addFormDataPart("resource","post")
                .addFormDataPart("resource_id","fetch_home_posts")*/
                .build();
        Request request = new Request.Builder()
                .url("https://diys.co/endpoints/v1/auth/login")
                .method("POST",body)
                .build();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("ApiResponse", response.body().string());

                    JSONArray array = new JSONArray(response);
                    //TODO: Meter el json con los resultados de la API en la lista exploreItems

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject post = array.getJSONObject(i);

                        exploreItems.add(new ExploreItem(
                                post.getString("image")
                        ));
                    }
                    ExploreAdapter adapter = new ExploreAdapter(getContext(), exploreItems);
                    recycler_view.setAdapter(adapter);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        return itemView;
    }
}