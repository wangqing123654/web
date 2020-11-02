Module.item=selectdata;deletedata;insertdata;updatedata;existsPosition

//��ѯְ�ƴ��룬ְ��˵����ְ����𣬲�����Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
selectdata.Type=TSQL
selectdata.SQL=SELECT POS_CODE,POS_DESC,POS_TYPE,OPT_USER,OPT_DATE,SEQ,PY1,PY2,REMARK FROM SYS_POSITION WHERE POS_CODE LIKE <POS_CODE> ORDER BY SEQ
selectdata.Debug=N

//ɾ��ְ�ƴ��룬ְ��˵����ְ����𣬲�����Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM SYS_POSITION WHERE POS_CODE = <POS_CODE>
deletedata.Debug=N

//����ְ�ƴ��룬ְ��˵����ְ����𣬲�����Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO SYS_POSITION (POS_CODE,POS_DESC,POS_TYPE,SEQ,PY1,PY2,REMARK) VALUES(<POS_CODE>,<POS_DESC>,<POS_TYPE>,<SEQ>,<PY1>,<PY2>,<REMARK>)
insertdata.Debug=N

//����ְ��˵����ְ����𣬲�����Ա���������ڣ�˳���ţ�ƴ�����룬ע�Ƿ�����ע
updatedata.Type=TSQL
updatedata.SQL=UPDATE SYS_POSITION SET POS_DESC=<POS_DESC>,POS_TYPE=<POS_TYPE>,SEQ=<SEQ>,PY1=<PY1>,PY2=<PY2>,REMARK=<REMARK> WHERE POS_CODE=<POS_CODE>
updatedata.Debug=N

//�Ƿ����ְ��
existsPosition.type=TSQL
existsPosition.SQL=SELECT COUNT(*) AS COUNT FROM SYS_POSITION WHERE POS_CODE=<POS_CODE>



