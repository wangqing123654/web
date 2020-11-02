   #
   # Title:�������ϸ��
   #
   # Description:���������
   #
   # Copyright: JavaHis (c) 2009 
   #
   # @author zhangy 2009/05/04

Module.item=createDispenseD;queryDispenseDOut;queryDispenseDOI;queryDispenseDIn;queryDispenseD;deleteDispenseMOutD


//�½����ⵥ��ϸ  
createDispenseD.Type=TSQL
createDispenseD.SQL=INSERT INTO INV_DISPENSED( &
			DISPENSE_NO , SEQ_NO , BATCH_SEQ, INV_CODE , INVSEQ_NO ,&
			SEQMAN_FLG , QTY , DISPENSE_UNIT, COST_PRICE , REQUEST_SEQ ,&
			BATCH_NO, VALID_DATE, DISPOSAL_FLG, &
			OPT_USER , OPT_DATE , OPT_TERM, IO_FLG) &
	    	      VALUES( &
	    	   	<DISPENSE_NO>, <SEQ_NO> , <BATCH_SEQ> , <INV_CODE> , <INVSEQ_NO> , &
	    	   	<SEQMAN_FLG> ,  <QTY> , <DISPENSE_UNIT> , <COST_PRICE> , <REQUEST_SEQ> , &
	    	   	<BATCH_NO>, <VALID_DATE>, <DISPOSAL_FLG>, &
	    	   	<OPT_USER> , <OPT_DATE> , <OPT_TERM>, <IO_FLG> )
createDispenseD.Debug=N


 
//��ѯ��Ҫ����ĳ��ⵥϸ��
queryDispenseDOut.Type=TSQL 
queryDispenseDOut.SQL=SELECT 'Y' AS SELECT_FLG, C.INV_CHN_DESC, A.INVSEQ_NO, C.DESCRIPTION, &
	                   A.QTY, E.QTY AS REQUEST_QTY, E.ACTUAL_QTY, &
	                   C.DISPENSE_UNIT, &
	                   C.COST_PRICE , A.QTY &
	                   * C.COST_PRICE AS SUM_AMT, &
	                   A.BATCH_SEQ, A.BATCH_NO, A.VALID_DATE, C.MAN_CODE, A.DISPOSAL_FLG, & 
	                   A.INV_CODE, E.SEQ_NO AS REQUEST_SEQ, C.SEQMAN_FLG, D.DISPENSE_QTY, D.STOCK_QTY &
			  FROM INV_DISPENSED A, INV_DISPENSEM B, INV_BASE C, INV_TRANSUNIT D, INV_REQUESTD E &
			 WHERE A.DISPENSE_NO = B.DISPENSE_NO &
			   AND A.INV_CODE = C.INV_CODE &
			   AND A.INV_CODE = D.INV_CODE &
			   AND A.REQUEST_SEQ = E.SEQ_NO & 
			   AND B.REQUEST_NO = E.REQUEST_NO &
			   AND C.INV_CODE = D.INV_CODE &
			   AND C.INV_CODE = E.INV_CODE &
			   AND D.INV_CODE = E.INV_CODE &
                           AND A.IO_FLG = B.IO_FLG  &
                           ORDER BY A.DISPENSE_NO  
queryDispenseDOut.ITEM=DISPENSE_NO;FINA_FLG;IO_FLG
queryDispenseDOut.DISPENSE_NO=A.DISPENSE_NO=<DISPENSE_NO>
queryDispenseDOut.FINA_FLG=B.FINA_FLG=<FINA_FLG>
queryDispenseDOut.IO_FLG=B.IO_FLG=<IO_FLG>        
queryDispenseDOut.Debug=N


	


