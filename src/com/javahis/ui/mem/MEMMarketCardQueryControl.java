package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 会员卡售卡查询</p>
 *
 * <p>Description: 会员卡售卡查询</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Company: bluecore</p>
 *
 * @author huangtt 20140120
 * @version 1.0
 */

public class MEMMarketCardQueryControl extends TControl{
	
	private TTable table;
	Pat pat;
	//日期格式化
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyy/MM/dd");
	//判断单选框是否勾选   add by sunqy 20140521
	TCheckBox select;
	/**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        //initData();//modify by sunqy 20140521
        initComponent();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm acceptData = (TParm) obj;
            String mrNo = acceptData.getData("MR_NO").toString();
            this.setValue("MR_NO", mrNo);
            //add by sunqy 20140521  ----start----
//            String checkBox = acceptData.getData("CKECK_BOX").toString();
            String checkBox = acceptData.getValue("CKECK_BOX");
            if (checkBox == "Y" || "Y".equals(checkBox)) {
            	select.setSelected(true);
            	callFunction("UI|START_DATE|setEnabled", true);//开始日期可用
    			callFunction("UI|END_DATE|setEnabled", true);//结束日期可用
            	initData();
            }
            if (checkBox == "N" || "N".equals(checkBox)) {
            	select.setSelected(false);
            	callFunction("UI|START_DATE|setEnabled", false);//开始日期置灰
    			callFunction("UI|END_DATE|setEnabled", false);//结束日期置灰
            }
            //add by sunqy 20140521  ----end----
            this.onMrno();
        }
        
        this.initTable();
    }
    
    /**
     * 
     */
    public void initTable(){
		String sql = "SELECT  A.PAYTYPE,A.GATHER_TYPE,B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A ,SYS_DICTIONARY B" +
		" WHERE A.GATHER_TYPE= B.ID AND B.GROUP_ID = 'GATHER_TYPE'"; 
		TParm payTypeParm = new TParm(TJDODBTool.getInstance().select(sql));
		String header = "会员卡号,120;病案号,100;姓名,80;会员卡类型,100,MEM_CODE;会费,80,double,###0.00;身份编码,150;操作人员,100,OPT_USER;操作时间,100;办/退,100;原因,100";
		String parmMap = "MEM_CARD_NO;MR_NO;PAT_NAME;MEM_CODE;MEM_FEE;IDNO;OPT_USER;OPT_DATE;STATUS;REASON";
		String aa = "1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,right;10,left";
		for (int i = 0; i < payTypeParm.getCount(); i++) {
			header = header + ";"+payTypeParm.getValue("CHN_DESC", i)+",100";
			parmMap = parmMap +";"+payTypeParm.getValue("PAYTYPE", i);
			aa = aa +";"+(10+i)+",right";
		}
		aa = aa +";"+(10+payTypeParm.getCount())+",left";
		table.setHeader(header);
		table.setParmMap(parmMap);
		table.setColumnHorizontalAlignmentData(aa);
	}
    
    /**
     * 初始化数据
     */
    public void initData() {
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
    	//Timestamp now = StringTool.getTimestamp(new Date());
//    	this.setValue("START_DATE",now.toString().substring(0, 10).replace('-', '/')
//					+ " 00:00:00");
    	//开始时间为上个月-默认
   	 	Calendar cd = Calendar.getInstance();
   	 	cd.add(Calendar.MONTH, -1);
   	 	String format = formateDate.format(cd.getTime());
		this.setValue("START_DATE", formateDate.format(cd.getTime())+" 00:00:00");
		this.setValue("END_DATE", now.toString().substring(0, 10).replace('-', '/')
					+ " 23:59:59");
    	
    }
    
    /**
	 * 初始化控件
	 */
    private void initComponent() {
    	//add by sunqy 20140521  ----start----
    	select = (TCheckBox) this.getComponent("CHECK_BOX");
    	callFunction("UI|START_DATE|setEnabled", false);//开始日期置灰
		callFunction("UI|END_DATE|setEnabled", false);//结束日期置灰
		//add by sunqy 20140521  ----end----
    	table = getTable("TABLE");
    }
    
