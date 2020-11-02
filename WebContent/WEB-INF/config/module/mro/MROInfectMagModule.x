# 
#  Title:传染病报告卡管理module
# 
#  Description:传染病报告卡管理module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangh 2013.11.05
#  version 1.0
#
Module.item=selectInfectReportInPatient;selectInfectReportOutPatient;updateInfectReport;updateInfectReportCancel

//查询传染病信息  住院
selectInfectReportInPatient.Type=TSQL
selectInfectReportInPatient.SQL=SELECT '' AS SELECT_FLG,CASE WHEN A.UPLOAD_DATE IS NULL THEN 'N' ELSE 'Y' END REPORT_FLG, &
			      B.DEPT_CODE,B.STATION_CODE,D.BED_NO_DESC AS BED_NO,B.IPD_NO,A.MR_NO,C.PAT_NAME, &
			      B.IN_DATE,B.VS_DR_CODE,A.UPLOAD_DATE,A.PAD_DEPT,A.CARD_SEQ_NO &
		       FROM MRO_INFECT_RECORD A &
		       LEFT JOIN ADM_INP B ON A.MR_NO = B.MR_NO &
		       LEFT JOIN SYS_PATINFO C ON A.MR_NO = C.MR_NO &
		       LEFT JOIN SYS_BED D ON B.BED_NO = D.BED_NO &
		       WHERE A.CASE_NO = B.CASE_NO &
		       ORDER BY REPORT_FLG DESC
selectInfectReportInPatient.item=START_DATE;END_DATE;MR_NO;REPORT_START_DATE;REPORT_END_DATE;REGION_CODE;REPORT_DATE_NULL;REPORT_DATE_NOT_NULL;ICD_CODE;DEPT_CODE;STATION_CODE
selectInfectReportInPatient.START_DATE=B.IN_DATE>=TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS')
selectInfectReportInPatient.END_DATE=B.IN_DATE<=TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS')
selectInfectReportInPatient.MR_NO=A.MR_NO=<MR_NO>
selectInfectReportInPatient.REPORT_START_DATE=A.UPLOAD_DATE>=TO_DATE(<REPORT_START_DATE>,'YYYYMMDDHH24MISS')
selectInfectReportInPatient.REPORT_END_DATE=A.UPLOAD_DATE<=TO_DATE(<REPORT_END_DATE>,'YYYYMMDDHH24MISS')
selectInfectReportInPatient.REGION_CODE=B.REGION_CODE = <REGION_CODE>
selectInfectReportInPatient.REPORT_DATE_NULL=A.UPLOAD_DATE IS NULL
selectInfectReportInPatient.REPORT_DATE_NOT_NULL=A.UPLOAD_DATE IS NOT NULL
selectInfectReportInPatient.ICD_CODE=A.ICD_CODE=<ICD_CODE>
selectInfectReportInPatient.DEPT_CODE=B.DEPT_CODE=<DEPT_CODE>
selectInfectReportInPatient.STATION_CODE=B.STATION_CODE=<STATION_CODE>
selectInfectReportInPatient.Debug=N

//查询传染病信息  门诊急诊
selectInfectReportOutPatient.Type=TSQL
selectInfectReportOutPatient.SQL=SELECT '' AS SELECT_FLG,CASE WHEN A.UPLOAD_DATE IS NULL THEN 'N' ELSE 'Y' END REPORT_FLG, &
			      B.DEPT_CODE,B.CLINICAREA_CODE,A.MR_NO,C.PAT_NAME, &
			      B.REG_DATE,B.REALDR_CODE,A.UPLOAD_DATE,A.PAD_DEPT,A.CARD_SEQ_NO &
		       FROM MRO_INFECT_RECORD A &
		       LEFT JOIN REG_PATADM B ON A.MR_NO = B.MR_NO &
		       LEFT JOIN SYS_PATINFO C ON A.MR_NO = C.MR_NO &
		       WHERE A.CASE_NO = B.CASE_NO &
		       ORDER BY REPORT_FLG DESC
selectInfectReportOutPatient.item=START_DATE;END_DATE;MR_NO;REPORT_START_DATE;REPORT_END_DATE;REGION_CODE;REPORT_DATE_NULL;REPORT_DATE_NOT_NULL;ICD_CODE;DEPT_CODE;CLINICAREA_CODE
selectInfectReportOutPatient.START_DATE=B.REG_DATE>=TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS')
selectInfectReportOutPatient.END_DATE=B.REG_DATE<=TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS')
selectInfectReportOutPatient.MR_NO=A.MR_NO=<MR_NO>
selectInfectReportOutPatient.REPORT_START_DATE=A.UPLOAD_DATE>=TO_DATE(<REPORT_START_DATE>,'YYYYMMDDHH24MISS')
selectInfectReportOutPatient.REPORT_END_DATE=A.UPLOAD_DATE<=TO_DATE(<REPORT_END_DATE>,'YYYYMMDDHH24MISS')
selectInfectReportOutPatient.REGION_CODE=B.REGION_CODE = <REGION_CODE>
selectInfectReportOutPatient.REPORT_DATE_NULL=A.UPLOAD_DATE IS NULL
selectInfectReportOutPatient.REPORT_DATE_NOT_NULL=A.UPLOAD_DATE IS NOT NULL
selectInfectReportOutPatient.ICD_CODE=A.ICD_CODE=<ICD_CODE>
selectInfectReportOutPatient.DEPT_CODE=B.DEPT_CODE=<DEPT_CODE>
selectInfectReportOutPatient.CLINICAREA_CODE=B.CLINICAREA_CODE=<CLINICAREA_CODE>
selectInfectReportOutPatient.Debug=N


//更新传染病报告卡信息
updateInfectReport.Type=TSQL
updateInfectReport.SQL=UPDATE MRO_INFECT_RECORD SET UPLOAD_DATE = TO_DATE(<UPLOAD_DATE>,'YYYYMMDDHH24MISS'), &
                                            UPLOAD_CODE = <UPLOAD_CODE>, &
                                            PAD_DEPT = <PAD_DEPT> &
                                      WHERE MR_NO = <MR_NO> &
                                      AND   CARD_SEQ_NO = <CARD_SEQ_NO>
updateInfectReport.Debug=N


//取消上传传染病报告卡信息
updateInfectReportCancel.Type=TSQL
updateInfectReportCancel.SQL=UPDATE MRO_INFECT_RECORD SET UPLOAD_DATE = NULL, &
                                            UPLOAD_CODE = <UPLOAD_CODE>, &
                                            PAD_DEPT = <PAD_DEPT> &
                                      WHERE MR_NO = <MR_NO> &
                                      AND   CARD_SEQ_NO = <CARD_SEQ_NO>
updateInfectReportCancel.Debug=N