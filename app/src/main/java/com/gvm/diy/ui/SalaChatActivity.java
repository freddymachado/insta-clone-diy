package com.gvm.diy.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gvm.diy.R;
import com.gvm.diy.adapter.AdaptadorChat;
import com.gvm.diy.adapter.ChatListAdapter;
import com.gvm.diy.models.ChatList;
import com.gvm.diy.models.MensajeRecibido;
import com.gvm.diy.models.TimelineItem;
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

public class SalaChatActivity extends AppCompatActivity implements ChatListAdapter.SelectedChat{

    RecyclerView recyclerView;

    List<ChatList> chatList = new ArrayList();

    ChatListAdapter chatAdapter;

    String id;
    String access_token,hash, user_id, avatar, server_key = "1539874186", name, favourites, following, followers;

    ProgressBar progressBar;

    LiquidRefreshLayout refreshLayout;

    int chats = 1, historial = 2;

    int whichFunction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_chat);

        progressBar = findViewById(R.id.progressBar);
        refreshLayout = findViewById(R.id.refreshLayout);

        recyclerView = findViewById(R.id.recyclerSalaChat);

        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        chatAdapter = new ChatListAdapter(chatList,this);
        recyclerView.setAdapter(chatAdapter);


        //Iniciamos la solicitud para obtener los datos del usuario
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("server_key",server_key)
                .addFormDataPart("access_token",access_token)
                .build();

        okhttp3.Request UserPostsRequest = new Request.Builder()
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
                        Log.e("ApiResponse", post.getString("avatar")+post.getString("time_text")+post.getString("username"));
                        Long codigoHora = Long.valueOf(post.getString("time"));
                        Date time = new Date(codigoHora);
                        SimpleDateFormat sFormat = new SimpleDateFormat("hh:mm a");//a = pm o am
                        ChatList chat = new ChatList(
                                post.getString("name"),
                                sFormat.format(time),
                                post.getString("message"),
                                id);

                        Log.d("idguardado", id);

                        chatList.add(chat);

                        recyclerView.setAdapter(chatAdapter);
                        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());

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
                        chatAdapter.notifyItemInserted(chatAdapter.getItemCount());
                        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
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
                                Log.e("ApiResponse", post.getString("avatar")+post.getString("time_text")+post.getString("username"));
                                Long codigoHora = Long.valueOf(post.getString("time"));
                                Date time = new Date(codigoHora);
                                SimpleDateFormat sFormat = new SimpleDateFormat("hh:mm a");//a = pm o am
                                ChatList chat = new ChatList(
                                        post.getString("name"),
                                        sFormat.format(time),
                                        post.getString("message"),
                                        id);

                                Log.d("idguardado", id);

                                chatList.add(chat);

                                recyclerView.setAdapter(chatAdapter);
                                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());

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
                                chatAdapter.notifyItemInserted(chatAdapter.getItemCount());
                                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void selectedChat(ChatList chatList, String id) {
        Log.d("idenviado",chatList.toString());
        startActivity(new Intent(SalaChatActivity.this,ChatActivity.class).putExtra("userId",chatList));
        finish();

    }
}