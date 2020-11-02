package com.javahis.ui.spc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.SPCGenDrugPutUpTool;
import jdo.spc.SPCUpdateElecTagPathTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title:UPDATE ���ӱ�ǩ
 * </p>
 * 
 * <p>
 * Description:UPDATE ���ӱ�ǩ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>                   
 * Company: bluecore
 * </p>  
 * 
 * @author liuzhen 2013.1.16
 * @version 1.0
 */
public class SPCUpdateElecTagControl extends TControl {
	
	TButton b1;
	TTextArea exe_result;
	TTextFormat dept;
	
	
	/**������   */   
	public SPCUpdateElecTagControl() {
		super();             	
	} 
	
    /**��ʼ������*/
    public void onInit() {
    	b1 = (TButton) this.getComponent("B1");
    	exe_result = (TTextArea) this.getComponent("EXE_RESULT"); 
    	dept = (TTextFormat) this.getComponent("DEPT");
    }
    
	/** b1��ť */
	public void newLable() {
		
		String resultMsg = "";//������Ϣ
		String detialMsg = "";//��ϸ����������Ϣ
		int falseCount =0;//������		
		int totalCount =0;//����
		
		String sysout="";
		try{												
			TParm parm = new TParm();
			
			String orgCode =(String)dept.getValue();
			
			if(null==orgCode || "".equals(orgCode.trim())){
				this.messageBox("��ѡ���沿�ţ�");
				return;
			}
			
			parm.setData("ORG_CODE",orgCode);
			
			TParm result = SPCUpdateElecTagPathTool.getInstance().query(parm);
			
			int count = result.getCount();
			totalCount = count; 
			
			for(int i = 0; i < count; i++){
										
//				String orgCode = result.getValue("ORG_CODE", i);
//				String orderCode = result.getValue("ORDER_CODE", i);
				String orderDesc = result.getValue("ORDER_DESC", i);
				String spec = result.getValue("SPECIFICATION", i);
				
				if(null!= spec && spec.length()>11) spec = spec.substring(0, 11);
				
				String eletagCode = result.getValue("ELETAG_CODE", i);
				String qty = result.getValue("QTY", i);
				sysout = sysout + "code:"+eletagCode+"--desc:"+orderDesc+"--spec:"+spec+"--qty"+qty+ ",\n";
				try{
					EleTagControl.getInstance().login();
					EleTagControl.getInstance().sendEleTag(eletagCode, orderDesc, spec, qty+"", 1);
				}catch(Exception e1){
					falseCount = falseCount + 1;
					detialMsg = detialMsg + "eletagCode:" +eletagCode+"--orderDesc:"+orderDesc + ",\n";
					e1.printStackTrace();
				}

			}
			
			
		}catch (Exception e){
			e.printStackTrace();			
			
			this.messageBox("ִ�г���!");
			exe_result.setText("ִ�г���!");
		}
		
		
		resultMsg = "�ܼ�¼:"+totalCount+" ������:"+ String.valueOf(falseCount)+"��\n"+detialMsg;
		this.messageBox("ִ�����!");
		exe_result.setText(resultMsg);
		
	}
	
	
	
	/**
	 * �µ��ӱ�ǩ�ӿ�
	 */
	public void b1Click() {
		
		String resultMsg = "";//������Ϣ
		String detialMsg = "";//��ϸ����������Ϣ
		int falseCount =0;//������		
		int totalCount =0;//����
		
		String sysout="";
		try{												
			TParm parm = new TParm();
			
			String orgCode =(String)dept.getValue();
			
			if(null==orgCode || "".equals(orgCode.trim())){
				this.messageBox("��ѡ���沿�ţ�");
				return;
			}
			parm.setData("ORG_CODE",orgCode);
			TParm result = SPCUpdateElecTagPathTool.getInstance().query(parm);
			int count = result.getCount();
			totalCount = count; 
			String apRegion = getApRegion(orgCode);
			
			for(int i = 1; i <= count; i++){
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>() ;
				Map<String, Object> map = new LinkedHashMap<String, Object> ();						
				String orderDesc = result.getValue("ORDER_DESC", i-1);
				String spec = result.getValue("SPECIFICATION", i-1);
				map.put("ProductName", orderDesc);
				String qty = result.getValue("QTY", i-1);
				 
				if(null!= spec && spec.length()>11)  {
					spec = spec.substring(0, 11);
				}
				map.put("SPECIFICATION", spec+" "+qty);
				
				String eletagCode = result.getValue("ELETAG_CODE", i-1);
				map.put("TagNo", eletagCode);
				map.put("Light", 5);
				map.put("APRegion", apRegion);
				list.add(map);
				//sysout = "code:"+eletagCode+"--desc:"+orderDesc+"--spec:"+spec+"--qty"+qty+ ",\n";
				String url = Constant.LABELDATA_URL ;
				boolean success = LabelControl.getInstance().sendLabelDate(list, url);
				/**
				System.out.println(i+"---:"+sysout);
				if(i%50==0||  ( i==count)){
					String url = Constant.LABELDATA_URL ;
					boolean success = LabelControl.getInstance().sendLabelDate(list, url);
//					if(!success){
//						this.messageBox("���õ��ӱ�ǩʧ��");
//					}
					list = new ArrayList<Map<String,Object>>();
				}*/
			}
			
			
		}catch (Exception e){
			e.printStackTrace();			
			
			this.messageBox("ִ�г���!");
			exe_result.setText("ִ�г���!");
		}
		
		
		resultMsg = "�ܼ�¼:"+totalCount+" ������:"+ String.valueOf(falseCount)+"��\n"+detialMsg;
		this.messageBox("ִ�����!");
		exe_result.setText(resultMsg);
		
	}
    
    
    
    
    /**��ѯ����*/
	public void onQuery() {
		
	} 
	
	/**
	 * �����޸Ĳ���
	 */
	public void onSave() {

	}
	
	/**ɾ������*/
	public void onDelete() {

	}
	
	/** table �����¼� */
	public void onTableClick() {

	}
	
	/** ����ҩƷ���ձ�*/
	public void onInsertPatByExl() {
		      
	}
	
	/**��ղ���*/
	public void onClear() {
		
	}
	
	
	  private String getApRegion( String orgCode) {
			TParm searchParm = new TParm () ;
			searchParm.setData("ORG_CODE",orgCode);
			TParm resulTParm = SPCGenDrugPutUpTool.getInstance().onQueryLabelByOrgCode(searchParm);
			String apRegion = "";
			if(resulTParm != null ){
				apRegion = resulTParm.getValue("AP_REGION");
			}
			return apRegion;
		}
	

	
	
}
