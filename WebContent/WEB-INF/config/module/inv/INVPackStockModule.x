Module.item=getStockSeq;updateQty;checkPackCount;insertPack;getPackQty;updatePackStatus;deletePack;getPackDate;getSumPackQty;updatePackStockMQty


//��ѯ���������
getStockSeq.Type=TSQL
getStockSeq.SQL=SELECT MAX(PACK_SEQ_NO) FROM INV_PACKSTOCKM 
getStockSeq.item=PACK_CODE
getStockSeq.PACK_CODE=PACK_CODE=<PACK_CODE>
getStockSeq.Debug=Y



//���¿������
updateQty.Type=TSQL
updateQty.SQL=UPDATE INV_PACKSTOCKM &
                    SET QTY=QTY+<QTY>
updateQty.item=PACK_CODE
updateQty.PACK_CODE=PACK_CODE=<PACK_CODE>
updateQty.Debug=Y


//�����������Ƿ����
checkPackCount.Type=TSQL
checkPackCount.SQL=SELECT PACK_CODE FROM INV_PACKSTOCKM 
checkPackCount.item=PACK_CODE
checkPackCount.PACK_CODE=PACK_CODE=<PACK_CODE>
checkPackCount.Debug=Y


//�����µ�������
insertPack.Type=TSQL
insertPack.SQL=INSERT INTO INV_PACKSTOCKM &
                           (ORG_CODE,PACK_CODE,PACK_SEQ_NO,DESCRIPTION,QTY,DISINFECTION_DATE,&
                        	DISINFECTION_VALID_DATE,DISINFECTION_USER,USE_COST,ONCE_USE_COST,STATUS,&
                            OPT_USER,OPT_DATE,OPT_TERM)&
                     VALUES (<ORG_CODE>,<PACK_CODE>,<PACK_SEQ_NO>,<DESCRIPTION>,<QTY>,<DISINFECTION_DATE>,&
                            <DISINFECTION_VALID_DATE>,<DISINFECTION_USER>,<USE_COST>,<ONCE_USE_COST>,<STATUS>,&
                            <OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertPack.Debug=Y

//���Ҳ�����Ź����������
getPackQty.Type=TSQL
getPackQty.SQL=SELECT QTY FROM INV_PACKSTOCKM 
getPackQty.item=PACK_CODE
getPackQty.PACK_CODE=PACK_CODE=<PACK_CODE>
getPackQty.Debug=Y



//������������״̬
updatePackStatus.Type=TSQL
updatePackStatus.SQL=UPDATE INV_PACKSTOCKM &
                        SET STATUS=<STATUS>
updatePackStatus.item=PACK_CODE;PACK_SEQ_NO
updatePackStatus.PACK_CODE=PACK_CODE=<PACK_CODE>
updatePackStatus.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>
updatePackStatus.Debug=Y




//ɾ��������
deletePack.Type=TSQL
deletePack.SQL=DELETE INV_PACKSTOCKM &
                        WHERE PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
deletePack.Debug=Y

//������������������Ч��
getPackDate.Type=TSQL
getPackDate.SQL=SELECT DISINFECTION_DATE,DISINFECTION_VALID_DATE,DISINFECTION_USER,OPT_USER FROM INV_PACKSTOCKM 
getPackDate.item=PACK_CODE;PACK_SEQ_NO
getPackDate.PACK_CODE=PACK_CODE=<PACK_CODE>
getPackDate.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>
getPackDate.Debug=Y


//������������
getSumPackQty.Type=TSQL
getSumPackQty.SQL=SELECT SUM(QTY) QTY FROM INV_PACKSTOCKM WHERE PACK_CODE=<PACK_CODE> AND STATUS IN ('0','3')
getSumPackQty.Debug=Y


//���¿������
updatePackStockMQty.Type=TSQL
updatePackStockMQty.SQL=UPDATE INV_PACKSTOCKM SET QTY=QTY-<QTY>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
		        WHERE PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updatePackStockMQty.Debug=Y








