// $Header$
// $Name$

package org.jax.mgi.shr.ioutils;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.jax.mgi.shr.timing.Stopwatch;
import org.jax.mgi.shr.log.Logger;


/**
 * An object that iterates through a file as a collection of
 * records.
 * @has a file and a record delimiter.
 * @does Manages the delimiting of data from an input file into individual
 * records
 * @company Jackson Laboratory
 * @author M. Walker
 */

public class RecordDataReader {

  // max length allocated for one line of text
  private int MAX_LINE_SIZE = 512000;

  // the delimiters used for regular expression matching
  private String beginDelimiter = null;
  private String endDelimiter = null;

  // the byte arrays used for delimiter sequence matching
  private byte[] matchSequenceEnd = null;
  private byte[] matchSequenceBegin = null;

  // decoder for decoding bytes into unicode characters
  private CharsetDecoder decoder = null;

  // end of line character for UNIX and PC
  private int EOL = 10;

  // nio buffer
  private ByteBuffer byteBuffer = null;

  // nio channel
  private ReadableByteChannel channel = null;

  // one of the choice of inner classes determined by the constructor
  private BufferReader bufferReader = null;

  // indicator for whether or not to use a begin delimiter when extracting
  // records
  private boolean okToUseBeginDelimiter;

  // logger for debug
  private Logger logger = null;



  /**
   * constructor which allows controlling the size of the internal buffer and
   * forces regular expression matching
   * @param in the input stream to read
   * @param beginDelimiter the regular expression used for begin delimiter
   * @param endDelimiter the regular expression used for end delimiter
   * @param charset the character set when decoding bytes from the input file
   * @param bufferSize the value to set on the internal buffer size
   * @param logger the Logger to use for debugging
   * @throws IOException thrown if the channel cannot be read from
   */

  public RecordDataReader(ReadableByteChannel in,
                          String beginDelimiter,
                          String endDelimiter,
                          String charset,
                          int bufferSize,
                          Logger logger)
  throws IOException {
    this.channel = in;
    this.beginDelimiter = beginDelimiter;
    this.endDelimiter = endDelimiter;
    this.bufferReader = new RegexReader();
    this.decoder = (Charset.forName(charset)).newDecoder();
    // create nio buffer to specified size
    this.byteBuffer = ByteBuffer.allocate(bufferSize);
    this.logger = logger;
    // clear and prepare the nio buffer for reading
    prepareNewBuffer();
  }

    /**
     * constructor which allows controlling the size of the internal buffer and
     * providing the begin and end delimiters as byte arrays
     * @param in the input stream to read
     * @param matchSequenceBegin the byte array used for begin delimiter
     * @param matchSequenceEnd the byte array used for end delimiter
     * @param charset the character set when decoding bytes from the input file
     * @param bufferSize the value to set on the internal buffer size
     * @param logger the Logger to use for debugging
     * @throws IOException thrown if there is an error accessing the file
     */

    public RecordDataReader(ReadableByteChannel in,
                            byte[] matchSequenceBegin,
                            byte[] matchSequenceEnd,
                            String charset,
                            int bufferSize,
                            Logger logger)
    throws IOException {
      this.channel = in;
      this.matchSequenceBegin = matchSequenceBegin;
      this.matchSequenceEnd = matchSequenceEnd;
      this.bufferReader = new ByteReader();
      this.decoder = (Charset.forName(charset)).newDecoder();
      // create nio buffer to specified size
      this.byteBuffer = ByteBuffer.allocate(bufferSize);
      this.logger = logger;
      // clear and prepare the nio buffer for reading
      prepareNewBuffer();
    }


  /**
   * designates whether more records are available for reading
   * @return true or false
   */
  public boolean hasNext() {
    return byteBuffer.hasRemaining();
  }

  /**
   * returns the next record from the file
   * @return the record
   * @throws IOException
   */
  public String next() throws IOException {
    return bufferReader.getRecord();
  }

  /**
   * close the channel associated with the input file
   * @throws IOException
   */
  public void closeResources() throws IOException {
    byteBuffer = null;
    if (channel != null)
      channel.close();
    bufferReader.close();
  }


  /**
   * clears the existing buffer and prepares for another read from the channel
   * @throws IOException thrown if the channel cannot be read from
   */
  private void prepareNewBuffer() throws IOException {
      //Stopwatch watch = new Stopwatch();
      //watch.start();

    byteBuffer.clear();
    channel.read(byteBuffer);
    // prepare the buffer for reading
    byteBuffer.flip();
    bufferReader.initBuffer();

      //watch.stop();
      //logger.logDebug("prepareNewBuffer : " + watch.time());
  }


