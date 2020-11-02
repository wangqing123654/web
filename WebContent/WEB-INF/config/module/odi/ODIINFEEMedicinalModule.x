Module.item=selectdata;selectdataM
selectdata.Type=TSQL

//查询所有长期医嘱存在两条抗菌药品的病患
selectdata.SQL=SELECT B.CASE_NO,B.DEPT_CODE,B.STATION_CODE,B.VS_DR_CODE,B.REGION_CODE FROM ODI_ORDER A,ADM_INP B WHERE A.CASE_NO=B.CASE_NO &
               AND A.ANTIBIOTIC_CODE IS NOT NULL  AND B.DS_DATE IS NOT NULL  &
               AND B.DS_DATE BETWEEN TO_DATE (<S_DATE>, 'YYYYMMDDHH24MISS') &
	       AND TO_DATE (<E_DATE>, 'YYYYMMDDHH24MISS') &
	       AND RX_KIND='UD' GROUP BY B.CASE_NO,B.DEPT_CODE,B.STATION_CODE,B.VS_DR_CODE,B.REGION_CODE HAVING COUNT(B.CASE_NO)>1 &
               ORDER BY  B.REGION_CODE,B.DEPT_CODE,B.STATION_CODE,B.VS_DR_CODE
selectdata.item=DEPT_CODE;STATION_CODE;VS_DR_CODE
selectdata.DEPT_CODE=B.DEPT_CODE=<DEPT_CODE> 
selectdata.STATION_CODE=B.STATION_CODE=<STATION_CODE>
selectdata.VS_DR_CODE=B.VS_DR_CODE=<VS_DR_CODE>
selectdata.Debug=N

//查询所有病患统计
selectdataM.Type=TSQL
selectdataM.SQL=SELECT A.REGION_CODE,A.DEPT_CODE,A.STATION_CODE,A.VS_DR_CODE ,COUNT(A.CASE_NO) TOTAL_NUM,0 JOINT_NUM,'0%' APPLICATION_SCALE FROM ( &
                SELECT A.REGION_CODE,A.DEPT_CODE,A.STATION_CODE,A.VS_DR_CODE,A.CASE_NO &
                FROM ADM_INP A,ODI_ORDER B &
		WHERE A.CASE_NO=B.CASE_NO &
		AND A.DS_DATE BETWEEN TO_DATE(<S_DATE>,'YYYYMMDDHH24MISS') &
	        AND TO_DATE (<E_DATE>,'YYYYMMDDHH24MISS') &
                GROUP BY  A.REGION_CODE,A.DEPT_CODE,A.STATION_CODE,A.VS_DR_CODE,A.CASE_NO ) A &
		GROUP BY  A.REGION_CODE,A.DEPT_CODE,A.STATION_CODE,A.VS_DR_CODE &
		ORDER BY  A.REGION_CODE,A.DEPT_CODE,A.STATION_CODE,A.VS_DR_CODE
selectdataM.item=DEPT_CODE;STATION_CODE;VS_DR_CODE
selectdataM.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE> 
selectdataM.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selectdataM.VS_DR_CODE=A.VS_DR_CODE=<VS_DR_CODE>
selectdataM.Debug=N