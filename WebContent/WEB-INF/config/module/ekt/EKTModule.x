# 
#  Title:ҽ�ƿ�module
# 
#  Description:ҽ�ƿ�module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.06.15
#  version 1.0
#
Module.item=insertEKTDetail;insertEKTMaster;updateEKTDetail;updateEKTMaster;confirmEKTDetail;getEKTCurrentBalance;&
            getEKTBusinessAmt;getDetail;detailCancel;getMrNo;getCardNo;getRxAmt;deleteEKTMaster;getRxBalance;getRxOriginal;&
            insetTrade;updateTrade;queryAccntdetail;updateAccntdetail;queryTrade;queryTradeNoCancel;sumAccntdetailCaseNo;&
            sumOpbdetailCaseNo;insertEKTBilPay;updateEKTBilPay;selectEKTBilPay;insertEKTIssuelog;updateEKTPWD;selectEKTIssuelog;&
            updateEKTIssuelog;selectPWDEKTIssuelog;selPatEktGreen;deleteTrade;updateEKTAndBank;updateTradePrint;selectTradeNo;deleteDetail;&
	    selectEKTTradeTotal;selectEKTTradeDetail;deleteOpbTrade;selectEktaccntDetailByChargeFlag;selectEktaccntDetailSum;updateEktAccntDetailStatus;&
	    insertEKTAccount;accountQuery;queryGroupAccntdetail;queryOpdOrderError

getDetail.Type=TSQL
getDetail.SQL=SELECT BUSINESS_NO,BUSINESS_SEQ,ORDER_CODE,RX_NO,SEQ_NO,BUSINESS_AMT FROM EKT_ACCNTDETAIL &
               WHERE CASE_NO=<CASE_NO> AND CHARGE_FLG='1' AND ORDER_CODE <>'REG' ORDER BY SEQ_NO
getDetail.item=RX_NO;SEQ_NO
getDetail.RX_NO=RX_NO=<RX_NO>
getDetail.SEQ_NO=SEQ_NO=<SEQ_NO>
getDetail.Debug=N

//ҽ�ƿ���ϸ��ҽ������
detailCancel.Type=TSQL
detailCancel.SQL=UPDATE EKT_ACCNTDETAIL SET CHARGE_FLG='6' WHERE CASE_NO=<CASE_NO> AND RX_NO=<RX_NO>  AND CHARGE_FLG='1'

deleteDetail.Type=TSQL
deleteDetail.SQL=DELETE FROM  EKT_ACCNTDETAIL WHERE BUSINESS_NO=<BUSINESS_NO> WHERE CHARGE_FLG='2'
deleteDetail.Debug=N

queryAccntdetail.Type=TSQL
queryAccntdetail.SQL=SELECT BUSINESS_AMT,BUSINESS_SEQ,CHARGE_FLG FROM EKT_ACCNTDETAIL WHERE CASE_NO=<CASE_NO> AND CHARGE_FLG = '1' 

sumAccntdetailCaseNo.Type=TSQL
sumAccntdetailCaseNo.SQL=SELECT SUM(BUSINESS_AMT)AS BUSINESS_AMT  FROM EKT_ACCNTDETAIL WHERE CASE_NO=<CASE_NO> AND RX_NO <> 'REG' AND  CHARGE_FLG = '1'

sumOpbdetailCaseNo.Type=TSQL
sumOpbdetailCaseNo.SQL=SELECT SUM(AR_AMT)AS AR_AMT FROM OPD_ORDER WHERE CASE_NO=<CASE_NO> AND BILL_FLG='Y' AND BILL_TYPE='E'

queryTrade.Type=TSQL
queryTrade.SQL=SELECT SUM(B.AMT+B.GREEN_BUSINESS_AMT) AS AMT FROM EKT_TRADE B WHERE B.TRADE_NO &
               IN(SELECT BUSINESS_NO FROM OPD_ORDER WHERE BILL_FLG='Y' AND CASE_NO=<CASE_NO> GROUP BY BUSINESS_NO) AND B.STATE='1'
queryTrade.Debug=N

