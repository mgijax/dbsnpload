#!/bin/sh
#
#  $Header
#  $Name
#
#  dbsnpload.sh
###########################################################################
#
#  Purpose:  This script controls the execution of the DB SNP load
#
   Usage="mgpload.sh [-s -f -v -h -p -c -r]"
#
#         These options provided primarily for development purposes, however
#         vocabs often do not change between dbsnp builds, so we provide
#         these options to avoid reloading them unnecessarily.
#
#         where:
#	        -s DO NOT put front-end snp db in sgl user mode 
#		-f DO NOT run the fxnClass vocload and translation load
#               -v DO NOT run the varClass vocload and translation load
#               -h DO NOT run the  subHandle vocload 
#		-p DO NOT run Population load - !warning! if you use this option
#		    be sure to configure SNP_OK_TO_DELETE_ACCESSIONS=true
#		-c DO NOT run snpcacheload
#		-r DO NOT run post Processing
#  Env Vars:
#
#      See the configuration file
#
#  Inputs:
#
#      - Common configuration file (/usr/local/mgi/etc/common.config.sh)
#      - dbsnpload.config
#      - dbsnpload/data input files for translations and vocabs 
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
doFxn=no
doVar=no
doHandle=no
doPop=no
doCache=no
doPost=no

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
        -f) doFxn=yes; shift;;
        -v) doVar=yes; shift;;
	-h) doHandle=yes; shift;;
	-p) doPop=yes; shift;;
	-c) doCache=es; shift;;
	-r) doPost=yes; shift;;
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
# Function that runs the java dbsnp load
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
# run fxn class vocload?
#
if [ ${doFxn} = "yes" ]
then
    ${DBSNP_VOCLOAD} -f
    STAT=$?
    msg="dbsnp fxnClass vocabulary load "
    checkstatus ${STAT} "${msg}"
fi

#
# run variation class vocload?
#
if [ ${doVar} = "yes" ]
then
    ${DBSNP_VOCLOAD} -v
    STAT=$?
    msg="dbsnp varClass vocabulary load "
    checkstatus ${STAT} "${msg}"
fi

#
# run submitter handle vocload?
#
if [ ${doHandle} = "yes" ]
then
    ${DBSNP_VOCLOAD} -h
    STAT=$?
    msg="dbsnp subHandle vocabulary load "
    checkstatus ${STAT} "${msg}"
fi

#
# run variation class translation load?
#
if [ ${doVar} = "yes" ]
then
    ${DBSNP_TRANS_LOAD} -v
    STAT=$?
    msg="dbsnp varClass translation load "
    checkstatus ${STAT} "${msg}"
fi

#
# run fxn class translation load?
#
if [ ${doFxn} = "yes" ]
then
    ${DBSNP_TRANS_LOAD} -f
    STAT=$?
    msg="dbsnp fxnClass translation load "
    checkstatus ${STAT} "${msg}"
fi

echo "run pop load"
# 
# run population load
#
if [ ${doPop} = "yes" ]
then
    echo "running population load"
    ${POPULATION_LOAD}
    STAT=$?
    msg="dbsnp population load "
    checkstatus ${STAT} "${msg}"
fi

#
# run java dbSnp loader
#
echo "running dbsnp load"
runsnpload

#
# load SNP_Transcript_Marker
#
#echo "running migrateRefSeqs.sh"

#${DBSNPLOAD}/bin/migrateRefSeqs.sh

#STAT=$?
#msg="dbsnp load "
#checkstatus ${STAT} "${msg}"

#
# order snp strains
#
#echo "running snp strain order update"
#${STRAIN_ORDER_LOAD}
#STAT=$?
#msg="snp strain order update "
#checkstatus  ${STAT} "${msg}"

#
# run snp marker cache load
#
if [ ${doCache} = "yes" ]
then
    echo "running ${SNP_MARKER_CACHE_LOAD}"
    ${SNP_MARKER_CACHE_LOAD}
    STAT=$?
    msg="dbsnp marker cache load "
    checkstatus  ${STAT} "${msg}"
fi
#
# run postProcessing - dump/load/update mgd MGI_dbinfo
#
if [ ${doPost} = "yes" ]
then
    echo "running post-processingggg"
    ${SNP_POST_PROCESS}
    STAT=$?
    msg="post-processing "
    checkstatus  ${STAT} "${msg}"
fi
# run postload cleanup and email logs

shutDown

exit 0

$Log

