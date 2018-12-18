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
 * is a Representation of RefSnp strain alleles from the DBSNP Genotype input file
 * @has a pointer to the input file
 * @does provides an iterator to iterate over dbSNP genotype RefSnp records
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
     * constructor which takes the name of the input file and a dbsnp
     * strain id to strain mapping as arguments
     * @param filename the name of the input file
     * @param map mapping of dbsnp strain id to strain name
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
    	inFile = new InputDataFile(filename);
    	interpreter = new MGPGenotypeInterpreter();
    	RecordDataIterator it = inFile.getIterator(interpreter);
    	DBSNPGenotypeRefSNPInput genoInput = null;
    	HashMap inputMap = new HashMap();
    	int genoRSId;
	
    	while (it.hasNext()) {
	    genoInput = (DBSNPGenotypeRefSNPInput)it.next();
		    if (genoInput != null) { // null if header line was processed 
		    	//following for writing parser output for testing
			    /*String line = genoInput.getRsId() + SNPLoaderConstants.TAB;
				
				HashMap popMap = (HashMap)genoInput.getSSPopulationsForRs();
				Collection v = popMap.values();
				
				for (Iterator k = v.iterator(); k.hasNext(); ) {
		            DBSNPGenotypePopulation[] popArray = (DBSNPGenotypePopulation[]) k.next();
		            
		            for (int l = 0; l < popArray.length; l++ ) {
		                DBSNPGenotypePopulation pop = popArray[l];
		                HashMap<String, Allele> strainalleles = (HashMap)pop.getStrainAlleles();
		                
		                for (String strainKey : strainalleles.keySet()) {
		                	    
		                        Allele a = strainalleles.get(strainKey);
		                        line = line +  strainKey + ':' + a.getAllele() + SNPLoaderConstants.TAB;
		                }
		
		            }
		           
				}
				//System.out.println(line);
				*/
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
    	String[] fields = header.split(SNPLoaderConstants.TAB);
        String[] strains = Arrays.copyOfRange(fields, 2, fields.length); 
        for (int i = 0; i < strains.length; i++) {
        	strainMap.put(i, strains[i].trim());
		//System.out.println("loadStrainMap: " +strains[i]);
        } 
    }
    /**
     * The RecordDataInterpreter for interpreting a tab-delimited file
     * @has nothing
     * @does implements the RecordDataInterpreter interface to interpret RefSnp info in
     * a  genotype input file creating DBSNPGenotypeRefSnpInput objects
     * @company The Jackson Laboratory
     * @author sc
     */

    public class MGPGenotypeInterpreter implements RecordDataInterpreter

    {
     
    	private int ssCtr = 1;
    	private int isHeader = 1;
    	
        public Object interpret(String rec) throws InterpretException {
        	//System.out.println("Interpreter rec: " + rec);
            // the current input object
            DBSNPGenotypeRefSNPInput currentInput = null;

            // current rsid
            int currentRSId;

            // current ssid for this rs
            String currentSSId = null;
            
            // orientation of the current ss to the RS flanking sequence
            String currentSSOrientToRS = null;

            // The current strain
            String currentStrain =  null;

            // currentStrainId converted to strain name 
            String currentConvertedStrainId = null;

            // allele for currentStrainId
            Allele currentAllele = null;

            // set of DBSNPGenotypePopulation objects for the current SS
            DBSNPGenotypePopulation[] currentSSPopulationArray = null;

            // current index of population array
            int currentPopArrIndex = 0;

            // current population of the current SS
            DBSNPGenotypePopulation currentPopulation = null;

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
	            
	            
	            currentRSId = new Integer(fields[1]).intValue();
	            System.out.println("rsID as integer: " + currentRSId);
	            String[]strains = Arrays.copyOfRange(fields, 2, fields.length);
	            
	            // create the input object for the record and set rsId
	            currentInput = new DBSNPGenotypeRefSNPInput();
	            currentInput.setRsId(currentRSId);
	            currentSSPopulationArray = new DBSNPGenotypePopulation[1];
	            // all MGS snps are in fwd orientation - we won't be using this, we'll be 
	            // using orient from XML file
	            currentSSOrientToRS = "fwd";
	                  
	            currentPopulation = new DBSNPGenotypePopulation();
	            currentPopulation.setPopId("");
	            // iterate through the strains creating Strain Alleles and 
	            // setting their orientation
	            
	            for (int i = 0; i < strains.length; i++) {
	            	String allele = strains[i].trim();
	            	if (allele == "-") {
	            		continue;
	            	}
	            	String strain = ((String)strainMap.get(i)).trim();
	            	//System.out.println("Interpreter strain " + strain + " allele " + allele);
	            	currentAllele = new Allele(allele,
	                        currentSSOrientToRS);
	            	currentPopulation.addStrainAlleles(strain, currentAllele);
	            			
	            }
	            currentSSPopulationArray[currentPopArrIndex] =  currentPopulation;
	            currentPopArrIndex++;
	            
	            // here we are using a ctr as we have no SS IDs, for MGP
	            // we will have only one SS/RS and the SS ID must come from
	            // the XML file (join on RS ID)
	            currentInput.addPopulation( Integer.toString(ssCtr),
	                    currentSSPopulationArray); 

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
