
'''
#
# Purpose:
#
# Create vocload input file for submitter handle
#
# Uses environment variables to determine Server and Database
# (DSQUERY and MGD).
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
import string

NL = '\n'
DL = '|'
TAB = '\t'
userKey = 0

# submitter handle vocab input file, created then run
handleFileName = os.environ['HANDLE_VOCAB_FILE']
handleFile = open(handleFileName, 'w')

def createVocabFile():
        print('Creating %s' % handleFileName)
        # input file
        inFile = open(os.environ['INT_HANDLE_VOCAB_FILE'], 'r')
        line = str.strip(inFile.readline())
        while line:
            handleFile.write("%s%s%s%s%s%s%s%s%s%s" % (line, TAB, line, TAB, TAB, TAB, TAB, TAB, TAB, NL))
            line = str.strip(inFile.readline())
        handleFile.close()

#
# Main Routine
#
print("MGD_DBUSER %s" % os.environ['MGD_DBUSER'])

print('%s' % mgi_utils.date())
createVocabFile()
print('%s' % mgi_utils.date())
