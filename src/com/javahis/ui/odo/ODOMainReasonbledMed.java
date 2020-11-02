package com.javahis.ui.odo;

import javax.swing.JOptionPane;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTabbedPane;
import com.javahis.util.ReasonableMedUtil;
import com.javahis.util.StringUtil;

import device.PassDriver;
import jdo.odo.OpdOrder;
import jdo.pha.PassTool;
import jdo.sys.Operator;
import jdo.sys.SYSNewRegionTool;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站合理用药对象
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站合理用药对象
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainReasonbledMed {
	
	OdoMainControl odoMainControl;
	ODOMainOpdOrder odoMainOpdOrder;
	
	private boolean passIsReady = false;// 合理用药开关
	private boolean enforcementFlg = false;
	
	private static String URLPHAOPTCHOOSE = "%ROOT%\\config\\pha\\PHAOptChoose.x";
	
	private static final String NULLSTR = "";
	
	public ODOMainReasonbledMed(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
	}
	
	public void onInit() throws Exception{
		odoMainOpdOrder = odoMainControl.odoMainOpdOrder;
		passIsReady = SYSNewRegionTool.getInstance().isOREASONABLEMED(
				Operator.getRegion());
		if (passIsReady) {
			if (!initReasonbledMed()) {
				odoMainControl.messageBox("合理用药初始化失败！");
			}
		}
		enforcementFlg = "Y".equals(TConfig
				.getSystemValue("EnforcementFlg"));
	}
	
	/**
	 * 初始化合理用药
	 */
	public boolean initReasonbledMed() throws Exception{
		try {
			if (PassDriver.init() == 0) {
				return false;
			}
			// 合理用药初始化
			if (PassDriver.PassInit(Operator.getName(), Operator.getDept(), 10) == 0) {
				return false;
			}
			// 合理用药控制参数
			if (PassDriver.PassSetControlParam(1, 2, 0, 2, 1) == 0) {
				return false;
			}
		} catch (UnsatisfiedLinkError e1) {
			return false;
		} catch (NoClassDefFoundError e2) {
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 合理用药按钮
	 */
	public void onResonablemed() throws Exception{
		if (!passIsReady) {
			odoMainControl.messageBox("E0067");
			return;
		}
		if (!initReasonbledMed()) {
			odoMainControl.messageBox("合理用药初始化失败，此功能不能使用！");
			return;
		}
		if (odoMainControl.odo == null) {
			odoMainControl.messageBox("E0024");
			return;
		}
		if (((TTabbedPane) odoMainControl.getComponent(ODOMainOpdOrder.TAGTTABPANELORDER))
				.getSelectedIndex() == 0
				|| ((TTabbedPane) odoMainControl.getComponent(ODOMainOpdOrder.TAGTTABPANELORDER))
						.getSelectedIndex() == 1) {
			odoMainControl.messageBox("不适用合理用药功能!");
			return;
		}
		TParm parm = setopdRecipeInfo();
		if (parm.getCount("ERR") > 0) {
			odoMainControl.messageBox("E0068");
			return;
		}
		if (parm.getCount("ORDER_CODE") < 0) {
			odoMainControl.messageBox("未检测到药品！");
			return;
		}
		// 门诊使用
		PassDriver.PassDoCommand(3);
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			PassDriver.PassGetWarn1(parm.getValue("SEQ", i) + NULLSTR);
		}
	}
	
	/**
	 * 
	 * 自动检测合理用药
	 * 
	 */
	public boolean checkDrugAuto() throws Exception{
		if (!passIsReady) {
			return true;
		}
		if (!initReasonbledMed()) {
			return true;
		}
//		long time = System.currentTimeMillis();
		PassTool.getInstance().setPatientInfo(odoMainControl.odo.getCaseNo());
		PassTool.getInstance().setAllergenInfo(odoMainControl.odo.getMrNo());
		PassTool.getInstance().setMedCond(odoMainControl.odo.getCaseNo());
		TParm parm = setopdRecipeInfoAuto();
		if (!odoMainOpdOrder.isWarn(parm)) {
			return true;
		}
		if (enforcementFlg) {
			return false;
		}
		if (JOptionPane.showConfirmDialog(null, "有药品使用不合理,是否存档?", "信息",
				JOptionPane.YES_NO_OPTION) != 0) {
			return false;
		}
		return true;
	}
	
	/**
     * 合理用药--药品信息查询
     */
    public void onQueryRationalDrugUse() throws Exception {// add by wanglong 20130522
    	if (!passIsReady) {
    		odoMainControl.messageBox("合理用药未启用");
            return;
        }
        if (!PassTool.getInstance().init()) {
        	odoMainControl.messageBox("合理用药初始化失败，此功能不能使用！");
            return;
        }
        int tabbedIndex = ((TTabbedPane) odoMainControl.getComponent(ODOMainOpdOrder.TAGTTABPANELORDER)).getSelectedIndex();
        int row = -1;
        String orderCode = NULLSTR;
        switch (tabbedIndex) {
            case ODORxMed.TABBEDPANE_INDEX:
                row = odoMainOpdOrder.odoRxMed.getTable().getSelectedRow();
                orderCode = odoMainOpdOrder.odoRxMed.getTable().getDataStore().getItemString(row, "ORDER_CODE");
                break;
            case ODORxChnMed.TABBEDPANE_INDEX:
                row = odoMainOpdOrder.odoRxChn.getTable().getSelectedRow();
                orderCode = odoMainOpdOrder.odoRxChn.getTable().getDataStore().getItemString(row, "ORDER_CODE");
                break;
            case ODORxCtrl.TABBEDPANE_INDEX:
                row = odoMainOpdOrder.odoRxCtrl.getTable().getSelectedRow();
                orderCode = odoMainOpdOrder.odoRxCtrl.getTable().getDataStore().getItemString(row, "ORDER_CODE");
                break;
        }
        if (row < 0) {
            return;
        }
        String value = (String) odoMainControl.openDialog(URLPHAOPTCHOOSE);
        if (value == null || value.length() == 0) {
            return;
        }
        int conmmand = Integer.parseInt(value);
        if (conmmand != 6) {
            PassTool.getInstance().setQueryDrug(orderCode, conmmand);
        } else {
            PassTool.getInstance().setWarnDrug2(NULLSTR, NULLSTR);
        }
    }
    
	/**
	 * 门诊医生检核药品自动
	 * 
	 * @return TParm
	 */
	public TParm setopdRecipeInfoAuto() throws Exception{
		TParm parm = setopdRecipeInfo();
		PassDriver.PassDoCommand(33);
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			result.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			result.addData("FLG", PassDriver.PassGetWarn1(parm.getValue("SEQ",
					i)));
		}
		return result;
	}
	
	/**
	 * 传入门诊医生药品信息
	 * 
	 * @return TParm
	 */
	public TParm setopdRecipeInfo()throws Exception {
		TParm parm = new TParm();
		int j;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		
		String[] orderInfo;
		ReasonableMedUtil resonableMed = new ReasonableMedUtil();
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_DESC"))) {
				continue;
			}
			orderInfo = resonableMed.getOrderInfo(order, i);
			j = PassDriver.PassSetRecipeInfo(orderInfo[0], orderInfo[1],
					orderInfo[2], orderInfo[3], orderInfo[4], orderInfo[5],
					orderInfo[6], orderInfo[7], orderInfo[8], orderInfo[9],
					orderInfo[10], orderInfo[11]);
			if (j != 1) {
				parm.addData("ERR", orderInfo[0]);
				break;
			} else {
				parm.addData("SEQ", orderInfo[0]);
				parm.addData("ORDER_CODE", orderInfo[1]);
			}
		}
		return parm;
	}

}
