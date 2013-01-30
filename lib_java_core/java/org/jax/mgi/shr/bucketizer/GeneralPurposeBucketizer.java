// $HEADER$
// $NAME$

package org.jax.mgi.shr.bucketizer;

import java.util.*;
import org.jax.mgi.shr.exception.MGIException;

/**
 *
 * An implementation of an AbstractBucketizer that uses 
 * BucketItemProcessors to handle 1:1, 1:N, N:1,
 * 0:1, 1:0, N:N types of BucketItems
 * @has a BucketItemProcessor for for each type of BucketItem
 * cardinality
 * @does bucketizing the input data and processes the results
 * using the given BucketItemProcessors
 * @company Jackson Laboratory
 * @author M Walker
 *
 */


public class GeneralPurposeBucketizer extends AbstractBucketizer
{
    protected  BucketItemProcessor One_To_One_Processor =
        new DefaultBucketItemProcessor();
    protected  BucketItemProcessor One_To_Many_Processor =
        new DefaultBucketItemProcessor();
    protected  BucketItemProcessor Many_To_One_Processor =
        new DefaultBucketItemProcessor();
    protected  BucketItemProcessor Many_To_Many_Processor =
        new DefaultBucketItemProcessor();
    protected  BucketItemProcessor Zero_To_One_Processor =
        new DefaultBucketItemProcessor();
    protected  BucketItemProcessor One_To_Zero_Processor =
        new DefaultBucketItemProcessor();

    public GeneralPurposeBucketizer(Iterator it1, Iterator it2,
                                    String[] svaNames, Decider decider)
    throws MGIException
    {
        super(it1, it2, svaNames);
    }

    public GeneralPurposeBucketizer(Iterator it1, Iterator it2,
                                    String[] svaNames)
    throws MGIException
    {
        super(it1, it2, svaNames);
    }

    public void setProcessor_One_To_One(BucketItemProcessor bip)
        throws MGIException
    {
        this.One_To_One_Processor = bip;
    }

    public void setProcessor_One_To_Many(BucketItemProcessor bip)
        throws MGIException
    {
        this.One_To_Many_Processor = bip;
    }

    public void setProcessor_Many_To_One(BucketItemProcessor bip)
        throws MGIException
    {
        this.Many_To_One_Processor = bip;
    }

    public void setProcessor_Many_To_Many(BucketItemProcessor bip)
        throws MGIException
    {
        this.Many_To_Many_Processor = bip;
    }

    public void setProcessor_Zero_To_One(BucketItemProcessor bip)
        throws MGIException
    {
        this.Zero_To_One_Processor = bip;
    }

    public void setProcessor_One_To_Zero(BucketItemProcessor bip)
        throws MGIException
    {
        this.One_To_Zero_Processor = bip;
    }


    public void process_One_To_One(BucketItem bucketItem)
        throws MGIException
    {
        this.One_To_One_Processor.processBucketItem(bucketItem);
    }

    public void process_One_To_Many(BucketItem bucketItem)
        throws MGIException
    {
        this.One_To_Many_Processor.processBucketItem(bucketItem);
    }

    public void process_Many_To_One(BucketItem bucketItem)
        throws MGIException
    {
        this.Many_To_One_Processor.processBucketItem(bucketItem);
    }

    public void process_Many_To_Many(BucketItem bucketItem)
        throws MGIException
    {
        this.Many_To_Many_Processor.processBucketItem(bucketItem);
    }

    public void process_Zero_To_One(BucketItem bucketItem)
        throws MGIException
    {
        this.Zero_To_One_Processor.processBucketItem(bucketItem);
    }

    public void process_One_To_Zero(BucketItem bucketItem)
        throws MGIException
    {
        this.One_To_Zero_Processor.processBucketItem(bucketItem);
    }

}

// $LOG$

