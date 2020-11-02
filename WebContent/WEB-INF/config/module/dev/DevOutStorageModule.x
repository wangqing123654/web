# 
#  Title:设备出库库操作module
# 
#  Description:设备出库库操作module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.06.15
#  version 1.0
#
Module.item=selectDevOutStorageInf;getExStorgeInf1;getExStorgeInf2;getDeptExStorgeQty;deleteExStorgeQty;modifyStorgeQty;insertExStorgeD;insertStock;updateStockD;insertExStorgeM;updateExStorgeD;queryExStorgeD;queryExReceiptData;updateStockM

//检索设备出库信息
selectDevOutStorageInf.Type=TSQL
selectDevOutStorageInf.SQL=SELECT EXWAREHOUSE_NO,EXWAREHOUSE_DATE,EXWAREHOUSE_USER,EXWAREHOUSE_DEPT,INWAREHOUSE_DEPT &
	                   FROM DEV_EXWAREHOUSEM &
	                   ORDER BY EXWAREHOUSE_NO
selectDevOutStorageInf.item=EXWAREHOUSE_NO;EXWAREHOUSE_DATE_BEGIN;EXWAREHOUSE_DEPT;INWAREHOUSE_DEPT;EXWAREHOUSE_USER
//出库单号
selectDevOutStorageInf.EXWAREHOUSE_NO=EXWAREHOUSE_NO LIKE <EXWAREHOUSE_NO>||'%'
//出库日期
selectDevOutStorageInf.EXWAREHOUSE_DATE_BEGIN=EXWAREHOUSE_DATE BETWEEN <EXWAREHOUSE_DATE_BEGIN> AND <EXWAREHOUSE_DATE_END>
//出库科室
selectDevOutStorageInf.EXWAREHOUSE_DEPT=EXWAREHOUSE_DEPT = <EXWAREHOUSE_DEPT>
//入库科室
selectDevOutStorageInf.INWAREHOUSE_DEPT=INWAREHOUSE_DEPT = <INWAREHOUSE_DEPT>
//出库人员
selectDevOutStorageInf.EXWAREHOUSE_USER=EXWAREHOUSE_USER = <EXWAREHOUSE_USER>
selectDevOutStorageInf.Debug=N



//查询待出库信息1
getExStorgeInf1.Type=TSQL
getExStorgeInf1.SQL=SELECT 'N' DEL_FLG,B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, 0 DEVSEQ_NO,&
		           B.DEV_CHN_DESC, B.DESCRIPTION, '' SETDEV_CODE , A.QTY STORGE_QTY, &
		           B.UNIT_CODE, B.UNIT_PRICE, A.CARE_USER, A.USE_USER, A.LOC_CODE,&
		           A.SCRAP_VALUE,A.GUAREP_DATE,A.DEP_DATE, A.DEPT_CODE,A.MAN_DATE,&
		           A.QTY,'' INWAREHOUSE_DEPT, A.QTY * B.UNIT_PRICE TOT_VALUE,'' REMARK1,'' REMARK2 &
		     FROM  DEV_STOCKM A,DEV_BASE B &
		     WHERE B.SEQMAN_FLG='N' &
		     AND A.DEPT_CODE=<DEPT_CODE> &
		     AND A.QTY <> 0 &
		     AND A.DEV_CODE=B.DEV_CODE &
		     ORDER BY B.DEV_CODE, A.BATCH_SEQ
getExStorgeInf1.item=DEV_CODE;DEVPRO_CODE
//设备编号
getExStorgeInf1.DEV_CODE=B.DEV_CODE = <DEV_CODE>
//设备属性
getExStorgeInf1.DEVPRO_CODE=B.DEVPRO_CODE = <DEVPRO_CODE>
getExStorgeInf1.Debug=N



