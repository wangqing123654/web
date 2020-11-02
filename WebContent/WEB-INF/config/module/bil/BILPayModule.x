# 
#  Title:Ԥ����module
# 
#  Description:Ԥ����module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.27
#  version 1.0
#
Module.item=selectAllData;selAllDataByRecpNo;updataData;insertData;selectPatCaseNo;seldataByCaseNo;seldataByIpdNo;selDataForCharge;selSumTotal;&
	    selLeftPay;updataOffBilPay;upRecpForRePrint;updateBilPayIpdNo

//��ѯԤ��������(�������,סԺ��)
selectAllData.Type=TSQL
selectAllData.SQL=SELECT A.RECEIPT_NO,A.CASE_NO,A.IPD_NO,A.MR_NO,A.TRANSACT_TYPE,&
			 A.REFUND_FLG,A.RESET_BIL_PAY_NO,A.RESET_RECP_NO,A.CASHIER_CODE,A.CHARGE_DATE,&
			 A.ADM_TYPE,A.PRE_AMT,A.PAY_TYPE,A.CHECK_NO,A.REMARK,&
			 A.REFUND_CODE,A.REFUND_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.PRINT_NO,B.CHN_DESC AS PAY_TYPE_NAME,A.CARD_TYPE &
		    FROM BIL_PAY A,SYS_DICTIONARY B WHERE A.PAY_TYPE=B.ID AND B.GROUP_ID ='GATHER_TYPE'
selectAllData.Item=CASE_NO;IPD_NO;RECEIPT_NO;RESET_RECP_NO
selectAllData.CASE_NO=CASE_NO=<CASE_NO>
selectAllData.RECEIPT_NO=RECEIPT_NO=<RECEIPT_NO>
selectAllData.RESET_RECP_NO=RESET_RECP_NO=<RESET_RECP_NO>
selectAllData.IPD_NO=IPD_NO=<IPD_NO>
selectAllData.Debug=N

//��ѯԤ��������(Ԥ�����վݺ�)
selAllDataByRecpNo.Type=TSQL
selAllDataByRecpNo.SQL=SELECT RECEIPT_NO,CASE_NO,IPD_NO,MR_NO,TRANSACT_TYPE,&
			      REFUND_FLG,RESET_BIL_PAY_NO,RESET_RECP_NO,CASHIER_CODE,CHARGE_DATE,&
			      ADM_TYPE,PRE_AMT,PAY_TYPE,CHECK_NO,REMARK,&
			      REFUND_CODE,REFUND_DATE,OPT_USER,OPT_DATE,OPT_TERM,PRINT_NO,BUSINESS_NO &
			 FROM BIL_PAY &
		      	WHERE REFUND_FLG = 'N' &
		          AND TRANSACT_TYPE IN ('01','04') &
		          AND RESET_RECP_NO IS NULL
selAllDataByRecpNo.Item=RECEIPT_NO;CASE_NO
selAllDataByRecpNo.RECEIPT_NO=RECEIPT_NO=<RECEIPT_NO>
selAllDataByRecpNo.CASE_NO=CASE_NO=<CASE_NO>
selAllDataByRecpNo.Debug=N

//��������(��Ԥ����)
updataData.Type=TSQL
updataData.SQL=UPDATE BIL_PAY &
		  SET REFUND_FLG = 'Y',REFUND_CODE = <REFUND_CODE> ,REFUND_DATE=<REFUND_DATE>,&
		      RESET_BIL_PAY_NO=<RESET_BIL_PAY_NO>��OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		WHERE RECEIPT_NO = <RECEIPT_NO>
updataData.Debug=N

//��������(��,��,����,�س�Ԥ����)
insertData.Type=TSQL
insertData.SQL=INSERT INTO BIL_PAY (RECEIPT_NO,CASE_NO,IPD_NO,MR_NO,TRANSACT_TYPE,&
				   REFUND_FLG,CASHIER_CODE,CHARGE_DATE,ADM_TYPE,PRE_AMT,&
				   PAY_TYPE,CHECK_NO,REMARK,OPT_USER,OPT_DATE,&
				   OPT_TERM,PRINT_NO,RESET_RECP_NO,CARD_TYPE,BUSINESS_NO) &
			    VALUES (<RECEIPT_NO>,<CASE_NO>,<IPD_NO>,<MR_NO>,<TRANSACT_TYPE>,&
			    	   <REFUND_FLG>,<CASHIER_CODE>,<CHARGE_DATE>,<ADM_TYPE>,<PRE_AMT>,&
			    	   <PAY_TYPE>,<CHECK_NO>,<REMARK>,<OPT_USER>,SYSDATE,&
			    	   <OPT_TERM>,<PRINT_NO>,<RESET_RECP_NO>,<CARD_TYPE>,<BUSINESS_NO>)
insertData.Debug=N

//��ѯ�����������
selectPatCaseNo.Type=TSQL
selectPatCaseNo.SQL=SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE MR_NO=<MR_NO>
selectPatCaseNo.Debug=N

