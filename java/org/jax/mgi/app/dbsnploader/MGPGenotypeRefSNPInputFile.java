package org.jax.mgi.app.dbsnploader;

import java.util.Arrays;
import java.util.HashMap;

import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.ioutils.InputDataFile;
import org.jax.mgi.shr.ioutils.InterpretException;
import org.jax.mgi.shr.ioutils.RecordDataInterpreter;
import org.jax.mgi.shr.ioutils.RecordDataIterator;

/**
 * is a Representation of RefSnp strain alleles from the MGP Genotype input file
 * @has a pointer to the input file
 * @does provides an iterator to iterate over MGP genotype RefSnp records
 * in the input file
 * @company The Jackson Laboratory
 * @author sc
 */
public class MGPGenotypeRefSNPInputFile  {
    private String filename = null;
    private MGPGenotypeInterpreter interpreter = null;
    
    // An input data file object for the input file 
    //
    private InputDataFile inFile = null; 
    private HashMap strainMap = new HashMap();
    
    /**
     * constructor which takes the name of the input file 
     * @param filename the name of the input file
     * @param map mapping of strain id to strain name
     * @throws ConfigException thrown if there is an error accessing
     * the configuration
     * @throws IOUException thrown if there is an error accessing the
     * file system
     */
    public MGPGenotypeRefSNPInputFile(String filename) throws ConfigException, IOUException
    {

        this.filename = filename;
    }

    public HashMap getInputMap () throws MGIException 
    {
        System.out.println("MGPGenotypeRefSNPInputFile: filename is" + filename);
    	inFile = new InputDataFile(filename);
    	interpreter = new MGPGenotypeInterpreter();
    	RecordDataIterator it = inFile.getIterator(interpreter);
    	MGPGenotypeRefSNPInput genoInput = null;
    	HashMap inputMap = new HashMap();
    	String genoRSId;
	
    	while (it.hasNext()) {
	    genoInput = (MGPGenotypeRefSNPInput)it.next();
		    if (genoInput != null) { // null if header line was processed 
		    	
				genoRSId = genoInput.getRsId(); //.substring(2);
				//System.out.println("getInputMap rsID: " + genoRSId);
				inputMap.put(genoRSId, genoInput);
		    }
	  }
      return inputMap;
    }


    public void loadStrainMap(String header) {
    	
        String s = header.replaceFirst(SNPLoaderConstants.CRT,"");
        //System.out.println("Header: " + s);
    	String[] fields = s.split(SNPLoaderConstants.TAB);
        //String[] strains = Arrays.copyOfRange(fields, 2, fields.length); 
        for (int i = 2; i < fields.length; i++) {
        	strainMap.put(i, fields[i].trim());
		//System.out.println("loadStrainMap: " +strains[i]);
        } 
    }
    /**
     * The RecordDataInterpreter for interpreting a tab-delimited file
     * @has nothing
     * @does implements the RecordDataInterpreter interface to interpret RefSnp info in
     * a  genotype input file creating MGPGenotypeRefSnpInput objects
     * @company The Jackson Laboratory
     * @author sc
     */

    public class MGPGenotypeInterpreter implements RecordDataInterpreter

    {
     
    	private int isHeader = 1;
    	
        public Object interpret(String rec) throws InterpretException {
        	//System.out.println("Interpreter rec: " + rec);
            // the current input object
            MGPGenotypeRefSNPInput currentInput = null;

            // current rsid
            String currentRSId;
           
            // allele for current strain
            Allele currentAllele = null;

 
            if (isHeader == 1) {
            	//System.out.println("is header record");
            	loadStrainMap(rec);
            	isHeader = 0;
            }
            else {
            	//System.out.println("is data record");
	            String s = rec.replaceFirst(SNPLoaderConstants.CRT,"");
	            String[] fields = s.split(SNPLoaderConstants.TAB);
	            
	            /*
	            String r = fields[1];
	            System.out.println("rsID from file: " + r);
	            // index of ';' semi-colon in rsID
	            int sci = r.indexOf(";");
	            if (sci != -1)  {
	            	r = r.substring(0, sci);
	            	System.out.println("first rsID taken: " + r);
	            }*/
	            
	            
	            currentRSId = fields[1];
	            //System.out.println("Interpret rsID: " + currentRSId);
	            
	            // all MGS snps are in fwd orientation - we won't be using this, we'll be 
	            // using orient from XML file
	                 

	            // iterate through the strains creating Strain Alleles and 
	            // setting their orientation
	            StringBuffer alleleBuffer = new StringBuffer();
	          
	            for (int i = 2; i < fields.length; i++) {
	            	if (fields[i].trim() == "-") {
	            		continue;
	            	}
	            	 
	            	alleleBuffer.append(  ((String)strainMap.get(i)).trim()  ); // strain
	            	alleleBuffer.append("|"); //strain|allele delimiter
	            	alleleBuffer.append(fields[i].trim()); // allele
	            	alleleBuffer.append(";"); //strainAllele;strainAllele delimiter
	            }
	            alleleBuffer.deleteCharAt(alleleBuffer.length()-1); // remove trailing ';'
	            
	            currentInput = new MGPGenotypeRefSNPInput(currentRSId, alleleBuffer.toString());
          }  
    
            return currentInput;
        }
        /**
         * Determines if the given input record is a valid record. A comment
         * line is considered to be invalid.
         * @assumes Nothing
         * @effects Nothing
         * @param rec A record from the input file
         * @return Indicator of whether the input record is valid (true) or a
         *         comment line (false)
         * @throws Nothing
         */
        public boolean isValid (String rec)
        {
            // If the first character of the input record is a "#", it is a
            // comment and should be ignored.
            //
            if (rec.contains(";"))
                return false;
            else
                return true; 
        }
    }
}
