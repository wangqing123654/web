package com.javahis.ui.mem;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import jdo.bil.PaymentTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.mem.MEMPatRegisterTool;
import jdo.opb.OPBTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.odo.ODOMainTmplt;
import com.javahis.util.StringUtil;

/**
 * <p>Title:套餐销售 </p>
 *
 * <p>Description:套餐销售 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author duzhw 2014.01.08
 * @version 4.5
 */
public class MEMPackageSalesTimeSelControl extends TControl {
	public ODOMainTmplt odoMainTmplt;//add by huangjw 20141106
	private TParm parmEKT;//医疗卡集合
	private String ektOper = "";//支付方式 1：现金   2：医疗卡
	Pat pat;
	//String IPD_NO = "";
	String MR_NO = ""; 			// 病案号
	
	//套餐销售交易号
	String tradeNo = "";
	int SECTION_ID = 0;			//病患时程id编号
	//int SECTION_D_ID = 0;		//病患时程明细id编号
	
	//主、细TABLE//医嘱列表tTable
	private TTable table,orderTable;
	
	String sectionCode = "";	//时程编号
	String packageCode = "";	//套餐编号
	
	PaymentTool paymentTool;
	
	boolean oper = true;//全局操作
	boolean flage = false;//折扣设置内单选框操作 add by sunqy 20140704
	double discountPrice = 0.00;//折扣金额
	String  caseNo;//就诊号
	/**
     * 树根
     */
    private TTreeNode treeRoot;
    
    /**
     * 编号规则类别工具
     */
    SYSRuleTool ruleTool;
    
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
    TDataStore treeDataStore = new TDataStore();
    
    private DecimalFormat df = new DecimalFormat("##########0.00");
    /**
     * 初始化
     */
    public void onInit() { // 初始化程序
        super.onInit();
        table = getTable("TABLE");
        orderTable = getTable("ORDER_TABLE");
        
        this.grabFocus("MR_NO");//设置焦点
//        //清除两张表
//        table.removeRowAll();
//        orderTable.removeRowAll();
//        this.setValue("PRICE_TYPE", "01");
        // 初始化树
        onInitSelectTree();
        // 给tree添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        // 给Table添加侦听事件
//        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
//                         "onTableChangeValue");
        
//        table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
//				this, "onTableChangeValue");
        // Table监听事件编辑
        addEventListener("TABLE->" + TTableEvent.CREATE_EDIT_COMPONENT,
        "onCreateEditComoponentUD");
//        // 医嘱细相新增医嘱事件
//        addEventListener("ORDER_TABLE->" + TTableEvent.CREATE_EDIT_COMPONENT, this,
//                                     "onDetailCreateEditComponent");
        
        
        // 种树
        onCreatTree();
        callFunction("UI|new|setEnabled", false);
        onPageInit();
        TPanel p = (TPanel) getComponent("tPanel_PAY_TYPE");//获取支付方式tpanel
        try {
			paymentTool = new PaymentTool(p, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
        initComponent();
        initData();
        initTextFormat();
        this.setValue("PRICE_TYPE", "01");
        
        //失去焦点、获得焦点执行的事件 add by huangjw 20141106 start
		TNumberTextField ar_amt;
		ar_amt = (TNumberTextField) this.getComponent("AR_AMT");

		//
		ar_amt.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				// 失去焦点执行的代码
				try {
					onChangeValue();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			public void focusGained(FocusEvent e) {
				// 获得焦点执行的代码
			}

		});
		//失去焦点、获得焦点执行的事件 add by huangjw 20141106 end

		

    }
    /**
	 * 初始化控件
	 */
    private void initComponent() {
    	// 给ORDER_TABLE添加侦听事件
        addEventListener("ORDER_TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onOrderTableChangeValue");
        //已购套餐置灰
        callFunction("UI|package|setEnabled", false);
    }
    /**
     * 初始化控件
     */
    private void initTextFormat() {
		String sql = " SELECT ID, CHN_DESC NAME" + " FROM SYS_DICTIONARY"
				+ " WHERE GROUP_ID = 'MEM_PACKAGE_TYPE' "
				//
				+ "AND ACTIVE_FLG = 'Y' ORDER BY SEQ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));

		TTextFormat memCombo = (TTextFormat) getComponent("PRICE_TYPE");

		memCombo.setHorizontalAlignment(2);
		memCombo.setPopupMenuHeader("代码,100;名称,100");
		memCombo.setPopupMenuWidth(300);
		memCombo.setPopupMenuHeight(300);
		memCombo.setFormatType("combo");
		memCombo.setShowColumnList("NAME");
		memCombo.setValueColumn("ID");
		memCombo.setPopupMenuData(parm);
//		memCombo.setClickedAction("onQuery");
//		memCombo.addEventListener(TTextFormatEvent.EDIT_ENTER, this, "onQuery");

	}

    
    
    /**
     * 初始化数据
     */
    private void initData(){
    	//折扣默认为1,不打折
    	this.setValue("DISCOUNT_RATE", "1.00");
    	
    	TRadioButton ektPay = (TRadioButton) getComponent("gatherFlg2");
		TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
		ektPay.setSelected(false);
		cashPay.setSelected(true);
    	this.onGatherChange(0);
    }
    /**
     * 初始化树
     */
    public void onInitSelectTree() {
        // 得到树根
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null)
            return;
        // 给根节点添加文字显示
        treeRoot.setText("套餐管理");
        // 给根节点赋tag
        treeRoot.setType("Root");
        // 设置根节点的id
        treeRoot.setID("");
        // 清空所有节点的内容
        treeRoot.removeAllChildren();
        // 调用树点初始化方法
        callMessage("UI|TREE|update");
    }
    /**
     * 初始化树上的节点
     */
    public void onCreatTree() {
        // 给dataStore赋值
        treeDataStore.setSQL("SELECT PACKAGE_CODE, PARENT_PACKAGE_CODE,PACKAGE_DESC,"
                             + " PACKAGE_ENG_DESC, PY1, PY2, SEQ,DESCRIPTION,"
                             + "  ORIGINAL_PRICE, PACKAGE_PRICE, OPT_DATE, OPT_USER,"
                             + "  OPT_TERM  FROM MEM_PACKAGE"
                             + "  ");
        // 如果从dataStore中拿到的数据小于0
        if (treeDataStore.retrieve() <= 0)
            return;
        // 过滤数据,是编码规则中的科室数据
        ruleTool = new SYSRuleTool("SYS_MEMPACKAGE");
        if (ruleTool.isLoad()) { // 给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "PACKAGE_CODE", "PACKAGE_DESC", "Path", "SEQ");
            
            // 循环给树安插节点
            for (int i = 0; i < node.length; i++){
            	treeRoot.addSeq(node[i]);
//                System.out.println("node="+node[i]);
            }
                
        }
        // 得到界面上的树对象
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        // 更新树
        tree.update();
        // 设置树的默认选中节点
        tree.setSelectNode(treeRoot);
    }

    /**
     * 单击树
     *
     * @param parm
     *            Object
     */
    public void onTreeClicked(Object parm) { // 新增按钮不能用
        callFunction("UI|new|setEnabled", false);
        // 得到点击树的节点对象
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        // 得到table对象
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        // table接收所有改变值
        table.acceptText();
        // 如果点击的是树的根结点
//        System.out.println("根节点："+node.getType());
        if (node.getType().equals("Root")){
            // 根据过滤条件过滤Tablet上的数据
//        	String id = node.getID();
        	//openMainPackage();//调用套餐主档维护页面
//        	System.out.println("Root id="+id);
            table.setFilter(getQuerySrc());
        	//table.setFilter("");
        }else { // 如果点的不是根结点

            // 拿到当前选中的节点的id值
            String id = node.getID();
            packageCode = id;
            // 得到父ID
//            String parentID = node.getID();
//            int classify = 1;
//            if (parentID.length() > 0)
//                classify = ruleTool.getNumberClass(parentID) + 1;
            // 如果是最小节点,可以增加一行
//            if (classify > ruleTool.getClassifyCurrent()) {
        	if (isLeaf(id)) {
            	//判断病案号是否存在-先决条件
            	if(existMrNo()){
            		showSectionDetail(id);//时程明细
            	}else{
            		this.grabFocus("MR_NO");
            	}
                
            }else{
            	//onClear();
            }
            
            
            
//            // 拿到过滤条件
//            String s = getQuerySrc();
//            // 如果过滤条件中存在数据
//            if (s.length() > 0)
//                s += " AND ";
//            s += "PACKAGE_CODE like '" + id + "%'";
//            // table中的datastore中过滤数据添加条件
//            table.setFilter(s);
        }
        // 执行table的过滤
//        table.filter();
//        // 给table数据加排序条件
//        table.setSort("PACKAGE_CODE");
//        // table排序后重新赋值
//        table.sort();
//        // 得到父ID
//        String parentID = node.getID();
//        int classify = 1;
//        if (parentID.length() > 0)
//            classify = ruleTool.getNumberClass(parentID) + 1;
//        // 如果是最小节点,可以增加一行
//        if (classify > ruleTool.getClassifyCurrent()) {
//            callFunction("UI|new|setEnabled", true);
//        }
    }
    
