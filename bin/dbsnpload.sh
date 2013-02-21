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
   Usage="dbsnpload.sh [-s -f -v -h -p -c -r]"
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

source ${MGICONFIG}/master.config.sh
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
        -f) doFxn=no; shift;;
        -v) doVar=no; shift;;
	-h) doHandle=no; shift;;
	-p) doPop=no; shift;;
	-c) doCache=no; shift;;
	-r) doPost=no; shift;;
        --) shift; break;;
    esac
done

#
#  Establish load configuration file name.
#

CONFIG_LOAD=`pwd`/dbsnpload.config

#
# Make sure load configuration file is readable
#

if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}" | tee -a ${LOG}
    exit 1
fi

#
# Source the load configuration file
#
. ${CONFIG_LOAD}

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
echo "snp dbserver:${SNPBE_DBSERVER}"
echo echo "snp database:${SNPBE_DBNAME}"

#
#  Source the DLA library functions.
#
#if [ "${DLAJOBSTREAMFUNC}" != "" ]
#then
#    if [ -r ${DLAJOBSTREAMFUNC} ]
#    then
#        . ${DLAJOBSTREAMFUNC}
#    else
#        echo "Cannot source DLA functions script: ${DLAJOBSTREAMFUNC}"
#        exit 1
#    fi
#else
#    echo "Environment variable DLAJOBSTREAMFUNC has not been defined."
#fi

#
#  Function that performs cleanup tasks for the job stream prior to
#  termination.
#
#shutDown ()
#{
#    #
#    # report location of logs
#    #
#    echo "\nSee logs at ${LOGDIR}\n" >> ${LOG_PROC}
#
#    #
#    # call DLA library function
#    #
#    postload
#
#}

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

    CONFIG_MASTER=${MGICONFIG}/master.config
    export CONFIG_MASTER

    echo "running load with ${CONFIG_MASTER} and ${CONFIG_LOAD}"
    echo "DATALOADSOUTPUT = ${DATALOADSOUTPUT}"

    ${JAVA} ${JAVARUNTIMEOPTS} -classpath ${CLASSPATH} \
	-DCONFIG=${CONFIG_MASTER},${CONFIG_LOAD} \
	-DJOBKEY=${JOBKEY} ${DLA_START}
}

#checkstatus ()
#{
#
#    if [ $1 -ne 0 ]
#    then
#        echo "$2 Failed. Return status: $1" | tee -a ${LOG_PROC} ${LOG_DIAG}
#        shutDown
#        exit 1
#    fi
#    echo "$2 completed successfully" | tee -a ${LOG_PROC} ${LOG_DIAG}
#
#}

##################################################################
# main
##################################################################
echo "main"

#
# createArchive, startLog, getConfigEnv, get job key
#
#preload 

#echo "preload"
#
# put production snp database in single user mode prior to loading snps
#
#if [ ${doSgl} = "yes" ]
#then
#    echo "calling  ${SNP_SGL_USER} ${SNP_DBSERVER} ${SNP_DBNAME} true ${SNP_SGL_USER_FILE} ${SNP_SLEEP_INTERVAL}"
#
#    ${SNP_SGL_USER} ${SNP_DBSERVER} ${SNP_DBNAME} true ${SNP_SGL_USER_FILE} ${SNP_SLEEP_INTERVAL}
#    STAT=$?
#    msg="${SNP_SGL_USER} "
#    checkstatus ${STAT} "${msg}"
#fi

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
#
#shutDown

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
