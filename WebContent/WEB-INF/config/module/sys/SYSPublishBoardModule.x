# 
#  Title:����������
# 
#  Description:����������
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author li.xiang790130@gmail.com 2011.04.21
#
#  version 1.0
#

// modified by wangqing 20171114 ����selectReceiveData
Module.item=queryByUserID;queryByDept;queryByRole;queryAll;inserPostRCV;inserBoard;updateBoardByMessageNo;deletePostRCV;deleteBoard;deleteRCVByUserIDMessNo;getBoardByMessageNo;updateResposeNo;updateReadFlag &
            ;selectReceiveData
            
// add by wangqing 20171114
// ��ѯĳ�˹��������Ϣ
selectReceiveData.Type=TSQL
selectReceiveData.SQL=SELECT A.MESSAGE_NO, A.POST_SUBJECT, A.POST_INFOMATION, B.USER_ID, B.READ_FLG, TO_CHAR(B.START_READ_TIME, 'yyyy/MM/dd HH24:MI:SS') AS START_READ_TIME FROM SYS_BOARD A, SYS_POSTRCV B &
                      WHERE A.MESSAGE_NO=B.MESSAGE_NO AND A.MESSAGE_NO=<MESSAGE_NO> AND B.USER_ID=<USER_ID>
selectReceiveData.Debug=Y             
            
//ͨ���û�ID,��ý����û�
queryByUserID.Type=TSQL
queryByUserID.SQL=SELECT USER_ID,ROLE_ID,E_MAIL FROM SYS_OPERATOR WHERE USER_ID=<RECIPIENTS> and REGION_CODE=<REGION> and ACTIVE_DATE<=to_date(<CURRENT_DATE>,'YYYYMMDD') and END_DATE>=to_date(<CURRENT_DATE>,'YYYYMMDD')
queryByUserID.Debug=N

//ͨ�����ţ���ý����û�
queryByDept.Type=TSQL
queryByDept.SQL=SELECT a.USER_ID,a.ROLE_ID,E_MAIL FROM SYS_OPERATOR a LEFT JOIN SYS_OPERATOR_DEPT b on a.USER_ID=b.USER_ID WHERE DEPT_CODE=<RECIPIENTS> and REGION_CODE=<REGION> and ACTIVE_DATE<=to_date(<CURRENT_DATE>,'YYYYMMDD') and END_DATE>=to_date(<CURRENT_DATE>,'YYYYMMDD')
queryByDept.Debug=N

//ͨ����ɫ����ý����û�
queryByRole.Type=TSQL
queryByRole.SQL=SELECT USER_ID,ROLE_ID,E_MAIL FROM SYS_OPERATOR WHERE ROLE_ID=<RECIPIENTS> and REGION_CODE=<REGION> and ACTIVE_DATE<=to_date(<CURRENT_DATE>,'YYYYMMDD') and END_DATE>=to_date(<CURRENT_DATE>,'YYYYMMDD')
queryByRole.Debug=N

//���������Ա
queryAll.Type=TSQL
queryAll.SQL=SELECT USER_ID,ROLE_ID,E_MAIL FROM SYS_OPERATOR WHERE REGION_CODE=<REGION> and ACTIVE_DATE<=to_date(<CURRENT_DATE>,'YYYYMMDD') and END_DATE>=to_date(<CURRENT_DATE>,'YYYYMMDD')
queryAll.Debug=N

//������յ�
inserPostRCV.Type=TSQL
inserPostRCV.SQL=INSERT INTO SYS_POSTRCV(MESSAGE_NO,POST_TYPE,POST_GROUP,USER_ID,READ_FLG,OPT_USER,OPT_DATE,OPT_TERM) VALUES(<MESSAGE_NO>,<POST_TYPE>,<POST_GROUP>,<USER_ID>,<READ_FLG>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
inserPostRCV.Debug=N

//���빫����
inserBoard.Type=TSQL
inserBoard.SQL=INSERT INTO SYS_BOARD(MESSAGE_NO,POST_SUBJECT,URG_FLG,POST_INFOMATION,RESPONSE_NO,POST_ID,POST_TIME,OPT_USER,OPT_DATE,OPT_TERM) VALUES(<MESSAGE_NO>,<POST_SUBJECT>,<URG_FLG>,<POST_INFOMATION>,<RESPONSE_NO>,<POST_ID>,<OPT_DATE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
inserBoard.Debug=N

//���¹���
updateBoardByMessageNo.Type=TSQL
updateBoardByMessageNo.SQL=UPDATE SYS_BOARD SET POST_SUBJECT=<POST_SUBJECT>,URG_FLG=<URG_FLG>,POST_INFOMATION=<POST_INFOMATION> WHERE MESSAGE_NO=<MESSAGE_NO>
updateBoardByMessageNo.Debug=N


//ɾ�����յ�
deletePostRCV.Type=TSQL
deletePostRCV.SQL=DELETE FROM SYS_POSTRCV WHERE MESSAGE_NO=<MESSAGE_NO>
deletePostRCV.Debug=N

//ɾ��������
deleteBoard.Type=TSQL
deleteBoard.SQL=DELETE FROM SYS_BOARD WHERE MESSAGE_NO=<MESSAGE_NO>
deleteBoard.Debug=N

//ͨ���û�ID����Ϣ��ɾ�����յ���¼
deleteRCVByUserIDMessNo.Type=TSQL
deleteRCVByUserIDMessNo.SQL=DELETE FROM SYS_POSTRCV WHERE MESSAGE_NO=<MESSAGE_NO> and USER_ID=<USER_ID>
deleteRCVByUserIDMessNo.Debug=N

//ȡ��ϸ���棻
getBoardByMessageNo.Type=TSQL
getBoardByMessageNo.SQL=SELECT POST_SUBJECT,POST_INFOMATION,RESPONSE_NO FROM SYS_BOARD WHERE MESSAGE_NO=<MESSAGE_NO>
getBoardByMessageNo.Debug=N

//������Ӧ��
updateResposeNo.Type=TSQL
updateResposeNo.SQL=UPDATE SYS_BOARD SET RESPONSE_NO=<RESPONSE_NO> WHERE MESSAGE_NO=<MESSAGE_NO>
updateResposeNo.Debug=N

// modified by wangqing 20171110 ͬʱ�����״��Ķ�ʱ��
//�����Ѷ����
updateReadFlag.Type=TSQL
updateReadFlag.SQL=UPDATE SYS_POSTRCV SET READ_FLG='Y', START_READ_TIME=SYSDATE WHERE MESSAGE_NO=<MESSAGE_NO> AND USER_ID=<USER_ID> &
                   AND NOT(READ_FLG IS NOT NULL AND READ_FLG='Y')
updateReadFlag.Debug=Y