    /**
     * 得到树对象
     *
     * @return TTree
     */
    public TTree getTree() {
        return (TTree) callFunction("UI|TREE|getThis");
    }
    /**
     * 初始化界面
     */
    public void onPageInit() {
        String s = "";
        //===========pangben modify 20110422 start
        if (null != Operator.getRegion() && !"".equals(Operator.getRegion()))
            s = " REGION_CODE='" + Operator.getRegion() + "' ";
        //===========pangben modify 20110422 stop

        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        table.setFilter(s);
        // 给table数据加排序条件
        table.setSort("DEPT_CODE");
        // table排序后重新赋值
        table.sort();
        // 执行table的过滤
        table.filter();
    }
    /**
     * 病患查询
     */
    public void onPatinfoQuery() {
        TParm sendParm = new TParm();
        TParm reParm = (TParm)this.openDialog(
                "%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
        if (reParm == null)
            return;
        this.setValue("MR_NO", reParm.getValue("MR_NO"));
        //System.out.println("病案号："+reParm.getValue("MR_NO"));
        this.onMrno();
    }
    
    /**
     * 病案号回车查询事件
     */
    public void onMrno() {
   	 	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
   	 	Date currentDate=SystemTool.getInstance().getDate();
        String date = df.format(currentDate);
        setValue("IN_DATE", StringTool.getTimestamp(date, "yyyyMMddHH")); // 操作日期
        pat = new Pat();
        String mrno = getValue("MR_NO").toString().trim();
        if (!this.queryPat(mrno))
            return;
        pat = Pat.onQueryByMrNo(mrno);
        if (pat == null || "".equals(getValueString("MR_NO"))) {
            this.messageBox_("查无病患! ");
            this.onClear(); // 清空
            //this.setUi(); // 病患信息可编辑
            //this.setUIAdmF(); // 住院登记信息不可编辑
            //this.setMenu(false);
            return;
        } else {
            callFunction("UI|MR_NO|setEnabled", false); // 病案号
            callFunction("UI|IPD_NO|setEnabled", false); // 住院号
            //已购套餐置有效
            callFunction("UI|package|setEnabled", true);
            //callFunction("UI|patinfo|setEnabled", true); // 病患信息
            //this.callFunction("UI|NEW_PAT|setText", "修改病患保存");
            //callFunction("UI|NEW_PAT|setEnabled", true); // 病患保存botton
            //callFunction("UI|PHOTO_BOTTON|setEnabled", true);
            MR_NO = pat.getMrNo();
        }
        
      //huangtt 20180515
		double payOther3 = EKTpreDebtTool.getInstance().getPayOther(pat.getMrNo(), EKTpreDebtTool.PAY_TOHER3);
		double payOther4 = EKTpreDebtTool.getInstance().getPayOther(pat.getMrNo(), EKTpreDebtTool.PAY_TOHER4);
		setValue("GIFT_CARD", payOther3);
		setValue("GIFT_CARD2", payOther4);
		setValue("NO_PAY_OTHER_ALL", getValueDouble("EKT_CURRENT_BALANCE") - payOther3 - payOther4);

        
        this.setPatForUI(pat);
        Timestamp t = StringTool.getTimestamp(date, "yyyyMMddHH");//add by lich 20141202 
        this.setValue("START_DATE", t);//add by lich 20141202 
//        //添加失效日期  start by huangjw 20141126
//        Calendar gc = Calendar.getInstance();
//        gc.setTime(currentDate);
//        gc.add(Calendar.YEAR,1);
//        gc.add(Calendar.DAY_OF_YEAR,-1);
//		this.setValue("END_DATE", gc.getTime());
//		//添加失效日期   end by huangjw 20141126
       
        t = StringTool.rollDate(t, 364);//add by lich 20141202 
        this.setValue("END_DATE", t);//add by lich 20141202 
        
        
        //如果树点击节点存在，则执行
        if(packageCode.length()>0){
        	showSectionDetail(packageCode);//时程明细
        }
    }
    /**
     * 病患信息赋值
     *
     * @param patInfo
     *            Pat
     */
    public void setPatForUI(Pat patInfo) {
        // 病案号,姓名,性别,生日,身份证号,电话
        this.setValueForParm(
                "MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;IDNO;TEL_HOME",patInfo.getParm());
        this.setValue("IPD_NO", pat.getIpdNo());
        this.setValue("CTZ_CODE1", patInfo.getCtz1Code());//增加身份显示add by huangjw 20141126
        
        setValue("CUSTOMER_SOURCE",MEMPatRegisterTool.getInstance().getMemCustomerSource(patInfo.getMrNo()));
		
        //System.out.println("tttttt:::"+patInfo.getParm());
        //add by sunqy 20140812 根据末次月经日期计算预产期信息  ----start----
        //modified by lich 20141016 ---start---
        String lmpStr = patInfo.getParm().getValue("LMP_DATE",0);
		if (!"".equals(lmpStr) && lmpStr != null) {//先判断LMP_DATE时间是否为空，否则执行会报错，setBirth()方法就不执行了 modify by huangjw 20141106
			Timestamp lmp = StringTool.getTimestamp(lmpStr, "yyyyMMddHHmmss");
			// modified by lich 20141016 ---end---

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lmp);
			calendar.add(Calendar.DAY_OF_YEAR, +279);
			String lmpDate = sdf.format(calendar.getTime());
			this.setValue("LMP_DATE", lmpDate);
		}
        //add by sunqy 20140812 根据末次月经日期计算预产期信息  ----end----
        setBirth(); // 计算年龄
    }
    /**
     * 计算年龄
     */
    public void setBirth() {
        if ("".equals(this.getValueString("BIRTH_DATE")))
            return;
//        String AGE = com.javahis.util.StringUtil.showAge(
//                (Timestamp) getValue("BIRTH_DATE"),
//                (Timestamp) getValue("IN_DATE"));
        String AGE = com.javahis.util.DateUtil.showAge(
                (Timestamp) getValue("BIRTH_DATE"),
                (Timestamp) getValue("IN_DATE"));
        setValue("AGE", AGE);
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
    
//    /**
//     * 根据就诊日期获取就诊号 add by huangjw 20141027
//     */
//    public void getCaseNo(){
//    	if("".equals(this.getValueString("MR_NO"))){
//    		this.messageBox("请输入病案号！");
//    		return;
//    	}
//    	String adm_date=this.getValueString("ADM_DATE");
//    	String adm_date_q=adm_date.substring(0,4)+adm_date.substring(5,7)+adm_date.substring(8,10);
//    	TParm result=new TParm(TJDODBTool.getInstance().select("SELECT CASE_NO FROM REG_PATADM WHERE" 
//    			+" MR_NO='"+this.getValueString("MR_NO")+"' AND ADM_DATE=TO_DATE('"+adm_date_q+"','YYYYMMDDHH24MISS')"));
//    	if(result.getCount()<0){
//    		this.messageBox("没有就诊记录");
//    		
//    	}else{
//    		if(result.getCount()==1){
//    			caseNo=result.getValue("CASE_NO",0);
//    		}else{
//    			TParm parm=new TParm();
//    			parm.setData("MR_NO", pat.getMrNo());
//    			parm.setData("PAT_NAME", pat.getName());
//    			parm.setData("SEX_CODE", pat.getSexCode());
//    			parm.setData("AGE", this.getValue("AGE"));
//    			// 判断是否从明细点开的就诊号选择
//    			parm.setData("count", "0");
//    			caseNo=(String) openDialog(
//    					"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
//    			
//    		}
//    			orderTable(caseNo);
//    	}
//    	
//    }
   /* *//**
     * 根据就诊号查询医嘱列表 add by huangjw 20141027
     * @param caseno
     *//*
    public void orderTable(String caseno){
    	
    	String sql="SELECT B.CHN_DESC AS ADM_TYPE,A.ORDER_DESC,A.DOSAGE_QTY,C.UNIT_CHN_DESC AS DOSAGE_UNIT,"
    			+" A.AR_AMT,D.USER_NAME AS DR_CODE,A.ORDER_DATE,A.BILL_FLG,A.PRINT_FLG,A.EXEC_FLG" 
    			+" FROM OPD_ORDER A, SYS_DICTIONARY B,SYS_UNIT C,SYS_OPERATOR D " 
    			+" WHERE B.GROUP_ID='SYS_ADMTYPE' AND ID=A.ADM_TYPE" 
    			+" AND C.UNIT_CODE=A.DOSAGE_UNIT "
    			+" AND D.USER_ID=A.DR_CODE"
    			+" AND HIDE_FLG = 'N' AND CASE_NO = '"+caseno+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount()>0){
    		tTable.setParmValue(result);
    	}else{
    		this.messageBox("没有数据");
    	}
    }*/
    /**
     * 查询
     */
    public void onQuery() {
    	this.onMrno();
    }
    /**
     * 保存
     */
    public void onSave() {
    	
    	
    	TParm result = new TParm();
    	TParm update = new TParm();
    	TParm tradeParm = new TParm();//交易主档parm
    	TParm patSectionParm = new TParm();//患者时程交易parm
    	TParm patSectionDParm = new TParm();//患者时程明细交易parm
    	
    	if (getValue("EKT_BILL_TYPE").equals("")) {
			this.messageBox("支付方式不能为空");
			return ;
		}
    	
    	//检查页面数据
    	if(!checkData()){
    		return;
    	}
    	
    	
    	//判断套餐是否过期add by sunqy 20140721
    	Date now = new Date();
    	DateFormat format = DateFormat.getDateInstance();
    	String dateNow = format.format(now).replaceAll("-", "/");
//    	String dateEnd = this.getValue("END_DATE").toString().replaceAll("-", "/").substring(0, 10);
    	String dateEnd = this.getValueString("END_DATE");
    	
    	if(!"".equals(dateEnd)&&dateEnd != null){
    		dateEnd = dateEnd.toString().substring(0, 10).replaceAll("-", "");
    	}
    	String dateStart=this.getValueString("START_DATE");
    	if(!"".equals(dateStart)&&dateStart != null){
    		dateStart = dateStart.toString().substring(0, 10).replaceAll("-", "");
    	}
    	/*if("".equals(dateEnd)||dateEnd == null){
    		messageBox("请输入失效日期");
    		return;
    	}else{
    		dateEnd = dateEnd.toString().replaceAll("-", "/").substring(0, 10);
    	}*/
    	
    	/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    	try {
    		Date startdate = sdf.parse(dateNow);
    		Date enddate = sdf.parse(dateEnd);
    		int i = enddate.compareTo(startdate);
    		if (i <= 0) {
    			if(this.messageBox("套餐有效期", "该套餐不在有效期内，是否继续", 2)!=0){
    				return;
    			}
    		}
    	} catch (ParseException e) {
    	}*/
    	//获取交易号
    	tradeNo = getTradeNo();
    	//System.out.println("tradeNo111111111="+tradeNo);
    	//获取时程最大编号
    	SECTION_ID = getMaxSeq("ID","MEM_PAT_PACKAGE_SECTION","","","","");
    	//System.out.println("时程编号："+SECTION_ID);
    	//获取时程明细最大编号
//    	SECTION_D_ID = getMaxSeq("ID","MEM_PAT_PACKAGE_SECTION_D","","","","");
//    	System.out.println("明细编号："+SECTION_D_ID);
    	//获取页面信息
    	tradeParm = getPatData();
    	tradeParm.addData("START_DATE", dateStart);
    	tradeParm.addData("END_DATE", dateEnd);
    	if(!oper){//全局操作-用于支付工具异常处理
    		return;
    	}
    	
//    	System.out.println("页面信息："+tradeParm);
    	//获得勾选时程parm
    	patSectionParm = getSectionParm(tradeParm);
//    	System.out.println("勾选时程parm="+patSectionParm);
    	//获取时程对应下明细parm+均摊价格
    	patSectionDParm = getSectionDParm(patSectionParm);
//    	System.out.println("patSectionDParm="+patSectionDParm);
    	
    	update.setData("TRADEDATA", tradeParm.getData());
    	update.setData("SECTIONDATA", patSectionParm.getData());
    	update.setData("SECTIONDDATA", patSectionDParm.getData());
    	if(parmEKT!=null){
    		update.setData("PARMEKT", parmEKT.getData());//医疗卡信息
    	}
    	TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
    	if(cashPay.isSelected()){
    		//add bby sunqy 20140710 判断有未设定支付方式的金额的情况
            TParm payTypeTParm = paymentTool.table.getParmValue();
        	//现金打票操作，校验是否存在支付宝或微信金额
    		TParm checkCashTypeParm=OPBTool.getInstance().checkCashTypeOther(payTypeTParm);
    		TParm payCashParm=null;
    		if(null!=checkCashTypeParm.getValue("WX_FLG")&&
    				checkCashTypeParm.getValue("WX_FLG").equals("Y")||null!=checkCashTypeParm.getValue("ZFB_FLG")&&
    				checkCashTypeParm.getValue("ZFB_FLG").equals("Y")){
    			Object resultParm = this.openDialog(
        	            "%ROOT%\\config\\bil\\BILPayTypeOnFeeTransactionNo.x", checkCashTypeParm, false);
    			if(null==resultParm){
    				return ;
    			}
    			payCashParm=(TParm)resultParm;
    		}
    		if(null!=payCashParm){
    			update.setData("payCashParm",payCashParm.getData());
    		}
    	}else{
    		if(this.onPayOther()){//未收费
				return ;
			}
    		
    		if(parmEKT == null){
    			this.messageBox("请读取医疗卡信息");
    			return ;
    		}
    		if (!EKTIO.getInstance().ektSwitch()) {
    			messageBox_("医疗卡流程没有启动!");
    			return ;
    		}
    		TParm parm = new TParm();
    		parm.setData("READ_CARD", parmEKT.getData());
    		parm.setData("PAY_OTHER3",this.getValueDouble("PAY_OTHER3"));
    		parm.setData("PAY_OTHER4",this.getValueDouble("PAY_OTHER4"));
    		parm.setData("EXE_AMT",this.getValueDouble("EKT_TOT_AMT"));
    		parm.setData("MR_NO",this.getValueString("MR_NO"));
    		parm.setData("BUSINESS_TYPE","MEM");
    		parm.setData("CASE_NO",tradeNo);//交易号
    		Object r =  this.openDialog("%ROOT%\\config\\ekt\\EKTChageOtherUI.x",parm);
    		if(r == null){
    			this.messageBox("医疗卡扣款取消，不执行保存");
    			return;
    		}
    		TParm rParm = (TParm) r;
    		if(rParm.getErrCode() < 0){
    			this.messageBox(rParm.getErrText());
    			return;
    		}else if(rParm.getValue("OP_TYPE").equals("2")){
    			return;
    		}else{
    			update.setData("ektSql", rParm.getData("ektSql"));
    		}
    		
    	}
    	
    	
    	result = TIOM_AppServer.executeAction("action.mem.MEMPackageSalesTimeSelAction",
				"onSave", update);
    	
    	String uSql =
    		" SELECT ID, ORDERSET_GROUP_NO, PACKAGE_CODE, SECTION_CODE FROM MEM_PAT_PACKAGE_SECTION_D WHERE  USED_FLG = 0 AND SETMAIN_FLG = 'Y' " +
    		" AND TRADE_NO = '"+tradeNo+"'";
//    	System.out.println("uSql====== "+uSql);
    	TParm uParm = new TParm(TJDODBTool.getInstance().select(uSql));
//    	System.out.println("uParm===== "+uParm);
//    	messageBox(""+uParm.getCount("ID"));
    	for (int i = 0; i < uParm.getCount("ID"); i++) {
    		uSql = 
    			" UPDATE MEM_PAT_PACKAGE_SECTION_D" +
    			" SET ORDERSET_ID = '" + uParm.getValue("ID", i) + "'" +
    			" WHERE TRADE_NO = '" + tradeNo + "'" +
    			" AND ORDERSET_GROUP_NO = '" + uParm.getValue("ORDERSET_GROUP_NO", i) + "'" +
    			" AND PACKAGE_CODE = '" + uParm.getValue("PACKAGE_CODE", i) + "'" +
    			" AND SECTION_CODE = '" + uParm.getValue("SECTION_CODE", i) + "'";
//    		System.out.println("uSql==asdasasdda=="+uSql);
    		TJDODBTool.getInstance().update(uSql);
		}
    	
    	if(result.getErrCode()<0){
    		this.messageBox("保存失败！"+result.getErrText());
    		return;
    	}else{
    		this.messageBox("保存成功！");
    		
    		//add by sunqy 20140616  ----start----
    		boolean isDedug=true; //add by huangtt 20160505 日志输出
    		try {
			
    		String sql = "SELECT  A.GATHER_TYPE,B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A ,SYS_DICTIONARY B" +
    				" WHERE A.GATHER_TYPE= B.ID AND B.GROUP_ID = 'GATHER_TYPE'"; 
    		TParm payTypeParm = new TParm(TJDODBTool.getInstance().select(sql));
    		DecimalFormat df=new DecimalFormat("0.00");
    		HashMap<String, String> map = new HashMap<String, String>();
    		for (int i = 0; i < payTypeParm.getCount(); i++) {
				map.put(payTypeParm.getValue("GATHER_TYPE", i), payTypeParm.getValue("CHN_DESC", i));
			}
//    		map.put("C0", "现金");
//    		map.put("C1", "刷卡");
//    		map.put("T0", "支票");
//    		map.put("C4", "应收款");
//    		map.put("LPK", "礼品卡");
//    		map.put("XJZKQ", "现金折扣券");
    		map.put("INS", "医保卡");
    		TParm parm = new TParm();
    		parm.setData("TITLE", "TEXT",  "套餐交款收据");
    		parm.setData("TYPE", "TEXT", "门诊"); //类别
    		parm.setData("MR_NO", "TEXT", this.getValue("MR_NO")); // 病案号
    		 sql = "SELECT MEMO1,MEMO2,MEMO3,MEMO4,MEMO5,MEMO6,MEMO7,MEMO8,MEMO9,MEMO10,CARD_TYPE,WX_BUSINESS_NO,ZFB_BUSINESS_NO " +
    				"FROM MEM_PACKAGE_TRADE_M WHERE MR_NO = '"+ this.getValueString("MR_NO")+"' ORDER BY OPT_DATE DESC";
    		TParm sqlResult = new TParm(TJDODBTool.getInstance().select(sql));
    		sqlResult = sqlResult.getRow(0);
    		parm.setData("RecNO", "TEXT", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO", "MEM_NO")); //票据号
    		parm.setData("DEPT_CODE", "TEXT", "");// 科别
    		parm.setData("PAT_NAME", "TEXT", this.getValue("PAT_NAME")); // 姓名
    		TTable table1 = paymentTool.table;//支付方式表格
			int tableRow = table1.getRowCount();
			int tableColumn0= table1.getColumnIndex("PAY_TYPE");//支付方式列
			int tableColumn1 = table1.getColumnIndex("AMT");//支付金额列
			String payType = "";
			for(int i=0;i<tableRow;i++){
				if(table1.getValueAt(i, tableColumn0)==null||table1.getValueAt(i, tableColumn0)==""){
					break;
				}
				if((table1.getValueAt(i, tableColumn0)!=null||table1.getValueAt(i, tableColumn0)!="") 
						&& (table1.getValueAt(i, tableColumn1)==null || table1.getValueAt(i, tableColumn1)=="")){
					break;
				}
				payType += ";"+map.get(table1.getValueAt(i,tableColumn0)) + ":" 
				+ df.format(Double.parseDouble((table1.getValueAt(i, tableColumn1))+""))+"元";// 将得到的支付方式与支付金额合并
			}
			payType = payType.substring(1, payType.length());
			String arr[] = payType.split(";");//如果支付方式是3个则分两行显示
			if(arr.length>2&&arr.length<4){
				parm.setData("PAY_TYPE2", "TEXT", arr[0]);
				parm.setData("PAY_TYPE3", "TEXT", arr[1]+";"+arr[2]);
			}
			//add by kangy 20171019 
			if(arr.length>=4){
				parm.setData("PAY_TYPE2", "TEXT", arr[0]+";"+arr[1]);
				parm.setData("PAY_TYPE3", "TEXT", arr[2]+";"+arr[3]);
			}
			if(arr.length<=2){
				parm.setData("PAY_TYPE", "TEXT", payType);// 支付方式
			}
			if(this.getValueBoolean("gatherFlg2")){
				parm.setData("PAY_TYPE", "TEXT", "医疗卡:"+this.getValueDouble("EKT_TOT_AMT")+"元");// 支付方式
			}
			parm.setData("MONEY", "TEXT", (df.format(StringTool.round(this.getValueDouble("AR_AMT"), 2))+"元")); // 金额
			parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(this.getValueDouble("AR_AMT"))); // 大写金额
			//String bankNo = "";
			//添加卡类型 add by huangjw 20141230
			//==start==modify by kangy 20171019 
			String memo2="";
			String memo9="";
			String memo10="";
			memo2=sqlResult.getValue("MEMO2");
			memo9=sqlResult.getValue("MEMO9");
			memo10=sqlResult.getValue("MEMO10");
//			String[] str;
//			String str1="";
//			int count=0;
//			for (int i = 1; i < 11; i++) {//sunqy 20140715
//				if(!"".equals(sqlResult.getValue("MEMO"+i))&&sqlResult.getValue("MEMO"+i)!=null){
//					if(!"".equals(cardtype)||cardtype!=null){
//						count++;
//						str=cardtype.split(";");
//						if(count<=str.length){
//							String cardsql="select CHN_DESC from sys_dictionary where id='"+str[count-1]+"' and group_id='SYS_CARDTYPE' ";
//							TParm Cardparm=new TParm(TJDODBTool.getInstance().select(cardsql));
//							str1=str1+Cardparm.getValue("CHN_DESC",0);
//						}
//					}
//					bankNo += str1+" "+sqlResult.getValue("MEMO"+i)+";";
//					str1="";
//				}
//			}
//			if(bankNo.length()>0){
//				bankNo = bankNo.substring(0, bankNo.length()-1);
//			}
			String cardtypeString="";
			if(!"".equals(memo2)&&!"#".equals(memo2)){
				String [] str=memo2.split("#");
				String [] str1=str[0].split(";");
//				String [] str2=str[1].split(";");
				String [] str2=null;
				if(str.length == 2){
					str2=str[1].split(";");
				}
				for(int m=0;m<str1.length;m++){
					String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
					TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
					cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
					if(str2 != null){
						if(m < str2.length ){
							cardtypeString=cardtypeString+str2[m]+" ";
						}
					}
					
				}
			}
			
			if(!"".equals(memo9)&&!"#".equals(memo9)){
				String [] str=memo9.split("#");
				String [] str1=str[0].split(";");
				String [] str2=null;
				if(str.length == 2){
					str2=str[1].split(";");
				}
				for(int m=0;m<str1.length;m++){
					String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
					TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
					cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
					if(str2 != null){
						if(m < str2.length ){
							cardtypeString+="备注:"+str2[m]+" ";
						}
					}
					if(sqlResult.getValue("WX_BUSINESS_NO").length()>0){
						cardtypeString+=" 交易号:"+sqlResult.getValue("WX_BUSINESS_NO");
					}
				}
			}
			if(!"".equals(memo10)&&!"#".equals(memo10)){
				String [] str=memo10.split("#");
				String [] str1=str[0].split(";");
//				String [] str2=str[1].split(";");
				String [] str2=null;
				if(str.length == 2){
					str2=str[1].split(";");
				}
				for(int m=0;m<str1.length;m++){
					String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
					TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
					cardtypeString+=";"+cardParm.getValue("CHN_DESC",0)+" ";				
					if(str2 != null){
						if(m < str2.length ){
							cardtypeString+="备注:"+str2[m]+" ";
						}
					}
					if(sqlResult.getValue("ZFB_BUSINESS_NO").length()>0){
						cardtypeString+=" 交易号:"+sqlResult.getValue("ZFB_BUSINESS_NO");
					}
					
				}
			}
			if (cardtypeString.length()>1) {
				cardtypeString = cardtypeString.substring(1, cardtypeString.length());
			}
			if(cardtypeString.length()<=50){
				parm.setData("ACOUNT_NO1", "TEXT", cardtypeString);// 产品
			}else{
				parm.setData("ACOUNT_NO2","TEXT",cardtypeString.substring(0,50));
				parm.setData("ACOUNT_NO3","TEXT",cardtypeString.substring(50,cardtypeString.length()));
			}
			//==end==modify by kangy20171019 
			//===================================modify by huangjw 20141110 start
			String ctz_code=this.getText("PACKAGE_CODE");
			if(ctz_code.length()<=7){
				parm.setData("CTZ_CODE0", "TEXT", ctz_code);// 产品
			}else{
				parm.setData("CTZ_CODE","TEXT",ctz_code.substring(0,7));
				parm.setData("CTZ_CODE1","TEXT",ctz_code.substring(7,ctz_code.length()));
			}
			//===================================modify by huangjw 20141110 end
//			parm.setData("REASON", "TEXT", ((TComboBox)getComponent("DISCOUNT_REASON")).getSelectedName());// 折扣原因
			parm.setData("REASON", "TEXT", ((TTextFormat)getComponent("DISCOUNT_REASON")).getText());// 折扣原因
			String date = StringTool.getTimestamp(new Date()).toString().substring(
					0, 19).replace('-', '/');
			parm.setData("DATE", "TEXT", date);// 日期
			parm.setData("OP_NAME", "TEXT", Operator.getID()); // 收款人
			parm.setData("RETURN", "TEXT", ""); // 退
			parm.setData("o", "TEXT", "");// 退
			parm.setData("COPY", "TEXT", ""); // 补印注记
			parm.setData("EXPLAIN", "TEXT", "说明：此收据不做报销凭证，就诊后可到客服打印发票"); //说明 add by huangtt 201500924
			parm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
	        		Operator.getHospitalCHNFullName() : "所有医院");
	        parm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
	        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
//			this.openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMPackReceiptV45.jhw",parm, true);
			this.openPrintDialog(IReportTool.getInstance().getReportPath("MEMPackReceiptV45.jhw"),
					IReportTool.getInstance().getReportParm("MEMPackReceiptV45.class", parm));//报表合并 
			
			} catch (Exception e) {
				// TODO: handle exception
				if(isDedug){  
					System.out.println(" come in class: MEMPackageSalesTimeSelControl ，method ：onSave");
					e.printStackTrace();
				}
			}
    		//add by sunqy 20140616  ----end----
    		
    		tradeNo = "";//交易号置空
    		onClear();//清除
    		
    	}
    	
    }
    
