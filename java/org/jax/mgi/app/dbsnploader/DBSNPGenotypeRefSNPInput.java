package org.jax.mgi.app.dbsnploader;

import java.util.HashMap;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;

class DBSNPGenotypeRefSNPInput {//implements DBSNPInput{

    // ssId:Vector of DBSNPGenotypePopulation objects
    private HashMap ssPopulationMap;
    private String rsId;

    public DBSNPGenotypeRefSNPInput() {
        ssPopulationMap = new HashMap();
    }
    public DBSNPGenotypeRefSNPInput(String rs) {
        this();
        rsId = rs;
    }
    public void setRsId(String r) {
        rsId = r;
    }
    public void addPopulation(String ssId, Vector pops) {
        ssPopulationMap.put(ssId, pops);
    }

    public String getRsId() {
        return rsId;
    }
    // get the Population Vector for 'ssId'
    public Vector getPopulationsForSS(String ssId) {
        return (Vector)ssPopulationMap.get(ssId);
    }

    // get all  ss to Population mappings for this RS
    public HashMap getSSPopulationsForRs() {
        return ssPopulationMap;
    }
}
