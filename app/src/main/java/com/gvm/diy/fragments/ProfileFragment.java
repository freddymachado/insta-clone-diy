package com.gvm.diy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.R;
import com.gvm.diy.adapter.PostAdapter;
import com.gvm.diy.adapter.ProfileAdapter;
import com.gvm.diy.models.Post;
import com.gvm.diy.models.ProfileItem;
import com.gvm.diy.ui.FollowersActivity;
import com.gvm.diy.ui.EditActivity;
import com.gvm.diy.ui.SettingsActivity;
import com.makeramen.roundedimageview.RoundedImageView;

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

public class ProfileFragment extends Fragment {
    //TODO: Guardar valores en SharedPreferences para evitar conexiones (última entrega)

    ImageButton imageButtonSettings, imageButtonGrid, imageButtonAdd, imageButtonList;

    RoundedImageView imageViewProfile;

    Button buttonEdit;

    TextView textViewFullname, textViewDescription, textViewFollowers, textViewNumberFollowers,
            textViewNumberFollowing, textViewFollowing, textViewNumberFavorites, textViewFavorites;

    String access_token,username, user_id, avatar, server_key = "1539874186", name, favourites, following,
            fname, lname, about, website, followers;

    GridLayoutManager gridLayoutManager;

    LinearLayoutManager linearLayoutManager;

    JSONObject userData;

    OkHttpClient client;
    RequestBody requestBody;
    Request UserPostsRequest;
    private List<Post> postList;

    private RecyclerView recycler_view;
    private List<ProfileItem> profileItems;

    PostAdapter adapterLinear;
    ProfileAdapter adapterGrid;

    LiquidRefreshLayout refreshLayout;

