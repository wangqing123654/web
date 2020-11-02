   #
   # Title:��Ӧ�ҳ�����ϸ
   #
   # Description:��Ӧ�ҳ�����ϸ
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insert;queryInv;queryPack


//�½�������ϸ
insert.Type=TSQL
insert.SQL=INSERT INTO INV_SUP_DISPENSED( &
			DISPENSE_NO , SEQ_NO , PACK_MODE, INV_CODE , INVSEQ_NO ,&
			QTY , STOCK_UNIT , COST_PRICE, REQUEST_SEQ , BATCH_NO, &
			BATCH_SEQ , VALID_DATE , DISPOSAL_FLG, &
			OPT_USER , OPT_DATE , OPT_TERM) &
	    	      VALUES( &
	    	   	<DISPENSE_NO> ,  <SEQ_NO> , <PACK_MODE> , <INV_CODE> , <INVSEQ_NO> , &
	    	   	<QTY> ,  <STOCK_UNIT> , <COST_PRICE> , <REQUEST_SEQ> , <BATCH_NO>, &
	    	   	<BATCH_SEQ>, <VALID_DATE> , <DISPOSAL_FLG> ,&
	    	   	<OPT_USER> , SYSDATE , <OPT_TERM> )
insert.Debug=N


//��ѯ������ϸ(һ������)
queryInv.Type=TSQL
queryInv.SQL=SELECT A.INV_CODE, B.INV_CHN_DESC, A.INVSEQ_NO, A.QTY, A.STOCK_UNIT, &
      		    A.COST_PRICE, A.BATCH_NO, A.VALID_DATE, A.BATCH_SEQ &
  	     FROM INV_SUP_DISPENSED A, INV_BASE B &
 	     WHERE A.INV_CODE = B.INV_CODE AND A.DISPENSE_NO = <DISPENSE_NO> ORDER BY A.SEQ_NO
queryInv.Debug=N

//��ѯ������ϸ(������)
queryPack.Type=TSQL
queryPack.SQL=SELECT A.INV_CODE, B.PACK_DESC AS INV_CHN_DESC, A.INVSEQ_NO, A.QTY, A.COST_PRICE &
  	      FROM INV_SUP_DISPENSED A, INV_PACKM B &
  	      WHERE A.INV_CODE = B.PACK_CODE AND A.DISPENSE_NO = <DISPENSE_NO> ORDER BY A.SEQ_NO
queryPack.Debug=N


       











