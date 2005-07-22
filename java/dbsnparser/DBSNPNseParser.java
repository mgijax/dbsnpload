package dbsnparser;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.jax.mgi.shr.stringutil.StringLib;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggerFactory;
import org.jax.mgi.shr.dla.log.DLALoggingException;

public class DBSNPNseParser implements DBSNPParser {
    // DEBUG/analysis
    private DLALogger logger;

    public DBSNPNseParser() throws DLALoggingException {
        logger = DLALogger.getInstance();
    }
    /**
     * This parses the file, using registered SAX handlers, and outputs
     * the events in the parsing process cycle.
     * @param uri the xml file to parse
     */
    public Vector parse(String uri) throws IOException {
        System.out.println("Parsing XML Files in: " + uri + "\n\n");

        // The SAX parser handler
        NSEContentHandler genContentHandler = new NSEContentHandler();

        try {
            // Instantiate an rs parser for the RS file
            XMLReader rsParser = XMLReaderFactory.createXMLReader(
                "org.apache.xerces.parsers.SAXParser");
            // register the ContentHander
            rsParser.setContentHandler(genContentHandler);
            rsParser.parse(uri);
        }
        catch (IOException e) {
            System.out.println("Error reading URI: " + e.getMessage());
        }
        catch (SAXException e) {
            System.out.println("Error in parsing: " + e.getMessage());
        }

        return genContentHandler.getNSEInputVector();

    }

    /**
     * defines callback behavior for the SAX callbacks
     * associated with an XML document's content.
     */
    class NSEContentHandler extends DefaultHandler {
        private Locator locator;
        Vector NSEInputVector = new Vector();
        StringBuffer charString;
        // The compound object we are creating which represents a complete RefSNP
        DBSNPNseInput currentNseInput;
        // the ss id of the ss exemplar (the flanking sequence is from this ss)
        String currentExemplar;
        // The current subSNP being built
        DBSNPNseSS currentSS;
        // The current contig hit being built
        DBSNPNseContigHit currentContigHit;
        // The current map location for this contigHit
        DBSNPNseMapLoc currentMapLoc;
        // The current function set for this map location
        DBSNPNseFxnSet currentFxnSet;

        public NSEContentHandler() {
            charString = new StringBuffer();
        }

        public Vector getNSEInputVector() {
            return NSEInputVector;
        }

        /**
             * Provide reference to "Locator" which provides information about where
         * in a dcument callbacks occur
         */
        public void setDocumentLocator(Locator locator) {
            //System.out.println("* setDocumentLocator() called");
            this.locator = locator;
        }

        /**
             * This indicates the start ofa Document parse-this precedes all callbacks
             * in all SAX Handlers with the sole exception of "{@link #setDocumentLocator}
         */
        public void startDocument() {
            System.out.println("Parsing begins...");
        }

        /**
         * This indicates the end of a Document parse - this occurs after all
         * callbacks in all SAX Handlers
         * @throws SAXEception when things go wrong
         */
        public void endDocument() throws SAXException {
            System.out.println("Parsing ends...");
        }


