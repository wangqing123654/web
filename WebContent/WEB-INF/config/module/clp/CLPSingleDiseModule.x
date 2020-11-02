# 
#  Title:������module
# 
#  Description:
# 
#  Copyright: Copyright (c) BlueCore 2012
# 
#  author WangLong 20120926
#  version 1.0
#
Module.item=queryPatDiagFromE;querySinglePatInfoFromE;queryPatInfoFromE;queryPatDiagFromI;querySinglePatInfoFromI;queryPatInfoFromI;queryASandPCFromI;&
            queryREGAdmSDInfo;queryADMResvSDInfo;queryADMInpSDInfo;updateREGAdmSDInfo;updateADMResvSDInfo;updateADMInpSDInfo;&
	        deleteREGAdmSDInfo;deleteADMResvSDInfo;deleteADMInpSDInfo;&
            queryEMRHistoryForMerge;clearSDFileHistory;copySDFileHistory;clearSDDBHistory;copySDDBHistory;querySDEMRFile;&
            deleteSDData;insertSDData;&
            querySDDataFromE;querySDDataFromI

//=========================����Ϊ �����ֲ�����ѯ����======================================================

//��ѯ���ﲡ���������
queryPatDiagFromE.Type=TSQL
queryPatDiagFromE.SQL=SELECT A.ICD_CODE, B.ICD_CHN_DESC &
                        FROM OPD_DIAGREC A, SYS_DIAGNOSIS B &
                       WHERE A.MAIN_DIAG_FLG = 'Y' AND A.ICD_CODE = B.ICD_CODE &
                         AND A.CASE_NO=<CASE_NO>
queryPatDiagFromE.Debug=Y

//��ѯ���ﵥ��������Ϣ
querySinglePatInfoFromE.Type=TSQL
querySinglePatInfoFromE.SQL=SELECT DISTINCT A.CASE_NO, A.MR_NO, B.PAT_NAME, B.SEX_CODE, A.CTZ1_CODE, A.REG_DATE, &
                                   A.REALDEPT_CODE AS DEPT_CODE, A.REALDR_CODE AS DR_CODE, A.DISE_CODE, B.BIRTH_DATE &
                              FROM REG_PATADM A, SYS_PATINFO B &
                             WHERE A.REGCAN_DATE IS NULL &
		                       AND A.MR_NO = B.MR_NO(+)
querySinglePatInfoFromE.item=MR_NO;CASE_NO;DEPT_CODE
querySinglePatInfoFromE.MR_NO=A.MR_NO=<MR_NO>
querySinglePatInfoFromE.CASE_NO=A.CASE_NO=<CASE_NO>
querySinglePatInfoFromE.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
querySinglePatInfoFromE.Debug=N


//��ѯ������������Ϣ
queryPatInfoFromE.Type=TSQL
queryPatInfoFromE.SQL=SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME, B.SEX_CODE, A.CTZ1_CODE, A.REG_DATE, &
                             A.REALDEPT_CODE AS DEPT_CODE, A.REALDR_CODE AS DR_CODE, A.DISE_CODE, B.BIRTH_DATE, D.ICD_CODE, D.ICD_CHN_DESC &
                        FROM REG_PATADM A, SYS_PATINFO B, OPD_DIAGREC C, SYS_DIAGNOSIS D &
                       WHERE A.REGCAN_DATE IS NULL &
		                 AND A.MR_NO = B.MR_NO(+) &
		                 AND A.CASE_NO = C.CASE_NO &
		                 AND C.MAIN_DIAG_FLG = 'Y' &
		                 AND C.ICD_CODE = D.ICD_CODE &
		                 AND A.REG_DATE BETWEEN <START_DATE> AND <END_DATE> &
                    ORDER BY A.REG_DATE DESC
queryPatInfoFromE.item=MR_NO;CASE_NO;DEPT_CODE
queryPatInfoFromE.MR_NO=A.MR_NO=<MR_NO>
queryPatInfoFromE.CASE_NO=A.CASE_NO=<CASE_NO>
queryPatInfoFromE.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
queryPatInfoFromE.Debug=N


