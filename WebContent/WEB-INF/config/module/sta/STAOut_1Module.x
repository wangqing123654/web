# 
#  Title: ҽԺ������Ժ����ʹ�ü�������̬����ͳ2��1��
# 
#  Description: ҽԺ������Ժ����ʹ�ü�������̬����ͳ2��1��
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.08
#  version 1.0
#
Module.item=selectSTA_OPD_DAILY_Sum;selectSTA_OUT_01;insertSTA_OUT_01;deleteSTA_OUT_01
//��ȡ �����м�����ͳ������
selectSTA_OPD_DAILY_Sum.Type=TSQL
selectSTA_OPD_DAILY_Sum.SQL=SELECT SUM(HRM_NUM) AS HRM_NUM &
				FROM STA_OPD_DAILY &
				WHERE STA_DATE LIKE <STA_DATE>
//=========pangben modify 20110520 start
selectSTA_OPD_DAILY_Sum,item=REGION_CODE
selectSTA_OPD_DAILY_Sum.REGION_CODE=REGION_CODE=<REGION_CODE>
//=========pangben modify 20110520 stop
selectSTA_OPD_DAILY_Sum.Debug=N

//���±����ݲ��뵽STA_OUT_01����
//=========pangben modify 20110520 ���������
insertSTA_OUT_01.Type=TSQL
insertSTA_OUT_01.SQL=INSERT INTO STA_OUT_01 ( &
				       STA_DATE, DATA_01, DATA_02, &
				       DATA_03, DATA_04, DATA_05, &
				       DATA_06, DATA_07, DATA_08, &
				       DATA_09, DATA_10, DATA_11, &
				       DATA_12, DATA_13, DATA_14, &
				       DATA_15, DATA_16, DATA_17, &
				       DATA_18, DATA_19, DATA_20, &
				       DATA_21, DATA_22, DATA_23, &
				       DATA_24, DATA_25, DATA_26, &
				       DATA_27, DATA_28, DATA_29, &
				       DATA_30, DATA_31, DATA_32, &
				       DATA_33, DATA_34, CONFIRM_FLG, &
				       CONFIRM_USER, CONFIRM_DATE, OPT_USER, &
				       OPT_DATE, OPT_TERM,REGION_CODE ) &
				VALUES  &
					( &
				       <STA_DATE>, <DATA_01>, <DATA_02>,  &
				       <DATA_03>, <DATA_04>, <DATA_05>, &
				       <DATA_06>, <DATA_07>, <DATA_08>, &
				       <DATA_09>, <DATA_10>, <DATA_11>, &
				       <DATA_12>, <DATA_13>, <DATA_14>, &
				       <DATA_15>, <DATA_16>, <DATA_17>, &
				       <DATA_18>, <DATA_19>, <DATA_20>, &
				       <DATA_21>, <DATA_22>, <DATA_23>, &
				       <DATA_24>, <DATA_25>, <DATA_26>, &
				       <DATA_27>, <DATA_28>, <DATA_29>, &
				       <DATA_30>, <DATA_31>, <DATA_32>, &
				       <DATA_33>, <DATA_34>, <CONFIRM_FLG>, &
				       <CONFIRM_USER>, <CONFIRM_DATE>, <OPT_USER>, &
				       SYSDATE, <OPT_TERM>,<REGION_CODE>)
insertSTA_OUT_01.Debug=N


//��ѯSTA_OUT_01��
selectSTA_OUT_01.Type=TSQL
selectSTA_OUT_01.SQL=SELECT &
			   STA_DATE, DATA_01, DATA_02, &
			   DATA_03, DATA_04, DATA_05, &
			   DATA_06, DATA_07, DATA_08, &
			   DATA_09, DATA_10, DATA_11, &
			   DATA_12, DATA_13, DATA_14, &
			   DATA_15, DATA_16, DATA_17, &
			   DATA_18, DATA_19, DATA_20, &
			   DATA_21, DATA_22, DATA_23, &
			   DATA_24, DATA_25, DATA_26, &
			   DATA_27, DATA_28, DATA_29, &
			   DATA_30, DATA_31, DATA_32, &
			   DATA_33, DATA_34, DATA_35, CONFIRM_FLG, & 
			   CONFIRM_USER, CONFIRM_DATE, OPT_USER, & 
			   OPT_DATE, OPT_TERM &
			FROM STA_OUT_01
selectSTA_OUT_01.item=STA_DATE;REGION_CODE
selectSTA_OUT_01.STA_DATE=STA_DATE=<STA_DATE>
//=========pangben modify 20110520 start 
selectSTA_OUT_01.REGION_CODE=REGION_CODE=<REGION_CODE>
//=========pangben modify 20110520 stop
selectSTA_OUT_01.Debug=N

//ɾ��STA_OUT_01������
//=========pangben modify 20110520 �����������
deleteSTA_OUT_01.Type=TSQL
deleteSTA_OUT_01.SQL=DELETE FROM STA_OUT_01 WHERE STA_DATE=<STA_DATE> AND REGION_CODE=<REGION_CODE>
deleteSTA_OUT_01.Debug=N
