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
import db
import mgi_utils
import string
import accessionlib

NL = '\n'
DL = '|'
TAB = '\t'
outputdir = os.environ['OUTPUTDIR']
popTable = os.environ['POP_TABLE']
accTable = os.environ['ACC_TABLE']

# to create SNP_Accession ofor popIds
snpPopLdbKey = 0
snpPopmgiTypeKey = 0

# to create SNP_Population and SNP_Accession
# record for submitter Handle
handleVocabKey = 0

# resolves a handle to its key
handleKeyLookup = {}

# current max _Accession_key
accKey = 0

# bcp file objects
popBCP = open('%s/%s.bcp' % (outputdir, popTable), 'w')
accBCP = open('%s/%s.pop.bcp' % (outputdir, accTable), 'w')

def setup():
	# ACC_Accession._logicalDB_key for Population id
	global snpPopLdbKey

	# ACC_Accession._MGIType_key for SNP_Population
	global snpPopmgiTypeKey

	# VOC_Vocab._Vocab_key for submitterHandle vocabulary
	global handleVocabKey

        # set up connection to the mgd database
        server = os.environ['MGD_DBSERVER']
        mgdDB = os.environ['MGD_DBNAME']
        user = os.environ['MGD_DBUSER']
        password = string.strip(open(os.environ['MGD_DBPASSWORDFILE'], 'r').readline())
        db.set_sqlLogin(user, password, server, mgdDB)

	cmds = []
	cmds.append('select _LogicalDB_key ' + \
		'from ACC_LogicalDB ' + \
		'where name = "%s" ' % os.environ['POP_LOGICALDB_NAME'])
	cmds.append('select _MGIType_key ' + \
                'from ACC_MGIType ' + \
                'where name = "%s" ' % os.environ['POP_MGITYPE_NAME'])
	cmds.append('select _Vocab_key ' + \
		'from VOC_Vocab ' + \
		'where name = "%s" ' % os.environ['HANDLE_VOCAB_NAME'])
	results = db.sql(cmds, 'auto')

        snpPopLdbKey = results[0][0]['_LogicalDB_key']
	snpPopmgiTypeKey = results[1][0]['_MGIType_key']
	handleVocabKey = results[2][0]['_Vocab_key']
	
	# create a submitter handle key lookup
	createHandleLookup()

	# get accKey
	getSnpAccessionKey()


def createHandleLookup():
    # dictionary mapping subHandle terms to _Term_keys 
    global handleKeyLookup

    print 'Creating Handle Lookup'
    cmds = []
    cmds.append('select _Term_key, term ' + \
	'from VOC_Term ' + \
	'where _Vocab_key = %s' % handleVocabKey)
    results = db.sql(cmds, 'auto')
    for r in results[0]:
	handleKeyLookup[r['term']] = r['_Term_key']

def getSnpAccessionKey():

	# current SNP_Accession max(_Accession_key)
	global accKey

        print 'Getting max SNP_Accession key'
	# set up connection to the snp database
        server = os.environ['SNPBE_DBSERVER']
        snpDB = os.environ['SNPBE_DBNAME']
        user = os.environ['SNPBE_DBUSER']
        password = string.strip(open(os.environ['SNPBE_DBPASSWORDFILE'], 'r').readline())
        db.set_sqlLogin(user, password, server, snpDB)

        cmd = 'select max(_Accession_key) as accMax ' + \
                'from SNP_Accession'
	results = db.sql(cmd, 'auto')
	accKey = results[0]['accMax']
	# if the table is empty, set accMax to 0
	if accKey == None:
	    accKey = 0

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
	    tokens = string.split(line)
	    popName = ''
	    handle = ''
	    popId = ''
	    handleKey = ''
	    for token in tokens:
		if string.find(token, '=') != -1:
			attrs= string.split(token, '=')
			key = string.strip(attrs[0])
			# remove quotes around value
			value = string.strip(attrs[1][1:-1])
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
	    bcpLine = str(primaryKey) + DL + \
		str(handle) + DL + \
		str(handleKey) + DL + \
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
    accBCP.write(str(accKey) + DL + \
	str(accid) + DL + \
	str(prefixpart) + DL + \
	str(numericpart) + DL + \
	str(ldbKey) + DL + \
	str(objectKey) + DL + \
	str(mgiTypeKey) + NL)
#
# Main Routine
#

print '%s' % mgi_utils.date()
setup()
print 'deleting population accessions'
createBCP()
print '%s' % mgi_utils.date()

