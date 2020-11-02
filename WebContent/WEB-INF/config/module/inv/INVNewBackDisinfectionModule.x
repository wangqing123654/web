   #
   # Title:������������ϴ����
   #
   # Description:������������ϴ����
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author wangm 2013/06/26

Module.item=queryPackageInfo;queryPackageDetailInfo;insertDisinfection;updatePackageStatus;updateRecountTime;queryBatchNo;updateStockM;updateStockD;queryBackDisinfection;queryDisinfectionByRecycleNo;queryBarcodeInfo;queryDeptName;queryUserName;queryPackageInfoByBarcode;queryPMInfoByBarcode


//��ѯ��������Ϣ
queryPackageInfo.Type=TSQL
queryPackageInfo.SQL=SELECT PM.PACK_CODE, PM.PACK_SEQ_NO, PM.STATUS, PM.QTY, P.PACK_DESC, P.VALUE_DATE & 
			FROM INV_PACKSTOCKM PM LEFT JOIN INV_PACKM P ON PM.PACK_CODE = P.PACK_CODE
queryPackageInfo.item=PACK_CODE;PACK_SEQ_NO
queryPackageInfo.PACK_CODE=PM.PACK_CODE=<PACK_CODE>
queryPackageInfo.PACK_SEQ_NO=PM.PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageInfo.Debug=N

//��ѯ��������ϸ
queryPackageDetailInfo.Type=TSQL
queryPackageDetailInfo.SQL=SELECT PD.ORG_CODE, PD.PACK_CODE, PD.PACK_SEQ_NO, PD.INV_CODE, PD.INVSEQ_NO, & 
			PD.COST_PRICE, PD.RECOUNT_TIME, PD.STOCK_UNIT, PD.ONCE_USE_FLG, PD.QTY, IB.INV_CHN_DESC & 
			FROM INV_PACKSTOCKD PD LEFT JOIN INV_BASE IB ON PD.INV_CODE = IB.INV_CODE 
queryPackageDetailInfo.item=PACK_CODE;PACK_SEQ_NO
queryPackageDetailInfo.PACK_CODE=PD.PACK_CODE=<PACK_CODE>
queryPackageDetailInfo.PACK_SEQ_NO=PD.PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageDetailInfo.Debug=N


//�����½����յ���¼
insertDisinfection.Type=TSQL
insertDisinfection.SQL=INSERT INTO INV_DISINFECTION ( &
			RECYCLE_NO, ORG_CODE, PACK_CODE, PACK_SEQ_NO, QTY, RECYCLE_DATE, &
			RECYCLE_USER , WASH_DATE, WASH_USER, DISINFECTION_POTSEQ, DISINFECTION_PROGRAM, &
			DISINFECTION_OPERATIONSTAFF, DISINFECTION_DATE, DISINFECTION_VALID_DATE, DISINFECTION_USER, & 
			OPT_USER, OPT_DATE, OPT_TERM, FINISH_FLG, BARCODE ) &
	    	      VALUES( &
	    	   	<RECYCLE_NO>, <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <QTY>, & 
			TO_DATE(<RECYCLE_DATE>,'yyyy/mm/dd hh24:mi:ss'), <RECYCLE_USER>, TO_DATE(<WASH_DATE>,'yyyy/mm/dd hh24:mi:ss'), <WASH_USER>, <DISINFECTION_POTSEQ>, & 
			<DISINFECTION_PROGRAM>, <DISINFECTION_OPERATIONSTAFF>, TO_DATE(<DISINFECTION_DATE>,'yyyy/mm/dd hh24:mi:ss'), TO_DATE(<DISINFECTION_VALID_DATE>,'yyyy/mm/dd hh24:mi:ss'), <DISINFECTION_USER>, & 
			<OPT_USER>, SYSDATE, <OPT_TERM>, <FINISH_FLG>, <BARCODE> & 
			)
insertDisinfection.Debug=N


//����������״̬ 
updatePackageStatus.Type=TSQL
updatePackageStatus.SQL=UPDATE INV_PACKSTOCKM SET STATUS = <STATUS> & 
			WHERE ORG_CODE = <ORG_CODE> AND PACK_CODE = <PACK_CODE> AND PACK_SEQ_NO = <PACK_SEQ_NO>
updatePackageStatus.Debug=N



//������е�������
updateRecountTime.Type=TSQL
updateRecountTime.SQL=UPDATE INV_PACKSTOCKD SET RECOUNT_TIME = <RECOUNT_TIME> & 
			WHERE ORG_CODE = <ORG_CODE> AND PACK_CODE = <PACK_CODE> AND PACK_SEQ_NO = <PACK_SEQ_NO> AND &
			      INV_CODE = <INV_CODE> AND INVSEQ_NO = <INVSEQ_NO> 
updateRecountTime.Debug=N


//��ѯ��е������
queryBatchNo.Type=TSQL
queryBatchNo.SQL=SELECT BATCH_NO FROM INV_STOCKDD WHERE PACK_CODE = <PACK_CODE> AND PACK_SEQ_NO = <PACK_SEQ_NO>
queryBatchNo.Debug=N



//����������
updateStockM.Type=TSQL
updateStockM.SQL=UPDATE INV_STOCKM SET STOCK_QTY = STOCK_QTY + 1 WHERE ORG_CODE = <ORG_CODE> AND INV_CODE = <INV_CODE>
updateStockM.Debug=N



