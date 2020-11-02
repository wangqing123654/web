package com.javahis.ui.odi;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import jdo.odi.OdiMainTool;
import jdo.pha.PhaAntiTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.javahis.util.OrderUtil;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:סԺҽ��վ���ؿ���ҩ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author SHIBL
 * @version 4.0
 */
public class ODIInfecPackSheetControl extends TControl {
	/**
	 * �������
	 */
	private TParm parmmeter;

	private TTable tablem;

	private TTable table1;

	private String rxKind;

	private String caseNo;//�����
	
	private String mrNo;//������
	
	private Timestamp admDate;//��Ժ����
	
	private String ipdNo;//סԺ��
	
	private String patName;//��������

	private String type;
	
	private String packCode;
	
	private String icdCode;
	
	private String icdDesc;
	
	private String catCode;//�пڴ���
	
//	private int antiTakeDays = 0;//����ҩƷʹ������
	
	private int day=7;//����ҩƷʹ������
	
	private String radioFlg="";//����ĸ�radioButton��ѡ��
	
	private String arvFlg = "";//�Ƿ�ԽȨ���
	
	private String catDesc= "";
	/**
	 * 
	 * ��ʱ��ҩԤ��Ƶ��
	 */
	private String odiUddStatCode;
	/**
	 * ��ʱ����Ԥ��Ƶ��
	 */
	private String odiStatCode;
	/**
	 * ���ڴ���Ԥ��Ƶ��
	 */
	private String odiDefaFreg;
	/**
	 * Ԥ����Ƭ����
	 */
	private String dctTakeDays;
	/**
	 * Ԥ����Ƭʹ�ü���
	 */
	private String dctTakeQty;

	public void onInitParameter() {
		// ��������
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.parmmeter = new TParm();
		Object obj = this.getParameter();
		if (obj.toString().length() != 0 || obj != null) {
			this.parmmeter = (TParm) obj;
		}
		type = parmmeter.getValue("TYPE");// UDD���ڡ�ST��ʱ
		caseNo = parmmeter.getValue("CASE_NO");// add caoyong ��Ӹ�Ⱦ����
		mrNo = parmmeter.getValue("MR_NO");// add caoyong ��Ӹ�Ⱦ����
		admDate = parmmeter.getTimestamp("ADM_DATE");// add YANJ ��Ӹ�Ⱦ����
		ipdNo = parmmeter.getValue("IPD_NO");// add caoyong ��Ӹ�Ⱦ����
		patName = parmmeter.getValue("PAT_NAME");
		packCode = parmmeter.getValue("PACK_CODE");
		this.setValue("DESC", parmmeter.getValue("DESC"));
		// ��ʼ��
		onChangeStart();
	}

