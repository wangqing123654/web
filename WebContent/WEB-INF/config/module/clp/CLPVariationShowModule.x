# 
#  Title:临床路径变异分析module
# 
#  Description:临床路径变异分析
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2011-07-07
#  version 1.0
#
Module.item=selectDataStadardWithoutReal;selectDataWithoutStandard
#未执行的order
selectDataStadardWithoutReal.Type=TSQL
selectDataStadardWithoutReal.SQL=SELECT ALLTABLE.* FROM &
        (&
        (&
        SELECT MAINCOUNT AS MAIN_COUNT,ORDER_DESC,DURATION_CHN_DESC AS SCHD_DESC,CHKTYPE_CHN_DESC,STANDCOUNT AS STANDARD_COUNT,MAINCOUNT/STANDCOUNT AS PER &
        FROM &
        ( &
        SELECT COUNT(D.CASE_NO) AS MAINCOUNT,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE &
        FROM CLP_MANAGED D,MRO_RECORD MR  &
        WHERE   D.ORDER_CODE IS NOT NULL  AND  D.MAINORD_CODE IS   NULL AND D.ORDER_FLG='Y' &
                AND D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL &
                AND MR.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
         GROUP BY  D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE &
        )A, &
        (   &
        SELECT COUNT(D.CASE_NO) AS STANDCOUNT,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE &
        FROM CLP_MANAGED D,MRO_RECORD MR & 
        WHERE D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND D.ORDER_FLG='Y' & 
        AND MR.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
        GROUP BY D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE & 
        )B, &
        SYS_FEE FE,CLP_CHKTYPE CH,CLP_DURATION DR  & 
        WHERE A.ORDER_CODE=B.ORDER_CODE AND A.SCHD_CODE=B.SCHD_CODE AND A.CHKTYPE_CODE=B.CHKTYPE_CODE & 
        AND FE.ORDER_CODE=B.ORDER_CODE AND CH.CHKTYPE_CODE=B.CHKTYPE_CODE AND DR.DURATION_CODE=B.SCHD_CODE  &
        ) & 
        UNION ALL  & 
        ( & 
        SELECT MAINCOUNT,ITEM.CHKITEM_CHN_DESC AS ORDER_DESC,DURATION_CHN_DESC,CHKTYPE_CHN_DESC,STANDCOUNT,MAINCOUNT/STANDCOUNT AS PERCENT & 
        FROM & 
        ( & 
        SELECT COUNT(D.CASE_NO) AS MAINCOUNT,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE & 
        FROM CLP_MANAGED D,MRO_RECORD MR  & 
        WHERE   D.ORDER_CODE IS NOT NULL  AND  ( D.PROGRESS_CODE NOT LIKE '%A%' OR D.PROGRESS_CODE IS NULL) AND D.ORDER_FLG='N' & 
                AND D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL & 
                AND MR.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
         GROUP BY  D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE & 
        )A, &
        (   & 
        SELECT COUNT(D.CASE_NO) AS STANDCOUNT,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE & 
        FROM CLP_MANAGED D,MRO_RECORD MR & 
        WHERE D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND D.ORDER_FLG='N' & 
        AND MR.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
        GROUP BY D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE & 
        )B, & 
        CLP_CHKITEM ITEM,CLP_CHKTYPE CH,CLP_DURATION DR  & 
        WHERE A.ORDER_CODE=B.ORDER_CODE AND A.SCHD_CODE=B.SCHD_CODE AND A.CHKTYPE_CODE=B.CHKTYPE_CODE  & 
        AND ITEM.CHKITEM_CODE=B.ORDER_CODE AND CH.CHKTYPE_CODE=B.CHKTYPE_CODE AND DR.DURATION_CODE=B.SCHD_CODE  &
        ) & 
        )ALLTABLE																 
selectDataStadardWithoutReal.item=REGION_CODE11
selectDataStadardWithoutReal.REGION_CODE11=B.REGION_CODE=<REGION_CODE>
selectDataStadardWithoutReal.Debug=N

#查执行的order
selectDataWithoutStandard.Type=TSQL
selectDataWithoutStandard.SQL=SELECT ALLTABLE.* FROM &
        (&
        (&
        SELECT MAINCOUNT AS MAIN_COUNT,ORDER_DESC,DURATION_CHN_DESC AS SCHD_DESC,CHKTYPE_CHN_DESC,STANDCOUNT AS STANDARD_COUNT,(STANDCOUNT-MAINCOUNT)/STANDCOUNT AS PER &
        FROM &
        ( &
        SELECT COUNT(D.CASE_NO) AS MAINCOUNT,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE &
        FROM CLP_MANAGED D,MRO_RECORD MR  &
        WHERE   D.ORDER_CODE IS  NULL  AND  D.MAINORD_CODE IS NOT NULL AND D.ORDER_FLG='Y' &
                AND D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL &
                AND MR.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
         GROUP BY  D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE &
        )A, &
        (   &
        SELECT COUNT(D.CASE_NO) AS STANDCOUNT,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE &
        FROM CLP_MANAGED D,MRO_RECORD MR & 
        WHERE D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND D.ORDER_FLG='Y' &
        AND MR.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
        GROUP BY D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE & 
        )B, &
        SYS_FEE FE,CLP_CHKTYPE CH,CLP_DURATION DR  & 
        WHERE A.MAINORD_CODE=B.MAINORD_CODE AND A.SCHD_CODE=B.SCHD_CODE AND A.CHKTYPE_CODE=B.CHKTYPE_CODE & 
        AND FE.ORDER_CODE=B.MAINORD_CODE AND CH.CHKTYPE_CODE=B.CHKTYPE_CODE AND DR.DURATION_CODE=B.SCHD_CODE  &
        ) & 
        UNION ALL  & 
        ( & 
        SELECT MAINCOUNT,ITEM.CHKITEM_CHN_DESC AS ORDER_DESC,DURATION_CHN_DESC,CHKTYPE_CHN_DESC,STANDCOUNT,MAINCOUNT/STANDCOUNT AS PERCENT & 
        FROM & 
        ( & 
        SELECT COUNT(D.CASE_NO) AS MAINCOUNT,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE & 
        FROM CLP_MANAGED D,MRO_RECORD MR  & 
        WHERE   D.ORDER_CODE IS  NULL  AND   D.PROGRESS_CODE  LIKE '%A%'  AND D.ORDER_FLG='N' & 
                AND D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL & 
                AND MR.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
         GROUP BY  D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE & 
        )A, &
        (   & 
        SELECT COUNT(D.CASE_NO) AS STANDCOUNT,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE & 
        FROM CLP_MANAGED D,MRO_RECORD MR & 
        WHERE D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND D.ORDER_FLG='N' & 
        AND MR.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
        GROUP BY D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE & 
        )B, & 
        CLP_CHKITEM ITEM,CLP_CHKTYPE CH,CLP_DURATION DR  & 
        WHERE A.MAINORD_CODE=B.MAINORD_CODE AND A.SCHD_CODE=B.SCHD_CODE AND A.CHKTYPE_CODE=B.CHKTYPE_CODE  & 
        AND ITEM.CHKITEM_CODE=B.MAINORD_CODE AND CH.CHKTYPE_CODE=B.CHKTYPE_CODE AND DR.DURATION_CODE=B.SCHD_CODE  &
        ) & 
        )ALLTABLE																 
selectDataWithoutStandard.item=REGION_CODE11
selectDataWithoutStandard.REGION_CODE11=B.REGION_CODE=<REGION_CODE>
selectDataWithoutStandard.Debug=N