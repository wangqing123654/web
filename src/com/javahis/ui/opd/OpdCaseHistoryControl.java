package com.javahis.ui.opd;

import java.util.HashMap;
import java.util.Map;

//import jdo.clp.intoPathStatisticsTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: ����ҽ������վ�����¼
 * </p>
 * 
 * <p>
 * Description:����ҽ������վ�����¼
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author ehui 20091029
 * @version 1.0
 */
public class OpdCaseHistoryControl extends TControl {
	// �ż�ס��
	private String admType; // yanj
	// ����MR_NO
	private String mrNo;
	// �����¼TABLE
	// �����¼TABLE,���DIAG_TABLE,������EXA_TABLE,����OP_TABLE,����ҩMED_TABLE,��ҩCHN_TABLE,����ҩƷCTRL_TABLE
	private TTable table, diagTable, exaTable, opTable, medTable, chnTable,
			ctrlTable;
	// ��ҽ����ǩCOMBO
	private TComboBox rxNoCom;
	// �����¼
	private TParm parm;
	// �����¼��ѯSQL,20130530 yanj���ADM_TYPE�ֶ�
	//20130617 ��ʱ�併������ yanj
	private static final String SQL = "SELECT ADM_DATE,REALDR_CODE DR_CODE,CASE_NO,ADM_TYPE FROM REG_PATADM WHERE MR_NO='#' AND SEE_DR_FLG!='N' ORDER BY ADM_DATE Desc";
	// ��ѯ���߿���
	private static final String SUBJ_SQL = "SELECT * FROM OPD_SUBJREC WHERE CASE_NO='#'";
	// ��ѯ���
	private static final String DIAG_SQL = "SELECT A.*,B.ICD_CHN_DESC,B.ICD_ENG_DESC "
			+ "	FROM OPD_DIAGREC A,SYS_DIAGNOSIS B "
			+ "	WHERE CASE_NO='#' "
			+ "		  AND A.ICD_CODE=B.ICD_CODE "
			+ "	ORDER BY A.MAIN_DIAG_FLG DESC,A.ICD_CODE ";
	// �������ѯ
	private static final String EXA_SQL = "SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='5' AND SETMAIN_FLG='Y' ORDER BY RX_NO,SEQ_NO";
	// ���ò�ѯ
	private static final String OP_SQL = "SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='4' ORDER BY RX_NO,SEQ_NO";
	// ��ҩ��ѯ
	//======yanj2013-03-20,20130530 ���EMG_FIT_FLG��OPD_FIT_FLG
	private static final String MED_SQL = "SELECT 'Y' AS USE,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.ACTIVE_FLG,A.ORDER_CODE,A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT," +
			"A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DISPENSE_QTY,A.RELEASE_FLG,A.GIVEBOX_FLG,A.DISPENSE_UNIT,A.EXEC_DEPT_CODE,A.DR_NOTE,A.NS_NOTE,A.URGENT_FLG,A.INSPAY_TYPE,B.ORDER_CODE AS ORDER_CODE_FEE,(CASE WHEN (PHA_DISPENSE_CODE IS NOT  NULL  AND PHA_DISPENSE_DATE IS NOT NULL ) THEN 'Y' ELSE 'N' END) AS DISPENSE_FLG " +//caowl 20131104 add ��ҩ���
			" ,A.DOSE_TYPE "+
			"FROM OPD_ORDER A , SYS_FEE B " +
			"WHERE A.ORDER_CODE = B.ORDER_CODE(+) AND A.CASE_NO='#' AND A.RX_TYPE='1' ORDER BY A.RX_NO,A.SEQ_NO";
	// ����ҩƷ��ѯ
	private static final String CTRL_SQL = "SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='2' ORDER BY RX_NO,SEQ_NO";
	// ��ҽ����ǩ��Ϣ��ѯ
	private static final String CHN_RX_SQL = "SELECT DISTINCT RX_NO ID FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='3' ORDER BY RX_NO";
	// ���ݴ���ǩ��ѯ��ҽ��Ϣ
	private static final String CHN_SQL_BY_RX = "SELECT * FROM OPD_ORDER WHERE RX_NO='#'";
	// �ش�������ҩ����
	private TParm chnComboParm;
	// ��ҽ����
	private Map chn = new HashMap();
	// =========pangben 2012-6-28
	//controlName:  TCheckBox ���ߣ����ߣ���������ϣ�����������飬ҽ����ȫѡ 
	private String[] controlName = { "ZS", "KS", "TZ", "DIAG", "JCJG", "JY",
			"ORDER" ,"ALLCHECK"};// ��ʼ��Ĭ��ѡ����������
	//=========liling 2014-3-19   ��Ӿ������ͳ��
	private int number;
	
