package org.jax.mgi.app.dbsnploader;

import java.util.HashMap;
import java.util.Iterator;
public class HashMapTest{
    public static void main(String[] args) {
        HashMap map = new HashMap();
        map.put("1", "one");
        map.put("2", "two");
        map.put("3", "three");
        map.put("4", "four");
        map.put("5", "five");
        HashMap newMap = new HashMap(map);
        for(Iterator i = map.keySet().iterator(); i.hasNext();) {
            String key = (String)i.next();
            System.out.println("map key: " + key);
            String r = (String) newMap.remove(key);
            System.out.println("newMap value removed: " + r);
            for (Iterator j = newMap.keySet().iterator(); j.hasNext(); ) {
                System.out.println(newMap.get( (String) j.next()));
            }
        }
    }
}