package com.javahis.ui.testOpb.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.bil.BIL;
import jdo.bil.BILStrike;
import jdo.pha.PhaBaseTool;
import jdo.reg.Reg;
import jdo.sys.SysFee;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.ui.TTableNode;
import com.javahis.ui.testOpb.bean.OpdOrder;
import com.javahis.ui.testOpb.bean.SysFeeOrderSetDetailVO;

/**
 * 医嘱工具
 * @author zhangp
 *
 */
public class OrderTool {
	
	private static OrderTool instanceObject;
	
	public static OrderTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OrderTool();
        return instanceObject;
    }
	
	/**
	 * 整理医嘱
	 * @param list
	 * @return
	 */
	public List<OpdOrder> initOrder(List<OpdOrder> list){
		List<OpdOrder> ml = new ArrayList<OpdOrder>();
		List<OpdOrder> sl = new ArrayList<OpdOrder>();
		for (OpdOrder opdOrder : list) {
			if((opdOrder.ordersetCode != null || opdOrder.ordersetCode.length() > 0)
					&& "N".equals(opdOrder.setmainFlg)){
				sl.add(opdOrder);
			}
			if((opdOrder.ordersetCode == null || opdOrder.ordersetCode.length() == 0)){
				ml.add(opdOrder);
			}
			if((opdOrder.ordersetCode != null || opdOrder.ordersetCode.length() > 0)
					&& "Y".equals(opdOrder.setmainFlg)){
				ml.add(opdOrder);
			}
		}
		for (int i = 0; i < ml.size(); i++) {
			OpdOrder mainOrder = ml.get(i);
			mainOrder.showOwnAmt = mainOrder.ownAmt;
			mainOrder.showOwnPrice = mainOrder.ownPrice;
			mainOrder.showAmt = mainOrder.arAmt;
			List<OpdOrder> l = mainOrder.orderList;
			for (OpdOrder setOrder : sl) {
				if("Y".equals(mainOrder.setmainFlg) 
						&& (setOrder.ordersetCode != null || setOrder.ordersetCode.length() > 0)
						&& "N".equals(setOrder.setmainFlg)
						&& mainOrder.rxNo.equals(setOrder.rxNo) 
						&& mainOrder.ordersetCode.equals(setOrder.ordersetCode)
						&& mainOrder.ordersetGroupNo.compareTo(setOrder.ordersetGroupNo) == 0 ){
					l.add(setOrder);
					mainOrder.showOwnPrice = mainOrder.showOwnPrice.add(setOrder.ownPrice);
					mainOrder.showOwnAmt = mainOrder.showOwnAmt.add(setOrder.ownAmt);
					mainOrder.showAmt = mainOrder.showAmt.add(setOrder.arAmt);
				}
			}
			mainOrder.orderList = l;
			mainOrder.showQty = mainOrder.dosageQty;
			ml.set(i, mainOrder);
		}
		return ml;
	}
	
	/**
	 * 反整理医嘱
	 * @param list
	 * @return
	 */
	public List<OpdOrder> deOrderSet(List<OpdOrder> list){
		List<OpdOrder> deList = new ArrayList<OpdOrder>();
		for (OpdOrder opdOrder : list) {
			deList.add(opdOrder);
			List<OpdOrder> setList = opdOrder.orderList;
			for (OpdOrder opdOrder2 : setList) {
				deList.add(opdOrder2);
			}
			opdOrder.orderList = new ArrayList<OpdOrder>();
		}
		return deList;
	}
	
	/**
	 * 给新医嘱赋值
	 * @param reg
	 * @param opdOrder
	 * @param optId
	 * @param optIp
	 * @param execDeptCode
	 * @return
	 */
	public OpdOrder newOrder(Reg reg, OpdOrder opdOrder, String optId, String optIp, 
			String execDeptCode){
		
		double d[] = BILStrike.getInstance().chargeC(reg.getCtz1Code(),
				reg.getCtz2Code(), reg.getCtz3Code(), opdOrder.orderCode,
				opdOrder.hexpCode, reg.getServiceLevel());
		
		TParm parm = PhaBaseTool.getInstance().selectByOrder(opdOrder.orderCode);

		//add by huangtt 20160106 根据服务等级得到价钱
		opdOrder.ownPrice = new BigDecimal( SysFee.getFee(opdOrder.orderCode,reg.getServiceLevel()));

		
		opdOrder.caseNo = reg.caseNo();
		opdOrder.mrNo = reg.getPat().getMrNo();
		opdOrder.admType = reg.getAdmType();
		opdOrder.drCode = reg.getRealdrCode();
		opdOrder.rxType = "7";
		opdOrder.rxNo = "";//TODO
		opdOrder.medApplyNo = "";//TODO
		opdOrder.presrtNo = "";//TODO
		opdOrder.optUser = optId;
		opdOrder.optTerm = optIp;
		opdOrder.optDate = getSystemTime();
		opdOrder.regionCode = reg.getRegion();
		opdOrder.deptCode = reg.getRealdeptCode();
		opdOrder.orderDate = getSystemTime();
		opdOrder.execDeptCode = execDeptCode;
		opdOrder.costCenterCode = execDeptCode;
		opdOrder.ctz1Code = reg.getCtz1Code();
		opdOrder.ctz2Code = reg.getCtz2Code();
		opdOrder.ctz3Code = reg.getCtz3Code();
		opdOrder.takeDays = new BigDecimal(1);//TODO
		opdOrder.setmainFlg = "N";

		opdOrder.execDrCode = optId;
		opdOrder.execDate = this.getSystemTime();
		opdOrder.execFlg = "Y";
		
		opdOrder.fileNo = new BigDecimal(0);
		opdOrder.packageTot = new BigDecimal(0);
		opdOrder.prescriptNo = new BigDecimal(0);
		opdOrder.printNo = "0";//TODO
		opdOrder.finalType = "F";
		opdOrder.receiptFlg = "N";
		
//		opdOrder.counterNo	null	//TODO
//		opdOrder.doseType	null	//TODO
		opdOrder.phaType = parm.getValue("PHA_TYPE", 0);
		opdOrder.routeCode = parm.getValue("ROUTE_CODE", 0);
		
		opdOrder.freqCode = "STAT";
		
		opdOrder.billFlg = "N";
		opdOrder.temporaryFlg = "N";
		opdOrder.releaseFlg = "N";
		opdOrder.linkmainFlg = "N";
		opdOrder.giveboxFlg = "N";
		opdOrder.hideFlg = "N";
		opdOrder.urgentFlg = "N";
		opdOrder.printtypeflgInfant = "N";
		opdOrder.atcFlg = "N";
		opdOrder.printFlg = "N";
		opdOrder.dctagentFlg = "N";
		opdOrder.billType = "C";

		opdOrder.rexpCode = BIL.getRexpCode(opdOrder.hexpCode, "O");
		
		opdOrder.dosageQty = new BigDecimal(1);
		opdOrder.mediQty = new BigDecimal(1);
		opdOrder.dispenseQty = new BigDecimal(1);
		
		opdOrder.mediUnit = opdOrder.dosageUnit;
		opdOrder.dispenseUnit = opdOrder.dosageUnit;
		
		opdOrder.discountRate = new BigDecimal(d[1]);
		opdOrder.ownAmt = opdOrder.ownPrice.multiply(opdOrder.dosageQty);
		opdOrder.arAmt = opdOrder.ownAmt.multiply(opdOrder.discountRate);
		
		opdOrder.showAmt = opdOrder.arAmt;
		opdOrder.showOwnAmt = opdOrder.ownAmt;
		opdOrder.showOwnPrice = opdOrder.ownPrice;
		opdOrder.subQty = new BigDecimal(0);
		
		opdOrder.showQty = opdOrder.dosageQty;
		opdOrder.invCode = opdOrder.invCode;  //add by huangtt 20141208
		opdOrder.updateTime =SystemTool.getInstance().getUpdateTime(); //add by huangtt 20170518
		
		return opdOrder;
	}
	
	/**
	 * 为新集合医嘱主项Opdorder添加细项
	 * @param mainOrder
	 * @param deptCode
	 * @param execDeptCode
	 * @param reg
	 * @param groupNo
	 * @return
	 * @throws Exception
	 */
	public OpdOrder queryOrderSet(OpdOrder mainOrder, String deptCode, String execDeptCode, Reg reg, int groupNo) throws Exception{
		String sql = ORDERSETSQL.replace("#", mainOrder.orderCode);
		mainOrder.setmainFlg = "Y";
		mainOrder.ordersetCode = mainOrder.orderCode;
		mainOrder.ordersetGroupNo = new BigDecimal(groupNo);
		SysFeeOrderSetDetailVO obj = new SysFeeOrderSetDetailVO();
		List<OpdOrder> list = new ArrayList<OpdOrder>();
		List<SysFeeOrderSetDetailVO> detailVOs = QueryTool.getInstance().queryBySql(sql, obj);
		BigDecimal mainShowArAmt = new BigDecimal(0);
		BigDecimal mainShowOwnAmt = new BigDecimal(0);
		BigDecimal mainShowOwnPrice = new BigDecimal(0);
		for (SysFeeOrderSetDetailVO sysFeeOrderSetDetailVO : detailVOs) {
            double d[] = BILStrike.getInstance().chargeC(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
            		sysFeeOrderSetDetailVO.orderCode,
            		sysFeeOrderSetDetailVO.chargeHospCode,
                    reg.getServiceLevel());
			OpdOrder opdOrder = new OpdOrder();
			opdOrder.caseNo = reg.caseNo();
			opdOrder.rxType = "7";
			opdOrder.freqCode = "STAT";
			opdOrder.orderDesc = sysFeeOrderSetDetailVO.orderDesc;
			opdOrder.orderCode = sysFeeOrderSetDetailVO.orderCode;
			opdOrder.goodsDesc = sysFeeOrderSetDetailVO.goodsDesc;
			opdOrder.optitemCode = sysFeeOrderSetDetailVO.optitemCode;
			opdOrder.dispenseUnit = sysFeeOrderSetDetailVO.unitCode;
			opdOrder.dosageUnit = sysFeeOrderSetDetailVO.unitCode;
			opdOrder.mediUnit = sysFeeOrderSetDetailVO.unitCode;
			opdOrder.cat1Type = sysFeeOrderSetDetailVO.cat1Type;
			opdOrder.deptCode = deptCode;
			opdOrder.execDeptCode = execDeptCode;
			opdOrder.takeDays = new BigDecimal(1);//TODO
			opdOrder.dosageQty = sysFeeOrderSetDetailVO.totqty;
			opdOrder.dispenseQty = sysFeeOrderSetDetailVO.totqty;
			opdOrder.mediQty = sysFeeOrderSetDetailVO.totqty;
            if ("2".equals(reg.getServiceLevel())) {
            	opdOrder.ownPrice = sysFeeOrderSetDetailVO.ownPrice2;
            }
            else if ("3".equals(reg.getServiceLevel())) {
            	opdOrder.ownPrice = sysFeeOrderSetDetailVO.ownPrice3;
            }
            else{
            	opdOrder.ownPrice = sysFeeOrderSetDetailVO.ownPrice;
            }
            opdOrder.inspayType = sysFeeOrderSetDetailVO.inspayType;
            opdOrder.orderCat1Code = sysFeeOrderSetDetailVO.orderCat1Code;
            opdOrder.rpttypeCode = sysFeeOrderSetDetailVO.rpttypeCode;
            opdOrder.degreeCode = sysFeeOrderSetDetailVO.degreeCode;
            opdOrder.printtypeflgInfant = "N";
            opdOrder.setmainFlg = "N";
            opdOrder.ctz1Code = reg.getCtz1Code();
            opdOrder.ctz2Code = reg.getCtz2Code();
            opdOrder.ctz3Code = reg.getCtz3Code();
            opdOrder.discountRate = new BigDecimal(d[1]);
            opdOrder.rexpCode = BIL.getRexpCode(sysFeeOrderSetDetailVO.chargeHospCode, "O");
            opdOrder.hexpCode = sysFeeOrderSetDetailVO.chargeHospCode;
            opdOrder.billFlg = "N";
            opdOrder.hideFlg = sysFeeOrderSetDetailVO.hideFlg;
            opdOrder.ordersetCode = mainOrder.ordersetCode;
            opdOrder.ordersetGroupNo = mainOrder.ordersetGroupNo;
//            BigDecimal eachown = sysFeeOrderSetDetailVO.totqty.multiply(sysFeeOrderSetDetailVO.ownPrice);
            BigDecimal eachown = sysFeeOrderSetDetailVO.totqty.multiply(opdOrder.ownPrice); 
			BigDecimal eachtot = eachown.multiply(opdOrder.discountRate);
            opdOrder.ownAmt = eachown;
            opdOrder.arAmt = eachtot;
            opdOrder.subQty = new BigDecimal(0);
            
    		opdOrder.optUser = mainOrder.optUser;
    		opdOrder.optTerm = mainOrder.optTerm;
    		opdOrder.optDate = mainOrder.optDate;
    		opdOrder.regionCode = mainOrder.regionCode;
    		opdOrder.mrNo = mainOrder.mrNo;
    		opdOrder.admType = mainOrder.admType;
    		opdOrder.temporaryFlg = mainOrder.temporaryFlg;
    		opdOrder.releaseFlg = mainOrder.releaseFlg;
    		opdOrder.linkmainFlg = mainOrder.linkmainFlg;
    		opdOrder.drCode = mainOrder.drCode;
    		opdOrder.orderDate = mainOrder.orderDate;
    		opdOrder.execDrCode = mainOrder.execDrCode;
    		opdOrder.urgentFlg = mainOrder.urgentFlg;
    		opdOrder.expensiveFlg = mainOrder.expensiveFlg;
    		opdOrder.atcFlg = mainOrder.atcFlg;
    		opdOrder.printFlg = mainOrder.printFlg;
    		opdOrder.dctagentFlg = mainOrder.dctagentFlg;
    		opdOrder.execFlg = mainOrder.execFlg;
    		opdOrder.receiptFlg = mainOrder.receiptFlg;
    		opdOrder.billType = mainOrder.billType;
    		opdOrder.finalType = mainOrder.finalType;
    		opdOrder.costCenterCode = mainOrder.costCenterCode;
    		opdOrder.execDate = mainOrder.execDate;
    		
    		opdOrder.invCode = mainOrder.invCode;  //add by huangtt 20141208
    		
    		opdOrder.updateTime =SystemTool.getInstance().getUpdateTime(); //add by huangtt 20170518
    		
            opdOrder.modifyState = Type.INSERT;
            list.add(opdOrder);
            mainShowOwnPrice = mainShowOwnPrice.add(opdOrder.ownPrice);
            mainShowOwnAmt = mainShowOwnAmt.add(opdOrder.ownAmt);
            mainShowArAmt = mainShowArAmt.add(opdOrder.arAmt);
		}
		mainOrder.orderList = list;
		mainOrder.showOwnPrice = mainShowOwnPrice;
		mainOrder.showOwnAmt = mainShowOwnAmt;
		mainOrder.showAmt = mainShowArAmt;
		return mainOrder;
	}
	
	/**
	 * 值改变
	 * @param tNode
	 * @param list
	 * @return
	 */
	public List<OpdOrder> changeValue(TTableNode tNode, List<OpdOrder> list){
		int column = tNode.getColumn();
		String colName = tNode.getTable().getParmMap(column);
		int row = tNode.getRow();
		OpdOrder mainOrder = list.get(row);
		List<OpdOrder> dList = mainOrder.orderList;
//		MathContext mc = new MathContext(2, RoundingMode.HALF_DOWN);
		if ("dosageQty".equals(colName)) {
			
			mainOrder.ownAmt = mainOrder.dosageQty.multiply(mainOrder.ownPrice);
			mainOrder.ownAmt = mainOrder.ownAmt.setScale(2, RoundingMode.HALF_DOWN);
			mainOrder.showOwnAmt = mainOrder.ownAmt;
			mainOrder.arAmt = mainOrder.ownAmt.multiply(mainOrder.discountRate);
			mainOrder.arAmt = mainOrder.arAmt.setScale(2, RoundingMode.HALF_DOWN);
			mainOrder.showAmt = mainOrder.arAmt;
			
			if(mainOrder.modifyState == Type.INSERT){
				mainOrder.showQty = mainOrder.dosageQty;
			}
			
			BigDecimal showOwnAmt = new BigDecimal(0);
			BigDecimal showAmt = new BigDecimal(0);
			
			for (int i = 0; i < dList.size(); i++) {
				OpdOrder opdOrder = dList.get(i);
				opdOrder.ownAmt = opdOrder.dosageQty.multiply(opdOrder.ownPrice);
				opdOrder.ownAmt = opdOrder.ownAmt.setScale(2, RoundingMode.HALF_DOWN);
				opdOrder.arAmt = opdOrder.ownAmt.multiply(opdOrder.discountRate);
				opdOrder.arAmt = opdOrder.arAmt.setScale(2, RoundingMode.HALF_DOWN);
				
				showOwnAmt = showOwnAmt.add(opdOrder.ownAmt);
				showAmt = showAmt.add(opdOrder.arAmt);
				dList.set(i, opdOrder);
			}
			
			if(mainOrder.showOwnAmt.compareTo(new BigDecimal(0)) == 0)
				mainOrder.showOwnAmt = showOwnAmt;
			if(mainOrder.showAmt.compareTo(new BigDecimal(0)) == 0)
				mainOrder.showAmt = showAmt;
			mainOrder.orderList = dList;
			
		}
		mainOrder.subQty = mainOrder.showQty.subtract(mainOrder.dosageQty);
		list.set(row, mainOrder);
		return list;
	}
	
	/**
	 * 插主键
	 * @param opdOrder
	 * @param index
	 * @param rxNo
	 * @return
	 */
	public OpdOrder setPkey(OpdOrder opdOrder, int index, String rxNo){
		opdOrder.rxNo = rxNo;
		opdOrder.seqNo = new BigDecimal(index);
		return opdOrder;
	}
	
	
	/**
	 * 取得系统时间
	 * @return
	 */
	public String getSystemTime(){
		Timestamp ts = SystemTool.getInstance().getDate();
		String d = ""+ts;
		d = d.substring(0, 4)+d.substring(5, 7)+d.substring(8, 10)+d.substring(11, 13)+d.substring(14, 16)+d.substring(17, 19);
		return d;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private final String ORDERSETSQL = 
		" SELECT A.ORDERSET_CODE AS ORDERSET_CODE," +
		" A.ORDER_CODE AS ORDER_CODE," +
		" A.DOSAGE_QTY AS TOTQTY," +
		" A.HIDE_FLG AS HIDE_FLG," +
		" A.DESCRIPTION AS ORDERSETDESC," +
		" B.ORDER_DESC AS ORDER_DESC," +
		" B.TRADE_ENG_DESC AS TRADE_ENG_DESC," +
		" B.GOODS_DESC AS GOODS_DESC," +
		" B.GOODS_PYCODE AS GOODS_PYCODE," +
		" B.ALIAS_DESC AS ALIAS_DESC," +
		" B.ALIAS_PYCODE AS ALIAS_PYCODE," +
		" B.NHI_FEE_DESC AS NHI_FEE_DESC," +
		" B.HABITAT_TYPE AS HABITAT_TYPE," +
		" B.SPECIFICATION AS SPECIFICATION," +
		" B.MAN_CODE AS MAN_CODE," +
		" B.UNIT_CODE AS UNIT_CODE," +
		" B.DESCRIPTION AS DESCRIPTION," +
		" B.CHARGE_HOSP_CODE AS CHARGE_HOSP_CODE," +
		" B.ORDER_CAT1_CODE AS ORDER_CAT1_CODE," +
		" B.OWN_PRICE AS OWN_PRICE," +
		" B.NHI_PRICE AS NHI_PRICE," +
		" B.GOV_PRICE AS GOV_PRICE," +
		" B.HYGIENE_TRADE_CODE AS HYGIENE_TRADE_CODE," +
		" B.LET_KEYIN_FLG AS LET_KEYIN_FLG," +
		" B.DISCOUNT_FLG AS DISCOUNT_FLG," +
		" B.EXPENSIVE_FLG AS EXPENSIVE_FLG," +
		" B.OPD_FIT_FLG AS OPD_FIT_FLG," +
		" B.EMG_FIT_FLG AS EMG_FIT_FLG," +
		" B.IPD_FIT_FLG AS IPD_FIT_FLG," +
		" B.HRM_FIT_FLG AS HRM_FIT_FLG," +
		" B.DR_ORDER_FLG AS DR_ORDER_FLG," +
		" B.INTV_ORDER_FLG AS INF_ORDER_FLG," +
		" B.LCS_CLASS_CODE AS LCS_CLASS_CODE," +
		" B.TRANS_OUT_FLG AS TRANS_OUT_FLG," +
		" B.TRANS_HOSP_CODE AS TRANS_HOSP_CODE," +
		" B.USEDEPT_CODE AS USEDEPT_CODE," +
		" B.EXEC_ORDER_FLG AS RBORDER_FLG," +
		" B.EXEC_DEPT_CODE AS RBORDER_DEPT_CODE," +
		" B.INSPAY_TYPE AS INSPAY_TYPE," +
		" B.ADDPAY_RATE AS ADDPAY_RATE," +
		" B.ADDPAY_AMT AS ADDPAY_AMT," +
		" B.NHI_CODE_O AS NHI_CODE_O," +
		" B.NHI_CODE_E AS NHI_CODE_E," +
		" B.NHI_CODE_I AS NHI_CODE_I," +
		" B.CTRL_FLG AS CTRL_FLG," +
		" B.CLPGROUP_CODE AS CLPGROUP_CODE," +
		" B.INDV_FLG AS INDV_FLG," +
		" B.RPTTYPE_CODE AS RPTTYPE_CODE," +
		" B.DEV_CODE AS DEV_CODE," +
		" B.MR_CODE AS MR_CODE," +
		" B.DEGREE_CODE AS DEGREE_CODE," +
		" B.ORDERSET_FLG AS SET_FLG," +
		" B.CIS_FLG AS CIS_FLG," +
		" B.PY1 AS PY1," +
		" B.PY2 AS PY2," +
		" B.SEQ AS SEQ," +
		" B.OPTITEM_CODE AS OPTITEM_CODE," +
		" B.CAT1_TYPE," +
		" B.SUB_SYSTEM_CODE AS SUB_SYSTEM_CODE," +
		" B.OWN_PRICE2 AS OWN_PRICE2," +
		" B.OWN_PRICE3 AS OWN_PRICE3" +
		" FROM SYS_ORDERSETDETAIL A, SYS_FEE B" +
		" WHERE A.ORDERSET_CODE = '#' AND B.ORDER_CODE = A.ORDER_CODE";
}
