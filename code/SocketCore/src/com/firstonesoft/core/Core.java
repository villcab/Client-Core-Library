// SOJ UN TROLAZO BISMARCK

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core;

import com.firstonesoft.event.EventListener;
import com.firstonesoft.event.EventListenerData;
import com.firstonesoft.event.EventCore;
import com.firstonesoft.event.EventSender;
import com.firstonesoft.util.ObjectUtil;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bismarck
 */
public class Core {

    private int port;
    private ServerSocket serverSocket;
    private Lista clientes;
    private Map<String, Object> keys;
    private EventCore eventCore;
    private EventListener eventListener;
    private EventListenerData eventListenerData;
    private EventSender eventSender;

    public Core(int port) {
        this.port = port;
        this.eventListenerData = new EventListenerData() {
            @Override
            public void onNewPackage(long size) {
                eventCore.onNewPackage(size);
            }

            @Override
            public void onNewTrama(int bytesRead) {
                eventCore.onNewTrama(bytesRead);
            }

            @Override
            public void onNewPackageComplet(byte[] data) {
                eventCore.onNewPackageComplet(data);
            }

            @Override
            public void onDisconnectClient(String key) {
                System.out.println("se desconecto el cliente: " + key);
                clientes.remove(key);
                eventCore.onDisconnectClient(key);
            }
        };

        this.eventSender = new EventSender() {
            @Override
            public void onSendState(boolean state, String key, Socket socket) {
                if (state) {
                    ListenerData listenerData = new ListenerData(key, socket);
                    listenerData.setRunning(true);
                    listenerData.setEventListenerData(eventListenerData);
                    listenerData.start();
                    clientes.add(key, listenerData);
                    eventCore.onConnectClient(key);
                }
            }

            @Override
            public void onSendComplet() {
            }
        };

        this.eventListener = new EventListener() {
            @Override
            public void onConnectClient(String key, Socket socket) {
                System.out.println("key: " + key + ", Socket: " + socket);
                if (key.equalsIgnoreCase("")) { //se le envia todos los keys
                    byte[] data = ObjectUtil.createBytes(keys);
                    Sender s = new Sender(data, socket);
                    s.start();
                } else {
                    if (clientes.containsKey(key)) { //no esta disponible el key
                        Sender s = new Sender(false, socket, key);
                        s.setEventSender(eventSender);
                        s.start();
                    } else { //esta disponible el key
                        System.out.println("esta disponible el key");
                        Sender s = new Sender(true, socket, key);
                        s.setEventSender(eventSender);
                        s.start();
                    }
                }
            }
        };

    }

    public void openSession(Map<String, Object> keys) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.clientes = new Lista();
        this.keys = keys;
        Listener listener = new Listener(serverSocket);
        listener.setRunning(true);
        listener.setEventListener(eventListener);
        listener.start();
    }

    public void closeSession() {
    }

    /**
     * *** METODOS PARA EL ENVIO DE PAQUETES ****
     */
    //ENVIA A UN CLIENTE EN ESPECIFICO
    public void sendPackage(String key, byte[] data) {
        List<ListenerData> list = new ArrayList<ListenerData>();
        list.add(clientes.getCliente(key));
        Sender sender = new Sender(data, list);
        sender.start();
    }

    //ENVIA A UNA LISTA DE CLIENTES EN ESPECIFICO
    public void sendPackage(List<String> keys, byte[] data) {
        List<ListenerData> list = new ArrayList<ListenerData>();
        for (String s : keys) {
            list.add(clientes.getCliente(s));
        }
        Sender sender = new Sender(data, list);
        sender.start();
    }

    //ENVIA A TODOS LOS CLIENTES
    public void sendAllPackage(byte[] data) {
        Sender sender = new Sender(data, clientes.getClientes());
        sender.start();
    }

    /**
     * *** GETTER AND SETTER ****
     */
    public void setEventCore(EventCore eventCore) {
        this.eventCore = eventCore;
    }

    public List<String> getKeysClients() {
        return clientes.getKeysClientes();
    }
}
