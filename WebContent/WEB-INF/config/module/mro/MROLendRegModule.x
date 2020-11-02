###############################################
#  Title:病案借阅登记module
# 
#  Description:病案借阅登记module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.5.11
#  version 4.0
###############################################
Module.item=selectLendDay;insertQueue;selectQueue

//查询借阅原因对应的天数
selectLendDay.Type=TSQL
selectLendDay.SQL=SELECT LEND_DAY,VALID_DAY &
			FROM MRO_LEND &
			WHERE LEND_CODE=<LEND_CODE>
selectLendDay.Debug=N

//插入病历待出表
insertQueue.Type=TSQL
insertQueue.SQL=INSERT INTO MRO_QUEUE ( &
                QUE_SEQ,QUE_DATE,MR_NO,IPD_NO,ADM_HOSP, ADM_AREA_CODE, QUE_NO, &
                SESSION_CODE,REQ_DEPT,MR_PERSON,ISSUE_CODE,RTN_DATE, &
                DUE_DATE,LEND_CODE,CAN_FLG,ADM_TYPE,CASE_NO, &
                QUE_HOSP,IN_DATE,IN_PERSON,OUT_TYPE,OPT_USER,OPT_DATE,OPT_TERM,LEND_BOX_CODE,LEND_BOOK_NO &
             ) &
             VALUES ( &
				<QUE_SEQ>,TO_DATE(<QUE_DATE>,'YYYY/MM/DD'),<MR_NO>,<IPD_NO>,<ADM_HOSP>,<ADM_AREA_CODE>,<QUE_NO>, &
                <SESSION_CODE>,<REQ_DEPT>,<MR_PERSON>,<ISSUE_CODE>,TO_DATE(<RTN_DATE>,'YYYY/MM/DD'), &
                <DUE_DATE>,<LEND_CODE>,<CAN_FLG>,<ADM_TYPE>,<CASE_NO>, &
                <QUE_HOSP>,<IN_DATE>,<IN_PERSON>,<OUT_TYPE>,<OPT_USER>,SYSDATE,<OPT_TERM>,<LEND_BOX_CODE>,<LEND_BOOK_NO> &
             )
insertQueue.Debug=N

//查询病历在借出后是否归还
selectQueue.Type=TSQL
selectQueue.SQL=SELECT ISSUE_CODE FROM MRO_QUEUE WHERE MR_NO=<MR_NO> ORDER BY QUE_SEQ DESC
selectQueue.Debug=N