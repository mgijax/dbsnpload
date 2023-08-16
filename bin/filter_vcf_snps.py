#
# Report:
#       filter eva snp file removing uneeded columns and attributes
#	normalizes allele calls in the 36 strain columns and adds B6
#	column
#	1. exclude records with no RS ID
#	2. exclude strains where genotype quality = 0
#	3. B6 always gets REF allele
#	4. Other strains get either REF or ALT allele depending on GT value
#	   see reqs doc
#	5. Do not load heterozygous calls - these should all be GT=0, but we check
#	
# History:
#
# sc    08/14/2023 
#       - updating for new version of MGP with new strains, also to python 3.7 
#
# sc	11/20/2018
#	- formalizing as a dbsnpload preprocessing script
#
# sc	10/20/2018 
#	- modifying as full pre-parser with statistics
#
# sc	8/2/2018
#	- created
#
 
import sys 
import os
import string
import time
import db
import re

CRT = '\n' 
TAB = '\t'

# columns # for the strain genotype (gt) and and genotype quality (fi)
GT = 0
FI = 5

# strains, these are the proper MGI strain names, IMPC column name in comment
s1 = '129P2/OlaHsd' 	# 129P2_OlaHsd (add to snp_strain)
s2 = '129S1/SvImJ'  	# 129S1_SvImJ 	
s3 = '129S5/SvEvBrd' 	# 129S5SvEvBrd (add to snp_strain)
s4 = 'A/J'              # A_J
s5 = 'AKR/J'		# AKR_J 
s6 = 'B10.RIII-H2<r> H2-T18<b>/(71NS)SnJ'      # B10.RIII (new) (add to snp_strain)
s7 = 'BALB/cByJ'        # BALB_cByJ (new) (add to snp_strain)
s8 = 'BALB/cJ'          # BALB_cJ  (add to snp_strain)
s9 = 'BTBR T<+> Itpr3<tf>/J'	# BTBR_T+_Itpr3tf_J (add to snp_strain)
s10 = 'BUB/BnJ' 	# BUB_BnJ
s11 = 'C3H/HeH' 	# C3H_HeH (add to snp_strain)
s12 = 'C3H/HeJ' 	# C3H_HeJ
s13 = 'C57BL/10J'       # C57BL_10J
s14 = 'C57BL/10SnJ'     # C57BL_10SnJ (new) (add to snp_strain)
s15 = 'C57BL/6NJ' 	# C57BL_6NJ (add to snp_strain)
s16 = 'C57BR/cdJ'	# C57BR_cdJ (add to snp_strain)
s17 = 'C57L/J'		# C57L_J 
s18 = 'C58/J' 		# C58_J
s19 = 'CAST/EiJ'        # CAST_EiJ
s20 = 'CBA/J'		# CBA_J
s21 = 'CE/J'            # CE_J (new)
s22 = 'CZECHII/EiJ'     # CZECHII_EiJ (new)
s23 = 'DBA/1J'		# DBA_1J
s24 = 'DBA/2J'		# DBA_2J
s25 = 'FVB/NJ'		# FVB_NJ
s26 = 'I/LnJ'		# l_LnJ
s27 = 'JF1/MsJ'         # JF1_MsJ (new) (add to snp_strain)
s28 = 'KK/HlJ'		# KK_HiJ
s29 = 'LEWES/EiJ'       # LEWES_EiJ (add to snp_strain)
s30 = 'LG/J'            # LG_J (new)
s31 = 'LP/J'    	# LP_J
s32 = 'MA/MyJ'          # MAMy_J (new)
s33 = 'MOLF/EiJ'        # MOLF_EiJ
s34 = 'NOD/ShiLtJ'      # NOD_ShiLtJ
s35 = 'NON/ShiLtJ'      # NON_LtJ (new)
s36 = 'NZB/BlNJ'        # NZB_B1NJ (add to snp_strain)
s37 = 'NZO/HlLtJ'       # NZO_HlLtJ (add to snp_strain)
s38 = 'NZW/LacJ'        # NZW_LacJ 
s39 = 'PL/J'            # PL_J (new)
s40 = 'PWK/PhJ' 	# PWK_PhJ (add to snp_strain)
s41 = 'QSi3/Ianm'       # QSi3 (new) (add to snp_strain)
s42 = 'QSi5/Ianm'       # QSi5 (new) (add to snp_strain)
s43 = 'RF/J'    	# RF_J (add to snp_strain)
s44 = 'RIIIS/J'         # RIIIS_J (new)
s45 = 'SEA/GnJ' 	# SEA_GnJ
s46 = 'SJL/J'           # SJL_J (new)
s47 = 'SM/J'            # SM_J (new)
s48 = 'SPRET/EiJ'       # SPRET_EiJ
s49 = 'ST/bJ'		# ST_bJ   
s50 = 'SWR/J'           # SWR_J (new)
s51 = 'WSB/EiJ'		# WSB_EiJ
s52 = 'ZALENDE/EiJ'	# ZALENDE_EiJ
s53 = 'C57BL/6J'        # new column not in VCF

