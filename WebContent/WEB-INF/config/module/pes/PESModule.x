# 
#  Title:PESmodule
# 
#  Description:PESmodule
# 
#  Copyright: Copyright (c) Bluecore 2012
# 
#  author zhangp 2012.10.10
#  version 1.0
#
Module.item=selectRxNo;insertPESOPDM;selectRxNoD;insertPESOPDD;insertPESRESULT;selectOPDM;selectOPDD;updatePESOPDM;updatePESOPDD;updatePESResult;selectOPDMforPrint;selectPESResult;selectZD

//查询待选列表
selectRxNo.Type=TSQL
selectRxNo.SQL=  SELECT DISTINCT (A.RX_NO||A.PRESRT_NO) RX_NO,C.MR_NO,C.PAT_NAME,A.CASE_NO,B.ADM_DATE,'N' FLG,A.DEPT_CODE,A.DR_CODE &
               			FROM OPD_ORDER A, REG_PATADM B,SYS_PATINFO C ,PHA_BASE D &
          		WHERE A.CASE_NO = B.CASE_NO &
            			AND A.RX_TYPE = 1 &
            			AND A.MR_NO = C.MR_NO &
            			AND A.ORDER_CODE = D.ORDER_CODE &
            			AND B.ADM_DATE BETWEEN TO_DATE (<START_DATE>, &
                                            'YYYYMMDDHH24MISS' &
                                           ) &
                               AND TO_DATE (<END_DATE>, &
                                            'YYYYMMDDHH24MISS' &
                                           ) &
                                         
                ORDER BY CASE_NO       
selectRxNo.item=DEPT_CODE;DR_CODE;ANTIBIOTIC_CODE;ADM_TYPE;PES_TYPE
selectRxNo.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectRxNo.DR_CODE=A.DR_CODE=<DR_CODE>
selectRxNo.ANTIBIOTIC_CODE=D.ANTIBIOTIC_CODE=<ANTIBIOTIC_CODE>
selectRxNo.PES_TYPE=D.ANTIBIOTIC_CODE IS NOT NULL
selectRxNo.ADM_TYPE_O=A.ADM_TYPE='O'
selectRxNo.ADM_TYPE_E=A.ADM_TYPE='E'
selectRxNo.ADM_TYPE=A.ADM_TYPE IN ('O','E')
selectRxNo.Debug=Y


//写入pes主档
insertPESOPDM.Type=TSQL
insertPESOPDM.SQL=INSERT INTO PES_OPDM &
   (TYPE_CODE, PES_NO, CASE_NO, SEQ, EVAL_CODE, &
   MR_NO, PAT_NAME, ORDER_DATE, AGE, DIAG, ORDER_QTY, &
   ANTIBIOTIC_QTY, INJECT_QTY, BASE_QTY, GOODS_QTY, &
   RX_TOTAL, DR_CODE, PHA_DOSAGE_CODE, PHA_DISPENSE_CODE, &
   OPT_USER, OPT_DATE, OPT_TERM ,PES_RX_NO ,RX_NO,DIAG_NOTE,WEIGHT,SEX_CODE) &
 Values &
   (<TYPE_CODE>, <PES_NO>, <CASE_NO>, <SEQ>, <EVAL_CODE>, &
    <MR_NO>, <PAT_NAME>, <ORDER_DATE>, <AGE>, <DIAG>, <ORDER_QTY>, &
    <ANTIBIOTIC_QTY>, <INJECT_QTY>, <BASE_QTY>, <GOODS_QTY>, &
    <RX_TOTAL>, <DR_CODE>, <PHA_DOSAGE_CODE>, <PHA_DISPENSE_CODE>, &
    <OPT_USER>, <OPT_DATE>, <OPT_TERM>, <PES_RX_NO>, <RX_NO>,<DIAG_NOTE>,<WEIGHT>,<SEX_CODE>)
insertPESOPDM.Debug=N


