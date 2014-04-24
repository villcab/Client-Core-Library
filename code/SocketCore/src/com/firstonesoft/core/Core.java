/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core;

import com.firstonesoft.core.event.EventListener;
import com.firstonesoft.core.event.EventListenerData;
import com.firstonesoft.core.event.EventCore;
import com.firstonesoft.core.event.EventSender;
import com.firstonesoft.core.util.ObjectUtil;
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
public class Core implements EventListener, EventListenerData, EventSender {

    private int maxBufferSize;
    private int port;
    private ServerSocket serverSocket;
    private Lista clientes;
    private Map<String, Object> keys;
    private EventCore eventCore;

    public Core(int port) {
        this.port = port;
        this.maxBufferSize = 1048576; // 1048576 Bytes = 1 Mg
    }

    public Core(int port, int maxBufferSize) {
        this.port = port;
        this.maxBufferSize = maxBufferSize;
    }

    public void openSession(Map<String, Object> keys) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.clientes = new Lista();
        this.keys = keys;
        Listener listener = new Listener(serverSocket);
        listener.setRunning(true);
        listener.setEventListener(this);
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

    /**
     * *** IMPLEMENT EVENT ****
     */
    @Override
    public void onConnectClient(boolean estatico, String key, Socket socket) throws IOException {
        System.out.println("key: " + key + ", Socket: " + socket);
        if (key.equalsIgnoreCase("")) { //se le envia todos los keys
            socket.setReceiveBufferSize(maxBufferSize);
            socket.setSendBufferSize(maxBufferSize);
            byte[] data = ObjectUtil.createBytes(keys);
            Sender s = new Sender(data, socket);
            s.setEventSender(this);
            s.start();
        } else {
            boolean result;
            if (clientes.containsKey(key)) { //no esta disponible el key
                result = false;
            } else { //esta disponible el key
                if (estatico) // Esta restringido a keys solo de la lista de keys del core
                {
                    result = !keys.containsKey(key);
                }
                else
                    result = true;
            }
            Sender s = new Sender(result, socket, key);
            s.setEventSender(this);
            s.start();
        }
    }

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
        clientes.remove(key);
        eventCore.onDisconnectClient(key);
    }

    @Override
    public void onSendState(boolean state, String key, Socket socket) throws IOException {
        if (state) {
            socket.setReceiveBufferSize(maxBufferSize);
            socket.setSendBufferSize(maxBufferSize);
            ListenerData listenerData = new ListenerData(key, socket);
            listenerData.setRunning(true);
            listenerData.setEventListenerData(this);
            listenerData.start();
            clientes.add(key, listenerData);
            eventCore.onConnectClient(key);
        }
    }

    @Override
    public void onFailedSendState(Exception e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onSendSocketBytes() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onFailedSendSocketBytes(Exception e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onSendClients() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onFailedSendClients(Exception e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onSendClient() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onFailedSendClient(Exception e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
