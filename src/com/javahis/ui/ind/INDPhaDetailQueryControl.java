package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.javahis.system.textFormat.TextFormatINDOrg;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import jdo.ind.INDSQL;
import jdo.sys.Operator;
import jdo.util.Manager;
import jdo.sys.SystemTool;
import com.dongyang.ui.TComboBox;

import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: ������ϸ��
 * </p>
 *
 * <p>
 * Description: ������ϸ��
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.09.22
 * @version 1.0
 */
public class INDPhaDetailQueryControl
    extends TControl {

    // ����TABLE
    private TTable table_m;
    // ������ϸTABLE
    private TTable table_d_a;
    // �п���ϸTABLE
    private TTable table_d_b;

    private Map map;

    public INDPhaDetailQueryControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        // ��ʼ��ͳ������
        //luhai modify 2012-1-24 �޸Ŀ�ʼʱ��ͽ���ʱ�� begin
//        Timestamp date = TJDODBTool.getInstance().getDBTime();
//        // ����ʱ��(���µĵ�һ��)
//        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
//                                                     substring(0, 4) + "/" +
//                                                     TypeTool.getString(date).
//                                                     substring(5, 7) +
//                                                     "/01 00:00:00",
//                                                     "yyyy/MM/dd HH:mm:ss");
//        setValue("END_DATE", dateTime);
//        // ��ʼʱ��(�ϸ��µ�һ��)
//        setValue("START_DATE",
//                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
//                 "/" +
//                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
//                 "/01 00:00:00");
        setStartEndDate();
      //luhai modify 2012-1-24 �޸Ŀ�ʼʱ��ͽ���ʱ�� end
        table_m = this.getTable("TABLE_M");
        table_d_a = this.getTable("TABLE_D_A");
        table_d_b =  this.getTable("TABLE_D_B");

        //���״̬����
        map = new HashMap();
        map.put("VER", "����");
        map.put("RET", "�˿�");
        map.put("THI", "�������");
        map.put("DEP", "����");
        map.put("GIF", "����");
        map.put("THO", "��������");
        map.put("REG", "�˻�");
        map.put("FRO", "�̵�");
        map.put("O_RET", "������ҩ");
        map.put("E_RET", "������ҩ");
        map.put("I_RET", "סԺ��ҩ");
        map.put("TEC", "��ҩ����");
        map.put("EXM", "���ұ�ҩ");
        map.put("WAS", "���");
        map.put("COS", "���Ĳ�����");
        map.put("O_DPN", "���﷢ҩ");
        map.put("E_DPN", "���﷢ҩ");
        map.put("I_DPN", "סԺ��ҩ");
    }
    /**
     *
     * ������ʼʱ��ͽ���ʱ�䣬����26-����25
     */
    private void setStartEndDate(){
    	Timestamp date = TJDODBTool.getInstance().getDBTime();
        // ����ʱ��(���µ�25)
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
                                                     substring(0, 4) + "/" +
                                                     TypeTool.getString(date).
                                                     substring(5, 7) +
                                                     "/25 23:59:59",
                                                     "yyyy/MM/dd HH:mm:ss");
        setValue("END_DATE", dateTime);
        // ��ʼʱ��(�ϸ���26)
        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(date.getTime());
        cd.add(Calendar.MONTH, -1);
        Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());
        setValue("START_DATE",
        		endDateTimestamp.toString().substring(0, 4) +
                 "/" +
                 endDateTimestamp.toString().substring(5, 7) +
                 "/26 00:00:00");
    }
    /**
     * ��ѯ����
     */
    public void onQuery() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("��ѡ��ͳ�Ʋ���");
            return;
        }
        String org_code = this.getValueString("ORG_CODE");
        String start_date = this.getValueString("START_DATE").substring(0, 4)
            + this.getValueString("START_DATE").substring(5, 7)
            + this.getValueString("START_DATE").substring(8, 10)
            + this.getValueString("START_DATE").substring(11, 13)
            + this.getValueString("START_DATE").substring(14, 16)
            + this.getValueString("START_DATE").substring(17, 19);
        String end_date = this.getValueString("END_DATE").substring(0, 4)
            + this.getValueString("END_DATE").substring(5, 7)
            + this.getValueString("END_DATE").substring(8, 10)
            + this.getValueString("END_DATE").substring(11, 13)
            + this.getValueString("END_DATE").substring(14, 16)
            + this.getValueString("END_DATE").substring(17, 19);
        String qty_in = this.getValueString("CHECK_A");
        String qty_out = this.getValueString("CHECK_B");
        String qty_check = this.getValueString("CHECK_C");
        String order_code = this.getValueString("ORDER_CODE");
        // ҩƷ��ϸ�˻��ܲ�ѯ
        String slqM = INDSQL.getINDPhaDetailMQuery(org_code, start_date,
                                                   end_date,
                                                   order_code);
