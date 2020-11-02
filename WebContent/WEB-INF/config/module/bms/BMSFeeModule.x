   #
   # Title:ѪҺ�����ֵ�
   #
   # Description:ѪҺ�����ֵ�
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author chenx 2013/04/24

Module.item=insert;update;query;delete;selectSubcat;selectOrdercode;selectOrderDetail;insertDdata;selectAdmInpFee;updateAdmInp
//����
insert.Type=TSQL
insert.SQL=INSERT INTO BMS_BLDFEE(BLD_CODE,SUBCAT_CODE,REMARK,DAY_END,BILL_CODE,MANG_CODE,CROSS_CODE,ANTI_CODE,      &
                                 ORDER_CODE,BLETEST_CODE,SERV_CODE,R_FILTER_CODE,B_FILTER_CODE,UNIT,UITOML,          &
                                 MANG_UNIT, OPT_USER,OPT_DATE,OPT_TERM ,BILL_DESC,MANG_DESC,CROSS_DESC,              &
                                 ANTI_DESC,ORDER_DESC,BLETEST_DESC,SERV_DESC,B_FILTER_DESC,R_FILTER_DESC)            &
			       VALUES(<BLD_CODE>, <SUBCAT_CODE>, <REMARK>, <DAY_END>, <BILL_CODE>,                   & 
			              <MANG_CODE>,<CROSS_CODE>,<ANTI_CODE>,<ORDER_CODE>,<BLETEST_CODE>,              &
			              <SERV_CODE>,<R_FILTER_CODE>,<B_FILTER_CODE>,                                   &
				      <UNIT>, <UITOML>, <MANG_UNIT>, <OPT_USER>, SYSDATE, <OPT_TERM> ,               &
				      <BILL_DESC>,<MANG_DESC>,<CROSS_DESC>,<ANTI_DESC>,<ORDER_DESC>,<BLETEST_DESC>,  &
				      <SERV_DESC>,<B_FILTER_DESC>,<R_FILTER_DESC>)            
insert.Debug=N
//����
update.Type=TSQL
update.SQL=UPDATE BMS_BLDFEE  SET  BLD_CODE = <BLD_CODE> ,SUBCAT_CODE = <SUBCAT_CODE> ,ORDER_CODE = <ORDER_CODE>,                        &
                                REMARK= <REMARK>,DAY_END=<DAY_END>,  BILL_CODE= <BILL_CODE>,MANG_CODE=<MANG_CODE>,                       &
                                CROSS_CODE= <CROSS_CODE>,ANTI_CODE=<ANTI_CODE>,  BLETEST_CODE= <BLETEST_CODE>,SERV_CODE=<SERV_CODE>,     &
                                R_FILTER_CODE= <R_FILTER_CODE>,B_FILTER_CODE=<B_FILTER_CODE>,  UNIT= <UNIT>,UITOML=<UITOML>,             &
                                MANG_UNIT = <MANG_UNIT>,OPT_USER= <OPT_USER>,OPT_DATE=SYSDATE,  OPT_TERM= <OPT_TERM> ,                    &
                                BILL_DESC=<BILL_DESC>,MANG_DESC=<MANG_DESC>,CROSS_DESC=<CROSS_DESC>,ANTI_DESC=<ANTI_DESC>,              &
                                ORDER_DESC=<ORDER_DESC>,BLETEST_DESC=<BLETEST_DESC>,SERV_DESC=<SERV_DESC>,                             &
                                B_FILTER_DESC=<B_FILTER_DESC>,R_FILTER_DESC=<R_FILTER_DESC>                                        &
                                WHERE  BLD_CODE = <BLD_CODE>  AND  SUBCAT_CODE = <SUBCAT_CODE>
update.Debug=N

//��ѯ
query.Type=TSQL
query.SQL=SELECT *  FROM   BMS_BLDFEE  
query.ITEM=BLD_CODE;SUBCAT_CODE
query.BLD_CODE=BLD_CODE=<BLD_CODE>
query.SUBCAT_CODE=SUBCAT_CODE=<SUBCAT_CODE>
query.Debug=N