    /**
     * 清除
     */
    public void onClear() {
    	callFunction("UI|MR_NO|setEnabled", true); // 病案号
        callFunction("UI|IPD_NO|setEnabled", true); // 住院号
        //清空病患信息
    	this.clearValue("MR_NO;IPD_NO;PAT_NAME;TEL_HOME;IDNO;SEX_CODE;BIRTH_DATE;AGE;ADM_DATE;CUSTOMER_SOURCE;CTZ_CODE1");
    	//套餐信息
    	this.clearValue("PRICE_TYPE;PACKAGE_CODE;START_DATE;END_DATE");
    	//已购套餐置灰
        callFunction("UI|package|setEnabled", false);
    	
    	//清空折扣信息
    	this.clearValue("DISCOUNT_REASON;DISCOUNT_APPROVER;DISCOUNT_TYPE;" +
    			"ORIGINAL_PRICE;RETAIL_PRICE;AR_AMT;DESCRIPTION;INTRODUCER1;INTRODUCER2;INTRODUCER3;DISCOUNT_PRICE;LMP_DATE");
    	//add by huangtt 20180515
    	this.clearValue("EKT_BILL_TYPE;EKT_CURRENT_BALANCE;EKT_AMT;GIFT_CARD2;GIFT_CARD;NO_PAY_OTHER_ALL;PAY_OTHER4;PAY_OTHER3;NO_PAY_OTHER;EKT_TOT_AMT;EKT_PAY;EKT_PAY_RETURN");

		TRadioButton ektPay = (TRadioButton) getComponent("gatherFlg2");
		TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
		ektPay.setSelected(false);
		cashPay.setSelected(true);
    	this.onGatherChange(0);
    	
    	this.setValue("DISCOUNT_RATE", "1.00");//设置折扣率为1.00
    	table.removeRowAll();
    	orderTable.removeRowAll();
    	//tTable.removeRowAll();
    	paymentTool.onClear();
    	TRadioButton defaultButton = (TRadioButton) this.getComponent("SALE_BUTTON");
    	defaultButton.setSelected(true);//add by sunqy 20140718清空后默认选中套餐原始价格
//    	initTextFormat();
    	initTextFormat();
        this.setValue("PRICE_TYPE", "01");
        flage=false;//add by huangjw 2014111
        
    }
    
