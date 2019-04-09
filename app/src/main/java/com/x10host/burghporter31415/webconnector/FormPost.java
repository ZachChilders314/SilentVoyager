/**
 * author: Dylan Porter
 * permission: Open-Source
 * project: Silent Voyager
 * file: FormPost.java
 *
 * Reference: https://stackoverflow.com/questions/27513282/send-data-via-post-android-to-php
 *
 */

package com.x10host.burghporter31415.webconnector;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FormPost <K,V> {

    private HashMap <K, V> KVMap;
    private List<V> values;

    public FormPost(K key, V value) {

        KVMap = new HashMap <K, V>();
        this.KVMap.put(key, value);

    }

    public FormPost() { KVMap = new HashMap <K, V>(); values = new ArrayList<V>(); }

    /*TODO: Handle GET and HEAD*/

    public String submitPost(PHPPage page, MethodType method) {

        List<NameValuePair> nameValuePairs
                = new ArrayList<NameValuePair>(this.KVMap.size());

        for(K key: KVMap.keySet()) {

            nameValuePairs.add(
                    new BasicNameValuePair(String.valueOf(key), String.valueOf(KVMap.get(key))));

        }

        String result = "";

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(page.getURL());

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            result = httpclient.execute(httppost, responseHandler);

        } catch(Exception ex) {
            result = "Failed";
            ex.printStackTrace();
        }

        return result;

    }

    public HashMap <K, V> getPairs() {
        return this.KVMap;
    }


    public void addPair(K key, V value) {
        this.KVMap.put(key, value);
        if(!this.values.contains(value)) {
            this.values.add(value);
        }
    }

    public void removePair(K key) {
        this.values.remove(this.KVMap.get(key));
        this.KVMap.remove(key);
    }

    public V[] getValues() {
        String[] arr = new String[values.size()];
        arr = values.toArray(arr);
        return (V[]) arr;
    }

}