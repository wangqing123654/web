package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.poi.util.SystemOutLogger;
import org.mortbay.jetty.security.SSORealm;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.OdiUtil;

/**
 * <p>Title: ���������ü�¼��ѯ</p>
 *
 * <p>Description: ���������ü�¼��ѯ</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2013</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author caowl
 * @version 1.0
 */
public class BILSPCINVRecordSelControl extends TControl{

	
	TTable Table;
	
	//action��·��
	private static final String actionName = "action.bil.SPCINVRecordAction";
	
	/**
	 * ��ʼ��
	 * */
	 public void onInit() {
	        super.onInit();	       
	        //���ȫ���ؼ�
	        getAllComponent(); 
	      
	 }
	 
	 //���ȫ���ؼ�
	 public void getAllComponent(){
		 Table = (TTable)this.getComponent("Table");
		 //��ʼ��ʱ���  ���쵽����
		 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                    getDate(), -1);
		 setValue("S_TIME", yesterday.toString().substring(0,10)+"00:00:00");
		 setValue("E_TIME", SystemTool.getInstance().getDate().toString().substring(0,10)+"23:59:59");
		 setValue("DEPT_CODE",Operator.getDept());
		 setValue("OPT_USER","");
	 }
	 /**
	  * ���벡����
	  * */
	 public void onMrNo(){
		 String mrNo =PatTool.getInstance().checkMrno(
					TypeTool.getString(getValue("MR_NO")));
		 this.setValue("MR_NO", mrNo);
		 
		 
		 
		 String mr_no =PatTool.getInstance().checkMrno(
					TypeTool.getString(getValue("MR_NO")));
		
		 this.setValue("MR_NO",mr_no);
		 TParm parm = new TParm();
		 parm.setData("MR_NO",mr_no);
		 parm.setData("ADM_TYPE","I");
		 
		 TParm selParm = TIOM_AppServer.executeAction(actionName, "onMrNo", parm);
		 System.out.println("��ѯ�����"+selParm);
		 if(selParm.getCount("CASE_NO")<0){
			 this.messageBox("���޴˲��ˣ�");
			 return;
		 }
		 Timestamp sysDate = SystemTool.getInstance().getDate();
		
//		 Date date = new Date();
//		 SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		 String time = df1.format(date);
//		 Timestamp CreateDate = Timestamp.valueOf(time);
		 String birthDate = selParm.getData("BIRTH_DATE",0).toString().substring(0,19).replace("-", "/");
		 //System.out.println("birthDate--->"+birthDate);
		 Timestamp birth_date = new Timestamp(Date.parse(birthDate));
		 
		 
		 Timestamp temp = birth_date == null ? sysDate: birth_date;
		
		// ��������
		String age = "0";
		if (birth_date != null)
			age = OdiUtil.getInstance().showAge(temp,sysDate);
		
		else
			age = "";
		selParm.addData("AGE", age);
		
		//this.setValue("IPD_NO", selParm.getValue("IPD_NO", 0));
		this.setValue("PAT_NAME", selParm.getValue("PAT_NAME", 0));
		this.setValue("AGE", selParm.getValue("AGE", 0));
		this.setValue("SEX_CODE", selParm.getValue("SEX_CODE", 0));
		//this.setValue("DEPT_CODE", selParm.getValue("DEPT_CODE",0));
		//this.setValue("STATION_CODE", selParm.getValue("STATION_CODE",0));
		//this.setValue("DR_CODE", selParm.getValue("DR_CODE",0));
		//this.setValue("CASE_NO", selParm.getValue("CASE_NO", 0));
	 }
		
	 /**
	  * ��ѯ
	  * */
	 public void onQuery()
		{
			String sTime = getValueString("S_TIME");
			String eTime = getValueString("E_TIME");
			String deptCode = getValueString("DEPT_CODE");
			String optUser = getValueString("OPT_USER");
			String class_code = getValueString("CLASS_CODE");
			String bar_code = getValueString("BAR_CODE");
			String billFlg = getValueString("BILL_FLG");
			String mr_no = getValueString("MR_NO");
			String sqlWhere = "";
			if (sTime == null || sTime.equals("") || eTime == null || eTime.equals(""))
			{
				messageBox("��ѡ��ʼ�ͽ���ʱ�䣡");
				return;
			}
			if (deptCode != null && !deptCode.equals(""))
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND EXE_DEPT_CODE = '").append(deptCode).append("'").toString();
			if (optUser != null && !optUser.equals(""))
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND OPT_USER = '").append(optUser).append("'").toString();
			if (mr_no != null && !mr_no.equals(""))
			{
				String mrNo = PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
				setValue("MR_NO", mrNo);
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND MR_NO = '").append(mrNo).append("'").toString();
			}
			if (billFlg != null && !billFlg.equals(""))
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND BILL_FLG = '").append(billFlg).append("'").toString();
			if (class_code != null && !class_code.equals(""))
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" ADN CLASS_CODE = '").append(class_code).append("'").toString();
			if (bar_code != null && !bar_code.equals(""))
				sqlWhere = (new StringBuilder(String.valueOf(sqlWhere))).append(" AND BAR_CODE = '").append(bar_code).append("'").toString();
			String sql = (new StringBuilder("SELECT *  FROM SPC_INV_RECORD  WHERE OPT_DATE BETWEEN TO_DATE('")).append(sTime.substring(0, 19)).append("','yyyy-MM-dd HH24:Mi:ss') ").append("        AND TO_DATE('").append(eTime.substring(0, 19)).append("','yyyy-MM-dd HH24:Mi:ss')").append(sqlWhere).toString();
			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			TParm tableParm = new TParm();
			int count = selParm.getCount();
			if (count < 0)
			{
				messageBox("û��Ҫ��ѯ������");
				return;
			}
			double qtySum = 0.0D;
			double arAmtSum = 0.0D;
			for (int i = 0; i < count; i++)
			{
				tableParm.addData("MR_NO", selParm.getValue("MR_NO", i));
				tableParm.addData("BAR_CODE", selParm.getValue("BAR_CODE", i));
				if (selParm.getValue("INV_DESC", i).equals(""))
					tableParm.addData("DESC", selParm.getValue("ORDER_DESC", i));
				else
					tableParm.addData("DESC", selParm.getValue("INV_DESC", i));
				tableParm.addData("QTY", selParm.getValue("QTY", i));
				qtySum += Double.parseDouble(selParm.getValue("QTY", i).toString());
				tableParm.addData("UNIT_CODE", selParm.getValue("UNIT_CODE", i));
				tableParm.addData("BILL_FLG", selParm.getValue("BILL_FLG", i));
				tableParm.addData("OWN_PRICE", selParm.getValue("OWN_PRICE", i));
				tableParm.addData("AR_AMT", selParm.getValue("AR_AMT", i));
				arAmtSum += Double.parseDouble(selParm.getValue("AR_AMT", i).toString());
				tableParm.addData("HEXP_CODE", selParm.getValue("ORDER_CODE", i));
				tableParm.addData("HEXP_DESC", selParm.getValue("ORDER_DESC", i));
				tableParm.addData("OP_ROOM", selParm.getValue("OP_ROOM", i));
				tableParm.addData("DEPT_CODE", selParm.getValue("DEPT_CODE", i));
				tableParm.addData("BILL_USER", selParm.getValue("OPT_USER",i));
			}

			tableParm.addData("MR_NO", "�ܼƣ�");
			tableParm.addData("BAR_CODE", "");
			tableParm.addData("INV_DESC", "");
			tableParm.addData("QTY", Double.valueOf(qtySum));
			tableParm.addData("UNIT_CODE", "");
			tableParm.addData("BILL_FLG", "");
			tableParm.addData("OWN_PRICE", "");
			tableParm.addData("AR_AMT", Double.valueOf(arAmtSum));
			tableParm.addData("HEXP_CODE", "");
			tableParm.addData("HEXP_DESC", "");
			tableParm.addData("OP_ROOM", "");
			tableParm.addData("DEPT_CODE", "");
			tableParm.addData("BILL_USER", "");
			callFunction("UI|Table|setParmValue", tableParm);
		}
