# 
#  Title:�Һ�ʱ��module
# 
#  Description:�Һ�ʱ��module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;deletedata;insertdata;updatedata;existsSession;initsessioncode;getdefSession;getSessionCode;getdefSessionNow;getDrSessionNow;getSessionInfoByCode

//��ѯ�ż�ס��,����,ʱ�δ���,ʱ��˵��,ƴ����,ע����,˳����,��ע,������ʱ,������ʱ,�Һ���ʱ,�Һ���ʱ,����ʱ��,����������,������Ա,��������,������ĩ
//=============pangben modify 20110602 ���ҽ��������
selectdata.Type=TSQL
selectdata.SQL=SELECT ADM_TYPE,REGION_CODE,SESSION_CODE,SESSION_DESC,PY1,&
		      PY2,SEQ,DESCRIPTION,START_CLINIC_TIME,END_CLINIC_TIME,&
		      START_REG_TIME,END_REG_TIME,VALUEADD_TIME,VALUEADD_CODE,OPT_USER,&
		      OPT_DATE,OPT_TERM,VALUEADD_DESC &
		 FROM REG_SESSION &
	     ORDER BY SESSION_CODE
selectdata.item=ADM_TYPE;REGION_CODE;SESSION_CODE
selectdata.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectdata.REGION_CODE=REGION_CODE=<REGION_CODE>
selectdata.SESSION_CODE=SESSION_CODE=<SESSION_CODE>
selectdata.Debug=N

//ɾ���ż�ס��,����,ʱ�δ���,ʱ��˵��,ƴ����,ע����,˳����,��ע,������ʱ,������ʱ,�Һ���ʱ,�Һ���ʱ,����ʱ��,����������,������Ա,��������,������ĩ
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_SESSION &
		WHERE ADM_TYPE= <ADM_TYPE> &
		  AND REGION_CODE= <REGION_CODE> &
		  AND SESSION_CODE= <SESSION_CODE>
deletedata.Debug=N

//�����ż�ס��,����,ʱ�δ���,ʱ��˵��,ƴ����,ע����,˳����,��ע,������ʱ,������ʱ,�Һ���ʱ,�Һ���ʱ,����ʱ��,����������,������Ա,��������,������ĩ
//=============pangben modify 20110602 ���ҽ��������
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_SESSION &
			   (ADM_TYPE,REGION_CODE,SESSION_CODE,SESSION_DESC,PY1,&
			   PY2,SEQ,DESCRIPTION,START_CLINIC_TIME,END_CLINIC_TIME,&
			   START_REG_TIME,END_REG_TIME,VALUEADD_TIME,VALUEADD_CODE,OPT_USER,&
			   OPT_DATE,OPT_TERM,VALUEADD_DESC) &
		    VALUES (<ADM_TYPE>,<REGION_CODE>,<SESSION_CODE>,<SESSION_DESC>,<PY1>,&
		    	   <PY2>,<SEQ>,<DESCRIPTION>,<START_CLINIC_TIME>,<END_CLINIC_TIME>,&
		    	   <START_REG_TIME>,<END_REG_TIME>,<VALUEADD_TIME>,<VALUEADD_CODE>,<OPT_USER>,&
		    	   SYSDATE,<OPT_TERM>,<VALUEADD_DESC>)
insertdata.Debug=N              

//�����ż�ס��,����,ʱ�δ���,ʱ��˵��,ƴ����,ע����,˳����,��ע,������ʱ,������ʱ,�Һ���ʱ,�Һ���ʱ,����ʱ��,����������,������Ա,��������,������ĩ
//=============pangben modify 20110602 ���ҽ��������
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_SESSION &
		  SET ADM_TYPE=<ADM_TYPE>,REGION_CODE=<REGION_CODE>,SESSION_CODE=<SESSION_CODE>,&
		      SESSION_DESC=<SESSION_DESC>,PY1=<PY1>,PY2=<PY2>,&
		      SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,START_CLINIC_TIME=<START_CLINIC_TIME>,&
		      END_CLINIC_TIME=<END_CLINIC_TIME>,START_REG_TIME=<START_REG_TIME>,END_REG_TIME=<END_REG_TIME>,&
		      VALUEADD_TIME=<VALUEADD_TIME>,VALUEADD_CODE=<VALUEADD_CODE>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,VALUEADD_DESC=<VALUEADD_DESC> &
		WHERE ADM_TYPE = <ADM_TYPE> &
		  AND SESSION_CODE=<SESSION_CODE>									
