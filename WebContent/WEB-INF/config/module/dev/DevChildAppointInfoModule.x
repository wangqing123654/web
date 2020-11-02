# 
#  Title:儿童保健科随访预约儿童信息表module
# 
#  Description:儿童保健科随访预约儿童信息表module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wukai 20160511
#  version 1.0
#

Module.item=queryAll;queryByCon

queryAll.Type=TSQL
queryAll.SQL=SELECT b.MR_NO,b.PAT_NAME,  &
    (CASE WHEN b.SEX_CODE = '1' THEN '男' WHEN b.SEX_CODE='2' THEN '女' WHEN b.SEX_CODE='0' THEN '未知' END) AS SEX_CODE, &
    b.BIRTH_DATE,m.MOTHER_NAME,m.CELL_PHONE FROM  &    
    (SELECT a.CASE_NO,A.IPD_NO, s.PAT_NAME , s.MR_NO, s.BIRTH_DATE ,s.SEX_CODE from SYS_PATINFO s, ADM_INP a WHERE s.MR_NO in (SELECT MR_NO FROM sys_patinfo WHERE new_born_flg='Y') and s.MR_NO = a.MR_NO ORDER BY S.MR_NO)  &
    b, &
    (SELECT s.PAT_NAME AS MOTHER_NAME,s.BIRTH_DATE AS MOTHER_DATE, s.CELL_PHONE, s.MR_NO FROM SYS_PATINFO s WHERE MR_NO in (SELECT IPD_NO FROM adm_inp WHERE new_born_flg='Y' ) ORDER BY s.MR_NO ) &
    m &
    WHERE b.IPD_NO = m.MR_NO AND (SELECT trunc(sysdate - b.birth_date) FROM dual) > 0 AND (SELECT trunc(sysdate - b.birth_date) FROM dual) < 7
queryAll.Debug=N

queryByCon.Type=TSQL
queryByCon.SQL=SELECT b.MR_NO,b.PAT_NAME,  &
    (CASE WHEN b.SEX_CODE = '1' THEN '男' WHEN b.SEX_CODE='2' THEN '女' WHEN b.SEX_CODE='0' THEN '未知' END) AS SEX_CODE, &
    b.BIRTH_DATE,m.MOTHER_NAME,m.CELL_PHONE FROM  &    
    (SELECT a.CASE_NO,A.IPD_NO, s.PAT_NAME , s.MR_NO, s.BIRTH_DATE ,s.SEX_CODE from SYS_PATINFO s, ADM_INP a WHERE s.MR_NO in (SELECT MR_NO FROM sys_patinfo WHERE new_born_flg='Y') and s.MR_NO = a.MR_NO ORDER BY S.MR_NO)  &
    b, &
    (SELECT s.PAT_NAME AS MOTHER_NAME,s.BIRTH_DATE AS MOTHER_DATE, s.CELL_PHONE, s.MR_NO FROM SYS_PATINFO s WHERE MR_NO in (SELECT IPD_NO FROM adm_inp WHERE new_born_flg='Y' ) ORDER BY s.MR_NO ) &
    m &
    WHERE b.IPD_NO = m.MR_NO
    
queryByCon.Debug=N





