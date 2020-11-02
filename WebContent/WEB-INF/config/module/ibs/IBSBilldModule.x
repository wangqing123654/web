# 
#  Title:סԺ����(��ϸ��)module
# 
#  Description:סԺ����(��ϸ��)module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.27
#  version 1.0
#
Module.item=selectAllData;selDataForCharge;insertDdata;deleteDdata;updataData

//��ѯ��������
selectAllData.Type=TSQL
selectAllData.SQL=SELECT BILL_NO,BILL_SEQ,REXP_CODE,OWN_AMT,&
			 AR_AMT,PAY_AR_AMT,OPT_USER,OPT_DATE,OPT_TERM, &
			 REFUND_BILL_NO,REFUND_FLG,REFUND_CODE,REFUND_DATE &
                       FROM IBS_BILLD
selectAllData.Item=BILL_NO;BILL_SEQ;REXP_CODE
selectAllData.BILL_NO=BILL_NO=<BILL_NO>
selectAllData.BILL_SEQ=BILL_SEQ=<BILL_SEQ>
selectAllData.REXP_CODE=REXP_CODE=<REXP_CODE>
selectAllData.Debug=N

//��ѯ�˵�����(�ɷ���ҵ)
selDataForCharge.Type=TSQL
selDataForCharge.SQL=SELECT D.BILL_NO, D.BILL_SEQ, D.REXP_CODE, D.OWN_AMT, D.AR_AMT,&
			    D.PAY_AR_AMT,D.OPT_USER, D.OPT_DATE, D.OPT_TERM &
                       FROM IBS_BILLM M, IBS_BILLD D &
                      WHERE M.BILL_NO = D.BILL_NO &
                        AND M.BILL_SEQ = D.BILL_SEQ 
                         //AND (D.AR_AMT - D.PAY_AR_AMT) != 0  =====pangben 2015-11-11 �޸��˵�Ϊ0Ҳ���Բ�ѯ
selDataForCharge.Item=CASE_NO;BILL_NO;BILL_SEQ;REXP_CODE
selDataForCharge.CASE_NO=M.CASE_NO=<CASE_NO>
selDataForCharge.BILL_NO=M.BILL_NO=<BILL_NO>
selDataForCharge.BILL_SEQ=M.BILL_SEQ=<BILL_SEQ>
selDataForCharge.REXP_CODE=M.REXP_CODE=<REXP_CODE>
selDataForCharge.Debug=N


//������ϸ��
insertDdata.Type=TSQL
insertDdata.SQL=INSERT INTO IBS_BILLD &
			    (BILL_NO,BILL_SEQ,REXP_CODE,OWN_AMT,&
			    AR_AMT,PAY_AR_AMT,OPT_USER,OPT_DATE,OPT_TERM,&
			    REFUND_BILL_NO,REFUND_FLG,REFUND_CODE,REFUND_DATE ) &
		    VALUES  (<BILL_NO>,<BILL_SEQ>,<REXP_CODE>,<OWN_AMT>,&
			    <AR_AMT>,<PAY_AR_AMT>,<OPT_USER>,SYSDATE,<OPT_TERM>,&
			    <REFUND_BILL_NO>,<REFUND_FLG>,<REFUND_CODE>,<REFUND_DATE>)
insertDdata.Debug=N

//ɾ����ϸ��
deleteDdata.Type=TSQL
deleteDdata.SQL=DELETE FROM IBS_BILLD WHERE BILL_NO = <BILL_NO> AND BILL_SEQ = <BILL_SEQ> AND REXP_CODE = <REXP_CODE>
deleteDdata.Debug=N

//��������
updataData.Type=TSQL
updataData.SQL=UPDATE IBS_BILLD &
		  SET REFUND_FLG = <REFUND_FLG>,REFUND_BILL_NO=<REFUND_BILL_NO>,REFUND_CODE=<REFUND_CODE>,REFUND_DATE = SYSDATE &
		WHERE BILL_NO IN (<BILL_NO>)

updataData.Debug=N
