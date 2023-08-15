#!/usr/bin/sh

cd `dirname $0`/..

CONFIG_LOAD=`pwd`/mgpload.config
echo ${CONFIG_LOAD}

cd `dirname $0`
LOG=`pwd`/dbsnpload.log
rm -rf ${LOG}

echo ${CONFIG_LOAD}

#
# verify & source the configuration file
#

echo "Verifying ${CONFIG_LOAD}"
if [ ! -r ${CONFIG_LOAD} ]
then
    echo "Cannot read configuration file: ${CONFIG_LOAD}"
    exit 1
fi

. ${CONFIG_LOAD}

date >  ${MGP_VCF_LOG_FILE}
echo 'Processing MGP' |  tee -a ${MGP_VCF_LOG_FILE}
zcat ${MGP_VCF_INPUT_FILE} | ${PYTHON} ${DBSNPLOAD}/bin/filter_vcf_snps.py | tee -a ${MGP_VCF_LOG_FILE}

echo 'Done processing MGP' |  tee -a ${MGP_VCF_LOG_FILE}
date |  tee -a ${MGP_VCF_LOG_FILE}
