package com.gvm.diy.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gvm.diy.R;
import com.gvm.diy.models.CountingRequestBody;
import com.gvm.diy.ui.MainActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadFragment extends Fragment {
    private static final String TAG = "UploadFragment";
    Uri resultUri;

    TextView textViewPublish, textViewName, textViewUser;
    ImageButton imageButtonBack;
    RoundedImageView roundedImageViewAvatar;

    ProgressBar progressBar;

    EditText editTextDescription;

    String access_token,username, user_id, avatar, server_key = "1539874186", name, favourites,
            following, followers, fname, lname, about, website, isFollowing;


    public UploadFragment() {
        // Required empty public constructor
    }
    public UploadFragment(Uri resultUri) {
        this.resultUri = resultUri;
    }
/*
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment(resultUri);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_upload, container, false);


        textViewPublish = itemView.findViewById(R.id.textViewPublish);
        textViewName = itemView.findViewById(R.id.textViewName);
        textViewUser = itemView.findViewById(R.id.textViewUser);

        progressBar = itemView.findViewById(R.id.progressBar);

        imageButtonBack = itemView.findViewById(R.id.imageButtonBack);
        roundedImageViewAvatar = itemView.findViewById(R.id.user_profile_image);

        // Get the Intent that started this activity and extract the string
        Intent intent = getActivity().getIntent();
        access_token = intent.getStringExtra("access_token");

        roundedImageViewAvatar.setImageURI(resultUri);
        cropImage(resultUri);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer,new HomeFragment());
                fragmentTransaction.commit();
            }
        });
        textViewPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage("image");

            }
        });

        return itemView;
    }

    private void uploadImage(String type) {
        if (resultUri == null){
            Log.i(TAG,uriToFileName(resultUri));
            return;
        }
        if(type.equals("image")){
            final File imageFile = new File(uriToFileName(resultUri));
            //TODO:La base de datos admite subida de archivos? E/UploadResponse: {"code":"400","status":"Bad Request","errors":{"error_id":"21","error_text":"An unknown error occurred. Please try again later!"}}
            Uri uris = Uri.fromFile(imageFile);
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString());
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
            String imageName = imageFile.getName();

            Log.i(TAG,imageFile.getName()+" "+mime+" "+uriToFileName(resultUri));
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("server_key",server_key)
                    .addFormDataPart("caption","prueba2")
                    .addFormDataPart("access_token",access_token)
                    .addFormDataPart("images[]",imageName,
                            RequestBody.create(imageFile, MediaType.parse(mime)))
                    .build();

            final CountingRequestBody.Listener progressListener = new CountingRequestBody.Listener() {
                @Override
                public void onRequestProgress(long bytesRead, long contentLength) {
                    if (bytesRead >= contentLength) {
                        if (progressBar != null)
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                    } else {
                        if (contentLength > 0) {
                            final int progress = (int) (((double) bytesRead / contentLength) * 100);
                            if (progressBar != null)
                                getActivity().runOnUiThread(new Runnable() {
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
            //TODO: Subir Videos, crear el flujo de subida y mejorar UI (last entrega)
            Request request = new Request.Builder()
                    .url("https://diys.co/endpoints/v1/post/new_post")
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("UploadResponse", mMessage);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            });

        }else{
            final File videoFile = new File(uriToFileName(resultUri));
            Uri uris = Uri.fromFile(videoFile);
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString());
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
            String videoName = videoFile.getName();

            Log.i(TAG,videoFile.getName()+" "+mime+" "+uriToFileName(resultUri));
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("server_key",server_key)
                    .addFormDataPart("caption","prueba1")
                    .addFormDataPart("access_token",access_token)
                    .addFormDataPart("video",videoName,
                            RequestBody.create(videoFile,MediaType.parse(mime)))
                    .build();

            final CountingRequestBody.Listener progressListener = new CountingRequestBody.Listener() {
                @Override
                public void onRequestProgress(long bytesRead, long contentLength) {
                    if (bytesRead >= contentLength) {
                        if (progressBar != null)
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                    } else {
                        if (contentLength > 0) {
                            final int progress = (int) (((double) bytesRead / contentLength) * 100);
                            if (progressBar != null)
                                getActivity().runOnUiThread(new Runnable() {
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
            Request request = new Request.Builder()
                    .url("https://diys.co/endpoints/v1/post/new_post")
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("UploadResponse", mMessage);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            });

        }

    }

    private  String uriToFileName(Uri uri){
        String path = null;
        path = getFilePath(getActivity(), uri);
        return path;
    }

    public void cropImage(Uri uri){

        // start cropping activity for pre-acquired image saved on the device
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON).setCropMenuCropButtonTitle("Listo")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setActivityTitle("Recortar")
                .setFixAspectRatio(true)
                .start(getActivity());



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