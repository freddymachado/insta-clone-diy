package com.gvm.diy.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gvm.diy.MyBounceInterpolator;
import com.gvm.diy.R;
import com.gvm.diy.adapter.ChatListAdapter;
import com.gvm.diy.adapter.CommentsAdapter;
import com.gvm.diy.adapter.FollowAdapter;
import com.gvm.diy.adapter.PostAdapter;
import com.gvm.diy.adapter.ProfileAdapter;
import com.gvm.diy.fragments.SearchHashtagsFragment;
import com.gvm.diy.models.ChatList;
import com.gvm.diy.models.CommentsItem;
import com.gvm.diy.models.FollowItem;
import com.gvm.diy.models.Post;
import com.gvm.diy.models.ProfileItem;
import com.madapps.liquid.LiquidRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FollowersActivity extends AppCompatActivity implements ChatListAdapter.SelectedChat {
    private RecyclerView recycler_view;
    private TextView textViewTitle;
    private ImageButton imageButtonBack;
    GridLayoutManager gridLayoutManager;
    private List<FollowItem> followItems;
    private List<CommentsItem> commentsItems;
    private List<ProfileItem> profileItems;

    LinearLayoutManager linearLayoutManager;
    String access_token,username, user_id, function, server_key = "1539874186", post_id, favourites,
            following, followers, tag, name, fname, lname, about, website, isFollowing, current_user,
            id;

    ProgressBar progressBar;

    LiquidRefreshLayout refreshLayout;

    private List<Post> postList;

    List<ChatList> chatList = new ArrayList();

    ChatListAdapter chatAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        recycler_view = findViewById(R.id.recycler_view);
        textViewTitle = findViewById(R.id.textViewTitle);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        refreshLayout = findViewById(R.id.refreshLayout);

        progressBar = findViewById(R.id.progressBar);

        gridLayoutManager = new GridLayoutManager(FollowersActivity.this,2,GridLayoutManager.VERTICAL,false);
        linearLayoutManager = new LinearLayoutManager(FollowersActivity.this);

        recycler_view.setHasFixedSize(true);

        followItems = new ArrayList<>();
        postList = new ArrayList<>();
        commentsItems = new ArrayList<>();

        profileItems = new ArrayList<>();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");
        username = intent.getStringExtra("username");
        function = intent.getStringExtra("function");
        post_id = intent.getStringExtra("post_id");
        tag = intent.getStringExtra("tag");

        OkHttpClient client;

        RequestBody requestBody;
        Request UserPostsRequest;
        //Cargamos la animcion del boton
        final Animation myAnim = AnimationUtils.loadAnimation(FollowersActivity.this,R.anim.bounce);

        //Usamos el BounceInterpolator con una amplitud de 0.2 y frecuencia de 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
        myAnim.setInterpolator(interpolator);

        switch(function){
            case "followers":
                textViewTitle.setText("Seguidores");
                recycler_view.setLayoutManager(linearLayoutManager);
                //Iniciamos la solicitud para obtener los datos del usuario
                client = new OkHttpClient().newBuilder().build();

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("access_token",access_token)
                        .addFormDataPart("user_id",user_id)
                        .build();

                UserPostsRequest = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/user/followers")
                        .post(requestBody)
                        .build();

                client.newCall(UserPostsRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        JSONObject array = null;
                        Log.e("ApiResponse", mMessage);
                        try {
                            array = new JSONObject(mMessage);
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                FollowAdapter adapter = new FollowAdapter(FollowersActivity.this, followItems,access_token);
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                                Log.e("failure Response", mMessage);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String mMessage = response.body().string();
                                JSONObject array = null;
                                Log.e("ApiResponse", mMessage);
                                try {
                                    array = new JSONObject(mMessage);
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

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        FollowAdapter adapter = new FollowAdapter(FollowersActivity.this, followItems,access_token);
                                        recycler_view.setAdapter(adapter);
                                    }
                                });
                            }
                        });

                    }
                });
                break;
            case "favorites":
                textViewTitle.setText("Favoritos");
                recycler_view.setLayoutManager(gridLayoutManager);
                //Iniciamos la solicitud para obtener los datos del usuario
                client = new OkHttpClient().newBuilder().build();

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("access_token",access_token)
                        .build();

                UserPostsRequest = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/fetch_favorites")
                        .post(requestBody)
                        .build();

                client.newCall(UserPostsRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        JSONObject array = null;
                        Log.e("ApiResponse", mMessage);
                        try {
                            array = new JSONObject(mMessage);

                            JSONArray userPosts = array.getJSONArray("data");
                            JSONObject file = new JSONObject();

                            for (int i = 0; i < userPosts.length(); i++) {
                                JSONObject post = userPosts.getJSONObject(i);
                                JSONArray postMedia = post.getJSONArray("media_set");
                                JSONObject media_set = postMedia.getJSONObject(0);

                                Log.e("FavApiResponse", mMessage);
                                profileItems.add(new ProfileItem(
                                        media_set.getString("file").substring(16)
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                //TODO: set OnClickListener
                                ProfileAdapter adapter = new ProfileAdapter(FollowersActivity.this, profileItems);
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
                        profileItems.clear();
                        client.newCall(UserPostsRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                String mMessage = e.getMessage().toString();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                                Log.e("failure Response", mMessage);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String mMessage = response.body().string();
                                JSONObject array = null;
                                Log.e("ApiResponse", mMessage);
                                try {
                                    array = new JSONObject(mMessage);

                                    JSONArray userPosts = array.getJSONArray("data");
                                    JSONObject file = new JSONObject();

                                    for (int i = 0; i < userPosts.length(); i++) {
                                        JSONObject post = userPosts.getJSONObject(i);
                                        JSONArray postMedia = post.getJSONArray("media_set");
                                        JSONObject media_set = postMedia.getJSONObject(0);

                                        Log.e("FavApiResponse", mMessage);
                                        profileItems.add(new ProfileItem(
                                                media_set.getString("file").substring(16)
                                        ));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        ProfileAdapter adapter = new ProfileAdapter(FollowersActivity.this, profileItems);
                                        recycler_view.setAdapter(adapter);
                                    }
                                });
                            }
                        });

                    }
                });
                break;
            case "following":
                textViewTitle.setText("Siguiendo");
                recycler_view.setLayoutManager(linearLayoutManager);
                //Iniciamos la solicitud para obtener los datos del usuario
                client = new OkHttpClient().newBuilder().build();

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("access_token",access_token)
                        .addFormDataPart("user_id",user_id)
                        .build();

                UserPostsRequest = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/user/following")
                        .post(requestBody)
                        .build();

                client.newCall(UserPostsRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        JSONObject array = null;
                        Log.e("ApiResponse", mMessage);
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                FollowAdapter adapter = new FollowAdapter(FollowersActivity.this,
                                        followItems,
                                        access_token);
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                                Log.e("failure Response", mMessage);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String mMessage = response.body().string();
                                JSONObject array = null;
                                Log.e("ApiResponse", mMessage);
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

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        FollowAdapter adapter = new FollowAdapter(FollowersActivity.this,
                                                followItems,
                                                access_token);
                                        recycler_view.setAdapter(adapter);
                                    }
                                });
                            }
                        });

                    }
                });
                break;
            case "likes":
                textViewTitle.setText("Me gusta");
                recycler_view.setLayoutManager(linearLayoutManager);
                //Iniciamos la solicitud para obtener los datos del usuario
                client = new OkHttpClient().newBuilder().build();

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("access_token",access_token)
                        .addFormDataPart("post_id",post_id)
                        .build();

                UserPostsRequest = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/fetch_likes")
                        .post(requestBody)
                        .build();

                client.newCall(UserPostsRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        JSONObject array = null;
                        Log.e("ApiResponse", mMessage);
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
                                        "likes",
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                FollowAdapter adapter = new FollowAdapter(FollowersActivity.this, followItems, access_token);
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                                Log.e("failure Response", mMessage);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String mMessage = response.body().string();
                                JSONObject array = null;
                                Log.e("ApiResponse", mMessage);
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
                                                "likes",
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

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        FollowAdapter adapter = new FollowAdapter(FollowersActivity.this, followItems, access_token);
                                        recycler_view.setAdapter(adapter);
                                    }
                                });
                            }
                        });

                    }
                });
                break;
            case "comments":
                textViewTitle.setText("Comentarios");
                recycler_view.setLayoutManager(linearLayoutManager);
                //Iniciamos la solicitud para obtener los datos del usuario
                client = new OkHttpClient().newBuilder().build();

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("access_token",access_token)
                        .addFormDataPart("post_id",post_id)
                        .build();

                UserPostsRequest = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/fetch_comments")
                        .post(requestBody)
                        .build();

                client.newCall(UserPostsRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        JSONObject array = null;
                        Log.e("ApiResponse", mMessage);
                        try {
                            array = new JSONObject(mMessage);
                            JSONArray data = array.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject post = data.getJSONObject(i);
                                Log.e("FAApiResponse", post.getString("avatar")+post.getString("time_text")+post.getString("username"));
                                commentsItems.add(new CommentsItem(
                                        post.getString("avatar"),
                                        post.getString("text"),
                                        post.getString("time_text"),
                                        post.getString("likes"),
                                        post.getString("id"),
                                        post.getString("user_id"),
                                        post.getString("is_liked")
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                CommentsAdapter adapter = new CommentsAdapter(FollowersActivity.this, commentsItems,access_token, user_id);
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
                        commentsItems.clear();

                        client.newCall(UserPostsRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                String mMessage = e.getMessage().toString();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                                Log.e("failure Response", mMessage);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String mMessage = response.body().string();
                                JSONObject array = null;
                                Log.e("ApiResponse", mMessage);
                                try {
                                    array = new JSONObject(mMessage);
                                    JSONArray data = array.getJSONArray("data");

                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject post = data.getJSONObject(i);
                                        Log.e("FAApiResponse", post.getString("avatar")+post.getString("time_text")+post.getString("username"));
                                        commentsItems.add(new CommentsItem(
                                                post.getString("avatar"),
                                                post.getString("text"),
                                                post.getString("time_text"),
                                                post.getString("likes"),
                                                post.getString("id"),
                                                post.getString("user_id"),
                                                post.getString("is_liked")
                                        ));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        CommentsAdapter adapter = new CommentsAdapter(FollowersActivity.this, commentsItems,access_token, user_id);
                                        recycler_view.setAdapter(adapter);
                                    }
                                });
                            }
                        });

                    }
                });
                break;
            case "chat":
                textViewTitle.setText("Mensajes");
                recycler_view.setLayoutManager(linearLayoutManager);
                recycler_view.addItemDecoration(new DividerItemDecoration(this,
                        DividerItemDecoration.VERTICAL));

                chatAdapter = new ChatListAdapter(chatList,this);
                recycler_view.setAdapter(chatAdapter);

                //Iniciamos la solicitud para obtener los datos del usuario
                client = new OkHttpClient().newBuilder().build();

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("access_token",access_token)
                        .build();

                UserPostsRequest = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/messages/get_chats")
                        .post(requestBody)
                        .build();

                client.newCall(UserPostsRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
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
                            int numeroItems = 0;

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject post = data.getJSONObject(i);
                                Log.e("ApiChatsResponse", post.getString("last_message")+post.getString("username"));
                                Long codigoHora = Long.valueOf(post.getString("time"));
                                Date time = new Date(codigoHora);
                                SimpleDateFormat sFormat = new SimpleDateFormat("hh:mm a");//a = pm o am
                                ChatList chat = new ChatList(
                                        post.getString("username"),
                                        sFormat.format(time),
                                        post.getString("last_message"),
                                        post.getString("user_id"),
                                        post.getString("avatar"),
                                        post.getString("new_message"));


                                chatList.add(chat);

                                numeroItems++;
                            }
