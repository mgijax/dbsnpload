package org.jax.mgi.app.dbsnploader;

public class DBSNPGenotypeIndividualInput {
    private String strainId;
    private String strain;

    public void setStrainId (String s)  {
        strainId = s;
    }
    public void setStrain (String s) {
        strain = s;
    }
    public String getStrainId () {
        return strainId;
    }
    public String getStrain() {
        return strain;
    }
}