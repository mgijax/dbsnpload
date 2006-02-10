package org.jax.mgi.app.dbsnploader;

import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * is an object that orders a '/' delimited string of alleles
 * @has a Comparator which encapsulates the ordering rules.
 * @does orders a string of alleles
 * @company Jackson Laboratory
 * @author sc
 *
 */

public class AlleleOrderer {

    /**
     * default constructor
     */
    public AlleleOrderer() {
    }

    /**
     * orders a '/' delimited string of alleles
     * @param in - a string of alleles separated by '/'
     * @return ordered string of alleles separated by '/'
     */
    public String order(String in) {
        String ret = "";
        String patternStr = "/";

            String[] arr = in.split(patternStr);
            Arrays.sort(arr, new CustomComparator(false));

            for (int i = 0; i < arr.length; i++) {
                ret += arr[i];

                if (i != (arr.length - 1)) {
                    ret += "/";
                }
            }

        return ret;
    }


    /**
     *
     * is an object that compares two objects for >, <, =
     * @has boolean if true reverse the comparison
     * @does orders a string of alleles
     * @company Jackson Laboratory
     * @author sc
     *
     */
    class CustomComparator implements Comparator {
        /**
         * Value that will contain the information about the order of the
         * sort: normal or reversal.
         */
        private boolean rev;

        /**
         * constructor
         * @param r true if order in reverse
         */
        public CustomComparator(boolean r) {
            this.rev = r;
        }

        /**
         * determines whether two objects are <, >, or = to each other
         * equal sized nucleotide values (-, A, C, T, G) use ascii value to determine
         * <,=,>
         * shorter allele strings are < longer allele strings
         * @param pObj1
         * @param pObj2
         * @return -1 if s1 < s2, 0 if s1 = s2, 1 if s1 > s2
         */
        public int compare(Object pObj1, Object pObj2) {
            String s1 = (String)pObj1;
            String s2 = (String)pObj2;

            int ret = 0;

            if (s1 == null && s2 != null) {
                ret = -1;
            }
            else if (s1 == null && s2 == null) {
                ret = 0;
            }
            else if (s1 != null && s2 == null) {
                ret = 1;
            }
            // sort by length
            else if (s1.length() != s2.length()) {
                if(s1.length() < s2.length()) {
                    ret = -1;
                }
                else {
                    ret = 1;
                }
            }
            // equal sized multiple nucleotide alleles are sorted alphabetically
            else if (s1.length() > 1 && (s1.length() == s2.length())) {
                     ret = s1.compareTo(s2);
            }
            // single allele compare
            else {
                ret = s1.compareTo(s2);
            }
            return rev ? (-1 * ret) : ret;
        }
    }
}
