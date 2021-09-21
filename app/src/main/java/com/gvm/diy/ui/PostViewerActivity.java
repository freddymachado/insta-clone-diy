package com.gvm.diy.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.gvm.diy.MyBounceInterpolator;
import com.gvm.diy.R;
import com.gvm.diy.adapter.ProfileAdapter;
import com.gvm.diy.models.ProfileItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostViewerActivity extends AppCompatActivity {
    //TODO: Probar diseño PostViewerActivity
    //TODO: BottomBar que llame los métodos del MainActivity (4ta entrega?)
    ImageButton imageButtonBack, imageButtonMore;

    ImageView user_profile_image, post_image, imageViewLike, imageViewComment, imageViewReply, imageViewFav;

    TextView username, textViewDescription, textViewLikes, textViewComments;

    String access_token, post_id, avatar, server_key = "1539874186", name, favourites, following, followers;

    Boolean is_liked, is_saved;

    AlertDialog.Builder builder;

    OkHttpClient client;
    RequestBody body;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        imageButtonMore = findViewById(R.id.imageButtonMore);
        user_profile_image = findViewById(R.id.user_profile_image);
        post_image = findViewById(R.id.post_image);
        imageViewLike = findViewById(R.id.imageViewLike);
        imageViewComment = findViewById(R.id.imageViewComment);
        imageViewReply = findViewById(R.id.imageViewReply);
        imageViewFav = findViewById(R.id.imageViewFav);
        username = findViewById(R.id.username);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewLikes = findViewById(R.id.textViewLikes);
        textViewComments = findViewById(R.id.textViewComments);

        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        post_id = intent.getStringExtra("post_id");
        intent.getBooleanExtra("is_liked", is_liked);
        intent.getBooleanExtra("is_saved", is_saved);
        Log.e("isLiked", is_liked.toString() + is_saved.toString());

        client = new OkHttpClient().newBuilder().build();

        builder = new AlertDialog.Builder(PostViewerActivity.this);
        builder.setTitle("Post")
                .setItems(new String[]{"Reportar Post", "Copiar"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("server_key", server_key)
                                .addFormDataPart("post_id", post_id)
                                .addFormDataPart("access_token", access_token)
                                .build();

                        request = new Request.Builder()
                                .url("https://diys.co/endpoints/v1/post/report_post")
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                String mMessage = e.getMessage().toString();
                                Toast.makeText(PostViewerActivity.this, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
                                Log.e("failure Response", mMessage);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String mMessage = response.body().string();
                                Log.e("Like Response", mMessage);
                            }
                        });
                        break;
                    case 1:
                        //TODO: Verificar comportamiento
                        break;
                    default:
                        break;
                }
            }
        });

        if (is_liked)
            imageViewLike.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_red));
        if (is_saved)
            imageViewFav.setImageDrawable(getDrawable(R.drawable.ic_baseline_star_yellow));
    }


    public void postClick(View view) {

        //Cargamos la animcion del boton
        final Animation myAnim = AnimationUtils.loadAnimation(PostViewerActivity.this, R.anim.bounce);

        //Usamos el BounceInterpolator con una amplitud de 0.2 y frecuencia de 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);


        switch (view.getId()) {

            case R.id.imageButtonBack:
                imageButtonBack.startAnimation(myAnim);
                finish();
                break;

            case R.id.imageButtonMore:
                //TODO: Probar MoreDialog
                imageButtonMore.startAnimation(myAnim);
                builder.show();
                Toast.makeText(PostViewerActivity.this, "more", Toast.LENGTH_SHORT).show();
                break;

            case R.id.username:
                username.startAnimation(myAnim);
                Intent intentProfileViewer = new Intent(PostViewerActivity.this, ProfileViewerActivity.class);
                startActivity(intentProfileViewer);
                break;

            case R.id.textViewDescription:
                //TODO: Alargar descripcion
                break;

            case R.id.imageViewLike:
                //TODO: Probar
                body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key", server_key)
                        .addFormDataPart("post_id", post_id)
                        .addFormDataPart("access_token", access_token)
                        .build();

                request = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/like_post")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        Toast.makeText(PostViewerActivity.this, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
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
                                imageViewLike.startAnimation(myAnim);
                                if (is_liked) {
                                    is_liked = false;
                                    imageViewLike.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_border_24));
                                } else {
                                    is_liked = true;
                                    imageViewLike.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_red));
                                }

                            }
                        });
                    }
                });
                break;


            case R.id.textViewLikes:
                //TODO: Probar
                Intent intentLikes = new Intent(PostViewerActivity.this, FollowersActivity.class);
                intentLikes.putExtra("function", "likes");
                intentLikes.putExtra("post_id", post_id);
                startActivity(intentLikes);
                break;

            case R.id.imageViewComment:
            case R.id.textViewComments:
                //TODO: Probar
                Intent intentComments = new Intent(PostViewerActivity.this, FollowersActivity.class);
                intentComments.putExtra("function", "comments");
                intentComments.putExtra("post_id", post_id);
                intentComments.putExtra("access_token", access_token);
                startActivity(intentComments);
                break;


            case R.id.imageViewReply:
                //TODO: Verificar funcionamiento
                break;


            case R.id.imageViewFav:
                //TODO: Probar

                body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key", server_key)
                        .addFormDataPart("post_id", post_id)
                        .addFormDataPart("access_token", access_token)
                        .build();

                request = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/add_to_favorite")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        Toast.makeText(PostViewerActivity.this, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String mMessage = response.body().string();
                        Log.e("fav Response", mMessage);
                        JSONObject array = null;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageViewFav.startAnimation(myAnim);
                                if (is_saved) {
                                    is_saved = false;
                                    imageViewLike.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_border_24));
                                } else {
                                    is_saved = true;
                                    imageViewLike.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_red));
                                }

                            }
                        });
                    }
                });
                break;

            default:
                break;
        }

    }


}
