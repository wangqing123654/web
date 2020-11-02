package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.testOpb.bean.OpdOrder;
import com.javahis.ui.testOpb.bean.OpdOrderReturn;
import com.javahis.ui.testOpb.tools.QueryTool;
import com.javahis.ui.testOpb.tools.TableTool;
import com.javahis.util.DateUtil;

public class OPBSelReturnOrdermControl extends TControl{
	private final static String TAG_TABLE = "TABLE";
	private final static String MSG_QUERY = "没有要查询的数据";
	private static final String MSG_PAT = "请输入病案号";	
	private static final String MSG_NO_PAT = "查无此病案号";	
	private static final String MSG_ERR_REG = "挂号信息错误!";
	private final static String TAG_START_DATE = "START_DATE";
	private final static String TAG_END_DATE = "END_DATE";
	public final static String TAG_MR_NO = "MR_NO";
	private final static String TAG_PAT_NAME = "PAT_NAME";
	private final static String URL_OPBCHOOSEVISIT = "%ROOT%\\config\\opb\\OPBChooseVisit.x";
	private final static String[] SYNCFIELDSNAMES = { "" };
	private final static String[] MUTIPLYFIELDSNAMES = { "" };
	public Pat pat;
	public Reg reg;
	public TTable table;
	public TableTool tableTool;
	List<OpdOrder> opdList=null;
	private String caseNo="";
	
	/**
	 * 初始化
	 */
	public void onInit(){
		Object obj = getParameter();
        if (obj != null) {
        	 caseNo = ( (TParm) obj).getValue("CASE_NO");
        	 this.setValue("MR_NO", ( (TParm) obj).getValue("MR_NO"));
        	 this.setValue("PAT_NAME", ( (TParm) obj).getValue("PAT_NAME"));
        }
		table = (TTable) getComponent(TAG_TABLE);
		tableTool = new TableTool(table, SYNCFIELDSNAMES, MUTIPLYFIELDSNAMES);
		Timestamp date =SystemTool.getInstance().getDate();
		this.setValue(TAG_END_DATE, date.toString().substring(0, 19).replace('-', '/'));
	    this.setValue(TAG_START_DATE, date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	    try {
	    	if(caseNo.length() > 0){
	    		onQueryTable();
	    	}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onClear(){
		this.clearValue(TAG_MR_NO+";"+TAG_PAT_NAME);
		Timestamp date =SystemTool.getInstance().getDate();
		this.setValue(TAG_END_DATE, date.toString().substring(0, 19).replace('-', '/'));
	    this.setValue(TAG_START_DATE, date.toString().substring(0, 10).replace('-', '/')+ " 00:00:00");
	    table.setParmValue(new TParm());
		tableTool = new TableTool(table, SYNCFIELDSNAMES, MUTIPLYFIELDSNAMES);
		pat = null;
		reg = null;
	}
	
	/**
	 * 查询
	 * @throws Exception
	 */
	public void onQuery() throws Exception{
		onQueryReg();
		if(caseNo.length() == 0){
			this.messageBox(MSG_PAT);
			return;
		}
		onQueryTable();
	    
	}
	
	/**
	 * 查询表格中显示的数据
	 */
	public void onQueryTable()throws Exception{
		String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(TAG_START_DATE)), "yyyyMMddHHmmss");
	    String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(TAG_END_DATE)), "yyyyMMddHHmmss");
	    String sql1 = "";
	    if(sDate.length() > 0 && eDate.length() > 0){
			sql1 = sql1 + " AND OPT_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')";
		}
	    String sql = "SELECT   A.* FROM (SELECT *" +
		" FROM OPD_ORDER_RETURN WHERE CASE_NO = '" + caseNo + "'" +
		" AND ORDERSET_CODE IN (" +
		" SELECT ORDER_CODE FROM OPD_ORDER_RETURN" +
		" WHERE CASE_NO = '" + caseNo + "'" +
		" AND SUB_QTY <> 0 " +
		sql1 +
		" AND SETMAIN_FLG = 'Y'" +
		" AND ORDERSET_CODE IS NOT NULL)" +
		" UNION SELECT * FROM OPD_ORDER_RETURN" +
		" WHERE CASE_NO = '" + caseNo + "'" +
		" AND ORDERSET_CODE IS NULL" +
		" AND SUB_QTY <> 0 " +
		sql1 +
		") A ORDER BY A.DC_ORDER_DATE";
	    
//	    System.out.println(sql);
	    
