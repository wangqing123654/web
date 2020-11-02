package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.SPCDispenseOutAssginNormalTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:סԺҩ����ҩ-�Ǿ��� ��ҩcontrol
 * </p>
 * 
 * <p>
 * Description: סԺҩ����ҩ-�Ǿ��� ��ҩcontrol
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author liuzhen 2013 05 31
 * @version 1.0
 */
	public class SPCDispenseOutAssginNormalControl extends TControl {
		
		
		TTable table_N;
		TTable table_Y;
	
		/**��ʼʱ��*/
		TTextFormat start_date;
		
		/**����ʱ��*/
		TTextFormat end_date;
	
		/**ͳҩ��table*/
		TTable table_order;
		
		/**ͳҩ���ſؼ�*/
		TTextField order_id;
		
		/**ͳҩ���� ��ѯ�ؼ�*/
		TTextField order_id_query;	
		
		/**��ҩ�˶Կؼ�*/
		TTextField box_check;
		
		/**������տؼ�*/
		TTextField box_chlear;
		
		/**��������*/
		TTextField pat_count;	
		
		/**ͳҩ������*/
		String intStationCode = "";
		
		
		/**��ʼ������*/
		public void onInit() {
			super.init();
			table_N = this.getTable("TABLE_N");//����table
			table_Y = this.getTable("TABLE_Y");//ҩƷ��ϸtable
			
			start_date = (TTextFormat) getComponent("START_DATE");//��ʼʱ��
			end_date = (TTextFormat) getComponent("END_DATE");//����ʱ��
			table_order = this.getTable("TABLE_ORDER");//ͳҩ��table
			order_id = (TTextField) getComponent("INTGMED_NO");//ͳҩ����  ����
			order_id_query = (TTextField) getComponent("INTGMED_NO_QUERY");//ͳҩ����ѯ�ؼ�
			
			box_check = (TTextField) getComponent("BOX_CHECK");//��ҩ�˶�
			box_chlear = (TTextField) getComponent("BOX_CLEAR");//�������
			pat_count = (TTextField) getComponent("PAT_COUNT");	//��������

			// ��ʼ����ѯ����
			Timestamp date = SystemTool.getInstance().getDate();        
	        this.setValue("START_DATE",date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
	        this.setValue("END_DATE",date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
	        onQuery();
	        		
		}

		/**��ѯͳҩ��*/
		public void onQuery() {
			TParm parm = new TParm();
			
			parm.setData("START_DATE", start_date.getValue());
			parm.setData("END_DATE", end_date.getValue());
			parm.setData("STATION_ID",this.getValueString("STATION_ID"));
			parm.setData("INTGMED_NO",order_id_query.getValue());
			
			TParm result = SPCDispenseOutAssginNormalTool
											.getInstance().query(parm);
			table_order.setParmValue(result);		
		}
	
		/**table_order ����ͳҩ�������¼�������ͳҩ����ѯ��������*/
		public void tableOrderClicked(){
        
	       int row_m = table_order.getSelectedRow();
	        if (row_m != -1) {
	        	
	        	TParm result = table_order.getParmValue();
				TParm parm = result.getRow(row_m);
								
	        	order_id.setValue(parm.getValue("INTGMED_NO"));
	        	intStationCode = parm.getValue("STATION_CODE");
	        	
	    		onIntgmEdNo();  //order_table�����¼�,Ϊ�����������ֵ 		
	        }
	        
	        int patient_count = table_N.getParmValue().getCount();
	        pat_count.setValue(String.valueOf(patient_count));
		}
		
		
		/**����ͳҩ���Ų�ѯ�ò���������ϸ */
		public void onIntgmEdNo() {
			String intgmedNo = (String) getValue("INTGMED_NO");//���ص�ͳҩ���ſؼ�����order_tebleѡ�е�ͳҩ��
			
			if (!StringUtil.isNullString(intgmedNo)){
				TParm parm = new TParm();
								
				parm = SPCDispenseOutAssginNormalTool
								.getInstance().queryPatientByIntgmedNo(intgmedNo,intStationCode);	//����ͳҩ���Ų�ѯ�ò�������

				//SELECT_FLG;MR_NO;BED_NO_DESC;PAT_NAME;BOX_ESL_ID;AP_REGION;CASE_NO;
				//INTGMED_NO;STATION_DESC;BED_NO;STATION_CODE
				
				//SELECT_FLG;MR_NO;BED_NO_DESC;PAT_NAME;BOX_ESL_ID;AP_REGION;CASE_NO;INTGMED_NO;STATION_DESC;BED_NO;STATION_CODE
				//SELECT_FLG;MR_NO;BED_NO_DESC;PAT_NAME;BOX_ESL_ID;AP_REGION;INTGMED_NO;CASE_NO;STATION_DESC;BED_NO;STATION_CODE
				
				if (null != parm && parm.getCount() > 0) {
					table_N.setParmValue(parm);
					
					String stationDesc = (String) parm.getData("STATION_DESC", 0);	//����
					setValue("STATION_DESC", stationDesc);	//���Բ������Ѿ����أ�
										
					this.getTextField("STATION_DESC").setEditable(false);
					this.getTextField("INTGMED_NO").setEditable(false);
					
					this.getTextField("BOX_ESL_ID").grabFocus();//�������ڰ󶨵��ӱ�ǩ�ؼ�
					table_N.setSelectedRow(0);//ѡ��table��һ��
					
				}
				
			}
		}
	
	/**ˢҩ�У��󶨲�������ӱ�ǩ������ҩ���벡�˵İ󶨣�û�󶨵�ҩ���ÿգ�*/
	public void onEleTagCode() {
		
		// �ж��Ƿ�ѡ��һ����¼		
		table_N.acceptText();
		TParm parm = table_N.getParmValue();	//����table������
		int currentRow = table_N.getSelectedRow(); 	//����table�У���ǰ�󶨵���
		
		if(currentRow < 0){
			this.messageBox("����ѡ�����ҩ�еĲ���");
			return;
		}
		
		//��ҩ�е��ӱ�ǩ
		String newEleTag = getValueString("BOX_ESL_ID");	//��ҩ�е��ӱ�ǩ
		
		TTextField BOX_ESL_ID = (TTextField) getComponent("BOX_ESL_ID");	//ҩ�е��ӱ�ǩ�ؼ�
		BOX_ESL_ID.select(0, newEleTag.length());
		
		if (StringUtil.isNullString(newEleTag)) return;		//���ҩ�е��ӱ�ǩΪ�գ�����	
		
					
		//��ǰѡ���е���Ϣ
		TParm rowParm = parm.getRow(currentRow);//����tableѡ���е�parma
		
		//SELECT_FLG;MR_NO;BED_NO_DESC;PAT_NAME;BOX_ESL_ID;AP_REGION;CASE_NO;
		//INTGMED_NO;STATION_DESC;BED_NO;STATION_CODE
		
		String mrNo = rowParm.getValue("MR_NO");//������
		String bedNoDesc = rowParm.getValue("BED_NO_DESC");//��λ
		String patName = rowParm.getValue("PAT_NAME");//��������
		
		String oldEleTag =  rowParm.getValue("BOX_ESL_ID");//ԭ���ӱ�ǩ      ����ʱΪ��
		String oldApRegion = rowParm.getValue("AP_REGION");//ѡ���еĵ��ӱ�ǩ����ԭ��ǩ ��������  ����ʱΪ��
		
		String caseNo = rowParm.getValue("CASE_NO");//����� ��ǰ��ѡ��¼ �����
		String orderNo = rowParm.getValue("INTGMED_NO");//ͳҩ����		
		
		String stationDesc = rowParm.getValue("STATION_DESC");//����
		String stationCode = rowParm.getValue("STATION_CODE");//������
		
		//�����ֵ���ֵ���  ��ֱ�ӷ���
//		if(null != oldEleTag){
//			if(oldEleTag.equals(newEleTag)){
//				return ;
//			}
//		}else{	//oldValue is null
//			if(null == newEleTag || "".equals(newEleTag.trim())){
//				return ;
//			}
//		}
		
		//����µ��ӱ�ǩ��ϵͳ�в�����  ϵͳ�Զ����  ����
		TParm result = SPCDispenseOutAssginNormalTool
										.getInstance().queryEleTagByTagCode(newEleTag);
		
		if(result.getErrCode() < 0){
			this.messageBox("���ݵ��ӱ�ǩ�Ų�ѯ���ӱ�ǩ����");
			return ;
    	}
		
		String newApReagion = result.getValue("AP_REGION",0) ;
		
		if(result.getCount() != 1){
//			this.messageBox("�õ��ӱ�ǩ��ϵͳ�в����ڣ�����ά����");
//			return ;
			//����ñ�ǩ��ϵͳ�в����ڣ�����룬����ǰ�Ȳ��AP_REGION
			
			//��ѯAP_REGION
			TParm res = SPCDispenseOutAssginNormalTool
								.getInstance().queryApRegion();
			
			if(res.getErrCode() < 0){
				this.messageBox("��ѯסԺҩ������������");
				return ;
	    	}
			
			String inPatApRegion = res.getValue("AP_REGION",0);
			
			if(null == inPatApRegion || "".equals(inPatApRegion.trim()) ){
				this.messageBox("û�в�ѯ��סԺҩ��������룡");
				return ;
			}
			
			String optUser = Operator.getID();
			String optTerm = Operator.getIP();
			
			res = SPCDispenseOutAssginNormalTool
							.getInstance().insertBoxLable(newEleTag,inPatApRegion,optUser, optTerm);
			
			if(res.getErrCode() < 0){
				this.messageBox("IND_MEDBOX�������ӱ�ǩ����");
				return ;
	    	}
			newApReagion = inPatApRegion;
			
    	}
		
//		String newApReagion = result.getValue("AP_REGION",0) ;
		
		
		//�����ѡ��¼�Ѿ���ҩ�У��Ƚ�ԭҩ���ÿ�  �����ýӿ�ˢ�µ��ӱ�ǩ
		if(null != oldEleTag && !"".equals(oldEleTag.trim())){
			
			//IND_INPATBOX��ոı�ǩ��Ӧ��case_no  staiton_code
			result = SPCDispenseOutAssginNormalTool
								.getInstance().clearInPatBoxCaseNoByEletagCode(oldEleTag);
			
			if(result.getErrCode() < 0){
				this.messageBox("�ÿ�IND_INPATBOXԭ���ӱ�ǩCASE_NO��STATION_CODE����");
				return;
	    	}
			
			result = SPCDispenseOutAssginNormalTool
								.getInstance().clearCaseNoByEletagCode(oldEleTag);
			
			if(result.getErrCode() < 0){
				this.messageBox("�ÿ�ԭ���ӱ�ǩCASE_NO����");
				return;
			}
			
			/**���ýӿ� �ÿ�ԭ���ӱ�ǩ��Ϣ*/
//			freshLable("","",
//						oldEleTag,3,oldApRegion);//����
			
//			freshLable("","","",oldEleTag,3);
			
		}

		
		//�ж�IND_INPATBOX�����Ƿ���ڸñ�ǩ �����������£������������insert
		result = SPCDispenseOutAssginNormalTool
						.getInstance().queryBoxByEletag(newEleTag);
		
		if(result.getErrCode() < 0){
			this.messageBox("����ELETAG_CODE��ѯIND_INPATBOX����");
			return;
    	}
		
		if(result.getCount() > 0){
			
			result = SPCDispenseOutAssginNormalTool
								.getInstance().updateIndPatBoxCaseNoByEletagCode(newEleTag,caseNo,stationCode);

			if(result.getErrCode() < 0){
				this.messageBox("����IND_INPATBOX���ӱ�ǩCASE_NO��STATION_CODE����");
				return;
			}
			
		}else{
			
			String optUser = Operator.getID();
			String optTerm = Operator.getIP();
			
			result = SPCDispenseOutAssginNormalTool
								.getInstance().insertInPatBoxBox(newEleTag,caseNo,stationCode,optUser,optTerm);
			
			if(result.getErrCode() < 0){
				this.messageBox("����IND_INPATBOX��¼����");
				return;
			}
		}
		
		
		result = SPCDispenseOutAssginNormalTool
						.getInstance().updateCaseNoByEletagCode(newEleTag,caseNo);
		
		if(result.getErrCode() < 0){
			this.messageBox("���µ��ӱ�ǩCASE_NO����");
			return;
		}
		
		
		/**���ýӿ� ���µ��ӱ�ǩ��ʾ��Ϣ*/	
		freshLable(patName,stationDesc+" "+bedNoDesc,
					newEleTag,3,newApReagion);//����
		
//		freshLable(patName,stationDesc,bedNoDesc,newEleTag,3);
		
		
				
		table_N.acceptText();
		table_N.setItem(currentRow, "BOX_ESL_ID", newEleTag);	//�򽫵�ǰ���ӱ�ǩ��ò����󶨣�ֻ���������У�
		table_N.setItem(currentRow, "AP_REGION", newApReagion);	//
		
		tableOrderClicked();//ˢ�²�����Ϣtable
//		table_N.setSelectedRow(currentRow );//ѡ��ԭ������
		BOX_ESL_ID.select(0, newEleTag.length());//����ҩ�пؼ���ý���
		
		onTableMClicked();	//��ʾ��һ��������ҩƷ��ϸ
		this.setValue("BOX_ESL_ID", "");
		
		if(table_N.getParmValue().getCount() - 1 > currentRow){
			table_N.setSelectedRow(currentRow + 1);
		}else{
			this.messageBox("�Ĳ��������Ѿ�����ϣ�");
			this.getTextField("BOX_CHECK").grabFocus();//�������ڰ󶨵��ӱ�ǩ�ؼ�
		}
		
		return;
	}

	
	
	/**�������ҩ��*/
	public void onBoxClear(){
		String eletagCode = getValueString("BOX_CLEAR");//ҩ�е��ӱ�ǩ		
		box_chlear.select(0, eletagCode.length());

		if (StringUtil.isNullString(eletagCode)) return;	//���ҩ�е��ӱ�ǩΪ�գ�����
		
		
		//��ѯAP_REGION
		TParm res = SPCDispenseOutAssginNormalTool
							.getInstance().queryApRegion();
		
		if(res.getErrCode() < 0){
			this.messageBox("��ѯסԺҩ������������");
			return ;
    	}
		
		String inPatApRegion = res.getValue("AP_REGION",0);
		
		if(null == inPatApRegion || "".equals(inPatApRegion.trim()) ){
			this.messageBox("û�в�ѯ��סԺҩ��������룡");
			return ;
		}

		
		//IND_INPATBOX��ոı�ǩ��Ӧ��case_no  staiton_code
		TParm result = SPCDispenseOutAssginNormalTool
							.getInstance().clearInPatBoxCaseNoByEletagCode(eletagCode);
		
		if(result.getErrCode() < 0){
			this.messageBox("�ÿ�IND_INPATBOXԭ���ӱ�ǩCASE_NO��STATION_CODE����");
			return;
    	}
		
		result = SPCDispenseOutAssginNormalTool
							.getInstance().clearCaseNoByEletagCode(eletagCode);
		
		if(result.getErrCode() < 0){
			this.messageBox("�ÿ�ԭ���ӱ�ǩCASE_NO����");
			return;
		}
		
				
		/**���ýӿ� �ÿ�ԭ���ӱ�ǩ��Ϣ*/
		freshLable("","",
				eletagCode,3,inPatApRegion);//����
		
//		freshLable("","","",eletagCode,3);
		
		box_chlear.setValue("");
	}
	
	
	
	/**��ҩ�˶�*/
	public void onBoxCheck(){
		
		String eletagCode = getValueString("BOX_CHECK");//ҩ�е��ӱ�ǩ		
		box_check.select(0, eletagCode.length());

		if (StringUtil.isNullString(eletagCode)) return;	//���ҩ�е��ӱ�ǩΪ�գ�����		
		
		// ����Ӧҩ��  "�˶�"  ���ѡ��
		table_N.acceptText();
		TParm parm = table_N.getParmValue();//����table������
		
		for(int i=0; i<parm.getCount(); i++){
			String boxId = parm.getValue("BOX_ESL_ID",i);
			
			if(eletagCode.equals(boxId)){
				table_N.setItem(i, "SELECT_FLG", true);
				table_N.setParmValue(parm);
				table_N.setSelectedRow(i);

				onTableMClicked();
				break;
			}
		}
	
		this.setValue("BOX_CHECK", "");
		this.getTextField("BOX_CHECK").grabFocus();//��������
		return;
	}
	
	/** ������Ϣ������(TABLE_N)�����¼�*/
	public void onTableMClicked() {
		TTable table = getTable("table_N");

		int row = table.getSelectedRow();
		if (row != -1) {
			TParm result = table.getParmValue();
			TParm parm = result.getRow(row);
			String intgmedNo = parm.getValue("INTGMED_NO");
			String caseNO = parm.getValue("CASE_NO");

			TParm detailParm = new TParm(
					TJDODBTool.getInstance().select(
							SPCDispenseOutAssginNormalTool
										.getInstance().getOrderCodeInfoDetailByPation(intgmedNo, caseNO)));
			table_Y.setParmValue(detailParm);
		}
		this.getTextField("BOX_ESL_ID").grabFocus();//�������ڰ󶨵��ӱ�ǩ�ؼ�
	}
	
	
	/**�������ҩ�б�ǩ*/
	public void onBind(){
		
		if (this.messageBox("��ʾ", "ȷ��Ҫ��ղ�������ҩ�е��ӱ�ǩ?", 2) == 0) {
            
        }else {
           return;
        }
		
		TParm result = SPCDispenseOutAssginNormalTool
								.getInstance().queryAllBox();
		for(int i=0; i < result.getCount(); i++){
			String eleTagCode = result.getValue("ELETAG_CODE",i);
			String apRegion = result.getValue("AP_REGION",i);
			String caseNo = result.getValue("CASE_NO",i);
			
			//��ձ�ǩcase_no
			TParm res = SPCDispenseOutAssginNormalTool
								.getInstance().clearCaseNoByEletagCode(eleTagCode);
			
			if(res.getErrCode() < 0){
				this.messageBox("�ÿյ��ӱ�ǩ����ų���  ��ǩ�ţ�" + eleTagCode);
				continue;
	    	}
			
			//���ýӿ��ÿձ�ǩ��ʾ����
			freshLable("","",
						eleTagCode,3,apRegion);//����
//			freshLable("","","",eleTagCode,3);
		}
		
		tableOrderClicked();
	}
	
	/**��ղ���*/
	public void onClear() {
		String controlName = "INTGMED_NO;STATION_DESC;BOX_ESL_ID";
		this.clearValue(controlName);

		this.getTextField("INTGMED_NO").setEditable(true);
		this.getTextField("BOX_ESL_ID").setEditable(true);
		this.getTextField("INTGMED_NO").grabFocus();
		table_N.removeRowAll();
		table_Y.removeRowAll();
		pat_count.setValue("");
	}
	
	/**���ýӿ� ��һ���� */	
	private void freshLable(String name,String stationDesc,String bedNoDesc,
							String eleTag,int lightNum){
		System.out.println("1��-----name:"+name+"-----stationDesc:"+stationDesc+"-----eleTag:"+eleTag+"-----lightNum:"+lightNum+"-----bedNoDesc:"+bedNoDesc);
        try{
        	EleTagControl.getInstance().login();
        }catch(Exception e){
        	this.messageBox("���ӱ�ǩ��������¼ʧ��,���ӱ�ǩ�޷�����!");
        }
		
		try{
			EleTagControl.getInstance().sendEleTag(eleTag, name, stationDesc,bedNoDesc, lightNum);
							
		}catch(Exception e){	
			this.messageBox("���µ��ӱ�ǩ��ʾ��Ϣʱ������ǩ�ţ�" + eleTag);
			System.out.println("--�󶨵��ӱ�ǩ����---EleTagControl.getInstance().sendEleTag----");
		}
		
	}

	/**���ýӿ� �������� */	
	private void freshLable(String name,String spc,
							String eleTag,int lightNum,
							String apRegion){

		System.out.println("2��-----name:"+name+"-----spc:"+spc+"-----eleTag:"+eleTag+"-----lightNum:"+lightNum+"-----apRegion:"+apRegion);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("ProductName", name);
		map.put("SPECIFICATION",  spc);
		map.put("TagNo", eleTag);
		map.put("Light", lightNum);
		map.put("APRegion", apRegion);
		
		list.add(map);
		
		try{
			String url = Constant.LABELDATA_URL ;
			LabelControl.getInstance().sendLabelDate(list, url);
		}catch (Exception e) {
	    	System.out.println("���õ��ӱ�ǩ����ʧ��");
	    	this.messageBox("���õ��ӱ�ǩ����ʧ��");
		}
		
	}
	
	/**���table*/
	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}
	
	/**���������*/
	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
	
}