    /**
     * 已购套餐
     */
    public void onPackage() {
    	TParm parm = new TParm();
    	parm.setData("MR_NO", this.getValueString("MR_NO"));
    	this.openDialog("%ROOT%\\config\\mem\\MEMPackageSalesInfo.x", parm);
    	
    }

    /**
     * 过滤条件
     *
     * @return String
     */
    private String getQuerySrc() { // 拿到科室代码
        String code = getText("PACKAGE_CODE");
        // 得到科室名称
        //String desc = getText("PACKAGE_DESC");
        String sb = "";
        if(null!=Operator.getRegion()&&!"".equals(Operator.getRegion()))
           // sb= " REGION_CODE='"+Operator.getRegion()+"' ";
        // 配过滤条件
        if (code != null && code.length() > 0)
            sb += "PACKAGE_CODE like '" + code + "%'";
//        if (desc != null && desc.length() > 0) {
//            if (sb.length() > 0)
//                sb += " AND ";
//            sb += "DEPT_CHN_DESC like '" + desc + "%'";
//        }
//        System.out.println("sb===="+sb);
        return sb;
    }
    /**
     * 时程明细列表显示
     */
    public void showSectionDetail(String packageCode){
    	
    	//生效日期-失效日期
    	//setStartAndEndDate(packageCode);
    	//设置套餐类型
    	this.setValue("PACKAGE_CODE", packageCode);
//    	//获取时程表CODE最大值
//    	sectionIdNo = getMaxSeq("SECTION_CODE","SYS_PACKAGE_SECTION","","","","");
//    	//获取时程表SEQ最大值
//    	sectionSeqNo = getMaxSeq("SEQ","SYS_PACKAGE_SECTION","PACKAGE_CODE",packageCode,"","");
    	
    	queryByPriceType();
    }
    /**
     * 套餐类型改变事件 add by huangjw 20141027
     */
    public void queryByPriceType(){
    	orderTable.removeRowAll();//清除细表数据
    	String sql = "SELECT 'N' AS EXEC, A.SECTION_CODE,A.PACKAGE_CODE,A.SECTION_DESC,A.SECTION_ENG_DESC, " +
		" A.PY1,A.PY2,A.SEQ,A.DESCRIPTION,A.ORIGINAL_PRICE,C.SECTION_PRICE,A.OPT_DATE," +
		" A.OPT_USER,A.OPT_TERM,A.START_DATE,A.END_DATE, B.PACKAGE_ENG_DESC " +
		" FROM MEM_PACKAGE_SECTION A,MEM_PACKAGE B," +
		" ( SELECT PRICE_TYPE,PACKAGE_CODE,SECTION_CODE,DISCOUNT_RATE,SECTION_PRICE FROM MEM_PACKAGE_SECTION_PRICE "
				+ " WHERE PRICE_TYPE = '"+ this.getValue("PRICE_TYPE")+ "') C" +
		" WHERE A.PACKAGE_CODE = '"+packageCode+"'" +
		" AND A.PACKAGE_CODE = B.PACKAGE_CODE   ";
    	if(!"".equals(this.getValueString("PRICE_TYPE"))){
    		sql+=" AND A.PACKAGE_CODE=C.PACKAGE_CODE(+) ";
    		sql+=" AND A.SECTION_CODE=C.SECTION_CODE(+) ";
    		sql+=" ORDER BY A.PACKAGE_CODE,A.SEQ";
    	}
		//System.out.println("时程明细sql-"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(result);
		paymentTool.onClear();
    }
    /**
     * 时程表点击事件-医嘱表显示
     */
    public void onMainTableClick(){
    	orderTable.removeRowAll();//清除表数据
    	int selectedIndx=table.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	if(table.getSelectedColumn() == 0){//第一列"选"
    		table.acceptText();
    		int row = table.getSelectedRow();
    		TParm parm = table.getParmValue();
    		parm.setData("EXEC",row, parm.getValue("EXEC", row).equals("Y")?"N":"Y");
    		table.setParmValue(parm);
    		table.setSelectedRow(row);
    		table.acceptText();
    		//价格统计
        	PriceStatistics();
    		
    	}

    	TParm tableparm=table.getParmValue();
    	if(!"".equals(tableparm.getValue("SECTION_CODE",selectedIndx))){
    		sectionCode = tableparm.getValue("SECTION_CODE",selectedIndx);
    	}
    	if(!"".equals(tableparm.getValue("PACKAGE_CODE",selectedIndx))){
    		packageCode = tableparm.getValue("PACKAGE_CODE",selectedIndx);
    	}
    	//System.out.println("sectionCode="+sectionCode+" packageCode="+packageCode);
    	
    	orderTable.setParmValue(packageDetail());
//    	onTableChangeValue();
    }
    
    /**
     * 套餐明细 modify by huangjw 20141117
     * @return
     */
	public TParm packageDetail() {
		String orderSql = "SELECT A.ID,A.SEQ,A.SECTION_DESC,A.ORDER_CODE,A.ORDER_DESC,A.ORDER_NUM,"
				+ " A.UNIT_CODE,A.UNIT_PRICE,B.RETAIL_PRICE,A.DESCRIPTION,A.OPT_DATE,A.OPT_USER,A.OPT_TERM,"
				+ " A.SECTION_CODE,A.PACKAGE_CODE,A.SETMAIN_FLG,A.ORDERSET_CODE,A.ORDERSET_GROUP_NO,A.HIDE_FLG"
				+ " FROM MEM_PACKAGE_SECTION_D A,"
				+ " (SELECT ID, PRICE_TYPE, PACKAGE_CODE, SECTION_CODE, DISCOUNT_RATE, RETAIL_PRICE"
				+ " FROM MEM_PACKAGE_SECTION_D_PRICE"
				+ " WHERE PRICE_TYPE = '"
				+ this.getValue("PRICE_TYPE")
				+ "') B"
				+ " WHERE A.PACKAGE_CODE = '"
				+ packageCode
				+ "' AND A.SECTION_CODE = '"
				+ sectionCode
				+ "' AND A.HIDE_FLG = 'N'";
		if (!"".equals(this.getValueString("PRICE_TYPE"))) {
			orderSql += " AND A.PACKAGE_CODE=B.PACKAGE_CODE(+) ";
			orderSql += " AND A.SECTION_CODE=B.SECTION_CODE(+) ";
			orderSql += " AND A.ID=B.ID(+) ";
			orderSql += " ORDER BY SEQ";
		}
		 //System.out.println("医嘱sql="+orderSql);
		TParm result = new TParm(TJDODBTool.getInstance().select(orderSql));
		return result;
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
	 * 全选
	 */
	public void onSelAll() {
		TParm parm = table.getParmValue();
		int rowCount = parm.getCount();
		if(rowCount>0){
			for (int i = 0; i < rowCount; i++) {
				if (this.getTCheckBox("ALLCHECK").isSelected())
					parm.setData("EXEC", i, "Y");
				else
					parm.setData("EXEC", i, "N");
			}
			table.setParmValue(parm);
			//价格统计
	    	PriceStatistics();
		}
		
	}
	/**
	 * 得到TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
     * 得到TextFormat对象
     * @param tagName String
     * @return TTextFormat
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
	/**
	 * 设置生效日期和失效日期
	 */
	public void setStartAndEndDate(String packageCode) {
		String sql = "SELECT START_DATE,END_DATE FROM MEM_PACKAGE WHERE PACKAGE_CODE ='"+packageCode+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()>0){
			if(result.getValue("START_DATE", 0).length()>0){
				this.setValue("START_DATE", result.getValue("START_DATE", 0).substring(0, 10).replace('-', '/'));
			}
			if(result.getValue("END_DATE", 0).length()>0){
				this.setValue("END_DATE", result.getValue("END_DATE", 0).substring(0, 10).replace('-', '/'));
			}
		}
	}
	/**
	 * 设置套餐价格类型 add by huangjw 20141027
	 */
	public void setPriceType(String packageCode){
		String sql = "SELECT B.PRICE_TYPE FROM MEM_PACKAGE_SECTION A, MEM_PACKAGE_SECTION_PRICE B " 
				+" WHERE A.PACKAGE_CODE=B.PACKAGE_CODE"
				+" AND A.SECTION_CODE=B.SECTION_CODE AND A.PACKAGE_CODE='"+packageCode+"'";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()>0){
			this.setValue("PRICE_TYPE", result.getValue("PRICE_TYPE",0));
		}
	}
	/**
	 * 病案号是否存在-先决条件
	 */
	public boolean existMrNo(){
		boolean flag = false;
		String mrNo = this.getValueString("MR_NO");
		if(mrNo.length()>0){
			flag = true;
		}else{
			this.messageBox("请先输入病案号！");
			return flag;
		}
		
		return flag;
	}
	
	/**
	 * table单选框点击事件
	 */
	public void onTableChangeValue() {
		table.acceptText();
//		int row = table.getSelectedRow();
//		TParm parm = table.getParmValue();
		
//		messageBox("s"+parm.getBoolean("EXEC", row)+row);
		int selectedIndx=table.getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}
    	//System.out.println("点选行2："+selectedIndx);
