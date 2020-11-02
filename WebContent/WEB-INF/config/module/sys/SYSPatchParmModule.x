# 
#  Title:���β�������
# 
#  Description:���β�������
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangy 2009.07.22
#
#  version 1.0
#
Module.item=insert;update;delete;queryPatchCode

//�������β�������
insert.Type=TSQL
insert.SQL=INSERT INTO SYS_PATCH_PARM( &
		PATCH_CODE, PATCH_PARM_NAME, PATCH_PARM_VALUE, OPT_USER, OPT_DATE, OPT_TERM ) &
           VALUES( &
           	<PATCH_CODE>, <PATCH_PARM_NAME>, <PATCH_PARM_VALUE>, <OPT_USER>, <OPT_DATE>, <OPT_TERM> )
insert.Debug=N

//�������β�������
update.Type=TSQL
update.SQL=UPDATE SYS_PATCH_PARM SET &
		PATCH_PARM_VALUE = <PATCH_PARM_VALUE> , &
		OPT_USER = <OPT_USER> , &
		OPT_DATE = <OPT_DATE> , &
		OPT_TERM = <OPT_TERM> &
	    WHERE PATCH_CODE = <PATCH_CODE> &
	      AND PATCH_PARM_NAME = <PATCH_PARM_NAME>
update.Debug=N

//ɾ�����β�������
delete.Type=TSQL
delete.SQL=DELETE FROM SYS_PATCH_PARM WHERE PATCH_CODE = <PATCH_CODE> AND PATCH_PARM_NAME = <PATCH_PARM_NAME>
delete.Debug=N

//��ѯ���γ����
queryPatchCode.Type=TSQL
queryPatchCode.SQL=SELECT PATCH_CODE FROM SYS_PATCH_PARM WHERE PATCH_PARM_NAME = <PATCH_PARM_NAME> AND PATCH_PARM_VALUE = <PATCH_PARM_VALUE>
queryPatchCode.Debug=N