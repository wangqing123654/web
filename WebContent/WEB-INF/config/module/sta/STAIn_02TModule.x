# 
#  Title: STA_IN_02ҽԺ�š����﹤��ͳ��̨��
# 
#  Description: STA_IN_02ҽԺ�š����﹤��ͳ��̨��
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.07.01
#  version 1.0
#
Module.item=selectBQ;selectQS

//�������ݲ�ѯ���
selectBQ.Type=TSQL
selectBQ.SQL=SELECT &
		   STA_DATE, DEPT_CODE, STATION_CODE, &
		   DATA_02, DATA_03, DATA_04, &
		   DATA_05, DATA_06, DATA_07, &
		   DATA_08, DATA_09, DATA_10, &
		   DATA_11, DATA_12, DATA_13, &
		   DATA_14, DATA_15, DATA_16, &
		   DATA_17, DATA_18, DATA_19, &
		   DATA_20, DATA_21, DATA_22, &
		   DATA_23, DATA_24, DATA_25, &
		   DATA_26, DATA_27, DATA_28, &
		   DATA_29, DATA_30, DATA_31, &
		   DATA_32, DATA_33, DATA_34, &
		   CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, &
		   OPT_USER, OPT_DATE, OPT_TERM &
		FROM STA_IN_02 
selectBQ.item=Month;Year;Dept;REGION_CODE
selectBQ.Month=STA_DATE=<Month>
selectBQ.Year=SUBSTR(STA_DATE,0,4) = <Year>
selectBQ.Dept=DEPT_CODE LIKE <Dept>||'%'
//=============pangben modify 20110523 start
selectBQ.REGION_CODE=REGION_CODE=<REGION_CODE>
//=============pangben modify 20110523 stop
selectBQ.Debug=N


//�������ݲ�ѯ���
selectQS.Type=TSQL
selectQS.SQL=SELECT &
		   STA_DATE, DEPT_CODE, STATION_CODE, &
		   DATA_02, DATA_03, DATA_04, &
		   DATA_05, DATA_06, DATA_07, &
		   DATA_08, DATA_09, DATA_10, &
		   DATA_11, DATA_12, DATA_13, &
		   DATA_14, DATA_15, DATA_16, &
		   DATA_17, DATA_18, DATA_19, &
		   DATA_20, DATA_21, DATA_22, &
		   DATA_23, DATA_24, DATA_25, &
		   DATA_26, DATA_27, DATA_28, &
		   DATA_29, DATA_30, DATA_31, &
		   DATA_32, DATA_33, DATA_34, DATA_35, &
		   CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, &
		   OPT_USER, OPT_DATE, OPT_TERM &
		FROM STA_IN_02 &
		WHERE DEPT_CODE LIKE <Dept>||'%' &
		AND TO_DATE(STA_DATE,'YYYYMM') BETWEEN TO_DATE(<DATE_S>,'YYYYMM') AND TO_DATE(<DATE_E>,'YYYYMM')
//=============pangben modify 20110523 start
selectQS.item=REGION_CODE
selectQS.REGION_CODE=REGION_CODE=<REGION_CODE>
//=============pangben modify 20110523 stop
selectQS.Debug=N