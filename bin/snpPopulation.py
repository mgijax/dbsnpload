#!/usr/local/bin/python

'''
#
# Purpose:
#
# Create bcp files for SNP_Population 
# ACC_Accession (associate popids with SNP_Population objects)
#
# Uses environment variables to determine Server and Database
# (DSQUERY, MGD, and RADAR_DBNAME).
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

# to create ACC_Accession for handles
#hdlLdbKey = 0
#hdlmgiTypeKey = 0

# current max+1 _Accession_key
acckey = 0

# to create SNP_Population and ACC_Accession
# record for submitter Handle
handleVocabKey = 0

# resolves a handle to its key
handleKeyLookup = {}

# bcp file objects
print "%s/%s.bcp" % (outputdir, popTable)
popBCP = open('%s/%s.bcp' % (outputdir, popTable), 'w')
accBCP = open('%s/%s.pop.bcp' % (outputdir, accTable), 'w')

# submitter handle vocab input file, created then run
#handleFile = open(os.environ['HANDLE_VOCAB_FILE'], 'w')
def setup():
	# resolve logicalDB and MGIType
	# get the max accession key
	global snpPopLdbKey
	global snpPopmgiTypeKey
	#global hdlLdbKey
	#global hdlmgiTypeKey 
	global acckey
	global handleVocabKey
	global handleKeyLookup
	       # set up connection the the database
        server = os.environ['MGD_DBSERVER']
        mgdDB = os.environ['MGD_DBNAME']
        user = os.environ['MGD_DBUSER']
        password = string.strip(open(os.environ['MGD_DBPASSWORDFILE'], 'r').readline())
        db.set_sqlLogin(user, password, server, mgdDB)
        #print "accKey %s" % acckey
        print "%s, %s, %s, %s" % (server, mgdDB, user, password)

	cmds = []
	#cmds.append('select max(_Accession_key) ' + \
        #       'from ACC_Accession')
	#results = db.sql(cmds, 'auto')
	#acckey = results[0][0]['']
	#snpPopLdbKey = 76
	#snpPopmgiTypeKey = 32 
	#handleVocabKey = 47
	#hdlLdbKey = 77
	#hdlmgiTypeKey = 13
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
#	cmds.append('select _LogicalDB_key ' + \
#                'from ACC_LogicalDB ' + \
#                'where name = "%s" ' % os.environ['HANDLE_LOGICALDB_NAME'])
#        cmds.append('select _MGIType_key ' + \
#                'from ACC_MGIType ' + \
#                'where name = "%s" ' % os.environ['HANDLE_MGITYPE_NAME'])
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
	#print r['term']
	#print r['_Term_key']
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
	print 'Creating %s/%s.bcp...%s' % (outputdir, popTable, mgi_utils.date())
	print 'and  %s/%s.bcp...%s' % (outputdir, accTable, mgi_utils.date())
	inFile = open(os.environ['POP_FILE'], 'r')
	primaryKey = 0
	
	line = string.strip(inFile.readline())
	# line looks like:
	# <Population popId="1064" handle="ROCHEBIO" locPopId="RPAMM">
	while line:
	    #print line
	    # remove <>
	    line = line[1:-1]
	    #print line	
	    tokens = string.split(line)
	    popName = ''
	    handle = ''
	    popId = ''
	    handleKey = ''
	    for token in tokens:
		if string.find(token, '=') != -1:
			attrs= string.split(token, '=')
			key = string.strip(attrs[0])
			#print key
			# remove quotes around value
			value = string.strip(attrs[1][1:-1])
			#print value
			if key == 'popId':
			    popId = value
			elif key == 'handle':
                            handle = value
			elif key == 'locPopId':
			    popName = value
	    #print 'popName: %s' % popName
	    #print 'handle: %s' % handle
	    #print 'popId: %s' % popId
	    if handleKeyLookup.has_key(handle):
    	        handleKey = handleKeyLookup[handle]
	    #print 'handleKey: %s' % handleKey
	    if popName == '' or handle == '' or popId == '' or handleKey == '':
		sys.exit("Not all tokens present on line %s" % line)
	    primaryKey = primaryKey + 1
	    #print 'primaryKey: %s' % primaryKey
	    bcpLine = str(primaryKey) + DL + \
		str(handleKey) + DL + \
                str(popName) + DL + \
                str(userKey) + DL + str(userKey) + DL + \
                loaddate + DL + loaddate + NL
	    #print bcpLine
	    popBCP.write(bcpLine)
	    #handleFile.write("%s%s%s%s%s%s%s%s%s" % (handle, TAB, handle, 
		#TAB, TAB, TAB, TAB, TAB, NL)) 
	    createAccession(popId, primaryKey, snpPopLdbKey, snpPopmgiTypeKey) 
	    # following now being done via vocload
	    #createAccession(handle, handleKey, hdlLdbKey, hdlmgiTypeKey)
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
# following now being done via vocload
#print 'deleting handle accessions'
#deleteAccessions(hdlmgiTypeKey, hdlLdbKey)
createBCP()
print '%s' % mgi_utils.date()