updatedata.Debug=N

//�Ƿ���ڹҺ�ʱ��
existsSession.type=TSQL
existsSession.SQL=SELECT COUNT(*) AS COUNT &
		    FROM REG_SESSION &
		   WHERE ADM_TYPE=<ADM_TYPE> &
		     AND REGION_CODE=<REGION_CODE> &
		     AND SESSION_CODE = <SESSION_CODE>

//ȡ��ʱ��combo��Ϣ
initsessioncode.Type=TSQL
initsessioncode.SQL=SELECT SESSION_CODE AS ID,SESSION_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 &
		      FROM REG_SESSION &
		  ORDER BY SEQ
initsessioncode.item=ADM_TYPE;REGION_CODE;REGION_CODE_ALL
initsessioncode.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
initsessioncode.REGION_CODE=REGION_CODE=<REGION_CODE>
initsessioncode.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
initsessioncode.Debug=N

//�����ż�ס��,�Һ���ʼʱ���ѯ�Һ�ʱ��
getdefSessionNow.Type=TSQL
getdefSessionNow.SQL=SELECT SESSION_CODE &
		       FROM REG_SESSION &
		      WHERE ADM_TYPE=<ADM_TYPE> &
			AND (       TO_CHAR (SYSDATE, 'HH24:MI') BETWEEN START_REG_TIME &
								     AND END_REG_TIME &
				AND (START_REG_TIME < END_REG_TIME) &
				OR (    (   TO_CHAR (SYSDATE, 'HH24:MI') BETWEEN '00:00' AND END_REG_TIME &
					OR TO_CHAR (SYSDATE, 'HH24:MI') BETWEEN START_REG_TIME &
									    AND '24:00') &
				AND (START_REG_TIME > END_REG_TIME) ) )
								     
getdefSessionNow.Debug=N

//�����ż�ס��,�Һ���ʼʱ���ѯ�Һ�ʱ��(For OPD)
getDrSessionNow.Type=TSQL
getDrSessionNow.SQL=SELECT SESSION_CODE &
		      FROM REG_SESSION &
		     WHERE ADM_TYPE=<ADM_TYPE> &
		       AND TO_CHAR(SYSDATE,'HH24:MI') BETWEEN START_CLINIC_TIME &
		       AND END_CLINIC_TIME
getDrSessionNow.item=REGION_CODE
getDrSessionNow.REGION_CODE=REGION_CODE=<REGION_CODE>
getDrSessionNow.Debug=N

//�õ�ʱ�α��
getSessionCode.Type=TSQL
getSessionCode.SQL=SELECT SESSION_CODE &
		     FROM REG_SESSION &
		    WHERE ADM_TYPE=<ADM_TYPE>
getSessionCode.item=REGION_CODE
getSessionCode.REGION_CODE=REGION_CODE=<REGION_CODE>
getSessionCode.Debug=N

getSessionInfoByCode.Type=TSQL
getSessionInfoByCode.SQL=SELECT  &
				ADM_TYPE, SESSION_CODE, SESSION_DESC,  &
				PY1, PY2, SEQ,  &
				DESCRIPTION, REGION_CODE, START_CLINIC_TIME,  &
				END_CLINIC_TIME, START_REG_TIME, END_REG_TIME,  &
				VALUEADD_TIME, VALUEADD_CODE, OPT_USER,  &
				OPT_DATE, OPT_TERM, ENG_DESC,  &
				ENG_NAME &
				FROM REG_SESSION &
				WHERE ADM_TYPE=<ADM_TYPE> &
				AND SESSION_CODE=<SESSION_CODE>
getSessionInfoByCode.item=REGION_CODE
getSessionInfoByCode.REGION_CODE=REGION_CODE=<REGION_CODE>
getSessionInfoByCode.Debug=N