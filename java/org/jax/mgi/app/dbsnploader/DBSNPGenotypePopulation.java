package org.jax.mgi.app.dbsnploader;

import java.util.Vector;
import java.util.HashMap;

/**
 * is a data object representing a dbsnp Population
 * @has a population id and a set of strain/alleles for the population
 * @does
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class DBSNPGenotypePopulation {
    // dbsnp Population id
    private String popId;

    // strain alleles (like {strain:allele, ..., strain:allele} )
    private HashMap strainAllelesMap;

    /**
     * default constructor creates the strain/allele map
     */
    public DBSNPGenotypePopulation () {
        strainAllelesMap = new HashMap();
    }

    /**
     * sets the Population id
     * @param p  - population id
     */

    public void setPopId(String p) {
        popId = p;
    }

    /**
     * adds a strain to allele mapping
     * @param strain - a strain
     * @param allele - the allele for 'strain'

     */
    public void addStrainAlleles(String strain, Allele allele) {
        strainAllelesMap.put(strain, allele);
    }

    /**
     * gets the Population id
     * @return the Population id
     */
    public String getPopId () {
        return popId;
    }

    /**
     * gets strain/allele map
     * @return HashMap of strain/alleles
     */
    public HashMap getStrainAlleles() {
        return strainAllelesMap;
    }
}
