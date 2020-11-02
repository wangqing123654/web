# 
#  Title:套餐余额结转收据主档module
# 
#  Description:套餐余额结转收据主档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author huangtt 2017.11.10
#  version 1.0
#
Module.item=insertMemReceipt;updateMemPackSessionD;getMemRecpCount;getSumAramt;accountAll;&
account;getSumAramt



//更新套为使用状态
updateMemPackSessionD.Type = TSQL
updateMemPackSessionD.SQL = UPDATE MEM_PAT_PACKAGE_SECTION_D SET USED_FLG='1' &
			WHERE ID=<ID> AND TRADE_NO=<TRADE_NO>
updateMemPackSessionD.Debug=N

//添加一条新的票据       套餐余额结转
insertMemReceipt.Type=TSQL
insertMemReceipt.SQL=INSERT INTO BIL_MEM_RECP( &
                   TRADE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO,&
                   PACKAGE_CODE,PACKAGE_DESC,&
                   RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE,&
                   PRINT_DATE,CHARGE01,CHARGE02,CHARGE03,CHARGE04,&
                   CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09,&
                   CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14,&
                   CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,&
                   CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24,&
                   CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,&
                   CHARGE30,TOT_AMT,REDUCE_REASON,REDUCE_AMT,&
                   REDUCE_DATE,REDUCE_DEPT_CODE,REDUCE_RESPOND,&
                   AR_AMT,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD,&
                   PAY_INS_CARD,PAY_CHECK,PAY_DEBIT,PAY_BILPAY,&
                   PAY_INS,PAY_OTHER1,PAY_OTHER2,PAY_REMARK,&
                   CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM,REDUCE_NO,&
                   PAY_OTHER3,PAY_OTHER4,TAX_FLG,TAX_DATE,TAX_USER,MEM_PACK_FLG) &
                   values(<TRADE_NO>,<RECEIPT_NO>,<ADM_TYPE>,&
                   <REGION_CODE>,<MR_NO>,<PACKAGE_CODE>,<PACKAGE_DESC>,<RESET_RECEIPT_NO>,&
                   <PRINT_NO>,<BILL_DATE>,<CHARGE_DATE>,<PRINT_DATE>,&
                   <CHARGE01>,<CHARGE02>,<CHARGE03>,<CHARGE04>,<CHARGE05>,&
                   <CHARGE06>,<CHARGE07>,<CHARGE08>,<CHARGE09>,<CHARGE10>,&
                   <CHARGE11>,<CHARGE12>,<CHARGE13>,<CHARGE14>,<CHARGE15>,&
                   <CHARGE16>,<CHARGE17>,<CHARGE18>,<CHARGE19>,<CHARGE20>,&
                   <CHARGE21>,<CHARGE22>,<CHARGE23>,<CHARGE24>,<CHARGE25>,&
                   <CHARGE26>,<CHARGE27>,<CHARGE28>,<CHARGE29>,<CHARGE30>,&
                   <TOT_AMT>,<REDUCE_REASON>,<REDUCE_AMT>,<REDUCE_DATE>,&
                   <REDUCE_DEPT_CODE>,<REDUCE_RESPOND>,<AR_AMT>,<PAY_CASH>,&
                   <PAY_MEDICAL_CARD>,<PAY_BANK_CARD>,<PAY_INS_CARD>,&
                   <PAY_CHECK>,<PAY_DEBIT>,<PAY_BILPAY>,<PAY_INS>,&
                   <PAY_OTHER1>,<PAY_OTHER2>,<PAY_REMARK>,<CASHIER_CODE>,<OPT_USER>,&
                   <OPT_DATE>,<OPT_TERM>,<REDUCE_NO>,<PAY_OTHER3>,<PAY_OTHER4>,&
                   <TAX_FLG>,<TAX_DATE>,<TAX_USER>,<MEM_PACK_FLG>)
insertMemReceipt.Debug=N

//得到日结票据张数
getMemRecpCount.Type=TSQL
getMemRecpCount.SQL=SELECT COUNT(PRINT_NO) COUNT FROM BIL_MEM_RECP &
		     WHERE ADM_TYPE = <ADM_TYPE> &
                           AND CASHIER_CODE=<CASHIER_CODE> &
                           AND PRINT_DATE<TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS') &
                           AND (ACCOUNT_FLG IS NULL OR ACCOUNT_FLG = 'N')
                           AND (RESET_RECEIPT_NO IS NULL OR RESET_RECEIPT_NO = '')
getMemRecpCount.Debug=N

//得到日结金额
getSumAramtAll.Type=TSQL
getSumAramtAll.SQL=SELECT SUM(AR_AMT) AR_AMT FROM BIL_MEM_RECP &
		WHERE CASHIER_CODE=<CASHIER_CODE> &
		  AND BILL_DATE<TO_DATE(<BILL_DATE>,'YYYYMMDDHH24MISS') &
		  AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL)
getSumAramtAll.Debug=N

//得到日结金额
getSumAramt.Type=TSQL
getSumAramt.SQL=SELECT SUM(AR_AMT) AR_AMT FROM BIL_MEM_RECP &
		WHERE CASHIER_CODE=<CASHIER_CODE> &
		  AND ADM_TYPE = <ADM_TYPE> &
		  AND BILL_DATE<TO_DATE(<BILL_DATE>,'YYYYMMDDHH24MISS') &
		  AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL)
getSumAramt.Debug=N

accountAll.Type=TSQL
accountAll.SQL=UPDATE BIL_MEM_RECP SET ACCOUNT_SEQ=<ACCOUNT_SEQ>,ACCOUNT_USER=<ACCOUNT_USER>,ACCOUNT_FLG='Y',ACCOUNT_DATE=SYSDATE &
            WHERE AND CASHIER_CODE=<CASHIER_CODE> &
                  AND BILL_DATE<TO_DATE(<BILL_DATE>,'YYYYMMDDHH24MISS') &
                  AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) 
accountAll.Debug=N

//account日结
account.Type=TSQL
account.SQL=UPDATE BIL_MEM_RECP SET ACCOUNT_SEQ=<ACCOUNT_SEQ>,ACCOUNT_USER=<ACCOUNT_USER>,ACCOUNT_FLG='Y',ACCOUNT_DATE=SYSDATE &
            WHERE ADM_TYPE=<ADM_TYPE> &
                  AND CASHIER_CODE=<CASHIER_CODE> &
                  AND BILL_DATE<TO_DATE(<BILL_DATE>,'YYYYMMDDHH24MISS') &
                  AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) 
account.Debug=N
