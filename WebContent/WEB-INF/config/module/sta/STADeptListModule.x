# 
#  Title:�м䵵�����б�module
# 
#  Description:�м䵵�����б�module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.04.21
#  version 1.0
#
Module.item=selectdata;deletedata;insertdata;updatedata;existsRegMethod;selectMaxSEQ;selectOE_DEPT;selectIPD_DEPT;selectDeptByCode;selectDeptByOECode;selectDeptByIPDCode;selectAll

//��ѯ
selectdata.Type=TSQL
selectdata.SQL=SELECT B.REGION_CHN_DESC,A.DEPT_CODE,A.DEPT_DESC,A.DEPT_LEVEL,A.SEQ,A.OE_DEPT_CODE,A.IPD_DEPT_CODE,A.STATION_CODE,A.PY1,A.REGION_CODE,A.STA_SEQ &
		FROM STA_OEI_DEPT_LIST A ,SYS_REGION B &
		WHERE A.REGION_CODE=B.REGION_CODE AND A.DEPT_CODE LIKE <DEPT_CODE> &
		ORDER BY TO_NUMBER(A.SEQ)
//=============pangben modify 20110519 start
selectdata.item=REGION_CODE
selectdata.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//=============pangben modify 20110519 stop
selectdata.Debug=N

//ɾ��
//=============pangben modify 20110525
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM STA_OEI_DEPT_LIST &
		WHERE STA_SEQ = <STA_SEQ> AND REGION_CODE=<REGION_CODE>

deletedata.Debug=N

//����
//=============pangben modify 20110519 ���������
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO STA_OEI_DEPT_LIST (STA_SEQ,DEPT_CODE,DEPT_DESC,DEPT_LEVEL,SEQ,OE_DEPT_CODE,IPD_DEPT_CODE,STATION_CODE, OPT_USER,OPT_DATE,OPT_TERM,PY1,REGION_CODE) &
		VALUES(<STA_SEQ>,<DEPT_CODE>,<DEPT_DESC>,<DEPT_LEVEL>,<SEQ>,<OE_DEPT_CODE>,<IPD_DEPT_CODE>,<STATION_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>,<PY1>,<REGION_CODE>)
insertdata.Debug=N

//����
updatedata.Type=TSQL
updatedata.SQL=UPDATE STA_OEI_DEPT_LIST SET DEPT_CODE=<DEPT_CODE>, &
		DEPT_DESC=<DEPT_DESC>, &
		DEPT_LEVEL=<DEPT_LEVEL>, &
		SEQ=<SEQ>, &
		OE_DEPT_CODE=<OE_DEPT_CODE>, &
		IPD_DEPT_CODE=<IPD_DEPT_CODE>, &
		STATION_CODE=<STATION_CODE>, &
		OPT_USER=<OPT_USER>, &
		OPT_DATE=SYSDATE, &
		OPT_TERM=<OPT_TERM>, &
		PY1=<PY1>, &
		//=======pangben modify 20110519 
		REGION_CODE=<REGION_CODE> &
		WHERE STA_SEQ=<STA_SEQ>
updatedata.Debug=N

//�Ƿ����
existsRegMethod.type=TSQL
existsRegMethod.SQL=SELECT COUNT(*) AS COUNT FROM REG_REGMETHOD WHERE REGMETHOD_CODE=<REGMETHOD_CODE>
existsRegMethod.Debug=N

//��ѯ��������
selectMaxSEQ.Type=TSQL
selectMaxSEQ.SQL=SELECT MAX(SEQ) AS SEQ FROM STA_OEI_DEPT_LIST
selectMaxSEQ.Debug=N

//��ѯ�����м���е����ﲿ��
selectOE_DEPT.Type=TSQL
selectOE_DEPT.SQL=SELECT A.DEPT_CODE,A.DEPT_DESC,A.DEPT_LEVEL,A.SEQ,A.OE_DEPT_CODE,A.IPD_DEPT_CODE,A.STATION_CODE,A.PY1,A.STA_SEQ &
			FROM STA_OEI_DEPT_LIST A &
			WHERE OE_DEPT_CODE IS NOT NULL &
			ORDER BY TO_NUMBER(SEQ)