//查询待出库信息2
getExStorgeInf2.Type=TSQL
getExStorgeInf2.SQL=SELECT 'N' DEL_FLG,B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, A.DEVSEQ_NO, &
		           B.DEV_CHN_DESC, B.DESCRIPTION, A.SETDEV_CODE , A.QTY STORGE_QTY, &
		           B.UNIT_CODE, A.UNIT_PRICE, A.CARE_USER, A.USE_USER, A.LOC_CODE, &
		           A.SCRAP_VALUE,A.GUAREP_DATE,A.DEP_DATE,A.DEPT_CODE,A.MAN_DATE, &
		           A.QTY,'' INWAREHOUSE_DEPT, A.QTY * A.UNIT_PRICE TOT_VALUE,'' REMARK1,'' REMARK2 &
		    FROM   DEV_STOCKD A,DEV_BASE B &
		    WHERE  B.SEQMAN_FLG='Y' &
		    AND A.DEPT_CODE=<DEPT_CODE> &
		    AND A.DEV_CODE=B.DEV_CODE &
		    ORDER BY B.DEV_CODE, A.BATCH_SEQ, A.DEVSEQ_NO
getExStorgeInf2.item=DEV_CODE;DEVSEQ_NO;DEVPRO_CODE
//设备编号
getExStorgeInf2.DEV_CODE=B.DEV_CODE = <DEV_CODE>
//设备序号
getExStorgeInf2.DEVSEQ_NO=A.DEVSEQ_NO = <DEVSEQ_NO>
//设备属性
getExStorgeInf2.DEVPRO_CODE=B.DEVPRO_CODE = <DEVPRO_CODE>
getExStorgeInf2.Debug=N


//取得出库科室设备库存
getDeptExStorgeQty.Type=TSQL
getDeptExStorgeQty.SQL=SELECT QTY &
	               FROM DEV_STOCKM &
	               WHERE DEPT_CODE=<DEPT_CODE> &
  	               AND   DEV_CODE=<DEV_CODE> &
  	               AND   BATCH_SEQ=<BATCH_SEQ>
getDeptExStorgeQty.Debug=N



//删除库存为零的信息
deleteExStorgeQty.Type=TSQL
deleteExStorgeQty.SQL=DELETE FROM DEV_STOCKM &
                      WHERE DEPT_CODE=<DEPT_CODE> &
  	              AND DEV_CODE=<DEV_CODE> &
  	              AND BATCH_SEQ=<BATCH_SEQ>
deleteExStorgeQty.Debug=N


//扣除库存
modifyStorgeQty.Type=TSQL
modifyStorgeQty.SQL=UPDATE DEV_STOCKM SET QTY=QTY+<QTY> &
	            WHERE DEPT_CODE=<DEPT_CODE> &
                    AND   DEV_CODE=<DEV_CODE> &
                    AND   BATCH_SEQ=<BATCH_SEQ>
modifyStorgeQty.Debug=N

