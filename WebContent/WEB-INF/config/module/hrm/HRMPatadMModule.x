# 
#  Title:�������module
# 
#  Description:�������ҽ��module
# 
#  Copyright: Copyright (c) bluecore 2012
# 
#  author pangb 2013.03.10
#  version 4.0
#
Module.item=deleteHrmPatadm;selectHrmPatadm

//ɾ����¼
deleteHrmPatadm.Type=TSQL
deleteHrmPatadm.SQL=DELETE FROM HRM_PATADM WHERE  CONTRACT_CODE=<CONTRACT_CODE> AND MR_NO=<MR_NO>        
deleteHrmPatadm.Debug=N

//��ѯ��¼--��ѯ�������
selectHrmPatadm.Type=TSQL
selectHrmPatadm.SQL=SELECT CASE_NO, MR_NO, FINAL_JUDGE_DR, FINAL_JUDGE_DATE, REPORT_DATE, START_DATE, END_DATE, COMPANY_CODE, &
		           CONTRACT_CODE, PAT_NAME, ID_NO, SEX_CODE, BIRTHDAY, CHARGE_DATE, TEL, POST_CODE, COVER_FLG, ADDRESS, &
		           DEPT_CODE, OPT_USER, OPT_DATE, OPT_TERM, COMPANY_PAY_FLG, PACKAGE_CODE, &
		           REPORT_STATUS, REPORTLIST, INTRO_USER, PAT_DEPT, DISCNT, BILL_FLG FROM HRM_PATADM &
		    WHERE  CONTRACT_CODE=<CONTRACT_CODE> AND MR_NO=<MR_NO>        
selectHrmPatadm.Debug=N