//=========pangben modify 20110519 start
selectOE_DEPT.item=REGION_CODE
selectOE_DEPT.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//=========pangben modify 20110519 stop
selectOE_DEPT.Debug=N

//��ѯ�����м���סԺ����
selectIPD_DEPT.Type=TSQL
selectIPD_DEPT.SQL=SELECT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,SEQ,OE_DEPT_CODE,IPD_DEPT_CODE,STATION_CODE,PY1,STA_SEQ & 
			FROM STA_OEI_DEPT_LIST &
			WHERE IPD_DEPT_CODE IS NOT NULL &
			ORDER BY TO_NUMBER(SEQ)
//=========pangben modify 20110519 start
selectIPD_DEPT.item=REGION_CODE
selectIPD_DEPT.REGION_CODE=REGION_CODE=<REGION_CODE>
//=========pangben modify 20110519 stop
selectIPD_DEPT.Debug=N

//���ݿ���CODE��ѯ������Ϣ
//=========pangben modify 20110523 �����������
selectDeptByCode.Type=TSQL
selectDeptByCode.SQL=SELECT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,SEQ,OE_DEPT_CODE,IPD_DEPT_CODE,STATION_CODE,PY1,STA_SEQ & 
			FROM STA_OEI_DEPT_LIST &
			WHERE DEPT_CODE = <DEPT_CODE>
//=========pangben modify 20110523 start
selectDeptByCode.item=REGION_CODE;STATION_CODE
selectDeptByCode.REGION_CODE=REGION_CODE=<REGION_CODE>
selectDeptByCode.STATION_CODE=STATION_CODE=<STATION_CODE>
//=========pangben modify 20110523 stop
selectDeptByCode.Debug=N

//����סԺ���ҿ���CODE��ѯ������Ϣ
selectDeptByIPDCode.Type=TSQL
selectDeptByIPDCode.SQL=SELECT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,SEQ,OE_DEPT_CODE,IPD_DEPT_CODE,STATION_CODE,PY1,STA_SEQ & 
			FROM STA_OEI_DEPT_LIST &
			WHERE IPD_DEPT_CODE = <IPD_DEPT_CODE>
//=========pangben modify 20110524 �����������
selectDeptByIPDCode.item=REGION_CODE
selectDeptByIPDCode.REGION_CODE=REGION_CODE=<REGION_CODE>
selectDeptByIPDCode.Debug=N

//����������ҿ���CODE��ѯ������Ϣ
selectDeptByOECode.Type=TSQL
selectDeptByOECode.SQL=SELECT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,SEQ,OE_DEPT_CODE,IPD_DEPT_CODE,STATION_CODE,PY1,STA_SEQ & 
			FROM STA_OEI_DEPT_LIST &
			WHERE OE_DEPT_CODE = <OE_DEPT_CODE>
//=========pangben modify 20110524 �����������
selectDeptByOECode.item=REGION_CODE
selectDeptByOECode.REGION_CODE=REGION_CODE=<REGION_CODE>
selectDeptByOECode.Debug=N

//��ѯ�м���ҵ�������Ϣ
selectAll.Type=TSQL
selectAll.SQL=SELECT &
		   DEPT_CODE, DEPT_DESC, DEPT_LEVEL, &
		   PY1, SEQ, OE_DEPT_CODE, &
		   IPD_DEPT_CODE,STATION_CODE, OPT_USER, OPT_DATE, &
		   OPT_TERM,STA_SEQ &
		FROM STA_OEI_DEPT_LIST &
		ORDER BY TO_NUMBER(SEQ)	
//=========pangben modify 20110524 �����������
selectAll.item=REGION_CODE
selectAll.REGION_CODE=REGION_CODE=<REGION_CODE>
selectAll.Debug=N