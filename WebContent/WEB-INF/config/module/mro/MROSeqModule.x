########################################
#  Title:��������ά��module
# 
#  Description:��������ά��module
# 
#  Copyright: Copyright (c) Javahis 2012
# 
#  author liuzhen 2012.8.2
#  version 4.0
########################################
Module.item=selectdata;insertdata;updatedata;deletedata

//��ѯ����
selectdata.Type=TSQL
selectdata.SQL=SELECT SEQ AS TEMP_SEQ,TO_CHAR(SEQ) AS SEQ,FILE_TYPE,OPT_USER,OPT_DATE,OPT_TERM  &
				FROM EMR_PDFLISTORDER ORDER BY TEMP_SEQ
selectdata.Debug=N

//��������
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO EMR_PDFLISTORDER (SEQ,FILE_TYPE,OPT_USER,OPT_DATE,OPT_TERM  &
                     ) &
              VALUES (<SEQ>, <FILE_TYPE>, <OPT_USER>, &
                      SYSDATE, <OPT_TERM> &
                     )
insertdata.Debug=N

//��������
updatedata.Type=TSQL
updatedata.SQL=UPDATE EMR_PDFLISTORDER SET SEQ=<SEQ>,&
				   FILE_TYPE=<FILE_TYPE>,&				   
				   OPT_USER=<OPT_USER>,&
				   OPT_DATE=SYSDATE,&
				   OPT_TERM=<OPT_TERM> &
			   WHERE   SEQ = <SEQ>
updatedata.Debug=N

//ɾ������
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM EMR_PDFLISTORDER WHERE SEQ = <SEQ>
deletedata.Debug=N