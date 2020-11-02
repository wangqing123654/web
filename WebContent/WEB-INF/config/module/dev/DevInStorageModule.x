# 
#  Title:�豸������module
# 
#  Description:�豸������module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.06.15
#  version 1.0
#
Module.item=selectDevInStorageInf1;selectDevInStorageInf2;selectDevReceipt;selectReceiptD;getBatchNo;getMaxBatchNo;insertDevInwarehouseD;insertDevInwarehouseDD;insertDevInwarehouseM;selectDevInwarehouseD;selectDevInwarehouseDD;updateDevInwarehouseD;updateDevInwarehouseDD;getStock;insertStock;insertStockD;updateStock;updateStockDSumQty;getMaxDevSeqNo

//�����豸������Ϣ1
selectDevInStorageInf1.Type=TSQL
selectDevInStorageInf1.SQL=SELECT A.INWAREHOUSE_NO, A.VERIFY_NO,A.INWAREHOUSE_DATE,A.INWAREHOUSE_USER, &
                                  A.INWAREHOUSE_DEPT, B.RECEIPT_DATE &
	                   FROM DEV_INWAREHOUSEM A, DEV_RECEIPTM B &
	                   WHERE A.VERIFY_NO = B.RECEIPT_NO 
selectDevInStorageInf1.item=INWAREHOUSE_NO;INWARE_START_DATE;INWAREHOUSE_DEPT;INWAREHOUSE_USER;RECEIPT_NO;RECEIPT_START_DATE
//��ⵥ��
selectDevInStorageInf1.INWAREHOUSE_NO=A.INWAREHOUSE_NO LIKE <INWAREHOUSE_NO>||'%'
//�������
selectDevInStorageInf1.INWARE_START_DATE=A.INWAREHOUSE_DATE BETWEEN <INWARE_START_DATE> AND <INWARE_END_DATE>
//������
selectDevInStorageInf1.INWAREHOUSE_DEPT=A.INWAREHOUSE_DEPT = <INWAREHOUSE_DEPT>
//�����Ա
selectDevInStorageInf1.INWAREHOUSE_USER=A.INWAREHOUSE_USER = <INWAREHOUSE_USER>
//���յ���
selectDevInStorageInf1.RECEIPT_NO=A.VERIFY_NO = <RECEIPT_NO>
//��������
selectDevInStorageInf1.RECEIPT_START_DATE=B.RECEIPT_DATE BETWEEN <RECEIPT_START_DATE> AND <RECEIPT_END_DATE>
selectDevInStorageInf1.Debug=N



//�����豸������Ϣ2
selectDevInStorageInf2.Type=TSQL
selectDevInStorageInf2.SQL=SELECT A.INWAREHOUSE_NO,A.VERIFY_NO,A.INWAREHOUSE_DATE,A.INWAREHOUSE_USER, &
                                  A.INWAREHOUSE_DEPT, '' RECEIPT_DATE &
	                   FROM   DEV_INWAREHOUSEM A 
selectDevInStorageInf2.item=INWAREHOUSE_NO;INWARE_START_DATE;INWAREHOUSE_DEPT;INWAREHOUSE_USER;RECEIPT_NO;RECEIPT_START_DATE
//��ⵥ��
selectDevInStorageInf2.INWAREHOUSE_NO=A.INWAREHOUSE_NO LIKE <INWAREHOUSE_NO>||'%'
//�������
selectDevInStorageInf2.INWARE_START_DATE=A.INWAREHOUSE_DATE BETWEEN <INWARE_START_DATE> AND <INWARE_END_DATE>
//������
selectDevInStorageInf2.INWAREHOUSE_DEPT=A.INWAREHOUSE_DEPT = <INWAREHOUSE_DEPT>
//�����Ա
selectDevInStorageInf2.INWAREHOUSE_USER=A.INWAREHOUSE_USER = <INWAREHOUSE_USER>
//���յ���
selectDevInStorageInf2.RECEIPT_NO=A.VERIFY_NO = <RECEIPT_NO>
//��������
selectDevInStorageInf2.RECEIPT_START_DATE=B.RECEIPT_DATE BETWEEN <RECEIPT_START_DATE> AND <RECEIPT_END_DATE>
selectDevInStorageInf2.Debug=N



