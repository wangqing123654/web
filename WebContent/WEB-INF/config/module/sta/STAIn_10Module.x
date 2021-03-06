# 
#  Title: STA_IN_10סԺҽʦҽ������
# 
#  Description: STA_IN_10סԺҽʦҽ������
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.26
#  version 1.0
#
Module.item=selectDATA_01;selectDATA_03;selectDATA_05;selectDATA_07;selectDATA_09;selectDATA_11;selectDATA_13;selectDATA_17;selectDATA_18;selectDATA_19;selectDATA_20;selectDATA_21;selectCheckNum;selectHospCharge;selectChargeMapping;selectDrByDept;selectDr

//��Ժ���˴�,ƽ��סԺ��
//===================pangben modify 20110525 
selectDATA_01.Type=TSQL
selectDATA_01.SQL=SELECT COUNT(A.CASE_NO) AS NUM,AVG(A.OUT_DATE-A.IN_DATE) AS INDAYS,A.VS_DR_CODE AS DR_CODE &
			FROM MRO_RECORD A,SYS_OPERATOR B &
			WHERE A.VS_DR_CODE=B.USER_ID AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY A.VS_DR_CODE
selectDATA_01.item=REGION_CODE
selectDATA_01.REGION_CODE=B.REGION_CODE=<REGION_CODE>
selectDATA_01.Debug=N

//��������
//===================pangben modify 20110525 
selectDATA_03.Type=TSQL
selectDATA_03.SQL=SELECT COUNT(A.CASE_NO) AS NUM,A.VS_DR_CODE AS DR_CODE &
			FROM MRO_RECORD A,SYS_OPERATOR B &
			WHERE A.VS_DR_CODE=B.USER_ID AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND A.CODE1_STATUS = '1' &
			GROUP BY A.VS_DR_CODE
selectDATA_03.item=REGION_CODE
selectDATA_03.REGION_CODE=B.REGION_CODE=<REGION_CODE>
selectDATA_03.Debug=N

//��ת����
//===================pangben modify 20110525 
selectDATA_05.Type=TSQL
selectDATA_05.SQL=SELECT COUNT(A.CASE_NO) AS NUM,A.VS_DR_CODE AS DR_CODE &
			FROM MRO_RECORD A,SYS_OPERATOR B &
			WHERE A.VS_DR_CODE=B.USER_ID AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND A.CODE1_STATUS = '2' &
			GROUP BY A.VS_DR_CODE 
selectDATA_05.item=REGION_CODE
selectDATA_05.REGION_CODE=B.REGION_CODE=<REGION_CODE>
selectDATA_05.Debug=N

//��������
//===================pangben modify 20110525 
selectDATA_07.Type=TSQL
selectDATA_07.SQL=SELECT COUNT(A.CASE_NO) AS NUM,A.VS_DR_CODE AS DR_CODE &
			FROM MRO_RECORD A,SYS_OPERATOR B &
			WHERE A.VS_DR_CODE=B.USER_ID AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND A.CODE1_STATUS = '4'  &
			GROUP BY A.VS_DR_CODE
selectDATA_07.item=REGION_CODE
selectDATA_07.REGION_CODE=B.REGION_CODE=<REGION_CODE>
selectDATA_07.Debug=N

//δ������
//===================pangben modify 20110525 
selectDATA_09.Type=TSQL
selectDATA_09.SQL=SELECT COUNT(CASE_NO) AS NUM,VS_DR_CODE AS DR_CODE &
			FROM MRO_RECORD A,SYS_OPERATOR B &
			WHERE A.VS_DR_CODE=B.USER_ID AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND A.CODE1_STATUS = '3' &
			GROUP BY A.VS_DR_CODE
selectDATA_09.item=REGION_CODE
selectDATA_09.REGION_CODE=B.REGION_CODE=<REGION_CODE>
selectDATA_09.Debug=N

//��������
//===================pangben modify 20110525 
selectDATA_11.Type=TSQL
selectDATA_11.SQL=SELECT COUNT(A.CASE_NO) AS NUM,A.VS_DR_CODE AS DR_CODE &
			FROM MRO_RECORD A,SYS_OPERATOR B &
			WHERE A.VS_DR_CODE=B.USER_ID AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND A.CODE1_STATUS = '5' &
			GROUP BY A.VS_DR_CODE
selectDATA_11.item=REGION_CODE
selectDATA_11.REGION_CODE=B.REGION_CODE=<REGION_CODE>			
selectDATA_11.Debug=N

