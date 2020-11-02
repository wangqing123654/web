package com.javahis.ui.phl;

import java.sql.Timestamp;

import jdo.phl.PhlOrderTool;
import jdo.phl.PhlRegisterTool;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
//import com.sun.jmx.snmp.Timestamp;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 静点室取医嘱
 * </p>
 *
 * <p>
 * Description: 静点室取医嘱
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
 * @author yanj 2015.01.19
 * @version 1.0
 */

public class PHLGetOrderControl extends TControl {
	private static final String TABLE = "TABLE"; // 医嘱表
	 private TTable table;
	 private String caseNo = "";
	 private String mrNo = "";
	 private String admType = "";
	 private Pat pat; // 病患信息
	 public String getCaseNo() {
			return caseNo;
		}

		public void setCaseNo(String caseNo) {
			this.caseNo = caseNo;
		}
		public String getMrNo() {
			return mrNo;
		}

		public void setMrNo(String mrNo) {
			this.mrNo = mrNo;
		}
		public String getAdmType() {
			return admType;
		}

		public void setAdmType(String admType) {
			this.admType = admType;
		}
	/**
     * 初始化方法
     */
    public void onInit() {
    	Object obj = this.getParameter();	
    	table = this.getTable(TABLE);
//    	System.out.println("======"+((TParm)obj));
    	TParm Parm = ((TParm)obj);
    	if(obj!=null){
			// 设置就诊号
			this.setCaseNo(Parm.getData("CASE_NO").toString());
			this.setMrNo(Parm.getData("MR_NO").toString());
			this.setAdmType(Parm.getData("ADM_TYPE").toString());
    	}
    	this.setValue("MR_NO", mrNo);
    	this.setValue("PAT_NAME", Parm.getData("PAT_NAME").toString());
    	this.setValue("ADM_TYPE", admType);
    	// 初始化验收时间
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("ADM_DATE", date);
    	this.onQurey();
    	
    }
    /**
     * 查询医生站添加的组液
     */
    private void onQurey(){
    	TParm parm = new TParm();
		TParm orderparm = new TParm();
		TParm result = new TParm();
		if(!"".equals(this.getValue("MR_NO"))){
			parm.setData("MR_NO",mrNo);
			parm.setData("CASE_NO", caseNo);
		}
		TParm orderParmAll = PhlRegisterTool.getInstance().onQueryOrderDetailBed(
				parm);//取得病人所开立的所有点滴
		if (orderParmAll == null || orderParmAll.getCount() <= 0) {
			this.messageBox("没有医嘱信息");
			table.removeRowAll();
			return ;
		}
		TParm sparm = new TParm();// add caoyong 已报到的医嘱 2014、4、3
		TParm oldorderparm = new TParm();// add caoyong 已报到的医嘱 2014、4、3
//		System.out.println("++++=====+++++orderParmAll orderParmAll is ::"+orderParmAll);
		for (int i = 0; i < orderParmAll.getCount(); i++) {
			sparm.setData("ADM_TYPE", admType);
			sparm.setData("CASE_NO", caseNo);
			sparm.setData("ORDER_NO", orderParmAll.getValue("RX_NO", i));
			sparm.setData("SEQ_NO", orderParmAll.getInt("SEQ_NO", i));
			sparm.setData("ORDER_CODE", orderParmAll.getValue("ORDER_CODE", i));
			oldorderparm = PhlRegisterTool.getInstance().onQueryoldOrder(
					sparm);
			if (oldorderparm.getCount() > 0) {//存在已经报到的点滴，从所有的Parm中将该部分移除
				continue;
			}
			orderparm.setData("FLG", i, true);
			orderparm.setData("ORDER_CODE", i,  orderParmAll.getValue("ORDER_CODE", i));
            orderparm.setData("ORDER_DESC", i,  orderParmAll.getValue("ORDER_DESC", i));
            orderparm.setData("ROUTE_CODE", i,  orderParmAll.getValue("ROUTE_CODE", i));
            orderparm.setData("MEDI_QTY", i,orderParmAll.getValue("DISPENSE_QTY", i));
            orderparm.setData("MEDI_UNIT", i,orderParmAll.getValue("SPECIFICATION", i));
            orderparm.setData("FREQ_CODE", i, orderParmAll.getValue("FREQ_CODE", i));
            orderparm.setData("DR_NOTE", i,  orderParmAll.getValue("DR_NOTE", i));
            orderparm.setData("DR_CODE", i,orderParmAll.getValue("DR_CODE", i));
            orderparm.setData("RX_NO", i,orderParmAll.getValue("RX_NO", i));
            orderparm.setData("SEQ_NO", i,orderParmAll.getValue("SEQ_NO", i));
            orderparm.setData("TAKE_DAYS", i,orderParmAll.getValue("TAKE_DAYS", i));
            orderparm.setData("LINKMAIN_FLG", i, "Y".equals(orderParmAll.getValue("LINKMAIN_FLG",i)) ? true : false);
            orderparm.setData("LINK_NO", i,
            		orderParmAll.getValue("LINK_NO",i) == null ? 0 :
            			orderParmAll.getValue("LINK_NO",i));
            orderparm.setData("ORDER_DATE", i,
            		 orderParmAll.getValue("ORDER_DATE", i));
		}
//		System.out.println("=======orderparm orderparm is ::"+orderparm);
        table.setParmValue(orderparm);
//		return orderparm;
    }
    /**
     * 病案号回车事件
     */
    public void onQueryMrNo(){
    	pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案信息");
			this.grabFocus("PAT_NAME");
			this.setValue("MR_NO", "");
			callFunction("UI|MR_NO|setEnabled", true); // 病案号可编辑
			return;
		}
		// 病案号
		this.setValue("MR_NO", pat.getMrNo());
		this.setMrNo(pat.getMrNo());
		// 姓名
		this.setValue("PAT_NAME", pat.getName());
		//获取就诊号
		Timestamp startTime = SystemTool.getInstance().getDate();
		String regionCode = Operator.getRegion();
		TParm parm = PatAdmTool.getInstance().selDateByMrNo(pat.getMrNo(),
				startTime,
				startTime, regionCode);
		// 查找错误
		if (parm.getCount() < 0) {
			messageBox_("就诊序号选择错误!");
			return;
		}else if (parm.getCount() ==1) {
//			System.out.println("++++++parm parm parm is ::"+parm);
			this.setCaseNo(parm.getValue("CASE_NO",0));
			this.setAdmType(parm.getValue("ADM_TYPE",0));
		}else{
			onRecord();
		}
//		System.out.println("admType admType is ::"+admType);
		this.setValue("ADM_TYPE", admType);
		this.onQurey();
    }
    /**
	 * 就诊记录选择
	 */
	public void onRecord() {
		// 初始化pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("查无此病案号!");
			// 若无此病案号则不能查找挂号信息
			callFunction("UI|record|setEnabled", false);
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("AGE", getValue("AGE"));
		// 判断是否从明细点开的就诊号选择
		parm.setData("count", "0");
		caseNo = (String) openDialog(
				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
		if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
			return;
		}else{
			String sql = "SELECT ADM_TYPE FROM REG_PATADM WHERE CASE_NO = '"+caseNo+"' ";
			TParm admParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(admParm.getCount()>0){
				this.setAdmType(admParm.getValue("ADM_TYPE",0));
				this.setCaseNo(caseNo);
			}
			
		}
	}

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    /**
	 * 全选
	 */
	public void onSelAll() {
		this.getTable(TABLE).acceptText();
		TParm parm = this.getTable(TABLE).getParmValue();
		int rowCount = parm.getCount("FLG");
		for (int i = 0; i < rowCount; i++) {
			if (parm.getBoolean("FLG", i)) {
				parm.setData("FLG", i, false);
			} else {
				parm.setData("FLG", i, true);
			}
		}
		this.getTable(TABLE).setParmValue(parm);
	}
	/**
	 * 传回方法
	 */
	public void onFetch(){
		TParm orderParmAll = new TParm();
		orderParmAll = table.getParmValue();
		TParm orderparm = new TParm();
		TParm result = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		String start_date = SystemTool.getInstance().getDate().toString();
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
            start_date.substring(8, 10) + start_date.substring(11, 13) +
            start_date.substring(14, 16) + start_date.substring(17, 19);
		for (int i = 0; i < orderParmAll.getCount("ORDER_CODE"); i++) {
			if(orderParmAll.getBoolean("FLG",i)){
				orderparm.setData("START_DATE", i, start_date);
	            orderparm.setData("ADM_TYPE", i, admType);
	            orderparm.setData("CASE_NO", i, caseNo);
	            orderparm.setData("ORDER_NO", i,orderParmAll.getValue("RX_NO", i));
	            orderparm.setData("SEQ_NO", i,
	            		orderParmAll.getInt("SEQ_NO", i));
	            orderparm.setData("ORDER_CODE", i,
	            		orderParmAll.getValue("ORDER_CODE", i));
	            orderparm.setData("MR_NO", i, mrNo);
	            orderparm.setData("DR_CODE", i,
	            		orderParmAll.getValue("DR_CODE", i));
	            //-------modify caoyong 20130114  start
	            Timestamp orderDttm = StringTool.getTimestamp(orderParmAll.getValue("ORDER_DATE",i),"yyyy/MM/dd HH:mm:ss");
	            orderparm.setData("ORDER_DTTM",i,orderDttm);
	          //-------modify caoyong 20130114 end 
	            
	            orderparm.setData("LINK_MAIN_FLG", i, "Y".equals(orderParmAll.getValue("LINKMAIN_FLG",i)) ? "Y" : "N");

	            orderparm.setData("LINK_NO", i,
	            		orderParmAll.getValue("LINK_NO",i) == null ? 0 :
	            			orderParmAll.getValue("LINK_NO",i));
	            orderparm.setData("ROUTE_CODE", i,
	            		orderParmAll.getValue("ROUTE_CODE", i));
	            orderparm.setData("FREQ_CODE", i,
	            		orderParmAll.getValue("FREQ_CODE", i));
	            orderparm.setData("TAKE_DAYS", i,
	            		orderParmAll.getData("TAKE_DAYS", i));
	            // 调用方法取得瓶签号
	            orderparm.setData("BAR_CODE", i, this.getBarCode(orderParmAll.getValue("RX_NO", i),
	            		orderParmAll.getValue("LINK_NO",i)));
	            //需要打印的瓶签 的医嘱 add caoyon 2014/4/4 start
	            if( "Y".equals(orderParmAll.getValue("LINKMAIN_FLG",i))&&!"".equals(orderParmAll.getData("LINK_NO",i).toString())){
	            	if(!"0".equals(orderParmAll.getData("LINK_NO",i))){
	            		orderparm.setData("BAR_CODE_PRINT_FLG", i,"Y");
	            	}
	            }else{
	            	   orderparm.setData("BAR_CODE_PRINT_FLG", i, "N");
	            }
	            //需要打印的瓶签 的医嘱 add caoyon 2014/4/4 end 
	            orderparm.setData("BAR_CODE_PRINT_FLG", i, "Y");
	            orderparm.setData("EXEC_STATUS", i, "0");
	            orderparm.setData("DR_NOTE", i,
	            		orderParmAll.getValue("DR_NOTE",i));
	            orderparm.setData("NS_NOTE", i,
	            		orderParmAll.getValue("NS_NOTE", i));
	            orderparm.setData("OPT_USER", i, Operator.getID());
	            orderparm.setData("OPT_DATE", i, date);
	            orderparm.setData("OPT_TERM", i, Operator.getIP());
	            result = PhlOrderTool.getInstance().onInsert(orderparm.getRow(i));
	            if (result.getErrCode() < 0) {
	                err("ERR:" + result.getErrCode() + result.getErrText()
	                    + result.getErrName());
	                return ;
	            }
			}
		}
		this.closeWindow();
	}

	 /**
     * 取得瓶签号
     * @param orderNo String
     * @param linkNo String
     * @return String
     */
    private String getBarCode(String orderNo, String linkNo) {
        if ("".equals(linkNo)) {
            return "";
        }
        linkNo = "00".substring(0, 2 - linkNo.length()) + linkNo.trim();
        return orderNo + linkNo;
    }
}
