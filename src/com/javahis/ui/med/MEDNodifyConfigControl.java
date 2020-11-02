package com.javahis.ui.med;
import jdo.med.MedNodifyTool;
import jdo.opd.Order;
import jdo.opd.OrderList;
import jdo.opd.PrescriptionList;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;


/**
 * <p>Title:������֪ͨ,������
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 2014.03.24
 * @version 4.0
 */
public class MEDNodifyConfigControl extends TControl{
	

	private  String  action ;

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit() ;
		this.onInitPage() ;
	}
	
	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage(){
		setValue("REGION_CODE", Operator.getRegion()) ;
		setValue("IP", Operator.getIP()) ;
		setValue("USER", Operator.getID()) ;
		setValue("STATION_CODE", Operator.getStation()) ;
         onQuery();
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		TParm parm = new TParm() ;
		parm.setData("IP", Operator.getIP()) ;
		TParm selectParm =MedNodifyTool.getInstance().queryIp(parm);
		if(selectParm.getCount()<0){
			action = "insert" ;
			return ;
		}
		selectParm = selectParm.getRow(0) ;
		action = "update" ;
	   if(selectParm.getValue("OPEN_FLG").equals("Y")){
		   getCheckBox("OPEN_FLG").setSelected(true) ;  
	   }
	   else  getCheckBox("OPEN_FLG").setSelected(false) ;  
	   setValue("OPT_TERM", selectParm.getValue("OPT_TERM")) ;
	   if(selectParm.getValue("SKT_TYPE").equals("1")){
		   getRadioButton("SKT_TYPE_1").setSelected(true) ;  
	   }
	   else   getRadioButton("SKT_TYPE_0").setSelected(true) ;  
	   if(selectParm.getValue("ADM_TYPE").equals("1")){
		   getRadioButton("ADM_TYPE_1").setSelected(true) ;  
	   }
	   else   getRadioButton("ADM_TYPE_0").setSelected(true) ;  
	}
	
	/**
	 * ����
	 */
	public void onSave(){
	if(action.equals("insert")){
		this.onInsert() ;
	}
	else this.onUpdate() ;
	this.onQuery() ;
	
	}
	
	/**
	 * ����
	 */
	
	public void onInsert(){
		TParm saveParm= new TParm() ;
		if (getRadioButton("SKT_TYPE_0").isSelected())
			saveParm.setData("SKT_TYPE", "0");
		else
			saveParm.setData("SKT_TYPE", "1");
		if (getRadioButton("ADM_TYPE_0").isSelected()){//�ż���
			saveParm.setData("ADM_TYPE", "0");
		}
			
		else{
			saveParm.setData("ADM_TYPE", "1");//סԺ
		}
		saveParm.setData("STATION_CODE", this.getValueString("STATION_CODE"));	
		saveParm.setData("OPEN_FLG", getCheckBox("OPEN_FLG").getValue());
		saveParm.setData("SKT_USER", this.getValueString("USER")) ;
		saveParm.setData("OPT_TERM", this.getValueString("IP")) ;
		saveParm.setData("REGION_CODE", Operator.getRegion()) ;
	
		TParm result = TIOM_AppServer.executeAction("action.med.MedNodifyAction", "onInsert", saveParm);
		if(result.getErrCode()<0){
			messageBox("ִ��ʧ��") ;
			return ;
		}
		    messageBox("ִ�гɹ�") ;
		
	}
	/**
	 * ����
	 */
	public void onUpdate(){
		TParm updateParm= new TParm() ;
		if (getRadioButton("SKT_TYPE_0").isSelected())
			updateParm.setData("SKT_TYPE", "0");
		else
			updateParm.setData("SKT_TYPE", "1");
		if (getRadioButton("ADM_TYPE_0").isSelected())
			updateParm.setData("ADM_TYPE", "0");
		else
			updateParm.setData("ADM_TYPE", "1");
		updateParm.setData("OPEN_FLG", getCheckBox("OPEN_FLG").getValue());
		updateParm.setData("SKT_USER", this.getValueString("USER")) ;
		updateParm.setData("OPT_TERM", this.getValueString("IP")) ;
		updateParm.setData("STATION_CODE", this.getValueString("STATION_CODE")) ;
		updateParm.setData("REGION_CODE", Operator.getRegion()) ;
		TParm result = TIOM_AppServer.executeAction("action.med.MedNodifyAction", "onUpdateData", updateParm);
		if(result.getErrCode()<0){
			messageBox("ִ��ʧ��") ;
			return ;
		}
		    messageBox("ִ�гɹ�") ;
	}
	/**
	 * ���
	 */
	public void onClear(){
		clearValue("IP") ;
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

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}
}
