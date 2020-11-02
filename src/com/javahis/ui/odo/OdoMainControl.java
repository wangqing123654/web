package com.javahis.ui.odo;

import java.awt.Color;
import jdo.odo.ODO;
import jdo.opb.OPB;
import jdo.opb.OPBTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TWindow;
import com.sun.awt.AWTUtilities;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站主档
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站主档控制类
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class OdoMainControl extends TControl {

	public ODO odo;// odo 对象
	public OPB opb;// opb对象
	public final static String BANKPAYTYPE = "com.javahis.ui.odo.BANKPay";
	public final static String CASHPAYTYPE = "com.javahis.ui.odo.CASHPay";
	public final static String EKTPAYTYPE = "com.javahis.ui.odo.EKTpay";
	public static final Color GREEN = new Color(0, 125, 0);// 颜色设置yanjing 20130614
	public static final Color RED = new Color(255, 0, 0); // liudy 既往史，过敏史变换为红色
	public TMovePane mp;// 左右的MOVEPANEL
	private static final String NULLSTR = "";
	public String caseNo;// 就诊号====pangben 20110914
	public boolean isEng;// 是否英语
	public String serviceLevel = "";// 服务等级
	public ODOMainOpdOrder odoMainOpdOrder;
	public ODOMainPat odoMainPat;
	public ODOMainReg odoMainReg;
	public ODOMainEkt odoMainEkt;
	public ODOMainTmplt odoMainTmplt;
	public ODOMainTjIns odoMainTjIns;
	public ODOMainOther odoMainOther;
	public ODOMainSpc odoMainSpc;
	public ODOMainReasonbledMed odoMainReasonbledMed;
	public IPayType pay;
	private static final String EN = "en";

	/**
	 * 初始化
	 */
	public void onInit() {
		try {
			odoMainOther = new ODOMainOther(this);
			odoMainReasonbledMed = new ODOMainReasonbledMed(this);
			odoMainOpdOrder = new ODOMainOpdOrder(this);// 初始化医生站医嘱对象
			odoMainPat = new ODOMainPat(this);// 初始化医生站病患对象
			odoMainReg = new ODOMainReg(this);// 初始化医生站挂号对象
			odoMainEkt = new ODOMainEkt(this);// 初始化医生站医疗卡对象
			odoMainTmplt = new ODOMainTmplt(this);// 初始化医生站模版对象
			odoMainTjIns = new ODOMainTjIns(this);// 初始化医生站天津医保对象
			odoMainSpc = new ODOMainSpc(this);
			// 初始化支付方式为现金
			pay = (IPayType) Class.forName(CASHPAYTYPE).newInstance();
			pay.onReadCard(this);
			odoMainOther.onInit();
			odoMainOpdOrder.onInit();
			odoMainPat.onInit();
			odoMainReg.onInit();
			odoMainEkt.onInit();
			odoMainTjIns.onInit();
			odoMainTmplt.onInit();
			odoMainSpc.onInit();
			odoMainReasonbledMed.onInit();
			callFunction("UI|REASSURE_FLG|setEnabled", false);
			super.onInit();
			mp = (TMovePane) callFunction("UI|MOV_MAIN|getThis");
			mp.onDoubleClicked(false);
			isEng = EN.equalsIgnoreCase(Operator.getLanguage());
			onInitEvent();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}

	/**
	 * 注册控件的事件
	 */
	public void onInitEvent() throws Exception {
		odoMainOpdOrder.onInitEvent();
		odoMainOther.onInitEvent();
	}

	/**
	 * 初始化所有panel
	 * 
	 * @throws Exception
	 */
	public void initPanel() throws Exception {
		if (odo == null)
			return;
		odoMainOther.initPanel();// 初始化其他对象panel
		odoMainOpdOrder.initPanel();// 初始化医嘱对象panel
	}

	/**
	 * 清空
	 */
	public void onClear() {
		try {
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|tempsave|setEnabled", true);
			odo = null;
			odoMainPat.onPatClear();// 医生站病患对象清空
			odoMainReg.onRegClear();// 医生站挂号对象清空
			odoMainOther.onOtherClear();// 医生站其他对象清空
			odoMainEkt.onEktClear();// 医生站医疗卡对象清空
			odoMainOpdOrder.onOpdClear();// 医生站医嘱对象清空
			// 初始化支付方式为现金
			pay = (IPayType) Class.forName(CASHPAYTYPE).newInstance();
			pay.onReadCard(this);
			// 门诊医生站清空操作 获得控件名称
			for (int i = 0; i < OPBTool.controlName.length; i++) {// ====pangben
																	// 2013-5-2
				this.setValue(OPBTool.controlName[i], NULLSTR);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		super.onClosing();
		try {
			odoMainPat.unLockPat();
			odoMainReg.SynLogin("0");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 退出叫号登陆
		return true;
	}

	/**
	 * 清空系统剪贴板
	 */
	public void onClearMenu() throws Exception {
		CopyOperator.clearComList();
	}

	/**
	 * 设置语种
	 * 
	 * @param language
	 *            String
	 */
	public void onChangeLanguage(String language) {
		try {
			isEng = EN.equalsIgnoreCase(language);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 保存
	 * 
	 * @throws Exception
	 */
	public void onSave() throws Exception {
		// if(odoMainEkt.isReadedCard()){
		// return;
		// }
		acceptForSave();
		if (odoMainOther.diagCanSave()) {
			return;
		}

		// add by huangtt 20141208
		if (odoMainReg.canSave()) {
			this.messageBox_(ODOMainReg.MEGOVERTIME);
			return;
		}

		if (!odoMainOpdOrder.canSave()) {
			this.messageBox_(ODOMainOpdOrder.MEGOVERTIME);
			return;
		}

		if (odoMainOther.onSaveSubjrec())
			return;
		if (odoMainReg.onSave())
			return;
		if (!odoMainOpdOrder.checkSave())// 校验遗嘱
			return;
		if (!odoMainOpdOrder.getTempSaveParm())// 校验数据
			return;

		odoMainOpdOrder.odoRxMed.getTable().setDSValue();

		pay.onSave();
		odoMainOpdOrder.odoRxMed.onSortRx(false);
		TParm orderParm = new TParm();
		TParm ctrlParm = new TParm();
		TParm chnParm = new TParm();
		TParm exaParm = new TParm();
		TParm opParm = new TParm();
		if (odo.getOpdOrder().isModified()) {
			orderParm = odo.getOpdOrder().getModifiedOrderRx();
			ctrlParm = odo.getOpdOrder().getModifiedCtrlRx();
			chnParm = odo.getOpdOrder().getModifiedChnRx();
			exaParm = odo.getOpdOrder().getModifiedExaRx();
			odo.getOpdOrder().updateMED(odo);
			opParm = odo.getOpdOrder().getModifiedOpRx();
			if (orderParm.getCount() > 0) {
				if (!odo.getOpdOrder().isOrgAvalible(odoMainOpdOrder.phaCode)) {
					messageBox("E0117");
				}
			}
		}
		odoMainOpdOrder.onSave(exaParm);
		if (!odoMainEkt.preDebtFlg)
			pay.isExeFee(odoMainEkt.ektOrderParmone, odoMainEkt.ektSumExeParm);
		odoMainTmplt.onSaveMemPackage();

		odoMainReg.onSaveReportStatus();

		// 通过reg和caseNo得到pat
		opb = OPB.onQueryByCaseNo(odoMainReg.reg);// ===============pangben 20110914
		messageBox("P0001"); 
		odoMainOther.onSave(orderParm, ctrlParm, chnParm, exaParm, opParm);
		odoMainOther.onDiagPnChange();
	}

	/**
	 * 挂号对象筛选病患
	 * 
	 * @param type
	 *            String 表示 那个控件调用该方法
	 */
	public void onSelectPat(String type) throws Exception {
		odoMainReg.onSelectPat(type);
	}

	/**
	 * 挂号对象代诊科别点击时间，将代诊医师combo置为可用，并初始化代诊医师combo
	 */
	public void onInsteadDept() throws Exception {
		odoMainReg.onInsteadDept();
	}

	/**
	 * 挂号对象重叫
	 */
	public void onReCallNo() throws Exception {
		odoMainReg.onReCallNo();
	}

	/**
	 * 挂号对象下一个
	 */
	public void onNextCallNo() throws Exception {
		odoMainReg.onNextCallNo();
	}

	/**
	 * 病历查询 add by huangtt 20150205
	 */
	public void onOpdEmrQuery() {
		odoMainReg.onOpdEmrQuery();
	}

	/**
	 * 挂号对象点选病人，初始化医生站
	 * 
	 * @throws Exception
	 */
	public void onTablePatDoubleClick() throws Exception {
		odoMainReg.onTablePatDoubleClick();
		callFunction("UI|save|setEnabled", true);
		callFunction("UI|tempsave|setEnabled", true);
	}

	/**
	 * 纳入既往史
	 */
	public void onImportMedHistory() throws Exception {
		odoMainOther.onImportMedHistory();
	}

	/**
	 * 诊断中英文显示
	 */
	public void onChnEng() throws Exception {
		odoMainOther.onChnEng();
	}

	/**
	 * 诊断页签双击事件，结构化病历选择模板
	 */
	public void onChangeTemplate() throws Exception {
		odoMainOther.onChangeTemplate();
	}

	/**
	 * LMP点击事件，计算怀孕周数
	 */
	public void onLmp() throws Exception {
		odoMainOther.onLmp();
	}

	/**
	 * 哺乳期点击事件，不能晚于哺乳期结束日期
	 */
	public void onBreastStartDate() throws Exception {
		odoMainOther.onBreastStartDate();
	}

	/**
	 * 哺乳期点击事件，不能晚于哺乳期结束日期
	 */
	public void onBreastEndDate() throws Exception {
		odoMainOther.onBreastEndDate();
	}

	/**
	 * 调用医师常用诊断
	 */
	public void onDrDiag() throws Exception {
		odoMainTmplt.onDrDiag();
	}

	/**
	 * 调用科常用诊断
	 */
	public void onDeptDiag() throws Exception {
		odoMainTmplt.onDeptDiag();
	}

	/**
	 * 显示引用表单界面
	 */
	public void onShowQuoteSheet() throws Exception {
		odoMainTmplt.onShowQuoteSheet();
	}

	/**
	 * 弹出申请单
	 */
	public void onEmr() throws Exception {
		odoMainTmplt.onEmr();
	}

	/**
	 * 住院预约
	 */
	public void onPreDate() throws Exception {
		odoMainOther.onPreDate();
	}

	/**
	 * 急诊留观
	 */
	public void onErd() throws Exception {
		odoMainOther.onErd();
	}

	/**
	 * 就诊记录
	 */
	public void onCaseHistory() throws Exception {
		odoMainTmplt.onCaseHistory();
	}

	/**
	 * 检验报告
	 */
	public void onLisReport() throws Exception {
		odoMainOther.onLisReport();
	}

	/**
	 * 检查报告
	 */
	public void onRisReport() throws Exception {
		odoMainOther.onRisReport();
	}

	/**
	 * 调用传染病报告卡
	 */
	public void onContagionReport() throws Exception {
		odoMainOther.onContagionReport();
	}

	/**
	 * 调用体温单
	 */
	public void onBodyTemp() throws Exception {
		odoMainOther.onBodyTemp();
	}

	/**
	 * 调用留观病历
	 */
	public void onErdSheet() throws Exception {
		odoMainOther.onErdSheet();
	}

	/**
	 * 调用医嘱单
	 */
	public void onOrderSheet() throws Exception {
		odoMainOther.onOrderSheet();
	}

	/**
	 * 手术申请
	 */
	public void onOpApply() throws Exception {
		odoMainOther.onOpApply();
	}

	/**
	 * 手术记录
	 */
	public void onOpRecord() throws Exception {
		odoMainOther.onOpRecord();
	}

	/**
	 * 存模板
	 */
	public void onSaveTemplate() throws Exception {
		odoMainOther.onSaveTemplate();
	}

	/**
	 * 过敏类型改变
	 * 
	 * @param type
	 *            String
	 */
	public void onAllg(String type) throws Exception {
		odoMainOther.onAllg(type);
	}

	/**
	 * 病历浏览 ==========pangben modify 20110706
	 */
	public void onShow() throws Exception {
		odoMainOther.onShow();
	}

	/**
	 * 打印病历
	 * 
	 * @return Object
	 */
	public Object onPrintCase() throws Exception {
		return odoMainOther.onPrintCase();
	}

	/**
	 * 复诊病历 add by huangjw 20150108
	 * 
	 * @return
	 * @throws Exception
	 */
	public void onRePrintCase() throws Exception {
		odoMainOther.onRePrintCase();
	}

	/**
	 * 调用补打界面
	 */
	public void onCaseSheet() throws Exception {
		odoMainOther.onCaseSheet("");
	}

	/**
	 * 身份1点击事件，将身分2和身份3清空
	 */
	public void onCtz1() throws Exception {
		odoMainPat.onCtz1();
	}

	/**
	 * 身份2点击事件，与身份1和身份3比较，不能和他们的值相同
	 */
	public void onCtz2() throws Exception {
		odoMainPat.onCtz2();
	}

	/**
	 * 身份3点击事件，与身份1和身份2比较，不能和他们的值相同
	 */
	public void onCtz3() throws Exception {
		odoMainPat.onCtz3();
	}

	/**
	 * 病生理1点击事件，清空病生理2、3
	 */
	public void onPat1() throws Exception {
		odoMainPat.onPat1();
	}

	/**
	 * 病生理2点击事件，与病生理1、3比较，不能和他们的值相同
	 */
	public void onPat2() throws Exception {
		odoMainPat.onPat2();
	}

	/**
	 * 病生理3点击事件，与病生理1、2比较，不能和他们的值相同
	 */
	public void onPat3() throws Exception {
		odoMainPat.onPat3();
	}

	/**
	 * 调用病患详细信息界面
	 */
	public void onPatDetail() throws Exception {
		odoMainPat.onPatDetail();
	}

	/**
	 * 合理用药按钮
	 */
	public void onResonablemed() throws Exception {
		odoMainReasonbledMed.onResonablemed();
	}

	/**
	 * 病患列表展开事件
	 */
	public void onPat() throws Exception {
		odoMainReg.onPat();
	}

	/**
	 * 预约挂号
	 */
	public void onReg() throws Exception {
		odoMainReg.onReg();
	}

	/**
	 * 预约挂号
	 */
	public void onCrmDr() throws Exception {
		odoMainReg.onCrmDr();
	}

	/**
	 * 非常态门诊
	 * 
	 * @throws Exception
	 */
	public void onAbnormalReg() throws Exception {
		odoMainReg.onAbnormalReg();
	}

	/**
	 * 门特特殊情况使用
	 */
	public void onSpecialCase() throws Exception {
		odoMainTjIns.onSpecialCase();
	}

	/**
	 * 就诊序号回车查询事件
	 * 
	 * @throws Exception
	 */
	public void onQueNo() throws Exception {
		odoMainReg.onQueNo();
	}

	/**
	 * xueyf 2012-02-28 医保门特处方查询
	 */
	public void onINSDrQuery() throws Exception {
		odoMainTjIns.onINSDrQuery();
	}

	/**
	 * 调用医师常用医嘱
	 */
	public void onDrOrder() throws Exception {
		odoMainTmplt.onDrOrder();
	}

	/**
	 * 调用科室常用医嘱
	 */
	public void onDeptOrder() throws Exception {
		odoMainTmplt.onDeptOrder();
	}

	/**
	 * 调用科常用模板
	 */
	public void onDeptPack() throws Exception {
		odoMainTmplt.onDeptPack();
	}

	/**
	 * 调用医师常用模板
	 */
	public void onDrPack() throws Exception {
		odoMainTmplt.onDrPack();
	}

	/**
	 * 医嘱页签点击事件
	 */
	public void onChangeOrderTab() throws Exception {
		odoMainOpdOrder.onChangeOrderTab();
	}

	/**
	 * 保存前使每个TABLE都没有编辑状态
	 */
	public void acceptForSave() throws Exception {
		odoMainOther.acceptOtherForSave();
		odoMainOpdOrder.acceptOpdOderForSave();
	}

	/**
	 * 医疗卡读卡
	 */
	public void onEKT() throws Exception {
		try {
			Object ob = Class.forName(EKTPAYTYPE).newInstance();
			pay = (IPayType) ob;
			pay.onReadCard(this);
			if (odo == null || !odoMainEkt.ektReadParm.getValue("MR_NO").equals(odo.getMrNo())) {
				odoMainReg.afterRead();
			}

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 新增一张处方签，更新combo，并初始table
	 * 
	 * @param rxType
	 *            处方类型
	 * @param rxName
	 *            combo名
	 * @param tableName
	 *            table名
	 */
	public void onAddOrderList(String rxType, String rxName, String tableName) throws Exception {
		odoMainOpdOrder.onAddOrderList(rxType, rxName, tableName);
	}

	/**
	 * 删除整张处方签
	 * 
	 * @param rxType
	 *            处方类型
	 */
	public void onDeleteOrderList(int rxType) throws Exception {
		odoMainOpdOrder.onDeleteOrderList(rxType);
	}

	/**
	 * 泰心门诊医疗卡收费 只能操作医疗卡收费操作
	 */
	public void onFee() throws Exception {
		odoMainEkt.onFee();
	}

	/**
	 * 西医诊断radio注记
	 */
	public void onWFlg() throws Exception {
		odoMainOpdOrder.onWFlg();
	}

	/**
	 * 中医诊断radio注记
	 */
	public void onCFlg() throws Exception {
		odoMainOpdOrder.onCFlg();
	}

	/**
	 * 商品名方法
	 * 
	 * @param tag
	 *            String
	 * @param checkBox
	 *            String
	 */
	public void onGoods(String tag, String checkBox) throws Exception {
		odoMainOpdOrder.onGoods(tag, checkBox);
	}

	/**
	 * 查询就诊病患金额 ======pangben 2013-3-28
	 */
	public void onMrSearchFee() throws Exception {
		odoMainEkt.onMrSearchFee();
	}

	/**
	 * 删除指定表格的一行数据
	 */
	public void deleteRow() throws Exception {
		odoMainOpdOrder.deleteRow();
	}

	/**
	 * 诊断、既往史、过敏史TABLE行点击事件
	 * 
	 * @param tag
	 *            String
	 */
	public void onTableClick(String tag) throws Exception {
		odoMainOpdOrder.onTableClick(tag);
	}

	/**
	 * 暂存，打印处置、检验检查通知单
	 */
	public void onTempSave() throws Exception {
		// add by huangtt 20141208
		if (odoMainReg.canSave()) {
			this.messageBox_(ODOMainReg.MEGOVERTIME);
			return;
		}

		odoMainOpdOrder.onTempSave(null, 1); // modigy by huangtt 20141118 1表示打印处方签
		odoMainReg.onSaveReportStatus();
		odoMainOther.onDiagPnChange();
	}

	/**
	 * combo点选事件,根据处方号初始化table
	 * 
	 * @param rxType
	 *            处方类型
	 */
	public void onChangeRx(String rxType) throws Exception {
		odoMainOpdOrder.onChangeRx(rxType);
	}

	/**
	 * 引用套餐 =======zhangp 2014
	 */
	public void onPack() throws Exception {
		odoMainTmplt.onPack();
	}

	/**
	 * 右击MENU弹出事件
	 * 
	 * @param tableName
	 *            String
	 */
	public void showPopMenu(String tableName) throws Exception {
		odoMainOpdOrder.showPopMenu(tableName);
	}

	/**
	 * 右击MENU显示集合医嘱事件
	 */
	public void onOrderSetShow() throws Exception {
		odoMainOpdOrder.onOrderSetShow();
	}

	/**
	 * 右击MENU显示诊疗项目细项事件
	 */
	public void onOpShow() throws Exception {
		odoMainOpdOrder.onOpShow();
	}

	/**
	 * 右击MENU显示SYS_FEE事件
	 */
	public void onSysFeeShow() throws Exception {
		odoMainOpdOrder.onSysFeeShow();
	}

	/**
	 * 合理用药--药品信息查询
	 */
	public void onQueryRationalDrugUse() throws Exception {
		odoMainReasonbledMed.onQueryRationalDrugUse();
	}

	/**
	 * 右击MENU显示使用说明-duzhw 20131209
	 */
	public void useDrugMenu() throws Exception {
		odoMainOpdOrder.useDrugMenu();
	}

	/**
	 * 
	 * @param rxKind
	 *            String 处方类型
	 * @param tableName
	 *            String TABLE名
	 */
	public void onEditAll(String rxKind, String tableName) throws Exception {
		odoMainOpdOrder.onEditAll(rxKind, tableName);
	}

	/**
	 * 预开检查查询 yanjing 20140331
	 * 
	 * @throws Exception
	 */
	public void onPreOrder() throws Exception {
		odoMainOpdOrder.onPreOrder();
	}

	/**
	 * 备血申请
	 */
	public void onBXResult() throws Exception {
		odoMainOther.onBXResult();
	}

	/**
	 * 会诊申请
	 */
	public void onConsApply() throws Exception {
		odoMainOther.onConsApply();
	}

	/**
	 * 患者保险信息 add by sunqy 20140516
	 */
	public void onInsureInfo() {
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("EDIT", "N");
		this.openDialog("%ROOT%\\config\\mem\\MEMInsureInfo.x", parm);
	}

	/**
	 * 报告进度 add by huangjw 20140812
	 */
	public void onPlanrep() {
		Pat pat = Pat.onQueryByMrNo(odo.getMrNo());
		Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
		TParm parm = new TParm();
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("CASE_NO", reg.caseNo());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("DEPT_CODE", reg.getDeptCode());
		parm.setData("CLINICROOM_NO", reg.getClinicroomNo());
		parm.setData("DR_CODE", reg.getDrCode());
		parm.setData("NUR_FLG", "N");// 根据这个标记 来判断是否 就诊科室与就诊诊间默认为空 add by haungjw 20150714
		this.openDialog("%ROOT%\\config\\onw\\ONWPlanReport.x", parm);
	}

	/**
	 * 提取体征信息 add by huangtt 20141125
	 * 
	 * @throws Exception
	 */
	public void getPHY() throws Exception {

		odoMainOther.onPHY();

	}

	/**
	 * 插入上下标功能
	 */
	public void onInsertMarkText() {
		odoMainOther.onInsertMarkText();
	}

	/**
	 * 上下标文本属性
	 */
	public void onMarkTextProperty() {
		odoMainOther.getmWord().onOpenMarkProperty();
	}
	//

	/**
	 * “特殊字符”单击事件
	 */
	public void onInsertSpecialChars() {
		// odoMainOther.onSpecialChars();
		if (!odoMainOther.getmWord().canEdit()) {
			this.messageBox("先选择病例模版!");
			return;
		}
		TParm parm = new TParm();
		parm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) this.openWindow("%ROOT%\\config\\emr\\EMRSpecialChars.x", parm, true);
		// window.setX(ImageTool.getScreenWidth() - window.getWidth());
		// window.setY(0);
		TPanel wordPanel = ((TPanel) this.getComponent("DIAGNOSISPANEL"));
		window.setX(wordPanel.getX() + 10);
		window.setY(130);
		window.setVisible(true);
		AWTUtilities.setWindowOpacity(window, 0.8f);
	}

	public void onReturnContent(String value) {
		if (!odoMainOther.getmWord().pasteString(value)) {
			// 执行失败
			this.messageBox("E0005");
		}
	}

	/**
	 * 分方
	 */
	public void onSortRx() throws Exception {
		odoMainOpdOrder.odoRxMed.onSortRx(true);
	}

	public void onMaternalAction() {
		odoMainOther.onMaternalAction();
	}

	public void onChnChange(String fieldName, String type) throws Exception {
		odoMainOpdOrder.odoRxChn.onChnChange(fieldName, type);
	}

	/**
	 * 取消看诊
	 */
	public void onCancelConsult() throws Exception {
		if (!odoMainOpdOrder.canSave()) {
			this.messageBox_(ODOMainOpdOrder.MEGOVERTIME);
			return;
		}

		if (this.messageBox("询问", "是否取消看诊", 2) == 0) {

			String sql2 = "SELECT RX_NO,AR_AMT ,ORDER_CODE FROM OPD_ORDER " + " WHERE CASE_NO = '" + odo.getCaseNo()
					+ "' AND MR_NO = '" + odo.getMrNo() + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql2));
//			System.out.println("parm:::::"+parm);
			Boolean flg = this.isHaveOthersRxNo(parm);
			if (flg==true) {
				messageBox("医生开过医嘱,无法退号！");
				return;
			}
			
			String delPreSql = "DELETE OPD_ORDER WHERE CASE_NO = '" + odo.getCaseNo() + "' AND  RX_NO = 'CLINIC_FEE'";
			TParm delPreParm = new TParm(TJDODBTool.getInstance().update(delPreSql));
			
			String sql3 ="UPDATE REG_PATADM SET SEE_DR_FLG='N',OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' WHERE CASE_NO='" + odo.getCaseNo()+"' AND MR_NO = '" + odo.getMrNo() + "'";
			TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql3));
			
			if (delPreParm.getErrCode() < 0) {
				messageBox("删除诊疗费失败");
				return;
			} else {
				messageBox("取消诊疗费成功");
				return;
			}
			
		} else {
			return;
		}
	}
	
	/**
	 * 
	 * @return true 有 false 没有
	 */
	public boolean isHaveOthersRxNo(TParm p) {
		for (int i = 0; i < p.getCount(); i++) {
			if (!"CLINIC_FEE".equals(p.getValue("RX_NO", i))) {
				return true;
			}
		}
		return false;
	}


}