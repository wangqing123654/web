Module.item=queryforAmout;selectByOrder;updateStockPrice;updateTradePrice;updateRetailPrice;selectByOrderRoute


queryforAmout.Type=TSQL
queryforAmout.SQL=SELECT SPECIFICATION,ALIAS_DESC,REUSE_FLG,HALF_USE_FLG,ODD_FLG,UDCARRY_FLG,DSPNSTOTDOSE_FLG,PHA_TYPE,DEFAULT_TOTQTY FROM PHA_BASE WHERE ORDER_CODE=<ORDER_CODE>
queryforAmout.Debug=N

selectByOrder.Type=TSQL
selectByOrder.SQL=SELECT SPECIFICATION,ALIAS_DESC,FREQ_CODE,ROUTE_CODE,TAKE_DAYS,&
			 MEDI_QTY,MEDI_UNIT,DEFAULT_TOTQTY,STOCK_UNIT,PURCH_UNIT,&
			 CTRLDRUGCLASS_CODE,HALF_USE_FLG,REUSE_FLG,ANTIBIOTIC_CODE,GIVEBOX_FLG,&
			 DOSAGE_UNIT ,STOCK_PRICE,B.DOSE_TYPE,A.PHA_TYPE,DSPNSTOTDOSE_FLG,ODD_FLG &
		    FROM PHA_BASE A,PHA_DOSE B &
		   WHERE ORDER_CODE=<ORDER_CODE> &
		     AND A.DOSE_CODE=B.DOSE_CODE
selectByOrder.Debug=N

//add by huangtt 20141104 抓取指定药品基本数据_用法
selectByOrderRoute.Type=TSQL
selectByOrderRoute.SQL=SELECT SPECIFICATION,ALIAS_DESC,FREQ_CODE,A.ROUTE_CODE,TAKE_DAYS, &
			MEDI_QTY,MEDI_UNIT,DEFAULT_TOTQTY,STOCK_UNIT,PURCH_UNIT, &
			CTRLDRUGCLASS_CODE,HALF_USE_FLG,REUSE_FLG,ANTIBIOTIC_CODE,GIVEBOX_FLG, &
			 DOSAGE_UNIT ,STOCK_PRICE,B.CLASSIFY_TYPE DOSE_TYPE,A.PHA_TYPE,DSPNSTOTDOSE_FLG,ODD_FLG  &
			 FROM PHA_BASE A,SYS_PHAROUTE B  &
			WHERE ORDER_CODE=<ORDER_CODE> &
			AND A.ROUTE_CODE=B.ROUTE_CODE
selectByOrderRoute.Debug=N

//更新移动加权平均价
updateStockPrice.Type=TSQL
updateStockPrice.SQL=UPDATE PHA_BASE SET STOCK_PRICE = <STOCK_PRICE> WHERE ORDER_CODE = <ORDER_CODE>
updateStockPrice.Debug=N

//更新批发价
updateTradePrice.Type=TSQL
updateTradePrice.SQL=UPDATE PHA_BASE SET TRADE_PRICE = <TRADE_PRICE> WHERE ORDER_CODE = <ORDER_CODE>
updateTradePrice.Debug=N

//更新零售价
updateRetailPrice.Type=TSQL
updateRetailPrice.SQL=UPDATE PHA_BASE SET RETAIL_PRICE = <RETAIL_PRICE>, TRADE_PRICE=<TRADE_PRICE> WHERE ORDER_CODE = <ORDER_CODE>
updateRetailPrice.Debug=N