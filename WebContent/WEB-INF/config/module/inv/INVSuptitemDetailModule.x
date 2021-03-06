   #
   # Title: 灭菌记账明细
   #
   # Description: 灭菌记账明细
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insert;update;delete;query


//新增
insert.Type=TSQL
insert.SQL=INSERT INTO INV_SUPTITEMDETAIL( &
			SUP_DETAIL_NO, SUP_DETAIL_SEQ, SUP_DATE, USE_DEPT, SUPITEM_CODE, &
			QTY, COST_PRICE, ADD_PRICE, DESCRIPTION, CASHIER_CODE, &
			OPT_USER, OPT_DATE, OPT_TERM ) &
		      VALUES( &
	    	        <SUP_DETAIL_NO>, <SUP_DETAIL_SEQ>, <SUP_DATE>, <USE_DEPT>, <SUPITEM_CODE>, &
			<QTY>, <COST_PRICE>, <ADD_PRICE>, <DESCRIPTION>, <CASHIER_CODE>, &
			<OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insert.Debug=N

//更新
update.Type=TSQL
update.SQL=UPDATE INV_SUPTITEMDETAIL SET &
			SUP_DATE=<SUP_DATE>, USE_DEPT=<USE_DEPT>, SUPITEM_CODE=<SUPITEM_CODE>, QTY=<QTY>, &
			COST_PRICE=<COST_PRICE>, ADD_PRICE=<ADD_PRICE>, DESCRIPTION=<DESCRIPTION>, CASHIER_CODE=<CASHIER_CODE>,&
			OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> &
		  WHERE SUP_DETAIL_NO = <SUP_DETAIL_NO> AND SUP_DETAIL_SEQ = <SUP_DETAIL_SEQ>
update.Debug=N

//删除
delete.Type=TSQL
delete.SQL=DELETE FROM INV_SUPTITEMDETAIL WHERE SUP_DETAIL_NO = <SUP_DETAIL_NO> AND SUP_DETAIL_SEQ = <SUP_DETAIL_SEQ>
delete.Debug=N

//查询
query.Type=TSQL
query.SQL=SELECT A.SUP_DETAIL_NO, A.SUP_DETAIL_SEQ, A.SUP_DATE, A.USE_DEPT, &
      		 B.SUPITEM_DESC, A.QTY, A.COST_PRICE, A.ADD_PRICE, A.CASHIER_CODE, &
       		 A.DESCRIPTION,A.SUPITEM_CODE &
  	    FROM INV_SUPTITEMDETAIL A, INV_SUPTITEM B &
  	    WHERE A.SUPITEM_CODE = B.SUPITEM_CODE
query.ITEM=SUP_DETAIL_NO;SUP_DETAIL_SEQ;USE_DEPT;START_DATE;SUPITEM_CODE
query.SUP_DETAIL_NO=A.SUP_DETAIL_NO=<SUP_DETAIL_NO>
query.SUP_DETAIL_SEQ=A.SUP_DETAIL_SEQ=<SUP_DETAIL_SEQ>
query.USE_DEPT=A.USE_DEPT=<USE_DEPT>
query.START_DATE=A.SUP_DATE BETWEEN <START_DATE> AND <END_DATE>
query.SUPITEM_CODE=A.SUPITEM_CODE=<SUPITEM_CODE>
query.Debug=N



