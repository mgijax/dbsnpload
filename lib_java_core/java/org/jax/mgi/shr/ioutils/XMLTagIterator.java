package org.jax.mgi.shr.ioutils;

import javax.xml.stream.*;

import org.jax.mgi.shr.ioutils.IOUExceptionFactory;
import java.util.HashMap;

/**
 * Is a class which views an xml file simply as a series of tags where each
 * tag has attributes and text (either could be null) and provides
 * functionality to iterate over these tags and extract text and attribute
 * values from them. The user indicates which xml element they are interested
 * in iterating over by providing a target tag name. Calls to nextTargetTag()
 * can move the pointer to the start of the next target element. Once a target
 * element has been located the user can iterate over the tags belonging to
 * that element by calling nextTag(). While the pointer is located on any tag,
 * the user can call accessor methods to obtain the name, text and attributes
 * of that tag. A state variable is maintained so that
 * the user can find where they are relative to the target element. For
 * example, the state can be evaluated to see if the user has reached the end
 * of the target element while iterating over the nested tags for that target.
 * This class is used by the InputXMLDataFile class for iteterating over
 * xml elements and converting them to Java objects. The user calls the
 * getIterator(String targetTagName) method on InputXMLDataFile and gets back
 * a DataIterator. The DataIterator has methods calls for hasNext() and next().
 * When the user calls next(), an instance of XMLTagIteraor is returned already
 * positioned at the next target element. The user can then parse this element
 * until reaching the end of the target element as indicated by the state. At
 * the application level it is usually more convient and satisfying for the
 * next() method to return an object already parsed from the xml. This is
 * done by calling getIterator(String targetTagName, XMLDataInterepreter i)
 * on the InputXMLDataFile object. When this call is made, a DataIterator is
 * also returned, but instead of returning the XMLTagIterator to the user on
 * the call to next(), the XMLTagIterator is passed to the provided
 * XMLDataInterpreter which handles the job of parsing the xml element and
 * creating a Java object. This object is subsequently returned to the user
 * on the call to next().
 * @has a XMLStreamReader for reading the xml, a target tag name which names
 * the xml element the user is interested in iterating over and a state which
 * indicates where the position in the XMLStreamReader is relative to a
 * target element.
 * @does iterates over xml tags, providing access to the tag names, text and
 * attributes.
 * @company The Jackson Laboratory</p>
 * @author M Walker
 *
 */

public class XMLTagIterator
{
    /**
     * indicates the xml position is prior to the next target element
     */
    public static final int TAG_PRIOR = 0;
    /**
     * indicates the xml position is within the target element
     */
    public static final int TAG_TARGET = 1;
    /**
     * indicates the xml position is at end of the target element
     */
    public static final int TAG_END = 2;
    /**
     * indicates the xml position is at the end of the xml file
     */
    public static final int TAG_EOF = 3;

    private int state = TAG_PRIOR;
    private int MAX_ATTRIBUTES = 15;

    private String target = null;
    private XMLStreamReader reader = null;

    private String[] attributeNames = new String[MAX_ATTRIBUTES];
    private String[] attributeValues = new String[MAX_ATTRIBUTES];

    private int currentAttributeCount = 0;
    private String currentTag = null;

    // the following are the exceptions that are thrown
    private static final String XMLStreamErr =
        IOUExceptionFactory.XMLStreamErr;
    private static final String XMLStreamCloseErr =
        IOUExceptionFactory.XMLStreamCloseErr;
    private static final String XMLAttributeOutOfRange =
        IOUExceptionFactory.XMLAttributeOutOfRange;

    /**
     * constructor
     * @param target the target tag name to iterate over
     * @param reader the XMLStreamReader for reading the xml
     */
    public XMLTagIterator(String target, XMLStreamReader reader)
    {
        this.target = target;
        this.reader = reader;
    }

    /**
     * get the current position of the XMLStreamReader as indicated by one
     * of 4 states, TAG_PRIOR, TAG_TARGET, TAG_END, TAG_EOF
     * @return the state indicating the position of the XMLStreamReader
     */
    public int getState()
    {
        return this.state;
    }

    /**
     * move the pointer to the next tag
     * @return true if the next tag was reached or false if the end of a
     * target element or the end of the file was reached
     * @assumes nothing
     * @effects the pointer of the XMLStreamReader is changed
     * @throws IOUException thrown if there is an error parsing the xml
     * file
     */
    public boolean nextTag()
    throws IOUException
    {
        try
        {
            if (this.state == TAG_PRIOR)
            {
                findFirstTag();
                if (this.state == TAG_EOF)
                    return false;
                else
                    return true;
            }
            else
            {
                this.state = this.TAG_TARGET;
                if (!reader.hasNext())
                {
                    this.state = TAG_EOF;
                    return false;
                }
                reader.next();
                if (reader.isWhiteSpace())
                    reader.next();
            }
            while (!reader.isStartElement() && reader.hasNext())
            {
                if (reader.isEndElement() &&
                    target.equals(reader.getLocalName()))
                {
                    this.state = TAG_END;
                    this.currentTag = reader.getLocalName();
			// You can't get attributes off of the end element... How did this ever work!???	
                    //this.setAttributes();
                    return false;
                }
                reader.next();
                if (reader.isWhiteSpace())
                    reader.next();
            }
            if (!reader.hasNext())
            {
                this.state = TAG_EOF;
                return false;
            }
            // store tag name and attributes and then move pointer to next
            this.currentTag = reader.getLocalName();
            this.setAttributes();
            reader.next();
        }
        catch (XMLStreamException e)
        {
            IOUExceptionFactory factory = new IOUExceptionFactory();
            IOUException e2 =
                (IOUException)factory.getException(XMLStreamErr, e);
            throw e2;
        }
        return true;
    }

