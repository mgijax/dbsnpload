package org.jax.mgi.app.dbsnploader;

import org.jax.mgi.shr.ioutils.InputXMLDataFile;
import org.jax.mgi.shr.ioutils.XMLDataIterator;
import org.jax.mgi.shr.ioutils.XMLDataInterpreter;
import org.jax.mgi.shr.ioutils.XMLTagIterator;
import org.jax.mgi.shr.ioutils.IOUException;
import org.jax.mgi.shr.ioutils.InterpretException;
import org.jax.mgi.shr.config.ConfigException;

/**
 * A Representation of RefSnp data from a dbsnp NSE file
 * @has a pointer to the input file
 * @does provides an iterator to iterate over dbSNP records
 * in an input file
 * @company The Jackson Laboratory
 * @author sc
 */


public class DBSNPNseInputFile extends InputXMLDataFile
{

    private String TAG =  "Rs";
    private String filename = null;

    /**
     * constructor which takes the name of the input file as an argument
     * @param filename the name of the input file
     * @throws ConfigException thrown if there is an error accessing
     * the configuration
     * @throws IOUException thrown if there is an error accessing the
     * file system
     */
    public DBSNPNseInputFile(String filename) throws ConfigException, IOUException
    {
        super(filename);
        this.filename = filename;
     }

    /**
     * get the iterator for this file which will iterate over
     * dbSNP records
     * @return an XMLDataIterator instance which provideds iteration over
     * dbSNP records in the input file
     */
    public XMLDataIterator getIterator()
    {
        return super.getIterator(TAG, new DBSNPNSEInterpreter());
    }

    /**
     * The XMLDataInterpreter for interpreting records of DBSNP genotype file
     * @has nothing
     * @does implements the XMLDataInterpreter interface to interpret input
     * xml data DBSNPGenotypeInput objectws
     * @company The Jackson Laboratory
     * @author sc
     */