  /**
   * gets a new channel from the input stream and prepares a buffer for
   * reading
   * @param in input stream
   * @param bufferSize the value to set for the buffer size
   * @throws IOException
   */
  private void setup(FileInputStream in, int bufferSize)
  throws IOException {

    // get nio channel from stream
    channel = in.getChannel();
    // create nio buffer to specified size
    byteBuffer = ByteBuffer.allocate(bufferSize);
    // clear and prepare the nio buffer for reading
    prepareNewBuffer();
  }

  /**
   * an interface defining the BufferReader class implemented by
   * the two inner classes, RegexReader and ByteReader
   */
  private interface BufferReader {
    /*
     * perform any initialitaion
     * @asumes nothing
     * @effects implementation specific
     */
    public void initBuffer();
    /**
     * get the next record from the byte buffer
     * @return the next record as a String
     * @throws IOException thrown if there is an error reading from the
     * byte buffer
     */
    public String getRecord() throws IOException;
    /**
     * perform any final functions
     * @assumes nothing
     * @effects nothing
     */
    public void close();
  }

  /**
   *  implements the BufferReader interface and reads the byte buffer
   * byte by byte and finds the record seperator by a direct comparison to
   * the given byte sequence. This class provides greater performance over
   * the RegexReader
   * @has a match sequence and a byte buffer
   * @does reads a record from the byte buffer
   */
  private class ByteReader
      implements BufferReader
  {
      // a strategy dependent on values of the begin/end delimiters
      DelimiterStrategy delimiterStrategy = null;

      private CharBuffer charBuffer = null;

      // indicator of whether or not the first record is being read
      private boolean firstRecord = true;

      public ByteReader() throws IOException
      {
          if (matchSequenceBegin == null && matchSequenceEnd == null)
              throw new IOException("Both end/begin delimiters cannot be null");
          if (matchSequenceBegin == null)
              okToUseBeginDelimiter = false;
          else
              okToUseBeginDelimiter = true;
      }

      /**
       * required for the interface but nothing is performed for ByteReader
       */
      public void initBuffer()
      {}


      public class BeginEndStrategy implements DelimiterStrategy
      {
          public String getRecord() throws IOException
          {
              // the record to be built
              String record = null;

              /**
               * the match sequence index pointers which increment on each
               * consecutive byte match of a match sequence. When an index reaches
               * the length of the match sequence, the match is complete. When there is
               * a byte mismatch before an index reaches the length of the sequence,
               * the index is reset to zero
               */
              int beginDelIndex = 0;
              int endDelIndex = 0;

              /**
               * Care must be taken when the end of the byte buffer is reached and
               * new data must be read into the buffer.
               * The following constants define three states this method can be in
               * when a buffer runs out of characters and requires refilling:
               * still looking for start delimiter, looking for end delimiter,
               * currently evaluating the start delimiter (that is, byte buffer
               * characters are matching the start delimiter, but we havent reached
               * the end of the start delimiter yet), and the end delimiter was
               * found.
               */
              final int LOOKING_BEGINDEL = 1;
              final int LOOKING_ENDDEL = 2;
              final int EVALUATING_BEGINDEL = 3;
              final int FOUND_ENDDEL = 4;

              /**
               * indicator for whether or no a begin delimiter was partially
               * read when the internal buffer reaches the end
               */
              boolean partialBeginDel = false;

              int state = LOOKING_BEGINDEL;
              byte b = 0;
              while (state != FOUND_ENDDEL && byteBuffer.hasRemaining())
              {
                  int startPosition = byteBuffer.position();
                  if (okToUseBeginDelimiter &&
                      (state == LOOKING_BEGINDEL || state == EVALUATING_BEGINDEL))
                  {
                      // read byte by byte from buffer comparing each byte to the
                      // begin match sequence until the beginning of record is
                      // found.
                      // If the buffer needs refilling from the channel, then stop
                      // parsing and refill the buffer
                      while (byteBuffer.hasRemaining())
                      {
                          b = byteBuffer.get();
                          // are the incoming bytes still matching the match
                          // sequence?
                          if (b == matchSequenceBegin[beginDelIndex])
                          {
                              beginDelIndex++; // increment the match sequence
                              state = EVALUATING_BEGINDEL;
                          }
                          else // no longer matching
                          {
                              beginDelIndex = 0; // reset the match sequence
                              state = LOOKING_BEGINDEL;
                              if (partialBeginDel) // clear the saved partial
                              {
                                  record = null;
                                  partialBeginDel = false;
                              }
                          }
                          if (beginDelIndex == matchSequenceBegin.length)
                          { // we reached the end of the match sequence for the
                            // begin delimiter
                              startPosition = byteBuffer.position() - beginDelIndex;
                              if (startPosition < 0) // buffer was refilled
                                  startPosition = 0;
                              state = LOOKING_ENDDEL;
                              beginDelIndex = 0; // reset the match sequence
                              break;
                          }
                      }
                      /**
                       * if we are in the middle of evaluating a begin sequence
                       * when the buffer runs out, then we need to set the
                       * start position to the beginning of the begin delimiter
                       */
                      if (state == EVALUATING_BEGINDEL)
                      {
                          startPosition = byteBuffer.position() - beginDelIndex;
                          if (startPosition < 0)
                              startPosition = 0;
                      }
                  }
                  else // not using a begin delimiter
                  {
                      state = LOOKING_ENDDEL;
                      startPosition = byteBuffer.position();
                  }
                  /**
                   * read byte by byte from buffer comparing each byte to the match
                   * sequence until the end of record delimiter is found.
                   * If the buffer needs refilling from the channel, then store the
                   * current characters for the current line and record in a cache
                   * before refilling the buffer and later append to them when
                   * processing the new buffer.
                   */
                  while (byteBuffer.hasRemaining())
                  {
                      b = byteBuffer.get();
                      // are the incoming bytes still matching the match sequence?
                      if (b == matchSequenceEnd[endDelIndex])
                          endDelIndex++; // increment the match sequence
                      else
                          endDelIndex = 0; // reset the match sequence
                      if (endDelIndex == matchSequenceEnd.length)
                      { // we reached the end of the match sequence for the
                        // end delimiter
                          state = FOUND_ENDDEL;
                          endDelIndex = 0; // reset the match sequence
                          break;
                      }
                  }
                  /**
                   * to get here indicates either end of buffer or end of record or
                   * both. What happens at this point depends on the state the
                   * process is in. If it is currently searching for the begin
                   * delimiter, then just refill the buffer without saving any
                   * record data. If the state is currently searching for the end
                   * delimiter, then store the buffer characters currently read for
                   * this record into storage. This storage can then be used to
                   * append charcters from subsequent buffers. If the state is
                   * currently in the process of evaluating the start buffer, then
                   * store the start delimiter characters read so far into storage
                   * (this storage be set to null if the start delimiter is not
                   * found afterall). If we are actually at the end of the file,
                   * then the current record will be returned to the caller
                   * regardless of whether or not the end of record delimiter was
                   * reached.
                   */
                  if (state != LOOKING_BEGINDEL)
                  {
                      charBuffer = CharBuffer.allocate(byteBuffer.position() -
                          startPosition);
                      int currentPosition = byteBuffer.position();
                      byteBuffer.position(startPosition);
                      //Stopwatch watch = new Stopwatch();
                      //watch.start();
                      decoder.decode(byteBuffer, charBuffer, true);
                      //watch.stop();
                      //logger.logDebug("decoder.decode() : " + watch.time());
                      byteBuffer.position(currentPosition);
                      /**
                       * set or append the current record instance variable.
                       * partial records could have been created if a previous
                       * buffer filled up and we had to temporarily store a partial
                       * record and refill the buffer.
                       */
                      if (record == null)
                          record =
                              ((CharBuffer) charBuffer.position(0)).toString();
                      else
                          record =
                              record.concat(
                              ((CharBuffer) charBuffer.position(0)).toString());
                        /**
                         * if the state is currently evaluating a begin delimiter
                         * then the partial record being cached contains the
                         * partial begin delimiter read so far. This will have to
                         * cleared if it turns out that the begin delimiter does
                         * not match afterall
                         */
                      if (state == EVALUATING_BEGINDEL)
                          partialBeginDel = true;
                  }
                  if (!byteBuffer.hasRemaining()) // buffer needs to be refilled
                      prepareNewBuffer();
              }
              return record;

          }
      }

      public class BeginOnlyStrategy implements DelimiterStrategy
      {
          public String getRecord() throws IOException
          {

              // the record to be built
              StringBuffer record = new StringBuffer();

              // the index of the matching begin char sequence
              // this is incremented with each match from the incoming bytes
              int beginDelIndex = 0;

              /**
               * Care must be taken when the end of the byte buffer is reached
               * and new data must be read into the buffer.
               * The following constants define these states the methods can be
               * in when a buffer runs out of characters and requires refilling:
               * still looking for start delimiter, looking for end delimiter,
               * currently evaluating the start delimiter (that is, byte buffer
               * characters are matching the start delimiter, but we havent
               * reached the end of the start delimiter yet), currently
               * evaluating the end delimiter, and the end delimiter was found.
               * Note that the end delimiter is actually the begin delimiter
               *
               */
              final int LOOKING_BEGINDEL = 1;
              final int EVALUATING_BEGINDEL = 2;
              final int LOOKING_ENDDEL = 3;
              final int EVALUATING_ENDDEL = 4;
              final int FOUND_ENDDEL = 5;

              /**
               * indicator for whether or no a begin delimiter was partially
               * read when the internal buffer reaches the end
               */
              boolean partialBeginDel = false;

              int state = LOOKING_BEGINDEL;
              if (!firstRecord)
                  state = LOOKING_ENDDEL;
              byte b = 0;
              // search out begin delimiter
              while (state != FOUND_ENDDEL && byteBuffer.hasRemaining())
              {
                  int startPos = byteBuffer.position();
                  int endPos = byteBuffer.position();
                  // read byte by byte from buffer comparing each byte to the
                  // begin match sequence until the beginning of record is
                  // found.
                  // If the buffer needs refilling from the channel, then stop
                  // parsing and refill the buffer
                  while ((state == LOOKING_BEGINDEL ||
                          state == EVALUATING_BEGINDEL) &&
                         byteBuffer.hasRemaining())
                  {
                      b = byteBuffer.get();
                      endPos = byteBuffer.position();
                      // are the incoming bytes still matching the match
                      // sequence?
                      if (b == matchSequenceBegin[beginDelIndex])
                      {
                          beginDelIndex++; // increment the match sequence
                          if (state == LOOKING_BEGINDEL) // first match byte
                          {
                              state = EVALUATING_BEGINDEL;
                              startPos = byteBuffer.position() - beginDelIndex;
                              if (startPos < 0)
                                  // caused if buffer was refilled while
                                  // evaluating the begin del char sequence
                                  startPos = 0;
                          }
                      }
                      else if (state == EVALUATING_BEGINDEL)
                      {
                          // no longer matching the begin delimiter
                          beginDelIndex = 0; // reset the match sequence
                          state = LOOKING_BEGINDEL;
                          if (partialBeginDel) // clear the saved partial
                          {
                              // clear any data that was stored during
                              // a buffer refill while evaluating a
                              // potential begin delimiter char sequence
                              record = new StringBuffer();
                              partialBeginDel = false;
                          }
                      }
                      if (beginDelIndex == matchSequenceBegin.length)
                      {
                          // found begin delimiter
                          state = LOOKING_ENDDEL;
                          partialBeginDel = false;
                          beginDelIndex = 0; // reset the match sequence
                          break;
                      }
                  } // end while looking for start of record

                  /**
                   * read byte by byte from buffer comparing each byte to the
                   * match sequence until the next begin delimiter is found.
                   * If the buffer needs refilling from the channel, then store
                   * the characters for the current record in a cache
                   * before refilling the buffer and later append to them when
                   * processing the new buffer.
                   */
                  while ((state == LOOKING_ENDDEL ||
                          state == EVALUATING_ENDDEL)
                         && byteBuffer.hasRemaining())
                  {
                      b = byteBuffer.get();
                      endPos = byteBuffer.position();
                      // are the incoming bytes still matching the sequence?
                      if (b == matchSequenceBegin[beginDelIndex])
                      {
                          beginDelIndex++; // increment the match sequence idx
                          if (state == LOOKING_ENDDEL) // first match byte
                          {
                              state = EVALUATING_ENDDEL;
                          }
                      }
                      else if (state == EVALUATING_ENDDEL)
                      {
                          beginDelIndex = 0; // reset the match sequence idx
                      }
                      if (beginDelIndex == matchSequenceBegin.length)
                      { // we reached the end of the match sequence for the
                        // next record's begin delimiter
                          state = FOUND_ENDDEL;
                          beginDelIndex = 0; // reset the match sequence
                          break;
                      }
                  }
                  /**
                   * to get here indicates either end of buffer or end of
                   * record or both. What happens at this point depends on the
                   * state the process is in. If it is currently searching for
                   * the start of the record, then just refill the buffer
                   * without saving any record data. If the state is currently
                   * searching for the end of record, then store the buffer
                   * characters currently read for this record into storage.
                   * This storage can then be used to append charcters from
                   * subsequent buffers. If the state is currently in the
                   * process of evaluating the begin delimiter, then
                   * store the start delimiter characters read so far into
                   * storage (it is set to null if the start delimiter is not
                   * found afterall). If we are actually at the end of the file,
                   * and the system is currently looking for end of record,
                   * then the current record will be returned to the caller
                   * regardless of whether or not the end of record delimiter
                   * was reached.
                   */
                  if (state != LOOKING_BEGINDEL)
                  {
                      charBuffer = CharBuffer.allocate(endPos - startPos);
                      int currentPosition = endPos; // begin of next rec
                      byteBuffer.position(startPos);
                      decoder.decode(byteBuffer, charBuffer, true);
                      byteBuffer.position(currentPosition);
                      /**
                       * set or append the current record instance variable.
                       * partial records could have been created if a previous
                       * buffer filled up and we had to temporarily store a partial
                       * record and refill the buffer.
                       */
                      record.append((CharBuffer) charBuffer.position(0));
                        /**
                         * if the state is currently evaluating a begin delimiter
                         * then the partial record being cached contains the
                         * partial begin delimiter read so far. This will have to
                         * cleared if it turns out that the begin delimiter does
                         * not match afterall
                         */
                      if (state == EVALUATING_BEGINDEL)
                          partialBeginDel = true;
                  }
                  if (!byteBuffer.hasRemaining()) // buffer needs to be refilled
                      prepareNewBuffer();
              }
              if (state == FOUND_ENDDEL)
              {
                  // lop off begin delimiter for next record
                  int size = record.length();
                  record =
                      record.delete(size - matchSequenceBegin.length, size);
              }
              String rtnValue = null;
              if (!firstRecord)
              {
                  rtnValue =
                      new String(matchSequenceBegin) + new String(record);
              }
              else
              {
                  firstRecord = false;
                  rtnValue = new String(record);
              }
              return rtnValue;

          }
      }

      public class EndOnlyStrategy implements DelimiterStrategy
      {
          public String getRecord() throws IOException
          {
              // the record to be built
              String record = null;

              // the index of the matching begin char sequence
              // this is incremented with each match from the incoming bytes
              int endDelIndex = 0;

              /**
               * Care must be taken when the end of the byte buffer is reached and
               * new data must be read into the buffer.
               * The following constants define three states this method can be in
               * when a buffer runs out of characters and requires refilling:
               * still looking for start delimiter, looking for end delimiter,
               * currently evaluating the start delimiter (that is, byte buffer
               * characters are matching the start delimiter, but we havent reached
               * the end of the start delimiter yet), and the end delimiter was
               * found.
               */
              final int LOOKING_ENDDEL = 1;
              final int FOUND_ENDDEL = 2;

              /**
               * indicator for whether or no a begin delimiter was partially
               * read when the internal buffer reaches the end
               */
              boolean partialBeginDel = false;

              int state = LOOKING_ENDDEL;
              byte b = 0;
              while (state != FOUND_ENDDEL && byteBuffer.hasRemaining())
              {
                  int startPosition = byteBuffer.position();
                  state = LOOKING_ENDDEL;
                  startPosition = byteBuffer.position();
                  /**
                   * read byte by byte from buffer comparing each byte to the match
                   * sequence until the end of record delimiter is found.
                   * If the buffer needs refilling from the channel, then store the
                   * current characters for the current line and record in a cache
                   * before refilling the buffer and later append to them when
                   * processing the new buffer.
                   */
                  while (byteBuffer.hasRemaining())
                  {
                      b = byteBuffer.get();
                      // are the incoming bytes still matching the match sequence?
                      if (b == matchSequenceEnd[endDelIndex])
                          endDelIndex++; // increment the match sequence
                      else
                          endDelIndex = 0; // reset the match sequence
                      if (endDelIndex == matchSequenceEnd.length)
                      { // we reached the end of the match sequence for the
                        // end delimiter
                          state = FOUND_ENDDEL;
                          endDelIndex = 0; // reset the match sequence
                          break;
                      }
                  }
                  /**
                   * to get here indicates either end of buffer or end of record or
                   * both. Store the buffer characters currently read for
                   * this record into storage. This storage can then be used to
                   * append charcters from subsequent buffers. If we are
                   * actually at the end of the file,
                   * then the current record will be returned to the caller
                   * regardless of whether or not the end of record delimiter was
                   * reached.
                   */

                      charBuffer = CharBuffer.allocate(byteBuffer.position() -
                          startPosition);
                      int currentPosition = byteBuffer.position();
                      byteBuffer.position(startPosition);
                      decoder.decode(byteBuffer, charBuffer, true);
                      byteBuffer.position(currentPosition);
                      /**
                       * set or append the current record instance variable.
                       * partial records could have been created if a previous
                       * buffer filled up and we had to temporarily store a partial
                       * record and refill the buffer.
                       */
                      if (record == null)
                          record =
                              ((CharBuffer) charBuffer.position(0)).toString();
                      else
                          record =
                              record.concat(
                              ((CharBuffer) charBuffer.position(0)).toString());

                  if (!byteBuffer.hasRemaining()) // buffer needs to be refilled
                      prepareNewBuffer();
              }
              return record;

          }
      }



      /**
       * returns the next record from the byte buffer
       * @return the record as a String
       * @throws IOException thrown if there is an eroor reading from the byte
       * buffer
       */
      public String getRecord()
          throws IOException
      {
          if (matchSequenceBegin == null)
              delimiterStrategy = new EndOnlyStrategy();
          else if (matchSequenceEnd == null)
              delimiterStrategy = new BeginOnlyStrategy();
          else // both delimiters are used
              delimiterStrategy = new BeginEndStrategy();
          return delimiterStrategy.getRecord();
      }

      public void close()
      {}
  }  // end ByteReader class