	//add by huangtt 20150804 start ������
	private String caseNo;
	private static String TABLEPANLE = "TABLEPANE_RE";
	private static String TABLE_RE = "TABLE_RE";
	private static String TABLE_LIS = "TABLE_LIS";
	private static String TABLE_RIS = "TABLE_RIS";
	//add by huangtt 20150804 end  ������
	
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		initParameter();
		initComponent();
		if (!initForm()) {
			// this.messageBox_("herer");
			return;
		}
	}

	/**
	 * ��ʼ������
	 */
	public void initParameter() {
		Object str = this.getParameter();
		if (str == null || !(str instanceof String)) {
			return;
		}
		mrNo = (String) str;
		if (StringUtil.isNullString(mrNo)) {
			return;
		}
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initComponent() {
//===========liling 2014-03-19 ���ע��
	// �����¼TABLE,���DIAG_TABLE,������EXA_TABLE,����OP_TABLE,����ҩMED_TABLE,��ҩCHN_TABLE,����ҩƷCTRL_TABLE
		table = (TTable) this.getComponent("TABLE");
		diagTable = (TTable) this.getComponent("DIAG_TABLE");
		exaTable = (TTable) this.getComponent("EXA_TABLE");
		opTable = (TTable) this.getComponent("OP_TABLE");
		medTable = (TTable) this.getComponent("MED_TABLE");
		chnTable = (TTable) this.getComponent("CHN_TABLE");
		ctrlTable = (TTable) this.getComponent("CTRL_TABLE");
		TTabbedPane pane = (TTabbedPane) this.getComponent("TTABBEDPANE");
//		System.out.println("%%%###pane is:"+pane);
		pane.setSelectedIndex(2);
		// =============pangben 2012-06-28 ��ʼ��Ĭ��ѡ��
		for (int i = 0; i < controlName.length; i++) {
			((TCheckBox) this.getComponent(controlName[i])).setSelected(true);
		}
		rxNoCom = (TComboBox) this.getComponent("RX_NO");
		
		//add by huangtt 20150804 start ������
		callFunction("UI|" + TABLE_RE + "|addEventListener", TABLE_RE + "->" + TTableEvent.CLICKED, this,
        "onTableClickedRe");
		callFunction("UI|" + TABLE_LIS + "|addEventListener", TABLE_LIS + "->" + TTableEvent.CLICKED,
        this, "onTableClickedLis");
		callFunction("UI|" + TABLE_RIS + "|addEventListener", TABLE_RIS + "->" + TTableEvent.CLICKED,
        this, "onTableClickedRis");
		//add by huangtt 20150804 end ������
	}

	/**
	 * ���ݸ���MR_NO��ʼ�������¼TABLE
	 * 
	 * @return
	 */
	private boolean initForm() {
		String sql = SQL.replace("#", mrNo);
		// System.out.println("initForm.sql="+sql);
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() != 0) {
			return false;
		}
		table.setParmValue(parm);
	//========liling 2014-03-19     �������	
		number=parm.getCount();
	//	System.out.println(number);
	    String text=""+number+" ��";
		this.setText("COUNT", text);
		return true;
	}

	/**
	 * �����¼TABLE�����¼�
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row < 0 || parm == null) {
			return;
		}
		String caseNo = parm.getValue("CASE_NO", row);
		this.setCaseNo(caseNo);
		admType = parm.getValue("ADM_TYPE",row);//yanj 20130530 ����ż�ס��
		// this.messageBox_(caseNo);
		if (StringUtil.isNullString(caseNo)) {
			// this.messageBox_("no caseNo");
			return;
		}
		// �����߿��߸�ֵ
		String subjSql = SUBJ_SQL.replace("#", caseNo);
		TParm subjRec = new TParm(TJDODBTool.getInstance().select(subjSql));
		// System.out.println("subjRec="+subjRec);
		if (subjRec.getErrCode() < 0) {
			this.messageBox_("��ѯ���߿�������ʧ��");
			return;
		}
		int count = subjRec.getCount();
		if (count > 0) {
			this.setValue("SUBJ_TEXT", subjRec.getValue("SUBJ_TEXT", 0));
			this.setValue("OBJ_TEXT", subjRec.getValue("OBJ_TEXT", 0));
			this.setValue("PHYSEXAM_REC", subjRec.getValue("PHYSEXAM_REC", 0));
			// =========pangben 2012-6-28 ��Ӽ���� ����������
			this.setValue("EXA_RESULT", subjRec.getValue("EXA_RESULT", 0));
			this.setValue("PROPOSAL", subjRec.getValue("PROPOSAL", 0));
		}

		String diagSql = DIAG_SQL.replace("#", caseNo);
		// System.out.println("diuagSql="+diagSql);
		TParm diagRec = new TParm(TJDODBTool.getInstance().select(diagSql));
		if (diagRec.getErrCode() < 0) {
			this.messageBox_("��ѯ�����Ϣʧ��");
			return;
		}
		diagTable.setParmValue(diagRec);
		// ��,30,boolean;��,30,int;ҽ��,200;����,40,double,##0.00;��λ,40,UNIT_CODE;Ƶ��,50,FREQ_CODE;�շ�,40,int,#0;����,40,double;ִ�п���,80,OP_EXEC_DEPT;ҽ����ע,200;��ʿ��ע,200;����,40,boolean;�������,80,INS_PAY
		// LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;DISPENSE_QTY;EXEC_DEPT_CODE;DR_NOTE;NS_NOTE;URGENT_FLG;INS_PAY
		// ============xueyf modify 20120322 start ��ʱ����  liling 20140410 start

		 String exaSql=EXA_SQL.replace("#", caseNo);
		 TParm exa=new TParm(TJDODBTool.getInstance().select(exaSql));
		 if(exa.getErrCode()<0){
		 this.messageBox_("��ѯҽ����Ϣʧ��");
		 return;
		 }
		 exaTable.setParmValue(exa);
		 String opSql=OP_SQL.replace("#", caseNo);
		 TParm op=new TParm(TJDODBTool.getInstance().select(opSql));
		 if(op.getErrCode()<0){
		 this.messageBox_("��ѯҽ����Ϣʧ��");
		 return;
		 }
		 opTable.setParmValue(op);
		// ============liling   end ȡ������
//========yanj20130319
		String medSql = MED_SQL.replace("#", caseNo);
		TParm med = new TParm(TJDODBTool.getInstance().select(medSql));
		if (med.getErrCode() < 0) {
			this.messageBox_("��ѯҽ����Ϣʧ��");
			return;
		}
		medTable.setParmValue(med);
//===liling 20140410 start
		 String chnRxSql=CHN_RX_SQL.replace("#", caseNo);
		 // System.out.println("chnRxSql="+chnRxSql);
		 chnComboParm=new TParm(TJDODBTool.getInstance().select(chnRxSql));
		 if(chnComboParm==null||chnComboParm.getErrCode()<0){
		 this.messageBox_("��ѯҽ����Ϣʧ��");
		 return;
		 }
		 count=chnComboParm.getCount();
		 for(int i=0;i<count;i++){
		 chnComboParm.setData("NAME", i, "��"+(i+1)+"�Ŵ���");
		 }
		 rxNoCom.setParmValue(chnComboParm);
		 rxNoCom.setSelectedID(chnComboParm.getValue("ID",0));
		 onChangeRx();
				
		 String ctrlSql=CTRL_SQL.replace("#", caseNo);
		 TParm ctrl=new TParm(TJDODBTool.getInstance().select(ctrlSql));
		 if(ctrl.getErrCode()<0){
		 this.messageBox_("��ѯҽ����Ϣʧ��");
		 return;
		 }
		 ctrlTable.setParmValue(ctrl);
		// ============xueyf modify 20120322 stop ��ʱ����  liling  end ȡ������
		 
		 //add by huangtt 20150804 start ��Ӽ����
		 this.getTComboBox("YX").setSelectedID("1");
		 queryData();
		 //add by huangtt 20150804 end
		 
	}
	
	/**
     * ��ѯ�����  add by huangtt 20150804 ������
     */
    public void queryData() {
        String medSql = "SELECT * FROM MED_APPLY WHERE CASE_NO='" + this.getCaseNo() + "' ";
        if (getRadioButtonFlg("LIS")) {
            medSql += " AND CAT1_TYPE='LIS'";
            getTTabbedPane(TABLEPANLE).setSelectedIndex(0);
            getTTabbedPane(TABLEPANLE).setEnabledAt(0, true);
            getTTabbedPane(TABLEPANLE).setEnabledAt(1, false);
            this.getTComboBox("YX").setSelectedID("1");
            this.getTTable(TABLE_LIS).removeRowAll();
            this.setValue("TEXT", "");
        } else {
            medSql += " AND CAT1_TYPE='RIS'";
            getTTabbedPane(TABLEPANLE).setSelectedIndex(1);
            getTTabbedPane(TABLEPANLE).setEnabledAt(0, false);
            getTTabbedPane(TABLEPANLE).setEnabledAt(1, true);
            this.getTComboBox("YX").setSelectedID("1");
            this.getTTable(TABLE_RIS).removeRowAll();
            this.setValue("TEXT", "");
        }
        if (admType.equals("O") || admType.equals("E")) {//wanglong add 20140908
            medSql +=
                    " AND APPLICATION_NO IN(SELECT DISTINCT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO = '#' AND MED_APPLY_NO IS NOT NULL) ";
            medSql = medSql.replaceFirst("#", this.getCaseNo());
        }
        if (admType.equals("I")) {
            medSql +=
                    " AND APPLICATION_NO IN(SELECT DISTINCT MED_APPLY_NO FROM ODI_ORDER WHERE CASE_NO = '#' AND MED_APPLY_NO IS NOT NULL) ";
            medSql = medSql.replaceFirst("#", this.getCaseNo());
        } else if (admType.equals("H")) {
            medSql +=
                    " AND APPLICATION_NO IN(SELECT DISTINCT MED_APPLY_NO FROM HRM_ORDER WHERE CASE_NO = '#' AND MED_APPLY_NO IS NOT NULL) ";
            medSql = medSql.replaceFirst("#", this.getCaseNo());
        }
        TParm parm = new TParm(TJDODBTool.getInstance().select(medSql));
        this.getTTable(TABLE_RE).setParmValue(new TParm());
        this.getTTable(TABLE_RE).setParmValue(parm);
    }
	
	
	/**
	 * ����ǩ�ı��¼�
	 */
	public void onChangeRx() {
		String rxNo = this.rxNoCom.getSelectedID();
		if (StringUtil.isNullString(rxNo)) {
			return;
		}
		TParm parm = new TParm();
		// if(chn.get(rxNo)==null){
		//			
		// chn.put(rxNo, parm);
		// }else{
		// parm=(TParm)chn.get(rxNo);
		// }

		parm = new TParm(TJDODBTool.getInstance().select(
				this.CHN_SQL_BY_RX.replace("#", rxNo)));
		TParm chnParm = this.getChnParm(parm);
		// System.out.println("chnParm="+chnParm);
		chnTable.setParmValue(chnParm);
		this.setValue("TAKE_DAYS", parm.getValue("TAKE_DAYS", 0));
		this.setValue("DCT_TAKE_QTY", parm.getValue("DCT_TAKE_QTY", 0));
		this.setValue("FREQ_CODE", parm.getValue("FREQ_CODE", 0));
		this.setValue("DCTAGENT_CODE", parm.getValue("DCTAGENT_CODE", 0));
		this.setValue("CHN_ROUTE_CODE", parm.getValue("ROUTE_CODE", 0));
	}

	/**
	 * ȡ��
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getChnParm(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErrCode(-1);
			return result;
		}
		int count = parm.getCount();
		if (count < 1) {
			result.setErrCode(-1);
			return result;
		}
		int countAll = (count / 4 + 1) * 4;
		for (int i = 0; i < countAll; i++) {
			int row = i / 4;
			int column = i % 4 + 1;
			if (i < count) {
				result.setData("ORDER_DESC" + column, row, parm.getValue(
						"ORDER_DESC", i));
				result.setData("MEDI_QTY" + column, row, parm.getValue(
						"MEDI_QTY", i));
				result.setData("DCTEXCEP_CODE" + column, row, parm.getValue(
						"DCTEXCEP_CODE", i));
			} else {
				result.setData("ORDER_DESC" + column, row, "");
				result.setData("MEDI_QTY" + column, row, "");
				result.setData("DCTEXCEP_CODE" + column, row, "");
			}

		}
		result.setCount(countAll);

		return result;
	}

	/**
	 * �ش�
	 */
	public void onFetch() {
// �����¼TABLE,���DIAG_TABLE,������EXA_TABLE,����OP_TABLE,����ҩMED_TABLE,��ҩCHN_TABLE,����ҩƷCTRL_TABLE
		if (table == null) {
			return;
		}
		if (table.getSelectedRow() < 0) {
			return;
		}
		//====liling 20140410 start
		TTabbedPane pane = (TTabbedPane) this.getComponent("TTABBEDPANE");
		int index=pane.getSelectedIndex();
		if(index!=2){
			this.messageBox("�����ҩҳǩ����ҽ��");
		pane.setSelectedIndex(2);
		return ;
		}
		//===liling  20140410 end
		TParm exa = exaTable.getParmValue();
		TParm op = opTable.getParmValue();
		medTable.acceptText();
		TParm med = medTable.getParmValue();
		System.out.println("med::"+med);
		TParm ctrl = ctrlTable.getParmValue();
		TParm result = new TParm();
		if (TypeTool.getBoolean(this.getValue("ZS"))) {
			result.setData("SUB", this.getValueString("SUBJ_TEXT"));
		}
		if (TypeTool.getBoolean(this.getValue("KS"))) {
			result.setData("OBJ", this.getValueString("OBJ_TEXT"));
		}
		if (TypeTool.getBoolean(this.getValue("TZ"))) {
			result.setData("PHY", this.getValueString("PHYSEXAM_REC"));
		}
		// ==========pangben 2012-6-28 ��Ӽ���� \����ش�ֵ
		if (TypeTool.getBoolean(this.getValue("JCJG"))) {
			result.setData("EXA_R", this.getValueString("EXA_RESULT"));
		}
		if (TypeTool.getBoolean(this.getValue("JY"))) {
			result.setData("PRO", this.getValueString("PROPOSAL"));
		}
		TParm medParm=new TParm();//=======pangben 2013-1-5 û��ͣ�õ�ҽ�����Դ���
		int medCount=0;//�ۼƸ���
		StringBuffer message = new StringBuffer();
		StringBuffer bufRed=new StringBuffer();
		if (TypeTool.getBoolean(this.getValue("ORDER"))) {
			//=========yanj2013-03-20
			for (int i = 0; i < med.getCount(); i++) {
				String flg = med.getValue("USE",i);
				if (flg.equals("N")) {
					this.setValue("ALLCHECK", false);
					
				}
			}
			// ==========yanj 2013-03-22 �����ͣ��ҩƷУ��
			for (int i = 0; i < med.getCount("ORDER_CODE"); i++) {
				//�ж��������ҽ����SYS_FEE���в����� ���ܴ��ز�����ʾ
				if(null==med.getValue("ORDER_CODE_FEE", i)||"".equals(med.getValue("ORDER_CODE_FEE", i))){//modify  caoyong 20131105
					 bufRed.append(med.getValue("ORDER_DESC",i)).append(",");
				}else{
					//========yanjing 2013-3-20ѡ��Ҫ���ص�ҩ��
				if(med.getValue("USE",i).equals("Y")&&med.getValue("ACTIVE_FLG",i).equals("N")){//ͣ��ע��
							message.append(med.getValue("ORDER_DESC", i)).append(",");//��ʾҽ������
					}else if (med.getValue("USE",i).equals("Y")){
						// add by yanj 2013/05/30 �ż�������У��
						// ����
						if ("O".equalsIgnoreCase(admType)) {
							// �ж��Ƿ�סԺ����ҽ��
							if (!("Y".equals(med.getValue("OPD_FIT_FLG",i)))) {
								// ������������ҽ����
								this.messageBox(med.getValue("ORDER_DESC", i)+",������������ҽ����");
								return;
							}
						}
						// ����
						if ("E".equalsIgnoreCase(admType)) {
							if (!("Y".equals(med.getValue("EMG_FIT_FLG",i)))) {
								// ������������ҽ����
								this.messageBox(med.getValue("ORDER_DESC", i)+",���Ǽ�������ҽ����");
								return;
							}
						}
						// $$===========add by yanj 2013/05/30�ż�������У��
						medParm.setRowData(medCount, med,i);
						medCount++;
						}
				    }
					}
			medParm.setCount(medCount);
			if (message.toString().length() > 0) {//�ۼ�ͣ��ҽ������
				if(this.messageBox("��ʾ","ҩƷҽ������:"+message.toString().substring(0,message.toString().lastIndexOf(","))+"�Ѿ�ͣ��,�Ƿ񴫻�����ҽ��",0)!=0){
					return;
				}
				//return;
			}
			//add  caoyong 20131105
			if(bufRed.toString().length()>0){
			       String bufMess=bufRed.toString().substring(0,bufRed.toString().lastIndexOf(","))+"ҽ�������ڣ��Ƿ񴫻�����ҽ����";
			       if(this.messageBox("��ʾ","ҩƷҽ������:"+bufMess,0)!=0){
						return;
					}
			}
			
//			result.setData("EXA", exa);//===liling 20140410 ����
//			result.setData("OP", op);//===liling 20140410 ����
			result.setData("MED", medParm);
//			result.setData("CTRL", ctrl);//===liling 20140410 ����
			if (chnComboParm != null) {
				int count = chnComboParm.getCount();
				if (count > -1) {
					for (int i = 0; i < count; i++) {
						String rxNo = chnComboParm.getValue("ID", i);
						TParm chnParm = new TParm(TJDODBTool.getInstance()
								.select(this.CHN_SQL_BY_RX.replace("#", rxNo)));
						chn.put(rxNo, chnParm);
					}
				}
			}
//			result.setData("CHN", chn);//===liling 20140410 ����
		}
		if (TypeTool.getBoolean(this.getValue("DIAG"))) {
			result.setData("DIAG", diagTable.getParmValue());
		}
		this.setReturnValue(result);
//		System.out.println("result is :"+result);
		this.closeWindow();
	}
	//=======yanj2013-03-20