//��ѯסԺ�����������
queryPatDiagFromI.Type=TSQL
queryPatDiagFromI.SQL=SELECT A.MAINDIAG AS ICD_CODE, B.ICD_CHN_DESC &
	                    FROM ADM_INP A, SYS_DIAGNOSIS B &
	                   WHERE A.MAINDIAG = B.ICD_CODE &
                         AND A.CASE_NO=<CASE_NO>
queryPatDiagFromI.Debug=N

//��ѯסԺ����������Ϣ
querySinglePatInfoFromI.Type=TSQL
querySinglePatInfoFromI.SQL=SELECT DISTINCT A.CASE_NO, A.MR_NO, A.IPD_NO, B.PAT_NAME, B.SEX_CODE, A.CTZ1_CODE, A.IN_DATE, A.DS_DATE AS OUT_DATE, &
		                           A.DEPT_CODE, A.VS_DR_CODE AS DR_CODE, A.TOTAL_AMT, B.BIRTH_DATE, A.DISE_CODE &
	                          FROM ADM_INP A, SYS_PATINFO B &
	                         WHERE A.IN_DATE BETWEEN <START_DATE> AND <END_DATE> &
                               AND A.MR_NO = B.MR_NO(+) 
querySinglePatInfoFromI.item=MR_NO;CASE_NO;DEPT_CODE
querySinglePatInfoFromI.MR_NO=A.MR_NO=<MR_NO>
querySinglePatInfoFromI.CASE_NO=A.CASE_NO=<CASE_NO>
querySinglePatInfoFromI.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
querySinglePatInfoFromI.Debug=N

//��ѯסԺ���������Ϣ
queryPatInfoFromI.Type=TSQL
queryPatInfoFromI.SQL=SELECT A.CASE_NO, A.MR_NO, A.IPD_NO, B.PAT_NAME, B.SEX_CODE, A.CTZ1_CODE, A.IN_DATE, A.DS_DATE AS OUT_DATE, &
		                     A.DEPT_CODE, A.VS_DR_CODE, A.TOTAL_AMT, B.BIRTH_DATE, A.DISE_CODE, C.ICD_CODE, C.ICD_CHN_DESC &
	                    FROM ADM_INP A, SYS_PATINFO B, SYS_DIAGNOSIS C &
	                   WHERE A.IN_DATE BETWEEN <START_DATE> AND <END_DATE> &
	                     AND A.MR_NO = B.MR_NO(+) &
	                     AND A.MAINDIAG = C.ICD_CODE &
	                ORDER BY IN_DATE DESC
queryPatInfoFromI.item=MR_NO;CASE_NO;DEPT_CODE
queryPatInfoFromI.MR_NO=A.MR_NO=<MR_NO>
queryPatInfoFromI.CASE_NO=A.CASE_NO=<CASE_NO>
queryPatInfoFromI.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
queryPatInfoFromI.Debug=N


//��ѯסԺ��������Դ����Ժ״̬
queryASandPCFromI.Type=TSQL
queryASandPCFromI.SQL=SELECT ADM_SOURCE, PATIENT_CONDITION FROM ADM_INP WHERE CASE_NO = <CASE_NO>
queryASandPCFromI.Debug=N

//=========================����Ϊ ����ҽ��վ��סԺԤԼ���Ǽǡ�סԺҽ��վ�ĵ����ֲ���======================================================

//��ѯ�Һű��еĵ���������
queryREGAdmSDInfo.Type=TSQL
queryREGAdmSDInfo.SQL=SELECT DISE_CODE FROM REG_PATADM WHERE CASE_NO = <CASE_NO>
queryREGAdmSDInfo.Debug=N

//��ѯԤԼסԺ���еĵ���������
queryADMResvSDInfo.Type=TSQL
queryADMResvSDInfo.SQL=SELECT DISE_CODE FROM ADM_RESV WHERE MR_NO = <MR_NO> ORDER BY RESV_NO DESC
queryADMResvSDInfo.Debug=N

