####################################################
#  Title:非常态门诊module
# 
#  Description:非常态门诊module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2010.10.27
#  version 1.0
####################################################
Module.item=saveReg;selectRegForOPD

saveReg.Type=TSQL
saveReg.SQL=INSERT INTO REG_PATADM &
			   (CASE_NO,ADM_TYPE,MR_NO,REGION_CODE,ADM_DATE,&
			   REG_DATE,SESSION_CODE,CLINICTYPE_CODE,DEPT_CODE,DR_CODE,REALDEPT_CODE,&
			   REALDR_CODE,APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE,&
			   CTZ2_CODE,CTZ3_CODE,ARRIVE_FLG,ADM_REGION,HEAT_FLG,&
			   ADM_STATUS,REPORT_STATUS,WEIGHT,HEIGHT,OPT_USER,&
			   OPT_DATE,OPT_TERM,SERVICE_LEVEL,CLINICROOM_NO) &
		    VALUES (<CASE_NO>,<ADM_TYPE>,<MR_NO>,<REGION_CODE>,TO_DATE(<ADM_DATE>,'YYYYMMDD'),&
		    	   <REG_DATE>,<SESSION_CODE>,<CLINICTYPE_CODE>,<DEPT_CODE>,<DR_CODE>,<REALDEPT_CODE>,&
		    	   <REALDR_CODE>,<APPT_CODE>,<VISIT_CODE>,<REGMETHOD_CODE>,<CTZ1_CODE>,&
		    	   <CTZ2_CODE>,<CTZ3_CODE>,<ARRIVE_FLG>,<ADM_REGION>,<HEAT_FLG>,&
		    	   <ADM_STATUS>,<REPORT_STATUS>,<WEIGHT>,<HEIGHT>,<OPT_USER>,&
			   SYSDATE,<OPT_TERM>,<SERVICE_LEVEL>,<CLINICROOM_NO>)
saveReg.Debug=N

selectRegForOPD.Type=TSQL
selectRegForOPD.SQL=SELECT A.QUE_NO AS QUE_NO, A.MR_NO AS MR_NO, B.PAT_NAME AS PAT_NAME,&
			       A.ADM_DATE,A.REALDEPT_CODE ,A.CLINICROOM_NO, A.REALDR_CODE,A.ADM_TYPE,&
			       A.ADM_STATUS AS ADM_STATUS, A.REPORT_STATUS AS REPORT_STATUS,A.REGION_CODE,&
			       A.CASE_NO AS CASE_NO ,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,&
			       B.BIRTH_DATE,B.PAT1_CODE,B.PAT2_CODE,B.PAT3_CODE,B.WEIGHT,&
			       B.PREMATURE_FLG,B.HANDICAP_FLG,B.LMP_DATE,B.PREGNANT_DATE,B.BREASTFEED_STARTDATE,B.PAT_NAME1,&
			       B.BREASTFEED_ENDDATE &
			  FROM REG_PATADM A, SYS_PATINFO B  &
			  WHERE A.MR_NO = B.MR_NO &
			  ORDER BY CASE_NO DESC
selectRegForOPD.item=MR_NO;CASE_NO
selectRegForOPD.MR_NO=A.MR_NO=<MR_NO>
selectRegForOPD.CASE_NO=A.CASE_NO=<CASE_NO>
selectRegForOPD.Debug=N