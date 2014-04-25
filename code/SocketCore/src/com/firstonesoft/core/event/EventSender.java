/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core.event;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author Bismarck
 */
public interface EventSender {

    public void onSendState(boolean state, String key, Socket socket) throws IOException ;
    public void onFailedSend(IOException e, String keys);
    public void onSendClients(List<String> key);
    public void onSendClient(String key);
    
}
