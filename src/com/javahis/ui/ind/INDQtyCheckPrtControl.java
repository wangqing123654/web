package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import jdo.ind.IndQtyCheckTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.jdo.TJDODBTool;
import jdo.ind.INDSQL;
import jdo.sys.Operator;
import jdo.util.Manager;
import com.dongyang.util.StringTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.sql.Timestamp;
import java.text.DecimalFormat;

/**
 * <p>
 * Title: �̵㱨��
 * </p>
 *
 * <p>
 * Description: �̵㱨��
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.10
 * @version 1.0
 */
public class INDQtyCheckPrtControl
    extends TControl {

    private TParm parm;

    private String org_code;

    public INDQtyCheckPrtControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        // ȡ�ô������
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            org_code = parm.getValue("ORG_CODE");
        }
        //org_code = "30101";
        // ��ʼ��������
        initPage();
    }

    /**
     * ��շ���
     */
    public void onClear() {
        getComboBox("FROZEN_DATE").setSelectedIndex(0);
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        if (this.getRadioButton("RadioButton1").isSelected()) {
            onPrintQtyCheckM();
        }
        else if (this.getRadioButton("RadioButton2").isSelected()) {
            onPrintQtyCheckD();
        }
        else {
            onPrintProfitLoss();
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm = IndQtyCheckTool.getInstance().onQueryFrozenDate(parm);
        String frozen_date = "";
        for (int i = 0; i < parm.getCount("FROZEN_DATE"); i++) {
            frozen_date = parm.getValue("FROZEN_DATE", i);
            frozen_date = frozen_date.substring(0, 4) + "/"
                + frozen_date.substring(4, 6) + "/"
                + frozen_date.substring(6, 8) + " "
                + frozen_date.substring(8, 10) + ":"
                + frozen_date.substring(10, 12) + ":"
                + frozen_date.substring(12, 14);
            parm.setData("F_DATE", i, frozen_date);
        }
        getComboBox("FROZEN_DATE").setParmValue(parm);
    }

    /**
     * �̵��
     */
    private void onPrintQtyCheckM() {
        String drozen_date = getComboBox("FROZEN_DATE").getSelectedID();
        if (getComboBox("FROZEN_DATE").getSelectedIndex() == 0) {
            this.messageBox("��ѡ�񶳽�ʱ��");
            return;
        }
        //String sql = INDSQL.getIndQtyCheckM(org_code, drozen_date);
        String sql = "SELECT X.DEPT_CHN_DESC,X.ORDER_DESC,X.SPECIFICATION,X.PHA_TYPE, " +
        		//" X.STOCK_QTY ||'  ' ||Y.BASE_QTY AS STOCK_QTY," +
                  " X.STOCK_QTY ," +
                  " Y.BASE_QTY ," +  
        		" X.ACTUAL_CHECK_QTY,X.MATERIAL_CHN_DESC,X.VALID_DATE,X.BATCH_NO,X.ORDER_CODE FROM" +
        		" (SELECT C.DEPT_CHN_DESC, CASE WHEN (D.GOODS_DESC IS NULL) "
				+ "THEN D.ORDER_DESC ELSE D.ORDER_DESC || ' ('|| D.GOODS_DESC||')'"
				+ "END AS ORDER_DESC, D.SPECIFICATION, J.PHA_TYPE, "
				+ "FLOOR (B.STOCK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
				+ "|| MOD (B.STOCK_QTY, E.DOSAGE_QTY) "  
				+ "|| G.UNIT_CHN_DESC AS STOCK_QTY, "    
				+ "FLOOR (A.ACTUAL_CHECK_QTY/E.DOSAGE_QTY)||F.UNIT_CHN_DESC "  
				+ "|| MOD (A.ACTUAL_CHECK_QTY, E.DOSAGE_QTY) "
				+ "|| G.UNIT_CHN_DESC AS ACTUAL_CHECK_QTY, H.MATERIAL_CHN_DESC, "
				+ " A.VALID_DATE , A.BATCH_NO,A.ORDER_CODE  "
				+ "FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D, "  
				+ "PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G, PHA_BASE J, "
				+ "IND_MATERIALLOC H WHERE A.ORG_CODE = B.ORG_CODE "
				+ "AND A.ORDER_CODE = B.ORDER_CODE AND A.BATCH_SEQ = B.BATCH_SEQ "
				+ "AND A.ORG_CODE = C.DEPT_CODE AND A.ORDER_CODE = D.ORDER_CODE "
				+ "AND A.ORDER_CODE = E.ORDER_CODE AND A.STOCK_UNIT = F.UNIT_CODE "
				+ "AND A.DOSAGE_UNIT =G.UNIT_CODE AND A.ORDER_CODE = J.ORDER_CODE "
				+ "AND B.MATERIAL_LOC_CODE = H.MATERIAL_LOC_CODE(+) "
				+ "AND B.STOCK_QTY > 0 AND A.ACTUAL_CHECK_QTY > 0 "
				+ "AND A.ORG_CODE = '" + org_code + "' AND A.FROZEN_DATE = '"
				+ drozen_date + "' ORDER BY A.ORDER_CODE)X," 
				+ "(SELECT D.ORDER_CODE, SUM(D.QTY)|| K.UNIT_CHN_DESC AS BASE_QTY  FROM IND_BASEMANAGEM M,IND_BASEMANAGED D ,SYS_UNIT K "
				+ " WHERE M.BASEMANAGE_NO = D.BASEMANAGE_NO " +
				  " AND D.UNIT_CODE = K.UNIT_CODE" +
				  " AND M.TO_ORG_CODE = "
				+ org_code  
				+ " GROUP BY  D.ORDER_CODE,K.UNIT_CHN_DESC )Y "
				+ " WHERE X.ORDER_CODE = Y.ORDER_CODE(+)  ORDER BY X.ORDER_CODE "; 
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("û���̵�����");
            return;
        }

        // ��ӡ����  
        TParm data = new TParm(); 
        // ��ͷ����
        data.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "�̵��");
        data.setData("ORG_CODE", "TEXT",
                     "�̵㲿��: " + parm.getValue("DEPT_CHN_DESC", 0));
        data.setData("FROZEN_DATE", "TEXT",
                     "����ʱ��: " + this.getComboBox("FROZEN_DATE").getSelectedName());
        Timestamp datetime = StringTool.getTimestamp(new Date());
        data.setData("DATE", "TEXT",
                     "�Ʊ�����: " +
                     datetime.toString().substring(0, 10).replace('-', '/'));

        // �������
        TParm parmData = new TParm();
        String pha_type = "";
        Map<String, Integer> map = new TreeMap<String, Integer>();
        String order_code = "";
        for (int i = 0; i < parm.getCount(); i++) {
            parmData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            parmData.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
            
            //luhai 2012-3-7 ��ҩƷ����ĳ�����Ч�� begin
//            if ("W".equals(parm.getValue("PHA_TYPE", i))) {
//                pha_type = "��ҩ";
//            }
//            else if ("C".equals(parm.getValue("PHA_TYPE", i))) {
//                pha_type = "�г�ҩ ";
//            }
//            else {
//                pha_type = "�в�ҩ";
//            }
//            parmData.addData("PHA_TYPE", pha_type);
//            parmData.addData("STOCK_QTY", parm.getValue("STOCK_QTY", i));
//            parmData.addData("CHECK_QTY", parm.getValue("ACTUAL_CHECK_QTY", i));   
//            parmData.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
//            parmData.addData("VALID_DATE",
//                             parm.getValue("VALID_DATE", i).substring(0, 10).
//                             replace('-', '/'));
//            parmData.addData("MAN_CODE", parm.getValue("MATERIAL_CHN_DESC", i));
//            parmData.addData("PHA_TYPE", pha_type);
            order_code = parm.getValue("ORDER_CODE", i);
            if (map.containsKey(order_code)) {
				map.remove(order_code);     
				map.put(order_code,        
						1);    
				parmData.addData("STOCK_QTY", parm.getValue("STOCK_QTY", i) );
            }      
            else {     
				map.put(order_code,  
						0);        
				parmData.addData("STOCK_QTY", parm.getValue("STOCK_QTY", i) +"   "+ parm.getValue("BASE_QTY", i)  );   
			}    
            
            
            //
            

           
//            parmData.addData("CHECK_QTY", parm.getValue("ACTUAL_CHECK_QTY", i));
            parmData.addData("CHECK_QTY", "");       
            parmData.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
            parmData.addData("VALID_DATE",
                             parm.getValue("VALID_DATE", i).substring(0, 10).
                             replace('-', '/'));
            parmData.addData("MAN_CODE", parm.getValue("MATERIAL_CHN_DESC", i));
          //luhai 2012-3-7 ��ҩƷ����ĳ�����Ч�� end
        }

        parmData.setCount(parm.getCount());
        parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parmData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parmData.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        parmData.addData("SYSTEM", "COLUMNS", "CHECK_QTY");
        parmData.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        //luhai 2012-2-13 delete batch_no man_code
