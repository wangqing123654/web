   #
   # Title: �����Ź���ϸ��
   #
   # Description: �����Ź���ϸ��
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04    

Module.item=insertInvStockDD;queryStockDD;queryOutStockDD;updateQtyOutIn;updateQtyOut;updateStockQtyByPack;updateStockQtyByReq;updateQtyCheck;updateOrginCode;updateQtyOutInGYS;updatePackAge



//���������Ź���ϸ��
insertInvStockDD.Type=TSQL
insertInvStockDD.SQL=INSERT INTO INV_STOCKDD(&
			   INV_CODE, INVSEQ_NO, REGION_CODE, BATCH_SEQ, ORG_CODE, &
			   BATCH_NO, VALID_DATE, STOCK_QTY, UNIT_PRICE, STOCK_UNIT, &
			   CHECKTOLOSE_FLG, WAST_FLG, VERIFYIN_DATE, PACK_FLG, OPT_USER, &
			   OPT_DATE, OPT_TERM,CABINET_ID,ACTIVE_FLG,RFID) &
	             VALUES( &
			   <INV_CODE>, <INVSEQ_NO>, <REGION_CODE>, <BATCH_SEQ>, <ORG_CODE>, &
			   <BATCH_NO>, <VALID_DATE>, <STOCK_QTY>, <UNIT_PRICE>, <STOCK_UNIT>, &
			   <CHECKTOLOSE_FLG>, <WAST_FLG>, <VERIFYIN_DATE>, <PACK_FLG>, <OPT_USER>, &
			   <OPT_DATE>, <OPT_TERM>,<CABINET_ID>,<ACTIVE_FLG>,<RFID>)
insertInvStockDD.Debug=N
 
 
//��ѯ�����ϸ
queryStockDD.Type=TSQL
queryStockDD.SQL=SELECT INV_CODE, INVSEQ_NO, REGION_CODE, BATCH_SEQ, ORG_CODE, &
		        BATCH_NO, VALID_DATE, STOCK_QTY, UNIT_PRICE, STOCK_UNIT, &
		        CHECKTOLOSE_FLG, WAST_FLG, VERIFYIN_DATE, OUT_DATE, OUT_USER, & 
		        MR_NO, CASE_NO, RX_SEQ, SEQ_NO, ADM_TYPE, &
		        WAIT_ORG_CODE, PACK_CODE, PACK_SEQ_NO, PACK_FLG,CABINET_ID,ACTIVE_FLG,RFID  &
		   FROM INV_STOCKDD &
		  WHERE INV_CODE = <INV_CODE>   
queryStockDD.ITEM=ORG_CODE  
queryStockDD.ORG_CODE=ORG_CODE=<ORG_CODE>
queryStockDD.Debug=N      
  

//��ѯ�����ϸ(����(RFID))  
queryOutStockDD.Type=TSQL
queryOutStockDD.SQL=SELECT 'Y' AS SELECT_FLG ,A.INV_CODE, A.INVSEQ_NO, A.REGION_CODE, A.BATCH_SEQ, A.ORG_CODE, &
		        A.BATCH_NO, A.VALID_DATE, A.STOCK_QTY, A.UNIT_PRICE, A.STOCK_UNIT, &
		        A.CHECKTOLOSE_FLG, A.WAST_FLG, A.VERIFYIN_DATE, A.OUT_DATE, A.OUT_USER, & 
		        A.MR_NO, A.CASE_NO, A.RX_SEQ, A.SEQ_NO, A.ADM_TYPE, &
		        A.WAIT_ORG_CODE, A.PACK_CODE, A.PACK_SEQ_NO, A.PACK_FLG, &
                        A.RFID,A.CABINET_ID,A.ACTIVE_FLG,B.INV_CHN_DESC,B.COST_PRICE,  & 
                        B.DESCRIPTION,B.DISPENSE_UNIT &  
		   FROM INV_STOCKDD A,INV_BASE B &
		  WHERE A.INV_CODE = B.INV_CODE &
                        AND A.ACTIVE_FLG IS NULL   