queryTradeNoCancel.Type=TSQL
queryTradeNoCancel.SQL=SELECT TRADE_NO FROM EKT_TRADE WHERE CARD_NO=<CARD_NO> AND STATE='0'

updateAccntdetail.Type=TSQL
updateAccntdetail.SQL=UPDATE EKT_ACCNTDETAIL SET CHARGE_FLG=<CHARGE_FLG> WHERE BUSINESS_NO=<BUSINESS_NO> AND BUSINESS_SEQ=<BUSINESS_SEQ>

//д��ҽ�ƿ���ϸ��
insertEKTDetail.Type=TSQL
insertEKTDetail.SQL=INSERT INTO EKT_ACCNTDETAIL(BUSINESS_NO,BUSINESS_SEQ,CARD_NO,MR_NO,CASE_NO,ORDER_CODE,RX_NO,&
                                                SEQ_NO,CHARGE_FLG,ORIGINAL_BALANCE,BUSINESS_AMT,CURRENT_BALANCE,&
                                                CASHIER_CODE,BUSINESS_DATE,BUSINESS_STATUS,ACCNT_STATUS,ACCNT_USER,&
                                                ACCNT_DATE,OPT_USER,OPT_DATE,OPT_TERM,GREEN_BALANCE,GREEN_BUSINESS_AMT,BUSINESS_TYPE)&
                          VALUES(<BUSINESS_NO>,<BUSINESS_SEQ>,<CARD_NO>,<MR_NO>,<CASE_NO>,<ORDER_CODE>,<RX_NO>,&
                                 <SEQ_NO>,<CHARGE_FLG>,<ORIGINAL_BALANCE>,<BUSINESS_AMT>,<CURRENT_BALANCE>,&
                                 <CASHIER_CODE>,<BUSINESS_DATE>,<BUSINESS_STATUS>,<ACCNT_STATUS>,<ACCNT_USER>,&
                                 <ACCNT_DATE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<GREEN_BALANCE>,<GREEN_BUSINESS_AMT>,<BUSINESS_TYPE>)
insertEKTDetail.Debug=N

//д��ҽ������
insertEKTMaster.Type=TSQL
insertEKTMaster.SQL=INSERT INTO EKT_MASTER(CARD_NO,ID_NO,MR_NO,NAME,CURRENT_BALANCE,CREAT_USER,&
                                           OPT_USER,OPT_DATE,OPT_TERM)&
                          VALUES(<CARD_NO>,<ID_NO>,<MR_NO>,<NAME>,<CURRENT_BALANCE>,<CREAT_USER>,&
                                 <OPT_USER>,SYSDATE,<OPT_TERM>)
insertEKTMaster.Debug=N


deleteEKTMaster.Type=TSQL
deleteEKTMaster.SQL= DELETE EKT_MASTER WHERE CARD_NO=<CARD_NO>

//����ҽ�ƿ���ϸ����˱��
updateEKTDetail.Type=TSQL
updateEKTDetail.SQL=UPDATE EKT_ACCNTDETAIL SET ACCNT_STATUS = <ACCNT_STATUS>, &
                                               ACCNT_USER = <ACCNT_USER>, &
                                               ACCNT_DATE = <ACCNT_DATE>, &
                                               OPT_USER = <OPT_USER>, &
                                               OPT_DATE = <OPT_DATE>, &
                                               OPT_TERM = <OPT_TERM>
                                         WHERE REGION_CODE = <REGION_CODE> &
                                         AND   MR_NO = <MR_NO> &
                                         AND   CASE_NO = <CASE_NO> &
                                         AND   BUSINESS_NO = <BUSINESS_NO> 
updateEKTDetail.Debug=N

//����ҽ�ƿ��������
updateEKTMaster.Type=TSQL
updateEKTMaster.SQL=UPDATE EKT_MASTER SET CURRENT_BALANCE = <CURRENT_BALANCE>, &
                                          OPT_USER = <OPT_USER>, &
                                          OPT_DATE = <OPT_DATE>, &
                                          OPT_TERM = <OPT_TERM> &
                                    WHERE CARD_NO = <CARD_NO>
updateEKTMaster.Debug=N

