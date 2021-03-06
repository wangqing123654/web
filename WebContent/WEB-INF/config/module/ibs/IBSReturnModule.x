# 
#  Title:住院补充计价退费module
# 
#  Description:住院补充计价退费module
# 
#  Copyright: Copyright (c) Javahis 2014
# 
#  author wangl 2014.05.12
#  version 1.0
Module.item=selFeeDetail;selMergeFee

//查询费用明细(费用查询)
selFeeDetail.Type=TSQL
selFeeDetail.SQL=SELECT 'N'AS FLG,D.REXP_CODE,D.ORDER_CODE,F.ORDER_DESC,F.SPECIFICATION DESCRIPTION,D.DOSAGE_QTY,D.DOSAGE_UNIT AS UNIT_CODE,&
		   D.OWN_PRICE,D.TOT_AMT,D.EXE_DEPT_CODE,D.EXE_DEPT_CODE AS EXEC_DEPT_CODE,D.BILL_DATE,F.CHARGE_HOSP_CODE,F.ORDERSET_FLG,D.CASE_NO_SEQ,D.ORDERSET_CODE,&
		   D.INDV_FLG,D.OWN_AMT,D.SEQ_NO,D.ORDERSET_GROUP_NO,D.OWN_RATE,D.INCLUDE_FLG,D.ORDER_CAT1_CODE,D.CAT1_TYPE &
		   FROM IBS_ORDD D, SYS_FEE F &
		   WHERE D.ORDER_CODE = F.ORDER_CODE &
		   //AND D.OWN_PRICE <> 0.00 &
		   AND (D.INDV_FLG IS NULL OR D.INDV_FLG = 'N') & 
		   AND D.BILL_DATE  BETWEEN TO_DATE (<S_DATE>, 'YYYYMMDDHH24MISS')   &
                   AND TO_DATE (<E_DATE>, 'YYYYMMDDHH24MISS')  &
		   ORDER BY D.REXP_CODE,D.ORDER_CODE
	      
selFeeDetail.item=CASE_NO;ORDER_CODE;COST_CENTER_CODE;REXP_CODE;DOSAGE_QTY_Y;DOSAGE_QTY_N
selFeeDetail.CASE_NO=D.CASE_NO=<CASE_NO>
selFeeDetail.ORDER_CODE=D.ORDER_CODE=<ORDER_CODE>
selFeeDetail.COST_CENTER_CODE=D.EXE_DEPT_CODE=<COST_CENTER_CODE>
selFeeDetail.REXP_CODE=D.REXP_CODE=<REXP_CODE>
selFeeDetail.DOSAGE_QTY_Y=D.DOSAGE_QTY>=0
selFeeDetail.DOSAGE_QTY_N=D.DOSAGE_QTY<0
selFeeDetail.Debug=N

selMergeFee.Type=TSQL
selMergeFee.SQL= SELECT A.FLG,A.REXP_CODE,A.ORDER_CODE,A.ORDER_DESC,A.DESCRIPTION,A.DOSAGE_QTY,A.UNIT_CODE,A.OWN_PRICE,A.TOT_AMT,A,RETURN_SUM,&
                   A.CHARGE_HOSP_CODE,A.ORDERSET_FLG FROM &
                   (SELECT 'N'AS FLG,D.REXP_CODE,D.ORDER_CODE,F.ORDER_DESC,&
	           F.SPECIFICATION DESCRIPTION,SUM(D.DOSAGE_QTY) AS DOSAGE_QTY,D.DOSAGE_UNIT AS UNIT_CODE, &
		   D.OWN_PRICE,SUM(D.TOT_AMT) AS TOT_AMT,'0' AS RETURN_SUM,F.CHARGE_HOSP_CODE,ORDERSET_FLG &
		   FROM IBS_ORDD D, SYS_FEE F &
		   WHERE D.ORDER_CODE = F.ORDER_CODE &
		   AND (D.INDV_FLG IS NULL OR D.INDV_FLG = 'N') AND D.CAT1_TYPE NOT IN('LIS','RIS') AND D.ORDER_CODE<>D.ORDERSET_CODE & 
		   AND D.BILL_DATE  BETWEEN TO_DATE (<S_DATE>, 'YYYYMMDDHH24MISS') &
                   AND TO_DATE (<E_DATE>, 'YYYYMMDDHH24MISS')  &
                   GROUP BY D.REXP_CODE,D.ORDER_CODE ,D.DOSAGE_UNIT,D.OWN_PRICE,F.ORDER_DESC,F.SPECIFICATION,F.CHARGE_HOSP_CODE,ORDERSET_FLG &
                   UNION ALL
                   SELECT 'N'AS FLG,D.REXP_CODE,D.ORDER_CODE,F.ORDER_DESC,F.SPECIFICATION DESCRIPTION,A.DOSAGE_QTY, D.DOSAGE_UNIT AS UNIT_CODE, &
		      A.TOT_AMT AS OWN_PRICE,'0' AS RETURN_SUM,F.CHARGE_HOSP_CODE,ORDERSET_FLG ,A.DOSAGE_QTY*A.TOT_AMT &
		      FROM IBS_ORDD D, SYS_FEE F ,(&
		      SELECT COUNT(CASE_NO_SEQ) DOSAGE_QTY ,TOT_AMT,OWN_AMT ,ORDERSET_CODE FROM ( &
		      SELECT CASE_NO_SEQ,ORDERSET_CODE,SUM(TOT_AMT) TOT_AMT,SUM(OWN_AMT) OWN_AMT,ORDERSET_GROUP_NO FROM IBS_ORDD WHERE &
		       BILL_DATE  BETWEEN TO_DATE (<S_DATE>, 'YYYYMMDDHH24MISS') &
                         AND TO_DATE (<E_DATE>, 'YYYYMMDDHH24MISS') AND INDV_FLG='Y' &
		   	     GROUP BY CASE_NO_SEQ,ORDERSET_CODE,ORDERSET_GROUP_NO) GROUP BY TOT_AMT,OWN_AMT ,ORDERSET_CODE) A &
		      WHERE D.ORDER_CODE = F.ORDER_CODE &
		      AND (D.INDV_FLG IS NULL OR D.INDV_FLG = 'N')  &
		      AND D. BILL_DATE  BETWEEN TO_DATE (<S_DATE>, 'YYYYMMDDHH24MISS') &
                         AND TO_DATE (<E_DATE>, 'YYYYMMDDHH24MISS')   AND D.ORDER_CODE=A.ORDERSET_CODE &
		   	   GROUP BY D.REXP_CODE,D.ORDER_CODE ,D.DOSAGE_UNIT,D.OWN_PRICE,F.ORDER_DESC,F.SPECIFICATION,&
		   	   F.CHARGE_HOSP_CODE,ORDERSET_FLG ,A.TOT_AMT,A.DOSAGE_QTY ) A &
		   ORDER BY A.REXP_CODE,A.ORDER_CODE 
selMergeFee.item=CASE_NO;ORDER_CODE;COST_CENTER_CODE;REXP_CODE
selMergeFee.CASE_NO=D.CASE_NO=<CASE_NO>
selMergeFee.ORDER_CODE=D.ORDER_CODE=<ORDER_CODE>
selMergeFee.COST_CENTER_CODE=D.EXE_DEPT_CODE=<COST_CENTER_CODE>
selMergeFee.REXP_CODE=D.REXP_CODE=<REXP_CODE>
selMergeFee.Debug=N
