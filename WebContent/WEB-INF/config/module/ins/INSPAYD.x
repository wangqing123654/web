Module.item=query;queryInsPayd;queryInsOrderList;insertInsPayDList;queryInsRuel;queryLimit;queryAddpay;queryAllNo;deleteInsPaydData;queryStartLinePrice;queryPlanPrice;queryInsOrderPrice;updateInsPaymData
//查询医保病患资料
query.Type=TSQL                                                                                                                     
query.SQL=SELECT IN_DEPT,MR_NO,PAT_NAME,ADM_DATE,INS_COMPANY,CTZ_CODE,INS_PAY_KIND,BED_NO,IPD_NO,AGE,ID_NO,DIS_DATE,OFFICE,CASE_NO,INS_STATUS,INS_ACCUL_AMT,ADM_TIMES,INP_DIAG,DIS_DIAG,OUT_STATUS,TOTAL_AMT,NHI_NO,SEX FROM INS_PAYM WHERE INS_COMPANY LIKE <INS_COMPANY> AND CTZ_CODE LIKE <CTZ> AND INS_PAY_KIND LIKE <INS_PAY> AND INS_STATUS LIKE <INS_STATUS> AND IN_DEPT LIKE <DEPT_CODE>
query.Debug=N
//申报明细结帐表查询
queryInsPayd.Type=TSQL
queryInsPayd.SQL=SELECT INS_COMPANY,CASE_NO,IPD_NO,MR_NO,FEE_TYPE,P1_INS_RATE,P1_INS_AMT,P1_OWN_RATE,P1_OWN_AMT FROM INS_PAYD WHERE INS_COMPANY=<INS_COMPANY> AND CASE_NO=<CASE_NO>
queryInsPayd.Debug=N
//查询费用分割档相关数据
queryInsOrderList.Type=TSQL
queryInsOrderList.SQL=SELECT FEE_TYPE,AMTPAY_FLG,ADDPAY_FLG,LIMIT_PRICE_FLG,ALLPRICE_FLG,SUM(OWN_AMT) AS OWN_AMT,SUM(NHI_AMT) AS NHI_AMT,SUM(TOTAL_AMT) AS TOTAL_AMT,SUM(LIMIT_PRICE) AS LIMIT_PRICE,SUM(AMTPAY_AMT) AS AMTPAY_AMT FROM INS_ORDER WHERE CASE_NO=<CASE_NO> GROUP BY FEE_TYPE,AMTPAY_FLG,ADDPAY_FLG,LIMIT_PRICE_FLG,ALLPRICE_FLG
queryInsOrderList.Debug=N
//插入结算表INS_PAYD
insertInsPayDList.Type=TSQL
insertInsPayDList.SQL=INSERT INTO INS_PAYD (INS_COMPANY,CASE_NO,IPD_NO,MR_NO,FEE_TYPE,P1_INS_RATE,P1_INS_AMT,P1_OWN_RATE,P1_OWN_AMT,OPT_USER,OPT_DATE,OPT_TERM ) VALUES (<INS_COMPANY>,<CASE_NO>,<IPD_NO>,<MR_NO>,<FEE_TYPE>,<P1_INS_RATE>,<P1_INS_AMT>,<P1_OWN_RATE>,<P1_OWN_AMT>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertInsPayDList.Debug=N
//查询支付标准档的支付比例总额支付
queryInsRuel.Type=TSQL
queryInsRuel.SQL=SELECT OWN_RATE FROM INS_RULE WHERE NHI_COMPANY=<INS_COMPANY> AND FEE_TYPE=<FEE_TYPE> AND CTZ_CODE=<CTZ_CODE> AND AMTPAY_FLG='Y' AND <OWN_AMT> BETWEEN START_RANGE AND END_RANGE
queryInsRuel.Debug=N
//最高限价支付比例
queryLimit.Type=TSQL
queryLimit.SQL=SELECT OWN_RATE FROM INS_RULE WHERE NHI_COMPANY=<INS_COMPANY> AND FEE_TYPE=<FEE_TYPE> AND CTZ_CODE=<CTZ_CODE> AND LIMIT_PRICE_FLG='Y' AND <OWN_AMT> BETWEEN START_RANGE AND END_RANGE
queryLimit.Debug=N
//累计支付
queryAddpay.Type=TSQL
queryAddpay.SQL=SELECT OWN_RATE FROM INS_RULE WHERE NHI_COMPANY=<INS_COMPANY> AND FEE_TYPE=<FEE_TYPE> AND CTZ_CODE=<CTZ_CODE> AND ADDPAY_FLG='Y' AND <OWN_AMT> BETWEEN START_RANGE AND END_RANGE
queryAddpay.Debug=N
//全为N的情况
queryAllNo.Type=TSQL
queryAllNo.SQL=SELECT OWN_RATE FROM INS_RULE WHERE NHI_COMPANY=<INS_COMPANY> AND FEE_TYPE=<FEE_TYPE> AND CTZ_CODE=<CTZ_CODE> AND AMTPAY_FLG='N' AND LIMIT_PRICE_FLG='N' AND ADDPAY_FLG='N' AND <OWN_AMT> BETWEEN START_RANGE AND END_RANGE
queryAllNo.Debug=N
//删除结算数据
deleteInsPaydData.Type=TSQL
deleteInsPaydData.SQL=DELETE FROM INS_PAYD WHERE  INS_COMPANY=<INS_COMPANY> AND CASE_NO=<CASE_NO> AND FEE_TYPE=<FEE_TYPE>
deleteInsPaydData.Debug=N
//查询起付线
queryStartLinePrice.Type=TSQL
queryStartLinePrice.SQL=SELECT STARTLINE_PRICE,FEE_TYPE,OWN_RATE FROM INS_RULE WHERE NHI_COMPANY=<INS_COMPANY> AND CTZ_CODE=<CTZ_CODE> AND STARTLINE_FLG='Y' AND <ADM_TIMES> BETWEEN START_RANGE AND END_RANGE
queryStartLinePrice.Debug=N
//查询统筹
queryPlanPrice.Type=TSQL
queryPlanPrice.SQL=SELECT PLAN_PRICE,FEE_TYPE,OWN_RATE,START_RANGE,END_RANGE FROM INS_RULE WHERE NHI_COMPANY=<INS_COMPANY> AND CTZ_CODE=<CTZ_CODE> AND PLAN_FLG='Y'
queryPlanPrice.Debug=N
//查询统筹和住院费用
queryInsOrderPrice.Type=TSQL
queryInsOrderPrice.SQL=SELECT SUM(NHI_AMT) AS NHI_AMT,SUM(TOTAL_AMT) AS TOTAL_AMT,SUM(PLAN_PRICE) AS PLAN_PRICE FROM INS_ORDER WHERE CASE_NO=<CASE_NO>
queryInsOrderPrice.Debug=N
//更新INS_PAYM数据汇入
updateInsPaymData.Type=TSQL
updateInsPaymData.SQL=UPDATE INS_PAYM SET CTZ_CODE=<CTZ_CODE>,INS_PAY_KIND=<INS_PAY_KIND>,OFFICE=<OFFICE>,INS_ACCUL_AMT=<INS_ACCUL_AMT>,ADM_TIMES=<ADM_TIMES>,IN_DEPT=<IN_DEPT>,DIS_DATE=<DIS_DATE>,INP_DIAG=<INP_DIAG>,DIS_DIAG=<DIS_DIAG>,OUT_STATUS=<OUT_STATUS>,TOTAL_AMT=<TOTAL_AMT>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,BED_NO=<BED_NO> WHERE INS_COMPANY=<INS_COMPANY> AND CASE_NO=<CASE_NO> AND NHI_NO=<NHI_NO> AND IPD_NO=<IPD_NO> AND MR_NO=<MR_NO>
updateInsPaymData.Debug=N
