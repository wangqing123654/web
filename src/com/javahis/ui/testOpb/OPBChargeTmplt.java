package com.javahis.ui.testOpb;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTableNode;

public class OPBChargeTmplt {

	public OPBChargeControl control;
	private OPBChargeOpdOrder opbChargeOpdOrder;
	
	private static final String URL_SYSFEE_ORDSETOPTION = "%ROOT%\\config\\sys\\sys_fee\\SYSFEE_ORDSETOPTION.x";
	public OPBChargeTmplt(OPBChargeControl opbChargeControl){
		control = opbChargeControl;
		opbChargeOpdOrder = control.opbChargeOpdOrder;
	}
	
	/**
	 * 计费模板 
	 * @throws Exception 
	 */
	public void onTmplt() throws Exception{
		
		TParm parm = new TParm();
		parm.setData("PACK", "DEPT", Operator.getDept());
		TParm operationParm = (TParm) control.openDialog(
				URL_SYSFEE_ORDSETOPTION, parm,
				false);
//		System.out.println("operationParm========"+operationParm);
		
		String orderCode, dosageUnit;
		double dosageQty;
		for (int i = 0; i < operationParm.getCount("ORDER_CODE"); i++) {
			
			orderCode = operationParm.getValue("ORDER_CODE", i);
			dosageUnit = operationParm.getValue("DOSAGE_UNIT", i);
			dosageQty = operationParm.getDouble("DOSAGE_QTY", i);
			String sql = SQL_SYS_FEE.replace("#", orderCode);
			TParm sysFeeParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(sysFeeParm.getCount() < 0){
				control.messageBox("无费用数据");
				return;
			}
			sysFeeParm = sysFeeParm.getRow(0);
			sysFeeParm.setData("DOSAGE_UNIT", dosageUnit);
			sysFeeParm.setData("HEXP_CODE", sysFeeParm.getValue("CHARGE_HOSP_CODE"));
			opbChargeOpdOrder.onReturn(sysFeeParm);
			
			TTableNode n = new TTableNode();
			n.setTable(opbChargeOpdOrder.table);
			String[] map = opbChargeOpdOrder.table.getParmMap().split(";");
			for (int j = 0; j < map.length; j++) {
				if("dosageQty".equals(map[j])){
					n.setColumn(j);
					break;
				}
			}
			
//			n.setColumn(2);//dosageQty
			n.setRow(opbChargeOpdOrder.tableTool.getList().size() - 1);
			n.setOldValue(1);
			n.setValue(dosageQty);
			opbChargeOpdOrder.onTableChangeValue(n);
		}
	}
	
	private final static String SQL_SYS_FEE = 
		"SELECT * FROM SYS_FEE WHERE ORDER_CODE = '#'";
}
