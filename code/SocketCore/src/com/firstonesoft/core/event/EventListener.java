/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core.event;

import java.net.Socket;

/**
 *
 * @author Bismarck
 */
public interface EventListener {
    
    public void onConnectClient(String key, Socket socket);
    
}
