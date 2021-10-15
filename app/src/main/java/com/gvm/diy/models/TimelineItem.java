package com.gvm.diy.models;

import java.io.Serializable;

public class TimelineItem implements Serializable {

    private PalmPhotoItem palmPhotoItem;
    private TextItem textItem;
    private int viewType;
    private MensajeRecibido mensajeRecibidoItem;

    public MensajeRecibido getMensajeRecibidoItem() {
        return mensajeRecibidoItem;
    }

    public TimelineItem() {
    }

    public void setMensajeRecibidoItem(MensajeRecibido mensajeRecibidoItem) {
        this.mensajeRecibidoItem = mensajeRecibidoItem;
    }

    public TimelineItem(MensajeRecibido mensajeRecibidoItem) {
        this.mensajeRecibidoItem = mensajeRecibidoItem;
        viewType = Constant.ITEM_TEXT_VIEWTYPE;
    }

    public TimelineItem(PalmPhotoItem palmPhotoItem) {
        this.palmPhotoItem = palmPhotoItem;
        viewType = Constant.ITEM_PALM_PHOTO_VIEWTYPE;
    }

    public TimelineItem(TextItem textItem) {
        this.textItem = textItem;
        viewType = Constant.ITEM_PALM_PHOTO_VIEWTYPE;
    }

    public PalmPhotoItem getPalmPhotoItem() {
        return palmPhotoItem;
    }

    public void setPalmPhotoItem(PalmPhotoItem palmPhotoItem) {
        this.palmPhotoItem = palmPhotoItem;
    }

    public TextItem getTextItem() {
        return textItem;
    }

    public void setTextItem(TextItem textItem) {
        this.textItem = textItem;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
