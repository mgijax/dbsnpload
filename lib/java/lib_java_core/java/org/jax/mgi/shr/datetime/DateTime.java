package org.jax.mgi.shr.datetime;

import java.util.*;
import java.text.*;

public class DateTime
{
        /* Concept:
                collection of cass methods for easy acces to date/time stuff
        */
   
   public static String getCurrentDateTime ()
   {
      // Purpose: get current time/date in the following format
      //     month/day/year hour:min:sec:am/pm marker

      SimpleDateFormat dateFormatter = new SimpleDateFormat( 
	"MM/dd/yyyy hh:mm:ssa" );
      c = Calendar.getInstance ();
      return ( dateFormatter.format( c.getTime() ));
   }

   public static String getCurrentDate ()
   {
      // Purpose: get current time/date in MM/dd/yyyy format

      SimpleDateFormat dateFormatter = new SimpleDateFormat( "MM/dd/yyyy" );
      c = Calendar.getInstance ();
      return ( dateFormatter.format( c.getTime() ));
   }
   public static String getCurrentDateForFileExt ()
   {
      // Purpose: get current time/date in dotted string format for file naming
      // this is a dotted string in the following format
      //      hour in day1-24.minute.second.month.day.year
      // e.g. 14.09.30.03.25.2002 is the stamp created for 2:09:30 pm on March
      //      25, 2002
      SimpleDateFormat dateFormatter = new SimpleDateFormat(
	"kk.mm.ss.MM.dd.yy");
      c = Calendar.getInstance ();
      return ( dateFormatter.format( c.getTime() ));	
   }
	
   // Calendar object shared by all methods 
   public static Calendar c; 
}

