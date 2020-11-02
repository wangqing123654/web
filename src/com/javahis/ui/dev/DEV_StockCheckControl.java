package com.javahis.ui.dev;

import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import java.text.SimpleDateFormat;
import com.dongyang.jdo.TJDODBTool; 
import com.dongyang.control.TControl;
import java.util.Date;
import java.text.DecimalFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TComboBoxEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import jdo.sys.SystemTool;
import jdo.dev.DEVTool;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import jdo.dev.DEVStockCheckTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTextFormat;

public class DEV_StockCheckControl extends TControl {
	//ֻ������Ź����̵� 
    //����   
    private TTable table; 
    TParm tparm ;
    SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");      
    DecimalFormat ff = new DecimalFormat("######0.00");
    SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat date1 = new SimpleDateFormat("yyyyMMdd");
    
    public void onInit() {
        super.init();  
        table = this.getTable("TABLE");  
        //�󶨿ؼ� 
        callFunction("UI|DEV_CODE|setPopupMenuParameter", "aaa",
        "%ROOT%\\config\\sys\\DEVBASEPopupUI.x");
        //textfield���ܻش�ֵ   
        callFunction("UI|DEV_CODE|addEventListener", 
        TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        //�����¼�
        addEventListener("TABLE->" + TTableEvent.CLICKED,
                         "onMTableClicked"); 
        //�̵�ʱ��ѡ���¼�1DEPTSelect
        callFunction("UI|CHECKQTY_DATE|addEventListener",  
                     TComboBoxEvent.SELECTED, this, "CheckSelect");
        //ֵ�ı��¼� 
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableMChangeValue");
        //ֵ�ı��¼�
        addEventListener("TABLE->" + TTableEvent.RIGHT_CLICKED,
                         "onTableRightClick");
        //��ʼ��ҳ��
        initpage();  
    }

    /**
     * ��ʼ���ؼ�
     */ 
    public void initpage() {
        //��ʼ���û� 
        String deptCode = Operator.getDept();  
        this.setValue("DEPT_CODE", deptCode); 
        //DEPTSelect();     
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|DEV_CODE|setEnabled", true);      
        callFunction("UI|DEV_CHN_DESC|setEnabled", false);
        callFunction("UI|LOC_CODE|setEnabled", false);
    } 

    /**
     * ����ѡ���¼� 
     */
    public void DEPTSelect() {
        String deptcode = this.getValueString("DEPT_CODE");
        if (!deptcode.equals("")) {
            //��ѯ�̵�ʱ��
            int type = 1;
            TParm checktimeParm = DEVTool.getInstance().getChecktime(deptcode,
                    type);
            TParm date = new TParm(); 
            String checktime;
            int count = 0;
            for (int i = 0; i < checktimeParm.getCount("ACTUAL_CHECKQTY_DATE");
                         i++) {
                checktime = checktimeParm.getValue("ACTUAL_CHECKQTY_DATE", i);
                if (!checktime.equals("")) {
                    checktime = checktime.substring(0, 4) + "/"
                                + checktime.substring(5, 7) + "/"
                                + checktime.substring(8, 10) + " "
                                + checktime.substring(11, 13) + ":"
                                + checktime.substring(14, 16) + ":"
                                + checktime.substring(17, 19);
                    date.setData("F_DATE", i, checktime);
                    count += 1;
                }
            }
            date.setCount(count);
            this.getComboBox("CHECKQTY_DATE").setParmValue(date);
            this.getComboBox("CHECKQTY_DATE").setSelectedIndex(0);
            callFunction("UI|TABLE|removeRowAll");
            callFunction("UI|TABLED|removeRowAll");
        } else {
            this.getComboBox("CHECKQTY_DATE").setSelectedIndex(0);
            callFunction("UI|TABLE|removeRowAll");
            callFunction("UI|TABLED|removeRowAll"); 
        }
    }


    /**
     * �趨�̵�����ַ�ʽ��״̬
     */
    private void setCheckTypeStatus(boolean type) {
        callFunction("UI|DEV_CODE|setEnabled", type);
        callFunction("UI|DEV_CHN_DESC|setEnabled", type);
        callFunction("UI|LOC_CODE|setEnabled", type);

    }
 
    /**
     * ȫѡ�¼�
     */
    public void allSelect() {
        if (this.getTable("TABLED").getParmValue().getCount() < 0) {
            this.messageBox("��ѡ�����ݣ�");
            return;
        }
        if (this.getCheckBox("all").isSelected()) {
            TParm parm = this.getTable("TABLED").getParmValue();
            for (int i = 0; i < parm.getCount(); i++) {
                this.getTable("TABLED").setItem(i, "FLG", "Y");
            }
            this.getNumberTextField("D_TOT").setValue(parm.getCount());
        } else {
            TParm parm = this.getTable("TABLED").getParmValue();
            for (int i = 0; i < parm.getCount(); i++) {
                this.getTable("TABLED").setItem(i, "FLG", "N");

            }
            this.getNumberTextField("D_TOT").setValue(0);
        }
    }

    /**
     * �̵�ʱ��ѡ���¼�
     */
    public void CheckSelect() {
        String checktime = this.getComboBox("CHECKQTY_DATE").getSelectedID();
        if (!checktime.equals("")) {
            callFunction("UI|save|setEnabled", true);
            callFunction("UI|check|setEnabled", false);
            callFunction("UI|query|setEnabled", false);
            callFunction("UI|CONFRIM|setEnabled", true);
            checktime = checktime.substring(0, 4) +
                        checktime.substring(5, 7) +
                        checktime.substring(8, 10) +
                        checktime.substring(11, 13) +
                        checktime.substring(14, 16) +
                        checktime.substring(17, 19);
            TParm parm = new TParm();   
            parm.addData("ACTUAL_CHECKQTY_DATE", checktime);
            parm.addData("DEPT_CODE", this.getValue("DEPT_CODE")); 
            parm.addData("REGION_CODE", DEVTool.getInstance().getArea());
            System.out.println("parm"+parm);
            //�õ����̵�ʱ���µ��̵�����  
            TParm result = DEVTool.getInstance().getCheckData(parm);
            if (result.getCount() <= 0) {
                this.messageBox("û���̵�����!");
                return; 
            }
            //��ʵ���̵㸳Ĭ�Ͽ��ֵ
            for (int i = 0; i < result.getCount(); i++) {
                result.setData("ACTUAL_CHECK_QTY", i,
                               result.getInt("ACTUAL_CHECK_QTY", i));
            }
            result.setCount(result.getCount("DEV_CODE"));
            this.callFunction("UI|TABLE|setParmValue", result);
        } else {
            callFunction("UI|TABLE|removeRowAll");
            callFunction("UI|TABLED|removeRowAll");
            callFunction("UI|save|setEnabled", false);
            callFunction("UI|Dsave|setEnabled", false);
            callFunction("UI|check|setEnabled", true);
            callFunction("UI|query|setEnabled", true);
            callFunction("UI|CONFRIM|setEnabled", false);
            this.clearValue("DEV_CODE;DEV_CHN_DESC;LOC_CODE;");
        } 
       
    }

    /**
     * ������ܷ���ֵ����
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj; 
        String dev_code = parm.getValue("DEV_CODE");
        if (!dev_code.equals("")) {
            getTextField("DEV_CODE").setValue(dev_code); 
        }                                                                    
        String dev_desc = parm.getValue("DEV_CHN_DESC");
        if (!dev_desc.equals("")) {
            getTextField("DEV_CHN_DESC").setValue(dev_desc);
        } 
    }

    /**
     * ���ֵ�ı��¼�
     *
     * @param obj
     * Object
     */
    public boolean onTableMChangeValue(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // �ж����ݸı�
        if (node.getValue().equals(node.getOldValue()))
            return true;
        // Table����
        int column = node.getColumn();
        int row = node.getRow();
        int column_stock = 9;
        int column_dosage = 12;
        if (column == column_stock) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("ʵ���̵���������С��0");
                return true;
            }
            getStockModiQty(row, qty);
        }
        if (column == column_dosage) {
            double qty = TypeTool.getDouble(node.getValue());
            getDosageModiQty(row, qty);
        }
        return false;
    }

