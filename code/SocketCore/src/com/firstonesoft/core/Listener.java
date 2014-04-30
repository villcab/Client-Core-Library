/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core;

import com.firstonesoft.core.event.EventListener;
import com.firstonesoft.core.util.ObjectUtil;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Bismarck
 */
public class Listener extends Thread {

    private EventListener eventListener;
    
    private ServerSocket serverSocket;
    private boolean running;

    public Listener(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    
    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                try {
                    Socket clientSocket;
                    clientSocket = serverSocket.accept();
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    String key = dis.readUTF();
                    boolean estatico = dis.readBoolean();
                    if (estatico)
                        eventListener.onConnectClientClosed(key, clientSocket);
                    else
                    {
                        ByteArrayOutputStream output;
                        int bytesRead;
                        long size = dis.readLong();
                        byte[] buffer = new byte[8388608];  // 8388608 bit => 1 mg
                        output = new ByteArrayOutputStream((int) size);
                        while (size > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                            output.write(buffer, 0, bytesRead);
                            size -= bytesRead;
                        }
                        Object o = ObjectUtil.createObject(output.toByteArray());
                        eventListener.onConnectClientOpened(key, clientSocket, o);
                    }
                } catch (IOException e) {
                    
                }
            }
        }
    }
    
    //GETTER AND SETTER
    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }
    
}
