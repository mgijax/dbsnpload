#!/bin/sh 
#
#   $Header
#   $Name
# loadTranslations.sh
##############################################################################
#
# Purpose: Load SNP translations
#
Usage="loadTranslations.sh [-f -v]"
# where:
#       -f run the fxnClass translationload
#       -v run the varClass translationload
#  Env Vars:  None
#
#  Inputs:
#
#       - Configuration files
#       - Configured translation input file
#  Outputs:
#       - logs
#       - bcp files
#       - MGI_Translation and MGI_TranslationType records in a database
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

cd `dirname $0`/..
LOG=`pwd`/loadTranslations.log
rm -f ${LOG}
date | tee -a ${LOG}

#
#  Verify the argument(s) to the shell script.
#
doFxn=no
doVar=no

set -- `getopt fv $*`
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
        --) shift; break;;
    esac
done

#
#  Establish the configuration file names
#
CONFIG_COMMON=`pwd`/common.config.sh
CONFIG_LOAD=`pwd`/dbsnpload.config
CONFIG_VARCLASS=`pwd`/varClassTrans.config
CONFIG_FXNCLASS=`pwd`/fxnClassTrans.config

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

if [ ! -r ${CONFIG_VARCLASS} ]
then
    echo "Cannot read configuration file: ${CONFIG_VARCLASS}" | tee -a ${LOG}
    exit 1
fi

if [ ! -r ${CONFIG_FXNCLASS} ]
then
    echo "Cannot read configuration file: ${CONFIG_FXNCLASS}" | tee -a ${LOG}
    exit 1
fi

#
# Source configuration files
#
. ${CONFIG_COMMON}
. ${CONFIG_LOAD}

# create translation output directory, if necessary
TRANS_OUTPUTDIR=${OUTPUTDIR}/translationload

if [ ! -d ${TRANS_OUTPUTDIR} ]
then
  echo "...creating translation output directory ${TRANS_OUTPUTDIR}"
  mkdir -p ${TRANS_OUTPUTDIR}
fi

# diagnostic log for translations
TRANS_LOG=${LOGDIR}/loadTranslations.log
touch ${TRANS_LOG}

checkstatus ()
{

    if [ $1 -ne 0 ]
    then
        echo "$2 Failed. Return status: $1" | tee -a  ${TRANS_LOG}
        exit 1
    fi
    echo "$2 completed successfully" | tee -a  ${TRANS_LOG}

}

#
# main
#

date | tee -a ${TRANS_LOG}

if [ ${doVar} = "yes" ]
then
    # source variation class config
    . ${CONFIG_VARCLASS}

    echo "Creating varClass translation..." | tee -a ${TRANS_LOG}
    ${TRANSLATION_LOAD} -S${MGD_DBSERVER} -D${MGD_DBNAME} -U${MGD_DBUSER} -P${MGD_DBPASSWORDFILE} -M${VARCLASS_TRANS_LOAD_MODE} -I${VARCLASS_TRANS_INPUT} -O${TRANS_OUTPUTDIR}
    STAT=$?
    msg="varClass translation load "
    checkstatus  ${STAT} "${msg}"
fi

if [ ${doFxn} = "yes" ]
then
    # source function class config        
    . ${CONFIG_FXNCLASS}

    echo "Creating fxnClass translation..." | tee -a ${TRANS_LOG}
    ${TRANSLATION_LOAD} -S${MGD_DBSERVER} -D${MGD_DBNAME} -U${MGD_DBUSER} -P${MGD_DBPASSWORDFILE} -M${FXNCLASS_TRANS_LOAD_MODE} -I${FXNCLASS_TRANS_INPUT} -O${TRANS_OUTPUTDIR}
    STAT=$?
    msg="fxnClass translation load "
    checkstatus  ${STAT} "${msg}"
fi

date | tee -a ${LOG} ${TRANS_LOG}
