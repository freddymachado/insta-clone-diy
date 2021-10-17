package com.gvm.diy.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.gvm.diy.R;
import com.gvm.diy.adapter.AdaptadorChat;
import com.gvm.diy.fragments.UploadFragment;
import com.gvm.diy.models.ChatList;
import com.gvm.diy.models.MensajeEnviar;
import com.gvm.diy.models.MensajeRecibido;
import com.gvm.diy.models.TimelineItem;
import com.madapps.liquid.LiquidRefreshLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity{

    TextView textViewTitle;

    String lastMessage, userName = "cargando..", TAG = "fcm";

    ImageView imageViewLogo;

    Button buttonSalir, buttonChat, buttonCerrar, buttonActivar;

    EditText editTextChat;

    String id, token, username;

    RecyclerView recyclerChat;

    int ADMIN = 1, USER = 2;

    private ImageButton imageButtonId, imageButtonBack, imageButtonMore;

    private static final int PHOTO_SENT = 1;

    private AdaptadorChat adapter;

    private Boolean chatIniciado = false;

    int whoIsSendingMessages;
    private List<TimelineItem> mData = new ArrayList<>();

    AlertDialog alertDialog;

    String adminid = "aminid";

    ProgressBar progressBar;

    LiquidRefreshLayout refreshLayout;

    String access_token,hash, user_id, avatar, server_key = "1539874186", name, favourites, following, followers;

    OkHttpClient client;
    RequestBody requestBody, FollowBody;
    Request UserPostsRequest, FollowRequest, request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Inicio del setLayout8

        editTextChat = findViewById(R.id.editTextChat);

        recyclerChat = findViewById(R.id.recyclerChat);

        textViewTitle = findViewById(R.id.textViewTitle);

        buttonChat = findViewById(R.id.buttonChat);

        imageButtonId = findViewById(R.id.imageButtonId);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        imageButtonMore = findViewById(R.id.imageButtonMore);

        progressBar = findViewById(R.id.progressBar);
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerChat = findViewById(R.id.recyclerChat);

        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");

        adapter = new AdaptadorChat(this,mData);
        LinearLayoutManager l = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(l);
        recyclerChat.setAdapter(adapter);

        client = new OkHttpClient().newBuilder().build();
        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("server_key",server_key)
                .addFormDataPart("user_id",user_id)
                .addFormDataPart("access_token",access_token)
                .build();

        android.app.AlertDialog.Builder builder;
        builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Post")
                .setItems(new String[]{"Bloquear","Limpiar Chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                progressBar.setVisibility(View.VISIBLE);
                                //TODO: Probar cuando pueda debuggear con varios users
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
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(ChatActivity.this, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
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
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(ChatActivity.this, "usuario bloqueado", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        Log.e("Like Response", mMessage);
                                    }
                                });
                                break;
                            case 1:
                                progressBar.setVisibility(View.VISIBLE);
                                //TODO: Probar
                                request = new Request.Builder()
                                        .url("https://diys.co/endpoints/v1/messages/clear_messages")
                                        .post(requestBody)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        String mMessage = e.getMessage().toString();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(ChatActivity.this, "Error de red: " + mMessage, Toast.LENGTH_LONG).show();
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
                                                mData.clear();
                                                progressBar.setVisibility(View.GONE);
                                                adapter.notifyItemInserted(adapter.getItemCount());
                                                recyclerChat.smoothScrollToPosition(adapter.getItemCount());
                                            }
                                        });
                                        Log.e("Like Response", mMessage);
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    }
                });

        //Iniciamos la solicitud para obtener los datos del usuario
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("server_key",server_key)
                .addFormDataPart("access_token",access_token)
                .addFormDataPart("user_id",user_id)
                .build();

        okhttp3.Request UserPostsRequest = new Request.Builder()
                .url("https://diys.co/endpoints/v1/messages/get_user_messages")
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
                    JSONObject data = array.getJSONObject("data");
                    JSONObject user_data = array.getJSONObject("user_data");
                    JSONArray messages = array.getJSONArray("messages");

                    avatar = user_data.getString("avatar");
                    username = user_data.getString("username");

                    //TODO: Dependiendo de si el mensaje es recibido o enviado, se coloca MensajeRecibido
                    //o TextItem al TimelineItem respectivamente.
                    for (int i = 0; i < messages.length(); i++) {
                        JSONObject message = messages.getJSONObject(i);
                        Log.e("ApiResponse", user_data.getString("avatar")+user_data.getString("username"));
                        if(message.getInt("from_id")==Integer.parseInt(user_id)){
                            mData.add(new TimelineItem(new TextItem(
                                    message.getString("text"),
                                    user_id,
                                    avatar,
                                    message.getLong("time"),
                                    message.getInt("to_id"),
                                    message.getInt("from_id")
                                    )
                            ));

                        }else{
                            mData.add(new TimelineItem(new MensajeRecibido(
                                    message.getString("text"),
                                    user_id,
                                    avatar,
                                    message.getLong("time"),
                                    message.getInt("to_id"),
                                    message.getInt("from_id")
                                    )
                            ));
                        }
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
                    Log.e("causaException",e.getCause().toString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        textViewTitle.setText(username);
                        adapter.notifyItemInserted(adapter.getItemCount());
                        recyclerChat.smoothScrollToPosition(adapter.getItemCount());
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
                mData.clear();
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

                            //TODO: Dependiendo de si el mensaje es recibido o enviado, se coloca MensajeRecibido
                            //o TextItem al TimelineItem respectivamente.
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject post = data.getJSONObject(i);
                                Log.e("ApiResponse", user_data.getString("avatar")+user_data.getString("username"));
                                if(message.getInt("from_id")==Integer.parseInt(user_id)){
                                    mData.add(new TimelineItem(new TextItem(
                                            message.getString("text"),
                                            user_id,
                                            avatar,
                                            message.getLong("time"),
                                            message.getInt("to_id"),
                                            message.getInt("from_id")
                                            )
                                    ));

                                }else{
                                    mData.add(new TimelineItem(new MensajeRecibido(
                                            message.getString("text"),
                                            user_id,
                                            avatar,
                                            message.getLong("time"),
                                            message.getInt("to_id"),
                                            message.getInt("from_id")
                                            )
                                    ));
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.finishRefreshing();

                                textViewTitle.setText(username);
                                progressBar.setVisibility(View.GONE);
                                adapter.notifyItemInserted(adapter.getItemCount());
                                recyclerChat.smoothScrollToPosition(adapter.getItemCount());
                            }
                        });
                    }
                });

            }
        });

        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Iniciamos la solicitud para obtener los datos del usuario
                OkHttpClient client = new OkHttpClient().newBuilder().build();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("access_token",access_token)
                        .addFormDataPart("user_id",user_id)
                        .addFormDataPart("text",editTextChat.getText().toString())
                        .build();

                okhttp3.Request UserPostsRequest = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/messages/send_message")
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
                        Log.e("ChatResponse", mMessage);
                        JSONObject array = null;
                        Long ts = System.currentTimeMillis()/1000;
                        try {
                            array = new JSONObject(mMessage);
                            JSONArray data = array.getJSONArray("data");

                            mData.add(new TimelineItem(new MensajeRecibido(
                                    editTextChat.getText().toString(),
                                    user_id,
                                    "",
                                    ts
                            )
                            ));
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
                                adapter.notifyItemInserted(adapter.getItemCount());
                                recyclerChat.smoothScrollToPosition(adapter.getItemCount());
                            }
                        });
                    }
                });
                recyclerChat.smoothScrollToPosition(adapter.getItemCount());
                editTextChat.setText("");
            }
        });

        imageButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SENT);
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ChatActivity.this,SalaChatActivity.class));
                finish();
            }
        });
       /* adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });    private void */


    }
}