//ɾ��
delete.Type=TSQL
delete.SQL=DELETE FROM BMS_BLDFEE WHERE BLD_CODE=<BLD_CODE> AND SUBCAT_CODE=<SUBCAT_CODE> 
delete.Debug=N
//��ѯѪƷ�����Ϣ
selectSubcat.Type=TSQL
selectSubcat.SQL=SELECT BLD_VOL,UNIT_CODE FROM   BMS_BLDSUBCAT  WHERE  BLD_CODE=<BLD_CODE> AND  SUBCAT_CODE=<SUBCAT_CODE>
selectSubcat.Debug=Y
//����ѪƷ����ѯ�Ʒѵ�order_code
selectOrdercode.Type=TSQL
selectOrdercode.SQL=SELECT BILL_CODE  AS ORDER_CODE  FROM   BMS_BLDFEE  WHERE  BLD_CODE=<BLD_CODE> AND  SUBCAT_CODE=<SUBCAT_CODE>  &
                    AND BILL_CODE IS NOT NULL  UNION  &
                    SELECT MANG_CODE  AS ORDER_CODE  FROM   BMS_BLDFEE  WHERE  BLD_CODE=<BLD_CODE> AND  SUBCAT_CODE=<SUBCAT_CODE>  &
                    AND    MANG_CODE IS NOT NULL  UNION  &
                    SELECT CROSS_CODE  AS ORDER_CODE  FROM   BMS_BLDFEE  WHERE  BLD_CODE=<BLD_CODE> AND  SUBCAT_CODE=<SUBCAT_CODE>  &
                    AND    CROSS_CODE IS NOT NULL  UNION  &
                    SELECT ANTI_CODE  AS ORDER_CODE  FROM   BMS_BLDFEE  WHERE  BLD_CODE=<BLD_CODE> AND  SUBCAT_CODE=<SUBCAT_CODE>  &
                    AND    ANTI_CODE IS NOT NULL  UNION  &
                    SELECT  ORDER_CODE  AS ORDER_CODE  FROM   BMS_BLDFEE  WHERE  BLD_CODE=<BLD_CODE> AND  SUBCAT_CODE=<SUBCAT_CODE>  &
                    AND     ORDER_CODE IS NOT NULL  UNION  &
                    SELECT BLETEST_CODE  AS ORDER_CODE  FROM   BMS_BLDFEE  WHERE  BLD_CODE=<BLD_CODE> AND  SUBCAT_CODE=<SUBCAT_CODE>  &
                    AND    BLETEST_CODE IS NOT NULL  UNION  &
                    SELECT SERV_CODE  AS ORDER_CODE  FROM   BMS_BLDFEE  WHERE  BLD_CODE=<BLD_CODE> AND  SUBCAT_CODE=<SUBCAT_CODE>  &
                    AND     SERV_CODE IS NOT NULL  UNION  &
                    SELECT B_FILTER_CODE  AS ORDER_CODE  FROM   BMS_BLDFEE  WHERE  BLD_CODE=<BLD_CODE> AND  SUBCAT_CODE=<SUBCAT_CODE>  &
                    AND    B_FILTER_CODE IS NOT NULL  UNION  &
                    SELECT R_FILTER_CODE  AS ORDER_CODE  FROM   BMS_BLDFEE  WHERE  BLD_CODE=<BLD_CODE> AND  SUBCAT_CODE=<SUBCAT_CODE>  &
                    AND    R_FILTER_CODE IS NOT NULL  
selectOrdercode.Debug=N

//��ѯҽ����ϸ
selectOrderDetail.Type=TSQL
selectOrderDetail.SQL=SELECT ORDER_CODE,ORDER_DESC,ORDER_CAT1_CODE,CHARGE_HOSP_CODE,OWN_PRICE,NHI_PRICE,UNIT_CODE,EXEC_DEPT_CODE FROM SYS_FEE &
                      WHERE  ORDER_CODE = <ORDER_CODE>  AND  ACTIVE_FLG = 'Y'
selectOrderDetail.Debug=N

//������ϸ��
insertDdata.Type=TSQL
insertDdata.Type=TSQL
insertDdata.SQL=INSERT INTO IBS_ORDD &
			    (CASE_NO,CASE_NO_SEQ,SEQ_NO,BILL_DATE,ORDER_NO,&
			    ORDER_SEQ,ORDER_CODE,ORDER_CAT1_CODE,ORDERSET_GROUP_NO,ORDERSET_CODE,&
			    DEPT_CODE,STATION_CODE,DR_CODE,EXE_DEPT_CODE,EXE_STATION_CODE,EXE_DR_CODE,&
			    MEDI_QTY,MEDI_UNIT,DOSE_CODE,FREQ_CODE,TAKE_DAYS,&
			    DOSAGE_QTY,DOSAGE_UNIT,OWN_PRICE,NHI_PRICE,TOT_AMT,&
			    OWN_FLG,BILL_FLG,REXP_CODE,HEXP_CODE,BILL_NO,&
			    BEGIN_DATE,END_DATE,OWN_AMT,OWN_RATE,REQUEST_FLG,&
			    REQUEST_NO,INV_CODE,OPT_USER,OPT_DATE,OPT_TERM,&
			    CAT1_TYPE,INDV_FLG,COST_AMT,ORDER_CHN_DESC,DS_FLG,COST_CENTER_CODE) &
		     VALUES (<CASE_NO>,<CASE_NO_SEQ>,<SEQ_NO>,<BILL_DATE>,<ORDER_NO>,&
			    <ORDER_SEQ>,<ORDER_CODE>,<ORDER_CAT1_CODE>,<ORDERSET_GROUP_NO>,<ORDERSET_CODE>,&
			    <DEPT_CODE>,<STATION_CODE>,<DR_CODE>,<EXE_DEPT_CODE>,<EXE_STATION_CODE>,<EXE_DR_CODE>,&
			    <MEDI_QTY>,<MEDI_UNIT>,<DOSE_CODE>,<FREQ_CODE>,<TAKE_DAYS>,&
			    <DOSAGE_QTY>,<DOSAGE_UNIT>,<OWN_PRICE>,<NHI_PRICE>,<TOT_AMT>,&
			    <OWN_FLG>,<BILL_FLG>,<REXP_CODE>,<HEXP_CODE>,<BILL_NO>,&
			    <BEGIN_DATE>,<END_DATE>,<OWN_AMT>,<OWN_RATE>,<REQUEST_FLG>,&
			    <REQUEST_NO>,<INV_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>,&
			    <CAT1_TYPE>,<INDV_FLG>,<COST_AMT>,<ORDER_CHN_DESC>,<DS_FLG>,<COST_CENTER_CODE>)
insertDdata.Debug=N

//����adm_inp ��ҽ�ƽ���ʣ����
selectAdmInpFee.Type=TSQL
selectAdmInpFee.SQL=SELECT TOTAL_AMT,CUR_AMT  FROM ADM_INP WHERE CASE_NO = <CASE_NO>
selectAdmInpFee.Debug=N
//����adm_inp ����Ӧ�Ľ��
updateAdmInp.Type=TSQL
updateAdmInp.SQL=UPDATE ADM_INP  SET TOTAL_AMT = <TOTAL_AMT> ,CUR_AMT = <CUR_AMT> WHERE CASE_NO = <CASE_NO>
updateAdmInp.Debug=N

