//����ҽ�ƿ�����״̬���
confirmEKTDetail.Type=TSQL
confirmEKTDetail.SQL=UPDATE EKT_ACCNTDETAIL SET BUSINESS_STATUS = <BUSINESS_STATUS>, &
                                                OPT_USER = <OPT_USER>, &
                                                OPT_DATE = <OPT_DATE>, &
                                                OPT_TERM = <OPT_TERM>
                                         WHERE REGION_CODE = <REGION_CODE> &
                                         AND   MR_NO = <MR_NO> &
                                         AND   CASE_NO = <CASE_NO> &
                                         AND   BUSINESS_NO = <BUSINESS_NO> 
confirmEKTDetail.Debug=N

//ȡ��ҽ�ƿ����
getEKTCurrentBalance.Type=TSQL
getEKTCurrentBalance.SQL=SELECT CURRENT_BALANCE &
                         FROM   EKT_MASTER &
                         WHERE  CARD_NO = <CARD_NO>
getEKTCurrentBalance.Debug=N



//ȡ�ù涨ʱ�����ҽ�ƿ����׽��
getEKTBusinessAmt.Type=TSQL
getEKTBusinessAmt.SQL=SELECT SUM(BUSINESS_AMT)AS BUSINESS_AMT &
                        FROM EKT_ACCNTDETAIL &
                       WHERE BUSINESS_DATE BETWEEN <START_DATE> AND <END_DATE>
getEKTBusinessAmt.Debug=N

getMrNo.Type=TSQL
getMrNo.SQL=SELECT MR_NO FROM REG_PATADM WHERE CASE_NO=<CASE_NO>

getCardNo.Type=TSQL
getCardNo.SQL=SELECT CARD_NO FROM EKT_MASTER WHERE CASE_NO=<CASE_NO>

getRxAmt.Type=TSQL
getRxAmt.SQL=SELECT SUM(BUSINESS_AMT) AS AMT FROM EKT_ACCNTDETAIL WHERE CASE_NO=<CASE_NO> AND RX_NO=<RX_NO> AND CHARGE_FLG='1'

getRxBalance.Type=TSQL
getRxBalance.SQL=SELECT CURRENT_BALANCE FROM EKT_ACCNTDETAIL WHERE CASE_NO=<CASE_NO> AND RX_NO=<RX_NO> &
			ORDER BY BUSINESS_NO DESC,BUSINESS_SEQ DESC
getRxBalance.Debug=N

getRxOriginal.Type=TSQL
getRxOriginal.SQL=SELECT ORIGINAL_BALANCE FROM EKT_ACCNTDETAIL WHERE CASE_NO=<CASE_NO> AND RX_NO=<RX_NO> &
			ORDER BY BUSINESS_NO DESC,BUSINESS_SEQ ASC
getRxOriginal.Debug=N

insetTrade.Type=TSQL
insetTrade.SQL=INSERT INTO EKT_TRADE(TRADE_NO,CARD_NO,MR_NO,CASE_NO,BUSINESS_NO,PAT_NAME,OLD_AMT,AMT,STATE,BUSINESS_TYPE,&
                                           OPT_USER,OPT_DATE,OPT_TERM,BANK_FLG)&
                          VALUES(<TRADE_NO>,<CARD_NO>,<MR_NO>,<CASE_NO>,<BUSINESS_NO>,<PAT_NAME>,<OLD_AMT>,<AMT>,<STATE>,<BUSINESS_TYPE>,&
                                 <OPT_USER>,SYSDATE,<OPT_TERM>,<BANK_FLG>)
insetTrade.Debug=N

updateTrade.Type=TSQL
updateTrade.SQL=UPDATE EKT_TRADE SET STATE = <STATE> &
                                    WHERE TRADE_NO = <TRADE_NO>
updateTrade.Debug=N

//���ҽ�ƿ���ֵ�˿===pangben 20110930  add by sunqy 20140715ҽ�ƿ���ֵ��֧����ʽΪˢ�����뱸ע�����п���

