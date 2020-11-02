# 
#  Title: RFID设备设定module
# 
#  Description: RFID设备设定module
# 
#  Copyright: Copyright (c) BlueCore 2012
# 
#  author wanglong 2012.12.03
#  version 1.0
#
Module.item=selectRFIDDevice;insertRFIDDevice;updateRFIDDevice;deleteRFIDDevice;selectRFIDLogs;insertRFIDLog;deleteRFIDLog

//============================= 以下为RFID设备登记使用 ==============================================
//查询不良事件
selectRFIDDevice.Type=TSQL
selectRFIDDevice.SQL=SELECT RFID_CODE, IP_ADDRESS, RFID_MODEL, SN, RFID_STATUS, IN_ANTENNA, OUT_ANTENNA,&
                            DEPT_CODE, STATION_CODE, RFID_POSE, RFID_DESC, OPT_USER, OPT_DATE, OPT_TERM &
                       FROM DEV_RFID_BASE
selectRFIDDevice.item=RFID_CODE;IP_ADDRESS
selectRFIDDevice.RFID_CODE=RFID_CODE = <RFID_CODE>
selectRFIDDevice.IP_ADDRESS=IP_ADDRESS = <IP_ADDRESS>
selectRFIDDevice.Debug=N

//新增不良事件
insertRFIDDevice.Type=TSQL
insertRFIDDevice.SQL=INSERT INTO DEV_RFID_BASE (RFID_CODE, IP_ADDRESS, RFID_MODEL, SN, RFID_STATUS, IN_ANTENNA, OUT_ANTENNA,&
                                                DEPT_CODE, STATION_CODE, RFID_POSE, RFID_DESC, OPT_USER, OPT_DATE, OPT_TERM) &
                          VALUES (<RFID_CODE>, <IP_ADDRESS>, <RFID_MODEL>, <SN>, <RFID_STATUS>, <IN_ANTENNA>, <OUT_ANTENNA>,&
                                  <DEPT_CODE>, <STATION_CODE>, <RFID_POSE>, <RFID_DESC>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertRFIDDevice.Debug=N

//更新不良事件信息
updateRFIDDevice.Type=TSQL
updateRFIDDevice.SQL=UPDATE DEV_RFID_BASE &
		        SET IP_ADDRESS = <IP_ADDRESS>, RFID_MODEL = <RFID_MODEL>, SN = <SN>, RFID_STATUS = <RFID_STATUS>,&
			    IN_ANTENNA = <IN_ANTENNA>, OUT_ANTENNA = <OUT_ANTENNA>, DEPT_CODE = <DEPT_CODE>,&
			    STATION_CODE = <STATION_CODE>, RFID_POSE = <RFID_POSE>, RFID_DESC = <RFID_DESC>,&
		            OPT_USER = <OPT_USER>, OPT_DATE = <OPT_DATE>, OPT_TERM = <OPT_TERM> &
		      WHERE RFID_CODE = <RFID_CODE>						
updateRFIDDevice.Debug=N

//删除不良事件
deleteRFIDDevice.Type=TSQL
deleteRFIDDevice.SQL=DELETE FROM DEV_RFID_BASE WHERE RFID_CODE = <RFID_CODE>
deleteRFIDDevice.Debug=N

//============================= 以下为RFID设备日志查询使用 ==============================================
//查询RFID设备日志
selectRFIDLogs.Type=TSQL
selectRFIDLogs.SQL=SELECT '' AS FLG, RFID_LOG_CODE, RECORD_DATE, IP_ADDRESS, RFID_MESSAGE, OPT_USER, OPT_DATE, OPT_TERM &
                     FROM DEV_RFID_LOG &
		    WHERE RECORD_DATE BETWEEN <START_DATE> AND <END_DATE> &
		 ORDER BY RFID_LOG_CODE
selectRFIDLogs.item=IP_ADDRESS
selectRFIDLogs.IP_ADDRESS=IP_ADDRESS = <IP_ADDRESS>
selectRFIDLogs.Debug=N

//RFID设备插入日志
insertRFIDLog.Type=TSQL
insertRFIDLog.SQL=INSERT INTO DEV_RFID_LOG (RFID_LOG_CODE, RECORD_DATE, IP_ADDRESS, RFID_MESSAGE, OPT_USER, OPT_DATE, OPT_TERM) &
                       VALUES (<RFID_LOG_CODE>, <RECORD_DATE>, <IP_ADDRESS>, <RFID_MESSAGE>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertRFIDLog.Debug=N

//删除RFID设备日志
deleteRFIDLog.Type=TSQL
deleteRFIDLog.SQL=DELETE FROM DEV_RFID_LOG WHERE RFID_LOG_CODE = <RFID_LOG_CODE>
deleteRFIDLog.Debug=N

