#############################################
#  Title:病案借阅module
# 
#  Description:病案借阅module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangbin 2014
#  version 4.0
#############################################
Module.item=insertMroReg;insertQueue;insertTRANHIS;queryMroRegAppointment;deleteMroRegAppointment;updateConfirmStatus;queryMroRegCancel;cancelMroRegAppointment;&
			updateMroMrvStoreLocation;deleteMroMrv


//插入MRO_REG数据
insertMroReg.Type=TSQL
insertMroReg.SQL=INSERT INTO MRO_REG ( &
                        MRO_REGNO,SEQ,BOOK_ID,ORIGIN_TYPE,ADM_TYPE,MR_NO,CASE_NO,ADM_DATE,ADM_AREA_CODE,SESSION_CODE, &
                        QUE_NO,PAT_NAME,SEX_CODE,BIRTH_DATE,CELL_PHONE,DEPT_CODE, &
                        DR_CODE,CONFIRM_STATUS,CANCEL_FLG,OPT_USER,OPT_DATE,OPT_TERM &
                        ) &
                 VALUES ( &
                        <MRO_REGNO>,<SEQ>,<BOOK_ID>,<ORIGIN_TYPE>,<ADM_TYPE>,<MR_NO>,<CASE_NO>,TO_DATE(<ADM_DATE>, 'YYYY-MM-DD'),<ADM_AREA_CODE>, &
                        <SESSION_CODE>,<QUE_NO>,<PAT_NAME>,<SEX_CODE>,TO_DATE(<BIRTH_DATE>, 'YYYY-MM-DD'),<CELL_PHONE>,<DEPT_CODE>, &
                        <DR_CODE>,<CONFIRM_STATUS>,<CANCEL_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM> &
                        ) 
insertMroReg.Debug=N

//插入病历借阅管理表
insertQueue.Type=TSQL
insertQueue.SQL=INSERT INTO MRO_QUEUE ( &
                QUE_SEQ,QUE_DATE,MR_NO,IPD_NO,ADM_HOSP, ADM_AREA_CODE, QUE_NO, &
                SESSION_CODE,REQ_DEPT,MR_PERSON,ISSUE_CODE,RTN_DATE, &
                DUE_DATE,LEND_CODE,CAN_FLG,ADM_TYPE,CASE_NO, &
                QUE_HOSP,IN_DATE,IN_PERSON,OUT_TYPE,OPT_USER,OPT_DATE,OPT_TERM,LEND_BOX_CODE,LEND_BOOK_NO,OUT_DATE &
             ) &
             VALUES ( &
				<QUE_SEQ>,TO_DATE(<QUE_DATE>,'YYYY/MM/DD'),<MR_NO>,<IPD_NO>,<ADM_HOSP>,<ADM_AREA_CODE>,<QUE_NO>, &
                <SESSION_CODE>,<REQ_DEPT>,<MR_PERSON>,<ISSUE_CODE>,TO_DATE(<RTN_DATE>,'YYYY/MM/DD'), &
                <DUE_DATE>,<LEND_CODE>,<CAN_FLG>,<ADM_TYPE>,<CASE_NO>, &
                <QUE_HOSP>,<IN_DATE>,<IN_PERSON>,<OUT_TYPE>,<OPT_USER>,SYSDATE,<OPT_TERM>,<LEND_BOX_CODE>,<LEND_BOOK_NO>,<OUT_DATE> &
             )
insertQueue.Debug=N

//插入病案借阅历史表 MRO_TRANHIS
insertTRANHIS.Type=TSQL
insertTRANHIS.SQL=INSERT INTO MRO_TRANHIS ( &
                   IPD_NO, MR_NO, QUE_DATE,TRAN_KIND, LEND_CODE, &
                   CURT_LOCATION, REGION_CODE, MR_PERSON, TRAN_HOSP, IN_DATE, &
                   IN_PERSON, OPT_USER, OPT_DATE, OPT_TERM,QUE_SEQ) &
           		VALUES ( &
		   		   <IPD_NO>, <MR_NO>, <QUE_DATE>,<TRAN_KIND>, <LEND_CODE>, &
                   <CURT_LOCATION>, <REGION_CODE>, <MR_PERSON>, <TRAN_HOSP>, <IN_DATE>, &
                   <IN_PERSON>, <OPT_USER>, SYSDATE, <OPT_TERM>,<QUE_SEQ> &
                    )
