# 
#  Title:����������module
# 
#  Description:����������module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.05.07
#  version 1.0
#
Module.item=selectAllData;updataData;insertData;CheckCounter;finishData
//;selectData;insertData;deleteData

//��ѯ �վ����ͣ���ʼƱ�ţ�����Ʊ�ţ�����ʱ�䣬����״̬��������Ա��������Ա����������
//������Ʊ�����ͣ��������ڣ��������ڣ�ʹ��״̬���������ڲ�����
selectAllData.Type=TSQL
selectAllData.SQL=SELECT CASHIER_CODE,RECP_TYPE,START_INVNO, &
                         OPEN_DATE,END_INVNO,CLS_DATE,OPT_USER, &
                         OPT_DATE,OPT_TERM FROM BIL_COUNTER &
                         WHERE CASHIER_CODE=<CASHIER_CODE> &
                         AND RECP_TYPE=<RECP_TYPE> &
                         AND CLS_DATE IS NULL
selectAllData.Debug=N


//���˲���������<CASHIER_CODE>,<RECP_TYPE>,<OPEN_DATE>,<START_INVNO>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>
insertData.Type=TSQL
insertData.SQL=INSERT INTO BIL_COUNTER (CASHIER_CODE,RECP_TYPE, &
                      OPEN_DATE,START_INVNO,OPT_USER,OPT_DATE, OPT_TERM) &
                      VALUES (<CASHIER_CODE>,<RECP_TYPE>, &
                      <OPEN_DATE>,<START_INVNO>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertData.Debug=N

//���ʸ�������//���������Ա���������ڣ������նˣ�����ʱ�䣬��ֹƱ��
updataData.Type=TSQL
updataData.SQL=UPDATE BIL_COUNTER SET OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, &
                      OPT_TERM=<OPT_TERM>,CLS_DATE=<CLS_DATE>,END_INVNO=<END_INVNO> &
                      WHERE RECP_TYPE=<RECP_TYPE> &
                      AND CASHIER_CODE=<CASHIER_CODE> &
                      AND CLS_DATE IS NULL
updataData.Debug=N


//��˿�����
CheckCounter.Type=TSQL
CheckCounter.SQL=SELECT CASHIER_CODE &
                        FROM BIL_COUNTER &
                        WHERE CASHIER_CODE=<CASHIER_CODE> &
                        AND RECP_TYPE=<RECP_TYPE> &
                        AND CLS_DATE IS NULL   
CheckCounter.Debug=N

//���ʹ���ص�Ʊ��
finishData.Type=TSQL
finishData.SQL=SELECT CASHIER_CODE &
                        FROM BIL_COUNTER &
                        WHERE RECP_TYPE=<RECP_TYPE> &
                        AND OPT_TERM=<OPT_TERM> &
                        AND START_INVNO=<START_INVNO> &
                        AND CLS_DATE IS NULL   
finishData.Debug=N
