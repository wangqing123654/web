   #
   # Title:申请单明细
   #
   # Description:申请单明细
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/24

Module.item=updateActualQty;createNewBaseManageD;updateBaseManageFlg

//更新申请主档
updateActualQty.Type=TSQL
updateActualQty.SQL=UPDATE IND_BASEMANAGED SET &
			   ACTUAL_QTY=ACTUAL_QTY+<ACTUAL_QTY>, &
			   UPDATE_FLG=<UPDATE_FLG>, &
			   OPT_USER=<OPT_USER>, &  
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &  
		     WHERE BASEMANAGE_NO=<BASEMANAGE_NO> &
		       AND SEQ_NO=<SEQ_NO>
updateActualQty.Debug=N
    

createNewBaseManageD.Type=TSQL
createNewBaseManageD.SQL=INSERT INTO IND_BASEMANAGED( &
			BASEMANAGE_NO, SEQ_NO, ORDER_CODE, BATCH_NO, VALID_DATE, &
			QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, &
			UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM) &
	    	   VALUES( &
	    	   	<BASEMANAGE_NO>, <SEQ_NO>, <ORDER_CODE>, <BATCH_NO>, <VALID_DATE>, &
			<QTY>, <UNIT_CODE>, <RETAIL_PRICE>, <STOCK_PRICE>, <ACTUAL_QTY>, &
			<UPDATE_FLG>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
createNewBaseManageD.Debug=N


//更新申请单状态
updateBaseManageFlg.Type=TSQL
updateBaseManageFlg.SQL=UPDATE IND_BASEMANAGED SET &
			   UPDATE_FLG=<UPDATE_FLG>, &  
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE BASEMANAGE_NO=<BASEMANAGE_NO> 
updateBaseManageFlg.Debug=N


