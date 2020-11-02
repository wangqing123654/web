 #
   # Title: 护士工作量统计
   #
   # Description:护士工作量统计
   #
   # Copyright: JavaHis (c) 2010
   #
   # @author zhangh 2013.12.9

Module.item=queryCareNum;queryExecType

//查询照顾病人数量
queryCareNum.Type=TSQL
queryCareNum.SQL=SELECT DEPT_CHN_DESC,STATION_DESC,NURSING_CLASS_DESC,COUNT(*) AS PERSON_COUNT &
		 FROM ADM_INP A &
		 LEFT JOIN ADM_NURSING_CLASS B ON A.NURSING_CLASS = B.NURSING_CLASS_CODE &
		 LEFT JOIN SYS_DEPT C ON A.DEPT_CODE = C.DEPT_CODE &
		 LEFT JOIN SYS_STATION D ON A.STATION_CODE = D.STATION_CODE &
		 WHERE A.NURSING_CLASS IS NOT NULL &
		 GROUP BY A.DEPT_CODE,STATION_DESC,NURSING_CLASS_DESC,NURSING_CLASS,DEPT_CHN_DESC &
		 ORDER BY A.DEPT_CODE,NURSING_CLASS 
queryCareNum.item=START_TIME;END_TIME;DEPT_CODE;STATION_CODE;NURSING_CLASS
queryCareNum.START_TIME=A.IN_DATE>=TO_DATE(<START_TIME>,'yyyy-MM-dd HH24:MI:SS')
queryCareNum.END_TIME=A.IN_DATE<=TO_DATE(<END_TIME>,'yyyy-MM-dd HH24:MI:SS')
queryCareNum.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
queryCareNum.STATION_CODE=A.STATION_CODE=<STATION_CODE>
queryCareNum.NURSING_CLASS=A.NURSING_CLASS=<NURSING_CLASS>
queryCareNum.Debug=N


//查询医嘱执行分类
queryExecType.Type=TSQL
queryExecType.SQL=SELECT DEPT_CHN_DESC, &
		     STATION_DESC, &
		     E.CLASSIFY_TYPE, &
		     COUNT (A.CASE_NO) AS NUM, &
		     F.USER_NAME, &
		     A.NS_EXEC_CODE &
		    FROM ODI_DSPND A, &
		     ODI_ORDER B, &
		     SYS_DEPT C, &
		     SYS_STATION D, &
		     SYS_PHAROUTE E, &
		     SYS_OPERATOR F &
		   WHERE A.DC_DATE IS NULL &
		     AND A.NS_EXEC_DATE >= TO_DATE (<START_TIME>, 'YYYYMMDDHH24MISS') &
		     AND A.NS_EXEC_DATE <= TO_DATE (<END_TIME>, 'YYYYMMDDHH24MISS') &
		     AND A.CASE_NO = B.CASE_NO &
		     AND A.ORDER_NO = B.ORDER_NO &
		     AND A.ORDER_SEQ = B.ORDER_SEQ &
		     AND B.DEPT_CODE = C.DEPT_CODE &
		     AND B.STATION_CODE = D.STATION_CODE &
		     AND B.ROUTE_CODE = E.ROUTE_CODE &
		     AND A.NS_EXEC_CODE = F.USER_ID &
		     AND (LINK_NO IS NULL OR LINKMAIN_FLG = 'Y') &
		GROUP BY B.DEPT_CODE, &
		     B.STATION_CODE, &
		     C.DEPT_CHN_DESC, &
		     D.STATION_DESC, &
		     E.CLASSIFY_TYPE, &
		     A.NS_EXEC_CODE, &
		     F.USER_NAME &
		ORDER BY B.DEPT_CODE, B.STATION_CODE
queryExecType.item=DEPT_CODE;STATION_CODE;USER_ID
queryExecType.DEPT_CODE=B.DEPT_CODE=<DEPT_CODE>
queryExecType.STATION_CODE=B.STATION_CODE=<STATION_CODE>
queryExecType.USER_ID=A.NS_EXEC_CODE=<USER_ID>
queryExecType.Debug=N