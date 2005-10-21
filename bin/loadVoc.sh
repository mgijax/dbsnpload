#!/bin/sh 
#
#   $Header
#   $Name
# loadVoc.sh
##############################################################################
# Purpose: Load SNP Vocabularies
#
Usage="loadVoc.sh [-f -v -h]"
# where:
#	-f run the fxnClass vocload
#	-v run the varClass vocload
#	-h run the subHandle vocload
#  Env Vars:  None
#
#  Inputs:
#
#      	- Configuration files
#      	- Configured vocab input file
#  Outputs:
#	- logs
#	- bcp files
#	- VOC_Term and VOC_Vocab records in a database
#  Exit Codes:
#	0 = Successful completion
#	1 = An error occurred
#	2 = Usage error occured
#  Assumes:
#	 - DLA standards are being followed for environment variable name
#	 - all config files are in the same directory as this script
#  Implementation:
#
#  Notes:  None
#
###########################################################################

cd `dirname $0`/..
LOG=`pwd`/loadVoc.log
rm -f ${LOG}

date | tee -a ${LOG}

#
#  Verify the argument(s) to the shell script.
#
doFxn=no
doVar=no
doHandle=no

set -- `getopt fvh $*`
if [ $? != 0 ]
then
    echo ${Usage}
    exit 2
fi

for i in $*
do
    case $i in
        -f) doFxn=yes; shift;;
        -v) doVar=yes; shift;;
	-h) doHandle=yes; shift;;
        --) shift; break;;
    esac
done

#
#  Establish general configuration file names
#
CONFIG_COMMON=`pwd`/common.config.sh
CONFIG_LOAD=`pwd`/dbsnpload.config

#
#  Establish vocload config file names
#

FXNCLASS_VOCAB_CONFIG=`pwd`/fxnClassDag.config
VARCLASS_VOCAB_CONFIG=`pwd`/varClassVocab.config
HANDLE_VOCAB_CONFIG=`pwd`/subHandleVocab.config
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
# Source general configuration files
#
. ${CONFIG_COMMON}
. ${CONFIG_LOAD}

# loadVoc log
VOC_LOG=${LOGDIR}/loadVoc.log
touch ${VOC_LOG}

#
# function that reports status given a status ($1) and a process name ($2)
#
checkstatus ()
{

    if [ $1 -ne 0 ]
    then
        echo "$2 Failed. Return status: $1" | tee -a ${LOG} ${VOC_LOG}
        exit 1
    fi
    echo "$2 completed successfully" | tee -a ${VOC_LOG}

}

#
# main
#

date | tee -a ${VOC_LOG}

if [ ${doFxn} = "yes" ]
then
    echo "Creating fxnClass vocab..." | tee -a ${VOC_LOG}
    ${VOCDAGLOAD} ${FXNCLASS_VOCAB_CONFIG}
    STAT=$?
    msg="fxnClass vocab load"
    checkstatus  ${STAT} "${msg}"
fi

if [ ${doVar} = "yes" ]
then
    echo "Creating varClass vocab..." | tee -a ${VOC_LOG}
    ${VOCSIMPLELOAD} ${VARCLASS_VOCAB_CONFIG}
    STAT=$?
    msg="varClass vocab load"
    checkstatus  ${STAT} "${msg}"
fi

if [ ${doHandle} = "yes" ]
then
    echo "Creating subHandle vocab intermediate file..." | tee -a ${VOC_LOG}
    # transforms: <NSE-ss_handle>WI</NSE-ss_handle>
    # into: WI
    /usr/bin/cat ${NSE_SNP_INFILEDIR}/*.xml | grep "<NSE-ss_handle>" | cut -d'>' -f2 | cut -d'<' -f1 | sort | uniq > ${INT_HANDLE_VOCAB_FILE}

    # creates subHandle vocab input file which will create
    # accession records for the vocab 
    # file looks like:
    # WI tab WI
    # where WI is the term name AND the accession id
    echo "Creating subHandle vocab input file..." | tee -a ${VOC_LOG}
    ${HANDLE_VOCAB_FILE_CREATOR}
    STAT=$?
    msg="subHandle vocab file creator"
    checkstatus  ${STAT} "${msg}"

    echo "Creating subHandle vocab ..." | tee -a ${VOC_LOG}
    ${VOCSIMPLELOAD} ${HANDLE_VOCAB_CONFIG}
    STAT=$?
    msg="subHandle vocab load"
    checkstatus  ${STAT} "${msg}"
fi
date | tee -a ${LOG} ${VOC_LOG}
