package com.gvm.diy.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gvm.diy.R;
import com.gvm.diy.fragments.ChatFragment;
import com.gvm.diy.fragments.HomeFragment;
import com.gvm.diy.fragments.ProfileFragment;
import com.gvm.diy.fragments.SearchFragment;
import com.gvm.diy.fragments.UploadFragment;
import com.gvm.diy.models.Post;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView textViewHello;

    List<Post> postList;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String access_token = intent.getStringExtra("access_token");
        String user_id = intent.getStringExtra("user_id");


        SpaceNavigationView spaceNavigationView = findViewById(R.id.bottomNavigation);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_home_24));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_search_24));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_chat_24));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_person_24));

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        SubActionButton button1 = itemBuilder.build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this).addSubActionView(button1).attachTo(spaceNavigationView).build();

        //add default fragment - HOME Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new HomeFragment()).commit();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient().newBuilder().build();

                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("server_key","473298749")
                        .addFormDataPart("access_token",access_token)
                        .addFormDataPart("images","12345678")
                        .addFormDataPart("caption","12345678")/*
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
        });

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                spaceNavigationView.setCentreButtonSelectable(true);
                //TODO: Probar que se abre el actionMenu, si no, probar con toggle y si no, hacer el bottomBar manualmente.
                actionMenu.open(true);
                setFragment(new UploadFragment());
                Toast.makeText(MainActivity.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                switch(itemIndex){
                    case 0:
                        setFragment(new HomeFragment());
                        break;
                    case 1:
                        setFragment(new SearchFragment());
                        break;
                    case 2:
                        setFragment(new ChatFragment());
                        break;
                    case 3:
                        setFragment(new ProfileFragment());
                        break;

                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
        fragmentTransaction.commit();
    }
}