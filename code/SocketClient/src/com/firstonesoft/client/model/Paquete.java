/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client.model;

/**
 *
 * @author Bismarck
 */
public class Paquete {

    public static final String TO_SERVER = "CORE";
    public static final String TO_ALL = "ALL";
    
    private String to;
    private String from;
    private byte[] contenido;

    public Paquete(String to, String from) {
        this.to = to;
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }
    
}
