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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.adapter.PostAdapter;
import com.gvm.diy.R;
import com.gvm.diy.adapter.ProfileAdapter;
import com.gvm.diy.models.Post;
import com.gvm.diy.models.ProfileItem;
import com.gvm.diy.ui.MainActivity;
import com.gvm.diy.ui.PostViewerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment implements PostAdapter.PostListener{
    //TODO: set onClickListener to make conections, go to other activities, open description or toggle the menu bar
    //TODO: Keep user logged in

    private static final String URL_POSTS = "https://diys.co/punto.php";

    private RecyclerView recycler_view;
    private List<Post> postList;

    String access_token,username, user_id, avatar, server_key = "1539874186", name, favourites, following, followers;

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

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();


        // Get the Intent that started this activity and extract the string
        Intent intent = getActivity().getIntent();
        access_token = intent.getStringExtra("access_token");

        /*

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
                            PostAdapter adapter = new PostAdapter(getContext(),
                                    postList,
                                    getActivity().getIntent().getStringExtra("access_token"));
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

        Volley.newRequestQueue(getContext()).add(stringRequest);*/

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
                //Toast.makeText(ChatScreen.this, "Error uploading file", Toast.LENGTH_LONG).show();
                Log.e("failure Response", mMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                //TODO: Parece que vamos a tener que usar mi script para obtener los posts del home.
                Log.e("ApiResponse", mMessage);
                JSONObject array = null;
                try {
                    array = new JSONObject(mMessage);
                    JSONArray data = array.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject post = data.getJSONObject(i);

                        JSONArray postMedia = post.getJSONArray("media_set");
                        Log.e("ApiResponse", String.valueOf(data.length()));

                        postList.add(new Post(
                                post.getString("description"),
                                post.getString("time_text"),
                                post.getString("username"),
                                post.getString("avatar"),
                                postMedia.getString(0).split("file")[1].substring(3).split(".jpg")[0].replace("\\",""),
                                post.getString("likes"),
                                post.getString("comments"),
                                post.getBoolean("is_liked"),
                                post.getBoolean("is_saved"),
                                post.getString("post_id")
                        ));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PostAdapter adapter = new PostAdapter(getContext(),
                                postList,
                                getActivity().getIntent().getStringExtra("access_token"));
                        recycler_view.setAdapter(adapter);
                    }
                });
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