//        parmData.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parmData.addData("SYSTEM", "COLUMNS", "VALID_DATE");
//        parmData.addData("SYSTEM", "COLUMNS", "MAN_CODE");

        data.setData("TABLE", parmData.getData());

        // ��β����
        data.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());  
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\QtyCheckM.jhw",
                             data);
    }

    /**
     * �̵���ϸ��
     */
    private void onPrintQtyCheckD() {
        String drozen_date = getComboBox("FROZEN_DATE").getSelectedID();
        if (getComboBox("FROZEN_DATE").getSelectedIndex() == 0) {
            this.messageBox("��ѡ�񶳽�ʱ��");
            return;
        }
        String sql = INDSQL.getIndQtyCheckD(org_code, drozen_date);
//        System.out.println("sql---"+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("û���̵���ϸ����");
            return;
        }

        // ��ӡ����
        TParm data = new TParm();
        // ��ͷ����
        data.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "�̵���ϸ��");
        data.setData("ORG_CODE", "TEXT",
                     "�̵㲿��: " + parm.getValue("DEPT_CHN_DESC", 0));
        data.setData("FROZEN_DATE", "TEXT",
                     "����ʱ��: " + this.getComboBox("FROZEN_DATE").getSelectedName());
        Timestamp datetime = StringTool.getTimestamp(new Date());
        data.setData("DATE", "TEXT",
                     "�Ʊ�����: " +
                     datetime.toString().substring(0, 10).replace('-', '/'));

        // �������
        TParm parmData = new TParm();
        String pha_type = "";
        double contract_amt_w = 0;
        double own_amt_w = 0;
        double contract_amt_c = 0;
        double own_amt_c = 0;
        double contract_amt_g = 0;
        double own_amt_g = 0;
        for (int i = 0; i < parm.getCount(); i++) {
            parmData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            parmData.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
            if ("W".equals(parm.getValue("PHA_TYPE", i))) {
                pha_type = "��ҩ";
                contract_amt_w += parm.getDouble("STOCK_AMT", i);
                own_amt_w += parm.getDouble("OWM_AMT", i);
            }
            else if ("C".equals(parm.getValue("PHA_TYPE", i))) {
                pha_type = "�г�ҩ ";
                contract_amt_c += parm.getDouble("STOCK_AMT", i);
                own_amt_c += parm.getDouble("OWM_AMT", i);
            }
            else {
                pha_type = "�в�ҩ";
                contract_amt_g += parm.getDouble("STOCK_AMT", i);
                own_amt_g += parm.getDouble("OWM_AMT", i);
            }
            parmData.addData("PHA_TYPE", pha_type);
            parmData.addData("STOCK_QTY", parm.getValue("STOCK_QTY", i));
            parmData.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
            parmData.addData("VALID_DATE",
                             parm.getValue("VALID_DATE", i).substring(0, 10).
                             replace('-', '/'));
            parmData.addData("CONTRACT_PRICE",
                             parm.getValue("CONTRACT_PRICE", i));
            parmData.addData("OWN_PRICE", parm.getValue("OWN_PRICE", i));
            parmData.addData("STOCK_AMT", parm.getValue("STOCK_AMT", i));
            parmData.addData("OWM_AMT", parm.getValue("OWM_AMT", i));
        }

        parmData.setCount(parm.getCount());
        parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parmData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parmData.addData("SYSTEM", "COLUMNS", "PHA_TYPE");
        parmData.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        //luhai delete 2012-2-13 begin
