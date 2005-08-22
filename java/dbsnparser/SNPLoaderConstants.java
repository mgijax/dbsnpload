package dbsnparser;

public class SNPLoaderConstants {
    /**
* An object that contains constant definitions for SNP loaders.
* @has
*   <UL>
*   <LI> Constant definitions for:
*   <UL>
*       <LI> characters, e.g. tab
*       <LI> SNP objects type
*   </UL>
*   </UL>
* @does Nothing
* @company The Jackson Laboratory
* @author sc
* @version 1.0
*/


        // Object type values
        public static final String OBJECTYPE_CSNP = "Consensus SNP";
        public static final String OBJECTYPE_SSNP = "Sub SNP";
        //public static final String OBJECTYPE_SMARKER = "MGI_SNP_Marker";

        // LogicalDB values
        public static final String LDB_CSNP = "RefSNP";
        public static final String LDB_SSNP = "SubSNP";
        public static final String LDB_SUBMITTER = "SubmitterSNP";
        public static final String LDB_REFSEQ = "RefSeq";

        // Assemblies as found in dbSNP
        public static final String DBSNP_BL6 = "C57BL/6J";

        // dbSNP fxn classes
        public static final String REFERENCE = "reference";

        // orientation values from genotype file
        public static final String GENO_FORWARD_ORIENT = "fwd";
        public static final String GENO_REVERSE_ORIENT = "rev";
        // orientation values from NSE file
        public static final String NSE_FORWARD = "forward";
        public static final String NSE_SS_REVERSE = "reversed";
        public static final String NSE_RS_REVERSE = "reverse";
    }
