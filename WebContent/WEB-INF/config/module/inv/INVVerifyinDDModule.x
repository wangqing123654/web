   #
   # Title:������Ź���ϸ������
   #
   # Description:������Ź���ϸ������
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author lit 2013/05/04

Module.item=createVerifyinDD;queryVerifyinDD;updateOrginCode;onUpdateValData;onUpdateValDataByStockDD


//��ѯ������Ź���ϸ������
queryVerifyinDD.Type=TSQL
queryVerifyinDD.SQL=SELECT 'Y' as FLG,A.VERIFYIN_NO, A.SEQ_NO, A.DDSEQ_NO,A.RFID,A.ORGIN_CODE , &
   			   A.INV_CODE, A.INVSEQ_NO, A.BATCH_SEQ, &
   			   A.BATCH_NO, A.VALID_DATE, A.UNIT_PRICE, &
   			   B.INV_CHN_DESC, B.DESCRIPTION, B.STOCK_UNIT &
  		      FROM INV_VERIFYINDD A, INV_BASE B &
 		      WHERE A.INV_CODE = B.INV_CODE
queryVerifyinDD.ITEM=VERIFYIN_NO;SEQ_NO
queryVerifyinDD.VERIFYIN_NO=A.VERIFYIN_NO=<VERIFYIN_NO> 
queryVerifyinDD.SEQ_NO=A.SEQ_NO=<SEQ_NO>
queryVerifyinDD.Debug=N


//�½�������Ź���ϸ������
createVerifyinDD.Type=TSQL
createVerifyinDD.SQL=INSERT INTO INV_VERIFYINDD( &
			VERIFYIN_NO , SEQ_NO , DDSEQ_NO, INV_CODE , INVSEQ_NO ,&
			BATCH_SEQ , BATCH_NO , VALID_DATE, STOCK_UNIT , UNIT_PRICE ,&
			OPT_USER , OPT_DATE , OPT_TERM,RFID) &
	    	      VALUES( &
	    	   	<VERIFYIN_NO> ,  <SEQ_NO> , <DDSEQ_NO> , <INV_CODE> , <INVSEQ_NO> , &
	    	   	<BATCH_SEQ> ,  <BATCH_NO> , <VALID_DATE> , <STOCK_UNIT> , <UNIT_PRICE> , &
	    	   	<OPT_USER> , <OPT_DATE> , <OPT_TERM> , <RFID> )
createVerifyinDD.Debug=N

//����
updateOrginCode.Type=TSQL
updateOrginCode.SQL=UPDATE INV_VERIFYINDD SET &
			ORGIN_CODE=<ORGIN_CODE> & 
	    	       WHERE RFID =<RFID> 
updateOrginCode.Debug=Y


//����Ч��
onUpdateValData.Type=TSQL
onUpdateValData.SQL=UPDATE INV_VERIFYINDD SET &
			VALID_DATE=<VALID_DATE> & 
	    	       WHERE RFID =<RFID> 
onUpdateValData.Debug=Y


//����Ч��
onUpdateValDataByStockDD.Type=TSQL
onUpdateValDataByStockDD.SQL=UPDATE INV_STOCKDD SET &
			VALID_DATE=<VALID_DATE> & 
	    	       WHERE RFID =<RFID> 
onUpdateValDataByStockDD.Debug=Y