    ProgressBar progressBar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_profile, container, false);
        imageButtonSettings = itemView.findViewById(R.id.imageButtonSettings);
        imageButtonGrid = itemView.findViewById(R.id.imageButtonGrid);
        imageButtonAdd = itemView.findViewById(R.id.imageButtonAdd);
        imageButtonList = itemView.findViewById(R.id.imageButtonList);
        imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
        buttonEdit = itemView.findViewById(R.id.buttonEdit);
        textViewFullname = itemView.findViewById(R.id.textViewFullname);
        textViewDescription = itemView.findViewById(R.id.textViewDescription);
        textViewFollowers = itemView.findViewById(R.id.textViewFollowers);
        textViewNumberFollowers = itemView.findViewById(R.id.textViewNumberFollowers);
        textViewNumberFollowing = itemView.findViewById(R.id.textViewNumberFollowing);
        textViewFollowing = itemView.findViewById(R.id.textViewFollowing);
        textViewNumberFavorites = itemView.findViewById(R.id.textViewNumberFavorites);
        textViewFavorites = itemView.findViewById(R.id.textViewFavorites);
        refreshLayout = itemView.findViewById(R.id.refreshLayout);

        recycler_view = itemView.findViewById(R.id.recycler_view);

        progressBar = itemView.findViewById(R.id.progressBar);

        gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(),2,GridLayoutManager.VERTICAL,false);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(gridLayoutManager);

        profileItems = new ArrayList<>();
        postList = new ArrayList<>();

        // Get the Intent that started this activity and extract the string
        Intent intent = getActivity().getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");
        username = intent.getStringExtra("username");

        //Iniciamos la solicitud para obtener los datos del usuario
        client = new OkHttpClient().newBuilder().build();

        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("server_key",server_key)
                .addFormDataPart("user_id",user_id)
                .addFormDataPart("access_token",access_token)
                .build();

        UserPostsRequest = new Request.Builder()
                .url("https://diys.co/endpoints/v1/post/fetch_user_posts")
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
                Log.e("failure Response", mMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();
                JSONObject array = null;
                try {
                    array = new JSONObject(mMessage);
                    JSONObject data = array.getJSONObject("data");
                    userData = data.getJSONObject("user_data");
                    name = userData.getString("name");
                    following = userData.getString("following");
                    followers = userData.getString("followers");
                    favourites = userData.getString("favourites");
                    avatar = userData.getString("avatar");
                    fname = userData.getString("fname");
                    lname = userData.getString("lname");
                    about = userData.getString("about");
                    website = userData.getString("website");

                    JSONArray userPosts = data.getJSONArray("user_posts");

                    for (int i = 0; i < userPosts.length(); i++) {
                        JSONObject post = userPosts.getJSONObject(i);
                        JSONArray postMedia = post.getJSONArray("media_set");
                        String postImageLink = postMedia.getString(0).split("diy")[1]
                                .substring(3).split("\\.")[0].substring(1).replace("\\","");
                        String extension = postMedia.getString(0).split("diys")[1]
                                .substring(3).split("\\.")[1].substring(0,3);

                        Log.e("PrFApiResponse", mMessage);
                        profileItems.add(new ProfileItem(
                                postImageLink+"."+extension
                        ));

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
                                        "false"
                                ));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if(followers.equals("false")){
                            followers = "0";
                        }
                        if(following.equals("false")){
                            following = "0";
                        }
                        if(favourites.equals("false")){
                            favourites = "0";
                        }
                        textViewFullname.setText(name);
                        textViewDescription.setText(about);
                        textViewNumberFollowing.setText(following);
                        textViewNumberFollowers.setText(followers);
                        textViewNumberFavorites.setText(favourites);
                        Glide.with(getActivity().getApplicationContext()).load(avatar)
                                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                                .into(imageViewProfile);
                        adapterGrid = new ProfileAdapter(getContext(), postList, access_token);
                        recycler_view.setAdapter(adapterGrid);
                        adapterLinear = new PostAdapter(getContext(),
                                        postList,
                                        getActivity().getIntent().getStringExtra("access_token"),
                                        getActivity().getIntent().getStringExtra("access_token"));
                    }
                });
            }
        });
        /*E/ApiResponseProfilePosts: {"code":"200",
                                        "status":"OK",
                                        "data":{
                                            "user_data":{
                                                "user_id":1476,
                                                "username":"example_",
                                                "email":"example_@example.com",
                                                "ip_address":"201.242.65.9",
                                                "fname":"",
                                                "lname":"",
                                                "gender":"male",
                                                "email_code":"c38872d6fc797062ff6de3e26d644f822a74682b",
                                                "language":"spanish",
                                                "avatar":"https:\/\/diys.co\/media\/img\/d-avatar.jpg",
                                                "cover":"https:\/\/diys.co\/media\/img\/d-cover.jpg",
                                                "country_id":0,
                                                "about":null,
                                                "google":"",
                                                "facebook":"",
                                                "twitter":"",
                                                "website":"",
                                                "active":1,
                                                "admin":0,
                                                "verified":0,
                                                "last_seen":"1631793272",
                                                "registered":"2021\/8",
                                                "is_pro":0,
                                                "posts":0,
                                                "p_privacy":"2",
                                                "c_privacy":"1",
                                                "n_on_like":"1",
                                                "n_on_mention":"1",
                                                "n_on_comment":"1",
                                                "n_on_follow":"1",
                                                "n_on_comment_like":"1",
                                                "n_on_comment_reply":"1",
                                                "startup_avatar":0,
                                                "startup_info":0,
                                                "startup_follow":0,
                                                "src":"",
                                                "search_engines":"1",
                                                "mode":"day",
                                                "device_id":"6ec5b02b-d672-4e29-837a-7fcf0a4a4ad4",
                                                "balance":"0",
                                                "wallet":"0.00",
                                                "conversation_id":"",
                                                "referrer":0,
                                                "profile":1,
                                                "business_account":0,
                                                "paypal_email":"",
                                                "b_name":"",
                                                "b_email":"",
                                                "b_phone":"",
                                                "b_site":"",
                                                "b_site_action":"",
                                                "uploads":0,
                                                "address":"",
                                                "city":"",
                                                "state":"",
                                                "zip":0,
                                                "phone_number":"",
                                                "name":"example_",
                                                "uname":"example_",
                                                "url":"https:\/\/diys.co\/example_",
                                                "followers":5,
                                                "following":0,
                                                "favourites":0,
                                                "posts_count":2,
                                                "time_text":"0 segundos"},
                                            "total_posts":2,
                                            "user_followers":5,
                                            "user_following":0,
                                            "profile_privacy":true,
                                            "chat_privacy":true,
                                            "is_owner":true,
                                            "is_following":false,
                                            "is_reported":false,
                                            "is_blocked":false,
                                            "ami_blocked":false,
                                            "user_posts":[{
                                                "post_id":1415,
                                                "post_key":"",
                                                "user_id":1476,
                                                "description":"prueba1",
                                                "link":"",
                                                "thumbnail":"",
                                                "video_location":null,
                                                "youtube":"",
                                                "vimeo":"",
                                                "dailymotion":"",
                                                "playtube":"",
                                                "mp4":null,
                                                "time":"1631128952",
                                                "type":"image",
                                                "registered":"2021\/9",
                                                "views":0,
                                                "boosted":0,
                                                "stream_name":"",
                                                "live_time":0,
                                                "agora_resource_id":null,
                                                "agora_sid":null,
                                                "live_ended":0,
                                                "avatar":"https:\/\/diys.co\/media\/img\/d-avatar.jpg",
                                                "username":"example_",
                                                "likes":0,"votes":0,
                                                "media_set":[{
                                                    "id":695,
                                                    "post_id":1415,
                                                    "user_id":1476,
                                                    "file":"https:\/\/diys.co\/media\/upload\/photos\/2021\/09\/P8wjoIUhVFFKLBK28kdxMrDEpwb1xQOlahLgUt77Bmser1EvQt_08_c091b472c4ada84d2656d8af38fbe29d_image.jpg",
                                                    "extra":""}],
                                                "comments":[],
                                                "is_owner":true,
                                                "is_liked":false,
                                                "is_saved":false,
                                                "reported":false,
                                                "user_data":{
                                                    "user_id":1476,
                                                    "username":"example_",
                                                    "email":"example_@example.com",
                                                    "ip_address":"201.242.65.9",
                                                    "password":"$2y$10$OruQp07YGxejJ9SIwWMkpuT\/TIZ8aIt\/X6AiOOJkMbFPf.6gwb6DW",
                                                    "fname":"",
                                                    "lname":"",
                                                    "gender":"male",
                                                    "email_code":"c38872d6fc797062ff6de3e26d644f822a74682b",
                                                    "language":"spanish",
                                                    "avatar":"https:\/\/diys.co\/media\/img\/d-avatar.jpg",
                                                    "cover":"media\/img\/d-cover.jpg",
                                                    "country_id":0,"about":null,
                                                    "google":"","facebook":"",
                                                    "twitter":"","website":"","active":1,"admin":0,"verified":0,"last_seen":"1631793272",
                                                    "registered":"2021\/8",
                                                    "is_pro":0,"posts":0,"p_privacy":"2",
                                                    "c_privacy":"1","n_on_like":"1","n_on_mention":"1","n_on_comment":"1","n_on_follow":"1",
                                                    "n_on_comment_like":"1","n_on_comment_reply":"1","startup_avatar":0,"startup_info":0,"startup_follow":0,
                                                    "src":"","login_token":"","search_engines":"1","mode":"day",
                                                    "device_id":"6ec5b02b-d672-4e29-837a-7fcf0a4a4ad4","balance":"0","wallet":"0.00","conversation_id":"",
                                                    "referrer":0,"profile":1,"business_account":0,"paypal_email":"","b_name":"","b_email":"","b_phone":"","b_site":"",
                                                    "b_site_action":"","uploads":0,"address":"","city":"","state":"","zip":0,"phone_number":"","name":"example_",
                                                    "uname":"example_","url":"https:\/\/diys.co\/example_","edit":"https:\/\/diys.co\/settings\/general\/example_",
                                                    "followers":false,"following":false,"favourites":0,"posts_count":2},
                                                "is_verified":0,"is_should_hide":false,"name":"example_","time_text":"8 dias hace"},
                                            {"post_id":1414,"post_key":"","user_id":1476,"description":"prueba1","link":"","thumbnail":"","video_location":null,"youtube":"",
                                            "vimeo":"","dailymotion":"","playtube":"","mp4":null,"time":"1631122351","type":"image","registered":"2021\/9","views":0,"boosted":0,
                                            "stream_name":"","live_time":0,"agora_resource_id":null,"agora_sid":null,"live_ended":0,
                                            "avatar":"https:\/\/diys.co\/media\/im
         */

        refreshLayout.setOnRefreshListener(new LiquidRefreshLayout.OnRefreshListener() {
            @Override
            public void completeRefresh() {
            }

            @Override
            public void refreshing() {
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
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        JSONObject array = null;
                        try {
                            array = new JSONObject(mMessage);
                            JSONObject data = array.getJSONObject("data");
                            userData = data.getJSONObject("user_data");
                            name = userData.getString("name");
                            following = userData.getString("following");
                            followers = userData.getString("followers");
                            favourites = userData.getString("favourites");
                            avatar = userData.getString("avatar");
                            fname = userData.getString("fname");
                            lname = userData.getString("lname");
                            about = userData.getString("about");
                            website = userData.getString("website");

                            JSONArray userPosts = data.getJSONArray("user_posts");

                            for (int i = 0; i < userPosts.length(); i++) {
                                JSONObject post = userPosts.getJSONObject(i);
                                JSONArray postMedia = post.getJSONArray("media_set");
                                String postImageLink = postMedia.getString(0).split("diy")[1]
                                        .substring(3).split("\\.")[0].substring(1).replace("\\","");
                                String extension = postMedia.getString(0).split("diys")[1]
                                        .substring(3).split("\\.")[1].substring(0,3);

                                Log.e("PrFApiResponse", mMessage);
                                profileItems.add(new ProfileItem(
                                        postImageLink+"."+extension
                                ));

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
                                                "false"
                                        ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.finishRefreshing();
                                if(followers.equals("false")){
                                    followers = "0";
                                }
                                if(following.equals("false")){
                                    following = "0";
                                }
                                if(favourites.equals("false")){
                                    favourites = "0";
                                }
                                textViewFullname.setText(name);
                                textViewDescription.setText(about);
                                textViewNumberFollowing.setText(following);
                                textViewNumberFollowers.setText(followers);
                                textViewNumberFavorites.setText(favourites);
                                Glide.with(getActivity().getApplicationContext()).load(avatar)
                                        .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                                        .into(imageViewProfile);
                                adapterGrid = new ProfileAdapter(getContext(), postList, access_token);
                                recycler_view.setAdapter(adapterGrid);
                                adapterLinear = new PostAdapter(getContext(),
                                                postList,
                                                getActivity().getIntent().getStringExtra("access_token"),
                                                getActivity().getIntent().getStringExtra("access_token"));
                            }
                        });
                    }
                });

            }
        });

        imageButtonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler_view.setAdapter(adapterLinear);
                recycler_view.setLayoutManager(linearLayoutManager);
            }
        });
        imageButtonGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler_view.setHasFixedSize(true);
                recycler_view.setLayoutManager(gridLayoutManager);
                recycler_view.setAdapter(adapterGrid);

            }
        });
        textViewFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFollowers = new Intent(getActivity().getApplicationContext(), FollowersActivity.class);
                intentFollowers.putExtra("function", "followers");
                intentFollowers.putExtra("user_id", user_id);
                intentFollowers.putExtra("access_token", access_token);
                startActivity(intentFollowers);
            }
        });
        textViewFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFavorites = new Intent(getActivity().getApplicationContext(), FollowersActivity.class);
                intentFavorites.putExtra("function", "favorites");
                intentFavorites.putExtra("user_id", user_id);
                intentFavorites.putExtra("access_token", access_token);
                startActivity(intentFavorites);
            }
        });
        textViewFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFollowing = new Intent(getActivity().getApplicationContext(), FollowersActivity.class);
                intentFollowing.putExtra("function", "following");
                intentFollowing.putExtra("user_id", user_id);
                intentFollowing.putExtra("access_token", access_token);
                startActivity(intentFollowing);
            }
        });
        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Investigar cómo acceder a los botones del MainActivity. (última entrega)
            }
        });
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFollowing = new Intent(getActivity().getApplicationContext(), EditActivity.class);
                intentFollowing.putExtra("function", "following");
                intentFollowing.putExtra("user_id", user_id);
                intentFollowing.putExtra("access_token", access_token);
                intentFollowing.putExtra("fname", fname);
                intentFollowing.putExtra("lname", lname);
                intentFollowing.putExtra("about", about);
                intentFollowing.putExtra("website", website);
                startActivity(intentFollowing);
            }
        });
        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(intentMain);
            }
        });

        return itemView;
    }

    @Override
    public void onResume(){
        super.onResume();
        //OnResume Fragment
    }


}