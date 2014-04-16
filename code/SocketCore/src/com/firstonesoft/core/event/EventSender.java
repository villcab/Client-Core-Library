/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core.event;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Bismarck
 */
public interface EventSender {

    public void onSendState(boolean state, String key, Socket socket) throws IOException ;
    public void onFailedSendState(Exception e);
    public void onSendSocketBytes();
    public void onFailedSendSocketBytes(Exception e);
    public void onSendClients();
    public void onFailedSendClients(Exception e);
    public void onSendClient();
    public void onFailedSendClient(Exception e);
    
}
