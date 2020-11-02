   #
   # Title:�˻���������
   #
   # Description:�˻���������
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/21

Module.item=queryRegressgoodsM;createRegressgoodsM;updateRegressgoodsM;deleteRegressgoodsM;getQueryRegressgoods;getQueryRegressgoodsCheck

//��ѯ�˻�����
queryRegressgoodsM.Type=TSQL
queryRegressgoodsM.SQL=SELECT REGRESSGOODS_NO, REGRESSGOODS_DATE, REGRESSGOODS_USER, ORG_CODE, SUP_CODE, &
			      CHECK_USER, CHECK_DATE, DESCRIPTION, REASON_CHN_DESC, BILL_DATE, &
			      BILLPRINT_FLG, OPT_USER, OPT_DATE, OPT_TERM &
		         FROM IND_REGRESSGOODSM ORDER BY REGRESSGOODS_NO
queryRegressgoodsM.ITEM=ORG_CODE;SUP_CODE;REGRESSGOODS_NO;START_DATE;END_DATE;REGION_CODE
queryRegressgoodsM.ORG_CODE=ORG_CODE=<ORG_CODE>
queryRegressgoodsM.SUP_CODE=SUP_CODE=<SUP_CODE>
queryRegressgoodsM.REGRESSGOODS_NO=REGRESSGOODS_NO=<REGRESSGOODS_NO>
queryRegressgoodsM.REGION_CODE=REGION_CODE=<REGION_CODE>
queryRegressgoodsM.START_DATE=REGRESSGOODS_DATE>=<START_DATE>
queryRegressgoodsM.END_DATE=REGRESSGOODS_DATE<=<END_DATE>
queryRegressgoodsM.Debug=N


//�½��˻�����
createRegressgoodsM.Type=TSQL
createRegressgoodsM.SQL=INSERT INTO IND_REGRESSGOODSM( &
				REGRESSGOODS_NO , REGRESSGOODS_DATE , REGRESSGOODS_USER , ORG_CODE , SUP_CODE , &
				CHECK_USER , CHECK_DATE , DESCRIPTION , REASON_CHN_DESC , OPT_USER , &
				OPT_DATE , OPT_TERM, REGION_CODE) &
	    	   	VALUES( &
	    	   		<REGRESSGOODS_NO> ,  <REGRESSGOODS_DATE> , <REGRESSGOODS_USER> , <ORG_CODE> , <SUP_CODE> , &
	    	   		<CHECK_USER> , <CHECK_DATE> , <DESCRIPTION> , <REASON_CHN_DESC> , <OPT_USER> , &
	    	   		<OPT_DATE> , <OPT_TERM>, <REGION_CODE>)
createRegressgoodsM.Debug=N


//�����˻�����
updateRegressgoodsM.Type=TSQL
updateRegressgoodsM.SQL=UPDATE IND_REGRESSGOODSM SET &
		      	       ORG_CODE=<ORG_CODE>, &
		      	       SUP_CODE=<SUP_CODE>, &
		      	       CHECK_USER=<CHECK_USER>, &
		      	       CHECK_DATE=<CHECK_DATE>, &
		      	       DESCRIPTION=<DESCRIPTION>, &
		      	       REASON_CHN_DESC=<REASON_CHN_DESC>, &
		      	       OPT_USER=<OPT_USER>, &
		      	       OPT_DATE=<OPT_DATE>, &
		      	       OPT_TERM=<OPT_TERM> &
		   	 WHERE REGRESSGOODS_NO=<REGRESSGOODS_NO>
updateRegressgoodsM.Debug=N


//ɾ���˻�����
deleteRegressgoodsM.Type=TSQL
deleteRegressgoodsM.SQL=DELETE FROM IND_REGRESSGOODSM WHERE REGRESSGOODS_NO=<REGRESSGOODS_NO>
deleteRegressgoodsM.Debug=N


