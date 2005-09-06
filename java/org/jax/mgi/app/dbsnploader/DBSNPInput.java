package org.jax.mgi.app.dbsnploader;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

public abstract class DBSNPInput {
    // RefSNP id
    protected String rsId;

    // set the RefSNP id
    public void setRsId(String id) {
        rsId = id;
    }
    // get the RS id
     public String getRsId() {
         return rsId;
     }
}