//��ѯסԺ�����еĵ���������
queryADMInpSDInfo.Type=TSQL
queryADMInpSDInfo.SQL=SELECT DISE_CODE FROM ADM_INP WHERE MR_NO = <MR_NO> ORDER BY CASE_NO DESC
queryADMInpSDInfo.Debug=N

//���뼱�ﵥ������Ϣ
updateREGAdmSDInfo.Type=TSQL
updateREGAdmSDInfo.SQL=UPDATE REG_PATADM SET DISE_CODE = <DISE_CODE> WHERE CASE_NO = <CASE_NO>
updateREGAdmSDInfo.Debug=N

//����ԤԼסԺ��������Ϣ
updateADMResvSDInfo.Type=TSQL
updateADMResvSDInfo.SQL=UPDATE ADM_RESV SET DISE_CODE = <DISE_CODE> WHERE RESV_NO = <RESV_NO>
updateADMResvSDInfo.Debug=N

//����סԺ������������Ϣ
updateADMInpSDInfo.Type=TSQL
updateADMInpSDInfo.SQL=UPDATE ADM_INP SET DISE_CODE = <DISE_CODE> WHERE CASE_NO = <CASE_NO>
updateADMInpSDInfo.Debug=N

//ɾ���Һű��еĵ�������Ϣ
deleteREGAdmSDInfo.Type=TSQL
deleteREGAdmSDInfo.SQL=UPDATE REG_PATADM SET DISE_CODE = '' WHERE CASE_NO = <CASE_NO>
deleteREGAdmSDInfo.Debug=N

//ɾ��ԤԼסԺ���еĵ�������Ϣ
deleteADMResvSDInfo.Type=TSQL
deleteADMResvSDInfo.SQL=UPDATE ADM_RESV SET DISE_CODE = '' WHERE CASE_NO = <CASE_NO>
deleteADMResvSDInfo.Debug=N

//ɾ��סԺ�����еĵ�������Ϣ
deleteADMInpSDInfo.Type=TSQL
deleteADMInpSDInfo.SQL=UPDATE ADM_INP SET DISE_CODE = '' WHERE CASE_NO = <CASE_NO>
deleteADMInpSDInfo.Debug=N

//=========================����Ϊ �����ֺϲ�����======================================================

//��ȡ���ﲡ�����λ�����Ϣ�����ڡ��ϲ�����������ʾ��
queryEMRHistoryForMerge.Type=TSQL
queryEMRHistoryForMerge.SQL=SELECT DISTINCT CASE WHEN C.CASE_NO_E IS NOT NULL THEN 'Y' ELSE 'N' END AS MERGE_FLG, &
                                   A.CASE_NO, A.MR_NO, B.PAT_NAME, B.SEX_CODE, A.CTZ1_CODE, A.REG_DATE, A.DEPT_CODE, A.DR_CODE, A.DISE_CODE &
                              FROM REG_PATADM A, SYS_PATINFO B, CLP_DISE C &
		                     WHERE A.MR_NO = <MR_NO> &
		                       AND A.REGCAN_DATE IS NULL &
		                       AND A.DISE_CODE IS NOT NULL &
		                       AND A.ADM_TYPE = 'E' &
		                       AND A.MR_NO = B.MR_NO(+) &
		                       AND A.CASE_NO < <CASE_NO> &
		                       AND A.CASE_NO = C.CASE_NO_E(+) &
		                  ORDER BY A.REG_DATE DESC
queryEMRHistoryForMerge.Debug=N

//������������¼���
//clearSDDBHistory.Type=TSQL
//clearSDDBHistory.SQL=DELETE FROM CLP_DISE WHERE CASE_NO_E = <CASE_NO_OLD>
//clearSDDBHistory.Debug=N

//�����ֲ����ļ���¼���
//clearSDFileHistory.Type=TSQL
//clearSDFileHistory.SQL=DELETE FROM EMR_FILE_INDEX B &
//                             WHERE EXISTS (SELECT 1 FROM CLP_DISE WHERE A.CASE_NO = B.CASE_NO AND A.SEQ = B.FILE_SEQ AND A.CASE_NO = <CASE_NO_OLD> )
//clearSDFileHistory.Debug=N

