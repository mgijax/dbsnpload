package org.jax.mgi.app.dbsnploader;

/**
 *
 * is a data object representing an 'individual' from the dbsnp genotype input file
 * @has a strain (which may be a JAX strain id) and a dbsnp strain id
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class DBSNPGenotypeIndividualInput {
    // dbsnp strainId
    private String strainId;

    // dbsnp strain, which may be a JAX strain id
    private String strain;

    /**
     * sets strain id
     * @param s a dbsnp strain id
     */
    public void setStrainId (String s)  {
        strainId = s;
    }

    /**
     * sets strain
     * @param s - a strain or JAX strain id
     */
    public void setStrain (String s) {
        strain = s;
    }

    /**
     * gets the dbsnp strain id
     * @return the strain id
     */
    public String getStrainId () {
        return strainId;
    }

    /**
     * gets the strain (or JAX strain id)
     * @return the strain (or JAX strain id)
     */
    public String getStrain() {
        return strain;
    }
}
