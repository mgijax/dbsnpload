#!/bin/sh

# this script bcp's all the mgp bcp files into a database. 
#
# DP_SNP_Marker
# SNP_Accession
# SNP_ConsensusSnp
# SNP_ConsensusSnp_StrainAllele
# SNP_Coord_Cache
# SNP_Flank
# SNP_SubSnp
# SNP_SubSnp_StrainAllele


cd `dirname $0`/..
CONFIG_LOAD=`pwd`/mgpload.config

if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}"
    exit 1
fi

. ${CONFIG_LOAD}

LOG=${LOGDIR}/doMgpBcp.log
rm -f ${LOG}
date | tee -a ${LOG}

# bcp file row delimiter
NL="\n"

# bcp file column delimiter
DL="\t"

# name of the snp schema
SCHEMA='snp'
export SCHEMA

#####################################
echo "Processing DP_SNP_Marker.bcp"

TABLE='DP_SNP_Marker'
export TABLE

BCPFILEPATH=${OUTPUTDIR}/DP_SNP_Marker.bcp

echo ""

date | tee -a ${LOG}

echo "disabling triggers on table ${TABLE} ${SCHEMA}"
${MGD_DBSCHEMADIR}/trigger/disable_triggers.sh ${TABLE} ${SCHEMA}

echo "copying into table ${TABLE}"

date | tee -a ${LOG}
echo "copying file ${BCPFILEPATH}" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}

#####################################
echo "Processing SNP_Accession bcp files"

TABLE='SNP_Accession'
export TABLE

BCPFILEPATH=${OUTPUTDIR}/SNP_Accession.bcp

echo ""

date | tee -a ${LOG}

echo "disabling triggers on table ${TABLE} ${SCHEMA}"
${MGD_DBSCHEMADIR}/trigger/disable_triggers.sh ${TABLE} ${SCHEMA}

echo "copying into table ${TABLE}"

date | tee -a ${LOG}
echo "copying file ${BCPFILEPATH}" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}
#####################################
echo "Processing SNP_ConsensusSnp.bcp"

TABLE='SNP_ConsensusSnp'
export TABLE

BCPFILEPATH=${OUTPUTDIR}/SNP_ConsensusSnp.bcp

echo ""

date | tee -a ${LOG}

echo "disabling triggers on table ${TABLE} ${SCHEMA}"
${MGD_DBSCHEMADIR}/trigger/disable_triggers.sh ${TABLE} ${SCHEMA}


date | tee -a ${LOG}
echo "copying file ${BCPFILEPATH}" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}

#####################################

echo "Processing SNP_Coord_Cache"

TABLE='SNP_Coord_Cache'
export TABLE

BCPFILEPATH=${OUTPUTDIR}/SNP_Coord_Cache.bcp

echo ""

date | tee -a ${LOG}

echo "disabling triggers on table ${TABLE} ${SCHEMA}"
${MGD_DBSCHEMADIR}/trigger/disable_triggers.sh ${TABLE} ${SCHEMA}


date | tee -a ${LOG}
echo "copying file ${BCPFILEPATH}" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}

#####################################

echo "Processing SNP_Flank"

TABLE='SNP_Flank'
export TABLE

BCPFILEPATH=${OUTPUTDIR}/SNP_Flank.bcp000

echo ""

date | tee -a ${LOG}

echo "disabling triggers on table ${TABLE} ${SCHEMA}"
${MGD_DBSCHEMADIR}/trigger/disable_triggers.sh ${TABLE} ${SCHEMA}


date | tee -a ${LOG}
echo "copying file ${BCPFILEPATH}" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}

#####################################

echo "Processing SNP_SubSnp"

TABLE='SNP_SubSnp'
export TABLE

BCPFILEPATH=${OUTPUTDIR}/SNP_SubSnp.bcp

echo ""

date | tee -a ${LOG}

echo "disabling triggers on table ${TABLE} ${SCHEMA}"
${MGD_DBSCHEMADIR}/trigger/disable_triggers.sh ${TABLE} ${SCHEMA}


date | tee -a ${LOG}
echo "copying file ${BCPFILEPATH}" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}


######################################

echo "Processing SNP_ConsensusSnp_StrainAllele bcp files"

TABLE='SNP_ConsensusSnp_StrainAllele'
export TABLE

BCPFILEPATH=${OUTPUTDIR}/SNP_ConsensusSnp_StrainAllele.bcp

echo ""

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
    exit 1
fi

date | tee -a ${LOG}
echo "copying file 004" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}004' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}004" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}
echo "copying file 005" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}005' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}005" | tee -a ${LOG}
    exit 1
fi

##########################################
echo "Processing SNP_SubSnp_StrainAllele bcp files"

TABLE='SNP_SubSnp_StrainAllele'
export TABLE

BCPFILEPATH=${OUTPUTDIR}/SNP_SubSnp_StrainAllele.bcp

echo ""

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
    exit 1
fi

date | tee -a ${LOG}
echo "copying file 004" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}004' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}004" | tee -a ${LOG}
    exit 1
fi

date | tee -a ${LOG}
echo "copying file 005" | tee -a ${LOG}
psql -a -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "\copy ${SCHEMA}.${TABLE} from '${BCPFILEPATH}005' with null as '' delimiter as E'${DL}';"
STAT=$?
echo "exit code ${STAT}"
if [ ${STAT} -ne 0 ]
then
    echo "pg copy failed on ${BCPFILEPATH}005" | tee -a ${LOG}
n" | tee -a ${LOG}
    exit 1
fi

echo "enabling triggers on table ${TABLE} ${SCHEMA}"
${MGD_DBSCHEMADIR}/trigger/enable_triggers.sh ${TABLE} ${SCHEMA}

sleep 5

echo "about to analyze table ${TABLE}"
psql -h${MGD_DBSERVER} -d${MGD_DBNAME} -U${MGD_DBUSER} --command "analyze ${SCHEMA}.${TABLE};"
date | tee -a ${LOG}
