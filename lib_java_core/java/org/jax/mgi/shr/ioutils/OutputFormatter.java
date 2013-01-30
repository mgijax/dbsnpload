package org.jax.mgi.shr.ioutils;

/**
 * An object which can format data objects for a specific report
 * format and specifies headers, trailers and file name suffixes for the
 * formatted output file
 * @has nothing.
 * @does formats report files
 * @company Jackson Laboratory
 * @author M. Walker
 */


public interface OutputFormatter {

    /**
     * execute any preprocessing before formatting the data
     * @assumes nothing
     * @effects pre processing will be performed in advance of formatting
     * the output
     * @throws IOUException thrown if there is an error accessing the
     * file system
     */
    public void preprocess() throws IOUException;

    /**
     * execute any post processing after formatting the data
     * @assumes nothing
     * @effects post processing will be performed after formatting
     * the output
     * @throws IOUException thrown if there is an error accessing the
     * file system
     */
    public void postprocess() throws IOUException;

    /**
     * get the header text for this formatted file
     * @assumes nothing
     * @effects nothing
     * @return the header text for this formatted file
     */
    public String getHeader();

    /**
     * get the trailer text for this formatted file
     * @assumes nothing
     * @effects nothing
     * @return the trailer text for this formatted file
     */
    public String getFileSuffix();

    /**
     * format the given data to produce a formatted text string
     * @assumes nothing
     * @effects nothing
     * @param data the given data object
     * @return a formatted text string
     */
    public String format(Object data);

}
