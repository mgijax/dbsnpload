package org.jax.mgi.app.dbsnploader;

import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;

import org.jax.mgi.shr.ioutils.InputXMLDataFile;
import org.jax.mgi.shr.ioutils.XMLDataIterator;
import org.jax.mgi.shr.ioutils.XMLDataInterpreter;
import org.jax.mgi.shr.ioutils.XMLTagIterator;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.ioutils.InterpretException;
import org.jax.mgi.shr.config.ConfigException;
import org.jax.mgi.shr.stringutil.StringLib;
/**
 * A Representation of the DBSNP Genotype file
 * @has a pointer to the input file
 * @does provides an itertator to iterate over dbSNP genotype records
 * in the input file
 * @company The Jackson Laboratory
 * @author sc
 */


public class DBSNPGenotypeRefSNPInputFile extends InputXMLDataFile {
    private String TAG =  "SnpInfo";
    private String filename = null;
    HashMap strainMap;

    /**
     * constructor which takes the name of the input file as an argument
     * @param filename the name of the input file
     * @throws ConfigException thrown if there is an error accessing
     * the configuration
     * @throws IOUException thrown if there is an error accessing the
     * file system
     */
    public DBSNPGenotypeRefSNPInputFile(String filename, HashMap map) throws ConfigException, IOUException
    {
        super(filename);
        this.filename = filename;
        this.strainMap = map;
    }

    /**
     * get the iterator for this file
     * @return an XMLDataIterator instance which provideds iteration over
     * dbSNP genotype records in the file
     */
    public XMLDataIterator getIterator()
    {
        return super.getIterator(TAG, new DBSNPGenotypeInterpreter());
    }

    /**
     * The XMLDataInterpreter for interpreting records of DBSNP genotype file
     * @has nothing
     * @does implements the XMLDataInterpreter interface to interpret genotype
     * xml input file creating DBSNPGenotypeInput objects
     * @company The Jackson Laboratory
     * @author sc
     */

