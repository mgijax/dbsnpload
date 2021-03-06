package org.jax.mgi.app.dbsnploader;

import java.util.Vector;

/**
 *
 * is an object that represents the Contig Hit data from the dbsnp NSE input file
 * @has a chromosome name, an assembly (e.g. C57BL/6J), and a set of locations (hits)
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class DBSNPNseContigHit{

    // the chromosome on which this coordinate is found
    private String chromosome;
    // the assembly on which this coordinate is found
    private String assembly;
    // the assembly build number
    private String buildNum;

    // set of DBSNPMapLoc for this contig hit
    private Vector mapLocations;

    public DBSNPNseContigHit() {
        mapLocations = new Vector(1);
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

    public void setBuildNum (String b) {
        buildNum = b;
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
    public String getBuildNum() {
        return buildNum;
    }
    public Vector getMapLocations() {
        return mapLocations;
    }
}