#!/bin/sh 
#
#   $Header
#   $Name
# updateSnpStrainOrder.sh
##############################################################################
#
# Purpose: Update SNP Strain Set members ordering
#
Usage=updateSnpStrainOrder.sh
#  Env Vars:  None
#
#  Inputs:
#
#       - Configuration files
#  Outputs:
#       - logs
#       - MGI_SetMember.sequenceNum updated in a database
#  Exit Codes:
#       0 = Successful completion
#       1 = An error occurred
#       2 = Usage error occured
#  Assumes:
#  Implementation:
#
#  Notes:  None
#
###########################################################################

cd `dirname $0`/..
LOG=`pwd`/updateSnpStrainOrder.log
rm -f ${LOG}
date | tee ${LOG}


#
#  Establish the configuration file names
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
        echo "$2 Failed. Return status: $1" | tee -a  ${LOG_STRAIN}
	date | tee -a ${LOG_STRAIN}
        exit 1
    fi
    echo "$2 completed successfully" | tee -a ${LOG_STRAIN}

}

#
# main
#

date | tee ${LOG_STRAIN}


echo "updating strain order..." | tee -a ${LOG_STRAIN}
${INSTALLDIR}/bin/updateSnpStrainOrder.py
STAT=$?
msg="snp strain order update"
checkstatus  ${STAT} "${msg}"

date | tee -a ${LOG} ${LOG_STRAIN}