insertEKTBilPay.Type=TSQL
insertEKTBilPay.SQL=INSERT INTO EKT_BIL_PAY(CARD_NO,CURT_CARDSEQ,ACCNT_TYPE,MR_NO,ID_NO,NAME,AMT,CREAT_USER,OPT_USER, &
		     OPT_DATE,OPT_TERM,BIL_BUSINESS_NO,GATHER_TYPE,STORE_DATE,PROCEDURE_AMT,DESCRIPTION,CARD_TYPE,PRINT_NO) VALUES(<CARD_NO>,<CURT_CARDSEQ>,<ACCNT_TYPE>,<MR_NO>,<ID_NO>,<NAME>,<AMT>,<CREAT_USER>,&
		     <OPT_USER>,SYSDATE,<OPT_TERM>,<BIL_BUSINESS_NO>,<GATHER_TYPE>,<STORE_DATE>,<PROCEDURE_AMT>,<DESCRIPTION>,<CARD_TYPE>,<PRINT_NO>) 
insertEKTBilPay.Debug=N

//ҽ�ƿ�������¼��===pangben 20110930

insertEKTIssuelog.Type=TSQL
insertEKTIssuelog.SQL=INSERT INTO EKT_ISSUELOG(CARD_NO,MR_NO,CARD_SEQ,ISSUE_DATE,ISSUERSN_CODE,FACTORAGE_FEE,PASSWORD,WRITE_FLG,OPT_USER, &
		     OPT_DATE,OPT_TERM,EKT_CARD_NO) VALUES(<CARD_NO>,<MR_NO>,<CARD_SEQ>,<ISSUE_DATE>,<ISSUERSN_CODE>,<FACTORAGE_FEE>,<PASSWORD>,<WRITE_FLG>,&
		     <OPT_USER>,SYSDATE,<OPT_TERM>,<EKT_CARD_NO>) 
insertEKTIssuelog.Debug=N

//ҽ�ƿ�������¼���޸�����

updateEKTPWD.Type=TSQL
updateEKTPWD.SQL=UPDATE EKT_ISSUELOG SET PASSWORD=<PASSWORD> WHERE CARD_NO=<CARD_NO>
updateEKTPWD.Debug=N

//��ȡҽ�ƿ���Ϣ��ѯ����û�����ϵĿ�Ƭ��Ϣ

selectEKTIssuelog.Type=TSQL
selectEKTIssuelog.SQL=SELECT A.CARD_NO,A.MR_NO,A.CARD_SEQ,A.ISSUE_DATE,A.ISSUERSN_CODE,A.FACTORAGE_FEE,A.PASSWORD,A.WRITE_FLG,B.CURRENT_BALANCE,A.EKT_CARD_NO FROM EKT_ISSUELOG A,EKT_MASTER B WHERE A.CARD_NO=B.CARD_NO AND A.MR_NO=<MR_NO> AND A.WRITE_FLG='Y'
selectEKTIssuelog.Debug=N

//ҽ�ƿ�����д������
updateEKTIssuelog.Type=TSQL
updateEKTIssuelog.SQL=UPDATE EKT_ISSUELOG SET WRITE_FLG='N',OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE MR_NO=<MR_NO> AND WRITE_FLG='Y'
updateEKTIssuelog.Debug=N

//����ȷ��
selectPWDEKTIssuelog.Type=TSQL
selectPWDEKTIssuelog.SQL=SELECT A.CARD_NO,A.MR_NO,A.CARD_SEQ,A.ISSUE_DATE,A.ISSUERSN_CODE,A.FACTORAGE_FEE,A.PASSWORD,A.WRITE_FLG,B.CURRENT_BALANCE FROM EKT_ISSUELOG A,EKT_MASTER B WHERE A.CARD_NO=B.CARD_NO AND A.CARD_NO=<CARD_NO> AND A.WRITE_FLG='Y' 
selectPWDEKTIssuelog.Debug=N

deleteTrade.Type=TSQL
deleteTrade.SQL=DELETE FROM  EKT_TRADE  WHERE TRADE_NO = <TRADE_NO>
deleteTrade.Debug=N

//���п���ҽ�ƿ�����
updateEKTAndBank.Type=TSQL
updateEKTAndBank.SQL=UPDATE EKT_ISSUELOG SET BANK_CARD_NO=<BANK_CARD_NO> WHERE CARD_NO=<CARD_NO>
updateEKTAndBank.Debug=N

