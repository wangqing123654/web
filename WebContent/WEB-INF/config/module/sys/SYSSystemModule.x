Module.item=getDate;getMrNo;getIpdNo;getNo;get3No;loadOdo;getUpdateTime

//�õ�ϵͳʱ��
getDate.Type=TSQL
getDate.SQL=SELECT SYSDATE FROM DUAL
getDate.Debug=N


getUpdateTime.Type=TSQL
getUpdateTime.SQL=SELECT TO_CHAR(SYSDATE,'yyyymmddhh24mmssSSS') UPDATETIME FROM DUAL
getUpdateTime.Debug=N

getMrNo.Type=TSQL
getMrNo.SQL={call GET_MR_NO(?)}
getMrNo.OutType=MR_NO:VARCHAR
getMrNo.Debug=N

//סԺ��
getIpdNo.Type=TSQL
getIpdNo.SQL={call GET_IPD_NO(?)}
getIpdNo.OutType=IPD_NO:VARCHAR
getIpdNo.Debug=N

//ȡ��
getNo.Type=TSQL
getNo.SQL={call SYSGETNO(<REGION_CODE>,<SYSTEM_CODE>,<OPERATION>,<SECTION>,?)}
getNo.OutType=NO:VARCHAR
getNo.Debug=N

//3.0ȡ��
//ȡ��
get3No.Type=TSQL
get3No.SQL={call SYSGETNO(<HOSP_AREA>,<REGION_CODE>,<SYSTEM_CODE>,<OPERATION>,<SECTION>,?,?,?)}
get3No.OutType=CODE:CHAR;MSG:VARCHAR;NO:INT
get3No.Debug=N

loadOdo.Type=TSQL
loadOdo.SQL={call GET_LOAD_ODO(<CLINICROOM_NO>,<REALDR_CODE>,<ADM_DATE>,<ADM_TYPE>,<SESSION_CODE>,?,?)}
loadOdo.OutType=MR_NO:VARCHAR;CASE_NO:VARCHAR
loadOdo.Debug=N