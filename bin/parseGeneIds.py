#!/usr/bin/python
#
#	parses information abou entrez gene ids out of the xml data files
#
#	Author: kstone (2013,Feb 11)

#	This script is not intended to be robust.
#	It was created to gather rough statistics from dbsnp XML files
#

import xml.etree.cElementTree as et
from optparse import OptionParser

data_dir = '/data/downloads/ftp.ncbi.nih.gov/snp/mouse'

def main(chr):
	genotype_file = '%s/genotype_137/gt_chr%s.xml'%(data_dir,chr)
	ds_file = '%s/XML_137/ds_ch%s.xml'%(data_dir,chr)
	context = et.iterparse(ds_file, events=("start","end"))

	context = iter(context)

	event,root = context.next()

	count = 0
	entrezGeneMap = {}
	# I have no idea why this shows up in the tag name, but it does
	TAG_PREFIX='{http://www.ncbi.nlm.nih.gov/SNP/docsum}'
	currentRsId = None
	currentAssembly = None
	for event,elem in context:
		count += 1
		#if count==1:
			#print dir(elem)
		# only care about the start of tags
		if event=='start':
			if elem.tag == '%s%s'%(TAG_PREFIX,'Rs'):
				# set to next rsId
				currentRsId = elem.get('rsId')
				#print "Rs ID = %s"%currentRsId
			#print elem.tag
			if elem.tag == '%s%s'%(TAG_PREFIX,'Assembly'):
				# set to next Assembly build
				currentAssembly = elem.get('groupLabel')	

			if currentAssembly == 'GRCm38' and \
				elem.tag == '%s%s'%(TAG_PREFIX,'FxnSet'):
				geneId = elem.get('geneId')
				#print "geneID = %s"%geneId
				entrezGeneMap.setdefault(geneId,set()).add(currentRsId)
		elif event =='end':
			elem.clear()
			root.clear()
			#if count>=10000:
			#	break

	# sort by entrez gene ID
	geneIds = entrezGeneMap.keys()
	geneIds.sort()
	MAX_RSIDS_TO_PRINT = 5
	print "\t".join(['Entrez Gene ID','RS Id Count','First %s Rs Ids'%MAX_RSIDS_TO_PRINT,'\n'])
	for geneId in geneIds:
		rsIds = entrezGeneMap[geneId]
		rsIdCount = len(rsIds)
		if rsIdCount > MAX_RSIDS_TO_PRINT:
			rsIdsString = ",".join(list(rsIds)[0:MAX_RSIDS_TO_PRINT])
			rsIdsString ="%s,..."%rsIdsString
		else:
			rsIdsString = ",".join(rsIds)
		print "\t".join([geneId,"%s"%rsIdCount,rsIdsString,'\n'])	

if __name__ == "__main__":
	parser = OptionParser(usage="usage: %prog chromosome")
	(options,args) = parser.parse_args()
	if len(args) < 1:
		parser.error("no chromosome specified")
	chr = args[0]
	main(chr)
