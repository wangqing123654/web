   #
   # Title:��Ӧ�ҳ����嵥
   #
   # Description:��Ӧ�ҳ����嵥
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insert


//�½������嵥
insert.Type=TSQL
insert.SQL=INSERT INTO INV_SUP_DISPENSEDD( &
			DISPENSE_NO , SEQ_NO , PACK_MODE, PACK_CODE , PACK_SEQ_NO ,&
			INV_CODE , INVSEQ_NO , ONCE_USE_FLG, QTY , STOCK_UNIT, &
			COST_PRICE , REQUEST_SEQ , BATCH_NO, VALID_DATE, DISPOSAL_FLG, &
			OPT_USER , OPT_DATE , OPT_TERM) &
	    	      VALUES( &
	    	   	<DISPENSE_NO> ,  <SEQ_NO> , <PACK_MODE> , <PACK_CODE> , <PACK_SEQ_NO> , &
	    	   	<INV_CODE> ,  <INVSEQ_NO> , <ONCE_USE_FLG> , <QTY> , <STOCK_UNIT>,&
	    	   	<COST_PRICE> ,  <REQUEST_SEQ> , <BATCH_NO> , <VALID_DATE>, <DISPOSAL_FLG>, &
	    	   	<OPT_USER> , SYSDATE , <OPT_TERM> )
insert.Debug=N




       











