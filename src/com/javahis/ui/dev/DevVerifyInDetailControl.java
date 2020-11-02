package com.javahis.ui.dev;
import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.jdo.TJDODBTool;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import com.javahis.util.ExportExcelUtil;
import java.util.Date;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.dev.DEVTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TCheckBox;

/**
 * <p>Title: �豸�����ϸ��ѯ</p>
 * 
 * <p>Description:��ѯ</p>
 * 
 * <p>Copyright: Copyright (c) 20130412</p>
 * 
 * <p>Company: BLUECORE </p>
 *  
 * @author  fux
 * 
 * @version 4.0
 */             

public class DevVerifyInDetailControl extends TControl {
	    private TTable table;
	    private static String TABLE = "TABLE";
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
	    	String now = StringTool.getString(SystemTool.getInstance().getDate(),
			"yyyyMMdd");
	        this.setValue("S_DATE", StringTool.getTimestamp(now ,
			"yyyyMMdd"));// ��ʼʱ��
	        this.setValue("E_DATE", StringTool.getTimestamp(now ,
			"yyyyMMdd"));// ����ʱ��   
	        String ID = Operator.getID();
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
	     * ��ס��񲻿ɱ༭��λ
	     */
	    public void setTableLock(){
//	        ((TTable)getComponent("TABLE")).setLockColumns("3,4,5,6,8,"+
//	                                                       "9,10,12,16,17,18,"+
//	                                                       "19,20,21,22,"+
//	                                                       "24,25,26,27,28");
	    }
	    
	    
	    /**
	     * ��ѯ����<br>
	     *
	     */