//�������յ���Ϣ
selectDevReceipt.Type=TSQL
selectDevReceipt.SQL=SELECT A.RECEIPT_NO,A.PURORDER_NO,A.RECEIPT_DATE,A.SUP_CODE,A.REMARK,&
                            A.RECEIPT_DEPT,A.RECEIPT_USER,A.INVOICE_NO,A.INVOICE_DATE,A.INVOICE_AMT,&
                            A.RECEIPT_MINUTE,B.SUP_SALES1,B.SUP_SALES1_TEL &
		      FROM DEV_RECEIPTM A, SYS_SUPPLIER B &
		      WHERE A.SUP_CODE = B.SUP_CODE
selectDevReceipt.item=RECEIPT_NO;INVOICE_NO;RECEIPT_DATE_BEGIN;RECEIPT_DEPT;RECEIPT_USER;SUP_CODE
//���յ���
selectDevReceipt.RECEIPT_NO=A.RECEIPT_NO=<RECEIPT_NO>
//�վݺ�
selectDevReceipt.INVOICE_NO=A.INVOICE_NO=<INVOICE_NO>
//����ʱ��
selectDevReceipt.RECEIPT_DATE_BEGIN=A.RECEIPT_DATE BETWEEN <RECEIPT_DATE_BEGIN> AND <RECEIPT_DATE_END>
//���տ���
selectDevReceipt.RECEIPT_DEPT=A.RECEIPT_DEPT = <RECEIPT_DEPT>
//������Ա
selectDevReceipt.RECEIPT_USER=A.RECEIPT_USER = <RECEIPT_USER>
//��Ӧ����
selectDevReceipt.SUP_CODE=A.SUP_CODE = <SUP_CODE>
selectDevReceipt.Debug=N



//�������յ���ϸ��Ϣ
selectReceiptD.Type=TSQL
selectReceiptD.SQL=SELECT B.DEVPRO_CODE, B.DEV_CODE,B.DEV_CHN_DESC,B.SPECIFICATION, B.MAN_CODE,&
	                  (A.RECEIPT_QTY-A.SUM_QTY) QTY, A.SUM_QTY, A.RECEIPT_QTY, B.UNIT_CODE, A.UNIT_PRICE,&
		          B.DEPR_METHOD, B.USE_DEADLINE, B.MAN_NATION, B.SEQMAN_FLG, B.MEASURE_FLG,&
		          B.BENEFIT_FLG,A.SEQ_NO ,B.DEVKIND_CODE &
		   FROM   DEV_RECEIPTD A, DEV_BASE B &
		   WHERE  A.RECEIPT_NO=<RECEIPT_NO> &
		   AND    A.RECEIPT_QTY > A.SUM_QTY &
		   AND    A.DEV_CODE=B.DEV_CODE
selectReceiptD.Debug=N


//ȡ������
getBatchNo.Type=TSQL
getBatchNo.SQL=SELECT BATCH_SEQ &
	       FROM   DEV_STOCKM &
	       WHERE  DEV_CODE = <DEV_CODE> &
	       AND    DEP_DATE = <DEP_DATE> &
	       AND    GUAREP_DATE = <GUAREP_DATE>
getBatchNo.Debug=N


//ȡ���������
getMaxBatchNo.Type=TSQL
getMaxBatchNo.SQL=SELECT MAX(BATCH_SEQ) BATCH_SEQ &
	          FROM   DEV_STOCKM &
	          WHERE  DEV_CODE=<DEV_CODE>
getMaxBatchNo.Debug=N


//д������ϸ��
insertDevInwarehouseD.Type=TSQL
insertDevInwarehouseD.SQL=INSERT INTO DEV_INWAREHOUSED ( INWAREHOUSE_NO, SEQ_NO, DEV_CODE, BATCH_SEQ, SEQMAN_FLG, &
                                                         QTY, UNIT_PRICE, MAN_DATE, SCRAP_VALUE, GUAREP_DATE, &
                                                         DEP_DATE, FILES_WAY, VERIFY_NO, VERIFY_NO_SEQ, OPT_USER, &
                                                         OPT_DATE, OPT_TERM )& 
                                                VALUES ( <INWAREHOUSE_NO>, <SEQ_NO>, <DEV_CODE>, <BATCH_SEQ>, <SEQMAN_FLG>, &
                                                         <QTY>, <UNIT_PRICE>, <MAN_DATE>, <SCRAP_VALUE>, <GUAREP_DATE>, &
                                                         <DEP_DATE>, <FILES_WAY>, <VERIFY_NO>, <VERIFY_NO_SEQ>, <OPT_USER>, &
                                                         <OPT_DATE>, <OPT_TERM> )
