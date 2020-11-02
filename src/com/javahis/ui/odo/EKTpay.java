package com.javahis.ui.odo;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTNewIO;
import jdo.odo.OPDAbnormalRegTool;
import jdo.odo.OpdOrder;
import jdo.opb.OPBTool;
import jdo.opd.OrderTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站医疗卡收费方式实现类
 * </p>
 * 
 * <p>
 * Description: 门诊医生工作站医疗卡收费方式实现类
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class EKTpay implements IPayType {
	
	private OdoMainControl odoMainControl;
	private ODOMainEkt odoMainEkt;
	private ODOMainOpdOrder odoMainOpdOrder;
	
	private String tredeNo;
	
	private static final String NULLSTR = "";
	private static final String URLEKTINFOUI = "%ROOT%\\config\\ekt\\EKTInfoUI.x";
	private static final String URLOPDREGCHOOSE = "%ROOT%\\config\\opd\\OPDRegChoose.x";
	
	public static final String PAY_TYPE	= "E";
	
	private void onInit(OdoMainControl odoMainControl) throws Exception{
		this.odoMainControl = odoMainControl;
		this.odoMainEkt = odoMainControl.odoMainEkt;
		this.odoMainOpdOrder = odoMainControl.odoMainOpdOrder;
	}
	
	/**
	 * 读卡
	 * @return
	 */
	private boolean readEKT() {
		odoMainEkt.ektReadParm = EKTIO.getInstance().TXreadEKT();
		if (odoMainEkt.ektReadParm.getErrCode() < 0) {
			odoMainControl.messageBox("医疗卡读卡有误。");
			return false;
		}
		if (!odoMainEkt.ektReadParm.getValue("MR_NO").equals(odoMainControl.getValue("MR_NO"))) {
			odoMainControl.messageBox("病患信息不符,此医疗卡病患名称为:"
					+ odoMainEkt.ektReadParm.getValue("PAT_NAME"));
			odoMainEkt.ektReadParm = null;
			return false;
		}
		if (null == odoMainEkt.ektReadParm) {
			odoMainControl.messageBox("未确认身份，请读医疗卡");
			return false;
		}
		return true;
	}
	
	/**
	 * 读卡监听
	 */
	@Override
	public TParm readCard() {
		return EKTIO.getInstance().TXreadEKT();
	}
	
	/**
	 * 判断此次操作的医嘱在数据库中是否已经存在，如果存在,在执行收费操作时，判断医疗卡中金额是否充足
	 * 如果金额不足，执行此处方签所收费的医嘱退还医疗卡中
	 */
	private boolean updateOrderParm(TParm orderParm ,TParm orderOldParm,TParm unParm){
		boolean unFlg = false;
		int unCount = 0;
		// System.out.println("orderParm：：：：：：：" + orderParm);
		for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
			if (orderParm.getValue("CAT1_TYPE",i).equals("RIS")
					|| orderParm.getValue("CAT1_TYPE",i).equals("LIS")
					|| orderParm.getValue("ORDER_CODE",i).length() <= 0) {
				continue;
			}
			for (int j = 0; j < orderOldParm.getCount("ORDER_CODE"); j++) {
				if (orderParm.getValue("RX_NO",i).equals(
						orderOldParm.getValue("RX_NO", j))
						&& orderParm.getValue("SEQ_NO",i).equals(
								orderOldParm.getValue("SEQ_NO", j))) {
					if (orderParm.getDouble("AMT",i) == orderOldParm
							.getDouble("AR_AMT", j)) {
						break;
					}
					unParm.setRowData(unCount, orderOldParm, j);// 获得执行修改的医嘱
					unCount++;
					unFlg = true;// 判断是否存在医嘱
					break;
				}
			}
		}
		unParm.setCount(unCount);
		return unFlg;
	}
	
	/**
	 * 校验医疗卡 删除医嘱 添加回冲 OPD_ORDER 数据
	 * 
	 * @param parm
	 */
	private void ektDeleteChackOut(TParm parm) {
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			for (int j = 0; j < OPBTool.orderName.length; j++) {
				if (null == parm.getValue(OPBTool.orderName[j], i)
						|| parm.getValue(OPBTool.orderName[j], i).length() <= 0) {
					parm.setData(OPBTool.orderName[j], i, NULLSTR);
				}
			}
		}
	}
	
	/**
	 * 校验是否可以删除医嘱 医疗卡删除医嘱可以直接删除,如果已经扣款的医嘱删除 将直接执行扣款操作
	 * 
	 * @param order
	 * @param row
	 * boolean flg true 删除一条医嘱 
	 * @return
	 * =======pangben 2013-1-29 添加参数 校验检验检查医嘱是否已经登记
	 * @throws Exception 
	 */
	@Override
	public boolean deleteOrder(OpdOrder order, int row, String message,String medAppMessage) throws Exception {
		// 医疗卡操作可以删除医嘱====pangben 2011-12-16
		if (null == odoMainEkt.ektReadParm || null == odoMainEkt.ektReadParm.getValue("MR_NO")
				|| odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0) {
			if (!order.isRemovable(row, false)) {
				odoMainControl.messageBox("已计费,请执行退费操作"); // 已计费医嘱不能删除
				return false;
			} 
			return true;
		} else {
			// 已收费医嘱没有主诊断不能删除
			//modify by huangtt 20141126
			int rowMainDiag = odoMainControl.odo.getDiagrec().getMainDiag();
			if (rowMainDiag < 0) {
				odoMainControl.messageBox("请开立主诊断");
				return false;
			}
			
			
			if (!ektDelete(order, row)) {// 校验是否可以删除医嘱
				odoMainControl.messageBox(message); // 已计费医嘱不能删除
				return false;
			}
			//=========pangben 2013-1-29
			if(!odoMainControl.odoMainOpdOrder.odoRxExa.medAppyCheckDate(order, row)){
				odoMainControl.messageBox(medAppMessage); // 校验 检验检查已经登记的数据不能删除操作
				return false;
			}
			boolean flg = false;
			if(odoMainEkt.preDebtFlg){
				flg = true;
			}else{
				flg = readEKT();
			}
			return flg;
		}
	}
	
	/**
	 * 校验医疗卡删除操作 rxFlg false : 删除整张处方签操作使用 true :删除单个处方qi
	 */
	private boolean ektDelete(OpdOrder order, int row) {
		// 执行医疗卡操作，判断是否已经可以使用医疗卡
		if (!order.isRemovable(row, false)) {// FALSE : 已经收费 要执行 onFee() 方法
			// TRUE : 未收费 不执行onFee() 方法
			//ektDeleteOrder = false;
			return false;
		} 
		return true;
	}
	
	/**
	 * 修改医嘱操作时，点击保存或者暂存操作执行直接收费操作
	 * @throws Exception 
	 */
	@Override
	public void isExeFee(TParm ektOrderParmone, TParm ektSumExeParm) throws Exception {
		if (null == odoMainEkt.ektReadParm || odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0) {
		} else {
			// 判断此次操作的医嘱在数据库中是否已经存在，如果存在,在执行收费操作时，判断医疗卡中金额是否充足
			// 如果金额不足，执行此处方签所收费的医嘱退还医疗卡中
			updateOrderUnConcle(ektOrderParmone, ektSumExeParm);	
		}
		// 删除\修改医嘱收费
		if (!onEktSave(ektOrderParmone, ektSumExeParm)) {
		}
	}
	
	/**
	 * 修改收费医嘱不能执行扣款界面撤销动作
	 * @return
	 */
	@Override
	public boolean updateOrderUnConcle(TParm ektOrderParmone,TParm ektOrderParm) throws Exception{
		//点击暂存按钮 如果已收费的医嘱修改金额 执行 修改医嘱扣款或退款操作
		boolean unFlg = false;
		TParm updateParm=ektOrderParm.getParm("updateParm");
		for (int i = 0; i < updateParm.getCount("ORDER_CODE"); i++) {
			if (updateParm.getValue("CAT1_TYPE", i).equals("RIS")
					|| updateParm.getValue("CAT1_TYPE", i).equals(
							"LIS")
					|| updateParm.getValue("ORDER_CODE", i).length() <= 0) {
				continue;
			}
			for (int j = 0; j < ektOrderParmone.getCount("ORDER_CODE"); j++) {//====pangb 2013-2-28 修改医嘱操作暂存有问题
				if (updateParm.getValue("RX_NO", i).equals(
						ektOrderParmone.getValue("RX_NO", j))
						&& updateParm.getValue("SEQ_NO", i).equals(
								ektOrderParmone.getValue("SEQ_NO", j))) {
					if (updateParm.getDouble("AMT", i) != ektOrderParmone
							.getDouble("AR_AMT", j)) {
						unFlg = true;// 判断是否修改医嘱
						break;
					}
				}
			}
			if (unFlg) {
				ektOrderParmone.setData("OPBEKTFEE_FLG", "Y");
				//ektOnFee = true;
				break;
			}
		}
		return unFlg;
	}
	
	/**
	 * 医疗卡操作撤销删除的医嘱 =====pangben 2012-01-06
	 */
	private void concelDeleteOrder(TParm orderParm, TParm oldOrderParm,
			boolean exeDelOrder) {
		TParm tempParm = new TParm();
		int count = 0;
		// if (!exeDelOrder) {// 医嘱数据不同 不是全部删除的orderParm医嘱 没有这么多数据
		for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
			for (int j = 0; j < oldOrderParm.getCount("ORDER_CODE"); j++) {
				if (oldOrderParm.getValue("SEQ_NO", j).equals(
						orderParm.getValue("SEQ_NO", i))
						&& oldOrderParm.getValue("RX_NO", j).equals(
								orderParm.getValue("RX_NO", i))
						&& !orderParm.getBoolean("BILL_FLG", i)) {
					tempParm.setRowData(count, oldOrderParm, j);
					count++;
				}
			}
		}
		orderParm = tempParm;
		if (orderParm.getCount("ORDER_CODE") > 0) {
			// 判断是否存在修改的医嘱信息
			for (int i = orderParm.getCount("ORDER_CODE") - 1; i >= 0; i--) {
				if (!orderParm.getBoolean("BILL_FLG", i)) {
					orderParm.setData("BILL_FLG", i, "Y");
					orderParm.setData("BILL_DATE", i, SystemTool.getInstance()
							.getDate());
					orderParm.setData("BILL_USER", i, Operator.getID());
				}
			}
			ektDeleteChackOut(orderParm);
			// System.out.println("concleDeleteorder方法入参Parm:::::"+orderParm);
			orderParm.setData("MED_FLG", "Y");// 医疗卡撤销操作不执行添加MED_APPLY 表数据
			TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
					"concleDeleteOrder", orderParm);
			if (result.getErrCode() < 0) {
				System.out.println("医疗卡撤销医嘱操作失败");
			}
		} else {
			System.out.println("没有要执行的医嘱操作");
		}
	}
	
	/**
	 * 医疗卡保存
	 * 
	 * @param FLG
	 *            String 执行扣款
	 * @param FLG
	 *            double 总费用
	 * @return boolean ====================pangben 20110915 flg ：false 没有添加执行的医嘱
	 *         true 有添加或删除的医嘱 boolean flg 医生操作修改医嘱使用 true 删除医嘱收费操作 false 添加修改医嘱
	 *         收费
	 * @throws Exception 
	 */
	private boolean onEktSave(TParm orderParm, TParm ektSumExeParm) throws Exception {
		int type = 0;
		// 先执行暂存医嘱， 获得医疗卡中历史记录判断此次操作医生是否修改医嘱
		TParm parm = new TParm();
		// TParm detailParm = null;
		// 如果使用医疗卡，并且扣款失败，则返回不保存
		if (EKTIO.getInstance().ektSwitch()) { // 医疗卡开关，记录在后台config文件中
			parm = onOpenCard(orderParm, ektSumExeParm);
			if (parm == null) {
				odoMainControl.messageBox("E0115");
				return false;
			}
			type = parm.getInt("OP_TYPE");
			if (type == 3) {
				odoMainControl.messageBox("E0115");
				return false;
			}
			if (type == -1) {
				odoMainControl.messageBox("读卡错误!");
				return false;
			}
			if (type == 5) {
				return false;
			}
			odoMainControl.odo.setTredeNo(parm.getValue("TRADE_NO"));
			tredeNo = parm.getValue("TRADE_NO");
			// System.out.println("医疗卡保存入参" + parm);
			if (parm.getErrCode() < 0) {
				odoMainControl.messageBox("E0005");
				return false;
			}
			if (Operator.getSpcFlg().equals("Y")
					&& ektSumExeParm.getValue("PHA_RX_NO").length() > 0) {
//				odoMainControl.odoMainSpc.saveSpcOpdOrder(ektSumExeParm.getValue("PHA_RX_NO"));//物联网保存医嘱
				odoMainControl.odoMainSpc.onSendInw(ektSumExeParm,true);//==pangben 2013-12-18
			}
			if (null != parm.getValue("OPD_UN_FLG")
					&& parm.getValue("OPD_UN_FLG").equals("Y")) {
				TParm tempParm = new TParm();
				tempParm.setData("CASE_NO", odoMainControl.caseNo);
				// parm.setData("MR_NO", getValue("MR_NO"));
				TParm reg = OPDAbnormalRegTool.getInstance().selectRegForOPD(
						tempParm);
				odoMainOpdOrder.wc = ODOMainOpdOrder.W; // 默认为西医
				odoMainControl.odoMainReg.initOpd(reg, 0);
			} else {
				// 调用HL7
				odoMainControl.odoMainOther.sendHL7Mes();
			}
			double reduceAmt = 0.00;
			String re = EKTIO.getInstance().check(tredeNo, odoMainControl.odoMainReg.reg.caseNo(),reduceAmt);
			if (re != null && re.length() > 0) {
				odoMainControl.messageBox_(re);
				odoMainControl.messageBox_("请马上与信息中心联系");
				// deleteLisPosc = false;
				odoMainOpdOrder.onExeFee();
				return false;
			}
		} else {
			odoMainControl.messageBox_("医疗卡接口未开启");
			// deleteLisPosc = false;
			return false;
		}
		// 收费成功重新刷新当前病患
		// onClear();
		//ektDeleteOrder = false;// 删除可以执行
		//isFee = false;// 执行收费以后不可以再次执行收费
		//deleteLisPosc = false;
		odoMainOpdOrder.onExeFee();
		return true;
	}

	@Override
	public boolean onSave(TParm orderParm, TParm sumExeParm) throws Exception {
		// TODO Auto-generated method stub
		return onEktSave(orderParm, sumExeParm);
	}
	
	/**
	 * 打开医疗卡 =====pangben 2011-12-16 flg ：false 没有添加执行的医嘱 true 有添加或删除的医嘱
	 * orderParm 需要操作的医嘱 ：删除 、添加、 修改
	 * 
	 * @return TParm
	 */
	public TParm onOpenCard(TParm orderOldParm, TParm orderParm) {
		if (odoMainControl.odo == null) {
			return null;
		}
		TParm unParm = new TParm();
		if (orderOldParm == null) {
			odoMainControl.messageBox("没有需要操作的医嘱");
			unParm.setData("OP_TYPE", 5);
			return unParm;
		}
		if(orderParm.getValue("OP_FLG").length()>0 && orderParm.getInt("OP_FLG")==5){
			odoMainControl.messageBox("没有需要操作的医嘱");
			unParm.setData("OP_TYPE", 5);
			return unParm;
		}
		// 准备送入医疗卡接口的数据
		// 判断此次操作的医嘱在数据库中是否已经存在，如果存在,在执行收费操作时，判断医疗卡中金额是否充足
		// 如果金额不足，执行此处方签所收费的医嘱退还医疗卡中
		TParm updateParm=orderParm.getParm("updateParm");
		boolean unFlg = updateOrderParm(updateParm, orderOldParm, unParm);
		TParm parm = new TParm();
		boolean isDelOrder = false;// 执行删除医嘱
		//boolean exeDelOrder = false;// 执行删除医嘱
		String delFlg=orderParm.getValue("DEL_FLG");
		// 如果出现所有医嘱删除也会出现IS_NEW = false 状态 所有需要在执行方法时先查询当前所有医嘱
		// 校验是否发送删除检验检查接口
		if(delFlg.equals("Y")){
			isDelOrder = true;
		}
		orderParm.setData("BUSINESS_TYPE", "ODO");
		parm.setData("CASE_NO",odoMainControl.odoMainReg.reg.caseNo());
		orderParm.setData("REGION_CODE", Operator.getRegion());
		orderParm.setData("MR_NO", odoMainControl.odoMainPat.pat.getMrNo());
		orderParm.setData("NAME", odoMainControl.odoMainPat.pat.getName());
		orderParm.setData("IDNO", odoMainControl.odoMainPat.pat.getIdNo());
		orderParm.setData("SEX", odoMainControl.odoMainPat.pat.getSexCode() != null
				&& odoMainControl.odoMainPat.pat.getSexCode().equals("1") ? "男" : "女");
		// 送医疗卡，返回医疗卡的回传值
		orderParm.setData("INS_FLG", "N");// 医保卡操作
		orderParm.setData("UN_FLG", unFlg ? "Y" : "N");// 医生修改的医嘱超过医疗卡金额执行的操作
		orderParm.setData("unParm", unParm.getData());// 获得执行修改的医嘱
		if (null != orderOldParm.getValue("OPBEKTFEE_FLG")
				&& orderOldParm.getValue("OPBEKTFEE_FLG").equals("Y")) {
			orderParm.setData("OPBEKTFEE_FLG", "Y");
		}
		//直接收费操作如果有修改的收费医嘱 不能执行取消操作
		if(null == orderOldParm.getValue("OPBEKTFEE_FLG")
				|| orderOldParm.getValue("OPBEKTFEE_FLG").length()<=0){
			if(unFlg)
				orderParm.setData("OPBEKTFEE_FLG", "Y");
		}

		odoMainEkt.ektReadParm = EKTIO.getInstance().TXreadEKT();
		if (null == odoMainEkt.ektReadParm || odoMainEkt.ektReadParm.getErrCode() < 0
				|| null == odoMainEkt.ektReadParm.getValue("MR_NO")) {
			parm.setData("OP_TYPE", 5);
			odoMainControl.messageBox("医疗卡读卡有误。");
			odoMainControl.setValue("LBL_EKT_MESSAGE", "未读卡");//====pangben 2013-5-3添加读卡
			odoMainEkt.ekt_lable.setForeground(OdoMainControl.RED);//======yanjing 2013-06-14设置读卡颜色
			return parm;
		}else{
			odoMainControl.setValue("LBL_EKT_MESSAGE", "已读卡");//====pangben 2013-5-3添加读卡
			odoMainEkt.ekt_lable.setForeground(OdoMainControl.GREEN);//======yanjing 2013-06-14设置读卡颜色
		}
		if (!odoMainEkt.ektReadParm.getValue("MR_NO").equals(odoMainControl.getValue("MR_NO"))) {
			odoMainControl.messageBox("病患信息不符,此医疗卡病患名称为:"
					+ odoMainEkt.ektReadParm.getValue("PAT_NAME"));
			odoMainEkt.ektReadParm = null;
			parm.setData("OP_TYPE", 5);
			return parm;
		}
		int type=0;
		//parm.setData("BILL_FLG", "Y");
		orderParm.setData("ektParm", odoMainEkt.ektReadParm.getData()); // 医疗卡数据
		try {
			parm = EKTNewIO.getInstance().onOPDAccntClient(orderParm, odoMainControl.odo.getCaseNo(),
					odoMainControl);
		} catch (Exception e) {
			System.out.println("医生站收费出现问题::::"+e.getMessage());
		}finally{
			type = parm.getInt("OP_TYPE");
			TParm delExeParm=orderParm.getParm("delExeParm");//存在收费删除的医嘱
			if (parm == null || type == 3 || type == -1 || type == 5) {
				if(delExeParm.getCount("ORDER_CODE")>0){
					concelDeleteOrder(delExeParm, orderOldParm, isDelOrder);// 删除医嘱选择取消操作
					odoMainEkt.ektExeConcel=true;//删除撤销操作
				}
			}
		}
		if (type == 6) {
			odoMainControl.messageBox("没有需要操作的医嘱");
			parm.setData("OP_TYPE", 5);
			return parm;
		}
		if(null!=parm.getValue("OPD_UN_FLG") && parm.getValue("OPD_UN_FLG").equals("Y")){//修改医嘱操作,金额不足 将此收费交易号的所有医嘱退还回去，变成未收费状态
			orderParm.setData("newParm",parm.getParm("unParm").getData());
		}
		// 得到收费项目
		odoMainControl.odoMainOther.sendHL7Parm = orderParm.getParm("hl7Parm");
		//hl7Temp(checkParm);
		// 删除数据操作时不使用只有修改医嘱操作时使用
		parm.setData("orderParm", orderParm.getData());// 需要操作的医嘱
		return parm;
	}

	/**
	 * 
	 */
	@Override
	public void onReadCard(OdoMainControl odoMainControl) throws Exception {
		// TODO Auto-generated method stub
		onInit(odoMainControl);
		onReadCard();
	}
	
	/**
	 * 医疗卡读卡方法
	 * @throws Exception
	 */
	private void onReadCard() throws Exception{
		odoMainEkt.isReadEKT = true;
		odoMainEkt.ektReadParm = readCard();
		if (odoMainEkt.ektReadParm.getErrCode() < 0) {
			odoMainControl.messageBox("医疗卡读卡有误。");
			return;
		}
		// 执行医疗卡操作，判断是否已经可以使用医疗卡
		boolean isMrNoNull = StringUtil
				.isNullString((String) odoMainControl.getValue("MR_NO"));
		if (null == odoMainControl.caseNo || isMrNoNull) {
//			odoMainControl.messageBox("请选择一个病患");
			return;
		}		
//		if (!odoMainEkt.ektReadParm.getValue("MR_NO").equals(odoMainControl.getValue("MR_NO"))) {
//			odoMainControl.messageBox("病患信息不符,此医疗卡病患名称为:"
//					+ odoMainEkt.ektReadParm.getValue("PAT_NAME"));
//			// 当现在有病人的时候 读医疗卡只是用来对比 卡片是否属于该病人
//			odoMainEkt.ektReadParm.setData("SEX",
//					odoMainEkt.ektReadParm.getValue("SEX_CODE").equals("1") ? "男" : "女");
//			odoMainControl.openDialog(URLEKTINFOUI, odoMainEkt.ektReadParm);
//			odoMainEkt.ektReadParm = null;
//			return;
//		}
		
		TParm parm = new TParm();
		parm.setData("CASE_NO", odoMainControl.caseNo);
		// parm.setData("MR_NO", getValue("MR_NO"));
		TParm reg = OPDAbnormalRegTool.getInstance().selectRegForOPD(parm);
		if (reg.getCount("CASE_NO") > 1) {
			TParm re = (TParm) odoMainControl.openDialog(
					URLOPDREGCHOOSE, reg);
			if (re == null)
				return;
			TParm result = new TParm();
			result.setRowData(0, re);
			odoMainOpdOrder.wc = ODOMainOpdOrder.W; // 默认为西医
			// ============xueyf modify 20120227 start
			if (isMrNoNull) {
				odoMainControl.odoMainReg.initOpd(result, 0);
			}
		} else if (reg.getCount("CASE_NO") == 1 && isMrNoNull) {
					odoMainOpdOrder.wc = ODOMainOpdOrder.W; // 默认为西医
			odoMainControl.odoMainReg.initOpd(reg, 0);
			// ============xueyf modify 20120227 stop
		}
		odoMainControl.setValue("LBL_EKT_MESSAGE", "已读卡");//====pangben 2013-3-19 添加读卡
		odoMainEkt.ekt_lable.setForeground(OdoMainControl.GREEN);//======yanjing 2013-06-14设置读卡颜色
	}
	
	/**
	 * 暂存
	 * @param ektOrderParm
	 * @param ektOrderParmone
	 * @param ektSumExeParm
	 * @throws Exception
	 */
	@Override
	//斯巴达
	public void onTempSave(TParm ektOrderParm, TParm ektOrderParmone, TParm ektSumExeParm) throws Exception{
		if (null == odoMainEkt.ektReadParm || odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0) {
		} else {
			// 判断此次操作的医嘱在数据库中是否已经存在，如果存在,在执行收费操作时，判断医疗卡中金额是否充足
			// 如果金额不足，执行此处方签所收费的医嘱退还医疗卡中
			if (null == ektOrderParm) {
				//点击暂存按钮 如果已收费的医嘱修改金额 执行 修改医嘱扣款或退款操作
				if(updateOrderUnConcle(ektOrderParmone, ektSumExeParm))
					ektOrderParm=ektOrderParmone;
			}
		}
		// 删除\修改医嘱收费
		if (null!=ektOrderParm){
			//斯巴达
			//====zhangp 20131202
			if(odoMainEkt.preDebtFlg && !(null == odoMainEkt.ektReadParm || odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0)){
				
			}else{
				onEktSave(ektOrderParm, ektSumExeParm);//=======pangben 2013-3-19 修改金额不足也打印处方签
			}
			onEktSave(ektOrderParm, ektSumExeParm);//=======pangben 2013-3-19 修改金额不足也打印处方签
		}
	}

	@Override
	public void onSave() throws Exception {
		// TODO Auto-generated method stub
			odoMainEkt.ektOrderParmone = new TParm();
			odoMainEkt.ektSumExeParm = new TParm();
			// parm.setData("REGION_CODE", Operator.getRegion());
			odoMainEkt.ektOrderParmone.setData("CASE_NO", odoMainControl.odoMainReg.reg.caseNo());
			// 获得此次操作医疗卡所有的医嘱 在执行删除所有医嘱时使用
			odoMainEkt.ektOrderParmone = OrderTool.getInstance().selDataForOPBEKT(
					odoMainEkt.ektOrderParmone);
			if (odoMainEkt.ektOrderParmone.getErrCode() < 0) {
				return;
			}
			// 获得此次医疗卡操作所有需要执行的医嘱=====pangben 2012-4-14
			odoMainEkt.ektSumExeParm = odoMainControl.odo.getOpdOrder().getEktParam(odoMainEkt.ektOrderParmone);
	}
}
