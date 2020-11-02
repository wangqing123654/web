# 
#  Title:智能柜主档module
# 
#  Description:智能柜主档module
# 
#  Copyright: Copyright (c) Javahis 2013
# 
#  author wangzl 2012.01.14
#  version 1.0
#
Module.item=insertInfo;updateInfo;deleteInfo;queryInfo;queryLog;queryCode;queryChild;deleteChild



queryInfo.Type=TSQL
queryInfo.SQL=SELECT REGION_CODE,CABINET_ID,CABINET_DESC,CABINET_IP,OPT_USER,OPT_DATE,OPT_TERM,DESCRIPTION,ORG_CODE,RFID_IP,GUARD_IP FROM INV_CABINET A
queryInfo.item=CABINET_ID;CABINET_DESC
queryInfo.CABINET_ID=A.CABINET_ID=<CABINET_ID>
queryInfo.CABINET_DESC=A.CABINET_DESC=<CABINET_DESC>		
queryInfo.Debug=N

insertInfo.Type=TSQL
insertInfo.SQL=INSERT INTO INV_CABINET &
			   (CABINET_ID,CABINET_DESC,CABINET_IP,DESCRIPTION,ORG_CODE,RFID_IP,GUARD_IP,REGION_CODE,OPT_USER,OPT_DATE,OPT_TERM) &
		    VALUES (<CABINET_ID>,<CABINET_DESC>,<CABINET_IP>,<DESCRIPTION>,<ORG_CODE>,<RFID_IP>,<GUARD_IP>,<REGION_CODE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertInfo.Debug=N
  


//保存挂号信息
updateInfo.Type=TSQL
updateInfo.SQL=UPDATE INV_CABINET &
		  SET CABINET_DESC=<CABINET_DESC>,CABINET_IP=<CABINET_IP>,DESCRIPTION=<DESCRIPTION>,ORG_CODE=<ORG_CODE>,RFID_IP=<RFID_IP>,GUARD_IP=<GUARD_IP>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		WHERE CABINET_ID=<CABINET_ID>
updateInfo.Debug=N



deleteInfo.Type=TSQL	
deleteInfo.SQL=DELETE FROM INV_CABINET WHERE CABINET_ID=<CABINET_ID>


deleteInfo.Debug=N


queryLog.Type=TSQL

queryLog.SQL= SELECT CABINET_ID,(SELECT A.CABINET_DESC FROM INV_CABINET A WHERE A.CABINET_ID=CABINET_ID ) AS CABINET_DESC, &
    	LOG_TIME, (CASE WHEN TASK_TYPE=1 THEN '普药入库' WHEN TASK_TYPE=2 THEN '一般普药出库' WHEN TASK_TYPE=3 THEN '紧急普药出库' &
	WHEN TASK_TYPE=4 THEN '麻精入库' WHEN TASK_TYPE=5 THEN '一般麻精出库' WHEN TASK_TYPE=6 THEN '紧急麻精出库' WHEN TASK_TYPE=7 THEN '其他麻精入库' &
	WHEN TASK_TYPE=8 THEN '其他麻精出库' &
	ELSE '' END) AS TASK_TYPE,TASK_NO,(CASE WHEN EVENT_TYPE=1 THEN '开门' WHEN EVENT_TYPE=2 THEN '关门' ELSE '' END) AS EVENT_TYPE, &
	GUARD_ID,OPT_USER,OPT_DATE,OPT_TERM &
	 FROM INV_CABINET_LOG ORDER BY LOG_TIME DESC

queryLog.item=TASK_TYPE;EVENT_TYPE
queryLog.TASK_TYPE=TASK_TYPE=<TASK_TYPE>
queryLog.EVENT_TYPE=EVENT_TYPE=<EVENT_TYPE>			

queryLog.Debug=N



queryCode.Type=TSQL
queryCode.SQL=SELECT GUARD_ID FROM INV_CABINET_GUARD WHERE CABINET_ID=<CABINET_ID> AND GUARD_ID=<GUARD_ID>
		
queryCode.Debug=Y


queryChild.Type=TSQL
queryChild.SQL=SELECT GUARD_ID FROM INV_CABINET_GUARD WHERE CABINET_ID=<CABINET_ID>
		
queryChild.Debug=Y


deleteChild.Type=TSQL
deleteChild.SQL=DELETE FROM INV_CABINET_GUARD WHERE GUARD_ID=<GUARD_ID>
		
deleteChild.Debug=Y





