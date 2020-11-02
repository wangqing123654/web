package com.javahis.ui.sys;

import java.text.NumberFormat;

import jdo.sys.Operator;
import jdo.sys.SYSPublishBoardTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import com.sun.jmx.snmp.Timestamp;

/**
 *
 * <p>Title: 诊断码</p>
 *
 * <p>Description:诊断码 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author ehui 200800901
 * @version 1.0
 */
public class SYSDiagnosisControl
    extends TControl {

	//每页缺省大小
    private static final int DEFAULT_PAGE_SIZE = 50;
    //第页记录数量
    private int pageSize = DEFAULT_PAGE_SIZE;
    //记录总数；
    private long totalCount = 0;
    //开始索引
    private int start = 0;

    private int pageNo = 1;
	
    TLabel labPage;
    TLabel labTotalCount;
    TButton btnDown;
    TButton btnUp;

    TParm data;
    int selectRow = -1;
    TDataStore allTds, ccmd;
    TCheckBox syndromeFlg;
    TTable table;
    TTextField text;
    String oldText = "";
    String icdType;
    boolean flg=false;
    
    String icdCode;
	TTable table1;
	TTextFormat tf;
    
    public final static String INIT_QUERY = "SELECT ICD_TYPE, ICD_CODE, "
        + " ICD_CHN_DESC, ICD_ENG_DESC, PY1, PY2, SEQ, "
        + " DESCRIPTION, SYNDROME_FLG, MDC_CODE, CCMD_CODE, "
        + " MAIN_DIAG_FLG, CAT_FLG, STANDARD_DAYS, CHLR_FLG, "
        + " DISEASETYPE_CODE, MR_CODE, CHRONIC_FLG, START_AGE, "
        + " LIMIT_DEPT_CODE, LIMIT_SEX_CODE, END_AGE, "
        + " AVERAGE_FEE, OPT_USER, OPT_DATE, OPT_TERM,STA1_CODE,STA2_CODE,MIC_FLG,NOTE_FLG,DIAG_DEPT_CODE"//modify by wanglong 20140321 增加形态学诊断注记
        + "	FROM SYS_DIAGNOSIS "
        //+ "	WHERE ICD_CODE LIKE 'A%' AND ICD_TYPE='W' ORDER BY SEQ,ICD_CODE";
    	+ "	WHERE ICD_TYPE='W' ORDER BY SEQ,ICD_CODE";
    public final static String INIT_QUERY1 = "SELECT count(*)"
        + "	FROM SYS_DIAGNOSIS "
        //+ "	WHERE ICD_CODE LIKE 'A%' AND ICD_TYPE='W' ORDER BY SEQ,ICD_CODE";
    	+ "	WHERE ICD_TYPE='W' ";
    
    public final static String ICD_QUERY = "SELECT ICD_TYPE, ICD_CODE, "
        + " ICD_CHN_DESC, ICD_ENG_DESC, PY1, PY2, SEQ, "
        + " DESCRIPTION, SYNDROME_FLG, MDC_CODE, CCMD_CODE, "
        + " MAIN_DIAG_FLG, CAT_FLG, STANDARD_DAYS, CHLR_FLG, "
        + " DISEASETYPE_CODE, MR_CODE, CHRONIC_FLG, START_AGE, "
        + " LIMIT_DEPT_CODE, LIMIT_SEX_CODE, END_AGE, "
        + " AVERAGE_FEE, OPT_USER, OPT_DATE, OPT_TERM,STA1_CODE,STA2_CODE,MIC_FLG,NOTE_FLG,DIAG_DEPT_CODE"//modify by wanglong 20140321
        + "	FROM SYS_DIAGNOSIS ";
    public final static String ICD_QUERY1 = "SELECT count(*)"//modify by wanglong 20140321
        + "	FROM SYS_DIAGNOSIS ";
    public final static String CCMD_QUERY =
        " SELECT GROUP_ID, ID, CHN_DESC, ENG_DESC, PY1,"
        + "PY2, SEQ, DESCRIPTION, TYPE, PARENT_ID,"
        + "STATE, DATA, STA1_CODE, STA2_CODE, STA3_CODE,"
        + "OPT_USER, OPT_DATE, OPT_TERM "
        + " FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CCMD'";
    public final static String TAG = "ICD_CODE;SYNDROME_FLG;SEQ;ICD_DESC;ICD_CHN_DESC;PY1;PY2;ICD_ENG_DESC;DESCRIPTION;MDC_CODE;LIMIT_SEX_CODE;START_AGE;END_AGE;LIMIT_DEPT_CODE;STANDARD_DAYS;AVERAGE_FEE;MAIN_DIAG_FLG;CAT_FLG;CHRONIC_FLG;CHLR_FLG;DISEASETYPE_CODE;DISEASETYPE_CODE;LIMIT_SEX_CODE;CCMD_CODE;CCMD_DESC;MIC_FLG;NOTE_FLG;DIAG_DEPT_CODE;STA1_CODE;STA2_CODE";//modify by wanglong 20140321
    private static final String URLSYSICDPOPUP = "%ROOT%\\config\\sys\\SYSICDPopup.x";// add by wangbin 20141226
    
    public void onInit() {
        super.onInit();
        init();
    }

    /**
     * 初始化界面，查询所有的数据
     *
     * @return TParm
     */
    public void init() {
        ccmd = new TDataStore();
        ccmd.setSQL(CCMD_QUERY);
        ccmd.retrieve();
    	
        labPage = (TLabel) getComponent("Lab_Page");
        labTotalCount = (TLabel) getComponent("Lab_TotalCount");
        btnDown = (TButton) getComponent("Btn_Down");
        btnUp = (TButton) getComponent("Btn_Up");
   
    	table1 = (TTable) this.getComponent("TABLE");
        syndromeFlg = (TCheckBox)this.getComponent("SYNDROME_FLG");
        onClear();
        onLoadReceiveMessTable();
        // add by wangbin 20141226 外部需求 #630 START 
		// 事件绑定
        callFunction("UI|ICD_DESC|setPopupMenuParameter", "",
        		URLSYSICDPOPUP);
		// 接受回传值
		callFunction("UI|ICD_DESC|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// add by wangbin 20141226 外部需求 #630 END 
		
		callFunction("UI|save|setEnabled", false); //保存按钮置灰
		callFunction("UI|delete|setEnabled", false); //删除按钮置灰
		
    }
    

    /**
     * 加载收到公告消息列表(带分页50条一页)
     */
    public void onLoadReceiveMessTable() {
        totalCount = this.getTotalCount();
        labPage.setText("页次 " + pageNo + "/" + getTotalPageCount());
        labTotalCount.setText("共" + totalCount + "条");
        start = getStartOfPage(pageNo);
        //按扭设置
        //后一页
        if (this.hasNextPage()) {
            btnDown.setEnabled(true);
        }
        else {
            btnDown.setEnabled(false);
        }
        //前一页
        if (this.hasPreviousPage()) {
            btnUp.setEnabled(true);
        }
        else {
            btnUp.setEnabled(false);
        }

        TParm parm = new TParm();
        parm.setData("startIndex", start);
        parm.setData("endIndex", pageSize);
        //System.out.println(queryInitSql(parm));
        TParm query = null;
        
        query = new TParm(TJDODBTool.getInstance().select(queryInitSql(parm)));
        
        table1.setParmValue(query);   
    }
    
//    public String querySql(TParm parm){
//    	int startIndex = (int) parm.getInt("startIndex");
//        int endIndex = (int) parm.getInt("endIndex");
//        int low = startIndex + 1;
//        int up = startIndex + endIndex;
//        
//        String sql = "SELECT * "+
//	     "FROM (SELECT A.*, ROWNUM RN " +
//        		"FROM (  SELECT ICD_TYPE, "+
//	                       "ICD_CODE, "+
//	                       "ICD_CHN_DESC, "+
//	                       "ICD_ENG_DESC, "+
//	                       "PY1, "+
//	                       "PY2, "+
//	                       "SEQ, "+
//	                       "DESCRIPTION, "+
//	                       "SYNDROME_FLG, "+
//	                       "MDC_CODE, "+
//	                       "CCMD_CODE, "+
//	                       "MAIN_DIAG_FLG, "+
//	                       "CAT_FLG, "+
//	                       "STANDARD_DAYS, "+
//	                       "CHLR_FLG, "+
//	                       "DISEASETYPE_CODE, "+
//	                       "MR_CODE, "+
//	                       "CHRONIC_FLG, "+
//	                       "START_AGE, "+
//	                       "LIMIT_DEPT_CODE, "+
//	                       "LIMIT_SEX_CODE, "+
//	                       "END_AGE, "+
//	                       "AVERAGE_FEE, "+
//	                       "OPT_USER, "+
//	                       "OPT_DATE, "+
//	                       "OPT_TERM, "+
//	                       "STA1_CODE, "+
//	                       "STA2_CODE, "+
//	                       "MIC_FLG, "+
//	                       "NOTE_FLG, "+
//	                       "DIAG_DEPT_CODE "+
//	                     "FROM SYS_DIAGNOSIS "+
//	                     "WHERE ICD_TYPE = 'W' "+
//			             "ORDER BY SEQ, ICD_CODE) A) "+
//	            "WHERE RN BETWEEN "+low+" AND "+up+" ";
//        return sql;
//        
//    }
    
    public String queryInitSql(TParm parm){
    	int startIndex = (int) parm.getInt("startIndex");
        int endIndex = (int) parm.getInt("endIndex");
        int low = startIndex + 1;
        int up = startIndex + endIndex;
        
        String code = this.getValueString("ICD_CODE").toUpperCase();

    	String sql = "SELECT * "+
				     "FROM (SELECT A.*, ROWNUM RN " +
			         		"FROM (  SELECT ICD_TYPE, "+
				                       "ICD_CODE, "+
				                       "ICD_CHN_DESC, "+
				                       "ICD_ENG_DESC, "+
				                       "PY1, "+
				                       "PY2, "+
				                       "SEQ, "+
				                       "DESCRIPTION, "+
				                       "SYNDROME_FLG, "+
				                       "MDC_CODE, "+
				                       "CCMD_CODE, "+
				                       "MAIN_DIAG_FLG, "+
				                       "CAT_FLG, "+
				                       "STANDARD_DAYS, "+
				                       "CHLR_FLG, "+
				                       "DISEASETYPE_CODE, "+
				                       "MR_CODE, "+
				                       "CHRONIC_FLG, "+
				                       "START_AGE, "+
				                       "LIMIT_DEPT_CODE, "+
				                       "LIMIT_SEX_CODE, "+
				                       "END_AGE, "+
				                       "AVERAGE_FEE, "+
				                       "OPT_USER, "+
				                       "OPT_DATE, "+
				                       "OPT_TERM, "+
				                       "STA1_CODE, "+
				                       "STA2_CODE, "+
				                       "MIC_FLG, "+
				                       "NOTE_FLG, "+
				                       "DIAG_DEPT_CODE "+
				                     "FROM SYS_DIAGNOSIS "+
				                 "WHERE UPPER(ICD_CODE) LIKE '"+code+"%' AND ICD_TYPE = 'W' "+
				              "ORDER BY SEQ, ICD_CODE) A) "+
				     "WHERE RN BETWEEN "+low+" AND "+up+" ";
    	return sql;
    }
    
    
    /**
     * 上一页
     */
    public void onUpPage() {

        pageNo = pageNo - 1;
        //调用加载列表
        onLoadReceiveMessTable();
    }

    /**
     * 下一页
     */
    public void onDownPage() {
        pageNo = pageNo + 1;
        //调用加载列表
        TTextField tt = (TTextField)getComponent("ICD_CODE");
        if(!tt.isEnabled()){
        	onClear();
        }
        onLoadReceiveMessTable();
    }

    /**
     * 获得列表总数；
     * @return long
     */
    private long getTotalCount() {
        TParm parm = new TParm(TJDODBTool.getInstance().select(ICD_QUERY1));
        //this.messageBox("messCount===="+parm.getLong(0,0));
        return parm.getLong(0, 0);
    }

    /**
     * 取得总页数；
     * @return long
     */
    private long getTotalPageCount() {
        return totalCount % (long) pageSize != 0L ? totalCount
            / (long) pageSize + 1L : totalCount / (long) pageSize;
    }

    /**
     * 获得当前页号
     * @return int
     */
    private int getCurrentPageNo() {
        return start / pageSize + 1;
    }

    /**
     * 是否下一页
     * @return boolean
     */
    private boolean hasNextPage() {
        return (long) getCurrentPageNo() < getTotalPageCount();
    }

    /**
     * 是否有前一页
     * @return boolean
     */
    private boolean hasPreviousPage() {
        return getCurrentPageNo() > 1;
    }

    /**
     * 获得起始页索引
     * @param pageNo int
     * @return int
     */
    protected static int getStartOfPage(int pageNo) {
        return getStartOfPage(pageNo, DEFAULT_PAGE_SIZE);
    }

    private static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }
    
    
    
    
    
    
    /**
     * 新增按钮事件
     */
    public void onNew(){
    	this.setValue("ICD_CODE", "");
		this.grabFocus("ICD_CODE");
		callFunction("UI|save|setEnabled", true); 
		callFunction("UI|query|setEnabled", false); 
		//this.getTextField("ICD_CODE").setEnabled(true);
		icdDescAction(true);
		flg=true;
		//callFunction("UI|TABLE|removeRowAll"); 
    }
    /**
	 * 处理中文名称控件的显示
	 * @param flg
	 */
	public void icdDescAction(boolean flg){
		TTextField textfield = (TTextField) this.getTextField("ICD_DESC");
		TTextField OPT_CHN_DESC = (TTextField) getComponent("ICD_CHN_DESC");
		if(flg){
			textfield.setVisible(false);
			OPT_CHN_DESC.setVisible(true);
			getLabel("tLabel_10").setColor("black");
			getLabel("tLabel_13").setColor("black");
			getLabel("tLabel_22").setColor("black");
		}else{
			textfield.setVisible(true);
			OPT_CHN_DESC.setVisible(false);
			getLabel("tLabel_10").setColor("blue");
			getLabel("tLabel_13").setColor("blue");
			getLabel("tLabel_22").setColor("blue");
		}
	}
    public void onTableClick() {
    	icdDescAction(true);
    	callFunction("UI|save|setEnabled", true); 
		callFunction("UI|delete|setEnabled", true); 
		callFunction("UI|new|setEnabled", false); 
        int row = table1.getSelectedRow();
        selectRow = row;
        
//        TParm parm = allTds.getBuffer(allTds.PRIMARY);
//        //System.out.println("parm" + parm);
//        setValueForParm(
//            TAG,
//            parm, row);
        
        TParm parm = table1.getParmValue().getRow(row);
        setValueForParm(
                TAG,
                parm);
//        System.out.println(parm);
        if (StringTool.getBoolean(this.getValueString("MAIN_DIAG_FLG"))) {
            this.callFunction("UI|CAT_FLG|setEnabled", false);
        }
        else {
            this.callFunction("UI|CAT_FLG|setEnabled", true);
        }

        callFunction("UI|ICD_CODE|setEnabled", false);
        flg=false;
    }

    /**
     *清空
     */
    public void onClear() {
        this.clearValue(TAG);
//        this.setValue("LIMIT_SEX_CODE", "1");
        this.setValue("W", "Y");
        syndromeFlg.setEnabled(false);
        icdType = "W";
//        table = (TTable)this.getComponent("TABLE");
//        table1.removeRowAll();
//        allTds = table.getDataStore();
//        allTds.setSQL(INIT_QUERY);
//        allTds.retrieve();
//        if (allTds == null) {
//            //System.out.println("allTds is null");
//        }
        
//        table1.removeRowAll();
//        TParm result = new TParm(TJDODBTool.getInstance().select(INIT_QUERY));
//        table1.setParmValue(result);
        
	    totalCount = 0;
	    start = 0;
        pageNo = 1;
        
        onLoadReceiveMessTable();
        
//        table.setDSValue();
		callFunction("UI|save|setEnabled", false); 
		callFunction("UI|delete|setEnabled", false); 
		callFunction("UI|new|setEnabled", true); 
		callFunction("UI|query|setEnabled", true); 
		flg=false;
		callFunction("UI|ICD_CODE|setEnabled", true);
		icdDescAction(false);
    }

    /**
     * 西医诊断
     */
    public void onW() {
        TComboBox mdcCode = (TComboBox)this.getComponent("MDC_CODE");
        TCheckBox synd = (TCheckBox)this.getComponent("SYNDROME_FLG");
        synd.setSelected(false);
        synd.setEnabled(false);
        mdcCode.setValue("");
        mdcCode.setEnabled(true);
//		this.callFunction("UI|SYNDROME_FLG|setEnabled", false);
//		//MDC_CODE中医不使用
//		this.callFunction("UI|MDC_CODE|setEnabled", true);
        icdType = "W";
//		StringBuffer sb = new StringBuffer(ICD_QUERY);
//		sb.append("WHERE ICD_TYPE='" + icdType + "'");
//		allTds.setSQL(sb.toString());
//		allTds.retrieve();
//		table.setDSValue();
    }

    /**
     * 中医诊断
     */
    public void onC() {
        TComboBox mdcCode = (TComboBox)this.getComponent("MDC_CODE");
        TCheckBox synd = (TCheckBox)this.getComponent("SYNDROME_FLG");
        synd.setSelected(false);
        synd.setEnabled(true);
        mdcCode.setValue("");
        mdcCode.setEnabled(false);
//		this.callFunction("UI|SYNDROME_FLG|setEnabled", true);
//		this.callFunction("UI|MDC_CODE|setEnabled", false);
        icdType = "C";
//		StringBuffer sb = new StringBuffer(ICD_QUERY);
//		sb.append("WHERE ICD_TYPE='" + icdType + "'");
//		allTds.setSQL(sb.toString());
//		allTds.retrieve();
//		table.setDSValue();
    }

    /**
     * ICD_CODE栏回车时的查询事件
     */  
    public void onQueryIcd() {
        StringBuffer sb = new StringBuffer(ICD_QUERY);
        String code = this.getValueString("ICD_CODE").toUpperCase();
        this.setValue("ICD_CODE", code);//add by wanglong 20140321
        //fux modify 20160219
        sb.append("WHERE UPPER(ICD_CODE) LIKE '"+code+"%' AND ICD_TYPE='").append(icdType).append(
              "' ORDER BY SEQ,ICD_CODE");
        
//        sb.append("WHERE (ICD_CODE LIKE '%").append(code).append(  
//            "%' OR ICD_ENG_DESC LIKE '%").append(code).append(
//                "%' OR ICD_CHN_DESC LIKE '%").append(code).append(
//                    "%' OR PY1 LIKE '%").append(code).append(
//            "%')  AND ICD_TYPE='").append(icdType).append(
//                "' ORDER BY SEQ,ICD_CODE");
        //System.out.println(sb.toString());
        callFunction("UI|TABLE|removeRowAll"); 
//        System.out.println("sb:"+sb.toString());
//        allTds.setSQL(sb.toString());
//        allTds.retrieve();
//        table.setDSValue();
        
        TParm result = new TParm(TJDODBTool.getInstance().select(sb.toString()));
        table1.setParmValue(result);
    }

    /**
     * 查询
     */
    public void onQuery() {
	    totalCount = 0;
	    start = 0;
        pageNo = 1;
        onLoadReceiveMessTable();
        //onQueryIcd();
        flg=false;
    }

    /**
     * CCMD combol点击时将汉字带入到文本框中
     */
    public void onClickCCMD() {
    	if(!flg){
	        /*序号,60;疾病代码,120;中文名称,160;拼音,80;注记码,80;英文名称,160;诊断类别,80,ICD_TYPE;
	         * 主诊断,100,boolean;MDC,180,MDC_CODE;
	         * CCMD代码,180,CCMD_CODE;操作人员,120;操作日期,120
	         * 1,right;2,left;3,left;4,left;5,left;6,left;8,left;9,left;10,left;11,right
	         */
	        String ccmdCode = this.getValueString("CCMD_CODE").toUpperCase();
	        ccmd.setFilter("ID='" + ccmdCode + "'");
	        ccmd.filter();
	        this.setValue("CCMD_DESC", ccmd.getItemData(0, "CHN_DESC"));
	        StringBuffer sb = new StringBuffer(ICD_QUERY);
	        sb.append("WHERE CCMD_CODE='" + ccmdCode + "' ORDER BY SEQ,ICD_CODE ");
//	        allTds.setSQL(sb.toString());
//	        allTds.retrieve();
//	        table.setDSValue();
	        
	        TParm result = new TParm(TJDODBTool.getInstance().select(sb.toString()));
	        table1.setParmValue(result);
    	}
    }

    /**
     * mdc Combo改变时的查询事件
     */
    public void onClickMdc() {
    	if(!flg){
	        String mdcCode = this.getValueString("MDC_CODE");
	        StringBuffer sb = new StringBuffer(ICD_QUERY);
	        sb.append("WHERE MDC_CODE='" + mdcCode + "' ORDER BY SEQ,ICD_CODE ");
//	        allTds.setSQL(sb.toString());
//	        allTds.retrieve();
//	        table.setDSValue();
	        
	        TParm result = new TParm(TJDODBTool.getInstance().select(sb.toString()));
	        table1.setParmValue(result);
    	}
    }

    /**
     * 主诊断checkbox选中时，不可点击粗码chechbox
     */
    public void onMAINDIAGFLGclick() {
        if ("Y".equalsIgnoreCase(TCM_Transform.getString(this
            .getValue("MAIN_DIAG_FLG")))) {
            this.setValue("CAT_FLG","N");
            this.callFunction("UI|CAT_FLG|setEnabled", false);
            return;
        }
        this.callFunction("UI|CAT_FLG|setEnabled", true);
        return;
    }

    /**
     * 校验合法年龄，并且起必须小于末
     */
    public void onCheckAge() {
        int sAge = this.getValueInt("START_AGE");
        int eAge = this.getValueInt("END_AGE");
        TNumberTextField s = (TNumberTextField)this.getComponent("START_AGE");
        if (sAge >= 200 || eAge >= 200 || sAge > eAge) {
            this.messageBox_("年龄限制不合法");
            this.setValue("START_AGE", 0);
            this.setValue("END_AGE", 0);
            s.grabFocus();
        }

    }

    /**
     * 删除事件
     */
    public void onDelete() {
        if (selectRow < 0) {
            this.messageBox_("没有可删除的数据");
            return;
        }
//        allTds.deleteRow(selectRow);
//        String[] sql = allTds.getUpdateSQL();
//        if (sql == null || sql.length < 1) {
//            this.messageBox("E0003");
//            onClear();
//            return;
//        }
//        for (String temp : sql) {
//            System.out.println("temp->" + temp);
//        }
        TParm parm = table1.getParmValue().getRow(table1.getSelectedRow());
        String icdType1 = parm.getValue("ICD_TYPE");
        String icdCode1 = parm.getValue("ICD_CODE");
        
        String sql = "DELETE FROM sys_diagnosis WHERE "+
        			 "ICD_TYPE = '"+icdType1+"' AND "+
        			 "ICD_CODE = '"+icdCode1+"'";
        //System.out.println(sql);
        //System.out.println("hello :"+new Timestamp(System.currentTimeMillis()));
        
        //sql = "update sys_diagnosis set opt_user = 'D002' WHERE ICD_TYPE = '"+icdType+"' and ICD_CODE = '"+icdCode+"'";
        
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        
        //System.out.println("hello2 :"+new Timestamp(System.currentTimeMillis()));
        if (result.getErrCode() != 0) {
            this.messageBox("E0003");
        }
        else {
            this.messageBox("P0003");
        }
        onClear();
        return;

    }

    public String updateSql(TParm parm){
    	String avgerAgeFee = parm.getValue("AVERAGE_FEE");
    	NumberFormat nf = NumberFormat.getNumberInstance();   
    	nf.setMaximumFractionDigits(2);   
    	Double fee = Double.parseDouble(nf.format(Double.parseDouble(avgerAgeFee)));//System.out.println(fee);
    	String sql = "UPDATE SYS_diagnosis "+
    				 "SET "+
    				 "ICD_CHN_DESC = '"+parm.getValue("ICD_CHN_DESC")+"' , "+
    				 "ICD_ENG_DESC = '"+parm.getValue("ICD_ENG_DESC")+"' , "+
    				 "PY1 = '"+parm.getValue("PY1")+"' , "+
    				 "PY2 = '"+parm.getValue("PY2")+"' , "+
    				 "SEQ = "+Integer.parseInt(parm.getValue("SEQ"))+" , "+
    				 "DESCRIPTION = '"+parm.getValue("DESCRIPTION")+"' , "+
    				 "SYNDROME_FLG = '"+parm.getValue("SYNDROME_FLG")+"' , "+
    				 "MDC_CODE = '"+parm.getValue("MDC_CODE")+"' , "+
    				 "CCMD_CODE = '"+parm.getValue("CCMD_CODE")+"' , "+
    				 "MAIN_DIAG_FLG = '"+parm.getValue("MAIN_DIAG_FLG")+"' , "+
    				 "CAT_FLG = '"+parm.getValue("CAT_FLG")+"' , "+
    				 "STANDARD_DAYS = "+Integer.parseInt(parm.getValue("STANDARD_DAYS"))+" , "+
    				 "CHLR_FLG = '"+parm.getValue("CHLR_FLG")+"' , "+
    				 "DISEASETYPE_CODE = '"+parm.getValue("DISEASETYPE_CODE")+"' , "+
    				 "MR_CODE = '"+parm.getValue("MR_CODE")+"' , "+
    				 "CHRONIC_FLG = '"+parm.getValue("CHRONIC_FLG")+"' , "+
    				 "START_AGE = "+Integer.parseInt(parm.getValue("START_AGE"))+" , "+
    				 "LIMIT_DEPT_CODE = '"+parm.getValue("LIMIT_DEPT_CODE")+"' , "+
    				 "LIMIT_SEX_CODE = '"+parm.getValue("LIMIT_SEX_CODE")+"' , "+
    				 "END_AGE = "+Integer.parseInt(parm.getValue("END_AGE"))+" , "+
    				 "AVERAGE_FEE = "+fee+" , "+
    				 "OPT_USER = '"+parm.getValue("OPT_USER")+"' , "+
    				 "OPT_TERM = '"+parm.getValue("OPT_TERM")+"' , "+
//    				 "OPT_DATE = TO_DATE(substr(parm.getValue("OPT_DATE"),1,19)+"','yyyy-MM-dd HH24:MI:ss') , " +
					//add yanglu 20181212 
					"OPT_DATE=SYSDATE"+" , "+
    				 "STA1_CODE = '"+parm.getValue("STA1_CODE")+"' , "+
    				 "STA2_CODE = '"+parm.getValue("STA2_CODE")+"' , "+
    				 "MIC_FLG = '"+parm.getValue("MIC_FLG")+"' , "+
    				 "NOTE_FLG = '"+parm.getValue("NOTE_FLG")+"' , "+
    				 "DIAG_DEPT_CODE = '"+parm.getValue("DIAG_DEPT_CODE")+"' "+
    				 "WHERE "+
    				 "ICD_TYPE = '"+parm.getValue("ICD_TYPE")+"' AND "+
    				 "ICD_CODE = '"+parm.getValue("ICD_CODE")+"' ";
    	
    	return sql;
    }
    
    public String insertSql(TParm parm){
    	String avgerAgeFee = parm.getValue("AVERAGE_FEE");
    	NumberFormat nf = NumberFormat.getNumberInstance();   
    	nf.setMaximumFractionDigits(2);   
    	Double fee = Double.parseDouble(nf.format(Double.parseDouble(avgerAgeFee)));//System.out.println(fee);
    	String sql = "INSERT INTO SYS_diagnosis "+
    				 "(ICD_CODE,ICD_TYPE,ICD_CHN_DESC,ICD_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,SYNDROME_FLG,MDC_CODE,"+
    				 "CCMD_CODE,MAIN_DIAG_FLG,CAT_FLG,STANDARD_DAYS,CHLR_FLG,DISEASETYPE_CODE,MR_CODE,CHRONIC_FLG,START_AGE,LIMIT_DEPT_CODE,LIMIT_SEX_CODE,"+
    				 "END_AGE,AVERAGE_FEE,OPT_USER,OPT_DATE,OPT_TERM,STA1_CODE,STA2_CODE,MIC_FLG,NOTE_FLG,DIAG_DEPT_CODE)"+
    				 "VALUES "+
    				 "('"+parm.getValue("ICD_CODE")+"',"+
    				 "'"+parm.getValue("ICD_TYPE")+"',"+
    				 "'"+parm.getValue("ICD_CHN_DESC")+"',"+
    				 "'"+parm.getValue("ICD_ENG_DESC")+"',"+
    				 "'"+parm.getValue("PY1")+"',"+
    				 "'"+parm.getValue("PY2")+"',"+
    				 ""+Integer.parseInt(parm.getValue("SEQ"))+","+
    				 "'"+parm.getValue("DESCRIPTION")+"',"+
    				 "'"+parm.getValue("SYNDROME_FLG")+"',"+
    				 "'"+parm.getValue("MDC_CODE")+"',"+
    				 "'"+parm.getValue("CCMD_CODE")+"',"+
    				 "'"+parm.getValue("MAIN_DIAG_FLG")+"',"+
    				 "'"+parm.getValue("CAT_FLG")+"',"+
    				 ""+Integer.parseInt(parm.getValue("STANDARD_DAYS"))+","+
    				 "'"+parm.getValue("CHLR_FLG")+"',"+
    				 "'"+parm.getValue("DISEASETYPE_CODE")+"',"+
    				 "'"+parm.getValue("MR_CODE")+"',"+
    				 "'"+parm.getValue("CHRONIC_FLG")+"',"+
    				 ""+Integer.parseInt(parm.getValue("START_AGE"))+","+
    				 "'"+parm.getValue("LIMIT_DEPT_CODE")+"',"+
    				 "'"+parm.getValue("LIMIT_SEX_CODE")+"',"+
    				 ""+Integer.parseInt(parm.getValue("END_AGE"))+","+
    				 ""+fee+","+
    				 "'"+parm.getValue("OPT_USER")+"',"+
//    				 "TO_DATE('"+parm.getValue("OPT_DATE").substring(0, parm.getValue("OPT_DATE").length()-2)+"','yyyy-MM-dd HH24:MI:ss') , " +	    				 
					"SYSDATE"+" , "+
					"'"+parm.getValue("OPT_TERM")+"',"+
    				 "'"+parm.getValue("STA1_CODE")+"',"+
    				 "'"+parm.getValue("STA2_CODE")+"',"+
    				 "'"+parm.getValue("MIC_FLG")+"',"+
    				 "'"+parm.getValue("NOTE_FLG")+"',"+
    				 "'"+parm.getValue("DIAG_DEPT_CODE")+"'"+
    				 ")";
    	
    	return sql;
    }
    /**
     * 保存事件
     */
    public void onSave() {
    	
    	TParm parm = new TParm();
    	parm.setData("ICD_TYPE", (StringTool.getBoolean(this.getValueString("W")) == true) ?
    							 "W" : "C" );
    	parm.setData("ICD_CODE", this.getValueString("ICD_CODE").toUpperCase());
    	parm.setData("ICD_CHN_DESC", this.getValueString("ICD_CHN_DESC"));
    	parm.setData("ICD_ENG_DESC", this.getValueString("ICD_ENG_DESC"));
    	parm.setData("PY1", this.getValueString("PY1"));
    	parm.setData("PY2", this.getValueString("PY2"));
    	parm.setData("SEQ", TCM_Transform.getLong(this.getValue("SEQ")));
    	parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
    	parm.setData("SYNDROME_FLG", this.getValueString("SYNDROME_FLG"));
    	parm.setData("MDC_CODE", this.getValueString("MDC_CODE"));
    	parm.setData("CCMD_CODE", this.getValueString("CCMD_CODE"));
    	parm.setData("MAIN_DIAG_FLG", this.getValueString("MAIN_DIAG_FLG"));
    	parm.setData("CAT_FLG", this.getValueString("CAT_FLG"));
    	parm.setData("STANDARD_DAYS", this.getValueString("STANDARD_DAYS"));
    	parm.setData("CHLR_FLG", this.getValueString("CHLR_FLG"));
    	parm.setData("DISEASETYPE_CODE", this.getValueString("DISEASETYPE_CODE"));
    	parm.setData("MR_CODE", this.getValueString("MR_CODE"));
    	parm.setData("CHRONIC_FLG", this.getValueString("CHRONIC_FLG"));
    	parm.setData("START_AGE", TCM_Transform.getLong(this.getValue("START_AGE")));
    	parm.setData("LIMIT_DEPT_CODE", this.getValueString("LIMIT_DEPT_CODE"));
    	parm.setData("LIMIT_SEX_CODE", this.getValueString("LIMIT_SEX_CODE"));
    	parm.setData("END_AGE", TCM_Transform.getLong(this.getValue("END_AGE")));
    	parm.setData("AVERAGE_FEE", TCM_Transform.getLong(this.getValue("AVERAGE_FEE")));
    	parm.setData("OPT_USER", Operator.getID());
    	parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
    	parm.setData("OPT_TERM", Operator.getIP());
    	parm.setData("STA1_CODE", this.getValueString("STA1_CODE"));
    	parm.setData("STA2_CODE", this.getValueString("STA2_CODE"));
    	parm.setData("MIC_FLG", this.getValueString("MIC_FLG"));
    	parm.setData("NOTE_FLG", this.getValueString("NOTE_FLG"));
    	parm.setData("DIAG_DEPT_CODE", this.getValueString("DIAG_DEPT_CODE"));

    	TTextField tt = (TTextField)getComponent("ICD_CODE");
    	TParm result = null;
    	//保存方法save
    	if(tt.isEnabled()){
    		String sql = insertSql(parm);
    		result = new TParm(TJDODBTool.getInstance().update(sql));
    		if(result.getErrCode() != 0){
    			this.messageBox("保存失败");    			
    		}else{
    			this.messageBox("保存成功");
    		}
    		TIOM_Database.logTableAction("SYS_DIAGNOSIS");  //add by huangtt 20150303
    		onClear();
    		return;
    	}else{
    	//更新方法
	    	String sql = updateSql(parm);
	    	result = new TParm(TJDODBTool.getInstance().update(sql));
	    	if(result.getErrCode() != 0){
				this.messageBox("保存失败");
			}else{
    			this.messageBox("保存成功");
    		}
	    	TIOM_Database.logTableAction("SYS_DIAGNOSIS");  //add by huangtt 20150303
    		onClear();
    		return;
    	}
//        int row = -1;
//        if (selectRow < 0) {
//            row = allTds.insertRow();
//        }
//        else {
//            row = table.getSelectedRow();
//        }
//        if (row < 0) {
//            row = 0;
//        }
//        //ICD_TYPE
//        allTds.setItem(row, "ICD_TYPE",
//                       (StringTool.getBoolean(this.getValueString("W")) == true) ?
//                       "W" : "C");
//        //ICD_CODE
//        allTds.setItem(row, "ICD_CODE", this.getValueString("ICD_CODE").toUpperCase());//modify by wanglong 20140321
//        //ICD_CHN_DESC
//        allTds.setItem(row, "ICD_CHN_DESC", this.getValueString("ICD_CHN_DESC"));
//        //ICD_ENG_DESC
//        allTds.setItem(row, "ICD_ENG_DESC", this.getValueString("ICD_ENG_DESC"));
//        //PY1
//        allTds.setItem(row, "PY1", this.getValueString("PY1"));
//        //PY2
//        allTds.setItem(row, "PY2", this.getValueString("PY2"));
//        //SEQ
//        allTds.setItem(row, "SEQ", TCM_Transform.getLong(this.getValue("SEQ")));
//        //DESCRIPTION
//        allTds.setItem(row, "DESCRIPTION", this.getValueString("DESCRIPTION"));
//        //SYNDROME_FLG
//        //this.messageBox_(this.getValueString("ICD_CHN_DESC"));
//        allTds.setItem(row, "SYNDROME_FLG", this.getValueString("SYNDROME_FLG"));
//        //MDC_CODE
//        allTds.setItem(row, "MDC_CODE", this.getValueString("MDC_CODE"));
//        //CCMD_CODE
//        allTds.setItem(row, "CCMD_CODE", this.getValueString("CCMD_CODE"));
//        //MAIN_DIAG_FLG
//        allTds.setItem(row, "MAIN_DIAG_FLG",
//                       this.getValueString("MAIN_DIAG_FLG"));
//        //CAT_FLG
//        allTds.setItem(row, "CAT_FLG", this.getValueString("CAT_FLG"));
//        //STANDARD_DAYS
//        allTds.setItem(row, "STANDARD_DAYS",
//                       this.getValueString("STANDARD_DAYS"));
//        //CHLR_FLG
//        allTds.setItem(row, "CHLR_FLG", this.getValueString("CHLR_FLG"));
//        //DISEASETYPE_CODE
//        allTds.setItem(row, "DISEASETYPE_CODE",
//                       this.getValueString("DISEASETYPE_CODE"));
//        //MR_CODE
//        allTds.setItem(row, "MR_CODE", this.getValueString("MR_CODE"));
//        //CHRONIC_FLG
//        allTds.setItem(row, "CHRONIC_FLG", this.getValueString("CHRONIC_FLG"));
//        //START_AGE
//        allTds.setItem(row, "START_AGE",
//                       TCM_Transform.getLong(this.getValue("START_AGE")));
//        //LIMIT_DEPT_CODE
//        allTds.setItem(row, "LIMIT_DEPT_CODE",
//                       this.getValueString("LIMIT_DEPT_CODE"));
//        //LIMIT_SEX_CODE
//        allTds.setItem(row, "LIMIT_SEX_CODE",
//                       this.getValueString("LIMIT_SEX_CODE"));
//        //END_AGE
//        allTds.setItem(row, "END_AGE",
//                       TCM_Transform.getLong(this.getValue("END_AGE")));
//        //AVERAGE_FEE
//        allTds.setItem(row, "AVERAGE_FEE",
//                       TCM_Transform.getLong(this.getValue("AVERAGE_FEE")));
//        //OPT_USER
//        allTds.setItem(row, "OPT_USER", Operator.getID());
//        //OPT_DATE
//        allTds.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
//        //OPT_TERM
//        allTds.setItem(row, "OPT_TERM", Operator.getIP());
//        allTds.setItem(row, "STA1_CODE", this.getValueString("STA1_CODE"));
//        allTds.setItem(row, "STA2_CODE", this.getValueString("STA2_CODE"));//add by guoy 20160217 增加对应卫生部疾病名称
//        allTds.setItem(row, "MIC_FLG", this.getValueString("MIC_FLG"));//add by wanglong 20140321 增加形态学诊断注记
//        allTds.setItem(row, "NOTE_FLG", this.getValueString("NOTE_FLG"));
//        allTds.setItem(row, "DIAG_DEPT_CODE", this.getValueString("DIAG_DEPT_CODE"));
//        String[] sql = allTds.getUpdateSQL();
//        System.out.println("sql1::::::"+sql[0]);
//        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//        if (result.getErrCode() != 0) {
//            //this.messageBox_(result.getErrText());
//            this.messageBox("保存成功");
//        }
//        else {
//            this.messageBox("保存成功");
//        }
//        TIOM_Database.logTableAction("SYS_DIAGNOSIS");  //add by huangtt 20150303
//        onClear();
    }
//	public void onCode(){
//		String code=this.getValueString("ICD_CHN_DESC");
//		StringUtil s;
//		if(StringUtil.isNullString(code)){
//			return ;
//		}
//
//	}
    
	/**
	 * 传回诊断数据
	 *
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 * @author wangbin 2014/12/26
	 */
	public void popReturn(String tag, Object obj) throws Exception {
		TParm parm = (TParm) obj;
		parm.setData("ICD_DESC",parm.getValue("ICD_CHN_DESC"));
		setValueForParm(TAG, parm);
		if (StringUtils.equals("W", parm.getValue("ICD_TYPE"))) {
			this.setValue("W", "Y");
			this.onW();
		} else {
			this.setValue("C", "Y");
			this.onC();
		}
		this.onQuery();
		if (table.getDataStore() != null && table.getDataStore().rowCount() > 0) {
			/*table.setSelectedRow(0);
			this.onTableClick();*/
		}
	}
	
	 /**
     *手术弹出界面 OpICD
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComponentOP() {
    	
        TTextField textfield = (TTextField) this.getTextField("ICD_DESC");
        textfield.onInit();
        //给table上的新text增加ICD10弹出窗口
//        textfield.setPopupMenuParameter("OP_ICD",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSOpICD.x"));
		callFunction("UI|ICD_DESC|setPopupMenuParameter", "OP_ICD",
				"%ROOT%\\config\\sys\\SYSOpICD.x");// 医嘱代码
        //给新text增加接受ICD10弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newOPOrder");
    }
    /**
     * 取得手术ICD返回值
     * @param tag String
     * @param obj Object
     */
    public void newOPOrder(String tag, Object obj) {
        //sysfee返回的数据包
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("OPERATION_ICD");
        if("en".equals(this.getLanguage())){
        	this.setValue("ICD_DESC", parm.getValue("OPT_ENG_DESC"));
        }else{
        	this.setValue("ICD_DESC", parm.getValue("OPT_CHN_DESC"));
        }
        this.setValue("OPERATION_ICD", orderCode);
        this.onQuery();
    }
    
    /**
	 * 校验icd 是否已经存在
	 */
	public void checkIcd(){
		if(flg){
			String icdCode = getText("ICD_CODE");
			TParm result = new TParm(TJDODBTool.getInstance().select("SELECT ICD_CODE FROM SYS_DIAGNOSIS WHERE ICD_CODE='"+icdCode+"'"));
			if(result.getCount()>0){
				this.messageBox("ICD代码不可重复");
				return;
			}
		}
	}
    
    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    /**
     * 得到TLabel对象
     * @param tagName
     * @return
     */
    private TLabel getLabel(String tagName){
    	return (TLabel) getComponent(tagName);
    }
}
