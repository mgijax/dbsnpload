package org.jax.mgi.shr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
*	A class of static methods for manipulating Streams.
*	@author Josh Winslow (jw@informatics.jax.org)
*/
public class StreamUtils {


	/**
	* A convenence method for reading from InputStreams.  The InputStream
	* will be emptied of all data and closed upon return.
	* @param is An InputStream to be read.
	* @return A String containing the contents of the Stream.
	* @throws IOException if any problems with the inputstream occur.
	*/
	public static String readStream(InputStream is) throws IOException {
			BufferedReader br;
			StringBuffer strbReturn = new StringBuffer();
			String strTmp;

			br = new BufferedReader(new InputStreamReader(is));

			//Read from the BufferedReader until there is no more data.
			strTmp = br.readLine();
			while(strTmp != null) {
				strbReturn.append(strTmp+'\n');
				strTmp = br.readLine();
			}
			is.close();
			return strbReturn.toString();
	}

	/**
	* A convience method for writing to OutputStreams.  The stream will be
	* closed upon return.
	* @param os The OutputStream to which the data will be written.
	* @param strOutput The string to be written to the OutputStream.
	* @throws IOException if any problems with the outputstream occur.
	*/
	public static void writeStream(OutputStream os,
								   String strOutput)
								   throws IOException {

		OutputStreamWriter osw = new OutputStreamWriter(os);

		osw.write(strOutput);
		osw.flush();
		os.close();
	}
}
