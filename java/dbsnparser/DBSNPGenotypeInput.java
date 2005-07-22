package dbsnparser;

import java.util.HashMap;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;

class DBSNPGenotypeInput extends DBSNPInput{

    // ssId:HashMap(strain:allele)
    private HashMap ssAlleleMap;

    public DBSNPGenotypeInput() {
        ssAlleleMap = new HashMap();
    }
    public DBSNPGenotypeInput(String rs) {
        this();
        rsId = rs;
    }

    public void addSSAlleles(String ssId, HashMap alleles) {
        ssAlleleMap.put(ssId, alleles);
    }

    // get the strain allele map for 'ssId'
    public HashMap getAlleleMapForSS(String ssId) {
        return (HashMap)ssAlleleMap.get(ssId);
    }
    // get all the ss to strain allele mappings for this rs
    public HashMap getAlleleMapForRs() {
        return ssAlleleMap;
    }
/*
    // Vector of DBSNPGenotypeSS objects
    private Vector ssVector;


    public DBSNPGenotypeInput() {
        ssVector = new Vector();
    }

    // Add a SS object to the Vector
    public void addSS(DBSNPGenotypeSS s) {
        ssVector.add(s);
    }

    // get the SS objects for this RS
    public Vector getSubSNPs() {
        return ssVector;
    }
    */
}