//���úϼ�
//===================pangben modify 20110525 
selectDATA_13.Type=TSQL
selectDATA_13.SQL=SELECT SUM(A.CHARGE_01) AS CHARGE_01,SUM(A.CHARGE_02) AS CHARGE_02,SUM(A.CHARGE_03) AS CHARGE_03,SUM(A.CHARGE_04) AS CHARGE_04,SUM(A.CHARGE_05) AS CHARGE_05, &
			SUM(A.CHARGE_07) AS CHARGE_07,SUM(A.CHARGE_09) AS CHARGE_09,SUM(A.CHARGE_10) AS CHARGE_10,SUM(A.CHARGE_11) AS CHARGE_11,SUM(A.CHARGE_13) AS CHARGE_13, &
			SUM(A.CHARGE_17) AS CHARGE_17 ,A.VS_DR_CODE AS DR_CODE &
			FROM MRO_RECORD A,SYS_OPERATOR B &
			WHERE  A.VS_DR_CODE=B.USER_ID AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY A.VS_DR_CODE
selectDATA_13.item=REGION_CODE
selectDATA_13.REGION_CODE=B.REGION_CODE=<REGION_CODE>	
selectDATA_13.Debug=N

// ����90 �ֲ�����
//===================pangben modify 20110525 
selectDATA_17.Type=TSQL
selectDATA_17.SQL=SELECT COUNT(CASE_NO) AS NUM,VS_CODE AS DR_CODE &
			FROM  &
			(SELECT SUM(A.DEDUCT_SCORE) AS DEDUCT_SCORE,A.VS_CODE,A.CASE_NO &
			FROM MRO_CHRTVETREC A,MRO_RECORD B,SYS_OPERATOR C &
			WHERE A.CASE_NO=B.CASE_NO &
			AND B.VS_DR_CODE=C.USER_ID &
			AND C.REGION_CODE=<REGION_CODE> &
			AND B.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY A.VS_CODE,A.CASE_NO) &
			WHERE 100-DEDUCT_SCORE >90 &
			GROUP BY VS_CODE
selectDATA_17.Debug=N

// ����80 �ֲ�����
//===================pangben modify 20110525 
selectDATA_18.Type=TSQL
selectDATA_18.SQL=SELECT COUNT(CASE_NO) AS NUM,VS_CODE AS DR_CODE &
			FROM  &
			(SELECT SUM(A.DEDUCT_SCORE) AS DEDUCT_SCORE,A.VS_CODE,A.CASE_NO &
			FROM MRO_CHRTVETREC A,MRO_RECORD B,SYS_OPERATOR C &
			WHERE A.CASE_NO=B.CASE_NO &
			AND B.VS_DR_CODE=C.USER_ID &
			AND C.REGION_CODE=<REGION_CODE> &
			AND B.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY A.VS_CODE,A.CASE_NO) &
			WHERE 100-DEDUCT_SCORE >80 &
			GROUP BY VS_CODE
selectDATA_18.Debug=N

// ����70 �ֲ�����
//===================pangben modify 20110525 
selectDATA_19.Type=TSQL
selectDATA_19.SQL=SELECT COUNT(CASE_NO) AS NUM,VS_CODE AS DR_CODE &
			FROM  &
			(SELECT SUM(A.DEDUCT_SCORE) AS DEDUCT_SCORE,A.VS_CODE,A.CASE_NO &
			FROM MRO_CHRTVETREC A,MRO_RECORD B,SYS_OPERATOR C &
			WHERE A.CASE_NO=B.CASE_NO &
			AND B.VS_DR_CODE=C.USER_ID &
			AND C.REGION_CODE=<REGION_CODE> &
			AND B.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY A.VS_CODE,A.CASE_NO) &
			WHERE 100-DEDUCT_SCORE >70 &
			GROUP BY VS_CODE
selectDATA_19.Debug=N

//70 �����²�����
//===================pangben modify 20110525 
selectDATA_20.Type=TSQL
selectDATA_20.SQL=SELECT COUNT(CASE_NO) AS NUM,VS_CODE AS DR_CODE &
			FROM  &
			(SELECT SUM(A.DEDUCT_SCORE) AS DEDUCT_SCORE,A.VS_CODE,A.CASE_NO &
			FROM MRO_CHRTVETREC A,MRO_RECORD B,SYS_OPERATOR C &
			WHERE A.CASE_NO=B.CASE_NO &
			AND B.VS_DR_CODE=C.USER_ID &
			AND C.REGION_CODE=<REGION_CODE> &
			AND B.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY A.VS_CODE,A.CASE_NO) &
			WHERE 100-DEDUCT_SCORE <70 &
			GROUP BY VS_CODE
