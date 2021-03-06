#
# Title:静点报到
#
# Description:静点报到
#
# Copyright: JavaHis (c) 2009
#
# @author wangl 2009/10/271

Module.item=queryRegister;queryOrderDetail;onQueryoldOrder;queryOrderDetailBed


//根据条件查询静点室报到人员
queryRegister.Type=TSQL
queryRegister.SQL=SELECT DISTINCT A.ADM_TYPE, A.CASE_NO, A.QUE_NO, A.ADM_DATE, A.APPT_CODE, &
                         A.ARRIVE_FLG, A.SESSION_CODE, D.SESSION_DESC, A.DR_CODE, &
                         B.USER_NAME AS DR_NAME, A.REALDR_CODE, C.USER_NAME AS REALDR_NAME, A.DEPT_CODE, &
			 E.DEPT_CHN_DESC, A.CLINICROOM_NO, F.CLINICROOM_DESC, &
			 A.SEE_DR_FLG,  A.CTZ1_CODE, A.CTZ2_CODE, &
			 A.CTZ3_CODE, A.VISIT_CODE, A.CONTRACT_CODE, &
                         A.MR_NO,G.PAT_NAME,G.SEX_CODE, G.BIRTH_DATE,K.BED_DESC &
	           FROM  REG_PATADM A, &
			 SYS_OPERATOR B, &
			 SYS_OPERATOR C, &
			 REG_SESSION D, &
			 SYS_DEPT E, &
			 REG_CLINICROOM F, &
			 SYS_PATINFO G, &
			 OPD_ORDER H, &
			 PHL_REGION I, &
			 PHL_BED K, &
			 SYS_PHAROUTE L &
	           WHERE A.DR_CODE = B.USER_ID &
		     AND A.REALDR_CODE = C.USER_ID &
		     AND A.ADM_TYPE = D.ADM_TYPE &
		     AND A.SESSION_CODE = D.SESSION_CODE &
		     AND A.DEPT_CODE = E.DEPT_CODE &
		     AND A.ADM_TYPE = F.ADM_TYPE &
		     AND A.CLINICROOM_NO = F.CLINICROOM_NO &
		     AND A.MR_NO = G.MR_NO &
		     AND A.ADM_TYPE = H.ADM_TYPE &
		     AND A.CASE_NO = H.CASE_NO &
		     AND H.ROUTE_CODE = L.ROUTE_CODE &
		     AND A.REGCAN_USER IS  NULL &
		     //AND A.ADM_STATUS IN ('3','4') &
		     AND A.ADM_DATE = <ADM_DATE> &
		     AND L.CLASSIFY_TYPE = 'F' &
                    // AND H.PHA_RETN_CODE IS NULL &
                     AND F.PHL_REGION_CODE = I.REGION_CODE(+) &
		     AND A.CASE_NO=K.CASE_NO(+) &
		     AND A.MR_NO=K.MR_NO(+) 
queryRegister.ITEM=MR_NO;ADM_TYPE_O;ADM_TYPE_E;ADM_TYPE;SESSION_CODE;DEPT_CODE;CLINICROOM_NO;DR_CODE;PHL_REGION_CODE;REGION_CODE
queryRegister.MR_NO=A.MR_NO=<MR_NO>
queryRegister.REGION_CODE=A.REGION_CODE=<REGION_CODE>
queryRegister.ADM_TYPE_O=A.ADM_TYPE='O'
queryRegister.ADM_TYPE_E=A.ADM_TYPE='E'
queryRegister.ADM_TYPE=A.ADM_TYPE IN ('O','E')
queryRegister.SESSION_CODE=A.SESSION_CODE=<SESSION_CODE>
queryRegister.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
queryRegister.CLINICROOM_NO=A.CLINICROOM_NO=<CLINICROOM_NO>
queryRegister.DR_CODE=A.DR_CODE=<DR_CODE>
queryRegister.PHL_REGION_CODE=I.REGION_CODE=<PHL_REGION_CODE>
queryRegister.Debug=N



//根据条件查询静点室区域 
//==liling 20140703将A.DISPENSE_QTY||F.UNIT_CHN_DESC AS DISPENSE_QTY 改为  A.MEDI_QTY || ' ' ||F.UNIT_CHN_DESC AS DISPENSE_QTY
//A.DISPENSE_UNIT = F.UNIT_CODE 改为  A.MEDI_UNIT = F.UNIT_CODE  与门诊输液单一致
queryOrderDetail.Type=TSQL
queryOrderDetail.SQL=SELECT 'N' AS REPORT_FLG,'Y' AS SELECT_FLG, A.LINKMAIN_FLG, A.LINK_NO, B.ORDER_DESC, B.SPECIFICATION, A.DR_NOTE, &
                            C.DOSE_CHN_DESC, TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH:MM:SS') AS ORDER_DATE, D.USER_NAME AS DR_NAME, &
                            TO_CHAR(A.PHA_DISPENSE_DATE,'YYYY/MM/DD HH:MM:SS') AS PHA_DISPENSE_DATE , E.USER_NAME AS PHA_DISPENSE_NAME, &
                            A.RX_NO, A.DR_CODE,A.ROUTE_CODE,A.FREQ_CODE,N.ROUTE_CHN_DESC AS ROUTE_CODEA, M.FREQ_CHN_DESC AS FREQ_CODEA, A.TAKE_DAYS,A.PHA_DISPENSE_CODE,A.PHA_RETN_CODE, &
                            A.NS_NOTE,A.ORDER_CODE,  TO_CHAR (A.MEDI_QTY, 'fm9999990.09999')||F.UNIT_CHN_DESC AS DISPENSE_QTY , G.ROUTE_CHN_DESC, A.SEQ_NO,A.EXEC_FLG, &
			    H.USER_NAME AS PHA_DOSAGE_CODE, K.USER_NAME AS PHA_CHECK_CODE,A.INFLUTION_RATE &
	              FROM  OPD_ORDER A, PHA_BASE B, PHA_DOSE C, SYS_OPERATOR D, SYS_OPERATOR E, &
		      SYS_UNIT F, SYS_PHAROUTE G,SYS_OPERATOR H,SYS_OPERATOR K ,SYS_PHAFREQ M,SYS_PHAROUTE N &
	           WHERE A.ORDER_CODE = B.ORDER_CODE &
		     AND B.DOSE_CODE = C.DOSE_CODE &
		     AND A.DR_CODE = D.USER_ID  &
		     AND A.PHA_DOSAGE_CODE=H.USER_ID (+) &
		     AND A.PHA_CHECK_CODE=K.USER_ID (+) &
		     AND A.MEDI_UNIT = F.UNIT_CODE &
    	             AND A.ROUTE_CODE = G.ROUTE_CODE &
		     AND A.PHA_DISPENSE_CODE = E.USER_ID (+) &
		     //AND A.PHA_DISPENSE_CODE IS NOT NULL &
		     AND A.DC_DR_CODE IS NULL &
		     AND N.CLASSIFY_TYPE = 'F' &
		     AND A.ROUTE_CODE=N.ROUTE_CODE &
		     AND A.FREQ_CODE=M.FREQ_CODE &
		     ORDER BY A.RX_NO,A.LINK_NO ,A.SEQ_NO
