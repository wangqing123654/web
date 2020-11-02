###############################################
# <p>Title:短信管理 </p>
#
# <p>Description:短信管理 </p>
#
# <p>Copyright: Copyright (c) 2012</p>
#
# <p>Company:JavaHis </p>
#
# @author robo
# @version 1.0
###############################################

Module.item=selectdata;insertdata;updatedata;selectminute;selectdeanorcompementtel;selectdirectortel;updatedatatime

//查询全字段
selectdata.Type=TSQL
selectdata.SQL=SELECT MR_NO,PATIENT_NAME,TESTITEM_CODE,TEST_VALUE,CRTCLLWLMT,&
		       STATE,BILLING_DOCTORS,NOTIFY_DOCTORS_TIME,DIRECTOR_DR_CODE,NOTIFY_COMPETENT_TIME,&
                       NOTIFY_DIRECTOR_DR_TIME,NOTIFY_DEAN_TIME,HANDLE_USER,HANDLE_TIME,SMS_CODE,&
                       HANDLE_OPINION,ADM_TYPE,SEND_TIME,SMS_CONTENT,STATION_CODE,&
                      CASE_NO,OPT_USER,OPT_DATE,OPT_TERM,DEAN_CODE, &
                      COMPETENT_CODE,TESTITEM_CHN_DESC,REPOTR_TIME &
		FROM MED_SMS
selectdata.item=DEPT_CODE;STATION_CODE;SMS_STATE;MR_NO;BEGIN_TIME;END_TIME
selectdata.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
selectdata.STATION_CODE=STATION_CODE=<STATION_CODE>
selectdata.SMS_STATE=STATE=<SMS_STATE>
selectdata.MR_NO=MR_NO=<MR_NO>
selectdata.BEGIN_TIME=SEND_TIME>=<BEGIN_TIME>
selectdata.END_TIME=SEND_TIME<=<END_TIME>
selectdata.Debug=N

//定时查询表
selectminute.Type=TSQL
selectminute.SQL=SELECT PATIENT_NAME, MR_NO,SMS_CONTENT,CASE_NO, (TO_CHAR(SEND_TIME,'yyyy-MM-dd HH24:MI:SS')) as SEND_TIME,&
    			ADM_TYPE,DEPT_CODE,SMS_CODE &
		 FROM MED_SMS  &
                 WHERE STATE IN ('1','2','3','4') AND (( TO_DATE(<CURRENT_DATE>,'yyyy-MM-dd HH24:MI:SS')-SEND_TIME)*24*60 >= 30)
selectminute.Debug=N


//根据科室与主管类型门诊主任电话号码或医务科主管或者主管院长的电话号码
selectdeanorcompementtel.Type=TSQL
selectdeanorcompementtel.SQL=SELECT s.TEL1,s.USER_ID &
		 FROM MED_SMSDEPT_SETUP mss ,SYS_OPERATOR s   &
                 WHERE mss.PERSON_CODE = s.USER_ID AND  mss.DEPT_CODE=<DEPT_CODE> AND  mss.COMPETENT_TYPE=<COMPETENT_TYPE>
selectdeanorcompementtel.Debug=N

//根据就诊号得到住院主任电话号码
selectdirectortel.Type=TSQL
selectdirectortel.SQL=SELECT s.TEL1,s.USER_ID &
		 FROM　ADM_INP a,SYS_OPERATOR s   &
                 WHERE a.DIRECTOR_DR_CODE =s.USER_ID AND  a.CASE_NO=<CASE_NO>
selectdirectortel.Debug=N


//插入全字段
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO MED_SMS &
		      (SMS_CODE,PATIENT_NAME,CASE_NO,MR_NO,STATION_CODE,&
		        BED_NO,IPD_NO,DEPT_CODE,BILLING_DOCTORS,APPLICATION_NO,&
                       TESTITEM_CODE,TESTITEM_CHN_DESC,TEST_VALUE,CRTCLLWLMT,STATE,&
                       SEND_TIME,SMS_CONTENT,OPT_USER,OPT_DATE,OPT_TERM,&
                       ADM_TYPE,NOTIFY_DOCTORS_TIME,COMPETENT_CODE,DEAN_CODE ,DIRECTOR_DR_CODE,HANDLE_OPINION,&
                       HANDLE_USER,HANDLE_TIME,SEX_CODE,BIRTH_DAY,REPOTR_TIME)&
		VALUES(<SMS_CODE>,<PAT_NAME>,<CASE_NO>,<MR_NO>,<STATION_CODE>,&
		       <BED_NO>,<IPD_NO>,<DEPT_CODE>,<BILLING_DOCTORS>,<APPLICATION_NO>,&
		       <TESTITEM_CODE>,<TESTITEM_CHN_DESC>,<TEST_VALUE>,<CRTCLLWLMT>,<STATE>,&
			SYSDATE,<SMS_CONTENT>,<OPT_USER>,SYSDATE,<OPT_TERM>,&
			<ADM_TYPE>,SYSDATE,<COMPETENT_CODE>,<DEAN_CODE>,<DIRECTOR_DR_CODE>,<HANDLE_OPINION>,<HANDLE_USER>,SYSDATE,&
			<SEX_CODE>,<BIRTH_DAY>,<REPOTR_TIME>)
insertdata.Debug=Y

//根据 SMS_CODE 更新字段
updatedata.Type=TSQL
updatedata.SQL=UPDATE MED_SMS SET &
		 	STATE=<STATE>,HANDLE_SUGGEST_TIME=SYSDATE,&
		 	HANDLE_SUGGEST=<HANDLE_SUGGEST>,&
		 	HANDLE_SUGGEST_USER=<HANDLE_SUGGEST_USER> &
		 WHERE SMS_CODE=<SMS_CODE> 
updatedata.Debug=Y

//根据 SMS_CODE 更新通知通知时间
updatedatatime.Type=TSQL
updatedatatime.SQL=UPDATE MED_SMS SET &
		 	<SMS_TIME>=SYSDATE &
		 WHERE SMS_CODE=<SMS_CODE> 
updatedatatime.Debug=Y