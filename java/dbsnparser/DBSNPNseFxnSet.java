package dbsnparser;

public class DBSNPNseFxnSet {
    String locusId;
    String fxnClass;

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
    /******************
     * Get methods
     *****************/
    public String getLocusId() {
        return locusId;
    }
    public String getFxnClass() {
        return fxnClass;
    }
}