queryOrderDetail.ITEM=MR_NO;CASE_NO;ADM_TYPE
queryOrderDetail.MR_NO=A.MR_NO=<MR_NO>
queryOrderDetail.CASE_NO=A.CASE_NO=<CASE_NO>
queryOrderDetail.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
queryOrderDetail.Debug=N


//add caoyong 已报道的医嘱
onQueryoldOrder.Type=TSQL
onQueryoldOrder.SQL=SELECT ORDER_NO,ORDER_CODE &
	              FROM PHL_ORDER &
	           WHERE ADM_TYPE = <ADM_TYPE> &
		     AND CASE_NO = <CASE_NO> &
		     AND ORDER_NO = <ORDER_NO>  &
		     AND SEQ_NO=<SEQ_NO> &
		     AND ORDER_CODE=<ORDER_CODE>     
onQueryoldOrder.Debug=N



queryOrderDetailBed.Type=TSQL
queryOrderDetailBed.SQL=SELECT 'N' AS REPORT_FLG,'Y' AS SELECT_FLG, A.LINKMAIN_FLG, A.LINK_NO, B.ORDER_DESC, B.SPECIFICATION, A.DR_NOTE, &
                            C.DOSE_CHN_DESC, TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH:MM:SS') AS ORDER_DATE, D.USER_NAME AS DR_NAME, &
                            TO_CHAR(A.PHA_DISPENSE_DATE,'YYYY/MM/DD HH:MM:SS') AS PHA_DISPENSE_DATE , E.USER_NAME AS PHA_DISPENSE_NAME, &
                            A.RX_NO, A.DR_CODE,A.ROUTE_CODE,A.FREQ_CODE,N.ROUTE_CHN_DESC AS ROUTE_CODEA, M.FREQ_CHN_DESC AS FREQ_CODEA, A.TAKE_DAYS,A.PHA_DISPENSE_CODE,A.PHA_RETN_CODE, &
                            A.NS_NOTE,A.ORDER_CODE,  TO_CHAR (A.MEDI_QTY, '999D9')||F.UNIT_CHN_DESC AS DISPENSE_QTY , G.ROUTE_CHN_DESC, A.SEQ_NO,A.EXEC_FLG, &
			    H.USER_NAME AS PHA_DOSAGE_CODE, K.USER_NAME AS PHA_CHECK_CODE &
	              FROM  OPD_ORDER A, PHA_BASE B, PHA_DOSE C, SYS_OPERATOR D, SYS_OPERATOR E, &
		      SYS_UNIT F, SYS_PHAROUTE G,SYS_OPERATOR H,SYS_OPERATOR K ,SYS_PHAFREQ M,SYS_PHAROUTE N ,PHL_BED I &
	           WHERE A.ORDER_CODE = B.ORDER_CODE &
		     AND B.DOSE_CODE = C.DOSE_CODE &
		     AND A.DR_CODE = D.USER_ID  &
		     AND A.PHA_DOSAGE_CODE=H.USER_ID (+) &
		     AND A.PHA_CHECK_CODE=K.USER_ID (+) &
		     AND A.MEDI_UNIT = F.UNIT_CODE &
    	             AND A.ROUTE_CODE = G.ROUTE_CODE &
		     AND A.PHA_DISPENSE_CODE = E.USER_ID (+) &
		     //AND A.PHA_DISPENSE_CODE IS NOT NULL &
		     AND A.DC_DR_CODE IS NULL &
		     AND N.CLASSIFY_TYPE = 'F' &
		     AND A.ROUTE_CODE=N.ROUTE_CODE &
		     AND A.FREQ_CODE=M.FREQ_CODE &
		     AND A.CASE_NO = I.CASE_NO &
		     ORDER BY A.RX_NO,A.LINK_NO ,A.SEQ_NO
queryOrderDetailBed.ITEM=MR_NO;CASE_NO;ADM_TYPE
queryOrderDetailBed.MR_NO=A.MR_NO=<MR_NO>
queryOrderDetailBed.CASE_NO=A.CASE_NO=<CASE_NO>
queryOrderDetailBed.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
queryOrderDetailBed.Debug=N