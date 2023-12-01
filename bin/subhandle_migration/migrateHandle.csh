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

cat - <<EOSQL | ${PG_DBUTILS}/bin/doisql.csh $0 >>& $LOG

delete from snp_population where _population_key in (28, 29)
;

--insert into VOC_Term values(nextval('voc_term_seq'), 51, 'EVA_MGPV3', null, null, 45, 0, 1001, 1001, now(), now())
--;

--select setval('voc_term_seq', (select max(_Term_key) from voc_term))
--;

-- verify new term
select * from voc_term
where _vocab_key = 51
;

-- update snp_population with correct _subhandle_key
select p._population_key, p.subhandle,
        p._subhandle_key as badKey, t.term, t._term_key as goodKey
    into temporary table handleUpdates
    from snp_population p, voc_term t
    where p.subhandle = t.term
    and t._vocab_key = 51
;

-- before updates report
select * from handleUpdates
;

create index idx1 on handleUpdates(_population_key)
;

update snp_population
set _subhandle_key = h.goodKey
from handleUpdates h
where h._population_key = snp_population._population_key
;

-- after population update report
select p._population_key, p.name, p.subhandle, p._subhandle_key as popHandleKey, t.term, t._term_key as termHandleKey
from snp_population p, voc_term t
where p.subhandle = t.term
and t._vocab_key = 51
order by _population_key
;

-- update snp_subsnp with correct handle keys
update snp_subsnp
set _subhandle_key = h.goodKey
from handleUpdates h
where snp_subsnp._subhandle_key  = h.badKey
;

-- after subsnp update report
select count(*)
from snp_subsnp s
where not exists (select 1
from voc_term t
where s._subhandle_key = t._term_key
and t._vocab_key = 51)
;

EOSQL

date | tee -a ${LOG}

