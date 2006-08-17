#!/bin/sh

#
# Usage:  postProcess.sh
#
# History
#
# sc    03/23/2006 - created
#
# This script 
# 1. dumps snp a database
# 2. loads a snp database
# 3. updates MGI_dbInfo in a mgd database

cd `dirname $0`/..

LOG=`pwd`/postProcess.log
rm -f ${LOG}

date | tee -a ${LOG}

#
#  Verify the argument(s) to the shell script.
#
if [ $# -ne 0 ]
then
    echo ${Usage} | tee -a ${LOG}
    exit 1
fi

#
#  Establish the configuration file name
#
CONFIG_LOAD=`pwd`/dbsnpload.config
#
#  Make sure the configuration file is readable.
#
if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}" | tee -a ${LOG}
    exit 1
fi

#
# Source the DBSNP Load configuration files
#
. ${CONFIG_LOAD}

#  postProcess log
PP_LOG=${LOGDIR}/postProcess.log
rm -f ${PP_LOG}

#
# main
#
date | tee -a ${PP_LOG}

#
# dump
#
echo "dumping ${SNPBE_DBSERVER} ${SNPBE_DBNAME} to ${SNP_BACKUP_LOCALPATH}" | tee -a ${PP_LOG}
${MGI_DBUTILS}/bin/dump_db.csh ${SNPBE_DBSERVER} ${SNPBE_DBNAME} ${SNP_BACKUP_LOCALPATH}

#
# load
# Note: this also takes the database OUT of sgl user mode
#

echo "loading ${SNP_DBSERVER} ${SNP_DBNAME} from ${SNP_BACKUP_REMOTEPATH}" | tee -a ${PP_LOG}
echo ""
${MGI_DBUTILS}/bin/load_db.csh ${SNP_DBSERVER} ${NP_DBNAME} ${SNP_BACKUP_REMOTEPATH}

#
# update production mgd MGI_dbinfo snp_data_version
#
echo "updating mgd MGI_dbinfo snp_data_version" | tee -a ${PP_LOG}
${MGI_DBUTILS}/bin/updateSnpDataVersion.csh ${MGD_DBSCHEMADIR} "${SNP_DATAVERSION}"

date | tee -a ${LOG}  ${PP_LOG}
