package org.jax.mgi.app.dbsnploader;

import java.util.Vector;

/**
 *
 * is an object that represents the set of raw data for a dbSNP RefSnp
 * @has
 * <OL>
 * <LI>A DBSNPNseRS
 * <LI>An rsId
 * <LI>A set of DBSNPNseSS objects representing SS belonging to this RS
 * <LI>A set of 5' DBSNPNseFlank objects
 * <LI>A set of 3' DBSNPNseFlank objects
 * <LI>A set of DBSNPNseContigHit objects
 * </OL>
 * @company Jackson Laboratory
 * @author sc
 *
 */

class DBSNPNseInput {
    // the RS object
    private DBSNPNseRS rs;
    private String rsId;
    // set of DBSNPNseSS objects
    private Vector subSNPs;
    // set of of 5' DBSNPNseFlank objects (each representing 255 char chunks)
    private Vector flank5Prime;
    // set of 3' DBSNPNseFlank objects (each representing 255 char chunks)
    private Vector flank3Prime;
    // set DBSNPNseContigHit objects
    private Vector contigHits;

    /**********************
     * Constructors
     **********************/
    public DBSNPNseInput() {
        // build 126 1.09 SubSnp per Snp
        subSNPs = new Vector(2);
        // build 126 2.78 flanks (3' +  5') per Snp
        flank5Prime = new Vector(2);
        flank3Prime = new Vector(2);
        // build 126 1.05 coordinates per Snp
        contigHits = new Vector(1);
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
