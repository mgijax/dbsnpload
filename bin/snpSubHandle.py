#!/usr/local/bin/python

'''
#
# Purpose:
#
# Create vocload input file for submitter handle
#
# Usage:
#	snpSubHandle.py
#
# History
#
# 08/31/2005	sc
#	- SNP (TR 1560)
#
'''

import sys
import os
import db
import mgi_utils
import loadlib
import string

NL = '\n'
DL = '|'
TAB = '\t'
userKey = 0
loaddate = loadlib.loaddate

# submitter handle vocab input file, created then run
handleFileName = os.environ['HANDLE_VOCAB_FILE']
handleFile = open(handleFileName, 'w')

def createVocabFile():
	print 'Creating %s' % handleFileName
	# input file
	inFile = open(os.environ['INT_HANDLE_VOCAB_FILE'], 'r')
	line = string.strip(inFile.readline())
	while line:
	    handleFile.write("%s%s%s%s%s%s%s%s%s%s" % (line, TAB, line, TAB, TAB, TAB, TAB, TAB, TAB, NL))
	    line = string.strip(inFile.readline())
	handleFile.close()

#
# Main Routine
#

userKey = loadlib.verifyUser(os.environ['MGD_DBUSER'], 1, None)

print '%s' % mgi_utils.date()
createVocabFile()
print '%s' % mgi_utils.date()

