Module.item=selectall;selectdata;deletedata;insertdata;updatedata;existsSpecialVein

//根据EXCPHATYPE_CODE查询全字段
selectdata.Type=TSQL
selectdata.SQL=SELECT SEQ, EXCPHATYPE_CODE,EXCPHATYPE_CHN_DESC,PY1,PY2,EXCPHATYPE_ENG_DESC ,DESCRIPTION,PRINT_FLG,OPT_USER ,OPT_DATE  FROM UDD_EXCPHATYPE  WHERE EXCPHATYPE_CODE = <EXCPHATYPE_CODE> ORDER BY SEQ
selectdata.Debug=N


//根据EXCPHATYPE_CODE删除
deletedata.Type=TSQL
deletedata.SQL=DELETE UDD_EXCPHATYPE  WHERE EXCPHATYPE_CODE = <EXCPHATYPE_CODE> 
deletedata.Debug=N

//根据EXCPHATYPE_CODE更新全字段
updatedata.Type=TSQL
updatedata.SQL=UPDATE UDD_EXCPHATYPE SET SEQ=<SEQ>,PY1=<PY1>,PY2=<PY2>,EXCPHATYPE_CHN_DESC=<EXCPHATYPE_CHN_DESC> ,EXCPHATYPE_ENG_DESC=<EXCPHATYPE_ENG_DESC>,PRINT_FLG=<PRINT_FLG> ,DESCRIPTION=<DESCRIPTION>,OPT_USER =<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> WHERE EXCPHATYPE_CODE = <EXCPHATYPE_CODE> 
updatedata.Debug=N

//根据EXCPHATYPE_CODE插入全字段
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO UDD_EXCPHATYPE (EXCPHATYPE_CODE,EXCPHATYPE_CHN_DESC,EXCPHATYPE_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,PRINT_FLG,OPT_USER,OPT_TERM,OPT_DATE ) VALUES( <EXCPHATYPE_CODE> ,<EXCPHATYPE_CHN_DESC> ,<EXCPHATYPE_ENG_DESC>,<PY1>,<PY2>,<SEQ>,<DESCRIPTION>,<PRINT_FLG>,<OPT_USER>,<OPT_TERM>,<OPT_DATE>)
insertdata.Debug=N

//查询全字段
selectall.Type=TSQL
selectall.SQL=SELECT SEQ, EXCPHATYPE_CODE,EXCPHATYPE_CHN_DESC,PY1,PY2,EXCPHATYPE_ENG_DESC ,DESCRIPTION,PRINT_FLG,OPT_USER ,OPT_DATE  FROM UDD_EXCPHATYPE ORDER BY SEQ
selectall.Debug=N

//是否存在EXCPHATYPE_CODE
existsSpecialVein.type=TSQL
existsSpecialVein.SQL=SELECT COUNT(EXCPHATYPE_CODE) AS COUNT FROM UDD_EXCPHATYPE WHERE EXCPHATYPE_CODE=<EXCPHATYPE_CODE>