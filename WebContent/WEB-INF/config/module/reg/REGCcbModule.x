# 
#  Title:建行接口module
# 
#  Description:建行接口module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author fuwj 2012.06.28
#  version 1.0
#
Module.item=getPatInfoByidNo;updatePatInfo;getRegAchDay;updatequeno;insertInfo;updateVipqueno;getVipQueNo;insertCcbTrade;&
insertBilRegRecp;insertInvrcp;selectPatadmByCaseno;cancelPatadm;getPatInfoByPersonNo;getCcbTrade;selectPatInfoByMrno;selectConfirmReg;&
getInsOpd;selectTradeByCaseno;insertBilOpbRecp;getPatInfoByPersonNo;query


//根据Name,idNo查询病患信息  
getPatInfoByidNo.Type=TSQL
getPatInfoByidNo.SQL=SELECT PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,ADDRESS,CONTACTS_TEL,MR_NO FROM SYS_PATINFO &
                                 WHERE (IDNO=<IDNO> OR IDNO=<CCB_ID>) &
				       AND PAT_NAME=<PAT_NAME>
getPatInfoByidNo.Debug=N

//根据Name,idNo查询病患信息  
getPatInfoByPersonNo.Type=TSQL
getPatInfoByPersonNo.SQL=SELECT PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,ADDRESS,CONTACTS_TEL,MR_NO FROM SYS_PATINFO &
                                 WHERE CCB_PERSON_NO=<CCB_PERSON_NO>   
getPatInfoByPersonNo.Debug=N
 

//更新病患信息
updatePatInfo.Type=TSQL
updatePatInfo.SQL=UPDATE SYS_PATINFO set PAT_NAME=<PAT_NAME>,IDNO=<IDNO>,ADDRESS=<ADDRESS>,SEX_CODE=<SEX_CODE>,& 
				TEL_HOME=<TEL_HOME>,CCB_PERSON_NO=<CCB_PERSON_NO>,BIRTH_DATE=<BIRTH_DATE> WHERE MR_NO=<MR_NO>
updatePatInfo.Debug=N   


//查询排班信息   
getRegAchDay.Type=TSQL
getRegAchDay.SQL=SELECT VIP_FLG,REGION_CODE,CLINICROOM_NO,CLINICTYPE_CODE,QUE_NO,REALDEPT_CODE,REALDR_CODE FROM  REG_SCHDAY &
                          WHERE DR_CODE=<DR_CODE> &	
                            AND SESSION_CODE=<SESSION_CODE> &
			    AND ADM_DATE=<ADM_DATE>
getRegAchDay.Debug=N 
  

//更新就诊号
updatequeno.Type=TSQL
updatequeno.SQL=UPDATE REG_SCHDAY &
		   SET QUE_NO=QUE_NO+1 &
	         WHERE ADM_DATE = <ADM_DATE> &
	           AND SESSION_CODE = <SESSION_CODE> &
		   AND DR_CODE=<DR_CODE>  
updatequeno.Debug=N


//更新VIP诊号
updateVipqueno.Type=TSQL
updateVipqueno.SQL=UPDATE REG_CLINICQUE &
		   SET QUE_STATUS='Y' &
		 WHERE ADM_TYPE = <ADM_TYPE> &
		   AND ADM_DATE = <ADM_DATE> &
		   AND SESSION_CODE = <SESSION_CODE> &
		   AND CLINICROOM_NO=<CLINICROOM_NO> &
		   AND START_TIME=<START_TIME> &
		   AND QUE_STATUS='N'
updateVipqueno.Debug=N 

