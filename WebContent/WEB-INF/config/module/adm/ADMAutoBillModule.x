##################################################
# <p>Title:�Զ��Ʒ���Ŀ </p>
#
# <p>Description:�Զ��Ʒ���Ŀ </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY
# @version 1.0
##################################################
Module.item=querydata;update;insertdata;existsORDER;deletedata

//��ѯȫ�ֶ�
querydata.Type=TSQL
querydata.SQL=SELECT A.ORDER_CODE,A.DOSEAGE_QTY, B.UNIT_CODE, B.OWN_PRICE AS PRICE,&
           A.DOSEAGE_QTY * B.OWN_PRICE AS SUM_PRICE, A.OCCUFEE_FLG, A.BABY_FLG, A.START_DATE,&
           A.END_DATE, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, A.DEPT_FLG &
  	   FROM ADM_AUTOBILL A, SYS_FEE B &
 	   WHERE A.ORDER_CODE = B.ORDER_CODE &
  	   ORDER BY   ORDER_CODE
querydata.item=ORDER_CODE
querydata.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
querydata.Debug=N

//��������
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO ADM_AUTOBILL ( ORDER_CODE,DOSEAGE_QTY,OCCUFEE_FLG,BABY_FLG,& 
 		START_DATE,END_DATE,OPT_USER,OPT_DATE,OPT_TERM )&
 		VALUES (<ORDER_CODE>,<DOSEAGE_QTY>,<OCCUFEE_FLG>,<BABY_FLG>,&
 		<START_DATE>,<END_DATE>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N

//�޸�����
update.Type=TSQL
update.SQL=UPDATE ADM_AUTOBILL SET DOSEAGE_QTY=<DOSEAGE_QTY>,OCCUFEE_FLG=<OCCUFEE_FLG>,&   
 	BABY_FLG=<BABY_FLG>, START_DATE=<START_DATE>,END_DATE=<END_DATE>,&
   	OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
	WHERE ORDER_CODE = <ORDER_CODE>
update.Debug=N

//ɾ������
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM ADM_AUTOBILL WHERE ORDER_CODE = <ORDER_CODE>
deletedata.Debug=N
 
//�Ƿ����order
existsORDER.type=TSQL
existsORDER.SQL=SELECT COUNT(*) AS COUNT FROM ADM_AUTOBILL WHERE ORDER_CODE = <ORDER_CODE>
existsORDER.Debug=N
 
 
 