/*
                MensajeRecibido m = snapshot.getValue(MensajeRecibido.class);
                //Si el mensaje es enviado por el admin
                if(m.getTipo().equals("3")){

                    TextItem textItem = snapshot.getValue(TextItem.class);
                    TimelineItem textTimelineItem2 = new TimelineItem(textItem);
                    mData.add(textTimelineItem2);
                    lastMessage = m.getMensaje();
                    Log.d("lastmessage", lastMessage);

                }else{
                    TimelineItem textTimelineItem2 = new TimelineItem(m);
                    mData.add(textTimelineItem2);
                    //metodo que chequea si el mensaje contiene un nombre de usuario
                }
*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                recycler_view.setAdapter(chatAdapter);
                                recycler_view.smoothScrollToPosition(chatAdapter.getItemCount());
                                chatAdapter.notifyItemInserted(chatAdapter.getItemCount());
                                recycler_view.smoothScrollToPosition(chatAdapter.getItemCount());
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
                        chatList.clear();
                        client.newCall(UserPostsRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                String mMessage = e.getMessage().toString();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        Toast.makeText(getApplicationContext(), "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
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
                                    int numeroItems = 0;

                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject post = data.getJSONObject(i);
                                        Log.e("ApiChatsResponse", post.getString("last_message")+post.getString("username"));
                                        Long codigoHora = Long.valueOf(post.getString("time"));
                                        Date time = new Date(codigoHora);
                                        SimpleDateFormat sFormat = new SimpleDateFormat("hh:mm a");//a = pm o am
                                        ChatList chat = new ChatList(
                                                post.getString("username"),
                                                sFormat.format(time),
                                                post.getString("last_message"),
                                                post.getString("user_id"),
                                                post.getString("avatar"),
                                                post.getString("new_message"));


                                        chatList.add(chat);

                                        numeroItems++;
                                    }
/*
                MensajeRecibido m = snapshot.getValue(MensajeRecibido.class);
                //Si el mensaje es enviado por el admin
                if(m.getTipo().equals("3")){

                    TextItem textItem = snapshot.getValue(TextItem.class);
                    TimelineItem textTimelineItem2 = new TimelineItem(textItem);
                    mData.add(textTimelineItem2);
                    lastMessage = m.getMensaje();
                    Log.d("lastmessage", lastMessage);

                }else{
                    TimelineItem textTimelineItem2 = new TimelineItem(m);
                    mData.add(textTimelineItem2);
                    //metodo que chequea si el mensaje contiene un nombre de usuario
                }
*/
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        recycler_view.setAdapter(chatAdapter);
                                        recycler_view.smoothScrollToPosition(chatAdapter.getItemCount());
                                        chatAdapter.notifyItemInserted(chatAdapter.getItemCount());
                                        recycler_view.smoothScrollToPosition(chatAdapter.getItemCount());
                                    }
                                });
                            }
                        });                    }
                });
                break;
            case "hashs":
                textViewTitle.setText("#"+tag);
                //Iniciamos la solicitud para obtener los datos del usuario
                client = new OkHttpClient().newBuilder().build();

                requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("access_token",access_token)
                        .addFormDataPart("hash",tag)
                        .build();

                UserPostsRequest = new okhttp3.Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/fetch_hash_posts")
                        .post(requestBody)
                        .build();

                client.newCall(UserPostsRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        //Toast.makeText(ChatScreen.this, "Error uploading file", Toast.LENGTH_LONG).show();
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        final String mMessage = response.body().string();
                        Log.e("SearchResponse", mMessage);
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                PostAdapter adapter = new PostAdapter(FollowersActivity.this,
                                        postList,
                                        access_token, getCurrentUser());
                                recycler_view.setAdapter(adapter);
                            }
                        });
                                /*
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
                                        progressBar.setVisibility(View.GONE);
                                        PostAdapter adapter = new PostAdapter(getContext(),
                                                postList,
                                                getActivity().getIntent().getStringExtra("access_token"), "home");
                                        recycler_view.setAdapter(adapter);
                                    }
                                });*/
                    }
                });

                refreshLayout.setOnRefreshListener(new LiquidRefreshLayout.OnRefreshListener() {
                    @Override
                    public void completeRefresh() {
                    }

                    @Override
                    public void refreshing() {
                        commentsItems.clear();

                        client.newCall(UserPostsRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                String mMessage = e.getMessage().toString();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        Toast.makeText(FollowersActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                                Log.e("failure Response", mMessage);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String mMessage = response.body().string();
                                JSONObject array = null;
                                Log.e("ChatResponse", mMessage);
                                try {
                                    array = new JSONObject(mMessage);
                                    JSONArray data = array.getJSONArray("data");

                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject post = data.getJSONObject(i);
                                        commentsItems.add(new CommentsItem(
                                                post.getString("avatar"),
                                                post.getString("text"),
                                                post.getString("time_text"),
                                                post.getString("likes"),
                                                post.getString("id"),
                                                post.getString("user_id"),
                                                post.getString("is_liked")
                                        ));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayout.finishRefreshing();
                                        CommentsAdapter adapter = new CommentsAdapter(FollowersActivity.this, commentsItems,access_token, user_id);
                                        recycler_view.setAdapter(adapter);
                                    }
                                });
                            }
                        });

                    }
                });
                break;
            default:
                break;
