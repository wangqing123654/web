Module.item=getDate;getUpDateNewLabStust;upDateLisDJ;upDateLisQXDJ;upDateLisJSDJ;selectNBWStat;upDateSHWC;delLAB_GENRPTDTLData;delLAB_ANTISENSTESTData;delLAB_CULRPTDTLData;insertLAB_GENRPTDTLData;insertLAB_ANTISENSTESTData;insertLAB_CULRPTDTLData;upDateBGEND
//得到系统时间
getDate.Type=TSQL
getDate.SQL=SELECT SYSDATE FROM DUAL
getDate.Debug=N
//更新LIS的新医嘱发送状态
getUpDateNewLabStust.Type=TSQL
getUpDateNewLabStust.SQL=UPDATE LAB_TSTRPTMAS SET SEND_FLG = 2 WHERE LAB_NUMBER = <LAB_NUMBER>
getUpDateNewLabStust.Debug=Y
//LIS检体核收
upDateLisDJ.Type=TSQL
upDateLisDJ.SQL=UPDATE LAB_TSTRPTMAS SET RPT_STTS = 4 WHERE LAB_NUMBER = <LAB_NUMBER>
upDateLisDJ.Debug=Y
//LIS取消检体核收
upDateLisQXDJ.Type=TSQL
upDateLisQXDJ.SQL=UPDATE LAB_TSTRPTMAS SET RPT_STTS = 5 WHERE LAB_NUMBER = <LAB_NUMBER>
upDateLisQXDJ.Debug=Y
//LIS检体拒收
upDateLisJSDJ.Type=TSQL
upDateLisJSDJ.SQL=UPDATE LAB_TSTRPTMAS SET RPT_STTS = 3 WHERE LAB_NUMBER = <LAB_NUMBER>
upDateLisJSDJ.Debug=Y
//更新护士工作站状态
selectNBWStat.Type=TSQL
selectNBWStat.SQL=SELECT HOSP_AREA, LAB_NUMBER, TESTSET_SEQ, CASE_NO, ORDER_NO, SEQ_NO, REGION_CODE FROM LAB_TSTRPTMAS WHERE LAB_NUMBER = <LAB_NUMBER>
selectNBWStat.Debug=Y
//审核完成
upDateSHWC.Type=TSQL
upDateSHWC.SQL=UPDATE LAB_TSTRPTMAS SET RPT_STTS = 6,TEST_DATE = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') WHERE LAB_NUMBER = <LAB_NUMBER>
upDateSHWC.Debug=Y
//删除临检表的LIS报告数据
delLAB_GENRPTDTLData.Type=TSQL
delLAB_GENRPTDTLData.SQL=DELETE LAB_GENRPTDTL WHERE LAB_NUMBER = <LAB_NUMBER>
delLAB_GENRPTDTLData.Debug=Y
//删除微免表的LIS报告数据
delLAB_ANTISENSTESTData.Type=TSQL
delLAB_ANTISENSTESTData.SQL=DELETE LAB_ANTISENSTEST WHERE LAB_NUMBER = <LAB_NUMBER>
delLAB_ANTISENSTESTData.Debug=Y
//删除细菌表的LIS报告数据
delLAB_CULRPTDTLData.Type=TSQL
delLAB_CULRPTDTLData.SQL=DELETE LAB_CULRPTDTL WHERE LAB_NUMBER = <LAB_NUMBER>
delLAB_CULRPTDTLData.Debug=Y
//插入临检表数据
insertLAB_GENRPTDTLData.Type=TSQL
insertLAB_GENRPTDTLData.SQL=INSERT INTO LAB_GENRPTDTL(HOSP_AREA,LAB_NUMBER,TESTSET_SEQ,RPT_STTS,TESTITEM_CODE,TEST_VALUE,TEST_UNIT,REMARK,UPPE_LIMIT,LOWER_LIMIT,CRTCLUPLMT,CRTCLLWLMT,SEND_DTTM)VALUES(<HOSP_AREA>,<LAB_NUMBER>,<TESTSET_SEQ>,<RPT_STTS>,<TESTITEM_CODE>,<TEST_VALUE>,<TEST_UNIT>,<REMARK>,<UPPE_LIMIT>,<LOWER_LIMIT>,<CRTCLUPLMT>,<CRTCLLWLMT>,<SEND_DTTM>)
insertLAB_GENRPTDTLData.Debug=Y
//插入微免表数据
insertLAB_ANTISENSTESTData.Type=TSQL
insertLAB_ANTISENSTESTData.SQL=INSERT INTO LAB_ANTISENSTEST(HOSP_AREA,LAB_NUMBER,TESTSET_SEQ,RPT_STTS,CULRPT_SEQ,ANTI_CODE,SENS_LEVEL,SEND_DTTM)VALUES(<HOSP_AREA>,<LAB_NUMBER>,<TESTSET_SEQ>,<RPT_STTS>,<CULRPT_SEQ>,<ANTI_CODE>,<SENS_LEVEL>,<SEND_DTTM>)
insertLAB_ANTISENSTESTData.Debug=Y
//插入细菌表数据
insertLAB_CULRPTDTLData.Type=TSQL
insertLAB_CULRPTDTLData.SQL=INSERT INTO LAB_CULRPTDTL(HOSP_AREA,LAB_NUMBER,TESTSET_SEQ,RPT_STTS,CULRPT_SEQ,CULTURE_RESULT,INFECT_LEVEL,GRAM_STAIN,COLONY_COUNT,SEND_DTTM)VALUES(<HOSP_AREA>,<LAB_NUMBER>,<TESTSET_SEQ>,<RPT_STTS>,<CULRPT_SEQ>,<CULTURE_RESULT>,<INFECT_LEVEL>,<GRAM_STAIN>,<COLONY_COUNT>,<SEND_DTTM>)
insertLAB_CULRPTDTLData.Debug=Y
//报告完成
upDateBGEND.Type=TSQL
upDateBGEND.SQL=UPDATE LAB_TSTRPTMAS SET RPT_STTS = 7,RPT_DTTM = <RPT_DTTM>,DLVRYRPT_USER = <DLVRYRPT_USER> WHERE LAB_NUMBER = <LAB_NUMBER> AND TESTSET_SEQ = <TESTSET_SEQ>
upDateBGEND.Debug=Y