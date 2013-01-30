// $Header$
// $Name$

package org.jax.mgi.shr.config;

/**
 * A class that provides public accessor methods to any parameter
 * within a ConfigurationManager.
 * @has a reference to a ConfigurationManager
 * @does provides a way to lookup parameter values by name.
 * <p>Note: The use of this class requires hardcoding paramater lookup calls.
 * It may be better to create a special configurator class that provides
 * named get methods for the desired parameters and calls the
 * ConfigurationManager directly. Such a class would be added as part of
 * the org.jax.mgi.shr.config package and will draw benefit from keeping
 * hardcoded parameter values confined to a single package.</p>
 * @company Jackson Laboratory
 * @author M. Walker
 */

public class RebelReader {

  /**
   * the ConfigurationManager object
   */
  static protected ConfigurationManager cm = null;

  /**
   * default constructor which obtains a reference to the ConfigurationManager
   * singleton class.
   * @throws ConfigException
   */
  public RebelReader() throws ConfigException {
    cm = ConfigurationManager.getInstance();
  }

  /**
   * get the named parameter value
   * @param name the parameter for which to perform a lookup
   * @return the value of the given parameter
   */
  public String get(String name) {
    return cm.get(name);
  }
}

// $Log$
// Revision 1.3  2005/08/15 14:58:43  mbw
// javadocs only
//
// Revision 1.2  2004/07/21 19:10:29  mbw
// javadocs only
//
// Revision 1.1  2003/12/30 16:50:11  mbw
// imported into this product
//
// Revision 1.2  2003/12/09 22:47:24  mbw
// merged jsam branch onto the trunk
//
// Revision 1.1.4.3  2003/06/04 15:08:50  mbw
// javadoc edits
//
// Revision 1.1.4.2  2003/05/22 15:48:48  mbw
// javadocs edits
//
// Revision 1.1.4.1  2003/05/20 18:42:23  mbw
// merged from branch lib_java_shrdbutils-1-0-7-jsam
//
// Revision 1.1.2.2  2003/04/22 00:11:23  mbw
// no longer throwing KnownException type
//
// Revision 1.1.2.1  2003/04/18 00:11:55  mbw
// renamed from ConfigReader
//
// Revision 1.3.2.7  2003/03/21 14:32:47  mbw
// javadcos
//
// Revision 1.3.2.6  2003/03/21 14:25:28  mbw
// added standard header/footer
//
// Revision 1.3.2.5  2003/03/21 14:14:52  mbw
// added standard header/footer
//
/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
