# 
#  Title:医嘱费用审核module
# 
#  Description:医嘱费用审核module
# 
#  Copyright: Copyright (c) Javahis 2013
# 
#  author caoyong 2013.10.24
#  version 1.0
#
Module.item=selectdatall;selecttotamt


//汇总
selectdatall.Type=TSQL
selectdatall.SQL=SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,SUM(TOT_AMT) AS TOT_AMT,ORDER_CODE,OWN_PRICE, &
                         DOSAGE_UNIT,ORDERSET_CODE,FREQ_CODE,CASE_NO FROM IBS_ORDD &
			 WHERE &
			 CASE_NO=<CASE_NO> AND &
			 BILL_DATE  BETWEEN TO_DATE (<S_DATE>, 'YYYYMMDDHH24MISS')   &
                         AND TO_DATE (<E_DATE>, 'YYYYMMDDHH24MISS')  &
			 AND ORDER_CODE=<ORDER_CODE> &
			 GROUP BY ORDER_CODE,OWN_PRICE,DOSAGE_UNIT,ORDERSET_CODE,FREQ_CODE,CASE_NO 
selectdatall.item=DEPT_CODE;FREQ_CODE
selectdatall.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
selectdatall.FREQ_CODE=FREQ_CODE=<FREQ_CODE>
selectdatall.Debug=N


//新增明细表
selecttotamt.Type=TSQL
selecttotamt.SQL=SELECT  SUM(TOT_AMT) AS TOT_AMT &
		         FROM IBS_ORDD  &
			 WHERE &
			 ORDERSET_CODE<>ORDER_CODE AND & 
			 CASE_NO=<CASE_NO> AND &
			 BILL_DATE  BETWEEN TO_DATE (<S_DATE>, 'YYYYMMDDHH24MISS') &
			 AND TO_DATE (<E_DATE>, 'YYYYMMDDHH24MISS') &
			 AND ORDERSET_CODE=<ORDERSET_CODE> 
			
selecttotamt.item=DEPT_CODE
selecttotamt.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
selecttotamt.Debug=N