  /**
   *  implements the BufferReader interface and provides an interpretation
   * of the byte buffer as characters and uses regular expressions as record
   * delimiters
   * @has a character view of the byte buffer and a regular expression for
   * the begin and end delimiters
   * @does reads records from the byte buffer delimited by the designated
   * begin and end delimiters.
   */
  private class RegexReader implements BufferReader {

      // a strategy dependent on values of the begin/end delimiters
      DelimiterStrategy delimiterStrategy = null;

      // the current record being read (returned on call to next())
      private String currentRecord = null;

      // the current line being read.
      // the record delimiter is tested on a line by line basis
      private CharSequence currentLine = null;

      private Pattern regexEndDel= null;
      private Pattern regexBeginDel = null;

      private Matcher matcher = null;

      // another view of the nio buffer as the current line being read.
      private ByteBuffer lineBuffer = null;

      // line containning begin delimiter
      private String firstLine = new String();

      // indicator of whether or not this is the first match found
      private boolean firstMatch = true;

      /**
       * constructor
       * @throws IOException thrown if there is an io error
       */
      public RegexReader() throws IOException
      {
          if (beginDelimiter == null && endDelimiter == null)
              throw new IOException("Both end and begin delimiters cannot be null");
          if (beginDelimiter == null)
              okToUseBeginDelimiter = false;
          else
              okToUseBeginDelimiter = true;

          if (endDelimiter != null)
              regexEndDel = Pattern.compile(endDelimiter);
          if (beginDelimiter != null)
              regexBeginDel = Pattern.compile(beginDelimiter);
      }


