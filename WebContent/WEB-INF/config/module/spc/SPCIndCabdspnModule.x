# 
#  Title 护士审核主档module
# 
#  Description:护士审核主档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author robo 2013.5.23
#  version 1.0
#
Module.item=query;insert;delete


query.Type=TSQL
query.SQL= SELECT CASE_NO, ORDER_NO, ORDER_SEQ, START_DTTM, END_DTTM, &
    		REGION_CODE, STATION_CODE, DEPT_CODE, VS_DR_CODE, BED_NO, &
    		MR_NO, DSPN_KIND, DSPN_DATE, DSPN_USER, ORDER_CAT1_CODE, &
    		CAT1_TYPE, ORDER_CODE, ORDER_DESC, GOODS_DESC, SPECIFICATION, &
    		MEDI_QTY, MEDI_UNIT, FREQ_CODE, ROUTE_CODE, TAKE_DAYS, &
    		DOSAGE_QTY, DOSAGE_UNIT, DISPENSE_QTY, DISPENSE_UNIT, PHA_DISPENSE_NO, &
    		TAKEMED_ORG, TAKEMED_NO, IS_CONFIRM, TAKEMED_USER, TAKEMED_DATE, &
    		IS_RECLAIM, RECLAIM_USER, RECLAIM_DATE, TOXIC_ID1, TOXIC_ID2, &
    		TOXIC_ID3, OPT_USER, OPT_DATE, OPT_TERM   &
	    FROM IND_CABDSPN  &
query.item=CASE_NO;ORDER_NO;ORDER_SEQ;START_DTTM
query.CASE_NO=CASE_NO=<CASE_NO>
query.ORDER_NO=ORDER_NO=<ORDER_NO>
query.ORDER_SEQ=ORDER_SEQ=<ORDER_SEQ>
query.START_DTTM=START_DTTM=<START_DTTM>
query.Debug=N


insert.Type=TSQL
insert.SQL=INSERT INTO IND_CABDSPN  &
   		(CASE_NO, ORDER_NO, ORDER_SEQ, START_DTTM, END_DTTM, &
    		REGION_CODE, STATION_CODE, DEPT_CODE, VS_DR_CODE, BED_NO, &
    		MR_NO, DSPN_KIND, DSPN_DATE, DSPN_USER, ORDER_CAT1_CODE, &
    		CAT1_TYPE, ORDER_CODE, ORDER_DESC, GOODS_DESC, SPECIFICATION, &
    		MEDI_QTY, MEDI_UNIT, FREQ_CODE, ROUTE_CODE, TAKE_DAYS, &
    		DOSAGE_QTY, DOSAGE_UNIT, DISPENSE_QTY, DISPENSE_UNIT, PHA_DISPENSE_NO, &
    		TAKEMED_ORG, TAKEMED_NO, IS_CONFIRM, TAKEMED_USER, TAKEMED_DATE, &
    		IS_RECLAIM, RECLAIM_USER, RECLAIM_DATE, TOXIC_ID1, TOXIC_ID2, &
    		TOXIC_ID3, OPT_USER, OPT_DATE, OPT_TERM,CTRLDRUGCLASS_CODE ) &
		VALUES (  &
		<CASE_NO>, <ORDER_NO>, <ORDER_SEQ>, <START_DTTM>, <END_DTTM>, &
    		<REGION_CODE>, <STATION_CODE>, <DEPT_CODE>, <VS_DR_CODE>, <BED_NO>, &
    		<MR_NO>, <DSPN_KIND>, <DSPN_DATE>, <DSPN_USER>, <ORDER_CAT1_CODE>, &
    		<CAT1_TYPE>, <ORDER_CODE>,<ORDER_DESC>, <GOODS_DESC>, <SPECIFICATION>, &
    		<MEDI_QTY>, <MEDI_UNIT>, <FREQ_CODE>, <ROUTE_CODE>, <TAKE_DAYS>, &
    		<DOSAGE_QTY>, <DOSAGE_UNIT>, <DISPENSE_QTY>, <DISPENSE_UNIT>, <PHA_DISPENSE_NO>, &
    		<TAKEMED_ORG>, <TAKEMED_NO>, <IS_CONFIRM>, <TAKEMED_USER>, <TAKEMED_DATE>, &
    		<IS_RECLAIM>, <RECLAIM_USER>, <RECLAIM_DATE>, <TOXIC_ID1>, <TOXIC_ID2>, &
    		<TOXIC_ID3>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>,<CTRLDRUGCLASS_CODE>)
insert.Debug=Y

delete.Type=TSQL
delete.SQL=DELETE FROM IND_CABDSPN WHERE CASE_NO=<CASE_NO> AND  ORDER_NO=<ORDER_NO> AND  ORDER_SEQ=<ORDER_SEQ> AND START_DTTM=<START_DTTM>
delete.Debug=Y


