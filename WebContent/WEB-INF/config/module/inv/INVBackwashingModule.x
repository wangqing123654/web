   #
   # Title: 反洗率模块
   #
   # Description: 反洗率模块
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author sdr 2013/08/01

Module.item=insertBSInfo;queryPackMByBarcode;queryPackRByBarcode;queryBSInfo;updateBSInfo;deleteBSInfo;queryBSCount


//插入反洗率登记表
insertBSInfo.Type=TSQL
insertBSInfo.SQL=INSERT INTO INV_BACKWASHING &
			( ID, BARCODE, PACK_CODE, PACK_SEQ_NO, BACKWASHING_REASON, BACKWASHING_DATE, & 
			  POT_SEQ, PROGRAM, OPERATIONSTAFF, REMARK, OPT_USER, OPT_DATE, OPT_TERM, ORG_CODE ) &
			VALUES &  
			( SYS_GUID(), <BARCODE>, <PACK_CODE>, <PACK_SEQ_NO>, <BACKWASHING_REASON>, TO_DATE(<BACKWASHING_DATE>,'yyyy/mm/dd hh24:mi:ss'), & 
			  <POT_SEQ>, <PROGRAM>, <OPERATIONSTAFF>, <REMARK>, <OPT_USER>,  TO_DATE(<OPT_DATE>,'yyyy/mm/dd hh24:mi:ss'), <OPT_TERM>, <ORG_CODE> )
insertBSInfo.Debug=N

//根据条码在手术包主表中查询手术包类型和序号
queryPackMByBarcode.Type=TSQL
queryPackMByBarcode.SQL=SELECT M.PACK_CODE, M.PACK_SEQ_NO FROM INV_PACKSTOCKM M WHERE M.BARCODE = <BARCODE>
queryPackMByBarcode.Debug=N


//根据条码在打包表中查询手术包类型和序号
queryPackRByBarcode.Type=TSQL
queryPackRByBarcode.SQL=SELECT R.PACK_CODE, R.PACK_SEQ_NO FROM INV_REPACK R WHERE R.OLDBARCODE = <BARCODE>
queryPackRByBarcode.Debug=N


//根据条件查询反洗率记录
queryBSInfo.Type=TSQL
queryBSInfo.SQL=SELECT B.ID, B.BARCODE, B.PACK_CODE, B.PACK_SEQ_NO, B.BACKWASHING_REASON, B.BACKWASHING_DATE, & 
                       B.POT_SEQ, B.PROGRAM, B.OPERATIONSTAFF, B.REMARK, B.OPT_USER, B.OPT_DATE, B.OPT_TERM, B.ORG_CODE, P.PACK_DESC AS PACK_CHN_DESC & 
		       FROM INV_BACKWASHING B LEFT JOIN INV_PACKM P ON B.PACK_CODE = P.PACK_CODE &
		       WHERE B.BACKWASHING_DATE BETWEEN TO_DATE(<BACKWASHING_DATE_BEGIN>,'yyyy/mm/dd hh24:mi:ss') AND TO_DATE(<BACKWASHING_DATE_END>,'yyyy/mm/dd hh24:mi:ss')
queryBSInfo.ITEM=BARCODE;PACK_CODE;ORG_CODE
queryBSInfo.BARCODE=B.BARCODE=<BARCODE>
queryBSInfo.PACK_CODE=B.PACK_CODE=<PACK_CODE>
queryBSInfo.ORG_CODE=B.ORG_CODE=<ORG_CODE>
queryBSInfo.Debug=N


//更新反洗率记录
updateBSInfo.Type=TSQL
updateBSInfo.SQL=UPDATE INV_BACKWASHING SET BACKWASHING_REASON=<BACKWASHING_REASON>,BACKWASHING_DATE=TO_DATE(<BACKWASHING_DATE>,'yyyy/mm/dd hh24:mi:ss'),ORG_CODE=<ORG_CODE>, &
		      POT_SEQ=<POT_SEQ>,PROGRAM=<PROGRAM>,OPERATIONSTAFF=<OPERATIONSTAFF>,REMARK=<REMARK> WHERE ID=<ID>
updateBSInfo.Debug=N



//删除
deleteBSInfo.Type=TSQL
deleteBSInfo.SQL=DELETE FROM INV_BACKWASHING WHERE ID=<ID>
deleteBSInfo.Debug=N



//反洗率统计查询
queryBSCount.Type=TSQL
queryBSCount.SQL=SELECT COUNT(B.PACK_CODE) AS PCOUNT,B.PACK_CODE,P.PACK_DESC & 
			FROM INV_BACKWASHING B LEFT JOIN INV_PACKM P ON B.PACK_CODE = P.PACK_CODE & 
			WHERE B.BACKWASHING_DATE BETWEEN TO_DATE(<BACKWASHING_DATE_START>, 'yyyy/mm/dd hh24:mi:ss') AND TO_DATE(<BACKWASHING_DATE_END>, 'yyyy/mm/dd hh24:mi:ss') & 
			GROUP BY B.PACK_CODE,P.PACK_DESC
queryBSCount.ITEM=PACK_CODE
queryBSCount.PACK_CODE=B.PACK_CODE=<PACK_CODE>
queryBSCount.Debug=N