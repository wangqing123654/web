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
 * <p>Title: �豸������ϸ��ѯ</p>
 * 
 * <p>Description:��ѯ</p>
 * 
 * <p>Copyright: Copyright (c) 20120912</p>
 * 
 * <p>Company: BLUECORE </p>
 *     
 * @author  fux
 * 
 * @version 4.0  
 */

public class DevVerifyOutDetailControl extends TControl {
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
	    	
//	����Ź���		  
//	    	  " SELECT B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, 0 DEVSEQ_NO, " +
//			  " B.DEV_CHN_DESC, B.DESCRIPTION, '' SETDEV_CODE , A.QTY STOREQTY, " +
//			  " B.UNIT_CODE, B.UNIT_PRICE, '' CARE_USER, '' USE_USER, '' LOC_CODE, " +
//			  " A.SCRAP_VALUE, TO_CHAR(A.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE, TO_CHAR(A.DEP_DATE,'YYYYMMDD') DEP_DATE, A.DEPT_CODE, TO_CHAR(A.MAN_DATE,'YYYYMMDD') MAN_DATE,B.DEV_PYCODE,A.BATCH_CODE,A.SUP_CODE " +
//			  " FROM DEV_STOCKM A,DEV_BASE B " +
//			  " WHERE B.SEQMAN_FLG='N' " +
//			  " <DEV_CODE> " +
//			  " <DEVSEQ_NO> " +//ADD BY LIUC 20070917
//			  " <DEVPRO_CODE> " +
//			  " AND A.HOSP_AREA=B.HOSP_AREA " +
//			  " AND A.DEV_CODE=B.DEV_CODE " +
//			  " AND A.DEPT_CODE='<DEPT_CODE>'" +
//			  " ORDER BY B.DEV_CODE, A.BATCH_SEQ";//ADD BY LIUC 20070816
	    	
//	����Ź���		  
//	    	  " SELECT B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, A.DEVSEQ_NO DEVSEQ_NO, " +
//			  " B.DEV_CHN_DESC, B.DESCRIPTION, A.SETDEV_CODE , A.QTY STOREQTY, " +
//			  " B.UNIT_CODE, A.UNIT_PRICE, A.CARE_USER, A.USE_USER, A.LOC_CODE, " +
//			  " A.SCRAP_VALUE, TO_CHAR(A.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE, TO_CHAR(A.DEP_DATE,'YYYYMMDD') DEP_DATE, A.DEPT_CODE, TO_CHAR(A.MAN_DATE,'YYYYMMDD') MAN_DATE,B.DEV_PYCODE,A.BATCH_CODE,A.SUP_CODE " +
//			  " FROM DEV_STOCKD A,DEV_BASE B " +
//			  " WHERE B.SEQMAN_FLG='Y' " +
//			  " <DEV_CODE> " +
////			  " <DEVSEQ_NO> " +//ADD BY LIUC 20070917
//			  " <DEVPRO_CODE> " +
//			  " AND A.HOSP_AREA=B.HOSP_AREA " +
//			  " AND A.DEV_CODE=B.DEV_CODE " +
//			  " AND A.DEPT_CODE='<DEPT_CODE>'" +
//			  " ORDER BY B.DEV_CODE, A.BATCH_SEQ, A.DEVSEQ_NO";//add by liuc 20070816
	    	
//			  " SELECT B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, A.DEVSEQ_NO, " +
//			  " B.DEV_CHN_DESC, B.DESCRIPTION, C.SETDEV_CODE, A.QTY, C.QTY STOREQTY, " +
//			  " B.UNIT_CODE, C.UNIT_PRICE, A.CARE_USER, A.USE_USER, A.LOC_CODE, " +
//			  " C.SCRAP_VALUE, TO_CHAR(C.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE, TO_CHAR(C.DEP_DATE,'YYYYMMDD') DEP_DATE,TO_CHAR(C.MAN_DATE,'YYYYMMDD') MAN_DATE, A.SEQ_NO, A.REMARK1, A.REMARK2,A.BATCH_CODE,A.SUP_CODE " +//MOD BY LIUC 200700918
//			  " FROM DEV_EXWAREHOUSEM M, DEV_EXWAREHOUSED A, DEV_BASE B, DEV_STOCKD C  " +
//			  " WHERE M.HOSP_AREA = '<HOSP_AREA>' " +
//			  " AND M.EXWAREHOUSE_NO='<EXWAREHOUSE_NO>' " +
//			  " AND B.SEQMAN_FLG='Y' " +
//			  " AND A.HOSP_AREA=M.HOSP_AREA " +
//			  " AND A.EXWAREHOUSE_NO = M.EXWAREHOUSE_NO " +
//			  " AND B.HOSP_AREA=A.HOSP_AREA " +
//			  " AND A.HOSP_AREA=C.HOSP_AREA " +
//			  " AND B.DEV_CODE=A.DEV_CODE " +
//			  " AND A.DEV_CODE=C.DEV_CODE " +
//			  " AND A.BATCH_SEQ=C.BATCH_SEQ " +
//			  " AND A.DEVSEQ_NO=C.DEVSEQ_NO " +
//			  " AND C.DEPT_CODE=M.INWAREHOUSE_DEPT " +
//			  " UNION " +
//			  " SELECT B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, 0 DEVSEQ_NO, " +
//			  " B.DEV_CHN_DESC, B.DESCRIPTION, '' SETDEV_CODE, A.QTY, C.QTY STOREQTY, " +
//			  " B.UNIT_CODE, C.UNIT_PRICE, '' CARE_USER, '' USE_USER, '' LOC_CODE, " +
//			  " C.SCRAP_VALUE, TO_CHAR(C.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE, TO_CHAR(C.DEP_DATE,'YYYYMMDD') DEP_DATE,TO_CHAR(C.MAN_DATE,'YYYYMMDD') MAN_DATE, A.SEQ_NO, A.REMARK1, A.REMARK2,A.BATCH_CODE,A.SUP_CODE " +//MOD BY LIUC 200700918
//			  " FROM DEV_EXWAREHOUSEM M, DEV_EXWAREHOUSED A,DEV_BASE B, DEV_STOCKM C " +
//			  " WHERE M.HOSP_AREA = '<HOSP_AREA>' " +
//			  " AND M.EXWAREHOUSE_NO='<EXWAREHOUSE_NO>' " +
//			  " AND B.SEQMAN_FLG='N' " +
//			  " AND A.HOSP_AREA=M.HOSP_AREA " +
//			  " AND A.EXWAREHOUSE_NO = M.EXWAREHOUSE_NO " +
//			  " AND B.HOSP_AREA=A.HOSP_AREA " +
//			  " AND A.HOSP_AREA=C.HOSP_AREA " +
//			  " AND B.DEV_CODE=A.DEV_CODE " +
//			  " AND A.DEV_CODE=C.DEV_CODE " +
//			  " AND A.BATCH_SEQ=C.BATCH_SEQ " +
//			  " AND C.DEPT_CODE=M.INWAREHOUSE_DEPT ";
	    	
