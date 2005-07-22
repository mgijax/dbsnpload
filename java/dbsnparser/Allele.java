package dbsnparser;

class Allele {
    // The snp typing for this strain e.g. "T", "-", "ACTG"
    private String allele;
    // dbSNP flag conflict flag; not using this
    private boolean gtyFlag1 = false;
    // the orientation of this allele with respect to the RS flanking sequence
    private String orient;
    //default constructor
    public Allele() {

    }
    // construct an allele with its orientation
    public Allele(String all, String o) {
        allele = all;
        orient = o;
    }
    public void setAllele (String all) {
        allele = all;
    }
    public void setGtyFlag1 (boolean f) {
        gtyFlag1 = f;
    }
    public void setOrientation(String o) {
        orient = o;
    }
    public String getAllele() {
        return allele;
    }
    public boolean getGtyFlag1() {
        return gtyFlag1;
    }
    public String getOrientation() {
        return orient;
    }
}
