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
#      - SNP_Strain records updated
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
import os
import string
import reportlib
import pg_db
db = pg_db

CRT = reportlib.CRT
SPACE = reportlib.SPACE
TAB = reportlib.TAB
PAGE = reportlib.PAGE

outFile = open(os.environ['SNP_STRAIN_FILE'], 'w')
inFile = open(os.environ['MGI_STRAINORDER_FILE'], 'r')

# strains in snp..SNP_Strain {strain:_Strain_key}
snpStrainDict = {}

# strains from the strain order input file {strain:sequenceNum}
snpStrainOrderDict = {}

# turn of tracing statements
db.setTrace(True)
password = db.get_sqlPassword()

print 'connecting to database...\n'
sys.stdout.flush()

# set up connection to the mgd database
db.useOneConnection(1)

# Get postgres output, don't translate to old db.py output
db.setReturnAsSybase(False)

print 'querying for snp strains in mgi...\n'
# query for the SNP strains
results = db.sql('''
	select strain, _mgdStrain_key
    	from SNP_Strain
    	order by strain
	''', 'auto')

print 'reporting strains in MGI to %s%s' % (os.environ['SNP_STRAIN_FILE'], CRT)
for r in results[1]:
    snpStrainDict[ r[0] ] = r[1]
    outFile.write("%s%s%s%s" % (r[0], TAB, r[1], CRT))

print 'reading the strain order input file ...\n'
sequenceNum = 0
line = string.strip(inFile.readline())
while line:
    sequenceNum = sequenceNum + 1
    snpStrainOrderDict[line] = sequenceNum
    line = string.strip(inFile.readline())
outFile.close()
inFile.close()

# QC what is in the database against the input file
print 'qc of snp strains in MGI against the strain order file ... \n'
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
    print "Writing discrepancies to %s\n" % (discrepFile)
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
    sql = '''
	update SNP_Strain
	set sequenceNum = %s
	where _mgdStrain_key = %s 
 	''' % (sequenceNum, strainKey)
    print sql
    db.sql(sql, None)
    db.commit()

