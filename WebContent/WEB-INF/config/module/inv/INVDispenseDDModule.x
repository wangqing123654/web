   #
   # Title:�������ϸ��
   #
   # Description:���������
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04
 
Module.item=createDispenseDD;queryDispenseDD;deleteDispenseDDOut

 
//�½����ⵥ��ϸ
createDispenseDD.Type=TSQL 
createDispenseDD.SQL=INSERT INTO INV_DISPENSEDD( &
			DISPENSE_NO,SEQ_NO,BATCH_SEQ,INV_CODE,INVSEQ_NO, &
			DISPENSE_UNIT,COST_PRICE,REQUEST_SEQ,BATCH_NO, &
			VALID_DATE ,DISPOSAL_FLG,ORGIN_CODE,RFID,CABINET_ID, &
			OPT_USER , OPT_DATE , OPT_TERM , IO_FLG) &
	    	      VALUES( &  
	    	   	<DISPENSE_NO>, <SEQ_NO> , <BATCH_SEQ> , <INV_CODE> , <INVSEQ_NO> , &
	    	   	<DISPENSE_UNIT> ,  <COST_PRICE> , <REQUEST_SEQ> , <BATCH_NO> ,  &
	    	   	<VALID_DATE>, <DISPOSAL_FLG>, <ORGIN_CODE>, <RFID>,<CABINET_ID>, &
	    	   	<OPT_USER> , <OPT_DATE> , <OPT_TERM> , <IO_FLG>) 
createDispenseDD.Debug=Y     

   
    
//��ѯ��Ҫ��������ϸ��DD��         
queryDispenseDD.Type=TSQL 
queryDispenseDD.SQL=SELECT  'Y' AS SELECT_FLG,A.DISPENSE_NO,A.SEQ_NO,A.BATCH_SEQ,A.INV_CODE,A.INVSEQ_NO, &  
                            A.DISPENSE_UNIT,A.COST_PRICE,A.REQUEST_SEQ,A.BATCH_NO, &
                            A.VALID_DATE,A.DISPOSAL_FLG,A.ORGIN_CODE,A.RFID,A.CABINET_ID,  &
                            B.INV_CHN_DESC,B.DESCRIPTION  &
                         FROM  INV_DISPENSEDD A,INV_BASE B,INV_DISPENSEM C &
			 WHERE A.INV_CODE = B.INV_CODE   &
                           AND A.DISPENSE_NO = C.DISPENSE_NO  
queryDispenseDD.ITEM=DISPENSE_NO;IO_FLG;FINA_FLG   
queryDispenseDD.DISPENSE_NO=A.DISPENSE_NO=<DISPENSE_NO>  
queryDispenseDD.IO_FLG=A.IO_FLG=<IO_FLG> 
queryDispenseDD.FINA_FLG=C.FINA_FLG=<FINA_FLG>
queryDispenseDD.Debug=N   


//ɾ�����ⵥ����;��
deleteDispenseDDOut.Type=TSQL
deleteDispenseDDOut.SQL=DELETE * FROM INV_DISPENSEM  &    
		    WHERE DISPENSE_NO=<DISPENSE_NO>    
deleteDispenseDDOut.Debug=N  
   


	



       
       











