# 
#  Title:挂号主档module
# 
#  Description:挂号主档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;selectdata_name;getInfoForCaseNo;insertInfo;updateInfo;selcaseNoBypatName;selDateByMrNo;selDateForODO;selDateForODOByWait;&
	    updateInfoForONW;selPatInfoForREG;updateForUnReg;updateForArrive;getClinicRoomForODO;selDateForODOEmgc;selSummaryPersonO;&
	    selSummaryPersonE;selSummaryPersonReturn;selMaxCaseNoByMrNo;updateWHForONW;selDateByMrNoAdm;selEKTByMrNo;updateEKTGreen;&
	    updateEKTGreen1;selDataNew;insertInfoGreen

//查询门急别,就诊序号,病历号,区域,挂号日期,挂号操作日期,时段代码,诊区,诊间号,就诊号,预约时间,科别代码,看诊医师,实际看诊科别,实际看诊医师,当诊预约,初复诊,&
	挂号方式,身分别,折扣2,折扣3,转诊院所代码,检伤号,记账单位,报到注记,退挂人员,退挂日期,挂号院区,计划免疫,DRG码,发热注记,就诊进度,报告状态,体重,身高,操作人员,&
	操作日期,操作端末
selectdata.Type=TSQL
selectdata.SQL=SELECT ADM_TYPE,CASE_NO,MR_NO,REGION_CODE,ADM_DATE,&
		      REG_DATE,SESSION_CODE,CLINICAREA_CODE,CLINICROOM_NO,QUE_NO,&
		      REG_ADM_TIME,DEPT_CODE,DR_CODE,REALDEPT_CODE,REALDR_CODE,&
		      APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE,CTZ2_CODE,&
		      CTZ3_CODE,TRANHOSP_CODE,TRIAGE_NO,CONTRACT_CODE,ARRIVE_FLG,&
		      REGCAN_USER,REGCAN_DATE,ADM_REGION,PREVENT_SCH_CODE,DRG_CODE,&
		      HEAT_FLG,ADM_STATUS,REPORT_STATUS,WEIGHT,HEIGHT,&
		      CLINICTYPE_CODE,VIP_FLG,OPT_USER,OPT_DATE,OPT_TERM,&
		      SERVICE_LEVEL,SEE_DR_FLG &
		 FROM REG_PATADM 
selectdata.item=REGION_CODE;ADM_TYPE;ADM_DATE;DEPT_CODE;DR_CODE;CASE_NO
selectdata.REGION_CODE=REGION_CODE=<REGION_CODE>
selectdata.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectdata.ADM_DATE=ADM_DATE=<ADM_DATE>
selectdata.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
selectdata.DR_CODE=DR_CODE=<DR_CODE>
selectdata.CASE_NO=CASE_NO=<CASE_NO>
selectdata.Debug=N

//周转日查询(新)
selDataNew.Type=TSQL
selDataNew.SQL=SELECT ADM_TYPE,CASE_NO,MR_NO,REGION_CODE,ADM_DATE,&
		      REG_DATE,SESSION_CODE,CLINICAREA_CODE,CLINICROOM_NO,QUE_NO,&
		      REG_ADM_TIME,DEPT_CODE,DR_CODE,REALDEPT_CODE,REALDR_CODE,&
		      APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE,CTZ2_CODE,&
		      CTZ3_CODE,TRANHOSP_CODE,TRIAGE_NO,CONTRACT_CODE,ARRIVE_FLG,&
		      REGCAN_USER,REGCAN_DATE,ADM_REGION,PREVENT_SCH_CODE,DRG_CODE,&
		      HEAT_FLG,ADM_STATUS,REPORT_STATUS,WEIGHT,HEIGHT,&
		      CLINICTYPE_CODE,VIP_FLG,OPT_USER,OPT_DATE,OPT_TERM,&
		      SERVICE_LEVEL &
		 FROM REG_PATADM 
selDataNew.item=REGION_CODE;ADM_TYPE;ADM_DATE;DEPT_CODE;DR_CODE;CASE_NO;CLINICROOM_NO
selDataNew.REGION_CODE=REGION_CODE=<REGION_CODE>
selDataNew.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selDataNew.ADM_DATE=ADM_DATE=<ADM_DATE>
selDataNew.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
selDataNew.CLINICROOM_NO=CLINICROOM_NO IN (<CLINICROOM_NO>)
selDataNew.DR_CODE=DR_CODE=<DR_CODE>
selDataNew.CASE_NO=CASE_NO=<CASE_NO>
selDataNew.Debug=N