	    OpdOrderReturn opdOrderReturn = new OpdOrderReturn();
	    List<OpdOrderReturn> list = QueryTool.getInstance().queryBySql(sql, opdOrderReturn);
	    list = initOrder(list);
	    if(list.isEmpty()){
			this.messageBox(MSG_QUERY);
			 table.setParmValue(new TParm());
			 return;
		}
	    tableTool.setList(list);
		tableTool.show();
		table.removeRow(table.getRowCount()-1);
	}
	
	/**
	 * 查询挂号信息
	 */
	public void onQueryReg(){
		//查询挂号信息
		String regionCode = Operator.getRegion();
		TParm parm = PatAdmTool.getInstance().selDateByMrNo(pat.getMrNo(),
				(Timestamp) getValue(TAG_START_DATE),
				(Timestamp) getValue(TAG_END_DATE), regionCode);
		if (parm.getCount() == 0) {
			caseNo = onRecord();
		}else if (parm.getCount() == 1) {
			// 初始化reg
			caseNo = parm.getValue("CASE_NO", 0);
		}else if (parm.getCount() > 1){
			caseNo = onRecord();
		}
		
		reg = Reg.onQueryByCaseNo(pat, caseNo);
		if (reg == null) {
			this.messageBox(MSG_ERR_REG);
			return;
		}
	}
	
	/**
	 * 查询病患信息
	 */
	public void onMrNo(){
		//查询病患信息
		String mrNo = this.getValueString("MR_NO");
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox_(MSG_NO_PAT);
			return;
		}
		this.setValue(TAG_MR_NO, pat.getMrNo());
		this.setValue(TAG_PAT_NAME, pat.getName());
		try {
			onQuery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 就诊记录选择
	 */
	private String onRecord() {
		TParm parm = new TParm();
		parm.setData("MR_NO",  pat.getMrNo());
		parm.setData("PAT_NAME",  pat.getName());
		parm.setData("SEX_CODE",  pat.getSexCode());
		parm.setData("AGE", patAge(pat.getBirthday()));
		// 判断是否从明细点开的就诊号选择
		parm.setData("count", "0");
		String caseNo = (String) openDialog(URL_OPBCHOOSEVISIT, parm);
		return caseNo;
	}
	
	/**
	    * 计算年龄
	    * @param date add by huangjw 20150106
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
		 * 整理医嘱
		 * @param list
		 * @return
		 */
	   public List<OpdOrderReturn> initOrder(List<OpdOrderReturn> list){
		   List<OpdOrderReturn> ml = new ArrayList<OpdOrderReturn>();
			List<OpdOrderReturn> sl = new ArrayList<OpdOrderReturn>();
			for (OpdOrderReturn opdOrder : list) {
				if((opdOrder.ordersetCode != null || opdOrder.ordersetCode.length() > 0)
						&& "N".equals(opdOrder.setmainFlg)){
					sl.add(opdOrder);
				}
				if((opdOrder.ordersetCode == null || opdOrder.ordersetCode.length() == 0)){
					ml.add(opdOrder);
				}
				if((opdOrder.ordersetCode != null || opdOrder.ordersetCode.length() > 0)
						&& "Y".equals(opdOrder.setmainFlg)){
					ml.add(opdOrder);
				}
			}
			return ml;
	   }
	

}
