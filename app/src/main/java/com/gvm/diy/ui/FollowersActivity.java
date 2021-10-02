package com.gvm.diy.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gvm.diy.R;
import com.gvm.diy.adapter.CommentsAdapter;
import com.gvm.diy.adapter.FollowAdapter;
import com.gvm.diy.models.CommentsItem;
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

public class FollowersActivity extends AppCompatActivity {
    private RecyclerView recycler_view;
    private TextView textViewTitle;
    private ImageButton imageButtonBack;
    GridLayoutManager gridLayoutManager;
    private List<FollowItem> followItems;
    private List<CommentsItem> commentsItems;

    LinearLayoutManager linearLayoutManager;
    String access_token,username, user_id, function, server_key = "1539874186", post_id, favourites, following, followers;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        recycler_view = findViewById(R.id.recycler_view);
        textViewTitle = findViewById(R.id.textViewTitle);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        progressBar = findViewById(R.id.progressBar);

        gridLayoutManager = new GridLayoutManager(FollowersActivity.this,2,GridLayoutManager.VERTICAL,false);
        linearLayoutManager = new LinearLayoutManager(FollowersActivity.this);

        recycler_view.setHasFixedSize(true);

        followItems = new ArrayList<>();
        commentsItems = new ArrayList<>();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");
        username = intent.getStringExtra("username");
        function = intent.getStringExtra("function");
        post_id = intent.getStringExtra("post_id");

        OkHttpClient client;

        RequestBody requestBody;
        Request UserPostsRequest;

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
                                Log.e("ApiResponse", post.getString("avatar")+post.getString("time_text")+post.getString("username"));
                                followItems.add(new FollowItem(
                                        post.getString("avatar"),
                                        post.getString("time_text"),
                                        post.getString("username")
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                FollowAdapter adapter = new FollowAdapter(FollowersActivity.this, followItems);
                                recycler_view.setAdapter(adapter);
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
                            JSONObject data = array.getJSONObject("data");

                            JSONArray userPosts = data.getJSONArray("user_posts");
                            JSONObject file = new JSONObject();

                            for (int i = 0; i < userPosts.length(); i++) {
                                JSONObject post = userPosts.getJSONObject(i);
                                Log.e("ApiResponse", post.getString("file"));
                                followItems.add(new FollowItem(
                                        mMessage
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                FollowAdapter adapter = new FollowAdapter(FollowersActivity.this, followItems);
                                recycler_view.setAdapter(adapter);
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
                                        post.getString("username")
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                FollowAdapter adapter = new FollowAdapter(FollowersActivity.this, followItems);
                                recycler_view.setAdapter(adapter);
                            }
                        });
                    }
                });
                break;
            case "likes":
                //TODO: Probar y quitar el botón del item
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
                                        post.getString("username")
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                FollowAdapter adapter = new FollowAdapter(FollowersActivity.this, followItems);
                                recycler_view.setAdapter(adapter);
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
                        //TODO: Verificar respuesta para obtener los datos requeridos
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
                                        user_id, post.getBoolean("is_liked")
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
                finish();
            }
        });


    }
}