insertDevInwarehouseD.Debug=N


//д���������ϸ��
insertDevInwarehouseDD.Type=TSQL
insertDevInwarehouseDD.SQL=INSERT INTO DEV_INWAREHOUSEDD ( INWAREHOUSE_NO, SEQ_NO, DDSEQ_NO, DEV_CODE, DEVSEQ_NO, &
                                                           BATCH_SEQ, SETDEV_CODE, MAN_DATE, MANSEQ_NO, SCRAP_VALUE, &
                                                           GUAREP_DATE,DEP_DATE,UNIT_PRICE,OPT_USER, OPT_DATE, &
                                                           OPT_TERM) &
                                                  VALUES ( <INWAREHOUSE_NO>, <SEQ_NO>, <DDSEQ_NO>, <DEV_CODE>, <DEVSEQ_NO>, &
                                                           <BATCH_SEQ>, <SETDEV_CODE>, <MAN_DATE>, <MANSEQ_NO>, <SCRAP_VALUE>,&
                                                           <GUAREP_DATE>,<DEP_DATE>,<UNIT_PRICE>,<OPT_USER>, <OPT_DATE>, &
                                                           <OPT_TERM>)

insertDevInwarehouseDD.Debug=N


//д��������
insertDevInwarehouseM.Type=TSQL
insertDevInwarehouseM.SQL=INSERT INTO DEV_INWAREHOUSEM ( INWAREHOUSE_NO,VERIFY_NO,INWAREHOUSE_DATE,INWAREHOUSE_USER,INWAREHOUSE_DEPT,&
                                                         OPT_USER,OPT_DATE,OPT_TERM ) &
		                                VALUES ( <INWAREHOUSE_NO>,<VERIFY_NO>,<INWAREHOUSE_DATE>,<INWAREHOUSE_USER>,<INWAREHOUSE_DEPT>,&
		                                         <OPT_USER>,<OPT_DATE>,<OPT_TERM>) 
insertDevInwarehouseM.Debug=N


//���¿������
updateDevInwarehouseM.Type=TSQL
updateDevInwarehouseM.SQL=UPDATE DEV_INWAREHOUSEM SET INWAREHOUSE_NO = <INWAREHOUSE_NO>,VERIFY_NO = <VERIFY_NO>,INWAREHOUSE_DATE = <INWAREHOUSE_DATE>, &
                                                      INWAREHOUSE_USER = <INWAREHOUSE_USER>,INWAREHOUSE_DEPT = <INWAREHOUSE_DEPT>,OPT_USER = <OPT_USER>,&
                                                      OPT_DATE = <OPT_DATE>,OPT_TERM = <OPT_TERM> &
                                                WHERE INWAREHOUSE_NO = <INWAREHOUSE_NO>
updateDevInwarehouseM.Debug=N

//���¿����ϸ��
updateDevInwarehouseD.Type=TSQL
updateDevInwarehouseD.SQL=UPDATE DEV_INWAREHOUSED SET INWAREHOUSE_NO = <INWAREHOUSE_NO>, SEQ_NO = <SEQ_NO>, DEV_CODE = <DEV_CODE>,&
                                                      BATCH_SEQ = <BAT_NO>, SEQMAN_FLG = <SEQMAN_FLG>,QTY = <QTY>, &
                                                      UNIT_PRICE = <UNIT_PRICE>, MAN_DATE = <MAN_DATE>, SCRAP_VALUE = <LAST_PRICE>,&
                                                      GUAREP_DATE = <GUAREP_DATE>,DEP_DATE = <DEP_DATE>, FILES_WAY = <FILES_WAY>,&
                                                      VERIFY_NO = <VERIFY_NO>, VERIFY_NO_SEQ = <VERIFY_NO_SEQ>, OPT_USER = <OPT_USER>, &
                                                      OPT_DATE = <OPT_DATE>, OPT_TERM = <OPT_TERM> &
                                                WHERE INWAREHOUSE_NO = <INWAREHOUSE_NO> &
                                                AND   SEQ_NO = <SEQ_NO>
updateDevInwarehouseD.Debug=N


