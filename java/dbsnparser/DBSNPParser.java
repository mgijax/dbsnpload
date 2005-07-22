package dbsnparser;

import java.io.IOException;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Vector;

public interface DBSNPParser{
    /**
     * Implementing classes implement this method to parse DBSNP input
     * @param file the xml file to parse
     */
    public Vector parse(String file) throws IOException;

}


