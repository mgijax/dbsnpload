// $HEADER$
// $NAME$

package org.jax.mgi.shr.bucketizer;

import java.util.*;

import org.jax.mgi.shr.sva.SVASet;

/**
 * A basic implementation of the Bucketizable interface
 * @has an SVASet of attributes, an id and a provider tag
 * @does provides accessors for adding and retreiving attributes from the
 * SVASet
 * @company Jackson Laboratory
 * @author M Walker
 *
 */

public class SimpleBucketizable implements Bucketizable
{
    /**
     *  the id of the instance
     */
    protected String id = null;
    /**
     *  the name of the provider for this instance
     */
    protected String provider = null;
    /**
     *  the SVASet of attributes used when bucketizing
     */
    protected SVASet svaSet = null;

    /**
     * Constructor
     * @param id the id tag
     * @param provider the provider tag
     * @param svaNames an array of names for the SVAs used in the SVASet
     */
    public SimpleBucketizable(String id, String provider, String[] svaNames)
    {
        this.id = id;
        this.provider = provider;
        this.svaSet = new SVASet(svaNames);
    }

    /**
     * return the id tag
     * @assumes nothing
     * @effects nothing
     * @return the id tag
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * return the provider tag
     * @assumes nothing
     * @effects nothing
     * @return the provider tag
     */
    public String getProvider()
    {
        return this.provider;
    }

    /**
     * add a new attribute to the SVASet
     * @assumes nothing
     * @effects the object is added to the SVA
     * @param svaName the name of the SVA
     * @param svaValue the value of the attribute
     */
    public void add(String svaName, Object svaValue)
    {
        this.svaSet.addSVAMember(svaName, svaValue);
    }

    /**
     * get the SVASet
     * @assumes nothing
     * @effects nothing
     * @return the SVASet
     */
    public SVASet getSVASet()
    {
        return this.svaSet;
    }

    /**
     * create a String representation of this instance
     * @return a String representation of this instance
     */
    public String toString()
    {
        return this.svaSet.toString();
    }

}

// $LOG$

