#!/bin/sh

#
# Load SNP translations
#
cd `dirname $0`/..
LOG=`pwd`/loadTrans.log
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

#
# Source configuration files
#
. ${CONFIG_COMMON}
. ${CONFIG_LOAD}

checkstatus ()
{

    if [ $1 -ne 0 ]
    then
        echo "$2 Failed. Return status: $1" | tee -a  ${LOG_PROC} ${LOG_DIAG}
        exit 1
    fi
    echo "$2 completed successfully" | tee -a  ${LOG_PROC} ${LOG_DIAG}

}

#
# main
#

date | tee -a ${LOG_DIAG}

echo "Creating varClass translation..." | tee -a ${LOG} ${LOG_DIAG}
echo "${VARCLASS_TRANS_LOAD} ${MGD_DBSERVER} ${MGD_DBNAME} ${VARCLASS_TRANS_INPUT} ${VARCLASS_TRANS_LOAD_MODE} >> ${LOG_DIAG}  2>&1"

${VARCLASS_TRANS_LOAD} ${MGD_DBSERVER} ${MGD_DBNAME} ${VARCLASS_TRANS_INPUT} ${VARCLASS_TRANS_LOAD_MODE} >> ${LOG_DIAG}  2>&1
STAT=$?
msg="varClass translation load"
checkstatus  ${STAT} ${msg}

echo "Creating fxnClass translation..." | tee -a ${LOG} ${LOG_DIAG}
echo "${FXNCLASS_TRANS_LOAD} ${MGD_DBSERVER} ${MGD_DBNAME} ${FXNCLASS_TRANS_INPUT} ${FXNCLASS_TRANS_LOAD_MODE} >> ${LOG_DIAG}  2>&1"
${FXNCLASS_TRANS_LOAD} ${MGD_DBSERVER} ${MGD_DBNAME} ${FXNCLASS_TRANS_INPUT} ${FXNCLASS_TRANS_LOAD_MODE} >> ${LOG_DIAG}  2>&1
STAT=$?
msg="fxnClass translation load"
checkstatus  ${STAT} ${msg}

date | tee -a ${LOG} ${LOG_DIAG}
