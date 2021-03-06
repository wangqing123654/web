# 
#  Title:包干套餐维护module
# 
#  Description:包干套餐维护module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author liling 20140507
#  version 1.0
#
Module.item=selectdata;deletedata;insertdata;updatedata;existsQueGroup

insertdata.Type=TSQL
insertdata.SQL=INSERT INTO MEM_LUMPWORK &
//			   (LUMPWORK_CODE,LUMPWORK_DESC,ENG_DESC,PY1,PY2,SEQ,&
			  (LUMPWORK_CODE,LUMPWORK_DESC,ENG_DESC,PY1,SEQ,&
			   FEE,START_DATE,END_DATE,DESCRIPTION,OPT_DATE,OPT_USER,&
			   OPT_TERM,CTZ_CODE) &
//		    VALUES (<LUMPWORK_CODE>,<LUMPWORK_DESC>,<ENG_DESC>,<PY1>,<PY2>,<SEQ>,&
 			VALUES (<LUMPWORK_CODE>,<LUMPWORK_DESC>,<ENG_DESC>,<PY1>,<SEQ>,&
			   <FEE>,to_date(<START_DATE>,'yyyy/MM/dd HH24:mi:ss'),to_date(<END_DATE>,'yyyy/MM/dd HH24:mi:ss'),<DESCRIPTION>,SYSDATE,<OPT_USER>,&
			   <OPT_TERM>,<CTZ_CODE>)
insertdata.Debug=N

updatedata.Type=TSQL
updatedata.SQL=UPDATE  MEM_LUMPWORK &
//		SET LUMPWORK_CODE=<LUMPWORK_CODE>,LUMPWORK_DESC=<LUMPWORK_DESC>,ENG_DESC=<ENG_DESC>,PY1=<PY1>,PY2=<PY2>,SEQ=<SEQ>,&
		SET LUMPWORK_CODE=<LUMPWORK_CODE>,LUMPWORK_DESC=<LUMPWORK_DESC>,ENG_DESC=<ENG_DESC>,PY1=<PY1>,SEQ=<SEQ>,CTZ_CODE=<CTZ_CODE>,&
			    FEE=<FEE>,START_DATE=to_date(<START_DATE>,'yyyy/MM/dd HH24:mi:ss'),END_DATE=to_date(<END_DATE>,'yyyy/MM/dd HH24:mi:ss'),DESCRIPTION=<DESCRIPTION>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                WHERE LUMPWORK_CODE = <LUMPWORK_CODE> 
updatedata.Debug=N 

selectdata.Type=TSQL
selectdata.SQL=SELECT LUMPWORK_CODE,LUMPWORK_DESC,ENG_DESC,PY1,SEQ,&
//		SELECT LUMPWORK_CODE,LUMPWORK_DESC,ENG_DESC,PY1,PY2,SEQ,&
			   FEE,START_DATE,END_DATE,DESCRIPTION,OPT_DATE,OPT_USER,&
			   OPT_TERM,CTZ_CODE  &
		 FROM MEM_LUMPWORK &
	     ORDER BY SEQ,LUMPWORK_CODE
selectdata.item=LUMPWORK_CODE;LUMPWORK_DESC
selectdata.LUMPWORK_CODE=LUMPWORK_CODE=<LUMPWORK_CODE>
selectdata.LUMPWORK_DESC=LUMPWORK_DESC=<LUMPWORK_DESC>
selectdata.Debug=N

deletedata.Type=TSQL
deletedata.SQL=DELETE FROM MEM_LUMPWORK  &
		     WHERE LUMPWORK_CODE=<LUMPWORK_CODE>
deletedata.Debug=N

//是否存在学号
existsQueGroup.type=TSQL
existsQueGroup.SQL=SELECT COUNT(*) AS COUNT &
		      FROM MEM_LUMPWORK  &
		     WHERE LUMPWORK_CODE=<LUMPWORK_CODE>
existsQueGroup.Debug=N