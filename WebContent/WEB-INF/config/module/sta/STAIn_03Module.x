# 
#  Title: 医院住院病患动态及疗效报表
# 
#  Description: 医院住院病患动态及疗效报表
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.19
#  version 1.0
#
Module.item=selectSTA_DAILY_02;selectSTA_DAILY_02Day;insertSTA_IN_03;deleteSTA_IN_03;selectSTA_IN_03;selectSTA_DAILY_02_DAYS;updateSTA_IN_03

//查询STA_DAILY_02表信息(月信息)
selectSTA_DAILY_02.Type=TSQL
selectSTA_DAILY_02.SQL=SELECT DEPT_CODE,STATION_CODE, &
			   SUM(DATA_08) AS DATA_08,SUM(DATA_08_1) AS DATA_08_1,SUM(DATA_09) AS DATA_09,SUM(DATA_10) AS DATA_10,SUM(DATA_07) AS DATA_07,&
			   SUM(DATA_12) AS DATA_12,SUM(DATA_13) AS DATA_13,SUM(DATA_14) AS DATA_14,SUM(DATA_15) AS DATA_15,SUM(DATA_15_1) AS DATA_15_1,&
			   SUM(DATA_21) AS DATA_21,SUM(DATA_18) AS DATA_18,AVG(DATA_36) AS DATA_36,AVG(DATA_34) AS DATA_34,AVG(DATA_31) AS DATA_31,&
			   AVG(DATA_32) AS DATA_32,AVG(DATA_33) AS DATA_33,AVG(DATA_16) AS DATA_16,AVG(DATA_21) AS DATA_21_1,AVG(DATA_35) AS DATA_35,&
			   AVG(DATA_17) AS DATA_17,SUM(DATA_11) AS DATA_11,SUM(DATA_20) AS DATA_20 &
			FROM STA_DAILY_02 &
			WHERE STA_DATE LIKE <STA_DATE>||'%' &
			AND STA_DATE <> <STA_DATE> &
			GROUP BY DEPT_CODE,STATION_CODE
//==============pangben modify 20110523 start
selectSTA_DAILY_02.item=REGION_CODE
selectSTA_DAILY_02.REGION_CODE=REGION_CODE=<REGION_CODE>
//==============pangben modify 20110523 stop
selectSTA_DAILY_02.Debug=N

//查询STA_DAILY_02表数据 （日数据）
selectSTA_DAILY_02Day.Type=TSQL
selectSTA_DAILY_02Day.SQL=SELECT &
			   STA_DATE, DEPT_CODE, STATION_CODE, &
			   DATA_01, DATA_02, DATA_03, &
			   DATA_04, DATA_05, DATA_06, &
			   DATA_07, DATA_08, DATA_08_1, &
			   DATA_09, DATA_10, DATA_11, &
			   DATA_12, DATA_13, DATA_14, &
			   DATA_15, DATA_15_1, DATA_16, &
			   DATA_17, DATA_18, DATA_19, &
			   DATA_20, DATA_21, DATA_22, &
			   DATA_23, DATA_24, DATA_25, &
			   DATA_26, DATA_27, DATA_28, &
			   DATA_29, DATA_30, DATA_31, &
			   DATA_32, DATA_33, DATA_34, &
			   DATA_35, DATA_36, DATA_37, &
			   DATA_38, DATA_39, DATA_40, &
			   DATA_41, DATA_41_1, DATA_41_2, &
			   CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, &
			   OPT_USER, OPT_DATE, OPT_TERM &
			FROM STA_DAILY_02 
selectSTA_DAILY_02Day.item=STA_DATE;DEPT_CODE;REGION_CODE
selectSTA_DAILY_02Day.STA_DATE=STA_DATE=<STA_DATE>
selectSTA_DAILY_02Day.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
//==============pangben modify 20110523 start
selectSTA_DAILY_02Day.REGION_CODE=REGION_CODE=<REGION_CODE>
//==============pangben modify 20110523 stop
selectSTA_DAILY_02Day.Debug=N

//删除表STA_IN_03数据
//==============pangben modify 20110524  添加区域 条件
deleteSTA_IN_03.Type=TSQL
deleteSTA_IN_03.SQL=DELETE FROM STA_IN_03 WHERE STA_DATE=<STA_DATE> AND REGION_CODE=<REGION_CODE>
deleteSTA_IN_03.Debug=N

//插入STA_IN_02表数据
//==============pangben modify 20110524  添加区域列
insertSTA_IN_03.Type=TSQL
insertSTA_IN_03.SQL=INSERT INTO STA_IN_03 ( &
			STA_DATE,DEPT_CODE,STATION_CODE,DATA_01,DATA_02,DATA_03,&
			DATA_04,DATA_05,DATA_06,DATA_07,DATA_08,&
			DATA_09,DATA_10,DATA_11,DATA_12,DATA_13,&
			DATA_14,DATA_15,DATA_16,DATA_17,DATA_18,&
			DATA_19,DATA_20,DATA_21,DATA_22,DATA_23,&
			DATA_24,DATA_25,DATA_26,DATA_27,&
			CONFIRM_FLG,CONFIRM_USER,CONFIRM_DATE,OPT_USER,&
			OPT_DATE,OPT_TERM,REGION_CODE &
		) VALUES ( &
			<STA_DATE>,<DEPT_CODE>,<STATION_CODE>,<DATA_01>,<DATA_02>,<DATA_03>,&
			<DATA_04>,<DATA_05>,<DATA_06>,<DATA_07>,<DATA_08>,&
			<DATA_09>,<DATA_10>,<DATA_11>,<DATA_12>,<DATA_13>,&
			<DATA_14>,<DATA_15>,<DATA_16>,<DATA_17>,<DATA_18>,&
			<DATA_19>,<DATA_20>,<DATA_21>,<DATA_22>,<DATA_23>,&
			<DATA_24>,<DATA_25>,<DATA_26>,<DATA_27>,&
			<CONFIRM_FLG>,<CONFIRM_USER>,<CONFIRM_DATE>,<OPT_USER>,&
			SYSDATE,<OPT_TERM>,<REGION_CODE> &
		)
