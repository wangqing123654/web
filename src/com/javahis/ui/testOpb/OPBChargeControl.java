package com.javahis.ui.testOpb;


import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSOperatorTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * ����Ʒѿ�����
 * 
 * @author zhangp
 *
 */
public class OPBChargeControl extends TControl {
	
	public OPBChargePat opbChargePat;
	public OPBChargeReg opbChargeReg;
	public OPBChargeEkt opbChargeEkt;
	public OPBChargeOpdOrder opbChargeOpdOrder;
	public OPBChargeTmplt opbChargeTmplt;
	public OPBChargeSelFee opbChargeSelFee;
	
	public Pay pay;
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		try {
			opbChargePat = new OPBChargePat(this);
			opbChargeReg = new OPBChargeReg(this);
			opbChargeEkt = new OPBChargeEkt(this);
			opbChargeOpdOrder = new OPBChargeOpdOrder(this);
			opbChargeTmplt = new OPBChargeTmplt(this);
			opbChargeSelFee = new OPBChargeSelFee(this);
			onInitEvent();
			pay = new PayCash();
			onInitReceive();
			onInitPopemed();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��ʼ��Ȩ��   add by huangtt 20150513
	 */
	public void onInitPopemed(){
		TParm parm = SYSOperatorTool.getUserPopedem(Operator.getID(), getUITag());
		for (int i = 0; i < parm.getCount(); i++) {
			this.setPopedem(parm.getValue("AUTH_CODE", i), true);
		}
	}

	
	
	/**
	 * ע��ؼ����¼�
	 */
	public void onInitEvent() throws Exception {
		opbChargeOpdOrder.onInitEvent();
	}
	
	/**
	 * ���մ��β���ʼ������
	 */
	private void onInitReceive(){
		//�õ�ǰ̨���������ݲ���ʾ�ڽ�����
		try{
			TParm recptype = (TParm) getParameter();
	        if (recptype.getData("count").equals("1")) {
	            this.setValue("MR_NO", recptype.getData("MR_NO"));
	            this.onMrNo();
	        }
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ɾ����
	 * @throws Exception
	 */
	public void deleteRow() throws Exception{
		opbChargeOpdOrder.deleteRow();
	}
	
	/**
	 * ����
	 * @throws Exception 
	 */
	public void onReadCard() throws Exception{
		if(opbChargeEkt.readCard())
			return;
		setValue(OPBChargePat.TAG_MR_NO, opbChargeEkt.ektReadParm.getValue("MR_NO"));
		onMrNo();
	}
	
	/**
	 * MR_NO�ؼ��س��¼�
	 * @throws Exception 
	 */
	public void onMrNo() throws Exception{ 
		//add by huangtt 20141021 start
		if(opbChargeEkt.ektReadParm == null){
			if(opbChargeEkt.onQueryEkt(PatTool.getInstance().checkMrno(""+getValue(OPBChargePat.TAG_MR_NO)))){
				setValue(OPBChargePat.TAG_MR_NO, PatTool.getInstance().checkMrno(""+getValue(OPBChargePat.TAG_MR_NO)));
			}else{
				setValue(OPBChargePat.TAG_MR_NO, opbChargeEkt.ektReadParm.getValue("MR_NO"));
			}
			
		}
		//add by huangtt 20141021 end
		
		opbChargePat.onQueryPat(""+getValue(OPBChargePat.TAG_MR_NO));
		opbChargeReg.onQueryReg();
		opbChargeOpdOrder.table.setLockRows("");
		onQuery();
	}
	
	/**
	 * ��ѯҽ��
	 * @throws Exception 
	 */
	public void onQuery() throws Exception{
		opbChargeOpdOrder.onQuery();
	}
	
	/**
	 * ����
	 * @throws Exception
	 */
	public void onSave() throws Exception{
		if(opbChargeOpdOrder.onSave()){
			opbChargeOpdOrder.onClear();
			opbChargeOpdOrder.table.setLockRows("");
			opbChargeOpdOrder.onQuery();
		}
	}
	
	/**
	 * ���
	 */
	public void onClear(){
		opbChargePat.onClear();
		opbChargeReg.onClear();
		opbChargeEkt.onClear();
		opbChargeOpdOrder.onClear();
	}
	
	/**
	 * �Ʒ�ģ��
	 * @throws Exception 
	 */
	public void onTmplt() throws Exception{
		opbChargeTmplt.onTmplt();
	}
	
	/**
	 * ���ò�ѯ
	 */
	public void onSelFee(){
		opbChargeSelFee.onSelFee();
	}
	
	/**
	 * �˷Ѳ�ѯ
	 */
	public void onSelReturnFee(){
		opbChargeSelFee.onSelReturnFee();
	}
	
	/**
	 * ��������ѡ���¼�
	 * @throws Exception 
	 */
	public void onSelAdmDate() throws Exception{
		if(!"".equals(getValueString(OPBChargePat.TAG_MR_NO))){
			onMrNo();
		}
	}
	
	
	public void onCat1CodeChange() throws Exception{
		opbChargeOpdOrder.onCat1CodeChange();
	}
}
