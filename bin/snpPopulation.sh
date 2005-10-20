#!/bin/sh

#
# Usage:  snpPopulations.sh
#
# History
#
# sc    08/17/2005
#
# This script 
# 1. loads SNP_Population and
# 2. creates accession objects to associate a popid
#     with each SNP_Population
# Note: were are not dropping ACC_Accession indexes or updating
#       statistics since there are so few population ids (code
#       is commented out below.

cd `dirname $0`/..

LOG=`pwd`/snpPopulation.log
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

checkstatus ()
{

    if [ $1 -ne 0 ]
    then
        echo "$2 Failed. Return status: $1" >> ${LOG_PROC}
        exit 1
    fi
    echo "$2 completed successfully" >> ${LOG_PROC}

}

date | tee -a ${LOG_DIAG}

echo "creating population input file in ${POP_FILE}" | tee -a ${LOG_DIAG}
/usr/bin/cat ${GENO_SNP_INFILEDIR}/*.xml | grep "<Population" | cut -f2-4 | sort | uniq > ${POP_FILE}

echo "creating population bcp file"
${INSTALLDIR}/bin/snpPopulation.py 
STAT=$?
msg="snpPopulation.py "
checkstatus ${STAT} "${msg}"

# Allow bcp into database and truncate POP_TABLE
echo "truncating ${POP_TABLE}"
${MGIDBUTILSDIR}/bin/turnonbulkcopy.csh ${MGD_DBSERVER} ${MGD_DBNAME} | tee -a ${LOG_DIAG}
${MGD_DBSCHEMADIR}/table/${POP_TABLE}_truncate.object | tee -a ${LOG_DIAG}


echo "dropping indexes on ${POP_TABLE}" 
${MGD_DBSCHEMADIR}/index/${POP_TABLE}_drop.object | tee -a ${LOG_DIAG}

echo "bcp'ing data into ${POP_TABLE}"
cat ${MGD_DBPASSWORDFILE} | bcp ${MGD_DBNAME}..${POP_TABLE} in ${OUTPUTDIR}/${POP_TABLE}.bcp -c -t\| -S${MGD_DBSERVER} -U${MGD_DBUSER} | tee -a ${LOG_DIAG}

echo "creating indexes on ${POP_TABLE}"
${MGD_DBSCHEMADIR}/index/${POP_TABLE}_create.object | tee -a ${LOG_DIAG}

echo "updating statistics on ${POP_TABLE}"
${MGIDBUTILSDIR}/bin/updateStatistics.csh ${MGD_DBSERVER} ${MGD_DBNAME} ${POP_TABLE} | tee -a ${LOG_DIAG}

#echo "dropping indexes on ${ACC_TABLE}"
#${MGD_DBSCHEMADIR}/index/${ACC_TABLE}_drop.object | tee -a ${LOG_DIAG}

echo "bcp'ing data into ${ACC_TABLE}"
cat ${MGD_DBPASSWORDFILE} | bcp ${MGD_DBNAME}..${ACC_TABLE} in ${OUTPUTDIR}/${ACC_TABLE}.pop.bcp -c -t \| -S${MGD_DBSERVER} -U${MGD_DBUSER} | tee -a ${LOG_DIAG}

#echo "creating indexes on ${ACC_TABLE}"
#${MGD_DBSCHEMADIR}/index/${ACC_TABLE}_create.object | tee -a ${LOG_DIAG}

#echo "updating statistics on ${ACC_TABLE}"
#${MGIDBUTILSDIR}/bin/updateStatistics.csh ${MGD_DBSERVER} ${MGD_DBNAME} ${ACC_TABLE} | tee - a ${LOG_DIAG}

date | tee -a ${LOG} ${LOG_DIAG}
