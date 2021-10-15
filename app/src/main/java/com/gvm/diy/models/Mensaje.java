package com.gvm.diy.models;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private String mensaje;
    private String tipo;
    private String urlFoto;
    private String status;

    public Mensaje(String status) {
        this.status = status;
    }

    public Mensaje() {
    }

    public Mensaje(String mensaje, String tipo) {
        this.mensaje = mensaje;
        this.tipo = tipo;
    }

    public Mensaje(String mensaje, String tipo, String urlFoto) {
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.urlFoto = urlFoto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }
}