    /**
     * 查询
     */
    public void onQuery() {
    	String mrNo = this.getValueString("MR_NO");
//    	if(mrNo==null || mrNo.length()<=0){
//    		this.messageBox("病案号不能为空！");
//    		this.grabFocus("MR_NO");
//    		return;
//    	}else{
//    		onMrno(); 
//    	}
    	onQueryHisInfo();
    	
    }
    
    /**
     * 查询历史信息
     */
    public void onQueryHisInfo(){
    	String mrNo = this.getValueString("MR_NO");
    	String sql = getSql(mrNo);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()<=0){
    		this.messageBox("查无记录！");
    		table.removeRowAll();
    		return;
    	}
    	DecimalFormat df = new DecimalFormat("##########0.00");
    	for(int i=0;i<parm.getCount();i++){
			parm.setData("PAY_TYPE01", i, df.format(parm.getDouble("PAY_TYPE01", i)));
			parm.setData("PAY_TYPE02", i, df.format(parm.getDouble("PAY_TYPE02", i)));
			parm.setData("PAY_TYPE03", i, df.format(parm.getDouble("PAY_TYPE03", i)));
			parm.setData("PAY_TYPE04", i, df.format(parm.getDouble("PAY_TYPE04", i)));
			parm.setData("PAY_TYPE05", i, df.format(parm.getDouble("PAY_TYPE05", i)));
			parm.setData("PAY_TYPE06", i, df.format(parm.getDouble("PAY_TYPE06", i)));
			parm.setData("PAY_TYPE07", i, df.format(parm.getDouble("PAY_TYPE07", i)));
			parm.setData("PAY_TYPE08", i, df.format(parm.getDouble("PAY_TYPE08", i)));
			parm.setData("PAY_TYPE09", i, df.format(parm.getDouble("PAY_TYPE09", i)));
			parm.setData("PAY_TYPE10", i, df.format(parm.getDouble("PAY_TYPE10", i)));
			parm.setData("PAY_TYPE11", i, df.format(parm.getDouble("PAY_TYPE11", i)));
			parm.setData("PAY_MEDICAL_CARD", i, df.format(parm.getDouble("PAY_MEDICAL_CARD", i)));
    	}
    	table.setParmValue(parm);
    }
    
    /**
     * 病案号回车  
     */
    public void onMrno() {
    	pat = new Pat();
        String mrno = getValue("MR_NO").toString().trim();
        if (!this.queryPat(mrno))
            return;
        pat = pat.onQueryByMrNo(mrno);
        //System.out.println("pat="+pat);
        if (pat == null || "".equals(getValueString("MR_NO"))) {
            this.messageBox_("查无病患! ");
            this.onClear(); // 清空
            return;
        }else{
        	this.setValue("MR_NO", pat.getMrNo());//病案号
        	//this.setValue("PAT_NAME", pat.getName());//姓名
        	//this.setValue("SEX_CODE", pat.getSexCode());//性别
        	//病案号置无效
//        	callFunction("UI|MR_NO|setEnabled", false); 
        	//查询操作
        	onQuery();
        }
        
    }
    
    /**
     * 查询病患信息
     * @param mrNo String
     * @return boolean
     */
    public boolean queryPat(String mrNo) {
        //this.setMenu(false); //MENU 显示控制
        pat = new Pat();
        pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            //this.setMenu(false); //MENU 显示控制
            this.messageBox("E0081");
            return false;
        }
        String allMrNo = PatTool.getInstance().checkMrno(mrNo);
        if (mrNo != null && !allMrNo.equals(pat.getMrNo())) {
            //============xueyf modify 20120307 start
            messageBox("病案号" + allMrNo + " 已合并至" + pat.getMrNo());
            //============xueyf modify 20120307 stop
        }

        return true;
    }
    
    /**
     * 清除
     */
    public void onClear() {
    	//病案号置有效
    	callFunction("UI|MR_NO|setEnabled", true); 
    	this.clearValue("MR_NO;MEM_CODE");
    	Timestamp now = TJDODBTool.getInstance().getDBTime();
//    	this.setValue("START_DATE",now.toString().substring(0, 10).replace('-', '/')
//					+ " 00:00:00");
    	//开始时间为上个月-默认
   	 	Calendar cd = Calendar.getInstance();
   	 	cd.add(Calendar.MONTH, -1);
   	 	String format = formateDate.format(cd.getTime());
		this.setValue("START_DATE", formateDate.format(cd.getTime())+" 00:00:00");
		this.setValue("END_DATE", now.toString().substring(0, 10).replace('-', '/')
					+ " 23:59:59");
    	table.removeRowAll();
    }
    
    /**
     * 获取sql
     */
    public String getSql(String mrNo){
    	String startDate = getValueString("START_DATE");
		String endDate = getValueString("END_DATE");
		if(startDate.length()>0){
			startDate = startDate.substring(0, startDate.lastIndexOf(".")).replace(":", "")
			.replace("-", "").replace(" ", "");
		}
		if(endDate.length()>0){
			endDate = endDate.substring(0, endDate.lastIndexOf(".")).replace(":", "")
			.replace("-", "").replace(" ", "");
		}
    	String memCode = this.getValueString("MEM_CODE");
    	
    	String sql = "SELECT ROWNUM AS ID,CASE STATUS WHEN '0' THEN '预办卡' WHEN '1' THEN '已办卡' " +
    			" WHEN '2' THEN '退卡' WHEN '3' THEN '停卡' END STATUS,A.TRADE_NO,A.MR_NO,A.MEM_CODE,A.MEM_DESC,A.MEM_CARD_NO,A.MEM_FEE," +
    			" A.START_DATE,A.END_DATE,A.DESCRIPTION,A.OPT_DATE,A.OPT_USER,A.OPT_TERM,A.DESCRIPTION REASON," +
    			" A.LAST_DEPRECIATION_END_DATE,A.INTRODUCER1,A.INTRODUCER2,A.INTRODUCER3,A.GATHER_TYPE,B.PAT_NAME,B.IDNO, " +
    			" A.PAY_TYPE01,A.PAY_TYPE02,A.PAY_TYPE03," + 
    			" A.PAY_TYPE04,A.PAY_TYPE05,A.PAY_TYPE06,A.PAY_TYPE07,A.PAY_TYPE08,A.PAY_TYPE09," + 
    			" A.PAY_TYPE10,A.PAY_TYPE11,A.PAY_MEDICAL_CARD" +
    			" FROM MEM_TRADE A, SYS_PATINFO B WHERE A.MR_NO=B.MR_NO  ";
    	if(mrNo.length()>0){
    		sql +=" AND A.MR_NO = '"+mrNo+"' ";
    	}
    	if(startDate.trim().length()>0 || endDate.trim().length()>0){
    		sql += " AND A.START_DATE BETWEEN TO_DATE('"+startDate+ "','YYYYMMDDHH24MISS') " + 
    			" AND TO_DATE('" + endDate + "','YYYYMMDDHH24MISS')"; 
    	}
    	if(memCode.length()>0){
    		sql += " AND A.MEM_CODE = '"+memCode+"' ";
    	}
    	System.out.println("sql="+sql);
    	return sql;
    }
    
    /**
	 * 得到页面中Table对象
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}
	
	/**
	 * 单选框点击事件 add by sunqy 20140521
	 */
	public void onSelect(){
		if(select.isSelected()){
			this.initData();
			callFunction("UI|START_DATE|setEnabled", true);//开始日期可用
			callFunction("UI|END_DATE|setEnabled", true);//结束日期可用
		}else{
			callFunction("UI|START_DATE|setEnabled", false);//开始日期置灰
			callFunction("UI|END_DATE|setEnabled", false);//结束日期置灰
			this.clearValue("START_DATE;END_DATE");
		}
	}
}
