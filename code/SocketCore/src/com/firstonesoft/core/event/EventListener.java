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
public interface EventListener {
    
    public void onConnectClientClosed(String key, Socket socket) throws IOException ;
    public void onConnectClientOpened(String key, Socket socket, Object o) throws IOException ;
    
}
