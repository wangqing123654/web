######################################################
# <p>Title:��ͨ�����ҽ��ά��Module </p>
#
# <p>Description:��ͨ�����ҽ��ά��Module </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:javahis </p>
#
# @author zhangk
# @version 4.0
#
######################################################
Module.item=selectdate;insertdata;update;deletedata;selectall;updateAll

//��ѯ��䣬���ң����ң�������Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
selectall.Type=TSQL
selectall.SQL=SELECT REGION,ADM_TYPE,SESSION_CODE,CLINIC_AREA,CLINICROOM_NO,DEPT_CODE,DR_CODE,CLINICTYPE_CODE,OPT_USER,OPT_DATE,OPT_TERM &
	      FROM REG_SCHDAY_DR &
	      ORDER BY CLINIC_AREA,CLINICROOM_NO,SESSION_CODE
selectall.item=ADM_TYPE;SESSION_CODE;CLINIC_AREA;CLINICROOM_NO
selectall.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectall.SESSION_CODE=SESSION_CODE=<SESSION_CODE>
selectall.CLINIC_AREA=CLINIC_AREA=<CLINIC_AREA>
selectall.CLINICROOM_NO=CLINICROOM_NO=<CLINICROOM_NO>
selectall.Debug=N

//��ѯ��ͨ��ҽ�����
selectdate.Type=TSQL
selectdate.SQL=SELECT adm_type,session_code,clinic_area,clinicroom_no FROM reg_schday_dr
selectdate.Debug=N

//�޸���ͨ��ҽ�����
update.Type=TSQL
update.SQL=UPDATE REG_SCHDAY_DR SET DEPT_CODE=<DEPT_CODE>,CLINICTYPE_CODE=<CLINICTYPE_CODE>,DR_CODE =<DR_CODE> , OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE ,OPT_TERM=<OPT_TERM> WHERE ADM_TYPE=<ADM_TYPE> AND SESSION_CODE=<SESSION_CODE>  AND CLINICROOM_NO=<CLINICROOM_NO> AND REGION=<REGION>
update.Debug=N

//������ͨ��ҽ�����
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO reg_schday_dr (region, adm_type, session_code, clinic_area, clinicroom_no) VALUES (<REGION>,<ADM_TYPE>,<SESSION_CODE>,<CLINIC_AREA>,<CLINICROOM_NO>)
insertdata.Debug=N

//ɾ����ͨ��ҽ�����
deletedata.Type=TSQL
deletedata.SQL=DELETE REG_SCHDAY_DR WHERE REGION=<REGION> AND ADM_TYPE=<ADM_TYPE> AND SESSION_CODE=<SESSION_CODE> AND CLINIC_AREA=<CLINIC_AREA> AND CLINICROOM_NO=<CLINICROOM_NO>
deletedata.Debug=N

//�޸���ͨ��ҽ�����
updateAll.Type=TSQL
updateAll.SQL=UPDATE REG_SCHDAY_DR SET DEPT_CODE=<DEPT_CODE>,CLINICTYPE_CODE=<CLINICTYPE_CODE>,DR_CODE =<DR_CODE> , OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE ,OPT_TERM=<OPT_TERM> WHERE ADM_TYPE=<ADM_TYPE> AND SESSION_CODE=<SESSION_CODE>  AND CLINICROOM_NO=<CLINICROOM_NO> AND REGION=<REGION> AND CLINIC_AREA=<CLINIC_AREA>
updateAll.Debug=N



