###############################
# <p>Title:病患会员注册 </p>
#
# <p>Description:病患会员注册  </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:javahis </p>
#
# @author sunqy 2014.05.13
# @version 4.5
#
###############################
Module.item=insertMemInsureInfo;updateMemInsureInfo;deleteMemInsureInfo;selectMemInsureInfo;onQuerySysPatInfo

//新增数据到MEM_INSURE_INFO//==liling add MEMO 保险欠费备注
insertMemInsureInfo.Type=TSQL
insertMemInsureInfo.SQL=INSERT INTO MEM_INSURE_INFO &
				(MR_NO, CONTRACTOR_CODE, INSURANCE_NUMBER, &
				INSURE_PAY_TYPE, START_DATE, END_DATE, &
				VALID_FLG, DEPT_FLG, INSURANCE_BILL_NUMBER,AMT,MEMO, &
				INSURANCE_CLAUSE_O,  INSURANCE_CLAUSE_I, OPT_DATE, OPT_USER, OPT_TERM) &
			VALUES &
			      (<MR_NO>, <CONTRACTOR_CODE>, <INSURANCE_NUMBER>, &
			      <INSURE_PAY_TYPE>, TO_DATE(<START_DATE>,'yyyy/MM/dd'), TO_DATE(<END_DATE>,'yyyy/MM/dd'), &
			      <VALID_FLG>,<DEPT_FLG>, <INSURANCE_BILL_NUMBER>, <AMT>,<MEMO>, &
			      <INSURANCE_CLAUSE_O>, <INSURANCE_CLAUSE_I>, sysdate, <OPT_USER>, <OPT_TERM>)
insertMemInsureInfo.Debug=Y

//修改数据(MEM_INSURE_INFO)
updateMemInsureInfo.Type=TSQL
updateMemInsureInfo.SQL=UPDATE MEM_INSURE_INFO SET  MEMO=<MEMO>,&
					INSURE_PAY_TYPE=<INSURE_PAY_TYPE>,&
					START_DATE=TO_DATE(<START_DATE>,'yyyy/MM/dd'),END_DATE=TO_DATE(<END_DATE>,'yyyy/MM/dd'),&
					INSURANCE_CLAUSE_O=<INSURANCE_CLAUSE_O>,INSURANCE_CLAUSE_I=<INSURANCE_CLAUSE_I>,&
					VALID_FLG=<VALID_FLG>,DEPT_FLG=<DEPT_FLG>,INSURANCE_BILL_NUMBER=<INSURANCE_BILL_NUMBER>,AMT=<AMT>,&
					OPT_DATE=sysdate,OPT_USER=<OPT_USER>,OPT_TERM=<OPT_TERM> &
			WHERE MR_NO=<MR_NO> AND CONTRACTOR_CODE=<CONTRACTOR_CODE> AND INSURANCE_NUMBER=<INSURANCE_NUMBER>
updateMemInsureInfo.Debug=Y

//删除数据(MEM_INSURE_INFO)
deleteMemInsureInfo.Type=TSQL
deleteMemInsureInfo.SQL=DELETE FROM MEM_INSURE_INFO WHERE MR_NO=<MR_NO> AND CONTRACTOR_CODE=<CONTRACTOR_CODE> AND INSURANCE_NUMBER=<INSURANCE_NUMBER>
deleteMemInsureInfo.Debug=N

//查询数据(MEM_INSURE_INFO)
selectMemInsureInfo.Type=TSQL
selectMemInsureInfo.SQL=SELECT VALID_FLG, CONTRACTOR_CODE, CONTRACTOR_NAME, INSURANCE_NUMBER,  INSURANCE_BILL_NUMBER, &
							   INSURE_PAY_TYPE, START_DATE, END_DATE, INSURANCE_CLAUSE_O, INSURANCE_CLAUSE_I, &
							   OPT_USER, OPT_TERM,OPT_DATE &
						FROM MEM_INSURE_INFO &
						WHERE MR_NO=<MR_NO>
selectMemInsureInfo.Debug=N

//查询患者数据(SYS_PATINFO)
onQuerySysPatInfo.Type=TSQL
onQuerySysPatInfo.SQL=SELECT PAT_NAME,FIRST_NAME,LAST_NAME,SEX_CODE,BIRTH_DATE,ID_TYPE,IDNO &
						FROM SYS_PATINFO WHERE MR_NO=<MR_NO>
onQuerySysPatInfo.Debug=Y