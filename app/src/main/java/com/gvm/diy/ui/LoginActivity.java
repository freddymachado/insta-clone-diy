package com.gvm.diy.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.gvm.diy.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    //TODO: Perfeccionar diseño del layout

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    TextInputLayout textInputLayoutPass, textInputLayoutUser;
    TextInputEditText textInputEditTextPass, textInputEditTextUser, textInputEditTextEmail;

    Boolean error = false;


    private FirebaseAuth mAuth;

    String TAG = "LoginActivity", access_token, user_id;
    JSONObject token;

    Dialog dialog;
    public static final int REQUEST_CODE = 55463;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textInputLayoutPass = findViewById(R.id.textInputLayoutPass);
        textInputLayoutUser = findViewById(R.id.textInputLayoutUser);
        textInputEditTextPass = findViewById(R.id.textInputEditTextPass);
        textInputEditTextUser = findViewById(R.id.textInputEditTextUser);

        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.email_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg_round));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        textInputEditTextEmail = dialog.findViewById(R.id.textInputEditTextEmail);

        mAuth = FirebaseAuth.getInstance();

        //Debug:
        textInputEditTextUser.setText("example_");
        textInputEditTextPass.setText("12345678");
/*
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Toast.makeText(LoginActivity.this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show();
                }else{
                    startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(provider)
                    .setIsSmartLockEnabled(false)
                    .build(),REQUEST_CODE);
                }
            }
        };*/
    }


    public void initSession(View view)    {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body;
        Request request;
        Thread thread;
        switch (view.getId()) {

            case R.id.buttonLogIn:
                body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("username", String.valueOf(textInputEditTextUser.getText()))
                        .addFormDataPart("password", String.valueOf(textInputEditTextPass.getText()))
                        .addFormDataPart("server_key","1539874186")
                        .build();
                request = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/auth/login")
                        .method("POST",body)
                        .build();
/*
                D/ApiResponse: string(3) "off"
                array(2) {
                  ["user_id"]=>
                  int(1476)
                  ["access_token"]=>
                  string(82) "21119437edc7bff5a1b971a7940fcb1485fd90d116302538852a9745cf6dff8b3e14262cb2a40ba0a7"
                }
                {"code":"200","status":"OK","data":{"user_id":1476,"access_token":"21119437edc7bff5a1b971a7940fcb1485fd90d116302538852a9745cf6dff8b3e14262cb2a40ba0a7","message":"You have successfully joined"}}
*/
                thread = new Thread(new Runnable() {
                    public void run() {
                        Looper.prepare();
                        try {
                            Response response = client.newCall(request).execute();
                            JSONObject json = new JSONObject(response.body().string());
                            Log.d(TAG, String.valueOf(json));
                            if(json.getString("code").equals("200")){
                                token = json.getJSONObject("data");
                                access_token =token.getString("access_token");
                                user_id =token.getString("user_id");
                                goMain(user_id,access_token);
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //TODO: Probar setError
                                        Toast.makeText(getApplicationContext(), "Nombre de usuario o contraseña inválida", Toast.LENGTH_SHORT).show();
                                        textInputEditTextUser.setText("");
                                        textInputEditTextPass.setText("");
                                        textInputLayoutUser.setError("Ingresa tu nombre de usuario nuevamente");
                                        textInputLayoutPass.setError("Ingresa tu contraseña nuevamente");
                                    }
                                });
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                break;

            case R.id.buttonContinue:
                 body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("server_key","473298749")
                        .addFormDataPart("email",String.valueOf(textInputEditTextEmail.getText()))/*
                .addFormDataPart("access_token","1d1ae6d02edb7d9ef2dbc305b184eb2e08518d251579769480857c0547956829822027d585cce3e5bd")
                .addFormDataPart("offset","633")
                .addFormDataPart("limit","2")
                .addFormDataPart("v","1")
                .addFormDataPart("resource","post")
                .addFormDataPart("resource_id","fetch_home_posts")*/
                        .build();
                request = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/auth/forget")
                        .method("POST",body)
                        .build();

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response response = client.newCall(request).execute();
                            //TODO: Probar funcionamiento para ver con qué proceder
                            Log.d(TAG, response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                thread.start();

                break;

            case R.id.buttonExit:
                 dialog.dismiss();
                break;

            case R.id.buttonFb:
                //TODO: Probar inicio con fb
                body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("server_key","473298749")
                        .addFormDataPart("provider","facebook")
                        .addFormDataPart("access_token","EAADj0LiNiWoBAGkWJcpzljnOkaRFRivLAyrZCMnZCXWz1evjHnUVg7gYCZC0YCuuEswsJWkZCA9N6p2ZCRvGxfd210eQDW19t0VS90kY5OB17F1RRYJrc7tQzn2jnAGEKLdZCeDVIn13JCuz3IZB6m9yJHNSUnjENt3rSIZBZC5CgrbiBEz5N7B00Rwy1wC083PfPppeBYa9clbPZAUrSzw5nUZAv65c0glmrA4wqgtTcZBZApAZDZD")
                        /*
                      .addFormDataPart("access_token","1d1ae6d02edb7d9ef2dbc305b184eb2e08518d251579769480857c0547956829822027d585cce3e5bd")
                      .addFormDataPart("offset","633")
                      .addFormDataPart("limit","2")
                      .addFormDataPart("v","1")
                      .addFormDataPart("resource","post")
                      .addFormDataPart("resource_id","fetch_home_posts");*/
                        .build();
                request = new Request.Builder()
                        .url("https://diys.co/endpoints/v1/auth/social_login")
                        .method("POST",body)
                        .build();

                thread = new Thread(new Runnable() {
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

                break;

            case R.id.imageButtonBack:
                finish();
                break;

            case R.id.buttonGoogle:
                //TODO: Probar inicio con google

                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

            case R.id.textViewRegister:
                Intent intentRegister = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intentRegister);
                finish();
                break;

            default:
                break;
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            register(user.getEmail(),user.getDisplayName(),user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Error, intentelo de nuevo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setError() {
    }

    private void register(String email, String user, String password){
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("server_key","473298749")
                .addFormDataPart("username",user)
                .addFormDataPart("password",password)
                .addFormDataPart("conf_password",password)
                .addFormDataPart("email",email)/*
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
                    JSONObject json = new JSONObject(response.body().string());
                    if(json.getString("code").equals("200")){
                        token = json.getJSONObject("data");
                        access_token =token.getString("access_token");
                        user_id =token.getString("user_id");
                        goMain(user_id,access_token);
                    }else{/*
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });*/
                        //TODO: Probar setError
                        setError();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    private void goMain(String user_id, String access_token) {
        Toast.makeText(LoginActivity.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
        Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
        intentMain.putExtra("access_token", access_token);
        intentMain.putExtra("user_id", user_id);
        startActivity(intentMain);
        finish();
    }

    public void forgotPass(View view){
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
  //      firebaseAuth.removeAuthStateListener(authStateListener);
    }
}