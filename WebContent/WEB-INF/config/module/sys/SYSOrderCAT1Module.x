Module.item=selectall;selectdata;deletedata;insertdata;updatedata;existsPosition;initOrderCat1Code

//��ѯҽ��ϸ������룬ҽ��ϸ�������˵����ƴ��1��ƴ��2,ҽ��ϸ����Ⱥ�� ��������,��ע��������Ա����������
selectdata.Type=TSQL
selectdata.SQL=SELECT SEQ, ORDER_CAT1_CODE,ORDER_CAT1_DESC,ENNAME,PY1,PY2,CAT1_TYPE ,DEAL_SYSTEM ,DEAL_SYSTEM2 ,DEAL_SYSTEM3 ,DESCRIPTION,TREAT_FLG,OPT_USER ,OPT_DATE  FROM SYS_ORDER_CAT1  WHERE ORDER_CAT1_CODE = <ORDER_CAT1_CODE> ORDER BY SEQ
selectdata.Debug=N


//ɾ��ҽ��ϸ������룬ҽ��ϸ�������˵����ҽ��ϸ����Ⱥ�� �������� ,������Ա���������ڣ� ������ĩ
deletedata.Type=TSQL
deletedata.SQL=DELETE SYS_ORDER_CAT1  WHERE ORDER_CAT1_CODE = <ORDER_CAT1_CODE> 
deletedata.Debug=N

//����ҽ��ϸ������룬ҽ��ϸ�������˵����ƴ��1��ƴ��2,ҽ��ϸ����Ⱥ�� ��������,��ע��������Ա���������ڣ� ������ĩ
updatedata.Type=TSQL
updatedata.SQL=UPDATE SYS_ORDER_CAT1 SET SEQ=<SEQ>,ORDER_CAT1_DESC=<ORDER_CAT1_DESC>,ENNAME=<ENNAME>,PY1=<PY1>,PY2=<PY2>,CAT1_TYPE=<CAT1_TYPE> ,DEAL_SYSTEM=<DEAL_SYSTEM> ,DEAL_SYSTEM2=<DEAL_SYSTEM2> ,DEAL_SYSTEM3=<DEAL_SYSTEM3> ,DESCRIPTION=<DESCRIPTION>,TREAT_FLG=<TREAT_FLG>,OPT_USER =<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
 WHERE ORDER_CAT1_CODE = <ORDER_CAT1_CODE> 
updatedata.Debug=N

//����ҽ��ϸ������룬ҽ��ϸ�������˵����ƴ��1��ƴ��2,ҽ��ϸ����Ⱥ�� ��������,��ע��������Ա���������ڣ� ������ĩ 
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO SYS_ORDER_CAT1(ORDER_CAT1_CODE,ORDER_CAT1_DESC,ENNAME,CAT1_TYPE,DEAL_SYSTEM,DEAL_SYSTEM2,DEAL_SYSTEM3,SEQ,DESCRIPTION,TREAT_FLG,PY1,PY2,OPT_USER,OPT_DATE,OPT_TERM)  VALUES( <ORDER_CAT1_CODE> ,<ORDER_CAT1_DESC> ,<ENNAME>,<CAT1_TYPE>,<DEAL_SYSTEM>,<DEAL_SYSTEM2>,<DEAL_SYSTEM3>,<SEQ>,<DESCRIPTION>,<TREAT_FLG>,<PY1>,<PY2>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertdata.Debug=N

//��ѯҽ��ϸ������룬ҽ��ϸ�������˵����ƴ��1��ƴ��2,ҽ��ϸ����Ⱥ�� ��������,��ע��������Ա����������
selectall.Type=TSQL
selectall.SQL=SELECT SEQ,ORDER_CAT1_CODE,ORDER_CAT1_DESC,ENNAME,PY1,PY2,CAT1_TYPE,DEAL_SYSTEM,DEAL_SYSTEM2,DEAL_SYSTEM3,TREAT_FLG,DESCRIPTION,OPT_USER,OPT_DATE FROM SYS_ORDER_CAT1  ORDER BY SEQ 
selectall.Debug=N

//�Ƿ����ҽ��ϸ�������
existsPosition.type=TSQL
existsPosition.SQL=SELECT COUNT(ORDER_CAT1_CODE) AS COUNT FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE=<ORDER_CAT1_CODE>

//����combo
initOrderCat1Code.Type=TSQL
initOrderCat1Code.SQL=SELECT ORDER_CAT1_CODE AS ID,ORDER_CAT1_DESC AS NAME,ENNAME,PY1,PY2 FROM SYS_ORDER_CAT1  ORDER BY SEQ,ORDER_CAT1_CODE
initOrderCat1Code.item=CAT1_TYPE
initOrderCat1Code.CAT1_TYPE=CAT1_TYPE=<CAT1_TYPE>
initOrderCat1Code.Debug=N
