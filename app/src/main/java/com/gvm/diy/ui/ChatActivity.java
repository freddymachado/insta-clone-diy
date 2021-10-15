package com.gvm.diy.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gvm.diy.R;
import com.gvm.diy.adapter.AdaptadorChat;
import com.gvm.diy.models.ChatList;
import com.gvm.diy.models.TimelineItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity{

    TextView textViewSoporte;

    String lastMessage, userName = "cargando..", TAG = "fcm";

    ImageView imageViewLogo;

    Button buttonSalir, buttonChat, buttonCerrar, buttonActivar;

    EditText editTextChat;

    String id, token;

    RecyclerView recyclerChat;

    int ADMIN = 1, USER = 2;

    private AdaptadorChat adapter;

    private ImageButton imageButtonId;

    private static final int PHOTO_SENT = 1;

    private Boolean chatIniciado = false;

    int whoIsSendingMessages;
    private List<TimelineItem> mData = new ArrayList<>();

    AlertDialog alertDialog;

    String adminid = "aminid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Inicio del setLayout8

        Intent intent = getIntent();
        setLayout8();

        adapter = new AdaptadorChat(this,mData);
        LinearLayoutManager l = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(l);
        recyclerChat.setAdapter(adapter);

        //Chequeamos si se ingresa desde un perfil de usuario o admin
        if(intent.getExtras() != null){

            //Si los extras no son nulos, guardamos el id del chat seleccionado
            ChatList chatList = (ChatList) intent.getSerializableExtra("userId");
            id = chatList.getId();
            userName = chatList.getTipo();
            Log.d("idarrived",chatList.getId());

            whoIsSendingMessages = ADMIN;

            imageButtonId.setVisibility(View.GONE);

        }else {
            //En caso contrario, preguntamos si no se habia iniciado el chat previamente
            chatExists(id);

            whoIsSendingMessages = USER;
            buttonActivar.setVisibility(View.GONE);
        }
        reference = db.getReference("Usuarios/"+id+"/chat"); //Sala de chat del usuario
        historial = db1.getReference("Usuarios/"+id+"/historial"); //Historial del usuario
        notificaciones = db2.getReference("Notificaciones"); //Historial del usuario


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                token = task.getResult();

                // Log
                Log.d(TAG, token);

                sendRegistrationToServer(token);
            }
        });

        storage = FirebaseStorage.getInstance();
        //Fin del setLayout8

        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Si el mensaje es enviado desde un perfil de usuario..
                if(whoIsSendingMessages==USER){
                    reference.push().setValue(new MensajeEnviar(editTextChat.getText().toString(),"1", ServerValue.TIMESTAMP));
                    historial.push().setValue(new MensajeEnviar(editTextChat.getText().toString(),"1", ServerValue.TIMESTAMP));
                    notificaciones.push().setValue(new MensajeEnviar(editTextChat.getText().toString(),"1",
                            "jfhk8ygQOXbE7DDt8ql5fUNxPH43", userName, "admin", ServerValue.TIMESTAMP));

/*
                    //Creamos un Intent para iniciar la clase que contiene el servicio de notificaciones
                    Intent serviceIntent = new Intent(ChatActivity.this, NotificacionesServicio.class);

                    //Enviamos el id del chat
                    serviceIntent.putExtra("id",id);
                    serviceIntent.putExtra("lastMessage", (Serializable) mData);
                    serviceIntent.putExtra("currentUser", "user");
                    Log.d("lastMessage", lastMessage);
                    //NotificacionesServicio.enqueueWork(ChatActivity.this,serviceIntent);
                    */

                }else{
                    reference.push().setValue(new MensajeEnviar(editTextChat.getText().toString(),"3", ServerValue.TIMESTAMP));
                    historial.push().setValue(new MensajeEnviar(editTextChat.getText().toString(),"3", ServerValue.TIMESTAMP));
                    notificaciones.push().setValue(new MensajeEnviar(editTextChat.getText().toString(),"3",
                            id, "admin", userName, ServerValue.TIMESTAMP));
/*
                    //Creamos un Intent para iniciar la clase que contiene el servicio de notificaciones
                    Intent serviceIntent = new Intent(ChatActivity.this, NotificacionesServicio.class);

                    //Enviamos el id del chat
                    serviceIntent.putExtra("id",id);
                    serviceIntent.putExtra("lastMessage", (Serializable) mData);
                    serviceIntent.putExtra("currentUser", "admin");
                    Log.d("lastMessage", lastMessage);
                    //NotificacionesServicio.enqueueWork(ChatActivity.this,serviceIntent);*/
                }
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
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
                adapter.notifyItemInserted(adapter.getItemCount());
                recyclerChat.smoothScrollToPosition(adapter.getItemCount());
                //adapter.addMensaje(m);
                //TextItem itemText1 = new TextItem("Hello! I am Andry. I practice the technique of palm reading.");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonSalir.setOnClickListener(new View.OnClickListener() {
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

        //Al cerrar el ticket guardamos la bd en el nodo historial del usuario y finalizamos la actividad
       buttonCerrar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               reference.setValue(null);

               //Si el usuario actual es admin..
               if (whoIsSendingMessages == ADMIN) {
                   //Regresamos la Sala de chats
                   startActivity(new Intent(ChatActivity.this,SalaChatActivity.class));
               }
               finish();
           }
       });

        buttonActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Si el usuario actual es admin..
                if (whoIsSendingMessages == ADMIN) {
                    //Nos dirijimos al panel admin con el id del user
                    startActivity(new Intent(ChatActivity.this,ProfileAdminActivity.class).putExtra("userId",id));
                }
                finish();
            }
        });
    }

    private void setLayout8() {
        textViewSoporte = findViewById(R.id.textViewSoporte);

        imageViewLogo = findViewById(R.id.imageViewLogo);

        editTextChat = findViewById(R.id.editTextChat);

        recyclerChat = findViewById(R.id.recyclerChat);

        buttonSalir = findViewById(R.id.buttonSalir);
        buttonChat = findViewById(R.id.buttonChat);
        buttonCerrar = findViewById(R.id.buttonCerrar);
        buttonActivar = findViewById(R.id.buttonActivar);

        imageButtonId = findViewById(R.id.imageButtonId);
    }

    private String findReceptorToken(String id) {

        //Creo la conexión con la base de datos Usuarios
        database.child("Usuarios").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String aux = dataSnapshot.child("fcm").getValue().toString();

                token=aux;
                Log.d("Receptor Token", "onDataChange: "+token);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return token;


    }

    private void sendRegistrationToServer(String token) {

            //Declaro el HashMap tokenMap
            final Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("fcm",token);

            //Actualizo la base de datos
            database.child("Usuarios").child(auth.getCurrentUser().getUid()).updateChildren(tokenMap);


    }

    private void showAlert(){
        //Instanciamos el AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);

        //Agregamos los items de las categorias
        final CharSequence[] categorias = {
                "Activar Plan 1 Pantalla",
                "Activar Plan 2 Pantallas",
                "Activar Plan 4 Pantallas",
                "Informar Pago",
                "Consulta General",
                "Reportar un problema"};

        //Configuramos las propiedades
        builder.setTitle("Indíque una categoría:").setItems(categorias, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (categorias[i].toString()){
                    case "Activar Plan 1 Pantalla":
                        reference.push().setValue(new MensajeEnviar(mensajeAutomaticoPlan1,"3", ServerValue.TIMESTAMP));
                        historial.push().setValue(new MensajeEnviar(mensajeAutomaticoPlan1,"3", ServerValue.TIMESTAMP));
                        break;
                    case "Activar Plan 2 Pantallas":
                        reference.push().setValue(new MensajeEnviar(mensajeAutomaticoPlan2,"3", ServerValue.TIMESTAMP));
                        historial.push().setValue(new MensajeEnviar(mensajeAutomaticoPlan2,"3", ServerValue.TIMESTAMP));
                        break;
                    case "Activar Plan 4 Pantallas":
                        reference.push().setValue(new MensajeEnviar(mensajeAutomaticoPlan4,"3", ServerValue.TIMESTAMP));
                        historial.push().setValue(new MensajeEnviar(mensajeAutomaticoPlan4,"3", ServerValue.TIMESTAMP));
                        break;
                    case "Informar Pago":
                        reference.push().setValue(new MensajeEnviar(mensajeAutomaticoPago,"3", ServerValue.TIMESTAMP));
                        historial.push().setValue(new MensajeEnviar(mensajeAutomaticoPago,"3", ServerValue.TIMESTAMP));
                        break;
                    case "Consulta General":
                        reference.push().setValue(new MensajeEnviar(mensajeAutomaticoGeneral,"3", ServerValue.TIMESTAMP));
                        historial.push().setValue(new MensajeEnviar(mensajeAutomaticoGeneral,"3", ServerValue.TIMESTAMP));
                        break;
                    case "Reportar un problema":
                        reference.push().setValue(new MensajeEnviar(mensajeAutomaticoProblema,"3", ServerValue.TIMESTAMP));
                        historial.push().setValue(new MensajeEnviar(mensajeAutomaticoProblema,"3", ServerValue.TIMESTAMP));
                        break;


                    default:
                        break;
                }
            }
        });
        //Creamos el dialog
        alertDialog = builder.create();
        alertDialog.show();
        Log.d("chatExists","chatdoesnotexisst");
    }

    private boolean chatExists(String id) {

        //Establezco una conexión con la base de datos del usuario para determinar si ha iniciado un chat o no.
        database.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                //Rescatamos el nombre de usuario
                String user = dataSnapshot2.child("name").getValue().toString();

                userName = user;
                //Si no se ha abierto un ticket...
                if(!dataSnapshot2.child("chat").exists()){
                    //... mostramos el chooser
                    showAlert();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return  true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO_SENT && resultCode == RESULT_OK){
            Uri u = data.getData();
            storageReference = storage.getReference("imagenesChat");
            assert u != null;
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> u = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String downloadUri = task.getResult().toString();
                            Log.d("Uri",downloadUri);

                            if(whoIsSendingMessages==USER){
                                MensajeEnviar m = new MensajeEnviar(userName+" te ha enviado una foto","2",downloadUri,ServerValue.TIMESTAMP);
                                reference.push().setValue(m);
                                historial.push().setValue(m);
                                notificaciones.push().setValue(new MensajeEnviar(userName+" te ha enviado una foto","2", "jfhk8ygQOXbE7DDt8ql5fUNxPH43", userName, "admin", ServerValue.TIMESTAMP));

                            }else{
                                MensajeEnviar m = new MensajeEnviar("Te han enviado una foto","3",downloadUri,ServerValue.TIMESTAMP);
                                reference.push().setValue(m);
                                historial.push().setValue(m);

                            }
                        }
                    });
                }
            });
        }
    }

}