      public class BeginOnlyStrategy implements DelimiterStrategy
      {
          public String getRecord() throws IOException
          {
              // start a new record
              currentRecord = firstLine;

              /**
               * The following states have been designed to facilitate the handling
               * of buffer refilling. The buffer size limits the amount of data that
               * can be brought into memory at one time. Once the end of buffer is
               * reached, special handling must be done to cache current results and
               * refill the buffer
               */
              final int LOOKING_DEL = 1; // searching start of record
              final int FOUND_DEL = 2; // found end of record

              int state = LOOKING_DEL;

              byte b = 0;

              while (state == LOOKING_DEL && byteBuffer.hasRemaining()) {

                  /**
                   * read byte by byte from buffer until the end of line (EOL)
                   * character is reached, at which time a check for a match of
                   * the line delimiter as a regular expression is made.
                   * If the buffer needs refilling from the channel, then store
                   * the characters for the current line in a cache before
                   * refilling the buffer and later append to them when
                   * processing the new buffer.
                   */
                while (byteBuffer.hasRemaining()) {
                  b = byteBuffer.get();
                  if (b == EOL) {
                      readCurrentLine();
                      // found end of line ...
                      // check to see if it contains the delimiter
                      matcher = regexBeginDel.matcher(currentLine);
                      if (matcher.find())
                      {
                          firstLine = currentLine.toString();
                          if (!firstMatch)
                          {
                              state = FOUND_DEL;
                              currentLine = null;
                              break;
                          }
                          else
                              firstMatch = false;
                          state = LOOKING_DEL;
                      }
                      currentRecord =
                          currentRecord.concat((currentLine.toString()));
                      currentLine = null;
                  }  // end while (byteBuffer.hasRemaining())
                }
                if (!byteBuffer.hasRemaining())
                { // buffer needs to be refilled
                  if (state == LOOKING_DEL)
                      // store partial line before refilling buffer
                      readCurrentLine();
                  prepareNewBuffer();
                }
              }
              if (state == LOOKING_DEL)
              {
                  /**
                   * we reached end of buffer without finding the delimiter.
                   * add the current line to the record before returning
                   */
                  currentRecord =
                          currentRecord.concat((currentLine.toString()));
              }
              return currentRecord;

          }
      }

