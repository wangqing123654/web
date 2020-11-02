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
 * Title: ����ҽ������վ����
 * </p>
 * 
 * <p>
 * Description:����ҽ������վ����������
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

	public ODO odo;// odo ����
	public OPB opb;// opb����
	public final static String BANKPAYTYPE = "com.javahis.ui.odo.BANKPay";
	public final static String CASHPAYTYPE = "com.javahis.ui.odo.CASHPay";
	public final static String EKTPAYTYPE = "com.javahis.ui.odo.EKTpay";
	public static final Color GREEN = new Color(0, 125, 0);// ��ɫ����yanjing 20130614
	public static final Color RED = new Color(255, 0, 0); // liudy ����ʷ������ʷ�任Ϊ��ɫ
	public TMovePane mp;// ���ҵ�MOVEPANEL
	private static final String NULLSTR = "";
	public String caseNo;// �����====pangben 20110914
	public boolean isEng;// �Ƿ�Ӣ��
	public String serviceLevel = "";// ����ȼ�
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
	 * ��ʼ��
	 */
	public void onInit() {
		try {
			odoMainOther = new ODOMainOther(this);
			odoMainReasonbledMed = new ODOMainReasonbledMed(this);
			odoMainOpdOrder = new ODOMainOpdOrder(this);// ��ʼ��ҽ��վҽ������
			odoMainPat = new ODOMainPat(this);// ��ʼ��ҽ��վ��������
			odoMainReg = new ODOMainReg(this);// ��ʼ��ҽ��վ�ҺŶ���
			odoMainEkt = new ODOMainEkt(this);// ��ʼ��ҽ��վҽ�ƿ�����
			odoMainTmplt = new ODOMainTmplt(this);// ��ʼ��ҽ��վģ�����
			odoMainTjIns = new ODOMainTjIns(this);// ��ʼ��ҽ��վ���ҽ������
			odoMainSpc = new ODOMainSpc(this);
			// ��ʼ��֧����ʽΪ�ֽ�
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
	 * ע��ؼ����¼�
	 */
	public void onInitEvent() throws Exception {
		odoMainOpdOrder.onInitEvent();
		odoMainOther.onInitEvent();
	}

	/**
	 * ��ʼ������panel
	 * 
	 * @throws Exception
	 */
	public void initPanel() throws Exception {
		if (odo == null)
			return;
		odoMainOther.initPanel();// ��ʼ����������panel
		odoMainOpdOrder.initPanel();// ��ʼ��ҽ������panel
	}

	/**
	 * ���
	 */
	public void onClear() {
		try {
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|tempsave|setEnabled", true);
			odo = null;
			odoMainPat.onPatClear();// ҽ��վ�����������
			odoMainReg.onRegClear();// ҽ��վ�ҺŶ������
			odoMainOther.onOtherClear();// ҽ��վ�����������
			odoMainEkt.onEktClear();// ҽ��վҽ�ƿ��������
			odoMainOpdOrder.onOpdClear();// ҽ��վҽ���������
			// ��ʼ��֧����ʽΪ�ֽ�
			pay = (IPayType) Class.forName(CASHPAYTYPE).newInstance();
			pay.onReadCard(this);
			// ����ҽ��վ��ղ��� ��ÿؼ�����
			for (int i = 0; i < OPBTool.controlName.length; i++) {// ====pangben
																	// 2013-5-2
				this.setValue(OPBTool.controlName[i], NULLSTR);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * �ر��¼�
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
		} // �˳��кŵ�½
		return true;
	}

	/**
	 * ���ϵͳ������
	 */
	public void onClearMenu() throws Exception {
		CopyOperator.clearComList();
	}

	/**
	 * ��������
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
	 * ����
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
		if (!odoMainOpdOrder.checkSave())// У������
			return;
		if (!odoMainOpdOrder.getTempSaveParm())// У������
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

		// ͨ��reg��caseNo�õ�pat
		opb = OPB.onQueryByCaseNo(odoMainReg.reg);// ===============pangben 20110914
		messageBox("P0001"); 
		odoMainOther.onSave(orderParm, ctrlParm, chnParm, exaParm, opParm);
		odoMainOther.onDiagPnChange();
	}

	/**
	 * �ҺŶ���ɸѡ����
	 * 
	 * @param type
	 *            String ��ʾ �Ǹ��ؼ����ø÷���
	 */
	public void onSelectPat(String type) throws Exception {
		odoMainReg.onSelectPat(type);
	}

	/**
	 * �ҺŶ������Ʊ���ʱ�䣬������ҽʦcombo��Ϊ���ã�����ʼ������ҽʦcombo
	 */
	public void onInsteadDept() throws Exception {
		odoMainReg.onInsteadDept();
	}

	/**
	 * �ҺŶ����ؽ�
	 */
	public void onReCallNo() throws Exception {
		odoMainReg.onReCallNo();
	}

	/**
	 * �ҺŶ�����һ��
	 */
	public void onNextCallNo() throws Exception {
		odoMainReg.onNextCallNo();
	}

	/**
	 * ������ѯ add by huangtt 20150205
	 */
	public void onOpdEmrQuery() {
		odoMainReg.onOpdEmrQuery();
	}

	/**
	 * �ҺŶ����ѡ���ˣ���ʼ��ҽ��վ
	 * 
	 * @throws Exception
	 */
	public void onTablePatDoubleClick() throws Exception {
		odoMainReg.onTablePatDoubleClick();
		callFunction("UI|save|setEnabled", true);
		callFunction("UI|tempsave|setEnabled", true);
	}

	/**
	 * �������ʷ
	 */
	public void onImportMedHistory() throws Exception {
		odoMainOther.onImportMedHistory();
	}

	/**
	 * �����Ӣ����ʾ
	 */
	public void onChnEng() throws Exception {
		odoMainOther.onChnEng();
	}

	/**
	 * ���ҳǩ˫���¼����ṹ������ѡ��ģ��
	 */
	public void onChangeTemplate() throws Exception {
		odoMainOther.onChangeTemplate();
	}

	/**
	 * LMP����¼������㻳������
	 */
	public void onLmp() throws Exception {
		odoMainOther.onLmp();
	}

	/**
	 * �����ڵ���¼����������ڲ����ڽ�������
	 */
	public void onBreastStartDate() throws Exception {
		odoMainOther.onBreastStartDate();
	}

	/**
	 * �����ڵ���¼����������ڲ����ڽ�������
	 */
	public void onBreastEndDate() throws Exception {
		odoMainOther.onBreastEndDate();
	}

	/**
	 * ����ҽʦ�������
	 */
	public void onDrDiag() throws Exception {
		odoMainTmplt.onDrDiag();
	}

	/**
	 * ���ÿƳ������
	 */
	public void onDeptDiag() throws Exception {
		odoMainTmplt.onDeptDiag();
	}

	/**
	 * ��ʾ���ñ�����
	 */
	public void onShowQuoteSheet() throws Exception {
		odoMainTmplt.onShowQuoteSheet();
	}

	/**
	 * �������뵥
	 */
	public void onEmr() throws Exception {
		odoMainTmplt.onEmr();
	}

	/**
	 * סԺԤԼ
	 */
	public void onPreDate() throws Exception {
		odoMainOther.onPreDate();
	}

	/**
	 * ��������
	 */
	public void onErd() throws Exception {
		odoMainOther.onErd();
	}

	/**
	 * �����¼
	 */
	public void onCaseHistory() throws Exception {
		odoMainTmplt.onCaseHistory();
	}

	/**
	 * ���鱨��
	 */
	public void onLisReport() throws Exception {
		odoMainOther.onLisReport();
	}

	/**
	 * ��鱨��
	 */
	public void onRisReport() throws Exception {
		odoMainOther.onRisReport();
	}

	/**
	 * ���ô�Ⱦ�����濨
	 */
	public void onContagionReport() throws Exception {
		odoMainOther.onContagionReport();
	}

	/**
	 * �������µ�
	 */
	public void onBodyTemp() throws Exception {
		odoMainOther.onBodyTemp();
	}

	/**
	 * �������۲���
	 */
	public void onErdSheet() throws Exception {
		odoMainOther.onErdSheet();
	}

	/**
	 * ����ҽ����
	 */
	public void onOrderSheet() throws Exception {
		odoMainOther.onOrderSheet();
	}

	/**
	 * ��������
	 */
	public void onOpApply() throws Exception {
		odoMainOther.onOpApply();
	}

	/**
	 * ������¼
	 */
	public void onOpRecord() throws Exception {
		odoMainOther.onOpRecord();
	}

	/**
	 * ��ģ��
	 */
	public void onSaveTemplate() throws Exception {
		odoMainOther.onSaveTemplate();
	}

	/**
	 * �������͸ı�
	 * 
	 * @param type
	 *            String
	 */
	public void onAllg(String type) throws Exception {
		odoMainOther.onAllg(type);
	}

	/**
	 * ������� ==========pangben modify 20110706
	 */
	public void onShow() throws Exception {
		odoMainOther.onShow();
	}

	/**
	 * ��ӡ����
	 * 
	 * @return Object
	 */
	public Object onPrintCase() throws Exception {
		return odoMainOther.onPrintCase();
	}

	/**
	 * ���ﲡ�� add by huangjw 20150108
	 * 
	 * @return
	 * @throws Exception
	 */
	public void onRePrintCase() throws Exception {
		odoMainOther.onRePrintCase();
	}

	/**
	 * ���ò������
	 */
	public void onCaseSheet() throws Exception {
		odoMainOther.onCaseSheet("");
	}

	/**
	 * ���1����¼��������2�����3���
	 */
	public void onCtz1() throws Exception {
		odoMainPat.onCtz1();
	}

	/**
	 * ���2����¼��������1�����3�Ƚϣ����ܺ����ǵ�ֵ��ͬ
	 */
	public void onCtz2() throws Exception {
		odoMainPat.onCtz2();
	}

	/**
	 * ���3����¼��������1�����2�Ƚϣ����ܺ����ǵ�ֵ��ͬ
	 */
	public void onCtz3() throws Exception {
		odoMainPat.onCtz3();
	}

	/**
	 * ������1����¼�����ղ�����2��3
	 */
	public void onPat1() throws Exception {
		odoMainPat.onPat1();
	}

	/**
	 * ������2����¼����벡����1��3�Ƚϣ����ܺ����ǵ�ֵ��ͬ
	 */
	public void onPat2() throws Exception {
		odoMainPat.onPat2();
	}

	/**
	 * ������3����¼����벡����1��2�Ƚϣ����ܺ����ǵ�ֵ��ͬ
	 */
	public void onPat3() throws Exception {
		odoMainPat.onPat3();
	}

	/**
	 * ���ò�����ϸ��Ϣ����
	 */
	public void onPatDetail() throws Exception {
		odoMainPat.onPatDetail();
	}

	/**
	 * ������ҩ��ť
	 */
	public void onResonablemed() throws Exception {
		odoMainReasonbledMed.onResonablemed();
	}

	/**
	 * �����б�չ���¼�
	 */
	public void onPat() throws Exception {
		odoMainReg.onPat();
	}

	/**
	 * ԤԼ�Һ�
	 */
	public void onReg() throws Exception {
		odoMainReg.onReg();
	}

	/**
	 * ԤԼ�Һ�
	 */
	public void onCrmDr() throws Exception {
		odoMainReg.onCrmDr();
	}

	/**
	 * �ǳ�̬����
	 * 
	 * @throws Exception
	 */
	public void onAbnormalReg() throws Exception {
		odoMainReg.onAbnormalReg();
	}

	/**
	 * �����������ʹ��
	 */
	public void onSpecialCase() throws Exception {
		odoMainTjIns.onSpecialCase();
	}

	/**
	 * ������Żس���ѯ�¼�
	 * 
	 * @throws Exception
	 */
	public void onQueNo() throws Exception {
		odoMainReg.onQueNo();
	}

	/**
	 * xueyf 2012-02-28 ҽ�����ش�����ѯ
	 */
	public void onINSDrQuery() throws Exception {
		odoMainTjIns.onINSDrQuery();
	}

	/**
	 * ����ҽʦ����ҽ��
	 */
	public void onDrOrder() throws Exception {
		odoMainTmplt.onDrOrder();
	}

	/**
	 * ���ÿ��ҳ���ҽ��
	 */
	public void onDeptOrder() throws Exception {
		odoMainTmplt.onDeptOrder();
	}

	/**
	 * ���ÿƳ���ģ��
	 */
	public void onDeptPack() throws Exception {
		odoMainTmplt.onDeptPack();
	}

	/**
	 * ����ҽʦ����ģ��
	 */
	public void onDrPack() throws Exception {
		odoMainTmplt.onDrPack();
	}

	/**
	 * ҽ��ҳǩ����¼�
	 */
	public void onChangeOrderTab() throws Exception {
		odoMainOpdOrder.onChangeOrderTab();
	}

	/**
	 * ����ǰʹÿ��TABLE��û�б༭״̬
	 */
	public void acceptForSave() throws Exception {
		odoMainOther.acceptOtherForSave();
		odoMainOpdOrder.acceptOpdOderForSave();
	}

	/**
	 * ҽ�ƿ�����
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
	 * ����һ�Ŵ���ǩ������combo������ʼtable
	 * 
	 * @param rxType
	 *            ��������
	 * @param rxName
	 *            combo��
	 * @param tableName
	 *            table��
	 */
	public void onAddOrderList(String rxType, String rxName, String tableName) throws Exception {
		odoMainOpdOrder.onAddOrderList(rxType, rxName, tableName);
	}

	/**
	 * ɾ�����Ŵ���ǩ
	 * 
	 * @param rxType
	 *            ��������
	 */
	public void onDeleteOrderList(int rxType) throws Exception {
		odoMainOpdOrder.onDeleteOrderList(rxType);
	}

	/**
	 * ̩������ҽ�ƿ��շ� ֻ�ܲ���ҽ�ƿ��շѲ���
	 */
	public void onFee() throws Exception {
		odoMainEkt.onFee();
	}

	/**
	 * ��ҽ���radioע��
	 */
	public void onWFlg() throws Exception {
		odoMainOpdOrder.onWFlg();
	}

	/**
	 * ��ҽ���radioע��
	 */
	public void onCFlg() throws Exception {
		odoMainOpdOrder.onCFlg();
	}

	/**
	 * ��Ʒ������
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
	 * ��ѯ���ﲡ����� ======pangben 2013-3-28
	 */
	public void onMrSearchFee() throws Exception {
		odoMainEkt.onMrSearchFee();
	}

	/**
	 * ɾ��ָ������һ������
	 */
	public void deleteRow() throws Exception {
		odoMainOpdOrder.deleteRow();
	}

	/**
	 * ��ϡ�����ʷ������ʷTABLE�е���¼�
	 * 
	 * @param tag
	 *            String
	 */
	public void onTableClick(String tag) throws Exception {
		odoMainOpdOrder.onTableClick(tag);
	}

	/**
	 * �ݴ棬��ӡ���á�������֪ͨ��
	 */
	public void onTempSave() throws Exception {
		// add by huangtt 20141208
		if (odoMainReg.canSave()) {
			this.messageBox_(ODOMainReg.MEGOVERTIME);
			return;
		}

		odoMainOpdOrder.onTempSave(null, 1); // modigy by huangtt 20141118 1��ʾ��ӡ����ǩ
		odoMainReg.onSaveReportStatus();
		odoMainOther.onDiagPnChange();
	}

	/**
	 * combo��ѡ�¼�,���ݴ����ų�ʼ��table
	 * 
	 * @param rxType
	 *            ��������
	 */
	public void onChangeRx(String rxType) throws Exception {
		odoMainOpdOrder.onChangeRx(rxType);
	}

	/**
	 * �����ײ� =======zhangp 2014
	 */
	public void onPack() throws Exception {
		odoMainTmplt.onPack();
	}

	/**
	 * �һ�MENU�����¼�
	 * 
	 * @param tableName
	 *            String
	 */
	public void showPopMenu(String tableName) throws Exception {
		odoMainOpdOrder.showPopMenu(tableName);
	}

	/**
	 * �һ�MENU��ʾ����ҽ���¼�
	 */
	public void onOrderSetShow() throws Exception {
		odoMainOpdOrder.onOrderSetShow();
	}

	/**
	 * �һ�MENU��ʾ������Ŀϸ���¼�
	 */
	public void onOpShow() throws Exception {
		odoMainOpdOrder.onOpShow();
	}

	/**
	 * �һ�MENU��ʾSYS_FEE�¼�
	 */
	public void onSysFeeShow() throws Exception {
		odoMainOpdOrder.onSysFeeShow();
	}

	/**
	 * ������ҩ--ҩƷ��Ϣ��ѯ
	 */
	public void onQueryRationalDrugUse() throws Exception {
		odoMainReasonbledMed.onQueryRationalDrugUse();
	}

	/**
	 * �һ�MENU��ʾʹ��˵��-duzhw 20131209
	 */
	public void useDrugMenu() throws Exception {
		odoMainOpdOrder.useDrugMenu();
	}

	/**
	 * 
	 * @param rxKind
	 *            String ��������
	 * @param tableName
	 *            String TABLE��
	 */
	public void onEditAll(String rxKind, String tableName) throws Exception {
		odoMainOpdOrder.onEditAll(rxKind, tableName);
	}

	/**
	 * Ԥ������ѯ yanjing 20140331
	 * 
	 * @throws Exception
	 */
	public void onPreOrder() throws Exception {
		odoMainOpdOrder.onPreOrder();
	}

	/**
	 * ��Ѫ����
	 */
	public void onBXResult() throws Exception {
		odoMainOther.onBXResult();
	}

	/**
	 * ��������
	 */
	public void onConsApply() throws Exception {
		odoMainOther.onConsApply();
	}

	/**
	 * ���߱�����Ϣ add by sunqy 20140516
	 */
	public void onInsureInfo() {
		TParm parm = new TParm();
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("EDIT", "N");
		this.openDialog("%ROOT%\\config\\mem\\MEMInsureInfo.x", parm);
	}

	/**
	 * ������� add by huangjw 20140812
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
		parm.setData("NUR_FLG", "N");// ���������� ���ж��Ƿ� ���������������Ĭ��Ϊ�� add by haungjw 20150714
		this.openDialog("%ROOT%\\config\\onw\\ONWPlanReport.x", parm);
	}

	/**
	 * ��ȡ������Ϣ add by huangtt 20141125
	 * 
	 * @throws Exception
	 */
	public void getPHY() throws Exception {

		odoMainOther.onPHY();

	}

	/**
	 * �������±깦��
	 */
	public void onInsertMarkText() {
		odoMainOther.onInsertMarkText();
	}

	/**
	 * ���±��ı�����
	 */
	public void onMarkTextProperty() {
		odoMainOther.getmWord().onOpenMarkProperty();
	}
	//

	/**
	 * �������ַ��������¼�
	 */
	public void onInsertSpecialChars() {
		// odoMainOther.onSpecialChars();
		if (!odoMainOther.getmWord().canEdit()) {
			this.messageBox("��ѡ����ģ��!");
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
			// ִ��ʧ��
			this.messageBox("E0005");
		}
	}

	/**
	 * �ַ�
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
	 * ȡ������
	 */
	public void onCancelConsult() throws Exception {
		if (!odoMainOpdOrder.canSave()) {
			this.messageBox_(ODOMainOpdOrder.MEGOVERTIME);
			return;
		}

		if (this.messageBox("ѯ��", "�Ƿ�ȡ������", 2) == 0) {

			String sql2 = "SELECT RX_NO,AR_AMT ,ORDER_CODE FROM OPD_ORDER " + " WHERE CASE_NO = '" + odo.getCaseNo()
					+ "' AND MR_NO = '" + odo.getMrNo() + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql2));
//			System.out.println("parm:::::"+parm);
			Boolean flg = this.isHaveOthersRxNo(parm);
			if (flg==true) {
				messageBox("ҽ������ҽ��,�޷��˺ţ�");
				return;
			}
			
			String delPreSql = "DELETE OPD_ORDER WHERE CASE_NO = '" + odo.getCaseNo() + "' AND  RX_NO = 'CLINIC_FEE'";
			TParm delPreParm = new TParm(TJDODBTool.getInstance().update(delPreSql));
			
			String sql3 ="UPDATE REG_PATADM SET SEE_DR_FLG='N',OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"' WHERE CASE_NO='" + odo.getCaseNo()+"' AND MR_NO = '" + odo.getMrNo() + "'";
			TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql3));
			
			if (delPreParm.getErrCode() < 0) {
				messageBox("ɾ�����Ʒ�ʧ��");
				return;
			} else {
				messageBox("ȡ�����Ʒѳɹ�");
				return;
			}
			
		} else {
			return;
		}
	}
	
	/**
	 * 
	 * @return true �� false û��
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