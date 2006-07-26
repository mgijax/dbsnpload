package org.jax.mgi.app.dbsnploader;

import java.util.Vector;
import java.util.HashMap;

/**
 *
 * is a data object representing the populations, by SubSnp, for a RefSnp
 * @has an rs id and a mapping of ss id to its population(s)
 * @company Jackson Laboratory
 * @author sc
 *
 */

class DBSNPGenotypeRefSNPInput {

    // mapping looks like: {ssId:Vector of DBSNPGenotypePopulation objects, ...}
    private HashMap ssPopulationMap;

    // RefSnp id
    private String rsId;

    /**
     * default constructor
     */
    public DBSNPGenotypeRefSNPInput() {
        // build 126 lets try 2 for now. In build 125 there were very few multi-
        //population subSnps
        ssPopulationMap = new HashMap(2);
    }

    /**
     * constructor which takes an rs id
     */
    public DBSNPGenotypeRefSNPInput(String rs) {
        this();
        rsId = rs;
    }

    /**
     * sets the rs id
     * @param r - a rs id
     */
    public void setRsId(String r) {
        rsId = r;
    }

    /**
     * adds a set of populations for an ss id
     * @param ssId - an ss id
     * @param pops - a Vector of DBSNPGenotypePopulation objects for 'ssId'
     */
    public void addPopulation(String ssId, Vector pops) {
        ssPopulationMap.put(ssId, pops);
    }

    /**
     * gets the rs id
     * @return the rs id
     */
    public String getRsId() {
        return rsId;
    }

    /**
     * gets the set of population objects for 'ssId'
     * @return Vector of DBSNPGenotypePopulation objects
     */
    public Vector getPopulationsForSS(String ssId) {
        return (Vector)ssPopulationMap.get(ssId);
    }

    /**
     * gets mapping of all ss population objects for this rs
     * @return HashMap of DBSNPGenotypePopulation objects by ss id
     */
    public HashMap getSSPopulationsForRs() {
        return ssPopulationMap;
    }
}

