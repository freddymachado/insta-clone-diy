package com.gvm.diy.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gvm.diy.R;

public class EditActivity extends AppCompatActivity {
    //TODO: clickListeners de los botones y rellenar info con extras
    ImageButton imageButtonBack;

    TextView textViewSave;

    EditText editTextName, editTextSurname, editTextAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        imageButtonBack = findViewById(R.id.imageButtonBack);
        textViewSave = findViewById(R.id.textViewSave);
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextAbout = findViewById(R.id.editTextAbout);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}