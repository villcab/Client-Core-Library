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
    
    public void onConnectClient(boolean estatico, String key, Socket socket) throws IOException ;
    
}
