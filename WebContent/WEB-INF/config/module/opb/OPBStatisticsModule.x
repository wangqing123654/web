##############################################
# <p>Title:�ţ���������ϸ�����ܱ��� </p>
#
# <p>Description:�ţ���������ϸ�����ܱ��� </p>
#
# <p>Copyright: Copyright (c) 2013</p>
#
# <p>Company:Javahis </p>
#
# @author wangm  2013-3-14
# @version 1.0
##############################################
Module.item=selectOSUM;selectODET;selectOTJSUM;selectOTJDET;selectOTGSUM;selectOTGDET;selectETGSUM


//OSUM	�������_(��������)
selectOSUM.Type=TSQL
selectOSUM.SQL=SELECT a.order_code AS ORDER_CODE, a.order_desc AS ORDER_DESC, a.SPECIFICATION AS SPECIFICATION, &
       			c.unit_chn_desc AS UNIT_CHN_DESC, a.own_price AS OWN_PRICE, sum(a.dosage_qty) AS DOSAGE_QTY_SUM, sum(a.own_amt) AS OWN_AMT_SUM  &
  		FROM opd_order a, sys_unit c &
 		WHERE a.medi_unit = c.unit_code	AND a.adm_type = 'O' &
   			AND a.bill_date >= TO_DATE (<DATE_START>, 'YYYY-MM-DD HH24:MI:SS') &
   			AND a.bill_date <= TO_DATE (<DATE_END>, 'YYYY-MM-DD HH24:MI:SS') &
   			AND a.cat1_type = 'PHA' &
   		group by a.order_code, a.order_desc, a.SPECIFICATION, c.unit_chn_desc, a.own_price &
   		order by a.order_code
selectOSUM.Debug=N



//ODET	������ϸ_(��������)
selectODET.Type=TSQL
selectODET.SQL=SELECT a.bill_date AS BILL_DATE,  a.mr_no AS MR_NO,  b.pat_name AS PAT_NAME, a.order_code AS ORDER_CODE, &
       			a.order_desc AS ORDER_DESC, a.SPECIFICATION AS SPECIFICATION, c.unit_chn_desc AS UNIT_CHN_DESC, a.own_price AS OWN_PRICE, &
       			a.dosage_qty AS DOSAGE_QTY, a.own_amt AS OWN_AMT, d.user_name AS USER_NAME &
  	       FROM opd_order a, sys_patinfo b, sys_unit c, sys_operator d &
               WHERE a.mr_no = b.mr_no AND a.medi_unit = c.unit_code AND a.dr_code = d.user_id AND a.adm_type = 'O'  &
   			AND a.bill_date >= TO_DATE (<DATE_START>, 'YYYY-MM-DD HH24:MI:SS') &
   			AND a.bill_date <= TO_DATE (<DATE_END>, 'YYYY-MM-DD HH24:MI:SS') &
   			AND a.cat1_type = 'PHA' &
	       order by a.mr_no
selectODET.Debug=N



//OTJSUM    �����������
selectOTJSUM.Type=TSQL
selectOTJSUM.SQL=SELECT  a.order_code AS ORDER_CODE, a.order_desc AS ORDER_DESC, a.SPECIFICATION AS SPECIFICATION, &
       			c.unit_chn_desc AS UNIT_CHN_DESC, a.own_price AS OWN_PRICE, sum(a.dosage_qty) AS DOSAGE_QTY_SUM, sum(a.own_amt) AS OWN_AMT_SUM  &
                 FROM opd_order a, sys_unit c, reg_patadm e &
                 WHERE a.medi_unit = c.unit_code AND a.case_no = e.case_no AND e.realdept_code = '020103' &
   			AND a.bill_date >= TO_DATE (<DATE_START>, 'YYYY-MM-DD HH24:MI:SS') &
   			AND a.bill_date <= TO_DATE (<DATE_END>, 'YYYY-MM-DD HH24:MI:SS') &
   			AND a.cat1_type = 'PHA' &
                 group by a.order_code, a.order_desc, a.SPECIFICATION, c.unit_chn_desc, a.own_price &
                 order by a.order_code
selectOTJSUM.Debug=N