//新增挂号
insertInfo.Type=TSQL
insertInfo.SQL=INSERT INTO REG_PATADM &
			   (CASE_NO,ADM_TYPE,MR_NO,REGION_CODE,ADM_DATE,&
			   REG_DATE,SESSION_CODE,CLINICAREA_CODE,CLINICROOM_NO,CLINICTYPE_CODE,&
			   QUE_NO,REG_ADM_TIME,DEPT_CODE,DR_CODE,REALDEPT_CODE,&
			   REALDR_CODE,APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE,&
			   ADM_REGION,&
			   HEAT_FLG,ADM_STATUS,REPORT_STATUS,WEIGHT,&
			   HEIGHT,OPT_USER,OPT_DATE,OPT_TERM,CONFIRM_NO,NHI_NO,ARRIVE_FLG,INS_PAT_TYPE) &
		    VALUES (<CASE_NO>,<ADM_TYPE>,<MR_NO>,<REGION_CODE>,<ADM_DATE>,&
		    	   <REG_DATE>,<SESSION_CODE>,<CLINICAREA_CODE>,<CLINICROOM_NO>,<CLINICTYPE_CODE>,&
		    	   <QUE_NO>,<REG_ADM_TIME>,<DEPT_CODE>,<DR_CODE>,<REALDEPT_CODE>,&
		    	   <REALDR_CODE>,<APPT_CODE>,<VISIT_CODE>,<REGMETHOD_CODE>,<CTZ1_CODE>,&
		    	   <ADM_REGION>,&
		    	   <HEAT_FLG>,<ADM_STATUS>,<REPORT_STATUS>,& 
			   <WEIGHT>,<HEIGHT>,<OPT_USER>,SYSDATE,<OPT_TERM>,<CONFIRM_NO>,<NHI_NO>,<ARRIVE_FLG>,<INS_PAT_TYPE>)     
insertInfo.Debug=N


//查询VIP的QUENO
getVipQueNo.Type=TSQL
getVipQueNo.SQL=SELECT QUE_NO,START_TIME FROM REG_CLINICQUE &
		   WHERE ADM_TYPE = <ADM_TYPE> &
		   AND ADM_DATE = <ADM_DATE> &
		   AND SESSION_CODE = <SESSION_CODE> &
		   AND CLINICROOM_NO=<CLINICROOM_NO> &
		   AND START_TIME=<START_TIME> &
		   AND QUE_STATUS='N'  

getVipQueNo.Debug=N  

//添加交易记录
insertCcbTrade.Type=TSQL
insertCcbTrade.SQL=INSERT INTO EKT_CCB_TRADE &
                          (TRADE_NO,CARD_NO,MR_NO,CASE_NO,BUSINESS_NO,PAT_NAME,&
                           AMT,STATE,BUSINESS_TYPE,OPT_USER,OPT_DATE,OPT_TERM,BANK_FLG,TOKEN,GUID,RECEIPT_NO) &
                    VALUES (<TRADE_NO>,<CARD_NO>,<MR_NO>,<CASE_NO>,<BUSINESS_NO>,<PAT_NAME>,&
                           <AMT>,<STATE>,<BUSINESS_TYPE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<BANK_FLG>,<TOKEN>,<GUID>,<RECEIPT_NO>)   


insertCcbTrade.Debug=N

//添加挂号收据
insertBilRegRecp.Type=TSQL
insertBilRegRecp.SQL=INSERT INTO BIL_REG_RECP &
                            (CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO,PRINT_NO,BILL_DATE,CHARGE_DATE,PRINT_DATE,&
                             REG_FEE,CLINIC_FEE,REG_FEE_REAL,CLINIC_FEE_REAL,PAY_INS_CARD,OTHER_FEE2,AR_AMT,CASH_CODE,PAY_CASH,OPT_USER,OPT_DATE,OPT_TERM) &
                             VALUES (<CASE_NO>,<RECEIPT_NO>,<ADM_TYPE>,<REGION_CODE>,<MR_NO>,<PRINT_NO>,<BILL_DATE>,&
			    <CHARGE_DATE>,<PRINT_DATE>,<REG_FEE>,<CLINIC_FEE>,<REG_FEE_REAL>,<CLINIC_FEE_REAL>,<PAY_INS_CARD>,<OTHER_FEE2>,<AR_AMT>,<CASH_CODE>,<PAY_CASH>, &    
                            <OPT_USER>,<OPT_DATE>,<OPT_TERM>)     	                                   

                  
