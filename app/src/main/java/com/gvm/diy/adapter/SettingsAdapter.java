package com.gvm.diy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gvm.diy.R;
import com.gvm.diy.models.Settings;

import java.util.List;

public class SettingsAdapter extends BaseAdapter {
  Context mContext;
  List<Settings> settingsList;

  public SettingsAdapter(Context mContext, List<Settings> settings) {
    this.mContext = mContext;
    this.settingsList = settings;
  }

  public SettingsAdapter() {
  }

  @Override
  public int getCount() {
    return settingsList.size();
  }

  @Override
  public Object getItem(int i) {
    return i;
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    ImageView imageViewSettings;
    TextView textViewSettings;

    Settings settings = settingsList.get(i);

    if(view==null)
      view = LayoutInflater.from(mContext).inflate(R.layout.listview_settings,null);

    imageViewSettings = view.findViewById(R.id.imageViewSetting);
    textViewSettings = view.findViewById(R.id.textViewSetting);

    imageViewSettings.setImageResource(settings.icon);
    textViewSettings.setText(settings.config);
    return view;
  }
}
