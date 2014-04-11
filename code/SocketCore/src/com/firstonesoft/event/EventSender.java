/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.event;

import java.net.Socket;

/**
 *
 * @author Bismarck
 */
public interface EventSender {
    
    public void onSendState(boolean state, String key, Socket socket);
    public void onSendComplet();
    
}
