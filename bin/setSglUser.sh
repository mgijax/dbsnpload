#!/bin/sh

# Purpose: set a database sgl user mode on/off - if database is in use
#          wait a specified length of time (secs) and try again
#
Usage="setSglUser.sh DBSERVER DBNAME mode outputfile sleepInterval"
# where:
#        mode is 'true' if turning on sgl user mode
#        mode is 'false' when turning off sgl user mode
#        sleepInterval is number of seconds to wait before trying again
# 
# This script retries when:
# 1) not in sgl user mode and someone in the database when attempt 
#    made to put in single user mode - output will contain 'failed'
# 2) someone in the and sgl user mode is on and attempting to log in
#    - output will contain 'already open'

cd `dirname $0`/..

#
#  Verify the argument(s) to the shell script.
#
if [ $# -ne 5 ]
then
    echo ${Usage} 
    exit 1
fi

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
    echo "Cannot read configuration file: ${CONFIG_COMMON}" 
    exit 1
fi

if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}" 
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

DBSERVER=$1
DBNAME=$2
MODE=$3
OUTFILE=$4
SLEEP=$5

# expression in output file when someone in db and turning sgl user mode on
FAILED=failed

# expression in output file when someone in db and sgl user mode is on and 
# attempting to log in
OPEN='already open'

# check that MODE parameter is valid
if [ "${MODE}" != "true" -a "${MODE}" != "false" ]
then
    echo "Mode must be true or false"
    exit 1
fi

# grep for the two expressions in the output file
dogrep ()
{
    cat ${OUTFILE} | /usr/bin/grep "${FAILED}"
    failed=$?
    
    cat ${OUTFILE} | grep "${OPEN}"
    open=$?
}

########
# main #
########

# attempt to put DBNAME in sgl user mode
echo "Running ${SNP_DO_SGL_USER} ${DBSERVER} ${DBNAME} ${MODE} ${OUTFILE}"
${SNP_DO_SGL_USER} ${DBSERVER} ${DBNAME} ${MODE} ${OUTFILE}

# see if it found an expression indicating failure
dogrep 

# grep returns 0 when pattern found, 1 when not found
while [ ${failed} -eq 0 -o ${open} -eq 0 ]
do
    sleep ${SLEEP}
    ${SNP_DO_SGL_USER} ${DBSERVER} ${DBNAME} ${MODE} ${OUTFILE}
    dogrep 
done
echo "Successfully set ${DBSERVER} ${DBNAME} single user mode to ${MODE} sql output in ${OUTFILE}"
