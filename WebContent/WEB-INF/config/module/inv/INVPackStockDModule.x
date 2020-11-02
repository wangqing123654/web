   #
   # Title: �����������ϸ��
   #
   # Description: �����������ϸ��
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insertStockQtyByPack;updateStockQtyByPack;updateQtyBySupReq;deleteOnceUserInvBySupReq;deleteAll;insertInv;updateQty


//���������������Ź���ϸ��Ŀ����
insertStockQtyByPack.Type=TSQL
insertStockQtyByPack.SQL=INSERT INTO INV_PACKSTOCKD(&
			   ORG_CODE, PACK_CODE, PACK_SEQ_NO, INV_CODE, BATCH_SEQ, &
			   INVSEQ_NO, DESCRIPTION, RECOUNT_TIME, COST_PRICE, QTY, &
			   STOCK_UNIT, ONCE_USE_FLG, OPT_USER, OPT_DATE, OPT_TERM) &
	                 VALUES( &
			   <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <INV_CODE>, <BATCH_SEQ>, &
			   <INVSEQ_NO>, <DESCRIPTION>, <RECOUNT_TIME>, <COST_PRICE>, <QTY>, &
			   <STOCK_UNIT>, <ONCE_USE_FLG>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertStockQtyByPack.Debug=N


//���������������Ź���ϸ��Ŀ����
updateStockQtyByPack.Type=TSQL
updateStockQtyByPack.SQL=UPDATE INV_PACKSTOCKD SET  QTY=QTY+<QTY>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO> AND INV_CODE=<INV_CODE> AND INVSEQ_NO=<INVSEQ_NO>
updateStockQtyByPack.Debug=N


//û����Ź����������,�۳������������ϸ�еĿ����
updateQtyBySupReq.Type=TSQL
updateQtyBySupReq.SQL=UPDATE INV_PACKSTOCKD SET  QTY=QTY-<QTY>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO> AND INV_CODE=<INV_CODE> AND INVSEQ_NO=<INVSEQ_NO>
updateQtyBySupReq.Debug=N


//����Ź����������,ɾ����������ϸ�е�һ��������
deleteOnceUserInvBySupReq.Type=TSQL
deleteOnceUserInvBySupReq.SQL=DELETE FROM INV_PACKSTOCKD &
	    	              WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO> AND INV_CODE=<INV_CODE> AND INVSEQ_NO=<INVSEQ_NO>
deleteOnceUserInvBySupReq.Debug=N


//ɾ��ȫ����ϸ
deleteAll.Type=TSQL
deleteAll.SQL=DELETE FROM INV_PACKSTOCKD WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
deleteAll.Debug=N




//����������GYSUsed
insertInv.Type=TSQL
insertInv.SQL=INSERT INTO INV_PACKSTOCKD &
                          (ORG_CODE,PACK_CODE,PACK_SEQ_NO,INV_CODE,INVSEQ_NO,BATCH_SEQ,&
                           DESCRIPTION,RECOUNT_TIME,COST_PRICE,QTY,STOCK_UNIT,&
                           ONCE_USE_FLG,OPT_USER,OPT_DATE,OPT_TERM,BARCODE)&
                    VALUES (<ORG_CODE>,<PACK_CODE>,<PACK_SEQ_NO>,<INV_CODE>,<INVSEQ_NO>,<BATCH_SEQ>,&
                           <DESCRIPTION>,<RECOUNT_TIME>,<COST_PRICE>,<QTY>,<STOCK_UNIT>,&
                           <ONCE_USE_FLG>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<BARCODE>)
insertInv.Debug=N



//���¿����GYSUsed
updateQty.Type=TSQL
updateQty.SQL=UPDATE INV_PACKSTOCKD SET QTY=<QTY>+QTY, OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> 
updateQty.item=PACK_CODE;PACK_SEQ_NO;INV_CODE
updateQty.PACK_CODE=PACK_CODE=<PACK_CODE>
updateQty.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>
updateQty.INV_CODE=INV_CODE=<INV_CODE>
updateQty.Debug=Y