//	 public void onQuery(){
//		 System.out.println("------------��ѯ��ʼ --------------");
//		 String sTime = this.getValueString("S_TIME");
//		 String eTime = this.getValueString("E_TIME");
//		 String deptCode = this.getValueString("DEPT_CODE");
//		 String optUser =this.getValueString("OPT_USER");
//		 String class_code = this.getValueString("CLASS_CODE");
//		 String bar_code = this.getValueString("BAR_CODE");
//		// String mrNo = this.getValueString("MR_NO");
//		 String billFlg = this.getValueString("BILL_FLG");
//		 String mr_no = this.getValueString("MR_NO");
//		 String sqlWhere = "";
//		 if(sTime == null || sTime.equals("") || eTime == null || eTime.equals("") ){
//			this.messageBox("��ѡ��ʼ�ͽ���ʱ�䣡");
//			return;
//		 }
//		 if(deptCode != null && !deptCode.equals("")){
//			 sqlWhere += " AND EXE_DEPT_CODE = '"+deptCode+"'";
//		 }
//		 if(optUser != null && !optUser.equals("")){
//			 sqlWhere += " AND OPT_USER = '"+optUser+"'";
//		 }
//		 if(mr_no != null && !mr_no.equals("")){
//			 String mrNo =PatTool.getInstance().checkMrno(
//						TypeTool.getString(getValue("MR_NO")));
//			 this.setValue("MR_NO", mrNo);
//			 sqlWhere += " AND MR_NO = '"+mrNo+"'";
//		 }
//		 if(billFlg != null && !billFlg .equals("")){
//			 sqlWhere += " AND BILL_FLG = '"+billFlg+"'";
//		 }
//		 if(null != class_code && !class_code.equals("")){
//			 sqlWhere += " ADN CLASS_CODE = '"+class_code+"'";
//		 }
//		 
//		 if(null != bar_code && !bar_code.equals("")){
//			 sqlWhere += " AND BAR_CODE = '"+bar_code+"'";
//		 }
//		 
//		 String sql = "SELECT * " +
//		 		" FROM SPC_INV_RECORD A " +
//		 		" WHERE OPT_DATE BETWEEN TO_DATE('"+sTime.substring(0, 19)+"','yyyy-MM-dd HH24:Mi:ss') " +
//		 				"        AND TO_DATE('"+eTime.substring(0,19)+"','yyyy-MM-dd HH24:Mi:ss')"+sqlWhere;
//		 System.out.println("sql------->"+sql);
//		 TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
//		 TParm tableParm = new TParm();
//		 int count = selParm.getCount();
//		 if(count<0){
//			 this.messageBox("û��Ҫ��ѯ������");
//			 return;
//		 }
//		 double qtySum = 0;
//		 double arAmtSum = 0;
// 		 //BAR_CODE;DESC;QTY;UNIT_CODE;BILL_FLG;OWN_PRICE;AR_AMT;HEXP_CODE;HEXP_DESC;OP_ROOM;DEPT_CODE
//		 for(int i=0;i<count;i++){
//			 tableParm.addData("BAR_CODE", selParm.getValue("BAR_CODE", i));
//			 if(selParm.getValue("INV_DESC", i).equals("")){
//				 tableParm.addData("DESC", selParm.getValue("ORDER_DESC", i));
//			 }else{
//				 tableParm.addData("DESC", selParm.getValue("INV_DESC", i));
//			 }
////			 if(!(selParm.getValue("INV_CODE",i)== null || selParm.getValue("INV_CODE",i).equals(""))){
////				 String invCode = selParm.getValue("INV_CODE",i);
////				 String sqlDescr = "SELECT DISTINCT DESCRIPTION FROM INV_BASE WHERE INV_CODE = '"+invCode+"'"; 
////				 System.out.println("sqlDescr----------->"+sqlDescr);
////				 TParm descrParm = new TParm(TJDODBTool.getInstance().select(sqlDescr));
////				 tableParm.addData("DESCRIPTION", descrParm.getValue("DESCRIPTION",0));
////			 }else{
////				 tableParm.addData("DESCRIPTION", "");
////			 }
//			
//			 tableParm.addData("QTY", selParm.getValue("QTY", i));
//			 qtySum += Double.parseDouble(selParm.getValue("QTY",i).toString());
//			 tableParm.addData("UNIT_CODE", selParm.getValue("UNIT_CODE", i));
//			 tableParm.addData("BILL_FLG", selParm.getValue("BILL_FLG", i));
//			 tableParm.addData("OWN_PRICE", selParm.getValue("OWN_PRICE", i));
//			 tableParm.addData("AR_AMT", selParm.getValue("AR_AMT", i));
//			 arAmtSum += Double.parseDouble(selParm.getValue("AR_AMT",i).toString());
//			 tableParm.addData("HEXP_CODE", selParm.getValue("ORDER_CODE", i));
//			 tableParm.addData("HEXP_DESC", selParm.getValue("ORDER_DESC", i));
//			 tableParm.addData("OP_ROOM", selParm.getValue("OP_ROOM", i));
//			 tableParm.addData("DEPT_CODE", selParm.getValue("DEPT_CODE", i));
//		 }
//		 System.out.println("---------�ܼ�֮ǰ----------");
//		 //����ܼ�
//		 tableParm.addData("BAR_CODE", "�ܼƣ�");
//		 tableParm.addData("INV_DESC", "");
//		 tableParm.addData("DESCRIPTION", "");
//		 tableParm.addData("QTY",qtySum);
//		 tableParm.addData("UNIT_CODE", "");
//		 tableParm.addData("BILL_FLG", "");
//		 tableParm.addData("OWN_PRICE", "");
//		 tableParm.addData("AR_AMT",arAmtSum);
//		 tableParm.addData("HEXP_CODE", "");
//		 tableParm.addData("HEXP_DESC", "");
//		 tableParm.addData("OP_ROOM", "");		
//		 tableParm.addData("DEPT_CODE", "");
//		 System.out.println("------------�ܼ�֮��-----------");
//		 System.out.println("tableParm--->"+tableParm);
//		 TTable table = (TTable)this.getComponent("Table"); 
//		 table.setParmValue(tableParm);
//		// this.callFunction("UI|Table|setParmValue", tableParm);
//			
//	 }

	 /**
	  * ���
	  * */
	 public void onClear(){
		 Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                 getDate(), -1);
		 setValue("S_TIME", yesterday);
		 setValue("E_TIME", SystemTool.getInstance().getDate());
		 this.setValue("", "");
		 this.setValue("", "");
		 this.setValue("", "");
		 this.setValue("", "");
		 this.setValue("", "");
		 this.setValue("", "");
		 this.setValue("", "");
		 this.setValue("", "");
	 }
	 /**
	  * ����excel
	  * */
	 public void onExcel(){
		 //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
	        TTable table = (TTable) this.getComponent("Table");
	        if (table.getRowCount() > 0)
	            ExportExcelUtil.getInstance().exportExcel(table, "���������ü�¼��ѯ");
	 }
} 
	