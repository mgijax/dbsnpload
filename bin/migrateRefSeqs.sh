#!/bin/sh 
#
# migrateRefSeqs.sh
#
##############################################################################
#
# Purpose: load uniq transcript/protein pairs from DP_SNP_Marker
#	to SNP_Transcript_Protein
#
Usage="migrateRefSeqs.sh"
# where:
#       
#  Env Vars:  None
#
#  Inputs:
#
#       - DP_SNP_Marker
#  Outputs:
#       - log
#       - bcp file
#       - records in SNP_Transcript_Protein
#  Exit Codes:
#       0 = Successful completion
#       1 = An error occurred
#       2 = Usage error occured
#  Assumes:
#        - DLA standards are being followed for environment variable name
#        - all config files are in the same directory as this script
#  Implementation:
#
#  Notes:  None
#
###########################################################################

#
# Establish bcp file delimiters
#


# bcp file row delimiter
NL="\n"

# bcp file column delimiter
DL="\t"

# name of the snp schema
SCHEMA='snp'
export SCHEMA

cd `dirname $0`/..
LOG=`pwd`/migrateRefSeqs.log
rm -f ${LOG}
date | tee -a ${LOG}

#
#  Verify the argument(s) to the shell script.
#
if [ $? != 0 ]
then
    echo ${Usage}
    exit 2
fi

#
#  Establish the configuration file names
#
CONFIG_LOAD=`pwd`/dbsnpload.config

#
#  Make sure the configuration files are readable.
#
if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}" | tee -a ${LOG}
    exit 1
fi

#
# Source load configuration file
#
. ${CONFIG_LOAD}

echo ${PG_DBSERVER} ${PG_DBNAME}
echo "PGPASSFILE: ${PGPASSFILE}"
checkstatus ()
{

    if [ $1 -ne 0 ]
    then
        echo "$2 Failed. Return status: $1" | tee -a  ${LOADTRANSLOG}
        exit 1
    fi
    echo "$2 completed successfully" | tee -a  ${LOADTRANSLOG}

}

#
# main
#
echo "Running RefSeq migration " | tee ${REFSEQ_LOG}
echo `date`  >> ${REFSEQ_LOG}


# truncate SNP_Protein_RefSeq
${PG_SNP_DBSCHEMADIR}/table/SNP_Transcript_Protein_truncate.object
${PG_SNP_DBSCHEMADIR}/index/SNP_Transcript_Protein_drop.object

${PYTHON} ${DBSNPLOAD}/bin/migrateRefSeqs.py >> ${REFSEQ_LOG} 2<&1
STAT=$?
msg="RefSeq Migration script "
checkstatus  ${STAT} "${msg}"

date | tee -a ${REFSEQ_LOG}
echo "copy in  ${REFSEQ_TABLE}" | tee -a ${REFSEQ_LOG}
echo "" | tee -a ${SNPMARKER_LOG}
echo "${PG_DBUTILS}/bin/bcpin.csh ${PG_DBSERVER} ${PG_DBNAME} ${REFSEQ_TABLE} ${OUTPUTDIR} ${REFSEQ_BCP_FILE} ${DL} 'notused' ${SCHEMA}"

${PG_DBUTILS}/bin/bcpin.csh ${PG_DBSERVER} ${PG_DBNAME} ${REFSEQ_TABLE} ${OUTPUTDIR} ${REFSEQ_BCP_FILE} ${DL} 'notused' ${SCHEMA} >> ${REFSEQ_LOG} 2>&1
STAT=$?
echo "migrateRefSeqs.sh exit code from bulkLoadPostres ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "bcpin.csh failed" | tee -a ${SNPMARKER_LOG}
    exit 1
fi

${PG_SNP_DBSCHEMADIR}/index/SNP_Transcript_Protein_create.object
echo `date`  >> ${REFSEQ_LOG}


