/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Bismarck
 */
public class Lista {
    
    private Map<String, ListenerData> clients;
    
    public Lista() {
        clients = new HashMap<String, ListenerData>();
    }
    
    public void add(String key, ListenerData client) {
        clients.put(key, client);
    }
    
    public void remove(String key) {
        clients.remove(key);
    }
    
    public boolean containsKey(String key) {
        return clients.containsKey(key);
    }
    
    public ListenerData getCliente(String key) {
        return clients.get(key);
    }
    
    public List<String> getKeysClientes() {
        Collection<ListenerData> list = clients.values();
        List<String> cs = new ArrayList<String>();
        for (ListenerData c: list) {
            cs.add(c.getKey());
        }
        return cs;
    }
    
    public List<ListenerData> getClientes() {
        return new ArrayList<ListenerData>(clients.values());
    }
    
    public void closeConections()
    {
        Collection<ListenerData> cls = clients.values();
        for (ListenerData ld : cls)
        {
            ld.closeListenerData();
        }
    }
    
}
