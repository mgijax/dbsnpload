#!/bin/sh

# this script bcp's in the snp_subsnp_strain allele bcp files

cd `dirname $0`/..
CONFIG_LOAD=`pwd`/mgpload.config

if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}"
    exit 1
fi

. ${CONFIG_LOAD}

LOG=${LOGDIR}/finishBcp.log
rm -f ${LOG}
date | tee -a ${LOG}

# bcp file row delimiter
NL="\n"

# bcp file column delimiter
DL="\t"

# name of the snp schema
SCHEMA='snp'
export SCHEMA

TABLE='SNP_SubSnp_StrainAllele'
export TABLE

BCPFILEPATH=${OUTPUTDIR}/SNP_SubSnp_StrainAllele.bcp

echo ""
echo "processing remaining bcp files"

date | tee -a ${LOG}

echo "disabling triggers on table ${TABLE} ${SCHEMA}"
${MGD_DBSCHEMADIR}/trigger/disable_triggers.sh ${TABLE} ${SCHEMA}

echo "copying into table ${TABLE}"

date | tee -a ${LOG}
echo "copying file 000" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}000' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}000" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}
echo "copying file 001" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}001' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}001" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}
echo "copying file 002" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}002' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}002" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}
echo "copying file 003" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}003' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}003" | tee -a ${LOG}
n" | tee -a ${LOG}
    exit 1
fi

echo "enabling triggers on table ${TABLE} ${SCHEMA}"
${MGD_DBSCHEMADIR}/trigger/enable_triggers.sh ${TABLE} ${SCHEMA}

sleep 5

echo "about to analyze table ${TABLE}"
psql -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "analyze ${SCHEMA}.${TABLE};"
date | tee -a ${LOG}
