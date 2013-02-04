#!/usr/local/bin/python

'''
#
# Purpose:
#
# Create bcp files for SNP_Population 
# SNP_Accession (associate popids with SNP_Population objects)
#
# Uses environment variables to determine Server and Database
# (DSQUERY, MGD).
#
# Usage:
#	snpPopulation.py
#
# History
#
# 08/25/2005	sc
#	- SNP (TR 1560)
#
'''

import sys
import os
import string
import mgi_utils
import accessionlib
import pg_db
db = pg_db

NL = '\n'
DL = '|'
TAB = '\t'
outputdir = os.environ['OUTPUTDIR']
popTable = os.environ['POP_TABLE']
accTable = os.environ['ACC_TABLE']

# to create SNP_Accession ofor popIds
snpPopLdbKey = 76
snpPopmgiTypeKey = 33

# to create SNP_Population and SNP_Accession
# record for submitter Handle
handleVocabKey = 51

# resolves a handle to its key
handleKeyLookup = {}

# current max _Accession_key
accKey = 0

# bcp file objects
popBCP = open('%s/%s.bcp' % (outputdir, popTable), 'w')
accBCP = open('%s/%s.pop.bcp' % (outputdir, accTable), 'w')

def setup():
    	# dictionary mapping subHandle terms to _Term_keys 
    	global handleKeyLookup
	# current SNP_Accession max(_Accession_key)
	global accKey

    	# turn of tracing statements
    	db.setTrace(True)

    	password = db.get_sqlPassword()

    	print 'connecting to database...%s' % NL
    	sys.stdout.flush()

    	# set up connection to the mgd database
    	db.useOneConnection(1)

    	# Get postgres output, don't translate to old db.py output
    	db.setReturnAsSybase(False)

    	print 'Creating Handle Lookup'
	sys.stdout.flush()
    	results = db.sql('''
    		SELECT _Term_key, term FROM mgd.VOC_Term where _Vocab_key = %s
		''' % (handleVocabKey), 'auto')
    	for r in results[1]:
		handleKeyLookup[r[1]] = r[0]
	print handleKeyLookup

	print 'Delete from SNP_Accession...'
	sys.stdout.flush()
	db.sql('delete from SNP_Accession where _mgitype_key = 33', None)
	db.commit()

    	print 'Creating SNP_Accession max key'
	sys.stdout.flush()
	results = db.sql('''SELECT max(_Accession_key) as maxKey FROM SNP_Accession''', 'auto')
	accKey = results[1][0][0]

def createBCP():

	print 'Creating %s/%s.bcp' % (outputdir, popTable)
	print 'and  %s/%s.bcp' % (outputdir, accTable)

	inFile = open(os.environ['POP_FILE'], 'r')

	primaryKey = 0
	problemHandles = ''
	line = string.strip(inFile.readline())

	# line looks like:
	# <Population popId="1064" handle="ROCHEBIO" locPopId="RPAMM">

	while line:

	    # remove <>
	    line = line[1:-1]
	    line = line.replace('Acadl SNP variants', 'Acadl_SNP_variants')
	    tokens = string.split(line)

	    popName = ''
	    handle = ''
	    popId = ''
	    handleKey = ''

	    for token in tokens:
		if string.find(token, '=') != -1:
			attrs = string.split(token, '=')
			key = string.strip(attrs[0])
			# remove quotes around value
			value = string.strip(attrs[1][1:-1])
	                value = value.replace('Acadl_SNP_variants', 'Acadl SNP variants')
			if key == 'popId':
			    popId = value
			elif key == 'handle':
                            handle = value
			elif key == 'locPopId':
			    popName = value

	    if handleKeyLookup.has_key(handle):
    	        handleKey = handleKeyLookup[handle]

	    if popName == '' or handle == '' or popId == '' or handleKey == '':
		problemHandles = '%s%s%s' % (problemHandles, NL, line)

	    primaryKey = primaryKey + 1

	    bcpLine = str(primaryKey) + TAB + \
		str(handle) + TAB + \
		str(handleKey) + TAB + \
                str(popName) + NL

	    popBCP.write(bcpLine)

	    createAccession(popId, primaryKey, snpPopLdbKey, snpPopmgiTypeKey) 

	    line = string.strip(inFile.readline())

	popBCP.close()
	accBCP.close()

	if problemHandles != '':
	    sys.exit('Problem Handles %s' % problemHandles)

def createAccession(accid, objectKey, ldbKey, mgiTypeKey):
    global accKey

    accKey = accKey + 1
    prefixpart, numericpart = accessionlib.split_accnum(accid)

    accBCP.write(str(accKey) + TAB + \
	str(accid) + TAB + \
	str(prefixpart) + TAB + \
	str(numericpart) + TAB + \
	str(ldbKey) + TAB + \
	str(objectKey) + TAB + \
	str(mgiTypeKey) + NL)
#
# Main Routine
#

print '%s' % mgi_utils.date()
setup()
createBCP()
print '%s' % mgi_utils.date()