//OTJDET    ����������ϸ
selectOTJDET.Type=TSQL
selectOTJDET.SQL=SELECT a.case_no AS CASE_NO, a.bill_date AS BILL_DATE, a.mr_no AS MR_NO, b.pat_name AS PAT_NAME, &
			a.order_code AS ORDER_CODE, a.order_desc AS ORDER_DESC, a.SPECIFICATION AS SPECIFICATION, &
                        c.unit_chn_desc AS UNIT_CHN_DESC, a.own_price AS OWN_PRICE, a.dosage_qty AS DOSAGE_QTY, &
                        a.own_amt AS OWN_AMT, d.user_name AS USER_NAME &
		 FROM opd_order a, sys_patinfo b, sys_unit c, sys_operator d, reg_patadm e &
                 WHERE a.mr_no = b.mr_no AND a.medi_unit = c.unit_code AND a.dr_code = d.user_id AND a.case_no = e.case_no &
			AND a.bill_date >= TO_DATE (<DATE_START>, 'YYYY-MM-DD HH24:MI:SS') &
			AND a.bill_date <= TO_DATE (<DATE_END>, 'YYYY-MM-DD HH24:MI:SS') &
			AND a.cat1_type = 'PHA' &
			AND e.realdept_code = '020103' &
                 order by a.mr_no 
selectOTJDET.Debug=N


//OTGSUM    Ժ���������
selectOTGSUM.Type=TSQL
selectOTGSUM.SQL=SELECT a.order_code AS ORDER_CODE, a.order_desc AS ORDER_DESC, a.SPECIFICATION AS SPECIFICATION, c.unit_chn_desc AS UNIT_CHN_DESC, &
			 a.own_price AS OWN_PRICE, sum(a.dosage_qty) AS DOSAGE_QTY_SUM, sum(a.own_amt) AS OWN_AMT_SUM &
		 FROM opd_order a, sys_unit c, reg_patadm e &
		 WHERE a.medi_unit = c.unit_code AND a.case_no = e.case_no AND a.adm_type = 'O' AND e.realdept_code != '020103' &
			AND a.bill_date >= TO_DATE (<DATE_START>, 'YYYY-MM-DD HH24:MI:SS') &
			AND a.bill_date <= TO_DATE (<DATE_END>, 'YYYY-MM-DD HH24:MI:SS') &
			AND a.cat1_type = 'PHA' &
		 group by a.order_code, a.order_desc, a.SPECIFICATION, c.unit_chn_desc, a.own_price &
		 order by a.order_code
selectOTGSUM.Debug=N



//OTGDET    Ժ��������ϸ
selectOTGDET.Type=TSQL
selectOTGDET.SQL=SELECT a.bill_date AS BILL_DATE, a.mr_no AS MR_NO, b.pat_name AS PAT_NAME, a.order_code AS ORDER_CODE, &
			a.order_desc AS ORDER_DESC, a.SPECIFICATION AS SPECIFICATION, c.unit_chn_desc AS UNIT_CHN_DESC, &
			a.own_price AS OWN_PRICE, a.dosage_qty AS DOSAGE_QTY, a.own_amt AS OWN_AMT, d.user_name AS USER_NAME &
		 FROM opd_order a, sys_patinfo b, sys_unit c, sys_operator d, reg_patadm e &
		 WHERE a.mr_no = b.mr_no AND a.medi_unit = c.unit_code AND a.dr_code = d.user_id AND a.case_no = e.case_no AND a.adm_type = 'O' &
			AND a.bill_date >= TO_DATE (<DATE_START>, 'YYYY-MM-DD HH24:MI:SS') &
			AND a.bill_date <= TO_DATE (<DATE_END>, 'YYYY-MM-DD HH24:MI:SS') &
			AND a.cat1_type = 'PHA' &
			AND e.realdept_code != '020103' &
		 order by a.mr_no
selectOTGDET.Debug=N




//ETGSUM    Ժ�ڼ������
selectETGSUM.Type=TSQL
selectETGSUM.SQL=SELECT a.order_code AS ORDER_CODE, a.order_desc AS ORDER_DESC, a.SPECIFICATION AS SPECIFICATION, c.unit_chn_desc AS UNIT_CHN_DESC, &
			a.own_price AS OWN_PRICE, sum(a.dosage_qty) AS DOSAGE_QTY_SUM, sum(a.own_amt) AS OWN_AMT_SUM &
		 FROM opd_order a, sys_unit c  &
		 WHERE a.medi_unit = c.unit_code AND a.adm_type = 'E' &
			AND a.bill_date >= TO_DATE (<DATE_START>, 'YYYY-MM-DD HH24:MI:SS') &
			AND a.bill_date <= TO_DATE (<DATE_END>, 'YYYY-MM-DD HH24:MI:SS') &
			AND a.cat1_type = 'PHA' &
		 group by a.order_code, a.order_desc, a.SPECIFICATION, c.unit_chn_desc, a.own_price &
		 order by a.order_code
selectETGSUM.Debug=N










