//查询待插入数据
selectRxNoD.Type=TSQL
selectRxNoD.SQL=SELECT A.CASE_NO, A.RX_NO||A.PRESRT_NO RX_NO, A.SEQ_NO, A.PRESRT_NO, A.REGION_CODE, A.MR_NO, &
       				A.ADM_TYPE, A.RX_TYPE, A.TEMPORARY_FLG, A.RELEASE_FLG, A.LINKMAIN_FLG, &
       				A.ORDER_CODE, A.ORDER_DESC, A.SPECIFICATION, A.ORDER_CAT1_CODE, &
       				A.MEDI_QTY, A.MEDI_UNIT, A.FREQ_CODE, A.ROUTE_CODE, A.TAKE_DAYS, &
       				A.DOSAGE_QTY, A.DOSAGE_UNIT, A.DISPENSE_QTY, A.DISPENSE_UNIT, &
       				A.GIVEBOX_FLG, A.OWN_PRICE, A.NHI_PRICE, A.DISCOUNT_RATE, A.OWN_AMT, &
       				A.AR_AMT, A.DR_CODE, A.ORDER_DATE, A.DEPT_CODE, A.EXEC_DEPT_CODE, &
       				A.EXEC_DR_CODE, A.SETMAIN_FLG, A.ORDERSET_GROUP_NO, A.HIDE_FLG, &
       				A.FILE_NO, A.URGENT_FLG, A.INSPAY_TYPE, A.PHA_TYPE, A.DOSE_TYPE, &
       				A.EXPENSIVE_FLG, A.PRINTTYPEFLG_INFANT, A.PRESCRIPT_NO, A.ATC_FLG, &
       				A.RECEIPT_NO, A.BILL_FLG, A.BILL_DATE, A.BILL_USER, A.PRINT_FLG, &
       				A.REXP_CODE, A.HEXP_CODE, A.CTZ1_CODE, A.CTZ2_CODE, A.PHA_CHECK_CODE, &
       				A.PHA_CHECK_DATE, A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, &
       				A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE, A.DCT_TAKE_QTY, &
       				A.PACKAGE_TOT, A.DCTAGENT_FLG, A.CAT1_TYPE, A.TRADE_ENG_DESC, &
       				A.PRINT_NO, A.COUNTER_NO, A.EXEC_FLG, A.RECEIPT_FLG, A.BILL_TYPE, &
       				A.FINAL_TYPE, A.COST_AMT, A.COST_CENTER_CODE, A.BATCH_SEQ1, &
       				A.VERIFYIN_PRICE1, A.DISPENSE_QTY1, A.BATCH_SEQ2, A.VERIFYIN_PRICE2, &
       				A.DISPENSE_QTY2, A.BATCH_SEQ3, A.VERIFYIN_PRICE3, A.DISPENSE_QTY3, &
       				A.BUSINESS_NO, A.GOODS_DESC, A.LINK_NO, C.ICD_CODE,C.DIAG_NOTE, B.ANTIBIOTIC_CODE, B.CTRLDRUGCLASS_CODE,E.SYS_GRUG_CLASS, &
       				CASE &
          				WHEN (   B.CTRLDRUGCLASS_CODE = '01' &
                				OR B.CTRLDRUGCLASS_CODE = '02' &
                				OR B.CTRLDRUGCLASS_CODE = '05' &
              			 	) &
             				THEN '1' &
          				WHEN B.CTRLDRUGCLASS_CODE = '03' &
             				THEN '2' &
             			ELSE '' &
       				END PHSY_FLG ,D.DOSE_TYPE &
  				FROM OPD_ORDER A, PHA_BASE B ,OPD_DIAGREC C ,PHA_DOSE D,SYS_FEE E &
 				WHERE A.ORDER_CODE = B.ORDER_CODE &
 					AND A.RX_TYPE = 1 &
 					AND A.CASE_NO = C.CASE_NO  &
   					AND C.MAIN_DIAG_FLG = 'Y' &
   					AND B.DOSE_CODE = D.DOSE_CODE &
					AND A.ORDER_CODE = E.ORDER_CODE &
					ORDER BY A.CASE_NO,A.RX_NO,A.PRESRT_NO
selectRxNoD.item=CASE_NO
selectRxNoD.CASE_NO=A.CASE_NO=<CASE_NO>
selectRxNoD.Debug=N


