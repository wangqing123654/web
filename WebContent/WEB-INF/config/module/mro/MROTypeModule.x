########################################
#  Title:������Ŀ����module
# 
#  Description:������Ŀ����module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.4.29
#  version 4.0
########################################
Module.item=selectdata;insertdata;updatedata;deletedata

//��ѯ����
selectdata.Type=TSQL
selectdata.SQL=SELECT  TYPE_CODE, TYPE_DESC, PY1, &
		PY2, DESCRIPTION, OPT_USER, &
		OPT_DATE, OPT_TERM &
		FROM MRO_TYPE &
		WHERE TYPE_CODE LIKE <TYPE_CODE> &
		AND TYPE_DESC LIKE <TYPE_DESC>
selectdata.Debug=N

//��������
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO MRO_TYPE (TYPE_CODE, TYPE_DESC, PY1, &
                      PY2, DESCRIPTION, OPT_USER, &
                      OPT_DATE, OPT_TERM &
                     ) &
              VALUES (<TYPE_CODE>, <TYPE_DESC>, <PY1>, & 
                      <PY2>, <DESCRIPTION>, <OPT_USER>, &
                      SYSDATE, <OPT_TERM> &
                     )
insertdata.Debug=N

//��������
updatedata.Type=TSQL
updatedata.SQL=UPDATE MRO_TYPE SET TYPE_DESC=<TYPE_DESC>,&
				   PY1=<PY1>,&
				   PY2=<PY2>,&
				   DESCRIPTION=<DESCRIPTION>,&
				   OPT_USER=<OPT_USER>,&
				   OPT_DATE=SYSDATE,&
				   OPT_TERM=<OPT_TERM> &
			   WHERE   TYPE_CODE = <TYPE_CODE>
updatedata.Debug=N

//ɾ������
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM MRO_TYPE WHERE TYPE_CODE=<TYPE_CODE>
deletedata.Debug=N