//        System.out.println("slqM==="+slqM);
        TParm parmM = new TParm(TJDODBTool.getInstance().select(slqM));

        // �������⣬�п��ѯ
        if (getRadioButton("IND_ORG_A").isSelected()) {
            // ҩƷ��ϸ����ϸ��ѯ(����)
            String slqD_A = INDSQL.getINDPhaDetailDQueryA(org_code, start_date,
                end_date, qty_in, qty_out, qty_check, order_code);
//            System.out.println("slqD_A====" + slqD_A);
            TParm parmD_A = new TParm(TJDODBTool.getInstance().select(slqD_A));

            if (getRadioButton("TYPE_A").isSelected()) {
                if (parmM == null || parmM.getCount("ORDER_DESC") <= 0) {
                    table_m.removeRowAll();
                    this.messageBox("�޲�ѯ���");
                }
                else {
                    table_m.setParmValue(parmM);
                    //luhai add ����ϼ���2012-2-22 begin
                    addTotRowM();
                    //luhai add ����ϼ���2012-2-22 end
                }
            }
            else {
                if (parmD_A == null || parmD_A.getCount("ORDER_DESC") <= 0) {
                    table_d_a.removeRowAll();
                    this.messageBox("�޲�ѯ���");
                }
                else {
                    table_d_a.setParmValue(parmD_A);
                    addTotRowD();
                }
            }
            setSumAmt();
        }
        else {
            // ҩƷ��ϸ����ϸ��ѯ(�п�)
            String slqD_B = INDSQL.getINDPhaDetailDQueryB(org_code, start_date,
                end_date, qty_in, qty_out, qty_check, order_code);
//            System.out.println("slqD_B---" + slqD_B);
            TParm parmD_B = new TParm(TJDODBTool.getInstance().select(slqD_B));

            if (getRadioButton("TYPE_A").isSelected()) {
                if (parmM == null || parmM.getCount("ORDER_DESC") <= 0) {
                    table_m.removeRowAll();
                    this.messageBox("�޲�ѯ���");
                }
                else {
                    table_m.setParmValue(parmM);
                    //luhai 2012-2-22 add totRow
                    addTotRowM();
                }
            }
            else {
                if (parmD_B == null || parmD_B.getCount("ORDER_DESC") <= 0) {
                    table_d_b.removeRowAll();
                    this.messageBox("�޲�ѯ���");
                }
                else {
                    table_d_b.setParmValue(parmD_B);
                    //����ϼ���
                    addTotRowD();
                }
            }
            setSumAmt();
        }
    }
    private void addTotRowM(){
        TParm tableParm = table_m.getParmValue();
        TParm parm = new TParm();
        //ҩƷ���˼���ϼƹ��� begin
        double totLastTotAMT=0;
        double totStockOutAMT=0;
        double totStockInAMT=0;
        double totModiyAMT=0;
        double totTotAMT=0;
        for (int i = 0; i < table_m.getRowCount(); i++) {
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("SPECIFICATION",
                         tableParm.getValue("SPECIFICATION", i));
            parm.addData("LAST_TOTSTOCK_QTY",
                         tableParm.getValue("LAST_TOTSTOCK_QTY", i));
            parm.addData("UNIT_CHN_DESC",
                         tableParm.getValue("UNIT_CHN_DESC", i));
            parm.addData("LAST_TOTSTOCK_AMT",
                         tableParm.getValue("LAST_TOTSTOCK_AMT", i));
            totLastTotAMT+=Double.parseDouble(tableParm.getValue("LAST_TOTSTOCK_AMT", i));
            parm.addData("STOCKIN_QTY", tableParm.getValue("STOCKIN_QTY", i));
            parm.addData("STOCKIN_AMT", tableParm.getValue("STOCKIN_AMT", i));
            totStockInAMT+=Double.parseDouble(tableParm.getValue("STOCKIN_AMT", i));
            parm.addData("STOCKOUT_QTY",
                         tableParm.getValue("STOCKOUT_QTY", i));
            parm.addData("STOCKOUT_AMT",
                         tableParm.getValue("STOCKOUT_AMT", i));
            totStockOutAMT+=Double.parseDouble(tableParm.getValue("STOCKOUT_AMT", i));
            parm.addData("CHECKMODI_QTY",
                         tableParm.getValue("CHECKMODI_QTY", i));
            parm.addData("CHECKMODI_AMT",
                         tableParm.getValue("CHECKMODI_AMT", i));
            totModiyAMT+=Double.parseDouble(tableParm.getValue("CHECKMODI_AMT", i));
            parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
            parm.addData("STOCK_AMT", tableParm.getValue("STOCK_AMT", i));
            totTotAMT+=Double.parseDouble(tableParm.getValue("STOCK_AMT", i));
        }
        //����ϼ���
        parm.addData("ORDER_DESC","�ϼƣ�");
        parm.addData("SPECIFICATION",
                    "");
        parm.addData("LAST_TOTSTOCK_QTY",
                     "");
        parm.addData("UNIT_CHN_DESC",
                     "");
        parm.addData("LAST_TOTSTOCK_AMT",
        		totLastTotAMT );
        parm.addData("STOCKIN_QTY","");
        parm.addData("STOCKIN_AMT", StringTool.round(totStockInAMT,2));
        parm.addData("STOCKOUT_QTY",
                     "");
        parm.addData("STOCKOUT_AMT",
        		StringTool.round(totStockOutAMT,2));
        parm.addData("CHECKMODI_QTY",
                     "");
        parm.addData("CHECKMODI_AMT",
        		StringTool.round(totModiyAMT,2));
        parm.addData("STOCK_QTY", "");
        parm.addData("STOCK_AMT",StringTool.round(totTotAMT,2));
        //����ϼ���end
        parm.setCount(parm.getCount("ORDER_DESC"));
        this.table_m.setParmValue(parm);
    }
    private void addTotRowD(){
    	TParm parm=new TParm();
        if (this.getRadioButton("IND_ORG_A").isSelected()) {
            //�����ܼ���Ϣ begin
            double totAmt = 0;
            TParm tableParm = table_d_a.getParmValue();
            for (int i = 0; i < table_d_a.getRowCount(); i++) {
                parm.addData("CHECK_DATE",
                             tableParm.getValue("CHECK_DATE",
                             i).substring(0, 10));
                parm.addData("STATUS",
                             map.get(tableParm.getValue("STATUS", i)));
                parm.addData("ORDER_DESC",
                             tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("QTY", tableParm.getValue("QTY", i));
                parm.addData("UNIT_CHN_DESC",
                             tableParm.getValue("UNIT_CHN_DESC", i));
                parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                parm.addData("AMT", tableParm.getValue("AMT", i));
                totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
                parm.addData("ORG_CHN_DESC",
                             tableParm.getValue("ORG_CHN_DESC", i));
            }
            //����ϼ���Ϣ begin
            parm.addData("CHECK_DATE",
            		"�ϼƣ�");
            parm.addData("STATUS",
            		"");
            parm.addData("ORDER_DESC",
            		"");
            parm.addData("SPECIFICATION",
            		"");
            parm.addData("QTY", "");
            parm.addData("UNIT_CHN_DESC",
            		"");
            parm.addData("OWN_PRICE","");
            parm.addData("AMT", StringTool.round(totAmt, 2));
            parm.addData("ORG_CHN_DESC",
            		"");

            //����ϼ���Ϣ end
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
            parm.addData("SYSTEM", "COLUMNS", "STATUS");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
            table_d_a.setParmValue(parm);
        }
        else {
            //�����ܼ���Ϣ
            double totAmt = 0;
            TParm tableParm = table_d_b.getParmValue();
            for (int i = 0; i < table_d_b.getRowCount(); i++) {
                parm.addData("CHECK_DATE",
                             tableParm.getValue("CHECK_DATE",
                             i).substring(0, 10));
                parm.addData("STATUS",
                             map.get(tableParm.getValue("STATUS", i)));
                parm.addData("ORDER_DESC",
                             tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("QTY", tableParm.getValue("QTY", i));
                parm.addData("UNIT_CHN_DESC",
                             tableParm.getValue("UNIT_CHN_DESC", i));
                parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                parm.addData("AMT", tableParm.getValue("AMT", i));
                totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
                parm.addData("DEPT_CHN_DESC",
                             tableParm.getValue("DEPT_CHN_DESC", i));
                parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
                parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
            }
            //����ϼ���
	           parm.addData("CHECK_DATE","�ϼƣ�");
	           parm.addData("STATUS",
	                       "");
	           parm.addData("ORDER_DESC",
	                       "");
	           parm.addData("SPECIFICATION",
	                        "");
	           parm.addData("QTY", "");
	           parm.addData("UNIT_CHN_DESC",
	                       "");
	           parm.addData("OWN_PRICE","");
	           parm.addData("AMT", StringTool.round(totAmt,2));
	           parm.addData("DEPT_CHN_DESC",
	                        "");
	           parm.addData("MR_NO","");
	           parm.addData("PAT_NAME","");
	           parm.addData("CASE_NO","");
	           //add �ϼ���end
	           parm.setCount(parm.getCount("CHECK_DATE"));
	           table_d_b.setParmValue(parm);
        }

    }
    /**
     * ��շ���
     */
    public void onClear() {
        String clearStr = "ORG_CODE;ORDER_CODE;ORDER_DESC;SUM_AMT";
        this.clearValue(clearStr);
        this.getRadioButton("IND_ORG_A").setSelected(true);
        onChangeOrgType();
        this.getRadioButton("TYPE_A").setSelected(true);
        onChangeInfoType();
        this.getCheckBox("CHECK_A").setSelected(true);
        this.getCheckBox("CHECK_B").setSelected(true);
        this.getCheckBox("CHECK_C").setSelected(true);
        //luhai modify 2012-1-24 ���ó�ʼ��ʱ��Ĺ��÷��� begin
//        // ��ʼ��ͳ������
//        Timestamp date = TJDODBTool.getInstance().getDBTime();
//        // ����ʱ��(���µĵ�һ��)
//        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
//            substring(0, 4) + "/" +
//            TypeTool.getString(date).
//            substring(5, 7) +
//            "/01 00:00:00",
//            "yyyy/MM/dd HH:mm:ss");
//        setValue("END_DATE", dateTime);
//        // ��ʼʱ��(�ϸ��µ�һ��)
//        setValue("START_DATE",
//                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
//                 "/" +
//                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
//                 "/01 00:00:00");
        //��ʼ����ѯʱ��
        setStartEndDate();
        //luhai modify 2012-1-24 ���ó�ʼ��ʱ��Ĺ��÷��� end
        table_m.removeRowAll();
        table_d_a.removeRowAll();
        table_d_b.removeRowAll();
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
    	 //*********************************************
    	//ҩ����ϸ�˼���ϼƹ��� luhai begin 2012-2-22
    	//*********************************************
        // ��ӡ����
        TParm date = new TParm();
        date.setData("ORG_CODE", "TEXT", "ͳ�Ʋ���: " +
                     getTextFormat("ORG_CODE").getText());
        String start_date = getValueString("START_DATE");
        String end_date = getValueString("END_DATE");
        date.setData("DATE_AREA", "TEXT", "ͳ������: " +
                     start_date.substring(0, 4) + "/" +
                     start_date.substring(5, 7) + "/" +
                     start_date.substring(8, 10) + " " +
                     start_date.substring(11, 13) + ":" +
                     start_date.substring(14, 16) + ":" +
                     start_date.substring(17, 19) + " ~ " +
                     end_date.substring(0, 4) + "/" +
                     end_date.substring(5, 7) + "/" +
                     end_date.substring(8, 10) + " " +
                     end_date.substring(11, 13) + ":" +
                     end_date.substring(14, 16) + ":" +
                     end_date.substring(17, 19) );
        date.setData("DATE", "TEXT", "�Ʊ�ʱ��: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 10).replace('-', '/'));
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());

        // �������
        TParm parm = new TParm();
        if (this.getRadioButton("TYPE_A").isSelected()) {
            //����
            if (this.getRadioButton("IND_ORG_A").isSelected()) {
                //ҩ��
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "ҩ��ҩƷ����");
            }
            else {
                //ҩ��
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "ҩ��ҩƷ����");
            }
            TParm tableParm = table_m.getParmValue();
            for (int i = 0; i < table_m.getRowCount(); i++) {
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             tableParm.getValue("SPECIFICATION", i));
                parm.addData("LAST_TOTSTOCK_QTY",
                             tableParm.getValue("LAST_TOTSTOCK_QTY", i));
                parm.addData("UNIT_CHN_DESC",
                             tableParm.getValue("UNIT_CHN_DESC", i));
                parm.addData("LAST_TOTSTOCK_AMT",
                             tableParm.getValue("LAST_TOTSTOCK_AMT", i));
                parm.addData("STOCKIN_QTY", tableParm.getValue("STOCKIN_QTY", i));
                parm.addData("STOCKIN_AMT", tableParm.getValue("STOCKIN_AMT", i));
                parm.addData("STOCKOUT_QTY",
                             tableParm.getValue("STOCKOUT_QTY", i));
                parm.addData("STOCKOUT_AMT",
                             tableParm.getValue("STOCKOUT_AMT", i));
                parm.addData("CHECKMODI_QTY",
                             tableParm.getValue("CHECKMODI_QTY", i));
                parm.addData("CHECKMODI_AMT",
                             tableParm.getValue("CHECKMODI_AMT", i));
                parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
                parm.addData("STOCK_AMT", tableParm.getValue("STOCK_AMT", i));
            }
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_QTY");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_AMT");
            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_QTY");
            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_AMT");
            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_QTY");
            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_AMT");
            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_QTY");
            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_AMT");
            parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
            parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
            date.setData("TABLE", parm.getData());
            // ��β����
            date.setData("SUM_AMT", "TEXT",
                         "�ܽ� " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai add �������� 2012-2-13
            // ���ô�ӡ����
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\IND\\INDPhaDetailQueryA.jhw", date);
        }
        else {
            if (this.getRadioButton("IND_ORG_A").isSelected()) {
                //ҩ����ϸ
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "ҩ��ҩƷ��ϸ��");
                TParm tableParm = table_d_a.getParmValue();
                for (int i = 0; i < table_d_a.getRowCount(); i++) {
                	if(tableParm.getValue("CHECK_DATE",
                            i).length()>11){
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i).substring(0, 10));
                	}else{
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i));
                	}
                	String key = tableParm.getValue("STATUS", i);