//写入pes明细
insertPESOPDD.Type=TSQL
insertPESOPDD.SQL=INSERT INTO PES_OPDD &
            (PES_NO, CASE_NO, RX_NO, SEQ_NO, REGION_CODE, MR_NO, ADM_TYPE, PHSY_FLG, &
             RX_TYPE, RELEASE_FLG, LINKMAIN_FLG, LINK_NO, ORDER_CODE, &
             ORDER_DESC, GOODS_DESC, SPECIFICATION, ORDER_CAT1_CODE, &
             CAT1_TYPE, MEDI_QTY, MEDI_UNIT, FREQ_CODE, ROUTE_CODE, &
             TAKE_DAYS, DOSAGE_QTY, DOSAGE_UNIT, DISPENSE_QTY, DISPENSE_UNIT, &
             GIVEBOX_FLG, DISPENSE_FLG, OWN_PRICE, NHI_PRICE, DISCOUNT_RATE, &
             OWN_AMT, AR_AMT, DR_NOTE, DR_CODE, &
             ORDER_DATE, DEPT_CODE, &
             EXEC_DEPT_CODE, EXEC_DR_CODE, INSPAY_TYPE, PHA_TYPE, DOSE_TYPE, &
             EXPENSIVE_FLG, PRINTTYPEFLG_INFANT, CTRLDRUGCLASS_CODE, &
             PRESRT_NO, PRESCRIPT_NO, RECEIPT_NO, CTZ1_CODE, PHA_CHECK_CODE, &
             PHA_CHECK_DATE, PHA_DOSAGE_CODE, &
             PHA_DOSAGE_DATE, PHA_DISPENSE_CODE, &
             PHA_DISPENSE_DATE, PHA_RETN_CODE, &
             PHA_RETN_DATE, DCTEXCEP_CODE, &
             DCT_TAKE_QTY, PACKAGE_TOT, &
             OPT_USER, OPT_DATE, &
             OPT_TERM, PES_RX_NO &
            ) &
     VALUES (<PES_NO>, <CASE_NO>, <RX_NO>, <SEQ_NO>, <REGION_CODE>, <MR_NO>, <ADM_TYPE>, <PHSY_FLG>, &
             <RX_TYPE>, <RELEASE_FLG>, <LINKMAIN_FLG>, <LINK_NO>, <ORDER_CODE>, &
             <ORDER_DESC>, <GOODS_DESC>, <SPECIFICATION>, <ORDER_CAT1_CODE>, &
             <CAT1_TYPE>, <MEDI_QTY>, <MEDI_UNIT>, <FREQ_CODE>, <ROUTE_CODE>, &
             <TAKE_DAYS>, <DOSAGE_QTY>, <DOSAGE_UNIT>, <DISPENSE_QTY>, <DISPENSE_UNIT>, &
             <GIVEBOX_FLG>, <DISPENSE_FLG>, <OWN_PRICE>, <NHI_PRICE>, <DISCOUNT_RATE>, &
             <OWN_AMT>, <AR_AMT>, <DR_NOTE>, <DR_CODE>, &
             <ORDER_DATE>, <DEPT_CODE>, &
             <EXEC_DEPT_CODE>, <EXEC_DR_CODE>, <INSPAY_TYPE>, <PHA_TYPE>, <DOSE_TYPE>, &
             <EXPENSIVE_FLG>, <PRINTTYPEFLG_INFANT>, <CTRLDRUGCLASS_CODE>, &
             <PRESRT_NO>, <PRESCRIPT_NO>, <RECEIPT_NO>, <CTZ1_CODE>, <PHA_CHECK_CODE>, &
             <PHA_CHECK_DATE>, <PHA_DOSAGE_CODE>, &
             <PHA_DOSAGE_DATE>, <PHA_DISPENSE_CODE>, &
             <PHA_DISPENSE_DATE>, <PHA_RETN_CODE>, &
             <PHA_RETN_DATE>, <DCTEXCEP_CODE>, &
             <DCT_TAKE_QTY>, <PACKAGE_TOT>, &
             <OPT_USER>, <OPT_DATE>, &
             <OPT_TERM>, <PES_RX_NO> &
            )
insertPESOPDD.Debug=N


//写入pes明细
insertPESRESULT.Type=TSQL
insertPESRESULT.SQL=INSERT INTO PES_RESULT &
            (TYPE_CODE, PES_NO, EVAL_CODE, &
             PES_DATE, OPT_USER, &
             OPT_DATE, OPT_TERM &
            ) &
     VALUES (<TYPE_CODE>, <PES_NO>, <EVAL_CODE>, &
             <PES_DATE>, <OPT_USER>, &
             <OPT_DATE>, <OPT_TERM> &
            )
