/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firstonesoft.client.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 *
 * @author Bismarck
 */
public class ObjectUtil {
    
    public static Object createObject(byte [] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in;
        Object o = null;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();
            bis.close();
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        return o;
    }

    public static byte [] createBytes(Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        byte[] yourBytes = null;
        try {

            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            yourBytes = bos.toByteArray();
            out.close();
            bos.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return yourBytes;
    }
    
}
