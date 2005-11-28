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


public class DBSNPGenotypeIndividualInputFile extends InputXMLDataFile {
    private String TAG =  "Individual";
    private String filename = null;

    /**
     * constructor which takes the name of the input file as an argument
     * @param filename the name of the input file
     * @throws ConfigException thrown if there is an error accessing
     * the configuration
     * @throws IOUException thrown if there is an error accessing the
     * file system
     */
    public DBSNPGenotypeIndividualInputFile(String filename) throws ConfigException, IOUException
    {
        super(filename);
        this.filename = filename;
    }

    /**
     * get the iterator for this file
     * @return an XMLDataIterator instance which provideds iteration over
     * dbSNP genotype records in the file
     */
    public XMLDataIterator getIterator()
    {
        return super.getIterator(TAG, new DBSNPGenotypeIndividualInterpreter());
    }

    /**
     * The XMLDataInterpreter for interpreting records of DBSNP genotype file
     * @has nothing
     * @does implements the XMLDataInterpreter interface to interpret genotype
     * xml input file creating DBSNPGenotypeInput objects
     * @company The Jackson Laboratory
     * @author sc
     */

    public class DBSNPGenotypeIndividualInterpreter
        implements XMLDataInterpreter
    {
        /**
         * interprets the xml input as a DBSNPGenoty[eInput instance
         * @param it the XMLTagIterator from which to obtain the xml data used
         * to create the DBSNPGenotypeInput instance
         * @return the newly created DBSNPGenotypeInput object
         * @throws InterpretException thrown if there is an error during
         * interpreteration
         *
         * 'Individual' and SourceInfo tags are found at the top of
         * each genotype file. Load a mapping of the dbSNP strain id
         * to the strain. Note that the strain could be either a strain
         * name or a JAX registry id
         * example XML for submitter strain
         * <Individual indId="5384" taxId="10090" sex="?">
              <SourceInfo source="1445" sourceType="submitter"
                   ncbiPedId="4580" pedId="4580" indId="PWD/PHJ"
                   maId="0" paId="0"/>
             <SubmitInfo popId="1445" submittedIndId="PWD/PHJ"/>
           </Individual>
         * example XML for JAX registry (repository) strain
         * <Individual indId="2914" taxId="10090" sex="?">
               <SourceInfo source="The Jackson Laboratory"
                     sourceType="repository" ncbiPedId="1942"
                     pedId="1942" indId="000461" maId="0" paId="0"/>
               <SubmitInfo popId="1064"
                     submittedIndId="B10.D2-H2/OSNJ"
                     subIndGroup="Mouse"/>
               <SubmitInfo popId="1219"
                     submittedIndId="B10.D2-HC0 H2D H2-T18C/OSNJ"
                     subIndGroup="Mouse"/>
          * </Individual>
          */

        public Object interpret(XMLTagIterator it) throws InterpretException {
            // current strain id and strain from the Individual section
            DBSNPGenotypeIndividualInput currentInput = null;

            try {
                while (it.getState() != it.TAG_END) {
                    String[] atts = it.getAttributeNames();
                    int attsCt = it.getAttributeCount();
                    //System.out.println(it.getTagName());
                    if (it.getTagName().equals("Individual")) {
                        currentInput = new DBSNPGenotypeIndividualInput();
                        for (int i = 0; i < attsCt; i++) {
                            // get the dbsSnp strainId
                            if (atts[i] != null && atts[i].equals("indId")) {
                                currentInput.setStrainId(it.getAttributeValue(i));
                            }
                        }
                    }
                    else if (it.getTagName().equals("SourceInfo")) {
                        for (int i = 0; i < attsCt; i++) {
                            // get the strain which maps to the strainId
                            if (atts[i] != null && atts[i].equals("indId")) {
                                currentInput.setStrain(it.getAttributeValue(i));
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
