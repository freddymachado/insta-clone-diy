package com.gvm.diy.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    //TODO: Logo fb

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    TextInputLayout textInputLayoutPass, textInputLayoutUser;
    TextInputEditText textInputEditTextPass, textInputEditTextUser, textInputEditTextEmail;

    ProgressBar progressBar, progressBarD;

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

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.email_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg_round));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        textInputEditTextEmail = dialog.findViewById(R.id.textInputEditTextEmail);
        progressBarD = dialog.findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        //Debug:
        textInputEditTextUser.setText("FedeSan");
        textInputEditTextPass.setText("eclipse42");
    }


    public void initSession(View view)    {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body;
        Request request;
        Thread thread;/*
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                timer.cancel();
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        },10000,1000);*/

        switch (view.getId()) {

            case R.id.buttonLogIn:
                progressBar.setVisibility(View.VISIBLE);
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
                                goMain(user_id,access_token,String.valueOf(textInputEditTextUser.getText()),String.valueOf(textInputEditTextPass.getText()));
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Revisa tu conexión e inténtalo de nuevo", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                thread.start();
                break;

            case R.id.buttonContinue:
                progressBarD.setVisibility(View.VISIBLE);
                 body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("server_key","1539874186")
                        .addFormDataPart("email",String.valueOf(textInputEditTextEmail.getText()))
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
                            JSONObject json = new JSONObject(response.body().string());
                            Log.d(TAG, String.valueOf(json));
                            if(json.getString("code").equals("200")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBarD.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Hemos enviado un correo de recuperación a "+textInputEditTextEmail.getText(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBarD.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Correo no registrado", Toast.LENGTH_LONG).show();
                                        textInputEditTextEmail.setText("");
                                        textInputEditTextEmail.setError("Ingresa tu correo nuevamente");
                                    }
                                });
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBarD.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Revisa tu conexión e inténtalo de nuevo", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    }
                });
                thread.start();

                break;

            case R.id.buttonExit:
                 dialog.dismiss();
                break;

            case R.id.buttonFb:
                //TODO: Tal parece que hay que hacerle una configuración específica para este paquete, por lo tanto la API no funciona en este caso (segunda entrega)
                // "error_text":"please check your details"  "error_text":"Error validating access token: The session has been invalidated because the user changed their password or Facebook has changed the session for security reasons."}}

                Toast.makeText(getApplicationContext(), "In progress", Toast.LENGTH_LONG).show();
                /*progressBar.setVisibility(View.VISIBLE);
                body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("server_key","1539874186")
                        .addFormDataPart("provider","facebook")
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
                            progressBar.setVisibility(View.GONE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                thread.start();*/

                break;

            case R.id.imageButtonBack:
                finish();
                break;

            case R.id.buttonGoogle:
                // Configure Google Sign In
                progressBar.setVisibility(View.VISIBLE);
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
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
                            register(user.getEmail(),user.getUid().substring(0,8),user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Error, intentelo de nuevo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void register(String email, String user, String password){
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("server_key","1539874186")
                .addFormDataPart("username",user)
                .addFormDataPart("password",password)
                .addFormDataPart("conf_password",password)
                .addFormDataPart("email",email)
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
                    //Log.d(TAG, response.body().string());
                    JSONObject json = new JSONObject(response.body().string());//
                     Log.d(TAG, String.valueOf(json));
                    if(json.getString("code").equals("200")){
                        token = json.getJSONObject("data");
                        access_token =token.getString("access_token");
                        user_id =token.getString("user_id");
                        goMain(user_id,access_token, user, password);
                    }else if (json.getJSONObject("errors").getString("error_id").equals("6")) {
                        logIn(user,password);
                    }else {
                            String error = json.getJSONObject("errors").getString("error_text");
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
                            Toast.makeText(getApplicationContext(), "Registro no exitoso", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        thread.start();
    }

    public void logIn(String user, String password) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body;
        Request request;
        Thread thread;
        progressBar.setVisibility(View.VISIBLE);
        body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("username", user)
                .addFormDataPart("password", password)
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
                        goMain(user_id,access_token,user,password);
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
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
        thread.start();
    }

    public void goMain(String user_id, String access_token, String username, String pass) {
        Toast.makeText(LoginActivity.this, "Sesión iniciada correctamente", Toast.LENGTH_SHORT).show();
        Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
        intentMain.putExtra("access_token", access_token);
        intentMain.putExtra("user_id", user_id);
        intentMain.putExtra("username", username);
        intentMain.putExtra("password", pass);
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