package org.jax.mgi.shr.unitTest;

import java.io.IOException;

/**
 * @is An interface which defines a data file create method. The
 * DataFileClassCreator creates a class that implements this interface.
 * @has nothing
 * @does nothing
 * @company Jackson Laboratory
 * @author M Walker
 */

public interface DataFileWriter {
  public void createDataFile(String name) throws IOException;
}