//�����ֲ����ļ���¼�ϲ�
copySDFileHistory.Type=TSQL
copySDFileHistory.SQL=INSERT INTO EMR_FILE_INDEX (CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH, FILE_NAME, &
                                                  DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, DISPOSAC_FLG, &
                                                  CREATOR_USER, CREATOR_DATE, OPT_USER, OPT_DATE, OPT_TERM, REPORT_FLG) &
                          SELECT <CASE_NO>,(SELECT CASE WHEN NVL (MAX(FILE_SEQ), 0) = 0 THEN 1 ELSE MAX(FILE_SEQ) + 1 END &
                                              FROM EMR_FILE_INDEX &
                                             WHERE CASE_NO = <CASE_NO>&
                                              )+ROWNUM AS FILE_SEQ,MR_NO,IPD_NO,&
		                        'JHW\'||substr(<CASE_NO>,1,2)||'\'||substr(<CASE_NO>,3,2)||'\'||MR_NO AS FILE_PATH,&
		                         <CASE_NO>||substr(FILE_NAME,13) AS FILE_NAME,DESIGN_NAME,CLASS_CODE, SUBCLASS_CODE,DISPOSAC_FLG,&
		                         CREATOR_USER,CREATOR_DATE,OPT_USER,OPT_DATE,OPT_TERM,REPORT_FLG &
                            FROM EMR_FILE_INDEX &
                           WHERE CASE_NO = <CASE_NO_OLD> &
                             AND (SUBCLASS_CODE LIKE 'EMR800102%' OR SUBCLASS_CODE LIKE 'EMR800103%')
copySDFileHistory.Debug=N

//������������¼�ϲ�
copySDDBHistory.Type=TSQL
copySDDBHistory.SQL=INSERT INTO CLP_DISE (SEQ, CASE_NO, MR_NO, IPD_NO, CASE_NO_E, FILE_SEQ_E, ADM_TYPE, DISE_CODE, &
                                          PAT_NAME, SEX_CODE, AGE, IN_DATE, OUT_DATE, STAY_DAYS, &
                                          ICD_CODE, ICD_CHN_DESC, TBYS, OPT_USER, OPT_DATE, OPT_TERM, FILE_PATH, FILE_NAME) &
                         SELECT (SELECT CASE WHEN NVL (MAX(SEQ), 0) = 0 THEN 1 ELSE MAX(SEQ) + 1 END &
                                              FROM CLP_DISE &
                                             WHERE CASE_NO = <CASE_NO>&
                                              )+ROWNUM AS SEQ, <CASE_NO>, A.MR_NO, A.IPD_NO, <CASE_NO_OLD>,B.FILE_SEQ ,A.ADM_TYPE, A.DISE_CODE, A.PAT_NAME, & 
                                A.SEX_CODE, A.AGE, A.IN_DATE, A.OUT_DATE, A.STAY_DAYS, A.ICD_CODE, A.ICD_CHN_DESC, & 
                                A.TBYS, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, &
			                   'JHW\'||substr(<CASE_NO>,1,2)||'\'||substr(<CASE_NO>,3,2)||'\'||A.MR_NO AS FILE_PATH,&
		                       <CASE_NO>||substr(B.FILE_NAME,13) AS FILE_NAME &
                          FROM CLP_DISE A,EMR_FILE_INDEX B &
                         WHERE A.CASE_NO = <CASE_NO_OLD> &
		                   AND A.ADM_TYPE = 'E' &
			               AND (B.SUBCLASS_CODE LIKE 'EMR800102%' OR B.SUBCLASS_CODE LIKE 'EMR800103%') &
		                   AND A.CASE_NO = B.CASE_NO &
                           AND A.FILE_NAME = B.FILE_NAME
copySDDBHistory.Debug=N


