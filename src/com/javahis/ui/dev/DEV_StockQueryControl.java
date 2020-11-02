package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.jdo.TJDODBTool;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.javahis.util.ExportExcelUtil;
import java.util.Date;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import jdo.dev.DEVTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TCheckBox;

public class DEV_StockQueryControl extends TControl {
    private TTable table;
    public void onInit() {
        super.init();
        callFunction("UI|DEV_CODE|setPopupMenuParameter", "aaa",
        "%ROOT%\\config\\sys\\DEVBASEPopupUI.x");
        //textfield���ܻش�ֵ
        callFunction("UI|DEV_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        initpage();
    }
    /**
     * ��ʼ���ؼ�
     */
    public void initpage() {
        //��ʼ���û�
        String deptCode = Operator.getDept();   
        this.setValue("DEPT_CODE", deptCode);
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
     * ��ѯ����<br>
     *
     */ 
    public void onQuery() {
        if(this.getValueString("DEPT_CODE").equals("")){
            this.messageBox("�������ѯ���ţ�");
            return;
        }    
        SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
        DecimalFormat ff = new DecimalFormat("######0.00");
        //����
        String deptcode = this.getValueString("DEPT_CODE");
        //�豸����
        String devcode = this.getValueString("DEV_CODE");
        //�豸����
        String devkind = this.getValueString("DEVKIND_CODE");
        //�豸����
        String devtype = this.getValueString("DEVPRO_CODE");
        //�ص�
        String loccode = this.getValueString("LOC_CODE");
        
        //�豸����;�豸����;����;���;���;�豸����DEVKIND_CODE;�豸����DEVPRO_CODE;
        //��������;�����;��λ;���Ʋ���ֵ;����;�������;������ֹ����;�۾���ֹ����;��ŵص�,LOC_CODE;������;ʹ����;���ʼ�ֵ�ϼ�;
        //�����۾ɺϼ�;���ʲ��ϼ�
        //DEV_CODE;DEV_CHN_DESC;BATCH_SEQ;SEQMAN_FLG;DESCRIPTION;DEVKIND_CODE;DEVPRO_CODE;
        //MAN_DESC;QTY;UNIT_DESC;UNIT_PRICE;BATCH_CODE;INWAREHOUSE_DATE;GUAREP_DATE;
        //DEP_DATE;LOC_CODE;CARE_USER;USE_USER;TOT;DEP_TOT;BALANCE_TOT
        //���
        String description = this.getValueString("DESCRIPTION");
        //���Ʋ���ֵ
        String price = this.getValueString("UNIT_PRICE");
        //�����
        String qty = this.getValueString("QTY");
        //��������
        String mandesc = this.getValueString("MAN_DESC");
        //��ֵ�ܼ�
        double totvalue = 0.00;
        //�۾��ܼ�
        double depvalue = 0.00;
        //���
        double balancevalue = 0.00;
        String sql =
                "SELECT A.DEPT_CODE,A.DEV_CODE,B.DEV_CHN_DESC,B.SPECIFICATION,B.DEVKIND_CODE," +
                " B.DEVPRO_CODE,E.MAN_CHN_DESC,B.SEQMAN_FLG,A.BATCH_SEQ,A.QTY,"
                + " C.UNIT_CHN_DESC,A.UNIT_PRICE,A.SCRAP_VALUE,A.GUAREP_DATE,A.INWAREHOUSE_DATE," 
                + " D.USER_NAME CARE_USER,D.USER_NAME USE_USER, A.DEP_DATE,A.LOC_CODE "
                + " FROM DEV_STOCKD A,DEV_BASE B,SYS_UNIT C,SYS_OPERATOR D,SYS_MANUFACTURER E "  
                + " WHERE A.DEV_CODE=B.DEV_CODE(+)"  
                + " AND B.UNIT_CODE=C.UNIT_CODE(+) " 
                + " AND A.CARE_USER=D.USER_ID(+)" 
                + " AND A.USE_USER=D.USER_ID(+) AND B.MAN_CODE=E.MAN_CODE(+) " 
                + " AND A.REGION_CODE='"+Operator.getRegion()+"'";
        StringBuffer SQL = new StringBuffer(); 
        SQL.append(sql);
        //����
        if (!deptcode.equals("")) {
            SQL.append("AND A.DEPT_CODE='" + deptcode + "'");
        }
        //�豸����
        if (!devtype.equals("")) {
            SQL.append("AND B.DEVPRO_CODE='" + devtype + "'");
        }
        //�豸����
        if (!devcode.equals("")) {
            SQL.append("AND A.DEV_CODE='" + devcode + "'");
        }
        //�豸����
        if (!devkind.equals("")) {
            SQL.append("AND B.DEVKIND_CODE='" + devkind + "'");
        }
        //�ص�
        if (!loccode.equals("")) {
            SQL.append("AND A.LOC_CODE='" + loccode + "'");
        }
        // fux midify start
        //���
        if(!description.equals("")){
        	SQL.append("AND��B.DESCRIPTION='"+description+"'");
        }
        //���Ʋ���ֵ
        if(!price.equals("")){   
        	SQL.append("AND A.UNIT_PRICE like '%"+price+"%'");
        }
        //�����
        if(!qty.equals("")){
        	SQL.append("AND A.QTY like '%"+qty+"%'");
        }
        //��������
        if(!mandesc.equals("")){
        	SQL.append("AND E.MAN_DESC like '%"+mandesc+"%'");
        }
        // fux modify end
        //��治Ϊ0
           if(this.getCheckBox("STOCK_0").isSelected()){
               SQL.append("AND A.QTY>0");
           } 
           System.out.println("SQL"+SQL); 
        TParm result = new TParm(this.getDBTool().select(SQL.toString()));
        // �жϴ���ֵ
        if (result == null || result.getCount() <= 0) {
            callFunction("UI|TABLE|removeRowAll");
            this.messageBox("û�в�ѯ����");
            return;
        }
        TParm date = new TParm();
        for (int i = 0; i < result.getCount(); i++) {
            date.addData("DEPT_CODE", result.getValue("DEPT_CODE", i));
            date.addData("DEV_CODE", result.getValue("DEV_CODE", i));
            date.addData("DEV_CHN_DESC", result.getValue("DEV_CHN_DESC", i));
            date.addData("BATCH_SEQ", result.getInt("BATCH_SEQ", i));
            date.addData("SEQMAN_FLG", result.getValue("SEQMAN_FLG", i));
            date.addData("DESCRIPTION", result.getValue("DESCRIPTION", i));
            date.addData("MAN_DESC", result.getValue("MAN_DESC", i));
            date.addData("QTY", result.getInt("QTY", i));
            date.addData("DEVKIND_CODE", result.getValue("DEVKIND_CODE", i));
            date.addData("DEVPRO_CODE", result.getValue("DEVPRO_CODE", i));
            date.addData("UNIT_DESC", result.getValue("UNIT_DESC", i));
            date.addData("UNIT_PRICE",
                         ff.format(result.getDouble("UNIT_PRICE", i)));
            date.addData("SCRAP_VALUE",
                         ff.format(result.getDouble("SCRAP_VALUE", i)));
            date.addData("INWAREHOUSE_DATE",
                         dd.format(result.getTimestamp("INWAREHOUSE_DATE", i)));
            date.addData("GUAREP_DATE",
                         dd.format(result.getTimestamp("GUAREP_DATE", i)));
            date.addData("DEP_DATE",
                         dd.format(result.getTimestamp("DEP_DATE", i)));
            date.addData("LOC_CODE", result.getValue("LOC_CODE", i));
            date.addData("CARE_USER", result.getValue("CARE_USER", i));
            date.addData("USE_USER", result.getValue("USE_USER", i));
            double S_tot=result.getDouble("UNIT_PRICE", i) *
                                   result.getInt("QTY", i);
            double S_deptot=result.getDouble("SCRAP_VALUE", i) *
                                   result.getInt("QTY", i);
            date.addData("TOT",
                         ff.format(S_tot));
            date.addData("DEP_TOT",
                         ff.format(S_deptot));
            date.addData("BALANCE_TOT",
                         ff.format(S_tot-S_deptot));
            totvalue += result.getDouble("UNIT_PRICE", i) *
                    result.getInt("QTY", i);
            depvalue += result.getDouble("SCRAP_VALUE", i) *
                    result.getInt("QTY", i);
        }
        balancevalue=totvalue-depvalue;
        this.getNumberTextField("TOT_VALUE").setValue( ff.format(totvalue));
        this.getNumberTextField("DEP_VALUE").setValue( ff.format(depvalue));
        this.getNumberTextField("NUMBER").setValue(String.valueOf(result.getCount()));
        this.getNumberTextField("BALANCE_VALUE").setValue( ff.format(balancevalue));
        this.callFunction("UI|TABLE|setParmValue", date);
    }
    /**
    * �������¼� 
    */
   public void onTableClicked() {
       int row =  this.getTable("TABLE").getClickedRow();
       TParm parm = this.getTable("TABLE").getParmValue().getRow(row);
       if (parm.getBoolean("SEQMAN_FLG")) {
           String sql = 
                   "SELECT A.DEV_CODE,A.BATCH_SEQ,B.DEV_CHN_DESC,A.DEVSEQ_NO,A.MANSEQ_NO,A.HOSP_AREA,"+ 
                   "A.QTY,C.UNIT_CHN_DESC,A.GUAREP_DATE,A.INWAREHOUSE_DATE,"+
                   "A.DEP_DATE,A.LOC_CODE FROM DEV_STOCKD A,DEV_BASE B,SYS_UNIT C WHERE A.DEV_CODE=B.DEV_CODE(+)"+
                   "AND B.UNIT_CODE=C.UNIT_CODE(+) AND A.REGION_CODE='" +Operator.getRegion() + "'";
           StringBuffer SQL = new StringBuffer(); 
           SQL.append(sql);
           SQL.append("AND A.DEV_CODE='" + parm.getValue("DEV_CODE") +
                      "' AND A.BATCH_SEQ='" + parm.getInt("BATCH_SEQ") +
                      "' AND A.INWAREHOUSE_DATE=TO_DATE('" +
                      parm.getValue("INWAREHOUSE_DATE").substring(0, 10) +
                      "','yyyy/MM/dd')");
           SQL.append("ORDER BY A.DEVSEQ_NO");
//           System.out.println("----------------" + SQL.toString());
           TParm result = new TParm(this.getDBTool().select(SQL.toString()));
           if (result.getCount() <= 0) {
               this.getNumberTextField("D_TOT").setValue(0);
               callFunction("UI|TABLED|removeRowAll");
               return;
           }
           this.getNumberTextField("D_TOT").setValue(result.getCount());
           this.callFunction("UI|TABLED|setParmValue", result);
       } else {
           callFunction("UI|TABLED|removeRowAll");
           this.getNumberTextField("D_TOT").setValue(0);
       }
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
                "DEPT_CODE;DEVPRO_CODE;DEV_CODE;DEVKIND_CODE;LOC_CODE;DEV_CHN_DESC;" +
                "DESCRIPTION;MAN_DESC;QTY;UNIT_PRICE");
        callFunction("UI|TABLE|removeRowAll");
    }

    /**
     * ����Excel
     */
    public void onExport() {
        if (this.getTable("TABLE").getRowCount() > 0) {
            ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
                    "�豸������ͳ�Ʊ���");
        }
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        if (this.getTable("TABLE").getRowCount() <= 0) {
            this.messageBox("û��Ҫ��ӡ������");
            return;
        }
        TParm prtParm = new TParm();
        //��ͷ
        prtParm.setData("TITLE","TEXT","�豸������ͳ�Ʊ���");
        //����
        prtParm.setData("PRINT_DATE","TEXT","��ӡ���ڣ�" +
                        StringTool.getString(StringTool.getTimestamp(new Date()),
                                             "yyyy��MM��dd��"));
        String sql="SELECT * FROM SYS_DEPT WHERE DEPT_CODE='"+this.getValue("DEPT_CODE")+"'";
        TParm result = new TParm(this.getDBTool().select(sql));
        //����
        prtParm.setData("DEPT_CODE","TEXT", "ͳ�Ʋ��ţ�" +result.getValue("DEPT_DESC",0));
        //�Ʋ��ܼ�
        prtParm.setData("TOT","TEXT", "�Ʋ��ܼƣ�" +this.getValueDouble("TOT_VALUE"));
        //�۾��ܼ�
        prtParm.setData("DET_TOT","TEXT", "�۾��ܼƣ�" +this.getValueDouble("DEP_VALUE"));
        //�ϼƱ���
        prtParm.setData("NUMBER","TEXT", "�ϼƱ�����" +this.getValueInt("NUMBER"));
        //����ܼ�
        prtParm.setData("BALANCE_VALUE","TEXT", "����ܼƣ�" +this.getValueDouble("BALANCE_VALUE"));
        TParm parm = this.getTable("TABLE").getParmValue();
        TParm prtTableParm=new TParm();
        for(int i=0;i<parm.getCount("DEV_CODE");i++){
            prtTableParm.addData("DEV_CHN_DESC",parm.getRow(i).getValue("DEV_CHN_DESC"));
            prtTableParm.addData("DESCRIPTION",parm.getRow(i).getValue("DESCRIPTION"));
            prtTableParm.addData("QTY",parm.getRow(i).getValue("QTY"));
            prtTableParm.addData("UNIT_DESC",parm.getRow(i).getValue("UNIT_DESC"));
            prtTableParm.addData("UNIT_PRICE",parm.getRow(i).getValue("UNIT_PRICE"));
            prtTableParm.addData("SCRAP_VALUE",parm.getRow(i).getValue("SCRAP_VALUE"));
            prtTableParm.addData("LOC_CODE",parm.getRow(i).getValue("LOC_CODE"));
            prtTableParm.addData("TOT",parm.getRow(i).getValue("TOT"));
            prtTableParm.addData("DEP_TOT",parm.getRow(i).getValue("DEP_TOT"));
            prtTableParm.addData("BALANCE_TOT",parm.getRow(i).getValue("BALANCE_TOT"));
        }
        prtTableParm.setCount(prtTableParm.getCount("DEV_CHN_DESC"));
        prtTableParm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
        prtTableParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        prtTableParm.addData("SYSTEM", "COLUMNS", "QTY");
        prtTableParm.addData("SYSTEM", "COLUMNS", "UNIT_DESC");
        prtTableParm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "SCRAP_VALUE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "LOC_CODE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "TOT");
        prtTableParm.addData("SYSTEM", "COLUMNS", "DEP_TOT");
        prtTableParm.addData("SYSTEM", "COLUMNS", "BALANCE_TOT");
        prtParm.setData("prtTABLE", prtTableParm.getData());
        //��β
        prtParm.setData("USER","TEXT", "�Ʊ��ˣ�" + Operator.getName());
        this.openPrintWindow("%ROOT%\\config\\prt\\DEV\\DEV_QUERYPRINT.jhw",
                             prtParm);
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
     * �õ�TNumberTextField����
     * @param tagName String
     * @return TNumberTextField
     */
    private TNumberTextField getNumberTextField(String tagName) {
        return (TNumberTextField) getComponent(tagName);
    }
    /**
     * �õ�TTextFormat����
     * @param tagName String
     * @return TCheckBox
     */
    private TTextFormat getTextFormat (String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
    /**
    * �õ�TCheckBox����
    * @param tagName String
    * @return TCheckBox
    */
   private TCheckBox getCheckBox(String tagName) {
       return (TCheckBox) getComponent(tagName);
   }


}