columnHeader = 'CHROM%sID%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s' % (TAB, TAB, s1, TAB, s2, TAB, s3, TAB, s4, TAB, s5, TAB, s6, TAB, s7, TAB, s8, TAB, s9, TAB, s10, TAB, s11, TAB, s12, TAB, s13, TAB, s14, TAB, s15, TAB, s16, TAB, s17, TAB, s18, TAB, s19, TAB, s20, TAB, s21, TAB, s22, TAB, s23, TAB, s24, TAB, s25, TAB, s26, TAB, s27, TAB, s28, TAB, s29, TAB, s30, TAB, s31, TAB, s32, TAB, s33, TAB, s34, TAB, s35, TAB, s36, TAB, s37, TAB, s38, TAB, s39, TAB, s40, TAB, s41, TAB, s42, TAB, s43, TAB, s44, TAB, s45, TAB, s46, TAB, s47, TAB, s48, TAB, s49, TAB, s50, TAB, s51, TAB, s52, TAB, s53, CRT)

vcfColumnHeader = 'CHROM%sPOS%sID%sREF%sALT%sQUAL%sFILTER%sINFO%sFORMAT%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s' % (TAB, TAB, TAB, TAB, TAB, TAB, TAB, TAB, TAB, s1, TAB, s2, TAB, s3, TAB, s4, TAB, s5, TAB, s6, TAB, s7, TAB, s8, TAB, s9, TAB, s10, TAB, s11, TAB, s12, TAB, s13, TAB, s14, TAB, s15, TAB, s16, TAB, s17, TAB, s18, TAB, s19, TAB, s20, TAB, s21, TAB, s22, TAB, s23, TAB, s24, TAB, s25, TAB, s26, TAB, s27, TAB, s28, TAB, s29, TAB, s30, TAB, s31, TAB, s32, TAB, s33, TAB, s34, TAB, s35, TAB, s36, TAB, s37, TAB, s38, TAB, s39, TAB, s40, TAB, s41, TAB, s42, TAB, s43, TAB, s44, TAB, s45, TAB, s46, TAB, s47, TAB, s48, TAB, s49, TAB, s50, TAB, s51, TAB, s52, CRT)

# list of chromosomes for creating separate files
chrList = str.split(os.getenv('SNP_CHROMOSOMES_TOLOAD'), ',')

#
# dynamically created file pointers one for each c in chrList
#

# fp for the dbsnpload input
inputFileDict = {}

# fp for the edited vcf format for help in testing
vcfFileDict = {}

#
# Counts
#

# current count of rs on this chr (excludes those missing rsIDs
rsCtByChrDict = {} # current count of rs on this chr (excludes those missing rsIDs
totalCt = 0
noRsCt = 0
multiCt = 0	# compound alt allele (contains ',')
fi0Ct = 0	# number of strain alleles with FI='0' or FI='.' genotype quality
fi1hetCt = 0    # number of strain alleles with FI=1 and allele is heterozygous
noAlleleCt = 0  # number of rs with no strain alleles, but FI=1
inFile = sys.stdin
logFile = os.getenv('MGP_VCF_LOG_FILE')

outFileDir = os.getenv('MGP_VCF_OUTPUT_DIR')

def init():
    # Purpose: create file descriptor lookup, open files
    # Returns: 1 if error, else 0
    # Assumes: Nothing
    # Effects: Sets global variables, exits if a file can't be opened,
    #  creates files in the file system

    global inputFileDict, vcfFileDict

    openFiles()

    # initialize the chromosome file pointers and write column header to each
    for c in chrList:
        try:
            inputFileDict[c] = open('%s/chr%s.txt' % (outFileDir, c), 'w')
            inputFileDict[c].write(columnHeader)
            #vcfFileDict[c] = open('%s/chr%s.vcf' % (outFileDir, c), 'w')
            #vcfFileDict[c].write(vcfColumnHeader)
        except:
            return 1
    return 0