//生成出库明细
insertExStorgeD.Type=TSQL
insertExStorgeD.SQL=INSERT INTO DEV_EXWAREHOUSED (EXWAREHOUSE_NO,SEQ_NO,DEV_CODE,DEVSEQ_NO,BATCH_SEQ,&
                                                  QTY,CARE_USER,USE_USER,LOC_CODE,REMARK1,REMARK2,&
                                                  OPT_USER,OPT_DATE,OPT_TERM) &
                                          VALUES (<EXWAREHOUSE_NO>,<SEQ_NO>,<DEV_CODE>,<DEVSEQ_NO>,<BATCH_SEQ>,&
                                                  <QTY>,<CARE_USER>,<USE_USER>,<LOC_CODE>,<REMARK1>,<REMARK2>,&
                                                  <OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertExStorgeD.Debug=N


//写入库存信息
insertStock.Type=TSQL
insertStock.SQL=INSERT INTO DEV_STOCKM (DEPT_CODE, DEV_CODE, BATCH_SEQ, QTY,UNIT_PRICE, &
                                        MAN_DATE,SCRAP_VALUE,GUAREP_DATE,DEP_DATE, &
                                        CARE_USER,USE_USER,LOC_CODE,INWAREHOUSE_DATE,&
                                        OPT_USER, OPT_DATE,OPT_TERM) &
                                VALUES (<INWAREHOUSE_DEPT>,<DEV_CODE>,<BATCH_SEQ>,<QTY>,<UNIT_PRICE>, &
                                        <MAN_DATE>,<SCRAP_VALUE>,<GUAREP_DATE>,<DEP_DATE>, &
                                        <CARE_USER>,<USE_USER>,<LOC_CODE>,<INWAREHOUSE_DATE>,&
                                        <OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertStock.Debug=N


//更新库存明细信息
updateStockD.Type=TSQL
updateStockD.SQL=UPDATE DEV_STOCKD SET DEPT_CODE=<INWAREHOUSE_DEPT>, & 
                                       CARE_USER=<CARE_USER>, & 
                                       USE_USER=<USE_USER>, & 
                                       LOC_CODE=<LOC_CODE>, & 
                                       OPT_USER=<OPT_USER>, & 
                                       OPT_DATE=<OPT_DATE>, & 
                                       OPT_TERM=<OPT_TERM> & 
                                 WHERE DEPT_CODE=<EXWAREHOUSE_DEPT> & 
                                 AND   DEV_CODE=<DEV_CODE> & 
                                 AND   BATCH_SEQ=<BATCH_SEQ> & 
                                 AND   DEVSEQ_NO=<DEVSEQ_NO> 
updateStockD.Debug=N



//生成库存明细
insertExStorgeM.Type=TSQL
insertExStorgeM.SQL=INSERT INTO DEV_EXWAREHOUSEM (EXWAREHOUSE_NO,EXWAREHOUSE_DATE,EXWAREHOUSE_USER,EXWAREHOUSE_DEPT,INWAREHOUSE_DEPT,&
                                                  OPT_USER,OPT_DATE,OPT_TERM) &
		                          VALUES (<EXWAREHOUSE_NO>,<EXWAREHOUSE_DATE>,<EXWAREHOUSE_USER>,<EXWAREHOUSE_DEPT>,<INWAREHOUSE_DEPT>,&
		                                  <OPT_USER>,<OPT_DATE>,<OPT_TERM>)

insertExStorgeM.Debug=N


//修改出库明细信息
updateExStorgeD.Type=TSQL
updateExStorgeD.SQL=UPDATE DEV_EXWAREHOUSED SET CARE_USER=<CARE_USER>, &
                                                USE_USER=<USE_USER>, &
                                                LOC_CODE=<LOC_CODE>, &
                                                OPT_USER=<OPT_USER>, &
                                                OPT_DATE=<OPT_DATE>,&
                                                OPT_TERM=<OPT_TERM>,&
                                                REMARK1=<REMARK1>,&
                                                REMARK2=<REMARK2> &
                                          WHERE EXWAREHOUSE_NO=<EXWAREHOUSE_NO> &
                                          AND   SEQ_NO=<SEQ_NO>
updateExStorgeD.Debug=N



//得到已经出库信息
queryExStorgeD.Type=TSQL
queryExStorgeD.SQL=SELECT 'N' DEL_FLG,C.SEQMAN_FLG,C.DEVPRO_CODE,C.DEV_CODE,B.BATCH_SEQ,&
                          CASE WHEN E.DEVSEQ_NO IS NULL THEN 0 ELSE E.DEVSEQ_NO END DEVSEQ_NO,&
                          C.DEV_CHN_DESC,C.DESCRIPTION,B.QTY,D.QTY STORGE_QTY,A.INWAREHOUSE_DEPT,&
                          B.CARE_USER,B.USE_USER,B.LOC_CODE,E.SETDEV_CODE,C.UNIT_CODE,D.UNIT_PRICE,&
                          B.QTY * C.UNIT_CODE TOT_VALUE,D.SCRAP_VALUE,D.GUAREP_DATE,D.DEP_DATE,&
                          B.REMARK1,B.REMARK2,B.SEQ_NO,D.MAN_DATE &
                     FROM DEV_EXWAREHOUSEM A,DEV_EXWAREHOUSED B,DEV_BASE C,DEV_STOCKM D,DEV_STOCKD E &
                     WHERE A.EXWAREHOUSE_NO=<EXWAREHOUSE_NO> & 
                     AND   A.EXWAREHOUSE_NO=B.EXWAREHOUSE_NO &
                     AND   B.DEV_CODE = C.DEV_CODE &
                     AND   A.INWAREHOUSE_DEPT = D.DEPT_CODE &
                     AND   B.DEV_CODE = D.DEV_CODE &
                     AND   B.BATCH_SEQ = D.BATCH_SEQ &
                     AND ((C.SEQMAN_FLG = 'Y' AND B.DEVSEQ_NO = E.DEVSEQ_NO) OR C.SEQMAN_FLG = 'N') &
                     AND   D.DEPT_CODE = E.DEPT_CODE(+) &
                     AND   D.DEV_CODE = E.DEV_CODE(+) &
                     AND   D.BATCH_SEQ = E.BATCH_SEQ(+)
queryExStorgeD.Debug=N



//出库单打印时得到已经出库信息
queryExReceiptData.Type=TSQL
queryExReceiptData.SQL=SELECT A.EXWAREHOUSE_NO,A.EXWAREHOUSE_DATE,A.EXWAREHOUSE_DEPT,A.EXWAREHOUSE_USER,A.INWAREHOUSE_DEPT,&
                              C.DEVPRO_CODE,B.BATCH_SEQ,B.SEQ_NO,C.DEV_CHN_DESC,C.SPECIFICATION,&
                              B.QTY,E.SETDEV_CODE,D.UNIT_PRICE * B.QTY TOT_VALUE,B.CARE_USER,B.USE_USER,&
                              B.LOC_CODE,B.REMARK1,B.REMARK2,D.UNIT_PRICE &
                         FROM DEV_EXWAREHOUSEM A,DEV_EXWAREHOUSED B,DEV_BASE C,DEV_STOCKM D,DEV_STOCKD E &
                         WHERE A.EXWAREHOUSE_NO = <EXWAREHOUSE_NO> &
                         AND   A.EXWAREHOUSE_NO = B.EXWAREHOUSE_NO &
                         AND   B.DEV_CODE = C.DEV_CODE &
                         AND   A.INWAREHOUSE_DEPT = D.DEPT_CODE &
                         AND   B.DEV_CODE = D.DEV_CODE &
                         AND   B.BATCH_SEQ = D.BATCH_SEQ &
                         AND ((C.SEQMAN_FLG = 'Y' AND B.DEVSEQ_NO = E.DEVSEQ_NO) OR C.SEQMAN_FLG = 'N') &
                         AND   D.DEPT_CODE = E.DEPT_CODE(+) &
                         AND   D.DEV_CODE = E.DEV_CODE(+) &
                         AND   D.BATCH_SEQ = E.BATCH_SEQ(+)
queryExReceiptData.Debug=N



//更新库存主档信息
updateStockM.Type=TSQL
updateStockM.SQL=UPDATE DEV_STOCKM SET CARE_USER=<CARE_USER>, & 
                                       USE_USER=<USE_USER>, & 
                                       LOC_CODE=<LOC_CODE>, & 
                                       OPT_USER=<OPT_USER>, & 
                                       OPT_DATE=<OPT_DATE>, & 
                                       OPT_TERM=<OPT_TERM> & 
                                 WHERE DEPT_CODE=<INWAREHOUSE_DEPT> & 
                                 AND   DEV_CODE=<DEV_CODE> & 
                                 AND   BATCH_SEQ=<BATCH_SEQ>
updateStockM.Debug=N