	/**
	 * ҳǩ�ı��¼� type ҳǩ���� W:���� OP Ԥ�� add caoyong 2013905
	 */
	public void onChangeStart() {
		onInitPage();// modify caoyong 2013805 ��ʼ��
		// TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (type.equals("UDD")) {
			icdCode = parmmeter.getValue("PACK_CODE1");
			icdDesc = parmmeter.getValue("DESC1");
			if (parmmeter.getValue("DESC").length()<=0) {//===pangben 2013-12-23 û���������
				this.callFunction("UI|WRDO|setEnabled", false);
				((TRadioButton) this.getComponent("OPRDO")).setSelected(true);
			}
			onQuery("W");// mofifby caoyong 2013805 ��ѯ��ͬ���͵�����
			this.onChang2();
			//��ѯ�Ƿ�������
			String sql1 = "SELECT PHA_PREVENCODE FROM SYS_OPERATIONICD WHERE OPERATION_ICD = '"+icdCode+"'";
			System.out.println("���sql is:::"+sql1);
			TParm resultparm = new TParm(TJDODBTool.getInstance().select(sql1));
			if(resultparm.getCount()<=0){
				this.callFunction("UI|OPRDO|setEnabled", false);
			}
		} else {
			String catType = "SELECT B.ID,B.CHN_DESC FROM SYS_OPERATIONICD A,SYS_DICTIONARY B " +
					"WHERE A.PHA_PREVENCODE = B.ID AND B.GROUP_ID = 'PHA_PREVEN' " +
					"AND A.OPERATION_ICD = '"+parmmeter.getValue("PACK_CODE")+"'";
			TParm catParm = new TParm(TJDODBTool.getInstance().select(catType));
			catCode = catParm.getValue("ID",0);
			catDesc =catParm.getValue("CHN_DESC",0);
			this.setValue("CAT_TYPE",catParm.getValue("CHN_DESC",0));
			onQuery("OP");// mofifby caoyong 2013805 ��ѯ��ͬ���͵�����
			this.onChang1();
		}
	}

	private void onInitPage() {
		// TODO Auto-generated method stub
		rxKind = parmmeter.getValue("RX_KIND");
		tablem = this.getTTable("TABLE");
		table1 = this.getTTable("TABLE1");
		// ��ʱ��ҩԤ��Ƶ��
		this.setOdiUddStatCode(getOdiSysParmData("UDD_STAT_CODE").toString());
		// ��ʱ����Ԥ��Ƶ��
		this.setOdiStatCode(getOdiSysParmData("ODI_STAT_CODE").toString());
		// ���ڴ���Ԥ��Ƶ��
		this.setOdiDefaFreg(getOdiSysParmData("ODI_DEFA_FREG").toString());
		// Ԥ����Ƭ����
		this.setDctTakeDays(getOpdSysParmData("DCT_TAKE_DAYS").toString());
		// Ԥ����Ƭʹ�ü���
		this.setDctTakeQty(getOpdSysParmData("DCT_TAKE_QTY").toString());
		// onQuery();
	}

	/**
	 *��ʱ ҳǩradiobutton�����¼�
	 * yanjing 20130930
	 */
	public void onChang1() {
		if(((TRadioButton) this.getComponent("WRDO")).isSelected()){
			TParm parmorder =null;
			TParm parm=new TParm();
			parm.setData("CASE_NO",caseNo);
			parm.setData("APPROVE_FLG","Y");//���ｨ��
			//parm.setData("USE_FLG","N");//ҽ��ʹ��״̬
			parmorder=PhaAntiTool.getInstance().queryPhaAnti(parm);
			if (parmorder.getErrCode() < 0) {
				this.messageBox("�ײ�ҽ����ѯ����");
				return;
			}
			TParm parmTable=new TParm();
			TParm ope_result=new TParm();
			String sql="";
			for (int i = 0; i < parmorder.getCount(); i++) {
				if (parmorder.getValue("USE_FLG",i).equals("Y")) {//ҽ���Ѿ�ʹ�õ�ҽ��
					sql="SELECT CASE_NO FROM ODI_ORDER WHERE CASE_NO='"+caseNo+
					"' AND RX_KIND='UD' AND ORDER_CODE='"+parmorder.getValue("ORDER_CODE",i)+"'";
					ope_result = new TParm(TJDODBTool.getInstance().select(sql));//��ѯ����ҽ���Ƿ���
					if (ope_result.getCount()>0) {//����ҽ���Ѿ����ڣ���ʱҽ�����Կ���һ��
						sql="SELECT CASE_NO,ORDER_DESC FROM ODI_ORDER WHERE CASE_NO='"+caseNo+
						"' AND RX_KIND='ST' AND ORDER_CODE='"+parmorder.getValue("ORDER_CODE",i)+"'";
						ope_result = new TParm(TJDODBTool.getInstance().select(sql));//У����ʱҽ���Ƿ��Ѿ�����
						if (ope_result.getCount()<=0) {
							parmTable.addRowData(parmorder, i);
						}
					}
				}else{
					parmTable.addRowData(parmorder, i);
				}
				if (this.rxKind.equals("ST")) {
					parmTable.setData("FREQ_CODE", i, getOdiUddStatCode());
				}
				if (this.rxKind.equals("DS")) {
					parmTable.setData("TAKE_DAYS", i, 1);
				}
			}
			parmTable.setCount(parmTable.getCount("ORDER_CODE"));
			table1.removeRowAll();
			table1.setParmValue(parmTable);
		}else if(((TRadioButton) this.getComponent("OPRDO")).isSelected()){
			TParm inparm = new TParm();
			String sql ="";
			this.setValue("LBL", "������");
			this.setValue("DESC", parmmeter.getValue("DESC"));
			this.setValue("CAT_TYPE", catDesc);
			//��������������ϵĴ��룬�ж��ײʹ���
			String sql1 = "SELECT PACK_CODE FROM ODI_INFEC_PACKM WHERE PHA_PREVENCODE = '"+catCode+"'";
			TParm resultparm = new TParm(TJDODBTool.getInstance().select(sql1));
			String packcode = resultparm.getValue("PACK_CODE", 0);
			sql = "SELECT 'N' AS APP_FLG,'N' AS FLG,A.PACK_CODE,A.SEQ_NO,A.DESCRIPTION,A.SPECIFICATION,A.ORDER_DESC,A.TRADE_ENG_DESC, "
				+ " A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.MEDI_QTY, MEDI_UNIT, "
				+ " ROUTE_CODE,FREQ_CODE,A.DCTEXCEP_CODE,A.TAKE_DAYS,B.PY1,B.PY2, "
				+ " B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, "
				+ " B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, "
				+ " B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, "
				+ " B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE, "
				+ " B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, "
				+ " B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, "
				+ " B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE,'N' AS APPROVE_FLG "
				+ " FROM ODI_INFEC_PACKORDER A,SYS_FEE B  WHERE PACK_CODE='"
				+ packcode
				+ "' AND A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "
				+ " ORDER BY A.PACK_CODE,A.SEQ_NO ";
			inparm = new TParm(TJDODBTool.getInstance().select(sql));
			for (int i = 0; i < inparm.getCount(); i++) {
				if (this.rxKind.equals("ST")) {
					inparm.setData("FREQ_CODE", i, getOdiUddStatCode());
				}
				if (this.rxKind.equals("DS")) {
					inparm.setData("TAKE_DAYS", i, 1);
				}
			}
			table1.removeRowAll();
		    table1.setParmValue(inparm);
//		    this.table1.setRowColor(0, new Color(255, 255, 132));
		}else if(((TRadioButton) this.getComponent("ORDO")).isSelected()){//���
			//radioFlg = "ORDO";//���Ʊ��
			this.setValue("LBL", "��ϣ�");
			this.setValue("DESC", parmmeter.getValue("DESC1"));
			TParm icdparm = parmmeter.getParm("ICDPARM");	
			this.setValue("CAT_TYPE", "");
			TParm inparm = this.getTableParm(icdparm);
			String packcode = inparm.getValue("PACK_CODE", 0);
			String sql = "SELECT C.TAKE_DAYS AS ANTI_TAKE_DAYS, 'N' AS APP_FLG,'N' AS FLG,A.PACK_CODE,A.SEQ_NO,A.DESCRIPTION,A.SPECIFICATION,A.ORDER_DESC,A.TRADE_ENG_DESC, "
				+ " A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.MEDI_QTY,A.MEDI_UNIT, "
				+ " A.ROUTE_CODE,A.FREQ_CODE,A.DCTEXCEP_CODE,A.TAKE_DAYS,B.PY1,B.PY2, "
				+ " B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, "
				+ " B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, "
				+ " B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, "
				+ " B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE, "
				+ " B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, "
				+ " B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, "
				+ " B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE,'N' AS APPROVE_FLG "
				+ " FROM ODI_INFEC_PACKORDER A,SYS_FEE B,SYS_ANTIBIOTIC C,PHA_BASE D WHERE PACK_CODE='"
				+ packcode
				+ "' AND A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "
				+ "  AND C.ANTIBIOTIC_CODE = D.ANTIBIOTIC_CODE AND D.ORDER_CODE = A.ORDER_CODE  "
				+ " ORDER BY A.PACK_CODE,A.SEQ_NO ";
			inparm = new TParm(TJDODBTool.getInstance().select(sql));
			table1.removeRowAll();
		    table1.setParmValue(inparm);
		}
		
	}
	/**
	 * ����ҳǩradiobutton�����¼�
	 */
	public void onChang2() {
		if(((TRadioButton) this.getComponent("CONS")).isSelected()){//������
			radioFlg = "CONS";//�������ı��
			TParm parmorder =null;
			TParm parm=new TParm();
			parm.setData("CASE_NO",caseNo);
			parm.setData("APPROVE_FLG","Y");//���ｨ��
			parm.setData("USE_FLG","N");//ҽ��ʹ��״̬
			parmorder=PhaAntiTool.getInstance().queryPhaAnti(parm);
			if (parmorder.getErrCode() < 0) {
				this.messageBox("�ײ�ҽ����ѯ����");
				return;
			}
			for (int i = 0; i < parmorder.getCount(); i++) {
				if(parmorder.getInt("ANTI_TAKE_DAYS", i) == 0){
//					this.messageBox("����д�������뵥!");
//					return;
				}else{
					day = parmorder.getInt("ANTI_TAKE_DAYS", i);
				if (this.rxKind.equals("ST")) {
					parmorder.setData("FREQ_CODE", i, getOdiUddStatCode());
				}
				if (this.rxKind.equals("DS")) {
					parmorder.setData("TAKE_DAYS", i, 1);
				}
				}
			}
			
			table1.removeRowAll();
			table1.setParmValue(parmorder);
			}else if(((TRadioButton) this.getComponent("OPRDO")).isSelected()){//����
				radioFlg = "OPRDO";//Ԥ�����
				this.setValue("DESC", icdDesc);
				this.setValue("NAME", "������");
				TParm inparm = new TParm();
				String sql ="";
				//��������������ϵĴ��룬�ж��ײʹ���
				String sql1 = "SELECT PHA_PREVENCODE FROM SYS_OPERATIONICD WHERE OPERATION_ICD = '"+icdCode+"'";
				TParm resultparm = new TParm(TJDODBTool.getInstance().select(sql1));
				String sql2 = "SELECT PACK_CODE FROM ODI_INFEC_PACKM WHERE PHA_PREVENCODE = '"+resultparm.getValue("PHA_PREVENCODE", 0)+"'";
				TParm resultparm1 = new TParm(TJDODBTool.getInstance().select(sql2));
				String packcode = resultparm1.getValue("PACK_CODE", 0);
				sql = "SELECT C.DESCRIPTION AS ANTI_TAKE_DAYS,'N' AS APP_FLG,'N' AS FLG,A.PACK_CODE,A.SEQ_NO,A.DESCRIPTION,A.SPECIFICATION,A.ORDER_DESC,A.TRADE_ENG_DESC, "
					+ " A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.MEDI_QTY,MEDI_UNIT, "
					+ " ROUTE_CODE,FREQ_CODE,A.DCTEXCEP_CODE,A.TAKE_DAYS,B.PY1,B.PY2, "
					+ " B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, "
					+ " B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, "
					+ " B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, "
					+ " B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE, "
					+ " B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, "
					+ " B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, "
					+ " B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE,'N' AS APPROVE_FLG "
					+ " FROM ODI_INFEC_PACKORDER A,SYS_FEE B,SYS_DICTIONARY C,ODI_INFEC_PACKM D WHERE A.PACK_CODE='"
					+ packcode
					+ "' AND A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "
					+ "  AND D.PHA_PREVENCODE=C.ID AND C.GROUP_ID='PHA_PREVEN' AND D.SMEALTYPE='OP' AND A.PACK_CODE=D.PACK_CODE  "
					+ " ORDER BY A.PACK_CODE,A.SEQ_NO ";
				inparm = new TParm(TJDODBTool.getInstance().select(sql));
				table1.removeRowAll();
			    table1.setParmValue(inparm);
//			    this.table1.setRowColor(0, new Color(255, 255, 132));
			}else if(((TRadioButton) this.getComponent("WRDO")).isSelected()){//���
				radioFlg = "WRDO";//���Ʊ��
				this.setValue("NAME", "��ϣ�");
				this.setValue("DESC", parmmeter.getValue("DESC"));
				TParm icdparm = parmmeter.getParm("ICDPARM");	
				TParm inparm = this.getTableParm(icdparm);
				String packcode = inparm.getValue("PACK_CODE", 0);
				String sql = "SELECT C.TAKE_DAYS AS ANTI_TAKE_DAYS, 'N' AS APP_FLG,'N' AS FLG,A.PACK_CODE,A.SEQ_NO,A.DESCRIPTION,A.SPECIFICATION,A.ORDER_DESC,A.TRADE_ENG_DESC, "
					+ " A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.MEDI_QTY,A.MEDI_UNIT, "
					+ " A.ROUTE_CODE,A.FREQ_CODE,A.DCTEXCEP_CODE,A.TAKE_DAYS,B.PY1,B.PY2, "
					+ " B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, "
					+ " B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, "
					+ " B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, "
					+ " B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE, "
					+ " B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, "
					+ " B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, "
					+ " B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE,'N' AS APPROVE_FLG "
					+ " FROM ODI_INFEC_PACKORDER A,SYS_FEE B,SYS_ANTIBIOTIC C,PHA_BASE D WHERE PACK_CODE='"
					+ packcode
					+ "' AND A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "
					+ "  AND C.ANTIBIOTIC_CODE = D.ANTIBIOTIC_CODE AND D.ORDER_CODE = A.ORDER_CODE  "
					+ " ORDER BY A.PACK_CODE,A.SEQ_NO ";
				inparm = new TParm(TJDODBTool.getInstance().select(sql));
				table1.removeRowAll();
			    table1.setParmValue(inparm);
			}
			
	}
	/**
	 * Ԥ����ѯ���� ����
	 */
	private TParm onQueryST(String typeRdo){
		String sql = "SELECT 'N' AS FLG,PACK_CODE,PACK_DESC FROM ODI_INFEC_PACKM WHERE SMEALTYPE='"
				+ typeRdo + "'ORDER BY PACK_CODE"; // modify caoyong 2013805
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0) {
			this.messageBox("��ѯ����");
			return parm;
		}
		return parm;
	}
	/**
	 * ���Ʋ�ѯ���� ���
	 */
	private TParm onQueryUDD(String typeRdo){
		TParm icdparm = parmmeter.getParm("ICDPARM");	
		TParm inparm = this.getTableParm(icdparm);
		return inparm;
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(String typeRdo) {
		TParm inparm=new TParm();
		if (type.equals("ST")) {//��ʱ ����
			inparm=onQueryST(typeRdo);
		}else if (type.equals("UDD")){//���� UDD ���
			if (((TRadioButton) this.getComponent("WRDO")).isSelected()) {
				inparm=onQueryUDD(typeRdo);
			}
		}
		if (inparm.getErrCode()<0) {
			return;
		}
	}

	/**
	 * �������ݲ�ѯ
	 * @param parm
	 * @return
	 */
	private TParm getTableParm(TParm icdparm) {
		if (icdparm.getCount() <= 0)
			return icdparm;
		String sql = "SELECT DISTINCT  A.PACK_CODE,B.PACK_DESC,'N' AS FLG "
				+ " FROM ODI_INFEC_PACKICD A,ODI_INFEC_PACKM B"
				+ " WHERE A.PACK_CODE=B.PACK_CODE AND B.SMEALTYPE='W' AND (";
		String line = "";
		for (int i = 0; i < icdparm.getCount(); i++) {
			TParm parmRow = icdparm.getRow(i);
			String icd = parmRow.getValue("ICD_CODE1");
			if (line.length() > 0) {
				line += " OR ";
			}
			line += " '" + icd + "'"
					+ " BETWEEN A.ICD_CODE_BEGIN AND A.ICD_CODE_END";
		}
		TParm check = new TParm(TJDODBTool.getInstance().select(
				sql + line + ")"));
		if (check.getErrCode() < 0){
			this.messageBox("��ѯ����");
			return check;
		}
		return check;
	}
	/**
	 * ����
	 */
	public void onSend() {
		String anti_flg = "02";//���ƣ����ÿ�����ʶ
		this.table1.acceptText();// modify caoyong 201394
		TParm parm = table1.getParmValue();// modify caoyong 201394
		int rowCount = parm.getCount();
		TParm result=new TParm();
		if ((type.equals("UDD")&&((TRadioButton) this.getComponent("OPRDO")).isSelected())) {
			String sql="SELECT TIMEOUT_DATE FROM OPE_OPBOOK WHERE CASE_NO = '"+caseNo+"' " +
			"AND OP_CODE1 = '"+parmmeter.getValue("PACK_CODE1")+"' " +
					"ORDER BY TIMEOUT_DATE DESC";//TIME OUT ʱ��=====pangben 201403-4 
			TParm opeResult = new TParm(TJDODBTool.getInstance().select(sql));
			if (opeResult.getCount() > 0) {
				result.addData("OP_START_DATE", opeResult.getData(
						"TIMEOUT_DATE", 0));
			} else {
				sql = "SELECT OP_START_DATE FROM OPE_OPDETAIL WHERE CASE_NO = '"
						+ caseNo+"' AND OP_CODE1 = '"
						+ parmmeter.getValue("PACK_CODE1")+"' ORDER BY OP_START_DATE DESC";
				TParm ope_result = new TParm(TJDODBTool.getInstance().select(
						sql));
				if (ope_result.getCount() > 0) {
					result.addData("OP_START_DATE", ope_result.getData(
							"OP_START_DATE", 0));// ������Ƥʱ��
				} else {
					result.addData("OP_START_DATE", SystemTool.getInstance()
							.getDate());
				}
			}
			anti_flg = "01";//Ԥ�������ÿ�����ʶ
//			System.out.println("qwertyͣ��ʱ�� is������"+ope_result.getData("OP_START_DATE", 0));
//			System.out.println("qwertyͣ��ʱ�� is������"+ope_result.getData("OP_START_DATE"));
		}
		if ((type.equals("UDD")&&((TRadioButton) this.getComponent("WRDO")).isSelected())) {//����Ĭ������
//			// ���֤��
//			Object obj = parm.getData("LCS_CLASS_CODE");
//			if (obj != null && obj.toString().length() != 0) {
//				if (!OrderUtil.getInstance().checkLcsClassCode(Operator.getID(),
//						"" + obj)) {
//					if(messageBox("��ʾ��Ϣ Tips", "�Ƿ�ԽȨ? ",
//							this.YES_NO_OPTION)!= 0){//��
//						arvFlg = "NO";
//					}else{//��
//						arvFlg = "YES";
//					}
//				}
//			}
			
		}else if(type.equals("ST")&&(((TRadioButton) this.getComponent("ORDO")).isSelected()||
				((TRadioButton) this.getComponent("OPRDO")).isSelected())){//��ʱҽ������ϻ�Ԥ��
			String sql = "";
//			TParm ope_result = null;
			for (int i = 0; i < rowCount; i++) {
				TParm temp = parm.getRow(i);
				if (!temp.getBoolean("FLG")) {
					continue;
				}// =======pangben 2014-4-25 Ԥ����ҩһ������������ܿ�һ����ʱҽ����Ƥ����ҩ������
//				sql = "SELECT CASE_NO,ORDER_DATE FROM ODI_ORDER WHERE CASE_NO='"
//						+ caseNo
//						+ "' AND RX_KIND='UD' AND ORDER_CODE='"
//						+ temp.getValue("ORDER_CODE") + "'";
//				ope_result = new TParm(TJDODBTool.getInstance().select(sql));// ��ѯ����ҽ���Ƿ���
//				if (ope_result.getCount() > 0) {
//					sql = "SELECT CASE_NO,ORDER_DESC FROM ODI_ORDER WHERE CASE_NO='"
//							+ caseNo
//							+ "' AND RX_KIND='ST' AND ORDER_CODE='"
//							+ temp.getValue("ORDER_CODE")
//							+ "' AND ROUTE_CODE<>'PS' AND ORDER_DATE>TO_DATE('"
//							+ SystemTool.getInstance().getDateReplace(ope_result.getValue("ORDER_DATE",0), true)+"','YYYYMMDDHH24MISS')"
//							+ " AND ANTIBIOTIC_WAY='01'";
//				} else {
//					sql = "SELECT CASE_NO,ORDER_DESC FROM ODI_ORDER WHERE CASE_NO='"
//							+ caseNo
//							+ "' AND RX_KIND='ST' AND ORDER_CODE='"
//							+ temp.getValue("ORDER_CODE")
//							+ "' AND ROUTE_CODE<>'PS' " 
//							+ " AND ANTIBIOTIC_WAY='01' ";
//				}
//				System.out.println("Ԥ����ҩ"+sql);
//				ope_result = new TParm(TJDODBTool.getInstance().select(sql));// У����ʱҽ���Ƿ��Ѿ�����
//				// �������ҽ���Ѿ�������ʱҽ��������һ��
//				if (ope_result.getCount() > 0) {
//					this.messageBox(ope_result.getValue("ORDER_DESC", 0)
//							+ "Ԥ����ҩֻ�ܿ���һ��");
//					return;
//				}
				//===start===add by kangy 20170718
				String skiNoSql = "SELECT MAX(A.OPT_DATE),A.BATCH_NO,A.SKINTEST_NOTE,B.ORDER_DESC "
					+ "FROM PHA_ANTI A, ODI_ORDER B WHERE A.CASE_NO= '"
					+ caseNo
					+ "' AND A.ORDER_CODE= '"
					+ temp.getValue("ORDER_CODE")
					+ "' "
					+ "AND  A.BATCH_NO IS NOT NULL " 
					+" AND A.ORDER_CODE=B.ORDER_CODE "
					+ "GROUP BY A.BATCH_NO ,A.SKINTEST_NOTE,A.OPT_DATE,B.ORDER_DESC " 
					+ "ORDER BY A.OPT_DATE";
			TParm skiNoParm = new TParm(TJDODBTool.getInstance().select(skiNoSql));
			if(skiNoParm.getValue("SKINTEST_NOTE", 0).equals("1")){
			if(messageBox("��ʾ��Ϣ Tips", skiNoParm.getValue("ORDER_DESC", 0)+"Ƥ�Խ��Ϊ���ԣ��Ƿ��������",
					this.YES_NO_OPTION)!= 0){//��
			return;
		}
		//===end=== add by kangy 20170718
			}
			}
		}
		
		if (type.equals("ST")) {
			anti_flg = "01";//Ԥ��	
		}
		for (int i = 0; i < rowCount; i++) {
			day = TypeTool.getInt(table1.getItemData(i, "ANTI_TAKE_DAYS"));//���ÿ����ص�Ĭ������
			TParm temp = parm.getRow(i);
			String skintest_note = "";//Ƥ�Ա�ע��Ϣ
			String order_code = parm.getValue("ORDER_CODE", i);//ÿһ�е�ҩ������
			String tempLinkNo = temp.getValue("LINK_NO");
			if ("N".equals(temp.getValue("FLG")))
				continue;
			if (null!=tempLinkNo && tempLinkNo.length()>0) {
				for(int j = 0;j<rowCount; j++){
					String linkNo = parm.getValue("LINK_NO", j);
					if(("N".equals(parm.getValue("FLG",j))&&(tempLinkNo.equals(linkNo)))){
						this.messageBox("��������δ���ء�");
						return;
					}
				}
			}
			//��Ϊ����ҽ����Ƥ��ҩƷ�����ݾ���ź�ҩ�������ѯ���һ�ε�Ƥ�Խ����Ƥ������
			if(type.equals("UDD")){
				//��ѯ�Ƿ�ΪƤ��ҩƷ
				String skiSql= "SELECT SKINTEST_FLG, ANTIBIOTIC_CODE" +
				" FROM PHA_BASE  WHERE ORDER_CODE = '"+order_code+"' ";
//				System.out.println("��ΪƤ��ҩƷ"+skiSql);
				TParm result1= new TParm(TJDODBTool.getInstance().select(skiSql));
				if(result1.getCount() <=0){
					this.messageBox("�ֵ䵵�����ڸ�ҩƷ��");
					return;
				} else if (result1.getValue("SKINTEST_FLG", 0).equals("Y")) {// Ƥ��ҩƷ����ѯƤ�����ż����
					String skiNoSql = "SELECT MAX(OPT_DATE),BATCH_NO,SKINTEST_NOTE "
							+ "FROM PHA_ANTI WHERE CASE_NO= '"
							+ caseNo
							+ "' AND ORDER_CODE= '"
							+ order_code
							+ "' "
							+ "AND  BATCH_NO IS NOT NULL "
							+ "GROUP BY BATCH_NO ,SKINTEST_NOTE,OPT_DATE "
							+ "ORDER BY OPT_DATE";
					// System.out.println("Ƥ�Բ�ѯ��䣺"+skiNoSql);
					TParm skiNoParm = new TParm(TJDODBTool.getInstance()
							.select(skiNoSql));
					if (skiNoParm.getCount() <= 0) {// ��ҩƷ������Ƥ�����ż����
						skintest_note = "";// Ƥ�Խ��
					} else {// ����
						String skiResult = "";
						if(skiNoParm.getValue("SKINTEST_NOTE", 0).equals("0")){//����
							skiResult = "(-)����";
						}else if(skiNoParm.getValue("SKINTEST_NOTE", 0).equals("1")){
							skiResult = "(+)����";
						}
						skintest_note = "����:"
								+ skiNoParm.getValue("BATCH_NO", 0) + ";Ƥ�Խ��:"
								+ skiResult;
					}
				}
			}
			result.addRowData(parm, i);
			result.addData("SKINTEST_NOTE", skintest_note);//Ƥ�Ա�ע
			result.addData("ARV_FLG", arvFlg);//�Ƿ�ԽȨ���
			result.addData("RX_KIND", rxKind);
			result.addData("PHA_ANTI_FLG", "Y");//����״̬
			result.addData("PHA_DS_DAY", day);//ͣ��ʱ��
			result.addData("ANTI_FLG", anti_flg);//������ʶ
			result.addData("RADIO_FLG", radioFlg);//radioButton ѡ�б��
			result.addData("OP_START_DATE", result.getData("OP_START_DATE", 0));//
			result.addData("DISPENSE_FLG", "N");//��ҩ
		}
		if (result.getCount("ORDER_CODE")<=0) {
			this.messageBox("��ѡ����Ҫ���ص�����");
			return;
		}
		result.setData("PHA_APPROVE_FLG",null!=result.getValue("APPROVE_FLG",0)&&result.getValue("APPROVE_FLG",0).equals("Y")?"Y":"N");
		
		this.setReturnValue(result);
//		System.out.println("111fff���ܷ���ֵ����"+result);
		//this.parmmeter.runListener("INSERT_TABLE", orderList);
		this.closeWindow();
	}

	/**
	 * ȫѡ
	 */
	public void onSelAll() {
		this.getTTable("TABLE1").acceptText();
		TParm parm = this.getTTable("TABLE1").getParmValue();
		int rowCount = parm.getCount();
		for (int i = 0; i < rowCount; i++) {
			if (parm.getBoolean("FLG", i)) {
				parm.setData("FLG", i, false);
			} else {
				parm.setData("FLG", i, true);
			}
		}
		this.getTTable("TABLE1").setParmValue(parm);
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

	public String getOdiUddStatCode() {
		return odiUddStatCode;
	}

	public void setOdiUddStatCode(String odiUddStatCode) {
		this.odiUddStatCode = odiUddStatCode;
	}

	public String getOdiStatCode() {
		return odiStatCode;
	}

	public void setOdiStatCode(String odiStatCode) {
		this.odiStatCode = odiStatCode;
	}

	public String getOdiDefaFreg() {
		return odiDefaFreg;
	}

	public void setOdiDefaFreg(String odiDefaFreg) {
		this.odiDefaFreg = odiDefaFreg;
	}

	public String getDctTakeDays() {
		return dctTakeDays;
	}

	public void setDctTakeDays(String dctTakeDays) {
		this.dctTakeDays = dctTakeDays;
	}

	public String getDctTakeQty() {
		return dctTakeQty;
	}

	public void setDctTakeQty(String dctTakeQty) {
		this.dctTakeQty = dctTakeQty;
	}

	/**
	 * �õ��������
	 * 
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object getOpdSysParmData(String key) {
		return OdiMainTool.getInstance().getOpdSysParmData(key);
	}
	 /**
     * �ر��¼�
     * @return boolean
     */
    public boolean onClosing(){
        TParm parm = new TParm();
        parm.setData("YYLIST","Y");
        this.parmmeter.runListener("INSERT_TABLE_FLG",parm);
        return true;
    }
	/**
	 * �õ�סԺ����
	 * 
	 * @param key
	 *            String
	 * @return Object
	 */
	public Object getOdiSysParmData(String key) {
		return OdiMainTool.getInstance().getOdiSysParmData(key);
	}
}
