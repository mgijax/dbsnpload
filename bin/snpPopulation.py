#!/usr/local/bin/python

'''
#
# Purpose:
#
# Create bcp files for SNP_Population 
# ACC_Accession (associate popids with SNP_Population objects)
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
import loadlib
import string
import accessionlib

NL = '\n'
DL = '|'
TAB = '\t'
outputdir = os.environ['OUTPUTDIR']
popTable = os.environ['POP_TABLE']
accTable = os.environ['ACC_TABLE']
userKey = 0
loaddate = loadlib.loaddate

# to create ACC_Accession ofor popIds
snpPopLdbKey = 0
snpPopmgiTypeKey = 0

# current max+1 _Accession_key
acckey = 0

# to create SNP_Population and ACC_Accession
# record for submitter Handle
handleVocabKey = 0

# resolves a handle to its key
handleKeyLookup = {}

# bcp file objects
popBCP = open('%s/%s.bcp' % (outputdir, popTable), 'w')
accBCP = open('%s/%s.pop.bcp' % (outputdir, accTable), 'w')

def setup():
	# resolve logicalDB and MGIType
	# get the max accession key
	global snpPopLdbKey
	global snpPopmgiTypeKey
	global acckey
	global handleVocabKey
	global handleKeyLookup
	       # set up connection the the database
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
	cmds.append('select max(_Accession_key) ' + \
		'from ACC_Accession')
	cmds.append('select _Vocab_key ' + \
		'from VOC_Vocab ' + \
		'where name = "%s" ' % os.environ['HANDLE_VOCAB_NAME'])
	results = db.sql(cmds, 'auto')
        snpPopLdbKey = results[0][0]['_LogicalDB_key']
	snpPopmgiTypeKey = results[1][0]['_MGIType_key']
	acckey = results[2][0]['']
	handleVocabKey = results[3][0]['_Vocab_key']
	#hdlLdbKey = results[4][0]['_LogicalDB_key']
	#hdlmgiTypeKey = results[5][0]['_MGIType_key']
	
	# create a submitter handle key lookup
	createHandleLookup()

def createHandleLookup():
    print 'Creating Handle Lookup'
    global handleKeyLookup
    cmds = []
    cmds.append('select _Term_key, term ' + \
	'from VOC_Term ' + \
	'where _Vocab_key = %s' % handleVocabKey)
    results = db.sql(cmds, 'auto')
    for r in results[0]:
	handleKeyLookup[r['term']] = r['_Term_key']

def deleteAccessions(mgiTypeKey, ldbKey):
    cmds = []
    cmds.append('select a._Accession_key ' + \
    'into #todelete ' + \
    'from ACC_Accession a ' + \
    'where a._MGIType_key = %s ' % mgiTypeKey + \
    'and a._LogicalDB_key = %s' % ldbKey)

    cmds.append('create index idx1 on #todelete(_Accession_key)')

    cmds.append('delete ACC_Accession ' + \
    'from #todelete d, ACC_Accession a ' + \
    'where d._Accession_key = a._Accession_key')

    results = db.sql(cmds, 'auto')

def createBCP():
	print 'Creating %s/%s.bcp' % (outputdir, popTable)
	print 'and  %s/%s.bcp' % (outputdir, accTable)
	inFile = open(os.environ['POP_FILE'], 'r')
	primaryKey = 0
	
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
		sys.exit("Not all tokens present on line %s" % line)
	    primaryKey = primaryKey + 1
	    bcpLine = str(primaryKey) + DL + \
		str(handleKey) + DL + \
                str(popName) + NL
	    popBCP.write(bcpLine)
	    createAccession(popId, primaryKey, snpPopLdbKey, snpPopmgiTypeKey) 
	    line = string.strip(inFile.readline())

	popBCP.close()
	accBCP.close()
	#handleFile.close()

def createAccession(accid, objectKey, ldbKey, mgiTypeKey):
    global acckey
    acckey = acckey + 1
    prefixpart, numericpart = accessionlib.split_accnum(accid)
    accBCP.write(str(acckey) + DL + \
	str(accid) + DL + \
	str(prefixpart) + DL + \
	str(numericpart) + DL + \
	str(ldbKey) + DL + \
	str(objectKey) + DL + \
	str(mgiTypeKey) + DL + \
	str(0) + DL + \
	str(1)+ DL + \
	str(userKey) + DL + str(userKey) + DL + \
	loaddate + DL + loaddate + NL)
#
# Main Routine
#

userKey = loadlib.verifyUser(os.environ['MGD_DBUSER'], 1, None)

print '%s' % mgi_utils.date()
setup()
print 'deleting population accessions'
deleteAccessions(snpPopmgiTypeKey, snpPopLdbKey)
createBCP()
print '%s' % mgi_utils.date()