      public class BeginEndStrategy implements DelimiterStrategy
      {
          public String getRecord() throws IOException
          {

              // start a new record
              currentRecord = null;

              /**
               * The following states have been designed to facilitate the
               * handling of buffer refilling. The buffer size limits the
               * amount of data that can be brought into memory at one time.
               * Once the end of buffer is reached, special handling must be
               * done to cache current results and refill the buffer
               */
              final int LOOKING_BEGINDEL = 1; // searching start of record
              final int LOOKING_ENDDEL = 2; // searching end of record
              final int FOUND_ENDDEL = 3; // found end of record

              int state = LOOKING_BEGINDEL;

              if (!okToUseBeginDelimiter)
              {
                  state = LOOKING_ENDDEL;
              }

              byte b = 0;

              while (state != FOUND_ENDDEL && byteBuffer.hasRemaining()) {

                  /**
                   * read byte by byte from buffer until the end of line (EOL)
                   * character is reached, at which time a check for a match of
                   * the line delimiter as a regular expression is made.
                   * If the buffer needs refilling from the channel, then store the
                   * characters for the current line in a cache before
                   * refilling the buffer and later append to them when processing the
                   * new buffer.
                   */
                while (byteBuffer.hasRemaining()) {
                  b = byteBuffer.get();
                  if (b == EOL) {
                    readCurrentLine();
                    // found end of line - check to see if it contains the delimiter
                    if (state == LOOKING_BEGINDEL)
                    {
                        matcher = regexBeginDel.matcher(currentLine);
                        if (matcher.find())
                        {
                            state = LOOKING_ENDDEL;
                        }

                    }
                    else // (state == LOOKING_ENDDEL)
                    {
                        matcher = regexEndDel.matcher(currentLine);
                        if (matcher.find())
                        {
                            state = FOUND_ENDDEL;
                        }
                    }
                    if (state != LOOKING_BEGINDEL)
                    {
                        if (currentRecord == null)
                          currentRecord = currentLine.toString();
                        else
                          currentRecord =
                              currentRecord.concat((currentLine.toString()));
                    }
                    currentLine = null;
                    if (state == FOUND_ENDDEL)
                        break;
                  }  // end while (byteBuffer.hasRemaining())
                }
                if (!byteBuffer.hasRemaining()) { // buffer needs to be refilled
                  if (state != FOUND_ENDDEL)
                    readCurrentLine(); // store partial line before refilling buffer
                  prepareNewBuffer();
                }
              }
              // getting here indicates end of record.
              // this could be null if the end delimiter was never reached
              if (state == LOOKING_ENDDEL && currentRecord == null) {
                  if (currentLine == null)
                      throw new IOException("Attempting to read past end of file");
                  else
                      return currentLine.toString();
              }
              return currentRecord;

          }
      }

