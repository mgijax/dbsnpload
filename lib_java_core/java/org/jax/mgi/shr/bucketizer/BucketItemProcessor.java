// $HEADER$
// $NAME$

package org.jax.mgi.shr.bucketizer;

/**
 *
 * An interface which defines a class that processes a BucketItem, an object
 * created by the AbstractBucketizer during bucketization which represents
 * a grouping of related members from the two data sets
 * @has nothing
 * @does processes a BucketItem in an implementation specific way
 * @company Jackson Laboratory
 * @author M Walker
 *
 */

public interface BucketItemProcessor {
    /**
     * processes a BucketItem
     * @param bucketItem the BucketItem to process
     */
    void processBucketItem(BucketItem bucketItem);
}

// $LOG$

