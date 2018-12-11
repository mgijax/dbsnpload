package org.jax.mgi.app.dbsnploader;

/**
 *
 * is a data object representing a dbsnp allele
 * @has an allele typing and its orientation to the RS flanking sequence,
 *      forward or reverse
 * @does
 * @company Jackson Laboratory
 * @author sc
 *
 */

class Allele {
    // The snp typing for this strain e.g. "T", "-", "ACTG"
    private String allele;

    // the orientation of this allele with respect to the RS flanking sequence
    private String orient;

    /**
     * default constructor
     */

    public Allele() {

    }

    /**
     * constructor which takes an allele and its orientation to the RS flanking
     * sequence
     * @param all - the allele
     * @param o - the orientation
     */
    public Allele(String all, String o) {
        allele = all;
        orient = o;
    }

    /**
     * sets the allele
     * @param all - an allele
     */
    public void setAllele (String all) {
        allele = all;
    }

    /**
     * sets the orientation
     * @param o - the orientation
     */
    public void setOrientation(String o) {
        orient = o;
    }

    /**
     * gets the allele
     * @return the allele value
     */
    public String getAllele() {
        return allele;
    }

    /**
     * gets the orientation
     * @return the orientation of the allele to the RS Flanking sequence
     */
    public String getOrientation() {
        return orient;
    }
}

