##################################################
# <p>Title:���ռ�¼�� </p>
#
# <p>Description:���ռ�¼�� </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: JavaHis</p>
#
# @author JiaoY 2009.05.15
# @version 4.0
##################################################
Module.item=deletedata;checkDAILY_REC;insertDailyRec;selectDailyRec
 
//ɾ�����ռ�¼
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM ADM_DAILY_REC &
 		WHERE POST_DATE = <POST_DATE> &
 		AND CASE_NO = <CASE_NO>
deletedata.Debug=N
 
//���봲�ռ�¼
insertDailyRec.Type=TSQL
insertDailyRec.SQL=INSERT INTO ADM_DAILY_REC ( &
			   POST_DATE, CASE_NO, MR_NO, &
			   IPD_NO, DEPT_CODE, STATION_CODE, &
			   BED_NO, VS_DR_CODE, NURSING_CLASS, &
			   PATIENT_CONDITITION,CARE_NUM, OPT_USER, OPT_DATE, &
			   OPT_TERM) &
			   VALUES ( &
			   <POST_DATE>, <CASE_NO>, <MR_NO>, &
			   <IPD_NO>, <DEPT_CODE>, <STATION_CODE>, &
			   <BED_NO>, <VS_DR_CODE>, <NURSING_CLASS>, &
			   <PATIENT_CONDITITION>,<CARE_NUM>, <OPT_USER>, SYSDATE, &
			   <OPT_TERM>)
insertDailyRec.Debug=N

//��ѯ���ռ�¼�� �Ƿ���ڸò���������
checkDAILY_REC.Type=TSQL
checkDAILY_REC.SQL=SELECT POST_DATE,CASE_NO &
			FROM ADM_DAILY_REC &
			WHERE POST_DATE=<POST_DATE> &
			AND CASE_NO=<CASE_NO>
checkDAILY_REC.Debug=N

//��ѯ���ռ�¼��������
selectDailyRec.Type=TSQL
selectDailyRec.SQL=SELECT &
		   POST_DATE, CASE_NO, MR_NO, &
		   IPD_NO, DEPT_CODE, STATION_CODE, &
		   BED_NO, VS_DR_CODE, NURSING_CLASS, &
		   PATIENT_CONDITITION,CARE_NUM, OPT_USER, OPT_DATE, &
		   OPT_TERM &
		   FROM ADM_DAILY_REC
selectDailyRec.item=POST_DATE;CASE_NO;MR_NO
selectDailyRec.POST_DATE=POST_DATE=<POST_DATE>
selectDailyRec.CASE_NO=CASE_NO=<CASE_NO>
selectDailyRec.MR_NO=MR_NO=<MR_NO>
selectDailyRec.Debug=N