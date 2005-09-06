#!/bin/sh
#
#  $Header
#  $Name
#
#  dbsnpload.sh
###########################################################################
#
#  Purpose:  This script controls the execution of the DB SNP
#		Data Provider Load
#
   Usage="dbsnpload.sh config_file"
#
#  Env Vars:
#
#      See the configuration file
#
#  Inputs:
#
#      - Common configuration file (/usr/local/mgi/etc/common.config.sh)
#      - 
#      - 
#
#  Outputs:
#
#      - An archive file
#      - Log files defined by the environment variables ${LOG_PROC},
#        ${LOG_DIAG}, ${LOG_CUR} and ${LOG_VAL}
#      - BCP files for for inserts to each database table to be loaded
#      - SQL script file for updates
#      - Records written to the database tables
#      - Exceptions written to standard error
#      - Configuration and initialization errors are written to a log file
#        for the shell script
#      - QC reports as defined by ${APP_SEQ_QCRPT} and ${APP_MSP_QCRPT}
#        
#
#  Exit Codes:
#
#      0:  Successful completion
#      1:  Fatal error occurred
#      2:  Non-fatal error occurred
#
#  Assumes:  Nothing
#
#  Implementation:  
#
#  Notes:  None
#
###########################################################################

#
#  Set up a log file for the shell script in case there is an error
#  during configuration and initialization.
#
cd `dirname $0`/..
LOG=`pwd`/dbsnpload.log
rm -f ${LOG}

