# 
#  Title:ҽ�ƿ�module
# 
#  Description:ҽ�ƿ�module
# 
#  Copyright: Copyright (c) bluecore 2012
# 
#  author pangb 2012.10.08
#  version 4.0
#
Module.item=selectEktTrade;selectSumOrderUnBillFlg;deleteEKTMaster;insertEktTrade;insertEKTMaster;selectOpdRxNo;&
            updateOpdEktTrade;selectEktTradeUnSum;updateOpdOrderBusinessNo;selectBusinessByReceiptNo

//��ѯ�˲������շѵ�δ��Ʊ���������ݻ��ܽ��
selectEktTrade.Type=TSQL
selectEktTrade.SQL=SELECT TRADE_NO,CARD_NO,CARD_NO, MR_NO, &
			  CASE_NO, PAT_NAME, &
                          OLD_AMT, AMT, STATE,& 
                          BUSINESS_TYPE,GREEN_BALANCE,GREEN_BUSINESS_AMT,CANCEL_FLG, OPT_USER, OPT_DATE, &
                          OPT_TERM FROM EKT_TRADE &
                    WHERE CASE_NO=<CASE_NO> AND STATE='1' &
                    	  AND BUSINESS_TYPE IN (<EKT_TRADE_TYPE>)              
selectEktTrade.Debug=N

//��ѯ�˾��ﲡ���������ݻ��ܽ��
selectSumOrderUnBillFlg.Type=TSQL
selectSumOrderUnBillFlg.SQL=SELECT SUM(AR_AMT) TOT_AMT FROM OPD_ORDER &
                    WHERE CASE_NO=<CASE_NO> AND (RELEASE_FLG IS NULL OR RELEASE_FLG ='N')
selectSumOrderUnBillFlg.item=BILL_FLG;PRINT_FLG
selectSumOrderUnBillFlg.BILL_FLG=BILL_FLG=<BILL_FLG> 
selectSumOrderUnBillFlg.PRINT_FLG=PRINT_FLG=<PRINT_FLG>   
selectSumOrderUnBillFlg.Debug=N

//ɾ��ҽ�ƿ����
deleteEKTMaster.Type=TSQL
deleteEKTMaster.SQL=DELETE FROM  EKT_MASTER WHERE CARD_NO=<CARD_NO> 
deleteEKTMaster.Debug=N

//���ҽ�ƿ����
insertEKTMaster.Type=TSQL
insertEKTMaster.SQL=INSERT INTO EKT_MASTER(CARD_NO, ID_NO, MR_NO, NAME, CURRENT_BALANCE, CREAT_USER, OPT_USER, OPT_DATE, OPT_TERM) &
		    VALUES(<CARD_NO>, <ID_NO>, <MR_NO>, <NAME>, <CURRENT_BALANCE>, <CREAT_USER>, <OPT_USER>, SYSDATE, <OPT_TERM>)
insertEKTMaster.Debug=N

//��ӿۿ�������
insertEktTrade.Type=TSQL
insertEktTrade.SQL=INSERT INTO EKT_TRADE(TRADE_NO, CARD_NO, MR_NO,CASE_NO, PAT_NAME,OLD_AMT, AMT, STATE,&
	           BUSINESS_TYPE,GREEN_BALANCE,GREEN_BUSINESS_AMT,OPT_USER, OPT_DATE,OPT_TERM) &
	           VALUES(<TRADE_NO>, <CARD_NO>, <MR_NO>,<CASE_NO>,<PAT_NAME>,<OLD_AMT>, <AMT>, <STATE>,&
	           <BUSINESS_TYPE>,<GREEN_BALANCE>,<GREEN_BUSINESS_AMT>,<OPT_USER>, SYSDATE,<OPT_TERM>)
insertEktTrade.Debug=N

//ҽ��վ����ɾ�����ݲ�ѯ ͨ������ǩ��ѯ����Ҫ������ҽ��
selectOpdRxNo.Type=TSQL
selectOpdRxNo.SQL=SELECT BUSINESS_NO,SUM(AR_AMT) AS AR_AMT FROM  OPD_ORDER WHERE CASE_NO=<CASE_NO> AND RELEASE_FLG='N' AND RX_NO IN(<RX_NO>) GROUP BY BUSINESS_NO
selectOpdRxNo.item=BILL_FLG
selectOpdRxNo.BILL_FLG=BILL_FLG=<BILL_FLG>
selectOpdRxNo.Debug=N

//���������޸�״̬
updateOpdEktTrade.Type=TSQL
updateOpdEktTrade.SQL=UPDATE EKT_TRADE SET STATE='3',OPT_DATE=SYSDATE,OPT_USER=<OPT_USER>,OPT_TERM=<OPT_TERM>  WHERE TRADE_NO=<TRADE_NO> AND STATE='1'
updateOpdEktTrade.Debug=N

//��ѯ�˴β����շѵ�������Ҫ�˻����
selectEktTradeUnSum.Type=TSQL
selectEktTradeUnSum.SQL=SELECT SUM(AMT) AS AMT,SUM(GREEN_BUSINESS_AMT) AS GREEN_BUSINESS_AMT FROM  EKT_TRADE WHERE TRADE_NO IN(<TRADE_NO>)
selectEktTradeUnSum.Debug=N

//ҽ���˷Ѳ��� ���޸�OPD_ORDER �����ڲ����׺���
updateOpdOrderBusinessNo.Type=TSQL
updateOpdOrderBusinessNo.SQL=UPDATE OPD_ORDER SET BUSINESS_NO=<BUSINESS_NO> WHERE CASE_NO=<CASE_NO> AND RECEIPT_NO=<RECEIPT_NO>
updateOpdOrderBusinessNo.Debug=N

//ҽ����Ʊ���� �۳�ҽ�ƿ�����ѯ�˲������վݺ������еĲ�ͬ���ڲ����׺�
selectBusinessByReceiptNo.Type=TSQL
selectBusinessByReceiptNo.SQL=SELECT BUSINESS_NO FROM OPD_ORDER &
                    WHERE CASE_NO=<CASE_NO> AND RECEIPT_NO=<RECEIPT_NO> GROUP BY  BUSINESS_NO   
selectBusinessByReceiptNo.Debug=N