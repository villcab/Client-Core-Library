/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client;

import com.firstonesoft.client.event.EventSender;
import java.io.IOException;

/**
 *
 * @author Bismarck
 */
public class Sender extends Thread {

    private byte[] data;
    private ListenerData listenerData;
    private EventSender eventSender;

    public Sender(byte[] data, ListenerData listenerData) {
        this.data = data;
        this.listenerData = listenerData;
    }

    @Override
    public void run() {
        synchronized (this) {
            sendBytes();
        }
    }

    private void sendBytes() {
        try {
            listenerData.sendBytes(data);
            eventSender.onSendBytes();
        } catch (IOException e) {
            eventSender.onFailedSendBytes(e);
        }
    }
    
    /**
     * *** GETTER AND SETTER ****
     */
    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }

}
