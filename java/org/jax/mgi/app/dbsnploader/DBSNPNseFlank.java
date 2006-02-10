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

public class DBSNPNseFlank {
    private String flankSeq;
    private Integer sequenceNum;

    /**********************
     * Constructors
     **********************/
    public DBSNPNseFlank() {

    }
    public DBSNPNseFlank(String flank, Integer sNum) {
        flankSeq = flank;
        sequenceNum = sNum;
    }
    /******************
      * Set methods
      *****************/
    public void setFlank(String flank) {
        flankSeq = flank;
    }
    public void setSequenceNum(Integer sNum) {
        sequenceNum = sNum;
    }
    /******************
     * Get methods
     *****************/
    public String getFlank() {
        return flankSeq;
    }
    public Integer getSequenceNum() {
        return sequenceNum;
    }
}