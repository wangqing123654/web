#
# Title:护嘱类别
#
# Description:护嘱类别
#
# Copyright: JavaHis (c) 2011
# @author luhai 2011/05/04
Module.item=insertData;selectData;deleteData;updateData;checkDataExist

insertData.Type=TSQL
insertData.SQL= INSERT INTO CLP_NURSETYPE (TYPE_CODE,REGION_CODE,TYPE_CHN_DESC,TYPE_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,CLASS_FLG,OPT_USER,OPT_DATE,OPT_TERM)&
								VALUES (<TYPE_CODE>,<REGION_CODE>,<TYPE_CHN_DESC>,<TYPE_ENG_DESC>,<PY1>,<PY2>,&
								(SELECT CASE WHEN <SEQ> IS NULL THEN (SELECT CASE  (COUNT(MAX(SEQ))) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ)+1)) END FROM CLP_NURSETYPE GROUP BY SEQ) ELSE <SEQ> END FROM DUAL),&
								<DESCRIPTION>,<CLASS_FLG>,<OPT_USER>,TO_DATE(<OPT_DATE>,'YYYYMMDD'),<OPT_TERM>)
insertData.Debug=N

selectData.Type=TSQL
selectData.SQL=SELECT TYPE_CODE,REGION_CODE,TYPE_CHN_DESC,TYPE_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,CLASS_FLG,OPT_USER,OPT_DATE,OPT_TERM &
							 FROM CLP_NURSETYPE WHERE 1=1 AND REGION_CODE = <REGION_CODE>
selectData.item=TYPE_CODE;PY2;TYPE_CHN_DESC;TYPE_ENG_DESC;CLASS_FLG;DESCRIPTION;SEQ
selectData.TYPE_CODE=TYPE_CODE LIKE <TYPE_CODE>
selectData.PY2= PY2 LIKE <PY2> 
selectData.TYPE_CHN_DESC= TYPE_CHN_DESC LIKE <TYPE_CHN_DESC>
selectData.TYPE_ENG_DESC= TYPE_ENG_DESC LIKE <TYPE_ENG_DESC>
selectData.CLASS_FLG= CLASS_FLG = <CLASS_FLG>
selectData.DESCRIPTION= DESCRIPTION LIKE <DESCRIPTION>
selectData.SEQ= SEQ LIKE <SEQ>
selectData.Debug=N


deleteData.Type=TSQL
deleteData.SQL=	DELETE FROM  CLP_NURSETYPE  WHERE TYPE_CODE = <TYPE_CODE> &
AND REGION_CODE = <REGION_CODE> 
deleteData.Debug=N

updateData.Type=TSQL
updateData.SQL=UPDATE CLP_NURSETYPE SET TYPE_CHN_DESC=<TYPE_CHN_DESC>,TYPE_ENG_DESC=<TYPE_ENG_DESC>,PY1=<PY1>,PY2=<PY2>,&
							 SEQ=(SELECT CASE WHEN <SEQ> IS NULL THEN (SELECT CASE  (COUNT(MAX(SEQ))) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ)+1)) END FROM CLP_NURSETYPE GROUP BY SEQ) ELSE <SEQ> END FROM DUAL),&
							 DESCRIPTION=<DESCRIPTION>,CLASS_FLG=<CLASS_FLG>,OPT_USER=<OPT_USER>,OPT_DATE=TO_DATE(<OPT_DATE>,'YYYYMMDD'),OPT_TERM=<OPT_TERM> &
  						 WHERE TYPE_CODE = <TYPE_CODE> 
updateData.Debug=N

checkDataExist.Type=TSQL
checkDataExist.SQL=	SELECT COUNT(*) AS DATACOUNT FROM  CLP_NURSETYPE  WHERE REGION_CODE=<REGION_CODE>&
AND TYPE_CODE = <TYPE_CODE>
checkDataExist.Debug=N
