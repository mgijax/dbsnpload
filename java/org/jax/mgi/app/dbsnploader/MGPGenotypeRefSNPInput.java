package org.jax.mgi.app.dbsnploader;

/**
 *
 * is a data object representing the populations, by SubSnp, for a RefSnp
 * @has an rs id and a mapping of ss id to its population(s)
 * @company Jackson Laboratory
 * @author sc
 *
 */

class MGPGenotypeRefSNPInput {

    // mapping looks like: {strain:AlleleObject, ...}
	// only one population for mgp
    //private HashMap ssPopulationMap;

    // RefSnp id
    private String  rsId;
    
    // pipe-delim string for strain alleles for this rs
    private String strainAlleles;
   

    /**
     * constructor
     */
    public MGPGenotypeRefSNPInput(String rs, String sa) {
        rsId = rs;
        strainAlleles = sa;
    }

    /**
     * gets the rs id
     * @return the rs id
     */
    public String getRsId() {
        return rsId;
    }
    
    /**
     * gets strain/allele map
     * @return HashMap of strain/alleles
     */
    public String getStrainAlleles() {
        return strainAlleles;
    }

}

