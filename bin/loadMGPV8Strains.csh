#!/bin/csh -fx

###----------------------###
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

#
date | tee -a ${LOG}
cat - <<EOSQL | ${PG_DBUTILS}/bin/doisql.csh $0 >>& $LOG
insert into snp_strain values(89, 39224, '129S5/SvEvBrdi', 1)
;

insert into snp_strain values(90,39344, 'C57BL/6NJ', 1)
;

insert into snp_strain values(91, 110120, 'QSi3/Ianm', 1)
;

insert into snp_strain values(92, 11, 'C3H/HeH', 1)
;

insert into snp_strain values(93, 32853, 'B10.RIII-H2<r> H2-T18<b>/(71NS)SnJ', 1)
;

insert into snp_strain values(94, 28319, '129P2/OlaHsd', 1)
;

insert into snp_strain values(95, 29001, 'LEWES/EiJ', 1)
;

insert into snp_strain values(96, 32915, 'NZO/HlLtJ', 1)
;

insert into snp_strain values(97, 218, 'C57BL/10SnJ', 1)
;

insert into snp_strain values(98, 1368, 'PWK/PhJ', 1)
;

insert into snp_strain values(99, 89851, 'QSi5/Ianm', 1)
;

insert into snp_strain values(100, 18, 'RF/J', 1)
;

insert into snp_strain values(101, 69579, 'JF1/MsJ', 1)
;

EOSQL
date | tee -a ${LOG}

