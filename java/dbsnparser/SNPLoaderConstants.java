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
        public static final String OBJECTYPE_CSNP = "MGI_SNP_ConsensusSNP";
        public static final String OBJECTYPE_SSNP = "MGI_SNP_SubSNP";

        // LogicalDB values
        public static final String LDB_CSNP = "ConsensusSNP";
        public static final String LDB_SSNP = "SubSNP";
        public static final String LDB_SUBMITTER = "Submitter";

        // Assemblies as found in dbSNP
        public static final String DBSNP_BL6 = "C57BL/6J";

        // dbSNP fxn classes
        public static final String REFERENCE = "reference";

        // orientation values
        public static final String FORWARD_ORIENT = "fwd";
        public static final String REVERSE_ORIENT = "rev";
    }