      public class EndOnlyStrategy implements DelimiterStrategy
      {
          public String getRecord() throws IOException
          {
              // start a new record
              currentRecord = null;

              /**
               * The following states have been designed to facilitate the handling
               * of buffer refilling. The buffer size limits the amount of data that
               * can be brought into memory at one time. Once the end of buffer is
               * reached, special handling must be done to cache current results and
               * refill the buffer
               */
              final int LOOKING_ENDDEL = 1; // searching end of record
              final int FOUND_ENDDEL = 2; // found end of record

              int state = LOOKING_ENDDEL;

              byte b = 0;

              while (state != FOUND_ENDDEL && byteBuffer.hasRemaining()) {

                  /**
                   * read byte by byte from buffer until the end of line (EOL)
                   * character is reached, at which time a check for a match of
                   * the end delimiter is made.
                   * If the buffer needs refilling from the channel, then store
                   * the characters for the current line in a cache before
                   * refilling the buffer and later append to them when
                   * processing the new buffer.
                   */
                while (byteBuffer.hasRemaining()) {
                  b = byteBuffer.get();
                  if (b == EOL) {
                    readCurrentLine();
                    // found end of line - check to see if it contains the delimiter
                    matcher = regexEndDel.matcher(currentLine);
                    if (matcher.find())
                    {
                        state = FOUND_ENDDEL;
                    }
                    if (currentRecord == null)
                        currentRecord = currentLine.toString();
                    else
                        currentRecord =
                              currentRecord.concat((currentLine.toString()));
                    currentLine = null;
                    if (state == FOUND_ENDDEL)
                        break;
                  }  // end while (byteBuffer.hasRemaining())
                }
                if (!byteBuffer.hasRemaining()) { // buffer needs to be refilled
                  if (state != FOUND_ENDDEL)
                    readCurrentLine(); // store partial line before refilling buffer
                  prepareNewBuffer();
                }
              }
              // getting here indicates end of record.
              // this could be null if the end delimiter was never reached
              if (state == LOOKING_ENDDEL && currentRecord == null) {
                  if (currentLine == null)
                      throw new IOException("Attempting to read past end of file");
                  else
                      return currentLine.toString();
              }
              return currentRecord;

          }
      }




