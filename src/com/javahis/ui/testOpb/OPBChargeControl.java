package com.javahis.ui.testOpb;


import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSOperatorTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * 补充计费控制类
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
	 * 初始化
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
	 * 初始化权限   add by huangtt 20150513
	 */
	public void onInitPopemed(){
		TParm parm = SYSOperatorTool.getUserPopedem(Operator.getID(), getUITag());
		for (int i = 0; i < parm.getCount(); i++) {
			this.setPopedem(parm.getValue("AUTH_CODE", i), true);
		}
	}

	
	
	/**
	 * 注册控件的事件
	 */
	public void onInitEvent() throws Exception {
		opbChargeOpdOrder.onInitEvent();
	}
	
	/**
	 * 接收传参并初始化界面
	 */
	private void onInitReceive(){
		//得到前台传来的数据并显示在界面上
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
	 * 删除行
	 * @throws Exception
	 */
	public void deleteRow() throws Exception{
		opbChargeOpdOrder.deleteRow();
	}
	
	/**
	 * 读卡
	 * @throws Exception 
	 */
	public void onReadCard() throws Exception{
		if(opbChargeEkt.readCard())
			return;
		setValue(OPBChargePat.TAG_MR_NO, opbChargeEkt.ektReadParm.getValue("MR_NO"));
		onMrNo();
	}
	
	/**
	 * MR_NO控件回车事件
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
	 * 查询医嘱
	 * @throws Exception 
	 */
	public void onQuery() throws Exception{
		opbChargeOpdOrder.onQuery();
	}
	
	/**
	 * 保存
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
	 * 清空
	 */
	public void onClear(){
		opbChargePat.onClear();
		opbChargeReg.onClear();
		opbChargeEkt.onClear();
		opbChargeOpdOrder.onClear();
	}
	
	/**
	 * 计费模板
	 * @throws Exception 
	 */
	public void onTmplt() throws Exception{
		opbChargeTmplt.onTmplt();
	}
	
	/**
	 * 费用查询
	 */
	public void onSelFee(){
		opbChargeSelFee.onSelFee();
	}
	
	/**
	 * 退费查询
	 */
	public void onSelReturnFee(){
		opbChargeSelFee.onSelReturnFee();
	}
	
	/**
	 * 就诊日期选择事件
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