//����ϸ����
updateStockD.Type=TSQL
updateStockD.SQL=UPDATE INV_STOCKD SET STOCK_QTY = STOCK_QTY + 1 & 
			WHERE ORG_CODE = <ORG_CODE> AND INV_CODE = <INV_CODE> AND BATCH_NO = <BATCH_NO>
updateStockD.Debug=N




//��ѯ���յ���Ϣ
queryBackDisinfection.Type=TSQL
queryBackDisinfection.SQL=SELECT RECYCLE_NO,OPT_DATE,OPT_USER,OPT_TERM,ORG_CODE & 
				FROM INV_DISINFECTION & 
				WHERE OPT_DATE BETWEEN TO_DATE(<START_DATE>,'yyyy/mm/dd hh24:mi:ss') AND TO_DATE(<END_DATE>,'yyyy/mm/dd hh24:mi:ss') & 
				GROUP BY RECYCLE_NO,OPT_DATE,OPT_USER,OPT_TERM,ORG_CODE ORDER BY RECYCLE_NO DESC
queryBackDisinfection.ITEM=RECYCLE_NO
queryBackDisinfection.RECYCLE_NO=RECYCLE_NO=<RECYCLE_NO>
queryBackDisinfection.Debug=N


//���ݻ��յ��Ų�ѯ���յ���ϸ
queryDisinfectionByRecycleNo.Type=TSQL
queryDisinfectionByRecycleNo.SQL=SELECT P.PACK_DESC,D.PACK_CODE,D.PACK_SEQ_NO,D.RECYCLE_DATE,D.RECYCLE_USER, & 
					D.WASH_DATE,D.WASH_USER,D.DISINFECTION_DATE,D.DISINFECTION_VALID_DATE,D.DISINFECTION_USER, & 
					D.DISINFECTION_POTSEQ,D.DISINFECTION_PROGRAM,D.QTY,D.BARCODE  & 
					FROM INV_DISINFECTION D LEFT JOIN INV_PACKM P ON D.PACK_CODE = P.PACK_CODE 
queryDisinfectionByRecycleNo.ITEM=RECYCLE_NO
queryDisinfectionByRecycleNo.RECYCLE_NO=RECYCLE_NO=<RECYCLE_NO>
queryDisinfectionByRecycleNo.Debug=N


//��ѯ�����������ӡ������Ϣ
queryBarcodeInfo.Type=TSQL
queryBarcodeInfo.SQL=SELECT P.PACK_CODE, P.PACK_SEQ_NO, S.USER_NAME, O.ORG_DESC, D.DISINFECTION_DATE, D.DISINFECTION_VALID_DATE, D.DISINFECTION_USER & 
			FROM INV_PACKSTOCKM P LEFT JOIN INV_DISINFECTION D ON P.PACK_CODE = D.PACK_CODE AND P.PACK_SEQ_NO = D.PACK_SEQ_NO & 
			LEFT JOIN INV_ORG O ON P.ORG_CODE = O.ORG_CODE LEFT JOIN SYS_OPERATOR S ON D.DISINFECTION_USER = S.USER_ID & 
			WHERE P.PACK_CODE = <PACK_CODE> AND P.PACK_SEQ_NO = <PACK_SEQ_NO> AND D.RECYCLE_NO = <RECYCLE_NO> 
queryBarcodeInfo.Debug=N


//��ѯ��������
queryDeptName.Type=TSQL
queryDeptName.SQL=SELECT O.ORG_DESC FROM INV_ORG O WHERE O.ORG_CODE = <ORG_CODE>
queryDeptName.Debug=N


//��ѯ�û�����
queryUserName.Type=TSQL
queryUserName.SQL=SELECT S.USER_NAME FROM SYS_OPERATOR S WHERE S.USER_ID = <USER_ID>
queryUserName.Debug=Y


//���������ѯ��������Ϣ
queryPackageInfoByBarcode.Type=TSQL
queryPackageInfoByBarcode.SQL=SELECT PM.PACK_CODE, PM.PACK_SEQ_NO, PM.STATUS, PM.QTY, P.PACK_DESC, P.VALUE_DATE, PM.BARCODE & 
			FROM INV_PACKSTOCKM PM LEFT JOIN INV_PACKM P ON PM.PACK_CODE = P.PACK_CODE 
queryPackageInfoByBarcode.item=BARCODE;PACK_CODE;PACK_SEQ_NO
queryPackageInfoByBarcode.BARCODE=PM.BARCODE=<BARCODE>
queryPackageInfoByBarcode.PACK_CODE=PM.PACK_CODE=<PACK_CODE>
queryPackageInfoByBarcode.PACK_SEQ_NO=PM.PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageInfoByBarcode.Debug=N


//���������ѯ����������
queryPMInfoByBarcode.Type=TSQL
queryPMInfoByBarcode.SQL=SELECT B.INV_CHN_DESC, D.STOCK_UNIT, D.QTY, B.COST_PRICE, D.RECOUNT_TIME & 
				FROM INV_PACKSTOCKD D LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE LEFT JOIN SYS_UNIT S ON D.STOCK_UNIT = S.UNIT_CODE & 
				WHERE D.ONCE_USE_FLG = 'N' AND D.BARCODE = <BARCODE>
queryPMInfoByBarcode.Debug=N















       











