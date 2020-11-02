package com.javahis.ui.emr;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import jdo.odi.OdiOrderTool;
import jdo.odo.OpdRxSheetTool;
import jdo.opd.ODOTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.odo.OdoMainControl;
import com.javahis.util.DateUtil;
import com.javahis.util.OdoUtil;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMRSingleControl extends TControl {
    /**
     * 定义TABLE
     */
    private final String TABLE="TABLEPAS";
    private final String RTABLE="TABLE";
    public OdoMainControl odoMainControl;
    /**
     * 系统别
     */
    private String systemCode;
    /**
     * 门急住别
     */
    private String admType;
    /**
     * 病案号
     */
    private String mrNo;
    /**
     * 病患姓名
     */
    private String patName;
    /**
     * 病患英文名称
     */
    private String patEnName;
    /**
     * 就诊号
     */
    private String caseNo;
    /**
     * 住院号
     */
    private String ipdNo;
    /**
     * 数据
     */
    private TParm parmData;
    /**
     * 入院日期
     */
    private Timestamp admDate;
    /**
     * 部门
     */
    private String deptCode;
    /**
     * 病区
     */
    private String stationCode;
    /**
     * 打开样式
     */
    private String styleType;
    /**
     * 权限
     */
    private String rultType;
    
    /**
     * 住院检查医嘱所属病区
     */
    private String orderStationDesc;
    
    /**
     * 应付金额  add by kangy 20170522
     */
    private String arAmt;
    public String getarAmt() {
		return arAmt;
	}
	public void setarAmt(String arAmt) {
		this.arAmt = arAmt;
	}
    /**
     * 套餐项目  add by kangy  20170522
     */
    private String memPacakgeFlg;
    public String getmemPacakgeFlg() {
		return memPacakgeFlg;
	}
	public void setmemPacakgeFlg(String memPacakgeFlg) {
		this.memPacakgeFlg = memPacakgeFlg;
	}
    
    /**
     * 病人床号  add by wukai on 20160825
     */
    private String bedNo;
    public String getOrderStationDesc() {
		return orderStationDesc;
	}
	public void setOrderStationDesc(String orderStationDesc) {
		this.orderStationDesc = orderStationDesc;
	}
	/**
     * 类型：抗菌药品申请单使用
     * =======pangben 2013-7-30
     */
    private String phaType;
    private Timestamp lmpTime;//末次月经时间
    public String getPhaType() {
		return phaType;
	}
	public void setPhaType(String phaType) {
		this.phaType = phaType;
	}
	TWord word;
	 
	TParm MParm;
	 
	public TParm getMParm() {
		return MParm;
	}
	public void setMParm(TParm mParm) {
		MParm = mParm;
	}
	
	public TParm Eresult;
	public TParm getEresult() {
		return Eresult;
	}
	public void setEresult(TParm eresult) {
		Eresult = eresult;
	}
	
	private String exexPlace;//add caoyong 检验检查执行地点
	
	public String getExexPlace() {
		return exexPlace;
	}
	public void setExexPlace(String exexPlace) {
		this.exexPlace = exexPlace;
	}
	private String drNote;//add liling 医师备注
	public String getDrNote() {
		return drNote;
	}
	public void setDrNote(String drNote) {
		this.drNote = drNote;
	}
	/**
	 * 预约号 add caoy
	 */
	private String resvNo;
	/**
	 * 住院申请单标记 add caoy
	 */
	private String resvFlg;
	
	public String getResvNo() {
		return resvNo;
	}
	public void setResvNo(String resvNo) {
		this.resvNo = resvNo;
	}
	public String getResvFlg() {
		return resvFlg;
	}
	public void setResvFlg(String resvFlg) {
		this.resvFlg = resvFlg;
	}
	
	public Timestamp getLmpTime() {
		return lmpTime;
	}
	public void setLmpTime(Timestamp lmpTime) {
		this.lmpTime = lmpTime;
	}
	/**
     * 初始化
     */
    public void onInit(){
        super.onInit();
        //初始化界面
        word = (TWord)this.getComponent("WORD1");
        this.initPage();
        //监听TABLE
        callFunction("UI|" + "TABLEPAS" + "|addEventListener",
                    "TABLEPAS" + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableClicked");
        callFunction("UI|TABLE|addEventListener","TABLE->"+TTableEvent.CLICKED,this,"onTABLEClicked");//检验
        TTabbedPane tabbedPane = ((TTabbedPane) this
				.getComponent("tTabbedPane_2"));
    }
    /**
     * 初始化页面
     */
    public void initPage(){
  
        TParm parm  = new TParm();
        Object obj = this.getParameter();
//        this.messageBox(obj+"");
        if(obj!=null){
            parm = (TParm)obj;
            //System.out.println("---parm111---"+parm);
            this.setSystemCode(parm.getValue("SYSTEM_CODE"));
            this.setCaseNo(parm.getValue("CASE_NO"));
            this.setMrNo(parm.getValue("MR_NO"));
            this.patEnName = getPatEnName(this.getMrNo());
            this.setIpdNo(parm.getValue("IPD_NO"));
            this.setPatName(parm.getValue("PAT_NAME"));
            //System.out.println("EMRSingle bed no ::::::  " + parm.getValue("BED_NO"));
            this.setBedNo(parm.getValue("BED_NO"));
            this.setAdmDate(parm.getTimestamp("ADM_DATE"));
            this.setAdmType(parm.getValue("ADM_TYPE"));
            TParm dParm = (TParm) parm.getData("CParm");
//            //科室改为开单科室 modify by huangjw 20140912
//            String dept_code=dParm.getValue("DEPT_CODE",0);
//            this.setDeptCode(dept_code);
            this.setStationCode(parm.getValue("STATION_CODE"));
            this.setStyleType(parm.getValue("STYLETYPE"));
            this.setRultType(parm.getValue("RULETYPE"));
            TParm JXparm=new TParm();
            JXparm=(TParm)parm.getData("EMR_DATA_LIST");
            for(int i=0;i<JXparm.getCount(); i++){
            	for(int j=0;j<dParm.getCount();j++){
            		if(dParm.getValue("MED_APPLY_NO",j).equals(JXparm.getValue("MED_APPLY_NO",i))){
            			JXparm.setData("AR_AMT",i,dParm.getValue("AR_AMT",j));
            		}
            	}
            }
            this.setParmData(JXparm);
            this.setPhaType(parm.getValue("PHATYPE"));//======pangben 2013-7-30 抗菌药申请单
            this.setResvFlg(parm.getValue("RESVfLG"));
            this.setResvNo(parm.getValue("RESV_NO"));
            
//            this.setDeptCode(parm.getValue("DEPT_CODE"));//20151217 wangjc add 住院科室
            
//            //检查申请单的 “服务者” 修改为 开单医师 modify by haungjw 20140915
//            TParm newParm=(TParm)parm.getData("EMR_DATA_LIST");
//            this.setDrcode(newParm.getValue("DR_CODE",0));
            TTabbedPane tabPane = (TTabbedPane) this
			.callFunction("UI|TABLEPANE|getThis");
            tabPane.setSelectedIndex(0);//默认打开第1页面（检验）
//			//add duzhw start 20140404 住院进入检验页签锁死
//            if("I".equals(this.getAdmType())) {
//            	TTabbedPane tabPane = (TTabbedPane) this
//						.callFunction("UI|TABLEPANE|getThis");
//            	//int selType = tabPane.getSelectedIndex();
//            	tabPane.setEnabledAt(0, false);//锁定第一页签（检验）
//                tabPane.setSelectedIndex(1);//默认打开第二页面（检查）
//                
//            }
//            //add duzhw end 20140404 住院进入检验页签锁死
            //add caoyong  20140324-------------start
//           if(!"I".equals(this.getAdmType())){
			TParm cParm = (TParm) parm.getData("CParm");
			if (cParm != null) {

				if (cParm.getCount("MED_APPLY_NO") <= 0) {
					TTabbedPane tabbedPane = ((TTabbedPane) this
							.getComponent("TABLEPANE"));
					tabbedPane.setSelectedIndex(1);
				} else {
					this.setEresult(cParm);
					List l = new ArrayList();
					double pageAmt = 0;
					for (int i = 0; i < cParm.getCount("MED_APPLY_NO"); i++) {
						if ("LIS".equals(cParm.getValue("ORDER_CAT1_CODE", i))) {
							if (!l.contains(cParm.getData("MED_APPLY_NO", i))) {
								l.add(cParm.getData("MED_APPLY_NO", i));
							}
						}
					}
					TParm sresult = new TParm();
					Iterator iter = l.iterator();
					TParm aresult = new TParm();
					while (iter.hasNext()) {
						String mapplyno = (String) iter.next();
						for (int g = 0; g < cParm.getCount("MED_APPLY_NO"); g++) {
							if ("LIS".equals(cParm.getValue("ORDER_CAT1_CODE",
									g))) {
								if (mapplyno.equals(cParm.getData(
										"MED_APPLY_NO", g))) {
									sresult.addRowData(cParm, g);
								}
							}
						}
						for (int k = 0; k < sresult.getCount(); k++) {
							pageAmt += StringTool.round(sresult.getDouble(
									"AR_AMT", k), 2);
						}
						aresult.addData("MED_APPLY_NO", mapplyno);
						
						//==================医嘱名称 add by huangjw 20140825
						StringBuffer str=new StringBuffer(sresult.getValue("ORDER_DESC",0));
						for(int i=1;i<sresult.getCount();i++){
							str.append(";"+sresult.getValue("ORDER_DESC",i));
						}
						aresult.addData("ORDER_DESC_SPECIFICATION", str);
						//==================医嘱名称 add by huangjw 20140825
						
						aresult.addData("DR_NOTE", sresult.getValue("CHN_DESC",
								0));
						aresult.addData("TOT_AMT", pageAmt);
						
						
						
						aresult.addData("DEPT_CODE", sresult.getValue("DEPT_CODE", 0));
						aresult.addData("STATION_CODE", sresult.getValue("STATION_CODE", 0));//20160118 wangjc 加入病区显示
//						aresult.addData("DR_CODE", sresult.getValue(
//								"ORDER_DR_CODE", 0));//==liling 20140819 modify
						if("ODI".equals(this.getSystemCode())){//住院开单医师
							aresult.addData("DR_CODE", sresult.getValue("ORDER_DR_CODE", 0));				
						}else{//门诊开单医师
							aresult.addData("DR_CODE", sresult.getValue("DR_CODE", 0));
						}
						pageAmt = 0;
						sresult = new TParm();

					}
					aresult.setCount(cParm.getCount());
					this.setMParm(aresult);
				}
			}
//			System.out.println("cParm++++"+cParm);
		}
		// }
        //add caoyong  20140324-------------end
        if("ODI".equals(this.getSystemCode())){
            this.setValue("MR_NO",this.getMrNo());
            this.patEnName = getPatEnName(this.getMrNo());
            this.setValue("IPD_NO",this.getIpdNo());
            if("en".equals(this.getLanguage())&& patEnName!=null&&patEnName.length()>0){
                 this.setValue("PAT_NAME", this.patEnName);
            }else{
                this.setValue("PAT_NAME",this.getPatName());
            }
        }
        if("ODO".equals(this.getSystemCode())){
            this.setValue("MR_NO",this.getMrNo());
            this.patEnName = getPatEnName(this.getMrNo());
            if("en".equals(this.getLanguage())&& patEnName!=null&&patEnName.length()>0){
                 this.setValue("PAT_NAME", this.patEnName);
            }else{
                this.setValue("PAT_NAME",this.getPatName());
            }
           // ((TLabel)this.getComponent("IPD_LAB")).setVisible(false);
          //  ((TTextField)this.getComponent("IPD_NO")).setVisible(false);
            
        }
        if("EMG".equals(this.getSystemCode())){
            this.setValue("MR_NO",this.getMrNo());
            this.patEnName = getPatEnName(this.getMrNo());
            if("en".equals(this.getLanguage())&& patEnName!=null&&patEnName.length()>0){
                 this.setValue("PAT_NAME", this.patEnName);
            }else{
                this.setValue("PAT_NAME",this.getPatName());
            }
            ((TLabel)this.getComponent("IPD_LAB")).setVisible(false);
            ((TTextField)this.getComponent("IPD_NO")).setVisible(false);
        }
        //初始化TABLE
//        this.messageBox_(this.getParmData());
         this.getTTable(TABLE).setParmValue(this.getParmData());
         this.getTTable(RTABLE).setParmValue(this.getMParm());
         
    }
    /**
     * 得到TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * 拿到病患英文名
     * @param mrNo String
     * @return String
     */
    public String getPatEnName(String mrNo){
        String patEnName = "";
        TParm parm = new TParm(this.getDBTool().select("SELECT PAT_NAME1 FROM SYS_PATINFO WHERE MR_NO='"+this.getMrNo()+"'"));
        if(parm.getCount()>0){
            patEnName = parm.getValue("PAT_NAME1",0);
        }
        return patEnName;
    }

    /**
     * 双击
     * @param row int
     */
    public void onTableClicked(int row){
//    	TParm parm=this.getTTable(TABLE).getParmValue().getRow(row);
//    	this.setExexPlace(parm.getValue("EXEA_PLACE"));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                onOpen();
            }
        });
    }
    /**
     * 打开检查检验单
     */
    public void onOpen(){
        int selectRow = (Integer) callFunction("UI|" + TABLE +"|getSelectedRow");
        if(selectRow<0){
            this.messageBox("E0111");
            return;
        }
        
        TParm pa = (TParm)this.getParameter();
        TParm parm  = this.getSelectRowData(TABLE);
        //this.messageBox_("11111RPTTYPE_DESC"+parm.getValue("RPTTYPE_DESC"));
//        System.out.println("++++++++"+parm);
        this.setExexPlace(parm.getValue("EXEA_PLACE"));
        this.setarAmt(parm.getValue("AR_AMT"));// add by kangy 20170522
        this.setmemPacakgeFlg(parm.getValue("MEM_PACKAGE_FLG"));// add by kangy 20170522
        this.setDrNote(parm.getValue("DR_NOTE"));
        TParm actionParm = this.getEmrFilePath(parm);
        TParm action = new TParm();
        getDiag(action);
        action.setData("DR_NOTE",parm.getValue("DR_NOTE"));
        action.setData("EXEA_PLACE", this.getExexPlace());// add caoy 添加检查申请单执行地点 2014、6,4
        action.setData("CTZ_CODE",getRegInfo().getValue("CTZ_DESC",0));
        
        if("ODI".equals(this.getSystemCode())){
        	String stationDesc = getStationInfo(parm.getValue("ORDER_NO"),parm.getValue("ORDER_SEQ"),this.getCaseNo());
        	action.setData("STATION_CODE",stationDesc);
        	this.setOrderStationDesc(stationDesc);
        	// start wuxy 2017 11/01
        	String OrderDate= getOrderDate(parm.getValue("ORDER_NO"),parm.getValue("ORDER_SEQ"),this.getCaseNo());
        	Timestamp t = Timestamp.valueOf(OrderDate);
        	action.setData("ADM_DATE",t);
        	//this.messageBox("!!!!!!:"+t.toString());
        }else{
        	action.setData("ADM_DATE",this.getAdmDate());
        }
        	//end wuxy 2017 11/01
        action.setData("SYSTEM_TYPE", this.getSystemCode());
        action.setData("CASE_NO",this.getCaseNo());
        action.setData("PAT_NAME",this.getPatName());
        action.setData("BED_NO",this.getBedNo());  //add by wukai on 20160825 添加病人床号  
        action.setData("MR_NO",this.getMrNo());
        action.setData("IPD_NO",this.getIpdNo()); 
        
        action.setData("ADM_TYPE",this.getAdmType());
        action.setData("DEPT_CODE",parm.getValue("DEPT_CODE"));
//        action.setData("DEPT_CODE",this.getDeptCode());//20151217 wangjc add 住院科室
        action.setData("STATION_CODE",this.getStationCode());
        action.setData("STYLETYPE",this.getStyleType());
        action.setData("RULETYPE",this.getRultType());
        action.setData("EMR_FILE_DATA",actionParm);
        action.setData("RESV_NO",this.getResvNo());
        action.setData("RESVfLG",this.getResvFlg());
        action.setData("DR_CODE",parm.getValue("DR_CODE"));
        action.setData("PRE_WEEK",onLmp()+"");//检查申请单 添加孕周信息 add by huangjw 20141014
        action.setData("LMP_TIME",this.getLmpTime());//检查申请单 添加末次月经时间 add by huangjw 20160218
        actionParm.setData("URGENT_FLG",parm.getValue("URGENT_FLG"));//========pangben modify 20110706 添加急作注记
        actionParm.setData("MED_APPLY_NO",parm.getValue("MED_APPLY_NO")); 
        action.setData("MEM_PACKAGE_FLG",this.getmemPacakgeFlg()); 
        action.setData("AR_AMT",this.getarAmt()); 
        action.setListenerData(pa.getListenerData());
        action.addListener("EMR_LISTENER",this,"emrListener");
        action.addListener("EMR_SAVE_LISTENER",this,"emrSaveListener");
        //System.out.println("---action----------------"+action);
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", action);
    }
    
    /**
     * 
     */
    public TParm getRegInfo(){
    	String ctzSql = "";
    	 if("ODI".equals(this.getSystemCode())){
    		 ctzSql = "SELECT B.CTZ_DESC FROM ADM_INP A,SYS_CTZ B WHERE CASE_NO = '"+this.getCaseNo()+"' " +
    		 		" AND A.CTZ1_CODE = B.CTZ_CODE  ";
    		 
    	 }else{
    		 ctzSql = "SELECT CTZ_DESC FROM REG_PATADM A,SYS_CTZ B WHERE CASE_NO = '"+this.getCaseNo()+"' " +
    		 		" AND A.CTZ1_CODE = B.CTZ_CODE ";
    	 }
    	 TParm result = new TParm(TJDODBTool.getInstance().select(ctzSql));
    	 return result;
    }
    
    public String getStationInfo(String orderNo,String orderSeq, String caseNo){
    	String sql = " SELECT B.STATION_DESC FROM ODI_ORDER A, SYS_STATION B WHERE A.CASE_NO = '"+caseNo+"'" +
    			" AND A.ORDER_NO = '"+orderNo+"' AND A.ORDER_SEQ = '"+orderSeq+"' AND A.STATION_CODE = B.STATION_CODE";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result.getValue("STATION_DESC",0);
    }
    //add wuxy 2017/11/01
    /**
     * 获取开立日期
     */
    public String getOrderDate(String orderNo,String orderSeq, String caseNo){
    	String sql = "SELECT ORDER_DATE FROM ODI_ORDER A WHERE A.CASE_NO='"+caseNo+"'" +
    			" AND A.ORDER_NO = '"+orderNo+"' AND A.ORDER_SEQ = '"+orderSeq+"' ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	return result.getValue("ORDER_DATE",0);
    }
    //end wuxy 2017/11/01
    /**
     * 获得诊断 add by huangjw 20150106
     * @param parm
     */
    public void getDiag(TParm parm){
        String strDiag="";
        if("ODI".equals(this.getSystemCode())){
        	strDiag=OdiOrderTool.getInstance().getIcdName(this.getCaseNo());
        }else{
        	strDiag=OpdRxSheetTool.getInstance().getIcdName(this.getCaseNo());
        } 
        int diagNum=0;
        if(strDiag.indexOf("#")!=-1){
        	// ---###start  modify caoyong 2014/4/17 临床诊断最多显示三个 ---
        	String diag[]=strDiag.split("#") ;
        	StringBuffer buf=new StringBuffer();
        	if(diag.length>3){
        		diagNum=3;
        	}else{
        		diagNum=diag.length;
        	}
        	//parm.setData("DIAG",diag[0]);
        	for(int i=0;i<diagNum;i++){
        		buf.append(";"+diag[i]);
        	}
        	//inParam.setData("DIAG1", text, "" + strDiag.split(";")[1]);
        	if(buf.toString().indexOf(";")!=-1){
        		parm.setData("DIAG", "" + buf.toString().substring(1,buf.toString().length() ));
        	}
        }else{
        	parm.setData("DIAG", strDiag);
        }
    }
	/**
     * 事件
     * @param parm TParm
     */
	public void emrListener(TParm parm) {
		if (parm.getBoolean("FLG"))
			return;
		TParm tableParm = this.getSelectRowData(TABLE);
		//System.out.println("----tableParm---"+tableParm);
		String iptItemCode = getDictionary("SYS_OPTITEM", tableParm
				.getValue("OPTITEM_CODE"));
		parm.runListener("setCaptureValue", "EXEA_PLACE", this.getExexPlace());// add
																				// caoy
																				// 添加检查申请单执行地点
																				// 2014、6,4
		parm.runListener("setCaptureValue", "CTZ_CODE", getRegInfo().getValue("CTZ_DESC",0));
		 if("ODI".equals(this.getSystemCode())){
			 parm.runListener("setCaptureValue", "STATION_CODE", this.getOrderStationDesc());
		 }
		parm.runListener("setCaptureValue", "ORDER_CODE", tableParm
				.getValue("ORDER_DESC"));
		parm.runListener("setCaptureValue", "ITEM_CODE", iptItemCode);
		// 增加执行科室
		parm.runListener("setCaptureValue", "EXEC_DEPT_DESC", OpdRxSheetTool
				.getInstance()
				.getDeptName(tableParm.getValue("EXEC_DEPT_CODE")));
		parm.runListener("setCaptureValue", "DR_NOTE", this.getDrNote());//==liling 20140826 add 医师备注
		//
		parm.runListener("setCaptureValue", "RPTTYPE_DESC", tableParm.getValue("RPTTYPE_DESC"));
		parm.runListener("setCaptureValue", "AR_AMT", this.getarAmt());// add by kangy 20170522
		parm.runListener("setCaptureValue", "MEM_PACKAGE_FLG", this.getmemPacakgeFlg());// add by kangy 20170522
//		
		String title="";
		if("ODI".equals(this.getSystemCode())){
//        	this.messageBox("11zhu");
        	title="（住）检查申请单";
	    }
        if("ODO".equals(this.getSystemCode())){
//        	this.messageBox("men");
        	title= "（门）检查申请单";
	    }
        if("EMG".equals(this.getSystemCode())){
        	title="（急）检查申请单";
	    }
		parm.runListener("setCaptureValue", "APP_检查申请单_ADMTYPE", title);//==liling 20140807 add 
	}
    /**
     * 执行保存
     * @param pamr TParm
     * @return boolean
     */
    public boolean emrSaveListener(TParm parm){
        if(parm.getValue("CASE_NO").length()==0)
            return true;
//        System.out.println("parm===="+parm);
//        this.messageBox_("保存==="+parm);
        TParm emrParm = new TParm();
        if("I".equals(parm.getValue("ADM_TYPE"))){
            //System.out.println("UPDATE ODI_ORDER SET FILE_NO='"+parm.getValue("FILE_SEQ")+"' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND ORDER_NO='"+parm.getValue("ORDER_NO")+"' AND ORDER_SEQ='"+parm.getValue("ORDER_SEQ")+"'");
            emrParm = new TParm(this.getDBTool().update("UPDATE ODI_ORDER SET FILE_NO='"+parm.getValue("FILE_SEQ")+"' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND ORDER_NO='"+parm.getValue("ORDER_NO")+"' AND ORDER_SEQ='"+parm.getValue("ORDER_SEQ")+"'"));
//            ECCCall ecall = new ECCCall();
//            if(!ecall.init()){
//                return true;
//            }
//            TParm eccParm = new TParm(this.getDBTool().select("SELECT * FROM ODI_ORDER WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND ORDER_NO='"+parm.getValue("ORDER_NO")+"' AND ORDER_SEQ='"+parm.getValue("ORDER_SEQ")+"'"));
//            if(eccParm.getCount()>0){eccParm.getTimestamp("ORDER_DATE",0);
//                ecall.callEccApp(eccParm.getValue("MED_APPLY_NO",0),eccParm.getValue("CASE_NO",0),eccParm.getValue("CASE_NO",0),eccParm.getValue("DR_NOTE",0),eccParm.getValue("DR_NOTE",0),"2","3",eccParm.getValue("EXEC_DEPT_CODE",0),"电生理",StringTool.getString(eccParm.getTimestamp("ORDER_DATE",0),"yyyyMMddHHmmss"),eccParm.getValue("ORDER_DEPT_CODE",0),eccParm.getValue("ORDER_DR_CODE",0),"XX","",eccParm.getValue("ORDER_NO",0),eccParm.getValue("ORDER_SEQ",0),eccParm.getValue("ORDER_DESC",0),eccParm.getValue("ORDER_CODE",0),"1","20");
//            }
        }
        if("O".equals(parm.getValue("ADM_TYPE"))){
           emrParm = new TParm(this.getDBTool().update("UPDATE OPD_ORDER SET FILE_NO='"+parm.getValue("FILE_SEQ")+"' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND SEQ_NO='"+parm.getValue("ORDER_SEQ")+"'"));
        }
        if("E".equals(parm.getValue("ADM_TYPE"))){
           emrParm = new TParm(this.getDBTool().update("UPDATE OPD_ORDER SET FILE_NO='"+parm.getValue("FILE_SEQ")+"' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND SEQ_NO='"+parm.getValue("ORDER_SEQ")+"'"));
        }
        if("H".equals(parm.getValue("ADM_TYPE"))){
           emrParm = new TParm(this.getDBTool().update("UPDATE HRM_ORDER SET FILE_NO='"+parm.getValue("FILE_SEQ")+"' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND SEQ_NO='"+parm.getValue("ORDER_SEQ")+"'"));
        }
        if(emrParm.getErrCode()<0){
            return false;
        }
        TParm parmTable  = this.getTTable(TABLE).getParmValue();
        int rowCount = parmTable.getCount("FILE_NO");
        for(int i=0;i<rowCount;i++){
            TParm temp = parmTable.getRow(i);
            if(temp.getValue("ORDER_NO").equals(parm.getValue("ORDER_NO"))&&temp.getValue("ORDER_SEQ").equals(parm.getValue("ORDER_SEQ"))){
                parmTable.setData("FILE_NO",i,parm.getValue("FILE_SEQ"));
            }
        }
//        this.messageBox_(parmTable);
        this.getTTable(TABLE).setParmValue(parmTable);
        return true;
    }
    /**
     * 得到EMR路径
     * @param parm TParm
     * @return String
     */
    public TParm getEmrFilePath(TParm parm){
        String sqlO="SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,A.TEMPLET_PATH,A.DEPT_CODE,"+
            " B.FILE_NAME,B.FILE_PATH,B.DESIGN_NAME,B.FILE_SEQ,B.DISPOSAC_FLG "+
            " FROM EMR_TEMPLET A,EMR_FILE_INDEX B WHERE A.CLASS_CODE=B.CLASS_CODE AND A.SUBCLASS_CODE = B.SUBCLASS_CODE AND A.OPD_FLG='Y'"+
            " AND A.SUBCLASS_CODE='"+parm.getValue("MR_CODE")+"' AND B.FILE_SEQ='"+parm.getValue("FILE_NO")+"'  AND B.CASE_NO='"+this.getCaseNo()+"' AND B.DISPOSAC_FLG='N' ORDER BY FILE_SEQ DESC";

        String sqlE="SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,A.TEMPLET_PATH,A.DEPT_CODE,"+
            " B.FILE_NAME,B.FILE_PATH,B.DESIGN_NAME,B.FILE_SEQ,B.DISPOSAC_FLG "+
            " FROM EMR_TEMPLET A,EMR_FILE_INDEX B WHERE A.CLASS_CODE=B.CLASS_CODE AND A.SUBCLASS_CODE = B.SUBCLASS_CODE AND A.EMG_FLG='Y'"+
            " AND A.SUBCLASS_CODE='"+parm.getValue("MR_CODE")+"' AND B.FILE_SEQ='"+parm.getValue("FILE_NO")+"'  AND B.CASE_NO='"+this.getCaseNo()+"' AND B.DISPOSAC_FLG='N' ORDER BY FILE_SEQ DESC";

        String sqlH="SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,A.TEMPLET_PATH,A.DEPT_CODE,"+
           " B.FILE_NAME,B.FILE_PATH,B.DESIGN_NAME,B.FILE_SEQ,B.DISPOSAC_FLG "+
           " FROM EMR_TEMPLET A,EMR_FILE_INDEX B WHERE A.CLASS_CODE=B.CLASS_CODE AND A.SUBCLASS_CODE = B.SUBCLASS_CODE AND A.HRM_FLG='Y'"+
           " AND A.SUBCLASS_CODE='"+parm.getValue("MR_CODE")+"' AND B.FILE_SEQ='"+parm.getValue("FILE_NO")+"'  AND B.CASE_NO='"+this.getCaseNo()+"' AND B.DISPOSAC_FLG='N' ORDER BY FILE_SEQ DESC";

        String sqlI="SELECT A.SUBCLASS_CODE,A.EMT_FILENAME,A.SUBCLASS_DESC,A.CLASS_CODE,A.TEMPLET_PATH,A.DEPT_CODE,"+
            " B.FILE_NAME,B.FILE_PATH,B.DESIGN_NAME,B.FILE_SEQ,B.DISPOSAC_FLG "+
            " FROM EMR_TEMPLET A,EMR_FILE_INDEX B WHERE A.CLASS_CODE=B.CLASS_CODE AND A.SUBCLASS_CODE = B.SUBCLASS_CODE AND A.IPD_FLG='Y'"+
            " AND A.SUBCLASS_CODE='"+parm.getValue("MR_CODE")+"' AND B.FILE_SEQ='"+parm.getValue("FILE_NO")+"'  AND B.CASE_NO='"+this.getCaseNo()+"' AND B.DISPOSAC_FLG='N' ORDER BY FILE_SEQ DESC";
        TParm result = new TParm();
        if("O".equals(this.getAdmType())){
            result = new TParm(this.getDBTool().select(sqlO));
            //System.out.println("=======EMRO路径:"+sqlO);
        }
        if("E".equals(this.getAdmType())){
            result = new TParm(this.getDBTool().select(sqlE));
            //System.out.println("=======EMRE路径:"+sqlE);
        }
        if("H".equals(this.getAdmType())){
            result = new TParm(this.getDBTool().select(sqlH));
            //System.out.println("=======EMRH路径:"+sqlH);
        }
        if("I".equals(this.getAdmType())){
            result = new TParm(this.getDBTool().select(sqlI));
            //System.out.println("=======EMRI路径:"+sqlI);
        }
//        this.messageBox_("查询历史:"+result);
        if(result.getInt("ACTION","COUNT")>0){
            result.setData("FLG",true);
            TParm action = result.getRow(0);
            action.setData("FLG",result.getData("FLG"));
            return action;
        }else{
            //System.out.println("MR_CODE"+parm.getValue("MR_CODE"));
            String sqlODO = "SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+parm.getValue("MR_CODE")+"' AND OPD_FLG='Y'";
            String sqlEMG = "SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+parm.getValue("MR_CODE")+"' AND EMG_FLG='Y'";
            String sqlHRM = "SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+parm.getValue("MR_CODE")+"' AND HRM_FLG='Y'";
            String sqlODI = "SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE"+
                " FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '"+parm.getValue("MR_CODE")+"' AND IPD_FLG='Y'";
            if("O".equals(this.getAdmType())){
                result = new TParm(this.getDBTool().select(sqlODO));
                //System.out.println("=======EMROSQL:"+sqlODO);
            }
            if("E".equals(this.getAdmType())){
                result = new TParm(this.getDBTool().select(sqlEMG));
                //System.out.println("=======EMRESQL:"+sqlEMG);
            }
            if("H".equals(this.getAdmType())){
                result = new TParm(this.getDBTool().select(sqlHRM));
            }
            if("I".equals(this.getAdmType())){
                result = new TParm(this.getDBTool().select(sqlODI));
            }
            result.setData("FLG",false);
        }
        //System.out.println("result"+result);
        TParm action = result.getRow(0);
        action.setData("FLG",result.getData("FLG"));
        action.setData("ORDER_NO",parm.getValue("ORDER_NO"));
        action.setData("ORDER_SEQ",parm.getValue("ORDER_SEQ"));
        action.setData("TYPEEMR","SQD");
//        System.out.println("action================"+action);

        return action;
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
        return TJDODBTool.getInstance();
    }

    /**
     * 得到选中行数据
     * @param tableTag String
     * @return TParm
     */
    public TParm getSelectRowData(String tableTag){
        int selectRow = (Integer) callFunction("UI|" + tableTag +"|getSelectedRow");
        if(selectRow<0)
            return new TParm();
//        out("行号" + selectRow);
        TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
//        out("GRID数据" + parm);
        TParm parmRow = parm.getRow(selectRow);
        //System.out.println("选中行:"+parmRow);
        return parmRow;
    }
    /**
     * 拿到字典信息
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId,String id){
        String result="";
        TParm parm = new TParm(this.getDBTool().select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"+groupId+"' AND ID='"+id+"'"));
        result = parm.getValue("CHN_DESC",0);
        return result;
    }
    
    /**
	 * 单击事件 add caoyong 2014/03/22
	 */
	public void onTABLEClicked(int row){
		 DecimalFormat df = new DecimalFormat("#.00");
		if(row<0){
			return;
		}
		String mapplyno=this.getTTable(RTABLE).getParmValue().getRow(row).getValue("MED_APPLY_NO");
	  	String drCode =   this.getTTable(RTABLE).getParmValue().getRow(row).getValue("DR_CODE");
	  	String dept_code=this.getTTable(RTABLE).getParmValue().getRow(row).getValue("DEPT_CODE");
	  	
	  	String stationCode = this.getTTable(RTABLE).getParmValue().getRow(row).getValue("STATION_CODE");
//			this.messageBox("drCode+++"+row+"++++"+drCode); 
		  TParm inParam = new TParm();
	        String text = "TEXT";
	       
	        inParam.setData("BIRTH_DAY", text,
	        		  OpdRxSheetTool.getInstance().getBirthDays(this.getMrNo()));
	        //20160118 wangjc 加入病区显示
	        TParm stationParm = new TParm(TJDODBTool.getInstance().select("SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE = '"+stationCode+"' "));
	        String stationDesc = "";
	        if(stationParm.getCount("STATION_DESC") > 0){
	        	stationDesc = "("+stationParm.getValue("STATION_DESC", 0)+")";
	        }
	        inParam.setData("DEPT_CODE", text, OpdRxSheetTool.getInstance().getDeptName(dept_code)+stationDesc);//20151217 wangjc modify
	      //20160118 wangjc 加入病区显示
//	        inParam.setData("DEPT_CODE", text, OpdRxSheetTool.getInstance().getDeptName(this.getDeptCode()));//20151217 wangjc add 住院科室
	        inParam.setData("BED_NO", text, this.getBedNo());   //add by wukai 20160913 增加床号显示
	        inParam.setData("MR_NO", text,this.getMrNo());
	        inParam.setData("PAT_NAME", text,
	                        OpdRxSheetTool.getInstance().getPatName(this.getMrNo()));
	        inParam.setData("BAR_CODE", text, mapplyno);
	        String strDiag="";
	        if("ODI".equals(this.getSystemCode())){
	        	strDiag=OdiOrderTool.getInstance().getIcdName(this.getCaseNo());
	        }else{
	        	strDiag=OpdRxSheetTool.getInstance().getIcdName(this.getCaseNo());
	        } 
	        int diagNum=0;
	        if(strDiag.indexOf("#")!=-1){
	        	// ---###start  modify caoyong 2014/4/17 临床诊断最多显示三个 ---
	        	String diag[]=strDiag.split("#") ;
	        	StringBuffer buf=new StringBuffer();
	        	if(diag.length>3){
	        		diagNum=3;
	        	}else{
	        		diagNum=diag.length;
	        	}
	        	inParam.setData("DIAG", text,diag[0]);
	        	for(int i=1;i<diagNum;i++){
	        		buf.append(";"+diag[i]);
	        	}
	        	//inParam.setData("DIAG1", text, "" + strDiag.split(";")[1]);
	        	if(buf.toString().indexOf(";")!=-1){
	        		inParam.setData("DIAG1", text, "" + buf.toString().substring(1,buf.toString().length() ));
	        	}
	        	// ---###   modify caoyong 2014/4/17 临床诊断最多显示三个 ---
	        }else{
	        	inParam.setData("DIAG", text, strDiag);
	        }
//	        inParam.setData("DIAG", text,  OpdRxSheetTool.getInstance().getIcdName(this.getCaseNo()));
	        inParam.setData("SEX_CODE", text,OpdRxSheetTool.getInstance().getSexName(this.getMrNo())); 
	        
	        //add by yangjj 20150612
//	        Timestamp sysDate = SystemTool.getInstance().getDate();
//	        Timestamp temp = OpdRxSheetTool.getInstance().getPatInfo(mrNo).getTimestamp("BIRTH_DATE", 0);
//	        String age = "0";
//	        age =DateUtil.showAge(temp, sysDate);
//	        
//	        if("ODI".equals(this.getSystemCode())){
//	        	inParam.setData("AGE", text, age);
//	        	//inParam.setData("AGE", text, OdiOrderTool.getInstance().getAgeName(this.getCaseNo(), this.getMrNo()).replace("日","天"));
//	        	inParam.setData("DR_CODE", text, OdiOrderTool.getInstance().GetAdmDr(drCode));
//	        }else{
//	        	inParam.setData("AGE", text, age);
//	        	//inParam.setData("AGE", text,OpdRxSheetTool.getInstance().getAgeName(this.getCaseNo(), this.getMrNo()).replace("日","天"));
////	        	inParam.setData("DR_CODE", text, OpdRxSheetTool.getInstance().GetRegDr(this.getCaseNo()));//==liling 20140819 modify
//	        	inParam.setData("DR_CODE", text, OpdRxSheetTool.getInstance().GetOpdDr(drCode));
//	        }
	        
	        //inParam.setData("FOOT_DR", text, "医师:" + this.GetRealRegDr(odo.getCaseNo()));
	        
	        TParm sresult=new TParm();
			TParm spram=new TParm();
			TParm cParm=this.getEresult();
			 
				 
				 
				 
				  for(int g=0;g<cParm.getCount();g++){
					  if("LIS".equals(cParm.getValue("ORDER_CAT1_CODE",g))){
					  if(mapplyno.equals(cParm.getData("MED_APPLY_NO", g))){
						  sresult.addRowData(cParm, g);
					  }
					  }
				  }
					  for(int k=0;k<sresult.getCount();k++){
						  spram.addData("ORDER_DESC", sresult.getValue("ORDER_DESC", k)); 
						  spram.addData("CHN_DESC", sresult.getValue("CHN_DESC", k)); 
						  spram.addData("AR_AMT",df.format(sresult.getDouble("AR_AMT", k)));
						  if(sresult.getValue("MEM_PACKAGE_FLG", k).equals("Y")
								  ||sresult.getValue("MEM_PACKAGE_FLG", k).equals("√")){
							  spram.addData("MEM_PACKAGE_FLG", "套餐项目");
						  }else{
							  spram.addData("MEM_PACKAGE_FLG", "");
						  }
						  //20151211 wangjc add start
						  if(sresult.getValue("URGENT_FLG", k).equals("Y")
								  ||sresult.getValue("URGENT_FLG", k).equals("√")){
							  spram.addData("URGENT_FLG", "急作");
						  }else{
							  spram.addData("URGENT_FLG", "");
						  }
						//20151211 wangjc add end
					  }
					  TParm psresulParm=ODOTool.getInstance().getSyscate(sresult.getValue("ORDER_CODE", 0).substring(0, 5));
					    spram.setCount(sresult.getCount("ORDER_DESC"));
					    spram.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
					    spram.addData("SYSTEM", "COLUMNS", "URGENT_FLG");//20151211 wangjc add
					    spram.addData("SYSTEM", "COLUMNS", "CHN_DESC");
					    spram.addData("SYSTEM", "COLUMNS", "AR_AMT");
					    spram.addData("SYSTEM", "COLUMNS", "MEM_PACKAGE_FLG");
					    inParam.setData("ORDER_TABLE", spram.getData());
					    inParam.setData("PRINT_TIME", text,sresult.getValue("OPT_DATE", 0));
					    inParam.setData("APPLICATION_NO", text, mapplyno);
					    inParam.setData("SAMPLE_SOURCE",text, sresult.getValue("BF", 0));
					    //add by huangtt 20140730
//					    inParam.setData("TITLE","TEXT", psresulParm.getValue("CATEGORY_CHN_DESC", 0)+" 门诊检验申请单");
					    
					    //add by wuxy 2017/9/12
					    Timestamp sysDate = Timestamp.valueOf(sresult.getValue("OPT_DATE", 0).replace("/", "-"));
					    Timestamp temp = OpdRxSheetTool.getInstance().getPatInfo(mrNo).getTimestamp("BIRTH_DATE", 0);
				        String age = "0";
				        age =DateUtil.showAge(temp, sysDate);
				        //this.messageBox(age);
				        if("ODI".equals(this.getSystemCode())){
				        	inParam.setData("AGE", text, age);
				        	//inParam.setData("AGE", text, OdiOrderTool.getInstance().getAgeName(this.getCaseNo(), this.getMrNo()).replace("日","天"));
				        	inParam.setData("DR_CODE", text, OdiOrderTool.getInstance().GetAdmDr(drCode));
				        }else{
				        	inParam.setData("AGE", text, age);
				        	//inParam.setData("AGE", text,OpdRxSheetTool.getInstance().getAgeName(this.getCaseNo(), this.getMrNo()).replace("日","天"));
//				        	inParam.setData("DR_CODE", text, OpdRxSheetTool.getInstance().GetRegDr(this.getCaseNo()));//==liling 20140819 modify
				        	inParam.setData("DR_CODE", text, OpdRxSheetTool.getInstance().GetOpdDr(drCode));
				        }
					    //end wuxy
					    
					    
					    
					    if("ODI".equals(this.getSystemCode())){
					    	inParam.setData("TITLE","TEXT", "住院检验申请单");
					    }else{
					    	inParam.setData("TITLE","TEXT", "门诊检验申请单");
					    }
					    //=========================================报表标题改为活的  modify by huangjw 20140909  start
						inParam.setData("HOSPITAL_DESC",text,Operator.getHospitalCHNFullName());
						inParam.setData("HOSPITAL_ENG",text,Operator.getHospitalENGFullName());
						//=========================================报表标题改为活的  modify by huangjw 20140909  end
						//===================孕周大于0则显示，等于0则不显示 modify by huangjw 20141212 start
						String preWeekCount="怀孕周数 PREWEEK:";
						if("".equals(onLmp())){
							preWeekCount="";
						}else{
							preWeekCount=preWeekCount+" "+onLmp();
						}
						//===================孕周大于0则显示，等于0则不显示 modify by huangjw 20141212 end
						inParam.setData("PRE_WEEK",text,preWeekCount);//添加孕周时间  add by huangjw 20140910
						
	        inParam.setData("PAT_NAME",this.getPatName());
		    word.setWordParameter(inParam);
	        word.setPreview(true);
	        word.setFileName("%ROOT%\\config\\prt\\OPD\\OPDApplicAtion.jhw");
			    
		  
	}
	
	/**
	 * 计算怀孕周数 add by huangjw 20140910
	 */
	public String onLmp()  {
		String sql = "SELECT LMP_DATE FROM REG_PATADM WHERE CASE_NO = '"+this.getCaseNo()+"'";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() <= 0 || result.getErrCode() < 0){
			sql="SELECT LMP_DATE FROM SYS_PATINFO WHERE MR_NO='"+this.getValue("MR_NO")+"'";
			result=new TParm(TJDODBTool.getInstance().select(sql));
		}
		this.setLmpTime(result.getTimestamp("LMP_DATE",0));
		String week = "";
		if(this.getAdmDate()!=null && result.getTimestamp("LMP_DATE",0)!=null){
			week = OdoUtil
				.getPreWeekNew(this.getAdmDate(),result.getTimestamp("LMP_DATE",0));
		}
		/*int week = OdoUtil
		.getPreWeek(TJDODBTool.getInstance().getDBTime(),result.getTimestamp("LMP_DATE",0));*/
		//System.out.println("week::::::"+week);
		return week;
	}
	public void onPrint() {
	      //  if (this.getTTable(RTABLE).getParmValue().getCount() <= 0) {
	           // this.messageBox_("没有数据");
	            //return;
	     //   }	
				int row=this.getTTable(RTABLE).getSelectedRow();
				if(row<0){
					this.messageBox("请选中行");
					return;
				}
				//int printNum = OpdRxSheetTool.getInstance().getPrintNum(caseNo, rxNo);
				//$$=========Modified by lx 2012/07/02 改成直接打印 START========$$//
				boolean flg=word.getWordText().getPM().getPageManager().print();
				//$$=========Modified by lx 2012/07/02 改成直接打印 END========$$//
		
		        /**boolean flg=word.getWordText().getPM().getPageManager().printDialog(printNum > 0 ?
		                printNum : 1);**/
		        
		        
		        //System.out.println("======flg========"+flg);
		     /*   if(flg){
		        	//保存EMR (保存，写文件)
		        	this.saveEMR(this.EMRName, this.classCode,this.subClassCode);
		        }*/
	        
	    }
	/**
	 * 全部打印 add by huangjw 20140825
	 */
	public void onPrintAll(){
		TTable table=this.getTTable(RTABLE);
		TParm tableParm=table.getParmValue();
		
		for(int i=0;i<=tableParm.getCount();i++){
				table.setSelectedRow(i);
				onTABLEClicked(i);
				boolean flg=word.getWordText().getPM().getPageManager().print();
		}
	}

    public String getCaseNo() {
        return caseNo;
    }

    public String getIpdNo() {
        return ipdNo;
    }

    public String getMrNo() {
        return mrNo;
    }

    public String getPatName() {
        return patName;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public TParm getParmData() {
        return parmData;
    }

    public Timestamp getAdmDate() {
        return admDate;
    }

    public String getAdmType() {
        return admType;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public String getRultType() {
        return rultType;
    }

    public String getStyleType() {
        return styleType;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setIpdNo(String ipdNo) {
        this.ipdNo = ipdNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public void setParmData(TParm parmData) {
        this.parmData = parmData;
    }

    public void setAdmDate(Timestamp admDate) {
        this.admDate = admDate;
    }

    public void setAdmType(String admType) {
        this.admType = admType;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public void setRultType(String rultType) {
        this.rultType = rultType;
    }

    public void setStyleType(String styleType) {
        this.styleType = styleType;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }
    
    public void setBedNo(String bedNo) {
        this.bedNo = bedNo;
    }

    public String getBedNo() {
        return this.bedNo;
    }
    /**
	 *
	 * @param word
	 *            TWord
	 */
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	} 
	
	/**
     * 多页签切换事件
     */
    public void onTabChange() {// wanglong add 20140418
        int index = ((TTabbedPane) getComponent("TABLEPANE")).getSelectedIndex();
        switch (index) {
            case 0:
                ((TButton) getComponent("PRINT_BUTTON")).setEnabled(true);
                ((TButton) getComponent("PRINT_ALL_BUTTON")).setEnabled(true);
                break;
            case 1:
                ((TButton) getComponent("PRINT_ALL_BUTTON")).setEnabled(false);
                ((TButton) getComponent("PRINT_BUTTON")).setEnabled(false);
                break;
        }
    }
}