insertBilRegRecp.Debug=N    


//添加门诊收据
insertBilOpbRecp.Type=TSQL
insertBilOpbRecp.SQL=INSERT INTO BIL_OPB_RECP( &
                   CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO,&
                   PRINT_NO,BILL_DATE,CHARGE_DATE,&
                   PRINT_DATE,CHARGE01,CHARGE02,CHARGE03,CHARGE04,&
                   CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09,&
                   CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14,&
                   CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,&
                   CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24,&
                   CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,&
                   CHARGE30,TOT_AMT,REDUCE_AMT,AR_AMT,CASHIER_CODE,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD,PAY_INS_CARD,&
		   PAY_CHECK,PAY_DEBIT,PAY_BILPAY,PAY_INS,PAY_OTHER1,PAY_OTHER2,OPT_USER,OPT_DATE,OPT_TERM) &
                   values(<CASE_NO>,<RECEIPT_NO>,<ADM_TYPE>,&
                   <REGION_CODE>,<MR_NO>,&
                   <PRINT_NO>,<BILL_DATE>,<CHARGE_DATE>,<PRINT_DATE>,&
                   <CHARGE01>,<CHARGE02>,<CHARGE03>,<CHARGE04>,<CHARGE05>,&
                   <CHARGE06>,<CHARGE07>,<CHARGE08>,<CHARGE09>,<CHARGE10>,&
                   <CHARGE11>,<CHARGE12>,<CHARGE13>,<CHARGE14>,<CHARGE15>,&
                   <CHARGE16>,<CHARGE17>,<CHARGE18>,<CHARGE19>,<CHARGE20>,&
                   <CHARGE21>,<CHARGE22>,<CHARGE23>,<CHARGE24>,<CHARGE25>,&
                   <CHARGE26>,<CHARGE27>,<CHARGE28>,<CHARGE29>,<CHARGE30>,&
                   <TOT_AMT>,<REDUCE_AMT>,<AR_AMT>,<CASHIER_CODE>,<PAY_CASH>,<PAY_MEDICAL_CARD>,<PAY_BANK_CARD>,<PAY_INS_CARD>,&
		   <PAY_CHECK>,<PAY_DEBIT>,<PAY_BILPAY>,<PAY_INS>,<PAY_OTHER1>,<PAY_OTHER2>,<OPT_USER>,&
                   <OPT_DATE>,<OPT_TERM>)                                   

                     
insertBilOpbRecp.Debug=N    


//添加票据主档
insertInvrcp.Type=TSQL
insertInvrcp.SQL= INSERT INTO BIL_INVRCP &	
                          (RECP_TYPE,INV_NO,RECEIPT_NO,AR_AMT,CASHIER_CODE,CANCEL_FLG,ADM_TYPE,PRINT_USER,PRINT_DATE,OPT_USER,OPT_DATE,OPT_TERM) &
                          VALUES (<RECP_TYPE>,<INV_NO>,<RECEIPT_NO>,<AR_AMT>,<CASHIER_CODE>,<CANCEL_FLG>,<ADM_TYPE>,<PRINT_USER>,<PRINT_DATE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>) 


insertInvrcp.Debug=N  			

//根据MR_NO查询REG_PATADM
selectPatadmByCaseno.Type=TSQL
selectPatadmByCaseno.SQL= SELECT CASE_NO,APPT_CODE,REGCAN_USER,REGCAN_DATE,TO_CHAR(ADM_DATE,'yyyy-mm-dd') AS ADM_DATE &
				,SESSION_CODE,CLINICROOM_NO,QUE_NO,APPT_CODE,DEPT_CODE,MR_NO,DR_CODE,CONFIRM_NO &
				FROM REG_PATADM WHERE CASE_NO=<CASE_NO>  

   

selectPatadmByCaseno.Debug=N 

