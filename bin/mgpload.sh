#!/bin/sh
#
#  $Header
#  $Name
#
#  mgpload.sh
###########################################################################
#
#  Purpose:  This script controls the execution of the DB SNP load
#
   Usage="mgpload.sh [-s -c]"
#
#         These options provided primarily for development purposes, however
#         vocabs often do not change between dbsnp builds, so we provide
#         these options to avoid reloading them unnecessarily.
#
#         where:
#	        -s DO NOT put front-end snp db in sgl user mode 
#		-c run snpcacheload
#  Env Vars:
#
#      See the configuration file
#
#  Inputs:
#
#      - Common configuration file (/usr/local/mgi/etc/common.config.sh)
#      - mgpload.config
#      - snp and mgd databases
#
#  Outputs:
#
#      - An archive file
#      - Log files defined by the environment variables ${LOG_PROC},
#        ${LOG_DIAG}, ${LOG_CUR} and ${LOG_VAL}
#      - BCP files for inserts to each database table to be loaded
#      - script files for updates to  snp MGI_dbinfo and MGI_Table
#      - Records written to the database tables
#      - Exceptions written to standard error
#      - Configuration and initialization errors are written to a log file
#        for the shell script
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
cd `dirname $0`/..
CONFIG_LOAD=`pwd`/mgpload.config
echo ${CONFIG_LOAD}

cd `dirname $0`
LOG=`pwd`/mgpload.log
rm -rf ${LOG}

echo ${CONFIG_LOAD}

#
# verify & source the configuration file
#

echo "Verifying ${CONFIG_LOAD}"
if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}"
    exit 1
fi

. ${CONFIG_LOAD}

#
#  Verify the argument(s) to the shell script.
#
doSgl=no
doCache=no

set -- `getopt sfvhpcr $*`
if [ $? != 0 ]
then
    echo ${usage}
    exit 2
fi
for i in $*
do
    case $i in
	-s) doSgl=no; shift;;
	-c) doCache=yes; shift;;
        --) shift; break;;
    esac
done

#
#  Make sure the master configuration file is readable
#

if [ ! -r ${CONFIG_MASTER} ]
then
    echo "Cannot read configuration file: ${CONFIG_MASTER}"
    exit 1
fi

echo "javaruntime:${JAVARUNTIMEOPTS}"
echo "classpath:${CLASSPATH}"
echo "mgd dbserver:${MGD_DBSERVER}"
echo "mgd database:${MGD_DBNAME}"
echo "snp dbserver:${SNP_DBSERVER}"
echo echo "snp database:${SNP_DBNAME}"

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
    echo "" >> ${LOG_PROC}
    echo "See logs at ${LOGDIR}" >> ${LOG_PROC}
    echo "" >> ${LOG_PROC}

    #
    # call DLA library function
    #
    postload

}

#
# Function that runs the java mpg snp load
#

runsnpload ()
{
    #
    # log time 
    #

    echo "" >> ${LOG_PROC}
    echo "`date`" >> ${LOG_PROC}

    #
    # run mgpload
    #

    CONFIG_MASTER=${MGICONFIG}/master.config
    export CONFIG_MASTER

    echo "running load with ${CONFIG_MASTER} and ${CONFIG_LOAD}"
    echo "DATALOADSOUTPUT = ${DATALOADSOUTPUT}"

    ${JAVA} ${JAVARUNTIMEOPTS} -classpath ${CLASSPATH} \
	-DCONFIG=${CONFIG_MASTER},${CONFIG_LOAD} \
	-DJOBKEY=${JOBKEY} ${DLA_START}
}

checkstatus ()
{

    if [ $1 -ne 0 ]
    then
        echo "$2 Failed. Return status: $1" | tee -a ${LOG_PROC} ${LOG_DIAG}
        shutDown
        exit 1
    fi
    echo "$2 completed successfully" | tee -a ${LOG_PROC} ${LOG_DIAG}

}

##################################################################
# main
##################################################################
echo "main"

#
# run java dbSnp loader
#

echo "running mgp load"
runsnpload

#
# run postProcessing - dump/load/update mgd MGI_dbinfo
#
echo "running post-processing; bcp into snp tables" | tee -a ${LOG_DIAG}
${SNP_POST_PROCESS} >>  ${LOG_DIAG} 2>&1
STAT=$?
msg="post-processing "
checkstatus  ${STAT} "${msg}"

#
# load SNP_Transcript_Marker
#
echo "running migrateRefSeqs.sh this will truncate and bcp SNP_Transcript_Protein.bcp" | tee -a ${LOG_DIAG}

${DBSNPLOAD}/bin/migrateRefSeqs.sh >> ${LOG_DIAG} 2>&1

STAT=$?
msg="mgp snp load "
checkstatus ${STAT} "${msg}"

#
# run snp marker cache load
#
if [ ${doCache} = "yes" ]
then
    echo "running ${SNP_MARKER_CACHE_LOAD}" | tee -a ${LOG_DIAG}
    ${SNP_MARKER_CACHE_LOAD} >>  ${LOG_DIAG} 2>&1
    STAT=$?
    msg="dbsnp/mgp marker cache load "
    checkstatus  ${STAT} "${msg}"
fi

# run postload cleanup and email logs
shutDown

exit 0