//���¿�������ϸ��
updateDevInwarehouseDD.Type=TSQL
updateDevInwarehouseDD.SQL=UPDATE DEV_INWAREHOUSEDD SET INWAREHOUSE_NO = <INWAREHOUSE_NO>, SEQ_NO = <SEQ_NO>, DDSEQ_NO = <DDSEQ_NO>, &
                                                        DEV_CODE = <DEV_CODE>, BATCH_SEQ = <BAT_NO>,&
                                                        SETDEV_CODE = <MAIN_DEV>, MAN_DATE = <MAN_DATE>, MANSEQ_NO = <MAN_SEQ>, &
                                                        SCRAP_VALUE = <LAST_PRICE>, GUAREP_DATE = <GUAREP_DATE>, DEP_DATE = <DEP_DATE>,&
                                                        UNIT_PRICE = <TOT_VALUE>,OPT_USER = <OPT_USER>, OPT_DATE = <OPT_DATE>, &
                                                        OPT_TERM = <OPT_TERM> &
                                            WHERE   INWAREHOUSE_NO = <INWAREHOUSE_NO> &
					    AND     SEQ_NO = <SEQ_NO> &
					    AND     DDSEQ_NO = <DDSEQ_NO>

updateDevInwarehouseDD.Debug=N



//��ѯ�����������ϸ
selectDevInwarehouseD.Type=TSQL
selectDevInwarehouseD.SQL=SELECT 'N' DEL_FLG,C.RECEIPT_NO VERIFY_NO,C.SEQ_NO VERIFY_NO_SEQ,D.DEVPRO_CODE,B.DEV_CODE,B.BATCH_SEQ BAT_NO,&
                                  D.DEV_CHN_DESC,D.SPECIFICATION,D.MAN_CODE,B.QTY,C.SUM_QTY,&
                                  C.RECEIPT_QTY,D.UNIT_CODE,B.UNIT_PRICE,B.QTY * B.UNIT_PRICE TOT_VALUE,B.MAN_DATE,&
                                  B.SCRAP_VALUE LAST_PRICE,B.GUAREP_DATE,D.DEPR_METHOD,D.USE_DEADLINE,B.DEP_DATE,&
                                  D.MAN_NATION,D.SEQMAN_FLG,D.MEASURE_FLG,D.BENEFIT_FLG,B.FILES_WAY,&
                                  B.INWAREHOUSE_NO,B.SEQ_NO,D.DEVKIND_CODE &
                          FROM   DEV_INWAREHOUSEM A,DEV_INWAREHOUSED B,DEV_RECEIPTD C,DEV_BASE D &
                          WHERE  A.INWAREHOUSE_NO = <INWAREHOUSE_NO> & 
                          AND    A.INWAREHOUSE_NO = B.INWAREHOUSE_NO &
                          AND    B.VERIFY_NO = C.RECEIPT_NO(+) &
                          AND    B.VERIFY_NO_SEQ = C.SEQ_NO(+) &
                          AND    B.DEV_CODE = D.DEV_CODE 
selectDevInwarehouseD.Debug=N



//��ѯ�����������Ź�����ϸ
selectDevInwarehouseDD.Type=TSQL
selectDevInwarehouseDD.SQL=SELECT 'N' DEL_FLG, C.DEVPRO_CODE, B.BATCH_SEQ BAT_NO,B.DEV_CODE,B.DDSEQ_NO,&
                                  C.DEV_CHN_DESC,B.SETDEV_CODE MAIN_DEV,B.MAN_DATE,B.MANSEQ_NO MAN_SEQ,B.SCRAP_VALUE LAST_PRICE,&
                                  B.GUAREP_DATE,B.DEP_DATE,B.UNIT_PRICE TOT_VALUE,B.INWAREHOUSE_NO,B.SEQ_NO,&
                                  B.DEVSEQ_NO &
                          FROM    DEV_INWAREHOUSEM A,DEV_INWAREHOUSEDD B,DEV_BASE C &
                          WHERE   A.INWAREHOUSE_NO = <INWAREHOUSE_NO> &
                          AND     A.INWAREHOUSE_NO = B.INWAREHOUSE_NO &
                          AND     B.DEV_CODE = C.DEV_CODE
selectDevInwarehouseDD.Debug=N



//ȡ�ÿ�����
getStock.Type=TSQL
getStock.SQL=SELECT DEPT_CODE &
             FROM DEV_STOCKM &
             WHERE DEPT_CODE=<INWAREHOUSE_DEPT> &
             AND   BATCH_SEQ=<BATCH_SEQ> &
             AND   DEV_CODE=<DEV_CODE> 
getStock.Debug=N


