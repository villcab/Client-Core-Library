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
    
    public void onConnectClient(String key);
    public void onDisconnectClient(String key);
    
    public void onNewPackage(long size);
    public void onNewTrama(int bytesRead);
    public void onNewPackageComplet(byte [] data);
    public void onExceptionListening(String key, IOException ioe); //
    
    
    public void onFailedSend(IOException ioe, String key);
    
    public void onSendClients(List<String> keys);
    public void onSendClient(String key);
    
    
}
