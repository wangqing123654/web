# 
#  Title:�������module
# 
#  Description:�������module
# 
#  Copyright: Copyright (c) bluecore 2012
# 
#  author pangb 2013.03.10
#  version 4.0
#
Module.item=deleteHrmOrder;selectMedApplyOrder

//ɾ����¼
deleteHrmOrder.Type=TSQL
deleteHrmOrder.SQL=DELETE FROM HRM_ORDER WHERE  CONTRACT_CODE=<CONTRACT_CODE> AND MR_NO=<MR_NO> AND EXEC_DR_CODE IS NULL AND BILL_FLG='N'     
deleteHrmOrder.Debug=N

//��ѯ�����Ŀ
selectMedApplyOrder.Type=TSQL
selectMedApplyOrder.SQL=SELECT * FROM MED_APPLY A, HRM_PATADM B WHERE B.CONTRACT_CODE = <CONTRACT_CODE> AND B.MR_NO = <MR_NO> AND B.CASE_NO = A.CASE_NO
selectMedApplyOrder.Debug=N
