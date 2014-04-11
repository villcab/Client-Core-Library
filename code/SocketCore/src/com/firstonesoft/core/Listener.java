/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core;

import com.firstonesoft.event.EventListener;
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
                    System.out.println("Esperando conexion de algun cliente");
                    clientSocket = serverSocket.accept();
                    System.out.println("cliente conectado: " + clientSocket);
                    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                    System.out.println("Esperando a recibir su key");
                    String key = dis.readUTF();
                    System.out.println("key recibido: " + key);
                    eventListener.onConnectClient(key, clientSocket);
                } catch (IOException e) {
                    System.out.println("Fallo al intentar conexion del cliente:" + e);
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
