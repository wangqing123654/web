   #
   # Title: �������
   #
   # Description: �������
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=updateStockQty;updateStockQtyOut;createStockM;updateStockQtyByPack;updateStockQtyByReq;updateStockM;getStockQty;updateStockQtyGYS


//���¿�������Ŀ����
updateStockQty.Type=TSQL
updateStockQty.SQL=UPDATE INV_STOCKM SET &
			STOCK_QTY=STOCK_QTY+<STOCK_QTY> , OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockQty.Debug=N


//������ҵ���¿����
updateStockQtyOut.Type=TSQL
updateStockQtyOut.SQL=UPDATE INV_STOCKM SET &
			STOCK_QTY=STOCK_QTY-<STOCK_QTY> , OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockQtyOut.Debug=N


//�����������
createStockM.Type=TSQL
createStockM.SQL=INSERT INTO INV_STOCKM( &
			ORG_CODE, INV_CODE, REGION_CODE, DISPENSE_FLG, DISPENSE_ORG_CODE, &
			STOCK_FLG, MATERIAL_LOC_CODE, SAFE_QTY, MIN_QTY, MAX_QTY, &
			ECONOMICBUY_QTY, STOCK_QTY, MM_USE_QTY, AVERAGE_DAYUSE_QTY, STOCK_UNIT, &
			OPT_USER, OPT_DATE, OPT_TERM,BASE_QTY) &
			VALUES( &
			<ORG_CODE>, <INV_CODE>, <REGION_CODE>, <DISPENSE_FLG>, <DISPENSE_ORG_CODE>, &
			<STOCK_FLG>, <MATERIAL_LOC_CODE>, <SAFE_QTY>, <MIN_QTY>, <MAX_QTY>, &
			<ECONOMICBUY_QTY>, <STOCK_QTY>, <MM_USE_QTY>, <AVERAGE_DAYUSE_QTY>, <STOCK_UNIT>, &
			<OPT_USER>, <OPT_DATE>, <OPT_TERM>,<BASE_QTY>)
createStockM.Debug=N

//���¿������
updateStockM.Type=TSQL
updateStockM.SQL=UPDATE INV_STOCKM SET &
			REGION_CODE=<REGION_CODE> , DISPENSE_FLG=<DISPENSE_FLG>, DISPENSE_ORG_CODE=<DISPENSE_ORG_CODE>, &
			MATERIAL_LOC_CODE=<MATERIAL_LOC_CODE>, SAFE_QTY=<SAFE_QTY>, MIN_QTY=<MIN_QTY>, &
			MAX_QTY=<MAX_QTY>, ECONOMICBUY_QTY=<ECONOMICBUY_QTY>, MM_USE_QTY=<MM_USE_QTY>,BASE_QTY=<BASE_QTY> , &
			AVERAGE_DAYUSE_QTY=<AVERAGE_DAYUSE_QTY>, OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockM.Debug=N

//������������¿�������Ŀ����GYSUsed
updateStockQtyByPack.Type=TSQL
updateStockQtyByPack.SQL=UPDATE INV_STOCKM SET &
			STOCK_QTY=STOCK_QTY-<STOCK_QTY> , OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockQtyByPack.Debug=N


//��Ӧ�ҳ�����ҵ���¿����(������ҵ)
updateStockQtyByReq.Type=TSQL
updateStockQtyByReq.SQL=UPDATE INV_STOCKM SET &
			       STOCK_QTY=STOCK_QTY-<QTY> , OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	         WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockQtyByReq.Debug=N


//����ȫԺ���ʿ����GYSUsed
getStockQty.Type=TSQL
getStockQty.SQL=SELECT SUM(STOCK_QTY) FROM INV_STOCKM 
getStockQty.item=INV_CODE;ORG_CODE;
getStockQty.INV_CODE=INV_CODE=<INV_CODE>
getStockQty.ORG_CODE=ORG_CODE=<ORG_CODE>
getStockQty.Debug=N


//���¿������GYSUsed
updateStockQtyGYS.Type=TSQL
updateStockQtyGYS.SQL=UPDATE INV_STOCKM SET STOCK_QTY=STOCK_QTY+<STOCK_QTY>
updateStockQtyGYS.item=INV_CODE;ORG_CODE
updateStockQtyGYS.INV_CODE=INV_CODE=<INV_CODE>
updateStockQtyGYS.ORG_CODE=ORG_CODE=<ORG_CODE>
updateStockQtyGYS.Debug=N










