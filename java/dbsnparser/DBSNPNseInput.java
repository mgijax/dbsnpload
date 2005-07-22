package dbsnparser;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

class DBSNPNseInput extends DBSNPInput{
    // RS variation class
    private String rsVarClass;
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
        //subSNPs = new HashMap();
        flank5Prime = new Vector();
        flank3Prime = new Vector();
        contigHits = new Vector();
    }

    /******************
     * Set methods
     *****************/
    // set the RS variation class
    public void setRSVarClass(String rsVar) {
        rsVarClass = rsVar;
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
    // get the RS variation class
    public String getRSVarClass() {
        return rsVarClass;    }

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
