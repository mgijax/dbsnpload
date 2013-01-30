package org.jax.mgi.shr;

import java.util.*;

/**
 *  A class that provides static methods for set operations
 * @has
 *   <UL>
 *   <LI>A TwoTuple class representing one member of the set created by the
 *      cross-product of two sets
 *   <LI>static set methods
 *   </UL>
 * @does
 *   <UL>
 *   <LI>Provides static methods for set operations
 *   </UL>
 * @company The Jackson Laboratory
 * @author jer
 */

public class Sets {

    /**
     *  an object that bundles a pair of Objects and can compare
     *     this TwoTuple to another TwoTuple for equality
     * @has
     *   <UL>
     *   <LI>Two Objects
     *   <LI>
     *   </UL>
     * @does
     *   <UL>
     *   <LI>Compares this TwoTuple to another for equality
     *   <LI>gets either member of the the TwoTuple
     *   </UL>
     * @company The Jackson Laboratory
     * @author jer
     */

        public static class TwoTuple {
        // the two members of the TwoTuple
        private Object a;
                private Object b;

        /**
        * constructor
        * @param xa the first member of the TwoTuple
        * @param xb the second member of the TwoTuple
        */

                public TwoTuple(Object xa, Object xb){
                        this.a = xa;
                        this.b = xb;
                }

        /**
         * compare this TwoTuple to another for equality
         * @assumes nothing
         * @effects Nothing
         * @param other the TwoTuple to compare to this one
         */
                public boolean equals(TwoTuple other){
                        return this.a.equals(other.a)
                          && this.b.equals(other.b);
                }

        /**
         *
         * @assumes nothing
         * @effects Nothing
         */
                public int hashCode(){
                        return this.a.hashCode() ^ this.b.hashCode();
                }
        /**
         * Converts the TwoTuple to a String
         * @assumes nothing
         * @effects Nothing
         * @return concatenation of both objects converted to a String
         */

                public String toString(){
                        return "(" + this.a + ", " + this.b + ")";
                }

                public Object getFirst(){ return this.a; }
                public Object getSecond(){ return this.b; }


        }
    /**
      * Add all elements of b into a
      * @assumes nothing
      * @effects Nothing
      * @param a Set to add into
      * @param b Set to add from
      * @return a the Set we added into
      */

        public static Set unionInto(Set a, Set b){
                a.addAll(b);
                return a;
        }

   /**
    * Remove elements from a not also in b
    * @effects Nothing
    * @param a Set to intersect into
    * @param b Set to intersect from
    * @return a the Set we intersected into
    */

        public static Set intersectInto(Set a, Set b){
                a.retainAll(b);
                return a;
        }

        /** Remove elements from a that are also in b, return a.
     * @assumes nothing
     * @effects Nothing
     * @param a Set to difference into
     * @param b Set to difference from
     * @return a the Set we differenced into
     */

        public static Set differenceInto(Set a, Set b){
                a.removeAll(b);
                return a;
        }

        /** Union sets a and b
    * @assumes nothing
    * @effects Nothing
    * @param a Set
    * @param b Set
    * @return a new Set object representing the union of a and b
    */

        public static Set union(Set a, Set b){
                Set x = new HashSet(a);
                return unionInto(x,b);
        }

        /** Intersect sets a and b
    * @assumes nothing
    * @effects Nothing
    * @param a Set
    * @param b Set
    * @return a new Set object representing the intersection of a and b
    */

        public static Set intersect(Set a, Set b){
                Set x = new HashSet(a);
                return intersectInto(x,b);
        }

        /** Find the set difference, a - b
    * @assumes nothing
    * @effects Nothing
    * @param a Set
    * @param b Set
    * @return a new Set object representing the a - b
    */

        public static Set difference(Set a, Set b){
                Set x = new HashSet(a);
                return differenceInto(x,b);
        }

        /** Determines if Set a is a subset of Set b
    * @assumes nothing
    * @effects Nothing
    * @param a Set
    * @param b Set
    * @return true if a is a subset of b, else false
    */
        public static boolean subsetOf(Set a, Set b){
                Iterator it = a.iterator();
                while(it.hasNext())
                  if(! b.contains(it.next()))
                    return false;
                return true;
        }

        /** Compute the combinatorial cross-product of sets a and b.
    * @assumes nothing
    * @effects Nothing
    * @param a Set
    * @param b Set
    * @return a set of TwoTuples, each containing a pair
    * of elements, one from set a and one from set b.
    */
        public static Set crossProduct(Set a, Set b){
                Set r = new HashSet();
                Iterator ita = a.iterator();
                while(ita.hasNext()){
                    Object xa = ita.next();
                    Iterator itb = b.iterator();
                    while(itb.hasNext()){
                        Object xb = itb.next();
                        r.add(new TwoTuple(xa,xb));
                    }
                }
                return r;
        }
}
