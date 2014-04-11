/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client;

import com.firstonesoft.event.EventClient;
import com.firstonesoft.event.EventListenerData;
import com.firstonesoft.util.ObjectUtil;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 *
 * @author Bismarck
 */
public class Cliente {

    private int port;
    private String ip;
    private Socket clientSocket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ListenerData listenerData;
    private EventClient eventClient;
    private EventListenerData eventListenerData;

    public Cliente(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.eventListenerData = new EventListenerData() {

            @Override
            public void onNewPackage(long size) {
                eventClient.onNewPackage(size);
            }

            @Override
            public void onNewTrama(int bytesRead) {
                eventClient.onNewTrama(bytesRead);
            }

            @Override
            public void onNewPackageComplet(byte[] data) {
                eventClient.onNewPackageComplet(data);
            }

            @Override
            public void onDisconnectCore() {
                eventClient.onDisconnectCore();
            }
        };
    }

    public void connect(final String key) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(ip, port);
                    dis = new DataInputStream(clientSocket.getInputStream());
                    dos = new DataOutputStream(clientSocket.getOutputStream());
                    dos.writeUTF(key);
                    boolean ok = dis.readBoolean();
                    if (ok) {
                        listenerData = new ListenerData(clientSocket);
                        listenerData.setRunning(true);
                        listenerData.setEventListenerData(eventListenerData);
                        listenerData.start();
                    }
                    eventClient.onConnet(ok);
                } catch (UnknownHostException e) {
                    System.out.println("UnknownHostException: " + e);
                    eventClient.onFailedConnect(e.getMessage());
                } catch (IOException e) {
                    System.out.println("IOException: " + e);
                    eventClient.onFailedConnect(e.getMessage());
                }
            }
        };
        t.start();
    }

    public void connect() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(ip, port);
                    dis = new DataInputStream(clientSocket.getInputStream());
                    dos = new DataOutputStream(clientSocket.getOutputStream());
                    dos.writeUTF("");
                    int bytesRead;
                    ByteArrayOutputStream output;
                    try {
                        long size = dis.readLong();
                        byte[] buffer = new byte[8388608];  // 8388608 bit => 1 mg
                        output = new ByteArrayOutputStream((int) size);
                        while (size > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                            output.write(buffer, 0, bytesRead);
                            size -= bytesRead;
                        }
                        Object o = ObjectUtil.createObject(output.toByteArray());
                        if (o instanceof Map) {
                            Map<String, Object> keys = (Map<String, Object>) o;
                            eventClient.onReceiveDataKeys(keys);
                        } else {
                            eventClient.onFailedReceiveDataKeys("No se pudo instanciar como Map el Object");
                        }
                    } catch (IOException e) {
                        System.out.println(e);
                        eventClient.onFailedReceiveDataKeys(e.getMessage());
                    }
                } catch (UnknownHostException e) {
                    System.out.println(e);
                    eventClient.onFailedConnect(e.getMessage());
                } catch (IOException e) {
                    System.out.println(e);
                    eventClient.onFailedConnect(e.getMessage());
                }
            }
        };
        t.start();
    }

    public void connectWithValidate(final String key) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(ip, port);
                    if (eventClient.validateConnect(key)) {
                        dis = new DataInputStream(clientSocket.getInputStream());
                        dos = new DataOutputStream(clientSocket.getOutputStream());
                        dos.writeUTF(key);
                        boolean ok = dis.readBoolean();
                        if (ok) {
                            listenerData = new ListenerData(clientSocket);
                            listenerData.setRunning(true);
                            listenerData.setEventListenerData(eventListenerData);
                            listenerData.start();
                        }
                        dos.flush();
                        dos.close();
                        dis.close();
                        eventClient.onConnet(ok);
                    } else {
                        eventClient.onFailedConnectWithValidate("El key no existe en la validacion del cliente");
                    }
                } catch (UnknownHostException e) {
                    System.out.println(e);
                    eventClient.onFailedConnect(e.getMessage());
                } catch (IOException e) {
                    System.out.println(e);
                    eventClient.onFailedConnect(e.getMessage());
                }
            }
        };
        t.start();
    }
    
    /**
     * *** METODOS PARA EL ENVIO DE PAQUETES ****
     */
    public void sendPackage(byte [] data) {
        Sender sender = new Sender(data, listenerData);
        sender.start();
    }

    /**
     * *** GETTER AND SETTER ****
     */
    public void setEventClient(EventClient eventClient) {
        this.eventClient = eventClient;
    }
}
