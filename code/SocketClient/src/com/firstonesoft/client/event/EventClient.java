/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client.event;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author JMCM
 */
public interface EventClient {
    
    public void onNewPackage(long size);
    public void onNewTrama(int bytesRead);
    public void onNewPackageComplet(byte [] data);
    public void onDisconnectCore(IOException e);
    
}