/**
 * ȫѡ�¼�
 */
	public void onSelAll() {
		String select = getValueString("ALLCHECK");
        TParm parm = medTable.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("USE", i, select);
        }
        medTable.setParmValue(parm);
	}
	
	
	/**
	 * ҽ������ //add by huangtt 20150804  ������
	 * 
	 * @param row
	 *            int
	 */
	public void onSelectCombo() {
		TTable table = this.getTTable(TABLE_RE);
		int index = getTTabbedPane(TABLEPANLE).getSelectedIndex();
		switch (index) {
		case 0:
			TParm inparm = new TParm();
			String medSql = "SELECT * FROM MED_APPLY WHERE CASE_NO='"
					+ this.getCaseNo() + "' AND CAT1_TYPE='LIS' ";
			TParm parm = new TParm(TJDODBTool.getInstance().select(medSql));
			// ����
			if ("2".equals(this.getValueString("YX"))) {
				for (int i = 0; i < parm.getCount(); i++) {
					String cat1Type = parm.getValue("CAT1_TYPE", i);
					String applyNo = parm.getValue("APPLICATION_NO", i);
					if (this.getflg(applyNo, cat1Type))
						continue;
					inparm.addRowData(parm, i);
				}
				inparm.setCount(inparm.getCount("CASE_NO"));
			}
			// �쳣
			if ("3".equals(this.getValueString("YX"))) {
				for (int i = 0; i < parm.getCount(); i++) {
					String cat1Type = parm.getValue("CAT1_TYPE", i);
					String applyNo = parm.getValue("APPLICATION_NO", i);
					if (!this.getflg(applyNo, cat1Type))
						continue;
					inparm.addRowData(parm, i);
				}
				inparm.setCount(inparm.getCount("CASE_NO"));
			}
			if ("1".equals(this.getValueString("YX")))
				inparm.setData(parm.getData());
			this.getTTable(TABLE_RE).setParmValue(inparm);
			this.getTTable(TABLE_LIS).removeRowAll();
			this.setValue("TEXT", "");
			break;
		case 1:
			TParm Rinparm = new TParm();
			String RmedSql = "SELECT * FROM MED_APPLY WHERE CASE_NO='"
					+ this.getCaseNo() + "' AND CAT1_TYPE='RIS' ";
			TParm Rparm = new TParm(TJDODBTool.getInstance().select(RmedSql));
			// ����
			if ("2".equals(this.getValueString("YX"))) {
				for (int i = 0; i < Rparm.getCount(); i++) {
					String cat1Type = Rparm.getValue("CAT1_TYPE", i);
					String applyNo = Rparm.getValue("APPLICATION_NO", i);
					if (this.getflg(applyNo, cat1Type))
						continue;
					Rinparm.addRowData(Rparm, i);
				}
				Rinparm.setCount(Rinparm.getCount("CASE_NO"));
			}
			// �쳣
			if ("3".equals(this.getValueString("YX"))) {
				for (int i = 0; i < Rparm.getCount(); i++) {
					String cat1Type = Rparm.getValue("CAT1_TYPE", i);
					String applyNo = Rparm.getValue("APPLICATION_NO", i);
					if (!this.getflg(applyNo, cat1Type))
						continue;
					Rinparm.addRowData(Rparm, i);
				}
				Rinparm.setCount(Rinparm.getCount("CASE_NO"));
			}
			if ("1".equals(this.getValueString("YX"))) {
				Rinparm.setData(Rparm.getData());
			}
			this.getTTable(TABLE_RE).setParmValue(Rinparm);
			this.getTTable(TABLE_RIS).removeRowAll();
			this.setValue("TEXT", "");
			break;
		}
	}
	
	/**
	 * add by huangtt 20150804  ������
	 * @param applyNo
	 * @return
	 */
	public boolean getflg(String applyNo, String cat1Type) {
		String sql = "";
		if (cat1Type.equals("LIS")) {
			sql = "SELECT * FROM MED_LIS_RPT WHERE CAT1_TYPE='" + cat1Type
					+ "' AND APPLICATION_NO='" + applyNo
					+ "' AND CRTCLLWLMT<>'NM'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm.getCount() > 0) {
				return true;
			}
			return false;
		} else if (cat1Type.equals("RIS")) {
			sql = "SELECT * FROM MED_RPTDTL WHERE CAT1_TYPE='" + cat1Type
					+ "' AND APPLICATION_NO='" + applyNo
					+ "' AND OUTCOME_TYPE = 'H'";//����
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm.getCount() > 0) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	
	/**
	 * ���鵥�� add by huangtt 20150804  ������
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedLis(int row) {
		TTable table = this.getTTable(TABLE_LIS);
		TParm parm = table.getDataStore().getRowParm(row);
		StringBuffer str = new StringBuffer();
		if (this.getValueString("TEXT").length() != 0) {
			str.append("\n" + parm.getValue("TESTITEM_CHN_DESC") + ":");
			str.append("����ֵ:" + parm.getValue("TEST_VALUE") + " "
					+ parm.getValue("TEST_UNIT"));
		} else {
			str.append(parm.getValue("TESTITEM_CHN_DESC") + ":");
			str.append("����ֵ:" + parm.getValue("TEST_VALUE") + " "
					+ parm.getValue("TEST_UNIT"));
		}
		this.setValue("TEXT", this.getValueString("TEXT") + str);
	}

	/**
	 * ��鵥�� add by huangtt 20150804  ������
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedRis(int row) {
		TTable table = this.getTTable(TABLE_RIS);
		TParm parm = table.getDataStore().getRowParm(row);
		StringBuffer str = new StringBuffer();
		if (this.getValueString("TEXT").length() != 0) {
			if (parm.getValue("OUTCOME_TYPE").length() != 0) {
				str.append("\n" + "������:" + (parm.getValue("OUTCOME_TYPE").equals("T")?"����":"����")+"\n");
			} else {
				str.append("\n");
			}
			str.append("�����ӡ��:" + parm.getValue("OUTCOME_DESCRIBE")+"\n");
			str.append("���������:" + parm.getValue("OUTCOME_CONCLUSION"));
		} else {
			if (parm.getValue("OUTCOME_TYPE").length() != 0) {
				str.append("������:" + (parm.getValue("OUTCOME_TYPE").equals("T")?"����":"����")+"\n");
			} else {
				str.append("");
			}
			str.append("�����ӡ��:" + parm.getValue("OUTCOME_DESCRIBE")+"\n");
			str.append("���������:" + parm.getValue("OUTCOME_CONCLUSION"));
		}
		this.setValue("TEXT", this.getValueString("TEXT") + str);
	}
	
	/**
	 * ҽ������ add by huangtt 20150804  ������
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedRe(int row) {
		TTable table = this.getTTable(TABLE_RE);
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		int index = getTTabbedPane(TABLEPANLE).getSelectedIndex();
		// ҽ��ϸ����
		String orderCat1Type = parm.getValue("CAT1_TYPE");
		// ���뵥��
		String applicationNo = parm.getValue("APPLICATION_NO");
		switch (index) {
		case 0:
			TDataStore tableLis = new TDataStore();
			tableLis.setSQL("SELECT * FROM MED_LIS_RPT WHERE CAT1_TYPE='"
					+ orderCat1Type + "' AND APPLICATION_NO='" + applicationNo
					+ "'");
			tableLis.retrieve();
			this.getTTable(TABLE_LIS).setDataStore(tableLis);
			this.getTTable(TABLE_LIS).setDSValue();
			break;
		case 1:
			TDataStore tableRis = new TDataStore();
			tableRis.setSQL("SELECT * FROM MED_RPTDTL WHERE CAT1_TYPE='"
					+ orderCat1Type + "' AND APPLICATION_NO='" + applicationNo
					+ "'");
			tableRis.retrieve();
			this.getTTable(TABLE_RIS).setDataStore(tableRis);
			this.getTTable(TABLE_RIS).setDSValue();
			break;
		}
	}

	/**
	 * �иı��¼� add by huangtt 20150804  ������
	 */
	public void onSelRow() {
		onTableClickedRe(this.getTTable(TABLE_RE).getSelectedRow());
	}
	
	/**
	 * ��� add by huangtt 20150804  ������
	 */
	public void onClearText() {
		this.clearValue("TEXT");
	}
	

	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	
	
	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	
	/**
	 * �õ��Ƿ�ѡ��
	 * 
	 * @param tag
	 *            String
	 * @return boolean
	 */
	public boolean getRadioButtonFlg(String tag) {
		return getTRadioButton(tag).isSelected();
	}
	

	/**
	 * ����TRadioButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}
	
	/**
	 * �õ�TTabbedPane
	 * 
	 * @param tag
	 *            String
	 * @return TTabbedPane
	 */
	public TTabbedPane getTTabbedPane(String tag) {
		return (TTabbedPane) this.getComponent(tag);
	}

	/**
	 * �õ�TComboBox
	 * 
	 * @param tag
	 *            String
	 * @return TComboBox
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}


}
