# 
#  Title:�ٴ�·��������ԭ��module
# 
#  Description:�ٴ�
# 
#  Copyright: Copyright (c) bluecore 2015
# 
#  author pangben 2015-8-10
#  version 1.0
#
//�ٴ�·�����
Module.item=insertClpCauseHistory;queryCauseHistory;queryMaxSeqNo

//�����ٴ�·��������ԭ��
insertClpCauseHistory.Type=TSQL
insertClpCauseHistory.SQL=INSERT INTO CLP_CAUSE_HISTORY( &
			CASE_NO,SEQ_NO,MR_NO,CAUSE_CODE,CAUSE_DATE,CAUSE_USER,OPT_USER,OPT_DATE,OPT_TERM) &
		VALUES( <CASE_NO>,<SEQ_NO>,<MR_NO>,<CAUSE_CODE>,SYSDATE,<CAUSE_USER>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertClpCauseHistory.Debug=N

//��ѯ��������
queryCauseHistory.Type=TSQL
queryCauseHistory.SQL=SELECT CASE_NO,SEQ_NO,MR_NO,CAUSE_CODE,CAUSE_DATE,CAUSE_USER,OPT_USER,OPT_DATE,OPT_TERM FROM CLP_CAUSE_HISTORY
queryCauseHistory.item=CASE_NO;MR_NO
queryCauseHistory.CASE_NO=CASE_NO=<CASE_NO>
queryCauseHistory.MR_NO=MR_NO=<MR_NO>
queryCauseHistory.Debug=N

//��ѯ�������
queryMaxSeqNo.Type=TSQL
queryMaxSeqNo.SQL=SELECT MAX(SEQ_NO) SEQ_NO FROM CLP_CAUSE_HISTORY
queryMaxSeqNo.item=CASE_NO;MR_NO
queryMaxSeqNo.CASE_NO=CASE_NO=<CASE_NO>
queryMaxSeqNo.MR_NO=MR_NO=<MR_NO>
queryMaxSeqNo.Debug=N