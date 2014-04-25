/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core;

import com.firstonesoft.core.event.EventListenerData;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Bismarck
 */
public class ListenerData extends Thread {

    private String key;
    private Socket socket;
    private EventListenerData eventListenerData;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean running;

    public ListenerData(String key, Socket socket) {
        try {
            this.key = key;
            this.socket = socket;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
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
                    onExceptionListening(e);
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        eventListenerData.onDisconnectClient(key);
    }
    
    public void onExceptionListening(IOException ioe) {
        try {
            running = false;
            if (dis != null) {
                dis.close();
            }
            if (dos != null) {
                dos.flush();
                dos.close();
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        eventListenerData.onExceptionListening(this.getKey(),ioe);
    }

    /**
     * *** ENVIAR PAQUETES EN BYTES ****
     */
    public void sendBytes(byte[] data) throws IOException {
        dos.writeLong(data.length);
        dos.write(data);
        dos.flush();
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

    public String getKey() {
        return key;
    }

    public Socket getSocket() {
        return socket;
    }
}
