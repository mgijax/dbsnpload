package org.jax.mgi.app.dbsnploader;

/**
 *
 * is an object that represents the Flank data from a dbsnp NSE input file
 * @has a nucleotide sequence, a sequence number (if there are > 255 chars in a
 * flanking sequence, the sequence is split into multiple pieces.
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
   /**
    * set the flanking sequence
    * @param flank String of nucleotide sequence
    */

    public void setFlank(String flank) {
        flankSeq = flank;
    }
    /**
     * set the sequence number of this Flanking sequence
     * @param sNum the sequence number, in a series, of this flanking sequence
     */
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