        /**
         * This reports the occurrence of an actual element. It includes the
         * element's attributes, with the exception of XML vocabulary specific
             * attributes, suc as "xmlns:[namespace prefix]" and "xsi:schemaLocation
         * @param namespaceURI String namespace URI this element is associated with, or an empty String
         * @param localName String name of element (with no namespace prefix, if on is present
         * @param rawName String XML version of element name: [namespace prefix':[localName]
         * @throws SAXException
         */
        public void startElement(String namespaceURI, String localName,
                                 String rawName, Attributes atts) throws
            SAXException {
            // reset the StringBuffer for the characters method
            charString = new StringBuffer();
            //System.out.println("startElement: " + localName);
            if (localName.equals("NSE-rs")) {
                // we are at the beginning of a record create a new input object
                currentNseInput = new DBSNPNseInput();
            }
            else if (localName.equals("NSE-rs_snp-class")) {
                // set the rs snp class in the input object
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getLocalName(i) == "value") {
                        //System.out.println(atts.getValue(i));
                        currentNseInput.setRSVarClass(atts.getValue(i));
                    }
                }
            }
            else if (localName.equals("NSE-ss")) {
                 currentSS = new DBSNPNseSS();
            }
            else if (localName.equals("NSE-ss_subsnp-class")) {
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getLocalName(i) == "value") {
                        currentSS.setSSVarClass(atts.getValue(i));
                    }
                }
            }
            else if (localName.equals("NSE-ss_orient")) {
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getLocalName(i) == "value") {
                        currentSS.setSSOrientToRS(atts.getValue(i));
                    }
                }
            }
            else if (localName.equals("NSE-ss_validated")) {
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getLocalName(i) == "value") {
                        currentSS.setSSValidStatus(atts.getValue(i));
                    }
                }
            }
            else if (localName.equals("NSE-rsContigHit")) {
                currentContigHit = new DBSNPNseContigHit();
            }
            else if (localName.equals("NSE-rsMaploc")) {
                currentMapLoc = new DBSNPNseMapLoc();
            }
            else if (localName.equals("NSE-rsMaploc_orient")) {
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getLocalName(i) == "value") {
                        currentMapLoc.setRSOrientToChr(atts.getValue(i));
                    }
                }
            }
            else if (localName.equals("NSE-FxnSet")) {
                currentFxnSet = new DBSNPNseFxnSet();
            }
            else if (localName.equals("NSE-FxnSet_fxn-class-contig")) {
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getLocalName(i) == "value") {
                        currentFxnSet.setFxnClass(atts.getValue(i));
                    }
                }
            }
        }

        /**
             * Indicate the end of an element has been reached, note that the parser
         * does not distinguish between empty elements and non-empty elements,
         * so this occurs uniformly
             * @param namespaceURI String URI of namespace this element is associated with
         * @param localName String name of element without prefix
         * @param rawName String name of element in XML form
         * @throws SAXExcpetion when things go wrong
         */
        public void endElement(String namespaceURI, String localName,
                               String rawName) throws SAXException {
            String currentCharString = charString.toString().trim();

            if (localName.equals("NSE-rs_refsnp-id")) {
                currentNseInput.setRsId(currentCharString);
                //System.out.println("rsId: " + currentCharString);
            }
            else if (localName.equals("NSE-rs_seq-5_E")) {
                currentNseInput.add5PrimeFlank(new DBSNPNseFlank(
                    currentCharString, currentNseInput.getNext5PrimeSeqNum()));
            }
            else if (localName.equals("NSE-rs_seq-3_E")) {
                currentNseInput.add3PrimeFlank(new DBSNPNseFlank(
                    currentCharString, currentNseInput.getNext3PrimeSeqNum()));
            }
            else if (localName.equals("NSE-rs_seq-ss-exemplar")) {
                currentExemplar = currentCharString;
            }
            else if (localName.equals("NSE-ss")) {
                currentNseInput.addSS(currentSS);
            }
            else if (localName.equals("NSE-ss_handle")) {
                currentSS.setSubmitterHandle(currentCharString);
            }
            else if (localName.equals("NSE-ss_subsnp-id")) {
                String ssId = currentCharString;
                currentSS.setSSId(ssId);
                // is this the exemplar?
                if (ssId.equals(currentExemplar)) {
                    currentSS.setIsExemplar(Boolean.TRUE);
                }
            }
            else if (localName.equals("NSE-ss_loc-snp-id")) {
                currentSS.setSubmitterSNPId(currentCharString);
            }
            else if (localName.equals("NSE-rsMaploc_physmap-int")) {
                currentMapLoc.setStartCoord(new Double(currentCharString));
           }
            else if (localName.equals("NSE-FxnSet_locusid")) {
                currentFxnSet.setLocusId(currentCharString);
            }
            else if (localName.equals("NSE-FxnSet")) {
                /*if (currentFxnSet.fxnClass == null) {
                    System.out.println(currentNseInput.getRsId() + "\t" + currentFxnSet.getLocusId() + "\t" + currentFxnSet.getFxnClass()) ;
                }*/
                currentMapLoc.addFxn(currentFxnSet);
            }
            else if (localName.equals("NSE-rsMaploc")) {
                currentContigHit.addMapLocation(currentMapLoc);
            }
            else if (localName.equals("NSE-rsContigHit_chromosome")) {
                currentContigHit.setChromosome(currentCharString);
            }
            else if (localName.equals("NSE-rsContigHit_assembly")) {
                currentContigHit.setAssembly(currentCharString);
            }
            else if (localName.equals("NSE-rsContigHit")) {
                currentNseInput.addContigHit(currentContigHit);
            }
            else if (localName.equals("NSE-rs")) {
                NSEInputVector.add(currentNseInput);
                /*
                 // Report ss orientation to the curation log
                Vector ssVector = currentNseInput.getSubSNPs();
                int fwdCtr = 0;
                int revCtr = 0;
                int otherCtr = 0;

                for (Iterator i = ssVector.iterator(); i.hasNext(); ) {
                    DBSNPNseSS ss = (DBSNPNseSS)i.next();
                    if(ss.getSSOrientToRS().equals("forward")) {
                       fwdCtr++;
                    }
                    else if (ss.getSSOrientToRS().equals("reversed")) {
                        revCtr++;
                    }
                    else {
                        otherCtr++;
                        System.out.println("Value not fwd or ref");
                    }
                }
                if (otherCtr > 0 ) {
                    logger.logcInfo("RS" + currentNseInput.getRsId() + " OTHER", false);
                }
                if (fwdCtr == 0) {
                    logger.logcInfo("RS" + currentNseInput.getRsId() + " REV_ALL", false);
                }
                if (revCtr == 0) {
                    logger.logcInfo("RS" + currentNseInput.getRsId() + " FWD_ALL", false);
                }
                else if (revCtr < fwdCtr) {
                    logger.logcInfo("RS" + currentNseInput.getRsId() + " FWD_GREATER", false);
                }
                else if(revCtr > fwdCtr) {
                    logger.logcInfo("RS" + currentNseInput.getRsId() + " REV_GREATER", false);
                }
                else if (revCtr == fwdCtr) {
                    logger.logcInfo("RS" + currentNseInput.getRsId() + " EQUAL", false);
                }
                else {
                    logger.logcInfo("RS" + currentNseInput.getRsId() + " UNHANDLED", false);
                }*/
            }
        }

        /**
         * this reports character data (within an element)
         * @param ch char[] character array with character data
         * @param start int index in array where data starts
         * @param length int length of characters in array
         * @throws SAXException
         */
        public void characters(char[] ch, int start, int length) throws
            SAXException {
            charString.append(new String(ch, start, length));
        }

        /**
         * This reports whitespace that can be ignored in the
         * originating document. This is typically invoked only when validation
         * is occuring in the parsing process.
         * 2param ch char[] character array with character data
         * @param start
         * @param length
         * @throws SAXException
         */
        public void ignorableWhitespace(char[] ch, int start, int length) throws
            SAXException {
            //System.out.println("ignorableWhitespace: [" + s + "]");
        }

        /**
             * This reports an entity that is skipped by the parser. This should only
         * occur for non-validating parser, and then is still
         * implementation-dependent behaviour
         * @param name String name of entity being skipped
         * @throws SAXException
         */
        public void skippedEntity(String name) throws SAXException {
            //System.out.println("Skipping entity: " + name);
        }
    }
}

