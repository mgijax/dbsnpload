package org.jax.mgi.shr.dbutils.dao;

import java.sql.Timestamp;

/**
 * An RecordStampable interface that follows the MGD record stamp format,
 * meaning it stamps the following fields: _CreatedBy_key, _ModifiedBy_key,
 * creation_date, modification_date
 * @has nothing
 * @does provides the interface
 * @company The Jackson Laboratory
 * @author M Walker
 */
public interface RecordStampable_MGD
    extends RecordStampable
{
    /**
     * set the createdBy field
     * @param key the key to use
     */
    public void setCreatedByKey(Integer key);

    /**
     * set the modifiedBy field
     * @param key the key to use
     */
    public void setModifiedByKey(Integer key);

    /**
     * set the creationDate field
     * @param t the date to use
     */
    public void setCreationDate(Timestamp t);

    /**
     * set the modificationDate
     * @param t the date to use
     */
    public void setModificationDate(Timestamp t);
}

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
