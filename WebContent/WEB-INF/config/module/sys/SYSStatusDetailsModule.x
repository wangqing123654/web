Module.item=selectalldata;selectdata;updateData;insertData;deleteData;selectOwnRate

//��ѯȫ�ֶ� ��ݴ��룬������ƣ���ݼ�ƣ��Է�ע�ǣ�ҽ��ע�ǣ����²���������������Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
selectalldata.Type=TSQL
selectalldata.SQL=SELECT CHARGE_HOSP_CODE,CTZ_CODE,DISCOUNT_RATE,DESCRITPION,SEQ,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_CHARGE_DETAIL ORDER BY SEQ
selectalldata.Debug=N



//�������� CTZ_CODE ��ѯȫ�ֶ� ��ݴ��룬������ƣ���ݼ�ƣ��Է�ע�ǣ�ҽ��ע�ǣ����²���������������Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
selectdata.Type=TSQL
selectdata.SQL=SELECT CHARGE_HOSP_CODE,CTZ_CODE,DISCOUNT_RATE,DESCRITPION,SEQ,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_CHARGE_DETAIL WHERE CTZ_CODE=<CTZ_CODE>  ORDER BY SEQ
selectdata.item=CHARGE_HOSP_CODE;CTZ_CODE
selectdata.CHARGE_HOSP_CODE=CHARGE_HOSP_CODE=<CHARGE_HOSP_CODE>
selectdata.CTZ_CODE=CTZ_CODE=<CTZ_CODE>
selectdata.Debug=N

//��������
updateData.Type=TSQL
updateData.SQL=UPDATE SYS_CHARGE_DETAIL SET CHARGE_HOSP_CODE=<CHARGE_HOSP_CODE>,CTZ_CODE=<CTZ_CODE>,DISCOUNT_RATE=<DISCOUNT_RATE>,DESCRITPION=<DESCRITPION>,SEQ=<SEQ>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE CHARGE_HOSP_CODE=<CHARGE_HOSP_CODE> AND CTZ_CODE=<CTZ_CODE>
updateData.Debug=N

//��������
insertData.Type=TSQL
insertData.SQL=INSERT INTO SYS_CHARGE_DETAIL VALUES (<CTZ_CODE>,<CHARGE_HOSP_CODE>,<DISCOUNT_RATE>,<DESCRITPION>,<SEQ>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertData.Debug=N



//����CHARGE_HOSP_CODE��CTZ_CODEɾ������
deleteData.Type=TSQL
deleteData.SQL=DELETE SYS_CHARGE_DETAIL 
deleteData.item=CHARGE_HOSP_CODE;CTZ_CODE
deleteData.CHARGE_HOSP_CODE=CHARGE_HOSP_CODE=<CHARGE_HOSP_CODE>
deleteData.CTZ_CODE=CTZ_CODE=<CTZ_CODE>
deleteData.Debug=N

//��������ۿ���
selectOwnRate.Type=TSQL
selectOwnRate.SQL=SELECT DISCOUNT_RATE FROM SYS_CHARGE_DETAIL WHERE CTZ_CODE=<CTZ_CODE> AND CHARGE_HOSP_CODE=<CHARGE_HOSP_CODE>
selectOwnRate..Debug=N




