/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author JMCM
 */
public class Sender {

    private DataOutputStream dos;
    
    
    public Sender(Socket clientSocket) throws IOException {
        dos = new DataOutputStream(clientSocket.getOutputStream());
    }
    
    /**
     * *** ENVIAR PAQUETES EN BYTES ****
     */
    public void sendBytes(byte[] data) throws IOException {
        dos.writeLong(data.length);
        dos.write(data);
        dos.flush();
    }
    
    public void closeSenderData() throws IOException
    {
        if (dos != null) {
            dos.flush();
            dos.close();
       }
    }
    

}
