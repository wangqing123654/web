Module.item=getStockM;insertStockM;updateStockM;getStockQty;updateStockQty;updateStockOut;updateStockIn

//��ѯ��������
getStockM.Type=TSQL
getStockM.SQL=SELECT * FROM INV_STOCKM 
getStockM.item=INV_CODE;ORG_CODE;
getStockM.INV_CODE=INV_CODE=<INV_CODE>
getStockM.ORG_CODE=ORG_CODE=<ORG_CODE>
getStockM.Debug=Y

//����������
insertStockM.Type=TSQL
insertStockM.SQL=INSERT INTO INV_STOCKM (ORG_CODE,INV_CODE,REGION_CODE,STOCK_QTY,OPT_USER,&
                                         OPT_DATE,OPT_TERM,STOCK_UNIT)&
                                  VALUES (<ORG_CODE>,<INV_CODE>,<REGION_CODE>,<STOCK_QTY>,<OPT_USER>,&
                                          <OPT_DATE>,<OPT_TERM>,<STOCK_UNIT>)
insertStockM.Debug=Y


//���¿������
updateStockM.Type=TSQL
updateStockM.SQL=UPDATE INV_STOCKM SET STOCK_QTY=<STOCK_QTY>
updateStockM.item=INV_CODE;ORG_CODE
updateStockM.INV_CODE=INV_CODE=<INV_CODE>
updateStockM.ORG_CODE=ORG_CODE=<ORG_CODE>
updateStockM.Debug=Y

//���¿������
updateStockQty.Type=TSQL
updateStockQty.SQL=UPDATE INV_STOCKM SET STOCK_QTY=STOCK_QTY+<STOCK_QTY>
updateStockQty.item=INV_CODE;ORG_CODE
updateStockQty.INV_CODE=INV_CODE=<INV_CODE>
updateStockQty.ORG_CODE=ORG_CODE=<ORG_CODE>
updateStockQty.Debug=Y


//����ȫԺ���ʿ����
getStockQty.Type=TSQL
getStockQty.SQL=SELECT SUM(STOCK_QTY) FROM INV_STOCKM 
getStockQty.item=INV_CODE;ORG_CODE;
getStockQty.INV_CODE=INV_CODE=<INV_CODE>
getStockQty.ORG_CODE=ORG_CODE=<ORG_CODE>
getStockQty.Debug=Y


//�����������ʿ����(���ⲿ�ż��ٿ��)
updateStockOut.Type=TSQL
updateStockOut.SQL=UPDATE INV_STOCKM &
                   SET STOCK_QTY=STOCK_QTY-<QTY>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
                   WHERE INV_CODE=<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockOut.Debug=Y


//�����������ʿ����(��ⲿ�����ӿ��)
updateStockIn.Type=TSQL
updateStockIn.SQL=UPDATE INV_STOCKM &
                  SET STOCK_QTY=STOCK_QTY+<QTY>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
                  WHERE INV_CODE=<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockIn.Debug=Y