//查询REG主档(FOR ONW)
selectdata_name.Type=TSQL
selectdata_name.SQL=SELECT A.ADM_TYPE,A.CASE_NO,A.MR_NO,A.REGION_CODE,A.ADM_DATE,A.REASSURE_FLG,&
			   A.REG_DATE,A.SESSION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,A.QUE_NO,&
			   A.REG_ADM_TIME,A.DEPT_CODE,(SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = A.DR_CODE) AS DR_CODE ,A.REALDEPT_CODE,A.REALDR_CODE,&
			   A.APPT_CODE,A.VISIT_CODE,A.REGMETHOD_CODE,A.CTZ1_CODE,A.CTZ2_CODE,&
			   A.CTZ3_CODE,A.TRANHOSP_CODE,A.TRIAGE_NO,A.CONTRACT_CODE,A.ARRIVE_FLG,&
			   A.REGCAN_USER,A.REGCAN_DATE,A.ADM_REGION,A.PREVENT_SCH_CODE,A.DRG_CODE,&
			   A.HEAT_FLG,A.ADM_STATUS,A.REPORT_STATUS,A.WEIGHT,A.HEIGHT,&
			   A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PAT_NAME,A.CLINICTYPE_CODE,A.LMP_DATE,B.LMP_DATE PAT_LMP_DATE,  &
			   A.VIP_FLG,B.SEX_CODE,A.SERVICE_LEVEL,A.ARRIVE_DATE AS ARRIVE_TIME,A.REVISIT_DATE AS REVISIT_TIME,PAT_PACKAGE, &
			   //添加出生日期和性别 add by huangjw 20140923
			   B.BIRTH_DATE,(SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX' AND ID=B.SEX_CODE) AS PAT_SEX, &
			   (SELECT COUNT(*) FROM  BIL_OPB_RECP WHERE CASE_NO = A.CASE_NO AND TOT_AMT > 0 AND RESET_RECEIPT_NO IS NULL AND PRINT_NO IS NOT NULL ) COUNT &
		      FROM REG_PATADM A,SYS_PATINFO B &
		     WHERE A.MR_NO=B.MR_NO &
		       AND A.REGCAN_USER IS NULL &
		       ORDER BY ARRIVE_TIME, QUE_NO
selectdata_name.item=REGION_CODE;ADM_TYPE;SESSION_CODE;ADM_DATE;CLINICROOM_NO;REALDR_CODE;REALDEPT_CODE
selectdata_name.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectdata_name.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
selectdata_name.ADM_DATE=A.ADM_DATE=TO_DATE(<ADM_DATE>,'YYYYMMDD')
selectdata_name.SESSION_CODE=A.SESSION_CODE=<SESSION_CODE>
selectdata_name.CLINICROOM_NO=A.CLINICROOM_NO=<CLINICROOM_NO>
selectdata_name.REALDR_CODE=A.REALDR_CODE=<REALDR_CODE>
selectdata_name.REALDEPT_CODE=A.REALDEPT_CODE=<REALDEPT_CODE>
selectdata_name.Debug=N

//得到病患信息
getInfoForCaseNo.Type=TSQL
getInfoForCaseNo.SQL=SELECT CASE_NO,ADM_TYPE,MR_NO,REGION_CODE,ADM_DATE,SEEN_DR_TIME,&
			    REG_DATE,SESSION_CODE,CLINICAREA_CODE,CLINICROOM_NO,QUE_NO,&
			    REG_ADM_TIME,DEPT_CODE,DR_CODE,REALDEPT_CODE,REALDR_CODE,&
			    APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE,CTZ2_CODE,&
			    CTZ3_CODE,TRANHOSP_CODE,TRIAGE_NO,CONTRACT_CODE,ARRIVE_FLG,&
			    REGCAN_USER,REGCAN_DATE,ADM_REGION,PREVENT_SCH_CODE,DRG_CODE,&
			    HEAT_FLG,ADM_STATUS,REPORT_STATUS,WEIGHT,HEIGHT,&
			    SEE_DR_FLG,VIP_FLG,SERVICE_LEVEL,NHI_NO,INS_PAT_TYPE,CONFIRM_NO,VISIT_STATE,REASSURE_FLG &
		       FROM REG_PATADM &
		      WHERE CASE_NO=<CASE_NO>
getInfoForCaseNo.Debug=N

//新增挂号
//====================pangben modify 20110808 添加 NHI_NO：市民卡号 添加 INS_PAT_TYPE :医保卡就诊 ：门特 普通
//=====huangtt modify 20131106 REQUIREMENT 备注
//=====yanjing 添加OLD_CASE_NO IS_PRE_ORDER 字段
//=====sukai 添加REASSURE_FLG 字段
insertInfo.Type=TSQL
insertInfo.SQL=INSERT INTO REG_PATADM &
			   (CASE_NO,ADM_TYPE,MR_NO,REGION_CODE,ADM_DATE,&
			   REG_DATE,SESSION_CODE,CLINICAREA_CODE,CLINICROOM_NO,CLINICTYPE_CODE,&
			   QUE_NO,REG_ADM_TIME,DEPT_CODE,DR_CODE,REALDEPT_CODE,&
			   REALDR_CODE,APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE,&
			   CTZ2_CODE,CTZ3_CODE,TRANHOSP_CODE,TRIAGE_NO,CONTRACT_CODE,&
			   ARRIVE_FLG,REGCAN_USER,REGCAN_DATE,ADM_REGION,PREVENT_SCH_CODE,&
			   DRG_CODE,HEAT_FLG,ADM_STATUS,REPORT_STATUS,WEIGHT,&
			   HEIGHT,ERD_LEVEL,OPT_USER,OPT_DATE,OPT_TERM,SERVICE_LEVEL,NHI_NO,INS_PAT_TYPE, &
			   CONFIRM_NO,REQUIREMENT,IS_PRE_ORDER,OLD_CASE_NO,VIP_FLG,REASSURE_FLG) &
		    VALUES (<CASE_NO>,<ADM_TYPE>,<MR_NO>,<REGION_CODE>,<ADM_DATE>,&
		    	   <REG_DATE>,<SESSION_CODE>,<CLINICAREA_CODE>,<CLINICROOM_NO>,<CLINICTYPE_CODE>,&
		    	   <QUE_NO>,<REG_ADM_TIME>,<DEPT_CODE>,<DR_CODE>,<REALDEPT_CODE>,&
		    	   <REALDR_CODE>,<APPT_CODE>,<VISIT_CODE>,<REGMETHOD_CODE>,<CTZ1_CODE>,&
		    	   <CTZ2_CODE>,<CTZ3_CODE>,<TRANHOSP_CODE>,<TRIAGE_NO>,<CONTRACT_CODE>,&
		    	   <ARRIVE_FLG>,<REGCAN_USER>,<REGCAN_DATE>,<ADM_REGION>,<PREVENT_SCH_CODE>,&
		    	   <DRG_CODE>,<HEAT_FLG>,<ADM_STATUS>,<REPORT_STATUS>,&
			   <WEIGHT>,<HEIGHT>,<ERD_LEVEL>,<OPT_USER>,SYSDATE,<OPT_TERM>,<SERVICE_LEVEL>,<NHI_NO>, &
			   <INS_PAT_TYPE>,<CONFIRM_NO>,<REQUIREMENT>,<IS_PRE_ORDER>,<OLD_CASE_NO>,<VIP_FLG>,<REASSURE_FLG>)
insertInfo.Debug=N


//新增挂号
//====================pangben modify 20120406 特批款预约挂号
//=====huangtt modify 20131106 REQUIREMENT 备注
insertInfoGreen.Type=TSQL
insertInfoGreen.SQL=INSERT INTO REG_PATADM &
			   (CASE_NO,ADM_TYPE,MR_NO,REGION_CODE,ADM_DATE,&
			   REG_DATE,SESSION_CODE,CLINICAREA_CODE,CLINICROOM_NO,CLINICTYPE_CODE,&
			   QUE_NO,REG_ADM_TIME,DEPT_CODE,DR_CODE,REALDEPT_CODE,&
			   REALDR_CODE,APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE,&
			   CTZ2_CODE,CTZ3_CODE,TRANHOSP_CODE,TRIAGE_NO,CONTRACT_CODE,&
			   ARRIVE_FLG,REGCAN_USER,REGCAN_DATE,ADM_REGION,PREVENT_SCH_CODE,&
			   DRG_CODE,HEAT_FLG,ADM_STATUS,REPORT_STATUS,WEIGHT,&
			   HEIGHT,ERD_LEVEL,OPT_USER,OPT_DATE,OPT_TERM,SERVICE_LEVEL,NHI_NO,INS_PAT_TYPE,CONFIRM_NO,REQUIREMENT) &
		    VALUES (<CASE_NO>,<ADM_TYPE>,<MR_NO>,<REGION_CODE>,TO_DATE(<ADM_DATE>,'YYYYMMDD'),&
		    	   <REG_DATE>,<SESSION_CODE>,<CLINICAREA_CODE>,<CLINICROOM_NO>,<CLINICTYPE_CODE>,&
		    	   <QUE_NO>,<REG_ADM_TIME>,<DEPT_CODE>,<DR_CODE>,<REALDEPT_CODE>,&
		    	   <REALDR_CODE>,<APPT_CODE>,<VISIT_CODE>,<REGMETHOD_CODE>,<CTZ1_CODE>,&
		    	   <CTZ2_CODE>,<CTZ3_CODE>,<TRANHOSP_CODE>,<TRIAGE_NO>,<CONTRACT_CODE>,&
		    	   <ARRIVE_FLG>,<REGCAN_USER>,<REGCAN_DATE>,<ADM_REGION>,<PREVENT_SCH_CODE>,&
		    	   <DRG_CODE>,<HEAT_FLG>,<ADM_STATUS>,<REPORT_STATUS>,&
			   <WEIGHT>,<HEIGHT>,<ERD_LEVEL>,<OPT_USER>,SYSDATE,<OPT_TERM>,<SERVICE_LEVEL>,<NHI_NO>,<INS_PAT_TYPE>,<CONFIRM_NO>,<REQUIREMENT>)
insertInfoGreen.Debug=N

//保存挂号信息
updateInfo.Type=TSQL
updateInfo.SQL=UPDATE REG_PATADM &
		  SET ADM_TYPE=<ADM_TYPE>,CASE_NO=<CASE_NO>,MR_NO=<MR_NO>,&
		      REGION_CODE=<REGION_CODE>,ADM_DATE=<ADM_DATE>,REG_DATE=<REG_DATE>,&
		      SESSION_CODE=<SESSION_CODE>,CLINICAREA_CODE=<CLINICAREA_CODE>,CLINICROOM_NO=<CLINICROOM_NO>,&
		      QUE_NO=<QUE_NO>,REG_ADM_TIME=<REG_ADM_TIME>,DEPT_CODE=<DEPT_CODE>,&
		      DR_CODE=<DR_CODE>,REALDEPT_CODE=<REALDEPT_CODE>,REALDR_CODE=<REALDR_CODE>,&
		      APPT_CODE=<APPT_CODE>,VISIT_CODE=<VISIT_CODE>,REGMETHOD_CODE=<REGMETHOD_CODE>,&
		      CTZ1_CODE=<CTZ1_CODE>,CTZ2_CODE=<CTZ2_CODE>,CTZ3_CODE=<CTZ3_CODE>,&
		      TRANHOSP_CODE=<TRANHOSP_CODE>,TRIAGE_NO=<TRIAGE_NO>,CONTRACT_CODE=<CONTRACT_CODE>,&
		      ARRIVE_FLG=<ARRIVE_FLG>,REGCAN_USER=<REGCAN_USER>,REGCAN_DATE=<REGCAN_DATE>,&
		      ADM_REGION=<ADM_REGION>,PREVENT_SCH_CODE=<PREVENT_SCH_CODE>,DRG_CODE=<DRG_CODE>,&
		      HEAT_FLG=<HEAT_FLG>,ADM_STATUS=<ADM_STATUS>,REPORT_STATUS=<REPORT_STATUS>,&
		      WEIGHT=<WEIGHT>,HEIGHT=<HEIGHT>,CLINICTYPE_CODE=<CLINICTYPE_CODE>,&
		      SEE_DR_FLG=<SEE_DR_FLG>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,&
		      OPT_TERM=<OPT_TERM> &
		WHERE CASE_NO=<CASE_NO>
updateInfo.Debug=N

//根据病患姓名查询就诊序号
selcaseNoBypatName.Type=TSQL
selcaseNoBypatName.SQL=SELECT A.CASE_NO ,B.PAT_NAME &
			 FROM REG_PATADM A, SYS_PATINFO B &
			WHERE A.MR_NO=B.MR_NO AND B.PAT_NAME=<PAT_NAME>		       
selcaseNoBypatName.Debug=N
		
//根据MRNO查询的挂号记录
//==========YANJING 添加IS_PRE_ORDER 预开检查标记 20131231 
selDateByMrNo.Type=TSQL
selDateByMrNo.SQL=SELECT CASE_NO,ADM_TYPE,ADM_DATE,SESSION_CODE,QUE_NO,REALDEPT_CODE AS DEPT_CODE, & 
                         REALDR_CODE AS DR_CODE,IS_PRE_ORDER,REASSURE_FLG &
		    FROM REG_PATADM &
                   WHERE MR_NO=<MR_NO> &
                     AND REGCAN_USER IS NULL &
                     AND (ADM_DATE BETWEEN <STARTTIME> AND <ENDTIME>  OR (ADM_DATE IS NULL) &
		     OR (ADM_DATE > <STARTTIME> AND IS_PRE_ORDER = 'Y'))  &
		ORDER BY ADM_DATE DESC,SESSION_CODE    
//==========pangben modify 20110421 start
selDateByMrNo.item=REGION_CODE;ARRIVE_FLG
selDateByMrNo.REGION_CODE=REGION_CODE=<REGION_CODE>
selDateByMrNo.ARRIVE_FLG=ARRIVE_FLG=<ARRIVE_FLG>
//==========pangben modify 20110421 start
selDateByMrNo.Debug=N


//根据MRNO查询的挂号记录	
selDateByMrNoAdm.Type=TSQL
selDateByMrNoAdm.SQL=SELECT CASE_NO,ADM_TYPE,ADM_DATE,SESSION_CODE,QUE_NO,DEPT_CODE,DR_CODE &
		    FROM REG_PATADM &
                   WHERE MR_NO=<MR_NO>  AND ADM_TYPE=<ADM_TYPE> &
                     AND ADM_DATE BETWEEN <STARTTIME> AND <ENDTIME> &
		ORDER BY ADM_DATE,SESSION_CODE                      
selDateByMrNoAdm.item=REGION_CODE
selDateByMrNoAdm.REGION_CODE=REGION_CODE=<REGION_CODE>
selDateByMrNoAdm.Debug=N

//根据医生，科室，就诊时间查询ODO使用的数据（病患列表数据）	
selDateForODO.Type=TSQL
selDateForODO.SQL=SELECT A.QUE_NO AS QUE_NO, A.MR_NO AS MR_NO, B.PAT_NAME AS PAT_NAME,&
			 A.ADM_STATUS AS ADM_STATUS, A.REPORT_STATUS AS REPORT_STATUS,A.CASE_NO AS CASE_NO &
		    FROM REG_PATADM A, SYS_PATINFO B  &
		   WHERE A.ADM_TYPE = <ADM_TYPE> &
		     AND A.ARRIVE_FLG = 'Y' &
		     AND A.ADM_DATE = TO_DATE (<ADM_DATE>, 'YYYY-MM-DD') &
		     AND A.SESSION_CODE = <SESSION_CODE> &
		     AND A.MR_NO = B.MR_NO(+) &
		ORDER BY A.CASE_NO
selDateForODO.item=REALDEPT_CODE;REALDR_CODE
selDateForODO.REALDEPT_CODE=REALDEPT_CODE=<DEPT_CODE>
selDateForODO.REALDR_CODE=REALDR_CODE=<DR_CODE>
selDateForODO.Debug=N

//根据医生，科室，就诊时间查询,保存的状态ODO使用的数据（病患列表数据）
//高危产妇标识字段HIGHRISKMATERNAL_FLG  sys_patinfo表中	
//=============pangben 2012-6-28 start 添加初复诊显示字段
selDateForODOByWait.Type=TSQL
selDateForODOByWait.SQL=SELECT A.QUE_NO AS QUE_NO, A.MR_NO AS MR_NO, B.PAT_NAME AS PAT_NAME,&
			       A.ADM_DATE,A.REALDEPT_CODE ,A.CLINICROOM_NO, A.REALDR_CODE,A.ADM_TYPE,&
			       A.ADM_STATUS AS ADM_STATUS, A.REPORT_STATUS AS REPORT_STATUS,A.REGION_CODE,&
			       A.CASE_NO AS CASE_NO ,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,&
			       B.BIRTH_DATE,B.PAT1_CODE,B.PAT2_CODE,B.PAT3_CODE,B.WEIGHT,B.HIGHRISKMATERNAL_FLG,&
			       B.PREMATURE_FLG,B.HANDICAP_FLG,B.LMP_DATE,B.PREGNANT_DATE,B.BREASTFEED_STARTDATE,B.PAT_NAME1,&
			       B.BREASTFEED_ENDDATE,A.VISIT_CODE,A.REASSURE_FLG &
			  FROM REG_PATADM A, SYS_PATINFO B  &
			 WHERE A.MR_NO = B.MR_NO & 
			   AND  A.ADM_TYPE = <ADM_TYPE> &
			   AND A.ADM_DATE = TO_DATE (<ADM_DATE>, 'YYYY-MM-DD') &			   
			   AND A.REGCAN_USER IS NULL &
			   AND A.REALDR_CODE=<DR_CODE> &
	            //ORDER BY A.CASE_NO
	              ORDER BY A.QUE_NO
		        //modify by wanglong 20120815  修改排序方式为按QUE_NO排序
selDateForODOByWait.item=WAIT_DR;SEE_DR;REALDEPT_CODE;REALDR_CODE;TEMP_DR;ARRIVE_FLG;REGION_CODE;CLINICROOM_NO;SESSION_CODE
selDateForODOByWait.WAIT_DR=(SEE_DR_FLG IS NULL OR SEE_DR_FLG=<WAIT_DR>)
selDateForODOByWait.SEE_DR=SEE_DR_FLG=<SEE_DR>
selDateForODOByWait.TEMP_DR=SEE_DR_FLG=<TEMP_DR>
selDateForODOByWait.REGION_CODE=REGION_CODE=<REGION_CODE>
selDateForODOByWait.CLINICROOM_NO=CLINICROOM_NO=<CLINICROOM_NO>
selDateForODOByWait.SESSION_CODE=SESSION_CODE=<SESSION_CODE>
selDateForODOByWait.ARRIVE_FLG=A.ARRIVE_FLG = 'Y'
selDateForODOByWait.Debug=N

//急诊查询病人，并且因为TABLE的parmMAP里面有ADM_DATE，所以把急诊的到院时间的别名定为ADM_DATE
//高危产妇标识字段HIGHRISKMATERNAL_FLG  sys_patinfo表中
//=============pangben 2012-6-28 start 添加初复诊显示字段
selDateForODOEmgc.Type=TSQL
selDateForODOEmgc.SQL=SELECT A.QUE_NO AS QUE_NO, A.MR_NO AS MR_NO, B.PAT_NAME AS PAT_NAME,&
			     A.ADM_DATE,A.REG_DATE AS REG_DATE,A.REALDEPT_CODE,A.CLINICROOM_NO,A.REALDR_CODE,&
			     A.ADM_TYPE,A.ADM_STATUS AS ADM_STATUS,A.REPORT_STATUS AS REPORT_STATUS,&
			     A.REGION_CODE,A.CASE_NO AS CASE_NO ,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,&
			     B.BIRTH_DATE,B.PAT1_CODE,B.PAT2_CODE,B.PAT3_CODE,B.WEIGHT,B.HIGHRISKMATERNAL_FLG,&
			     B.PREMATURE_FLG,B.HANDICAP_FLG,B.LMP_DATE,B.PREGNANT_DATE,B.BREASTFEED_STARTDATE,&
			     B.BREASTFEED_ENDDATE,A.ERD_LEVEL ,MAX(ORDER_DATE) AS ORDER_DATE ,D.TIME_LIMIT,B.PAT_NAME1,A.VISIT_CODE,A.REASSURE_FLG &
			FROM REG_PATADM A, SYS_PATINFO B ,OPD_ORDER C,REG_ERD_LEVEL D &
		       WHERE A.MR_NO = B.MR_NO & 
		         AND A.CASE_NO=C.CASE_NO (+) &
		         AND A.ERD_LEVEL=D.LEVEL_CODE (+) &
		         AND  A.ADM_TYPE = <ADM_TYPE> &
		         AND A.ARRIVE_FLG = 'Y' &
		         AND A.ADM_DATE = TO_DATE (<ADM_DATE>, 'YYYY-MM-DD') &
		         AND A.REGCAN_USER IS NULL &
		    GROUP BY A.QUE_NO,A.MR_NO,B.PAT_NAME,A.ADM_DATE,A.REG_DATE,A.REALDEPT_CODE,&
			     A.CLINICROOM_NO,A.REALDR_CODE,A.ADM_TYPE,A.ADM_STATUS,A.REPORT_STATUS,&
			     A.REGION_CODE,A.CASE_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,&
			     B.BIRTH_DATE,B.PAT1_CODE,B.PAT2_CODE,B.PAT3_CODE,B.WEIGHT,B.HIGHRISKMATERNAL_FLG,&
			     B.PREMATURE_FLG,B.HANDICAP_FLG,B.LMP_DATE,B.PREGNANT_DATE,B.BREASTFEED_STARTDATE,&
			     B.BREASTFEED_ENDDATE,A.ERD_LEVEL,D.TIME_LIMIT,B.PAT_NAME1,A.VISIT_CODE, A.REASSURE_FLG &
		    ORDER BY A.CASE_NO 
selDateForODOEmgc.item=WAIT_DR;SEE_DR;REALDEPT_CODE;REALDR_CODE;TEMP_DR;SESSION_CODE;CLINICROOM_NO;DR_CODE;REGION_CODE
selDateForODOEmgc.SESSION_CODE=A.SESSION_CODE=<SESSION_CODE>
selDateForODOEmgc.CLINICROOM_NO=A.CLINICROOM_NO=<CLINICROOM_NO>
selDateForODOEmgc.DR_CODE=A.REALDR_CODE=<DR_CODE>
selDateForODOEmgc.REALDEPT_CODE=A.REALDEPT_CODE=<REALDEPT_CODE>
selDateForODOEmgc.WAIT_DR=(SEE_DR_FLG IS NULL OR SEE_DR_FLG=<WAIT_DR>)
selDateForODOEmgc.SEE_DR=SEE_DR_FLG=<SEE_DR>
selDateForODOEmgc.TEMP_DR=SEE_DR_FLG=<TEMP_DR>
selDateForODOEmgc.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selDateForODOEmgc.Debug=N

//更新挂号信息(FOR ONW)
updateInfoForONW.Type=TSQL
updateInfoForONW.SQL=UPDATE REG_PATADM &
			SET SESSION_CODE=<SESSION_CODE>,REALDEPT_CODE=<REALDEPT_CODE>,REALDR_CODE=<REALDR_CODE>,&
			    CLINICROOM_NO=<CLINICROOM_NO>,OPT_USER = <OPT_USER>,OPT_DATE = SYSDATE,&
			    OPT_TERM = <OPT_TERM>,QUE_NO=<QUE_NO> &
		      WHERE CASE_NO=<CASE_NO>
updateInfoForONW.Debug=N

//按病案号,就诊日期查询病患就诊信息(FOR REG)
//add by huangtt 20131121 添加 REQUIREMENT  'N' CRM_FLG
//YANJING 20140102 添加 ADM_DATE IS NULL
selPatInfoForREG.Type=TSQL
selPatInfoForREG.SQL=SELECT 'N' CRM_FLG,ADM_TYPE,CASE_NO,MR_NO,REGION_CODE,ADM_DATE,&
			    REG_DATE,SESSION_CODE,CLINICAREA_CODE,CLINICROOM_NO,QUE_NO,&
			    REG_ADM_TIME,DEPT_CODE,DR_CODE,REALDEPT_CODE,REALDR_CODE,&
			    APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE,CTZ2_CODE,&
			    CTZ3_CODE,TRANHOSP_CODE,TRIAGE_NO,CONTRACT_CODE,ARRIVE_FLG,&
			    REGCAN_USER,REGCAN_DATE,ADM_REGION,PREVENT_SCH_CODE,DRG_CODE,&
			    HEAT_FLG,ADM_STATUS,REPORT_STATUS,WEIGHT,HEIGHT,&
			    CLINICTYPE_CODE,VIP_FLG,OPT_USER,OPT_DATE,OPT_TERM,SERVICE_LEVEL,&
			    IS_PRE_ORDER,OLD_CASE_NO,&
			    CONFIRM_NO,INS_PAT_TYPE,REQUIREMENT,INSURE_INFO,PAT_PACKAGE,REASSURE_FLG &
		       FROM REG_PATADM &
		      WHERE MR_NO = <MR_NO> AND &
		      	(ADM_DATE BETWEEN TO_DATE (<YY_START_DATE>, 'YYYYMMDD') &
		      	AND TO_DATE (<YY_END_DATE>, 'YYYYMMDD')  OR (ADM_DATE IS NULL)) &
		      	AND ADM_TYPE = <ADM_TYPE>  &
		      	AND REGCAN_USER IS NULL &
		   ORDER BY CASE_NO
selPatInfoForREG.item=REGION_CODE
selPatInfoForREG.REGION_CODE=REGION_CODE=<REGION_CODE>
selPatInfoForREG.Debug=N

//退挂更新(FOR REG)
updateForUnReg.Type=TSQL
updateForUnReg.SQL=UPDATE REG_PATADM &
		      SET REGCAN_USER=<REGCAN_USER>,REGCAN_DATE=SYSDATE &
		    WHERE CASE_NO=<CASE_NO>
updateForUnReg.Debug=N

//报道动作,更新报道注记(FOR REG)
updateForArrive.Type=TSQL
updateForArrive.SQL=UPDATE REG_PATADM &
		      SET ARRIVE_FLG='Y',CTZ1_CODE=<CTZ1_CODE>,CTZ2_CODE=<CTZ2_CODE>,CTZ3_CODE=<CTZ3_CODE> &
		    WHERE CASE_NO=<CASE_NO>
updateForArrive.Debug=N

//按时间段查询挂号收入统计表平诊数据(FOR MEDTIIS)
selSummaryPersonO.Type=TSQL
selSummaryPersonO.SQL=SELECT D.DEPT_CODE,SUM (REG_FEE_REAL) AS REG_FEE,SUM (CLINIC_FEE_REAL) AS CLINIC_FEE,COUNT (B.DEPT_CODE) AS COUNT &
			FROM REG_RECEIPT A,REG_PATADM B,SYS_DEPT C,STA_OEI_DEPT_LIST D &
		       WHERE A.HOSP_AREA = B.HOSP_AREA &
			 AND A.HOSP_AREA = C.HOSP_AREA &
			 AND B.HOSP_AREA = C.HOSP_AREA &
			 AND B.DEPT_CODE = C.DEPT_CODE &
			 AND A.CASE_NO = B.CASE_NO &
			 AND A.CHARGE_DATE BETWEEN TO_DATE (<S_TIME>, 'yyyymmdd') &
			 AND TO_DATE (<E_TIME>, 'yyyymmdd') &
			 AND A.ADM_TYPE = 'O' &
			 AND A.RESET_RECEIPT_NO IS NULL &
			 AND B.DEPT_CODE = D.OE_DEPT_CODE &
		    GROUP BY D.DEPT_CODE &
		    ORDER BY D.DEPT_CODE
selSummaryPersonO.Debug=N

//按时间段查询挂号收入统计表急诊数据(FOR MEDTIIS)
selSummaryPersonE.Type=TSQL
selSummaryPersonE.SQL=SELECT D.DEPT_CODE,SUM (REG_FEE_REAL) AS REG_FEE,SUM (CLINIC_FEE_REAL) AS CLINIC_FEE,COUNT (B.DEPT_CODE) AS COUNT &
		        FROM REG_RECEIPT A,REG_PATADM B,SYS_DEPT C,STA_OEI_DEPT_LIST D &
		       WHERE A.HOSP_AREA = B.HOSP_AREA &
		      	 AND A.HOSP_AREA = C.HOSP_AREA &
		      	 AND B.HOSP_AREA = C.HOSP_AREA &
		      	 AND B.DEPT_CODE = C.DEPT_CODE &
		      	 AND A.CASE_NO = B.CASE_NO &
		      	 AND A.CHARGE_DATE BETWEEN TO_DATE (<S_TIME>, 'yyyymmdd') &
		      	 AND TO_DATE (<E_TIME>, 'yyyymmdd') &
		      	 AND A.ADM_TYPE = 'E' &
		      	 AND A.RESET_RECEIPT_NO IS NULL &
		      	 AND B.DEPT_CODE = D.OE_DEPT_CODE &
		    GROUP BY D.DEPT_CODE &
		    ORDER BY D.DEPT_CODE
selSummaryPersonE.Debug=N

//按时间段查询挂号收入统计表退费数据(FOR MEDTIIS)
selSummaryPersonReturn.Type=TSQL
selSummaryPersonReturn.SQL=SELECT D.DEPT_CODE,-SUM (A.REG_FEE_REAL + A.CLINIC_FEE_REAL) AS RETURN_FEE,-COUNT (B.DEPT_CODE) AS COUNT &
		             FROM REG_RECEIPT A,REG_PATADM B,SYS_DEPT C,STA_OEI_DEPT_LIST D &
		            WHERE A.HOSP_AREA = B.HOSP_AREA &
			      AND A.HOSP_AREA = C.HOSP_AREA &
			      AND B.HOSP_AREA = C.HOSP_AREA &
			      AND B.DEPT_CODE = C.DEPT_CODE &
			      AND A.CASE_NO = B.CASE_NO &
			      AND A.CHARGE_DATE BETWEEN TO_DATE (<S_TIME>, 'yyyymmdd') &
			      AND TO_DATE (<E_TIME>, 'yyyymmdd') &
			      AND A.RESET_RECEIPT_NO IS NOT NULL &
			      AND (A.REG_FEE_REAL + A.CLINIC_FEE_REAL) > 0 &
			      AND B.DEPT_CODE = D.OE_DEPT_CODE &
			 GROUP BY D.DEPT_CODE &
			 ORDER BY D.DEPT_CODE
selSummaryPersonReturn.Debug=N

//通过mr_no拿到最大case_no
selMaxCaseNoByMrNo.Type=TSQL
selMaxCaseNoByMrNo.SQL=SELECT MAX(CASE_NO) AS CASE_NO &
			 FROM REG_PATADM &
		        WHERE MR_NO = <MR_NO>
selMaxCaseNoByMrNo.item=REGION_CODE
selMaxCaseNoByMrNo.REGION_CODE=REGION_CODE=<REGION_CODE>
selMaxCaseNoByMrNo.Debug=N

//报道动作,更新报道注记(FOR REG)
updateWHForONW.Type=TSQL
updateWHForONW.SQL=UPDATE REG_PATADM &
		      SET WEIGHT = <WEIGHT>,&
		          HEIGHT = <HEIGHT> &
		    WHERE CASE_NO = <CASE_NO>
updateWHForONW.Debug=N

//医疗卡绿色通道查找病患就诊号==pangben 20111009

selEKTByMrNo.Type=TSQL
selEKTByMrNo.SQL=SELECT CASE_NO,MR_NO,GREEN_BALANCE,GREEN_PATH_TOTAL &
			 FROM REG_PATADM 
selEKTByMrNo.item=REGION_CODE;CASE_NO;MR_NO
selEKTByMrNo.REGION_CODE=REGION_CODE=<REGION_CODE>
selEKTByMrNo.MR_NO=MR_NO=<MR_NO>
selEKTByMrNo.CASE_NO=CASE_NO=<CASE_NO>
selEKTByMrNo.Debug=N

//医疗卡绿色通道修改申请批准金额==pangben 20111009
updateEKTGreen.Type=TSQL
updateEKTGreen.SQL=UPDATE REG_PATADM &
		      SET GREEN_BALANCE = <GREEN_BALANCE>,&
		          GREEN_PATH_TOTAL = <GREEN_PATH_TOTAL> &
		    WHERE CASE_NO = <CASE_NO>
updateEKTGreen.Debug=N

//医疗卡绿色通道扣款操作==pangben 20111009

updateEKTGreen1.Type=TSQL
updateEKTGreen1.SQL=UPDATE REG_PATADM &
		      SET GREEN_BALANCE = <GREEN_BALANCE> &
		    WHERE CASE_NO = <CASE_NO>
updateEKTGreen1.Debug=N

