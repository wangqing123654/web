   #
   # Title: ���ʵ�λ
   #
   # Description: ���ʵ�λ
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insert;update;delete


//�������ʵ�λ
insert.Type=TSQL
insert.SQL=INSERT INTO INV_TRANSUNIT( &
			INV_CODE, PURCH_UNIT, PURCH_QTY, STOCK_UNIT, STOCK_QTY, &
			DISPENSE_UNIT, DISPENSE_QTY, OPT_USER, OPT_DATE, OPT_TERM ) &
		       VALUES( &
	    	        <INV_CODE>, <PURCH_UNIT>, <PURCH_QTY>, <STOCK_UNIT>, <STOCK_QTY>, &
			<DISPENSE_UNIT>, <DISPENSE_QTY>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insert.Debug=N

//�������ʵ�λ
update.Type=TSQL
update.SQL=UPDATE INV_TRANSUNIT SET &
			PURCH_UNIT=<PURCH_UNIT>, PURCH_QTY=<PURCH_QTY>, STOCK_UNIT=<STOCK_UNIT>, STOCK_QTY=<STOCK_QTY>, &
			DISPENSE_UNIT=<DISPENSE_UNIT>, DISPENSE_QTY=<DISPENSE_QTY>, OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> &
		  WHERE INV_CODE = <INV_CODE>
update.Debug=N

//ɾ�����ʵ�λ
delete.Type=TSQL
delete.SQL=DELETE FROM INV_TRANSUNIT WHERE INV_CODE = <INV_CODE>
delete.Debug=N



