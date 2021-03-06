# 
#  Title:医保卡结算档module
# 
#  Description:医保卡结算档  INS_OPD 表module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author pangben 2011-11-07
#  version 1.0
#

Module.item=insertINSOpd;updateINSOpd

//新增

insertINSOpd.Type=TSQL
insertINSOpd.SQL=INSERT INTO INS_OPD &
            (REGION_CODE, CASE_NO,CONFIRM_NO,MR_NO,PHA_AMT,PHA_NHI_AMT,EXM_AMT,EXM_NHI_AMT,TREAT_AMT,&
            TREAT_NHI_AMT, OP_AMT, OP_NHI_AMT, BED_AMT, BED_NHI_AMT, MATERIAL_AMT,MATERIAL_NHI_AMT,OTHER_AMT,OTHER_NHI_AMT,&
            BLOODALL_AMT, BLOODALL_NHI_AMT, BLOOD_AMT, BLOOD_NHI_AMT, TOT_AMT,NHI_AMT,START_STANDARD_AMTS,OTOT_PAY_AMT,&
            START_STANDARD_AMT, ADD_AMT, OWN_AMT, INS_STANDARD_AMT, TRANBLOOD_OWN_AMT,APPLY_AMT,&
            OTOT_AMT,OINSTOT_AMT,INS_DATE,INSAMT_FLG,OPT_USER,OPT_DATE,OPT_TERM,ACCOUNT_PAY_AMT,UNACCOUNT_PAY_AMT,ACCOUNT_LEFT_AMT,&
            REFUSE_AMT,REFUSE_ACCOUNT_PAY_AMT,REFUSE_DESC,CONFIRM_F_USER,CONFIRM_F_DATE,CONFIRM_S_USER,CONFIRM_S_DATE,SLOW_DATE,&
            SLOW_PAY_DATE,REFUSE_CODE,REFUSE_OTOT_AMT,RECEIPT_NO,MZ_DIAG,DRUG_DAYS,INS_STD_AMT,INS_CROWD_TYPE,INS_PAT_TYPE,REG_OPB_FLG,&
            OWEN_PAY_SCALE,REDUCE_PAY_SCALE,REAL_OWEN_PAY_SCALE,SALVA_PAY_SCALE,BASEMED_BALANCE,INS_BALANCE,ISSUE,BCSSQF_STANDRD_AMT,&
            PERCOPAYMENT_RATE_AMT,INS_HIGHLIMIT_AMT,TOTAL_AGENT_AMT,FLG_AGENT_AMT,APPLY_AMT_R,FLG_AGENT_AMT_R,REFUSE_DATE,REAL_PAY_LEVEL,&
            VIOLATION_REASON_CODE,ARMY_AI_AMT,SERVANT_AMT,INS_PAY_AMT,UNREIM_AMT,REIM_TYPE)&
             VALUES&
             (<REGION_CODE>, <CASE_NO>,<CONFIRM_NO>,<MR_NO>,<PHA_AMT>,<PHA_NHI_AMT>,<EXM_AMT>,<EXM_NHI_AMT>,<TREAT_AMT>,&
            <TREAT_NHI_AMT>, <OP_AMT>, <OP_NHI_AMT>, <BED_AMT>, <BED_NHI_AMT>, <MATERIAL_AMT>,<MATERIAL_NHI_AMT>,<OTHER_AMT>,<OTHER_NHI_AMT>,&
            <BLOODALL_AMT>, <BLOODALL_NHI_AMT>, <BLOOD_AMT>, <BLOOD_NHI_AMT>, <TOT_AMT>,<NHI_AMT>,<START_STANDARD_AMTS>,<OTOT_PAY_AMT>,&
            <START_STANDARD_AMT>, <ADD_AMT>, <OWN_AMT>, <INS_STANDARD_AMT>, <TRANBLOOD_OWN_AMT>,<APPLY_AMT>,&
            <OTOT_AMT>,<OINSTOT_AMT>,<INS_DATE>,<INSAMT_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>,<ACCOUNT_PAY_AMT>,<UNACCOUNT_PAY_AMT>,<ACCOUNT_LEFT_AMT>,&
            <REFUSE_AMT>,<REFUSE_ACCOUNT_PAY_AMT>,<REFUSE_DESC>,<CONFIRM_F_USER>,<CONFIRM_F_DATE>,<CONFIRM_S_USER>,<CONFIRM_S_DATE>,<SLOW_DATE>,&
            <SLOW_PAY_DATE>,<REFUSE_CODE>,<REFUSE_OTOT_AMT>,<RECEIPT_NO>,<MZ_DIAG>,<DRUG_DAYS>,<INS_STD_AMT>,<INS_CROWD_TYPE>,<INS_PAT_TYPE>,<REG_OPB_FLG>,&
            <OWEN_PAY_SCALE>,<REDUCE_PAY_SCALE>,<REAL_OWEN_PAY_SCALE>,<SALVA_PAY_SCALE>,<BASEMED_BALANCE>,<INS_BALANCE>,<ISSUE>,<BCSSQF_STANDRD_AMT>,&
            <PERCOPAYMENT_RATE_AMT>,<INS_HIGHLIMIT_AMT>,<TOTAL_AGENT_AMT>,<FLG_AGENT_AMT>,<APPLY_AMT_R>,<FLG_AGENT_AMT_R>,<REFUSE_DATE>,<REAL_PAY_LEVEL>,&
            <VIOLATION_REASON_CODE>,<ARMY_AI_AMT>,<SERVANT_AMT>,<INS_PAY_AMT>,<UNREIM_AMT>,<REIM_TYPE>)
insertINSOpd.Debug=N

//INSAMT_FLG对账标志为3
updateINSOpd.Type=TSQL
updateINSOpd.SQL=UPDATE INS_OPD SET INSAMT_FLG=3 WHERE REGION_CODE=<REGION_CODE> AND CASE_NO=<CASE_NO> AND CONFIRM_NO=<CONFIRM_NO>
updateINSOpd.Debug=N