//                	System.out.println(""+getStatus1(key));
                    parm.addData("STATUS",getStatus1(key));
                    parm.addData("ORDER_DESC",
                                 tableParm.getValue("ORDER_DESC", i));
                    parm.addData("SPECIFICATION",
                                 tableParm.getValue("SPECIFICATION", i));
                    parm.addData("QTY", tableParm.getValue("QTY", i));
                    parm.addData("UNIT_CHN_DESC",
                                 tableParm.getValue("UNIT_CHN_DESC", i));
                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                    parm.addData("AMT", tableParm.getValue("AMT", i));
                    parm.addData("ORG_CHN_DESC",
                                 tableParm.getValue("ORG_CHN_DESC", i));
                }
                parm.setCount(parm.getCount("ORDER_DESC"));
                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
                parm.addData("SYSTEM", "COLUMNS", "STATUS");
                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
                parm.addData("SYSTEM", "COLUMNS", "QTY");
                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
                parm.addData("SYSTEM", "COLUMNS", "AMT");
                parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
                date.setData("TABLE", parm.getData());
                // ��β����
                date.setData("SUM_AMT", "TEXT",
                             "�ܽ� " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai 2012-2-13 ��������
                // ���ô�ӡ����
                this.openPrintWindow(
                    "%ROOT%\\config\\prt\\IND\\INDPhaDetailQueryB.jhw", date);
            }
            else {
                //ҩ����ϸ
                date.setData("TITLE", "TEXT", Manager.getOrganization().
                             getHospitalCHNFullName(Operator.getRegion()) +
                             "ҩ��ҩƷ��ϸ��");
                TParm tableParm = table_d_b.getParmValue();
                for (int i = 0; i < table_d_b.getRowCount(); i++) {
                	if(tableParm.getValue("CHECK_DATE",
                            i).length()>=11){
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i).substring(0, 10));
                	}else{
                		parm.addData("CHECK_DATE",
                				tableParm.getValue("CHECK_DATE",
                						i));
                	}
                    parm.addData("STATUS",
                                 map.get(tableParm.getValue("STATUS", i)));
                    parm.addData("ORDER_DESC",
                                 tableParm.getValue("ORDER_DESC", i));
                    parm.addData("SPECIFICATION",
                                 tableParm.getValue("SPECIFICATION", i));
                    parm.addData("QTY", tableParm.getValue("QTY", i));
                    parm.addData("UNIT_CHN_DESC",
                                 tableParm.getValue("UNIT_CHN_DESC", i));
                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
                    parm.addData("AMT", tableParm.getValue("AMT", i));
                    parm.addData("DEPT_CHN_DESC",
                                 tableParm.getValue("DEPT_CHN_DESC", i));
                    parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                    parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
                    parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
                }
                parm.setCount(parm.getCount("ORDER_DESC"));
                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
                parm.addData("SYSTEM", "COLUMNS", "STATUS");
                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
                parm.addData("SYSTEM", "COLUMNS", "QTY");
                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
                parm.addData("SYSTEM", "COLUMNS", "AMT");
                parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
                parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
                parm.addData("SYSTEM", "COLUMNS", "MR_NO");
                parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
                date.setData("TABLE", parm.getData());
                // ��β����
                date.setData("SUM_AMT", "TEXT",
                             "�ܽ� " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai 2012-2-13 ��������
                // ���ô�ӡ����
                this.openPrintWindow(
                    "%ROOT%\\config\\prt\\IND\\INDPhaDetailQueryC.jhw", date);
            }
        }