//退号
cancelPatadm.Type=TSQL
cancelPatadm.SQL= UPDATE REG_PATADM SET REGCAN_USER=<REGCAN_USER>,REGCAN_DATE=<REGCAN_DATE> WHERE CASE_NO=<CASE_NO> 

cancelPatadm.Debug=N        


//根据Persion_no查询病患信息
getPatInfoByPersonNo.Type=TSQL
getPatInfoByPersonNo.SQL=SELECT MR_NO,PAT_NAME,CCB_PERSON_NO FROM SYS_PATINFO WHERE CCB_PERSON_NO=<CCB_PERSON_NO>

getPatInfoByPersonNo.Debug=N 


//查询交易记录
getCcbTrade.Type=TSQL
getCcbTrade.SQL=SELECT CARD_NO,MR_NO,CASE_NO,BUSINESS_NO,PAT_NAME,AMT FROM EKT_CCB_TRADE WHERE CASE_NO=<CASE_NO>



getCcbTrade.Debug=N 

//根据MR_NO查询病患
selectPatInfoByMrno.Type=TSQL

selectPatInfoByMrno.SQL=SELECT PAT_NAME FROM SYS_PATINFO WHERE MR_NO=<MR_NO> 

selectPatInfoByMrno.Debug=N 

//当日报道
selectConfirmReg.Type=TSQL
selectConfirmReg.SQL=SELECT CASE_NO,MR_NO,APPT_CODE,REGCAN_USER,REGCAN_DATE,TO_CHAR(ADM_DATE,'yyyy-mm-dd') AS ADM_DATE &
				,SESSION_CODE,CLINICROOM_NO,QUE_NO,APPT_CODE,DEPT_CODE,MR_NO,DR_CODE,CLINICTYPE_CODE &
				FROM REG_PATADM WHERE CASE_NO=<CASE_NO> AND APPT_CODE='Y' &
				AND ARRIVE_FLG='N' AND REGCAN_USER IS NULL AND REGCAN_DATE IS NULL                     



selectConfirmReg.Debug=N 


//查询医保信息
getInsOpd.Type=TSQL
getInsOpd.SQL=SELECT B.TOT_AMT,A.INS_PAY_AMT,A.UNREIM_AMT,A.OINSTOT_AMT,A.OWN_AMT,A.UNACCOUNT_PAY_AMT,A.ACCOUNT_PAY_AMT,A.TOT_AMT AS INS_TOT_AMT & 
                    FROM INS_OPD A,ins_mz_confirm B &  
                    WHERE A.CASE_NO=<CASE_NO> AND A.CONFIRM_NO=<CONFIRM_NO> AND A.CONFIRM_NO=B.CONFIRM_NO AND A.CASE_NO=B.CASE_NO AND INSAMT_FLG='1'

getInsOpd.Debug=N   


//根据MR_NO查询REG_PATADM
selectTradeByCaseno.Type=TSQL
selectTradeByCaseno.SQL= SELECT A.CASE_NO,A.REGION_CODE,A.CONFIRM_NO,A.MR_NO AS MR_NO,B.PAT_NAME AS PAT_NAME,CLINICTYPE_CODE & 
				FROM REG_PATADM A,SYS_PATINFO B WHERE CASE_NO=<CASE_NO> AND A.MR_NO=B.MR_NO AND ARRIVE_FLG='N'    
                                                              
selectTradeByCaseno.Debug=N


//门诊收据类型查询
//条件是门急住别 ADM_TYPE
selectChargeCode.Type=TSQL
selectChargeCode.SQL=SELECT CHARGE01,CHARGE02,CHARGE03,CHARGE04,CHARGE05, &
                            CHARGE06,CHARGE07,CHARGE08,CHARGE09,CHARGE10, &
                            CHARGE11,CHARGE12,CHARGE13,CHARGE14,CHARGE15, &
                            CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20 &
                            FROM BIL_RECPPARM  &
                            WHERE ADM_TYPE=<ADM_TYPE>
selectChargeCode.Debug=N


