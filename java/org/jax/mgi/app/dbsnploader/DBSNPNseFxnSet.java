package org.jax.mgi.app.dbsnploader;

/**
 *
 * is an object that represents a Function Set in the DBSNP NSE input file
 * @has
 * <OL>
 * <LI>locusId - entrezgene id of a Marker associated with this snp
 * <LI>fxnClass - functional class of the snp
 * <LI>contigAllele - allele on the contig where this snp lies
 * <LI>aaResidue - the amino acid where this snp lies on the contig
 * <LI>aaPosition - amino acid position
 * <LI>readingFrame - reading frame where this snp lies on the contig
 * <LI>nuclId - RefSeq nucleotide id for the marker
 * <LI>protId - RefSeq protein id for the marker
 * </OL
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class DBSNPNseFxnSet {
    String locusId;
    String fxnClass;
    String contigAllele;
    String aaResidue;
    Integer aaPosition;
    Integer readingFrame;
    String nuclId;
    String protId;

    /**********************
     * Constructors
     **********************/
    public DBSNPNseFxnSet() {

    }
    public DBSNPNseFxnSet(String lId, String fClass) {
        locusId = lId;
        fClass = fxnClass;
    }
    /******************
      * Set methods
      *****************/
    public void setLocusId (String lId) {
        locusId = lId;
    }
    public void setFxnClass(String fClass) {
        fxnClass = fClass;
    }
    public void setContigAllele(String a) {
        contigAllele = a;
    }
    public void setAAResidue(String r) {
        aaResidue = r;
    }
    public void setAAPosition(String p) {
	int pos = Integer.parseInt(p);
	 pos ++;
        // dbSNP is 0-based as of build 125
        aaPosition = new Integer(pos);
    }
    public void setReadingFrame(String f) {
        readingFrame = new Integer(f);
    }
    public void setNucleotideId(String i) {
        nuclId = i;
    }
    public void setProteinId(String i) {
        protId = i;
    }
    /******************
     * Get methods
     *****************/
    public String getLocusId() {
        return locusId;
    }
    public String getFxnClass() {
        return fxnClass;
    }
    public String getContigAllele() {
        return contigAllele;
    }
    public String getAAResidue() {
        return aaResidue;
    }
    public Integer getAAPosition() {
        return aaPosition;
    }
    public Integer getReadingFrame () {
        return readingFrame;
    }
    public String getNucleotideId() {
        return nuclId;
    }
    public String getProteinId() {
        return protId;
    }
}
