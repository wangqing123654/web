# 
#  Title: RFID�豸�趨module
# 
#  Description: RFID�豸�趨module
# 
#  Copyright: Copyright (c) BlueCore 2012
# 
#  author wanglong 2012.12.03
#  version 1.0
#
Module.item=selectRFIDDevice;insertRFIDDevice;updateRFIDDevice;deleteRFIDDevice;selectRFIDLogs;insertRFIDLog;deleteRFIDLog

//============================= ����ΪRFID�豸�Ǽ�ʹ�� ==============================================
//��ѯ�����¼�
selectRFIDDevice.Type=TSQL
selectRFIDDevice.SQL=SELECT RFID_CODE, IP_ADDRESS, RFID_MODEL, SN, RFID_STATUS, IN_ANTENNA, OUT_ANTENNA,&
                            DEPT_CODE, STATION_CODE, RFID_POSE, RFID_DESC, OPT_USER, OPT_DATE, OPT_TERM &
                       FROM DEV_RFID_BASE
selectRFIDDevice.item=RFID_CODE;IP_ADDRESS
selectRFIDDevice.RFID_CODE=RFID_CODE = <RFID_CODE>
selectRFIDDevice.IP_ADDRESS=IP_ADDRESS = <IP_ADDRESS>
selectRFIDDevice.Debug=N

//���������¼�
insertRFIDDevice.Type=TSQL
insertRFIDDevice.SQL=INSERT INTO DEV_RFID_BASE (RFID_CODE, IP_ADDRESS, RFID_MODEL, SN, RFID_STATUS, IN_ANTENNA, OUT_ANTENNA,&
                                                DEPT_CODE, STATION_CODE, RFID_POSE, RFID_DESC, OPT_USER, OPT_DATE, OPT_TERM) &
                          VALUES (<RFID_CODE>, <IP_ADDRESS>, <RFID_MODEL>, <SN>, <RFID_STATUS>, <IN_ANTENNA>, <OUT_ANTENNA>,&
                                  <DEPT_CODE>, <STATION_CODE>, <RFID_POSE>, <RFID_DESC>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertRFIDDevice.Debug=N

//���²����¼���Ϣ
updateRFIDDevice.Type=TSQL
updateRFIDDevice.SQL=UPDATE DEV_RFID_BASE &
		        SET IP_ADDRESS = <IP_ADDRESS>, RFID_MODEL = <RFID_MODEL>, SN = <SN>, RFID_STATUS = <RFID_STATUS>,&
			    IN_ANTENNA = <IN_ANTENNA>, OUT_ANTENNA = <OUT_ANTENNA>, DEPT_CODE = <DEPT_CODE>,&
			    STATION_CODE = <STATION_CODE>, RFID_POSE = <RFID_POSE>, RFID_DESC = <RFID_DESC>,&
		            OPT_USER = <OPT_USER>, OPT_DATE = <OPT_DATE>, OPT_TERM = <OPT_TERM> &
		      WHERE RFID_CODE = <RFID_CODE>						
updateRFIDDevice.Debug=N

//ɾ�������¼�
deleteRFIDDevice.Type=TSQL
deleteRFIDDevice.SQL=DELETE FROM DEV_RFID_BASE WHERE RFID_CODE = <RFID_CODE>
deleteRFIDDevice.Debug=N

//============================= ����ΪRFID�豸��־��ѯʹ�� ==============================================
//��ѯRFID�豸��־
selectRFIDLogs.Type=TSQL
selectRFIDLogs.SQL=SELECT '' AS FLG, RFID_LOG_CODE, RECORD_DATE, IP_ADDRESS, RFID_MESSAGE, OPT_USER, OPT_DATE, OPT_TERM &
                     FROM DEV_RFID_LOG &
		    WHERE RECORD_DATE BETWEEN <START_DATE> AND <END_DATE> &
		 ORDER BY RFID_LOG_CODE
selectRFIDLogs.item=IP_ADDRESS
selectRFIDLogs.IP_ADDRESS=IP_ADDRESS = <IP_ADDRESS>
selectRFIDLogs.Debug=N

//RFID�豸������־
insertRFIDLog.Type=TSQL
insertRFIDLog.SQL=INSERT INTO DEV_RFID_LOG (RFID_LOG_CODE, RECORD_DATE, IP_ADDRESS, RFID_MESSAGE, OPT_USER, OPT_DATE, OPT_TERM) &
                       VALUES (<RFID_LOG_CODE>, <RECORD_DATE>, <IP_ADDRESS>, <RFID_MESSAGE>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertRFIDLog.Debug=N

//ɾ��RFID�豸��־
deleteRFIDLog.Type=TSQL
deleteRFIDLog.SQL=DELETE FROM DEV_RFID_LOG WHERE RFID_LOG_CODE = <RFID_LOG_CODE>
deleteRFIDLog.Debug=N

