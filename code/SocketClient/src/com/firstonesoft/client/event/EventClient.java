/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client.event;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Bismarck
 */
public interface EventClient {
    
    public void onConnet(boolean state);
    public void onFailedConnect(IOException e);
    public void onReceiveDataKeys(Map<String, Object> keys);
    public void onFailedReceiveDataKeys(IOException e);
    public void onFailedConnectWithValidate(String msg);
    public void onNewPackage(long size);
    public void onNewTrama(int bytesRead);
    public void onNewPackageComplet(byte [] data);
    public void onDisconnectCore(IOException e);
    public boolean validateConnect(String key);
    
}
