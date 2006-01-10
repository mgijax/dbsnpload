package org.jax.mgi.app.dbsnploader;

/**
 *
 * is an object that
 * @has
 * @does
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class DBSNPNseFxnSet {
    String locusId;
    String fxnClass;
    String contigAllele;
    String aaResidue;
    Integer aaPosition;
    Integer readingFrame;
    String nuclId;
    String protId;

    /**********************
     * Constructors
     **********************/
    public DBSNPNseFxnSet() {

    }
    public DBSNPNseFxnSet(String lId, String fClass) {
        locusId = lId;
        fClass = fxnClass;
    }
    /******************
      * Set methods
      *****************/
    public void setLocusId (String lId) {
        locusId = lId;
    }
    public void setFxnClass(String fClass) {
        fxnClass = fClass;
    }
    public void setContigAllele(String a) {
        contigAllele = a;
    }
    public void setAAResidue(String r) {
        aaResidue = r;
    }
    public void setAAPosition(String p) {
        aaPosition = new Integer(p);
    }
    public void setReadingFrame(String f) {
        readingFrame = new Integer(f);
    }
    public void setNucleotideId(String i) {
        nuclId = i;
    }
    public void setProteinId(String i) {
        protId = i;
    }
    /******************
     * Get methods
     *****************/
    public String getLocusId() {
        return locusId;
    }
    public String getFxnClass() {
        return fxnClass;
    }
    public String getContigAllele() {
        return contigAllele;
    }
    public String getAAResidue() {
        return aaResidue;
    }
    public Integer getAAPostition() {
        return aaPosition;
    }
    public Integer getReadingFrame () {
        return readingFrame;
    }
    public String getNucleotideId() {
        return nuclId;
    }
    public String getProteinId() {
        return protId;
    }
}