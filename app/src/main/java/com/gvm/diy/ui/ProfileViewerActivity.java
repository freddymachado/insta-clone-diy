package com.gvm.diy.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.ProgressBar;
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

    String access_token, avatar, user_id, server_key = "1539874186", name, favourites, following,
            followers, web, about, username, isFollowing, isFollowingN, website;

    Boolean is_liked, is_saved;

    AlertDialog.Builder builder;

    OkHttpClient client;
    RequestBody requestBody, FollowBody;
    Request UserPostsRequest, FollowRequest, request;

    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    JSONObject userData;

    ProfileAdapter adapterGrid;
    PostAdapter adapterLinear;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");
        web = intent.getStringExtra("web");;
        name = intent.getStringExtra("name");
        about = intent.getStringExtra("about");
        username = intent.getStringExtra("username");
        avatar = intent.getStringExtra("avatar");
        isFollowing = intent.getStringExtra("isFollowing");

        imageButtonBack = findViewById(R.id.imageButtonBack);
        imageButtonMore = findViewById(R.id.imageButtonMore);
        imageButtonGrid = findViewById(R.id.imageButtonGrid);
        imageButtonWeb = findViewById(R.id.imageButtonWeb);
        imageButtonList = findViewById(R.id.imageButtonList);

        progressBar = findViewById(R.id.progressBar);

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

        if(isFollowing.equals("false")){
            buttonFollowing.setText("Follow");            
        }
        recyclerView = findViewById(R.id.recyclerView);

        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2,GridLayoutManager.VERTICAL,false);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        profileItems = new ArrayList<>();
        postList = new ArrayList<>();

        textViewName.setText(name);
        textViewUser.setText(username);
        textViewDescription.setText(about);
        Glide.with(getApplicationContext()).load(avatar)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(roundedImageViewAvatar);


        ClipboardManager clipboardManager = (ClipboardManager) ProfileViewerActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);

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
                        progressBar.setVisibility(View.GONE);
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
                    following = userData.getString("following");
                    followers = userData.getString("followers");
                    favourites = userData.getString("favourites");

                    isFollowingN = data.getString("is_following");

                    JSONArray userPosts = data.getJSONArray("user_posts");
 
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
                                        favourites, about, web,
                                        isFollowingN
                                ));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        adapterGrid = new ProfileAdapter(getApplicationContext(), profileItems);
                        recyclerView.setAdapter(adapterGrid);
                        adapterLinear = new PostAdapter(ProfileViewerActivity.this,
                                        postList,
                                        access_token, "PV");
                        textViewNumberFollowing.setText(following);
                        textViewNumberFollowers.setText(followers);
                        textViewNumberFavorites.setText(favourites);
                    }
                });
            }
        });

        builder = new AlertDialog.Builder(ProfileViewerActivity.this);
        builder.setTitle("Post")
                .setItems(new String[]{"Reportar usuario", "Bloquear usuario", "Copiar link del perfil"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            //TODO: Verificar comportamiento
                            case 0:

                                request = new Request.Builder()
                                        .url("https://diys.co/endpoints/v1/user/report_user")
                                        .post(requestBody)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        String mMessage = e.getMessage().toString();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ProfileViewerActivity.this, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        Log.e("failure Response", mMessage);
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final String mMessage = response.body().string();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ProfileViewerActivity.this, "usuario reportado", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        Log.e("Like Response", mMessage);
                                    }
                                });
                                break;
                            case 1:

                                request = new Request.Builder()
                                        .url("https://diys.co/endpoints/v1/user/block")
                                        .post(requestBody)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        String mMessage = e.getMessage().toString();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ProfileViewerActivity.this, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        Log.e("failure Response", mMessage);
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final String mMessage = response.body().string();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ProfileViewerActivity.this, "usuario bloqueado", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        Log.e("Like Response", mMessage);
                                    }
                                });
                                break;
                            case 2:
                                ClipData clip = ClipData.newPlainText("ir al perfil","https://diys.co//post/"+user_id);

                                Toast.makeText(ProfileViewerActivity.this, "Texto copiado en el portapapeles", Toast.LENGTH_SHORT).show();
                                clipboardManager.setPrimaryClip(clip);
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
                recyclerView.setAdapter(adapterGrid);
                break;

            case R.id.imageButtonWeb:
                //TODO: Cambiar mimundodemoda
                    Uri uriWeb = Uri.parse(web);
                    Intent intentWeb = new Intent(Intent.ACTION_VIEW, uriWeb);
                    startActivity(intentWeb);
                break;

            case R.id.imageButtonList:
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapterLinear);
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
                //TODO: Probar funcionamiento
                buttonFollowing.startAnimation(myAnim);
                if (buttonFollowing.getText().equals("following")) {
                    buttonFollowing.setText("follow");
                } else {
                    buttonFollowing.setText("following");
                }
                FollowBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key", server_key)
                        .addFormDataPart("user_id", user_id)
                        .addFormDataPart("access_token", access_token)
                        .build();

                FollowRequest = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/user/follow")
                        .post(FollowBody)
                        .build();

                client.newCall(FollowRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                buttonFollowing.startAnimation(myAnim);
                                if (buttonFollowing.getText().equals("following")) {
                                    buttonFollowing.setText("follow");
                                } else {
                                    buttonFollowing.setText("following");
                                }
                                Toast.makeText(ProfileViewerActivity.this, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();

                            }
                        });
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        Log.e("Like Response", mMessage);
                        JSONObject array = null;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                });

                break;


            case R.id.buttonMessage:
                //TODO: Funcion de mensajería (last entrega)

                break;

            default:
                break;
        }

    }
}