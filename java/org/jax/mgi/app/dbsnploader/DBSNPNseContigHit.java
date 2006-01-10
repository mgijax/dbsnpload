package org.jax.mgi.app.dbsnploader;

import java.util.Vector;

/**
 *
 * is an object that
 * @has
 * @does
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class DBSNPNseContigHit{

    // the chromosome on which this coordinate is found
    private String chromosome;
    // the assembly on which this coordinate is found
    private String assembly;

    // set of DBSNPMapLoc for this contig hit
    private Vector mapLocations;

    public DBSNPNseContigHit() {
        mapLocations = new Vector();
    }
    /******************
      * Set methods
      *****************/
    public void setChromosome (String c) {
        chromosome = c;
    }

    public void setAssembly (String a) {
        assembly = a;
    }

    public void addMapLocation(DBSNPNseMapLoc mLoc) {
        mapLocations.add(mLoc);
    }

    /******************
     * Get methods
     *****************/
    public String getChromosome() {
        return chromosome;
    }

    public String getAssembly() {
        return assembly;
    }
    public Vector getMapLocations() {
        return mapLocations;
    }
}