# 
#  Title:药品不良事件module
# 
#  Description:药品不良事件module
# 
#  Copyright: Copyright (c) BlueCore 2013
# 
#  author wanglong 2013.09.30
#  version 1.0
#
Module.item=queryADRData;deleteADRMData;deleteADRDData;updateADRExamineState;updateADRReportState


//查询药品不良事件
queryADRData.Type=TSQL
queryADRData.SQL=SELECT A.ACI_NO,CASE WHEN A.FIRST_FLG='0' THEN 'Y' ELSE 'N' END FIRST_FLG, A.REPORT_TYPE, A.ADM_TYPE, &
                        A.CASE_NO, A.MR_NO, A.PAT_NAME, A.SEX_CODE, A.SPECIES_CODE, A.WEIGHT, A.TEL, &
                        A.BIRTH_DATE, FLOOR(MONTHS_BETWEEN(SYSDATE, A.BIRTH_DATE) / 12) AS AGE, &
                        A.DIAG_CODE1, A.DIAG_CODE2, A.DIAG_CODE3, A.DIAG_CODE4, A.DIAG_CODE5, &
                        A.PERSON_HISTORY, A.PERSON_REMARK, A.FAMILY_HISTORY, A.FAMILY_REMARK, &
                        A.SMOKE_FLG, A.DRINK_FLG, A.PREGNANT_FLG, A.HEPATOPATHY_FLG, A.NEPHROPATHY_FLG, &
                        A.ALLERGY_FLG, A.ALLERGY_REMARK, A.OTHER_FLG, A.OTHER_REMARK, &
                        A.ADR_ID1, A.ADR_DESC1, A.ADR_ID2, A.ADR_ID3, A.ADR_ID4, &
                        TRIM(REPLACE(A.ADR_DESC1,' ','') || ' ' || &
                        REPLACE((SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID2 = C.ADR_ID),' ','') || ' ' || &
                        REPLACE((SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID3 = C.ADR_ID),' ','') || ' ' || &
                        REPLACE((SELECT C.ADR_DESC FROM ACI_ADRNAME C WHERE A.ADR_ID4 = C.ADR_ID),' ','')) AS ADR_DESC, &
                        A.EVENT_DATE, A.EVENT_DESC, A.EVENT_RESULT, A.RESULT_REMARK, A.DIED_DATE, &
                        A.STOP_REDUCE, A.AGAIN_APPEAR, A.DISE_DISTURB, A.USER_REVIEW, A.UNIT_REVIEW, &
                        A.REPORT_USER, A.REPORT_OCC, A.OCC_REMARK, A.REPORT_TEL, A.REPORT_EMAIL, &
                        A.REPORT_DEPT, A.REPORT_STATION, A.REPORT_DATE, &
                        A.REPORT_UNIT, A.UNIT_CONTACTS,A.UNIT_TEL, &
                        A.UPLOAD_FLG, A.UPLOAD_DATE, &
                        REPLACE(WM_CONCAT(B.ORDER_DESC), ',', ';') ORDER_DESC &
                   FROM ACI_ADRM A, ACI_ADRD B &
                  WHERE A.ACI_NO = B.ACI_NO &
                    AND A.REPORT_DATE BETWEEN <START_DATE> AND <END_DATE> &
               GROUP BY A.ACI_NO, A.FIRST_FLG, A.REPORT_TYPE, A.ADM_TYPE, A.CASE_NO, A.MR_NO, A.PAT_NAME, A.SEX_CODE, A.SPECIES_CODE, A.WEIGHT, A.TEL, &
                        A.BIRTH_DATE, A.DIAG_CODE1, A.DIAG_CODE2, A.DIAG_CODE3, A.DIAG_CODE4, A.DIAG_CODE5, &
                        A.PERSON_HISTORY, A.PERSON_REMARK, A.FAMILY_HISTORY, A.FAMILY_REMARK, &
                        A.SMOKE_FLG, A.DRINK_FLG, A.PREGNANT_FLG, A.HEPATOPATHY_FLG, A.NEPHROPATHY_FLG, &
                        A.ALLERGY_FLG, A.ALLERGY_REMARK, A.OTHER_FLG, A.OTHER_REMARK, &
                        A.ADR_ID1, A.ADR_DESC1, A.ADR_ID2, A.ADR_ID3, A.ADR_ID4, &
                        A.EVENT_DATE, A.EVENT_DESC, A.EVENT_RESULT, A.RESULT_REMARK, A.DIED_DATE, A.STOP_REDUCE, A.AGAIN_APPEAR, A.DISE_DISTURB, &
                        A.USER_REVIEW, A.UNIT_REVIEW, A.REPORT_USER, A.REPORT_OCC, A.OCC_REMARK, &
                        A.REPORT_TEL, A.REPORT_EMAIL, A.REPORT_DEPT, A.REPORT_STATION, A.REPORT_DATE, &
                        A.REPORT_UNIT, A.UNIT_CONTACTS, A.UNIT_TEL, &
                        A.UPLOAD_FLG, A.UPLOAD_DATE &
               ORDER BY A.ACI_NO DESC
queryADRData.item=UNREPORTED;REPORTED;REPORT_DEPT;REPORT_TYPE;ADR_ID;MEDI_TYPE
queryADRData.UNREPORTED=A.UPLOAD_DATE IS NULL
queryADRData.REPORTED=A.UPLOAD_DATE IS NOT NULL
queryADRData.REPORT_DEPT=A.REPORT_DEPT = <REPORT_DEPT>
queryADRData.REPORT_TYPE=A.REPORT_TYPE = <REPORT_TYPE>
queryADRData.ADR_ID=A.ADR_ID1 = <ADR_ID>
queryADRData.MEDI_TYPE=B.ORDER_CODE like '<MEDI_TYPE>%'
queryADRData.Debug=N

//删除不良事件主档数据
deleteADRMData.Type=TSQL
deleteADRMData.SQL=DELETE FROM ACI_ADRM WHERE ACI_NO = <ACI_NO>
deleteADRMData.Debug=N

//删除不良事件细档数据
deleteADRDData.Type=TSQL
deleteADRDData.SQL=DELETE FROM ACI_ADRD WHERE ACI_NO = <ACI_NO>
deleteADRDData.Debug=N

//已上报/取消已上报
updateADRReportState.Type=TSQL
updateADRReportState.SQL=UPDATE ACI_ADRM SET UPLOAD_FLG = <UPLOAD_FLG>, UPLOAD_DATE = <UPLOAD_DATE> WHERE ACI_NO = <ACI_NO>
updateADRReportState.Debug=N

