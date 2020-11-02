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
 * Title: 门诊医生工作站就诊记录
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站就诊记录
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
	// 门急住别
	private String admType; // yanj
	// 给入MR_NO
	private String mrNo;
	// 就诊记录TABLE
	// 就诊记录TABLE,诊断DIAG_TABLE,检验检查EXA_TABLE,处置OP_TABLE,西成药MED_TABLE,草药CHN_TABLE,管制药品CTRL_TABLE
	private TTable table, diagTable, exaTable, opTable, medTable, chnTable,
			ctrlTable;
	// 中医处方签COMBO
	private TComboBox rxNoCom;
	// 就诊记录
	private TParm parm;
	// 就诊记录查询SQL,20130530 yanj添加ADM_TYPE字段
	//20130617 按时间降序排列 yanj
	private static final String SQL = "SELECT ADM_DATE,REALDR_CODE DR_CODE,CASE_NO,ADM_TYPE FROM REG_PATADM WHERE MR_NO='#' AND SEE_DR_FLG!='N' ORDER BY ADM_DATE Desc";
	// 查询主诉客诉
	private static final String SUBJ_SQL = "SELECT * FROM OPD_SUBJREC WHERE CASE_NO='#'";
	// 查询诊断
	private static final String DIAG_SQL = "SELECT A.*,B.ICD_CHN_DESC,B.ICD_ENG_DESC "
			+ "	FROM OPD_DIAGREC A,SYS_DIAGNOSIS B "
			+ "	WHERE CASE_NO='#' "
			+ "		  AND A.ICD_CODE=B.ICD_CODE "
			+ "	ORDER BY A.MAIN_DIAG_FLG DESC,A.ICD_CODE ";
	// 检验检查查询
	private static final String EXA_SQL = "SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='5' AND SETMAIN_FLG='Y' ORDER BY RX_NO,SEQ_NO";
	// 处置查询
	private static final String OP_SQL = "SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='4' ORDER BY RX_NO,SEQ_NO";
	// 西药查询
	//======yanj2013-03-20,20130530 添加EMG_FIT_FLG，OPD_FIT_FLG
	private static final String MED_SQL = "SELECT 'Y' AS USE,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.ACTIVE_FLG,A.ORDER_CODE,A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT," +
			"A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DISPENSE_QTY,A.RELEASE_FLG,A.GIVEBOX_FLG,A.DISPENSE_UNIT,A.EXEC_DEPT_CODE,A.DR_NOTE,A.NS_NOTE,A.URGENT_FLG,A.INSPAY_TYPE,B.ORDER_CODE AS ORDER_CODE_FEE,(CASE WHEN (PHA_DISPENSE_CODE IS NOT  NULL  AND PHA_DISPENSE_DATE IS NOT NULL ) THEN 'Y' ELSE 'N' END) AS DISPENSE_FLG " +//caowl 20131104 add 发药标记
			" ,A.DOSE_TYPE "+
			"FROM OPD_ORDER A , SYS_FEE B " +
			"WHERE A.ORDER_CODE = B.ORDER_CODE(+) AND A.CASE_NO='#' AND A.RX_TYPE='1' ORDER BY A.RX_NO,A.SEQ_NO";
	// 管制药品查询
	private static final String CTRL_SQL = "SELECT * FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='2' ORDER BY RX_NO,SEQ_NO";
	// 中医处方签信息查询
	private static final String CHN_RX_SQL = "SELECT DISTINCT RX_NO ID FROM OPD_ORDER WHERE CASE_NO='#' AND RX_TYPE='3' ORDER BY RX_NO";
	// 根据处方签查询中医信息
	private static final String CHN_SQL_BY_RX = "SELECT * FROM OPD_ORDER WHERE RX_NO='#'";
	// 回传所有中药处方
	private TParm chnComboParm;
	// 中医数据
	private Map chn = new HashMap();
	// =========pangben 2012-6-28
	//controlName:  TCheckBox 主诉，客诉，体征，诊断，检查结果，建议，医嘱，全选 
	private String[] controlName = { "ZS", "KS", "TZ", "DIAG", "JCJG", "JY",
			"ORDER" ,"ALLCHECK"};// 初始化默认选中所有数据
	//=========liling 2014-3-19   添加就诊次数统计
	private int number;
	
	//add by huangtt 20150804 start 报告结果
	private String caseNo;
	private static String TABLEPANLE = "TABLEPANE_RE";
	private static String TABLE_RE = "TABLE_RE";
	private static String TABLE_LIS = "TABLE_LIS";
	private static String TABLE_RIS = "TABLE_RIS";
	//add by huangtt 20150804 end  报告结果
	
	/**
	 * 初始化
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
	 * 初始化参数
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
	 * 初始化控件
	 */
	private void initComponent() {
//===========liling 2014-03-19 添加注释
	// 就诊记录TABLE,诊断DIAG_TABLE,检验检查EXA_TABLE,处置OP_TABLE,西成药MED_TABLE,草药CHN_TABLE,管制药品CTRL_TABLE
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
		// =============pangben 2012-06-28 初始化默认选中
		for (int i = 0; i < controlName.length; i++) {
			((TCheckBox) this.getComponent(controlName[i])).setSelected(true);
		}
		rxNoCom = (TComboBox) this.getComponent("RX_NO");
		
		//add by huangtt 20150804 start 报告结果
		callFunction("UI|" + TABLE_RE + "|addEventListener", TABLE_RE + "->" + TTableEvent.CLICKED, this,
        "onTableClickedRe");
		callFunction("UI|" + TABLE_LIS + "|addEventListener", TABLE_LIS + "->" + TTableEvent.CLICKED,
        this, "onTableClickedLis");
		callFunction("UI|" + TABLE_RIS + "|addEventListener", TABLE_RIS + "->" + TTableEvent.CLICKED,
        this, "onTableClickedRis");
		//add by huangtt 20150804 end 报告结果
	}

	/**
	 * 根据给入MR_NO初始化就诊记录TABLE
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
	//========liling 2014-03-19     就诊次数	
		number=parm.getCount();
	//	System.out.println(number);
	    String text=""+number+" 次";
		this.setText("COUNT", text);
		return true;
	}

	/**
	 * 就诊记录TABLE单击事件
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row < 0 || parm == null) {
			return;
		}
		String caseNo = parm.getValue("CASE_NO", row);
		this.setCaseNo(caseNo);
		admType = parm.getValue("ADM_TYPE",row);//yanj 20130530 添加门急住别
		// this.messageBox_(caseNo);
		if (StringUtil.isNullString(caseNo)) {
			// this.messageBox_("no caseNo");
			return;
		}
		// 给主诉客诉赋值
		String subjSql = SUBJ_SQL.replace("#", caseNo);
		TParm subjRec = new TParm(TJDODBTool.getInstance().select(subjSql));
		// System.out.println("subjRec="+subjRec);
		if (subjRec.getErrCode() < 0) {
			this.messageBox_("查询主诉客诉数据失败");
			return;
		}
		int count = subjRec.getCount();
		if (count > 0) {
			this.setValue("SUBJ_TEXT", subjRec.getValue("SUBJ_TEXT", 0));
			this.setValue("OBJ_TEXT", subjRec.getValue("OBJ_TEXT", 0));
			this.setValue("PHYSEXAM_REC", subjRec.getValue("PHYSEXAM_REC", 0));
			// =========pangben 2012-6-28 添加检查结果 、建议数据
			this.setValue("EXA_RESULT", subjRec.getValue("EXA_RESULT", 0));
			this.setValue("PROPOSAL", subjRec.getValue("PROPOSAL", 0));
		}

		String diagSql = DIAG_SQL.replace("#", caseNo);
		// System.out.println("diuagSql="+diagSql);
		TParm diagRec = new TParm(TJDODBTool.getInstance().select(diagSql));
		if (diagRec.getErrCode() < 0) {
			this.messageBox_("查询诊断信息失败");
			return;
		}
		diagTable.setParmValue(diagRec);
		// 连,30,boolean;组,30,int;医嘱,200;用量,40,double,##0.00;单位,40,UNIT_CODE;频次,50,FREQ_CODE;日份,40,int,#0;总量,40,double;执行科室,80,OP_EXEC_DEPT;医生备注,200;护士备注,200;急作,40,boolean;给付类别,80,INS_PAY
		// LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;DISPENSE_QTY;EXEC_DEPT_CODE;DR_NOTE;NS_NOTE;URGENT_FLG;INS_PAY
		// ============xueyf modify 20120322 start 暂时屏蔽  liling 20140410 start

		 String exaSql=EXA_SQL.replace("#", caseNo);
		 TParm exa=new TParm(TJDODBTool.getInstance().select(exaSql));
		 if(exa.getErrCode()<0){
		 this.messageBox_("查询医嘱信息失败");
		 return;
		 }
		 exaTable.setParmValue(exa);
		 String opSql=OP_SQL.replace("#", caseNo);
		 TParm op=new TParm(TJDODBTool.getInstance().select(opSql));
		 if(op.getErrCode()<0){
		 this.messageBox_("查询医嘱信息失败");
		 return;
		 }
		 opTable.setParmValue(op);
		// ============liling   end 取消屏蔽
//========yanj20130319
		String medSql = MED_SQL.replace("#", caseNo);
		TParm med = new TParm(TJDODBTool.getInstance().select(medSql));
		if (med.getErrCode() < 0) {
			this.messageBox_("查询医嘱信息失败");
			return;
		}
		medTable.setParmValue(med);
//===liling 20140410 start
		 String chnRxSql=CHN_RX_SQL.replace("#", caseNo);
		 // System.out.println("chnRxSql="+chnRxSql);
		 chnComboParm=new TParm(TJDODBTool.getInstance().select(chnRxSql));
		 if(chnComboParm==null||chnComboParm.getErrCode()<0){
		 this.messageBox_("查询医嘱信息失败");
		 return;
		 }
		 count=chnComboParm.getCount();
		 for(int i=0;i<count;i++){
		 chnComboParm.setData("NAME", i, "第"+(i+1)+"张处方");
		 }
		 rxNoCom.setParmValue(chnComboParm);
		 rxNoCom.setSelectedID(chnComboParm.getValue("ID",0));
		 onChangeRx();
				
		 String ctrlSql=CTRL_SQL.replace("#", caseNo);
		 TParm ctrl=new TParm(TJDODBTool.getInstance().select(ctrlSql));
		 if(ctrl.getErrCode()<0){
		 this.messageBox_("查询医嘱信息失败");
		 return;
		 }
		 ctrlTable.setParmValue(ctrl);
		// ============xueyf modify 20120322 stop 暂时屏蔽  liling  end 取消屏蔽
		 
		 //add by huangtt 20150804 start 添加检查结果
		 this.getTComboBox("YX").setSelectedID("1");
		 queryData();
		 //add by huangtt 20150804 end
		 
	}
	
	/**
     * 查询检查结果  add by huangtt 20150804 报告结果
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
	 * 处方签改变事件
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
	 * 取得
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
	 * 回传
	 */
	public void onFetch() {
// 就诊记录TABLE,诊断DIAG_TABLE,检验检查EXA_TABLE,处置OP_TABLE,西成药MED_TABLE,草药CHN_TABLE,管制药品CTRL_TABLE
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
			this.messageBox("请从西药页签传回医嘱");
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
		// ==========pangben 2012-6-28 添加检查结果 \建议回传值
		if (TypeTool.getBoolean(this.getValue("JCJG"))) {
			result.setData("EXA_R", this.getValueString("EXA_RESULT"));
		}
		if (TypeTool.getBoolean(this.getValue("JY"))) {
			result.setData("PRO", this.getValueString("PROPOSAL"));
		}
		TParm medParm=new TParm();//=======pangben 2013-1-5 没有停用的医嘱可以传回
		int medCount=0;//累计个数
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
			// ==========yanj 2013-03-22 添加已停用药品校验
			for (int i = 0; i < med.getCount("ORDER_CODE"); i++) {
				//判断如果传回医嘱在SYS_FEE表中不存在 则不能传回并且提示
				if(null==med.getValue("ORDER_CODE_FEE", i)||"".equals(med.getValue("ORDER_CODE_FEE", i))){//modify  caoyong 20131105
					 bufRed.append(med.getValue("ORDER_DESC",i)).append(",");
				}else{
					//========yanjing 2013-3-20选择要传回的药嘱
				if(med.getValue("USE",i).equals("Y")&&med.getValue("ACTIVE_FLG",i).equals("N")){//停用注记
							message.append(med.getValue("ORDER_DESC", i)).append(",");//显示医嘱名称
					}else if (med.getValue("USE",i).equals("Y")){
						// add by yanj 2013/05/30 门急诊适用校验
						// 门诊
						if ("O".equalsIgnoreCase(admType)) {
							// 判断是否住院适用医嘱
							if (!("Y".equals(med.getValue("OPD_FIT_FLG",i)))) {
								// 不是门诊适用医嘱！
								this.messageBox(med.getValue("ORDER_DESC", i)+",不是门诊适用医嘱。");
								return;
							}
						}
						// 急诊
						if ("E".equalsIgnoreCase(admType)) {
							if (!("Y".equals(med.getValue("EMG_FIT_FLG",i)))) {
								// 不是门诊适用医嘱！
								this.messageBox(med.getValue("ORDER_DESC", i)+",不是急诊适用医嘱。");
								return;
							}
						}
						// $$===========add by yanj 2013/05/30门急诊适用校验
						medParm.setRowData(medCount, med,i);
						medCount++;
						}
				    }
					}
			medParm.setCount(medCount);
			if (message.toString().length() > 0) {//累计停用医嘱名称
				if(this.messageBox("提示","药品医嘱名称:"+message.toString().substring(0,message.toString().lastIndexOf(","))+"已经停用,是否传回其它医嘱",0)!=0){
					return;
				}
				//return;
			}
			//add  caoyong 20131105
			if(bufRed.toString().length()>0){
			       String bufMess=bufRed.toString().substring(0,bufRed.toString().lastIndexOf(","))+"医嘱不存在，是否传回其它医嘱？";
			       if(this.messageBox("提示","药品医嘱名称:"+bufMess,0)!=0){
						return;
					}
			}
			
//			result.setData("EXA", exa);//===liling 20140410 屏蔽
//			result.setData("OP", op);//===liling 20140410 屏蔽
			result.setData("MED", medParm);
//			result.setData("CTRL", ctrl);//===liling 20140410 屏蔽
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
//			result.setData("CHN", chn);//===liling 20140410 屏蔽
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
 * 全选事件
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
	 * 医嘱单击 //add by huangtt 20150804  报告结果
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
			// 正常
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
			// 异常
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
			// 正常
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
			// 异常
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
	 * add by huangtt 20150804  报告结果
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
					+ "' AND OUTCOME_TYPE = 'H'";//阳性
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm.getCount() > 0) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	
	/**
	 * 检验单击 add by huangtt 20150804  报告结果
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
			str.append("检验值:" + parm.getValue("TEST_VALUE") + " "
					+ parm.getValue("TEST_UNIT"));
		} else {
			str.append(parm.getValue("TESTITEM_CHN_DESC") + ":");
			str.append("检验值:" + parm.getValue("TEST_VALUE") + " "
					+ parm.getValue("TEST_UNIT"));
		}
		this.setValue("TEXT", this.getValueString("TEXT") + str);
	}

	/**
	 * 检查单击 add by huangtt 20150804  报告结果
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
				str.append("\n" + "阴阳性:" + (parm.getValue("OUTCOME_TYPE").equals("T")?"阴性":"阳性")+"\n");
			} else {
				str.append("\n");
			}
			str.append("检查结果印象:" + parm.getValue("OUTCOME_DESCRIBE")+"\n");
			str.append("检查结果结论:" + parm.getValue("OUTCOME_CONCLUSION"));
		} else {
			if (parm.getValue("OUTCOME_TYPE").length() != 0) {
				str.append("阴阳性:" + (parm.getValue("OUTCOME_TYPE").equals("T")?"阴性":"阳性")+"\n");
			} else {
				str.append("");
			}
			str.append("检查结果印象:" + parm.getValue("OUTCOME_DESCRIBE")+"\n");
			str.append("检查结果结论:" + parm.getValue("OUTCOME_CONCLUSION"));
		}
		this.setValue("TEXT", this.getValueString("TEXT") + str);
	}
	
	/**
	 * 医嘱单击 add by huangtt 20150804  报告结果
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedRe(int row) {
		TTable table = this.getTTable(TABLE_RE);
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		int index = getTTabbedPane(TABLEPANLE).getSelectedIndex();
		// 医令细分类
		String orderCat1Type = parm.getValue("CAT1_TYPE");
		// 申请单号
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
	 * 行改变事件 add by huangtt 20150804  报告结果
	 */
	public void onSelRow() {
		onTableClickedRe(this.getTTable(TABLE_RE).getSelectedRow());
	}
	
	/**
	 * 清空 add by huangtt 20150804  报告结果
	 */
	public void onClearText() {
		this.clearValue("TEXT");
	}
	

	/**
	 * 得到TABLE
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
	 * 得到是否选中
	 * 
	 * @param tag
	 *            String
	 * @return boolean
	 */
	public boolean getRadioButtonFlg(String tag) {
		return getTRadioButton(tag).isSelected();
	}
	

	/**
	 * 返回TRadioButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}
	
	/**
	 * 拿到TTabbedPane
	 * 
	 * @param tag
	 *            String
	 * @return TTabbedPane
	 */
	public TTabbedPane getTTabbedPane(String tag) {
		return (TTabbedPane) this.getComponent(tag);
	}

	/**
	 * 得到TComboBox
	 * 
	 * @param tag
	 *            String
	 * @return TComboBox
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}


}