// 查询全字段
query.Type=TSQL
query.SQL=SELECT A.CASE_NO,A.RX_NO,A.SEQ_NO,A.OPT_USER,A.OPT_DATE,&
		 A.OPT_TERM,A.PRESRT_NO,A.REGION_CODE,A.MR_NO,A.ADM_TYPE,&
		 A.RX_TYPE,A.TEMPORARY_FLG,A.RELEASE_FLG,A.LINKMAIN_FLG,A.LINK_NO,&
		 A.ORDER_CODE,A.ORDER_DESC ||CASE &
					 WHEN TRIM(A.SPECIFICATION) IS NOT NULL OR TRIM(A.SPECIFICATION) <>''&
					 THEN '(' || A.SPECIFICATION || ')'&
					 ELSE ''&
					  END AS ORDER_DESC,A.SPECIFICATION,A.GOODS_DESC,A.ORDER_CAT1_CODE,A.MEDI_QTY,&
					      A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DOSAGE_QTY,A.DOSAGE_UNIT,&
					      A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,A.OWN_PRICE,A.NHI_PRICE,&
					      A.DISCOUNT_RATE,A.OWN_AMT,A.AR_AMT,A.DR_NOTE,A.NS_NOTE,&
					      A.DR_CODE,A.ORDER_DATE,A.DEPT_CODE,A.DC_DR_CODE,A.DC_ORDER_DATE,&
					      A.DC_DEPT_CODE,A.EXEC_DEPT_CODE,A.SETMAIN_FLG,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,&
					      A.HIDE_FLG,A.RPTTYPE_CODE,A.OPTITEM_CODE,A.DEV_CODE,A.MR_CODE,&
					      A.FILE_NO,A.DEGREE_CODE,A.URGENT_FLG,A.INSPAY_TYPE,A.PHA_TYPE,&
					      A.DOSE_TYPE,A.PRINTTYPEFLG_INFANT,A.EXPENSIVE_FLG,A.CTRLDRUGCLASS_CODE,A.PRESCRIPT_NO,&
					      A.ATC_FLG,A.SENDATC_DATE,A.RECEIPT_NO,A.BILL_FLG,A.BILL_DATE,&
					      A.BILL_USER,A.PRINT_FLG,A.REXP_CODE,A.HEXP_CODE,A.CONTRACT_CODE,&
					      A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.PHA_CHECK_CODE,A.PHA_CHECK_DATE,&
					      A.PHA_DOSAGE_CODE,A.PHA_DOSAGE_DATE,A.PHA_DISPENSE_CODE,A.PHA_DISPENSE_DATE,A.NS_EXEC_CODE,&
					      A.NS_EXEC_DATE,A.NS_EXEC_DEPT,A.DCTAGENT_CODE,A.DCTEXCEP_CODE,A.DCT_TAKE_QTY,&
					      A.PACKAGE_TOT,A.AGENCY_ORG_CODE,A.DCTAGENT_FLG,A.DECOCT_CODE,A.EXEC_FLG,A.RECEIPT_FLG,A.CAT1_TYPE,&
					      A.MED_APPLY_NO,A.BILL_TYPE,A.PHA_RETN_CODE,A.BUSINESS_NO,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I FROM OPD_ORDER A,SYS_FEE B &
	   WHERE A.ORDER_CODE=B.ORDER_CODE AND A.BILL_FLG='N' AND A.PRINT_FLG='N' AND TO_CHAR(A.ORDER_DATE,'yyyy-MM-dd')=TO_CHAR(SYSDATE,'yyyy-MM-dd') ORDER BY A.RX_TYPE,A.RX_NO,A.SEQ_NO
query.item=CASE_NO;RX_NO;SEQ_NO;RECEIPT_NO
query.CASE_NO=CASE_NO=<CASE_NO>
query.RX_NO=RX_NO=<RX_NO>
query.SEQ_NO=SEQ_NO=<SEQ_NO>
query.RECEIPT_NO=RECEIPT_NO=<RECEIPT_NO>
query.Debug=N