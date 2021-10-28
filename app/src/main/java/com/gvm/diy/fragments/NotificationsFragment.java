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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.R;
import com.gvm.diy.adapter.FollowAdapter;
import com.gvm.diy.adapter.PostAdapter;
import com.gvm.diy.adapter.VideoPlayerRecyclerAdapter;
import com.gvm.diy.adapter.VideoPlayerRecyclerView;
import com.gvm.diy.models.FollowItem;
import com.gvm.diy.models.MediaObject;
import com.gvm.diy.models.Post;
import com.gvm.diy.utils.Resources;
import com.gvm.diy.utils.VerticalSpacingItemDecorator;
import com.madapps.liquid.LiquidRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    private RecyclerView recycler_view;
    private TextView textViewTitle;
    private ImageButton imageButtonBack;

    String access_token,username, user_id, avatar, server_key = "1539874186", name, favourites,
            following, followers, fname, lname, about, website, isFollowing;

    LiquidRefreshLayout refreshLayout;

    ProgressBar progressBar;

    private List<FollowItem> followItems;
    private List<Post> postList;
    ArrayList<MediaObject> mediaObjects;
    private VideoPlayerRecyclerView mRecyclerView;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
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

        mRecyclerView = itemView.findViewById(R.id.recycler_view);

        // Get the Intent that started this activity and extract the string
        Intent intent = getActivity().getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");
        initRecyclerView();

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
/*
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
        });*/

        return itemView;
    }
    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(itemDecorator);


        mediaObjects = new ArrayList<>();
        //Iniciamos la solicitud para obtener los datos del usuario
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("server_key",server_key)
                .addFormDataPart("access_token",access_token)
                .build();

        okhttp3.Request UserPostsRequest = new Request.Builder()
                .url("https://diys.co/endpoints/v1/post/fetch_home_posts")
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
                        JSONObject userData = post.getJSONObject("user_data");

                        name = userData.getString("name");
                        following = userData.getString("following");
                        followers = userData.getString("followers");
                        favourites = userData.getString("favourites");
                        fname = userData.getString("fname");
                        lname = userData.getString("lname");
                        about = userData.getString("about");
                        website = userData.getString("website");
                        isFollowing = userData.getString("following");

                        JSONArray postMedia = post.getJSONArray("media_set");
                        String postImageLink = postMedia.getString(0).split("diy")[1]
                                .substring(3).split("\\.")[0].substring(1).replace("\\","");
                        String extension = postMedia.getString(0).split("diys")[1]
                                .substring(3).split("\\.")[1].substring(0,3);

                        Log.e("HFApiResponse", following+followers+favourites);

                        mediaObjects.add(new MediaObject(
                                post.getString("description"),
                                post.getString("time_text"),
                                post.getString("username"),
                                post.getString("avatar"),
                                postImageLink+"."+extension,
                                post.getString("likes"),
                                post.getString("comments"),
                                post.getString("is_liked"),
                                post.getString("is_saved"),
                                post.getString("post_id"),
                                post.getString("user_id"),
                                name, following, followers,
                                favourites, about, website,
                                isFollowing
                        ));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);

                        mRecyclerView.setMediaObjects(mediaObjects);
                        VideoPlayerRecyclerAdapter adapter = new VideoPlayerRecyclerAdapter(mediaObjects,
                                initGlide(),getActivity().getApplicationContext(), access_token);
                        mRecyclerView.setAdapter(adapter);
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
                postList.clear();
                //Iniciamos la solicitud para obtener los datos del usuario
                OkHttpClient client = new OkHttpClient().newBuilder().build();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("access_token",access_token)
                        .build();

                okhttp3.Request UserPostsRequest = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/fetch_home_posts")
                        .post(requestBody)
                        .build();

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
                                JSONObject userData = post.getJSONObject("user_data");

                                name = userData.getString("name");
                                following = userData.getString("following");
                                followers = userData.getString("followers");
                                favourites = userData.getString("favourites");
                                fname = userData.getString("fname");
                                lname = userData.getString("lname");
                                about = userData.getString("about");
                                website = userData.getString("website");
                                isFollowing = userData.getString("following");

                                JSONArray postMedia = post.getJSONArray("media_set");
                                String postImageLink = postMedia.getString(0).split("diy")[1]
                                        .substring(3).split("\\.")[0].substring(1).replace("\\","");
                                String extension = postMedia.getString(0).split("diys")[1]
                                        .substring(3).split("\\.")[1].substring(0,3);

                                Log.e("HFApiResponse", following+followers+favourites);

                                postList.add(new Post(
                                        post.getString("description"),
                                        post.getString("time_text"),
                                        post.getString("username"),
                                        post.getString("avatar"),
                                        postImageLink+"."+extension,
                                        post.getString("likes"),
                                        post.getString("comments"),
                                        post.getString("is_liked"),
                                        post.getString("is_saved"),
                                        post.getString("post_id"),
                                        post.getString("user_id"),
                                        name, following, followers,
                                        favourites, about, website,
                                        isFollowing
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.finishRefreshing();
                                PostAdapter adapter = new PostAdapter(getContext(),
                                        postList,
                                        getActivity().getIntent().getStringExtra("access_token"), user_id);
                                recycler_view.setAdapter(adapter);
                            }
                        });
                    }
                });

            }
        });
    }

    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }


    @Override
    public void onDestroy() {
        if(mRecyclerView!=null)
            mRecyclerView.releasePlayer();
        super.onDestroy();
    }
}