#
#  Verify the argument(s) to the shell script.
#
if [ $# -ne 0 ]
then
    echo ${Usage} | tee -a ${LOG}
    exit 1
fi

#
#  Establish the configuration file names.
#
CONFIG_COMMON=`pwd`/common.config.sh
CONFIG_LOAD=`pwd`/dbsnpload.config
echo ${CONFIG_LOAD}

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

#if [ ! -r ${CONFIG_DBSNPCOMMON} ]
#then
#    echo "Cannot read configuration file: ${CONFIG_DBSNPCOMMON}" | tee -a ${LOG}
#    exit 1
#
#fi

#
# Source the common configuration files
#
. ${CONFIG_COMMON}

#
# Source the DBSNP Load configuration files
#
. ${CONFIG_LOAD}
#. ${CONFIG_DBSNPCOMMON}

echo "javaruntime:${JAVARUNTIMEOPTS}"
echo "classpath:${CLASSPATH}"
echo "dbserver:${MGD_DBSERVER}"
echo "database:${MGD_DBNAME}"

#
#  Source the DLA library functions.
#
if [ "${DLAJOBSTREAMFUNC}" != "" ]
then
    if [ -r ${DLAJOBSTREAMFUNC} ]
    then
        . ${DLAJOBSTREAMFUNC}
    else
        echo "Cannot source DLA functions script: ${DLAJOBSTREAMFUNC}"
        exit 1
    fi
else
    echo "Environment variable DLAJOBSTREAMFUNC has not been defined."
fi

#
#  Function that performs cleanup tasks for the job stream prior to
#  termination.
#
shutDown ()
{
    #
    # report location of logs
    #
    echo "\nSee logs at ${LOGDIR}\n" >> ${LOG_PROC}

    #
    # call DLA library function
    #
    postload

}

#
# Function that runs the java dbsnp load
#

runsnpload ()
{
    #
    # log time 
    #
    echo "\n`date`" >> ${LOG_PROC}
    #
    # run dbsnpload
    #
    ${JAVA} ${JAVARUNTIMEOPTS} -classpath ${CLASSPATH} \
	-DCONFIG=${CONFIG_COMMON},${CONFIG_LOAD} \
	-DJOBKEY=${JOBKEY} ${DLA_START}
}

#
# function that runs the java snp coordinate load
#
runcoordload ()
{
    #
    # log time
    #
    echo "\n`date`" >> ${LOG_PROC}
    #
    # run dbsnp coordload

    # Here we override the Configured value of DLA_LOADER
    # and set it to the Configured coordload class
    # we also override 
    # and DLA_TRUNCATE_LOAD and  DLA_TRUNCATE_QC_TABLES
    # so we don't delete the SNP tables during the coordload
    ${JAVA} ${JAVARUNTIMEOPTS} -classpath ${CLASSPATH} \
    -DCONFIG=${CONFIG_COMMON},${CONFIG_LOAD} \
    -DDLA_LOADER=${COORD_DLA_LOADER} \
    -DDLA_TRUNCATE_LOAD_TABLES="" \
    -DDLA_TRUNCATE_QC_TABLES="" \
    -DJOBKEY=${JOBKEY} ${DLA_START}
}


checkstatus ()
{

    if [ $1 -ne 0 ]
    then
        echo "$2 Failed. Return status: $1" >> ${LOG_PROC}
        shutDown
        exit 1
    fi
    echo "$2 completed successfully" >> ${LOG_PROC}

}

# doing this from the java load now
#runtruncate () 
#{
#    ${MGD_DBSCHEMADIR}/table/SNP_truncate.logical | tee -a ${LOG}
#    STAT=$?
#    msg="truncate mgd tables "
#    checkstatus msg STAT 
#
#    ${RADAR_DBSCHEMADIR}/table/MGI_SNP_truncate.logical
#    STAT=$?
#    msg="truncate radar tables "
#    checkstatus msg STAT 
#}

##################################################################
# main
##################################################################

#
# createArchive including OUTPUTDIR, startLog, getConfigEnv, get job key
#
preload ${OUTPUTDIR}

#
# rm all files/dirs from OUTPUTDIR and RPTDIR
#
cleanDir ${OUTPUTDIR} ${RPTDIR}

echo "running vocabulary loads"
${DBSNP_VOCLOAD}
STAT=$?
msg="dbsnp vocabulary load "
checkstatus ${STAT} ${msg}

echo "running translation load"
${DBSNP_TRANS_LOAD}
STAT=$?
msg="dbsnp translation load "
checkstatus ${STAT} ${msg}

echo "running population load"
${POPULATION_LOAD}
STAT=$?
msg=" population load "
checkstatus ${STAT} ${msg}

echo "running dbsnp load"
runsnpload
STAT=$?
msg="dbsnp load "
checkstatus ${STAT} ${msg}

echo "running snp coordinate load"
runcoordload
STAT=$?
msg="snp coordload "
checkstatus  ${STAT} ${msg}

echo "running ${SNP_COORD_CACHE_LOAD}"
${SNP_COORD_CACHE_LOAD}
STAT=$?
msg="snp coord cache load "
checkstatus  ${STAT} ${msg}

echo "running ${SNP_MARKER_CACHE_LOAD}"
${SNP_MARKER_CACHE_LOAD}
STAT=$?
msg="snp coord cache load "
checkstatus  ${STAT} ${msg}

# run postload cleanup and email logs
#
shutDown

exit 0

$Log

###########################################################################
#
# Warranty Disclaimer and Copyright Notice
#
#  THE JACKSON LABORATORY MAKES NO REPRESENTATION ABOUT THE SUITABILITY OR
#  ACCURACY OF THIS SOFTWARE OR DATA FOR ANY PURPOSE, AND MAKES NO WARRANTIES,
#  EITHER EXPRESS OR IMPLIED, INCLUDING MERCHANTABILITY AND FITNESS FOR A
#  PARTICULAR PURPOSE OR THAT THE USE OF THIS SOFTWARE OR DATA WILL NOT
#  INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS, OR OTHER RIGHTS.
#  THE SOFTWARE AND DATA ARE PROVIDED "AS IS".
#
#  This software and data are provided to enhance knowledge and encourage
#  progress in the scientific community and are to be used only for research
#  and educational purposes.  Any reproduction or use for commercial purpose
#  is prohibited without the prior express written permission of The Jackson
#  Laboratory.
#
# Copyright \251 1996, 1999, 2002, 2003 by The Jackson Laboratory
#
# All Rights Reserved
#
###########################################################################
