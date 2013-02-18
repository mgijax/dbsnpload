package org.jax.mgi.shr;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;


/**
* A class of static methods for connecting to remote CGIs and getting
* the responses from the CGIs.
*/
public class CGIBuilder {

    public final static String METHOD_POST = "POST";
    public final static String METHOD_GET = "GET";

    /**
    * Contacts the cgi specifed by strCGIURL, downloads the response from
    * the server, and returns it the form of a String
    * @return A String containing the contents of the response from the
    *         server, minus the headers.
    *
    * @param strCGIURL The URL of the cgi including the protocol, like:
    *                  http://www.informatics.jax.org/searches/marker.cgi
    *                  Note that this does not include the query string.
    * @param strMethod The method to use when contacting the CGI.
    * @param strQuery  The query string to pass the cgi.  NOTE: It is assumed
    *                  that the query string will include the leading ?,
    *                  I.E. what is returned by generateQueryStr.
    * @throws MalformedURLException When strCGIURL does not point to a valid
    *         URL.
    * @throws IOException When there is a communication error speaking with
    *         the CGI.
    * @throws IllegalArgumentException If strMethod is neither METHOD_POST
    *                                  nor METHOD_GET.
    */
    // This method could easily be converted to handling abitrary content
    // by changing the way the stream handling works.
    public static String sendQuery(String strCGIURL,
                                   String strMethod,
                                   String strQuery)
                                   throws MalformedURLException,
                                          IOException,
                                          IllegalArgumentException {

        URL uQuery;
        URLConnection ucQuery;
	InputStream is;
        if(strMethod == METHOD_POST) {
            // Setup the server connection
            uQuery = new URL(strCGIURL);
            ucQuery = uQuery.openConnection();
            // We need to set this to mozilla due to some problems with
            // some old CGIs
            ucQuery.setRequestProperty("User-Agent","Mozilla/4.0/java/org.jax.mgi.shr.CGIBuilder");
            ucQuery.setDoOutput(true);
            //Post the query string to the server
            StreamUtils.writeStream(ucQuery.getOutputStream(),strQuery);
	    is = ucQuery.getInputStream();
            return StreamUtils.readStream(is);
        } else if(strMethod == METHOD_GET) {
            //see above
            uQuery = new URL(strCGIURL+strQuery);
            ucQuery = uQuery.openConnection();
            ucQuery.setRequestProperty("User-Agent","Mozilla/4.0/java/org.jax.mgi.shr.CGIBuilder");
            return StreamUtils.readStream(ucQuery.getInputStream());
        } else throw new IllegalArgumentException("strMethod must be either "+
                                                  METHOD_POST+" or "+
                                                  METHOD_GET+".");
    }

    /**
    * Using the pairs stored in haArgs a query string is generated.
    * The keys to the ListHash are used as the parameter names while
    * all of the values associated with that key are used as parameters.
    * All of the keys and values are encoded by URLEncoder to ensure that
    * a valid query string is constructed.
    * For example, haArgs was made up of one key/value pair
    * {"foo",["bar","foobar"]} the resulting query string would be:
    * "?foo=bar&foo=foobar"
    * If {"",["foo bar"]} was passed in, the value of the query string
    * would be:
    * "?foo+bar"
    * Note that a query string returned by this method is a valid argument
    * for sendQuery above.
    * @return A String containing the generated query string, null
    *         if lhArgs is null, "" if lhArgs is empty
    * @param lhArgs A ListHash containing the name/value pairs to be
    *               used to generate the query string.
    */
    public static String generateQueryStr(ListHash lhArgs) {

        if(lhArgs == null)
            return null;
        Iterator itKeys = lhArgs.keySet().iterator(); //An interator for the
                                                      //set of query
                                                      //parameter names.
        String strKey,strValue;  //strKey is maps to the parameter name in the
                                 //query string, strValue maps to its value.
        ArrayList alValue;       //The set of values for a given parameter.
        StringBuffer strbReturn = new StringBuffer(); //Buffer to build the
                                                      //query string in.
        //If there are any keys, start a query string
        if(itKeys.hasNext())
            strbReturn.append("?");

        //Run through all the keys
        while(itKeys.hasNext()) {
            strKey = (String)itKeys.next();
            alValue = (ArrayList)lhArgs.get(strKey);
            strKey = URLEncoder.encode(strKey);
            //pretiffy all the values
            for(int i=0;i<alValue.size();i++) {
                strValue = (String)alValue.get(i);
                strValue = URLEncoder.encode(strValue);
                //and insert the pair into the query string
                if(strKey.length() == 0) {
                    strbReturn.append(strValue);
                } else {
                    strbReturn.append(strKey + "=" +strValue);
                }
	        if (itKeys.hasNext())
                    strbReturn.append("&");
            }
        }
        return strbReturn.toString();
    }
}
