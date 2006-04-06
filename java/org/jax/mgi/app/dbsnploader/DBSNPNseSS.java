package org.jax.mgi.app.dbsnploader;

/**
 *
 * is an object that represents a dbSNP SS in the NSE input file
 * @has an ssId, a submitter SNP id, a submitter handle that identifies the sub
 * mitter, a variation class, the orientation of the SS to the RS, whether
 * this is the 'exemplar' SS (SS from which the flaning sequence is taken), and
 * the set of alleles observed in this SS.
 * @company Jackson Laboratory
 * @author sc
 *
 */

class DBSNPNseSS {

    // SS id
    private String ssId;
    // Submitter SNP id
    private String submitterSNPId;
    //submitter snp handle for this SS
    private String handle;
    // SS variation class
    private String ssVarClass;
    // SS orientation to RS
    private String ssOrientToRS;
    // true if exemplar ss
    private Boolean isExemplar = Boolean.FALSE;
    // dbsnp ss observed alleles; a summary of the ss alleles
    private String observedAlleles;

    /**********************
     * Constructors
     **********************/
    // create an empty DBSNPGenotypeSS
    public DBSNPNseSS() {

    }
    // create a DBSNPGenotypeSS with an ssId
   public DBSNPNseSS(String s) {
        ssId = s;
    }

    /******************
     * Set methods
     *****************/
    public void setSSId(String s) {
        ssId = s;
    }
    public void setSubmitterSNPId(String i) {
        submitterSNPId = i;
    }
    public void setSubmitterHandle(String h) {
        handle = h;
    }
    public void setSSVarClass(String c) {
         ssVarClass = c;
    }
    public void setSSOrientToRS(String o) {
        ssOrientToRS = o;
    }

    public void setIsExemplar(Boolean i) {
       isExemplar = i;
    }
    public void setObservedAlleles(String s) {
        observedAlleles = s;
    }
    /******************
     * Get methods
     *****************/
    public String getSSId() {
        return ssId;
    }
    public String getSubmitterSNPId() {
        return submitterSNPId;
    }
    public String getSubmitterHandle() {
        return handle;
    }
    public String getSSVarClass() {
        return ssVarClass;
    }
    public String getSSOrientToRS() {
        return ssOrientToRS;
     }
     public Boolean getIsExemplar() {
          return isExemplar;
     }
     public String getObservedAlleles() {
         return observedAlleles;
     }
}
