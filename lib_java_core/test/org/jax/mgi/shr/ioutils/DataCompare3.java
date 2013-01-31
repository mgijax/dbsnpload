package org.jax.mgi.shr.ioutils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DataCompare3
{
   private BufferedWriter writer = null;

   public DataCompare3(String name) throws IOException
   {
      writer =
         new BufferedWriter(new FileWriter(name));

   }


   public void createFile() throws IOException
   {
      writer.write(">TC157197 histone H1d gene\n");
      writer.write("CTGCAGTTATCATAATGATAACTGACTATGAAATGTTTAAGTCCTTTGGTGGATACCACC\n");
      writer.write("TGCTAATTCTTTTGTTTGTGACAGACTATATTATATATGTAATATATGTAATGAGGTCAG\n");
      writer.write("CAGCTATCATGGAAATTAAAAAACAGATTTTCTTTCACATTCTGCTTGTTTTTAATAATG\n");
      writer.write("AAATTCAAATCATTTTAAAACTGGAAATATGCAGAAAACTGTTGTGATAAGTGTTGGCAA\n");
      writer.write("GGGGAAACAGCTGCATAACACAGGTCCTGAAACACGGCATATAACACAGCTGTAAACTTT\n");
      writer.write("GTACTCTAAACACGGAGTGAGTTGCTAAGTCAGGTTATATCTCACTGTTCTTCAGCTCCC\n");
      writer.write("AGTTGCTAAACTTCTGTTCCTTCTTGACCTAGGTATCTTTCTCTCTCTGTACATGTGATA\n");
      writer.write("GTTGGTGTACATTTCTCTATCAAATCTGAAAAATATGTGCTTTCAGCTTTTGATCTACAA\n");
      writer.write("TAGGGATGGCACAGACTCATCACGTCTCCAGAAAATGGCTAAATCACTAAAACAGGAAAA\n");
      writer.write("CCGAAGGGGAATTACACACTGGTATCTCAAATATCATTAAAAAATCCAATCGATATTTTC\n");
      writer.write("TACAATAATCATTCCCCCGGTATGCACTGCTTTCCCATCTAGTGCCAATTCCTACAGCTT\n");
      writer.write("GGTGCAGATAGCCCTGGAAGCTTGTGTTGTTGCTTTCTCGAAAAACTCAAATTTCCCTAT\n");
      writer.write("GTATTTCCGTGAAAACAAGTTATTTCATAGAGTCCTGAAATGATAAGAATGTGAGGAACC\n");
      writer.write("ATGTCCTGAAACAAAAGTTTGCTAATGACCGCGAGAGAAGTAGTATTTTTAAGGAAAACC\n");
      writer.write("TTAAAGTTGTCCCAATTTTCGTCAAGTTTGAAAGAGAGTTGGGGCAATCAGAGAAGTTTC\n");
      writer.write("AGGGTGCCGATTTTGAACATAGGTGCACAAATTAAGGCAAGAATGTAGCCTACAGGCTCT\n");
      writer.write("CGGATCAGTTTCCCCCAAGTCCCTAATTATTTCGTGCCCATATTTTTTATATTTTTATAC\n");
      writer.write("TTTTTTGAGGGGCAACAAACACAGCCACAAGGCAAAGCTGAAGATCCTTTCTCTGGCAAC\n");
      writer.write("GCGGCGCACGGCGCACGGCGCAGGGAACCAATCACCACGCAGCTTCTCTCTATATAAACC\n");
      writer.write("CAGAGCCTGCAGCACTGGGAACAACCTTCTCTGACTGTTTGTGCTTACTTTTTGCTTTAC\n");
      writer.write("TAGTAAAGCTTAGAACATGTCCGAGACCGCTCCCGCGGCGCCTGCTGCCCCTGCACCTGT\n");
      writer.write("GGAGAAGACACCTGTGAAGAAGAAGGCGAAGAAGACCGGCGCCGCTGCTGGGAAGCGCAA\n");
      writer.write("GGCGTCCGGACCCCCGGTGTCCGAGCTCATCACCAAGGCTGTGGCCGCCTCCAAGGAGCG\n");
      writer.write("CAGCGGCGTGTCCCTGGCTGCGCTCAAGAAGGCGCTGGCGGCCGCGGGGTACGATGTGGA\n");
      writer.write("GAAGAACAACAGCCGCATCAAGCTCGGGCTGAAGAGCCTGGTGAGCAAGGGTACCCTGGT\n");
      writer.write("GCAGACCAAGGGCACCGGCGCCTCCGGCTCCTTCAAACTCAACAAGAAGGCGGCTTCCGG\n");
      writer.write("TGAGGCTAAGCCCAAGGCTAAGAAGGCAGGCGCGGCCAAGGCCAAGAAGCCTGCGGGAGC\n");
      writer.write("AGCCAAGAAGCCTAAGAAGGCGACTGGTGCTGCCACACCCAAAAAGACGGCCAAGAAGAC\n");
      writer.write("TCCGAAGAAGGCGAAGAAGCCTGCGGCGGCTGCCGGCGCCAAGAAAGTTTCCAAGAGTCC\n");
      writer.write("CAAGAAGGTGAAGGCTGCTAAGCCCAAGAAGGCAGCAAAGAGTCCAGCCAAGGCCAAGGC\n");
      writer.write("TCCCAAGGCTAAGGCTTCCAAGCCTAAAGCTTCTAAGCCGAAGGCCACCAAGGCAAAGAA\n");
      writer.write("GGCTGCCCCTCGCAAGAAGTAGAGTGGTGCGTCCTGCTTTGAAAATCTCAAACGGCTCTT\n");
      writer.write("TTCAGAGCCACCCACAACCTCATTCAAAAGAGCTGAGCCTTTTTCTGGTTTTCTCATGGT\n");
      writer.write("ATGTCCGCTGACTCTGGCTATGTGTTCCGAAGCAGATCAATTCTGTGCACTTTGTTATGG\n");
      writer.write("GTACATTTAGGAGCATCTATAAACTCTAGTTGAGCGATAGATTGTATCATTTTCAGCTCT\n");
      writer.write("AGCTGGACTTCTGGCCGGCAGTTTTTTGCTTCTGTGTGTGCTCCACACACGTGTTCATAT\n");
      writer.write("TCCAAGAGTTTGGCGGGAAGTCCTTTCTAATGTCGCCTAGGCGTGATAAGGCATTAGGGG\n");
      writer.write("GGTTCTCTAGGAAGCTACTCTCACAATAGTGGCTACAGGCTGGAGCCTCTGTTTTAAGTT\n");
      writer.write("TTCCAATCCGGCTACACAAGGTGGTTTTGAGAATTAACCAATCAAGCCGTACTTTCAGTC\n");
      writer.write("ATGGGTGTTGCACCAATCAGAGATTGTGATATTAAGATTTACATTTACGTACTGGGACGC\n");
      writer.write("TGGCGTTTTTTCTCTGTCCCCCGCCCCTTTGTTCACTTCAGAGCTCTGGAATTCCGTCCT\n");
      writer.write("AAGAATTTTTAATTGTGTTTTCCGAGAAATTGAAAGCGCTCCTATTTCCTCCAAACTTAG\n");
      writer.write("CCACCTTCGCAGTTTTTAGGCACTGATCACATTGCCACTATAAGTGTGGAAGCTT\n");
      writer.write("-----------------------------------------------\n");
      writer.write(">TC157198 NADH-ubiquinone oxidoreductase 75 kDa subunit precursor {Bos taurus}SP|P15690|NUAM_BOVIN NADH-UBIQU\n");
      writer.write("TAGACTCAAACAGGCTTTTATTATTCACGTACTCGGTAGAAAACGGGGGTTTAGTAAACA\n");
      writer.write("GGGTGGAACTGTATAGGAAGACGCTGGGTTGGTCCGATACTATTTACACGTGGTGGCAGC\n");
      writer.write("GCTGTTCGTCACATCAGGCACAGCACGTGCACTTGTCCGCGGCGCCTTTGCAGACACAGC\n");
      writer.write("CCTGGGCACATTTGGAGCAGCCCACGGGACAGCAGGAGCAGCAGCTCTTCTTGCAGGAGG\n");
      writer.write("TGCACTTGCAGTTCTTGCAGAATTTTAGGAGAGTCTCCTAGATGGTCGTATCTGTAAGTG\n");
      writer.write("AGGTCCACTGGACTACCGATGAGGGCCACTTTTAAGTCATTATGAAGCCAGCTCTTTCTA\n");
      writer.write("ATTCTAGCATTAAACAGCGGTGCCTCAAAACGTGGATTTGTACCAACTAGAAGAACAACA\n");
      writer.write("TCTGCTTCTTCCACACCAGCAATTGTGGTATTGAGAAGATAATTGGAACGTAAGTCTGTA\n");
      writer.write("CCAGCTCCTTCAGTGGGGAAGATCTCTTCAGTGCATAAGTTGTCAGAGTCAACTTTATTA\n");
      writer.write("AGCAAGTCTTTCAGAGCTACTAAGGCTTCAGCATCCACCAAGCCTCCTGCAATCGCTGCC\n");
      writer.write("ACAGCATTGCCTTCAAAATTCTGTAACATTCCAGCTACACGAGAGAGTGCATCTTCCCAG\n");
      writer.write("GGGGTATAAGTAAAAAAGAAAACACCACTCGAAGCGCTTCAACTTGCCCACTTCAACGCG\n");
      writer.write("TCCTCACCTTTCCTCCGGAGCCCACCGAGTAGTTCCGCGGACCTGTCCGGGTCTCCCTGA\n");
      writer.write("CCCCTCAAAACCCTGGCCGCTAAGTCAATATGGCGGGCCACGGGCAAC\n");
      writer.write("-----------------------------------------------\n");
      writer.write(">TC157199 EUKARYOTIC TRANSLATION INITIATION FACTOR 4 GAMMA (EIF-4-GAMMA) (EIF- 4G) (EIF4G) (P220).\n");
      writer.write("CTCTTCCAGCCCAGTGATCAGTGTGTGGGTCGCCATCCGGATGTTGCTAGGCGTGGGCTC\n");
      writer.write("CTGGCCACCCAGGTAGTGCTGGTGGAAGAAAGATCGCAGCTGTGCTGGACCGCTCAGTGC\n");
      writer.write("TGAAATGCCATGGAGAACATCACCACATCCACCATGGAAAATTCTGGCACAGGAGAGAAG\n");
      writer.write("CAGAGCTCCGAAGAATCCAAGGGCGCCATCAGCCCCAGCCTCAAAGATGTTGCTGGATCC\n");
      writer.write("ACTGAGGCGCTGGATGAAGGCAGCGATACTCTCACTGATGCCAGCTCGAGCACCCAAGGA\n");
      writer.write("GCCCAGGAGGGAGCTCAACACGCCCTGCACCACTGAGGTGAAAATACTCTGTGTGGCAGG\n");
      writer.write("CTCTCACGACCTAAGCCTCCAAGGCTTGCTGTTCCACCAAAAGGAGACCCTGCATGGGGG\n");
      writer.write("TGTGCTCTGCTGCTCTGAAGCACATGGTGGAGGTGGAGCAGGTTGTCCAGGAGCTGGCTT\n");
      writer.write("CACCCTCCTTGCTCTTCATCTTCGTACGGCTTGGTATCGAGTCCACGCTGGAGCGTAGTA\n");
      writer.write("CCATTGCTCGTGAGCATATGGAGCGACTACTGCACCAACTGCTCTGTGCGGNGCACCTCT\n");
      writer.write("CTACTGCCCAGTACTATCAAGGGCTGTATGAAACACTAGACTTGTCTGAGGACATGGACA\n");
      writer.write("TAGATATCCCTCATGTATGGCTCTACCTGGCAGAACTGATTACACCTATTCTGCAGGAAG\n");
      writer.write("ATGGTGTGCCCATGGGAGAGCTCTTC\n");
      writer.write("-----------------------------------------------\n");
      writer.write(">TC157200 intestinal Muc 2-like protein\n");
      writer.write("TTTAAATCACAACACCAGAGTGCTATGCCTGAGTTTATTATCGGAAAGCAAAGAATGGAA\n");
      writer.write("CAGAAACTCCGAAAATAAATATGTGCAGAGAAGAAGCCAAGCCAGGAGTGGAGAAGGTCA\n");
      writer.write("GAGAGTCAGCAGAATAGTTTAAGATGCGAAGGAATGTGCACGGTACAACCCATTCACTTC\n");
      writer.write("CTTCCTAGAAAACGGGGGCTAGAACGCCTGGTGCGGACTTGCTGGGCCTGTGGGAGCCCA\n");
      writer.write("CAGACAGTGTCCTGGCACAGACAGCTCTCGATGTGTGTGTAGGTATGGCTCAGCTCACTG\n");
      writer.write("CCATCTGGGCATTCCAGGGTAACACTTCGGACACTGGTCTTCTCCTCCTTGCAGCAAGAG\n");
      writer.write("CACCTGTGATCCAGACCCTGGACCTGGGCAGAGTACATGGCAAAAGTCCCACAGGACCCA\n");
      writer.write("AAACAGTAGTTCATGGAGATGTTCTTGGTGCAGCCATTGTAGGAAATCTCCTTCATGACG\n");
      writer.write("GAGACAGCAGAGCAAGGGACTCTGGTCTGGTTTTGAGGGATGCATGTCTTACAGCAGCCA\n");
      writer.write("TTAGGCATGTATGTGATGGAGCCTGAAACACAATCACTTGGGTTGAAGTCGGGACAGGTG\n");
      writer.write("ATGTTGGAGACCGAGGAGATGAGCTGGTTGTTGATTTTCATGCAGCTGAAGAAGGTGCAC\n");
      writer.write("TTGTTGCTGGGGTTTTTGTGAATCTCCCCAGGCTTCAGAATAATGTACTGCTGCTTGGGC\n");
      writer.write("CCCTCGATGATGCAATGGGTCTGCTGGCACTTCTTGCAACATTCCCCAGGGACGTCCACA\n");
      writer.write("AGCTCAAAGCCAGAGCTGCAGGAGATGTTACAGGGCACATGGGTACAGGAGATGACGTTG\n");
      writer.write("AGCTGGGTGCTGTTGTCCAAGATGTTGGTACATACACAGTCCTGGCACTTGTTGGAATAC\n");
      writer.write("ACTGGAGATCCGGGCTGGTATTCAGCATTCTGGTGCACACACACACCCTTGGGCACACAG\n");
      writer.write("GAATAAACTGGACAGCATTTCCCGGGCACAATCTCGGTCTTCACTTCGAATCCCAACAAA\n");
      writer.write("CATGTGGGGCGTTCAGCTTTGCATCGTTTGGTGTCACACTTACATGAGGTGATATTGCAG\n");
      writer.write("CACTTGTCATCTGGGTTGGTCTCTACAACGAGGTAGGTGCCATCCTCCTCACATGTGGTC\n");
      writer.write("TGGTTGCCACCAGAACATTTCTTTGGTTGGCACACAATGCCACTTCCACCCTCCCGGCAA\n");
      writer.write("ACACAGTCCTTGCAGTCAAACTCAAAGTGCTCTCCAAACTCTCTGGGCACATTGTCAGGT\n");
      writer.write("CCCACACATCCACATGTCTTCACACAGACGTCATAGCCAGGGGCAAACTTTGTGGTGCCT\n");
      writer.write("TCAGGACAGAAGCAGCCTTCCACCAAGAGTGTGGAATTCTGAGAGGAGCTGGGCTGGCAG\n");
      writer.write("GTGGGTTCTTCTTCAGGGCCACAGGCCTGATACTGCTTGTGAGGTGGGCACTTCACAGAG\n");
      writer.write("CAGACTCCCTGGGTGTGGTTCCGCCAGTCAATGCAGACACCTTCCTTGGCACAGAGGGTG\n");
      writer.write("GCATAGGCCTGTACACTGGCACACTCCATATTAGAGCCAGGCACATAGCAGCTATCAAAC\n");
      writer.write("AGGCAAGCTTCATAGTAGTGCTTGGGAGGCACGAAGGCGTGGCACTGGGAGAATAGGCTG\n");
      writer.write("TCCATGATGAGGTGGCAGACAGGAGACACAGTGCAGTTATTAAGCGAAAGCCCTGGTGTC\n");
      writer.write("GTGGTGGCCGGACGCTTGGTGGTGAGGCCTTTGTGGGGACAGTGTGGCTTGGAGGGGTCA\n");
      writer.write("TTCACCAACCACTCGTCAGCAGCAATTTCACAGTCAGAGATGATCTTCCCACTGGGCAGG\n");
      writer.write("ATACAGTCATCTGCAGTATTGTTGGTGCAGGTGCCACACTGGCCCTTTGTGTTGTTGCCA\n");
      writer.write("AACAGTTTGTAGGGCAGCCGGATGGAGAAAGAAAGGCCATTGTAGGAGATCTTGGCTTCT\n");
      writer.write("AGCCTGGAGATGTTCACCACAATGTTGATGCCAGACTCGTATACCTCCAGCCCATACTTC\n");
      writer.write("TTGTAGGGCAAAGCCACTAACTGCTTGTTCACCTGTACCTCCACCTCAATGGGCATCATC\n");
      writer.write("CTCACGGTCTTGATCTGTACTTCCTGGGTCTCGTGGCGCACAATAAGTGTGCGGGGGCAG\n");
      writer.write("GACACCTTGTCATTGGCATCACAGTGGTAGTTGTCAATGTAGACCCCAAAGTTGCTCGTG\n");
      writer.write("CCGAATTC\n");
      writer.write("-----------------------------------------------\n");
      writer.write(">TC157201 BAT3 {Mus musculus}\n");
      writer.write("AATTCGGATCCAACGAACGCTTTGGTTTATGCAGAAGCTTCCAGAGACAGAGGGCATGGC\n");
      writer.write("ATAGTTTCAGAGGCAGTCATCCCTGTTCTTGCTCTTTTTTTCTTTGCTTTCTTTTATTTA\n");
      writer.write("TTTTTAATTCATTTTTAACACTCCATATTCCATTCCCCTCTCCCCATCCCTGTTCTTAAT\n");
      writer.write("ATCATGCTTTTCTGGGTTTGACCTGGGTACAGCATGAGGGCCGTGAGGAGGACCAGAGGT\n");
      writer.write("TGATCAACTTGGTTGGGGAGAGCCTGCGCCGACTGGGCAACACTTTCGTGGCATTGTCTG\n");
      writer.write("ATCTGCGCTGCAATCTAGCCTGTGCACCCCCACGGCACCTGCACGTGGTTCGGCCTATGT\n");
      writer.write("CTCACTACACGACTCCCATGGTGCTCCAGCAGGCAGCCATTCCCATTCAGATCAATGTGG\n");
      writer.write("GGACTACTGTGACCATGACAGGCAATGGGGCTCGGCCTCCACCAGCTCCTGGTGCGGAGG\n");
      writer.write("AGCAACCCCAGGTTCTGCCCAGGCCACATCCCTGCCTCCCTCTTCCACCACTGTTGATTC\n");
      writer.write("ATCAACTGAAGGAGCTCCCCACCAG\n");
      writer.write("-----------------------------------------------\n");
      writer.write(">TC157202 ubiquitin polyprotein (heat shock related) {Gallus gallus}GP|212851|gb|AAA49129.1||M11100 ubiquitin\n");
      writer.write("CGGCACAGGGAGGCTTTGTCCGGTTCGCGGTCTTTCTGTGAGGGTGTTTCGACGCGCTGG\n");
      writer.write("GCGGTTTGTGCTTTCATCACATTTGTTAACAGGTCAAAATGCAGATCTTCGTGAAGACCC\n");
      writer.write("TGACCGGCAAGACCATCACCCTAGAGGTGGAGCCCAGTGACACCATCGAGAACGTGAAGG\n");
      writer.write("CCAAGATCCAGGATAAAGAGGGCATCCCCCCTGACCAGCAGAGGCTGATCTTTGCCGGCA\n");
      writer.write("AGCAGCTAGAAGATGGCCGCACCCTCTCTGATTACAACATCCAGAAAGAGTCGACCCTGC\n");
      writer.write("ACCTGGTCCTCCGTCTGAGGGGTGGCTATTAATTATTCGGTCTGCATTCCCAGTGGGCAG\n");
      writer.write("TGATGGCATTACTCTGCACTCTAGCCACTTGCCCCAATTTAAGTTTAGAAATTACAAGTT\n");
      writer.write("TCAATAATAGCTGAACCTCTGTTAAAAATGTTAATAAAGGTTTTGTTGCATGGTAAGCAT\n");
      writer.write("A\n");
      writer.write("-----------------------------------------------\n");
      writer.write(">TC157203 BAT3\n");
      writer.write("ACCGAGCCGACTCGGTCCACACGACTTCCACTCCCTCAGACCCTCTGGAGCCTCCCACGC\n");
      writer.write("GGCACTCGCCACGGCCCTCGCTCCGCCGCCGCGTCTCCACACGCCGCACCCCCTCCCGCA\n");
      writer.write("CAGCACCTGCACGTGCGCCGCGCCGCTCCCCCAGCAGCCATCCACACGCGCCCCCACCAC\n");
      writer.write("CGCGCCTCACAACTCCAGCCCCCGCCCCGATCCCACGGCGCCCACCCCCGGCACTCCACC\n");
      writer.write("CCACGGCCCGCCCGCCCCGCCGCCCAGACGCGCTCCGCCGTCCTCCACAGCACCGCACCC\n");
      writer.write("GCGCCCATCGCCCTCCCAGCCCCCCACCCCCCCCGAGCCCCGCCGCGCCGCCCGCCCCGC\n");
      writer.write("CGACCCCCCCCGACCCCCCCTCCCCCGGCCTCCTCCCCCTCCTCTCCCTCGCATCCGCTG\n");
      writer.write("CCCTCCACCTCTCCTCCTCCCTCCCCCCCCTTCTCTCCCTCTCCCCTCCCTCCCTCTTTC\n");
      writer.write("CCTTTTTCTTTTTTCTTCTTTTTTTTACTAGGCAAAGGAGCCAGGAAAATCCGACTTTTA\n");
      writer.write("TTTCTTAAATACTGTGAAGGAAGGGGGGAAGTGGTCCCCTGGTGAGAAAGCTAGGGGTCA\n");
      writer.write("TCAGCAAATGCCCGATGGGCATTAGGGAAGCGCTGGGGGCTGTAGTTGGGATCTTCCTGC\n");
      writer.write("AGTCGTTTTTGGATATCAGACCGGAGCTGCTGCCTGTAGCTCTCCTGAACCTCTGGTGCC\n");
      writer.write("TCCAGGTCCCGGCTCAGGCTCTCGGGGCTTGTCAGGGGCCGAGCTCCGGCTGCCTTAGCT\n");
      writer.write("GCCCGGCTCACTGCCTCTGAGAGTAGCAGCTGGGGGCCCTCACCCTGCATTGTCTTTCGT\n");
      writer.write("CTCTTGGCAGGCATGCCACTGAGGTAGGCATCACTCAGGGGCGGCTGAGGTTTCACCTTC\n");
      writer.write("CGCTGGCTCTGAATGTCCTGCTGGATAATAGGGACCCATTCAGGGGGGACTGCAGCTGCC\n");
      writer.write("CAGGGTTCTGCATCAGCTGAAGCTCCATCCTGTTCATCTCGGGAACCTCCTTCAGGAGCA\n");
      writer.write("GGGGGCGGGCCTCGGGACATGGCTTCTTCTGCTGTTGTTCCAGGGGCTGGGGAAGCATTC\n");
      writer.write("TCTCTCTGAGGTTCAGGGGAAGTTCTTTCTGCTCCCTGAACTTCCATCGGCTCTTCAGGA\n");
      writer.write("AGTGTCTGAGGAGGATCACCAACCCTACGAACATATCTGAGGATGGCGTCGGGACCCACA\n");
      writer.write("GGCATGTGCTCCAAGACCACCTGAAGCCTCAGTCCCATCATGGTTGTCAGCCAGCTCACC\n");
      writer.write("AAGGATGGATTCACCCCGCGAGACATGCGGCGAATTCGACCATTGATGACAGCAGCAAGC\n");
      writer.write("TCCATTTGCTGTCCCCCCAAGCAGTGGAGGTTCAGGGCCAAGCATTCAAACAGGCCCTGG\n");
      writer.write("TTACACAGCTCCAGCAACCGGGCTCCAAATCCACTGTCTGTACAGTGCAGCACATGAGCA\n");
      writer.write("GCAATGCTGTTAAACTGCTCTTGGAGAAATTCTAAATTTGTCCGGATGATATCCACACCT\n");
      writer.write("GGCTGAACCTGTACCAAAGAAAAACTCTCCCTTACATACTCTTCCAGCCCAGTGATCAGT\n");
      writer.write("GTGTGGGTCGCCATCCGGATGTTGCTAGGCGTGGGCTCCTGGCCACCCAGGTAGTGCTGG\n");
      writer.write("TGGAAGAAAGATCGCAGCTGTGGCTGGAGCCGCTGCAGTGGCTGGAAATGGCCATGGAGA\n");
      writer.write("AGCATCACCACATCCACCATGGAGAAATTCTGGCACAGGAGAGAGAGCAGAGCTCCGAAG\n");
      writer.write("AATCCAAGGGCGCCATCAGCCCCAGGCTCAAAGATGTTGCTGGATCCACTGAGGCGTTGG\n");
      writer.write("ATGAAGGCAGCGATACTCTCACTGCTGCCAGCTCGAGCCCCCAAGGAGCCCAGGAGGGAG\n");
      writer.write("CTCAACACGCCCTGCACCACTGAGGTGAAAAACTCTGGTGGCAGGCTCTCAGGACCTAAG\n");
      writer.write("CCTCCAGGGCTTGCTGTTCCACCAGAAGGAGACCCTGGTGGGGGTGTGCTCTGCTGCTCT\n");
      writer.write("GGGGCAGGGGGTGGGGGTGGAGGAGGTGGAGGGGGTGGAGGGGCAGTCTGTGATGCCTGC\n");
      writer.write("AAAAAATCAGTCATGCCCTGGAGAAAAGCAGGGACACCAGGCATTGCCACAGTGATGGTG\n");
      writer.write("GGAGAAGCCATGCCTGGCCCCCCAGCCCCTGGCCCTGCAGGCCCCAGCAGATTTCCCAGG\n");
      writer.write("AGCTGAGAGAACTGAAGATCAGCTGCAGAGGGCTGAGGAGGTGGAGGCTGGGCAGGACCC\n");
      writer.write("CCAGGAGCAGGGCCAGCTGTGGTAGCTGTGTTGGTAGTACCAGCACTAGCTGAAGCAGTG\n");
      writer.write("GCAGGAGCTGGAGCTGGAGCCGGAGCCGGAGCTGGAGCCTGGGCCTGGGCCTGGGCCTGA\n");
      writer.write("GCCTGGGCCTGGGCCTGAGCCTGAGCCATTCCTGGAGTCCCCTGAGCCACAAGAACAGGC\n");
      writer.write("TGCATAAGAAGTTGCCCCACAAGGCCGCTCACCATCTGGGCCAATGAAGTGTTTGTACCC\n");
      writer.write("AGCCCAGCGCCCTGCAGAGCTCCAGAGACTGGAGGTCCCCCAGGATGGGAAGGCCTGGCC\n");
      writer.write("TGATGAGTGATCTGGTGGGCGACGGCGTGCATGAACTCAGGGGGCAGGGAGGGCAGCTGG\n");
      writer.write("ATGAGGGTGGAGCCCAGGGTCTGTCCATGACCAGGAGGTCCCAGTGGACCAGTGGGAGCA\n");
      writer.write("CTTGGCACACCACCAGGCTGTGCTCCAGAATCTTGAATGTTCATGTGCATCATGACCACG\n");
      writer.write("GGCTCCACACTCTGGTGGGAAATCCGGATGACCCGTGGGTGGCTGGAGGCTGGTGGCGGT\n");
      writer.write("GCTGGCCCTGGTGGGGGAGCTCCTTCAGTTGATGAATCAACAGTGGTGGAAGAGGGAGGC\n");
      writer.write("AGGGATGTGGCCTGGGCAGAACCTGGGGTTGCTGCCTCCGCACCAGGAGCTGGTGGAGGC\n");
      writer.write("CGAGCCCCATTGCCTGTCATGGTCACAGTAGTCCCCACATTGATCTGAATGGGAATGGCT\n");
      writer.write("GCCTGCTGGAGCACCATGGGAGTCGTGTAGTGAGACATAGGCCGAACCACGTGCAGGTGC\n");
      writer.write("CGTGGGGGTGCACAGGCTAGATTGCAGCGCAGATCAGACAATGCCACGAAATTGTTGCCC\n");
      writer.write("AGTAGCCGCAGGCTCTCCCCAACCAAGTTGATCAACCTCTGGTCCTCCTCACGGCCCTCA\n");
      writer.write("TGGTTGTTGTTGTAGTCTGTGGTGGCAGCAGCGCCCAGGACCTCGCAGTAGCGCTGTAGG\n");
      writer.write("AAAGGCTGAAGACGGCGCTGCAAGCGCTGAAGCTCCTGGAGCACCTCCACGTGCTCTGCG\n");
      writer.write("GGGGAAGGGTGGTTGGGTGCATTTGTCTCTGGCGCGGGAGCTGGGCCTGCGGGAGCTGGG\n");
      writer.write("CCTGCGGGAGCTGGGCCTGAGGGCGCAAGCTCTGGAGTCTGGGTTGGGGGACGTTCCTCC\n");
      writer.write("ATTTCTTCTGACTCCATGGGCTCTCGAGGAGGAGCTTCACTTTCGACTGGTTCTGATGTT\n");
      writer.write("TGTGAGTTCAAGGCTACCGTCTCTGAGGCCACAGTCTGCGGTGTCTGCGGGGGTGGCTGA\n");
      writer.write("CTGGCCTGAGCTTGGGTTCCCCCTCGACACTCCATCCGGGACAGTAAGGTCTGTATATCC\n");
      writer.write("CTAATCATGTGCTGAGCCATCACCAGCCGGACCCGGGGCTCACTCTGAATTGGGGCCTGT\n");
      writer.write("TCCATGTTGATGTGAACATCCACAGCAGAGCCGTCACTAGGAAGATTGAAGGTTCCAACC\n");
      writer.write("ATGACATAGCTGTTGGCATTCCGGTCATGAACAGAAGCCCCAGGGCCCCGAGTGCCAGGC\n");
      writer.write("AGGGGTGCCCCACCATGAGTTGCTGAGGCAGAGCCTGTCCCAGAAGATGCTCCAGAAGGA\n");
      writer.write("AGCTGAGTCTGAGGAGGAGCCCGTTCCACCAGGTGAATAACCTTTCCCCCAACGTTGTAC\n");
      writer.write("TCCTGGAGCTTCTTGTCGTCTTGTAGGACCCGGCCCTGGTAGATGAGCCGCTGTTTCTCG\n");
      writer.write("GAAGGGATGCTGACAGAGGCAGCTATGTGTTCCTTAAACTCCTTTACATTCATCTGGGCC\n");
      writer.write("CCCACAATAAAAGTCCGAGTCTGAGAGTCCAGGGTCTTCACCAGTACCTCCAGGCTGTCA\n");
      writer.write("GGCTCCTCCATAGCGGTACTGGCACTATCACTCGGCTCCATGGCCGACAGCTCTCGTCAC\n");
      writer.write("TTCCGGTCTCCCCCAACCTGGCACCAAGGGCCACTTCCGTTTCCCCGATCGTATTTGGGG\n");
      writer.write("GGATCTCGAGGCGGTACTTCCGGCTCCCCCAGTCTCCCCCGATTTACTTTCGTGGGGCAC\n");
      writer.write("GATGAGAAAGTCCACAGCCCCGAACAGTGAGTTTCTGGGGGGCGAGACGGGCCCGGGCCG\n");
      writer.write("GCCGAGATGGGAGGGAGCCGACCACCCGGAGGAGCCGCCGCCGCCGCCGTCGTCGCCCGG\n");
      writer.write("GGGACCGCACTGCGCCTGCGTGAGCGGCGCCACGCGTGCGCACACTCTAAGTACTTCCCC\n");
      writer.write("GCCCCCCTCCGGCTATGTGCGTTTCTTCCCTGCGCGCGCCAGAGTGCAACCGGACCGGGG\n");
      writer.write("TGGCTACTGGAAACGCCTAAAGAAAAATGACTGAGTGAGATGCTGTAATTTCCTCGAGAA\n");
      writer.write("AAATTGTGAGACTTGTGTGGAGTGCACCTCGTTAATCAACGTCCCATCTTGATGGTCAGA\n");
      writer.write("GACGTTTACAGGCACAATTCTAACTACCTAAAACTCTGCAAGACCTCTGGTCCTCCCATC\n");
      writer.write("ATTTCTGTTCTTCCGGAAGCCACTCTTTTTCGTGGACGGCTGGTGGCGTTCCGCGGGCTG\n");
      writer.write("TGTTTGCTTGTGCCCGACTCTAGTTCCTGGCGGGATTAGGCCGGTTGGGCTCTTTGCAGA\n");
      writer.write("CTTTGAGCATCCTGTTTATTGAGATCTCCGAGGGAGTCCCATGTTTAAGGAT\n");
      writer.write("-----------------------------------------------\n");
      writer.write(">TC157204 polyubiquitin {Rattus norvegicus}PIR|S45359|S45359 polyubiquitin 10 - rat\n");
      writer.write("CTGCAGGCGTGACTTGATCACATCCATGGGGGTTGCCACGGCCCAGGCCAGGACCCCAGC\n");
      writer.write("GCAGCCTCCAGCCACCAACACACCTAAGACATCTGGCTGACTATGGCCAGCAGGGGTGAG\n");
      writer.write("CCACTCGCAGAGCATAGCATAGGAGAGAAAGTAGGTGGCAAAGGAGTGACCCTCACGAAG\n");
      writer.write("GAGCAGAGCCGAGCTGCCCTTGTAGAGTCCCCGAAGCCCCTCCTCTCGAGCCACTGTGAC\n");
      writer.write("TAAACAGTGCAGTGGCCCACTGTACTTAGGCCTGGGCTCCAAGCAAGCAGTGGGTGTGGG\n");
      writer.write("ACACAACAGCGGGAGCCCCAGATGTCCAGGAGGCCGAGGACCGCCGCTGCTGTGTCTGAG\n");
      writer.write("CTTGGGTCTGTGTCTGCAGGCGGACTTTGGCCACCTCAGTGGGTGACGTCAGGAACACCC\n");
      writer.write("GGACAAGACCAGGTGCAGGGTGGACTCTTTCTGGATGTTGTAGTCTGACAGGGTGCGGCC\n");
      writer.write("ATCTCCCAGCTGCTTGCATGCAAAGATCAGCCTCTGCTGGTCAGGGGGGATGCCCCCCTT\n");
      writer.write("GTCCTGGATCTTGCCTTGACATTCTCTATGGTGTCACTGGGCTCGACTCCAGGGGTGATG\n");
      writer.write("GTCTTGCCTGTCAGGGTCTTCACAAAGATCTGCATGCCACCTCTGAGCCGAAGGACAGGT\n");
      writer.write("GCAGGGTGACTCTTTCTGGATGTGTAGTCTGACAGGGTGCGGCCATCTTCCAGCTGCTTG\n");
      writer.write("CCTGCAAAGATCAGCCTCTGCTGGTCAGGGGGGATGCCCTCCTTGTCCTGGATCTTTGCC\n");
      writer.write("TTGACATTCTCTATGGTGTCACTGGGCTCGACCTCCAGGGTGATGGTCTTGCCTGTCAGG\n");
      writer.write("GTCTTCACAAAGATCTGCATGCCACCTCTGAGGCGAAGGACCAGGTGCAGGGTGGACTCT\n");
      writer.write("TTCTGGATGTTGTAGTCTGACAGGGTGCGGCCATCTTCCAGCTGCTTGCCTGCAAAGATC\n");
      writer.write("AGCCTCTGCTGGTCAGGGGGGATGCCCTCCTTGTCCTGGATCTTTGCCTTGACATTCTCT\n");
      writer.write("ATGGTGTCACTGGGCTCGACCTCCAGGGTGATGGTCTTGCCTGTCAGGGTCTTCACAAAG\n");
      writer.write("ATCTGCATGCCACCTCTGAGGCGAAGGACCAGGTGCAGGGTGGACTCTTTCTGGATGTTG\n");
      writer.write("TAGTCTGACAGGGTGCGGCCATCTTCCAGCTGCTTGCCTGCAAAGATCAGCCTCTGCTGG\n");
      writer.write("TCAGGGGGGATGCCCTCCTTGTCCTGGATCTTTGCCTTGACATTCTCAATGGTGTCACTG\n");
      writer.write("GGCTCGACCTCCAGGGTGATGGTCTTGCCTGTCAGGGTCTTCACAAAGATCTGCATGCCA\n");
      writer.write("CCTCTGAGGCGAAGGACCAGGTGCAGGGTGGACTCTTTCTGGATGTTGTAGTCTGACAGG\n");
      writer.write("GTGCGGCCATCTTCCAGCTGCTTGCCTGCAAAGATCAGCCTCTGCTGGTCAGGGGGGATG\n");
      writer.write("CCCTCCTTGTCCTGGATCTTTGCCTTGACATTCTCAATGGTGTCACTGGGCTCGACCTCC\n");
      writer.write("AGGGTGATGGTCTTGCCTGTCAGGGTCTTCACAAAGATCTGCAATGCACACCTCTGAGGC\n");
      writer.write("GAAGGACCAGGTGCCAGGGTGGACTAGTTTCTGGATATATGTAGTCTGAACAGGGAGCGG\n");
      writer.write("CCATATTCCAGCTGATTGCCAGCAAAGATCAGCCTCTGCTGGATAAGGGGGGATGCCCTC\n");
      writer.write("CTTGTCCTGGATCTTTGCCTTGACATTCTCTATGGTGTCACTGGGCTCGACCTCCAGGGT\n");
      writer.write("GATGGTCTTGCCTGTCAGGGTCTTCACAAAGATCTGCATGCCACCTCTGAGGCGAAGGAC\n");
      writer.write("CAGGTGCAGGGTGGACTCTTTCTGGATGTTGTAGTCTGACAGGGTGCGGCCATCTTCCAG\n");
      writer.write("CTGCTTGCCTGCAAAGATCAGCCTCTGCTGGTCAGGGGGGATGCCCTCCTTGTCCTGGAT\n");
      writer.write("CTTTGCCTTGACATTCTCTATGGTGTCACTGGGCTCGACCTCCAGGGTGATGGTCTTGCC\n");
      writer.write("TGTCAGGGTCTTCACAAAGATCTGCATGCCACCTCTGAGGCGAAGGACCAGGTGCAGGGT\n");
      writer.write("GGACTCTTTCTGGATGTTGTAGTCTGACAGGGTGCGGCCATCTTCCAGCTGCTTGCCTGC\n");
      writer.write("AAAGATCAGCCTCTGCTGGTCAGGGGGGATGCCCTCCTTGTCCTGGATCTTTGCCTTGAC\n");
      writer.write("ATTCTCAATGGTGTCACTGGGCTCGACCTCCAGGGTGATGGTCTTGCCTGTCAGGGTCTT\n");
      writer.write("CACAAAGATCTGCATGCCACCTCTGAGGCGAAGGACCAGGTGCAGGGTGGACTCTTTCTG\n");
      writer.write("GATGTTGTAGTCTGACAGGGTGCGGCCATCTTCCAGCTGCTTGCCTGCAAAGATCAGCCT\n");
      writer.write("CTGCTGGTCAGGGGGGATGCCCTCCTTGTCCTGGATCTTTGCCTTGACATTCTCTATGGT\n");
      writer.write("GTCACTGGGCTCGACCTCCAGGGTGATGGTCTTGCCTGTCAGGGTCTTCACAAAGATCTG\n");
      writer.write("CATGCCACCTCTGAGGCGAAGGACCAGGTGCAGGGTGGACTCTTTCTGGATGTTGTAGTC\n");
      writer.write("TGACAGCTCNA\n");
      writer.write("-----------------------------------------------\n");
      writer.write(">TC157205 R26445_1 {Homo sapiens}\n");
      writer.write("TTCGGATCCTTGGCGCGCGGGGCCGCCAGATGTGCAGGCATTGGAGGCGGAGGAGAAAGA\n");
      writer.write("GATGGACACCCCAGACTCAGCCTCAAGGGTCTTCTGTGGCCGCTTCCTCAGCATGGTGAA\n");
      writer.write("CACTGATGATGTCAATGCCATAATCCTGGCCCAGAAGAATATGCTGGACCGCTTCGAGAA\n");
      writer.write("AACCAACGAGATGCTTCTGAACTTCAACAATCTGTCCAGTGTGCGGCTGCAACAGATGAG\n");
      writer.write("TGAGCGCTTCATGCACCATACCCGCACCTTGGTGGACATGAAACGGGATCTGGACAGCAT\n");
      writer.write("TTTCCGAAGATCCAGGACACTGAAGGGGAAGCTAGCCCGGCAGCACCCAGAGGCCTTCAG\n");
      writer.write("CCACATCCCAGAGGGTTCATTCCTAGAGGATGAAGATGAAGACCCTATCCCACCCAGTAT\n");
      writer.write("CACCACAACCATCGCCACCTCGGAGCAGAGCACGGGCTCCTGTGACACCAGCCCTGACAC\n");
      writer.write("GGTCTCGCCATCCCTAAGTCCTGGCTTTGAGGACCTATCACACATTCAACCTGGCTCTCC\n");
      writer.write("TGCCATCAACGGCCACAGACAGACAGATGATGAGGAAGAGACACATGAAGAATAGCCTCC\n");
      writer.write("CTCCCACCATCCCAGAAAGGCTCAGGGCAGCCGTGATCCTCCTAGGGTCACAGTCGGTCA\n");
      writer.write("GCTTGGCTGCTCCATTCTTTCATAGGCAGGATTCCTAATTCCTGCCACTCCCCCTAAGAG\n");
      writer.write("CAGGACTTCCCTATGCCAGCACATGTGCCCCAGGTGGGGCTGAGTACAGGTAACCTTGAG\n");
      writer.write("CTCAGCTTGGGCCAGCTTTGAACAGTGCTTCTGAGGTCCCCAGAGACCAAAGCCAGATCG\n");
      writer.write("TCTGTCCCCCACACCTTAGGTGGGCACAGGAGGGCCGAGCTTGTTTCTACCCACTGGCAG\n");
      writer.write("CTGCTGACTCAACTCTGGGCTCCCTGCCATCTGTGGAGTACTTTTTTCCTGTTGTGTTGA\n");
      writer.write("ATACTGGAGAAATAGAAGGTTCAAGATAGAGGCAGACCTGACTGTTTCTTGAGGGTCCAT\n");
      writer.write("CAGGCATCTCCAGGAGCTGGCCAGTTATCTTAGAAAACAACTGAATGGAATATTTTTGTA\n");
      writer.write("CCTGATGTTTCCAGATGTTGGGAAGTTATCAATAAAGACTGTTGTTAAAAGGGAACACTT\n");
      writer.write("ATAGC\n");
      writer.write("-----------------------------------------------\n");
      writer.close();
   }
}