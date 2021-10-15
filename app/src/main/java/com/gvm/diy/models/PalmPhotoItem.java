package com.gvm.diy.models;

import android.net.Uri;

public class PalmPhotoItem {
    private Long hora;

    public PalmPhotoItem() {
    }

    public PalmPhotoItem(Long hora) {
        this.hora = hora;
    }


    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }
}