//        parmData.addData("SYSTEM", "COLUMNS", "BATCH_NO");
//        parmData.addData("SYSTEM", "COLUMNS", "VALID_DATE");
        //luhai delete 2012-2-13 end
        parmData.addData("SYSTEM", "COLUMNS", "CONTRACT_PRICE");
        parmData.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parmData.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
        parmData.addData("SYSTEM", "COLUMNS", "OWM_AMT");

        data.setData("TABLE", parmData.getData());

        // ��β����
        data.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
        //ɾ���ϼ���Ϣ ��ҩ����ҩ����ҩ luhai 2012-3-7 begin
        TParm parmEnd = new TParm();
//        parmEnd.addData("COL_1", "");
//        parmEnd.addData("COL_2", "");
//        parmEnd.addData("COL_3", "");
//        parmEnd.addData("COL_4", "");
//        parmEnd.addData("COL_5", "��ҩ�ϼ�:");
//        parmEnd.addData("COL_6", StringTool.round(contract_amt_w, 2));
//        parmEnd.addData("COL_7", StringTool.round(own_amt_w, 2));
//
//        parmEnd.addData("COL_1", "");
//        parmEnd.addData("COL_2", "");
//        parmEnd.addData("COL_3", "");
//        parmEnd.addData("COL_4", "");
//        parmEnd.addData("COL_5", "�г�ҩ�ϼ�:");
//        parmEnd.addData("COL_6", StringTool.round(contract_amt_c, 2));
//        parmEnd.addData("COL_7", StringTool.round(own_amt_c, 2));
//
//        parmEnd.addData("COL_1", "");
//        parmEnd.addData("COL_2", "");
//        parmEnd.addData("COL_3", "");
//        parmEnd.addData("COL_4", "");
//        parmEnd.addData("COL_5", "�в�ҩ�ϼ�:");
//        parmEnd.addData("COL_6", StringTool.round(contract_amt_g, 2));
//        parmEnd.addData("COL_7", StringTool.round(own_amt_g, 2));
      //ɾ���ϼ���Ϣ ��ҩ����ҩ����ҩ luhai 2012-3-7 end
        parmEnd.addData("COL_1", "");
        parmEnd.addData("COL_2", "");
        parmEnd.addData("COL_3", "");
        parmEnd.addData("COL_4", "");
        parmEnd.addData("COL_5", "�ϼ�:");
        parmEnd.addData("COL_6",
                        StringTool.round(contract_amt_w + contract_amt_c + contract_amt_g,
                                         2));
        parmEnd.addData("COL_7",
                        StringTool.round(own_amt_w + own_amt_c + own_amt_g, 2));

        parmEnd.addData("COL_1", "������:");
        parmEnd.addData("COL_2", "ҩƷ���:");
        parmEnd.addData("COL_3", "ҩ��������:");
        parmEnd.addData("COL_4", "�����:");
        parmEnd.addData("COL_5", "�̵���Ա:");
        parmEnd.addData("COL_6", "        �Ʊ���:");
        parmEnd.addData("COL_7", Operator.getName());

        parmEnd.setCount(5);
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_1");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_2");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_3");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_4");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_5");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_6");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_7");

        data.setData("TABLE_2", parmEnd.getData());

        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\QtyCheckD.jhw",
                             data);
    }

    /**
     * ӯ����
     */
    private void onPrintProfitLoss() {
        DecimalFormat df = new DecimalFormat("##########0.00");
        DecimalFormat df2 = new DecimalFormat("##########0.0000");
        String drozen_date = getComboBox("FROZEN_DATE").getSelectedID();
        if (getComboBox("FROZEN_DATE").getSelectedIndex() == 0) {
            this.messageBox("��ѡ�񶳽�ʱ��");
            return;
        }
        String sql = INDSQL.getIndQtyProfitLoss(org_code, drozen_date);
//        System.out.println("sql=== "+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("û���̵�ӯ������");
            return;
        }

        // ��ӡ����
        TParm data = new TParm();
        // ��ͷ����
        data.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "ҩƷ�̵�ӯ����");
        data.setData("ORG_CODE", "TEXT",
                     "�̵㲿��: " + parm.getValue("DEPT_CHN_DESC", 0));
        data.setData("FROZEN_DATE", "TEXT",
                     "����ʱ��: " + this.getComboBox("FROZEN_DATE").getSelectedName());
        String unfreeze_date = parm.getValue("UNFREEZE_DATE", 0);
        if (unfreeze_date.length() > 19) {
            unfreeze_date = unfreeze_date.substring(0, 19).replace('-', '/');
        }
        data.setData("UN_FROZEN_DATE", "TEXT",
                     "�ⶳʱ��: " + unfreeze_date);
        Timestamp datetime = StringTool.getTimestamp(new Date());
        data.setData("DATE", "TEXT",
                     "�Ʊ�����: " +
                     datetime.toString().substring(0, 10).replace('-', '/'));

        // �������
        TParm parmData = new TParm();
        double sum_freeze_amt = 0;
        double sum_own_amt = 0;
        double sum_profitLossPrice = 0;
        for (int i = 0; i < parm.getCount(); i++) {
            parmData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            parmData.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
//            parmData.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
//            parmData.addData("VALID_DATE",
//                             parm.getValue("VALID_DATE", i).substring(0, 10).
//                             replace('-', '/'));
            parmData.addData("BATCH_NO", "");
            parmData.addData("VALID_DATE","");
            parmData.addData("STOCK_QTY", parm.getValue("STOCK_QTY", i));
            parmData.addData("ACTUAL_CHECK_QTY", parm.getValue("ACTUAL_CHECK_QTY", i));
            parmData.addData("MODI_QTY", parm.getValue("MODI_QTY", i));
            parmData.addData("OWN_PRICE",df2.format(parm.getDouble("OWN_PRICE", i)));
//            parmData.addData("FREEZE_AMT", df.format(parm.getDouble("FREEZE_AMT", i)));
            sum_freeze_amt += parm.getDouble("FREEZE_AMT", i);
            parmData.addData("OWN_AMT", df.format(parm.getDouble("OWN_AMT", i)));
            sum_own_amt += parm.getDouble("OWN_AMT", i);
            sum_profitLossPrice += parm.getDouble("PROFIT_LOSS_PRICE", i);
        }

        parmData.setCount(parm.getCount());
        parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parmData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parmData.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parmData.addData("SYSTEM", "COLUMNS", "VALID_DATE");
        parmData.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        parmData.addData("SYSTEM", "COLUMNS", "ACTUAL_CHECK_QTY");
        parmData.addData("SYSTEM", "COLUMNS", "MODI_QTY");
        parmData.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//        parmData.addData("SYSTEM", "COLUMNS", "FREEZE_AMT");
        parmData.addData("SYSTEM", "COLUMNS", "OWN_AMT");

        //luhai 2012-3-7 ɾ��������ϼ� begin
//        data.setData("SUM_FREEZE_AMT", "TEXT",
//        		"������ϼ�:" + StringTool.round(sum_freeze_amt, 2));
        data.setData("SUM_FREEZE_AMT", "TEXT",
        "");
        //luhai 2012-3-7 ɾ��������ϼ� end
        data.setData("SUM_OWN_AMT", "TEXT",
                     "�ִ�ʵ�ʽ��ϼ�:" + StringTool.round(sum_own_amt, 2));

        data.setData("TABLE", parmData.getData());

        // ��β����
        data.setData("USER", "TEXT", "�Ʊ���:" + Operator.getName());
        // add by wangbin 2014-11-27 �ⲿ���� #486 ������ӯ���ϼƽ��
        // ��ӯ���ϼƽ��
        data.setData("PROFIT_LOSS_PRICE", "TEXT", "��ӯ���ϼƽ��:" + StringTool.round(sum_profitLossPrice, 2));
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\ProfitLoss.jhw",
                             data);

    }

    /**
     * �õ�ComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

}
