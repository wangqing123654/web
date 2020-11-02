###############################################
#  Title:��������ԭ���ֵ�module
# 
#  Description:��������ԭ���ֵ�module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.5.6
#  version 4.0
###############################################
Module.item=selectdata;insertdata;updatedata;deletedata

//��ѯ����
selectdata.Type=TSQL
selectdata.SQL=SELECT &
		   LEND_CODE, LEND_TYPE, LEND_DESC, PY1, &
		   PY2, DESCRIPTION, LEND_DAY, &
		   VALID_DAY, PRIORITY, OPT_USER, OPT_DATE, &
		   OPT_TERM &
		FROM MRO_LEND &
		ORDER BY LEND_CODE
selectdata.item=LEND_CODE;LEND_DESC;LEND_TYPE
selectdata.LEND_CODE=LEND_CODE=<LEND_CODE>
selectdata.LEND_DESC=LEND_DESC LIKE <LEND_DESC>
selectdata.LEND_TYPE=LEND_TYPE=<LEND_TYPE>
selectdata.Debug=N

//��������
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO MRO_LEND ( &
                    LEND_CODE,LEND_TYPE,LEND_DESC,PY1,PY2,DESCRIPTION, &
                    LEND_DAY,VALID_DAY,PRIORITY,OPT_USER,OPT_DATE,OPT_TERM &
             ) &
             VALUES ( &
                    <LEND_CODE>,<LEND_TYPE>,<LEND_DESC>,<PY1>,<PY2>,<DESCRIPTION>, &
                    <LEND_DAY>,<VALID_DAY>,<PRIORITY>,<OPT_USER>,SYSDATE,<OPT_TERM> &
             )
insertdata.Debug=N

//��������
updatedata.Type=TSQL
updatedata.SQL=UPDATE MRO_LEND SET LEND_DESC=<LEND_DESC>, &
				   LEND_TYPE=<LEND_TYPE>, &
				   PY1=<PY1>, &
				   PY2=<PY2>, &
				   DESCRIPTION=<DESCRIPTION>, &
				   LEND_DAY=<LEND_DAY>, &
				   VALID_DAY=<VALID_DAY>, &
				   PRIORITY=<PRIORITY>, &
				   OPT_USER=<OPT_USER>, &
				   OPT_DATE=SYSDATE, &
				   OPT_TERM=<OPT_TERM> &
			   WHERE   LEND_CODE = <LEND_CODE>
updatedata.Debug=N

//ɾ������
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM MRO_LEND WHERE LEND_CODE=<LEND_CODE>
deletedata.Debug=N