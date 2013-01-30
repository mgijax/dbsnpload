// $HEADER$
// $NAME$

package org.jax.mgi.shr.bucketizer;

import java.util.Hashtable;

import org.jax.mgi.shr.sva.SVASet;

/**
 * An implementation of a Decider which always returns true. It is used
 * by the AbstractBucketizer class as the default implementation for
 * the Decider instance
 * @has nothing
 * @does returns an object which represents true
 * @company Jackson Laboratory
 * @author M Walker
 *
 */


public class DefaultDecider implements Decider
{
        /**
         * returns the value of svaSet argument which if not null
         * indicates to the AbstractBucketizer that b1 and b2 are related
         * @assumes nothing
         * @effects nothing
         * @param b1 Bucketizable one to compare
         * @param b2 Bucketizable two to compare
         * @param svaSet the SVASet of common attributes between b1 and b2
         */

    public Object decide(Bucketizable b1, Bucketizable b2, SVASet svaSet)
    {
        return svaSet;
    }

}

// $LOG$