//��ѯ���������β����ļ�
querySDEMRFile.Type=TSQL
querySDEMRFile.SQL=SELECT MR_NO, FILE_PATH, FILE_NAME &
                     FROM EMR_FILE_INDEX &
                    WHERE CASE_NO=<CASE_NO_OLD> &
                      AND (SUBCLASS_CODE LIKE 'EMR800102%' OR SUBCLASS_CODE LIKE 'EMR800103%')
querySDEMRFile.Debug=N

//=========================����Ϊ ���������ֲ���======================================================

//ɾ�������ֲ�����Ϣ
deleteSDData.Type=TSQL
deleteSDData.SQL=delete from CLP_DISE where FILE_NAME = <FILE_NAME>
deleteSDData.Debug=N

//���뵥���ֲ�����Ϣ
insertSDData.Type=TSQL
insertSDData.SQL=INSERT INTO CLP_DISE (SEQ, CASE_NO, MR_NO, IPD_NO, ADM_TYPE, DISE_CODE, PAT_NAME, SEX_CODE, AGE, IN_DATE, OUT_DATE, &
                                       STAY_DAYS, ICD_CODE, ICD_CHN_DESC, TBYS, OPT_USER, OPT_DATE, OPT_TERM, FILE_PATH, FILE_NAME) &
                      SELECT FILE_SEQ AS SEQ, &
		                      <CASE_NO>, <MR_NO>, <IPD_NO>, <ADM_TYPE>, <DISE_CODE>, <PAT_NAME>, <SEX_CODE>, <AGE>, <IN_DATE>, <OUT_DATE>, &
			                  <STAY_DAYS>, <ICD_CODE>, <ICD_CHN_DESC>, <TBYS>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>, <FILE_PATH>, <FILE_NAME> &
                        FROM EMR_FILE_INDEX &
                       WHERE CASE_NO = <CASE_NO> &
                         AND FILE_PATH = <FILE_PATH> &
                         AND FILE_NAME = <FILE_NAME>
insertSDData.Debug=N

//=========================����Ϊ ������ͳ�Ʋ���======================================================
//��ѯ���ﵥ���ֲ�����Ϣ(����ͳ�ƴ�����ʾ)
querySDDataFromE.Type=TSQL
querySDDataFromE.SQL=SELECT DISTINCT A.CASE_NO, A.MR_NO, A.IPD_NO, A.ADM_TYPE, A.DISE_CODE, &
                            A.PAT_NAME, A.SEX_CODE, A.AGE, A.IN_DATE, A.OUT_DATE, A.STAY_DAYS, &
                            A.ICD_CODE, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, B.REG_DATE &
                       FROM CLP_DISE A, REG_PATADM B &
		              WHERE A.ADM_TYPE='E' &
		                AND A.CASE_NO = B.CASE_NO &
		                AND B.REG_DATE BETWEEN <START_DATE> AND <END_DATE>
querySDDataFromE.item=ADM_TYPE;DISE_CODE
querySDDataFromE.ADM_TYPE=A.ADM_TYPE = <ADM_TYPE>
querySDDataFromE.DISE_CODE=A.DISE_CODE = <DISE_CODE>
querySDDataFromE.Debug=N

//��ѯסԺ�����ֲ�����Ϣ(����ͳ�ƴ�����ʾ)
querySDDataFromI.Type=TSQL
querySDDataFromI.SQL=SELECT DISTINCT CASE_NO, MR_NO, IPD_NO, ADM_TYPE, DISE_CODE, &
                            PAT_NAME, SEX_CODE, AGE, IN_DATE, OUT_DATE, STAY_DAYS, ICD_CODE, &
                            OPT_USER, OPT_DATE, OPT_TERM, '' AS REG_DATE &
                       FROM CLP_DISE &
                      WHERE ADM_TYPE='I' &
		                AND IN_DATE BETWEEN <START_DATE> AND <END_DATE>
querySDDataFromI.item=ADM_TYPE;DISE_CODE
querySDDataFromI.ADM_TYPE=ADM_TYPE = <ADM_TYPE>
querySDDataFromI.DISE_CODE=DISE_CODE = <DISE_CODE>
querySDDataFromI.Debug=N





