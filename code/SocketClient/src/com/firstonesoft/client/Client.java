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
 *
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
    
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.maxBufferSize = 1048576; // 1048576 Bytes = 1 Mg
    }

    public Client(String ip, int port, int maxBufferSize) {
        this.ip = ip;
        this.port = port;
        this.maxBufferSize = maxBufferSize;
    }

    public boolean connect(boolean cerrado, final String key) throws UnknownHostException, IOException{
        clientSocket = new Socket(getIp(), getPort());
        clientSocket.setReceiveBufferSize(maxBufferSize);
        clientSocket.setSendBufferSize(maxBufferSize);

        dis = new DataInputStream(clientSocket.getInputStream());
        dos = new DataOutputStream(clientSocket.getOutputStream());
        dos.writeUTF(key);
        dos.writeBoolean(cerrado);
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

    public Map<String, Object> requestKeys() throws UnknownHostException, IOException{
            clientSocket = new Socket(getIp(), getPort());
            clientSocket.setReceiveBufferSize(maxBufferSize);
            clientSocket.setSendBufferSize(maxBufferSize);

            dis = new DataInputStream(clientSocket.getInputStream());
            dos = new DataOutputStream(clientSocket.getOutputStream());
            dos.writeUTF("");
            dos.writeBoolean(false);
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
     * *** METODOS PARA EL ENVIO DE PAQUETES ****
     */
    public void sendPackage(byte[] data) throws IOException {
        sender.sendBytes(data);
    }

    /**
     * *** GETTER AND SETTER ****
     */
    public void setEventClient(EventClient eventClient) {
        this.eventClient = eventClient;
    }
    
    /**
     * *** IMPLEMENT EVENT ****
     */
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
            eventClient.onDisconnectCore(e);
        }catch(IOException io)
        {
            io.printStackTrace();
        }
    }

  
    /**
     * GETTERS Y SETTERS PARA LA IP Y PUERTO
     */
    
    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isConnected()
    {
        return clientSocket!= null && clientSocket.isConnected();
    }
    
    
}
