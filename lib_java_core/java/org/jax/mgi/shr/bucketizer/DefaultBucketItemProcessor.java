// $HEADER$
// $NAME$

package org.jax.mgi.shr.bucketizer;

/**
 * An implementation of a BucketItemProcessor which does nothing. It is used
 * by the BucketItemProcessor class as the default implementation for
 * processing all buckets
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author M Walker
 *
 */

public class DefaultBucketItemProcessor implements BucketItemProcessor
{
        /**
         * empty method which is intended to be overridden by the superclass
         * @assumes nothing
         * @effects nothing
         * @param bucketItem the BucketItem instance to process
         */
        public void processBucketItem(BucketItem bucketItem) {}
}

// $LOG$

