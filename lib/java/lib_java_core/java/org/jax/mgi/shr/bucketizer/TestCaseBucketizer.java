// $HEADER$
// $NAME$

package org.jax.mgi.shr.bucketizer;

import java.util.*;

import org.jax.mgi.shr.exception.MGIException;
import org.jax.mgi.shr.ioutils.OutputDataFile;

/**
 *
 * An implementation of an AbstractBucketizer used for tesing
 * @has nothing
 * @does counts the incoming BucketItems for testing purposes
 * and provides access to the counts
 * @company Jackson Laboratory
 * @author M Walker
 *
 */


public class TestCaseBucketizer extends AbstractBucketizer
{
    int counts[] = {0,0,0,0,0,0,0,0,0};
    public TestCaseBucketizer(Iterator it1, Iterator it2,
                              String[] svaNames)
    throws MGIException
    {
        super(it1, it2, svaNames);
    }



    public int[] getCounts()
    {
        return this.counts;
    }

    public void process_One_To_Many(BucketItem bucketItem)
        throws MGIException
    {
        int cnt = counts[BucketItem.ONE_TO_MANY];
        int newcnt = ++cnt;
        counts[BucketItem.ONE_TO_MANY] = newcnt;
    }
    public void process_Zero_To_One(BucketItem bucketItem)
        throws MGIException
    {
        int cnt = counts[BucketItem.ZERO_TO_ONE];
        int newcnt = ++cnt;
        counts[BucketItem.ZERO_TO_ONE] = newcnt;
    }
    public void process_One_To_Zero(BucketItem bucketItem)
        throws MGIException
    {
        int cnt = counts[BucketItem.ONE_TO_ZERO];
        int newcnt = ++cnt;
        counts[BucketItem.ONE_TO_ZERO] = newcnt;
    }
    public void process_Many_To_One(BucketItem bucketItem)
        throws MGIException
    {
        int cnt = counts[BucketItem.MANY_TO_ONE];
        int newcnt = ++cnt;
        counts[BucketItem.MANY_TO_ONE] = newcnt;
    }
    public void process_One_To_One(BucketItem bucketItem)
        throws MGIException
    {
        int cnt = counts[BucketItem.ONE_TO_ONE];
        int newcnt = ++cnt;
        counts[BucketItem.ONE_TO_ONE] = newcnt;
    }
    public void process_Many_To_Many(BucketItem bucketItem)
        throws MGIException
    {
        int cnt = counts[BucketItem.MANY_TO_MANY];
        int newcnt = ++cnt;
        counts[BucketItem.MANY_TO_MANY] = newcnt;
    }


}

// $LOG$

