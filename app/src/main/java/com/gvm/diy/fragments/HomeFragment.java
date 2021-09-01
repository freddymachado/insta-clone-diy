package com.gvm.diy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gvm.diy.adapter.PostAdapter;
import com.gvm.diy.R;
import com.gvm.diy.models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String URL_POSTS = "https://diys.co/punto.php";

    private RecyclerView recycler_view;
    private List<Post> postList;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View itemView = inflater.inflate(R.layout.fragment_home, container, false);
        recycler_view = itemView.findViewById(R.id.recycler_view);

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_POSTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            Log.d("ApiResponse", response);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject post = array.getJSONObject(i);

                                postList.add(new Post(
                                        post.getString("description"),
                                        post.getString("username"),
                                        post.getString("avatar"),
                                        post.getString("file"),
                                        post.getString("likes"),
                                        post.getString("comments")
                                ));
                            }
                            PostAdapter adapter = new PostAdapter(getContext(), postList);
                            recycler_view.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ApiResponse", String.valueOf(error));

            }
        });

        Volley.newRequestQueue(getContext()).add(stringRequest);



        return itemView;
    }


}