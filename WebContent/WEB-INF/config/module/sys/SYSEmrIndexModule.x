# 
#  Title:����������module
# 
#  Description:����������module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.06.15
#  version 1.0
#
Module.item=existdata;insertOpdEmg;insertIpd;updateIpd

//��ѯ�����������Ƿ����
existdata.Type=TSQL
existdata.SQL=SELECT COUNT(CASE_NO) AS COUNT FROM SYS_EMR_INDEX WHERE CASE_NO=<CASE_NO>
existdata.Debug=N


//�����������Һ�д�벡��������
insertOpdEmg.Type=TSQL
insertOpdEmg.SQL=INSERT INTO SYS_EMR_INDEX( &
			CASE_NO, ADM_TYPE, REGION_CODE, MR_NO, ADM_DATE, &
			DEPT_CODE, DR_CODE, OPT_USER, OPT_DATE, OPT_TERM) &
		 VALUES(<CASE_NO>, <ADM_TYPE>, <REGION_CODE>, <MR_NO>, <ADM_DATE>, &
			<DEPT_CODE>, <DR_CODE>, <OPT_USER>, SYSDATE, <OPT_TERM>)
insertOpdEmg.Debug=N


//����סԺ�Ǽ�д�벡��������
insertIpd.Type=TSQL
insertIpd.SQL=INSERT INTO SYS_EMR_INDEX( &
		     CASE_NO, ADM_TYPE, REGION_CODE, MR_NO, ADM_DATE, &
		     DEPT_CODE, DR_CODE, OPT_USER, OPT_DATE, OPT_TERM, &
		     IPD_NO) &
	      VALUES(<CASE_NO>, <ADM_TYPE>, <REGION_CODE>, <MR_NO>, <ADM_DATE>, &
		     <DEPT_CODE>, <DR_CODE>, <OPT_USER>, SYSDATE, <OPT_TERM>, &
		     <IPD_NO>)
insertIpd.Debug=N


//��Ժд�벡��������
updateIpd.Type=TSQL
updateIpd.SQL=UPDATE SYS_EMR_INDEX SET &
		     DR_DATE=<DR_DATE>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
	       WHERE CASE_NO=<CASE_NO>
updateIpd.Debug=N


