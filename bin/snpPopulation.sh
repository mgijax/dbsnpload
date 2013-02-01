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
if [ -f ${LOG} ]
then
    rm -f ${LOG}
fi

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
if [ -f ${LOG} ]
then
    rm -f ${POP_LOG}
fi
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

#echo "creating population input file in ${POP_FILE}" | tee -a ${POP_LOG}
#/usr/bin/cat ${GENO_SNP_INFILEDIR}/*.xml | grep "<Population" | cut -f2-4 > ${POP_FILE}.all
#/usr/bin/cat ${POP_FILE}.all | sort > ${POP_FILE}.all.sort
#/usr/bin/cat ${POP_FILE}.all.sort | uniq > ${POP_FILE}

echo "creating population bcp file"
${INSTALLDIR}/bin/snpPopulation.py | tee -a ${POP_LOG}
STAT=$?
msg="snpPopulation.py "
checkstatus ${STAT} "${msg}"
exit 0

echo "dropping indexes on ${POP_TABLE}" 
${PG_SNP_DBSCHEMADIR}/index/${POP_TABLE}_drop.object | tee -a ${POP_LOG}

echo "dropping indexes on ${ACC_TABLE}" 
${PG_SNP_DBSCHEMADIR}/index/${ACC_TABLE}_drop.object | tee -a ${POP_LOG}

echo "truncating ${POP_TABLE}"
${PG_SNP_DBSCHEMADIR}/table/${POP_TABLE}_truncate.object | tee -a ${POP_LOG}

echo "truncating ${ACC_TABLE}"
${PG_SNP_DBSCHEMADIR}/table/${ACC_TABLE}_truncate.object | tee -a ${POP_LOG}

echo "bcp'ing data into ${POP_TABLE}"
psql -h ${PG_DBSERVER} -d ${PG_DBNAME} -U ${PG_DBUSER} --command "\copy snp.${POP_TABLE} from '${OUTPUTDIR}/${POP_TABLE}.bcp' with null as ''"

echo "bcp'ing data into ${ACC_TABLE}"
psql -h ${PG_DBSERVER} -d ${PG_DBNAME} -U ${PG_DBUSER} --command "\copy snp.${ACC_TABLE} from '${OUTPUTDIR}/${ACC_TABLE}.pop.bcp' with null as ''"

echo "creating indexes on ${POP_TABLE}"
${PG_SNP_DBSCHEMADIR}/index/${POP_TABLE}_create.object | tee -a ${POP_LOG}

echo "creating indexes on ${ACC_TABLE}"
${PG_SNP_DBSCHEMADIR}/index/${ACC_TABLE}_create.object | tee -a ${POP_LOG}

date | tee -a ${LOG}  ${POP_LOG}
