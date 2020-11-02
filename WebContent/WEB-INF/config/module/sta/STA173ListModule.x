# 
#  Title:�м䵵173���ֵ���module
# 
#  Description:�м䵵173���ֵ���module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.02
#  version 1.0
#
Module.item=selectData;insertData;updateData;deleteData;selectDataList

//��ѯ����
selectData.Type=TSQL
selectData.SQL=SELECT &
		   SEQ, ICD_DESC, CONDITION, &
		   OPT_USER, OPT_DATE, OPT_TERM &
		FROM STA_173_LIST &
		ORDER BY TO_NUMBER(SEQ)
selectData.item=SEQ;ICD_DESC
selectData.SEQ=SEQ=<SEQ>
selectData.ICD_DESC=ICD_DESC LIKE <ICD_DESC>
selectData.Debug=N

//��������
insertData.Type=TSQL
insertData.SQL=INSERT INTO STA_173_LIST( &
		    SEQ, ICD_DESC, CONDITION, &
		    OPT_USER, OPT_DATE, OPT_TERM &
		    ) &
	    VALUES ( &
		    <SEQ>, <ICD_DESC>, <CONDITION>, &
		    <OPT_USER>, SYSDATE, <OPT_TERM> &
	     )
insertData.Debug=N

//�޸�����
updateData.Type=TSQL
updateData.SQL=UPDATE STA_173_LIST SET &
			 ICD_DESC=<ICD_DESC>, &
			 CONDITION=<CONDITION>, &
			 OPT_USER=<OPT_USER>, &
			 OPT_DATE=SYSDATE, &
			 OPT_TERM=<OPT_TERM> &
			 WHERE  SEQ=<SEQ>  
updateData.Debug=N

deleteData.Type=TSQL
deleteData.SQL=DELETE FROM STA_173_LIST WHERE SEQ=<SEQ>  
deleteData.Debug=N

//��ѯ���в�����Ϣ
selectDataList.Type=TSQL
selectDataList.SQL=SELECT SEQ, ICD_DESC, CONDITION FROM STA_173_LIST ORDER BY TO_NUMBER(SEQ)
selectDataList.Debug=N