    public class DBSNPNSEInterpreter
        implements XMLDataInterpreter
    {
        /**
         * interprets the xml input as a DBSNPGenoInput instance
         * @param it the XMLTagIterator from which to obtain the xml data used
         * to create a DBSNPNseInput object
         * @return the newly created DBSNPNseInput object
         * @throws InterpretException thrown if there is an error during
         * interpreteration
         */
        public Object interpret(XMLTagIterator it)
        throws InterpretException {
           // The compound object we are creating which represents a complete RefSNP
            DBSNPNseInput currentNseInput = new DBSNPNseInput();
            // the current RS being build
            DBSNPNseRS currentRS = null;
            // the ss id of the ss exemplar (the flanking sequence is from this ss)
            String currentExemplar = null;
            // The current subSNP being built
            DBSNPNseSS currentSS = null;
            // the current assembly
            String currentAssembly = null;
            // The current contig hit being built
            DBSNPNseContigHit currentContigHit = null;
            // The current map location for this contigHit
            DBSNPNseMapLoc currentMapLoc = null;
            // The current function set for this map location
            DBSNPNseFxnSet currentFxnSet;
            // true if we are looking for the RS flanking sequence (we don't want
            // ss flanking sequence)
            boolean getRSFlank = false;
            // true if we are looking for Component MapLoc
            // (we don't want PrimarySequence)
            boolean getCompLoc = false;
            try {
                 while (it.getState() != it.TAG_END) {
                     String[] atts = it.getAttributeNames();
                     int attsCt = it.getAttributeCount();
                     if(it.getTagName().equals("Rs")) {
                         // create an RS object
                         currentRS = new DBSNPNseRS();
                         // add the SS object reference to the current Input object
                         currentNseInput.setRS(currentRS);
                         for (int i = 0; i < attsCt; i++) {
                             if ( atts[i] != null && atts[i].equals("rsId" )) {
                                 currentRS.setRsId(it.getAttributeValue(i));
                             }
                             else if (atts[i] != null && atts[i].equals("snpClass" )) {
                                 currentRS.setRsVarClass(it.getAttributeValue(i));
                             }
                         }
                     }
                     else if(it.getTagName().equals("Create")) {
                         for (int i = 0; i < attsCt; i++) {
                             if (atts[i] != null && atts[i].equals("build" )) {
                                 currentRS.setBuildCreated(it.getAttributeValue(i));
                                 //System.out.println(it.getAttributeValue(i));
                             }
                         }
                     }
                     else if(it.getTagName().equals("Update")) {
                         for (int i = 0; i < attsCt; i++) {
                             if (atts[i] != null && atts[i].equals("build" )) {
                                 currentRS.setBuildUpdated(it.getAttributeValue(i));
                                 // add build this rs updated in dbSNP to DBSNPNseInput
                                 //System.out.println(it.getAttributeValue(i));
                             }
                         }
                     }
                     else if(it.getTagName().equals("Sequence")) {
                         for (int i = 0; i < attsCt; i++) {
                             if (atts[i] != null && atts[i].equals("exemplarSs" )) {
                                 // save the exemplar ssId when we have the ss
                                 // object we'll set its exemplar flag
                                 currentExemplar = it.getAttributeValue(i);
                                 // now that we have the exemplar we are looking for
                                 // the exemplar (aka RS) flanking sequence
                                 getRSFlank = true;
                             }
                         }
                         if (currentExemplar == null) {
                             //throw an exception
                             System.out.println("currentExemplar is null");
                         }
                     }
                     else if (it.getTagName().equals("Seq5") && getRSFlank == true) {
                         currentNseInput.add5PrimeFlank(new DBSNPNseFlank(
                            it.getText(), currentNseInput.getNext5PrimeSeqNum()));
                     }
                     else if (it.getTagName().equals("Seq3") && getRSFlank == true) {
                         currentNseInput.add3PrimeFlank(new DBSNPNseFlank(
                            it.getText(), currentNseInput.getNext3PrimeSeqNum()));
                     }
                     else if(it.getTagName().equals("Ss")) {
                         // create an SS object
                         currentSS = new DBSNPNseSS();
                         // add the SS object reference to the current Input object
                         currentNseInput.addSS(currentSS);
                         // once we've gotten here we have the rs flanking sequence
                         getRSFlank = false;
                         for (int i = 0; i < attsCt; i++) {
                             if (atts[i] != null && atts[i].equals("ssId" )) {
                                 String ssId = it.getAttributeValue(i);
                                 if (ssId.equals(currentExemplar)) {
                                     currentSS.setIsExemplar(Boolean.TRUE);
                                 }
                                 currentSS.setSSId(ssId);
                             }
                             else if(atts[i] != null && atts[i].equals("handle" )) {
                                 currentSS.setSubmitterHandle(it.getAttributeValue(i));
                             }
                             else if (atts[i] != null && atts[i].equals("locSnpId")) {
                                 currentSS.setSubmitterSNPId(it.getAttributeValue(i));
                             }
                             else if (atts[i] != null && atts[i].equals("subSnpClass")) {
                                 currentSS.setSSVarClass(it.getAttributeValue(i));
                             }
                             else if (atts[i] != null && atts[i].equals("orient")) {
                                 currentSS.setSSOrientToRS(it.getAttributeValue(i));
                             }

                         }
                     }
                     /// note here we are getting the ss observed - we don't want the rs
                     // observed, thus getRSFlank should be false
                     else if (it.getTagName().equals("Observed") && getRSFlank == false) {
                         currentSS.setObservedAlleles(it.getText());
                     }

                     else if (it.getTagName().equals("Assembly")) {
                         for (int i = 0; i < attsCt; i++) {
                             if (atts[i] != null && atts[i].equals("groupLabel")) {
                                 currentAssembly = it.getAttributeValue(i);
                             }
                         }
                         if (currentAssembly == null) {
                             //throw an exception
                             System.out.println("currentAssembly is null");
                         }

                     }
                     else if (it.getTagName().equals("Component")) {
                         // create ContigHit object reference
                         currentContigHit = new DBSNPNseContigHit();
                         // add the ContigHit object to the current Input object
                         currentNseInput.addContigHit(currentContigHit);
                         currentContigHit.setAssembly(currentAssembly);
                         for (int i = 0; i < attsCt; i++) {
                             if (atts[i] != null && atts[i].equals("chromosome")) {
                                 currentContigHit.setChromosome(it.getAttributeValue(i));
                             }

                         }
                         // we are looking for the '<Component' MapLoc (Not the
                         // '<PrimarySequence' MapLoc)
                         getCompLoc = true;
                     }

                     else if (it.getTagName().equals("MapLoc") && getCompLoc == true) {
                         // create a MapLoc object
                         currentMapLoc = new DBSNPNseMapLoc();
                         // add the MapLoc object reference to the current Input object
                         currentContigHit.addMapLocation(currentMapLoc);
                         for (int i = 0; i < attsCt; i++) {
                             if(atts[i] != null && atts[i].equals( "orient")) {
                                 currentMapLoc.setRSOrientToChr(it.getAttributeValue(i));
                             }
                             else if (atts[i] != null && atts[i].equals("physMapInt")) {
                                 currentMapLoc.setStartCoord(new Double(it.getAttributeValue(i)));
                              }
                         }
                     }
                     else if (it.getTagName().equals("FxnSet")) {
                            // create a FxnSet object
                            currentFxnSet = new DBSNPNseFxnSet();
                            // add the FxnSet object reference to the current Input object
                            currentMapLoc.addFxn(currentFxnSet);

                            for (int i = 0; i < attsCt; i++) {
                                if (atts[i] != null && atts[i].equals("geneId")) {
                                    currentFxnSet.setLocusId(it.getAttributeValue(i));
                                }
                                else if(atts[i] != null && atts[i].equals("fxnClass")) {
                                    currentFxnSet.setFxnClass(it.getAttributeValue(i));
                                }
                                else if (atts[i] != null && atts[i].equals("readingFrame")) {
                                    currentFxnSet.setReadingFrame(it.getAttributeValue(i));
                                }
                                else if (atts[i] != null && atts[i].equals("allele")) {
                                    currentFxnSet.setContigAllele(it.getAttributeValue(i));
                                }
                                else if (atts[i] != null && atts[i].equals("residue")) {
                                    currentFxnSet.setAAResidue(it.getAttributeValue(i));
                                }
                                else if (atts[i] != null && atts[i].equals("aaPosition")) {
                                    currentFxnSet.setAAPosition(it.getAttributeValue(i));
                                }
                                else if (atts[i] != null && atts[i].equals("mrnaAcc")) {
                                    currentFxnSet.setNucleotideId(it.getAttributeValue(i));
                                }
                                else if (atts[i] != null && atts[i].equals("protAcc")) {
                                    currentFxnSet.setProteinId(it.getAttributeValue(i));
                                }
                            }
                    }
                    // when we get to this tag we  are done with Component tags
                    // and no longer need to get Component map locations (Note
                    // that MapLoc is a nested tag under PrimarySequence too)
                    else if (it.getTagName().equals("PrimarySequence")) {
                        getCompLoc = false;
                    }
                     it.nextTag();
                 }
            }

            catch (IOUException e)
            {
                throw new InterpretException("Cannot read data from xml", e);
            }
            return currentNseInput;
        }
    }
}
