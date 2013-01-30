// $HEADER$
// $NAME$

package org.jax.mgi.shr.bucketizer;

import java.util.Set;
import org.jax.mgi.shr.sva.SVASet;

/**
 *
 * An interface for defining a class which can be represented within the
 * bucketizer algorithm of the AbstractBucketizer as one that contains an
 * SVASet (attributes used for determing the members that are related to each
 * other from two data sets). Two sets of Bucketizables are compared by the
 * AbstractBucketizer and all the relationships between Bucketizables are
 * calculated and 'bucketized'
 * @has an id, a provider and a SVASet of attributes
 * @does nothing
 * @company Jackson Laboratory
 * @author M Walker
 *
 */

public interface Bucketizable {
    /**
	* get the id for this instance
	* @return id for this instance
	*/
    public String getId();
    /**
     * get the provider name for this instance
	* @return provider name for this instance
	*/
    public String getProvider();
    /**
     * get the SVASet for this instance
	* @return SVASet for this instance
	*/
    public SVASet getSVASet();
}

// $LOG$

