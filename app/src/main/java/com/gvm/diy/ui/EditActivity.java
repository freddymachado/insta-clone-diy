package com.gvm.diy.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gvm.diy.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditActivity extends AppCompatActivity {
    ImageButton imageButtonBack;

    TextView textViewSave;

    EditText editTextName, editTextSurname, editTextAbout, editTextWeb;

    String access_token,username, user_id, website, server_key = "1539874186", name, surname, about, followers;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        textViewSave = findViewById(R.id.textViewSave);
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextAbout = findViewById(R.id.editTextAbout);
        editTextWeb = findViewById(R.id.editTextWeb);
        progressBar = findViewById(R.id.progressBar);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        user_id = intent.getStringExtra("user_id");
        website = intent.getStringExtra("website");
        name = intent.getStringExtra("fname");
        surname = intent.getStringExtra("lname");
        about = intent.getStringExtra("about");

        editTextName.setText(name);
        editTextSurname.setText(surname);
        editTextAbout.setText(about);
        editTextWeb.setText(website);

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                website = editTextWeb.getText().toString();
                name = editTextName.getText().toString();
                surname = editTextSurname.getText().toString();
                about = editTextAbout.getText().toString();

                OkHttpClient imageUploadClient = new OkHttpClient.Builder().build();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("server_key",server_key)
                        .addFormDataPart("access_token",access_token)
                        .addFormDataPart("fname",name)
                        .addFormDataPart("about",about)
                        .addFormDataPart("lname",surname)
                        .addFormDataPart("website",website)
                        .build();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://diys.co/endpoints/v1/settings/save_setting")
                        .post(requestBody)
                        .build();


                imageUploadClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(EditActivity.this, "Revisa tu conexión e inténtalo de nuevo: "+mMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e("failure Response", mMessage);
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) throws IOException {
                        final String mMessage = response.body().string();
                        Log.e("Like Response", mMessage);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(EditActivity.this, "Cambios guardados", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }
        });
    }
}