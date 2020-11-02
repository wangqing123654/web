package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.util.StringTool;

/**
*
* <p>Title: 患者保险信息维护页面</p>
*
* <p>Description: 患者保险信息维护页面</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author sunqy 20140512
* @version 4.5
*/

public class MEMInsureInfoControl extends TControl{
	
//	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	
	private static TTable table;
	
	private boolean flg = false;
	
	private String acceptEdit = "";
	
	public MEMInsureInfoControl(){
		super();
	}
	
	/**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        table = (TTable) getComponent("TABLE"); 
        //初始化保险时间
//        timerInit();
        //初始化控件
        ComponentInit();
        //当前时间
        this.setValue("IN_DATE",StringTool.getTimestamp(new Date()).toString().substring(0, 10).replace('-', '/'));//当前时间
        //接收上个页面传来的MR_NO
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm acceptData = (TParm) obj;
            String mrNo = acceptData.getData("MR_NO").toString();
            this.setValue("MR_NO", mrNo);
            //System.out.println("------------->"+mrNo);
            onMrno();
            setBirth();
            String edit = acceptData.getData("EDIT").toString();
            if("N".equals(edit)){
            	acceptEdit = edit;
            	callFunction("UI|save|setEnabled", false); //保存按钮置灰
//        		callFunction("UI|new|setEnabled", false); //新增按钮置灰//==liling 20140812 屏蔽
        		callFunction("UI|query|setEnabled", false); //查询按钮置灰
        		callFunction("UI|delete|setEnabled", false); //删除按钮置灰
        		callFunction("UI|clear|setEnabled", false); //清空按钮置灰
//        		callFunction("UI|MR_NO|setEnabled", true); //病案号可用
        		callFunction("UI|CONTRACTOR_CODE|setEnabled", false); //保险公司置灰
        		callFunction("UI|INSURANCE_NUMBER|setEnabled", false); //保险卡号置灰
        		callFunction("UI|INSURE_PAY_TYPE|setEnabled", false); //先期支付类型置灰
        		callFunction("UI|START_DATE|setEnabled", false); //保险开始日期置灰
        		callFunction("UI|END_DATE|setEnabled", false); //保险结束日期置灰
        		callFunction("UI|INSURANCE_CLAUSE_O|setEnabled", false); //门急诊保险细则置灰
        		callFunction("UI|INSURANCE_CLAUSE_I|setEnabled", false); //住院保险细则置灰
        		callFunction("UI|VALID_FLG|setEnabled", false); //有效checkBox置灰
        		callFunction("UI|DEPT_FLG|setEnabled", false); //欠费checkBox置灰
        		callFunction("UI|INSURANCE_BILL_NUMBER|setEnabled", false); //保险单号置灰
            }
        }
    }
    
	/**
	 * 初始化保险时间
	 */
//	private void timerInit() {
//		//保险生效日-次日0点
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, +1);
//		String insureStartDate = sdf.format(calendar.getTime())+ " 00:00:00";
//		this.setValue("START_DATE",insureStartDate);
//		//保险失效日，默认往后推迟一个月
//		Calendar calendar1 = Calendar.getInstance();
//		calendar1.add(Calendar.MONTH, +1);
//		String insureEndDate = sdf.format(calendar1.getTime())+ " 23:59:59";
//		this.setValue("END_DATE",insureEndDate);
//	}
	
    /**
     * 初始化控件
     */
	private void ComponentInit() {
		callFunction("UI|save|setEnabled", true); //保存按钮置灰
		callFunction("UI|delete|setEnabled", false); //删除按钮置灰	
		callFunction("UI|MR_NO|setEnabled", false); //病案号置灰
		callFunction("UI|PAT_NAME|setEnabled", false); //姓名置灰
		callFunction("UI|FIRST_NAME|setEnabled", false); //FIRSTNAME置灰
		callFunction("UI|LAST_NAME|setEnabled", false); //LASTNAME置灰
		callFunction("UI|SEX_CODE|setEnabled", false); //性别置灰
		callFunction("UI|BIRTH_DATE|setEnabled", false); //出生日期置灰
		callFunction("UI|AGE|setEnabled", false); //年龄置灰
		callFunction("UI|ID_TYPE|setEnabled", false); //证件类型置灰
		callFunction("UI|IDNO|setEnabled", false); //证件号置灰
	}
	
	/**
	 * 病案号查询
	 */
	public void onQuery(){
		onMrno();
	}
	public void onMrno(){
		if(acceptEdit!="N"){//==liling 20140812 add
		callFunction("UI|save|setEnabled", true); //保存按钮可用//==liling 20140812 取消屏蔽
		callFunction("UI|delete|setEnabled", true); //删除按钮可用
//		callFunction("UI|new|setEnabled", true); //新增按钮可用//==liling 20140812 屏蔽
		}
		//查询SYS_PATINFO数据
		TParm mrNoParm = new TParm();
		mrNoParm.setData("MR_NO", this.getValueString("MR_NO"));
		TParm sysParm = TIOM_AppServer.executeAction("action.mem.MEMInsureInfoAction",
				"onQuery", mrNoParm);
		//System.out.println("--------------!!!!sysParm=="+sysParm);
		//System.out.println("--------------!!!!mrNoParm=="+mrNoParm);
		if(sysParm.getCount()>0){
			//把数据set到页面上面TParm
    		this.setMemPatinfoForUI(sysParm);
    		//查询MEM_INSURE_INFO
    		TParm memTradeParm = TIOM_AppServer.executeAction("action.mem.MEMInsureInfoAction",
    				"onQueryMEM", mrNoParm);
    		if(memTradeParm.getCount()>0){
    			for(int i=0;i<memTradeParm.getCount();i++){
    				if("Y".equals(memTradeParm.getValue("VALID_FLG", i)))
    					memTradeParm.setData("VALID_FLG",i,"是");
    				else
    					memTradeParm.setData("VALID_FLG",i,"否");
    			}
    			//把数据set到页面上左下TABLE
    			table.setParmValue(memTradeParm);
    		}
		}
		setBirth();
	}
	 /**
     * 病患会员信息赋值
     */
    public void setMemPatinfoForUI(TParm parm) {
    	this.setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
    	this.setValue("FIRST_NAME", parm.getValue("FIRST_NAME", 0));
    	this.setValue("LAST_NAME", parm.getValue("LAST_NAME", 0));
    	this.setValue("SEX_CODE", parm.getValue("SEX_CODE", 0));
    	this.setValue("ID_TYPE", parm.getValue("ID_TYPE", 0));
    	this.setValue("IDNO", parm.getValue("IDNO", 0));
    	String bDate = parm.getValue("BIRTH_DATE", 0);
    	bDate = bDate.substring(0, 10).replaceAll("-", "/");
    	this.setValue("BIRTH_DATE", bDate);
    }
	/**
	 * 保存(针对修改功能)
	 */
	public void onSave(){
		
		if(!checkData()){
			return;
		}
		//==liling 20140812 modify start=====
		/** //============先判断此保险信息是否是有效的，如果是有效的，则直接进行下面的保存；如果不是有效的，同新增情况一样
		int i=table.getSelectedRow();
		TParm tableParm=table.getParmValue();
		if(i>=0){//==liling 20140812：此段代码逻辑是原先 保存功能 分为新增与修改 俩功能时的修改功能的部分逻辑；
			String insurance_no=tableParm.getValue("INSURANCE_NUMBER", i);
			TParm exitParm=new TParm(TJDODBTool.getInstance().select("SELECT * FROM MEM_INSURE_INFO WHERE INSURANCE_NUMBER='"+insurance_no+"'"));
			if(!"Y".equals(exitParm.getValue("VALID_FLG",0))){
				if(getCheckBox("VALID_FLG").isSelected()){
					if(!isExit()){
						onClear();
						return;
					}
				}
			}
		}
		//============ */
		if(getCheckBox("VALID_FLG").isSelected()){
			if(!isExit()){// isExit()=false 该病患已存在有效保险信息
				onClear();
				return;
			}
		}//==liling 20140812 modify end=====
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("CONTRACTOR_CODE", this.getValueString("CONTRACTOR_CODE"));
		parm.setData("INSURANCE_NUMBER", this.getValueString("INSURANCE_NUMBER"));
		
		parm.setData("INSURANCE_BILL_NUMBER",this.getValueString("INSURANCE_BILL_NUMBER"));
		parm.setData("VALID_FLG",this.getValueString("VALID_FLG"));
		parm.setData("DEPT_FLG",this.getValueString("DEPT_FLG"));
		//parm.setData("DETP_ATM",this.getValueString("DEPT_ATM"));
		parm.setData("AMT",this.getValueDouble("AMT"));
		parm.setData("INSURE_PAY_TYPE", this.getValueString("INSURE_PAY_TYPE"));
		String startDate = this.getValueString("START_DATE").toString().replaceAll("-", "/").replaceAll(":", "").substring(0,10);
		parm.setData("START_DATE", startDate);
		String endDate = this.getValueString("END_DATE").toString().replaceAll("-", "/").replaceAll(":", "").substring(0,10);
		parm.setData("END_DATE", endDate);
		parm.setData("INSURANCE_CLAUSE_O", this.getValueString("INSURANCE_CLAUSE_O"));
		parm.setData("INSURANCE_CLAUSE_I", this.getValueString("INSURANCE_CLAUSE_I"));
		parm.setData("MEMO", this.getValueString("MEMO"));//==liling 20140714 add 保险欠费备注
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		//System.out.println("parm=============================="+parm);
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMInsureInfoAction",
				"update", parm);
		if(result.getErrCode()<0){
			this.messageBox("保存失败!");
			return;
		}else{
			this.messageBox("保存成功!");
		}
		//==liling modify 20140714 start 保险欠费标记同步到病患信息表===
		 String sql2="UPDATE SYS_PATINFO SET  DEPT_FLG='"+parm.getValue("DEPT_FLG")+"' WHERE MR_NO='"+parm.getValue("MR_NO")+"'";
        TJDODBTool.getInstance().update(sql2);
      //==liling modify 20140714 end===
		onQuery();
		onClear();
	}
	
	/**
	 * 删除
	 */
	public void onDelete() {
		TParm result = new TParm();
		TParm parm = table.getParmValue();
		if (parm.getCount() <= 0) {

			this.messageBox("没有保存数据");
			return;
		}
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			this.messageBox("请选择要删除的数据");
			return;
		}
		if (this.messageBox("是否删除", "确认要删除吗？", 2) != 0) {
			return;
		}else{
			// TParm tparm=table.getParmValue().getRow(selectedRow);//@这里不对,不应该是table的MR_NO
			TParm dparm = new TParm();
			dparm.setData("MR_NO", this.getValueString("MR_NO"));
			dparm.setData("CONTRACTOR_CODE", parm.getValue("CONTRACTOR_CODE",selectedRow));
			dparm.setData("INSURANCE_NUMBER", parm.getValue("INSURANCE_NUMBER",selectedRow));
//			System.out.println("dparm=======>"+dparm);
			result = TIOM_AppServer.executeAction(
					"action.mem.MEMInsureInfoAction", "delete", dparm);
		}
		if (result.getErrCode() < 0) {
			this.messageBox(" 删除失败");
			return;
		} else {
			this.messageBox("删除成功");
			table.removeRowAll();
			onMrno();
		}
		onClear();
		
	}
	
	/**
	 * 新增
	 
	private void onNew(){
		
		if(!checkData()){
			return;
		}
//		onClear();
//		callFunction("UI|delete|setEnabled", false); //删除按钮置灰
//		callFunction("UI|query|setEnabled", false); //查询按钮置灰
//		callFunction("UI|save|setEnabled", true); //保存按钮可用
//		callFunction("UI|MR_NO|setEnabled", false); //病案号置灰
//		callFunction("UI|IDNO|setEnabled", false); //证件号置灰
		//判断该病患是否已经存在有效的保险信息，如果存在则不可再添加有效的保险信息了
		if(getCheckBox("VALID_FLG").isSelected()){
			if(!isExit()){
				onClear();
				return;
			}
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("CONTRACTOR_CODE", this.getValueString("CONTRACTOR_CODE"));
		parm.setData("INSURANCE_NUMBER", this.getValueString("INSURANCE_NUMBER"));
		
		parm.setData("INSURANCE_BILL_NUMBER",this.getValueString("INSURANCE_BILL_NUMBER"));
		parm.setData("VALID_FLG",this.getValueString("VALID_FLG"));
		parm.setData("DEPT_FLG",this.getValueString("DEPT_FLG"));
		parm.setData("AMT",this.getValueDouble("AMT"));
		parm.setData("INSURE_PAY_TYPE", this.getValueString("INSURE_PAY_TYPE"));
//		System.out.println("startDate=="+this.getValueString("START_DATE"));
		String startDate = this.getValueString("START_DATE").toString().replaceAll("-", "/").replaceAll(":", "").substring(0,10);
		parm.setData("START_DATE", startDate);
		String endDate = this.getValueString("END_DATE").toString().replaceAll("-", "/").replaceAll(":", "").substring(0,10);
		parm.setData("END_DATE", endDate);
		parm.setData("INSURANCE_CLAUSE_O", this.getValueString("INSURANCE_CLAUSE_O"));
		parm.setData("INSURANCE_CLAUSE_I", this.getValueString("INSURANCE_CLAUSE_I"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("MEMO", this.getValueString("MEMO"));//==liling 20140714 add 保险欠费备注
		
		TParm result = TIOM_AppServer.executeAction("action.mem.MEMInsureInfoAction",
				"onSave", parm);
//		System.out.println("resultLLLLLLLL"+result);
		if(result.getErrCode()<0){
			this.messageBox("新增失败");
			onClear();
			this.grabFocus("CONTRACTOR_CODE");
		}else{
			this.messageBox("新增成功");
		}
		//==liling modify 20140714 start 保险欠费标记同步到病患信息表===
		 String sql2="UPDATE SYS_PATINFO SET  DEPT_FLG='"+parm.getValue("DEPT_FLG")+"' WHERE MR_NO='"+parm.getValue("MR_NO")+"'";
         TJDODBTool.getInstance().update(sql2);
       //==liling modify 20140714 end===
		onClear();
		onMrno();
	}
	*/
	
	/**
	 * 判断病患是否存在已经存在有效的保险信息
	 */
	public boolean isExit(){
		String mr_no=this.getValueString("MR_NO");
		String contractor_code=this.getValueString("CONTRACTOR_CODE");
		String insurance_number=this.getValueString("INSURANCE_NUMBER");
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT VALID_FLG FROM MEM_INSURE_INFO WHERE MR_NO ='"+mr_no+"' " +
						"AND CONTRACTOR_CODE = '"+contractor_code+"' AND INSURANCE_NUMBER = '"+insurance_number+"'"));
		TParm exitParm=new TParm(TJDODBTool.getInstance().select("SELECT VALID_FLG FROM MEM_INSURE_INFO WHERE MR_NO='"+mr_no+"'"));
		if (exitParm.getErrCode() < 0) {
	           messageBox(exitParm.getErrText());
	           return false;
        }
		if(exitParm.getCount()>0){
			if("Y".equals(parm.getValue("VALID_FLG", 0))){
			}else{
				for(int i=0;i<exitParm.getCount();i++){
					if("Y".equals(exitParm.getValue("VALID_FLG",i))){
						messageBox("该患者已经存在有效保险信息");
						getCheckBox("VALID_FLG").setSelected(false);
						return false;
					}else{
						continue;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
//		callFunction("UI|IDNO|setEnabled", false); //证件号置灰
		callFunction("UI|save|setEnabled", true); //保存按钮置灰
		callFunction("UI|delete|setEnabled", false); //删除按钮置灰
//		callFunction("UI|MR_NO|setEnabled", true); //病案号可用
		callFunction("UI|query|setEnabled", true); //查询按钮可用
//		callFunction("UI|new|setEnabled", true); //新增按钮可用//==liling 20140812 屏蔽
		callFunction("UI|CONTRACTOR_CODE|setEnabled", true); //保险公司可用
		callFunction("UI|INSURANCE_NUMBER|setEnabled", true); //保险卡号可用
		callFunction("UI|DEPT_ATM|setEnabled",true);//欠费金额置灰
		this.clearValue("CONTRACTOR_CODE;INSURANCE_NUMBER;INSURE_PAY_TYPE;START_DATE;" +
				"END_DATE;INSURANCE_BILL_NUMBER;VALID_FLG;DEPT_FLG;AMT;MEMO;INSURANCE_CLAUSE_O;INSURANCE_CLAUSE_I;MEMO;START_DATE;END_DATE");
		isSelectDeptFlg();
		this.grabFocus("CONTRACTOR_CODE");
//		table.setSelectedRow(-1);//==liling 20140812 add
		onQuery();
//		table.removeRowAll();
//		timerInit();
	}
	
	/**
	 * 表格单击事件-在右边栏目显示信息,保险公司和保险号不允许修改
	 */
	public void onTableClick(){
		if(acceptEdit!="N"){
		callFunction("UI|save|setEnabled", true); //保存按钮可用
		callFunction("UI|delete|setEnabled", true);
//		callFunction("UI|new|setEnabled", false); //新增按钮置灰//==liling 20140812 屏蔽
		}
		if(acceptEdit=="N"){
			callFunction("UI|CONTRACTOR_CODE|setEnabled", false); //保险公司置灰
			callFunction("UI|INSURANCE_NUMBER|setEnabled", false); //保险卡号置灰
		}
		int row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		String mrNo = this.getValueString("MR_NO");
		String contractorCode = tableParm.getValue("CONTRACTOR_CODE", row);
		String insuranceNumber = tableParm.getValue("INSURANCE_NUMBER",row);
		String sql = "SELECT INSURE_PAY_TYPE,START_DATE,END_DATE,INSURANCE_CLAUSE_O,INSURANCE_CLAUSE_I,VALID_FLG,DEPT_FLG,AMT,MEMO,INSURANCE_BILL_NUMBER " +
				"FROM MEM_INSURE_INFO " +
				"WHERE MR_NO='"+mrNo+"' AND CONTRACTOR_CODE='"+contractorCode+"' AND INSURANCE_NUMBER='"+insuranceNumber+"'";
//		System.out.println("-------sql=="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("-------parm-->"+parm);
		this.setValue("CONTRACTOR_CODE", contractorCode);
		this.setValue("INSURANCE_NUMBER", insuranceNumber);
		this.setValue("INSURE_PAY_TYPE", parm.getValue("INSURE_PAY_TYPE",0));
		String startDate = parm.getValue("START_DATE",0).toString().replaceAll("-", "/").substring(0, 10);
//		System.out.println("--------startDate-:>"+startDate);
		this.setValue("START_DATE", startDate);
		String endDate = parm.getValue("END_DATE",0).toString().replaceAll("-", "/").substring(0, 10);
//		System.out.println("--------endDate-:>"+endDate);
		this.setValue("END_DATE", endDate);
		this.setValue("INSURANCE_CLAUSE_O", parm.getValue("INSURANCE_CLAUSE_O",0));
		this.setValue("INSURANCE_CLAUSE_I", parm.getValue("INSURANCE_CLAUSE_I",0));
		
	   this.setValue("VALID_FLG", parm.getValue("VALID_FLG",0));
	   this.setValue("DEPT_FLG", parm.getValue("DEPT_FLG",0));
	   this.setValue("INSURANCE_BILL_NUMBER", parm.getValue("INSURANCE_BILL_NUMBER",0));
	   this.setValue("AMT", parm.getValue("AMT",0));
	   this.setValue("MEMO", parm.getValue("MEMO",0));//==liling 20140714 add 保险欠费备注
	   isSelectDeptFlg();
	   
	   
	}

	/**
	 * 选择证件事件
	 */
	public void onClickIdType(){
		callFunction("UI|IDNO|setEnabled", true); //证件号可用
	}
	
	/**
     * 计算年龄
     */
    public void setBirth() {
        if (getValue("BIRTH_DATE") == null || "".equals(getValue("BIRTH_DATE")))
            return;
        String AGE = com.javahis.util.DateUtil.showAge(
                (Timestamp) getValue("BIRTH_DATE"),
                (Timestamp) getValue("IN_DATE"));
        setValue("AGE", AGE);
    }
    /**
     * 回车光标跳转  
     * add by huangjw 20140808
     */
    public void onUnitCode(){
    	if(getCheckBox("DEPT_FLG").isSelected()){
    		getNumberTextField("AMT").grabFocus();
    	}else{
    		getTextArea("INSURANCE_CLAUSE_O").grabFocus();
    	}
    }
    /**
     * 检查页面元素
     */
    public boolean checkData(){
    	//检查病案号
    	if(this.getValueString("MR_NO")==null || "".equals(this.getValueString("MR_NO"))){
    		this.messageBox("病案号不能为空!");
    		return flg;
    	}
    	//检查保险公司
    	if(this.getValueString("CONTRACTOR_CODE")==null || "".equals(this.getValueString("CONTRACTOR_CODE"))){
    		this.messageBox("请填写保险公司");
    		return flg;
    	}
    	//检查保险卡号
    	if(this.getValueString("INSURANCE_NUMBER")==null || "".equals(this.getValueString("INSURANCE_NUMBER"))){
    		this.messageBox("请填写保险卡号");
    		return flg;
    	}
    	//检查保险卡号位数
    	if(this.getValueString("INSURANCE_NUMBER").length()>50){
    		this.messageBox("保险卡号不得超过50位");
    		return flg;
    	}
    	//检查保险开始日期
    	if(this.getValueString("START_DATE")==null || "".equals(this.getValueString("START_DATE"))){
    		this.messageBox("请填写保险开始日期!");
    		return flg;
    	}
    	//检查保险结束日期
    	if(this.getValueString("END_DATE")==null || "".equals(this.getValueString("END_DATE"))){
    		this.messageBox("请填写保险结束日期!");
    		return flg;
    	}
    	//比较开始时间与结束时间的大小
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    	try {
			Date startdate = sdf.parse(this.getValueString("START_DATE").toString().replaceAll("-", "/").substring(0, 10));
			Date enddate = sdf.parse(this.getValueString("END_DATE").toString().replaceAll("-", "/").substring(0, 10));
			int i = enddate.compareTo(startdate);
//			System.out.println("startdate=="+startdate+"    enddate=="+enddate+"    i=="+i);
			if(i<=0){
				this.messageBox("开始日期与结束日期不符合要求!");
				return flg;
			}
		} catch (ParseException e) {
		}
		return true;
    }
    /**
     * 获得TCheckBox
     */
    private TCheckBox getCheckBox(String tagName){
    	return (TCheckBox)getComponent(tagName);
    }
    /**
     * 获得TNumberTextField  add by huangjw 20140808
     * @param tagName
     * @return
     */
    private TNumberTextField getNumberTextField(String tagName){
    	return (TNumberTextField)getComponent(tagName);
    }
    /**
     * 获得TTextArea   add by huangjw 20140808
     * @param tagName
     * @return
     */
    private TTextArea getTextArea(String tagName){
    	return (TTextArea)getComponent(tagName);
    }
    /**
     * 欠费控件改变事件
     */
    public void isSelectDeptFlg(){
    	if(getCheckBox("DEPT_FLG").isSelected()){
    		callFunction("UI|AMT|setEnabled",true);
    		callFunction("UI|MEMO|setEnabled",true);
    	}
    	else{
    		callFunction("UI|AMT|setEnabled",false);
    		callFunction("UI|MEMO|setEnabled",false);
    		this.setValue("AMT", 0.00);
    		this.setValue("MEMO", "");
    	}
    }
    /**
     * 更换保险
     */
    public void onChange(){
    	TParm parm=new TParm();
    	parm.setData("MR_NO",this.getValue("MR_NO"));
    	parm.setData("PAT_NAME",this.getValue("PAT_NAME"));
    	parm.setData("AGE",this.getValue("AGE"));
    	parm.setData("SEX_CODE",this.getValue("SEX_CODE"));
    	parm=(TParm) this.openDialog("%ROOT%\\config\\mem\\MEMModifyInsureInfo.x",parm);
    }
}