insertPESRESULT.Debug=N


//查询OPDM  WHERE A.DIAG = B.ICD_CODE &   , SYS_DIAGNOSIS B  huangtt  A.DIAG ICD_CHN_DESC
selectOPDM.Type=TSQL
selectOPDM.SQL=SELECT A.TYPE_CODE, A.PES_NO, A.CASE_NO, A.SEQ, A.EVAL_CODE, A.MR_NO, A.PAT_NAME, &
       				A.ORDER_DATE, A.AGE, A.DIAG, A.ORDER_QTY, A.ANTIBIOTIC_QTY, A.INJECT_QTY, A.BASE_QTY, &
       				A.GOODS_QTY, A.RX_TOTAL, A.DR_CODE, A.PHA_DOSAGE_CODE, A.PHA_DISPENSE_CODE, &
       				A.REASON_FLG, A.QUESTION_CODE, A.REMARK ,A.OPT_USER, A.OPT_DATE, A.OPT_TERM, A.DIAG ICD_CHN_DESC, &
       				A.RX_NO, A.PES_RX_NO,A.DIAG_NOTE,A.WEIGHT,A.SEX_CODE &
  				FROM PES_OPDM A &
  				
  					ORDER BY SEQ
selectOPDM.item=TYPE_CODE;PES_NO
selectOPDM.TYPE_CODE=TYPE_CODE=<TYPE_CODE>
selectOPDM.PES_NO=PES_NO=<PES_NO>
selectOPDM.Debug=N


//查询OPDD
selectOPDD.Type=TSQL
selectOPDD.SQL=SELECT   A.SEQ_NO, A.LINK_NO, A.ORDER_DESC || A.SPECIFICATION ORDER_DESC, &
         				A.MEDI_QTY || B.UNIT_CHN_DESC MEDI_QTY, A.ROUTE_CODE, A.FREQ_CODE, A.TAKE_DAYS, &
         				A.DISPENSE_QTY || C.UNIT_CHN_DESC DOSAGE_QTY, A.AR_AMT, A.REASON_FLG, &
         				A.QUESTION_CODE, A.REMARK, A.CASE_NO, A.RX_NO, A.PES_RX_NO, A.PES_NO &
    			FROM PES_OPDD A, SYS_UNIT B, SYS_UNIT C &
    					WHERE A.MEDI_UNIT = B.UNIT_CODE &
    					AND A.DISPENSE_UNIT = C.UNIT_CODE &
				ORDER BY CASE_NO, RX_NO, SEQ_NO
selectOPDD.item=CASE_NO;PES_RX_NO;PES_NO
selectOPDD.CASE_NO=CASE_NO=<CASE_NO>
selectOPDD.PES_RX_NO=PES_RX_NO=<PES_RX_NO>
selectOPDD.PES_NO=PES_NO=<PES_NO>
selectOPDD.Debug=N


//更新OPDM
updatePESOPDM.Type=TSQL
updatePESOPDM.SQL=UPDATE PES_OPDM &
   					SET QUESTION_CODE = <QUESTION_CODE>, &
   						REASON_FLG = <REASON_FLG>, &
       					REMARK = <REMARK>, &
       					OPT_USER = <OPT_USER>, &
       					OPT_DATE = <OPT_DATE>, &
       					OPT_TERM = <OPT_TERM> &
 				WHERE TYPE_CODE = <TYPE_CODE> &
   						AND PES_NO = <PES_NO> &
   						AND CASE_NO = <CASE_NO> &
   						AND PES_RX_NO = <PES_RX_NO> &
   						AND SEQ = <SEQ>
updatePESOPDM.Debug=N


//更新OPDD
updatePESOPDD.Type=TSQL
updatePESOPDD.SQL=UPDATE PES_OPDD &
   					SET QUESTION_CODE = <QUESTION_CODE>, &
       					REASON_FLG = <REASON_FLG>, &
       					REMARK = <REMARK>, &
      					OPT_USER = <OPT_USER>, &
       					OPT_DATE = <OPT_DATE>, &
       					OPT_TERM = <OPT_TERM> &
 				WHERE SEQ_NO = <SEQ_NO> &
   						AND CASE_NO = <CASE_NO> &
   						AND PES_RX_NO = <PES_RX_NO> &
   						AND RX_NO = <RX_NO> &
   						AND PES_NO = <PES_NO>
