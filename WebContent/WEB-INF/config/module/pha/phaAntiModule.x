# 
#  Title:抗菌药品module
# 
#  Description:抗菌药品module
# 
#  Copyright: Copyright (c) Javahis 2013
# 
#  author pangben 2013.09.09
#  version 4.0
#
Module.item=insertPhaAnti;queryPhaAnti;queryPhaAntiStatus;updatePhaAntiUseFlg;updatePhaAntiCheckUseFlg;updatePhaAntiApproveFlg

//查询数据
queryPhaAnti.Type=TSQL
queryPhaAnti.SQL=SELECT A.ANTI_MAX_DAYS AS ANTI_TAKE_DAYS, 'N' AS FLG,B.DESCRIPTION,A.CASE_NO, A.PHA_SEQ, A.SEQ_NO, &
  			 A.MR_NO, A.ORDER_CODE, A.ORDER_DESC,A.LINKMAIN_FLG,A.LINK_NO, & 
  		 	 A.SPECIFICATION,  MEDI_UNIT, A.MEDI_QTY,& 
   			  FREQ_CODE, A.ORDER_DATE, A.NODE_FLG, &
  		         A.APPROVE_FLG, A.USE_FLG, A.OPT_USER, &
  		         A.OPT_DATE, A.OPT_TERM,  ROUTE_CODE, &
			 '' AS LINKMAIN_FLG,'' AS LINK_NO,A.RX_KIND,A.INFLUTION_RATE,B.PY1,B.PY2, &
			 B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, &
			 B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, &
			 B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, &
			 B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE,& 
			 B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, &
			 B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, &
			 B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE  &
                  FROM PHA_ANTI A,SYS_FEE B  &
		  WHERE A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y'   
queryPhaAnti.item=CASE_NO;PHA_SEQ;NODE_FLG;USE_FLG;APPROVE_FLG
queryPhaAnti.CASE_NO=A.CASE_NO=<CASE_NO>
queryPhaAnti.PHA_SEQ=A.PHA_SEQ=<PHA_SEQ>
queryPhaAnti.NODE_FLG=A.NODE_FLG=<NODE_FLG>
queryPhaAnti.APPROVE_FLG=A.APPROVE_FLG=<APPROVE_FLG>
queryPhaAnti.USE_FLG=A.USE_FLG=<USE_FLG>
queryPhaAnti.Debug=N

//添加数据
insertPhaAnti.Type=TSQL
insertPhaAnti.SQL=INSERT INTO PHA_ANTI(ANTI_REASON,ANTI_MAX_DAYS,CASE_NO, PHA_SEQ, SEQ_NO, &
  			MR_NO, ORDER_CODE, ORDER_DESC,LINKMAIN_FLG,LINK_NO,& 
  		 	SPECIFICATION, MEDI_UNIT, MEDI_QTY,& 
   			FREQ_CODE, ORDER_DATE, NODE_FLG, &
  		        APPROVE_FLG, USE_FLG, OPT_USER, &
  		        OPT_DATE, OPT_TERM, ROUTE_CODE,OVERRIDE_FLG,CHECK_FLG,RX_KIND,INFLUTION_RATE) &
  		        VALUES(<ANTI_REASON>,<ANTI_MAX_DAYS>,<CASE_NO>,<PHA_SEQ>,<SEQ_NO>, &
  			<MR_NO>,<ORDER_CODE>,<ORDER_DESC>,<LINKMAIN_FLG>,<LINK_NO>,& 
  		 	<SPECIFICATION>,<MEDI_UNIT>,<MEDI_QTY>,& 
   			<FREQ_CODE>,<ORDER_DATE>,<NODE_FLG>, &
  		        <APPROVE_FLG>,<USE_FLG>,<OPT_USER>, &
  		        SYSDATE,<OPT_TERM>,<ROUTE_CODE>,<OVERRIDE_FLG>,<CHECK_FLG>,<RX_KIND>,<INFLUTION_RATE>)
insertPhaAnti.Debug=N

//条件查询数据
queryPhaAntiStatus.Type=TSQL
queryPhaAntiStatus.SQL=SELECT A.CASE_NO, A.PHA_SEQ, A.SEQ_NO, &
  			A.MR_NO, A.ORDER_CODE, A.ORDER_DESC,& 
  		 	A.SPECIFICATION, A.MEDI_UNIT, A.MEDI_QTY,& 
   			A.FREQ_CODE, A.ORDER_DATE, A.NODE_FLG, &
  		        A.APPROVE_FLG, A.USE_FLG, A.OPT_USER, &
  		        A.OPT_DATE, A.OPT_TERM, A.ROUTE_CODE,A.RX_KIND FROM PHA_ANTI A,PHA_BASE B 
  		        WHERE A.ORDER_CODE=B.ORDER_CODE 
queryPhaAntiStatus.item=CASE_NO;APPROVE_FLG;USE_FLG;PHA_SEQ
queryPhaAntiStatus.CASE_NO=A.CASE_NO=<CASE_NO>
queryPhaAntiStatus.PHA_SEQ=A.PHA_SEQ=<PHA_SEQ>
queryPhaAntiStatus.APPROVE_FLG=A.APPROVE_FLG=<APPROVE_FLG>
queryPhaAntiStatus.USE_FLG=A.USE_FLG=<USE_FLG>
queryPhaAntiStatus.Debug=N

//修改医生使用注记
updatePhaAntiUseFlg.Type=TSQL
updatePhaAntiUseFlg.SQL=UPDATE PHA_ANTI SET USE_FLG=<USE_FLG> ,OPT_DATE=SYSDATE,OPT_USER=<OPT_USER>,OPT_TERM=<OPT_TERM> &
                        WHERE  CASE_NO=<CASE_NO> AND PHA_SEQ=<PHA_SEQ> AND SEQ_NO=<SEQ_NO>
updatePhaAntiUseFlg.Debug=N

//修改审批标记和医生使用注记
updatePhaAntiCheckUseFlg.Type=TSQL
updatePhaAntiCheckUseFlg.SQL=UPDATE PHA_ANTI SET CHECK_FLG = <CHECK_FLG>, USE_FLG=<USE_FLG> ,OPT_DATE=SYSDATE,OPT_USER=<OPT_USER>,OPT_TERM=<OPT_TERM> &
                        WHERE  CASE_NO=<CASE_NO> AND PHA_SEQ=<PHA_SEQ> AND SEQ_NO=<SEQ_NO>
updatePhaAntiCheckUseFlg.Debug=N

//修改会诊注记
updatePhaAntiApproveFlg.Type=TSQL
updatePhaAntiApproveFlg.SQL=UPDATE PHA_ANTI SET APPROVE_FLG=<APPROVE_FLG> ,OPT_DATE=SYSDATE,OPT_USER=<OPT_USER>,OPT_TERM=<OPT_TERM> &
                        WHERE  CASE_NO=<CASE_NO> AND PHA_SEQ=<PHA_SEQ> AND SEQ_NO=<SEQ_NO>
updatePhaAntiApproveFlg.Debug=N