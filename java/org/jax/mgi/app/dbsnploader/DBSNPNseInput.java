package org.jax.mgi.app.dbsnploader;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

class DBSNPNseInput {
    // the RS object
    private DBSNPNseRS rs;
    private String rsId;
    // set of DBSNPNseSS objects
    private Vector subSNPs;
    //private HashMap subSNPs;
    // set of of 5' Flanking sequence (in 255 char chunks)
    private Vector flank5Prime;
    // set of 3' Flanking sequence (in 255 char chunks)
    private Vector flank3Prime;
    // set of contig hits
    private Vector contigHits;

    /**********************
     * Constructors
     **********************/
    public DBSNPNseInput() {
        subSNPs = new Vector();
        flank5Prime = new Vector();
        flank3Prime = new Vector();
        contigHits = new Vector();
    }

    /******************
     * Set methods
     *****************/
    // Add RS object
    public void setRS(DBSNPNseRS r ){
        rs = r;
        rsId = r.getRsId();
    }
    public void setRsId(String r) {
        rsId = r;
    }
    // Add a SS object
    public void addSS(DBSNPNseSS s) {
        subSNPs.add(s);
    }
    // Add a 5' flanking sequence object
    public void add5PrimeFlank(DBSNPNseFlank f) {
        flank5Prime.add(f);
    }
    // Add a 3' flanking sequence object
    public void add3PrimeFlank(DBSNPNseFlank f) {
        flank3Prime.add(f);
    }
    // Add a 5' contig hit
    public void addContigHit(DBSNPNseContigHit h) {
        contigHits.add(h);
    }

    /******************
     * Get methods
     *****************/
    // get the RS object
    public DBSNPNseRS getRS() {
        return rs;
    }
    public String getRsId() {
        return rsId;
    }
    // get the SS objects for this RS
    public Vector getSubSNPs() {
        return subSNPs;
    }
    // get the 5' flank objects for this RS
    public Vector get5PrimeFlank() {
        return flank5Prime;
    }
    // get the 3' flank objects for this RS
    public Vector get3PrimeFlank() {
        return flank3Prime;
    }
    // get contig hits
    public Vector getContigHits() {
        return contigHits;
    }
    // get next 5' flank chunk sequence num
    public Integer getNext5PrimeSeqNum() {
        return new Integer(flank5Prime.size() + 1);
    }
    // get next 5' flank chunk sequence num
    public Integer getNext3PrimeSeqNum() {
        return new Integer(flank3Prime.size() + 1);
    }




}
