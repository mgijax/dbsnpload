package org.jax.mgi.app.dbsnploader;

import java.util.Arrays;
import java.util.Comparator;

public class AlleleOrderer {

        /** Creates a new instance of Test */
        public AlleleOrderer() {
        }

        public String order(String in) {
            String ret = "";
            String patternStr = "/";

            try {
               String[] arr = in.split(patternStr);
               Arrays.sort(arr, new CustomComparator(false));

               for (int i = 0; i < arr.length; i++) {
                   ret += arr[i];

                   if (i != (arr.length - 1)) {
                       ret += "/";
                   }
               }

            } catch (Exception e) {
                // ignore for now
            }

            return ret;
        }
        /**
          * @param args the command line arguments
          */
         /*
         public static void main(String[] args) {
             // TODO code application logic here

             String in = "-/GC/CA/TAA/CGG/T/A/AAAAAAA";
             AlleleOrderer myApp = new AlleleOrderer();
             String out = myApp.order(in);
             System.out.println(in);
             System.out.println(out);
         }*/
    class CustomComparator implements Comparator {
        /**
         * Value that will contain the information about the order of the
         * sort: normal or reversal.
         */
        private boolean rev;

        /**
         * Constructor class for CustomComparator.
         * <br>
         * Example:
         * <br>
         * <code>Arrays.sort(pArray, new CustomComparator(rev));<code>
         */
        public CustomComparator(boolean r) {
            this.rev = r;
        }

        /**
         * Implementation of the compare method.
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