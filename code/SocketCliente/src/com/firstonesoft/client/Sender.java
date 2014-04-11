/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client;

/**
 *
 * @author Bismarck
 */
public class Sender extends Thread {

    private byte [] data;
    private ListenerData listenerData;
    
    public Sender(byte [] data, ListenerData listenerData) {
        this.data = data;
        this.listenerData = listenerData;
    }

    @Override
    public void run() {
        synchronized(this) {
            listenerData.sendBytes(data);
        }
    }
    
}