    public void initBuffer() {
      // create a buffer view for the current line.
      // the buffer position of this buffer is independent of the byte buffer,
      // so it remains static as we read from the byte buffer.
      // when we want to refer to a line we can refer to the
      // characters between the start position of the one of line buffer
      // and the current position of the byte buffer.
      lineBuffer = byteBuffer.duplicate();
    }

    /**
     * returns the next record from the buffer
     * @return the record
     * @throws IOException
     */
    public String getRecord() throws IOException {
        if (beginDelimiter == null)
            delimiterStrategy = new EndOnlyStrategy();
        else if (endDelimiter == null)
            delimiterStrategy = new BeginOnlyStrategy();
        else // both delimiters are used
            delimiterStrategy = new BeginEndStrategy();
        return delimiterStrategy.getRecord();
    }

    public void close() {}

    private void readCurrentLine() throws IOException {
      // create a CharBuffer and decode the line from the raw bytes.
      // the line consist of characters between the line buffer position
      // which was created at the start of the line and the current byte
      // buffer position which is currently at the end of the line.
      //Stopwatch watch = new Stopwatch();
      //watch.start();

      CharBuffer charBuffer = CharBuffer.allocate(
          byteBuffer.position() - lineBuffer.position());
      decoder.decode(lineBuffer, charBuffer, true);
      // append to any strings from previous buffers
      if (currentLine == null)
        currentLine = (CharBuffer)charBuffer.position(0);
      else // join buffers together as strings
        currentLine = currentLine.toString().concat(
            ( (CharBuffer) charBuffer.position(0)).toString());

            //watch.stop();
            //logger.logDebug("decoder.decode() : " + watch.time());


    }
  } // end RegexReader class