/*E/ApiResponse: {"code":"200","status":"OK","data":[{"user_id":1539,"username":"DayaminSilva","email":"dayamincoromoto@gmail.com",
                                                        "ip_address":"","fname":"Dayamin","lname":"Silva Colombia","gender":"male","language":"spanish",
                                                        "avatar":"https:\/\/diys.co\/media\/upload\/photos\/2021\/09\/pWWy4TxxLVbtUjA9c4ZFQO1s1EDrtVojc5O1qeOjUqc8xlngWT_09_c0603406c34c7aca37e458c45a09a570_image.jpg",
                                                        "cover":"https:\/\/diys.co\/media\/img\/d-cover.jpg","country_id":48,"about":null,"google":"",
                                                        "facebook":"","twitter":"","website":"","active":1,"admin":0,"verified":0,"last_seen":"1631175500",
                                                        "registered":"0000\/0","is_pro":0,"posts":0,"p_privacy":"2","c_privacy":"1","n_on_like":"1",
                                                        "n_on_mention":"1","n_on_comment":"1","n_on_follow":"1","n_on_comment_like":"1","n_on_comment_reply":"1",
                                                        "startup_avatar":1,"startup_info":1,"startup_follow":1,"src":"Google","search_engines":"1","mode":"day",
                                                        "device_id":"","balance":"0","wallet":"0.00","conversation_id":"","referrer":0,"profile":1,
                                                        "business_account":0,"paypal_email":"","b_name":"","b_email":"","b_phone":"","b_site":"",
                                                        "b_site_action":"","uploads":0,"address":"","city":"","state":"","zip":0,"phone_number":"","id":4810,
                                                        "follower_id":1539,"following_id":1476,"type":1,"time":"1631175014","name":"Dayamin Silva Colombia",
                                                        "uname":"DayaminSilva","url":"https:\/\/diys.co\/DayaminSilva","followers":5,"following":0,
                                                        "favourites":0,"posts_count":0,"is_following":false,"time_text":"9 dias hace"},
                                                    {"user_id":1486,"username":"MarisolMoralesRomero","email":"solesmero_mf7705@hotmail.com","ip_address":"",
                                                    "fname":"Marisol","lname":"Morales","gender":"male","language":"spanish",
                                                    "avatar":"https:\/\/diys.co\/media\/upload\/photos\/2021\/08\/mHmk8o67426GgbFVhdKyAX46hm6tGoFU3m3iv2B1W4wK23Ckos_31_7ae295caa518b46352435a4a3ed9438e_image.jpg",
                                                    "cover":"https:\/\/diys.co\/media\/img\/d-cover.jpg","country_id":48,"about":null,"google":"",
                                                    "facebook":"","twitter":"","website":"","active":1,"admin":0,"verified":0,"last_seen":"1630368812",
                                                    "registered":"0000\/0","is_pro":0,"posts":0,"p_privacy":"2","c_privacy":"1","n_on_like":"1",
                                                    "n_on_mention":"1","n_on_comment":"1","n_on_follow":"1","n_on_comment_like":"1","n_on_comment_reply":"1",
                                                    "startup_avatar":1,"startup_info":1,"startup_follow":1,"src":"Facebook","search_engines":"1",
                                                    "mode":"day","device_id":"","balance":"0","wallet":"0.00","conversation_id":"","referrer":0,
                                                    "profile":1,"business_account":0,"paypal_email":"","b_name":"","b_email":"","b_phone":"","b_site":"",
                                                    "b_site_action":"","uploads":0,"address":"","city":"","state":"","zip":0,"phone_number":"","id":4671,
                                                    "follower_id":1486,"following_id":1476,"type":1,"time":"1630368697","name":"Marisol Morales",
                                                    "uname":"MarisolMoralesRomero","url":"https:\/\/diys.co\/MarisolMoralesRomero","followers":5,
                                                    "following":0,"favourites":0,"posts_count":0,"is_following":false,"time_text":"18 dias hace"},

                                                    {"user_id":1483,"username":"BoliviaBolivia","email":"cinthiafelipes@hotmail.es","ip_address":"","fname":"Cinthia","lname":"Felipes","gender":"male","language":"spanish","avatar":"https:\/\/diys.co\/media\/upload\/photos\/2021\/08\/Bj95ZFFByVkOAisxIwg2MGxq13rTzceJEZTUTT8KIDNYhkJJ2t_30_44fe02da2bc37ed44ef7c8ec065b92fc_image.jpg","cover":"https:\/\/diys.co\/media\/img\/d-cover.jpg","country_id":28,"about":null,"google":"","facebook":"","twitter":"","website":"","active":1,"admin":0,"verified":0,"last_seen":"1630362777","registered":"0000\/0","is_pro":0,"posts":0,"p_privacy":"2","c_privacy":"1","n_on_like":"1","n_on_mention":"1","n_on_comment":"1","n_on_follow":"1","n_on_comment_like":"1","n_on_comment_reply":"1","startup_avatar":1,"startup_info":1,"startup_follow":1,"src":"Facebook","search_engines":"1","mode":"day","device_id":"","balance":"0","wallet":"0.00","conversation_id":"","referrer":0,"profile":1,"business_account":0,"paypal_email":"","b_name":"","b_email":"","b_phone":"","b_site":"","b_site_action":"","uploads":0,"address":"","city":"","state":"","zip":0,"phone_number":"","id":4658,"follower_id":1483,"following_id":1476,"type":1,"time":"1630362559","name":"Cinthia Felipes","uname":"BoliviaBolivia","url":"https:\/\/diy
 */
        }

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButtonBack.startAnimation(myAnim);
                finish();
            }
        });


    }

    @Override
    public void selectedChat(ChatList chatList, String id) {
        Log.d("idenviado",id);
        startActivity(new Intent(FollowersActivity.this,ChatActivity.class).putExtra("user_id",id).putExtra("access_token",access_token));
        finish();

    }

    private String getCurrentUser() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        current_user = pref.getString("current_user",current_user);
        return current_user;
    }
}