//��ѯ��Ҫ���ĳ��ⵥϸ��  
queryDispenseDOI.Type=TSQL
queryDispenseDOI.SQL=SELECT 'Y' AS SELECT_FLG, C.INV_CHN_DESC, A.INVSEQ_NO, C.DESCRIPTION, &
	                   A.QTY, E.QTY AS REQUEST_QTY, E.ACTUAL_QTY, &
	                   C.DISPENSE_UNIT, &
	                   C.COST_PRICE , A.QTY &
	                   * C.COST_PRICE AS SUM_AMT, &
	                   A.BATCH_SEQ, A.BATCH_NO, A.VALID_DATE, C.MAN_CODE, A.DISPOSAL_FLG, & 
	                   A.INV_CODE, E.SEQ_NO AS REQUEST_SEQ, C.SEQMAN_FLG, D.DISPENSE_QTY, D.STOCK_QTY &
			  FROM INV_DISPENSED A, INV_DISPENSEM B, INV_BASE C, INV_TRANSUNIT D, INV_REQUESTD E &
			 WHERE A.DISPENSE_NO = B.DISPENSE_NO &
			   AND A.INV_CODE = C.INV_CODE &
			   AND A.INV_CODE = D.INV_CODE &
			   AND A.REQUEST_SEQ = E.SEQ_NO &
			   AND B.REQUEST_NO = E.REQUEST_NO &
			   AND C.INV_CODE = D.INV_CODE &
			   AND C.INV_CODE = E.INV_CODE &
			   AND D.INV_CODE = E.INV_CODE &
                           AND A.IO_FLG = B.IO_FLG &   
                           AND B.FINA_FLG = '0' &        
                           AND A.IO_FLG = '2'  &
                           ORDER BY A.DISPENSE_NO
queryDispenseDOI.ITEM=DISPENSE_NO
queryDispenseDOI.DISPENSE_NO=A.DISPENSE_NO=<DISPENSE_NO>
queryDispenseDOI.Debug=N




//��ѯ���������ⵥϸ��     
queryDispenseDIn.Type=TSQL
queryDispenseDIn.SQL=SELECT 'Y' AS SELECT_FLG, C.INV_CHN_DESC, A.INVSEQ_NO, C.DESCRIPTION, &
	                   A.QTY, E.QTY AS REQUEST_QTY, E.ACTUAL_QTY, &
	                   C.DISPENSE_UNIT, &
	                   C.COST_PRICE , A.QTY &
	                   * C.COST_PRICE AS SUM_AMT, & 
	                   A.BATCH_SEQ, A.BATCH_NO, A.VALID_DATE, C.MAN_CODE, A.DISPOSAL_FLG, & 
	                   A.INV_CODE, E.SEQ_NO AS REQUEST_SEQ, C.SEQMAN_FLG, D.DISPENSE_QTY, D.STOCK_QTY &
			  FROM INV_DISPENSED A, INV_DISPENSEM B, INV_BASE C, INV_TRANSUNIT D, INV_REQUESTD E &
			 WHERE A.DISPENSE_NO = B.DISPENSE_NO &
			   AND A.INV_CODE = C.INV_CODE &
			   AND A.INV_CODE = D.INV_CODE &
			   AND A.REQUEST_SEQ = E.SEQ_NO &
			   AND B.REQUEST_NO = E.REQUEST_NO &  
			   AND C.INV_CODE = D.INV_CODE &
			   AND C.INV_CODE = E.INV_CODE &
			   AND D.INV_CODE = E.INV_CODE &
                           AND A.IO_FLG = B.IO_FLG &  
                           AND A.IO_FLG = '2' &
                           ORDER BY A.DISPENSE_NO        
queryDispenseDIn.ITEM=DISPENSE_NO 
queryDispenseDIn.DISPENSE_NO=A.DISPENSE_NO=<DISPENSE_NO>
queryDispenseDIn.Debug=N

  
 

//��ѯ���ⵥϸ��  
queryDispenseD.Type=TSQL         
queryDispenseD.SQL=SELECT 'Y' AS SELECT_FLG, A.DISPENSE_NO,A.SEQ_NO,A.BATCH_SEQ,A.INV_CODE,A.INVSEQ_NO,  &
                   A.SEQMAN_FLG,A.QTY,A.DISPENSE_UNIT,A.COST_PRICE,A.REQUEST_SEQ,  & 
                   A.BATCH_NO,A.VALID_DATE,B.INV_CHN_DESC, &     
                   B.DESCRIPTION,B.SEQMAN_FLG,B.VALIDATE_FLG,B.INVKIND_CODE &     
		   FROM INV_DISPENSED A, INV_BASE B &  
		   WHERE A.INV_CODE = B.INV_CODE &
                   ORDER BY A.DISPENSE_NO           
queryDispenseD.ITEM=DISPENSE_NO;IO_FLG   
queryDispenseD.DISPENSE_NO=A.DISPENSE_NO=<DISPENSE_NO>
queryDispenseD.IO_FLG=A.IO_FLG=<IO_FLG>    
queryDispenseD.Debug=Y       
                    

//ɾ�����ⵥ����;��
deleteDispenseMOutD.Type=TSQL
deleteDispenseMOutD.SQL=DELETE * FROM INV_DISPENSED  &    
		    WHERE DISPENSE_NO=<DISPENSE_NO>    
deleteDispenseMOutD.Debug=N  
       
       