//        // ��ӡ����
//        TParm date = new TParm();
//        date.setData("ORG_CODE", "TEXT", "ͳ�Ʋ���: " +
//                     getTextFormat("ORG_CODE").getText());
//        String start_date = getValueString("START_DATE");
//        String end_date = getValueString("END_DATE");
//        date.setData("DATE_AREA", "TEXT", "ͳ������: " +
//                     start_date.substring(0, 4) + "/" +
//                     start_date.substring(5, 7) + "/" +
//                     start_date.substring(8, 10) + " " +
//                     start_date.substring(11, 13) + ":" +
//                     start_date.substring(14, 16) + ":" +
//                     start_date.substring(17, 19) + " ~ " +
//                     end_date.substring(0, 4) + "/" +
//                     end_date.substring(5, 7) + "/" +
//                     end_date.substring(8, 10) + " " +
//                     end_date.substring(11, 13) + ":" +
//                     end_date.substring(14, 16) + ":" +
//                     end_date.substring(17, 19) );
//        date.setData("DATE", "TEXT", "�Ʊ�ʱ��: " +
//                     SystemTool.getInstance().getDate().toString().
//                     substring(0, 10).replace('-', '/'));
//        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
//
//        // �������
//        TParm parm = new TParm();
//        if (this.getRadioButton("TYPE_A").isSelected()) {
//            //����
//            if (this.getRadioButton("IND_ORG_A").isSelected()) {
//                //ҩ��
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "ҩ��ҩƷ����");
//            }
//            else {
//                //ҩ��
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "ҩ��ҩƷ����");
//            }
//            TParm tableParm = table_m.getParmValue();
//            //ҩƷ���˼���ϼƹ��� begin
//            double totLastTotAMT=0;
//            double totStockOutAMT=0;
//            double totStockInAMT=0;
//            double totModiyAMT=0;
//            double totTotAMT=0;
//            for (int i = 0; i < table_m.getRowCount(); i++) {
//                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
//                parm.addData("SPECIFICATION",
//                             tableParm.getValue("SPECIFICATION", i));
//                parm.addData("LAST_TOTSTOCK_QTY",
//                             tableParm.getValue("LAST_TOTSTOCK_QTY", i));
//                parm.addData("UNIT_CHN_DESC",
//                             tableParm.getValue("UNIT_CHN_DESC", i));
//                parm.addData("LAST_TOTSTOCK_AMT",
//                             tableParm.getValue("LAST_TOTSTOCK_AMT", i));
//                totLastTotAMT+=Double.parseDouble(tableParm.getValue("LAST_TOTSTOCK_AMT", i));
//                parm.addData("STOCKIN_QTY", tableParm.getValue("STOCKIN_QTY", i));
//                parm.addData("STOCKIN_AMT", tableParm.getValue("STOCKIN_AMT", i));
//                totStockInAMT+=Double.parseDouble(tableParm.getValue("STOCKIN_AMT", i));
//                parm.addData("STOCKOUT_QTY",
//                             tableParm.getValue("STOCKOUT_QTY", i));
//                parm.addData("STOCKOUT_AMT",
//                             tableParm.getValue("STOCKOUT_AMT", i));
//                totStockOutAMT+=Double.parseDouble(tableParm.getValue("STOCKOUT_AMT", i));
//                parm.addData("CHECKMODI_QTY",
//                             tableParm.getValue("CHECKMODI_QTY", i));
//                parm.addData("CHECKMODI_AMT",
//                             tableParm.getValue("CHECKMODI_AMT", i));
//                totModiyAMT+=Double.parseDouble(tableParm.getValue("CHECKMODI_AMT", i));
//                parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
//                parm.addData("STOCK_AMT", tableParm.getValue("STOCK_AMT", i));
//                totTotAMT+=Double.parseDouble(tableParm.getValue("STOCK_AMT", i));
//            }
//            //����ϼ���
//            parm.addData("ORDER_DESC","�ϼƣ�");
//            parm.addData("SPECIFICATION",
//                        "");
//            parm.addData("LAST_TOTSTOCK_QTY",
//                         "");
//            parm.addData("UNIT_CHN_DESC",
//                         "");
//            parm.addData("LAST_TOTSTOCK_AMT",
//            		totLastTotAMT );
//            parm.addData("STOCKIN_QTY","");
//            parm.addData("STOCKIN_AMT", StringTool.round(totStockInAMT,2));
//            parm.addData("STOCKOUT_QTY",
//                         "");
//            parm.addData("STOCKOUT_AMT",
//            		StringTool.round(totStockOutAMT,2));
//            parm.addData("CHECKMODI_QTY",
//                         "");
//            parm.addData("CHECKMODI_AMT",
//            		StringTool.round(totModiyAMT,2));
//            parm.addData("STOCK_QTY", "");
//            parm.addData("STOCK_AMT",StringTool.round(totTotAMT,2));
//            //����ϼ���end
//            parm.setCount(parm.getCount("ORDER_DESC"));
//            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//            parm.addData("SYSTEM", "COLUMNS", "LAST_TOTSTOCK_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKIN_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "STOCKOUT_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "CHECKMODI_AMT");
//            parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
//            parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
//            date.setData("TABLE", parm.getData());
//            // ��β����
//            date.setData("SUM_AMT", "TEXT",
//                         "�ܽ� " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai add �������� 2012-2-13
//            // ���ô�ӡ����
//            this.openPrintWindow(
//                "%ROOT%\\config\\prt\\IND\\INDPhaDetailQueryA.jhw", date);
//        }
//        else {
//            if (this.getRadioButton("IND_ORG_A").isSelected()) {
//                //ҩ����ϸ
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "ҩ��ҩƷ��ϸ��");
//                //�����ܼ���Ϣ begin
//                double totAmt = 0;
//                TParm tableParm = table_d_a.getParmValue();
//                for (int i = 0; i < table_d_a.getRowCount(); i++) {
//                    parm.addData("CHECK_DATE",
//                                 tableParm.getValue("CHECK_DATE",
//                                 i).substring(0, 10));
//                    parm.addData("STATUS",
//                                 map.get(tableParm.getValue("STATUS", i)));
//                    parm.addData("ORDER_DESC",
//                                 tableParm.getValue("ORDER_DESC", i));
//                    parm.addData("SPECIFICATION",
//                                 tableParm.getValue("SPECIFICATION", i));
//                    parm.addData("QTY", tableParm.getValue("QTY", i));
//                    parm.addData("UNIT_CHN_DESC",
//                                 tableParm.getValue("UNIT_CHN_DESC", i));
//                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
//                    parm.addData("AMT", tableParm.getValue("AMT", i));
//                    totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
//                    parm.addData("ORG_CHN_DESC",
//                                 tableParm.getValue("ORG_CHN_DESC", i));
//                }
//                //����ϼ���Ϣ begin
//                parm.addData("CHECK_DATE",
//                		"�ϼƣ�");
//                parm.addData("STATUS",
//                		"");
//                parm.addData("ORDER_DESC",
//                		"");
//                parm.addData("SPECIFICATION",
//                		"");
//                parm.addData("QTY", "");
//                parm.addData("UNIT_CHN_DESC",
//                		"");
//                parm.addData("OWN_PRICE","");
//                parm.addData("AMT", StringTool.round(totAmt, 2));
//                parm.addData("ORG_CHN_DESC",
//                		"");
//
//                //����ϼ���Ϣ end
//                parm.setCount(parm.getCount("ORDER_DESC"));
//                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
//                parm.addData("SYSTEM", "COLUMNS", "STATUS");
//                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//                parm.addData("SYSTEM", "COLUMNS", "QTY");
//                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//                parm.addData("SYSTEM", "COLUMNS", "AMT");
//                parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
//                date.setData("TABLE", parm.getData());
//                // ��β����
//                date.setData("SUM_AMT", "TEXT",
//                             "�ܽ� " +  StringTool.round(totAmt, 2));//luhai 2012-2-13 ��������
//                // ���ô�ӡ����
//                this.openPrintWindow(
//                    "%ROOT%\\config\\prt\\IND\\INDPhaDetailQueryB.jhw", date);
//            }
//            else {
//                //ҩ����ϸ
//                date.setData("TITLE", "TEXT", Manager.getOrganization().
//                             getHospitalCHNFullName(Operator.getRegion()) +
//                             "ҩ��ҩƷ��ϸ��");
//                //�����ܼ���Ϣ
//                double totAmt = 0;
//                TParm tableParm = table_d_b.getParmValue();
//                for (int i = 0; i < table_d_b.getRowCount(); i++) {
//                    parm.addData("CHECK_DATE",
//                                 tableParm.getValue("CHECK_DATE",
//                                 i).substring(0, 10));
//                    parm.addData("STATUS",
//                                 map.get(tableParm.getValue("STATUS", i)));
//                    parm.addData("ORDER_DESC",
//                                 tableParm.getValue("ORDER_DESC", i));
//                    parm.addData("SPECIFICATION",
//                                 tableParm.getValue("SPECIFICATION", i));
//                    parm.addData("QTY", tableParm.getValue("QTY", i));
//                    parm.addData("UNIT_CHN_DESC",
//                                 tableParm.getValue("UNIT_CHN_DESC", i));
//                    parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
//                    parm.addData("AMT", tableParm.getValue("AMT", i));
//                    totAmt+=Double.parseDouble(tableParm.getValue("AMT", i));
//                    parm.addData("DEPT_CHN_DESC",
//                                 tableParm.getValue("DEPT_CHN_DESC", i));
//                    parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
//                    parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
//                    parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
//                }
//                //����ϼ���
//		           parm.addData("CHECK_DATE","�ϼƣ�");
//		           parm.addData("STATUS",
//		                       "");
//		           parm.addData("ORDER_DESC",
//		                       "");
//		           parm.addData("SPECIFICATION",
//		                        "");
//		           parm.addData("QTY", "");
//		           parm.addData("UNIT_CHN_DESC",
//		                       "");
//		           parm.addData("OWN_PRICE","");
//		           parm.addData("AMT", StringTool.round(totAmt,2));
//		           parm.addData("DEPT_CHN_DESC",
//		                        "");
//		           parm.addData("MR_NO","");
//		           parm.addData("PAT_NAME","");
//		           parm.addData("CASE_NO","");
//           //add �ϼ���end
//                parm.setCount(parm.getCount("ORDER_DESC"));
//                parm.addData("SYSTEM", "COLUMNS", "CHECK_DATE");
//                parm.addData("SYSTEM", "COLUMNS", "STATUS");
//                parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//                parm.addData("SYSTEM", "COLUMNS", "QTY");
//                parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//                parm.addData("SYSTEM", "COLUMNS", "AMT");
//                parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
//                parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//                parm.addData("SYSTEM", "COLUMNS", "MR_NO");
//                parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
//                date.setData("TABLE", parm.getData());
//                // ��β����
//                date.setData("SUM_AMT", "TEXT",
//                             "�ܽ� " + StringTool.round(getValueDouble("SUM_AMT"),2));//luhai 2012-2-13 ��������
//                // ���ô�ӡ����
//                this.openPrintWindow(
//                    "%ROOT%\\config\\prt\\IND\\INDPhaDetailQueryC.jhw", date);
//            }
//        }
   	 //*********************************************
    	//ҩ����ϸ�˼���ϼƹ��� luhai end 2012-2-22
    	//*********************************************
    }

    /**
     * �����ܽ��
     */
    private void setSumAmt() {
        double amt = 0;
//        if (this.getRadioButton("IND_ORG_A").isSelected()) {
//        		for (int i = 0; i < table_m.getRowCount(); i++) {
//        			amt += table_m.getItemDouble(i, "STOCK_AMT");
//        		}
//        }
//        else if (this.getRadioButton("TYPE_A").isSelected()) {
//            for (int i = 0; i < table_d_a.getRowCount(); i++) {
//                amt += table_d_a.getItemDouble(i, "AMT");
//            }
//        }
//        else {
//            for (int i = 0; i < table_d_b.getRowCount(); i++) {
//                amt += table_d_b.getItemDouble(i, "AMT");
//            }
//        }
        if (this.getRadioButton("TYPE_A").isSelected()){
    		for (int i = 0; i < table_m.getRowCount()-1; i++) {//ȥ���ϼ���
			amt += table_m.getItemDouble(i, "STOCK_AMT");
    		}
        }else{
		       if (this.getRadioButton("IND_ORG_A").isSelected()) {
		          for (int i = 0; i < table_d_a.getRowCount()-1; i++) {//ȥ���ϼ���
		              amt += table_d_a.getItemDouble(i, "AMT");
		          }
		      }
		      else {
		          for (int i = 0; i < table_d_b.getRowCount()-1; i++) {//ȥ���ϼ���
		              amt += table_d_b.getItemDouble(i, "AMT");
		          }
		      }
        }
        this.setValue("SUM_AMT", amt);
    }

    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }

    /**
     * ͳ�Ƶ�λ����¼�
     */
    public void onChangeOrgType() {
        TextFormatINDOrg ind_org = (TextFormatINDOrg)this.getComponent(
            "ORG_CODE");
        ind_org.setValue("");
        if (getRadioButton("IND_ORG_A").isSelected()) {
            ind_org.setOrgType("A");
        }
        else {
            ind_org.setOrgType("B");
        }
        ind_org.onQuery();
        onChangeInfoType();
    }

    /**
     * ���ͱ���¼�
     */
    public void onChangeInfoType() {
        if (getRadioButton("TYPE_A").isSelected()) {
            getCheckBox("CHECK_A").setEnabled(false);
            getCheckBox("CHECK_B").setEnabled(false);
            getCheckBox("CHECK_C").setEnabled(false);
            table_m.setVisible(true);
            table_d_a.setVisible(false);
            table_d_b.setVisible(false);
        }
        else {
            getCheckBox("CHECK_A").setEnabled(true);
            getCheckBox("CHECK_B").setEnabled(true);
            getCheckBox("CHECK_C").setEnabled(true);
            table_m.setVisible(false);
            if (getRadioButton("IND_ORG_A").isSelected()) {
                table_d_a.setVisible(true);
                table_d_b.setVisible(false);
            }
            else {
                table_d_a.setVisible(false);
                table_d_b.setVisible(true);
            }
        }
        setSumAmt();
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�CheckBox����
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
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

    /**
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
    
    private static String getStatus1(String val){
    	String returnString = "����";
        if("VER".equals(val)){
        	returnString =  "����";
        }
        if("RET".equals(val)){
        	returnString =  "�˿�";
        }
        if("DEP".equals(val)){
        	returnString =  "����";
        }
        if("GIF".equals(val)){
        	returnString =  "����";
        }
        if("THO".equals(val)){
        	returnString =  "��������";
        }
        if("REG".equals(val)){
        	returnString =  "�˻�";
        }
        if("FRO".equals(val)){
        	returnString =  "�̵�";
        }
		return val;
    }

}
