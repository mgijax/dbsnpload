#!/bin/csh -fx
#
## add strains to snp_strain
#
#
####----------------------###
###--- initialization ---###
###----------------------###

if ( ${?MGICONFIG} == 0 ) then
        setenv MGICONFIG /usr/local/mgi/live/mgiconfig
endif

source ${MGICONFIG}/master.config.csh

setenv LOG $0.log
rm -rf ${LOG}
touch ${LOG}

date | tee -a ${LOG}
echo '--- starting part 1' | tee -a $LOG

echo 'MGD_DBNAME='$MGD_DBNAME | tee -a $LOG
echo 'MGD_DBPASSWORDFILE='$MGD_DBPASSWORDFILE | tee -a $LOG
echo 'MGD_DBSERVER='$MGD_DBSERVER | tee -a $LOG
echo 'MGD_DBUSER='$MGD_DBUSER | tee -a $LOG

date | tee -a ${LOG}
echo '--- running addSnpStrain.csh' | tee -a ${LOG}

cat - <<EOSQL | ${PG_DBUTILS}/bin/doisql.csh $0 >>& $LOG
insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 28319, '129P2/OlaHsd', 1)
;
insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 39224, '129S5/SvEvBrd', 1)
;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 32853, 'B10.RIII-H2<r> H2-T18<b>/(71NS)SnJ', 1)
;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 11, 'C3H/HeH', 1)
;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 218, 'C57BL/10SnJ', 1)
;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 39344, 'C57BL/6NJ', 1) ;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 69579, 'JF1/MsJ', 1)
;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 29001, 'LEWES/EiJ', 1)
;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 32915, 'NZO/HlLtJ', 1)
;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 1368, 'PWK/PhJ', 1)
;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 110120, 'QSi3/Ianm', 1)
;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 89851, 'QSi5/Ianm', 1)
;

insert into snp_strain values((select max(_snpstrain_key) + 1 from snp_strain), 18, 'RF/J', 1)
;

EOSQL

date | tee -a ${LOG}
echo '--- finished running addSnpStrain.csh' | tee -a ${LOG}

