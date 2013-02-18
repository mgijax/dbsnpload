package org.jax.mgi.shr.dbutils.dao;

import java.sql.Timestamp;
import java.util.Date;
import org.jax.mgi.shr.config.RecordStampCfg;
import org.jax.mgi.shr.config.ConfigException;

/**
 * A class for stamping DAO (State) classes by modifying their user and time
 * fields
 * @has a RecordStampCfg for looking up job stream names within the
 * configuration
 * @does determines what type of record stamp being implemented by the given
 * DAO (State) class and modifies the appropriate fields for update and
 * insert
 * @author mbw
 * @company The Jackson Laboratory
 */
public class RecordStamper
{
    /**
     * stamp a given RecordStampable object with the user/time stamp
     * for an insert operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * modified
     * @param rcd the given RecordStampable object
     * @throws RecordStampException throws if there is an error with the
     * coniguration or an error looking up key values in the database
     */
    public static void stampForInsert(RecordStampable rcd)
        throws
        RecordStampException
    {
        if (rcd instanceof RecordStampable_MGD)
            stampInsertMGD( (RecordStampable_MGD) rcd);
        else if (rcd instanceof RecordStampable_RADAR)
            stampInsertRADAR( (RecordStampable_RADAR) rcd);
        else if (rcd instanceof RecordStampable_MGDDate)
            stampInsertMGDDate( (RecordStampable_MGDDate) rcd);
        else if (rcd instanceof RecordStampable_MGDRelease)
            stampInsertMGDRelease( (RecordStampable_MGDRelease) rcd);
        else if (rcd instanceof RecordStampable_MGDOrg)
            stampInsertMGDOrg( (RecordStampable_MGDOrg) rcd);
        else
        {
            RecordStampExceptionFactory eFactory =
                new RecordStampExceptionFactory();
            RecordStampException e =
                (RecordStampException)
                eFactory.getException(
                              RecordStampExceptionFactory.UnknownType);
            e.bind(rcd.getClass().getName());
            throw e;
        }
    }

    /**
     * stamp a given RecordStampable object with the user/time stamp
     * for a update operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the given RecordStampable object
     * @throws RecordStampException throws if there is an error with the
     * coniguration or an error looking up key values in the database
     */
    public static void stampForUpdate(RecordStampable rcd)
        throws
        RecordStampException
    {
        if (rcd instanceof RecordStampable_MGD)
            stampUpdateMGD( (RecordStampable_MGD) rcd);
        else if (rcd instanceof RecordStampable_RADAR)
            stampUpdateRADAR( (RecordStampable_RADAR) rcd);
        else if (rcd instanceof RecordStampable_MGDDate)
            stampUpdateMGDDate( (RecordStampable_MGDDate) rcd);
        else if (rcd instanceof RecordStampable_MGDRelease)
            stampUpdateMGDRelease( (RecordStampable_MGDRelease) rcd);
        else if (rcd instanceof RecordStampable_MGDOrg)
            stampUpdateMGDOrg( (RecordStampable_MGDOrg) rcd);
        else
        {
            RecordStampExceptionFactory eFactory =
                new RecordStampExceptionFactory();
            RecordStampException e =
                (RecordStampException)
                eFactory.getException(
                           RecordStampExceptionFactory.UnknownType);
            e.bind(rcd.getClass().getName());
            throw e;
        }
    }

    /**
     * record stamp a RecordStampable_MGD object for the insert operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the RecordStampable_MGD object
     * @throws RecordStampException thrown if there is an error with the
     * configuration or looking up key values in the database
     */
    private static void stampInsertMGD(RecordStampable_MGD rcd)
        throws
        RecordStampException
    {
        Timestamp t = new Timestamp(new Date().getTime());
        Integer userKey = null;
        try
        {
            RecordStampCfg cfg = new RecordStampCfg();
            userKey = cfg.getJobStreamKey();
        }
        catch (ConfigException e)
        {
            RecordStampExceptionFactory eFactory =
                new RecordStampExceptionFactory();
            RecordStampException e2 = (RecordStampException)
                eFactory.getException(RecordStampExceptionFactory.UnknownUser,
                                      e);
            e2.bind(rcd.getClass().getName());
            throw e2;
        }
        rcd.setCreatedByKey(userKey);
        rcd.setModifiedByKey(userKey);
        rcd.setModificationDate(t);
        rcd.setCreationDate(t);
    }

    /**
     * record stamp a RecordStampable_RADAR object for the insert operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the RecordStampable_RADAR object
     * @throws RecordStampException thrown if there is an error with the
     * configuration or looking up key values in the database
     */
    private static void stampInsertRADAR(RecordStampable_RADAR rcd)
        throws
        RecordStampException
    {
        Integer userKey = null;
        Timestamp t = new Timestamp(new Date().getTime());
        try
        {
            RecordStampCfg cfg = new RecordStampCfg();
            userKey = cfg.getJobKey();
        }
        catch (ConfigException e)
        {
            RecordStampExceptionFactory eFactory =
                new RecordStampExceptionFactory();
            RecordStampException e2 = (RecordStampException)
                eFactory.getException(RecordStampExceptionFactory.UnknownUser,
                                      e);
            e2.bind(rcd.getClass().getName());
            throw e2;
        }
        rcd.setCreationDate(t);
        rcd.setJobStreamKey(userKey);
    }

