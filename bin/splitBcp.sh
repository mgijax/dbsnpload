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

# Largest bcp file we want in KB
maxsizeK=20000000
# Largest bcp file in G
maxsizeG="2G"

echo "splitting files > $maxsizeG"
for f in `ls ${OUTPUTDIR} | grep bcp$` # listing of bcp files
do
    file=${OUTPUTDIR}/$f
    actualsize=$(du -k "${OUTPUTDIR}/$f" | cut -f 1)
    echo  $file
    echo $actualsize
    if [ $actualsize -ge $maxsizeK ]
    then
	split --bytes $maxsizeG --numeric-suffixes --suffix-length=3 $file $file
	gzip $file
    fi
done 

echo ""
echo "processing unsplit bcp files"

for f in `ls  ${OUTPUTDIR} | grep bcp$`
do
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
echo ""
echo "processing split bcp files"
for f in `ls  ${OUTPUTDIR} | grep bcp0`
do
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
