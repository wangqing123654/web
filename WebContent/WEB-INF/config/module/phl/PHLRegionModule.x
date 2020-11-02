#
# Title:��������Module
#
# Description:��������Module
#
# Copyright: JavaHis (c) 2009
#
# @author wangl 2009/10/271

Module.item=initCombo;query;insert;update;delete

//����������ѯ��Ӧ���̵Ĺ�ӦҩƷ
initCombo.Type=TSQL
initCombo.SQL=SELECT REGION_CODE AS ID,REGION_DESC AS NAME,ENNAME,PY1,PY2 &
		FROM PHL_REGION &
	    ORDER BY REGION_CODE
initCombo.ITEM=REGION_CODE_ALL
initCombo.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
initCombo.Debug=N

//����������ѯ����������
query.Type=TSQL
query.SQL=SELECT REGION_CODE, REGION_DESC, PY1, PY2, DESCRIPTION, &
                 START_IP, END_IP, OPT_USER, OPT_DATE, OPT_TERM &
	   FROM  PHL_REGION 
query.ITEM=REGION_CODE
query.REGION_CODE=REGION_CODE=<REGION_CODE>
query.Debug=N

//��Ӿ���������
insert.Type=TSQL
insert.SQL = INSERT INTO PHL_REGION( &
		REGION_CODE, REGION_DESC, PY1, PY2, DESCRIPTION, &
	     	START_IP, END_IP, OPT_USER, OPT_DATE, OPT_TERM) &
	     VALUES(<REGION_CODE>, <REGION_DESC>, <PY1>, <PY2>, <DESCRIPTION>,&
	     	<START_IP>, <END_IP>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insert.Debug=N

//���¾���������
update.Type=TSQL
update.SQL = UPDATE PHL_REGION SET &
		REGION_DESC=<REGION_DESC> , PY1=<PY1> , PY2=<PY2> , DESCRIPTION=<DESCRIPTION> , START_IP=<START_IP> , &
		END_IP = <END_IP>, OPT_USER=<OPT_USER> , OPT_DATE=<OPT_DATE> , OPT_TERM=<OPT_TERM> &
	     WHERE REGION_CODE=<REGION_CODE>
update.Debug=N

//ɾ������������
delete.Type=TSQL
delete.SQL=DELETE FROM PHL_REGION WHERE REGION_CODE=<REGION_CODE>
delete.Debug=N

