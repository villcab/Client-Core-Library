/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.event;

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
    
}
