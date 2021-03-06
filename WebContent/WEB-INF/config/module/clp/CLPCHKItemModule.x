Module.item=insertData;selectData;deleteData;updateData;checkDataExist;maxSEQQuery

insertData.Type=TSQL
insertData.SQL=INSERT INTO CLP_CHKITEM (REGION_CODE,CHKTYPE_CODE,CHKITEM_CODE,CHKITEM_CHN_DESC,CLNCPATH_CODE,CHKUSER_CODE,INDV_FLG,&
OPT_USER,OPT_DATE,OPT_TERM,PY1,CHKITEM_ENG_DESC,DESCRIPTION,SEQ )&
VALUES( <REGION_CODE>,<CHKTYPE_CODE>,<CHKITEM_CODE>,<CHKITEM_CHN_DESC>,<CLNCPATH_CODE>,<CHKUSER_CODE>,<INDV_FLG>,&
<OPT_USER>,to_date(<OPT_DATE>,'yyyyMMdd'),<OPT_TERM>,<PY1>,<CHKITEM_ENG_DESC>,<DESCRIPTION>,<SEQ>)
insertData.Debug=N

selectData.Type=TSQL
selectData.SQL=SELECT A.REGION_CODE,A.CHKTYPE_CODE,A.CHKITEM_CODE,PY2,CHKITEM_CHN_DESC,A.CLNCPATH_CODE,A.CHKUSER_CODE,A.INDV_FLG,&
A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.PY1,A.CHKITEM_ENG_DESC,A.DESCRIPTION,A.SEQ,B.ORDTYPE_CODE,B.CLP_UNIT,B.CLP_RATE,B.CLP_QTY &
FROM CLP_CHKITEM A ,CLP_ORDERTYPE B &	
WHERE A.CHKITEM_CODE=B.ORDER_CODE(+) AND (B.ORDER_FLG ='N' OR B.ORDER_FLG IS NULL ) AND REGION_CODE=<REGION_CODE>  &
ORDER BY CHKTYPE_CODE,SEQ
selectData.item=CHKTYPE_CODE;CHKUSER_CODE;INDV_FLG;CHKITEM_CODE;CHKITEM_CHN_DESC;CHKITEM_ENG_DESC;DESCRIPTION;SEQ
selectData.CHKTYPE_CODE= CHKTYPE_CODE LIKE <CHKTYPE_CODE>
selectData.CHKUSER_CODE= CHKUSER_CODE LIKE <CHKUSER_CODE>
selectData.INDV_FLG=INDV_FLG = <INDV_FLG>
selectData.CHKITEM_CODE=CHKITEM_CODE LIKE <CHKITEM_CODE>
selectData.CHKITEM_CHN_DESC = CHKITEM_CHN_DESC LIKE <CHKITEM_CHN_DESC>
selectData.PY1 = PY1 LIKE <PY1>
selectData.CLNCPATH_CODE = CLNCPATH_CODE LIKE <CLNCPATH_CODE>
selectData.CHKITEM_ENG_DESC= CHKITEM_ENG_DESC LIKE <CHKITEM_ENG_DESC>
selectData.DESCRIPTION=DESCRIPTION LIKE <DESCRIPTION>
selectData.SEQ= SEQ =<SEQ>
selectData.Debug=N


deleteData.Type=TSQL
deleteData.SQL=	DELETE FROM  CLP_CHKITEM  WHERE REGION_CODE = <REGION_CODE> &
AND CHKTYPE_CODE = <CHKTYPE_CODE> AND CHKITEM_CODE = <CHKITEM_CODE> 
deleteData.Debug=N

updateData.Type=TSQL
updateData.SQL=	UPDATE CLP_CHKITEM SET &
CHKUSER_CODE = <CHKUSER_CODE> ,INDV_FLG = <INDV_FLG> ,CHKITEM_CHN_DESC = <CHKITEM_CHN_DESC> ,PY1 = <PY1> ,CLNCPATH_CODE = <CLNCPATH_CODE> ,CHKITEM_ENG_DESC = <CHKITEM_ENG_DESC>, &
DESCRIPTION = <DESCRIPTION> ,SEQ = <SEQ> &
WHERE REGION_CODE = <REGION_CODE> AND CHKTYPE_CODE = <CHKTYPE_CODE> AND CHKITEM_CODE = <CHKITEM_CODE> 
updateData.Debug=N

checkDataExist.Type=TSQL
checkDataExist.SQL=	SELECT COUNT(*) AS DATACOUNT FROM  CLP_CHKITEM  WHERE REGION_CODE=<REGION_CODE>&
AND CHKTYPE_CODE=<CHKTYPE_CODE> AND CHKITEM_CODE = <CHKITEM_CODE>
checkDataExist.Debug=N

maxSEQQuery.Type=TSQL
maxSEQQuery.SQL=SELECT MAX(SEQ) AS MAXSEQ  FROM CLP_CHKITEM
maxSEQQuery.Debug=N