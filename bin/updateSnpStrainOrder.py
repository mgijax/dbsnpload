#!/usr/local/bin/python

'''
#
# updateSnpStrainOrder.py 
#
# Purpose:
#       strains and strain keys for strains in the SNP Strain MGI_Set
#
# Usage:
#       updateSnpStrainOrder.py
#
#  Env Vars:
#
#      See the configuration file
#
#  Inputs:
#	- file of snp strain ordering 
#	- set of SNP strains from a database
#       - Common configuration file (/usr/local/mgi/etc/common.config.sh)
#       - dbsnpload.config
#  Outputs:
#      - Log files defined by the environment variables 
#		- ${LOG_STRAIN} - general log 
#		-${LOG_DISCREP} - discrepancies between MGI and the 
#			order input file
#		- ${SNP_STRAIN_FILE} - list of snp strains in MGI               
#      - MGI_SetMember database records updated
#  Exit Codes:
#
#      0:  Successful completion
#      1:  Fatal error occurred
#      2:  Non-fatal error occurred
#
#  Assumes:  Nothing
#
#  Implementation:
#
# Notes:
#
# History:
#
# sc	10/21/2005
#	- created
#
'''
 
import sys 
import db
import reportlib
import os
import string

CRT = reportlib.CRT
SPACE = reportlib.SPACE
TAB = reportlib.TAB
PAGE = reportlib.PAGE

outFile = open(os.environ['SNP_STRAIN_FILE'], 'w')
inFile = open(os.environ['MGI_STRAINORDER_FILE'], 'r')

snpStrainSetName = os.environ['SNP_STRAIN_SET_NAME']
strainSetKey = ""
snpStrainDict = {}
snpStrainOrderDict = {}

print 'querying for snp strains in mgi...%s' % CRT
cmds = []
# query for the SNP Strain _Set_key
cmds.append('select _Set_key ' + \
    'from MGI_Set ' + \
    'where name = "%s"' % snpStrainSetName)

# query for the SNP Strain Set members 
cmds.append('select ps.strain, ms._Object_key as _Strain_key ' + \
    'from MGI_SetMember ms, PRB_Strain ps ' + \
    'where ms._Set_key = 1023 ' + \
    'and ms._Object_key = ps._Strain_key ' + \
    'order by ps.strain')

results = db.sql(cmds, 'auto')

strainSetKey = results[0][0]['_Set_key']
if strainSetKey == "":
    sys.exit("Can't resolve SNP Strain Set name: %s" % snpStrainSetName)

print 'reporting strains in MGI to %s%s' % (os.environ['SNP_STRAIN_FILE'], CRT)
for r in results[1]:
    snpStrainDict[ r['strain'] ] = r['_Strain_key']
    outFile.write("%s%s%s%s" % (r['strain'],  TAB, r['_Strain_key'], CRT) )

print 'reading the strain order input file ...%s' % CRT 
sequenceNum = 0
line = string.strip(inFile.readline())
while line:
    sequenceNum = sequenceNum + 1
    snpStrainOrderDict[line] = sequenceNum
    line = string.strip(inFile.readline())
outFile.close()
inFile.close()

# QC what is in the database against the input file
print 'qc of snp strains in MGI against the strain order file ... %s' % CRT
snpStrainsList = snpStrainDict.keys()
snpStrainNotInOrderList = []
snpStrainOrderList = snpStrainOrderDict.keys()
orderStrainNotInSnpList = []

for strain in snpStrainsList:
    if strain not in snpStrainOrderList:
    	snpStrainNotInOrderList.append(strain)

for strain in snpStrainOrderList:
    if strain not in snpStrainsList:
	orderStrainNotInSnpList.append(strain)

if len(snpStrainNotInOrderList) > 0 or len(orderStrainNotInSnpList) > 0:
    discrepFile = os.environ['LOG_DISCREP']
    print "Writing discrepancies to %s%s" % (discrepFile, CRT)
    logDiscrep = open(discrepFile, 'w')
    logDiscrep.write("Snp Strains in MGI not in Order File: %s" % CRT)
    print "Snp Strains in MGI not in Order File:" 
    for strain in snpStrainNotInOrderList:
	logDiscrep.write("%s%s" % (strain, CRT) )
	print strain
    print "" 
    logDiscrep.write("Order File Strain not in Snp Strains in MGI: %s" % CRT)
    print "Order File Strain not in Snp Strains in MGI:"
    for strain in orderStrainNotInSnpList:
        logDiscrep.write("%s%s" % (strain, CRT) )
	print strain
    print ""
    logDiscrep.close()
    sys.exit(1)
print "updating SNP strain order"
for strain in snpStrainOrderDict.keys():
    sequenceNum = snpStrainOrderDict[strain]
    strainKey = snpStrainDict[strain]
    cmds = []
    cmds.append('update MGI_SetMember ' + \
	'set sequenceNum = %s ' % sequenceNum + \
	'where _Set_key = %s ' % strainSetKey + \
	'and _Object_key = %s ' % strainKey)
    results = db.sql(cmds, 'auto')