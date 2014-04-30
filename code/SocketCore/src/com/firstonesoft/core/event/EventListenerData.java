/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core.event;

import java.io.IOException;

/**
 *
 * @author Bismarck
 */
public interface EventListenerData {
    
    public void onNewPackage(long size,String key);
    public void onNewTrama(int bytesRead,String key);
    public void onNewPackageComplet(byte [] data,String key);
    public void onDisconnectClient(String key);
    
    public void onExceptionListening(String key, IOException ioe);
    
}