//ҽ�ƿ��շ�������Ʊ���
updateTradePrint.Type=TSQL
updateTradePrint.SQL=UPDATE EKT_TRADE SET STATE = <STATE>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                                    WHERE CASE_NO=<CASE_NO> AND BUSINESS_TYPE IN ('OPB','ODO')
updateTradePrint.Debug=N

//����CASE_NO ��� TRADE_NO 

selectTradeNo.Type=TSQL
selectTradeNo.SQL=SELECT TRADE_NO FROM EKT_TRADE WHERE CASE_NO=<CASE_NO> AND BUSINESS_TYPE=<BUSINESS_TYPE>
selectTradeNo.item=STATE
selectTradeNo.STATE=STATE=<STATE>
selectTradeNo.Debug=N


//ҽ�ƿ����׼�¼�ܶ�
selectEKTTradeTotal.Type=TSQL
selectEKTTradeTotal.SQL=SELECT BUSINESS_NO,CARD_NO,MR_NO,CASE_NO,PAT_NAME,AMT,BUSINESS_TYPE,STATE FROM EKT_TRADE WHERE STATE ='1' or STATE = '0' AND CARD_NO = <CARD_NO>
selectEKTTradeTotal.Debug=N

//ҽ�ƿ����׼�¼��ϸ��� 
selectEKTTradeDetail.Type=TSQL
selectEKTTradeDetail.SQL=SELECT BUSINESS_NO,CARD_NO,MR_NO,CASE_NO,ORIGINAL_BALANCE,BUSINESS_AMT,CURRENT_BALANCE FROM EKT_ACCNTDETAIL WHERE & 
				BUSINESS_NO = <BUSINESS_NO> 
selectEKTTradeDetail.Debug=N

//����ҽ��վ��EKT_TRADE ���е�������������״̬ͨ�������ɾ��
deleteOpbTrade.Type=TSQL
deleteOpbTrade.SQL=DELETE FROM  EKT_TRADE  WHERE CASE_NO = <CASE_NO>
deleteOpbTrade.Debug=N

//ҽ�ƿ��ս�ץ���ݣ���CHARGE_FLG��
selectEktaccntDetailByChargeFlag.Type=TSQL
selectEktaccntDetailByChargeFlag.SQL=SELECT SUM(BUSINESS_AMT) AS SUM , COUNT(1) AS COUNT FROM EKT_ACCNTDETAIL WHERE ACCNT_STATUS = '1' AND OPT_USER = <OPT_USER> AND CHARGE_FLG = <CHARGE_FLG> AND BUSINESS_DATE <TO_DATE(<BUSINESS_DATE>,'yyyyMMddHH24miss')
selectEktaccntDetailByChargeFlag.Debug=N

//ҽ�ƿ��ս�ץ���ݣ��ֽ����ܽ��ͱ�����
selectEktaccntDetailSum.Type=TSQL
selectEktaccntDetailSum.SQL=SELECT SUM(A.BUSINESS_AMT) AS SUM,COUNT(1) AS COUNT FROM EKT_ACCNTDETAIL A,EKT_BIL_PAY B WHERE A.ACCNT_STATUS = '1' AND A.CHARGE_FLG = <CHARGE_FLG> AND A.OPT_USER = <OPT_USER> AND A.BUSINESS_DATE <TO_DATE(<BUSINESS_DATE>,'yyyyMMddHH24miss') AND B.BIL_BUSINESS_NO = A.BUSINESS_NO
selectEktaccntDetailSum.Debug=N

//ҽ�ƿ��ս��޸�EKT_ACCNTDETAIL��ACCNT_STATUS
updateEktAccntDetailStatus.Type=TSQL
updateEktAccntDetailStatus.SQL=UPDATE EKT_ACCNTDETAIL SET ACCNT_STATUS = '2',ACCOUNT_SEQ = <ACCOUNT_SEQ> WHERE ACCNT_STATUS = '1' AND BUSINESS_DATE <TO_DATE(<BUSINESS_DATE>,'yyyyMMddHH24miss') AND CASHIER_CODE = <CASHIER_CODE>
updateEktAccntDetailStatus.Debug=N

