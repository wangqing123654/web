# 
#  Title:��ͬ��λԤ����module
# 
#  Description:��ͬ��λԤ����module
# 
#  Copyright: Copyright (c) bluecore
# 
#  author caowl 20130114
#  version 1.0
#
Module.item=insertData;selectByReceipt;selectByContractCode;refundData;updOffRecpNo

//����receipt_no��ѯ
selectByReceipt.Type=TSQL
selectByReceipt.SQL=SELECT RECEIPT_NO,CONTRACT_CODE,BUSINESS_TYPE,CASHIER_CODE,CHARGE_DATE, &
		PRE_AMT,REFUND_FLG,REFUND_CODE,REFUND_DATE,OFF_RECP_NO, &
		REMARK,OPT_USER,OPT_DATE,OPT_TERM,PAY_TYPE,PRINT_NO &
	      FROM BIL_CONTRACT_PAY &
	      WHERE RECEIPT_NO = <RECEIPT_NO>
selectByReceipt.Debug=N

//���ݺ�ͬ��λ��ѯ
selectByContractCode.Type=TSQL
selectByContractCode.SQL=SELECT RECEIPT_NO,CONTRACT_CODE,BUSINESS_TYPE,CASHIER_CODE,CHARGE_DATE, &
		PRE_AMT,REFUND_FLG,REFUND_CODE,REFUND_DATE,OFF_RECP_NO, &
		REMARK,OPT_USER,OPT_DATE,OPT_TERM,PAY_TYPE,PRINT_NO &
	      FROM BIL_CONTRACT_PAY &
	      WHERE CONTRACT_CODE = <CONTRACT_CODE>
selectByContractCode.Debug=N

//ɾ����ͬ��λ����
deleteData.Type=TSQL
deleteData.SQL=	
deleteData.Debug=N

//��ѯ��ͬ��λ�����Ƿ����
checkDataExist.Type=TSQL
checkDataExist.SQL=
checkDataExist.Debug=N

//��ͬ��λԤ�����˷�
refundData.Type=TSQL
refundData.SQL=UPDATE BIL_CONTRACT_PAY &
		  SET REFUND_FLG = 'Y',REFUND_CODE = <REFUND_CODE> ,REFUND_DATE=<REFUND_DATE>,&
		      OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
		WHERE RECEIPT_NO = <RECEIPT_NO> 
refundData.Debug=N


//�����ͬ��λ����
insertData.Type=TSQL
insertData.SQL= INSERT INTO BIL_CONTRACT_PAY (RECEIPT_NO,CONTRACT_CODE,BUSINESS_TYPE,CASHIER_CODE,CHARGE_DATE, &
         		PRE_AMT,REFUND_FLG,REFUND_CODE,REFUND_DATE,OFF_RECP_NO, &
        		 REMARK,OPT_USER,OPT_DATE,OPT_TERM,PAY_TYPE,PRINT_NO)  &
	VALUES(<RECEIPT_NO>,<CONTRACT_CODE>,<BUSINESS_TYPE>,<CASHIER_CODE>,<CHARGE_DATE>, &
        		<PRE_PAY>,<REFUND_FLG>,<REFUND_CODE>,<REFUND_DATE>,<OFF_RECP_NO>, &
        		<REMARK>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<PAY_TYPE>,<PRINT_NO>)
insertData.Debug=N

//��ͬԤ�������
updOffRecpNo.Type=TSQL
updOffRecpNo.SQL=UPDATE BIL_CONTRACT_PAY &
		  SET OFF_RECP_NO = <OFF_RECP_NO>, &
		      OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
		WHERE RECEIPT_NO = <RECEIPT_NO> 
updOffRecpNo.Debug=N