//    	PriceStatistics();
	}
	
	/**
	 * 检查数据
	 */
	public boolean checkData(){
		boolean flg2 = true;
		if(MR_NO.length()<=0){
    		this.messageBox("病案号不能为空！");
    		flg2 = false;
    		return flg2;
    	}
		TTextField packageCodeFlag = (TTextField)this.getComponent("MR_NO");
    	boolean oper = packageCodeFlag.isEnabled();
    	if(oper){
    		this.messageBox("病案号不存在！");
    		flg2 = false;
    		return flg2;
    	}
		//检验是否选中数据
		TParm parm = table.getParmValue();
		int count = parm.getCount();
		if(count<=0){
			this.messageBox("没有套餐数据，无法保存！");
			flg2 = false;
    		return flg2;
		}else{
			boolean flg = false;
			for (int i = 0; i < count; i++) {
				boolean exec = parm.getBoolean("EXEC", i);
				if(exec){
					flg = true;
				}
			}
			if(!flg){
				this.messageBox("没有选中时程套餐！");
				flg2 = false;
	    		return flg2;
			}
		}
		//支付方式
//		String payType = this.getValue("PAY_TYPE").toString();
//		if("".equals(payType) || payType.length()<=0){
//			this.messageBox("支付方式不能为空！");
//			this.grabFocus("PAY_TYPE");
//			flg2 = false;
//    		return flg2;
//		}
		
		//支付方式为医疗卡判断医疗卡是否有信息
//		if("2".equals(payType)){
//			if(parmEKT==null){
//				this.messageBox("医疗卡信息为空！");
//				flg2 = false;
//	    		return flg2;
//			}
//		}
		
    	double arAmt = this.getValueDouble("AR_AMT");
    	
    	BigDecimal arAmount = new BigDecimal(arAmt).setScale(2, RoundingMode.HALF_UP);
    	
    	//先注释掉，因为有免费的儿童套餐    所以折扣金额是0的，也可保存  modify by huangjw 20141106
    	/*if(arAmt <= 0){
    		this.messageBox("销售折扣价不能为空！");
    		this.grabFocus("AR_AMT");
    		flg2 = false;
    		return flg2;
    	}*/
//    	double retailPrice = this.getValueDouble("RETAIL_PRICE");//套餐销售价
//    	if(arAmt>retailPrice){
//    		this.messageBox("折扣价不能大于套餐价！");
//    		return;
//    	}
    	double discountRate = this.getValueDouble("DISCOUNT_RATE");
    	if(discountRate<0){
    		this.messageBox("折扣不能为负！");
    		flg2 = false;
    		return flg2;
    	}else if(discountRate > 1){//Modified by lich
    		this.messageBox("折扣不能大于1！");
    		flg2 = false;
    		return flg2;
    	}else if(discountRate>=100){
    		this.messageBox("折扣不能大于100！");
    		flg2 = false;
    		return flg2;
    	}
    	/** duzhw del 0513
    	//add by huangtt 20140512
    	TParm parm1 = null;
		try {
			parm1 = paymentTool.getAmts();
		} catch (Exception e) {
			e.printStackTrace();
			messageBox(e.getMessage());
			flg2 = false;
    		return flg2;
		}  **/
    	TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
    	if(cashPay.isSelected()){
    		//add bby sunqy 20140710 判断有未设定支付方式的金额的情况
        	TParm payParm = paymentTool.table.getParmValue();
        	int payCount = payParm.getCount();
        	String payType = "";
        	double amt = 0.00;
        	for (int i = 0; i <= payCount; i++) {
        		payType = payParm.getValue("PAY_TYPE", i);
    			amt = payParm.getDouble("AMT", i);
    			if(amt > 0 && ("".equals(payType)||payType==null)){
    				this.messageBox("存在未设定支付方式的金额,请填写！");
    				flg2 = false;
    				return flg2;
    			}
    		}
        	//----start-------add by kangy 20160718------微信支付宝支付需要添加交易号
        	boolean flg3=paymentTool.onCheckPayType(payParm);
    	    if (flg3) {
    	    } else {
    			this.messageBox("不允许出现相同的支付方式！");
    			flg2 = false;
    			return flg2;
    		}
    	    //----end-----add by kangy 20160718------微信支付宝支付需要添加交易号
    	}
    	
    	
    	
    	TRadioButton original = (TRadioButton)getComponent("ORIGINAL_BUTTON");
    	TRadioButton sale = (TRadioButton)getComponent("SALE_BUTTON");
    	String originalPrice = getValueString("ORIGINAL_PRICE").replaceAll(",", "");//原始价格
    	BigDecimal orgPrice = new BigDecimal(originalPrice).setScale(2, RoundingMode.HALF_UP);
    	String retailPrice = getValueString("RETAIL_PRICE").replaceAll(",", "");//套餐价格
    	BigDecimal retPrice = new BigDecimal(retailPrice).setScale(2, RoundingMode.HALF_UP);
    	String discountPrice = getValueString("DISCOUNT_PRICE").replaceAll(",", "");//折扣金额
    	BigDecimal disPrice = new BigDecimal(discountPrice).setScale(2, RoundingMode.HALF_UP);
    	if(original.isSelected()){
			if(!arAmount.equals(orgPrice.subtract(disPrice))){
    			this.messageBox("折扣金额有误!");
    			this.grabFocus("AR_AMT");
    			flg2 = false;
				return flg2;
    		}
    	}
    	if(sale.isSelected()){
			if(!arAmount.equals(retPrice.subtract(disPrice))){
    			this.messageBox("折扣金额有误!");
    			this.grabFocus("AR_AMT");
    			flg2 = false;
				return flg2;
    		}
    	}
    	//Modified by lich
    	if(discountRate < 1.0){
//    		messageBox("哦");
    		String discountReason = this.getValueString("DISCOUNT_REASON");
    		if(discountReason == null || "".equals(discountReason.trim())){
    			messageBox("请填写折扣原因");
    			flg2 = false;
    			return flg2;
    		}
    	}
    	
    	return flg2;
	}
	/**
	 * 价格统计-点选时程套餐的"选"或全选时进行套餐价格统计操作
	 */
	public void PriceStatistics(){
		String originalPrice = "";
		String retailPrice = "";
		double allOriginalPrice = 0.00;
		double allRetailPrice = 0.00;
		table.acceptText();
		TParm parm = table.getParmValue();
		//System.out.println("parm="+parm);
		//messageBox(""+parm.getData("EXEC"));
		int count = parm.getCount();
		//System.out.println("count="+count);
		if(count>0){
			for (int i = 0; i < count; i++) {
				boolean exec = parm.getBoolean("EXEC", i);
				double originalPrice1 = parm.getDouble("ORIGINAL_PRICE", i);
				double retailPrice1 = parm.getDouble("SECTION_PRICE", i);
				if(exec){
					allOriginalPrice += originalPrice1;
					allRetailPrice += retailPrice1;
//					String sql="SELECT DISCOUNT_RATE FROM MEM_PACKAGE_SECTION_PRICE " +
//					"WHERE PACKAGE_CODE='"+parm.getValue("PACKAGE_CODE",i)+"' " +
//					"AND SECTION_CODE='"+parm.getValue("SECTION_CODE",i)+"' AND PRICE_TYPE='"+this.getValueString("PRICE_TYPE")+"'";
//					TParm resultParm=new TParm(TJDODBTool.getInstance().select(sql));
//					this.setValue("DISCOUNT_RATE", resultParm.getDouble("DISCOUNT_RATE",0));
					
				}
			}
		}
		DecimalFormat df = new DecimalFormat("###,##0.00");
        originalPrice = df.format(allOriginalPrice);
        retailPrice = df.format(allRetailPrice);
//		originalPrice = feeConversion(originalPrice);//加逗号处理
//		retailPrice = feeConversion(retailPrice);//加逗号处理
		this.setValue("ORIGINAL_PRICE", originalPrice);
		this.setValue("RETAIL_PRICE", retailPrice);
		
		
		//根据折扣计算实收金额
		queryFee();
		queryDiscount();//add by sunqy 20140704折扣金额计算
	}
	/**
     * 每三位加逗号处理
     */
    public String feeConversion(String fee){
    	String str1 = ""; 
    	String[] s = fee.split("\\.");//以"."来分割
    	
        str1 = new StringBuilder(s[0].toString()).reverse().toString();     //先将字符串颠倒顺序  
        String str2 = "";  
        for(int i=0;i<str1.length();i++){  
            if(i*3+3>str1.length()){  
                str2 += str1.substring(i*3, str1.length());  
                break;  
            }  
            str2 += str1.substring(i*3, i*3+3)+",";  
        }  
        if(str2.endsWith(",")){  
            str2 = str2.substring(0, str2.length()-1);  
        }  
        //最后再将顺序反转过来  
        String str3 = new StringBuilder(str2).reverse().toString();
        //加上小数点后的数
        StringBuffer str4 = new StringBuffer(str3);
        str4 = str4.append(".").append(s[1]);
    	return str4.toString();
    }
    /**
     * 去逗号处理
     */
    public String getFeeConversion(String fee) {
    	fee = fee.replaceAll(",", "");
    	return fee;
    	
    }
    /**
	 * 套餐销售交易号-取得交易号
	 * 
	 * @return String
	 */
    public String getTradeNo() {
    	if (tradeNo.length() <= 0) {
    		tradeNo = SystemTool.getInstance().getNo("ALL", "MEM", "TRADENO",
					"TRADENO");
//    		System.out.println("=====tradeNo=="+tradeNo);
		}
		return tradeNo;
    }
    /**
     * 得到最大的编号 +1
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    public int getMaxSeq(String maxValue, String tableName,
                         String where1,String value1,String where2,String value2) {
    	String sql = "SELECT MAX("+maxValue+") AS "+maxValue+" FROM "+tableName+" WHERE 1=1 ";
    	if(where1.trim().length()>0){
    		sql += " AND "+where1+" ='"+value1+"'";
    	}
    	if(where2.trim().length()>0){
    		sql += " AND "+where2+" ='"+value2+"'";
    	}
    	//System.out.println("最大的编号sql="+sql);
    	// 保存最大号
        int max = 0;
    	//查询最大序号
    	TParm seqParm = new TParm(TJDODBTool.getInstance().select(sql));
    	String seq = seqParm.getValue(maxValue,0).toString().equals("")?"0"
    			:seqParm.getValue(maxValue,0).toString();
    	//System.out.println("seq="+seq);
    	int value = Integer.parseInt(seq);
    	//System.out.println("value="+value);
    	// 保存最大值
        if (max < value) {
            max = value;
        }
        // 最大号加1
        max++;
        //System.out.println("最大的编号 +1="+max);
        return max;
        
    }
    /**
     * 获取页面信息
     */
    public TParm getPatData() {
    	oper = true;//全局操作
    	TParm result = new TParm();
    	Timestamp date = StringTool.getTimestamp(new Date());
    	//介绍人1
    	String introducer1 = this.getValueString("INTRODUCER1");
    	//介绍人2
    	String introducer2 = this.getValueString("INTRODUCER2");
    	//介绍人3
    	String introducer3 = this.getValueString("INTRODUCER3");
    	//折扣原因
    	String discountReasion = this.getValueString("DISCOUNT_REASON");
    	//折扣审批人
    	String discountApprover = this.getValueString("DISCOUNT_APPROVER");
    	//折扣方式
    	String discountType = this.getValueString("DISCOUNT_TYPE");
    	//折扣
    	double discountRate = this.getValueDouble("DISCOUNT_RATE");
    	//套餐项目原始价格
    	String originalPrice = this.getValueString("ORIGINAL_PRICE");
    	originalPrice = getFeeConversion(originalPrice);//去逗号处理
    	//套餐销售价格
    	String retailPrice = this.getValueString("RETAIL_PRICE");
    	retailPrice = getFeeConversion(retailPrice);//去逗号处理
    	//折扣销售价格
    	double arAmt = new BigDecimal(this.getValueDouble("AR_AMT")).setScale(2, RoundingMode.HALF_UP).doubleValue();
    	//备注
    	String description = this.getValueString("DESCRIPTION");
    	//均摊比率
    	double retailPrice1=Double.parseDouble(retailPrice);
    	double rate = arAmt/retailPrice1;
    	//支付方式
    	ektOper = this.getValueString("PAY_TYPE");
    	
    	result.addData("TRADE_NO", tradeNo);
    	result.addData("MR_NO", MR_NO);
    	result.addData("INTRODUCER1", introducer1);
    	result.addData("INTRODUCER2", introducer2);
    	result.addData("INTRODUCER3", introducer3);
    	result.addData("DISCOUNT_REASON", discountReasion);
    	result.addData("DISCOUNT_APPROVER", discountApprover);
    	result.addData("DISCOUNT_TYPE", discountType);
    	result.addData("DISCOUNT_RATE", discountRate);
    	result.addData("ORIGINAL_PRICE", originalPrice);
    	result.addData("RETAIL_PRICE", retailPrice);
    	result.addData("AR_AMT", arAmt);
    	result.addData("DESCRIPTION", description);
    	

//    	for (int i = 0; i < 10; i++) {
//    		result.addData("MEMO"+(i+1), "");
//		}
//    	for (int i = 0; i <  paymentTool.table.getRowCount(); i++) {
//    		if("C1".equals(paymentTool.table.getItemData(i, "PAY_TYPE"))){
//    			int row =Integer.parseInt(payParm.getValue("PAY_TYPE", i).substring(payParm.getValue("PAY_TYPE", i).length()-2)) ;
//    			result.setData("MEMO"+row,0, payParm.getData("REMARKS", i));
//
//    		}
//		}
    	
    	
    	
//    	int count = 0;
//		for (int j = 0; j < paymentTool.table.getRowCount(); j++) {//add by sunqy 20140714添加刷卡时的备注(记录银行卡号)
//			if("C1".equals(paymentTool.table.getItemData(j, "PAY_TYPE"))){
//				result.addData("MEMO"+(j+1), paymentTool.table.getItemData(j, "REMARKS"));
//			}else{
//				result.addData("MEMO"+(j+1), "");
//			}
//			count ++ ;
//		}
//		for (int i = count; i < count+(10-paymentTool.table.getRowCount()); i++) {//add by sunqy 20140714将10个字段赋为空字符串
//			result.addData("MEMO"+(i+1), "");
//		}
//		for (int i = 1; i < 11; i++) {//add by sunqy 20140714将10个字段赋为空字符串
//			result.setData("MEMO"+i, "");
//		}
//		for (int j = 0; j < paymentTool.table.getRowCount(); j++) {//add by sunqy 20140714添加刷卡时的备注(记录银行卡号)
//			if("C1".equals(paymentTool.table.getItemData(j, "PAY_TYPE"))){
//				result.setData("MEMO"+(j+1), paymentTool.table.getItemData(j, "REMARKS"));
//			}
//		}
    	result.addData("RATE", rate);
    	result.addData("OPT_USER", Operator.getID());
    	result.addData("OPT_DATE", date);
    	result.addData("OPT_TERM", Operator.getIP());
    	result.addData("PAY_TYPE", ektOper);
    	
//    	result.addData("PAY_TYPE01", 0.00);
//    	result.addData("PAY_TYPE02", 0.00);
//    	result.addData("PAY_TYPE03", 0.00);
//    	result.addData("PAY_TYPE04", 0.00);
//    	result.addData("PAY_TYPE05", 0.00);
//    	result.addData("PAY_TYPE06", 0.00);
//    	result.addData("PAY_TYPE07", 0.00);
//    	result.addData("PAY_TYPE08", 0.00);
//    	result.addData("PAY_TYPE09", 0.00);
//    	result.addData("PAY_TYPE10", 0.00);
    	
//    	if(payParm.getCount()>0){
//    		String cardType = "";//add by sunqy 20140728用于记录各个卡类型
//    		for (int i = 0; i < payParm.getCount(); i++) {
//    			if(i<9){
////    				result.setData("PAY_TYPE0"+(i+1), 0, payParm.getValue("AMT", i));
//    				result.setData(payParm.getValue("PAY_TYPE", i), 0, payParm.getValue("AMT", i));//modify by sunqy 20140717
//    			}else if(i==9){
//    				result.setData("PAY_TYPE10", 0, payParm.getValue("AMT", i));
//    			}
//    			if(!"".equals(payParm.getValue("CARD_TYPE", i))){
//    				cardType += ","+payParm.getValue("CARD_TYPE", i);
//    			}
//			}
//    		if(cardType.length()>0){
//    			cardType = cardType.substring(1, cardType.length());
//    		}
//    		result.addData("CARD_TYPE", cardType);
//    	}
    	
    	result.addData("BILL_TYPE", this.getValueString("EKT_BILL_TYPE"));
    	
    	//新支付方式
    	TParm payParm = new TParm();
    	TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
    	if(cashPay.isSelected()){
    		result.addData("PAY_MEDICAL_CARD", 0);
    		try {
        		paymentTool.setAmt(arAmt);//add by sunqy 20140714 判断是否输入金额小于折扣销售价格
        		payParm =  paymentTool.getAmts();
        		
//    			System.out.println("payParm = "+payParm);
    		} catch (Exception e) {
    			e.printStackTrace();
    			messageBox(e.getMessage());
    			oper = false;
    		}
    	}else{
    		result.addData("PAY_MEDICAL_CARD", this.getValueString("EKT_TOT_AMT"));
    	}
    	

    	String cardType;//将卡类型和卡号存到一个字段中 modify by huangjw 20150104
		String cardTypeKey;
		double v;
		String key;
		TParm payCountParm = new TParm(TJDODBTool.getInstance().select("SELECT COUNT(PAYTYPE) PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE"));
//		System.out.println("payCountParm======:"+payCountParm.getInt("PAYTYPE", 0));
		for(int j=1;j<=payCountParm.getInt("PAYTYPE", 0);j++){
			cardTypeKey="MEMO"+j;
			if(j<10){
				key="PAY_TYPE0"+j;
			}else{
				key="PAY_TYPE"+j;
			}
			cardType = "";
			v=0.00;
			for(int i=0;i<payParm.getCount("PAY_TYPE");i++){
				if(key.equals(payParm.getValue("PAY_TYPE", i))){
					v = payParm.getDouble("AMT", i);
					//if("PAY_TYPE02".equals(payParm.getValue("PAY_TYPE", i))){
						cardType = payParm.getValue("CARD_TYPE", i)+"#"+payParm.getValue("REMARKS",i);
					//}
					break;
				}
			}
			result.addData(key, v);
			result.addData(cardTypeKey, cardType);
		}
//		System.out.println("result----"+result);
    	return result;
    }
    /**
     * 获得勾选时程parm
     */
    public TParm getSectionParm(TParm parm) {
    	TParm sectionParm = new TParm();
    	TParm result = new TParm();
    	TParm tableParm = table.getParmValue();
		int count = tableParm.getCount();
		if(count>0){
			int j = 0;
			double sumArAmt = 0;
			for (int i = 0; i < count; i++) {
				boolean exec = tableParm.getBoolean("EXEC", i);
				
				if(exec){
					//添加到sectionParm中
					sectionParm.addData("ID", SECTION_ID);
					sectionParm.addData("TRADE_NO", tradeNo);
					sectionParm.addData("PACKAGE_CODE", tableParm.getValue("PACKAGE_CODE", i));
					sectionParm.addData("SECTION_CODE", tableParm.getValue("SECTION_CODE", i));
					sectionParm.addData("MR_NO", MR_NO);
					sectionParm.addData("PACKAGE_DESC", this.getTextFormat("PACKAGE_CODE").getText());
					sectionParm.addData("PACKAGE_ENG_DESC", tableParm.getValue("PACKAGE_ENG_DESC", i));
					sectionParm.addData("SECTION_DESC", tableParm.getValue("SECTION_DESC", i));
					sectionParm.addData("ORIGINAL_PRICE", tableParm.getValue("ORIGINAL_PRICE", i));
					sectionParm.addData("SECTION_PRICE", tableParm.getValue("SECTION_PRICE", i));
					
					if(tableParm.getDouble("SECTION_PRICE", i) == 0){
						sectionParm.addData("AR_AMT", 0);

					}else{
						double arAmt = new BigDecimal(tableParm.getDouble("SECTION_PRICE", i)*Double.parseDouble(parm.getValue("RATE", 0))).setScale(2, RoundingMode.HALF_UP).doubleValue();
						sumArAmt = sumArAmt + arAmt;
						sectionParm.addData("AR_AMT", arAmt);

					}
//					sectionParm.addData("AR_AMT", tableParm.getDouble("SECTION_PRICE", i)*Double.parseDouble(parm.getValue("RATE", 0)));
					sectionParm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i));
					sectionParm.addData("USED_FLG", "0");//未使用
					sectionParm.addData("RATE", parm.getValue("RATE", 0));
					
					result.addRowData(sectionParm, j);
					j++;
					
					SECTION_ID++;
				}
			}
			
			double realAmt = new BigDecimal(parm.getDouble("AR_AMT", 0) - sumArAmt).setScale(2, RoundingMode.HALF_UP).doubleValue();
			
			result.setData("AR_AMT", 0, result.getDouble("AR_AMT", 0)+realAmt);
			
			
		}
    	return result;
    }
    /**
     * 获取时程下明细parm
     */
    public TParm getSectionDParm(TParm parm) {
    	TParm result = new TParm();
    	TParm uparm = new TParm();
    	TParm detailParm = new TParm();
    	TParm operParm = new TParm();
    	int count = parm.getCount("SECTION_CODE");
    	Timestamp date = StringTool.getTimestamp(new Date());
    	TParm orderSetParm = new TParm();
    	if(count>0){
    		int seq = 0;
    		int k = 0;
    		for (int i = 0; i < count; i++) {
    			//获取该时程下的所有明细数据
    			String sectionCode = parm.getValue("SECTION_CODE", i);
    			String packageCode = parm.getValue("PACKAGE_CODE", i);
    			operParm.addData("SECTION_CODE", sectionCode);
    			operParm.addData("PACKAGE_CODE", packageCode);
    			operParm.addData("PRICE_TYPE", getValueString("PRICE_TYPE"));
    			
//    			detailParm = TIOM_AppServer.executeAction("action.mem.MEMPackageSalesTimeSelAction",
//    					"onQueryDetail", operParm);
    			detailParm = new TParm(TJDODBTool.getInstance().select(onQueryDetailSql(operParm)));
    			System.out.println("detailParm== "+detailParm);
    			operParm.removeData("SECTION_CODE");
    			operParm.removeData("PACKAGE_CODE");
    			if(detailParm.getCount()>0){
    				double arAmt = parm.getDouble("AR_AMT", i);//时程价钱
    				double rate = parm.getDouble("RATE", i); //里程折扣率
    				BigDecimal sumAmt = BigDecimal.ZERO;
    				double maxPrice = 0;
    				int maxInt = 0;
    				for (int j = 0; j < detailParm.getCount(); j++) {
    					uparm.addData("ID", detailParm.getValue("ID", j));
    					uparm.addData("TRADE_NO", tradeNo);
    					uparm.addData("PACKAGE_CODE", parm.getValue("PACKAGE_CODE", i));
    					uparm.addData("SECTION_CODE", parm.getValue("SECTION_CODE", i));
    					uparm.addData("PACKAGE_DESC", this.getTextFormat("PACKAGE_CODE").getText());
    					uparm.addData("SECTION_DESC", detailParm.getValue("SECTION_DESC", j));
    					uparm.addData("CASE_NO", "1");//--未处理
    					uparm.addData("MR_NO", MR_NO);
    					uparm.addData("SEQ", seq++);
    					uparm.addData("ORDER_CODE", detailParm.getValue("ORDER_CODE", j));
    					uparm.addData("ORDER_DESC", detailParm.getValue("ORDER_DESC", j));
    					uparm.addData("ORDER_NUM", detailParm.getValue("ORDER_NUM", j));
    					uparm.addData("UNIT_CODE", detailParm.getValue("UNIT_CODE", j));
    					uparm.addData("UNIT_PRICE", detailParm.getValue("UNIT_PRICE", j));
    					BigDecimal retailPrice = BigDecimal.ZERO;
    					if(detailParm.getDouble("RETAIL_PRICE", j) > 0){
    						 retailPrice = new BigDecimal(detailParm.getDouble("RETAIL_PRICE", j)*rate).setScale(2, RoundingMode.HALF_UP);
    					}
    					if(("Y".equals(detailParm.getValue("SETMAIN_FLG", j)) && "N".equals(detailParm.getValue("HIDE_FLG", j)))
    							|| ("N".equals(detailParm.getValue("SETMAIN_FLG", j)) && "N".equals(detailParm.getValue("HIDE_FLG", j)))
    									){
    						sumAmt = sumAmt.add(retailPrice);
//        					System.out.println(j+"--retailPrice--"+retailPrice.doubleValue());
//        					System.out.println(j+"--sumAmt--"+sumAmt.doubleValue());
    					}
    					
    					uparm.addData("RETAIL_PRICE", retailPrice.doubleValue());
    					if(maxPrice < retailPrice.doubleValue()){
    						maxPrice = retailPrice.doubleValue();
    						maxInt = k;
    					}
    					uparm.addData("DESCRIPTION", detailParm.getValue("DESCRIPTION", j));
    					uparm.addData("OPT_DATE", date);
    					uparm.addData("OPT_USER", Operator.getID());
    					uparm.addData("OPT_TERM", Operator.getIP());
    					uparm.addData("SETMAIN_FLG", detailParm.getValue("SETMAIN_FLG", j));
    					uparm.addData("ORDERSET_CODE", detailParm.getValue("ORDERSET_CODE", j));
    					uparm.addData("ORDERSET_GROUP_NO", detailParm.getValue("ORDERSET_GROUP_NO", j));
    					uparm.addData("HIDE_FLG", detailParm.getValue("HIDE_FLG", j));
    					uparm.addData("USED_FLG", "0");
    					//pangben 2015-9-2 添加不限量字段
    					uparm.addData("UN_NUM_FLG", detailParm.getValue("UN_NUM_FLG", j));
    					//add by lich 增加英文医嘱字段  TRADE_ENG_DESC 20141010
    					uparm.addData("TRADE_ENG_DESC", parm.getValue("TRADE_ENG_DESC", i));
    					
    					if("Y".equals(detailParm.getValue("SETMAIN_FLG", j)) && "N".equals(detailParm.getValue("HIDE_FLG", j))){
    						orderSetParm.addRowData(uparm, k);
    					}
    					
    					result.addRowData(uparm, k);
    					k++;
    					//SECTION_D_ID++;
    				}
//    				System.out.println(i+"----"+parm.getRow(i));
//    				System.out.println("rate---"+rate);
//    				System.out.println("arAmt---"+arAmt);
//    				System.out.println("sumAmt---"+sumAmt.doubleValue());
    				double diffAmt = new BigDecimal(arAmt - sumAmt.doubleValue()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    				double price = new BigDecimal(result.getDouble("RETAIL_PRICE", maxInt)+diffAmt).setScale(2, RoundingMode.HALF_UP).doubleValue();
//    				System.out.println("diffAmt---"+diffAmt);
//    				System.out.println("price---"+price);
//    				System.out.println("maxInt---"+maxInt);
    				result.setData("RETAIL_PRICE", maxInt, price);
    				
    			}
    		}
    		
    	}
//    	System.out.println("result===="+result);
//    	System.out.println("orderSetParm---"+orderSetParm);
    	if(orderSetParm.getCount("SECTION_CODE") > 0){
    		for (int l = 0; l < orderSetParm.getCount("SECTION_CODE"); l++) {
				double sumAmt = 0;
				double maxAmt = 0;
				int row = 0;
				for (int m = 0; m < result.getCount("SECTION_CODE"); m++) {
					//ORDERSET_GROUP_NO PACKAGE_CODE SECTION_CODE
					if(orderSetParm.getValue("ORDERSET_GROUP_NO", l).equals(result.getValue("ORDERSET_GROUP_NO", m)) &&
							orderSetParm.getValue("PACKAGE_CODE", l).equals(result.getValue("PACKAGE_CODE", m)) &&
							orderSetParm.getValue("SECTION_CODE", l).equals(result.getValue("SECTION_CODE", m)) &&
							"N".equals(result.getValue("SETMAIN_FLG", m)) && "Y".equals(result.getValue("HIDE_FLG", m))
					){
						sumAmt = sumAmt + result.getDouble("RETAIL_PRICE", m);
						if(maxAmt < result.getDouble("RETAIL_PRICE", m)){
							maxAmt = result.getDouble("RETAIL_PRICE", m);
							row = m;
						}
					}	
				}
				double diffAmt = new BigDecimal(orderSetParm.getDouble("RETAIL_PRICE", l) - sumAmt).setScale(2, RoundingMode.HALF_UP).doubleValue();
				double price = new BigDecimal(maxAmt + diffAmt).setScale(2, RoundingMode.HALF_UP).doubleValue();
//				System.out.println("diffAmt----"+diffAmt);
//				System.out.println("price----"+price);
//				System.out.println("row----"+row);
				result.setData("RETAIL_PRICE", row, price);
				
			}
    	}
    	
    	return result;
    }
    
    /**
     * 根据折扣计算实收金额
     * @throws Exception 
     */
    public void queryRate() throws Exception{
    	//校验折扣不能大于1
    	double rate = this.getValueDouble("DISCOUNT_RATE");
    	if(rate>1){
    		this.messageBox("折扣率不能大于1！");
    		this.setValue("DISCOUNT_RATE", "1.00");
    		return;
    	}
    	queryFee();
    	queryDiscount();//add by sunqy 20140704
    }
    /**
     * 计算实收金额
     */
    public void queryFee(){
//    	DecimalFormat df = new DecimalFormat("##0.00");
    	double rate = this.getValueDouble("DISCOUNT_RATE");
    	BigDecimal rt = new BigDecimal(rate).setScale(2, RoundingMode.HALF_UP);
    	//计算实收金额（折扣销售价格）
    	//根据radiobutton 来确定收费金额 add by huangjw 20141106  start
    	String retailPrice = "";
    	if("Y".equals(this.getValueString("ORIGINAL_BUTTON"))){
    		retailPrice = this.getValueString("ORIGINAL_PRICE");
    	}else if("Y".equals(this.getValueString("SALE_BUTTON"))){
    		retailPrice = this.getValueString("RETAIL_PRICE");
    	}
    	//根据radiobutton 来确定收费金额 add by huangjw 20141106 end 
    	double retailPrice2 = 0.00;
    	double arAmt = 0.00;//折扣销售价
    	if(retailPrice.length()>0){
    		retailPrice = retailPrice.replaceAll(",", "");
    		//转换成double类型
    		retailPrice2 = Double.parseDouble(retailPrice);
    		BigDecimal bd = new BigDecimal(retailPrice2).setScale(2, RoundingMode.HALF_UP);
    		//System.out.println("rate="+rate+" retailPrice2="+retailPrice2);
//    		arAmt = retailPrice2 * rate;
    		arAmt = bd.multiply(rt).doubleValue();
    		this.setValue("AR_AMT", arAmt);
    		this.setValue("EKT_TOT_AMT", arAmt); //add by huangtt 20180515
    		this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-arAmt); //add by huangtt 20180515
    		paymentTool.setAmt(arAmt);//钱数变化时赋值
    		
    	}
    }
    
    /**
     * 读医疗卡操作
     */
    public void onEKTcard(){
    	//读取医疗卡
        parmEKT = EKTIO.getInstance().TXreadEKT();
        if (null == parmEKT || parmEKT.getErrCode() < 0 ||
            parmEKT.getValue("MR_NO").length() <= 0) {
            this.messageBox(parmEKT.getErrText());
            parmEKT = null;
            return;
        }
        
        //callFunction("UI|MR_NO|setEnabled", false); //病案不可编辑
        this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
        //this.setValue("CARD_NO", parmEKT.getValue("CARD_NO"));
        //支付方式设值：医疗卡支付
        this.setValue("PAY_TYPE", 2);
        
        parmEKT.setData("OPT_USER", Operator.getID());
        parmEKT.setData("OPT_TERM", Operator.getIP());
        
        //add by huangtt 20180515
        setValue("EKT_CURRENT_BALANCE", parmEKT
				.getDouble("CURRENT_BALANCE"));
        double totAmt = getValueDouble("EKT_TOT_AMT");
		setValue("EKT_AMT", StringTool.round((parmEKT.getDouble("CURRENT_BALANCE") - totAmt), 2));
		TRadioButton ektPay = (TRadioButton) getComponent("gatherFlg2");
		TRadioButton cashPay = (TRadioButton) getComponent("gatherFlg1");
		ektPay.setSelected(true);
		cashPay.setSelected(false);
		this.onGatherChange(1);
		onMrno();
        
    }
    
    /**
     * 套餐原始价格单选框 sunqy 20140704
     */
    public void onSelectOriginalPrice(){
    	if(flage == false){
    		flage = true;
    	}
    	double rate = this.getValueDouble("DISCOUNT_RATE");
    	double arAmt = Double.parseDouble(this.getValueString("ORIGINAL_PRICE").replaceAll(",", "")) * rate;
    	this.setValue("AR_AMT", arAmt);
    	this.setValue("EKT_TOT_AMT", arAmt); //add by huangtt 20180515
    	this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-arAmt); //add by huangtt 20180515
    	discountPrice = Double.parseDouble(this.getValueString("ORIGINAL_PRICE").replaceAll(",", ""))-this.getValueDouble("AR_AMT");
    	this.setValue("DISCOUNT_PRICE", new DecimalFormat("######0.00").format(discountPrice));
    	
    	queryFee();//add by huangjw 20141106
    }
    
    /**
     * 套餐销售价格单选框sunqy 20140704
     */
    public void onSelectSalePrice(){
    	if(flage == true){
    		flage = false;
    	}
    	double rate = this.getValueDouble("DISCOUNT_RATE");
    	double arAmt = Double.parseDouble(this.getValueString("RETAIL_PRICE").replaceAll(",", "")) * rate;
    	this.setValue("AR_AMT", arAmt);
    	this.setValue("EKT_TOT_AMT", arAmt); //add by huangtt 20180515
    	this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-arAmt); //add by huangtt 20180515
    	discountPrice = Double.parseDouble(this.getValueString("RETAIL_PRICE").replaceAll(",", ""))-this.getValueDouble("AR_AMT");
    	this.setValue("DISCOUNT_PRICE", new DecimalFormat("######0.00").format(discountPrice));
    	
    	queryFee();//add by huangjw 20141106
    }
    
    /**
     * 计算折扣金额 sunqy 20140704
     */
    private void queryDiscount(){
    	DecimalFormat df = new DecimalFormat("######0.00");
    	
    	if(flage == true){
    		double rate = this.getValueDouble("DISCOUNT_RATE");
        	double arAmt = Double.parseDouble(this.getValueString("ORIGINAL_PRICE").replaceAll(",", "")) * rate;
        	this.setValue("AR_AMT", arAmt);
        	this.setValue("EKT_TOT_AMT", arAmt); //add by huangtt 20180515
        	this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-arAmt); //add by huangtt 20180515
        	discountPrice = Double.parseDouble(this.getValueString("ORIGINAL_PRICE").replaceAll(",", ""))-this.getValueDouble("AR_AMT");
    		this.setValue("DISCOUNT_PRICE", df.format(discountPrice));
    	}
    	if(flage == false){
    		double rate = this.getValueDouble("DISCOUNT_RATE");
        	double arAmt = Double.parseDouble(this.getValueString("RETAIL_PRICE").replaceAll(",", "")) * rate;
        	this.setValue("AR_AMT", arAmt);
        	this.setValue("EKT_TOT_AMT", arAmt); //add by huangtt 20180515
        	this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-arAmt); //add by huangtt 20180515
        	discountPrice = Double.parseDouble(this.getValueString("RETAIL_PRICE").replaceAll(",", ""))-this.getValueDouble("AR_AMT");
    		this.setValue("DISCOUNT_PRICE", df.format(discountPrice));
    	}
    }
    
    /**
     * 折扣销售价格改变事件sunqy 20140704
     * @throws Exception
     */
    public void onChangeValue() throws Exception{
    	
    	DecimalFormat format = new DecimalFormat("0.00");
    	double amt = this.getValueDouble("AR_AMT");//折扣销售价格
    	this.setValue("EKT_TOT_AMT", amt); // add by huangtt 20180515
    	this.setValue("EKT_AMT", this.getValueDouble("EKT_CURRENT_BALANCE")-amt); //add by huangtt 20180515
    	paymentTool.onClear();
    	paymentTool.setAmt(amt);
    	if(flage){
    		double originalPrice = TypeTool.getDouble(getValue("ORIGINAL_PRICE").toString().replaceAll(",", ""));//套餐项目原始价格
    		BigDecimal origimnal = new BigDecimal(originalPrice);
    		BigDecimal bigAmt = new BigDecimal(amt);
    		this.setValue("DISCOUNT_PRICE", format.format(origimnal.subtract(bigAmt)));//折扣金额
    		this.setValue("DISCOUNT_RATE", bigAmt.divide(origimnal,4, BigDecimal.ROUND_HALF_UP).doubleValue());
    	}else{
    		double retailPrice = TypeTool.getDouble(getValue("RETAIL_PRICE").toString().replaceAll(",", ""));//套餐销售价格
    		BigDecimal retail = new BigDecimal(retailPrice);
    		BigDecimal bigAmt = new BigDecimal(amt);
    		System.out.println("retail--"+retail.doubleValue());
    		System.out.println("bigAmt--"+bigAmt.doubleValue());
//    		System.out.println("bigAmt/retail--"+ bigAmt.divide(retail).doubleValue());
    		this.setValue("DISCOUNT_PRICE", format.format(retail.subtract(bigAmt)));
    		this.setValue("DISCOUNT_RATE", bigAmt.divide(retail,4, BigDecimal.ROUND_HALF_UP).doubleValue());
    	}
    	
    }
    
    /**
     * 判断是否为最后节点 add by sunqy 20140811
     * @param packageCode
     * @return
     */
	private boolean isLeaf(String packageCode){
		String sql = "SELECT SEQ FROM MEM_PACKAGE WHERE PARENT_PACKAGE_CODE = '"+packageCode+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()<=0){
			return true;
		}
		return false;
	}
	
	
	/**
	 * 查询明细医嘱sql
	 */
	public String onQueryDetailSql(TParm parm) {
																				//add by lich 增加英文医嘱字段  TRADE_ENG_DESC 20141010

		String sql = "SELECT A.ID,A.SEQ,A.SECTION_DESC,A.ORDER_CODE,A.ORDER_DESC,A.ORDER_NUM,A.TRADE_ENG_DESC," +
				" A.UNIT_CODE,A.UNIT_PRICE,B.RETAIL_PRICE,A.DESCRIPTION,A.OPT_DATE,A.OPT_USER,A.OPT_TERM," +
				" A.SECTION_CODE,A.PACKAGE_CODE,A.SETMAIN_FLG,A.ORDERSET_CODE,A.ORDERSET_GROUP_NO,A.HIDE_FLG,A.UN_NUM_FLG" +//====pangben 2015-9-2 添加UN_NUM_FLG字段
				" FROM MEM_PACKAGE_SECTION_D A, (SELECT ID,"+
                " PRICE_TYPE,"+
                " PACKAGE_CODE,"+
                " SECTION_CODE,"+
                " DISCOUNT_RATE,"+
                " RETAIL_PRICE "+
                " FROM MEM_PACKAGE_SECTION_D_PRICE "+
                " WHERE PRICE_TYPE = '"+parm.getValue("PRICE_TYPE", 0)+"') B " +
				" WHERE A.PACKAGE_CODE = '"+parm.getValue("PACKAGE_CODE", 0)+"' " +
				" AND A.SECTION_CODE = '"+parm.getValue("SECTION_CODE", 0)+"'" +
				" AND A.PACKAGE_CODE = B.PACKAGE_CODE(+)" +
				" AND A.SECTION_CODE = B.SECTION_CODE(+)" +
				" AND A.ID = B.ID(+)" +
				//" AND B.PRICE_TYPE = '"+parm.getValue("PRICE_TYPE", 0)+"'" +
				" ORDER BY A.SEQ ";

		System.out.println("查询明细医嘱sql="+sql);
		return sql;
	}
	/**
	 * 就诊记录
	 * add by huangjw 20141106
	 */
	public void onCaseHistory() {
		Object obj=null;
		if(!"".equals(this.getValueString("MR_NO"))){
			 obj=this.openDialog("%ROOT%\\config\\opd\\OPDCaseHistory.x",this.getValueString("MR_NO"));
		}else{
			this.messageBox("请输入病案号");
			this.grabFocus("MR_NO");
		}
		if(obj==null)
			return;
		if (!(obj instanceof TParm)) {
			return;
		}
		TParm parm=(TParm)obj;
		TParm newParm=(TParm) parm.getData("DIAG");
		String sql="SELECT ADM_DATE FROM REG_PATADM WHERE CASE_NO='"+newParm.getValue("CASE_NO",0)+"'";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("ADM_DATE", result.getTimestamp("ADM_DATE",0));
	}
	
	public void onGatherChange(int t){

		boolean b = false;
		String lockRow = "";


		switch (t) {
		case 0:
			clearValue("PAY_OTHER4;PAY_OTHER3;EKT_PAY;");
			setValue("EKT_BILL_TYPE", "C");
			double amt = getValueDouble("AR_AMT");
			paymentTool.setAmt(amt);
			break;
		case 1:
			b = true;
			lockRow = "0";
			setValue("EKT_BILL_TYPE", "E");
			paymentTool.onClear();
			break;
		}

		TNumberTextField payOther4 = (TNumberTextField) getComponent("PAY_OTHER4");
		TNumberTextField payOther3 = (TNumberTextField) getComponent("PAY_OTHER3");
		TNumberTextField pay = (TNumberTextField) getComponent("EKT_PAY");

		payOther4.setEnabled(b);
		payOther3.setEnabled(b);
		pay.setEnabled(b);

		paymentTool.table.setLockRows(lockRow);
	}
	public void onPayOther3(){	
		double payOther3 = getValueDouble("PAY_OTHER3");
		double payOther4 = 0;
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double arAmt = getValueDouble("EKT_TOT_AMT");
		if(getValueDouble("PAY_OTHER4") == 0){
			payOther4 = arAmt - payOther3;
			setValue("PAY_OTHER4", df.format(payOther4) );
		}
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode() == -3){
			setValue("PAY_OTHER4", df.format(payOtherTop4) );
		}
		onPayOther();
	}

	public void onPayOther4(){		
		double payOther3 = 0;
		double payOther4 = getValueDouble("PAY_OTHER4");
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double arAmt = getValueDouble("EKT_TOT_AMT");
		if(getValueDouble("PAY_OTHER3") == 0){
			payOther3 = arAmt - payOther4;
			setValue("PAY_OTHER3", df.format(payOther3) );
		}
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode() == -2){
			setValue("PAY_OTHER3", df.format(payOtherTop3));
		}
		onPayOther();
	}

	public boolean onPayOther(){
		double payOther3 = getValueDouble("PAY_OTHER3");
		double payOther4 = getValueDouble("PAY_OTHER4");
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double payCashTop = getValueDouble("NO_PAY_OTHER_ALL");
		double arAmt = getValueDouble("EKT_TOT_AMT");
		double payCash = Double.parseDouble(df.format(arAmt - payOther3 - payOther4));
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			setValue("PAY_OTHER3", 0);
			setValue("PAY_OTHER4", 0);
			return true;
		}
		if(payCash > payCashTop){
			messageBox("现金不足");
			System.out.println(pat.getMrNo()+"-----payCash===="+payCash+"-----payCashTop==="+payCashTop);
			setValue("PAY_OTHER3", 0);
			setValue("PAY_OTHER4", 0);
			return true;
		}
		setValue("NO_PAY_OTHER", payCash);
		return false;
	}
	
	/**
	 * 交款金额回车事件 PAY
	 */
	public void onPay() {
		// 折扣金额
		double pay = getValueDouble("EKT_PAY");
		// 折扣金额
		double arAmt = getValueDouble("EKT_TOT_AMT");
		if (pay - arAmt < 0 || pay == 0) {
			this.messageBox("金额不足!");
			return;
		}
		// 赋值
		callFunction("UI|EKT_PAY_RETURN|setValue", StringTool.round((pay - arAmt),
				2));
		// this.grabFocus("CHARGE");
	}
}
