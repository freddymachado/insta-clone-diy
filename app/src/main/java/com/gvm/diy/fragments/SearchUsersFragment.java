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
/*
        word = ((SearchFragment)getParentFragment()).getData();
        Log.e("editTextWord",word);
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("server_key",server_key)
                .addFormDataPart("word",word)
                .addFormDataPart("access_token",access_token)
                .build();

        UserPostsRequest = new Request.Builder()
                .url("https://diys.co/endpoints/v1/user/search")
                .post(requestBody)
                .build();

        client.newCall(UserPostsRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Toast.makeText(getActivity().getApplicationContext(), "Error de red: "+mMessage, Toast.LENGTH_LONG).show();
                Log.e("failure Response", mMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                //TODO: Verificar respuesta para obtener los datos
                Log.e("ApiResponse", mMessage);
                JSONObject array = null;
                try {
                    array = new JSONObject(mMessage);
                    JSONObject data = array.getJSONObject("data");

                    JSONArray userPosts = data.getJSONArray("user_posts");
                    JSONObject file = new JSONObject();

                    for (int i = 0; i < userPosts.length(); i++) {
                        JSONObject post = userPosts.getJSONObject(i);
                        JSONArray postMedia = post.getJSONArray("media_set");
                        //Por alguna razón el jsonArray postMedia tiene la info en string plano.
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FollowAdapter adapter = new FollowAdapter(getActivity().getApplicationContext(), followItems, access_token);
                        recycler_view.setAdapter(adapter);
                    }
                });
            }
        });*/
        return itemView;
    }

    @Override
    public void update(String mMessage) {
        try {
            JSONObject array = new JSONObject(mMessage);
            JSONArray data = array.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject post = data.getJSONObject(i);
                Log.e("ApiResponse", post.getString("user_id")+post.getString("time_text")+post.getString("username"));
                followItems.add(new FollowItem(
                        post.getString("avatar"),
                        post.getString("time_text"),
                        post.getString("username"),
                        post.getString("is_following"),
                        post.getString("user_id"),
                        post.getString("about"),
                        post.getString("website"),
                        post.getString("followers"),
                        post.getString("following"),
                        post.getString("favourites"),
                        post.getString("name")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FollowAdapter adapter = new FollowAdapter(getActivity().getApplicationContext(), followItems,access_token);
        recycler_view.setAdapter(adapter);

    }
}