package org.jax.mgi.app.dbsnploader;

import java.util.Vector;
import java.util.HashMap;

public class DBSNPGenotypePopulation {
    private String popId;
    private HashMap strainAllelesMap;

    public DBSNPGenotypePopulation () {
        strainAllelesMap = new HashMap();
    }

    public void setPopId(String p) {
        popId = p;
    }
    public void addStrainAlleles(String strain, Allele allele) {
        strainAllelesMap.put(strain, allele);
    }
    public String getPopId () {
        return popId;
    }
    public HashMap getStrainAlleles() {
        return strainAllelesMap;
    }
}