  /**
    * move the pointer to the next target tag
    * @return true if the next tag was reached or false if the end of file
    * was reached
    * @assumes nothing
    * @effects the pointer of the XMLStreamReader is changed
    * @throws IOUException thrown if there is an error parsing the xml
    * file
    */
    public boolean nextTargetTag()
    throws IOUException
    {
        boolean got_one = false;
        while (!got_one)
        {
            got_one = this.nextTag();
            if (this.state == this.TAG_EOF)
                return false;
            if (got_one)
            {
                if (!this.target.equals(this.currentTag))
                    got_one = false;
            }
        }
        return true;
    }

    /**
     * get the name of the current tag the XMLStreamReader is positioned on
     * @return the name of the current tag
     */
    public String getTagName()
    {
        return this.currentTag;
    }

    /**
     * get the count of the set of attributes for the current tag on which the
     * XMLStreamReader is positioned
     * @return count of the current set of attributes
     */
    public int getAttributeCount() {
        return currentAttributeCount;
    }

    /**
     * get the attribute value for the given index of attributes for the
     * current tag the XMLStreamReader is positioned on
     * @param index the index of the attribute, starting at 0
     * @return the attribute value or null if attribute doesn't exist
     * @throws IOUException thrown if the index is out of range
     */
    //public String getAttributeValue(String attributeLocalName)
    public String getAttributeValue(int index)
    throws IOUException
    {
        if (index > this.currentAttributeCount - 1)
        {
            IOUExceptionFactory factory = new IOUExceptionFactory();
            IOUException e =
                (IOUException)factory.getException(XMLAttributeOutOfRange);
            e.bind(index);
            e.bind(this.reader.getLocalName());
            throw e;

        }
        //return reader.getAttributeValue(index);
        return (String)this.attributeValues[index];
    }
    /**
     *
     * @return attribute names
     * @throws IOUException
     */
    public String[] getAttributeNames() {
        return this.attributeNames;
    }
    /**
     * get the name of the current tag the XMLStreamReader is positioned on
     * @return the name of the current tag
     */
    public String getText()
    throws IOUException
    {
        StringBuffer text = new StringBuffer();
        while (reader.getEventType() != XMLStreamConstants.END_ELEMENT)
        {
            text.append(this.reader.getText());
            try
            {
                this.reader.next();
            }
            catch (XMLStreamException e)
            {
                IOUExceptionFactory factory = new IOUExceptionFactory();
                IOUException e2 =
                    (IOUException)factory.getException(XMLStreamErr, e);
                throw e2;
            }

        }
        return text.toString();
    }


    /**
     * close the xml file
     * @assumes nothing
     * @effects the xml file will be closed
     * @throws IOUException thrown if there is an error accessing the xml
     * file
     */
    public void close()
    throws IOUException
    {
        try
        {
            this.reader.close();
        }
        catch (XMLStreamException e)
        {
            IOUExceptionFactory factory = new IOUExceptionFactory();
            IOUException e2 =
                (IOUException)factory.getException(XMLStreamCloseErr, e);
            throw e2;
        }
    }

    private void findFirstTag()
    throws IOUException
    {
        try
        {
            while (reader.hasNext())
            {
                reader.next();
		// skip characters that are outside of XML tags
		if(reader.isCharacters() ) continue;
                if (target.equals(reader.getLocalName()))
                {
                    this.state = this.TAG_TARGET;
                    // store tag name and attributes and then move
		//System.out.println("reader State = "+reader.getEventType());
                    // pointer to next
                    this.currentTag = reader.getLocalName();
                    this.setAttributes();
                    reader.next();
                    break;
                }
            }
            if (!reader.hasNext())
                this.state = TAG_EOF;
        }
        catch (XMLStreamException e)
        {
            IOUExceptionFactory factory = new IOUExceptionFactory();
            IOUException e2 =
                (IOUException)factory.getException(XMLStreamErr, e);
            throw e2;

        }
    }

    private void setAttributes()
    {
        this.currentAttributeCount = reader.getAttributeCount();
        for (int i = 0; i < this.currentAttributeCount; i++)
        {
            // parallel arrays for attribute names/values
            this.attributeNames[i] = reader.getAttributeLocalName(i);
            this.attributeValues[i] = reader.getAttributeValue(i);
        }
    }

}
