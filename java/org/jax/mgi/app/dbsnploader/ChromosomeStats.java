package org.jax.mgi.app.dbsnploader;

/**
 * is an object that represents chromosome statistics
 * @has a chromosome, starting free memory in bytes, free memory after loading
 *      the genotype lookup etc
 * @does is simply a data object
 * @company Jackson Laboratory
 * @author sc
 *
 */


public class ChromosomeStats {
    // the chromsoome
    private String chromosome;
    // starting free memory, this is after garbage collection
    private long startFreeMem;
    // free memory after the genotype lookup is created
    private long freeMemAfterGenoLookup;
    // free memory at end of processing, htis is before garbage collection
    private long endFreeMem;
    // total RefSnps in the chromosome file
    private int totalSnpsOnChr;
    // total RefSnps loaded
    private int totalRefSnpsLoaded;
    // total SubSnps loaded
    private int totalSubSnpsLoaded;
    // elapsed time to process
    private float timeToProcess;

    public void setChromosome(String c) {
        chromosome = c;
    }
    public void setStartFreeMem(long m) {
        startFreeMem = m;
    }
    public void setFreeMemAfterGenoLookup(long m) {
        freeMemAfterGenoLookup = m;
    }
    public void setEndFreeMem(long m) {
        endFreeMem = m;
    }
    public void setTotalSnpsOnChr(int s) {
        totalSnpsOnChr = s;
    }
    public void setTotalRefSnpsLoaded(int s) {
        totalRefSnpsLoaded = s;
    }
    public void setTotalSubSnpsLoaded(int s) {
        totalSubSnpsLoaded = s;
    }
    public void setTimeToProcess(float t) {
        timeToProcess = t;
    }
    public String getChromosome() {
        return chromosome;
    }
    public long getStartFreeMem() {
        return startFreeMem;
    }
    public long getFreeMemAfterGenoLookup() {
        return freeMemAfterGenoLookup;
    }
    public long getEndFreeMem() {
        return endFreeMem;
    }
    public int getTotalRefSnpsOnChr() {
        return totalSnpsOnChr;
    }

    public int getTotalRefSnpsLoaded() {
        return totalRefSnpsLoaded;
    }
    public int getTotalSubSnpsLoaded() {
        return totalSubSnpsLoaded;
    }

    public float getTimeToProcess() {
        return timeToProcess;
    }
}