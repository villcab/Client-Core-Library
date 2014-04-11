/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core;

import com.firstonesoft.event.EventSender;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author Bismarck
 */
public class Sender extends Thread {

    private static final int TO_CLIENT = 0;
    private static final int TO_CLIENTS = 1;
    private static final int TO_SOCKET = 2;
    private static final int TO_SOCKET_STATE = 3;
    
    private int accion;
    private byte[] data;
    private ListenerData client;
    private List<ListenerData> clients;
    private Socket socket;
    private String key;
    private boolean state;
    private EventSender eventSender;

    public Sender(byte[] data, ListenerData client) {
        this.data = data;
        this.client = client;
        this.accion = TO_CLIENT;
    }

    public Sender(byte[] data, List<ListenerData> clients) {
        this.data = data;
        this.clients = clients;
        this.accion = TO_CLIENTS;
    }

    public Sender(byte[] data, Socket socket) {
        this.data = data;
        this.socket = socket;
        this.accion = TO_SOCKET;
    }

    public Sender(boolean state, Socket socket, String key) {
        this.state = state;
        this.socket = socket;
        this.key = key;
        this.accion = TO_SOCKET_STATE;
    }

    @Override
    public void run() {
        synchronized (this) {
            switch (accion) {
                case TO_CLIENT:
                    client.sendBytes(data);
                    eventSender.onSendComplet();
                    break;
                case TO_CLIENTS:
                    sendBytesClients();
                case TO_SOCKET:
                    sendBytes();
                    break;
                case TO_SOCKET_STATE:
                    sendState(state);
                    break;
            }
        }
    }

    private void sendBytesClients() {
        for (ListenerData c : clients) {
            c.sendBytes(data);
            break;
        }
        eventSender.onSendComplet();
    }

    private void sendBytes() {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeLong(data.length);
            dos.write(data, 0, data.length);
            dos.flush();
            eventSender.onSendComplet();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void sendState(boolean state) {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeBoolean(state);
            dos.flush();
            System.out.println("state enviado el state: " + state);
            eventSender.onSendState(state, key, socket);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * *** GETTER AND SETTER ****
     */
    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }
}