def calculateAllele(rsID, chr, strain, ref, alt, gtValue): # reference allele, alt allele(s), GT value
    # Purpose: calculates the allele based on the genotype (GT) value
    # Returns:  the allele
    # Assumes: Nothing
    # Effects: 

    allele = 'z'
    tokens = str.split(alt, ',')
    if gtValue ==  '0/0': # use the REF allele
        allele = ref
    elif gtValue ==  '1/1': # use the first ALT allele
        allele = tokens[0]
    elif gtValue == '2/2': # use the  second ALT allele
        allele = tokens[1]
    elif gtValue == '3/3': # use the  second ALT allele
        allele = tokens[2]
    else:
        fpLog.write('this is an error %s, %s, %s, %s, %s%s' % (rsID, chr, ref, alt, gtValue, CRT))
    
    return allele

def readNextLine():
    # Purpose: convenience function for reading lines

    line = inFile.readline()
    #print(line)
    sys.stdout.flush()

    return line


def writeVcfFile(chr, pos, rsID, ref, alt, qual, filter, info, format, tokens):
    # Purpose: write to the vcf files. These files are for testing purposed
    # writes to individual chromosome files
    # Returns: 0
    # Assumes: file descriptors have been initialized
    # Effects: creates files in the file system
    vcfFileDict[chr].write('%s%s%s%s%s%s%s%s%s%s%s%s%s%s' % (chr, TAB, pos, TAB, rsID, TAB, ref, TAB, alt, TAB, qual, TAB, filter, TAB))

    # INFO column multiple gene example:
    # DP=1778;DP4=684,692,187,215;CSQ=G|ENSMUSG00000051951|ENSMUST00000159265|Transcript|upstream_gene_variant|||||||387|-1,G|ENSMUSG00000051951|ENSMUST00000070533|Transcript|3_prime_UTR_variant|2097|||||||-1,G|ENSMUSG00000051951|ENSMUST00000162897|Transcript|non_coding_transcript_exon_variant&non_coding_transcript_variant|326||||||

# REL-1505 format for rs580370473:
#    DP=1283;AD=177,1104;DP4=153,24,313,793;MQ=47;CSQ=T|intergenic_variant|MODIFIER|||||||||||||||||||SNV||||||||,C|intergenic_variant|MODIFIER|||||||||||||||||||SNV||||||||;AC=102;AN=104

# REL-2112-v8 format for rs580370473:
#    DP=417;DP4=93,1,210,113;CSQ=T||||intergenic_variant||||||||

    # INFO column intergenic_variant example
    # DP=417;DP4=93,1,210,113;CSQ=T||||intergenic_variant||||||||
    #print('info: %s %s' % (info,info.count(';')))
    if info.count(';') > 3: # if two ';' then there is a CSQ section
        temp = str.split(info, ';')
        info = temp[4] # remove DP=417;DP4=93,1,210,113;
    else:
        print('no CSQ section')
        info = ''   # no CSQ section
        newInfo = ''
    # pipe-delim tokens that we want, if CSQ sectin not present then
    # output column will be blank
    # 0 = CSQ
    # 1 = Gene ID
    # 2 = Transcript ID
    # 4 = Fxn Class
    # 7 = Protein Position (AA position)
    # 8 = Amin Acids (Residue)
    # 9 = Codons
    # 12 = Strand
    if info != '':
        # one or more genes ',' delimited
        newInfo = ''
        geneTokens = str.split(info, ',')
        #print('geneTokens: %s ' % geneTokens)
        for t in geneTokens:
            geneTokens = str.split(t, '|')
            newInfo = newInfo + '%s|%s|%s|%s|%s|%s|%s|%s,' % (geneTokens[0], geneTokens[1], geneTokens[2], geneTokens[4], geneTokens[7], geneTokens[8], geneTokens[9], geneTokens[12])
        newInfo = newInfo[:-1]  # strip off final comma

    vcfFileDict[chr].write('%s%s' % (newInfo, TAB))
    gt = 0
    fi = 5
    # Parse format column
    fTokens = str.split(format, ':')
    GT = fTokens[gt]
    FI = fTokens[fi]
    newFormat = '%s:%s%s' % (GT, FI, TAB)
    vcfFileDict[chr].write(newFormat)  #FORMAT

    # parse strain columns
    for col in [9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60]:
        s = tokens[col]
        sTokens = str.split(s, ':')
        GT = sTokens[gt]
        FI = sTokens[fi]

        newStrain = ''
        if col == 60:
             newStrain = '%s:%s%s' % (GT, FI, CRT)
        else:
            newStrain = '%s:%s%s' % (GT, FI, TAB)
        vcfFileDict[chr].write(newStrain)
    return 0

