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
    private Listener listener;

    
    /**
     * Constructor
     * @param port Puerto habilitado para recibir a los Clientes 
     */
    public Core(int port) {
        this.port = port;
        this.maxBufferSize = 1048576; // 1048576 Bytes = 1 Mg
    }

    /**
     * Constructor
     * @param port Puerto habilitado para recibir a los Clientes
     * @param maxBufferSize Tamanio maximo de buffer que puede tener el Core
     */
    public Core(int port, int maxBufferSize) {
        this.port = port;
        this.maxBufferSize = maxBufferSize;
    }

    /**
     * Inicia la escucha del lado del Core para recibir conexiones y acceder a datos.
     * @param keys Conjunto de llaves con sus objetos para que los clientes puedan conectarse de manera estatica
     * @throws IOException 
     */
    public void openSession(Map<String, Object> keys) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.clientes = new Lista();
        this.keys = keys;
        listener = new Listener(serverSocket);
        listener.setRunning(true);
        listener.setEventListener(this);
        listener.start();
    }

    /**
     * Metodo para cerrar el Core y todos los clientes con los que esta conectado
     * @throws IOException 
     */
    public void closeSession() throws IOException {
        listener.setRunning(false);
        for (ListenerData ld : clientes.getClientes())
        {
            ld.closeListenerData();
        }
        serverSocket.close();
    }

    /**
     * Metodo para enviar un paquete a un cliente
     * @param key Cliente
     * @param data Objeto a enviar
     */
    public void sendPackage(String key, byte[] data) {
        Sender sender = new Sender(data, clientes.getCliente(key));
        sender.setEventSender(this);
        sender.start();
    }

    /**
     * Metodo para enviar un paquete a un conjunto de clientes
     * @param keys Clientes a los que se enviara la informacion
     * @param data Objeto a enviar
     */
    public void sendPackage(List<String> keys, byte[] data) {
        List<ListenerData> list = new ArrayList<ListenerData>();
        for (String s : keys) {
            list.add(clientes.getCliente(s));
        }
        Sender sender = new Sender(data, list);
        sender.setEventSender(this);
        sender.start();
    }

    /**
     * Metodo para enviar un paquete a todos los clientes que estan conectados
     * Realizar un Broadcast
     * @param data Objeto a enviar
     */
    public void sendAllPackage(byte[] data) {
        Sender sender = new Sender(data, clientes.getClientes());
        sender.setEventSender(this);
        sender.start();
    }

    /**
     * Escuchador de los eventos que suceden en el Core
     * @param eventCore Escuchador
     */
    public void setEventCore(EventCore eventCore) {
        this.eventCore = eventCore;
    }

    /**
     * Obtener la lista de keys existentes conectados
     * @return Lista
     */
    public List<String> getKeysClients() {
        return clientes.getKeysClientes();
    }

    /**
     * Devuelve el puerto con el que esta trabajando el Core
     * @return 
     */
    public int getPort()
    {
        return this.port;
    }
    
    /**
     * Devuelve el tamanio actual del buffer
     * @return 
     */
    public int getMaxSizeBuffer()
    {
        return this.maxBufferSize;
    }
    
    @Override
    public void onConnectClientClosed(String key, Socket socket) throws IOException {
        System.out.println("key: " + key + ", Socket: " + socket);
        if (key.equalsIgnoreCase("")) { //se le envia todos los keys
            socket.setReceiveBufferSize(maxBufferSize);
            socket.setSendBufferSize(maxBufferSize);
            byte[] data = ObjectUtil.createBytes(keys);
            Sender s = new Sender(data, socket, key);
            s.setEventSender(this);
            s.start();
        } else {
            boolean result;
            if (clientes.containsKey(key)) { //no esta disponible el key
                result = false;
            } else { //esta disponible el key
//                if (estatico) // Esta restringido a keys solo de la lista de keys del core
                    result = keys.containsKey(key);
//                else
//                    result = true;
            }
            Sender s = new Sender(result, socket, key);
            s.setEventSender(this);
            s.start();
            eventCore.onConnectClientClosed(key);
        }
    }

    @Override
    public void onNewPackage(long size, String key) {
        eventCore.onNewPackage(size,key);
    }

    @Override
    public void onNewTrama(int bytesRead,String key) {
        eventCore.onNewTrama(bytesRead, key);
    }

    @Override
    public void onNewPackageComplet(byte[] data, String key) {
        eventCore.onNewPackageComplet(data, key);
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
        }
    }

    @Override
    public void onSendClients(List<String> keys) {
        eventCore.onSendClients(keys);
    }


    @Override
    public void onSendClient(String key) {
        eventCore.onSendClient(key);
    }

    @Override
    public void onFailedSend(IOException e, String key) {
        eventCore.onFailedSend(e, key);
    }

    @Override
    public void onExceptionListening(String key, IOException ioe) {
        eventCore.onExceptionListening(key, ioe);
    }

    @Override
    public void onConnectClientOpened(String key, Socket socket, Object o) throws IOException {
        System.out.println("key: " + key + ", Socket: " + socket);
        if (key.equalsIgnoreCase("")) { //se le envia todos los keys
            socket.setReceiveBufferSize(maxBufferSize);
            socket.setSendBufferSize(maxBufferSize);
            byte[] data = ObjectUtil.createBytes(keys);
            Sender s = new Sender(data, socket, key);
            s.setEventSender(this);
            s.start();
        } else {
            boolean result;
            if (clientes.containsKey(key)) { //no esta disponible el key
                result = false;
            } else { //esta disponible el key
//                if (estatico) // Esta restringido a keys solo de la lista de keys del core
//                    result = keys.containsKey(key);
//                else
                    result = true;
            }
            Sender s = new Sender(result, socket, key);
            s.setEventSender(this);
            s.start();
            eventCore.onConnectClientOpened(key, o);
        }
    }
}