	    	//��ѯ���������ⵥ��EXWAREHOUSE_NO����ŵص�LOC_CODE���豸����DEVKIND_CODE��
	    	//��������EXWAREHOUSE_DATE�����DESCRIPTION
	    	//�������EXWAREHOUSE_DEPT���豸����DEV_CODE�������STOREQTY��
	    	//���Ʋ���ֵUNIT_PRICE����������MAN_DESC
	        SimpleDateFormat dd = new SimpleDateFormat("yyyy/MM/dd");
	        DecimalFormat ff = new DecimalFormat("######0.00");
	        //��ѯ��ʼ����
	        String s_date = this.getValueString("S_DATE");
	        //��ѯ��������
	        String e_date = this.getValueString("E_DATE");
	        //��ŵص�
	        String loccode = this.getValueString("LOC_CODE");
	        //���ⵥ��
	        String inwarehouse_no = this.getValueString("EXWAREHOUSE_NO");
	        //��������
	        String exwarehouse_date = this.getValueString("EXWAREHOUSE_DATE");
	        //�������
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
	        //ȷ�ϱ��Ժ��޸ģ�
	        String sql = 
                " SELECT 'N' AS FLG,M.EXWAREHOUSE_NO,M.EXWAREHOUSE_DEPT,D.COST_CENTER_CODE,TO_CHAR(M.EXWAREHOUSE_DATE,'YYYYMMDD') EXWAREHOUSE_DATE ,"+  
                " B.SEQMAN_FLG, B.DEVPRO_CODE, B.DEV_CODE, A.BATCH_SEQ, A.SEQ_NO,"+   
	        	" B.DEV_CHN_DESC, B.DESCRIPTION,B.SETDEV_CODE, A.QTY, C.QTY STOREQTY,"+   
	        	" B.UNIT_CODE, C.UNIT_PRICE,C.UNIT_PRICE*A.QTY AS TOT_PRICE , C.CARE_USER, C.USE_USER, C.LOC_CODE,"+   
	        	" C.SCRAP_VALUE, TO_CHAR(C.GUAREP_DATE,'YYYYMMDD') GUAREP_DATE,"+   
	        	" TO_CHAR(C.DEP_DATE,'YYYYMMDD') DEP_DATE,TO_CHAR(C.MAN_DATE,'YYYYMMDD') MAN_DATE,"+   
	        	" A.SEQ_NO, A.REMARK1, A.REMARK2"+
	        	" FROM DEV_EXWAREHOUSEM M, DEV_EXWAREHOUSED A,DEV_BASE B, DEV_STOCKD C ,SYS_COST_CENTER D"+   
	        	" WHERE A.EXWAREHOUSE_NO = M.EXWAREHOUSE_NO"+   
	        	" AND B.DEV_CODE=A.DEV_CODE "+  
	        	" AND A.DEV_CODE=C.DEV_CODE "+ 
	        	" AND A.BATCH_SEQ=C.BATCH_SEQ "+   
	        	" AND C.DEPT_CODE=M.INWAREHOUSE_DEPT "+  
	        	" AND D.COST_CENTER_CODE=M.INWAREHOUSE_DEPT "; 
	        System.out.println("sql==="+sql);        
	        StringBuffer SQL = new StringBuffer();  
	        SQL.append(sql);
	        //System.out.println("SQL==="+SQL);
	        //���ⵥ��
	        if ( !inwarehouse_no.equals("")){
	            SQL.append(" AND A.EXWAREHOUSE_NO='" + inwarehouse_no + "'");
	        }
	        //��ŵص�
	        if ( !loccode.equals("")){
	            SQL.append(" AND A.LOC_CODE='" + loccode + "'");
	        }
	        //��ѯ��ʼ����
	        if ( !s_date.equals("")) {
	        	s_date = s_date.substring(0, 19) ;
	        	SQL.append(" AND TO_CHAR(M.EXWAREHOUSE_DATE,'YYYY-MM-DD') >= '"+s_date+"'") ;
	        }
	        //��ѯ��������
             if ( !e_date.equals("")) {
            	e_date = e_date.substring(0, 19) ;
 	    		SQL.append(" AND TO_CHAR(M.EXWAREHOUSE_DATE,'YYYY-MM-DD') <= '"+e_date+"'") ;
	        }
	        //�������
	        if (!inwarehouse_dept.equals("")) {
	            SQL.append("AND A.EXWAREHOUSE_DEPT='" + inwarehouse_dept + "'");
	        }
	        //�豸����
	        if (!devcode.equals("")) {
	            SQL.append("AND A.DEV_CODE='" + devcode + "'");
	        }
	        //�豸����
	        if (!devkind.equals("")) {
	            SQL.append("AND B.DEVKIND_CODE='" + devkind + "'");
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
	        	SQL.append("AND B.MAN_CODE like '%"+mandesc+"%'");
	        }  
	        TParm result = new TParm(this.getDBTool().select(SQL.toString()));
	        System.out.println("result=="+result);
	        // �жϴ���ֵ
	        if (result == null || result.getCount() <= 0) {
	            callFunction("UI|TABLE|removeRowAll");
	            this.messageBox("û�в�ѯ����");
	            return; 
	        }
	        this.callFunction("UI|TABLE|setParmValue", result);
	    }
	   /**
	    * �������¼�
	    */
	   public void onTableClicked() {
	       int row =  this.getTable("TABLE").getClickedRow();
	       TParm parm = this.getTable("TABLE").getParmValue().getRow(row);
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
	        		 "EXWAREHOUSE_NO;DEPT_CODE;DEV_CODE;DEVKIND_CODE;S_DATE;" +
                     "E_DATE;DEV_CHN_DESC;DESCRIPTION;MAN_DESC;QTY;UNIT_PRICE;LOC_CODE");
	        callFunction("UI|TABLE|removeRowAll");
	    }

	    /**
	     * ����Excel
	     */
	    public void onExport() {
	        if (this.getTable("TABLE").getRowCount() > 0) {
	            ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
	                    "�豸����ϸ��ͳ�Ʊ���");
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
	        this.openPrintWindow("%ROOT%\\config\\prt\\DEV\\DEV_VerifyOutDetail.jhw",
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
