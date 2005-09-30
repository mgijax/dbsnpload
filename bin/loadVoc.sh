#!/bin/sh

#
# Load SNP VOC vocabularies
#
cd `dirname $0`/..
LOG=`pwd`/loadVoc.log
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
#  Establish general the configuration file names
#
CONFIG_COMMON=`pwd`/common.config.sh
CONFIG_LOAD=`pwd`/dbsnpload.config

#
#  Establish vocload config file names
#

FXNCLASS_VOCAB_CONFIG=`pwd`/fxnClass.config
VARCLASS_VOCAB_CONFIG=`pwd`/varClass.config
HANDLE_VOCAB_CONFIG=`pwd`/subHandle.config

#
#  Make sure the configuration files are readable.
#
if [ ! -r ${CONFIG_COMMON} ]
then
    echo "Cannot read configuration file: ${CONFIG_COMMON}" | tee -a ${LOG}
    exit 1
fi

if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}" | tee -a ${LOG}
    exit 1
fi

if [ ! -r ${FXNCLASS_VOCAB_CONFIG} ]
then
    echo "Cannot read configuration file: ${FXNCLASS_VOCAB_CONFIG}" | tee -a ${LOG}
    exit 1
fi

if [ ! -r ${VARCLASS_VOCAB_CONFIG} ]
then
    echo "Cannot read configuration file: ${VARCLASS_VOCAB_CONFIG}" | tee -a ${LOG}
    exit 1
fi

if [ ! -r ${HANDLE_VOCAB_CONFIG} ]
then
    echo "Cannot read configuration file: ${HANDLE_VOCAB_CONFIG}" | tee -a ${LOG}
    exit 1
fi

#
# Source configuration files
#
. ${CONFIG_COMMON}
. ${CONFIG_LOAD}

checkstatus ()
{

    if [ $1 -ne 0 ]
    then
        echo "$2 Failed. Return status: $1" | tee -a ${LOG_PROC} ${LOG_DIAG}
        exit 1
    fi
    echo "$2 completed successfully" | tee -a ${LOG_PROC} ${LOG_DIAG}

}

#
# main
#

echo ${LOG_DIAG}
date | tee -a ${LOG_DIAG}

echo "Creating fxnClass vocab..." | tee -a ${LOG} ${LOG_DIAG}
${VOCSIMPLELOAD} ${FXNCLASS_VOCAB_CONFIG} >> ${LOG_DIAG} 2>&1

## DEBUG ##
#. /home/sc/snp/vocload/Configuration
#DAG_ROOT_ID=""
#export DAG_ROOT_ID
#. /home/sc/snp/dbsnpload/fxnClass.config

#/home/sc/snp/vocload/simpleLoad.py -f -l testLog.txt /home/sc/snp/vocload/simple.rcd /home/sc/snp/dbsnpload/data/fxnClass.in
## END DEBUG ##

STAT=$?
msg="fxnClass vocab load"
checkstatus  ${STAT} ${msg}

echo "Creating varClass vocab..." | tee -a ${LOG} ${LOG_DIAG}
${VOCSIMPLELOAD} ${VARCLASS_VOCAB_CONFIG} >> ${LOG_DIAG}  2>&1
STAT=$?
msg="varClass vocab load"
checkstatus  ${STAT} ${msg}

#
# rename old vocab files
#
echo "Renaming ${HANDLE_VOCAB_FILE} to ${HANDLE_VOCAB_FILE}.old"
echo "Renaming ${INT_HANDLE_VOCAB_FILE} to ${INT_HANDLE_VOCAB_FILE}.old"
/usr/bin/mv ${HANDLE_VOCAB_FILE} ${HANDLE_VOCAB_FILE}.old
/usr/bin/mv ${INT_HANDLE_VOCAB_FILE} ${INT_HANDLE_VOCAB_FILE}.old

echo "Creating subHandle vocab input file..." | tee -a ${LOG} ${LOG_DIAG}
# transforms: <NSE-ss_handle>WI</NSE-ss_handle>
# into: WI
/usr/bin/cat ${NSE_SNP_INFILEDIR}/*.xml | grep "<NSE-ss_handle>" | cut -d'>' -f2 | cut -d'<' -f1 | sort | uniq > ${INT_HANDLE_VOCAB_FILE}

# creates subHandle vocab input file which will create
# accession records for the vocab 
# file looks like:
# WI tab WI
# where WI is the term name AND the accession id
${HANDLE_VOCAB_FILE_CREATOR}

echo "Creating subHandle vocab ..." | tee -a ${LOG} ${LOG_DIAG}
${VOCSIMPLELOAD} ${HANDLE_VOCAB_CONFIG} >> ${LOG_DIAG} 2>&1
STAT=$?
msg="subHandle vocab load"
checkstatus  ${STAT} ${msg}

date | tee -a ${LOG} ${LOG_DIAG}
