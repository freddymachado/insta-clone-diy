package com.gvm.diy.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gvm.diy.R;

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
import okhttp3.ResponseBody;

public class RegisterActivity extends AppCompatActivity {
    String TAG = "RegisterActivity", access_token, user_id;
    JSONObject token;

    EditText editTextPass, editTextEmail, editTextUsername, editTextConfirmPassword;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextPass = findViewById(R.id.editTextPass);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        editTextEmail.setText("jnfjdn@gmail.com");
        editTextUsername.setText("jdbgjsg");
        editTextPass.setText("1234567");
        editTextConfirmPassword.setText("1234567");

        progressBar = findViewById(R.id.progressBar);
    }

    public void register(View view){
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("server_key","1539874186")
                .addFormDataPart("username", String.valueOf(editTextUsername.getText()))
                .addFormDataPart("password", String.valueOf(editTextPass.getText()))
                .addFormDataPart("conf_password", String.valueOf(editTextConfirmPassword.getText()))
                .addFormDataPart("email", String.valueOf(editTextEmail.getText()))/*
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

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Revisa tu conexión e inténtalo de nuevo "+e, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    Log.d(TAG, String.valueOf(jsonObject));
                    if(jsonObject.getString("code").equals("200")){
                        token = jsonObject.getJSONObject("data");
                        access_token =token.getString("access_token");
                        user_id =token.getString("user_id");
                        goMain(user_id,access_token);
                    }else {
                        String error = jsonObject.getJSONObject("errors").getString("error_text");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Revisa tu conexión e inténtalo de nuevo "+e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
/*
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    JSONArray jsonObject = new JSONArray(response.body().string());
                    Log.d(TAG, String.valueOf(jsonObject));
                    if(jsonObject.getString(1).equals("200")){
                        token = jsonObject.getJSONObject(2);
                        access_token =token.getString("access_token");
                        user_id =token.getString("user_id");
                        goMain(user_id,access_token);
                    }else {
                        String error = jsonObject.getJSONObject(1).getString("error_text");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Revisa tu conexión e inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        thread.start();*/
    }

    private void goMain(String user_id, String access_token) {
        Toast.makeText(RegisterActivity.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
        Intent intentMain = new Intent(RegisterActivity.this, MainActivity.class);
        intentMain.putExtra("access_token", access_token);
        intentMain.putExtra("user_id", user_id);
        startActivity(intentMain);
        finish();
    }

    public void goLogin(View view) {
        Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intentLogin);
        finish();
    }
}