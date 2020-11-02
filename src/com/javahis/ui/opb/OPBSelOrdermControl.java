package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.testOpb.bean.OpdOrder;
import com.javahis.ui.testOpb.tools.OrderTool;
import com.javahis.ui.testOpb.tools.QueryTool;
import com.javahis.ui.testOpb.tools.TableTool;
import com.javahis.util.DateUtil;
/**
 * ���ò�ѯ
 * @author Administrator
 *
 */
public class OPBSelOrdermControl extends TControl {
	private final static String TAG_TABLE = "TABLE";
	private final static String TAG_COST_CENTER = "COST_CENTER_CODE";
	private final static String TAG_DEPT_CODE = "DEPT_CODE";
	private final static String TAG_ORDER_CAT1_CODE = "ORDER_CAT1_CODE";
	private final static String TAG_START_DATE = "START_DATE";
	private final static String TAG_END_DATE = "END_DATE";
	private final static String MSG_QUERY = "û��Ҫ��ѯ������";
	public TTable table;
	public TableTool tableTool;
	private final static String[] SYNCFIELDSNAMES = { "freqCode" };
	private final static String[] MUTIPLYFIELDSNAMES = { "dosageQty" };
	private String caseNo="";
	List<OpdOrder> opdList=null;
	public void onInit(){
		Object obj = getParameter();
        if (obj != null) {
        	caseNo = ( (TParm) obj).getValue("CASE_NO");
        }
		table = (TTable) getComponent(TAG_TABLE);
		tableTool = new TableTool(table, SYNCFIELDSNAMES, MUTIPLYFIELDSNAMES);
		Timestamp date =SystemTool.getInstance().getDate();
		this.setValue(TAG_END_DATE, date.toString().substring(0, 19).replace('-', '/'));
	    this.setValue(TAG_START_DATE, date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	    try {
			onQuery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onQuery() throws Exception{
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(TAG_START_DATE)), "yyyyMMddHHmmss");
	    String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(TAG_END_DATE)), "yyyyMMddHHmmss");
	    String execDeptCode = this.getValueString(TAG_COST_CENTER);		
		String deptCode = this.getValueString(TAG_DEPT_CODE);		
		String orderCat1Code = getValueString(TAG_ORDER_CAT1_CODE);
		String sql1 = "";		
		if(orderCat1Code.length() > 0){
			sql1 =sql1+ " AND ORDER_CAT1_CODE='"+orderCat1Code+"'";
 
		}
		if(execDeptCode.length() > 0){
			sql1 = sql1 + " AND EXEC_DEPT_CODE ='"+execDeptCode+"'";
		}
		
		if(deptCode.length() > 0){
			sql1 = sql1 + " AND DEPT_CODE ='"+deptCode+"'";
		}
		if(sDate.length() > 0 && eDate.length() > 0){
			sql1 = sql1 + " AND OPT_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')";
		}

		String sql = "SELECT   A.* FROM (SELECT *" +
		" FROM OPD_ORDER WHERE CASE_NO = '" + caseNo + "'" +
		" AND ORDERSET_CODE IN (" +
		" SELECT ORDER_CODE FROM OPD_ORDER" +
		" WHERE CASE_NO = '" + caseNo + "'" +
		sql1 +
		" AND SETMAIN_FLG = 'Y'" +
		" AND ORDERSET_CODE IS NOT NULL)" +
		" UNION SELECT * FROM OPD_ORDER" +
		" WHERE CASE_NO = '" + caseNo + "'" +
		" AND ORDERSET_CODE IS NULL" +
		sql1 +
		") A ORDER BY A.EXEC_FLG DESC, A.ORDER_DATE";
		
