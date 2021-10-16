package com.gvm.diy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gvm.diy.R;
import com.gvm.diy.adapter.FollowAdapter;
import com.gvm.diy.adapter.PostAdapter;
import com.gvm.diy.models.FollowItem;
import com.gvm.diy.models.Post;
import com.gvm.diy.ui.FollowersActivity;
import com.madapps.liquid.LiquidRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    private RecyclerView recycler_view;
    private TextView textViewTitle;
    private ImageButton imageButtonBack;

    String access_token,username, user_id, avatar, server_key = "1539874186", name, favourites,
            following, followers, fname, lname, about, website, isFollowing;

    LiquidRefreshLayout refreshLayout;

    ProgressBar progressBar;

    private List<FollowItem> followItems;

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
        refreshLayout = itemView.findViewById(R.id.refreshLayout);
        progressBar = itemView.findViewById(R.id.progressBar);

        // Get the Intent that started this activity and extract the string
        Intent intent = getActivity().getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");

        //Iniciamos la solicitud para obtener los datos del usuario
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("server_key",server_key)
                .addFormDataPart("access_token",access_token)
                .build();

        okhttp3.Request UserPostsRequest = new Request.Builder()
                .url("https://diys.co/endpoints/v1/user/fetch_notifications")
                .post(requestBody)
                .build();

        client.newCall(UserPostsRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity().getApplicationContext(), "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                    }
                });
                //Toast.makeText(ChatScreen.this, "Error uploading file", Toast.LENGTH_LONG).show();
                Log.e("failure Response", mMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                Log.e("ApiResponse", mMessage);
                JSONObject array = null;
                try {
                    array = new JSONObject(mMessage);
                    JSONArray data = array.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject post = data.getJSONObject(i);
                        Log.e("ApiResponse", post.getString("avatar")+post.getString("time_text")+post.getString("username"));
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
                                post.getString("favorites"),
                                post.getString("name")
                        ));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        FollowAdapter adapter = new FollowAdapter(getActivity().getApplicationContext(),
                                followItems,access_token);
                        recycler_view.setAdapter(adapter);
                    }
                });
            }
        });

        refreshLayout.setOnRefreshListener(new LiquidRefreshLayout.OnRefreshListener() {
            @Override
            public void completeRefresh() {
            }

            @Override
            public void refreshing() {
                followItems.clear();
                client.newCall(UserPostsRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.finishRefreshing();
                                Toast.makeText(getActivity().getApplicationContext(), "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        //Toast.makeText(ChatScreen.this, "Error uploading file", Toast.LENGTH_LONG).show();
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        Log.e("ApiResponse", mMessage);
                        JSONObject array = null;
                        try {
                            array = new JSONObject(mMessage);
                            JSONArray data = array.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject post = data.getJSONObject(i);
                                Log.e("ApiResponse", post.getString("avatar")+post.getString("time_text")+post.getString("username"));
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
                                        post.getString("favorites"),
                                        post.getString("name")
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.finishRefreshing();
                                FollowAdapter adapter = new FollowAdapter(getActivity().getApplicationContext(),
                                        followItems,access_token);
                                recycler_view.setAdapter(adapter);
                            }
                        });
                    }
                });

            }
        });

        return itemView;
    }
}