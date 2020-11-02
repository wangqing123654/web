####################################################
#  Title:�Ƴ�������module
# 
#  Description:�Ƴ�������module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.9.24
#  version 1.0
####################################################
Module.item=selectdata;insertdata;updatedata;deletedata

//��ѯ����
selectdata.Type=TSQL
selectdata.SQL=SELECT &
		   A.DEPT_CODE, A.OP_CODE,B.OPT_CHN_DESC, A.SEQ, &
		   A.OPT_USER, A.OPT_DATE, A.OPT_TERM,B.OPT_ENG_DESC &
		FROM OPE_DEPTOP A,SYS_OPERATIONICD B &
		WHERE A.OP_CODE=B.OPERATION_ICD(+) &
		ORDER BY A.SEQ
selectdata.item=DEPT_CODE;OP_CODE
selectdata.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectdata.OP_CODE=A.OP_CODE=<OP_CODE>
selectdata.Debug=N

//��������
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO OPE_DEPTOP ( &
		   DEPT_CODE, OP_CODE, SEQ, &
		   OPT_USER, OPT_DATE, OPT_TERM) &
		VALUES ( &
		    <DEPT_CODE>, <OP_CODE>, <SEQ>, &
		   <OPT_USER>, SYSDATE, <OPT_TERM> &
		)
insertdata.Debug=N

//��������
updatedata.Type=TSQL
updatedata.SQL=UPDATE OPE_DEPTOP SET &
		   SEQ=<SEQ>, &
		   OPT_USER=<OPT_USER>, &
		   OPT_DATE=SYSDATE, &
		   OPT_TERM=<OPT_TERM> &
		WHERE DEPT_CODE=<DEPT_CODE> &
		AND OP_CODE=<OP_CODE>
updatedata.Debug=N

//ɾ������
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM OPE_DEPTOP WHERE DEPT_CODE=<DEPT_CODE> AND OP_CODE=<OP_CODE>
deletedata.Debug=N