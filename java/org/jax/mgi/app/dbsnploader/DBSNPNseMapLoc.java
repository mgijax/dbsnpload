package org.jax.mgi.app.dbsnploader;

import java.util.Vector;

public class DBSNPNseMapLoc{
    // the RS orientation to the chromosome
    private String rsOrientToChr;
    // the coordinate
    private Double startCoord;
    // the set Function sets for this Map Location
    private Vector fxnSets;

    /**********************
     * Constructors
     **********************/
    public DBSNPNseMapLoc() {
        fxnSets = new Vector();
    }

    /******************
     * Set methods
     *****************/
    // set RS orientation to the chromosome
    public void setRSOrientToChr( String o) {
        rsOrientToChr = o;
    }
    public void setStartCoord (Double c) {
         startCoord = c;
    }
    // add fxn to fxnSet for this map location
    public void addFxn (String locusId, String fxnClass) {
        fxnSets.add(new DBSNPNseFxnSet(locusId, fxnClass));
    }
    public void addFxn(DBSNPNseFxnSet fSet) {
        fxnSets.add(fSet);
    }

    /******************
     * Get methods
     *****************/
    public Double getStartCoord() {
        return startCoord;
    }
    public String getRSOrientToChr() {
        return rsOrientToChr;
    }
    public Vector getFxnSets() {
        return fxnSets;
    }
}