  /**
   * A strategy for finding records based on the combination of a begin and
   * end delimiter.
   * @has nothing
   * @does provides the getRecord() which obtains the next record from an input
   * file.
   * @company The Jackson Laboratory
   * @author M Walker
   *
   */
  public interface DelimiterStrategy
  {
      public String getRecord() throws IOException;
  }

}
// $Log$
// Revision 1.10  2004/10/12 20:46:21  mbw
// removed debug statement
//
// Revision 1.9  2004/10/12 15:42:21  mbw
// removed debug statements from the RecordDataReader class
//
// Revision 1.8  2004/07/21 20:47:20  mbw
// made some bug fixes to the 'begin delimiter only' strategy and made some javadocs edits
//
// Revision 1.7  2004/05/24 16:13:17  mbw
// now accepts charset as a constructor parameter
//
// Revision 1.6  2004/03/29 20:00:11  mbw
// now allowing any combination of begin/end delimiters
//
// Revision 1.5  2004/03/04 18:40:38  mbw
// handling the end of file conditions in the RegexReader
//
// Revision 1.4  2004/03/04 15:50:36  mbw
// incorporated use of begin and end delimiters with regular expressions
//
// Revision 1.3  2004/03/01 19:41:31  mbw
// begun working begin/end delimiter using regex functionality
//
// Revision 1.2  2004/02/10 16:22:01  mbw
// added use of a begin delimiter for extracting records
//
// Revision 1.1  2003/12/30 16:56:38  mbw
// imported into this product
//
// Revision 1.6  2003/11/04 15:53:14  mbw
// added new functionality for turning on/off use of regular expression matching and for optionally obtaining input through standard in
//
// Revision 1.5  2003/10/27 18:05:21  mbw
// now accepting delimiters of type byte[] to bypass regular expression functionality
//
// Revision 1.4  2003/06/04 15:06:35  mbw
// javadoc edits
//
// Revision 1.3  2003/05/20 15:21:07  mbw
// merged code from branch lib_java_ioutils-1-0-5-jsam
//
// Revision 1.2.2.8  2003/05/16 15:10:39  mbw
// fixed javadocs to be in sync with code
//
// Revision 1.2.2.7  2003/04/29 17:26:02  mbw
// added changes from code reviews
//
// Revision 1.2.2.6  2003/04/15 11:58:10  mbw
// javadoc edits
//
// Revision 1.2.2.5  2003/04/10 17:17:11  mbw
// removed class from import
//
// Revision 1.2.2.4  2003/03/21 16:12:30  mbw
// added standard header/footer
//
/**************************************************************************
*
* Warranty Disclaimer and Copyright Notice
*
*  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
*  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
*  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
*  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
*  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
*  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
*
*  This software and data are provided to enhance knowledge and encourage
*  progress in the scientific community and are to be used only for research
*  and educational purposes.  Any reproduction or use for commercial purpose
*  is prohibited without the prior express written permission of The Jackson
*  Laboratory.
*
* Copyright \251 1996, 1999, 2002 by The Jackson Laboratory
*
* All Rights Reserved
*
**************************************************************************/
