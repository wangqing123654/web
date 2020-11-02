   #
   # Title:������ϸ
   #
   # Description:������ϸ
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=queryPurOrderD;updatePlanDVer;queryPurOrderQTY;createPurorderD

//��ѯ������ϸ
queryPurOrderD.Type=TSQL
queryPurOrderD.SQL=SELECT PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, &
			  BILL_UNIT, PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, &
			  OPT_USER, OPT_DATE, OPT_TERM &
		     FROM IND_PURORDERD
queryPurOrderD.ITEM=PURORDER_NO;SEQ_NO;ORDER_CODE
queryPurOrderD.PURORDER_NO=PURORDER_NO=<PURORDER_NO>
queryPurOrderD.SEQ_NO=SEQ_NO=<SEQ_NO>
queryPurOrderD.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
queryPurOrderD.Debug=N


//�ۼ����������ۼ�Ʒ�ʿۿ����
updatePlanDVer.Type=TSQL
updatePlanDVer.SQL=UPDATE IND_PURORDERD &
		     SET UPDATE_FLG=<UPDATE_FLG>, &
			 ACTUAL_QTY=ACTUAL_QTY+<VERIFYIN_QTY>, &
			 QUALITY_DEDUCT_AMT=QUALITY_DEDUCT_AMT+<QUALITY_DEDUCT_AMT> &
		     WHERE PURORDER_NO=<PURORDER_NO> &
		       AND SEQ_NO=<SEQ_NO>
updatePlanDVer.Debug=N


//��ѯ�ۼ�������
queryPurOrderQTY.Type=TSQL
queryPurOrderQTY.SQL=SELECT SUM(A.PURORDER_QTY) AS PURORDER_QTY &
		       FROM IND_PURORDERD A , IND_PURORDERM B &
		      WHERE A.PURORDER_NO = B.PURORDER_NO &
		        AND A.ORDER_CODE = <ORDER_CODE> &
		        AND B.PLAN_NO = <PLAN_NO>
queryPurOrderQTY.Debug=N

//��������ϸ��
createPurorderD.Type=TSQL
createPurorderD.SQL=INSERT INTO IND_PURORDERD( &
		      PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, &
		      BILL_UNIT, PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, &
		      OPT_USER, OPT_DATE, OPT_TERM) &
		    VALUES( &
		      <PURORDER_NO>, <SEQ_NO>, <ORDER_CODE>, <PURORDER_QTY>, <GIFT_QTY>, &
		      <BILL_UNIT>, <PURORDER_PRICE>, <ACTUAL_QTY>, <QUALITY_DEDUCT_AMT>, <UPDATE_FLG>, &
		      <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
createPurorderD.Debug=N



