 #
   # Title: �տ�潻�׵�
   #
   # Description:�տ�潻�׵�
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009.05.08

Module.item=insertDDStock;qyeryDDStock;queryDDStcokDayA;queryDDStcokDayB;queryDDStcokDayStockOwn;queryDDStcokDayC;queryDDStcokDayD;getbildrugQuery

//�տ�潻�׵�
insertDDStock.Type=TSQL
insertDDStock.SQL=INSERT INTO IND_DDSTOCK( &
			 TRANDATE, ORG_CODE, ORDER_CODE, BATCH_SEQ, BATCH_NO, &
			 VALID_DATE, REGION_CODE, STOCK_QTY, STOCK_AMT, LAST_TOTSTOCK_QTY, &
			 LAST_TOTSTOCK_AMT, IN_QTY, IN_AMT, OUT_QTY, OUT_AMT, &
			 CHECKMODI_QTY, CHECKMODI_AMT, VERIFYIN_QTY, VERIFYIN_AMT, FAVOR_QTY, &
			 REGRESSGOODS_QTY, REGRESSGOODS_AMT, DOSAGE_QTY, DOSAGE_AMT, REGRESSDRUG_QTY, &
			 REGRESSDRUG_AMT, PROFIT_LOSS_AMT, VERIFYIN_PRICE, STOCK_PRICE, RETAIL_PRICE, &
			 TRADE_PRICE, STOCKIN_QTY, STOCKIN_AMT, STOCKOUT_QTY, STOCKOUT_AMT, &
			 OPT_USER, OPT_DATE, OPT_TERM,REQUEST_IN_QTY,REQUEST_IN_AMT, &
			 REQUEST_OUT_QTY,REQUEST_OUT_AMT,GIF_IN_QTY,GIF_IN_AMT,GIF_OUT_QTY, &
			 GIF_OUT_AMT,RET_IN_QTY,RET_IN_AMT,RET_OUT_QTY,RET_OUT_AMT, &
			 WAS_OUT_QTY,WAS_OUT_AMT,THO_OUT_QTY,THO_OUT_AMT,THI_IN_QTY, &
			 THI_IN_AMT,COS_OUT_QTY,COS_OUT_AMT) &
		  VALUES( &
			 <TRANDATE>, <ORG_CODE>, <ORDER_CODE>, <BATCH_SEQ>, <BATCH_NO>, &
			 <VALID_DATE>, <REGION_CODE>, <STOCK_QTY>, <STOCK_AMT>, <LAST_TOTSTOCK_QTY>, &
			 <LAST_TOTSTOCK_AMT>, <IN_QTY>, <IN_AMT>, <OUT_QTY>, <OUT_AMT>, &
			 <CHECKMODI_QTY>, <CHECKMODI_AMT>, <VERIFYIN_QTY>, <VERIFYIN_AMT>, <FAVOR_QTY>, &
			 <REGRESSGOODS_QTY>, <REGRESSGOODS_AMT>, <DOSAGE_QTY>, <DOSAGE_AMT>, <REGRESSDRUG_QTY>, &
			 <REGRESSDRUG_AMT>, <PROFIT_LOSS_AMT>, <VERIFYIN_PRICE>, <STOCK_PRICE>, <RETAIL_PRICE>, &
			 <TRADE_PRICE>, <STOCKIN_QTY>, <STOCKIN_AMT>, <STOCKOUT_QTY>, <STOCKOUT_AMT>, &
			 <OPT_USER>, <OPT_DATE>, <OPT_TERM>, <REQUEST_IN_QTY>, <REQUEST_IN_AMT>, &
			 <REQUEST_OUT_QTY>, <REQUEST_OUT_AMT>, <GIF_IN_QTY>, <GIF_IN_AMT>, <GIF_OUT_QTY>, &
			 <GIF_OUT_AMT>, <RET_IN_QTY>, <RET_IN_AMT>, <RET_OUT_QTY>, <RET_OUT_AMT>, &
			 <WAS_OUT_QTY>, <WAS_OUT_AMT>, <THO_OUT_QTY>, <THO_OUT_AMT>, <THI_IN_QTY>, &
			 <THI_IN_AMT>, <COS_OUT_QTY>, <COS_OUT_AMT>)
insertDDStock.Debug=N



//��ѯ
qyeryDDStock.Type=TSQL
qyeryDDStock.SQL=SELECT TRANDATE, ORDER_CODE, STOCK_PRICE &
    		 FROM IND_DDSTOCK &
                 WHERE TO_DATE(TRANDATE, 'YYYYMMDD') &
                   BETWEEN TO_DATE(<START_DATE>,'YYYYMMDD') &
                   AND TO_DATE(<END_DATE>,'YYYYMMDD') &
	         GROUP BY TRANDATE, ORDER_CODE, STOCK_PRICE &
		 ORDER BY TRANDATE DESC
