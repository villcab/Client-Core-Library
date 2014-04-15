/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core.model;

import java.net.Socket;

/**
 *
 * @author Bismarck
 */
public class DataSocket {
    
    private String key;
    private Socket socket;

    public DataSocket(String key, Socket socket) {
        this.key = key;
        this.socket = socket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

}
