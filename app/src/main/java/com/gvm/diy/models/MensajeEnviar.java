package com.gvm.diy.models;

import java.util.Map;

public class MensajeEnviar extends Mensaje {
    private String id;
    private String emisor;
    private String receptor;
    private Long hora;

    public MensajeEnviar() {
    }

    public MensajeEnviar(Long hora) {
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String tipo, String urlFoto, Long hora) {
        super(mensaje, tipo, urlFoto);
        this.hora = hora;
    }


    public Long getHora() {
        return hora;
    }

    public MensajeEnviar(String mensaje, String tipo, String id, String emisor, String receptor, Long hora) {
        super(mensaje, tipo);
        this.id = id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.hora = hora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }

    public MensajeEnviar(String mensaje, String tipo, Long hora) {
        super(mensaje, tipo);
        this.hora = hora;
    }
}
