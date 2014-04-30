/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core.event;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author Bismarck
 */
public interface EventCore {
    
    public void onConnectClientClosed(String key);
    public void onConnectClientOpened(String key, Object o);
    public void onDisconnectClient(String key);
    
    public void onNewPackage(long size, String key);
    public void onNewTrama(int bytesRead, String key);
    public void onNewPackageComplet(byte [] data, String key);
    public void onExceptionListening(String key, IOException ioe); //
    
    
    public void onFailedSend(IOException ioe, String key);
    
    public void onSendClients(List<String> keys);
    public void onSendClient(String key);
    
    
}