    public class DBSNPGenotypeInterpreter
        implements XMLDataInterpreter
    {
        /**
         * interprets the xml input as a DBSNPGenoty[eInput instance
         * @param it the XMLTagIterator from which to obtain the xml data used
         * to create the DBSNPGenotypeInput instance
         * @return the newly created DBSNPGenotypeInput object
         * @throws InterpretException thrown if there is an error during
         * interpreteration
         */
        public Object interpret(XMLTagIterator it) throws InterpretException {

            // the current rs object
            DBSNPGenotypeRefSNPInput currentInput = null;

            // current ssid for this rs
            String currentSSId = null;

            // current orientation of the ss to the RS flanking sequence
            String currentSSOrientToRS = null;

            // The current strain id
            String currentStrainId = null;

            // currentStrainId converted to dbSNP id
            String currentConvertedStrainId = null;

            // allele for currentStrainId
            Allele currentAllele = null;

            // Vector of DBSNPGenotypePopulation objects for the current SS
            Vector currentSSPopulationVector = new Vector();
            DBSNPGenotypePopulation currentPopulation = null;

            try {

                while (it.getState() != it.TAG_END) {

                    String[] atts = it.getAttributeNames();
                    int attsCt = it.getAttributeCount();
                    /**
                     * remaining parsing is RS/SS
                     *
                     * example XML (ther can be multi 'ByPop' nested within
                     * each 'SsInfo' tag
                     * <SnpInfo rsId="2020458">
                             <SnpLoc genomicAssembly="0:C57BL/6J" chrom="1"
                         start="172997799" locType="2" rsOrientToChrom="fwd"/>
                             <SsInfo ssId="1565680" locSnpId="ERO23-241e3r_1"
                                   ssOrientToRs="fwd">
                                 <ByPop popId="542" hwProb="0.0025" hwChi2="10"
                                        hwDf="1" sampleSize="20">
                                     <AlleleFreq allele="T" freq="0.1"/>
                                     <AlleleFreq allele="C" freq="0.9"/>
                                     <GTypeFreq gtype="C/C" freq="0.9"/>
                                     <GTypeFreq gtype="T/T" freq="0.1"/>
                                     <GTypeByInd gtype="C/C" indId="2921"/>
                                     <GTypeByInd gtype="C/C" indId="2922"/>
                                     <GTypeByInd gtype="C/C" indId="2917"/>
                                     <GTypeByInd gtype="C/C" indId="2926"/>
                                     <GTypeByInd gtype="C/C" indId="4460"/>
                                     <GTypeByInd gtype="C/C" indId="4461"/>
                                     <GTypeByInd gtype="C/C" indId="2918"/>
                                     <GTypeByInd gtype="C/C" indId="2920"/>
                                     <GTypeByInd gtype="T/T" indId="4465"/>
                                     <GTypeByInd gtype="C/C" indId="4466"/>
                                 </ByPop>
                             </SsInfo>
                             <GTypeFreq gtype="C/C" freq="0.9"/>
                             <GTypeFreq gtype="T/T" freq="0.1"/>
                     * </SnpInfo>
                     */

                    // first tag of the RS record
                    if (it.getTagName().equals("SnpInfo")) {
                        // create the input object for the record and set rsId
                        currentInput = new DBSNPGenotypeRefSNPInput();
                        for (int i = 0; i < attsCt; i++) {
                            if (atts[i] != null && atts[i].equals("rsId")) {
                                currentInput.setRsId(it.getAttributeValue(i));
                            }
                        }
                    }
                    // Beginning of an SS
                    else if (it.getTagName().equals("SsInfo")) {
                        // SS can have multiple populations, create a pop Vector
                        currentSSPopulationVector = new Vector();
                        for (int i = 0; i < attsCt; i++) {
                            // get the ssId
                            if (atts[i] != null && atts[i].equals("ssId")) {
                                currentSSId = it.getAttributeValue(i);
                            }
                            // get the SS orienation to the RS
                            else if (atts[i] != null &&
                                     atts[i].equals("ssOrientToRs")) {
                                currentSSOrientToRS = it.getAttributeValue(i);
                            }
                        }
                    }
                    // Beginning of a Population
                    else if (it.getTagName().equals("ByPop")) {
                        // add the population vector to the input object; if there
                        // are multi populations, this will be done again; this
                        // should be done when we detect the 'SsInfo' end tag;
                        // can't do it at the 'SsInfo' start tag because we haven't
                        // got the ssId yet
                        currentInput.addPopulation(currentSSId,
                            currentSSPopulationVector);
                        for (int i = 0; i < attsCt; i++) {
                            if (atts[i] != null && atts[i].equals("popId")) {
                                currentPopulation = new DBSNPGenotypePopulation();
                                currentSSPopulationVector.add(currentPopulation);
                                currentPopulation.setPopId(it.getAttributeValue(
                                    i));
                            }
                        }
                    }
                    // beginning of a strain allele
                    else if (it.getTagName().equals("GTypeByInd")) {
                        for (int i = 0; i < attsCt; i++) {
                            // create an Allele ("A/A" becomes "A") and set its orientation
                            if (atts[i] != null && atts[i].equals("gtype")) {
                                String allele = it.getAttributeValue(i);
                                ArrayList a = StringLib.split(allele, "/");
                                allele = (String) a.get(0);
                                currentAllele = new Allele(allele,
                                    currentSSOrientToRS);
                            }
                            // get the strainId, map it to its strain name
                            // and add it to the population object
                            else if (atts[i] != null && atts[i].equals("indId")) {
                                currentStrainId = it.getAttributeValue(i);
                                if (strainMap.containsKey(currentStrainId)) {
                                    // set converted strain and its allele into the strain allele map
                                    currentConvertedStrainId = (String)
                                        strainMap.get(currentStrainId);
                                    currentPopulation.addStrainAlleles(
                                        currentConvertedStrainId, currentAllele);

                                }
                            }
                        }
                    }
                    it.nextTag();
                }
            }
            catch (IOUException e) {
                throw new InterpretException("Cannot read data from xml", e);
            }
            return currentInput;
        }
    }
}