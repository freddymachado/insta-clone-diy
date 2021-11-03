package com.gvm.diy.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.gvm.diy.R;
import com.gvm.diy.adapter.SettingsAdapter;
import com.gvm.diy.models.Settings;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    ListView listViewSettings;
    List<Settings> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        listViewSettings=findViewById(R.id.listView);
        
        SettingsAdapter adapter = new SettingsAdapter(this,getData());
        listViewSettings.setAdapter(adapter);

        listViewSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Settings settings = list.get(i);
                Toast.makeText(getBaseContext(), settings.config, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Settings> getData() {
        list= new ArrayList<>();
        list.add(new Settings(R.drawable.ic_baseline_settings_24,"General"));
        list.add(new Settings(R.drawable.ic_baseline_person_pin_24,"Perfil"));
        list.add(new Settings(R.drawable.ic_baseline_vpn_key_24,"Cambiar conntraseña"));
        list.add(new Settings(R.drawable.ic_baseline_remove_red_eye_24,"Privacidad"));
        list.add(new Settings(R.drawable.ic_baseline_notifications_24,"Notificaciones"));
        list.add(new Settings(R.drawable.ic_baseline_fingerprint_24,"Gestionar sesiones"));
        list.add(new Settings(R.drawable.ic_baseline_work_24,"Cuenta de negocios"));
        //TODO: Qué cantidad de items se requieren?
        return list;
    }

}