//���¿����
updateStock.Type=TSQL
updateStock.SQL=UPDATE DEV_STOCKM SET QTY=QTY+<QTY>,&
                                      OPT_USER=<OPT_USER>,&
                                      OPT_DATE=<OPT_DATE>,&
                                      OPT_TERM=<OPT_TERM> &
                WHERE DEPT_CODE=<INWAREHOUSE_DEPT> &
                AND   BATCH_SEQ=<BATCH_SEQ> &
                AND   DEV_CODE=<DEV_CODE>
updateStock.Debug=N

//д����������Ϣ
insertStock.Type=TSQL
insertStock.SQL=INSERT INTO DEV_STOCKM (DEPT_CODE, DEV_CODE, BATCH_SEQ, QTY,UNIT_PRICE, &
                                        MAN_DATE,SCRAP_VALUE,GUAREP_DATE,DEP_DATE,FILES_WAY, &
                                        CARE_USER,USE_USER,LOC_CODE,INWAREHOUSE_DATE,STOCK_FLG,&
                                        OPT_USER, OPT_DATE,OPT_TERM) &
                                VALUES (<INWAREHOUSE_DEPT>,<DEV_CODE>,<BATCH_SEQ>,<QTY>,<UNIT_PRICE>, &
                                        <MAN_DATE>,<SCRAP_VALUE>,<GUAREP_DATE>,<DEP_DATE>,<FILES_WAY>, &
                                        <CARE_USER>,<USE_USER>,<LOC_CODE>,<INWAREHOUSE_DATE>,<STOCK_FLG>,&
                                        <OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertStock.Debug=N



//д������ϸ��Ϣ
insertStockD.Type=TSQL
insertStockD.SQL=INSERT INTO DEV_STOCKD ( DEPT_CODE, DEV_CODE, BATCH_SEQ, DEVSEQ_NO, DEVKIND_CODE,& 
                         DEVTYPE_CODE, DEVPRO_CODE, SETDEV_CODE, SPECIFICATION, QTY,&
                         UNIT_PRICE, BUYWAY_CODE, MAN_NATION, MAN_CODE, SUPPLIER_CODE, &
                         MAN_DATE, MANSEQ_NO, FUNDSOURCE, APPROVE_AMT, SELF_AMT, &
                         GUAREP_DATE, DEPR_METHOD, DEP_DATE, SCRAP_VALUE, QUALITY_LEVEL, &
                         DEV_CLASS, STOCK_STATUS, SERVICE_STATUS, CARE_USER, USE_USER, &
                         LOC_CODE, MEASURE_FLG, MEASURE_ITEMDESC, MEASURE_DATE, OPT_USER, &
                         OPT_DATE, OPT_TERM ) &
                VALUES ( <DEPT_CODE>, <DEV_CODE>, <BATCH_SEQ>, <DEVSEQ_NO>, <DEVKIND_CODE>, &
                         <DEVTYPE_CODE>, <DEVPRO_CODE>, <SETDEV_CODE>, <SPECIFICATION>, <QTY>,&
                         <UNIT_PRICE>, <BUYWAY_CODE>, <MAN_NATION>, <MAN_CODE>, <SUPPLIER_CODE>, &
                         <MAN_DATE>, <MANSEQ_NO>, <FUNDSOURCE>, <APPROVE_AMT>, <SELF_AMT>, &
                         <GUAREP_DATE>, <DEPR_METHOD>, <DEP_DATE>, <SCRAP_VALUE>, <QUALITY_LEVEL>, &
                         <DEV_CLASS>, <STOCK_STATUS>, <SERVICE_STATUS>, <CARE_USER>, <USE_USER>, &
                         <LOC_CODE>, <MEASURE_FLG>, <MEASURE_ITEMDESC>, <MEASURE_DATE>, <OPT_USER>,& 
                         <OPT_DATE>, <OPT_TERM>)
insertStockD.Debug=N


//�������յ��ۼ������
updateStockDSumQty.Type=TSQL
updateStockDSumQty.SQL=UPDATE DEV_RECEIPTD SET SUM_QTY = SUM_QTY + <QTY> &
                       WHERE  RECEIPT_NO = <VERIFY_NO>&
                       AND    SEQ_NO = <VERIFY_NO_SEQ> 
updateStockDSumQty.Debug=N


//�õ��豸���˳���
getMaxDevSeqNo.Type=TSQL
getMaxDevSeqNo.SQL=SELECT MAX(DEVSEQ_NO) DEVSEQ_NO &
                   FROM   DEV_STOCKD &
                   WHERE  DEV_CODE=<DEV_CODE>
getMaxDevSeqNo.Debug