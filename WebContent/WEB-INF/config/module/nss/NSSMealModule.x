 #
   # Title: �ʹ����õ�
   #
   # Description:�ʹ����õ�
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009.05.08

Module.item=queryNSSMeal;insertNSSMeal;updateNSSMeal;deleteNSSMeal


//��ѯ�ʹ����õ�
queryNSSMeal.Type=TSQL
queryNSSMeal.SQL=SELECT MEAL_CODE, MEAL_CHN_DESC, MEAL_ENG_DESC, PY1, PY2, SEQ, &
             		DESCRIPTION, MEAL_TIME, STOP_ORDER_TIME, OPT_USER, OPT_DATE, OPT_TERM &
             	   FROM NSS_MEAL ORDER BY MEAL_CODE
queryNSSMeal.item=MEAL_CODE
queryNSSMeal.MEAL_CODE=MEAL_CODE=<MEAL_CODE>
queryNSSMeal.Debug=N


//�����ʹ����õ�
insertNSSMeal.Type=TSQL
insertNSSMeal.SQL=INSERT INTO NSS_MEAL &
            		  (MEAL_CODE, MEAL_CHN_DESC, MEAL_ENG_DESC, PY1, PY2, SEQ, &
             		   DESCRIPTION, MEAL_TIME, STOP_ORDER_TIME, OPT_USER, OPT_DATE, OPT_TERM) &
     		   VALUES (<MEAL_CODE>, <MEAL_CHN_DESC>, <MEAL_ENG_DESC>, <PY1>, <PY2>, <SEQ>, &
             		   <DESCRIPTION>, <MEAL_TIME>, <STOP_ORDER_TIME>, <OPT_USER>, SYSDATE, <OPT_TERM>)
insertNSSMeal.Debug=N


//���²ʹ����õ�
updateNSSMeal.Type=TSQL
updateNSSMeal.SQL=UPDATE  NSS_MEAL SET &
			  MEAL_CHN_DESC=<MEAL_CHN_DESC>,MEAL_ENG_DESC=<MEAL_ENG_DESC>,PY1=<PY1>,PY2=<PY2>, &
			  SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,MEAL_TIME=<MEAL_TIME>, &
			  STOP_ORDER_TIME=<STOP_ORDER_TIME>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
	            WHERE MEAL_CODE=<MEAL_CODE> 
updateNSSMeal.Debug=N


//ɾ���ʹ����õ�
deleteNSSMeal.Type=TSQL
deleteNSSMeal.SQL=DELETE FROM NSS_MEAL WHERE MEAL_CODE=<MEAL_CODE> 
deleteNSSMeal.Debug=N