selectDATA_20.Debug=N

//����ƽ����
//===================pangben modify 20110525 
selectDATA_21.Type=TSQL
selectDATA_21.SQL=SELECT AVG(100-DEDUCT_SCORE) AS NUM,VS_CODE AS DR_CODE &
			FROM  &
			(SELECT SUM(A.DEDUCT_SCORE) AS DEDUCT_SCORE,A.VS_CODE,A.CASE_NO &
			FROM MRO_CHRTVETREC A,MRO_RECORD B,SYS_OPERATOR C &
			WHERE A.CASE_NO=B.CASE_NO &
			AND B.VS_DR_CODE=C.USER_ID &
			AND C.REGION_CODE=<REGION_CODE> &
			AND B.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY A.VS_CODE,A.CASE_NO) &
			GROUP BY VS_CODE
selectDATA_21.Debug=N

//����������
selectCheckNum.Type=TSQL
selectCheckNum.SQL=SELECT A.VS_CODE AS DR_CODE,COUNT(DISTINCT(A.CASE_NO)) AS NUM &
			FROM MRO_CHRTVETREC A,MRO_RECORD B,SYS_OPERATOR C &
			WHERE A.CASE_NO=B.CASE_NO &
			AND B.VS_DR_CODE=C.USER_ID &
			AND B.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY A.VS_CODE
selectCheckNum.item=REGION_CODE
selectCheckNum.REGION_CODE=C.REGION_CODE=<REGION_CODE>			
selectCheckNum.Debug=N


//��ѯסԺ���� add by wanglong 20131012
selectHospCharge.Type=TSQL
selectHospCharge.SQL=SELECT B.VS_DR_CODE DR_CODE, A.HEXP_CODE, C.IPD_CHARGE_CODE, SUM(A.TOT_AMT) TOT_AMT, SUM(A.OWN_AMT) OWN_AMT &
                       FROM IBS_ORDD A, MRO_RECORD B, SYS_CHARGE_HOSP C &
                      WHERE B.REGION_CODE = <REGION_CODE> &
                        AND A.CASE_NO = B.CASE_NO &
                        AND B.OUT_DATE BETWEEN TO_DATE(<START_DATE>, 'YYYYMMDD') AND TO_DATE(<END_DATE> || '235959', 'YYYYMMDDHH24MISS') &
                        AND A.HEXP_CODE(+) = C.CHARGE_HOSP_CODE &
                  GROUP BY B.VS_DR_CODE, A.HEXP_CODE, C.IPD_CHARGE_CODE &
		  ORDER BY B.VS_DR_CODE, A.HEXP_CODE
selectHospCharge.Debug=N

//��ѯסԺ���� add by wanglong 20131012
selectChargeMapping.Type=TSQL
selectChargeMapping.SQL=SELECT * FROM BIL_RECPPARM WHERE RECP_TYPE = <RECP_TYPE>
selectChargeMapping.Debug=N


//���ݿ��Ҳ�ѯҽʦ
selectDrByDept.Type=TSQL
selectDrByDept.SQL=SELECT A.USER_NAME,A.USER_ID,A.PY1,B.DEPT_CODE &
			FROM SYS_OPERATOR A &
			LEFT JOIN SYS_OPERATOR_DEPT B ON A.USER_ID=B.USER_ID &
			WHERE A.USER_ID=B.USER_ID &
			AND A.ROLE_ID IN ('ODO','ODI','OIDR') &
			AND B.MAIN_FLG='Y' &
			AND B.DEPT_CODE = <DEPT_CODE>
selectDrByDept.item=REGION_CODE
selectDrByDept.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectDrByDept.Debug=N

//��ѯ����ҽʦ
selectDr.Type=TSQL
selectDr.SQL=SELECT A.USER_NAME,A.USER_ID,A.PY1 &
		FROM SYS_OPERATOR A &
		WHERE A.ROLE_ID IN ('ODO','ODI','OIDR') &
		AND A.END_DATE >SYSDATE &
		AND A.ACTIVE_DATE < SYSDATE 
selectDr.item=REGION_CODE
selectDr.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectDr.Debug=N