/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client;

import com.firstonesoft.client.event.EventListenerData;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author JMCM
 */
public class ListenerData extends Thread {

    private DataInputStream dis;
    
    private boolean running;
    private EventListenerData eventListenerData;

    public ListenerData(Socket socket) {
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                int bytesRead;
                int posRead = 0;
                byte [] output;
                try {
                    long size = dis.readLong();
                    eventListenerData.onNewPackage(size);
                    byte[] buffer = new byte[1048576];  // 1048576 byte => 1 mg
                    output = new byte[(int)size];
                    while (posRead < size && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                        eventListenerData.onNewTrama(bytesRead);
                        System.arraycopy(buffer, 0, output, posRead, bytesRead);
                        posRead += bytesRead;
                    }
                    eventListenerData.onNewPackageComplet(output);
                } catch (IOException e) {
                    eventListenerData.onDisconnectCore(e);
                }
            }
        }
    }

    /**
     * *** CUANDO SE DESCONECTA UN CLIENTE ****
     */
    public void closeListenerData() throws IOException {
        running = false;
        if (dis != null) {
            dis.close();
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