//ҩƷ�˻�����ͳ��(δ����)
getQueryRegressgoods.Type=TSQL
getQueryRegressgoods.SQL=SELECT A.REGRESSGOODS_NO, F.SUP_ABS_DESC, E.REGRESSGOODS_DATE, &
         		        CASE WHEN (B.GOODS_DESC IS NULL) THEN B.ORDER_DESC &
            			ELSE B.ORDER_DESC || ' (' || B.GOODS_DESC || ')' END AS ORDER_DESC, &
         			B.SPECIFICATION, C.UNIT_CHN_DESC, A.QTY - A.ACTUAL_QTY AS QTY, &
         			A.UNIT_PRICE, A.UNIT_PRICE * (A.QTY - A.ACTUAL_QTY) AS REG_AMT, &
         			A.RETAIL_PRICE, A.RETAIL_PRICE * (A.QTY - A.ACTUAL_QTY) AS OWN_AMT, &
           			A.RETAIL_PRICE * (A.QTY - A.ACTUAL_QTY) &
         			- A.UNIT_PRICE * (A.QTY - A.ACTUAL_QTY) AS DIFF_ATM, &
         			A.INVOICE_NO, A.INVOICE_DATE, A.BATCH_NO, A.VALID_DATE &
    			   FROM IND_REGRESSGOODSD A, SYS_FEE B, SYS_UNIT C, IND_VERIFYIND D, IND_REGRESSGOODSM E, SYS_SUPPLIER F &
   		           WHERE A.ORDER_CODE = B.ORDER_CODE &
     		             AND A.BILL_UNIT = C.UNIT_CODE &
     			     AND A.VERIFYIN_NO = D.VERIFYIN_NO &
     			     AND A.ORDER_CODE = D.ORDER_CODE &
     			     AND A.REGRESSGOODS_NO = E.REGRESSGOODS_NO &
     			     AND E.SUP_CODE = F.SUP_CODE &
     			     AND A.UPDATE_FLG = '0' &
     			     AND E.REGRESSGOODS_DATE BETWEEN <START_DATE> AND <END_DATE> &
     			     AND E.ORG_CODE = <ORG_CODE> &
			   ORDER BY E.SUP_CODE
getQueryRegressgoods.ITEM=SUP_CODE;ORDER_CODE
getQueryRegressgoods.SUP_CODE=E.SUP_CODE=<SUP_CODE>
getQueryRegressgoods.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
getQueryRegressgoods.Debug=N


//ҩƷ�˻�����ͳ��(�ѳ���)
getQueryRegressgoodsCheck.Type=TSQL
getQueryRegressgoodsCheck.SQL=SELECT A.REGRESSGOODS_NO, F.SUP_ABS_DESC, E.CHECK_DATE AS REGRESSGOODS_DATE, &
         		        CASE WHEN (B.GOODS_DESC IS NULL) THEN B.ORDER_DESC &
            			ELSE B.ORDER_DESC || ' (' || B.GOODS_DESC || ')' END AS ORDER_DESC, &
         			B.SPECIFICATION, C.UNIT_CHN_DESC, A.QTY - A.ACTUAL_QTY AS QTY, &
         			A.UNIT_PRICE, A.UNIT_PRICE * (A.QTY - A.ACTUAL_QTY) AS REG_AMT, &
         			A.RETAIL_PRICE, A.RETAIL_PRICE * (A.QTY - A.ACTUAL_QTY) AS OWN_AMT, &
           			A.RETAIL_PRICE * (A.QTY - A.ACTUAL_QTY) &
         			- A.UNIT_PRICE * (A.QTY - A.ACTUAL_QTY) AS DIFF_ATM, &
         			A.INVOICE_NO, A.INVOICE_DATE, A.BATCH_NO, A.VALID_DATE &
    			   FROM IND_REGRESSGOODSD A, SYS_FEE B, SYS_UNIT C, IND_VERIFYIND D, IND_REGRESSGOODSM E, SYS_SUPPLIER F &
   		           WHERE A.ORDER_CODE = B.ORDER_CODE &
     		             AND A.BILL_UNIT = C.UNIT_CODE &
     			     AND A.VERIFYIN_NO = D.VERIFYIN_NO &
     			     AND A.ORDER_CODE = D.ORDER_CODE &
     			     AND A.REGRESSGOODS_NO = E.REGRESSGOODS_NO &
     			     AND E.SUP_CODE = F.SUP_CODE &
     			     AND A.UPDATE_FLG IN ('1', '3') &
     			     AND E.CHECK_DATE BETWEEN <START_DATE> AND <END_DATE> &
     			     AND E.ORG_CODE = <ORG_CODE> &
			   ORDER BY E.SUP_CODE
getQueryRegressgoodsCheck.ITEM=SUP_CODE;ORDER_CODE
getQueryRegressgoodsCheck.SUP_CODE=E.SUP_CODE=<SUP_CODE>
getQueryRegressgoodsCheck.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
getQueryRegressgoodsCheck.Debug=N







