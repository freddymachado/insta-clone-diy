package com.gvm.diy.ui;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gvm.diy.R;
import com.gvm.diy.fragments.ChatFragment;
import com.gvm.diy.fragments.HomeFragment;
import com.gvm.diy.fragments.ProfileFragment;
import com.gvm.diy.fragments.SearchFragment;
import com.gvm.diy.fragments.UploadFragment;
import com.gvm.diy.models.CountingRequestBody;
import com.gvm.diy.models.Post;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Uploading..";
    TextView textViewHello;

    List<Post> postList;

    ImageView imageViewPhoto, imageViewVideo;

    RecyclerView recyclerView;

    ProgressBar progressBar;

    String access_token,username, password, server_key = "1539874186";

    private static final int PHOTO_SENT = 24;

    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        String user_id = intent.getStringExtra("user_id");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        savePrefsData(access_token, username, password);

        progressBar = findViewById(R.id.progressBar);

        SpaceNavigationView spaceNavigationView = findViewById(R.id.bottomNavigation);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_home_24));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_search_24));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_chat_24));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_baseline_person_24));

        int buttonSize = getResources().getDimensionPixelSize(R.dimen.action_button_size);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        imageViewPhoto = new ImageView(this);
        imageViewPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_photo_alternate_24));

        SubActionButton buttonUploadPhoto = itemBuilder.setContentView(imageViewPhoto).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_round)).build();

        imageViewVideo = new ImageView(this);
        imageViewVideo.setImageDrawable(getDrawable(R.drawable.ic_baseline_videocam_24));

        SubActionButton buttonUploadVideo = itemBuilder.setBackgroundDrawable(getDrawable(R.drawable.bg_round)).setContentView(imageViewVideo).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this).setStartAngle(-45).setEndAngle(-165).addSubActionView(buttonUploadPhoto)
                .setRadius(getResources().getDimensionPixelSize(R.dimen.action_button_size))
                .addSubActionView(buttonUploadVideo).attachTo(spaceNavigationView).build();

        //add default fragment - HOME Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new HomeFragment()).commit();

        buttonUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                OkHttpClient client = new OkHttpClient().newBuilder().build();

                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("server_key","1539874186")
                        .addFormDataPart("access_token",access_token)
                        .addFormDataPart("images","12345678")
                        .addFormDataPart("caption","12345678")
                        .build();
                Request request = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/post/new_post")
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
                thread.start();*/
                performFileSearch();
                actionMenu.toggle(true);


            }
        });
        buttonUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
                actionMenu.toggle(true);

            }
        });

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onCentreButtonClick() {
                spaceNavigationView.setCentreButtonSelectable(true);
                actionMenu.toggle(true);
                setFragment(new UploadFragment());
                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        }).check();
                Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.ACCESS_MEDIA_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        }).check();
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

    private void performFileSearch() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(i,"Selecciona una foto"),PHOTO_SENT);
    }

    private void savePrefsData(String access_token, String username, String password) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("access_token",access_token);
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();
        Log.d("prefsSaved",access_token+username+password);
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_SENT && resultCode == RESULT_OK){
            uri = null;
            if (data != null){
                uri = data.getData();
                Log.i(TAG,"Uri: "+uri.toString());
                uploadImage();

            }else{
                Log.i(TAG,"CHimbo");

            }
        }

    }

    private void uploadImage() {
        if (uri == null){
            Log.i(TAG,uriToFileName(uri));
            return;
        }

        final File imageFile = new File(uriToFileName(uri));
        Uri uris = Uri.fromFile(imageFile);
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString());
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        String imageName = imageFile.getName();

        Log.i(TAG,imageFile.getName()+" "+mime+" "+uriToFileName(uri));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("server_key",server_key)
                .addFormDataPart("caption","prueba1")
                .addFormDataPart("access_token",access_token)
                .addFormDataPart("images[]",imageName,
                        RequestBody.create(imageFile,MediaType.parse(mime)))
                .build();

        final CountingRequestBody.Listener progressListener = new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(long bytesRead, long contentLength) {
                if (bytesRead >= contentLength) {
                    if (progressBar != null)
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                } else {
                    if (contentLength > 0) {
                        final int progress = (int) (((double) bytesRead / contentLength) * 100);
                        if (progressBar != null)
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    progressBar.setVisibility(View.VISIBLE);
                                    progressBar.setProgress(progress);
                                }
                            });

                        if(progress >= 100){
                            progressBar.setVisibility(View.GONE);
                        }
                        Log.e("uploadProgress called", progress+" ");
                    }
                }
            }
        };

        OkHttpClient imageUploadClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        if (originalRequest.body() == null) {
                            return chain.proceed(originalRequest);
                        }
                        Request progressRequest = originalRequest.newBuilder()
                                .method(originalRequest.method(),
                                        new CountingRequestBody(originalRequest.body(), progressListener))
                                .build();

                        return chain.proceed(progressRequest);

                    }
                })
                .build();
        //TODO: Subir Videos y mejorar UI
        Request request = new Request.Builder()
                .url("https://diys.co/endpoints/v1/post/new_post")/*
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")*/
                .post(requestBody)
                .build();


        imageUploadClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                //Toast.makeText(ChatScreen.this, "Error uploading file", Toast.LENGTH_LONG).show();
                Log.e("failure Response", mMessage);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String mMessage = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("UploadResponse", mMessage);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

    }

    private  String uriToFileName(Uri uri){
        String path = null;
        path = getFilePath(MainActivity.this, uri);
        return path;
    }

    public String getFilePath(Context context, Uri uri) {
        //Log.e("uri", uri.getPath());
        String filePath = "";
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            //Log.e("wholeID", wholeID);
            // Split at colon, use second item in the array
            String[] splits = wholeID.split(":");
            if (splits.length == 2) {
                String id = splits[1];

                String[] column = {MediaStore.Images.Media.DATA};
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        } else {
            filePath = uri.getPath();
        }
        return filePath;
    }
}