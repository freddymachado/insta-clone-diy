package com.gvm.diy.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gvm.diy.MyBounceInterpolator;
import com.gvm.diy.R;
import com.gvm.diy.adapter.PostAdapter;
import com.gvm.diy.adapter.ProfileAdapter;
import com.gvm.diy.models.Post;
import com.gvm.diy.models.ProfileItem;
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

public class ProfileViewerActivity extends AppCompatActivity {
    //TODO: Probar ProfileViewer

    ImageButton imageButtonBack, imageButtonMore, imageButtonGrid, imageButtonWeb, imageButtonList;
    RoundedImageView roundedImageViewAvatar;
    TextView textViewName, textViewUser, textViewNumberFollowers, textViewFollowers, textViewNumberFollowing,
            textViewFollowing, textViewNumberFavorites, textViewFavorites, textViewDescription;
    Button buttonFollowing, buttonMessage;

    RecyclerView recyclerView;
    private List<ProfileItem> profileItems;
    private List<Post> postList;

    String access_token, avatar, user_id, server_key = "1539874186", name, favourites, following, followers, web;

    Boolean is_liked, is_saved;

    AlertDialog.Builder builder;


    OkHttpClient client;
    RequestBody requestBody;
    Request UserPostsRequest;

    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    JSONObject userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");
        web = intent.getStringExtra("web");

        imageButtonBack = findViewById(R.id.imageButtonBack);
        imageButtonMore = findViewById(R.id.imageButtonMore);
        imageButtonGrid = findViewById(R.id.imageButtonGrid);
        imageButtonWeb = findViewById(R.id.imageButtonWeb);
        imageButtonList = findViewById(R.id.imageButtonList);

        if(web.isEmpty()){
            imageButtonWeb.setVisibility(View.GONE);
        }

        roundedImageViewAvatar = findViewById(R.id.imageViewAvatar);

        textViewName = findViewById(R.id.textViewName);
        textViewUser = findViewById(R.id.textViewUser);
        textViewNumberFollowers = findViewById(R.id.textViewNumberFollowers);
        textViewFollowers = findViewById(R.id.textViewFollowers);
        textViewNumberFollowing = findViewById(R.id.textViewNumberFollowing);
        textViewFollowing = findViewById(R.id.textViewFollowing);
        textViewNumberFavorites = findViewById(R.id.textViewNumberFavorites);
        textViewFavorites = findViewById(R.id.textViewFavorites);
        textViewDescription = findViewById(R.id.textViewDescription);

        buttonFollowing = findViewById(R.id.buttonFollowing);
        buttonMessage = findViewById(R.id.buttonMessage);

        recyclerView = findViewById(R.id.recyclerView);

        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2,GridLayoutManager.VERTICAL,false);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        profileItems = new ArrayList<>();
        postList = new ArrayList<>();

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ProfileViewerActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
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

                    JSONArray userPosts = data.getJSONArray("user_posts");
                    JSONObject file = new JSONObject();

                    for (int i = 0; i < userPosts.length(); i++) {
                        JSONObject post = userPosts.getJSONObject(i);
                        JSONArray postMedia = post.getJSONArray("media_set");
                        //Por alguna razón el jsonArray postMedia tiene la info en string plano.
                        String postImageLink = postMedia.getString(0).split("diy")[1]
                                .substring(3).split("\\.")[0].substring(1).replace("\\","");
                        String extension = postMedia.getString(0).split("diys")[1]
                                .substring(3).split("\\.")[1].substring(0,3);

                        Log.e("PVApiResponse", postImageLink+"."+extension);

                        profileItems.add(new ProfileItem(
                                postImageLink+"."+extension
                        ));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewName.setText(name);
                        textViewNumberFollowing.setText(following);
                        textViewNumberFollowers.setText(followers);
                        textViewNumberFavorites.setText(favourites);
                        Glide.with(getApplicationContext()).load(avatar)
                                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                                .into(roundedImageViewAvatar);
                        ProfileAdapter adapter = new ProfileAdapter(getApplicationContext(), profileItems);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });

        builder = new AlertDialog.Builder(ProfileViewerActivity.this);
        builder.setTitle("Post")
                .setItems(new String[]{"Reportar Post", "Copiar"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            //TODO: Verificar comportamiento
                            case 0:
                                break;
                            case 1:
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    public void postClick(View view) {

        //Cargamos la animcion del boton
        final Animation myAnim = AnimationUtils.loadAnimation(ProfileViewerActivity.this, R.anim.bounce);

        //Usamos el BounceInterpolator con una amplitud de 0.2 y frecuencia de 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);


        switch (view.getId()) {

            case R.id.imageButtonBack:
                finish();
                break;

            case R.id.imageButtonMore:
                builder.show();
                break;

            case R.id.imageButtonGrid:
                recyclerView.setLayoutManager(gridLayoutManager);
                break;

            case R.id.imageButtonWeb:
                //TODO: Ver si funciona la web
                    Uri uriWeb = Uri.parse(web);
                    Intent intentWeb = new Intent(Intent.ACTION_VIEW, uriWeb);
                    startActivity(intentWeb);
                break;

            case R.id.imageButtonList:
                //TODO: Probar
                client.newCall(UserPostsRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProfileViewerActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        Log.e("ApiResponse", mMessage);
                        JSONObject array = null;
                        try {
                            array = new JSONObject(mMessage);
                            JSONObject data = array.getJSONObject("data");

                            JSONArray userPosts = data.getJSONArray("user_posts");

                            for (int i = 0; i < userPosts.length(); i++) {
                                JSONObject post = userPosts.getJSONObject(i);

                                JSONArray postMedia = post.getJSONArray("media_set");
                                String postImageLink = postMedia.getString(0).split("diy")[1]
                                        .substring(3).split("\\.")[0].substring(1).replace("\\","");
                                String extension = postMedia.getString(0).split("diys")[1]
                                        .substring(3).split("\\.")[1].substring(0,3);

                                Log.e("PVApiResponse", postImageLink+"."+extension);

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
                                        post.getString("post_id")
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PostAdapter adapter = new PostAdapter(ProfileViewerActivity.this,
                                        postList,
                                        access_token, "PV");
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(linearLayoutManager);
                            }
                        });
                    }
                });
                break;


            case R.id.textViewFollowers:
                //TODO: Probar
                Intent intentFollowers = new Intent(getApplicationContext(), FollowersActivity.class);
                intentFollowers.putExtra("function", "followers");
                intentFollowers.putExtra("user_id", user_id);
                intentFollowers.putExtra("access_token", access_token);
                startActivity(intentFollowers);
                break;

            case R.id.textViewFollowing:
                //TODO: Probar
                Intent intentFollowing = new Intent(getApplicationContext(), FollowersActivity.class);
                intentFollowing.putExtra("function", "following");
                intentFollowing.putExtra("user_id", user_id);
                intentFollowing.putExtra("access_token", access_token);
                startActivity(intentFollowing);
                break;


            case R.id.textViewFavorites:
                //TODO: Probar
                Intent intentFavorites = new Intent(getApplicationContext(), FollowersActivity.class);
                intentFavorites.putExtra("function", "favorites");
                intentFavorites.putExtra("user_id", user_id);
                intentFavorites.putExtra("access_token", access_token);
                startActivity(intentFavorites);
                break;


            case R.id.buttonFollowing:
                //TODO: Verificar funcionamiento

                break;


            case R.id.buttonMessage:
                //TODO: Verificar funcionamiento

                break;

            default:
                break;
        }

    }
}