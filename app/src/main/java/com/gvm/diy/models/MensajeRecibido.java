package com.gvm.diy.models;

import java.io.Serializable;

public class MensajeRecibido extends Mensaje implements Serializable {
    private Long hora;
    private String status;

    public MensajeRecibido(String status) {
        super(status);
        this.status = status;
    }

    public MensajeRecibido() {
    }

    public MensajeRecibido(Long hora) {
        this.hora = hora;
    }

    public MensajeRecibido(String mensaje, String tipo, String urlFoto, Long hora) {
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
