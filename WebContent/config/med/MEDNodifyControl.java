package com.javahis.ui.med;



import jdo.med.MedNodifyTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;


public class MEDNodifyControl extends TControl{
	//����ϸTABLE
	private TTable mainTable;
	private TParm   parameterParm =null;
	private String sendType;
	
	/**
	 * ��ʼ��
	 */

	public void onInit(){
		super.onInit() ;
		this.initComponent() ;
		onQuery();    
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){

		TParm result = MedNodifyTool.getInstance().selectCheckResult(this.getSelectParm()) ;		
		mainTable.setParmValue(result) ;
		setValue("REMARK", "") ;
	}
	/**
	 * ��ʼ����ѯ����
	 * 
	 */
	public TParm  getSelectParm(){
		TParm  parm = new TParm() ;
         String startDate = this.getValueString("S_DATE").substring(0, 10).replace("-", "")+
         this.getValueString("S_DATE").substring(11, 19).replace(":", "") ;
         String endDate = this.getValueString("S_DATE").substring(0, 10).replace("-", "")+
         this.getValueString("E_DATE").substring(11, 19).replace(":", "") ;;
		parm.setData("SEND_STAT", sendType) ;//δ�鿴Ϊ1���鿴Ϊ2  
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
	 * �������¼�
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
		//==================��ʾϸ��
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
	 * ��������
	 * ��ʾϸ��
	 */
	public void showTableDetail(TParm parm){
		TParm result = new TParm() ;
		TParm resultData = null ;
		result.setData("APPLICATION_NO", parm.getValue("APPLICATION_NO")) ;//����
		result.setData("CAT1_TYPE", parm.getValue("CAT1_TYPE")) ;//����
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
	 * ��ʼ���ؼ�
	 */
    private void initComponent() {
        mainTable = (TTable) this.getComponent("MAIN_TABLE");
        String now = SystemTool.getInstance().getDate().toString().substring(0, 10).replace('-', '/') ;
		this.setValue("S_DATE", now+" 00:00:00") ; //��ʼʱ��
		this.setValue("E_DATE", now+" 23:59:59") ; //����ʱ��
		sendType = "1" ;  //��ʼ��Ĭ��δ�鿴��״̬Ϊ1
        Object obj = this.getParameter();
        if (obj != null) {
        	parameterParm = (TParm) obj;
        }
    }
      /**
        * CheckBox ��ѡ�¼�
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
     * ͷ����Ҫ���ص�ֵ �ո�ֿ�����á�����
     * ���ݣ���ͷ�ĸ�ʽһ����һ�������ԡ������ֿ�
     * ��Ƥ��Ϊ��
     * ��� ���� ��ע
     * + 213 ���
     * 
     * ���飺��Ŀ����,150;��λ,80;��׼����,100;��׼����,100;��Σ����,100;��Σ����,100;��ע,150
     * TESTITEM_CHN_DESC;TEST_UNIT;UPPE_LIMIT;LOWER_LIMIT;CRTCLUPLMT;CRTCLLWLMT;REMARK
     * ��飺������,60;���ӡ��,200;�������,400
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
     * ��ȡ�������ݴ���
     * ���ڽӿ��Ǳߵ����ݲ��ô�������ֻ��lis��ris������������Ϣ����
     */
    public TParm   onDealData(TParm parm,String type){
    	TParm result = new TParm() ;
    	String remark ="" ;
    	if("LIS".equals(type)){
    		remark	="��Ŀ����          ��λ����          ��׼����          ��׼����          ��Σ����          ��Σ����;" ;
    		String[] arrayLis = {"TESTITEM_CHN_DESC","TEST_UNIT","UPPE_LIMIT","LOWER_LIMIT","CRTCLUPLMT","CRTCLLWLMT"} ;
    		for(int i=0;i<parm.getCount();i++){
    	     for(int j=0;j<arrayLis.length;j++){
    	    	 remark+=String.format("%-"+(18-BinaryString(parm.getValue(arrayLis[j],i)))+"s", parm.getValue(arrayLis[j],i));
    	     }
    	     remark+=";" ;
    		}
    	}
    	else if("RIS".equals(type)){
    		remark ="������       ���ӡ��                              �������;" ;
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
	 *���㺺�ָ���
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
	 * �õ�CheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

}
