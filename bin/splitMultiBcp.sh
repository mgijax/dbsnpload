#!/bin/sh

# this script splits up BCP files then bcp them into the database

cd `dirname $0`/..
CONFIG_LOAD=`pwd`/mgpload.config

if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}"
    exit 1
fi

. ${CONFIG_LOAD}

LOG=${LOGDIR}/splitBcp.log
rm -f ${LOG}
date | tee -a ${LOG}

# bcp file row delimiter
NL="\n"

# bcp file column delimiter
DL="\t"

# name of the snp schema
SCHEMA='snp'
export SCHEMA

# We will split files >= maxsizeK
maxsizeK=10000000
# number of lines for each file; we tried using --bytes equal to maxsizeK
# but this split files in the middle of lines
maxsizeLines="500000000"

echo ""
echo "processing split bcp files"
for f in `ls  ${OUTPUTDIR} | grep bcp0`
do
    date | tee -a ${LOG}
    echo $f
    table="$(cut -d'.' -f1 <<<$f)"
    echo $table
    echo "${PG_DBUTILS}/bin/bcpin.csh ${PG_DBSERVER} ${PG_DBNAME} ${table} ${OUTPUTDIR} ${f} ${DL} ${NL} ${SCHEMA}"

    ${PG_DBUTILS}/bin/bcpin.csh ${PG_DBSERVER} ${PG_DBNAME} ${table} ${OUTPUTDIR} ${f} ${DL} ${NL} ${SCHEMA} >> ${LOG} 2>&1
    STAT=$?
    echo "bcpin.csh exit code ${STAT}"
    if [ ${STAT} -ne 0 ]
    then
       echo "bcpin.csh failed on $f" | tee -a ${LOG}
       exit 1
    fi
done
date | tee -a ${LOG}