    /**
     * record stamp a RecordStampable_MGDDate object for the insert operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the RecordStampable_MGDDate object
     */
    private static void stampInsertMGDDate(RecordStampable_MGDDate rcd)
    {
        Timestamp t = new Timestamp(new Date().getTime());
        rcd.setCreationDate(t);
        rcd.setModificationDate(t);
    }

    /**
     * record stamp a RecordStampable_MGDRelease object for the insert
     * operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the RecordStampable_MGDRelease object
     */
    private static void stampInsertMGDRelease(RecordStampable_MGDRelease rcd)
    {
        Timestamp t = new Timestamp(new Date().getTime());
        rcd.setCreationDate(t);
        rcd.setReleaseDate(t);
        rcd.setModificationDate(t);
    }

    /**
     * record stamp a RecordStampable_MGDOrg object for the insert operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the RecordStampable_MGDOrg object
     * @throws RecordStampException thrown if there is an error with the
     * configuration
     */
    private static void stampInsertMGDOrg(RecordStampable_MGDOrg rcd)
        throws
        RecordStampException
    {
        Timestamp t = new Timestamp(new Date().getTime());
        String createdBy = null;
        String modifiedBy = null;
        RecordStampCfg cfg = null;
        try
        {
            cfg = new RecordStampCfg();
        }
        catch (ConfigException e)
        {
            RecordStampExceptionFactory eFactory =
                new RecordStampExceptionFactory();
            RecordStampException e2 = (RecordStampException)
                eFactory.getException(
                           RecordStampExceptionFactory.ConfigErr, e);
            e2.bind(rcd.getClass().getName());
            throw e2;
        }
        modifiedBy = cfg.getModifiedBy();
        createdBy = cfg.getCreatedBy();
        rcd.setCreatedBy(createdBy);
        rcd.setModifiedBy(modifiedBy);
        rcd.setCreationDate(t);
        rcd.setModificationDate(t);
    }

    /**
     * record stamp a RecordStampable_MGD object for the update operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the RecordStampable_MGD object
     * @throws RecordStampException thrown if there is an error with the
     * configuration or looking up key values in the database
     */
    private static void stampUpdateMGD(RecordStampable_MGD rcd)
        throws
        RecordStampException
    {
        Timestamp t = new Timestamp(new Date().getTime());
        Integer userKey = null;
        try
        {
            RecordStampCfg cfg = new RecordStampCfg();
            userKey = cfg.getJobStreamKey();
        }
        catch (ConfigException e)
        {
            RecordStampExceptionFactory eFactory =
                new RecordStampExceptionFactory();
            RecordStampException e2 = (RecordStampException)
                eFactory.getException(RecordStampExceptionFactory.UnknownUser,
                                      e);
            e2.bind(rcd.getClass().getName());
            throw e2;
        }
        rcd.setModifiedByKey(userKey);
        rcd.setModificationDate(t);
    }

    /**
     * record stamp a RecordStampable_RADAR object for the update operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the RecordStampable_RADAR object
     */
    private static void stampUpdateRADAR(RecordStampable_RADAR rcd)
    {
        /**
         * there are no update fields for this format
         */
    }

    /**
     * record stamp a RecordStampable_MGDDate object for the update operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the RecordStampable_MGDDate object
     */
    private static void stampUpdateMGDDate(RecordStampable_MGDDate rcd)
    {
        Timestamp t = new Timestamp(new Date().getTime());
        rcd.setModificationDate(t);
    }

    /**
     * record stamp a RecordStampable_MGDRelease object for the update
     * operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the RecordStampable_MGDRelease object
     */
    private static void stampUpdateMGDRelease(RecordStampable_MGDRelease rcd)
    {
        Timestamp t = new Timestamp(new Date().getTime());
        rcd.setModificationDate(t);
    }

    /**
     * record stamp a RecordStampable_MGDOrg object for the update operation
     * @assumes nothing
     * @effects the given object will have it's user/time stamp fields
     * @param rcd the RecordStampable_MGDOrg object
     * @throws RecordStampException thrown if there is an error with the
     * configuration
     */
    private static void stampUpdateMGDOrg(RecordStampable_MGDOrg rcd)
        throws
        RecordStampException
    {
        Timestamp t = new Timestamp(new Date().getTime());
        String modifiedBy = null;
        RecordStampCfg cfg = null;
        try
        {
            cfg = new RecordStampCfg();
        }
        catch (ConfigException e)
        {
            RecordStampExceptionFactory eFactory =
                new RecordStampExceptionFactory();
            RecordStampException e2 = (RecordStampException)
                eFactory.getException(
                              RecordStampExceptionFactory.ConfigErr, e);
            e2.bind(rcd.getClass().getName());
            throw e2;
        }
        modifiedBy = cfg.getModifiedBy();
        rcd.setModifiedBy(modifiedBy);
        rcd.setModificationDate(t);
    }
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
