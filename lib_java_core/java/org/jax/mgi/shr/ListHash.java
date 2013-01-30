package org.jax.mgi.shr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
* A ListHash is an Hashtable for which all values are ArrayLists.
* This allows more than one value to be stored per key.
* A LishHash has a set of key(Object)/value(ArrayList) pairs.
* A ListHash provides access methods to the stored key/value pairs.
*/
public class ListHash extends Hashtable {

	/**
	* This method inserts value into the Hash at key.
	* If you want to insert value as an ArrayList into the ArrayList at key
	* without merging the values, make sure to cast it to Object.
	* Inserting a ListHash into another ListHash is NOT supported and will
	* result in undefined behavoir and/or errors.
	* @param key The key to insert value at.
	* @param value The object to be inserted.
	* @return The new value stored at key (an ArrayList).
	*/
	public Object put(Object key, Object value) {

		ArrayList obj = (ArrayList)this.get(key);

		// If there is currently no value for this key
		if(obj==null)
			// Make a new ArrayList
			obj = new ArrayList();
		// Add the new value to the list
		obj.add(value);

		// Insert the ArrayList into the internal structure using the
		// superclass's put.  Using this class's put will cause an
		// infinite loop
		return super.put(key,obj);

	}


	/**
	* This method is a convenence wrapper around
	* put(String, String, String, int, int);
	* @param strList A String containing key/value pairs to be added to
	*				 the Hash.
	* @param strBetweenDelimiter A String containing the value used to
	*							 seperate key/value pairs.
	* @param strWithinDelimiter A String containing the value used to
	*			    seperate the key from the value in each pair.
    */
	public void put(String strList,
			String strBetweenDelimiter,
			String strWithinDelimiter) {

		put(strList,
			strBetweenDelimiter,
			strWithinDelimiter,
			0,
			strList.length());
	}



	/**
	* This method will parse a String, using the specified delimiters,
	* and insert the results into the Hash.  strList is parsed in two
	* passes, the first pass seperates strList into tokens using
	* strBetweenDelimiter.  Each of these tokens should be made up of a
	* key/value pair seperated by strWithinDelimiter.  This pair is then
	* inserted into the hash.
	* Example:
	* strList="query=Kit&selectedQuery=References&selectedQuery=Phenotypes"
	* hash.put(strList,"&","=",0,strList.length())
	* on an empty hash will add the following pairs to hash.
	* query:Kit
	* selectedQuery:References
	* selectedQuery:Phenotypes
	* @param strList A String containing key/value pairs to be added to
	*				 the Hash.
	* @param strBetweenDelimiter A String containing the value used to
	*							 seperate key/value pairs.
	* @param strWithinDelimiter A String containing the value used to
	*							seperate the key from the value in each
	*							pair.
	* @param iStart An int used to designate where in strList to start
	*				parsing.
	* @param iEnd An int used to designate where in strList to stop
	*			  parsing.
	*/
	public void put(String strList,
			String strBetweenDelimiter,
			String strWithinDelimiter,
			int iStart,
			int iEnd) {

		ArrayList alObjects = new ArrayList();
		// Limit the search to the specified region
		String strSearchSpace = strList.substring(iStart,iEnd);
		String strKey,strValue;
		// Start the parsing at the first instance of the between delimiter
		int i = strSearchSpace.indexOf(strBetweenDelimiter),j=0;
		int iTmp;

		// Until we hit the end of the search space
		while(i!=-1) {
			//add to alObjects every name/value pair
			alObjects.add(strSearchSpace.substring(j,i));
			i+=strBetweenDelimiter.length();
			j=i;
			i = strSearchSpace.indexOf(strBetweenDelimiter,i);
		}
		// add the last pair
		alObjects.add(strSearchSpace.substring(j,strSearchSpace.length()));

		// Parse each pair down to the key and value and insert them into
		// the hash.
		for(i=0;i<alObjects.size();i++) {
			strSearchSpace = (String)alObjects.get(i);
			iTmp = strSearchSpace.indexOf(strWithinDelimiter);
			strKey = strSearchSpace.substring(0,iTmp);
			strValue = strSearchSpace.substring(iTmp+
												strWithinDelimiter.length(),
												strSearchSpace.length());
			this.put(strKey,strValue);
		}

	}

	/**
	* This method adds the contents of value to the ArrayList specified
	* by key.
	* If you want to insert value as an ArrayList into the ArrayList at key,
	* rather than combining it's values, use put(Object,Object) (I.E. cast
	* value to an Object).
	* @param key The key to insert value at.
	* @param value The ArrayList to be merged with the ArrayList at key.
	*/
	public void put(Object key, ArrayList value) {

		for(int i=0;i<value.size();i++)
			this.put(key,value.get(i));
	}


	/**
	* This method is identical to calling containsValue()
	* @return A boolean; true if the value is contained in any ArrayList
	* 		  in the Hash, false otherwise.
	* @param value The Object being searched for.
	*/
	public boolean contains(Object value) {

		//get all the values (which are all ArrayLists)
		Collection c = values();

		//and convert the collection to an array of ArrayLists
		//the new ArrayList is just to specify the type, the size has no
		//effect.
		ArrayList[] al = (ArrayList[])c.toArray(new ArrayList[1]);

		//Check all the ArrayLists to see if it's contained anywhere
		for(int i=0;i<al.length;i++)
			if(al[i].contains(value))
				return true;
		//If not found, return false.
		return false;

	}

	/**
	* This method is identical to calling contains()
	* @return A boolean; true if the value is contained in any ArrayList
	*		  in the Hash, false otherwise.
	* @param value The Object being searched for.
	*/
	public boolean containsValue(Object value) {
		return contains(value);
	}


	/**
	* Inserts the entire contents of the ListHash into the internal Hash.
	* @param lh the ListHash to be inserted.
	*/
	public void merge(ListHash lh) {
		// Basically a copy of putAll, but casting the return from the Map
		// to an ArrayList so the correct put is used.

		Set s;	//The set of keys
		Iterator it; //An interator across the set of keys.
		Object key; //a single key.

		// Get the set of keys
		s = lh.keySet();
		it = s.iterator();

		// Loop through them, adding all the key/value pairs to the
		// internal Hash.
		while(it.hasNext()) {
			key = it.next();
			this.put(key,(ArrayList)lh.get(key));
		}

	}

	/**
	* Inserts the entire contents of the Map into the Hash.
	* The results are undefined if you use putAll on a Map containing
	* another ListHash.
	* If the Map is a ListHash, and any are shared between the ListHashes,
	* the ArrayLists will NOT be merged.
	* @param t the Map to be inserted.
	*/
	public void putAll(Map t) {

		Set s; //The set of keys
		Iterator it; //An interator across the set of keys.
		Object key; //a single key.

		// Get the set of keys
		s = t.keySet();
		it = s.iterator();

		// Loop through them, adding all the key/value pairs to the
		// internal Hash.
		while(it.hasNext()) {
			key = it.next();
			this.put(key,t.get(key));
		}
	}

	/**
	* Removes the specified value from the ArrayList specified by key.
	* If there is no object matching key or value, the list is not
	* changed.
	* @param key The key to the ArrayList from which you want to remove
	*		 	 value.
	* @param value The Object you want removed.
	*/
	public void remove(Object key, Object value) {

		ArrayList alVal = (ArrayList)get(key);
		//if the key matches
		if(alVal!=null) {
			int ind = alVal.indexOf(value);
			//and object matches a value in the list
			if(ind!=0) {
				//remove the object and update the list.
				alVal.remove(ind);
				put(key,alVal);
			}
		}
	}
}
