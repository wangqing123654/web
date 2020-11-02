##############################################
# <p>Title:Ժ�ڼ��￹��ҩ����ϸͳ�� </p>
#
# <p>Description:Ժ�ڼ��￹��ҩ����ϸͳ�� </p>
#
# <p>Copyright: Copyright (c) 2013</p>
#
# <p>Company:Javahis </p>
#
# @author wangm  2013-3-18
# @version 1.0
##############################################
Module.item=selectEAnti


//Ժ�ڼ��￹��ҩ����ϸͳ��
selectEAnti.Type=TSQL
selectEAnti.SQL=SELECT a.bill_date AS BILL_DATE, a.mr_no AS MR_NO, b.pat_name AS PAT_NAME, a.order_code AS ORDER_CODE, &
			a.order_desc AS ORDER_DESC, a.SPECIFICATION AS SPECIFICATION, c.unit_chn_desc AS UNIT_CHN_DESC, &
			a.own_price AS OWN_PRICE, a.dosage_qty AS DOSAGE_QTY, a.own_amt AS OWN_AMT, d.user_name AS USER_NAME  &
		FROM opd_order a, sys_patinfo b, sys_unit c, sys_operator d, pha_base e &
		WHERE a.mr_no = b.mr_no AND a.medi_unit = c.unit_code AND a.dr_code = d.user_id AND a.order_code = e.order_code &
			AND e.ANTIBIOTIC_CODE is not null AND a.adm_type = 'E' &
			AND a.bill_date >= TO_DATE (<DATE_START>, 'YYYY/MM/DD HH24:MI:SS') AND a.bill_date <= TO_DATE (<DATE_END>, 'YYYY/MM/DD HH24:MI:SS') &
			AND a.cat1_type = 'PHA'
		order by a.mr_no
selectEAnti.Debug=N


































