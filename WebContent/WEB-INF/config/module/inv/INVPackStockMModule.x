   #
   # Title: �������������
   #
   # Description: �������������
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insertStockQtyByPack;updateStockQtyByPack;queryStockM;updateQtyBySupReq;updateStatusBySupReq;updateQtyAndStatus;delete;getStockSeq;checkPackCount;insertPack;updateQty;getPackDate

 
//���������������Ź���ϸ��Ŀ����
insertStockQtyByPack.Type=TSQL
insertStockQtyByPack.SQL=INSERT INTO INV_PACKSTOCKM(&
			   ORG_CODE, PACK_CODE, PACK_SEQ_NO, DESCRIPTION, QTY, &
			   USE_COST, ONCE_USE_COST, STATUS, OPT_USER, OPT_DATE, OPT_TERM) &
	                 VALUES( &
			   <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <DESCRIPTION>, <QTY>, &
			   <USE_COST>, <ONCE_USE_COST>, <STATUS>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertStockQtyByPack.Debug=N


//���������������Ź���ϸ��Ŀ����
updateStockQtyByPack.Type=TSQL
updateStockQtyByPack.SQL=UPDATE INV_PACKSTOCKM SET  QTY=QTY+<QTY>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updateStockQtyByPack.Debug=N


//��ѯ����������  GYSUsed
queryStockM.Type=TSQL
queryStockM.SQL=SELECT B.PACK_DESC, A.PACK_SEQ_NO, A.QTY, A.STATUS, B.USE_COST, A.ONCE_USE_COST, &
       		       A.DESCRIPTION, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, A.ORG_CODE, A.PACK_CODE &
  	          FROM INV_PACKSTOCKM A, INV_PACKM B &
  	         WHERE A.PACK_CODE = B.PACK_CODE
queryStockM.ITEM=ORG_CODE;PACK_CODE;PACK_SEQ_NO;STATUS
queryStockM.ORG_CODE=A.ORG_CODE=<ORG_CODE>
queryStockM.PACK_CODE=A.PACK_CODE=<PACK_CODE>
queryStockM.PACK_SEQ_NO=A.PACK_SEQ_NO=<PACK_SEQ_NO>
queryStockM.STATUS=A.STATUS=<STATUS>
queryStockM.Debug=N


//û����Ź����������,�۳��������������Ŀ����
updateQtyBySupReq.Type=TSQL
updateQtyBySupReq.SQL=UPDATE INV_PACKSTOCKM SET  QTY=QTY-<QTY>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updateQtyBySupReq.Debug=N


//����Ź����������,������������״̬Ϊ����
updateStatusBySupReq.Type=TSQL
updateStatusBySupReq.SQL=UPDATE INV_PACKSTOCKM SET STATUS=<STATUS>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updateStatusBySupReq.Debug=N


//û����Ź����������,�����������������Ŀ���������״̬
updateQtyAndStatus.Type=TSQL
updateQtyAndStatus.SQL=UPDATE INV_PACKSTOCKM SET QTY=QTY+<STOCK_QTY>, STATUS=<STATUS> , &
				DISINFECTION_DATE=<DISINFECTION>, VALUE_DATE=<VALUE_DATE>, DISINFECTION_USER=<DISINFECTION_USER>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updateQtyAndStatus.Debug=N


//ɾ��ȫ����ϸ
delete.Type=TSQL
delete.SQL=DELETE FROM INV_PACKSTOCKM WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
delete.Debug=N


//��ѯ���������GYSUsed
getStockSeq.Type=TSQL
getStockSeq.SQL=SELECT MAX(PACK_SEQ_NO) FROM INV_PACKSTOCKM 
getStockSeq.item=PACK_CODE
getStockSeq.PACK_CODE=PACK_CODE=<PACK_CODE>
getStockSeq.Debug=N



//�����������Ƿ����GYSUsed
checkPackCount.Type=TSQL
checkPackCount.SQL=SELECT PACK_CODE FROM INV_PACKSTOCKM 
checkPackCount.item=PACK_CODE
checkPackCount.PACK_CODE=PACK_CODE=<PACK_CODE>
checkPackCount.Debug=N


//�����µ�������GYSUsed
insertPack.Type=TSQL
insertPack.SQL=INSERT INTO INV_PACKSTOCKM &
                           (ORG_CODE,PACK_CODE,PACK_SEQ_NO,DESCRIPTION,QTY,DISINFECTION_DATE,&
                            VALUE_DATE,DISINFECTION_USER,USE_COST,ONCE_USE_COST,STATUS,&
                            OPT_USER,OPT_DATE,OPT_TERM,BARCODE)&
                     VALUES (<ORG_CODE>,<PACK_CODE>,<PACK_SEQ_NO>,<DESCRIPTION>,<QTY>,<DISINFECTION_DATE>,&
                            <VALUE_DATE>,<DISINFECTION_USER>,<USE_COST>,<ONCE_USE_COST>,<STATUS>,&
                            <OPT_USER>,<OPT_DATE>,<OPT_TERM>,<BARCODE>)
insertPack.Debug=N




//���¿������GYSUsed
updateQty.Type=TSQL
updateQty.SQL=UPDATE INV_PACKSTOCKM &
                    SET QTY=QTY+<QTY>
updateQty.item=PACK_CODE
updateQty.PACK_CODE=PACK_CODE=<PACK_CODE>
updateQty.Debug=N



//������������������Ч��GYSUsed
getPackDate.Type=TSQL
getPackDate.SQL=SELECT PM.DISINFECTION_DATE,PM.DISINFECTION_USER,PM.OPT_USER,PM.OPT_DATE,P.VALUE_DATE,PM.BARCODE FROM INV_PACKSTOCKM PM LEFT JOIN INV_PACKM P ON PM.PACK_CODE = P.PACK_CODE
getPackDate.item=PACK_CODE;PACK_SEQ_NO
getPackDate.PACK_CODE=PM.PACK_CODE=<PACK_CODE>
getPackDate.PACK_SEQ_NO=PM.PACK_SEQ_NO=<PACK_SEQ_NO>
getPackDate.Debug=N