//    /**
//     * �������¼�
//     */
//    public void onMTableClicked() {
//
//            int row = tablem.getClickedRow();
//            TParm parm = tablem.getParmValue().getRow(row);
//            if (parm.getBoolean("SEQMAN_FLG")) {
//                String sql =
//                        "SELECT A.DEV_CODE,A.BATCH_SEQ,B.DEV_CHN_DESC,A.DEVSEQ_NO,A.MANSEQ_NO," +
//                        "A.QTY,C.UNIT_CHN_DESC,A.GUAREP_DATE,"
//                        //������� 
//                        +"A.INWAREHOUSE_DATE," 
//                        + "A.DEP_DATE,A.LOC_CODE FROM DEV_STOCKD A,DEV_BASE B,SYS_UNIT C WHERE A.DEV_CODE=B.DEV_CODE(+)"
//                        + 
//                        "AND B.UNIT_CODE=C.UNIT_CODE(+) ";
//                StringBuffer SQL = new StringBuffer();
//                SQL.append(sql);
//                SQL.append("AND A.DEV_CODE='" + parm.getValue("DEV_CODE") +
//                           "' AND A.BATCH_SEQ='" + parm.getInt("BATCH_SEQ") +
//                           "' AND A.INWAREHOUSE_DATE=TO_DATE('" +
//                           parm.getValue("INWAREHOUSE_DATE").substring(0, 10) +
//                           "','yyyy-MM-dd')");
//                SQL.append("ORDER BY A.DEVSEQ_NO");
//                TParm result = new TParm(this.getDBTool().select(SQL.toString()));
//                if (result.getCount() <= 0) {
//                    this.getNumberTextField("D_TOT").setValue(0);
//                    callFunction("UI|TABLED|removeRowAll");
//                    return;
//                }
//                for (int i = 0; i < result.getCount(); i++) {
//                    result.setData("FLG", i, "Y");
//                }
//                result.setCount(result.getCount());
//                this.getNumberTextField("D_TOT").setValue(result.getCount());
//                this.callFunction("UI|TABLED|setParmValue", result);
//            }
//    }

    /**
     * �̵���������
     */
    public void onSave() {
        TParm inparm = Data();
        if (inparm.getCount() < 0) {
            this.messageBox("û����Ҫ��������ݣ�");
            return;
        }
        TParm result = new TParm();  
        if (!this.getValueString("CHECKQTY_DATE").equals("")) {
            // �����̵�����
            result = TIOM_AppServer.executeAction(
                    "action.dev.DEV_StockCheckAction", "onUpdate", inparm);
        }                                                                  
        // �����ж�  
        if (result == null || result.getErrCode() < 0) { 
            this.messageBox("�̵�����¼��ʧ�ܣ�");
            return;
        } else {
            this.messageBox("�̵�����¼��ɹ���");
        }
 
    }

    /**
     * �̵����
     */
    public void onCheck() {
    	//��ȡ���̵�����
        TParm inparm = Data(); 
        if (inparm.getCount("DEV_CODE") <= 0) {
            this.messageBox("û����Ҫ�̵�����ݣ�");
            return;
        }
        TParm result = new TParm();
        if (this.getValueString("CHECKQTY_DATE").equals("")) {
        	System.out.println("�����̵�����");
            result = TIOM_AppServer.executeAction( 
                    "action.dev.DEV_StockCheckAction", "onInsert", inparm);
        }
        // �����ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("�̵����ݱ���ʧ�ܣ�");
            return;
        } else {  
            this.messageBox("�̵����ݱ���ɹ���");
            DEPTSelect();
        } 

    }

    /**
     * �̵�����
     * @return TParm
     */
    public TParm Data() {
    	table.acceptText(); 
        //��ȡҳ��ֵ 
        TParm tablemParm = table.getParmValue();
        TParm parmCheck = new TParm();
        for(int i=0;i<tablemParm.getCount();i++){
        	if(!tablemParm.getBoolean("FLG", i))
        		continue;
        	//FLG;DEV_CODE;DEV_CHN_DESC;SPECIFICATION;QTY;REAL_NUM;OPT_DATE;GUAREP_DATE;RFID
        	parmCheck.addData("DEV_CODE",tablemParm.getData("DEV_CODE", i));
        	parmCheck.addData("DEV_CHN_DESC",tablemParm.getData("DEV_CHN_DESC", i));
        	parmCheck.addData("SPECIFICATION",tablemParm.getData("SPECIFICATION", i));
        	parmCheck.addData("QTY",tablemParm.getData("QTY", i));
        	parmCheck.addData("REAL_NUM",tablemParm.getData("REAL_NUM", i));
        	parmCheck.addData("OPT_DATE",tablemParm.getData("OPT_DATE", i));
        	parmCheck.addData("GUAREP_DATE",tablemParm.getData("GUAREP_DATE", i));
        	parmCheck.addData("RFID",tablemParm.getData("RFID", i));
        }
        TParm parm = new TParm();
        parm.addData("DEPT_CODE", this.getValueString("DEPT_CODE"));
        parm.addData("OPT_USER", Operator.getID()); 
        parm.addData("OPT_TERM", Operator.getIP());   
        parm.addData("REGION_CODE",  Operator.getRegion());
        parm.addData("OPT_DATE", date.format(SystemTool.getInstance().getDate()));
        if (!this.getValueString("CHECKQTY_DATE").equals("")) {      
            String checktime = this.getComboBox("CHECKQTY_DATE").getSelectedID(); ;
            checktime = checktime.substring(0, 4) +
                        checktime.substring(5, 7) +
                        checktime.substring(8, 10) +
                        checktime.substring(11, 13) +
                        checktime.substring(14, 16) +
                        checktime.substring(17, 19); 

            parm.addData("ACTUAL_CHECKQTY_DATE",
                         checktime);
        }
        parm.setCount(1);
        //������ؼ���Ĭ�����ݷ���parmCheck   
        parmCheck.setData("Parm", parm.getData());
        //�����̵�����(������FLG״̬ΪY������) 
        return parmCheck;
    }

    /**
     * ȷ���̵�
     */
    public void confirmCheck() {
        if (!this.checkQty()) {
            return;
        }
        table.acceptText();
        TParm tablemParm = table.getParmValue(); 
        TParm parm = new TParm();
        parm.addData("DEPT_CODE", this.getValueString("DEPT_CODE"));
        parm.addData("USER_ID", Operator.getID());
        parm.addData("USER_IP", Operator.getIP());
        parm.addData("REGION_CODE", DEVTool.getInstance().getArea()); 
        parm.addData("SYSTEMDATE", date.format(SystemTool.getInstance().getDate()));
        parm.setCount(1);
        tablemParm.setData("Parm", parm.getData());
        TParm result = new TParm();
        if (!this.getValueString("CHECKQTY_DATE").equals("")) {
            // �����̵�����
            result = TIOM_AppServer.executeAction(  
                    "action.dev.DEV_StockCheckAction", "onConfrimCheck",
                    tablemParm);
        } 
        // �����ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("���¿��ʧ�ܣ�");
            return;  
        } else {
            this.messageBox("���¿��ɹ���");
        }
    }

    /**
     * ��ѯ����<br>
     *
     */
    public void onQuery() {
        	 TParm result = new TParm();
             if (!CheckData()) {
                 return;
             }    
             //����                       
             String deptcode = this.getValueString("DEPT_CODE");
             String con = "";
             con = con +"AND A.DEPT_CODE='" + deptcode + "'";
             //��治Ϊ0
             if (this.getCheckBox("STOCK_0").isSelected()) {
            	 con = con + "AND A.QTY>0";
             }  
             if (!"".equals(this.getValueString("DEV_CODE"))) {
                 //�豸����   
            	 con = con + "AND A.DEV_CODE =" +
            	 		     "'" +this.getValueString("DEV_CODE")+ "'";
             }
             if (!"".equals(this.getValueString("LOC_CODE"))) {
                 //��ŵص� 
            	 con = con + "AND A.LOC_CODE ='" + this.getValueString("LOC_CODE") +
                 "'";
             }  
             String sql =  
                         " SELECT 'N' AS FLG,A.DEPT_CODE,A.DEV_CODE,D.BATCH_SEQ,D.RFID,B.SEQMAN_FLG," +
                         " B.DEV_CHN_DESC,B.SPECIFICATION,B.DEVKIND_CODE,B.DEVPRO_CODE,"+
                         " A.QTY,C.UNIT_CHN_DESC,A.UNIT_PRICE,A.SCRAP_VALUE,A.GUAREP_DATE," +
                         " A.INWAREHOUSE_DATE,D.OPT_DATE,"+
                         " A.DEP_DATE,A.LOC_CODE FROM DEV_STOCKM A,DEV_BASE B,SYS_UNIT C,DEV_STOCKDD D"+
                         " WHERE A.DEV_CODE=B.DEV_CODE(+)"+
                         " AND A.DEV_CODE = D.DEV_CODE"+
                         " AND A.DEPT_CODE = D.DEPT_CODE"+
                         " AND B.UNIT_CODE=C.UNIT_CODE(+) " +
                          con+  
                         " ORDER BY D.RFID,A.DEV_CODE";
             System.out.println("sql"+sql); 
             result = new TParm(this.getDBTool().select(sql));
             // �жϴ���ֵ 
             if (result == null || result.getCount() <= 0) {
                 callFunction("UI|TABLE|removeRowAll");
                 this.messageBox("û�в�ѯ����");
                 return;
             } 
//             date.addData("FLG", result.getValue("FLG", i));
//             date.addData("DEV_CODE", result.getValue("DEV_CODE", i));
//             date.addData("DEV_CHN_DESC", result.getValue("DEV_CHN_DESC", i)); 
//             date.addData("SPECIFICATION", result.getValue("SPECIFICATION", i));
//             date.addData("QTY",result.getInt("QTY", i));   
//             date.addData("REAL_NUM", result.getValue("REAL_NUM", i));
//             date.addData("RFID", result.getValue("RFID", i)); 
//             date.addData("OPT_DATE",dd.format(result.getTimestamp("OPT_DATE", i)));
//             date.addData("GUAREP_DATE",dd.format(result.getTimestamp("GUAREP_DATE", i)));
             TParm date = new TParm();     
             for (int i = 0; i < result.getCount(); i++) { 
               date.addData("FLG", result.getValue("FLG", i));
               date.addData("DEV_CODE", result.getValue("DEV_CODE", i));
               date.addData("DEV_CHN_DESC", result.getValue("DEV_CHN_DESC", i)); 
               date.addData("SPECIFICATION", result.getValue("SPECIFICATION", i));
               date.addData("QTY",result.getInt("QTY", i));   
               date.addData("REAL_NUM", result.getValue("QTY", i)); 
               date.addData("RFID", result.getValue("RFID", i)); 
               date.addData("OPT_DATE",dd.format(result.getTimestamp("OPT_DATE", i)));
               date.addData("GUAREP_DATE",dd.format(result.getTimestamp("GUAREP_DATE", i)));
//                 date.addData("DEV_CODE", result.getValue("DEV_CODE", i));
//                 date.addData("DEV_CHN_DESC", result.getValue("DEV_CHN_DESC", i)); 
//                 date.addData("BATCH_SEQ", result.getValue("BATCH_SEQ", i));
//                 date.addData("SEQMAN_FLG", result.getValue("SEQMAN_FLG", i));
//                 date.addData("DESCRIPTION", result.getValue("DESCRIPTION", i)); 
//                 date.addData("INWAREHOUSE_DATE",
//                              dd.format(result.getTimestamp("INWAREHOUSE_DATE", i)));
//                 date.addData("GUAREP_DATE",   
//                              dd.format(result.getTimestamp("GUAREP_DATE", i)));
//                 date.addData("DEP_DATE", 
//                              dd.format(result.getTimestamp("DEP_DATE", i)));
//                 date.addData("LOC_CODE", result.getValue("LOC_CODE", i));
//                 date.addData("UNIT_CHN_DESC", 
//                              result.getValue("UNIT_CHN_DESC", i));
//                 date.addData("QTY",
//                              result.getInt("QTY", i));   
//                 date.addData("ACTUAL_CHECK_QTY", "");
//                 date.addData("CHECK_PHASE_QTY", 
//                              "");
//                 date.addData("CHECK_PHASE_AMT",  
//                              "");
//                 date.addData("MODI_QTY", 
//                              "");  
//                 date.addData("MODI_AMT", "");
//                 date.addData("UNIT_PRICE", result.getDouble("UNIT_PRICE", i));
             } 
             System.out.println("date"+date);
             this.callFunction("UI|TABLE|setParmValue", date);
    }
 
    /**
     * �������<br>        
     *
     */
    public void onClear() {
        if (this.getTable("TABLE").getRowCount() > 0) {
            callFunction("UI|TABLE|removeRowAll");
        }   
        this.clearValue(
                "FLG;DEPT_CODE;DEV_CODE;DEV_CHN_DESC;LOC_CODE;CHECKQTY_DATE;");
        callFunction("UI|TABLE|removeRowAll");
    }

    /**
     * �˶Կ��
     * @return boolean
     */
    public boolean checkQty() { 
        TParm inparm = table.getParmValue();
        for (int i = 0; i < inparm.getCount(); i++) {
            //����豸���ϸ����������
            if (inparm.getBoolean("SEQMAN_FLG", i)) {
                int RowCount = 0;
                TParm parm = new TParm();
                parm.addData("HOSP_AREA", DEVTool.getInstance().getArea());
                parm.addData("DEV_CODE", inparm.getRow(i).getValue("DEV_CODE"));
                parm.addData("BATCH_SEQ", inparm.getRow(i).getInt("BATCH_SEQ"));
                parm.addData("DEPT_CODE", this.getValue("DEPT_CODE"));
                parm.addData("INWAREHOUSE_DATE",
                             inparm.getRow(i).getValue("INWAREHOUSE_DATE"));
                TParm result1 = DEVTool.getInstance().onDQuery(parm);
                RowCount = result1.getInt("COUNT", 0);
                if (RowCount != inparm.getInt("ACTUAL_CHECK_QTY", i)) {
                    this.messageBox("��Ź����豸:�豸����" +
                                    inparm.getValue("DEV_CODE", i) +
                                    "|�豸����:" +
                                    inparm.getValue("DEV_CHN_DESC", i) +
                                    "\n �̵�����ʵ�ʿ�治�Եȣ���˶�!");
                    return false;
                }
            }     
            //����̵����������¿������ݲ����
            if (!inparm.getValue("ACTUAL_CHECK_QTY", i).trim().equals("")) {
                TParm parm1 = new TParm();
                parm1.setData("HOSP_AREA", DEVTool.getInstance().getArea());
                parm1.setData("DEV_CODE", inparm.getValue("DEV_CODE", i));
                parm1.setData("BATCH_SEQ", inparm.getValue("BATCH_SEQ", i));
                parm1.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
                String checktime = this.getComboBox("CHECKQTY_DATE").
                                   getSelectedID(); 
                checktime = checktime.substring(0, 4) +
                            checktime.substring(5, 7) +
                            checktime.substring(8, 10) +
                            checktime.substring(11, 13) +
                            checktime.substring(14, 16) +
                            checktime.substring(17, 19);
                parm1.setData("ACTUAL_CHECKQTY_DATE", checktime);
                TParm result2 = DEVStockCheckTool.getInstance().onQuery(parm1);
                if (inparm.getInt("ACTUAL_CHECK_QTY", i) !=
                    result2.getInt("ACTUAL_CHECK_QTY", 0) ||
                    inparm.getInt("MODI_QTY", i) !=
                    result2.getInt("MODI_QTY", 0)) {
                    this.messageBox("�̵���������¿�����ݲ���ȣ�");
                    return false;
                }
            }
        }
        return true;
    }


