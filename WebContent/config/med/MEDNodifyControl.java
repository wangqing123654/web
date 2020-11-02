package com.javahis.ui.med;



import jdo.med.MedNodifyTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;


public class MEDNodifyControl extends TControl{
	//主、细TABLE
	private TTable mainTable;
	private TParm   parameterParm =null;
	private String sendType;
	
	/**
	 * 初始化
	 */

	public void onInit(){
		super.onInit() ;
		this.initComponent() ;
		onQuery();    
	}
	/**
	 * 查询
	 */
	public void onQuery(){

		TParm result = MedNodifyTool.getInstance().selectCheckResult(this.getSelectParm()) ;		
		mainTable.setParmValue(result) ;
		setValue("REMARK", "") ;
	}
	/**
	 * 初始化查询条件
	 * 
	 */
	public TParm  getSelectParm(){
		TParm  parm = new TParm() ;
         String startDate = this.getValueString("S_DATE").substring(0, 10).replace("-", "")+
         this.getValueString("S_DATE").substring(11, 19).replace(":", "") ;
         String endDate = this.getValueString("S_DATE").substring(0, 10).replace("-", "")+
         this.getValueString("E_DATE").substring(11, 19).replace(":", "") ;;
		parm.setData("SEND_STAT", sendType) ;//未查看为1，查看为2  
		parm.setData("START_DATE", startDate) ;
		parm.setData("END_DATE", endDate) ;
		if( parameterParm !=null){
		if(parameterParm.getValue("TYPE").equals("1"))	
			parm.setData("BILLING_DOCTORS", parameterParm.getValue("ID")) ;
		else{
			if(parameterParm.getValue("ADM_TYPE").equals("1"))
				parm.setData("STATION_CODE", parameterParm.getValue("STATION_CODE")) ;
		else parm.setData("CLINICAREA_CODE", parameterParm.getValue("STATION_CODE")) ;
		} 
		}
		return parm ;
	}
	/**
	 * 主表单机事件
	 */
	public void onTableClick(){
		int row = mainTable.getSelectedRow() ;
		if(row<0)
			return ;
		TParm parm = new TParm() ;
		TParm resultData = mainTable.getParmValue() ;
		parm.setData("CASE_NO",resultData.getValue("CASE_NO", row)) ;
		parm.setData("ORDER_CODE", resultData.getValue("ORDER_CODE", row)) ;
		mainTable.setItem(row, "FLG", "Y") ;
		//==================显示细项
		this.showTableDetail(resultData.getRow(row));
		TParm   result = TIOM_AppServer.executeAction(
	                "action.med.MedNodifyAction", "onUpdateStat", parm); 
		if(result.getErrCode()<0){
			mainTable.setItem(row, "FLG", "N") ;
			return ;
		}
		
	}
	/**
	 * 
	 * 根据主项
	 * 显示细项
	 */
	public void showTableDetail(TParm parm){
		TParm result = new TParm() ;
		TParm resultData = null ;
		result.setData("APPLICATION_NO", parm.getValue("APPLICATION_NO")) ;//单号
		result.setData("CAT1_TYPE", parm.getValue("CAT1_TYPE")) ;//类型
		String  type = parm.getValue("CAT1_TYPE") ;
		if("LIS".equals(type)){
			result.setData("ORDER_NO", parm.getValue("ORDER_NO")) ;
			result.setData("SEQ_NO", parm.getValue("ORDER_SEQ")) ;
			resultData=	MedNodifyTool.getInstance().selectResultLIS(result);
       	   setValue("REMARK", formatParm(onDealData(resultData, "LIS"))) ;
			return ;
		}
		else if("RIS".equals(type)){
			resultData =MedNodifyTool.getInstance().selectResultRIS(result) ;
       	   setValue("REMARK", formatParm(onDealData(resultData, "RIS"))) ;
			return  ;	
		}
		else {
			setValue("REMARK", formatParm(parm)) ;
		}
		   
		
	}
	/**
	 * 初始化控件
	 */
    private void initComponent() {
        mainTable = (TTable) this.getComponent("MAIN_TABLE");
        String now = SystemTool.getInstance().getDate().toString().substring(0, 10).replace('-', '/') ;
		this.setValue("S_DATE", now+" 00:00:00") ; //开始时间
		this.setValue("E_DATE", now+" 23:59:59") ; //结束时间
		sendType = "1" ;  //初始化默认未查看，状态为1
        Object obj = this.getParameter();
        if (obj != null) {
        	parameterParm = (TParm) obj;
        }
    }
      /**
        * CheckBox 点选事件
       */
    public void onCheckBox(){
    	if("Y".equals(getCheckBox("CHECKBOX").getValue())){
    		callFunction("UI|S_DATE|setEnabled", true);
    		callFunction("UI|E_DATE|setEnabled", true);
    		sendType = "2" ;
    	}
    	else if("N".equals(getCheckBox("CHECKBOX").getValue())){
    		callFunction("UI|S_DATE|setEnabled", false);
    		callFunction("UI|E_DATE|setEnabled", false);
    		sendType = "1" ;
    	}
    	onQuery() ;
    }
    /**
     * 头：需要传回的值 空格分开最后用“；”
     * 内容：和头的格式一样，一条结束以“；”分开
     * 已皮试为例
     * 结果 批次 备注
     * + 213 你好
     * 
     * 检验：项目名称,150;单位,80;标准上限,100;标准下限,100;高危上限,100;高危下线,100;备注,150
     * TESTITEM_CHN_DESC;TEST_UNIT;UPPE_LIMIT;LOWER_LIMIT;CRTCLUPLMT;CRTCLLWLMT;REMARK
     * 检查：阴阳率,60;结果印象,200;结果结论,400
     * OUTCOME_TYPE;OUTCOME_DESCRIBE;OUTCOME_CONCLUSION
     */
    public String formatParm(TParm parm){
    	String result ="" ;
    	String[] remark = parm.getValue("REMARK").split(";") ;
    	for(String str:remark){
    		result+=str+"\n" ;
    	}
    	return result ;
    }
    /**
     * 将取出的数据处理
     * 由于接口那边的数据不好处理，这里只对lis、ris处理，其他的消息不管
     */
    public TParm   onDealData(TParm parm,String type){
    	TParm result = new TParm() ;
    	String remark ="" ;
    	if("LIS".equals(type)){
    		remark	="项目名称          单位名称          标准上限          标准下限          高危上限          高危下线;" ;
    		String[] arrayLis = {"TESTITEM_CHN_DESC","TEST_UNIT","UPPE_LIMIT","LOWER_LIMIT","CRTCLUPLMT","CRTCLLWLMT"} ;
    		for(int i=0;i<parm.getCount();i++){
    	     for(int j=0;j<arrayLis.length;j++){
    	    	 remark+=String.format("%-"+(18-BinaryString(parm.getValue(arrayLis[j],i)))+"s", parm.getValue(arrayLis[j],i));
    	     }
    	     remark+=";" ;
    		}
    	}
    	else if("RIS".equals(type)){
    		remark ="阴阳率       结果印象                              结果结论;" ;
    		String[] arrayRis  = {"OUTCOME_TYPE","OUTCOME_DESCRIBE","OUTCOME_CONCLUSION"} ;
    		int[] size = {12,38,38} ;
    		for(int i=0;i<parm.getCount();i++){
    			for(int j=0;j<arrayRis.length;j++){
    		remark+=String.format("%-"+size[j]+"s", parm.getValue(arrayRis[j],i));
       	     }
    			remark+=";" ;
        		}
    	}
       result.setData("REMARK", remark) ;
    	return result ;
    }
    /**
	 *计算汉字个数
	 * @param args
	 */
	public static int BinaryString(String str) {
		String len="";
		int j=0;
		char[] c=str.toCharArray();
		for(int i=0;i<c.length;i++){
			len=Integer.toBinaryString(c[i]);
			if(len.length()>8)
				j++;
		}
		return j ;
	}

	/**
	 * 得到CheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

}
