#!/usr/local/bin/python
'''
#
# Purpose:
#
# Drops and reloads SNP_Transcript_Protein by migrating
# distinct RefSeq pairs from DP_SNP_Marker
#
# Usage:
#       migrateRefSeqs.py
#
# History
#
# 08/25/2005    sc
#       - dbSNP build142 (TR11937)
#
'''

import sys
import os
import string
import db

NL = '\n'
TAB = '\t'
DL = TAB
db.useOneConnection(1)

# next available primary key
primaryKey = 1
fp = open(os.environ['REFSEQ_BCP'], 'w')

results = db.sql('''select distinct refseqnucleotide, refseqprotein
    from DP_SNP_Marker''', 'auto')

for r in results:
    nId = r['refseqnucleotide']
    pId = r['refseqprotein']
    if pId == None:
	pId = ''
    fp.write('%s%s%s%s%s%s' % (primaryKey, DL, nId, DL, pId, NL))
    primaryKey  += 1

fp.close()

db.useOneConnection(0)