insertSTA_IN_03.Debug=N

//查询STA_IN_03表数据
selectSTA_IN_03.Type=TSQL
selectSTA_IN_03.SQL=SELECT STA_DATE,DEPT_CODE,STATION_CODE,DATA_01,DATA_02,DATA_03,&
			DATA_04,DATA_05,DATA_06,DATA_07,DATA_08,&
			DATA_09,DATA_10,DATA_11,DATA_12,DATA_13,&
			DATA_14,DATA_15,DATA_16,DATA_17,DATA_18,&
			DATA_19,DATA_20,DATA_21,DATA_22,DATA_23,&
			DATA_24,DATA_25,DATA_26,DATA_27,&
			CONFIRM_FLG,CONFIRM_USER,CONFIRM_DATE,OPT_USER,&
			OPT_DATE,OPT_TERM &
			FROM STA_IN_03
selectSTA_IN_03.item=STA_DATE
selectSTA_IN_03.STA_DATE=STA_DATE=<STA_DATE>
selectSTA_IN_03.Debug=N

//查询STA_DAILY_02表信息(区间统计)
selectSTA_DAILY_02_DAYS.Type=TSQL
selectSTA_DAILY_02_DAYS.SQL=SELECT DEPT_CODE,STATION_CODE, &
			       SUM(DATA_08) AS DATA_08,SUM(DATA_08_1) AS DATA_08_1,SUM(DATA_09) AS DATA_09,SUM(DATA_10) AS DATA_10,SUM(DATA_07) AS DATA_07,&
			       SUM(DATA_12) AS DATA_12,SUM(DATA_13) AS DATA_13,SUM(DATA_14) AS DATA_14,SUM(DATA_15) AS DATA_15,SUM(DATA_15_1) AS DATA_15_1,&
			       SUM(DATA_21) AS DATA_21,SUM(DATA_18) AS DATA_18,AVG(DATA_36) AS DATA_36,AVG(DATA_34) AS DATA_34,AVG(DATA_31) AS DATA_31,&
			       AVG(DATA_32) AS DATA_32,AVG(DATA_33) AS DATA_33,AVG(DATA_16) AS DATA_16,AVG(DATA_21) AS DATA_21_1,AVG(DATA_35) AS DATA_35,&
			       AVG(DATA_17) AS DATA_17,SUM(DATA_11) AS DATA_11,SUM(DATA_20) AS DATA_20 &
			    FROM STA_DAILY_02 &
			    WHERE LENGTH(STA_DATE) = 8 &
			    AND TO_DATE(STA_DATE,'YYYYMMDD') BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			    GROUP BY DEPT_CODE,STATION_CODE
selectSTA_DAILY_02_DAYS.item=DEPT_CODE;REGION_CODE
selectSTA_DAILY_02_DAYS.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
//==================pangben 20110524 start
selectSTA_DAILY_02_DAYS.REGION_CODE=REGION_CODE=<REGION_CODE>
//==================pangben 20110524 stop
selectSTA_DAILY_02_DAYS.Debug=N
//修改STA_IN_03表数据
updateSTA_IN_03.Type=TSQL
updateSTA_IN_03.SQL=UPDATE STA_IN_03 SET &
DATA_01=<DATA_01>,DATA_02=<DATA_02>, DATA_03=<DATA_03>, DATA_04=<DATA_04>, DATA_05=<DATA_05>, &
DATA_06=<DATA_06>, DATA_07=<DATA_07>, DATA_08=<DATA_08>, DATA_09=<DATA_09>, &
DATA_10=<DATA_10>, DATA_11=<DATA_11>, DATA_12=<DATA_12>, DATA_13=<DATA_13>, DATA_14=<DATA_14>, &
DATA_15=<DATA_15>, DATA_16=<DATA_16>, DATA_17=<DATA_17>, DATA_18=<DATA_18>, DATA_19=<DATA_19>, DATA_20=<DATA_20>, &
DATA_21=<DATA_21>, DATA_22=<DATA_22>, DATA_23=<DATA_23>, DATA_24=<DATA_24>, DATA_25=<DATA_25>, DATA_26=<DATA_26>, &
DATA_27=<DATA_27>, &
OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM>,CONFIRM_FLG=<CONFIRM_FLG>,CONFIRM_USER=<CONFIRM_USER>,CONFIRM_DATE=<CONFIRM_DATE> &
WHERE STA_DATE=<STA_DATE> AND REGION_CODE=<REGION_CODE> AND DEPT_CODE=<DEPT_CODE> AND STATION_CODE=<STATION_CODE>
updateSTA_IN_03.Debug=N