//(���ݾ������)��ѯ����������Ϣ(��ԺҲ���˷�)
seldataByCaseNo.Type=TSQL
seldataByCaseNo.SQL=SELECT A.ADM_SOURCE,A.BED_NO,A.MR_NO,B.PAT_NAME,B.BIRTH_DATE,&
			   A.IN_DATE,A.CLNCPATH_CODE,A.DEPT_CODE,A.STATION_CODE,A.CASE_NO,&
			   A.CTZ1_CODE,B.SEX_CODE,A.HEIGHT,A.WEIGHT,A.YELLOW_SIGN,&
			   A.RED_SIGN,A.GREENPATH_VALUE,A.VS_DR_CODE,A.TOTAL_BILPAY,A.CUR_AMT,A.TOTAL_AMT, &
			   A.DS_DATE,A.IPD_NO &
		      FROM ADM_INP A,SYS_PATINFO B &
                     WHERE A.MR_NO = B.MR_NO
seldataByCaseNo.Item=CASE_NO
seldataByCaseNo.CASE_NO=A.CASE_NO=<CASE_NO>
seldataByCaseNo.Debug=N

//(����סԺ��)��ѯ����������Ϣ(��ԺҲ���˷�)
seldataByIpdNo.Type=TSQL
seldataByIpdNo.SQL=SELECT A.ADM_SOURCE,A.BED_NO,A.MR_NO,B.PAT_NAME,B.BIRTH_DATE,&
			  A.IN_DATE,A.CLNCPATH_CODE,A.DEPT_CODE,A.STATION_CODE,A.CASE_NO,&
			  A.CTZ1_CODE,B.SEX_CODE,A.HEIGHT,A.WEIGHT,A.YELLOW_SIGN,&
			  A.RED_SIGN,A.GREENPATH_VALUE,A.VS_DR_CODE,A.TOTAL_BILPAY,A.CUR_AMT,&
			  A.DS_DATE,A.IPD_NO &
                       FROM ADM_INP A,SYS_PATINFO B &
                      WHERE A.MR_NO = B.MR_NO
seldataByIpdNo.Item=IPD_NO
seldataByIpdNo.IPD_NO=A.IPD_NO=<IPD_NO>
seldataByIpdNo.Debug=N

//��ѯԤ��������(�ɷ���ҵ)
selDataForCharge.Type=TSQL
selDataForCharge.SQL=SELECT A.RECEIPT_NO,A.CASE_NO,A.IPD_NO,A.MR_NO,A.TRANSACT_TYPE, &
			    A.REFUND_FLG,A.RESET_BIL_PAY_NO,A.RESET_RECP_NO,A.CASHIER_CODE,A.CHARGE_DATE, &
			    A.ADM_TYPE,A.PRE_AMT,A.PAY_TYPE,A.CHECK_NO,A.REMARK,A.BUSINESS_NO, &
			    A.REFUND_CODE,A.REFUND_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PAT_NAME &
		       FROM BIL_PAY A, SYS_PATINFO B &
		      WHERE A.MR_NO=B.MR_NO &
		        AND A.REFUND_FLG = 'N' &
		        AND A.TRANSACT_TYPE IN ('01','04') &
		        AND A.RESET_RECP_NO IS NULL
selDataForCharge.Item=CASE_NO;IPD_NO
selDataForCharge.CASE_NO=A.CASE_NO=<CASE_NO>
selDataForCharge.IPD_NO=A.IPD_NO=<IPD_NO>
selDataForCharge.Debug=N

//��ѯ�������
selSumTotal.Type=TSQL
selSumTotal.SQL=
selSumTotal.Debug=N

//��ѯԤ�������
selLeftPay.Type=TSQL
selLeftPay.SQL=SELECT SUM(PRE_AMT) FROM BIL_PAY WHERE  ADM_TYPE='I' AND CASE_NO=<CASE_NO> AND REFUND_FLG='N'
selLeftPay.Debug=N

//��������(����(����)Ԥ����)
updataOffBilPay.Type=TSQL
updataOffBilPay.SQL=UPDATE BIL_PAY &
		       SET RESET_RECP_NO = <IBS_RECEIPT_NO>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		     WHERE RECEIPT_NO = <RECEIPT_NO>
updataOffBilPay.Debug=N

//����Ԥ����ӡ�վ�
upRecpForRePrint.Type=TSQL
upRecpForRePrint.SQL=UPDATE BIL_PAY &
			SET PRINT_NO=<PRINT_NO>,&
			    OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		      WHERE RECEIPT_NO = <RECEIPT_NO>
upRecpForRePrint.Debug=N

//����Ԥ�����IPD_NO
//�����ײ�ԤԼ��ֵ����ʹ�ã�����������Ժʱͬʱ����BIL_PAY��
//pangben 2014-7-31
updateBilPayIpdNo.Type=TSQL
updateBilPayIpdNo.SQL=UPDATE BIL_PAY &
			SET IPD_NO=<IPD_NO>,&
			    OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		      WHERE CASE_NO = <CASE_NO>
updateBilPayIpdNo.Debug=N


