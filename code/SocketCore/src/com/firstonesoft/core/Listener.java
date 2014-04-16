/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core;

import com.firstonesoft.core.event.EventListener;
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
                    eventListener.onConnectClient(key, clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
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
