# 
#  Title:������ά������module
# 
#  Description:������ά������module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.11.06
#  version 1.0
#
Module.item=selectData;insertData;updateData;deleteData;selectDataList

//��ѯ����
selectData.Type=TSQL
selectData.SQL=SELECT &
		   SEQ, SD_DESC,PY, CONDITION, &
		   OPT_USER, OPT_DATE, OPT_TERM &
		FROM STA_SD_LIST 
selectData.item=SEQ;SD_DESC
selectData.SEQ=SEQ=<SEQ>
selectData.SD_DESC=SD_DESC LIKE <SD_DESC>
selectData.Debug=N

//��������
insertData.Type=TSQL
insertData.SQL=INSERT INTO STA_SD_LIST( &
		    SEQ, SD_DESC,PY, CONDITION, &
		    OPT_USER, OPT_DATE, OPT_TERM &
		    ) &
	    VALUES ( &
		    <SEQ>, <SD_DESC>,<PY>, <CONDITION>, &
		    <OPT_USER>, SYSDATE, <OPT_TERM> &
	     )
insertData.Debug=N

//�޸�����
updateData.Type=TSQL
updateData.SQL=UPDATE STA_SD_LIST SET &
			 SD_DESC=<SD_DESC>, &
			 PY=<PY>, &
			 CONDITION=<CONDITION>, &
			 OPT_USER=<OPT_USER>, &
			 OPT_DATE=SYSDATE, &
			 OPT_TERM=<OPT_TERM> &
			 WHERE  SEQ=<SEQ>  
updateData.Debug=N

deleteData.Type=TSQL
deleteData.SQL=DELETE FROM STA_SD_LIST WHERE SEQ=<SEQ>  
deleteData.Debug=N

//��ѯ���в�����Ϣ
selectDataList.Type=TSQL
selectDataList.SQL=SELECT SEQ, SD_DESC,PY, CONDITION FROM STA_SD_LIST ORDER BY TO_NUMBER(SEQ)
selectDataList.Debug=N