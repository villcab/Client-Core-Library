/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client.event;

import java.io.IOException;

/**
 *
 * @author JMCM
 */
public interface EventListenerData {
    
    void onNewPackage(long size);
    void onNewTrama(int bytesRead);
    void onNewPackageComplet(byte [] data);
    void onDisconnectCore(IOException e);
    
}