//    /**
//     * ������Ź���������
//     */
//    public void onAdd() {
//        if (this.getTable("TABLE").getSelectedRow() < 0) {
//            this.messageBox("��ѡ�����ӿ����豸���ݣ�");
//            return;
//        }
//        int row = this.getTable("TABLE").getSelectedRow();
//        TParm inparm = tablem.getParmValue().getRow(row);
//        TParm parm = new TParm();
//        parm.addData("HOSP_AREA", DEVTool.getInstance().getArea());
//        parm.addData("DEV_CODE", inparm.getValue("DEV_CODE"));
//        parm.addData("BATCH_SEQ", inparm.getValue("BATCH_SEQ"));
//        TParm devparm = new TParm(this.getDBTool().select(DEVTool.getInstance().
//                MaxDSeq(parm)));
//        TParm dataParm = DEVTool.getInstance().getDDate(parm);
//        dataParm.setData("HOSP_AREA", dataParm.getValue("HOSP_AREA", 0));
//        dataParm.setData("DEV_CODE", dataParm.getValue("DEV_CODE", 0));
//        dataParm.setData("BATCH_SEQ", dataParm.getValue("BATCH_SEQ", 0));
//        dataParm.setData("DEVSEQ_NO", devparm.getInt("SEQ", 0) + 1);
//        dataParm.setData("DEPT_CODE", dataParm.getValue("DEPT_CODE", 0));
//        dataParm.setData("SETDEV_CODE", dataParm.getValue("SETDEV_CODE", 0));
//        dataParm.setData("QTY", 1);
//        dataParm.setData("UNIT_PRICE", dataParm.getDouble("UNIT_PRICE", 0));
//        dataParm.setData("SCRAP_VALUE", dataParm.getDouble("SCRAP_VALUE", 0));
//        dataParm.setData("MAN_DATE",
//                         date1.format(dataParm.getTimestamp("MAN_DATE", 0)));
//        dataParm.setData("GUAREP_DATE",
//                         date1.format(dataParm.getTimestamp("GUAREP_DATE", 0)));
//        dataParm.setData("DEP_DATE",
//                         date1.format(dataParm.getTimestamp("DEP_DATE", 0)));
//        dataParm.setData("MANSEQ_NO", dataParm.getValue("MANSEQ_NO", 0));
//        dataParm.setData("CARE_USER", dataParm.getValue("CARE_USER", 0));
//        dataParm.setData("USE_USER", dataParm.getValue("USE_USER", 0));
//        dataParm.setData("LOC_CODE", dataParm.getValue("LOC_CODE", 0));
//        dataParm.setData("BATCH_CODE", dataParm.getValue("BATCH_CODE", 0));
//        dataParm.setData("SUP_CODE", dataParm.getValue("SUP_CODE", 0));
//        dataParm.setData("INWAREHOUSE_DATE",
//                         date1.format(dataParm.getTimestamp("INWAREHOUSE_DATE",
//                0)));
//        dataParm.setData("OPT_USER", Operator.getID());
//        dataParm.setData("OPT_DATE",
//                         date.format(SystemTool.getInstance().getDate()));
//        dataParm.setData("OPT_TERM", Operator.getIP());
//        TParm result = DEVTool.getInstance().onDInsert(dataParm);
//        if (result.getErrCode() < 0) {
//            this.messageBox("������ſ������ʧ�ܣ�");
//            return;
//        } else {
//            this.messageBox("������ſ�����ݳɹ���");
//            onMTableClicked();
//        }
//
//    }


    /**
     * ��ѡѡ���¼�
     */
    public void onCheckBoxValue(Object obj) {
        TTable table = (TTable) obj;
        int col = table.getSelectedColumn();
        String columnName = this.getTable("TABLED").getDataStoreColumnName(col);
        TParm parm = this.getTable("TABLED").getParmValue();
        int rowcount = 0;
        if ("FLG".equals(columnName)) {
            for (int i = 0; i < parm.getCount(); i++) {
                if (parm.getBoolean("FLG", i)) {
                    rowcount += 1;
                }
            }
        }
        if (rowcount == parm.getCount()) {
            this.getCheckBox("all").setSelected(true);
        } else {
            this.getCheckBox("all").setSelected(false);
        }
        this.getNumberTextField("D_TOT").setValue(rowcount);
    }

    /**
     * �Ҽ��¼�
     */
    public void onTableRightClick(Object obj) {
        // ֵ�ı�ĵ�Ԫ��
        int column = table.getSelectedColumn();
        TParm parm = table.getParmValue();
        if (parm.getCount() < 0) {
            return; 
        }
        if (column == 8) { 
        	table.setPopupMenuSyntax("���ƿ��,onCopyData"); 
            return;
        } else {
        	table.setPopupMenuSyntax("");
        }
    }
 
    /**
     * �����¼�
     */
    public void onCopyData() { 
        TParm parm = table.getParmValue();
        for (int i = 0; i < parm.getCount(); i++) {
            parm.setData("ACTUAL_CHECK_QTY", i, parm.getInt("QTY", i));
        }
        parm.setCount(parm.getCount());
        this.callFunction("UI|TABLE|setParmValue", parm);
    }

    /**
     * ���ݼ��
     *
     * @return
     */
    private boolean CheckData() {
        if ("".equals(this.getValueString("DEPT_CODE"))) {
            this.messageBox("�̵㲿�Ŵ�����Ϊ�հ�");
            return false;
        }
        return true;
    }

    /**
     * ����Excel
     */
    public void onExport() {
        if (this.getTable("TABLE").getRowCount() <= 0) {
            this.messageBox("�޵������ݣ�");
            return;
        }
        TParm aaa = this.getTable("TABLE").getParmValue();
        if (this.getTable("TABLE").getRowCount() > 0) {
            ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
                    "�豸����̵�ͳ�Ʊ���");
        }
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
    	TTabbedPane tabPane = (TTabbedPane) this.callFunction("UI|TablePane|getThis");
        //0��ʾ��Ź���,1��ʾ����Ź���
        TParm actionParm = null;
        int selType = tabPane.getSelectedIndex();
        // 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