updatePESOPDD.Debug=N


//更新PESResult
updatePESResult.Type=TSQL
updatePESResult.SQL=UPDATE PES_RESULT &
						SET PES_A = <PES_A>, &
							PES_B = <PES_B>, &
							PES_C = <PES_C>, &
							PES_D = <PES_D>, &
							PES_E = <PES_E>, &
							PES_F = <PES_F>, &
							PES_G = <PES_G>, &
							PES_H = <PES_H>, &
							PES_I = <PES_I>, &
							PES_J = <PES_J>, &
							PES_K = <PES_K>, &
							PES_L = <PES_L>, &
							PES_O = <PES_O>, &
							PES_P = <PES_P>, &
							OPT_USER = <OPT_USER>, &
							OPT_DATE = <OPT_DATE>, &
							OPT_TERM = <OPT_TERM> &
						WHERE TYPE_CODE = <TYPE_CODE> &
							AND PES_NO = <PES_NO>
updatePESResult.Debug=N


//查询OPDM  SYS_DIAGNOSIS B, &   A.DIAG = B.ICD_CODE &  AND   huangtt  A.DIAG ICD_CHN_DESC
selectOPDMforPrint.Type=TSQL
selectOPDMforPrint.SQL=SELECT A.SEQ, A.ORDER_DATE, A.AGE, A.DIAG ICD_CHN_DESC, A.ORDER_QTY, &
       						A.ANTIBIOTIC_QTY, A.INJECT_QTY, A.BASE_QTY, A.GOODS_QTY, A.RX_TOTAL, &
       						C.USER_NAME DR_CODE, D.USER_NAME PHA_DOSAGE_CODE, &
       						E.USER_NAME PHA_DISPENSE_CODE, &
       						CASE  &
       						WHEN A.REASON_FLG = 'Y' &
       						THEN '1' &
       						ELSE '0' &
       						END REASON_FLG, F.CHN_DESC QUESTION_CODE &
  						FROM PES_OPDM A, &
       						
       						SYS_OPERATOR C, &
       						SYS_OPERATOR D, &
       						SYS_OPERATOR E, &
       						(SELECT ID, CHN_DESC &
          						FROM SYS_DICTIONARY &
         						WHERE GROUP_ID = 'PES_QUESTION_CODE') F &
 						WHERE  A.DR_CODE = C.USER_ID &
   							AND A.PHA_DOSAGE_CODE = D.USER_ID &
   							AND A.PHA_DISPENSE_CODE = E.USER_ID &
   							AND A.QUESTION_CODE = F.ID(+) &
  						ORDER BY SEQ
selectOPDMforPrint.item=TYPE_CODE;PES_NO
selectOPDMforPrint.TYPE_CODE=TYPE_CODE=<TYPE_CODE>
selectOPDMforPrint.PES_NO=PES_NO=<PES_NO>
selectOPDMforPrint.Debug=N


//查询PESResult
selectPESResult.Type=TSQL
selectPESResult.SQL=SELECT A.TYPE_CODE, A.PES_NO, B.USER_NAME EVAL_CODE, A.PES_DATE, A.PES_A, A.PES_B, A.PES_C, A.PES_D, &
       						A.PES_E, A.PES_F, A.PES_G, A.PES_H, A.PES_I, A.PES_J, A.PES_K, A.PES_L, A.PES_O, A.PES_P, &
       						A.OPT_USER, A.OPT_DATE, A.OPT_TERM &
  						FROM PES_RESULT A, SYS_OPERATOR B &
  							WHERE A.EVAL_CODE = B.USER_ID
selectPESResult.item=TYPE_CODE;PES_NO
selectPESResult.TYPE_CODE=A.TYPE_CODE=<TYPE_CODE>
selectPESResult.PES_NO=A.PES_NO=<PES_NO>
selectPESResult.Debug=N

//查询主诊断以外的诊断
selectZD.Type=TSQL
selectZD.SQL=SELECT CASE_NO,ICD_CODE,MAIN_DIAG_FLG FROM OPD_DIAGREC WHERE  MAIN_DIAG_FLG = 'N' 
selectZD.item=CASE_NO
selectZD.CASE_NO=CASE_NO=<CASE_NO>
selectZD.Debug=N