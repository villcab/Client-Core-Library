/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.event;

import java.util.Map;

/**
 *
 * @author Bismarck
 */
public interface EventClient {
    
    public void onConnet(boolean state);
    public void onFailedConnect(String msg);
    public void onReceiveDataKeys(Map<String, Object> keys);
    public void onFailedReceiveDataKeys(String msg);
    public void onFailedConnectWithValidate(String msg);
    public void onNewPackage(long size);
    public void onNewTrama(int bytesRead);
    public void onNewPackageComplet(byte [] data);
    public void onDisconnectCore();
    public boolean validateConnect(String key);
    
}
