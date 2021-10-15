package com.gvm.diy.models;


import java.io.Serializable;

public class TextItem extends Mensaje implements Serializable {
    private Long hora;

    public TextItem() {
    }

    public TextItem(Long hora) {
        this.hora = hora;
    }

    public TextItem(String mensaje, String tipo, String urlFoto, Long hora) {
        super(mensaje, tipo, urlFoto);
        this.hora = hora;
    }

    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }
}