qyeryDDStock.ITEM=ORG_CODE;ORDER_CODE;BATCH_SEQ
qyeryDDStock.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
qyeryDDStock.ORG_CODE=ORG_CODE=<ORG_CODE>
qyeryDDStock.BATCH_SEQ=BATCH_SEQ=<BATCH_SEQ>
qyeryDDStock.Debug=N


//ҩ���ս�ɱ�����ѯ
queryDDStcokDayA.Type=TSQL
queryDDStcokDayA.SQL=SELECT SUM (VERIFYIN_QTY * VERIFYIN_PRICE) AS VERIFYIN_AMT, &
       			    SUM (REGRESSGOODS_QTY * VERIFYIN_PRICE) AS REGRESSGOODS_AMT, &
       			    SUM (REQUEST_OUT_QTY * VERIFYIN_PRICE) AS REQUEST_OUT_AMT,  &
       			    SUM (RET_IN_QTY * VERIFYIN_PRICE) AS RET_IN_AMT, &
       			    SUM (THO_OUT_QTY * VERIFYIN_PRICE) AS THO_OUT_AMT, &
       			    SUM (THI_IN_QTY * VERIFYIN_PRICE) AS THI_IN_AMT, &
       			    SUM (WAS_OUT_QTY * VERIFYIN_PRICE) AS WAS_OUT_AMT, &
       			    SUM (COS_OUT_QTY * VERIFYIN_PRICE) AS COS_OUT_AMT, &
       			    SUM (CHECKMODI_QTY * VERIFYIN_PRICE) AS CHECKMODI_AMT &
  			FROM IND_DDSTOCK &
 			WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE (<START_DATE>, 'YYYYMMDD') &
                          AND TO_DATE (<END_DATE>, 'YYYYMMDD') &
   			  AND ORG_CODE = <ORG_CODE> &
   			  AND ORDER_CODE = <ORDER_CODE>
queryDDStcokDayA.Debug=N

//ҩ���ս�ɱ�����ѯ
queryDDStcokDayB.Type=TSQL
queryDDStcokDayB.SQL=SELECT SUM (REQUEST_IN_QTY * VERIFYIN_PRICE) AS REQUEST_IN_AMT, &
       			    SUM (RET_OUT_QTY * VERIFYIN_PRICE) AS RET_OUT_AMT, &
       			    SUM (RET_IN_QTY * VERIFYIN_PRICE) AS RET_IN_AMT,  &
       			    SUM (REQUEST_OUT_QTY * VERIFYIN_PRICE) AS REQUEST_OUT_AMT, &
       			    SUM (GIF_IN_QTY * VERIFYIN_PRICE) AS GIF_IN_AMT, &
       			    SUM (GIF_OUT_QTY * VERIFYIN_PRICE) AS GIF_OUT_AMT, &
       			    SUM (REGRESSDRUG_QTY * VERIFYIN_PRICE) AS REGRESSDRUG_AMT, &
       			    SUM (DOSAGE_QTY * VERIFYIN_PRICE) AS DOSAGE_AMT, &
       			    SUM (THO_OUT_QTY * VERIFYIN_PRICE) AS THO_OUT_AMT, &
			    SUM (THI_IN_QTY * VERIFYIN_PRICE) AS THI_IN_AMT, &
			    SUM (WAS_OUT_QTY * VERIFYIN_PRICE) AS WAS_OUT_AMT, &
			    SUM (COS_OUT_QTY * VERIFYIN_PRICE) AS COS_OUT_AMT, &
       			    SUM (CHECKMODI_QTY * VERIFYIN_PRICE) AS CHECKMODI_AMT &
  			FROM IND_DDSTOCK &
 			WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE (<START_DATE>, 'YYYYMMDD') &
                          AND TO_DATE (<END_DATE>, 'YYYYMMDD') &
   			  AND ORG_CODE = <ORG_CODE> &
   			  AND ORDER_CODE = <ORDER_CODE>
queryDDStcokDayB.Debug=N


//ҩ�⡢ҩ���ս��в�ѯ���ڽ��ĳɱ������۽��
queryDDStcokDayStockOwn.Type=TSQL
queryDDStcokDayStockOwn.SQL=SELECT SUM (STOCK_QTY * VERIFYIN_PRICE) AS STOCK_AMT, &
       			    SUM (STOCK_AMT) AS OWN_AMT &
  			FROM IND_DDSTOCK &
 			WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE (<START_DATE>, 'YYYYMMDD') &
                          AND TO_DATE (<END_DATE>, 'YYYYMMDD') &
   			  AND ORG_CODE = <ORG_CODE> &
   			  AND ORDER_CODE = <ORDER_CODE>
queryDDStcokDayStockOwn.Debug=N


