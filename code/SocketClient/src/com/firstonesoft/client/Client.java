/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client;

import com.firstonesoft.client.event.EventClient;
import com.firstonesoft.client.event.EventListenerData;
import com.firstonesoft.client.util.ObjectUtil;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Clase que se encarga de hacer la interaccion con el Core para recibir y enviar informacion
 * @author JMCM
 */
public class Client implements EventListenerData {

    private int maxBufferSize;
    
    private int port;
    private String ip;
    private Socket clientSocket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ListenerData listenerData;
    private Sender sender;
    
    private EventClient eventClient;
    
    /**
     * Constructor
     * @param ip del Core
     * @param port del Core
     */
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.maxBufferSize = 1048576; // 1048576 Bytes = 1 Mg
    }

    /**
     * Constructor
     * @param ip del Core
     * @param port del Core
     * @param maxBufferSize Tamanio para los buffers del socket que tendra maximo
     */
    public Client(String ip, int port, int maxBufferSize) {
        this.ip = ip;
        this.port = port;
        this.maxBufferSize = maxBufferSize;
    }

    /**
     * Metodo para conectar con el Core
     * @param cerrado Parametro que indica si el key debe ser de la lista de keys del server, si es true, en caso de que sea false, se puede utilizar cualquier key siempre y cuando no se repita
     * @param key Key con el que  el cliente trata de conectarse
     * @return Devuelve un valor booleano que indica si se conecto o no
     * @throws UnknownHostException
     * @throws IOException 
     */
    public boolean connectClosed(final String key) throws UnknownHostException, IOException{
        clientSocket = new Socket(getIp(), getPort());
        clientSocket.setReceiveBufferSize(maxBufferSize);
        clientSocket.setSendBufferSize(maxBufferSize);

        dis = new DataInputStream(clientSocket.getInputStream());
        dos = new DataOutputStream(clientSocket.getOutputStream());
        dos.writeUTF(key);
        dos.writeBoolean(true);
        boolean ok = dis.readBoolean();
        if (ok) {
            listenerData = new ListenerData(clientSocket);
            listenerData.setRunning(true);
            listenerData.setEventListenerData(Client.this);
            listenerData.start();

            sender = new Sender(clientSocket);
        }else
        {
            clientSocket.close();
            clientSocket = null;
        }
        return ok;
    }
    
    /**
     * Metodo para conectar con el Core
     * @param cerrado Parametro que indica si el key debe ser de la lista de keys del server, si es true, en caso de que sea false, se puede utilizar cualquier key siempre y cuando no se repita
     * @param key Key con el que  el cliente trata de conectarse
     * @return Devuelve un valor booleano que indica si se conecto o no
     * @throws UnknownHostException
     * @throws IOException 
     */
    public boolean connectOpened(final String key, Object o) throws UnknownHostException, IOException{
        clientSocket = new Socket(getIp(), getPort());
        clientSocket.setReceiveBufferSize(maxBufferSize);
        clientSocket.setSendBufferSize(maxBufferSize);

        dis = new DataInputStream(clientSocket.getInputStream());
        dos = new DataOutputStream(clientSocket.getOutputStream());
        dos.writeUTF(key);
        dos.writeBoolean(false);
        byte[] data = ObjectUtil.createBytes(o);
        dos.writeLong(data.length);
        dos.write(data, 0, data.length);
        dos.flush();
        boolean ok = dis.readBoolean();
        if (ok) {
            listenerData = new ListenerData(clientSocket);
            listenerData.setRunning(true);
            listenerData.setEventListenerData(Client.this);
            listenerData.start();

            sender = new Sender(clientSocket);
        }else
        {
            clientSocket.close();
            clientSocket = null;
        }
        return ok;
    }

    /**
     * Metodo para obtener los keys que tiene el Core para poder hacer conexiones 
     * @return Un Map que contiene Key y Objeto que el Core tiene
     * @throws UnknownHostException
     * @throws IOException 
     */
    public Map<String, Object> requestKeys() throws UnknownHostException, IOException{
            clientSocket = new Socket(getIp(), getPort());
            clientSocket.setReceiveBufferSize(maxBufferSize);
            clientSocket.setSendBufferSize(maxBufferSize);

            dis = new DataInputStream(clientSocket.getInputStream());
            dos = new DataOutputStream(clientSocket.getOutputStream());
            dos.writeUTF("");
            dos.writeBoolean(true);
            int bytesRead;
            ByteArrayOutputStream output;
            long size = dis.readLong();
            byte[] buffer = new byte[8388608];  // 8388608 bit => 1 mg
            output = new ByteArrayOutputStream((int) size);
            while (size > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }
            Object o = ObjectUtil.createObject(output.toByteArray());
            Map<String, Object> keys = null;
            if (o instanceof Map) 
                keys = (Map<String, Object>) o;

            disconect();
            return keys;
    }
    
    /**
     * Metodo para desconectar este cliente del Core
     * @throws IOException 
     */
    public void disconect() throws IOException   
    {
        if (listenerData != null)
        {
            listenerData.setRunning(false);
            listenerData.closeListenerData();
        }
        if (sender != null)
            sender.closeSenderData();
        if (clientSocket != null)
            clientSocket.close();
    }

    /**
     * Enviar un paquete al server
     * @param DATA que se envia solo al Core
     * @throws IOException 
     */
    public void sendPackage(byte[] data) throws IOException {
        sender.sendBytes(data);
    }

    /**
     * Instancia para que escuche los eventos generados
     * @param EventClient 
     */
    public void setEventClient(EventClient eventClient) {
        this.eventClient = eventClient;
    }
    
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
    public void onDisconnectCore(IOException e) {
        try{
            sender.closeSenderData();
            listenerData.closeListenerData();
            if (eventClient != null)
                eventClient.onDisconnectCore(e);
        }catch(IOException io)
        {
            io.printStackTrace();
        }
    }
    
    /**
     * @return Puerto del Core
     */
    public int getPort() {
        return port;
    }

    /**
     * @return IP del Core
     */
    public String getIp() {
        return ip;
    }

    
    /**
     * Devuelve true o false si esta conectado al Core
     * @return 
     */
    public boolean isConnected()
    {
        return clientSocket!= null && clientSocket.isConnected();
    }
    
    
}
