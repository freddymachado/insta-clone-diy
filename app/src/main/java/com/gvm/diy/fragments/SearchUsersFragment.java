package com.gvm.diy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gvm.diy.R;
import com.gvm.diy.adapter.FollowAdapter;
import com.gvm.diy.models.FollowItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchUsersFragment extends Fragment implements Updateable {

    private RecyclerView recycler_view;
    private List<FollowItem> followItems;

    OkHttpClient client;
    RequestBody requestBody;
    Request UserPostsRequest;

    String access_token,word, user_id, avatar, server_key = "1539874186", name, favourites, following, followers;

    public SearchUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_search_users, container, false);

        recycler_view = itemView.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_view.setLayoutManager(linearLayoutManager);

        followItems = new ArrayList<>();

        // Get the Intent that started this activity and extract the string
        Intent intent = getActivity().getIntent();
        access_token = intent.getStringExtra("access_token");

        //Iniciamos la solicitud para obtener los datos del usuario
        client = new OkHttpClient().newBuilder().build();
        return itemView;
    }

    @Override
    public void update(String mMessage) {
        try {
            JSONObject object = new JSONObject(mMessage);
            JSONObject data = object.getJSONObject("data");

            JSONArray usersArray = data.getJSONArray("users");

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                Log.e("UserResponse", user.getString("user_id")+user.getString("time_text")+user.getString("username"));
                followItems.add(new FollowItem(
                        user.getString("avatar"),
                        user.getString("time_text"),
                        user.getString("username"),
                        user.getString("is_following"),
                        user.getString("user_id"),
                        user.getString("about"),
                        user.getString("website"),
                        user.getString("followers"),
                        user.getString("following"),
                        user.getString("favourites"),
                        user.getString("name")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FollowAdapter adapter = new FollowAdapter(getContext(), followItems,access_token);
        recycler_view.setAdapter(adapter);
        recycler_view.setVisibility(View.VISIBLE);
    }
}