	    public void onQuery() {
	  //��ѯ��������ⵥ��INWAREHOUSE_NO��������ڣ�EXWAREHOUSE_DATE between S_DATE and E_DATE��
	  //�豸����DEVKIND_CODE�����DESCRIPTION��
	  //��������MAN_DESC��������INWAREHOUSE_DEPT���豸����DEV_CODE��
	  //�������QTY�����Ʋ���ֵUNIT_PRICE	    	
	        SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
	        DecimalFormat ff = new DecimalFormat("######0.00");
	        //��ѯ��ʼ����
	        String s_date = this.getValueString("S_DATE");
	        //��ѯ��������
	        String e_date = this.getValueString("E_DATE");
	        //��ⵥ��
	        String inwarehouse_no = this.getValueString("INWAREHOUSE_NO");
	        //�������
	        String exwarehouse_date = this.getValueString("INWAREHOUSE_DATE");
	        //������
	        String inwarehouse_dept = this.getValueString("DEPT_CODE");
	        //�豸����
	        String devcode = this.getValueString("DEV_CODE");
	        //�豸����
	        String devkind = this.getValueString("DEVKIND_CODE");
	        //���
	        String description = this.getValueString("DESCRIPTION");
	        //���Ʋ���ֵ
	        String price = this.getValueString("UNIT_PRICE");
	        //�������
	        String qty = this.getValueString("QTY");
	        //��������
	        String mandesc = this.getValueString("MAN_DESC");
	        String sql =
	    		  " SELECT DISTINCT 'N' AS FLG,A.INWAREHOUSE_NO,C.INWAREHOUSE_DEPT," +
	    		  " A.UNIT_PRICE*A.QTY AS TOT_PRICE, " +
	    		  " TO_CHAR(C.INWAREHOUSE_DATE,'YYYYMMDD') INWAREHOUSE_DATE,B.DEVPRO_CODE, " +
	    		  " A.DEV_CODE, B.DEV_CHN_DESC, B.DESCRIPTION, B.MAN_CODE, D.MAN_CHN_DESC, " +
	    		  " A.QTY, 0 SUM_QTY, 0 RECEIPT_QTY, B.UNIT_CODE, A.UNIT_PRICE, " +
	    		  " TO_CHAR(A.MAN_DATE,'YYYYMMDD') MAN_DATE, A.SCRAP_VALUE, " +
	    		  " TO_CHAR(A.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE, B.USE_DEADLINE, " +
	    		  " TO_CHAR(A.DEP_DATE,'YYYYMMDD') DEP_DATE, B.MAN_NATION, " +
	    		  " B.SEQMAN_FLG, B.MEASURE_FLG, B.BENEFIT_FLG, " +
	    		  " A.FILES_WAY, A.SEQ_NO, A.BATCH_SEQ,B.SPECIFICATION" +
	    		  " FROM DEV_INWAREHOUSED A, DEV_BASE B ,DEV_INWAREHOUSEM C ,SYS_MANUFACTURER D " +
	    		  " WHERE  A.DEV_CODE=B.DEV_CODE " + 
	    		  " AND A.INWAREHOUSE_NO = C.INWAREHOUSE_NO " +
	    		  " AND  B.MAN_CODE  = D.MAN_CODE ";
	        StringBuffer SQL = new StringBuffer();
	        //System.out.println("sql===="+sql);
	        SQL.append(sql);
	           
	        //��ⵥ��
	        if ( !inwarehouse_no.equals("")){
	            SQL.append(" AND A.INWAREHOUSE_NO='" + inwarehouse_no + "'");
	        }
	        //��ѯ��ʼ����
	        if ( !s_date.equals("")) {
	        	s_date = s_date.substring(0, 19) ;
	        	SQL.append(" AND TO_CHAR(INWAREHOUSE_DATE,'YYYY-MM-DD ') >= '"+s_date+"'") ;
	        }
	        //��ѯ��������
             if ( !e_date.equals("")) {
            	e_date = e_date.substring(0, 19) ;
 	    		SQL.append(" AND TO_CHAR(INWAREHOUSE_DATE,'YYYY-MM-DD  ') <= '"+e_date+"'") ;
	        }
	        //������
	        if (!inwarehouse_dept.equals("")) {
	            SQL.append(" AND C.INWAREHOUSE_DEPT='" + inwarehouse_dept + "'");
	        }
	        //�豸����
	        if (!devcode.equals("")) {
	            SQL.append(" AND A.DEV_CODE='" + devcode + "'");
	        }
	        //�豸����
	        if (!devkind.equals("")) {
	            SQL.append(" AND B.DEVKIND_CODE='" + devkind + "'");
	        }
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
	        	SQL.append("AND E.MAN_CHN_DESC like '"+mandesc+"'");
	        } 
	        System.out.println("SQl===="+SQL);
	        //TParm result1 = new TParm(TJDODBTool.getInstance().select(SQL.toString()));
	        TParm result = new TParm(this.getDBTool().select(SQL.toString()));
	        //System.out.println("result===="+result);
	        // �жϴ���ֵ
	        if (result == null || result.getCount() <= 0) {
	            callFunction("UI|TABLE|removeRowAll");
	            this.messageBox("û�в�ѯ����");
	            return;          
	        }
	        this.callFunction("UI|TABLE|setParmValue", result);
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
	                "INWAREHOUSE_NO;DEPT_CODE;DEV_CODE;DEVKIND_CODE;S_DATE;" +
	                "E_DATE;DEV_CHN_DESC;DESCRIPTION;MAN_DESC;QTY;UNIT_PRICE");
	        callFunction("UI|TABLE|removeRowAll");
	    }

	    /**
	     * ����Excel
	     */
	    public void onExport() {
	        if (this.getTable("TABLE").getRowCount() > 0) {
	            ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
	                    "�豸���ϸ��ͳ�Ʊ���");
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
	        prtParm.setData("TITLE","TEXT","�豸���ϸ��ͳ�Ʊ���");
	        //����
	        prtParm.setData("PRINT_DATE","TEXT","��ӡ���ڣ�" +
	                        StringTool.getString(StringTool.getTimestamp(new Date()),
	                                             "yyyy��MM��dd��"));
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
	        this.openPrintWindow("%ROOT%\\config\\prt\\DEV\\DEV_VerifyInDetail.jhw",
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
