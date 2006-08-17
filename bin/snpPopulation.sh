#!/bin/sh

#
# Usage:  snpPopulations.sh
#
# History
#
# sc    03/14/2006 - updated to load snp..SNP_Population
# sc    08/17/2005
#
# This script 
# 1. loads SNP_Population and
# 2. creates accession objects to associate a popid
#     with each SNP_Population
# Note: were are not dropping ACC_Accession indexes or updating
#       statistics since there are so few population ids 

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
CONFIG_LOAD=`pwd`/dbsnpload.config
#
#  Make sure the configuration file is readable.
#
if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}" | tee -a ${LOG}
    exit 1
fi

#
# Source the DBSNP Load configuration files
#
. ${CONFIG_LOAD}

# snpPopulation log
POP_LOG=${LOGDIR}/snpPopulation.log
rm ${POP_LOG}
touch ${POP_LOG}

checkstatus ()
{

    if [ $1 -ne 0 ]
    then
        echo "$2 Failed. Return status: $1" | tee -a ${POP_LOG}
	date | tee -a ${POP_LOG}
        exit 1
    fi
    echo "$2 completed successfully" | tee -a ${POP_LOG}

}

date | tee -a ${LOG} ${POP_LOG}

echo "creating population input file in ${POP_FILE}" | tee -a ${POP_LOG}
/usr/bin/cat ${GENO_SNP_INFILEDIR}/*.xml | grep "<Population" | cut -f2-4 > ${POP_FILE}.all
/usr/bin/cat ${POP_FILE}.all | sort > ${POP_FILE}.all.sort
/usr/bin/cat ${POP_FILE}.all.sort | uniq > ${POP_FILE}

echo "creating population bcp file"
${INSTALLDIR}/bin/snpPopulation.py tee -a ${POP_LOG}
STAT=$?
msg="snpPopulation.py "
checkstatus ${STAT} "${msg}"

# Allow bcp into database
${MGIDBUTILSDIR}/bin/turnonbulkcopy.csh ${SNPBE_DBSERVER} ${SNPBE_DBNAME} | tee -a ${POP_LOG}

echo "truncating ${POP_TABLE}"
${SNPBE_DBSCHEMADIR}/table/${POP_TABLE}_truncate.object | tee -a ${POP_LOG}

echo "truncating ${ACC_TABLE}"
${SNPBE_DBSCHEMADIR}/table/${ACC_TABLE}_truncate.object | tee -a ${POP_LOG}

echo "dropping indexes on ${POP_TABLE}" 
${SNPBE_DBSCHEMADIR}/index/${POP_TABLE}_drop.object | tee -a ${POP_LOG}

echo "bcp'ing data into ${POP_TABLE}"
cat ${MGD_DBPASSWORDFILE} | bcp ${SNPBE_DBNAME}..${POP_TABLE} in ${OUTPUTDIR}/${POP_TABLE}.bcp -c -t\| -S${SNPBE_DBSERVER} -U${SNPBE_DBUSER} | tee -a  ${POP_LOG}

echo "creating indexes on ${POP_TABLE}"
${SNPBE_DBSCHEMADIR}/index/${POP_TABLE}_create.object | tee -a ${POP_LOG}

echo "updating statistics on ${POP_TABLE}"
${MGIDBUTILSDIR}/bin/updateStatistics.csh ${SNPBE_DBSERVER} ${SNPBE_DBNAME} ${POP_TABLE} | tee -a ${POP_LOG}

# Note we don't drop indexes on ACC_TABLE as there aren't many records
echo "bcp'ing data into ${ACC_TABLE}"
cat ${MGD_DBPASSWORDFILE} | bcp ${SNPBE_DBNAME}..${ACC_TABLE} in ${OUTPUTDIR}/${ACC_TABLE}.pop.bcp -c -t \| -S${SNPBE_DBSERVER} -U${SNPBE_DBUSER} | tee -a ${POP_LOG}

date | tee -a ${LOG}  ${POP_LOG}
