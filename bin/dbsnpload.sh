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
   Usage="dbsnpload.sh [-f -v]"
#
#         where:
#		-f run the fxnClass vocload and translation load
#               -v run the varClass vocload and translation load
#               note: subHandle vocload is always run
#  Env Vars:
#
#      See the configuration file
#
#  Inputs:
#
#      - Common configuration file (/usr/local/mgi/etc/common.config.sh)
#      - dbsnpload.config
#      - two XML format input files (genotypes are in seperate file)
#      - dbsnpload/data input files for translations and vocabs 
#      - coordinate file created by dbsnpload is input to coordload
#  Outputs:
#
#      - An archive file
#      - Log files defined by the environment variables ${LOG_PROC},
#        ${LOG_DIAG}, ${LOG_CUR} and ${LOG_VAL}
#      - BCP files for for inserts to each database table to be loaded
#      - dbsnpload outputs a coordinate file which is input to coordload
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
doFxn=no
doVar=no

set -- `getopt fv $*`
if [ $? != 0 ]
then
    echo ${usage}
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
#  Establish the configuration file names.
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
# Source the common configuration files
#
. ${CONFIG_COMMON}

#
# Source the DBSNP Load configuration files
#
. ${CONFIG_LOAD}

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
        echo "$2 Failed. Return status: $1" | tee -a ${LOG_PROC} ${LOG_DIAG}
        shutDown
        exit 1
    fi
    echo "$2 completed successfully" | tee -a ${LOG_PROC} ${LOG_DIAG}

}

##################################################################
# main
##################################################################

#
# createArchive, startLog, getConfigEnv, get job key
#
preload 

#
# rm all files/dirs from OUTPUTDIR and RPTDIR
#
#cleanDir ${OUTPUTDIR} ${RPTDIR}

# run fxn class vocload?
if [ ${doFxn} = "yes" ]
then
    ${DBSNP_VOCLOAD} -f
    STAT=$?
    msg="dbsnp fxnClass vocabulary load"
    checkstatus ${STAT} "${msg}"
fi

# run variation class vocload?
if [ ${doVar} = "yes" ]
then
    ${DBSNP_VOCLOAD} -v
    STAT=$?
    msg="dbsnp varClass vocabulary load"
    checkstatus ${STAT} "${msg}"
fi

# always run submitter handle vocload
${DBSNP_VOCLOAD} -h
STAT=$?
msg="dbsnp subHandle vocabulary load"
checkstatus ${STAT} "${msg}"

# run variation class translation load?
if [ ${doVar} = "yes" ]
then
    ${DBSNP_TRANS_LOAD} -v
    STAT=$?
    msg="dbsnp varClass translation load"
    checkstatus ${STAT} "${msg}"
fi

# run fxn class translation load?
if [ ${doFxn} = "yes" ]
then
    ${DBSNP_TRANS_LOAD} -f
    STAT=$?
    msg="dbsnp fxnClass translation load"
    checkstatus ${STAT} "${msg}"
fi

echo "running population load"
${POPULATION_LOAD}
STAT=$?
msg="dbsnp population load"
checkstatus ${STAT} "${msg}"

echo "running dbsnp load"
runsnpload
STAT=$?
msg="dbsnp load"
checkstatus ${STAT} "${msg}"

echo "running snp coordinate load"
runcoordload
STAT=$?
msg="dbsnp coordload"
checkstatus  ${STAT} "${msg}"

echo "running ${SNP_COORD_CACHE_LOAD}"
${SNP_COORD_CACHE_LOAD}
STAT=$?
msg="dbsnp coord cache load"
checkstatus  ${STAT} "${msg}"

echo "running ${SNP_MARKER_CACHE_LOAD}"
${SNP_MARKER_CACHE_LOAD}
STAT=$?
msg="dbsnp marker cache load"
checkstatus  ${STAT} "${msg}"

echo "running snp strain order update"
${STRAIN_ORDER_LOAD}
STAT=$?
msg="snp strain order update"
checkstatus  ${STAT} "${msg}"

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