queryOutStockDD.ITEM=ORG_CODE;INV_CODE  
queryOutStockDD.ORG_CODE=A.ORG_CODE=<ORG_CODE>
queryOutStockDD.INV_CODE=A.INV_CODE=<INV_CODE>
queryOutStockDD.Debug=N      

 
  

//�����Ź�������(���⼴���)       
updateQtyOutIn.Type=TSQL 
updateQtyOutIn.SQL=UPDATE INV_STOCKDD SET &   
			CABINET_ID=<CABINET_ID>,ORG_CODE = <ORG_CODE>,WAIT_ORG_CODE = <WAIT_ORG_CODE>,  &  
			OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE RFID=<RFID>   
updateQtyOutIn.Debug=N



//����      
updateOrginCode.Type=TSQL 
updateOrginCode.SQL=UPDATE INV_STOCKDD SET &   
			ORGIN_CODE=<ORGIN_CODE>  &  
	    	      WHERE RFID=<RFID>   
updateOrginCode.Debug=N


//�����Ź�������(�̵�)       
updateQtyCheck.Type=TSQL 
updateQtyCheck.SQL=UPDATE INV_STOCKDD SET &   
		   CABINET_ID=<CABINET_ID> &
	    	   WHERE RFID=<RFID>   
updateQtyCheck.Debug=N  


//�����Ź�������(������;)
updateQtyOut.Type=TSQL
updateQtyOut.SQL=UPDATE INV_STOCKDD SET &
			ORG_CODE=<ORG_CODE>, WAST_FLG=<WAST_FLG>, STOCK_QTY=<STOCK_QTY>, WAIT_ORG_CODE=<WAIT_ORG_CODE>, &
			OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND INVSEQ_NO=<INVSEQ_NO>
updateQtyOut.Debug=Y                         



//���������������Ź���ϸ��Ŀ����
updateStockQtyByPack.Type=TSQL
updateStockQtyByPack.SQL=UPDATE INV_STOCKDD SET &
				PACK_CODE=<PACK_CODE>, PACK_SEQ_NO=<PACK_SEQ_NO>, PACK_FLG=<PACK_FLG>, &
				OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	   	   WHERE INV_CODE =<INV_CODE> AND INVSEQ_NO=<INVSEQ_NO>
updateStockQtyByPack.Debug=N 


//��Ӧ�ҳ�����ҵ���¿����(������ҵ)
updateStockQtyByReq.Type=TSQL
updateStockQtyByReq.SQL=UPDATE INV_STOCKDD SET &
			       ORG_CODE=<ORG_CODE>, OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	         WHERE INV_CODE =<INV_CODE> AND INVSEQ_NO=<INVSEQ_NO>
updateStockQtyByReq.Debug=N

//�����Ź�������(���⼴���)
updateQtyOutInGYS.Type=TSQL
updateQtyOutInGYS.SQL=UPDATE INV_STOCKDD SET &
			ORG_CODE=<ORG_CODE>, WAST_FLG=<WAST_FLG>, STOCK_QTY=<STOCK_QTY>, &
			OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND INVSEQ_NO=<INVSEQ_NO>
updateQtyOutInGYS.Debug=N

//��������������GYSUsed
updatePackAge.Type=TSQL
updatePackAge.SQL=UPDATE INV_STOCKDD &
                  SET PACK_CODE=<PACK_CODE>,PACK_SEQ_NO=<PACK_SEQ_NO>,PACK_FLG=<PACK_FLG>
updatePackAge.item=INV_CODE;INVSEQ_NO;
updatePackAge.INV_CODE=INV_CODE=<INV_CODE>
updatePackAge.INVSEQ_NO=INVSEQ_NO=<INVSEQ_NO>
updatePackAge.Debug=N