		OpdOrder opdOrder = new OpdOrder();
		List<OpdOrder> list = QueryTool.getInstance().queryBySql(sql, opdOrder);
		list = OrderTool.getInstance().initOrder(list);
		if(list.isEmpty()){
//			this.messageBox(MSG_QUERY);
			 table.setParmValue(new TParm());
			 return;
		}
		opdList=list;//��ӡʹ�ã�add by huangjw 20150203
		tableTool.setList(list);
		tableTool.show();
		table.removeRow(table.getRowCount()-1);
	}
	
	public void onClear(){
		this.clearValue(TAG_COST_CENTER+";"+TAG_ORDER_CAT1_CODE+";"+TAG_DEPT_CODE);
		Timestamp date =SystemTool.getInstance().getDate();
		this.setValue(TAG_END_DATE, date.toString().substring(0, 19).replace('-', '/'));
	    this.setValue(TAG_START_DATE, date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	    table.setParmValue(new TParm());
		tableTool = new TableTool(table, SYNCFIELDSNAMES, MUTIPLYFIELDSNAMES);
		opdList=null;//����ֶ�
	}
	/**
	 * ��ӡadd by huangjw 20150203
	 */
	public void onPrint(){
		TParm parm=table.getShowParmValue();
		if(parm.getCount()<=0){
			this.messageBox("û������");
			return;
		}
		for(int i=0;i<parm.getCount();i++){
			parm.setData("specification",i,opdList.get(i).specification);
			parm.setData("subQty",i,opdList.get(i).subQty);
			parm.setData("opdUser",i,opdList.get(i).optUser);
		}
		TParm tableParm=new TParm();
		TParm data=new TParm();
		double amt=0.00;//�ϼƽ��
		DecimalFormat df=new DecimalFormat("###0.0");//����double�������ݵľ���
		DecimalFormat df1=new DecimalFormat("###0.00");//����double�������ݵľ���
		for(int i=0;i<parm.getCount();i++){
			tableParm.addData("orderDesc", parm.getValue("orderDesc",i));//ҽ������
			tableParm.addData("specification", parm.getValue("specification",i));//���
			tableParm.addData("dosageQty", df.format(parm.getDouble("dosageQty",i)));//����
			tableParm.addData("subQty", df.format(parm.getDouble("subQty",i)));//�˷�����
			tableParm.addData("dosageUnit", parm.getValue("dosageUnit",i));//��λ
			tableParm.addData("showOwnPrice", df1.format(parm.getDouble("showOwnPrice",i)));//����
			tableParm.addData("showOwnAmt", df1.format(parm.getDouble("showOwnAmt",i)));//Ӧ��
			tableParm.addData("showAmt", df1.format(parm.getDouble("showAmt",i)));//�ܼ�
			tableParm.addData("opdUser", getUserName(parm.getValue("opdUser",i)));//ִ����
			tableParm.addData("execDeptCode", parm.getValue("execDeptCode",i));//ִ�п���
			tableParm.addData("optDate", parm.getValue("optDate",i));//ִ��ʱ��
			amt+=parm.getDouble("showAmt",i);
		}
		tableParm.setCount(parm.getCount());
		tableParm.addData("SYSTEM", "COLUMNS", "orderDesc");
		tableParm.addData("SYSTEM", "COLUMNS", "specification");
		tableParm.addData("SYSTEM", "COLUMNS", "dosageQty");
		tableParm.addData("SYSTEM", "COLUMNS", "subQty");
		tableParm.addData("SYSTEM", "COLUMNS", "dosageUnit");
		tableParm.addData("SYSTEM", "COLUMNS", "showOwnPrice");
		tableParm.addData("SYSTEM", "COLUMNS", "showOwnAmt");
		tableParm.addData("SYSTEM", "COLUMNS", "showAmt");
		tableParm.addData("SYSTEM", "COLUMNS", "opdUser");
		tableParm.addData("SYSTEM", "COLUMNS", "execDeptCode");
		tableParm.addData("SYSTEM", "COLUMNS", "optDate");
		data.setData("TABLE",tableParm.getData());
		Pat pat=getPat();
		data.setData("MR_NO","TEXT",pat.getMrNo());
		data.setData("PAT_NAME","TEXT",pat.getName());
		data.setData("SEX","TEXT",pat.getSexString());
		data.setData("AGE","TEXT",patAge(pat.getBirthday()));
		data.setData("AMT","TEXT",df1.format(amt));
		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBSelOrdermPrint.jhw", data);
	}
	/**
	 * ��ò�����Ϣ
	 * @return
	 */
	public Pat getPat(){
		
		String sql="SELECT MR_NO FROM REG_PATADM WHERE CASE_NO='"+caseNo+"' ";
		TParm parm =new TParm(TJDODBTool.getInstance().select(sql));
		String mrNo=parm.getValue("MR_NO",0);
		Pat pat = Pat.onQueryByMrNo(mrNo);
	    //Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
		
		return pat;
	}
	/**
	    * ��������
	    * @param date
	    * @return
	    */
	   private String patAge(Timestamp date){
		   Timestamp sysDate = SystemTool.getInstance().getDate();
	       Timestamp temp = date == null ? sysDate : date;
	       String age = "0";
	       age = DateUtil.showAge(temp, sysDate);
	       return age;
	   }
	   /**
	    * ��ȡִ���˵�����
	    * @param id
	    * @return
	    */
	   public String getUserName(String id){
		   String sql="SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+id+"'";
		   TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		   return result.getValue("USER_NAME",0);
	   }
	
}