insertTRANHIS.Debug=N

//查询验证MRO_REG预约数据
queryMroRegAppointment.Type=TSQL
queryMroRegAppointment.SQL=SELECT * FROM MRO_REG T WHERE T.ORIGIN_TYPE='0' AND CANCEL_FLG = 'N' ORDER BY MRO_REGNO
queryMroRegAppointment.item=BOOK_ID
queryMroRegAppointment.BOOK_ID=BOOK_ID=<BOOK_ID>
queryMroRegAppointment.Debug=N

//删除取消预约挂号(取消住院)的数据
cancelMroRegAppointment.Type=TSQL
cancelMroRegAppointment.SQL=UPDATE MRO_REG SET CANCEL_FLG = 'Y',OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> 
cancelMroRegAppointment.item=BOOK_ID;CASE_NO;MR_NO
cancelMroRegAppointment.BOOK_ID=BOOK_ID=<BOOK_ID>
cancelMroRegAppointment.CASE_NO=CASE_NO=<CASE_NO>
cancelMroRegAppointment.MR_NO=MR_NO=<MR_NO>
cancelMroRegAppointment.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
cancelMroRegAppointment.Debug=N

//更新MRO_REG临时表的待出库确认状态
updateConfirmStatus.Type=TSQL
updateConfirmStatus.SQL=UPDATE MRO_REG SET CONFIRM_STATUS = '1',OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> 
updateConfirmStatus.item=MRO_REGNO;SEQ
updateConfirmStatus.MRO_REGNO=MRO_REGNO=<MRO_REGNO>
updateConfirmStatus.SEQ=SEQ=<SEQ>
updateConfirmStatus.Debug=N

//查询验证MRO_REG临时表取消状态
queryMroRegCancel.Type=TSQL
queryMroRegCancel.SQL=SELECT MRO_REGNO FROM MRO_REG WHERE CANCEL_FLG = 'Y'
queryMroRegCancel.item=MRO_REGNO;SEQ
queryMroRegCancel.MRO_REGNO=MRO_REGNO=<MRO_REGNO>
queryMroRegCancel.SEQ=SEQ=<SEQ>
queryMroRegCancel.Debug=N

//修改病历主档表案卷的存放位置
updateMroMrvStoreLocation.Type=TSQL
updateMroMrvStoreLocation.SQL=UPDATE MRO_MRV SET CURT_LOCATION=<CURT_LOCATION>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> 
updateMroMrvStoreLocation.item=MR_NO;ADM_TYPE;BOX_CODE;BOOK_NO
updateMroMrvStoreLocation.MR_NO=MR_NO=<MR_NO>
updateMroMrvStoreLocation.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
updateMroMrvStoreLocation.BOX_CODE=BOX_CODE=<BOX_CODE>
updateMroMrvStoreLocation.BOOK_NO=BOOK_NO=<BOOK_NO>
updateMroMrvStoreLocation.Debug=N

//案卷合并后删除多余数据
deleteMroMrv.Type=TSQL
deleteMroMrv.SQL=DELETE FROM MRO_MRV 
deleteMroMrv.item=MR_NO;ADM_TYPE;BOX_CODE;BOOK_NO
deleteMroMrv.MR_NO=MR_NO=<MR_NO>
deleteMroMrv.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
deleteMroMrv.BOX_CODE=BOX_CODE=<BOX_CODE>
deleteMroMrv.BOOK_NO=BOOK_NO=<BOOK_NO>
deleteMroMrv.Debug=N