def processFile():
    # Purpose: main function of the script
    # iterates thru the vcf file parsing values to calculate allele calls for strains
    # writes to individual chromosome files
    # Returns: 0
    # Assumes: file descriptors have been initialized
    # Effects: creates files in the file system

    global totalCt, noRsCt, multiCt, fi0Ct, fi1hetCt, noAlleleCt
    pattern = r'[^-\t]'
    line = readNextLine()
    while line:
        line = str.strip(line)
        newLine = ''
        if str.find(line, '#') == 0:
            line = readNextLine()
            continue
        else:
            totalCt += 1
            tokens = str.split(line, TAB)
            chr = tokens[0]
            pos = tokens[1]  	# vcf file only
            rsID = tokens[2]
            
            if rsID == '.':	# no RS ID
                noRsCt += 1
                line = readNextLine()
                continue
            rsID = rsID[2:] # strip off the 'rs' prefix

            # for debug so we can develop on one chromosome 
            # if desired
            #if chr not in inputFileDict  r chr not in vcfFileDict:
            #    continue

            #inputFileDict[chr].write('%s%s%s%s' % (chr, TAB, rsID, TAB))
            newLine = newLine + '%s%s%s%s' % (chr, TAB, rsID, TAB)
            ref = tokens[3]
            alt = tokens[4]
            qual = tokens[5]
            filter = tokens[6]
            if str.find(alt, ',') != -1: # multiple alleles
                multiCt += 1
            info = tokens[7]
            format = tokens[8]
            #writeVcfFile(chr, pos, rsID, ref, alt, qual, filter, info, format, tokens)

            # parse strain columns
            strainPart = ''
            for col in [9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60]:
                s = tokens[col]
                #print('col: %s' % col)
                #print('s: %s' % s)
                sTokens = str.split(s, ':')
                #print('sTokens: %s' % sTokens)
                fi = sTokens[FI]
                gt = sTokens[GT]
                #print( '%s %s %s %s%s' % (chr, rsID, gt, fi, CRT))
                allele = '-'
                if fi != '1':
                    fi0Ct += 1
                else:
                    if gt == '0/1': # heterzygous, report it
                        fi1hetCt += 1 
                        fpLog.write('genotype quality is 1, but allele is heterzygous: %s strain column %s%s' % (rsID, col, CRT))
                    else:
                        allele = calculateAllele(rsID, chr, col, ref, alt, gt)
                #inputFileDict[chr].write('%s%s' % (allele, TAB))
                strainPart = strainPart + '%s%s' % (allele, TAB)
            if re.search(pattern, strainPart): # no alleles for any MGP strains
                newLine = newLine + strainPart
                # now add column for B6
                newLine = newLine + '%s%s' % (ref, CRT)
                inputFileDict[chr].write(newLine)
                if chr not in rsCtByChrDict:
                     rsCtByChrDict[chr] = 1
                else:
                    rsCtByChrDict[chr] += 1
            else:
                noAlleleCt += 1
        line = readNextLine()

    return 0

def writeLog():
    # Purpose: writes stats to the log file

    fpLog.write('\nTotal: %s\n' % totalCt)
    fpLog.write('Total with no RS ID: %s\n' % noRsCt)
    fpLog.write('Total with RS ID: %s\n' % (totalCt - noRsCt))
    fpLog.write('Total RS with no alleles for any strain %s\n' % noAlleleCt)
    fpLog.write('Total ALT multi-valued allele: %s\n' % multiCt)
    fpLog.write('Total strain alleles with genotype quality = 0: %s\n' % fi0Ct)
    fpLog.write('Total heterozygous strain alleles with genotype quality = 1: %s\n' % fi1hetCt)
    fpLog.write('Counts of RS by Chromosome\n')
    for c in rsCtByChrDict:
        fpLog.write('  chr%s: %s\n' % (c, rsCtByChrDict[c]))
    return 0

def openFiles():
    # Purpose: Open input/output files.
    # Returns: 1 if error, else 0
    # Assumes: Nothing
    # Effects: Sets global variables, exits if a file can't be opened,
    #  creates files in the file system

    global fpLog

    try:
        fpLog = open(logFile, 'a+')
    except:
        return 1
    return 0

def closeFiles():
    # Purpose: Close all file descriptors
    # Returns: 1 if error, else 0
    # Assumes: all file descriptors were initialized
    # Effects: Nothing
    # Throws: Nothing

    try:
        fpLog.close()
    except:
        return 1
    return 0

#
#  MAIN
#

if init() != 0:
    sys.exit(1)

if processFile() != 0:
    sys.exit(1)

if writeLog() != 0:
    sys.exit(1)

if closeFiles() != 0:
    sys.exit(1)
