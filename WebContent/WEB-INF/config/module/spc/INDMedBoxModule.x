  #
   # Title:����ҩ�ж���ά��
   #
   # Description:����ҩ�ж���ά��
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author shendr 2013-05-21
   
Module.item=queryInfo;insertInfo;updateInfo;deleteInfo

#��ѯ
queryInfo.Type=TSQL
queryInfo.SQL=SELECT ELETAG_CODE,AP_REGION,OPT_USER,OPT_DATE,OPT_TERM FROM IND_MEDBOX
queryInfo.item=ELETAG_CODE
queryInfo.ELETAG_CODE=ELETAG_CODE=<ELETAG_CODE>
queryInfo.Debug=N

#����
insertInfo.Type=TSQL
insertInfo.SQL=INSERT INTO IND_MEDBOX &
		   (ELETAG_CODE,AP_REGION,OPT_USER,OPT_DATE,OPT_TERM) VALUES (<ELETAG_CODE>,<AP_REGION>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertInfo.Debug=N

#����
updateInfo.Type=TSQL
updateInfo.SQL=UPDATE IND_MEDBOX SET AP_REGION=<AP_REGION> WHERE ELETAG_CODE=<ELETAG_CODE>
updateInfo.Debug=N

#ɾ��
deleteInfo.Type=TSQL
deleteInfo.SQL=DELETE FROM IND_MEDBOX WHERE ELETAG_CODE=<ELETAG_CODE>
deleteInfo.Debug=N