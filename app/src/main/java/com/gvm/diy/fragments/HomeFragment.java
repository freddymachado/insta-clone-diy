package com.gvm.diy.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;
/*
import com.android.volley.Request;
import com.android.volley.Response;*/
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.gvm.diy.adapter.PostAdapter;
import com.gvm.diy.R;
import com.gvm.diy.models.Post;
import com.gvm.diy.ui.FollowersActivity;
import com.gvm.diy.ui.SalaChatActivity;
import com.madapps.liquid.LiquidRefreshLayout;

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

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements PostAdapter.PostListener{
    //TODO: Keep user logged in (last entrega)

    private static final String URL_POSTS = "https://diys.co/pointed.php";

    private RecyclerView recycler_view;
    private List<Post> postList;

    String access_token,username, user_id, avatar, server_key = "1539874186", name, favourites,
            following, followers, fname, lname, about, website, isFollowing;

    ProgressBar progressBar;

    LiquidRefreshLayout refreshLayout;

    ImageButton imageButtonChat;

    public HomeFragment() {
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
        View itemView = inflater.inflate(R.layout.fragment_home, container, false);
        recycler_view = itemView.findViewById(R.id.recycler_view);
        imageButtonChat = itemView.findViewById(R.id.imageButtonChat);
        progressBar = itemView.findViewById(R.id.progressBar);
        refreshLayout = itemView.findViewById(R.id.refreshLayout);

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();

        // Get the Intent that started this activity and extract the string
        Intent intent = getActivity().getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");
/*
        JSONArray jsonBody = new JSONArray();
        try {
            jsonBody.put(0,user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.POST, URL_POSTS, jsonBody,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            //JSONArray array = new JSONArray(response.toString());
                            Log.d("ApiResponse", String.valueOf(response));

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject post = response.getJSONObject(i);

                                postList.add(new Post(
                                        post.getString("description"),
                                        post.getString("time"),
                                        post.getString("username"),
                                        post.getString("avatar"),
                                        post.getString("file"),
                                        post.getString("likes"),
                                        post.getString("comments"),
                                        post.getString("post_id"),
                                        post.getString("user_id"),
                                        post.getString("is_liked"),
                                        post.getString("is_saved"),
                                        post.getString("website")
                                ));
                            }
                            PostAdapter adapter = new PostAdapter(getContext(),
                                    postList,
                                    getActivity().getIntent().getStringExtra("access_token"),"home");
                            recycler_view.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ApiResponse", String.valueOf(error));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity().getApplicationContext(), "Revisa tu conexión e inténtalo de nuevo: "+error, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
*/
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
                                getActivity().getIntent().getStringExtra("access_token"), user_id);
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

        imageButtonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentComments = new Intent(getActivity().getApplicationContext(), FollowersActivity.class);
                intentComments.putExtra("function", "chat");
                intentComments.putExtra("access_token", access_token);
                intentComments.putExtra("user_id", user_id);
                startActivity(intentComments);
            }
        });

        return itemView;
    }

    @Override
    public void postImageOnClick(View v, int position) {

    }
/*    @Override
    public void onClickCallback(Post post, String view) {
        switch(view){
            case "imageViewLike":
                Toast.makeText(getActivity().getApplicationContext(), "It works!", Toast.LENGTH_SHORT).show();
                OkHttpClient imageUploadClient = new OkHttpClient.Builder().build();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("caption","prueba1")
                        .addFormDataPart("access_token",access_token)
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/new_post")
                        .post(requestBody)
                        .build();


                imageUploadClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        //Toast.makeText(ChatScreen.this, "Error uploading file", Toast.LENGTH_LONG).show();
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        final String mMessage = response.body().string();

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("UploadResponse", mMessage);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                break;
            default:
                break;

        }

    }*/
}