# 
#  Title:�ű����module
# 
#  Description:�ű����module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsClinictypefee;getOrderCode;getOrderCodeDetial

//��ѯ
queryTree.Type=TSQL
queryTree.SQL=SELECT ADM_TYPE,CLINICTYPE_CODE,ORDER_CODE,RECEIPT_TYPE,OPT_USER,&
		     OPT_DATE,OPT_TERM &
		FROM REG_CLINICTYPE_FEE &
	       WHERE CLINICTYPE_CODE=<CLINICTYPE_CODE> &
	    ORDER BY CLINICTYPE_CODE
queryTree.Debug=N

//��ѯ�ż���,�ű�,���ô���,�շ����,������Ա,��������,�����ն�
selectdata.Type=TSQL
selectdata.SQL=SELECT ADM_TYPE,CLINICTYPE_CODE,ORDER_CODE,RECEIPT_TYPE,OPT_USER,&
		      OPT_DATE,OPT_TERM &
		 FROM REG_CLINICTYPE_FEE &
	     ORDER BY CLINICTYPE_CODE
selectdata.item=ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE
selectdata.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectdata.CLINICTYPE_CODE=CLINICTYPE_CODE=<CLINICTYPE_CODE>
selectdata.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
selectdata.Debug=N

//ɾ���ż���,�ű�,���ô���,�շ����,������Ա,��������,�����ն�
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_CLINICTYPE_FEE &
		     WHERE ADM_TYPE=<ADM_TYPE> &
		       AND CLINICTYPE_CODE=<CLINICTYPE_CODE> &
		       AND ORDER_CODE = <ORDER_CODE>
deletedata.Debug=N

//�����ż���,�ű�,���ô���,�շ����,������Ա,��������,�����ն�
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_CLINICTYPE_FEE &
			   (ADM_TYPE,CLINICTYPE_CODE,ORDER_CODE,RECEIPT_TYPE,OPT_USER,&
			   OPT_DATE,OPT_TERM) &
		    VALUES (<ADM_TYPE>,<CLINICTYPE_CODE>,<ORDER_CODE>,<RECEIPT_TYPE>,<OPT_USER>,&
		           SYSDATE,<OPT_TERM>)
insertdata.Debug=N              

//�����ż���,�ű�,���ô���,�շ����,������Ա,��������,�����ն�
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_CLINICTYPE_FEE &
		  SET ADM_TYPE=<ADM_TYPE>,CLINICTYPE_CODE=<CLINICTYPE_CODE>,ORDER_CODE=<ORDER_CODE>,&
		      RECEIPT_TYPE=<RECEIPT_TYPE>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,&
		      OPT_TERM=<OPT_TERM> &
		WHERE ADM_TYPE=<ADM_TYPE> &
		  AND CLINICTYPE_CODE=<CLINICTYPE_CODE> &
		  AND ORDER_CODE = <ORDER_CODE>								
updatedata.Debug=N

//�Ƿ���ںű����
existsClinictypefee.type=TSQL
existsClinictypefee.SQL=SELECT COUNT(*) AS COUNT &
			  FROM REG_CLINICTYPE_FEE &
                         WHERE ADM_TYPE=<ADM_TYPE> &
                           AND CLINICTYPE_CODE=<CLINICTYPE_CODE> &
                           AND ORDER_CODE = <ORDER_CODE>

//����ADM_TYPE,CLINICTYPE_CODE����ORDER_CODE
getOrderCode.Type=TSQL
getOrderCode.SQL=SELECT ORDER_CODE &
		   FROM REG_CLINICTYPE_FEE &
		  WHERE ADM_TYPE=<ADM_TYPE> &
		    AND CLINICTYPE_CODE=<CLINICTYPE_CODE>
getOrderCode.Debug=N

//����ADM_TYPE,CLINICTYPE_CODE,RECEIPT_TYPE����ORDER_CODE
getOrderCodeDetial.Type=TSQL
getOrderCodeDetial.SQL=SELECT ORDER_CODE &
			 FROM REG_CLINICTYPE_FEE &
			WHERE ADM_TYPE=<ADM_TYPE> &
			  AND CLINICTYPE_CODE=<CLINICTYPE_CODE> &
			  AND RECEIPT_TYPE=<RECEIPT_TYPE>
getOrderCodeDetial.Debug=N


