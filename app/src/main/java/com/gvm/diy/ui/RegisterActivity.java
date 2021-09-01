package com.gvm.diy.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gvm.diy.R;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    String TAG = "RegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View view){
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("server_key","473298749")
                .addFormDataPart("username","example_")
                .addFormDataPart("password","12345678")
                .addFormDataPart("conf_password","12345678")
                .addFormDataPart("email","example_@example.com")/*
                .addFormDataPart("access_token","1d1ae6d02edb7d9ef2dbc305b184eb2e08518d251579769480857c0547956829822027d585cce3e5bd")
                .addFormDataPart("offset","633")
                .addFormDataPart("limit","2")
                .addFormDataPart("v","1")
                .addFormDataPart("resource","post")
                .addFormDataPart("resource_id","fetch_home_posts")*/
                .build();
        Request request = new Request.Builder()
                .url("https://diys.co/endpoints/v1/auth/register")
                .method("POST",body)
                .build();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    Log.d(TAG, response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }
}