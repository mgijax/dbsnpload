package org.jax.mgi.app.dbsnploader;

public class SNPLoaderConstants {
/**
* An object that contains constant definitions for SNP loaders.
* @has
*   <UL>
*   <LI> Constant definitions for:
*   <UL>
     *  <LI> SNP objects type
     *  <LI>accession id prefixex
     *  <LI>logicalDB values
     *  <LI>assemblies
     *  <LI>orientation
     *  <LI> function class
     *  <LI> variation class
*   </UL>
*   </UL>
* @does Nothing
* @company The Jackson Laboratory
* @author sc
*/

        // accession id prefixes
        public static final String PREFIX_CSNP = "rs";
        public static final String PREFIX_SSNP = "ss";

        // Assemblies as found in dbSNP
        public static final String MGI_MOUSE = "mouse, laboratory";
        public static final String DBSNP_BL6 = "C57BL/6J";

        // orientation values from genotype file
        public static final String GENO_FORWARD_ORIENT = "fwd";
        public static final String GENO_REVERSE_ORIENT = "rev";

        // orientation values from NSE file
        public static final String NSE_FORWARD = "forward";
        public static final String NSE_REVERSE = "reverse";

        // variation class constants
        public static final String VARCLASS_NAMED = "named-locus";
        public static final String VARCLASS_INDEL = "in-del";
        public static final String VARCLASS_SNP = "snp";
        public static final String VARCLASS_MNP = "multinucleotide-polymorphism";
        public static final String VARCLASS_MIXED = "mixed";
    }
