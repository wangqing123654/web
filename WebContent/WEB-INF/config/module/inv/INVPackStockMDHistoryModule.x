   #
   # Title: 手术包打包历史主细表
   #
   # Description: 手术包打包历史主细表
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author wangm 2013/07/31

Module.item=insertPackageMHistory;insertPackageDHistory

 
//插入手术包打包历史主表
insertPackageMHistory.Type=TSQL
insertPackageMHistory.SQL=INSERT INTO INV_PACKSTOCKM_HISTORY(&
			   ORG_CODE, PACK_CODE, PACK_SEQ_NO, BARCODE, DESCRIPTION, QTY, &
			   USE_COST, ONCE_USE_COST, STATUS, OPT_USER, OPT_DATE, OPT_TERM) &
	                 VALUES( &
			   <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <BARCODE>, <DESCRIPTION>, <QTY>, &
			   <USE_COST>, <ONCE_USE_COST>, <STATUS>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertPackageMHistory.Debug=N




//插入手术包打包历史细表
insertPackageDHistory.Type=TSQL
insertPackageDHistory.SQL=INSERT INTO INV_PACKSTOCKD_HISTORY(&
			   ORG_CODE, PACK_CODE, PACK_SEQ_NO, INV_CODE, BATCH_SEQ, &
			   INVSEQ_NO, BARCODE, DESCRIPTION, RECOUNT_TIME, COST_PRICE, QTY, USED_QTY, NOTUSED_QTY, &
			   STOCK_UNIT, ONCE_USE_FLG, OPT_USER, OPT_DATE, OPT_TERM) &
	                 VALUES( &
			   <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <INV_CODE>, <BATCH_SEQ>, &
			   <INVSEQ_NO>, <BARCODE>, <DESCRIPTION>, <RECOUNT_TIME>, <COST_PRICE>, <QTY>, <USED_QTY>, <NOTUSED_QTY>, &
			   <STOCK_UNIT>, <ONCE_USE_FLG>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertPackageDHistory.Debug=N




