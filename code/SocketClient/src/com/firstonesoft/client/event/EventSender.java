/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client.event;

import java.io.IOException;

/**
 *
 * @author Bismarck
 */
public interface EventSender {
    
    public void onSendBytes();
    public void onFailedSendBytes(IOException e);
    
}
