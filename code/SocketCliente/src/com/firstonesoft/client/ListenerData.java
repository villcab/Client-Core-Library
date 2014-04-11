/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client;

import com.firstonesoft.event.EventListenerData;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Bismarck
 */
public class ListenerData extends Thread {

    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean running;
    private EventListenerData eventListenerData;

    public ListenerData(Socket socket) {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                int bytesRead;
                ByteArrayOutputStream output;
                try {
                    long size = dis.readLong();
                    eventListenerData.onNewPackage(size);
                    byte[] buffer = new byte[8388608];  // 8388608 bit => 1 mg
                    output = new ByteArrayOutputStream((int) size);
                    while (size > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                        eventListenerData.onNewTrama(bytesRead);
                        output.write(buffer, 0, bytesRead);
                        size -= bytesRead;
                    }
                    eventListenerData.onNewPackageComplet(output.toByteArray());
                } catch (IOException e) {
                    closeListenerData();
                }
            }
        }
    }
    
    /**
     * *** CUANDO SE DESCONECTA UN CLIENTE ****
     */
    public void closeListenerData() {
        try {
            running = false;
            if (dis != null) {
                dis.close();
            }
            if (dos != null) {
                dos.flush();
                dos.close();
            }
            eventListenerData.onDisconnectCore();
        } catch (IOException e) {
            System.out.println("closeListenerData > IOException: " + e);
        }
    }
    
    /**
     * *** ENVIAR PAQUETES EN BYTES ****
     */
    public void sendBytes(byte [] data) {
        try {
            dos.writeLong(data.length);
            dos.write(data, 0, data.length);
            dos.flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /**
     * *** GETTER AND SETTER ****
     */
    public void setEventListenerData(EventListenerData eventListenerData) {
        this.eventListenerData = eventListenerData;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    
}
