/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core;

import com.firstonesoft.core.event.EventSender;
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
                    sendByteClient();
                    break;
                case TO_CLIENTS:
                    sendBytesClients();
                case TO_SOCKET:
                    sendSocketBytes();
                    break;
                case TO_SOCKET_STATE:
                    sendSocketState();
                    break;
            }
        }
    }
    
    private void sendByteClient() {
        try {
            client.sendBytes(data);
            eventSender.onSendClient();
        } catch (IOException e) {
            eventSender.onFailedSendClient(e);
        }
    }

    private void sendBytesClients() {
        try {
            for (ListenerData c : clients) {
                c.sendBytes(data);
            }
            eventSender.onSendClients();
        } catch (Exception e) {
            eventSender.onFailedSendClients(e);
        }
    }

    private void sendSocketBytes() {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeLong(data.length);
            dos.write(data, 0, data.length);
            dos.flush();
            eventSender.onSendSocketBytes();
        } catch (IOException e) {
            System.out.println(e);
            eventSender.onFailedSendSocketBytes(e);
        }
    }

    private void sendSocketState() {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeBoolean(state);
            dos.flush();
            eventSender.onSendState(state, key, socket);
        } catch (IOException e) {
            eventSender.onFailedSendState(e);
        }
    }

    /**
     * *** GETTER AND SETTER ****
     */
    public void setEventSender(EventSender eventSender) {
        this.eventSender = eventSender;
    }
}