//ҽ�ƿ��ս�����ս��
insertEKTAccount.Type=TSQL
insertEKTAccount.SQL=INSERT INTO EKT_ACCOUNT (ACCOUNT_DATE,ACCOUNT_SEQ,ACCOUNT_TYPE,ACCOUNT_USER,ADD_QTY,AR_AMT,BUY_QTY,CHANGE_QTY,&
			FACTORAGE_AMT,FACTORAGE_QTY,NPAY_MEDICAL_AMT,NPAY_MEDICAL_QTY,OPT_DATE,OPT_TERM,&
			OPT_USER,PAY_MEDICAL_ATM,PAY_MEDICAL_QTY,REGION_CODE,SENT_COUNT,STATUS)&
			VALUES (<ACCOUNT_DATE>,<ACCOUNT_SEQ>,<ACCOUNT_TYPE>,<ACCOUNT_USER>,<ADD_QTY>,<AR_AMT>,<BUY_QTY>,<CHANGE_QTY>,&
			<FACTORAGE_AMT>,<FACTORAGE_QTY>,<NPAY_MEDICAL_AMT>,<NPAY_MEDICAL_QTY>,<OPT_DATE>,<OPT_TERM>,&
			<OPT_USER>,<PAY_MEDICAL_ATM>,<PAY_MEDICAL_QTY>,<REGION_CODE>,<SENT_COUNT>,<STATUS>)
insertEKTAccount.Debug=N

//��ѯ�ս�����
accountQuery.Type=TSQL
accountQuery.SQL=SELECT 'N' AS FLG,B.REGION_CHN_ABN, A.ACCOUNT_SEQ,TO_CHAR (A.ACCOUNT_DATE, 'YYYY/MM/DD HH24:MI:SS')  AS ACCOUNT_DATE, A.ACCOUNT_USER, A.AR_AMT &
		     FROM EKT_ACCOUNT A ,SYS_REGION B &
		    WHERE ACCOUNT_DATE BETWEEN TO_DATE (<S_TIME>,'YYYYMMDDHH24MISS') &
		      AND TO_DATE (<E_TIME>,'YYYYMMDDHH24MISS') AND A.REGION_CODE=B.REGION_CODE &
		      ORDER BY B.REGION_CHN_DESC,A.ACCOUNT_SEQ
accountQuery.item=REGION_CODE;ACCOUNT_USER
accountQuery.REGION_CODE=A.REGION_CODE=<REGION_CODE>
accountQuery.ACCOUNT_USER=ACCOUNT_USER = <ACCOUNT_USER>
accountQuery.Debug=N


//��ѯ�˴β������Ľ��׺�,ҽ�ƿ���Ʊʱ��ô˴�������������׺���
//��ѯ����Ʊ�ݣ�ҽ�ƿ��˷�ʱ�޸�EKT_ACCNTDETAIL����CHARGE_FLG�ֶ�ʹ��
queryGroupAccntdetail.Type=TSQL
queryGroupAccntdetail.SQL=SELECT RX_NO,ORDER_CODE FROM  EKT_ACCNTDETAIL WHERE CHARGE_FLG='6'&
			  AND CASE_NO=<CASE_NO> AND BUSINESS_NO IN (SELECT MAX(BUSINESS_NO) AS BUSINESS_NO FROM EKT_ACCNTDETAIL &
			  WHERE CHARGE_FLG='6' AND CASE_NO=<CASE_NO> AND ORDER_CODE NOT LIKE 'REG%') GROUP BY RX_NO,PRINT_NO,ORDER_CODE
queryGroupAccntdetail.Debug=N

//��ѯOPD_ORDER ���շ��Ժ� BUSINESS_NO Ϊ�յ����� ====pangben 2013-4-27
queryOpdOrderError.Type=TSQL
queryOpdOrderError.SQL=SELECT ORDER_CODE,RX_NO FROM  OPD_ORDER WHERE CASE_NO=<CASE_NO> AND BILL_FLG='Y' AND (BUSINESS_NO IS NULL OR BUSINESS_NO='') AND BILL_TYPE='E'
queryOpdOrderError.Debug=N