//ҩ���ս����۽���ѯ
queryDDStcokDayC.Type=TSQL
queryDDStcokDayC.SQL=SELECT SUM (VERIFYIN_AMT) AS VERIFYIN_AMT, &
                            SUM (REGRESSGOODS_AMT) AS REGRESSGOODS_AMT, & 
                            SUM (REQUEST_OUT_AMT) AS REQUEST_OUT_AMT, &
                            SUM (RET_IN_AMT) AS RET_IN_AMT, &
                            SUM (THO_OUT_AMT) AS THO_OUT_AMT, &
                            SUM (THI_IN_AMT) AS THI_IN_AMT, &
                            SUM (WAS_OUT_AMT) AS WAS_OUT_AMT, &
                            SUM (COS_OUT_AMT) AS COS_OUT_AMT, &
                            SUM (CHECKMODI_AMT) AS CHECKMODI_AMT, &
                            SUM (PROFIT_LOSS_AMT) AS PROFIT_LOSS_AMT &
  			FROM IND_DDSTOCK &
 			WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE (<START_DATE>, 'YYYYMMDD') &
                          AND TO_DATE (<END_DATE>, 'YYYYMMDD') &
   			  AND ORG_CODE = <ORG_CODE> &
   			  AND ORDER_CODE = <ORDER_CODE>
queryDDStcokDayC.Debug=N


//ҩ���ս����۽���ѯ
queryDDStcokDayD.Type=TSQL
queryDDStcokDayD.SQL=SELECT SUM (REQUEST_IN_AMT) AS REQUEST_IN_AMT, &
                       	    SUM (RET_OUT_AMT) AS RET_OUT_AMT, &
                            SUM (RET_IN_AMT) AS RET_IN_AMT,  &
                            SUM (REQUEST_OUT_AMT) AS REQUEST_OUT_AMT, &
                            SUM (GIF_IN_AMT) AS GIF_IN_AMT, &
                            SUM (GIF_OUT_AMT) AS GIF_OUT_AMT, &
                            SUM (REGRESSDRUG_AMT) AS REGRESSDRUG_AMT, &
                            SUM (DOSAGE_AMT) AS DOSAGE_AMT, &
                            SUM (THO_OUT_AMT) AS THO_OUT_AMT, &
                	    SUM (THI_IN_AMT) AS THI_IN_AMT, &
                            SUM (WAS_OUT_AMT) AS WAS_OUT_AMT, &
                            SUM (COS_OUT_AMT) AS COS_OUT_AMT, &
                            SUM (CHECKMODI_AMT) AS CHECKMODI_AMT, &
                            SUM (PROFIT_LOSS_AMT) AS PROFIT_LOSS_AMT &
  			FROM IND_DDSTOCK &
 			WHERE TO_DATE (TRANDATE, 'YYYYMMDD') BETWEEN TO_DATE (<START_DATE>, 'YYYYMMDD') &
                          AND TO_DATE (<END_DATE>, 'YYYYMMDD') &
   			  AND ORG_CODE = <ORG_CODE> &
   			  AND ORDER_CODE = <ORDER_CODE>
queryDDStcokDayD.Debug=N

//ҩƷ������ͳ�Ʊ���
getbildrugQuery.Type=TSQL
getbildrugQuery.SQL=SELECT  B.ORDER_DESC ,B .SPECIFICATION ,C.UNIT_CHN_DESC  UNIT ,A.RETAIL_PRICE  PRICE,SUM(A.OUT_QTY)  AS QTY ,SUM(A.OUT_AMT) AS OUT_AMT ,B.SYS_PHA_CLASS &
                              FROM IND_DDSTOCK A , &
                              SYS_FEE B , &
                              SYS_UNIT C  , &
                              PHA_BASE D  &
                              WHERE   A.ORDER_CODE=B.ORDER_CODE(+) &
                              AND B.UNIT_CODE=C.UNIT_CODE  &
                              AND A.ORDER_CODE=D.ORDER_CODE(+)  &
                               AND B.ORDER_CODE=D.ORDER_CODE      &
                              AND TO_DATE(A.TRANDATE,'YYYYMMDD') BETWEEN TO_DATE(<START_DATE>,'YYYYMMDD') AND TO_DATE(<END_DATE>,'YYYYMMDD')  &
                              AND A.OUT_QTY>0   &
                              GROUP BY A.ORDER_CODE , &
                              B.ORDER_DESC, &
                              A.RETAIL_PRICE, &
                              C.UNIT_CHN_DESC , &
                              B.SPECIFICATION,B.SYS_PHA_CLASS
getbildrugQuery.ITEM=ORG_CODE;ORDER_CODE;ROUTE_CODE;SYS_GRUG_CLASS;REGION_CODE
getbildrugQuery.ORG_CODE=A.ORG_CODE=<ORG_CODE>
getbildrugQuery.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
getbildrugQuery.ROUTE_CODE=D.ROUTE_CODE=<ROUTE_CODE>
getbildrugQuery.SYS_GRUG_CLASS=B.SYS_GRUG_CLASS=<SYS_GRUG_CLASS>
getbildrugQuery.REGION_CODE=A.REGION_CODE=<REGION_CODE>
getbildrugQuery.Debug=N