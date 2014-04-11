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
    
    private Map<String, ListenerData> clientes;
    
    public Lista() {
        clientes = new HashMap<String, ListenerData>();
    }
    
    public void add(String key, ListenerData client) {
        clientes.put(key, client);
    }
    
    public void remove(String key) {
        clientes.remove(key);
    }
    
    public boolean containsKey(String key) {
        return clientes.containsKey(key);
    }
    
    public ListenerData getCliente(String key) {
        return clientes.get(key);
    }
    
    public List<String> getKeysClientes() {
        Collection<ListenerData> list = clientes.values();
        List<String> clients = new ArrayList<String>();
        for (ListenerData c: list) {
            clients.add(c.getKey());
        }
        return clients;
    }
    
    public List<ListenerData> getClientes() {
        return (List<ListenerData>) clientes.values();
    }
    
}
