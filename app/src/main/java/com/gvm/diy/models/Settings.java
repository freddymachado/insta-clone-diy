package com.gvm.diy.models;

import android.widget.ImageView;
import android.widget.TextView;

public class Settings {
  public int icon;
  public String config;

  public Settings(int icon, String config) {
    this.icon = icon;
    this.config = config;
  }

  public Settings() {
  }

  public int getIcon() {
    return icon;
  }

  public void setIcon(int icon) {
    this.icon = icon;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }
}
