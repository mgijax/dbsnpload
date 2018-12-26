package org.jax.mgi.app.dbsnploader;

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

    // mapping looks like: {ssId:Array of DBSNPGenotypePopulation objects, ...}
	// only one population for mgp
    private HashMap ssPopulationMap;

    // RefSnp id
    private int rsId;

    /**
     * default constructor
     */
    public DBSNPGenotypeRefSNPInput() {
        // build 126 lets try 2 for now. In build 125 there were very few multi-
        //population subSnps
        ssPopulationMap = new HashMap(1);
    }

    /**
     * constructor which takes an rs id
     */
    public DBSNPGenotypeRefSNPInput(int rs) {
        this();
        rsId = rs;
    }

    /**
     * sets the rs id
     * @param r - a rs id
     */
    public void setRsId(int r) {
        rsId = r;
    }

    /**
     * adds a set of populations for an ss id
     * @param ssId - an ss id
     * @param pops - an array of DBSNPGenotypePopulation objects for 'ssId'
     */
    public void addPopulation(String ssId, DBSNPGenotypePopulation[] pops) {
        ssPopulationMap.put(Integer.valueOf(ssId), pops);
    }

    /**
     * gets the rs id
     * @return the rs id
     */
    public int getRsId() {
        return rsId;
    }

    /**
     * gets the set of population objects for 'ssId'
     * @return array of DBSNPGenotypePopulation objects
     */
    public DBSNPGenotypePopulation[] getPopulationsForSS(String ssId) {
        return (DBSNPGenotypePopulation[])ssPopulationMap.get(Integer.valueOf(ssId));
    }

    /**
     * gets mapping of all ss population objects for this rs
     * @return HashMap of DBSNPGenotypePopulation objects by ss id
     */
    public HashMap getSSPopulationsForRs() {
        return ssPopulationMap;
    }
    public void reinit() {
    	
    }
}

