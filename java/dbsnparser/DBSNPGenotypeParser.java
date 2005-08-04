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
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.jax.mgi.shr.stringutil.StringLib;
import org.jax.mgi.shr.dla.log.DLALogger;
import org.jax.mgi.shr.dla.log.DLALoggingException;

public class DBSNPGenotypeParser implements DBSNPParser {

    // DEBUG
    DLALogger logger;
    public DBSNPGenotypeParser() throws DLALoggingException {
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
        DBSNPContentHandler genContentHandler = new DBSNPContentHandler();

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

        return genContentHandler.getRSVector();

    }

    /**
     * defines callback behavior for the SAX callbacks
     * associated with an XML document's content.
     */
    class DBSNPContentHandler extends DefaultHandler {
        //
        private Locator locator;

        // the current rs object
        private DBSNPGenotypeInput currentInput;

        // current ssid for this rs
        private String currentSSId;
        // current orientation of the ss to the RS flanking sequence
        private String currentSSOrientToRS;

        // current strain id and strain from the Indivuidual section; this is to create strainMap
        // for translation strainId to strain (or Jax Registry Id)
        private String currentStrainMapStrainId;
        private String currentStrain;

        // maps dbSNP strainId to strain or Jax Registry Id
        private HashMap strainMap = new HashMap();

        // The current strain and allele for currentSS
        private String currentStrainId;
        // currentStrainId converted to dbSNP id
        String currentConvertedStrainId;
        // allele for currentStrainId
        private Allele currentAllele;
        // The set of strain alleles for the currentSS
        private HashMap currentStrAlleleMap;

        // Vector of RS objects
        private Vector rsVector;

        public DBSNPContentHandler() {
            // one per rs record processed, reset in end element
            currentInput = new DBSNPGenotypeInput();
            // current ss processed
            //currentSS = new DBSNPGenotypeSS();
            // one of these per app invocation
            rsVector = new Vector();
        }

        public Vector getRSVector() {
            return rsVector;
        }

        /**
             * Provide reference to "Locator" which provides information about where
         * in a document callbacks occur
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

            // This is the start of a RefSNP genotype record, get the rsId
            // value and set in the current RSGenotype object
            /*if (localName.equals("Population")) {
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getLocalName(i) == "popId") {
                        //System.out.println(atts.getValue(i));
                        currentHandleMapPopId = atts.getValue(i);
                    }
                    else if (atts.getLocalName(i) == "handle") {
                        //System.out.println(atts.getValue(i));
                        currentHandle = atts.getValue(i);
                    }
                }
            }*/
            if (localName.equals("Individual")) {
                for (int i = 0; i < atts.getLength(); i++) {
                    // indId, in this section, is the dbSNP strain id used in
                    // the allele section to identify the strain
                    if (atts.getLocalName(i) == "indId") {
                        //System.out.println(atts.getValue(i));
                        currentStrainMapStrainId = atts.getValue(i);
                    }
                }
            }
            else if (localName.equals("SourceInfo")) {
                for (int i = 0; i < atts.getLength(); i++) {
                    // indId is either a jax registry id (if "sourceType=repository")
                    // or a strain name (if sourceType=submitter)
                    if (atts.getLocalName(i) == "indId") {
                        // Map the dbSNP strain id to the jax registry id or strain
                        // name
                        currentStrain = atts.getValue(i);
                    }
                }
            }

            else if (localName.equals("SnpInfo")) {
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getLocalName(i) == "rsId") {
                        //System.out.println(atts.getValue(i));
                        currentInput.setRsId(atts.getValue(i));
                    }
                }
            }
            else if (localName.equals("SsInfo")) {
                for (int i = 0; i < atts.getLength(); i++) {
                     if( atts.getLocalName(i) == "ssId") {
                         currentStrAlleleMap = new HashMap();
                         currentSSId = atts.getValue(i);
                     }
                     else if (atts.getLocalName(i) == "ssOrientToRs") {
                         currentSSOrientToRS = atts.getValue(i);
                     }
                }
            }
            else if (localName.equals("GTypeByInd")) {
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getLocalName(i) == "gtype") {
                        String allele = atts.getValue(i);
                        ArrayList a = StringLib.split(allele, "/");
                        allele = (String)a.get(0);
                        //currentAllele = new Allele(atts.getValue(i));
                        currentAllele = new Allele(allele, currentSSOrientToRS);
                        int size = allele.length();
                        if(size > 30) {
                            System.out.println(allele + ", " + size);
                        }
                    }
                    else if (atts.getLocalName(i) == "indId") {
                        currentStrainId = atts.getValue(i);
                        if (strainMap.containsKey(currentStrainId)) {
                            // add an strain/allele to the current input object
                            //currentInput.addSSAlleles();
                           // currentSS.addStrainAllele( (String) strainMap.get(
                             //   currentStrainId),
                               // currentAllele);
                            // set converted strain and its allele into the strain allele map
                            currentConvertedStrainId = (String)strainMap.get(currentStrainId);
                            currentStrAlleleMap.put(currentConvertedStrainId, currentAllele);
                        }
                        else {
                            System.out.println("No strain for strainId " +
                                               currentStrainId);
                        }
                    }
                    else if (atts.getLocalName(i) == "flag") {
                        String flag = (atts.getValue(i)).trim();
                        if (flag.equals("gtyFlag1")) {
                            //currentSS.addStrainAlleleFlag((String)strainMap.get(currentStrainId),true);
                            Allele a = (Allele)currentStrAlleleMap.get(currentConvertedStrainId);
                            a.setGtyFlag1(true);
                            currentStrAlleleMap.put(currentConvertedStrainId, a);
                        }
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
            if (localName.equals("SourceInfo")) {
                // map the dbSNP strain id to the strain name or jax id
                strainMap.put(currentStrainMapStrainId, currentStrain);
            }
            // When we get to the end of the ss information we are done with
            // the record. Add the alleles to the strain allele map
            else if (localName.equals("SsInfo")) {
                currentInput.addSSAlleles(currentSSId, currentStrAlleleMap);
              //  if (currentStrAlleleMap.size() < 1 ) {
              //      logger.logcInfo("NO ALLELES RS" + currentInput.getRsId() + " SS" + currentSSId, false);
              //  }
            }

            else if (localName.equals("SnpInfo")) {
                // add RS to the vector
                rsVector.add(currentInput);
                // reset globals for the next record
                currentInput = new DBSNPGenotypeInput();
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
            //System.out.println("characters: " + s);
        }
    }
}