//        if (selType == 0){
//     	   
//        } 
//        else{
//     	   
//        }	 
        if ("".equals(getValueString("DEPT_CODE"))) {
            this.messageBox("��ѡ���̵㲿��");
            return;
        }
        TParm parm = new TParm();
        parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
        Object result = openWindow("%ROOT%\\config\\dev\\DEV_PRINT.x",
                                   parm);
    }

    /**
     * ��������̲�
     *
     * @return
     */
    private void getStockModiQty(int row, double qty) {
    	table.acceptText();
        double rate = table.getParmValue().getDouble("QTY", row);
        double price = table.getParmValue().getDouble("UNIT_PRICE", row);
        double mod_qty = table.getItemDouble(row,
                                              "MODI_QTY");
        table.setItem(row, "CHECK_PHASE_QTY",
                       qty + mod_qty - rate);
        table.setItem(row, "CHECK_PHASE_AMT",
                       ff.format((qty + mod_qty - rate) * price));

    }

    /**
     * ��������̲�
     *
     * @return
     */
    private void getDosageModiQty(int row, double qty) {
        table.acceptText();
        double rate = table.getParmValue().getDouble("QTY", row);
        double price = table.getParmValue().getDouble("UNIT_PRICE", row);
        double check_qty = table.getItemDouble(row,
                                                "ACTUAL_CHECK_QTY");
        table.setItem(row, "CHECK_PHASE_QTY",
                       qty + check_qty - rate);
        table.setItem(row, "CHECK_PHASE_AMT",
                       ff.format((qty + check_qty - rate) * price));
        table.setItem(row, "MODI_AMT",
                       ff.format(qty * price));
    }
 
    //fux need modify
    /** 
     * �豸ʵʱ��ѯ��ʼ(��������)
     *
     * @return 
     */
    public  void   onStart(){
		if (table.getRowCount()<=0) {
			messageBox("���Ȳ�ѯ����");
			return;
			
		}
		// 1.У�� ���ⵥ��
		// 2.RFID���ݲɼ��������ṩWebService������
		// connect(string readerId)�� 
	    TIOM_AppServer.executeAction("action.device.RFIDAction",
				"connect", tparm);
    }  
    
    /** 
     * �豸ʵʱ��ѯ��ʼ(��������)
     *
     * @return 
     */
    public  void   onEnd(){
    	
    }  
    
    
    
    
    
    
    
    
    

    /**
     * �õ���ؼ�
     * @param tagName String
     * @return TTable
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ��ı��ؼ�
     * @param tagName String
     * @return TTextField
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * getDBTool
     * ���ݿ⹤��ʵ��
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * �õ�TNumberTextField����
     * @param tagName String
     * @return TNumberTextField
     */
    private TNumberTextField getNumberTextField(String tagName) {
        return (TNumberTextField) getComponent(tagName);
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
     * �õ�TCheckBox����
     * @param tagName String 
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * �õ�TTextFormat����
     * @param tagName String
     * @return TCheckBox
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
}
