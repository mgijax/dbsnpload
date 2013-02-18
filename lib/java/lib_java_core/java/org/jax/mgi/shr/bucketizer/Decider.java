// $HEADER$
// $NAME$

package org.jax.mgi.shr.bucketizer;

import java.util.Hashtable;

import org.jax.mgi.shr.sva.SVASet;

/**
 *
 * An interface for defining a class which can decide whether two objects are
 * truely related to each other. The AbstractBucketizer has a Decider which it
 * calls when it derives that the two objects are related and will allow an
 * implementation to accept or decline the association. A default Decider is
 * used by the AbstractBucketizer if none is provided by the application
 * which always returns true.
 * @has nothing
 * @does returns an object (defined by the implementation) if true else
 * returns null if false.
 * @company Jackson Laboratory
 * @author M Walker
 *
 */

public interface Decider {
    /**
     * decides whether or not two given Bucketizables are equivalent
     * @param setMember1 Bucketizable from set 1
     * @param setMember2 Bucketizable from set 2
     * @param svaSet the SVASet representation of the common attributes between
     * the two Bucketizables
     * @return true if the two are equivalent, false otherwise
     */
    Object decide(Bucketizable setMember1, Bucketizable setMember2,
                  SVASet svaSet);
}

// $LOG$

