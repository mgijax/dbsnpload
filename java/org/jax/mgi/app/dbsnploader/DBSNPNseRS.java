package org.jax.mgi.app.dbsnploader;

import java.util.HashMap;

/**
 *
 * is an object that
 * @has
 * @does
 * @company Jackson Laboratory
 * @author sc
 *
 */

class DBSNPNseRS {

    // RS id
    private String rsId;
    // RS variation class
    private String rsVarClass;
    // Build this RS created
    private String buildRSCreated;
    // build this RS updated
    private String buildRSUpdated;

    /**********************
     * Constructors
     **********************/
    // create an empty DBSNPNseRS
    public DBSNPNseRS() {

    }
    // create a DBSNPNseRS with an rsId
   public DBSNPNseRS(String s) {
        rsId = s;
    }

    /******************
     * Set methods
     *****************/
    public void setRsId(String s) {
        rsId = s;
    }

    public void setBuildCreated(String created) {
        buildRSCreated = created;
    }

    public void setBuildUpdated(String updated) {
        buildRSUpdated = updated;
    }

    public void setRsVarClass(String rsVar) {
        rsVarClass = rsVar;
    }

    /******************
     * Get methods
     *****************/
    public String getRsId() {
        return rsId;
    }

    public String getRsVarClass() {
        return rsVarClass;
    }

    public String getBuildCreated() {
        return buildRSCreated;
    }

    public String getBuildUpdated() {
        return buildRSUpdated;
    }
}
