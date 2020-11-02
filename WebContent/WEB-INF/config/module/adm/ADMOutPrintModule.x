##################################################
# <p>Title:��Ժͳ�Ʊ��� </p>
#
# <p>Description:��Ժͳ�Ʊ��� </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk 2009-10-29
# @version 4.0
##################################################
Module.item=selectOutHosp

//��ѯ��Ժ������Ϣ
selectOutHosp.Type=TSQL
selectOutHosp.SQL=SELECT F.REGION_CHN_ABN,A.MR_NO,A.IPD_NO,B.DEPT_CHN_DESC,H.STATION_DESC,C.CTZ_DESC,D.PAT_NAME, &
			    E.CHN_DESC,'' AS INS, SUM(G.AR_AMT) TOTAL_AMT,A.DS_DATE,A.CASE_NO,A.BILL_DATE AS INS_DATE,G.CASHIER_CODE AS INS_USER &
			    FROM ADM_INP A,SYS_DEPT B,SYS_CTZ C,SYS_PATINFO D,SYS_DICTIONARY E,SYS_REGION F,SYS_STATION H,BIL_IBS_RECPM G &
			    WHERE A.DS_DEPT_CODE = B.DEPT_CODE(+) &
			    AND A.CTZ1_CODE=C.CTZ_CODE &
			    AND A.MR_NO = D.MR_NO &
			    AND A.REGION_CODE=F.REGION_CODE(+) &
			    AND D.SEX_CODE = E.ID(+) &
			    AND E.GROUP_ID='SYS_SEX'  &
			    AND A.IN_STATION_CODE = H.STATION_CODE &
			    AND A.CASE_NO=G.CASE_NO(+) &
			    AND DS_DATE IS NOT NULL &
			    AND A.DS_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &  
			    GROUP BY F.REGION_CHN_ABN,A.MR_NO,A.IPD_NO,B.DEPT_CHN_DESC,H.STATION_DESC,C.CTZ_DESC,D.PAT_NAME,E.CHN_DESC, &
			    A.DS_DATE,A.CASE_NO,A.BILL_DATE,G.CASHIER_CODE
selectOutHosp.item=CTZ1_CODE;REGION_CODE
selectOutHosp.CTZ1_CODE=A.CTZ1_CODE=<CTZ1_CODE>
//========pangben modify 20110510 
selectInHosp.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectOutHosp.Debug=N