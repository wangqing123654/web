package com.javahis.ui.udd;
 
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.mail.Message;

import jdo.adm.ADMInpTool;
import jdo.bil.BILTool;
import jdo.ekt.EKTIO;
import jdo.ind.INDTool;
import jdo.inw.InwOrderExecTool;
import jdo.pha.TXNewATCTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SysPhaBarTool;
import jdo.sys.SystemTool;
import jdo.udd.UddChnCheckTool;
import jdo.udd.UddDispatchTool;
import jdo.util.Manager;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;
import com.sun.media.ui.MessageBox;

/**
 * <p>
 * Title: סԺҩ����ҩ��ҩ
 * </p>
 * 
 * <p>
 * Description: סԺҩ����ҩ��ҩ
 * </p>
 * 
 * <p>     
 * Copyright: javahis 2008   
 * </p>
 * 
 * <p>   
 * Company:
 * </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class UddMedDispense extends TControl {
	public static final String Y = "Y";
	public static final String N = "N";
	public static final String NULL = "";
	private TTable tblPat;
	private TTable tblMed;
	private TTable tblDtl;
	private TTable tblSht;
	private List execList;
	private TParm saveParm;
	// ��ҩ��ϸ������
	private int detailCount = 0;
	// ��ҩ��ϸ��ӡ����
	private int printCount = 0;
	// private static final String INIT_PAT_SQL =
	// "SELECT 'N' AS EXEC,A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO, A.PHA_DISPENSE_NO "
	// + " FROM ODI_DSPNM A , SYS_PATINFO B ,SYS_BED C "
	// + " WHERE B.MR_NO=A.MR_NO "
	// + " AND C.BED_NO=A.BED_NO      "
	// + " AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') "
	// +
	// " AND (C.BED_OCCU_FLG  IS  NULL OR C.BED_OCCU_FLG='N')      AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')      AND A.DISPENSE_FLG='N'";
	private boolean isDosage;
	private String controlName;
	private String charge;
	private boolean isCheckNeeded;
	private TTextFormat queryBed;
	
	/**
	 *  add by wukai on 20161117 ��ӡǰ�Ƿ���Ҫ���в�ѯ
	 */
	private boolean queryFlg = true;
	
	public UddMedDispense() {
		execList = new ArrayList();
		saveParm = new TParm();
	}
   
	public void onInitParameter() {    
		
		controlName = (new StringBuilder()).append(getParameter()).append("")
				.toString();
		// ===zhangp 20120809 start
		String sql = "SELECT CTRL_FLG FROM SYS_OPERATOR WHERE USER_ID = '"
				+ Operator.getID() + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TComboBox pha_ctrlcodeCombo = (TComboBox) getComponent("PHA_CTRLCODE");
		if (!parm.getValue("CTRL_FLG", 0).equals("Y")) {
			pha_ctrlcodeCombo.setEnabled(false);
		}   
		// ===zhangp 20120809 end
		if ("DOSAGE".equalsIgnoreCase(controlName)) { 
			setTitle("����ҩ����");
			// �����Ͱ�ҩ��ѡ��
			callFunction("UI|ALLATCDO|setVisible", true);
			this.callFunction("UI|ATC_MACHINENO_L|setVisible", true);

			this.callFunction("UI|ATC_MACHINENO|setVisible", true);  

			this.callFunction("UI|ATC_TYPE_L|setVisible", true);

			this.callFunction("UI|ATC_TYPE|setVisible", true);
		} else {
			setTitle("����ҩ��ҩ");
			this.callFunction("UI|ATC|setVisible", false);
		}
	}

	public void onInit() {
		super.onInit();
		callFunction("UI|TBL_PAT|addEventListener", new Object[] {
				"table.checkBoxClicked", this, "onTableCheckBoxClicked" });
		callFunction("UI|TBL_DTL|addEventListener", new Object[] {
				"table.checkBoxClicked", this, "onTable2CheckBoxClicked" });
		// �������tableע��CHECK_BOX_CLICKED��������¼�
		this.callFunction("UI|TBL_DTL|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onDownTableCheckBoxChangeValue");
		// // ���tableע��CHECK_BOX_CLICKED��������¼�
		// this.callFunction("UI|TBL_PAT|addEventListener",
		// TTableEvent.CHECK_BOX_CLICKED, this,
		// "onPatTableCheckBoxChangeValue");
		isDosage = TypeTool.getBoolean(TConfig.getSystemValue("IS_DOSAGE"));
		isCheckNeeded = TypeTool.getBoolean(TConfig.getSystemValue("IS_CHECK"));
		tblPat = (TTable) getComponent("TBL_PAT");
		tblMed = (TTable) getComponent("TBL_MED");
		tblDtl = (TTable) getComponent("TBL_DTL");	
		TTable TBL_DTL = (TTable) getComponent("TBL_DTL");	
		TBL_DTL.addEventListener("TBL_DTL->" + TTableEvent.CHANGE_VALUE, this,
		"onDownTableBatchNoChangeValue");										
		if (!"DOSAGE".equalsIgnoreCase(controlName)) {
			tblDtl.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17");
		}
		tblSht = (TTable) getComponent("TBL_SHT");
		queryBed = (TTextFormat) getComponent("QUERY_BED");
		charge = TConfig.getSystemValue("CHARGE_POINT");
		onClear();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		java.sql.Timestamp t = TJDODBTool.getInstance().getDBTime();
		setValue("START_DATE", t);
		setValue("END_DATE", StringTool.rollDate(t, 1L));
		setValue("ST", "Y");
		setValue("EXEC_DEPT_CODE", Operator.getDept());
		setValue("AGENCY_ORG_CODE", "");
		setValue("STA", "Y");
		setValue("STATION", "");
		setValue("UNCHECK", "Y");
		setValue("NO", "");
		setValue("NAME", "");
		// setValue("DOSE", "N");
		// TTextFormat cblDose = (TTextFormat) getComponent("CBL_DOSE");
		// setValue("CBL_DOSE", "");
		// cblDose.setEnabled(false);
		setValue("PHA_DISPENSE_NO", "");
		setValue("UNDONE", "Y");
		setValue("PPLT", "");
		setValue("EXEC_ALL", Boolean.valueOf(false));
		setValue("QUERY_BED", "");
		this.setValue("ATC_MACHINENO", "");
		this.setValue("ATC_TYPE", "");
		queryBed.setVisible(false);
		callFunction("UI|NO|setVisible", new Object[] { Boolean.valueOf(true) });
		tblPat.removeRowAll();
		tblDtl.removeRowAll();
		tblMed.removeRowAll();
		tblSht.removeRowAll();
		if (TypeTool.getBoolean(getValue("UNCHECK"))) {
			callFunction("UI|save|setEnabled", new Object[] { Boolean
					.valueOf(true) });
			callFunction("UI|delete|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|dispense|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|CONFIRM_BUT|setEnabled", new Object[] { Boolean
					.valueOf(false) });
		} else {
			callFunction("UI|save|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|delete|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|CONFIRM_BUT|setEnabled", new Object[] { Boolean
					.valueOf(true) });
			callFunction("UI|dispense|setEnabled", new Object[] { Boolean
					.valueOf(true) });
		}

		this.setValue("DOSE_TYPEALL", "Y");
		this.setValue("DOSE_TYPEO", "Y");
		this.setValue("DOSE_TYPEE", "Y");
		this.setValue("DOSE_TYPEI", "Y");
		this.setValue("DOSE_TYPEF", "Y");
		this.setValue("DOSE_TYPEF", "Y");
		this.setValue("DGT_NO", "");
		this.setValue("COMBO", "");	
		//alert by wukai ������ҩĬ�Ϲ�ѡ
		//fux modify 20171204 ������Һ Ĭ�Ϲ�ѡȡ��
		this.setValue("LINK_NO", "N");
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		String getDoseType = "";
		List list = new ArrayList();
		if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
			list.add("O");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
			list.add("E");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
			list.add("I");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
			list.add("F");
		}
		if (list == null || list.size() == 0) {
			this.messageBox("��ѡ����ͷ���");
			return;
		} else {
			getDoseType = " AND F.CLASSIFY_TYPE IN (";
			for (int i = 0; i < list.size(); i++) {
				getDoseType = getDoseType + "'" + list.get(i) + "' ,";
			}
			getDoseType = getDoseType.substring(0, getDoseType.length() - 1)
					+ ")";
		}
		if (TypeTool.getBoolean(getValue("UNCHECK"))) {
			callFunction("UI|save|setEnabled", new Object[] { Boolean
					.valueOf(true) });
			callFunction("UI|delete|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|dispense|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|CONFIRM_BUT|setEnabled", new Object[] { Boolean
					.valueOf(false) });
		} else {
			callFunction("UI|save|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|delete|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|CONFIRM_BUT|setEnabled", new Object[] { Boolean
					.valueOf(true) });
			callFunction("UI|dispense|setEnabled", new Object[] { Boolean
					.valueOf(true) });
		}
		StringBuffer sql = new StringBuffer();
		sql
				.append(
						"SELECT 'N' AS EXEC,A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO, A.PHA_DISPENSE_NO, "
						//fux modify 20150311
						//+ "A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE,G.USER_NAME AS USER_NAME1 "
								+ "A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE "
								//fux modify 20171204 ���� adm_inp���� ����case_no��bed_no
								+ "FROM ODI_DSPNM A , SYS_PATINFO B ,SYS_BED C, SYS_PHAROUTE F,SYS_OPERATOR G " 
								//+" ,ADM_INP H "
								+ "WHERE B.MR_NO=A.MR_NO "
								+ "AND C.BED_NO=A.BED_NO "
								+ "AND A.ORDER_DR_CODE=G.USER_ID "
								+ "AND A.ROUTE_CODE=F.ROUTE_CODE " 
								//+ "AND A.CASE_NO = H.CASE_NO " 
							    //+ "AND A.BED_NO= H.BED_NO "
								// +
								// " AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') "
								// +
								// "  AND (C.BED_OCCU_FLG IS  NULL OR C.BED_OCCU_FLG='N') "

								+ " AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')      AND A.DISPENSE_FLG='N'")
				.append(getWhere()).append(getDoseType);
		String sqlStr = sql.toString();
		if (!"".equals(getValueString("DGT_NO")))
			sqlStr = (new StringBuilder()).append(sqlStr).append(
					" AND A.PHA_DISPENSE_NO = '").append(
					getValueString("DGT_NO")).append("'").toString();
		// ===========pangben modify 20110511 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			sqlStr += " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ====zhangp 20121115 start
//		if ("Y".equals(this.getValueString("LINK_NO"))) {  
//			sqlStr += " AND A.LINK_NO IS NOT NULL ";
//		}
		
//		// ==== wukai 20161223 start
//		if ("N".equals(this.getValueString("LINK_NO"))) {
//			sqlStr += " AND A.LINK_NO IS NULL "; 
//		}
//		// ==== wukai 20161223 end
		
		//=== wukai PIVAs���� start
 		if("Y".equals(this.getValueString("LINK_NO"))){
			sqlStr += " AND A.LINK_NO IS NOT NULL AND A.IVA_FLG='Y' ";
		}else{
			sqlStr +=" AND (A.IVA_FLG IS NULL OR A.IVA_FLG = 'N') ";
		}
 		//=== wukai PIVAs���� end
		/*****************update by liyh ���ӷ�ҩ���� 20130603 start********************/
		//ȫԺ��0��סԺ��2��������ҩ��1
/*	    if (getRadioButton("IN_HOSPITAL").isSelected()) {//סԺ��ҩ
	    	sqlStr += " AND A.TAKEMED_ORG='2' ";
		}
	    if (getRadioButton("IN_STATION").isSelected()) {//������ҩ
	    	sqlStr += " AND A.TAKEMED_ORG='1' ";
		}*/
	    /*****************update by liyh ���ӷ�ҩ���� 20130603 end********************/	
		// ====zhangp 20121115 end
		sqlStr = (new StringBuilder())
				.append(sqlStr)
				// .append(" GROUP BY A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.PHA_DISPENSE_NO,A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE ORDER BY A.BED_NO,A.CASE_NO, A.PHA_DISPENSE_NO")
				.append(
						" GROUP BY A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.PHA_DISPENSE_NO,"
						//fux modify 20150311
						//+ "A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE,G.USER_NAME "
								+ "A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE "
								+ " ORDER BY A.BED_NO,A.CASE_NO, A.PHA_DISPENSE_NO")
				.toString();
				 
	    //System.out.println("��ҩ��ѯsqlStr---" + sqlStr);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sqlStr));  
		tblPat.setParmValue(parm);
		if (StringTool.getBoolean(getValueString("MR"))   
				|| StringTool.getBoolean(getValueString("BED")))
			setValue("NAME", parm.getValue("PAT_NAME", 0));  
		tblPat.setParmValue(parm);
		Map map=new HashMap();
		if(parm.getCount()>0){
			for(int i=0;i<parm.getCount();i++){
				TParm parmRow=parm.getRow(i);
				map.put(parmRow.getValue("MR_NO"), parmRow.getValue("MR_NO"));
			}
			this.setValue("SUM_TOT", ""+map.size());
		}else{
			this.setValue("SUM_TOT",""+0);
		}
		onQueryDtl();
		onQueryMed();
	}

	/**
	 * ��CASE_NO����ҩƷ����ʼ��ͳҩ��TABLE
	 */
	public void onQueryMed() {
		String doseType = getDoseType();
		if ("".equals(doseType)) {
			return;
		}
		TParm parm = new TParm();
		// ===========pangben modify 20110511 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ====zhangp 20120803 start
		String tables = "";
		String conditions = "";
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("")) {
			tables = " , PHA_BASE H, SYS_CTRLDRUGCLASS I ";
			conditions = " AND C.ORDER_CODE = H.ORDER_CODE"
					+ " AND H.CTRLDRUGCLASS_CODE = I.CTRLDRUGCLASS_CODE"
					+ " AND I.CTRL_FLG = '" + pha_ctrlcode + "'";
		}
		// ====zhangp 20120803 end
		// ===zhangp 20121115 start
//		// ==== wukai 20161223 start
//		if ("N".equals(this.getValueString("LINK_NO"))) {  
//			//conditions += " AND A.LINK_NO IS NOT NULL ";
//			conditions += " AND A.LINK_NO IS NULL ";
//		}
//		// ==== wukai 20161223 end
		/*****************update by liyh ���ӷ�ҩ���� 20130603 start********************/
		//ȫԺ��0��סԺ��2��������ҩ��1
	    String dispenseOrgCodeSql = " AND A.EXEC_DEPT_CODE='"+this.getValueString("EXEC_DEPT_CODE")+"' ";
	    if (getRadioButton("IN_HOSPITAL_ONE").isSelected()) {//סԺ��ҩ
	    	dispenseOrgCodeSql = " AND A.TAKEMED_ORG='2' ";
		}
	    if (getRadioButton("IN_STATION_ONE").isSelected()) {//������ҩ
	    	dispenseOrgCodeSql = " AND A.TAKEMED_ORG='1' ";
		}
	    /*****************update by liyh ���ӷ�ҩ���� 20130603 end********************/		
//		if (TypeTool.getBoolean(getValue("ST"))) {
//			conditions += " AND A.TAKEMED_ORG = '2' ";
//		}
		// ===zhangp 20121115 end
		// ===========pangben modify 20110511 stop
	    
	    //20150405 wangjingchun add start
	    //��ҩ�� ���������ƻ�PIVAs����
	    String piavsSql = "";
	    if("Y".equals(this.getValueString("LINK_NO"))){
	    	piavsSql += " AND A.IVA_FLG='Y' ";
	    }else if("N".equals(this.getValueString("LINK_NO"))){
	    	piavsSql += " AND (A.IVA_FLG IS NULL OR A.IVA_FLG='N') ";
	    }
	    //20150405 wangjingchun add end
	    
		if (this.getRadioButton("BY_ORDER").isSelected()) {
			// ��ҩƷ��ʾͳҩ��
			String sql = (new StringBuilder())
					.append(
							"  SELECT A.ORDER_DESC || ' ' || C.SPECIFICATION AS ORDER_DESC,"// ¬��2012-4-6  
							// ɾ����Ʒ��
									// A.GOODS_DESC
									// ||
									+ "SUM( A.DOSAGE_QTY) AS DISPENSE_QTY,A.DOSAGE_UNIT AS DISPENSE_UNIT, "
									+ "CASE WHEN B.SERVICE_LEVEL = 2 THEN C.OWN_PRICE2 WHEN B.SERVICE_LEVEL = 3 THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE, "
									+ "A.ORDER_CODE ,  "
									+ "CASE WHEN B.SERVICE_LEVEL = 2 THEN SUM (DOSAGE_QTY * C.OWN_PRICE2) WHEN B.SERVICE_LEVEL = 3 THEN SUM (DOSAGE_QTY * C.OWN_PRICE3) ELSE SUM (DOSAGE_QTY * C.OWN_PRICE) END AS OWN_AMT ," 
//									+" CASE WHEN A.TAKEMED_ORG=2 THEN 'סԺҩ��' ELSE '����' END AS  TAKEMED_ORG "
									+ " A.TAKEMED_ORG " 
									// ===zhangp 20120802 start
									// +
									// "FROM   ODI_DSPNM A, ADM_INP B ,SYS_FEE C, SYS_PATINFO D, SYS_PHAROUTE F  WHERE   A.CASE_NO = B.CASE_NO   AND A.MR_NO = D.MR_NO AND A.ROUTE_CODE=F.ROUTE_CODE AND A.CASE_NO IN (")
									+ "FROM   ODI_DSPNM A, ADM_INP B ,SYS_FEE C, SYS_PATINFO D, SYS_PHAROUTE F  "
									+ tables
									+ "WHERE   A.CASE_NO = B.CASE_NO   AND A.MR_NO = D.MR_NO AND A.ROUTE_CODE=F.ROUTE_CODE "
									+ piavsSql
									+ conditions 
									+ "AND A.CASE_NO IN (")
					.append(getCaseNos())
					.append(")")
					//fux modify 20161227 ���벡�� 
					.append(" AND A.STATION_CODE IN ("+getStationCodes()+") " )
					.append(" AND A.ORDER_CODE=C.ORDER_CODE ")
					// BY liyh 20120905 ���˵��Ѿ�ͣҽ����ҩƷ
					.append(
							"  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') ")
					.append("  AND A.DISPENSE_FLG='N' #").append("  AND #")
					//update by liyh ���ӷ�ҩ���� 20130603
					.append(dispenseOrgCodeSql)
					.toString();
			// ===zhangp 20120802 end
			String group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, C.OWN_PRICE,A.GOODS_DESC,B.SERVICE_LEVEL, C.OWN_PRICE2, C.OWN_PRICE3, C.SPECIFICATION, A.TAKEMED_ORG " +
					" ORDER BY A.TAKEMED_ORG, A.ORDER_CODE ";
			// ===zhangp 20121115 start
			if ("Y".equals(this.getValueString("LINK_NO"))) {
				//===wukai modify 20170112 ͳҩ���������� start
				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, C.OWN_PRICE, A.GOODS_DESC, B.SERVICE_LEVEL, C.OWN_PRICE2, C.OWN_PRICE3, C.SPECIFICATION, A.TAKEMED_ORG " +
						" ORDER BY  A.TAKEMED_ORG, A.ORDER_CODE ASC ";
			}
			// ===zhangp 20121115 end
			if (TypeTool.getBoolean(getValue("ST")))
				sql = sql.replaceFirst("#",
						" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F') ");
			else if (TypeTool.getBoolean(getValue("UD")))
				sql = sql.replaceFirst("#", " AND A.DSPN_KIND='UD'");
			else
				sql = sql.replaceFirst("#", " AND A.DSPN_KIND='DS'");
			if (TypeTool.getBoolean(getValue("UNCHECK"))) {
				if ("DOSAGE".equalsIgnoreCase(controlName)) {
					sql = sql
							.replaceFirst(
									"#",
									"A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL");
					// String bed_no = getBedNo();
					// if (!"".equals(bed_no) && !"''".equals(bed_no)) {
					// sql += "  AND A.BED_NO IN (" + bed_no + ") ";
					// }
				} else {
					sql = sql
							.replaceFirst(
									"#",
//									"A.PHA_DISPENSE_CODE IS NULL AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL");
									"A.PHA_DISPENSE_CODE IS NULL AND A.PHA_DOSAGE_CODE IS NOT NULL ");//modify by wangjc 20171227 ��������ͣҽ�����·�ҩ�����޷���ѯ������
					String dispense_no = getPhaDispenseNo();
					if (!"".equals(dispense_no) && !"''".equals(dispense_no))
						sql = (new StringBuilder()).append(sql).append(
								"  AND A.PHA_DISPENSE_NO IN (").append(
								dispense_no).append(")").toString();
				}
			} else if ("DOSAGE".equalsIgnoreCase(controlName)) {
				sql = sql
						.replaceFirst(
								"#",
								(new StringBuilder())
										.append(
												"A.PHA_DOSAGE_CODE IS NOT NULL  AND (A.PHA_DOSAGE_DATE IS NULL OR (A.PHA_DOSAGE_DATE>=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("START_DATE")),
																"yyyyMMdd"))
										.append(
												"','YYYYMMDD') AND A.PHA_DOSAGE_DATE<=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("END_DATE")),
																"yyyyMMdd"))
										.append("','YYYYMMDD')))").toString());
				String dispense_no = getPhaDispenseNo();
				if (!"".equals(dispense_no) && !"''".equals(dispense_no))
					sql = (new StringBuilder()).append(sql).append(
							"  AND A.PHA_DISPENSE_NO IN (").append(dispense_no)
							.append(")").toString();
			} else {
				sql = sql
						.replaceFirst(
								"#",
								(new StringBuilder())
										.append(
												"A.PHA_DISPENSE_CODE IS NOT NULL AND (A.PHA_DISPENSE_DATE IS NULL OR (A.PHA_DISPENSE_DATE>=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("START_DATE")),
																"yyyyMMdd"))
										.append(
												"','YYYYMMDD') AND A.PHA_DISPENSE_DATE<=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("END_DATE")),
																"yyyyMMdd"))
										.append("','YYYYMMDD')))").toString());
				String dispense_no = getPhaDispenseNo();
				if (!"".equals(dispense_no) && !"''".equals(dispense_no))
					sql = (new StringBuilder()).append(sql).append(
							"  AND A.PHA_DISPENSE_NO IN (").append(dispense_no)
							.append(")").toString();
			}
			// ���ͷ���
			sql = sql + doseType + region;
			 
		    System.out.println("��ҩƷ��ʾͳҩ��====" + sql + group_by);
			parm = new TParm(TJDODBTool.getInstance().select(sql + group_by));
			// ========================= modify by chenxi 20120704 ����ҩƷ��ɫ�仯
			Color blue = new Color(0, 0, 255);
			TTable table = (TTable) this.getComponent("TBL_MED");
			for (int i = 0; i < parm.getCount(); i++) {
				String orderCode = parm.getValue("ORDER_CODE", i);
				String parmsql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '"
						+ orderCode + "' ";
				TParm sqlParm = new TParm(TJDODBTool.getInstance().select(
						parmsql));
				sqlParm = sqlParm.getRow(0);
				//fux modify 20150820 
				 if (this.getColorByHighRiskOrderCode(orderCode)) {
					 table.setRowTextColor(i, Color.RED);//��ΣҩƷ����ʾΪ��ɫ
				 } 
				 else if (sqlParm.getValue("DRUG_NOTES_DR").length() != 0){
					table.setRowTextColor(i, blue);
				 }
				 else{
					 table.setRowTextColor(i, null);  
				 }
			
				// ================ chenxi modigy 20120704
			}

		} else {
			// ��������ʾͳҩ��
			String sql = (new StringBuilder())
					.append(
							" SELECT A.ORDER_DESC || '  ' ||  C.SPECIFICATION AS ORDER_DESC,"// A.GOODS_DESC  
							// ||
									// ¬��
									// ɾ����Ʒ��
									// 2012-04-06
									+ "SUM( DOSAGE_QTY) AS DISPENSE_QTY,DOSAGE_UNIT AS DISPENSE_UNIT,  "
									+ "CASE WHEN B.SERVICE_LEVEL = 2 THEN C.OWN_PRICE2 WHEN B.SERVICE_LEVEL = 3 THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE, "
									+ "A.CASE_NO,  A.ORDER_CODE ,  "
									+ "CASE WHEN B.SERVICE_LEVEL = 2 THEN SUM (DOSAGE_QTY * C.OWN_PRICE2) WHEN B.SERVICE_LEVEL = 3 THEN SUM (DOSAGE_QTY * C.OWN_PRICE3) ELSE SUM (DOSAGE_QTY * C.OWN_PRICE) END AS OWN_AMT, "
									+ "A.BED_NO, A.MR_NO, D.PAT_NAME   "
									+ "FROM   ODI_DSPNM A, ADM_INP B ,SYS_FEE C, SYS_PATINFO D, SYS_PHAROUTE F  WHERE   A.CASE_NO = B.CASE_NO   AND A.MR_NO = D.MR_NO AND A.ROUTE_CODE=F.ROUTE_CODE " 
									+ piavsSql
									+		"AND A.CASE_NO IN (")
					.append(getCaseNos())
					.append(")")
					.append(
							"  AND A.ORDER_CODE=C.ORDER_CODE AND A.DC_DATE IS NULL ")
					// BY liyh 20120905 ���˵��Ѿ�ͣҽ����ҩƷ
					.append(
							"  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') ")
					.append("  AND A.DISPENSE_FLG='N' #").append("  AND #")
					//update by liyh ���ӷ�ҩ���� 20130603
					.append(dispenseOrgCodeSql)
					//fux modify 20161227 ���벡�� 
					.append(" AND A.STATION_CODE IN ("+getStationCodes()+") " )
					.toString();
			String group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, C.OWN_PRICE,A.CASE_NO,A.GOODS_DESC, A.BED_NO, A.MR_NO, D.PAT_NAME, B.SERVICE_LEVEL, C.OWN_PRICE2, C.OWN_PRICE3,C.SPECIFICATION, A.ORDER_DATE ORDER BY A.MR_NO,A.ORDER_DATE,A.BED_NO";
			// ===zhangp 20121115 start
			if ("Y".equals(this.getValueString("LINK_NO"))) {
				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, C.OWN_PRICE,A.CASE_NO,A.GOODS_DESC, A.BED_NO, A.MR_NO, D.PAT_NAME, B.SERVICE_LEVEL, C.OWN_PRICE2, C.OWN_PRICE3,C.SPECIFICATION, A.ORDER_DATE ORDER BY A.MR_NO,A.ORDER_DATE,A.LINK_NO , A.LINKMAIN_FLG ORDER BY A.BED_NO ASC, A.LINK_NO ASC , A.LINKMAIN_FLG DESC";
				group_by += " ASC, A.LINK_NO ASC , A.LINKMAIN_FLG DESC ";
			}
			// ===zhangp 20121115 end
			if (TypeTool.getBoolean(getValue("ST")))
				sql = sql.replaceFirst("#",
						" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F') ");
			else if (TypeTool.getBoolean(getValue("UD")))
				sql = sql.replaceFirst("#", " AND A.DSPN_KIND='UD'");
			else
				sql = sql.replaceFirst("#", " AND A.DSPN_KIND='DS'");
			if (TypeTool.getBoolean(getValue("UNCHECK"))) {
				if ("DOSAGE".equalsIgnoreCase(controlName)) {
					sql = sql
							.replaceFirst(
									"#",
									"A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL");
				} else {
					sql = sql
							.replaceFirst(
									"#",
									"A.PHA_DISPENSE_CODE IS  NULL AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL");
					String dispense_no = getPhaDispenseNo();
					if (!"".equals(dispense_no) && !"''".equals(dispense_no))
						sql = (new StringBuilder()).append(sql).append(
								"  AND A.PHA_DISPENSE_NO IN (").append(
								dispense_no).append(")").toString();
				}
			} else if ("DOSAGE".equalsIgnoreCase(controlName)) {
				sql = sql
						.replaceFirst(
								"#",
								(new StringBuilder())
										.append(
												"A.PHA_DOSAGE_CODE IS NOT NULL  AND (A.PHA_DOSAGE_DATE IS NULL OR (A.PHA_DOSAGE_DATE>=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("START_DATE")),
																"yyyyMMdd"))
										.append(
												"','YYYYMMDD') AND A.PHA_DOSAGE_DATE<=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("END_DATE")),
																"yyyyMMdd"))
										.append("','YYYYMMDD')))").toString());
				String dispense_no = getPhaDispenseNo();
				if (!"".equals(dispense_no) && !"''".equals(dispense_no))
					sql = (new StringBuilder()).append(sql).append(
							"  AND A.PHA_DISPENSE_NO IN (").append(dispense_no)
							.append(")").toString();
			} else {
				sql = sql
						.replaceFirst(
								"#",
								(new StringBuilder())
										.append(
												"A.PHA_DISPENSE_CODE IS NOT NULL AND (A.PHA_DISPENSE_DATE IS NULL OR (A.PHA_DISPENSE_DATE>=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("START_DATE")),
																"yyyyMMdd"))
										.append(
												"','YYYYMMDD') AND A.PHA_DISPENSE_DATE<=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("END_DATE")),
																"yyyyMMdd"))
										.append("','YYYYMMDD')))").toString());
			}
			// ���ͷ���
			sql = sql + doseType + region;
			System.out.println("��������ʾͳҩ��====" + sql + group_by);
			parm = new TParm(TJDODBTool.getInstance().select(
					sql + " " + group_by));
			//20151016 wangjc add start 
			Color blue = new Color(0, 0, 255);
			TTable table = (TTable) this.getComponent("TBL_MED");
			for (int i = 0; i < parm.getCount(); i++) {
				String orderCode = parm.getValue("ORDER_CODE", i);
				String parmsql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '"
						+ orderCode + "' ";
				TParm sqlParm = new TParm(TJDODBTool.getInstance().select(
						parmsql));
				sqlParm = sqlParm.getRow(0);
				 if (this.getColorByHighRiskOrderCode(orderCode)) {
					 table.setRowTextColor(i, Color.RED);//��ΣҩƷ����ʾΪ��ɫ
				 } 
				 else if (sqlParm.getValue("DRUG_NOTES_DR").length() != 0){
					table.setRowTextColor(i, blue);
				 }
				 else{
					 table.setRowTextColor(i, null);  
				 }
			}
			//20151016 wangjc add end
		}
		if (parm.getErrCode() != 0) {  
			return;
		} else {System.out.println("ppppp"+parm);
			tblMed.removeRowAll(); 
			tblMed.setParmValue(parm);
			return;
		}
	}
	
	/**
	 * �ж��Ƿ�Ϊ��ΣҩƷ
	 * @param orderCode
	 * @return
	 * 20150731 wangjc add
	 */
	public boolean getColorByHighRiskOrderCode(String orderCode){
		boolean a=false;
		String sql = "SELECT HIGH_RISK_FLG FROM PHA_BASE WHERE ORDER_CODE='"
				+orderCode
				+ "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getValue("HIGH_RISK_FLG", 0).equals("Y")){
			a=true;
		}
		return a;
	}

	// ******************************************
	// add by chenxi 20120704
	// *****************************************
	/**
	 * ״̬����ʾҩƷ��ʾ����
	 */
	public void MedtableClick() {
		TTable table = (TTable) this.getComponent("TBL_MED");
		TParm action = table.getParmValue();
		int row = table.getSelectedRow();
		String orderCode = action.getValue("ORDER_CODE", row);
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,"
				+ "DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE"
				+ " WHERE ORDER_CODE = '" + orderCode + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		sqlparm = sqlparm.getRow(0);
		// ״̬����ʾҽ����ʾ
		callFunction("UI|setSysStatus", sqlparm.getValue("ORDER_CODE") + " "
				+ sqlparm.getValue("ORDER_DESC") + " "
				+ sqlparm.getValue("GOODS_DESC") + " "
				+ sqlparm.getValue("DESCRIPTION") + " "
				+ sqlparm.getValue("SPECIFICATION") + " "
				+ sqlparm.getValue("REMARK_1") + " "
				+ sqlparm.getValue("REMARK_2") + " "
				+ sqlparm.getValue("DRUG_NOTES_DR"));

	}

	// **************************************
	// chenxi add 20120704
	// *************************************

	/**
	 * ҽ�ƿ����� luhai 2012-2-27
	 */
	public void onEKT() {
		TParm parm = EKTIO.getInstance().TXreadEKT();
		// System.out.println("parm==="+parm);
		if (null == parm || parm.getValue("MR_NO").length() <= 0) {
			this.messageBox("��鿴ҽ�ƿ��Ƿ���ȷʹ��");
			return;
		}
		// zhangp 20120130
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
		}
		setValue("NO", parm.getValue("MR_NO"));
		TRadioButton tb = (TRadioButton) this.getComponent("MR");
		tb.setSelected(true);
		this.onQuery();
		// �޸Ķ�ҽ�ƿ����� end luhai 2012-2-27
	}

	/**
	 * ����ҩƷϸ��
	 */
	public void onQueryDtl() {
		String doseType = getDoseType();
		if ("".equals(doseType)) {
			return;
		}
		// ====zhangp 20120803 start
		String tables = "";
		String conditions = "";
		// ===zhangp 20121118 start
		String orderBy = " ORDER BY A.TAKEMED_ORG,A.BED_NO ASC, B.MR_NO ASC,B.PAT_NAME ASC,A.CASE_NO ASC, A.ORDER_NO ASC,A.ORDER_SEQ ASC, A.LINKMAIN_FLG DESC ";
		// ===zhangp 20121118 end
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("")) {
			tables = " , SYS_CTRLDRUGCLASS I ";
			conditions = " AND G.CTRLDRUGCLASS_CODE = I.CTRLDRUGCLASS_CODE"
					+ " AND I.CTRL_FLG = '" + pha_ctrlcode + "'";
		}
		// ====zhangp 20121115 start
		// ====wukai 20161223 start
//		if ("N".equals(this.getValueString("LINK_NO"))) {
//			//conditions += " AND A.LINK_NO IS NOT NULL ";
//			conditions += " AND A.LINK_NO IS NULL ";
//		} else {
			orderBy = " ORDER BY A.TAKEMED_ORG,A.BED_NO ASC, B.MR_NO ASC,B.PAT_NAME ASC,A.CASE_NO ASC, A.ORDER_NO ASC,A.ORDER_SEQ ASC, A.LINK_NO ASC , A.LINKMAIN_FLG DESC ";
//		}
		// ====wukai 20161223 end
		/*****************update by liyh ���ӷ�ҩ���� 20130603 start********************/
		//ȫԺ��0��סԺ��2��������ҩ��1
	    String dispenseOrgCodeSql = " AND A.EXEC_DEPT_CODE='"+this.getValueString("EXEC_DEPT_CODE")+"' ";
	    if (getRadioButton("IN_HOSPITAL_TWO").isSelected()) {//סԺ��ҩ
	    	dispenseOrgCodeSql = " AND A.TAKEMED_ORG='2' ";
		}
	    if (getRadioButton("IN_STATION_TWO").isSelected()) {//������ҩ
	    	dispenseOrgCodeSql = " AND A.TAKEMED_ORG='1' ";
		}
	    /*****************update by liyh ���ӷ�ҩ���� 20130603 end********************/
	    
	    //20150405 wukai add start
	    //��ҩ�� ���������ƻ�PIVAs����
	    String pivasSql = "";
	    if("Y".equals(this.getValueString("LINK_NO"))){
	    	pivasSql += " AND A.IVA_FLG='Y' ";
	    }else if("N".equals(this.getValueString("LINK_NO"))){
	    	pivasSql += " AND (A.IVA_FLG IS NULL OR A.IVA_FLG='N') ";
	    }
	    //20150405 wukai add end
	    
		String sql = (new StringBuilder())
				.append(
						"SELECT 'Y' AS EXEC,'N' AS SENDATC_FLG,B.PAT_NAME, A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.INFLUTION_RATE, "//���ӵ���   machao  2017/8/14
								+ "A.START_DTTM,A.END_DTTM,A.REGION_CODE,A.STATION_CODE,A.DEPT_CODE,"
								+ "A.VS_DR_CODE,A.BED_NO,A.IPD_NO,A.MR_NO,DSPN_KIND,A.CAT1_TYPE,"
								+ "A.DSPN_DATE,A.DSPN_USER,A.DISPENSE_EFF_DATE,A.DISPENSE_END_DATE,A.EXEC_DEPT_CODE,A.EXEC_DEPT_CODE AS ORG_CODE,"
								+ "A.AGENCY_ORG_CODE,A.RX_NO,A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,"
								+ "A.ORDER_DESC || ' (' || A.SPECIFICATION || ')' ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION,A.MEDI_QTY,A.MEDI_UNIT,A.ORDER_DESC AS DESC1,"
								+ "A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DOSAGE_QTY,A.DOSAGE_UNIT,"
								+ "A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,"
								+ "A.DISCOUNT_RATE,TOT_AMT,A.ORDER_DATE,A.ORDER_DEPT_CODE,"
								+ "A.ORDER_DR_CODE,A.DR_NOTE,A.ATC_FLG,A.SENDATC_FLG,A.SENDATC_DTTM,"
								+ "A.INJPRAC_GROUP,A.DC_DATE,A.DC_TOT,A.RTN_NO,A.RTN_NO_SEQ,"
								+ "A.RTN_DOSAGE_QTY,A.RTN_DOSAGE_UNIT,A.CANCEL_DOSAGE_QTY,A.CANCELRSN_CODE,A.TRANSMIT_RSN_CODE,"
								+ "A.PHA_RETN_CODE,A.PHA_RETN_DATE,A.PHA_CHECK_CODE,A.PHA_CHECK_DATE,A.PHA_DISPENSE_NO,"
								+ "A.PHA_DOSAGE_CODE,A.PHA_DOSAGE_DATE,A.PHA_DISPENSE_CODE,A.PHA_DISPENSE_DATE,A.NS_EXEC_CODE,"
								+ "A.NS_EXEC_DATE,A.NS_EXEC_DC_CODE,A.NS_EXEC_DC_DATE,A.NS_USER,A.CTRLDRUGCLASS_CODE,"
								+ "A.PHA_TYPE,A.DOSE_TYPE,A.DCTAGENT_CODE,A.DCTEXCEP_CODE,A.DCT_TAKE_QTY,"
								+ "A.PACKAGE_AMT,A.DCTAGENT_FLG,A.DECOCT_CODE,A.URGENT_FLG,A.SETMAIN_FLG,"
								+ "A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,A.RPTTYPE_CODE,A.OPTITEM_CODE,A.HIDE_FLG,"
								+ "A.DEGREE_CODE,BILL_FLG,A.CASHIER_USER,A.CASHIER_DATE,A.IBS_CASE_NO_SEQ,"
								+ "A.IBS_SEQ_NO,A.ORDER_CAT1_CODE ,'"
								+ Operator.getID()
								+ "' AS OPT_USER ,'"
								+ Operator.getIP()
								+ "' AS OPT_TERM ,"// SHIBL 20120404
								+ "CASE WHEN E.SERVICE_LEVEL=2 THEN C.OWN_PRICE2 WHEN E.SERVICE_LEVEL=3 THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE ,"
								+ "CASE WHEN E.SERVICE_LEVEL=2 THEN A.DOSAGE_QTY * C.OWN_PRICE2 WHEN E.SERVICE_LEVEL=3 THEN A.DOSAGE_QTY * C.OWN_PRICE3 ELSE A.DOSAGE_QTY * C.OWN_PRICE END AS OWN_AMT, "
								+ "D.BED_NO_DESC,D.ROOM_CODE,E.SERVICE_LEVEL, G.STOCK_PRICE * A.DOSAGE_QTY AS COST_AMT,F.CLASSIFY_TYPE, "// luhai
								// //
								// add
								// //
								// F.CLASSIFY_TYPE
								+ " TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI') AS ORDER_DATE_SHOW,TO_CHAR(A.PHA_CHECK_DATE,'YYYY/MM/DD HH24:MI') AS PHA_CHECK_DATE_SHOW,TO_CHAR(PHA_DOSAGE_DATE,'YYYY/MM/DD HH24:MI:SS')AS PHA_DOSAGE_DATE_SHOW,"
								// ==============add by lx
								// 2012/06/04��Ժ��ҩ��IBS_ORDD���ӳ�Ժ��ҩע���ֶΣ��ڼƷѵ㴫��start================$$//

								+ "CASE WHEN DSPN_KIND='DS' THEN 'Y' ELSE 'N' END AS DS_FLG,TO_CHAR(A.DC_DATE,'YYYY/MM/DD HH24:MI') AS DC_DATE_SHOW,TO_CHAR(A.DC_NS_CHECK_DATE,'YYYY/MM/DD HH24:MI') AS DC_NS_CHECK_DATE_SHOW,A.INTGMED_NO" // SHIBL

//								+ ",CASE WHEN A.TAKEMED_ORG=2 THEN 'סԺҩ��' ELSE '����' END AS  TAKEMED_ORG " // SHIBL
								+ ",A.TAKEMED_ORG,A.BATCH_SEQ1,A.BATCH_SEQ2,A.BATCH_SEQ3 " 
								// ADD
								// 20120928
								// DCʱ��
								// ==============add by lx
								// 2012/06/04��Ժ��ҩ��IBS_ORDD���ӳ�Ժ��ҩע���ֶΣ��ڼƷѵ㴫��end================$$//
								+ "FROM ODI_DSPNM A,SYS_PATINFO B,SYS_FEE C, SYS_BED D, ADM_INP E, SYS_PHAROUTE F, PHA_BASE G "
								// ===ZHANGP 20120806 START
								+ tables
								// ===ZHANGP 20120806 END
								+ "WHERE A.CASE_NO IN (")
				.append(getCaseNos())
				.append(") "+pivasSql+" AND B.MR_NO=A.MR_NO AND A.ROUTE_CODE=F.ROUTE_CODE ")
				.append("  AND A.ORDER_CODE=C.ORDER_CODE ")
				//
				.append(
						"  AND A.BED_NO =  D.BED_NO AND A.CASE_NO = E.CASE_NO AND A.ORDER_CODE = G.ORDER_CODE ")
				.append("  AND A.DISPENSE_FLG='N'")
				//fux modify 20161227 ���벡�� 
			    .append(" AND A.STATION_CODE IN ("+getStationCodes()+") " ) 
				// ===zhangp 20120806 start
				.append(conditions)
				// ===zhangp 20120806 end
				.append(
						"  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') ")
				// update by liyh 20130603 ���ӷ�ҩ ����
			    .append(dispenseOrgCodeSql)
				.append("  AND #").toString();
		/* end update by guoyi 20120504 for ƿǩ���ֵ��� */
		// luhai 2012-5-2 modify �����б���뿪��ʱ�����Ϣ end
		if (TypeTool.getBoolean(getValue("ST")))
			sql = (new StringBuilder()).append(sql).append(
					" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F')").toString();
		else if (TypeTool.getBoolean(getValue("UD")))
			sql = (new StringBuilder()).append(sql).append(
					" AND A.DSPN_KIND='UD'").toString();
		else
			sql = (new StringBuilder()).append(sql).append(
					" AND A.DSPN_KIND='DS'").toString();
		// ===========pangben modify 20110511 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110511 stop

		// ���ͷ���
		sql = sql + doseType + region;
		if (TypeTool.getBoolean(getValue("UNCHECK"))) {
			if ("DOSAGE".equalsIgnoreCase(controlName)) {
				sql = sql
						.replaceFirst(
								"#",
								"A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL");// SHBL
			} else {
				sql = sql
						.replaceFirst(
								"#",
//								"A.PHA_DISPENSE_CODE IS  NULL AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.DC_DATE IS NULL  AND A.DC_NS_CHECK_DATE IS NULL");// SHBL
								"A.PHA_DISPENSE_CODE IS  NULL AND A.PHA_DOSAGE_CODE IS NOT NULL ");//modify by wangjc 20171227 ��������ͣҽ�����·�ҩ�����޷���ѯ������
				// 20120927  ADD  dcҽ������
				String dispense_no = getPhaDispenseNo();
				if (!"".equals(dispense_no) && !"''".equals(dispense_no)) {
					sql += "  AND A.PHA_DISPENSE_NO IN (" + dispense_no + ") ";
				}
			}
		} else if ("DOSAGE".equalsIgnoreCase(controlName)) {
			sql = sql
					.replaceFirst(
							"#",
							(new StringBuilder())
									.append(
											"A.PHA_DOSAGE_CODE IS NOT NULL  AND (A.PHA_DOSAGE_DATE IS NULL OR (A.PHA_DOSAGE_DATE>=TO_DATE('")
									.append(
											StringTool
													.getString(
															TypeTool
																	.getTimestamp(getValue("START_DATE")),
															"yyyyMMdd"))
									.append(
											"','YYYYMMDD') AND A.PHA_DOSAGE_DATE<=TO_DATE('")
									.append(
											StringTool
													.getString(
															TypeTool
																	.getTimestamp(getValue("END_DATE")),
															"yyyyMMdd"))
									.append("','YYYYMMDD')))").toString());
			String dispense_no = getPhaDispenseNo();
			if (!"".equals(dispense_no) && !"''".equals(dispense_no))
				sql = (new StringBuilder()).append(sql).append(
						"  AND A.PHA_DISPENSE_NO IN (").append(dispense_no)
						.append(")").toString();
		} else {
			sql = sql
					.replaceFirst(
							"#",
							(new StringBuilder())
									.append(
											"A.PHA_DISPENSE_CODE IS NOT NULL AND (A.PHA_DISPENSE_DATE IS NULL OR (A.PHA_DISPENSE_DATE>=TO_DATE('")
									.append(
											StringTool
													.getString(
															TypeTool
																	.getTimestamp(getValue("START_DATE")),
															"yyyyMMdd"))
									.append(
											"','YYYYMMDD') AND A.PHA_DISPENSE_DATE<=TO_DATE('")
									.append(
											StringTool
													.getString(
															TypeTool
																	.getTimestamp(getValue("END_DATE")),
															"yyyyMMdd"))
									.append("','YYYYMMDD')))").toString());
			String dispense_no = getPhaDispenseNo();
			if (!"".equals(dispense_no) && !"''".equals(dispense_no))
				sql = (new StringBuilder()).append(sql).append(
						"  AND A.PHA_DISPENSE_NO IN (").append(dispense_no)
						.append(")").toString();
		}
		 if ("DOSAGE".equalsIgnoreCase(controlName)) {//wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
	            sql = (new StringBuilder()).append(sql).append(" AND (C.IS_REMARK <> 'Y' OR C.IS_REMARK IS NULL) ").toString();
	     }
		// ====zhangp 20121115 start
		sql = (new StringBuilder()).append(sql).append(orderBy)// A.MR_NO,
		// ====zhangp 20121115 end
				// ����B.MR_NO,
				// 20120407
				.toString();
		//System.out.println("��ҩ�б��ѯsql��" + sql);
		saveParm = new TParm(TJDODBTool.getInstance().select(sql));
		for(int i=0;i<saveParm.getCount();i++) {
			String dspnkind = saveParm.getValue("DSPN_KIND",i);
			String orderCode = saveParm.getValue("ORDER_CODE",i);
			String caseNo = saveParm.getValue("CASE_NO",i); 
			int batchSeq1 = saveParm.getInt("BATCH_SEQ1",i); 
			int batchSeq2 = saveParm.getInt("BATCH_SEQ2",i); 
			int batchSeq3 = saveParm.getInt("BATCH_SEQ3",i); 
			//fux modify 20180912 ���ӳ���������ʾ
		    String searchBatch = "SELECT A.BATCH_NO FROM IND_STOCK A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE " +
		    		"  AND A.ORG_CODE='"+
					this.getValueString("EXEC_DEPT_CODE")+"'" +  
					" AND A.ORDER_CODE='"+orderCode+"' AND A.BATCH_SEQ IN('"+batchSeq1+"','"+batchSeq2+"','"+batchSeq3+"')" +
					" ORDER BY A.VALID_DATE DESC";
		    //System.out.println("searchBatch:::"+searchBatch);
			TParm searchBatchParm = new TParm(TJDODBTool.getInstance().select(searchBatch));
			//������ϸ���� �������������ţ�����1,2,3 ������ʾ��
			StringBuffer allBatchNo = new StringBuffer();
			for (int j = 0; j < searchBatchParm.getCount("BATCH_NO"); j++) {
			    if(searchBatchParm.getCount("BATCH_NO")==1){
					allBatchNo.append(
						    searchBatchParm.getValue("BATCH_NO", j));  
			    }else{
					allBatchNo.append(
						    searchBatchParm.getValue("BATCH_NO", j)).append(",");
			    }
			}			
			//System.out.println("allBatchNo.toString():::"+allBatchNo.toString());
			//String[] strs = phaDispenseNo.split(",");
			saveParm.setData("ALLBATCH_NO", i, allBatchNo.toString() );         
			
			   
			
			if(TypeTool.getBoolean(getValue("ST"))&&TypeTool.getBoolean(getValue("UNCHECK"))&&"DOSAGE".equalsIgnoreCase(controlName)){                      //��ʱ������
				String search = "SELECT A.BATCH_NO FROM IND_STOCK A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND SYSDATE < A.VALID_DATE AND B.ANTIBIOTIC_CODE IS NOT NULL AND SKINTEST_FLG='Y' AND A.STOCK_QTY>0 AND A.ORG_CODE='"+this.getValueString("EXEC_DEPT_CODE")+"' AND A.ORDER_CODE='"+orderCode+"' ORDER BY A.VALID_DATE ASC";
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );		
				if(searchParm.getValue("BATCH_NO",0)==null||"".equals(searchParm.getValue("BATCH_NO",0))) {
					tblDtl.setLockCell(i, 23, true);
				}						
			}
			if(TypeTool.getBoolean(getValue("ST"))&&TypeTool.getBoolean(getValue("UNCHECK"))&&"DISPENSE".equalsIgnoreCase(controlName)){                      //����
				String search=" SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"' AND BATCH_NO IS NOT NULL ORDER BY ORDER_DATE DESC";
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );						
			}
			if(TypeTool.getBoolean(getValue("UD"))){                      //����
				String search=" SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"' AND BATCH_NO IS NOT NULL ORDER BY ORDER_DATE DESC";
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );		
			}
			if(TypeTool.getBoolean(getValue("ST"))&&TypeTool.getBoolean(getValue("CHECK"))){                      //��ʱ������
				String  search = "SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"' AND BATCH_NO IS NOT NULL ORDER BY ORDER_DATE DESC";   
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );		
			}			
			if("F".equals(dspnkind)) {					
				String  search = "SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"' AND BATCH_NO IS NOT NULL ORDER BY ORDER_DATE DESC";   
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );	
			}
		/*	if(TypeTool.getBoolean(getValue("UD"))&&TypeTool.getBoolean(getValue("CHECK"))){                      //����
				String search=" SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"'";
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );		
			}*/
		}
		      
		if ("DOSAGE".equalsIgnoreCase(controlName)) {
			saveParm.setData("ADM_TYPE", "I");
			saveParm = SysPhaBarTool.getInstance().getaddBarParm(saveParm,
					"UDD");
		}     
		 System.out.println("======"+saveParm);
		tblDtl.removeRowAll();   
		
		
		tblDtl.setParmValue(saveParm);  
		
		Color normalColor = new Color(0, 0, 0);
		for (int i = 0; i < saveParm.getCount(); i++) {
			//tblDtl.setRowTextColor(i,normalColor );
			String orderCode = saveParm.getValue("ORDER_CODE", i);
			String parmsql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '"
					+ orderCode + "' ";
			TParm sqlParm = new TParm(TJDODBTool.getInstance().select(
					parmsql));  
			sqlParm = sqlParm.getRow(0);  
			//fux modify 20150820 
			 if (this.getColorByHighRiskOrderCode(orderCode)) {  
				 tblDtl.setRowTextColor(i, Color.RED);//��ΣҩƷ����ʾΪ��ɫ
			 }else{
				 tblDtl.setRowTextColor(i, null);
			 }
		}  
	}

	/**
	 * ȡ��PAT table��ѡ�е�CASE_NO��Ϊ����SQLƴWHERE��
	 * 
	 * @return
	 */
	public String getCaseNos() {
		TParm parm = tblPat.getParmValue();
		// System.out.println( (new
		// StringBuilder()).append("parm111---").append(
		// parm).toString());
		StringBuffer caseNos = new StringBuffer();
		if (parm.getCount() < 1)
			return "''";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("EXEC", i)))
				caseNos.append("'").append(parm.getValue("CASE_NO", i)).append(
						"',");

		if (caseNos.length() < 1) {
			return "''";
		} else {
			caseNos.deleteCharAt(caseNos.length() - 1);
			return caseNos.toString();
		}
	}
	
	

	/**
	 * ȡ��PAT table��ѡ�е�STATION_CODE��Ϊ����SQLƴWHERE��
	 * 
	 * @return
	 */
	public String getStationCodes() {
		TParm parm = tblPat.getParmValue();
		// System.out.println( (new
		// StringBuilder()).append("parm111---").append(
		// parm).toString());
		StringBuffer stationCodes = new StringBuffer();
		if (parm.getCount() < 1)
			return "''";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("EXEC", i)))
				stationCodes.append("'").append(parm.getValue("STATION_CODE", i)).append(
						"',");

		if (stationCodes.length() < 1) {
			return "''";
		} else {
			stationCodes.deleteCharAt(stationCodes.length() - 1);
			return stationCodes.toString();
		}
	}

	private String getPhaDispenseNo() {
		TParm parm = tblPat.getParmValue();
		// System.out.println("-------"+parm);

		StringBuffer phaDispenseNo = new StringBuffer();
		if (parm.getCount() < 1)
			return "";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("EXEC", i)))
				phaDispenseNo.append("'").append(
						parm.getValue("PHA_DISPENSE_NO", i)).append("',");
		// System.out.println("-------"+phaDispenseNo);
		if (phaDispenseNo.length() < 1) {
			return "";
		} else {
			phaDispenseNo.deleteCharAt(phaDispenseNo.length() - 1);
			return phaDispenseNo.toString();
		}
	}
	private String getPhaDispenseNoWhere2() {
		TParm parm = tblDtl.getParmValue();
//		 System.out.println("923-tblDtl------parm:"+tblDtl.getParmValue());
		StringBuffer phaDispenseNo = new StringBuffer();
		if (parm.getCount() < 1)
			return "";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("EXEC", i)))
				phaDispenseNo.append("'").append(
						parm.getValue("PHA_DISPENSE_NO", i)).append("',");
		// System.out.println("-------"+phaDispenseNo);
		if (phaDispenseNo.length() < 1) {
			return "";
		} else {
			phaDispenseNo.deleteCharAt(phaDispenseNo.length() - 1);
			return phaDispenseNo.toString();
		}
	}	

	// private String getBedNo() {
	// TParm parm = tblPat.getParmValue();
	// //System.out.println("-------"+parm);
	//
	// StringBuffer phaBedNo = new StringBuffer();
	// if (parm.getCount() < 1)
	// return "";
	// int count = parm.getCount();
	// for (int i = 0; i < count; i++)
	// if (StringTool.getBoolean(parm.getValue("EXEC", i)))
	// phaBedNo.append("'").append(parm.getValue(
	// "BED_NO", i)).append("',");
	// //System.out.println("-------"+phaDispenseNo);
	// if (phaBedNo.length() < 1) {
	// return "";
	// }
	// else {
	// phaBedNo.deleteCharAt(phaBedNo.length() - 1);
	// return phaBedNo.toString();
	// }
	// }

	// /**
	// * ����CHECK_BOX����¼�
	// */
	// public void onDose() {
	// boolean value = TCM_Transform.getBoolean(getValue("DOSE"));
	// TTextFormat t = (TTextFormat) getComponent("CBL_DOSE");
	// if (value) {
	// t.setEnabled(true);
	// t.setValue("");
	// return;
	// }
	// else {
	// t.setValue("");
	// t.setEnabled(false);
	// return;
	// }
	// }

	/**
	 * ѡ�����
	 */
	public void onSelectDoseTypeAll() {
		String flg = this.getValueString("DOSE_TYPEALL");
		this.setValue("DOSE_TYPEO", flg);
		this.setValue("DOSE_TYPEE", flg);
		this.setValue("DOSE_TYPEI", flg);
		this.setValue("DOSE_TYPEF", flg);
	}

	/**
	 * ȡ��SQL����WHERE����
	 * 
	 * @return
	 */
	public String getWhere() {
		StringBuffer result = new StringBuffer();
		String startDate = StringTool.getString(TCM_Transform
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmm");
		String endDate = (new StringBuilder()).append(
				StringTool.getString(
						TCM_Transform.getTimestamp(getValue("END_DATE")),
						"yyyyMMddHHmm").substring(0, 8)).append("2359")
				.toString();
		if (StringTool.getBoolean(getValueString("UNCHECK"))) {
			if ("DOSAGE".equalsIgnoreCase(controlName)) {
				// ��ҩ��ɲ�ѯ
				if (isCheckNeeded)
					result
							.append((new StringBuilder())
									.append(
											" AND A.PHA_CHECK_CODE IS NOT NULL AND A.START_DTTM >='")
									.append(startDate)
									.append("' AND A.START_DTTM<='")
									.append(endDate)
									.append(
											"' AND A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL")// shibl
									// 20120927
									// add
									// ����DCҽ��
									.toString());
				else
					result
							.append((new StringBuilder())
									.append(" AND A.START_DTTM >='")
									.append(startDate)
									.append("' AND A.START_DTTM<='")
									.append(endDate)
									.append(
											"' AND A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL")// shibl
									// 20120927
									// add
									// ����DCҽ��
									.toString());
			} else {
				// ��ҩδ��ɲ�ѯ
				result
						.append((new StringBuilder())
								.append(
										" AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('")
								.append(startDate)
								.append("', 'YYYYMMDDHH24MI') AND TO_DATE ('")
								.append(endDate)
//								.append("', 'YYYYMMDDHH24MI') AND A.PHA_DISPENSE_CODE IS NULL  AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL")// shibl
								.append("', 'YYYYMMDDHH24MI') AND A.PHA_DISPENSE_CODE IS NULL ")//modify by wangjc 20171227 ��������ͣҽ�����·�ҩ�����޷���ѯ������
								// 20120927
								// add
								// ����DCҽ��
								.toString());
			}
			// else if (isDosage)
			// result.append( (new StringBuilder()).append(
			// " AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.START_DTTM >='").
			// append(startDate).append("' AND A.START_DTTM<='").

			// append(endDate).append(
			// "' AND A.PHA_DISPENSE_CODE IS NULL ").
			// toString());
			// else
			// if (isCheckNeeded)
			// result.append( (new StringBuilder()).append(
			// " AND A.PHA_CHECK_CODE IS NOT NULL AND A.START_DTTM >='").
			// append(startDate).append("' AND A.START_DTTM<='").
			// append(endDate).append(
			// "' AND A.PHA_DISPENSE_CODE IS NULL ").
			// toString());
			// else
			// result.append( (new StringBuilder()).append(
			// " AND A.START_DTTM >='").append(startDate).append(
			// "' AND A.START_DTTM<='").append(endDate).append(
			// "' AND A.PHA_DISPENSE_CODE IS NULL").toString());
		} else {
			if ("DOSAGE".equalsIgnoreCase(controlName))
				// ��ҩ��ɲ�ѯ
				result
						.append((new StringBuilder())
								.append(
										" AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('")
								.append(startDate).append(
										"', 'YYYYMMDDHH24MI') AND TO_DATE ('")
								.append(endDate)
								.append("', 'YYYYMMDDHH24MI') ").toString());
			else
				// ��ҩ��ɲ�ѯ
				result
						.append((new StringBuilder())
								.append(
										" AND A.PHA_DISPENSE_CODE IS NOT NULL AND A.PHA_DISPENSE_DATE BETWEEN TO_DATE ('")
								.append(startDate).append(
										"', 'YYYYMMDDHH24MI') AND TO_DATE ('")
								.append(endDate)
								.append("', 'YYYYMMDDHH24MI') ").toString());
		}

		result.append((new StringBuilder()).append(" AND A.EXEC_DEPT_CODE='")
				.append(getValueString("EXEC_DEPT_CODE")).append("'")
				.toString());
		if (!StringUtil.isNullString(getValueString("AGENCY_ORG_CODE")))
			result.append((new StringBuilder()).append(
					" AND A.AGENCY_ORG_CODE='").append(
					getValueString("AGENCY_ORG_CODE")).append("'").toString());
		if (StringTool.getBoolean(getValueString("STA"))) {
			if (!StringUtil.isNullString(getValueString("COMBO")))
				result
						.append((new StringBuilder()).append(
								" AND A.STATION_CODE='").append(
								getValueString("COMBO")).append("'").toString());
		} else if (TypeTool.getBoolean(getValue("MR"))) {
			String mr = this.getValueString("NO");
			int length = PatTool.getInstance().getMrNoLength();
			if(mr.contains("-")) {
				length= length+2;
			}
			String mrNo = StringTool.fill0(getValueString("NO"),length); // ====chenxi
			setValue("NO", mrNo);
			result.append((new StringBuilder()).append(" AND A.MR_NO='")
					.append(mrNo).append("'").toString());
		} else {
			result.append((new StringBuilder()).append(" AND A.BED_NO='")
					.append(getValueString("QUERY_BED")).append("' ")
					.toString());
		}
		if (TypeTool.getBoolean(getValue("ST")))
			result.append(" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F')");
		else if (TypeTool.getBoolean(getValue("UD")))
			result.append(" AND A.DSPN_KIND='UD'");
		else
			result.append(" AND A.DSPN_KIND='DS'");
		// if (TypeTool.getBoolean(getValue("DOSE")))
		// result.append( (new StringBuilder()).append(" AND A.DOSE_TYPE='").
		// append(getValueString("CBL_DOSE")).append("'").
		// toString());
		if (!StringUtil.isNullString(getValueString("PHA_DISPENSE_NO")))
			result.append((new StringBuilder()).append(
					" AND A.PHA_DISPENSE_NO='").append(
					getValueString("PHA_DISPENSE_NO")).append("'").toString());
		return result.toString();
	}

	// luhai delete �뵥ѡ��ť�¼���ͻ
	public void onTblPatClick() {
		// boolean value = TCM_Transform.getBoolean(tblPat.getValueAt(
		// tblPat.getSelectedRow(), 0));
		// int allRow = tblPat.getRowCount();
		//
		// for (int i = 0; i < allRow; i++) {
		// tblPat.setValueAt(false, i, 0);
		// tblPat.getParmValue().setData("EXEC", i, false);
		// }
		// tblPat.setValueAt(true, tblPat.getSelectedRow(), 0);
		// tblPat.getParmValue().setData("EXEC", tblPat.getSelectedRow(), true);
		// // System.out.println("click parm======"+tblPat.getParmValue());
		// tblDtl.removeRowAll();
		// onQueryDtl();
	}
	
	public void onQueryByDispenseOrg(){
		onQueryDtl();
		onQueryMed();
	}

	/**
	 * ���������嵥�е�ִ����
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxClicked(Object obj) {
		tblPat.acceptText();
		int column = tblPat.getSelectedColumn();
		int row = this.tblPat.getSelectedRow();
		TParm tblPatparm = tblPat.getParmValue();
		if (column == 0) {
			// =============pangben 2012-5-25 start ��Ժ���˲������ٴβ�����ҩ
			if (TypeTool.getBoolean(getValue("UNCHECK"))) {
				if (StringTool.getBoolean(tblPatparm.getValue("EXEC", row))) {
					if (!BILTool.getInstance().checkRecp(
							tblPatparm.getValue("CASE_NO", row))) {
						callFunction("UI|save|setEnabled", false);
						this.messageBox("�˲�����Ժ�ѽ���,����ִ����ҩ����");
						// return ;
					} else {
						callFunction("UI|save|setEnabled", true);
					}
				}
			}
			// =============pangben 2012-5-25 stop
			// wanglong add 20150226
            String caseNo = tblPatparm.getValue("CASE_NO", row);
            String sql =
                    "SELECT * FROM ADM_INP WHERE CASE_NO = '" + caseNo
                            + "' AND DS_DATE IS NOT NULL";
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                this.messageBox("ִ�г��� " + result.getErrText());
                return;
            }
            //20161014 lyl Ϊ��Ժ����Ҳ�ܴ�ӡͳҩ�� ȥ���˹ܿ�
//            if (result.getCount() > 0) {
//                this.messageBox(tblPatparm.getValue("PAT_NAME", row) + "�Ѿ���Ժ��");
//                tblPatparm.setData("EXEC", row, false);
//                tblPat.setValueAt(false, row, 0);
//                return;
//            }
            // add end
            
			onQueryDtl();
			
			onQueryMed();
		}
		// callFunction("UI|ALLATCDO|isSelected", true);

		// ��������table�ϵ��
		if (column == 0) {
			if ("DOSAGE".equalsIgnoreCase(controlName)) {
				// this.messageBox_("fafa");
				// this.messageBox_(tblPat.getValueAt(row, col)+"");
				if (TCM_Transform.getBoolean(tblPat.getValueAt(row, column))) {
					String stationCode = getTableSelectRowData("STATION_CODE",
							"TBL_PAT");
					// this.messageBox_(stationCode);
					String machineNo = this.getStationData(stationCode)
							.getValue("MACHINENO", 0);
					String atcType = this.getStationData(stationCode).getValue(
							"ATC_TYPE", 0);
					// this.messageBox_(atcType);
					this.setValue("ATC_MACHINENO", machineNo);
					this.setValue("ATC_TYPE", atcType);
					this.setValue("ALLATCDO", "Y");
					onATCDo();
				} else {
					// this.setValue("ALLATCDO", "N");
					onATCDo();
					this.setValue("ATC_MACHINENO", "");
					this.setValue("ATC_TYPE", "");
				}
			}
		}

	}

	public void onTable2CheckBoxClicked(Object obj) {
		tblDtl.acceptText();
	}

	/**
	 * ����
	 */
	public void onSave() {
		String userID = Operator.getID();
		// System.out.println("====onSave come in11111111111111111...=====");			
		if (saveParm == null) {
			messageBox_("û�б�������");
			return;
		}
		// ��ȫ��parm��ֵ�� ������ʱ��tblDtl
		TParm saveParmNew = saveParm;
		int count = saveParm.getCount("ORDER_SEQ");
		// ��ҩ��ϸ������
		detailCount = count;
		if (count < 1) {
			messageBox_("û�б�������");
			return;
		}
		
		// �������ҩȷ�ϱ���
		if ("DOSAGE".equalsIgnoreCase(controlName)) {
			String sql = "";
			String case_no = "";
			String order_no = "";
			int order_seq = 0;
			String start_dttm = "";
			// ===========pangben modify 20110512 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110512 stop
			boolean drugFlg = false;
			for (int i = 0; i < saveParm.getCount("ORDER_CODE"); i++) {
				String orderCode = saveParm.getValue("ORDER_CODE", i);
				boolean exec = saveParm.getBoolean("EXEC",i);
				if(exec) {
					String drugSql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='"+orderCode+"' AND CTRL_FLG='Y'";
					TParm drugParm = new TParm(TJDODBTool.getInstance().select(drugSql)) ;
					if(drugParm.getCount()>0) {
						drugFlg = true;
					}			
				}
				case_no = saveParm.getValue("CASE_NO", i);
				order_no = saveParm.getValue("ORDER_NO", i);
				order_seq = saveParm.getInt("ORDER_SEQ", i);
				start_dttm = saveParm.getValue("START_DTTM", i);
				sql = "SELECT PHA_DOSAGE_DATE FROM ODI_DSPNM WHERE CASE_NO = '"
						+ case_no + "' AND ORDER_NO = '" + order_no
						+ "' AND ORDER_SEQ = " + order_seq
						+ " AND START_DTTM = '" + start_dttm + "'" + region;
				TParm checkParm = new TParm(TJDODBTool.getInstance()
						.select(sql));
				if (checkParm.getCount("PHA_DOSAGE_DATE") > 0
						&& checkParm.getData("PHA_DOSAGE_DATE", 0) != null
						&& !"".equals(checkParm.getValue("PHA_DOSAGE_DATE", 0))) {
					this.messageBox("ҩƷ�Ѿ���ҩ�������²�ѯ");
					return;
				}
			}
			if(drugFlg) {	
				String searchSql = "SELECT * FROM SYS_OPERATOR WHERE USER_ID='"+userID+"' AND CTRL_FLG='Y'";  
				TParm drugParm = new TParm(TJDODBTool.getInstance().select(searchSql)) ;   
				if(drugParm.getCount()<=0) {
					this.messageBox("���龫ҩƷ����Ȩ��");			
					return;
				}
			}
		}
		for (int i = 0; i < count; i++) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", saveParm.getValue("CASE_NO", i));
			TParm admInp = ADMInpTool.getInstance().selectall(parm);
			saveParm.addData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
			saveParm.addData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
			saveParm.addData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
		}
		//fuwj ����ж�������his����		
		TParm result = new TParm();
		saveParm.setData("FLG", "ADD");
		// zhangyong20110516 �������REGION_CODE
		saveParm.setData("REGION_CODE", Operator.getRegion());
		//fuwj �������������
		if("Y".equals(Operator.getSpcFlg())) {
			saveParm.setData("SPC_FLG", "Y");// ��ҩ���� shibl add 20130423
		}else {
			saveParm.setData("SPC_FLG", "N");		
		}
		saveParm.setData("CHARGE", charge);
//		System.out.println("cont1"+saveParm.getCount("CASE_NO"));
		for (int i = saveParm.getCount("CASE_NO") - 1; i >= 0; i--)
			if (!"Y".equals(saveParm.getValue("EXEC", i)))
				saveParm.removeRow(i);

		Map map = new HashMap();
		String case_no = "";
		for (int i = 0; i < saveParm.getCount("CASE_NO"); i++) {
			case_no = saveParm.getValue("CASE_NO", i);
			if (map == null) {
				map.put(case_no, case_no);
				continue;
			}
			if (!map.containsValue(case_no))
				map.put(case_no, case_no);
		}

		Set set = map.keySet();
		Iterator iterator = set.iterator();
		String pha_dispense_no = "";
		String pah_dispense_no_list = "";
		printCount = 0;
		if ("DOSAGE".equalsIgnoreCase(controlName)) {// ����ҩ����
			while (iterator.hasNext()) {
				printCount++;
				pha_dispense_no = SystemTool.getInstance().getNo("ALL", "UDD",
						"UDDDspn", "No");
				pah_dispense_no_list = (new StringBuilder()).append(
						pah_dispense_no_list).append("'").append(
						pha_dispense_no).append("'").append(",").toString();
				case_no = TypeTool.getString(iterator.next());
				// ����ȼ�
				saveParm.addData("SERVICE_LEVEL", getServiceLevel(case_no));
				int i = 0;
				while (i < saveParm.getCount("CASE_NO")) {
					if (case_no.equals(saveParm.getValue("CASE_NO", i)))
						saveParm.setData("PHA_DISPENSE_NO", i, pha_dispense_no);
					i++;
				}
			}
		}
//		System.out.println("cont2"+saveParm.getCount("CASE_NO"));
		if ("DOSAGE".equalsIgnoreCase(controlName)) {// ����ҩ����
			result = TIOM_AppServer.executeAction("action.udd.UddAction",
					"onUpdateMedDosage", saveParm);
			printCount = saveParm.getCount();
		} else {// ����ҩ��ҩ
			result = TIOM_AppServer.executeAction("action.udd.UddAction",
					"onUpdateMedDispense", saveParm);
		}
		Object list = result.getData("ERR_LIST");
		StringBuffer sbErr = new StringBuffer();
		if (list != null) {
			TParm errList = result.getParm("ERR_LIST");
			if (errList != null) {
				int countErr = errList.getCount();
				if (countErr > 0) {
					for (int i = 0; i < countErr; i++) {
						String err = errList.getValue("MSG", i);
						messageBox_(err);
						sbErr.append(err).append("\r\n");
					}

				}
				String fileNmae = (new StringBuilder()).append(
						TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
						.append("\\��ҩ������־").append(
								StringTool.getString(TJDODBTool.getInstance()
										.getDBTime(), "yyyyMMddHHmmss"))
						.append(".txt").toString();
				messageBox_((new StringBuilder()).append(
						"��ϸ�����C:/JavaHis/logs/��ҩ������־").append(
						StringTool.getString(TJDODBTool.getInstance()
								.getDBTime(), "yyyyMMddHHmmss")).append(
						".txt�ļ�").toString());
				try {
					FileTool.setString(fileNmae, sbErr.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (result.getErrCode() != 0) {
				messageBox("E0001");
				return;
			}
			messageBox("P0001");
			// �Ͱ�ҩ��
			/*try {
				if ("DOSAGE".equalsIgnoreCase(controlName)&&"Y".equals(Operator.getAtcFlg())) {
					String stationCode = getTableSelectRowData("STATION_CODE",
							"TBL_PAT");
					boolean nowFlag = (Boolean) this
							.callFunction("UI|ALLATCDO|isSelected");
					if (nowFlag) {
						onGenATCFile();
					}
					// System.out.println("-==--------------"+stationCode);
					// TParm atcParm = this.getStationData(stationCode);
					// // System.out.println("12--------------------"+atcParm);
					// // String type = atcParm.getValue("ATC_TYPE", 0);
					// String type="2";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			// ��ӡ��ҩȷ�ϵ�
			if ("DOSAGE".equalsIgnoreCase(controlName))  
			{
				onDispenseSheetRe(pah_dispense_no_list.substring(0,
						pah_dispense_no_list.length() - 1));
		    //fux modify 20160122 ����ҩ��ҩ ��ӡͳҩ��
			} else {  
				//wukai modify 20161117 ����ҩ��ҩ ��ӡͳҩ�� ����������==== start
				queryFlg = false; 
				onUnDispense();
				queryFlg = true;
				onQueryMed();
				onQueryDtl();
				//wukai modify 20161117 ����ҩ��ҩ ��ӡͳҩ�� ����������==== end
			}   
			
			if (this.printCount < this.detailCount) {// �����ӡ������ С�� ��ϸ������ ��
				// ֻ����ұ�����
				tblDtl.setParmValue(saveParmNew);
				tblDtl.removeRowAll();
				this.tblMed.removeRowAll();
				/*
				 * for(int i = 0 ; i < this.detailCount; i++){
				 * this.messageBox("ai"+i); this.tblDtl.remove(i); }
				 */
			} else {
				onClear();
			}
			return;
		}									
	}

	/**
	 * ȡ����ҩ����ҩ
	 */
	public void onDelete() {
		if (TypeTool.getBoolean(getValue("UNCHECK"))) {
			messageBox_("δ�������ݲ���ȡ��");
			return;
		}
		if (saveParm == null) {
			messageBox_("û�б�������");
			return;
		}
		int count = saveParm.getCount("ORDER_SEQ");
		if (count < 1) {
			messageBox_("û�б�������");
			return;
		}
		String caseNos = getCaseNos();
		String caseNo[] = StringTool.parseLine(caseNos, ",");
		if (caseNo == null) {
			messageBox_("û�б�������");
			return;
		}
		if (caseNo.length != 1) {
			messageBox_("û�б�������");
			return;
		}

		Timestamp date = SystemTool.getInstance().getDate();
		for (int i = 0; i < count; i++) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", saveParm.getValue("CASE_NO", i));
			TParm admInp = ADMInpTool.getInstance().selectall(parm);
			saveParm.addData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
			saveParm.addData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
			saveParm.addData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
			saveParm.addData("SERVICE_LEVEL", admInp.getValue("SERVICE_LEVEL",
					0));
			saveParm.addData("OPT_USER", Operator.getID());
			saveParm.addData("OPT_DATE", date);
			saveParm.addData("OPT_TERM", Operator.getIP());
		}

		TParm result = new TParm();
		saveParm.setData("CHARGE", charge);
		saveParm.setData("FLG", "DELETE");
		// zhangyong20110516 �������REGION_CODE
		saveParm.setData("REGION_CODE", Operator.getRegion());
		// System.out.println("---------" + saveParm);
		TParm patParm = tblPat.getParmValue();
		if ("DOSAGE".equalsIgnoreCase(controlName)) {
			for (int i = 0; i < patParm.getCount("EXEC"); i++) {
				if ("Y".equals(patParm.getValue("EXEC", i))
						&& !"".equals(patParm.getValue("PHA_DISPENSE_CODE", i))) {
					this.messageBox("��ҩƷ�ѷ�ҩ������ȡ����ҩ");
					return;
				}
			}

			result = TIOM_AppServer.executeAction("action.udd.UddAction",
					"onCnclUpdateMedDosage", saveParm);
		} else
			result = TIOM_AppServer.executeAction("action.udd.UddAction",
					"onCnclUpdateMedDispense", saveParm);
		if (result.getErrCode() != 0) {
			messageBox("E0001");
			return;
		} else {
			messageBox("P0001");
			onClear();
			return;
		}
	}

	/**
	 * ��ӡ��ҩȷ�ϵ�  
	 */
	public void onDispenseSheet() {
		
		if(getRadioButton("ALL_DISPENSE_ORG_TWO").isSelected()){
			this.messageBox("���ڲ���ҩƷ��ϸҳ��ѡ��ҩ���ţ�סԺҩ������");
			return;
		}
		onQueryDtl();  
		onQueryMed();
		TParm inParm = new TParm();
		// luhai delete 2012-2-23 ɾ�� ���е���ҩȷ�ϵ���Ϊ��һ�δ�ӡ begin
		inParm.setData("FIRST_PRINT", Boolean.valueOf(true));
		// luhai delete 2012-2-23 ɾ�� ���е���ҩȷ�ϵ���Ϊ��һ�δ�ӡ end
		TTextFormat station = (TTextFormat) getComponent("COMBO");
		String stationName = station.getText();
		// String stationCode = getValueString("COMBO");
		if (StringUtil.isNullString(stationName))
			stationName = "ȫԺ";
		inParm.setData("STATION_NAME","TEXT", stationName);
		inParm.setData("START_DATE", TypeTool
				.getTimestamp(getValue("START_DATE")));
		// zhaolingling start
		String datetime = TJDODBTool.getInstance().getDBTime().toString()
				.substring(0, 19).replace("-", "/");
		inParm.setData("CUR_DATE", "TEXT",datetime + "");
		System.out.println("ͳҩʱ�䣺����������"+datetime);
		// zhaolingling end
		String flg = "";
		if (getRadioButton("IN_STATION_TWO").isSelected()) {
			inParm.setData("DISPENSE_ORG_TWO","TEXT","����");
			inParm.setData("WHERE_5", "'1'");
			flg = "'1'";
		}else {
			inParm.setData("WHERE_5", "'2'");				
			inParm.setData("DISPENSE_ORG_TWO","TEXT","סԺҩ��");
			flg= "'2'";
		}
		inParm.setData("DISPENSE_ORG_TWO",this.getText("EXEC_DEPT_CODE"));//wanglong add 20140812
		inParm.setData("END_DATE", TypeTool.getTimestamp(getValue("END_DATE")));
		inParm.setData("DONE", Boolean.valueOf(TypeTool
				.getBoolean(getValue("UNCHECK"))));
		boolean isStation = TypeTool.getBoolean(getValue("STA"));
		/*if (!isStation) {
			messageBox_("������ҩƷ��Ϣ���ܰ�������ʾ");
			return;
		}*/
		inParm.setData("IS_STATION", Boolean.valueOf(isStation));
		String caseNos = getCaseNos();
		inParm.setData("WHERE_1", caseNos);
		String phaDispenseNo = getPhaDispenseNoWhere2();
		inParm.setData("WHERE_2", phaDispenseNo);
		// ====zhangp 20121118 start
		String ctrl = "";
		String bar_code = "";
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("") && this.getRadioButton("ST").isSelected()) {
			ctrl = "�龫";
			// ===zhangp 20130225 start
			tblDtl.acceptText();
			TParm parm = tblDtl.getParmValue();
			if (parm.getCount() > 0) {
				int count = parm.getCount();
				for (int i = 0; i < count; i++) {
					if (StringTool.getBoolean(parm.getValue("EXEC", i)) && parm.getValue("TAKEMED_ORG", i).equals("2"))
						if(phaDispenseNo.length()>0){
							bar_code = phaDispenseNo.replace("'", "");
						}
					break;
				}
			}
		}
		// ===zhangp 20130225 end
		String type = "";
		if (this.getRadioButton("ST").isSelected()) {
			inParm.setData("TYPE_T","TEXT", "��ʱҽ��" + ctrl + "��ҩȷ�ϵ�");//����������ҩȷ�ϵ���Ϊ��ҩȷ�ϵ�--xiongwg20150129
			inParm.setData("WHERE_3", " 'ST','F'");
			type=  " 'ST','F'";
		} else if (this.getRadioButton("UD").isSelected()) {
			inParm.setData("TYPE_T","TEXT", "����ҽ��" + ctrl + "��ҩȷ�ϵ�");//����������ҩȷ�ϵ���Ϊ��ҩȷ�ϵ�--xiongwg20150129
			inParm.setData("WHERE_3", " 'UD'");
			type =" 'UD'";
		} else {
			inParm.setData("TYPE_T","TEXT", "��Ժ��ҩ" + ctrl + "��ҩȷ�ϵ�");//����������ҩȷ�ϵ���Ϊ��ҩȷ�ϵ�--xiongwg20150129
			inParm.setData("WHERE_3", " 'DS'");
			type = " 'DS'";
		}
		// ====zhangp 20121118 end
		if ("''".equalsIgnoreCase(caseNos)) {
			messageBox_("û������");
			return;
		}
		// ���ͷ���
		inParm.setData("WHERE_4", getDoseTypeByWhere());
		// �÷�
		inParm.setData("DOSE_TYPE", getDoseTypeText());
		
		
		
		

		TParm parmData = tblPat.getParmValue();
		TParm parmRowData = new TParm();
		for (int i = 0; i < parmData.getCount("EXEC"); i++) {
			if (StringTool.getBoolean(parmData.getValue("EXEC", i))) {
				parmRowData = parmData.getRow(i);
				break;
			}
		}
//		String bsql = "SELECT  A.CASE_NO,A.ORDER_CODE,D.BED_NO_DESC,A.MR_NO,C.PAT_NAME,A.LINK_NO," +
//				"B.ORDER_DESC||' '||B.SPECIFICATION ORDER_DESC, SUM(A.MEDI_QTY) AS MEDI_QTY," +
//				"H.UNIT_CHN_DESC AS MEDI_UNIT,I.ROUTE_CHN_DESC,J.FREQ_CHN_DESC,A.TAKE_DAYS," +
//				"SUM (A.DISPENSE_QTY) AS DISPENSE_QTY,G.UNIT_CHN_DESC," +
//				"SUM (A.DOSAGE_QTY) *B.OWN_PRICE AS OWN_AMT FROM ODI_DSPNM A,SYS_FEE B,SYS_PATINFO C, " +
//				"SYS_BED D, SYS_PHAROUTE F,SYS_UNIT G,SYS_UNIT H,SYS_PHAROUTE I,SYS_PHAFREQ J " +
//				"WHERE A.FREQ_CODE=J.FREQ_CODE AND A.ROUTE_CODE=I.ROUTE_CODE AND H.UNIT_CODE=A.MEDI_UNIT AND " +
//				"A.DISPENSE_UNIT=G.UNIT_CODE AND    A.ORDER_CODE = B.ORDER_CODE AND A.BED_NO = D.BED_NO " +
//				"AND A.MR_NO=C.MR_NO AND A.ROUTE_CODE = F.ROUTE_CODE AND " +
//				"(A.ORDER_CAT1_CODE = 'PHA_W'  OR A.ORDER_CAT1_CODE = 'PHA_C') " +
//				"AND A.DISPENSE_FLG = 'N' AND A.CASE_NO IN ("+caseNos+") AND A.PHA_DISPENSE_NO IN" +
//						" ("+phaDispenseNo+") AND A.DSPN_KIND IN ( "+type+") " +
//								"AND F.CLASSIFY_TYPE IN ("+getDoseTypeByWhere()+" ) AND " +
//										"A.TAKEMED_ORG IN ("+flg+") GROUP BY A.CASE_NO,A.ORDER_CODE,D.BED_NO_DESC,A.MR_NO,A.BED_NO," +
//												"C.PAT_NAME,A.LINK_NO,B.ORDER_DESC, H.UNIT_CHN_DESC,I.ROUTE_CHN_DESC," +
//												"J.FREQ_CHN_DESC,G.UNIT_CHN_DESC,B.OWN_PRICE,B.SPECIFICATION,A.LINKMAIN_FLG," +
//												"A.DSPN_KIND,A.ORDER_NO,A.ORDER_SEQ,A.TAKE_DAYS ORDER BY A.BED_NO,A.MR_NO," +
//												"A.DSPN_KIND DESC,A.ORDER_NO,A.ORDER_SEQ,A.LINK_NO,A.LINKMAIN_FLG DESC";
		String bsql = "SELECT  A.CASE_NO,A.ORDER_CODE,D.BED_NO_DESC,A.MR_NO,C.PAT_NAME,A.LINK_NO," +
		"B.ORDER_DESC||' '||B.SPECIFICATION ORDER_DESC, SUM(A.MEDI_QTY) AS MEDI_QTY," +
		"H.UNIT_CHN_DESC AS MEDI_UNIT,I.ROUTE_CHN_DESC,J.FREQ_CHN_DESC,A.TAKE_DAYS," +
		"SUM (A.DISPENSE_QTY) AS DISPENSE_QTY,G.UNIT_CHN_DESC," +  
		"SUM (A.DOSAGE_QTY) *B.OWN_PRICE AS OWN_AMT,W.USER_NAME AS USER_NAME1 FROM ODI_DSPNM A,SYS_FEE B,SYS_PATINFO C, " +
		"SYS_BED D, SYS_PHAROUTE F,SYS_UNIT G,SYS_UNIT H,SYS_PHAROUTE I,SYS_PHAFREQ J,SYS_OPERATOR W  " +
		"WHERE A.FREQ_CODE=J.FREQ_CODE AND A.ROUTE_CODE=I.ROUTE_CODE AND H.UNIT_CODE=A.MEDI_UNIT AND " +
		"A.DISPENSE_UNIT=G.UNIT_CODE AND    A.ORDER_CODE = B.ORDER_CODE AND A.BED_NO = D.BED_NO " +
		"AND A.MR_NO=C.MR_NO AND A.ROUTE_CODE = F.ROUTE_CODE AND  A.ORDER_DR_CODE = W.USER_ID AND " +
		"(A.ORDER_CAT1_CODE = 'PHA_W'  OR A.ORDER_CAT1_CODE = 'PHA_C') " +
		"AND A.DISPENSE_FLG = 'N' AND A.CASE_NO IN ("+caseNos+") AND A.PHA_DISPENSE_NO IN" +  
				" ("+phaDispenseNo+") AND A.DSPN_KIND IN ( "+type+") " +
						"AND F.CLASSIFY_TYPE IN ("+getDoseTypeByWhere()+" ) AND " +
								"A.TAKEMED_ORG IN ("+flg+") GROUP BY A.CASE_NO,A.ORDER_CODE,D.BED_NO_DESC,A.MR_NO,A.BED_NO," +
										"C.PAT_NAME,A.LINK_NO,B.ORDER_DESC, H.UNIT_CHN_DESC,I.ROUTE_CHN_DESC," +
										"J.FREQ_CHN_DESC,G.UNIT_CHN_DESC,B.OWN_PRICE,B.SPECIFICATION,A.LINKMAIN_FLG," +
										"A.DSPN_KIND,A.ORDER_NO,A.ORDER_SEQ,A.TAKE_DAYS,W.USER_NAME ORDER BY A.BED_NO,A.MR_NO," +
										"A.DSPN_KIND DESC,A.ORDER_NO,A.ORDER_SEQ,A.LINK_NO,A.LINKMAIN_FLG,W.USER_NAME DESC";
		//System.out.println("bsql:::::::::::::::::::::::::"+bsql);
		
		TParm  searchprintParm = new TParm(TJDODBTool.getInstance()
				.select(bsql));  
		if (searchprintParm.getCount() <=0) {  
			this.messageBox("�޴�ӡ����");
			return;
		}
		TParm printParm = new TParm();
		for(int i=0;i<searchprintParm.getCount();i++) {
			String orderCode = searchprintParm.getValue("ORDER_CODE",i);
			String caseNo = searchprintParm.getValue("CASE_NO",i);
			printParm.addData("BED", searchprintParm.getValue("BED_NO_DESC",i));
			printParm.addData("MR_NO", searchprintParm.getValue("MR_NO",i));
			printParm.addData("NAME", searchprintParm.getValue("PAT_NAME",i));
			printParm.addData("LIAN", searchprintParm.getValue("LINK_NO",i));
			printParm.addData("ORDER_DESC", searchprintParm.getValue("ORDER_DESC",i));
			printParm.addData("YL1", searchprintParm.getValue("MEDI_QTY",i));
			printParm.addData("YL2", searchprintParm.getValue("MEDI_UNIT",i));
			printParm.addData("YF", searchprintParm.getValue("ROUTE_CHN_DESC",i));
			printParm.addData("PC", searchprintParm.getValue("FREQ_CHN_DESC",i));
			printParm.addData("DAYS", searchprintParm.getValue("TAKE_DAYS",i));
			printParm.addData("NUM1", searchprintParm.getValue("DISPENSE_QTY",i));
			printParm.addData("NUM2", searchprintParm.getValue("UNIT_CHN_DESC",i));
			String sql = "SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"'";
			TParm  batchParm = new TParm(TJDODBTool.getInstance()
					.select(sql));	
			if (batchParm.getCount() <=0) {
				printParm.addData("BATCH_NO", "");
			}else {
				printParm.addData("BATCH_NO", batchParm.getValue("BATCH_NO",i));										
			}			
			printParm.addData("TOT_PRICE", searchprintParm.getValue("OWN_AMT",i));
			printParm.addData("DR_NAME", searchprintParm.getValue("USER_NAME1",i));
		}
		printParm.setCount(searchprintParm.getCount());  
		printParm.addData("SYSTEM", "COLUMNS", "BED");    
		printParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		printParm.addData("SYSTEM", "COLUMNS", "NAME");
		printParm.addData("SYSTEM", "COLUMNS", "LIAN");
		printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");  
		printParm.addData("SYSTEM", "COLUMNS", "YL1");
		printParm.addData("SYSTEM", "COLUMNS", "YL2");  
		printParm.addData("SYSTEM", "COLUMNS", "YF");
		printParm.addData("SYSTEM", "COLUMNS", "PC");   
		printParm.addData("SYSTEM", "COLUMNS", "DAYS");
		printParm.addData("SYSTEM", "COLUMNS", "NUM1");       
		printParm.addData("SYSTEM", "COLUMNS", "NUM2");
		printParm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
		printParm.addData("SYSTEM", "COLUMNS", "TOT_PRICE");
		printParm.addData("SYSTEM", "COLUMNS", "DR_NAME");

		inParm.setData("TABLE", printParm.getData());      
		

		inParm.setData("USER_NAME","TEXT", Operator.getName());
		// ===zhangp 20121120 start
		inParm.setData("BAR_CODE", "TEXT",phaDispenseNo.replace("'", ""));
		// ===zhangp 20121120 end
		//fux modify 20150320
		//inParm.setData("DR_NAME","TEXT" ,parmRowData.getData("USER_NAME1")); // lirui
		// 2012-6-8
		// �ڱ�����չʾ����ҽʦ
		// luhai modify 2012-05-09 add begin ��ҩ��Ա����ҩʱ��ֿ����� end		
		
		if ("''".equalsIgnoreCase(phaDispenseNo)) {
			messageBox_("û������");
			return;
		} else {
			 if (!pha_ctrlcode.equals("") && this.getRadioButton("ST").isSelected()) {
					String barCodes = inParm.getValue("BAR_CODE");
					Set<String> set= distinctPhaDispenseNo(phaDispenseNo);
					if (null != set && set.size()>1) {
						this.messageBox("�龫��ҩ����ӡ��һ��ֻ�ܴ�ӡһ����ҩ��");
						return;
					}	
					if (null != barCodes && barCodes.length()>12) {
						barCodes = barCodes.substring(0, 12);
					}
					inParm.setData("BAR_CODE",barCodes);				 
					openPrintWindow(
							"%ROOT%\\config\\prt\\UDD\\UddDispenseConfirmListOfDrug_V45.jhw",
							inParm, false);
					return;                  
			 }else {   
					openPrintWindow(
							"%ROOT%\\config\\prt\\UDD\\UddDispenseConfirmListOfPs_V45.jhw",			
							inParm, false);   
					  
					return;
			}
	
		}

	}
  
	/**
	 * ��ӡ��ҩȷ�ϵ�
	 */
	public void onDispenseSheetByPhaDispenseNo(String pha_dispense_no) {
		TParm inParm = new TParm();  
		tblDtl = (TTable) getComponent("TBL_DTL");
		inParm.setData("FIRST_PRINT", Boolean.valueOf(true));
		TTextFormat station = (TTextFormat) getComponent("COMBO");
		String stationName = station.getText();    
		String stationCode = getValueString("COMBO");             
		if (StringUtil.isNullString(stationName))  
			stationName = "ȫԺ";  
		inParm.setData("STATION_NAME", stationName);
		if (getRadioButton("IN_STATION_TWO").isSelected()) {
			inParm.setData("DISPENSE_ORG_TWO","����");      
			inParm.setData("WHERE_5", "1");     
		}else {
			inParm.setData("WHERE_5", "2");  
			inParm.setData("DISPENSE_ORG_TWO","סԺҩ��");
		}   
        inParm.setData("DISPENSE_ORG_TWO", "TEXT", this.getText("EXEC_DEPT_CODE"));//wanglong add 20140812
		inParm.setData("START_DATE", TypeTool
				.getTimestamp(getValue("START_DATE")));
		inParm.setData("END_DATE", TypeTool.getTimestamp(getValue("END_DATE")));
		inParm.setData("DONE", Boolean.valueOf(TypeTool
				.getBoolean(getValue("UNCHECK"))));
		boolean isStation = TypeTool.getBoolean(getValue("STA"));
		/*if (!isStation) {
			messageBox_("������ҩƷ��Ϣ���ܰ�������ʾ");
			return;
		}*/

		inParm.setData("IS_STATION", Boolean.valueOf(isStation));
		String caseNos = getCaseNos();
		inParm.setData("WHERE_1", caseNos);
		inParm.setData("WHERE_2", pha_dispense_no);
		// ====zhangp 20121118 start
		String ctrl = "";
		String bar_code = "";
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		String drCode = "";
		
		TParm parm = tblDtl.getParmValue();
		int count = parm.getCount();
		if (!pha_ctrlcode.equals("") && this.getRadioButton("ST").isSelected()) {
			ctrl = "�龫";
			// ===zhangp 20130225 start  
			tblDtl.acceptText();       
			if (parm.getCount() > 0) {
				for (int i = 0; i < count; i++) {
					if (StringTool.getBoolean(parm.getValue("EXEC", i)) && parm.getValue("TAKEMED_ORG", i).equals("2"))
						if(pha_dispense_no.length()>0){
							bar_code = pha_dispense_no.replace("'", "");
						}
					break;
				}
			}
		}
		
		for (int i = 0; i < count; i++) {
			if (StringTool.getBoolean(parm.getValue("EXEC", i)))
				drCode = parm.getValue("ORDER_DR_CODE", i);
			break;
		}
/*		Set<String> set= distinctPhaDispenseNo(pha_dispense_no);
		if (null != set && set.size()>1) {
			this.messageBox("�龫��ҩ����ӡ��һ��ֻ�ܴ�ӡһ����ҩ��");
			return;
		}	
		String barCodes = inParm.getValue("BAR_CODE");
		if (null != barCodes && barCodes.length()>12) {
			barCodes = barCodes.substring(0, 12);
		}
		inParm.setData("BAR_CODE",barCodes);	*/		
		// ===zhangp 20130225 end
		if (this.getRadioButton("ST").isSelected()) {
			inParm.setData("TYPE_T", "��ʱҽ��" + ctrl + "��ҩȷ�ϵ�");
			inParm.setData("WHERE_3", "'ST','F'");
		} else if (this.getRadioButton("UD").isSelected()) {
			inParm.setData("TYPE_T", "����ҽ��" + ctrl + "��ҩȷ�ϵ�");
			inParm.setData("WHERE_3", "'UD'");
		} else {
			inParm.setData("TYPE_T", "��Ժ��ҩ" + ctrl + "��ҩȷ�ϵ�");
			inParm.setData("WHERE_3", "'DS'");
		}
		// ====zhangp 20121118 end

		if ("''".equalsIgnoreCase(caseNos)) {
			messageBox_("û������");
			return;
		}

		// ���ͷ���
		inParm.setData("WHERE_4", getDoseTypeByWhere());
		// �÷�
		inParm.setData("DOSE_TYPE", getDoseTypeText());
		// ===zhangp 20120709 start
		TParm parmData = tblPat.getParmValue();
		TParm parmRowData = new TParm();
		for (int i = 0; i < parmData.getCount("EXEC"); i++) {
			if (StringTool.getBoolean(parmData.getValue("EXEC", i))) {
				parmRowData = parmData.getRow(i);
				break;
			}
		}
		// ===zhangp 20120709 end
		// ������Ա��ʱ��
		// luhai modify 2012-05-09 add begin ��ҩ��Ա����ҩʱ��ֿ����� begin
		// String datetime = TJDODBTool.getInstance().getDBTime().toString()
		// .substring(0, 19).replace("-", "/");
		// inParm.setData("USER_NAME", "������: " + Operator.getName() + "  "
		// + datetime);
		String datetime = TJDODBTool.getInstance().getDBTime().toString()
				.substring(0, 19).replace("-", "/");
		inParm.setData("USER_NAME", "������: " + Operator.getName() + "");
		inParm.setData("CUR_DATE", datetime + "");
		// luhai modify 2012-05-09 add begin ��ҩ��Ա����ҩʱ��ֿ����� end
		// luhai 2012004-07 add ������ݴ�ǰ̨���� begin  
		// luhai 2012004-07 add ������ݴ�ǰ̨���� end
		// ===zhangp 20120709 start  
		inParm.setData("USER_NAME", Operator.getName());
		// ===zhangp 20121120 start  
		inParm.setData("BAR_CODE", bar_code);
		// ===zhangp 20121120 end
		//fux modify 20150320
		String sql = " SELECT USER_NAME  FROM SYS_OPERATOR WHERE USER_ID =  '"+drCode+"'";
		TParm parmSql = new TParm(TJDODBTool.getInstance().select(sql));
		inParm.setData("DR_NAME", parmSql.getValue("USER_NAME", 0).toString()); // lirui  
		// 2012-6-8   
		// �ڱ�����չʾ����ҽʦ    
		// ===zhangp 20120709 end  
		
		TParm printParm = new TParm();  
		for(int i=0;i<5;i++) {  
			printParm.addData("ORDER_DESC", i+"");
		}  
		printParm.setCount(5);
		printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		//fux modify 20150320
		//printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		inParm.setData("TABLE", printParm.getData());
		
		  
		if ("''".equalsIgnoreCase(pha_dispense_no)) {  
			messageBox_("û������");
			return;					
		} else {
		//	String[] dispenseOrgArr = new String[]{"1","2"};//1������2סԺҩ��
		//	String[] dispenseOrgDescArr = new String[]{"����","סԺҩ��"};//1������2סԺҩ��
		//	for (int i = 0; i < dispenseOrgArr.length; i++) {//һ�δ�ӡ������ҩ��
				inParm.setData("WHERE_5","'1','2'");			  
			//	inParm.setData("DISPENSE_ORG_TWO", dispenseOrgDescArr[i]);
				openPrintWindow(
						"%ROOT%\\config\\prt\\UDD\\UddDispenseConfirmList.jhw",
						inParm, false);				
		//	}
		//	return;
		}
	}
	
	/**
	 * �ж��м�����ҩ����
	 * @param phaDispenseNo
	 * @return true,����1����false 1��
	 */
	public Set<String> distinctPhaDispenseNo(String phaDispenseNo){
//		System.out.println(">>>>distinctPhaDispenseNo("+phaDispenseNo+")");
		boolean flg = false;
		Set<String> set = new HashSet<String>();
		if(null != phaDispenseNo && phaDispenseNo.length()>1){
			phaDispenseNo = phaDispenseNo.replaceAll("'", "");
			String[] strs = phaDispenseNo.split(",");
			if (null != strs && strs.length>1) {
				for (int i = 0; i < strs.length; i++) {
					set.add(strs[i]+"");
				}
			}
			
		}
		return set;
	}

	/**
	 * ��ӡͳҩ��
	 */
	public void onUnDispense() {
		if (getRadioButton("ALL_DISPENSE_ORG_ONE").isSelected()) {
			this.messageBox("��ѡ��ҩ���ţ�סԺҩ������");
			return;
		}
		// add
		if(queryFlg) {
			onQueryDtl();
			onQueryMed();
		}
		TParm inParm = new TParm();
		// luhai modify 2012-2-23 begin ��ͳҩ���Ƿ��ǵ�һ�δ�ӡ�Ĺ���ɾ����֮ǰһֱ����ֵ��begin
		// inParm.setData("FIRST_PRINT", Boolean.valueOf(false));
		// ǿ�����ó�true����һ�δ�ӡ��ǣ�
		inParm.setData("FIRST_PRINT", Boolean.valueOf(true));
		// luhai modify 2012-2-23 begin ��ͳҩ���Ƿ��ǵ�һ�δ�ӡ�Ĺ���ɾ����֮ǰһֱ����ֵ��end
		TTextFormat station = (TTextFormat) getComponent("COMBO");
		String stationName = station.getText();
		String stationCode = getValueString("COMBO");
		String dispenseOrg = "";
		if (StringUtil.isNullString(stationName))
			stationName = "ȫԺ";
		inParm.setData("STATION_NAME", stationName);
		if (getRadioButton("IN_HOSPITAL_ONE").isSelected()) {
			inParm.setData("DISPENSE_ORG", "TEXT","סԺҩ��");
		}
		if (getRadioButton("IN_STATION_ONE").isSelected()) {
			inParm.setData("DISPENSE_ORG", "TEXT","����");
		}
        inParm.setData("DISPENSE_ORG", "TEXT", this.getText("EXEC_DEPT_CODE"));//wanglong add 20140812
		inParm.setData("STATION_NAME", "TEXT", stationName);
		inParm.setData("START_DATE", TypeTool
				.getTimestamp(getValue("START_DATE")));
		inParm.setData("START_DATE", "TEXT", (TypeTool
				.getTimestamp(getValue("START_DATE")) + "").replace(".0", ""));
		inParm.setData("END_DATE", TypeTool.getTimestamp(getValue("END_DATE")));
		inParm.setData("END_DATE", "TEXT", (TypeTool
				.getTimestamp(getValue("END_DATE")) + "").replace(".0", ""));
		inParm.setData("DONE", Boolean.valueOf(TypeTool
				.getBoolean(getValue("UNCHECK"))));
		// ������ҩȷ�������Ϣ luhai 2012-03-16
		if (Boolean.valueOf(TypeTool.getBoolean(getValue("UNCHECK")))) {
			inParm.setData("DONE_MSG", "TEXT", "��ҩδȷ��");
		} else {
			inParm.setData("DONE_MSG", "TEXT", "��ҩ��ȷ��");
		}
		TParm tableParm = tblMed.getParmValue();
		boolean isStation = TypeTool.getBoolean(getValue("STA"));
		if (StringTool.getBoolean(getValueString("BY_ORDER"))) {
			if (!isStation) {
				messageBox_("������ҩƷ��Ϣ���ܰ�������ʾ");
				return;
			}
			TParm parm = new TParm();
			
			//fux modify 20150911 
			
			if (this.getRadioButton("UD").isSelected()) {
				inParm.setData("TITLE", "TEXT", "סԺҽ��ͳҩ��(����)");
				} else if(this.getRadioButton("ST").isSelected()){
					inParm.setData("TITLE", "TEXT", "סԺҽ��ͳҩ��(��ʱ/������)");    
				} else if(this.getRadioButton("DC").isSelected()){  
					inParm.setData("TITLE", "TEXT", "סԺҽ��ͳҩ��(��Ժ��ҩ)");
				}    
			for (int i = 0; i < tableParm.getCount("ORDER_CODE"); i++) {
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
				parm.addData("DISPENSE_QTY", Double.valueOf(tableParm
						.getDouble("DISPENSE_QTY", i)));
				parm.addData("DISPENSE_UNIT", tableParm.getValue(
						"DISPENSE_UNIT", i));
				parm.addData("OWN_PRICE", Double.valueOf(tableParm.getDouble(
						"OWN_PRICE", i)));
				parm.addData("OWN_AMT", Double.valueOf(tableParm.getDouble(
						"OWN_PRICE", i)
						* tableParm.getDouble("DISPENSE_QTY", i)));
			}
			parm.setCount(parm.getCount("ORDER_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");  
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
			parm.addData("SYSTEM", "COLUMNS", "DISPENSE_UNIT");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
			inParm.setData("TABLE", parm.getData());
			inParm.setData("IS_STATION", Boolean.valueOf(isStation));
//			System.out.println("----UddUndispenseOrderSum.jhw==inParm:"+inParm);
			openPrintWindow(
					"%ROOT%\\config\\prt\\UDD\\UddUndispenseOrderSum_V45.jhw",
					inParm, false);
		} else {
			if (tableParm.getCount("ORDER_CODE") <= 0) {
				messageBox_("û������");
				return;
			}
			String patName = "";
			TParm parm = new TParm();
			for (int i = 0; i < tableParm.getCount("ORDER_CODE"); i++) {
				parm.addData("BED_NO", tableParm.getValue("BED_NO", i));
				// parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
				parm.addData("MR_NO", "");// ����mr_no ����ʾ
				if (patName.equals(tableParm.getValue("PAT_NAME", i))) {
					parm.addData("PAT_NAME", "");
				} else {
					parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
				}
				patName = tableParm.getValue("PAT_NAME", i);
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
				parm.addData("DISPENSE_QTY", Double.valueOf(tableParm
						.getDouble("DISPENSE_QTY", i)));
				parm.addData("DISPENSE_UNIT", tableParm.getValue(
						"DISPENSE_UNIT", i));
				parm.addData("OWN_PRICE", Double.valueOf(tableParm.getDouble(
						"OWN_PRICE", i)));
				parm.addData("OWN_AMT", Double.valueOf(tableParm.getDouble(
						"OWN_PRICE", i)
						* tableParm.getDouble("DISPENSE_QTY", i)));
			}

			parm.setCount(parm.getCount("ORDER_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "BED_NO");
			parm.addData("SYSTEM", "COLUMNS", "MR_NO");
			parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
			parm.addData("SYSTEM", "COLUMNS", "DISPENSE_UNIT");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");  
			parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
			//fux modify 20150911
			if (this.getRadioButton("UD").isSelected()) {
				inParm.setData("TITLE", "TEXT", "סԺҽ��ͳҩ��(����)");
				} else if(this.getRadioButton("ST").isSelected()){
					inParm.setData("TITLE", "TEXT", "סԺҽ��ͳҩ��(��ʱ/������)");    
				} else if(this.getRadioButton("DC").isSelected()){  
					inParm.setData("TITLE", "TEXT", "סԺҽ��ͳҩ��(��Ժ��ҩ)");
				}  
			
			inParm.setData("TABLE", parm.getData());
//			System.out.println("UddDispenseOrderDetail.jhw=inParm:"+inParm);
			openPrintWindow(
					"%ROOT%\\config\\prt\\UDD\\UddDispenseOrderDetail_V45.jhw",
					inParm, false);
			// luhai modify 2012-04-05 end
		}
	}

	/**
	 * ��ʿվcombo�ĵ���¼�
	 */
	public void onStationQuery() {
		onQuery();
	}

	/**
	 * ������ѡ�¼�
	 */
	public void onStation() {
		setValue("QUERY_BED", "");
		queryBed.setVisible(false);
		callFunction("UI|NO|setVisible", new Object[] { Boolean.valueOf(true) });
	}

	/**
	 * �����ŵ�ѡ�¼�
	 */
	public void onMrNo() {
		setValue("QUERY_BED", "");
		queryBed.setVisible(false);
		callFunction("UI|NO|setVisible", new Object[] { Boolean.valueOf(true) });
	}

	/**
	 * ������ѡ�¼�
	 */
	public void onBedNo() {
		setValue("QUERY_BED", "");
		queryBed.setVisible(true);
		callFunction("UI|NO|setVisible",
				new Object[] { Boolean.valueOf(false) });
	}

	/**
	 * �����б�ȫ��ִ��check_box����¼�
	 */
	public void onExecAll() {
		if (tblPat == null)
			return;
		TParm parm = tblPat.getParmValue();
		if (parm == null)
			return;
		int count = parm.getCount("EXEC");
		boolean value = TypeTool.getBoolean(getValue("EXEC_ALL"));
		for (int i = 0; i < count; i++) {
			 // wanglong add 20150226
            String caseNo = parm.getValue("CASE_NO", i);
            String sql =
                    "SELECT * FROM ADM_INP WHERE CASE_NO = '" + caseNo
                            + "' AND DS_DATE IS NOT NULL";
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                this.messageBox("ִ�г��� " + result.getErrText());
                return;
            }
            if (result.getCount() > 0) {
                this.messageBox(parm.getValue("PAT_NAME", i) + "�Ѿ���Ժ��");
                continue;
            }
            // add end
			parm.setData("EXEC", i, Boolean.valueOf(value));
			tblPat.setValueAt(Boolean.valueOf(value), i, 0);
		}

		onQueryMed();
		onQueryDtl();
	}

	/**
	 * ȱҩ��ϸ��ѯ
	 */
	public void onLackStore() {
		if (saveParm == null) {
			messageBox_("û����ҩ����");
			return;
		}
		int count = saveParm.getCount();
		if (count < 1) {
			messageBox_("û����ҩ����");
			return;
		}
		TParm parm = new TParm();
		String names[] = saveParm.getNames();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < names.length; i++)
			if (i < names.length - 1)
				sb.append(names[i]).append(";");

		String name = sb.toString();
		for (int i = 0; i < count; i++)
			parm.addRowData(saveParm, i, name);

		parm = UddChnCheckTool.getInstance().groupByStockParm(parm);
		// ====zhangp 20120803 start
		String tables = "";
		String conditions = "";
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("")) {
			tables = " ,SYS_FEE G, PHA_BASE H, SYS_CTRLDRUGCLASS I ";
			conditions = " AND A.ORDER_CODE = G.ORDER_CODE"
					+ " AND G.ORDER_CODE = H.ORDER_CODE"
					+ " AND H.CTRLDRUGCLASS_CODE = I.CTRLDRUGCLASS_CODE"
					+ " AND I.CTRL_FLG = '" + pha_ctrlcode + "'";
			 if ("DOSAGE".equalsIgnoreCase(controlName)) {// wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
	                conditions += " AND (G.IS_REMARK <> 'Y' OR G.IS_REMARK IS NULL) ";
	         }
		} else if ("DOSAGE".equalsIgnoreCase(controlName)) {// wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
	            tables = " ,SYS_FEE G ";
	            conditions +=
                    " AND A.ORDER_CODE = G.ORDER_CODE AND (G.IS_REMARK <> 'Y' OR G.IS_REMARK IS NULL) ";
		}
		parm.setData("TABLES", tables);
		parm.setData("CONDITIONS", conditions);
		// ====zhangp 20120803 end
		TParm result = INDTool.getInstance().defectIndStockQTY(parm);
		count = result.getCount("ORDER_CODE");
		if (count < 0) {
			messageBox_("������ҩ��������");
			return;
		} else {
			tblSht.setParmValue(result);
			return;
		}
	}

	// public void onDtlClick() {
	// int row = tblDtl.getSelectedRow();
	// if (row < 0)
	// return;
	// if (saveParm == null)
	// return;
	// int count = saveParm.getCount();
	// if (count < 1)
	// return;
	// String colName = "EXEC";
	// saveParm.setData(colName, row, Boolean.valueOf(!TypeTool
	// .getBoolean(saveParm.getData("EXEC", row))));
	// tblDtl.setParmValue(saveParm);
	// for (int i = 0; i < count; i++)
	// if (!TypeTool.getBoolean(saveParm.getData("EXEC", i))) {
	// setValue("EXEC_ALL_DTL", Boolean.valueOf(false));
	// return;
	// }
	//
	// setValue("EXEC_ALL_DTL", Boolean.valueOf(true));
	// }
	// ȫ��ִ��
	public void onDoEXE() {
		// �õ���ǰִ������״̬
		boolean nowFlag = (Boolean) this
				.callFunction("UI|ALLEXECUTE|isSelected");
		// �õ�����
		int ordCount = (Integer) this.callFunction("UI|TBL_DTL|getRowCount");
		for (int i = 0; i < ordCount; i++) {
			// ѭ��ȡ���Թ�������
			this.callFunction("UI|TBL_DTL|setValueAt", nowFlag, i, 0);
			saveParm.setData("EXEC", i, nowFlag);
			// ѭ������ÿһ�����ݵĵ�һ�е�ֵ�����ʣ�
		}
	}

	// ��ҩ��ȫ��
	public void onATCDo() {
		// �õ���ǰִ������״̬
		boolean nowFlag = (Boolean) this.callFunction("UI|ALLATCDO|isSelected");
		// �õ�����
		int ordCount = (Integer) this.callFunction("UI|TBL_DTL|getRowCount");
		for (int i = 0; i < ordCount; i++) {
			String ATCFlg = getATCFlgFromSYSFee(saveParm.getRow(i).getValue(
					"ORDER_CODE"));
			String orderNo = saveParm.getValue("ORDER_NO", i);
			int orderSeq = saveParm.getInt("ORDER_SEQ", i);
			String caseNo = saveParm.getValue("CASE_NO", i);
			String orderCode = saveParm.getValue("ORDER_CODE", i);
			String boxFlg = getBoxFlgFromOdiorder(orderNo, caseNo, orderSeq);
			if (ATCFlg.length() == 0 || ATCFlg.equals("N")
					|| boxFlg.equals("Y"))
				continue;
			// ѭ��ȡ���Թ�������
			this.callFunction("UI|TBL_DTL|setValueAt", nowFlag, i, 4);
			// ѭ������ÿһ�����ݵĵ�һ�е�ֵ�����ʣ�
			saveParm.setData("SENDATC_FLG", i, nowFlag);
		}
	}

	/**
	 * ����CASE_NOȡ�÷���ȼ�
	 * 
	 * @param case_no
	 *            String
	 * @return String
	 */
	private String getServiceLevel(String case_no) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		TParm result = ADMInpTool.getInstance().selectall(parm);
		return result.getValue("SERVICE_LEVEL", 0);
	}

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	// /**
	// * ������ҩ����XML�ļ����ϳ���
	// */
	// public void onSendACT() {
	// TParm pat_parm = tblPat.getParmValue();
	// String pat_sql =
	// " SELECT B.PAT_NAME, A.MR_NO, A.IPD_NO, A.BED_NO, A.DEPT_CODE, "
	// + " C.DEPT_CHN_DESC, A.STATION_CODE, D.STATION_DESC "
	// + " FROM ADM_INP A, SYS_PATINFO B, SYS_DEPT C, SYS_STATION D "
	// + " WHERE A.MR_NO = B.MR_NO "
	// + " AND A.DEPT_CODE = C.DEPT_CODE "
	// + " AND A.STATION_CODE = D.STATION_CODE ";
	// for (int i = 0; i < pat_parm.getCount("CASE_NO"); i++) {
	// if (!"Y".equals(pat_parm.getValue("EXEC", i))) {
	// continue;
	// }
	// String case_no = pat_parm.getValue("CASE_NO", i);
	// TParm patInfo = new TParm(TJDODBTool.getInstance().select(
	// pat_sql + " AND A.CASE_NO='" + case_no + "'"));
	// if (patInfo == null || patInfo.getCount("PAT_NAME") <= 0) {
	// // System.out.println(case_no + "������Ϣ����");
	// continue;
	// }
	// TParm parm = new TParm();
	// // ��������
	// parm.setData("NAME", patInfo.getValue("PAT_NAME", 0));
	// // ������
	// parm.setData("MRNO", patInfo.getValue("MR_NO", 0));
	// // סԺ��
	// parm.setData("IPDNO", patInfo.getValue("IPD_NO", 0));
	// // ������
	// parm.setData("BED_NO", patInfo.getValue("BED_NO", 0));
	// // ����
	// parm.setData("DEPT", patInfo.getValue("DEPT_CODE", 0));
	// // ��������
	// parm.setData("DEPT_DESC", patInfo.getValue("DEPT_CHN_DESC", 0));
	// // ����
	// parm.setData("STATION_CODE", patInfo.getValue("STATION_CODE", 0));
	// // ��������
	// parm.setData("STATION_DESC", patInfo.getValue("STATION_DESC", 0));
	// // ��ҩ����
	// parm.setData("DATE", SystemTool.getInstance().getDate().toString()
	// .substring(0, 19));
	// // סԺע��
	// parm.setData("TYPE", "2");
	//
	// // ҩƷ�б�
	// TParm orderListParm = new TParm();
	// int seq = 1;
	// String order_sql =
	// " SELECT B.ORDER_CODE, B.GOODS_DESC, B.ALIAS_DESC, B.TRADE_ENG_DESC, C.ROUTE_CHN_DESC "
	// + " FROM PHA_BASE A, SYS_FEE B, SYS_PHAROUTE C "
	// + " WHERE A.ORDER_CODE = B.ORDER_CODE "
	// + " AND A.ROUTE_CODE = C.ROUTE_CODE"
	// + " AND B.ATC_FLG_I = 'Y'";
	// TParm order_parm = tblDtl.getParmValue();
	// for (int j = 0; j < order_parm.getCount("ORDER_CODE"); j++) {
	// // �ж��Ƿ�ѡ
	// if (!"Y".equals(order_parm.getValue("EXEC", j))) {
	// continue;
	// }
	// // �ж��Ƿ�Ϊ���˵�ҩƷ
	// if (!case_no.equals(order_parm.getValue("CASE_NO", j))) {
	// continue;
	// }
	// // ���ݰ�ҩ��ע���жϸ�ҩƷ�Ƿ��Ͱ�ҩ��
	// String order_code = order_parm.getValue("ORDER_CODE", j);
	// // System.out.println("order_parm---" + order_parm);
	// // System.out.println("orderInfo---" + order_sql +
	// // " AND A.ORDER_CODE='" + order_code + "'");
	// TParm orderInfo = new TParm(TJDODBTool.getInstance().select(
	// order_sql + " AND A.ORDER_CODE='" + order_code + "'"));
	// if (orderInfo == null || orderInfo.getCount("ORDER_CODE") <= 0) {
	// // System.out.println(order_code+"------------");
	// continue;
	// }
	// // ���
	// orderListParm.addData("SEQ", seq);
	// // ҩƷ����
	// orderListParm.addData("ORDER_CODE",
	// orderInfo.getValue("ORDER_CODE", 0));
	// // ҩƷ��Ʒ��
	// orderListParm.addData("ORDER_GOODS_DESC",
	// orderInfo.getValue("GOODS_DESC", 0));
	// // ҩƷ��ѧ��
	// orderListParm.addData("ORDER_CHEMICAL_DESC",
	// orderInfo.getValue("ALIAS_DESC", 0));
	// // ҩƷӢ����
	// orderListParm.addData("ORDER_ENG_DESC",
	// orderInfo.getValue("TRADE_ENG_DESC", 0));
	// // ����
	// orderListParm.addData("QTY",
	// order_parm.getDouble("DISPENSE_QTY", j));
	// // ��ҩƵ��
	// orderListParm.addData("FREQ",
	// order_parm.getValue("FREQ_CODE", j));
	// // Ͷҩ����ʱ��
	// orderListParm.addData("DAY",
	// order_parm.getValue("ORDER_DATE", j).substring(0, 19));
	// // ��ҩʱ��
	// orderListParm.addData(
	// "DRUG_DATETIME",
	// order_parm.getValue("DISPENSE_EFF_DATE", j).substring(
	// 0, 19));
	// // ��ҩ;��(�ڷ�/����)
	// orderListParm.addData("ROUTE",
	// orderInfo.getValue("ROUTE_CHN_DESC", 0));
	// // ��ǰ����(0:��;1:��ǰ;2:����)
	// orderListParm.addData("MEAL_FLG", "");
	// // �ײ�ʱ��
	// // System.out.println(order_parm.getValue("START_DTTM", j));
	// orderListParm.addData(
	// "START_DTTM",
	// StringTool
	// .getTimestamp(
	// order_parm.getValue("START_DTTM", j),
	// "yyyyMMddHHmm").toString()
	// .substring(0, 19));
	// // �Ͱ�ע��
	// orderListParm.addData("FLG", "");
	// // ��ʱ����ҽ��
	// orderListParm.addData("OrderType", getRadioButton("ST")
	// .isSelected() ? "ST" : "UD");
	// seq++;
	// }
	// if (orderListParm == null || orderListParm.getCount("SEQ") <= 0) {
	// return;
	// }
	// parm.setData("DRUG_LIST_PARM", orderListParm.getData());
	// parm = TIOM_AppServer.executeAction("action.pha.PHAATCAction",
	// "onATCI", parm);
	// if (parm.getErrCode() < 0) {
	// messageBox("�Ͱ�ҩ��ʧ��");
	// return;
	// }
	// messageBox("�Ͱ�ҩ���ɹ�");
	// }
	// }
	/**
	 * ��ҩ������
	 */
	public void onGenATCFile() {
		String type = this.getValueString("ATC_TYPE");
		String machineNo = this.getValueString("ATC_MACHINENO");
		if (type.equals("1"))
			this.onOldATCFile();
		else if (type.equals("2")) {
			if (machineNo.equals("")) {
				this.messageBox("��ҩ��̨�Ų���Ϊ��");
				return;
			}
			try {
				this.onNewATCInsert(machineNo);
			} catch (Exception e) {
				System.out.println("�Ͱ�ҩ�������쳣");
			}
		}
	}

	/**
	 * ȡ�ñ��ѡ��������
	 * 
	 * @param rowName
	 *            String
	 * @param tableName
	 *            String
	 * @return String
	 */
	private String getTableSelectRowData(String rowName, String tableName) {
		return getTableRowData(getTable(tableName).getSelectedRow(), rowName,
				tableName);
	}

	/**
	 * ���к�ȡ�ø�������
	 * 
	 * @param row
	 *            int
	 * @param rowName
	 *            String
	 * @param tableName
	 *            String
	 * @return String
	 */
	private String getTableRowData(int row, String rowName, String tableName) {
		return getTable(tableName).getParmValue().getValue(rowName, row);
	}

	/**
	 * �����Ͱ�ҩ����txt�ļ�
	 */
	public void onOldATCFile() {
		// System.out.println("-----------------------1");
		if (getTable("TBL_PAT").getSelectedRow() < 0) {
			messageBox("��ѡ�񲡻���Ϣ");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", "I");
		int count = 0;
		// ҩƷ�б�
		TParm drugListParm = new TParm();
		for (int i = 0; i < saveParm.getCount(); i++) {
			TParm nowOrder = saveParm.getRow(i);
			// �Ͱ�ҩ��ע��
			if (!nowOrder.getBoolean("SENDATC_FLG"))
				continue;
			TParm desc = getOrderData(nowOrder.getValue("ORDER_CODE"));
			TParm ransRate = getPHAOrderTransRate(nowOrder
					.getValue("ORDER_CODE"));
			if (nowOrder.getValue("DSPN_KIND").equals("DS")
					|| nowOrder.getValue("DSPN_KIND").equals("ST")) {
				// ����
				drugListParm.addData("DSPN_KIND", nowOrder
						.getValue("DSPN_KIND"));
				// ��ҩ��
				drugListParm.addData("PRESCRIPT_NO", "0");
				// ����
				drugListParm.addData("PAT_NAME", nowOrder.getValue("PAT_NAME"));
				// ������
				drugListParm.addData("MR_NO", nowOrder.getValue("MR_NO"));
				// �Ͱ�ҩ��ʱ��
				drugListParm.addData("DATE", ("" + SystemTool.getInstance()
						.getDate()).substring(0, 19));
				// ҩƷ�б����
				drugListParm.addData("SEQ", i + 1);
				// ҩƷ����
				drugListParm.addData("ORDER_CODE", nowOrder
						.getValue("ORDER_CODE"));
				// ҩƷ��Ʒ��
				drugListParm.addData("ORDER_GOODS_DESC", desc.getData(
						"TRADE_ENG_DESC", 0));
				int time = getFreqData(nowOrder.getValue("FREQ_CODE")).getInt(
						"FREQ_TIMES", 0);
				double qty = nowOrder.getDouble("DOSAGE_QTY")
						/ nowOrder.getInt("TAKE_DAYS");
				double Minqty = (double) (qty) / time;
				// ҩƷ����
				drugListParm.addData("QTY", Minqty);
				// ҩƷƵ��
				drugListParm.addData("FREQ", nowOrder.getValue("FREQ_CODE"));
				// ��Ժ��ҩ
				if (nowOrder.getValue("DSPN_KIND").equals("DS")) {
					// �ײ�ʱ�䴫��Ĭ��ֵ
					drugListParm.addData("START_DTTM", "000000000000");
					// ��ҩ����
					drugListParm.addData("DAY", nowOrder.getInt("TAKE_DAYS"));
				}
				// ��ʱ
				else if (nowOrder.getValue("DSPN_KIND").equals("ST")) {
					// �ײ�ʱ�䴫��Ĭ��ֵ
					drugListParm.addData("START_DTTM", nowOrder
							.getValue("START_DTTM"));
					drugListParm.addData("DAY", "");
				}
				// �Ͱ�ע�Ǵ���Ĭ��ֵ
				drugListParm.addData("FLG", "Y");
				count++;
			} else {
				String start = nowOrder.getValue("START_DTTM");
				String end = nowOrder.getValue("END_DTTM");
				// ��ѯϸ���SQL
				String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
						+ "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_DATE_REAL,DOSAGE_QTY FROM ODI_DSPND "
						+ "WHERE CASE_NO='"
						+ nowOrder.getValue("CASE_NO")
						+ "' AND ORDER_NO='"
						+ nowOrder.getValue("ORDER_NO")
						+ "' AND ORDER_SEQ='"
						+ nowOrder.getValue("ORDER_SEQ")
						+ "' "
						+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
						+ start
						+ "','YYYYMMDDHH24MISS') "
						+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
						+ end
						+ "','YYYYMMDDHH24MISS')"
						+ " ORDER BY ORDER_DATE||ORDER_DATETIME";

				// ����ϸ���TDS,����������
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if (result.getCount() <= 0 || result.getErrCode() < 0) {
					continue;
				}
				for (int j = 0; j < result.getCount(); j++) {
					// ����
					drugListParm.addData("DSPN_KIND", nowOrder
							.getValue("DSPN_KIND"));
					// ��ҩ��
					drugListParm.addData("PRESCRIPT_NO", "0");
					// ����
					drugListParm.addData("PAT_NAME", nowOrder
							.getValue("PAT_NAME"));
					// ������
					drugListParm.addData("MR_NO", nowOrder.getValue("MR_NO"));
					// �Ͱ�ҩ��ʱ��
					drugListParm.addData("DATE", ("" + SystemTool.getInstance()
							.getDate()).substring(0, 19));
					// ҩƷ�б����
					drugListParm.addData("SEQ", i + 1);
					// ҩƷ����
					drugListParm.addData("ORDER_CODE", nowOrder
							.getValue("ORDER_CODE"));
					// ҩƷ��Ʒ��
					drugListParm.addData("ORDER_GOODS_DESC", desc.getData(
							"TRADE_ENG_DESC", 0));
					// ҩƷ����
					drugListParm.addData("QTY", result.getDouble("DOSAGE_QTY",
							j));
					// ҩƷƵ��
					drugListParm
							.addData("FREQ", nowOrder.getValue("FREQ_CODE"));
					drugListParm.addData("START_DTTM", result.getValue(
							"ORDER_DATE", j)
							+ result.getValue("ORDER_DATETIME", j));
					drugListParm.addData("DAY", "");

					// �Ͱ�ע�Ǵ���Ĭ��ֵ
					drugListParm.addData("FLG", "Y");
					count++;
				}
			}
		}
		if (count > 0) {
			if (drugListParm.getCount("SEQ") <= 0)
				return;
			parm.setData("DRUG_LIST_PARM", drugListParm.getData());
			parm.setData("TYPE", "1");
			parm = TIOM_AppServer.executeAction("action.pha.PHAATCAction",
					"onATCI", parm);
			if (parm.getErrCode() < 0) {
				messageBox("�Ͱ�ҩ��ʧ��");
				return;
			}
			messageBox("�Ͱ�ҩ���ɹ�");
		}
	}

	/**
	 * ��ҩ�����ݲ���
	 */
	public void onNewATCInsert(String machineNo) {
		TParm parm = new TParm();
		Pat pat = new Pat();
		String stationCode = "";
		int count = 0;
		String rxStr = "";
		String strtool1 = "";
		// ȥ���ظ�BarCode��Map
		Set setConst = new HashSet();
		// �ظ�BarCode�ĸ���Map
		Map setIsConstCount = new HashMap();
		// �ظ�seq��Map
		Map setIsConst = new HashMap();
		// �ظ�����
		int isConst = 1;
		for (int i = 0; i < saveParm.getCount(); i++) {
			TParm inparm = saveParm.getRow(i);
			// �Ͱ�ҩ��ע��
			if (!inparm.getBoolean("SENDATC_FLG"))
				continue;
			strtool1 = inparm.getValue("BAR_CODE");
			// ȥ���ظ�
			if (!setConst.contains(strtool1)) {
				if (rxStr.length() > 0)
					rxStr += ",";
				rxStr += strtool1;
				setConst.add(strtool1);
			} else {
				isConst++;
				setIsConstCount.put(strtool1, isConst);
			}
		}
		// System.out.println("�ظ�������" + isConst);
		// setConst=null;
		// System.out.println("-=-------------" + rxStr);
		Map seqMap = new HashMap();
		if (!rxStr.equals("") || rxStr.length() > 0)
			seqMap = TXNewATCTool.getInstance().executeQuery(rxStr, "I");
		else
			return;
		// System.out.println("-=---Map----------" + seqMap);
		String preStr1 = "";
		String preStr2 = "";
		int seq = 0;
		Map map = new HashMap();
		Map map1 = new HashMap();
		String preNo = "";
		// ҩƷ�б�
		TParm drugListParm = new TParm();
		TParm HISParm = new TParm();
		for (int i = 0; i < saveParm.getCount(); i++) {
			TParm inparm = saveParm.getRow(i);
			// �Ͱ�ҩ��ע��
			if (!inparm.getBoolean("SENDATC_FLG"))
				continue;
			String sendTime = TXNewATCTool.getInstance().getSendAtcFlg(inparm);
			if (!sendTime.equals("")) {
				switch (this.messageBox("��ʾ��Ϣ", "������: "
						+ inparm.getValue("MR_NO") + " ���� :"
						+ inparm.getValue("PAT_NAME") + " ҽ�� : "
						+ inparm.getValue("ORDER_DESC") + "\r\n"
						+ "���͹���ҩ�����ϴ�ʱ��:" + sendTime + "���Ƿ����ͣ�",
						this.YES_NO_OPTION)) {
				case 0: // ����
					break;
				case 1: // ������
					continue;
				}
			}
			String BarCode = inparm.getValue("BAR_CODE");
			if (BarCode.equals("")) {
				System.out.println("�Ͱ�ҩ��ҽ���쳣BAR_CODE���: ������ "
						+ inparm.getValue("MR_NO") + " ���� "
						+ inparm.getValue("PAT_NAME") + "ҽ��  "
						+ inparm.getValue("ORDER_CODE"));
				continue;
			}
			// ��ҩ������
			drugListParm.setData("TYPE", 2);
			// ������ 1
			drugListParm.addData("PRESCRIPTIONNO", inparm.getValue("BAR_CODE"));
			preStr1 = BarCode;
			// ��ͬ����ǩ��ȡ˳���
			if (map1.get(preStr1) == null) {
				// if (!preStr1.equals(preStr2)) {
				// // �˴���ǩ�Ŵ��ڿ���ȡ����+1
				seq = (Integer) seqMap.get(BarCode) + 1;
				// System.out.println("���ظ���:" + BarCode + "    seq:" + seq);
				// ˳��� 2
				drugListParm.addData("SEQNO", seq);
				preStr2 = preStr1;
				map.put(preStr1, seq);
			} else {
				// int iseq = (Integer)setIsConstCount.get(preStr1);
				// System.out.println("�ظ���:" + BarCode + "    seq:" + seq +
				// "   iseq:" + iseq);
				// if(!setIsConst.containsKey(preStr1)){
				// setIsConst.put(preStr1, seq);
				// }
				seq = (Integer) map.get(preStr1) + 1;
				// System.out.println("-----seq------" + seq);
				// ˳��� 2
				drugListParm.addData("SEQNO", seq);
				map.put(preStr1, seq);
			}
			map1.put(preStr1, preStr1);
			// ��ţ�Ĭ�ϣ�3
			drugListParm.addData("GROUP_NO", 1);
			// �����ţ�ҩ����̨���ã� 4
			drugListParm.addData("MACHINENO", TypeTool.getInt(machineNo));
			// ����״̬��Ĭ�ϣ� 5
			drugListParm.addData("PROCFLG", 0);
			// ����ID 6
			drugListParm.addData("PATIENTID", inparm.getValue("MR_NO"));
			// �������� 7
			drugListParm.addData("PATIENTNAME", inparm.getValue("PAT_NAME"));
			pat = pat.onQueryByMrNo(inparm.getValue("MR_NO"));
			// ��������ƴ��8
			drugListParm.addData("ENGLISHNAME", "");
			// �������� 9
			drugListParm.addData("BIRTHDAY", pat.getBirthday());
			// �Ա� 10
			drugListParm.addData("SEX", pat.getSexCode());
			// ��� ���� �� 1:���� 2:סԺ[����] 3:סԺ[��ʱ] ��Ժ��ҩ�������ﴦ��
			String ioFlg = "";
			if (inparm.getValue("DSPN_KIND").equals("UD")
					|| inparm.getValue("DSPN_KIND").equals("F"))
				ioFlg = "2";
			else if (inparm.getValue("DSPN_KIND").equals("ST"))
				ioFlg = "3";
			else if (inparm.getValue("DSPN_KIND").equals("DS"))
				ioFlg = "1";
			drugListParm.addData("IOFLG", ioFlg);
			// �������� 12
			drugListParm.addData("WARDCD", inparm.getValue("STATION_CODE"));
			// �������� 13
			drugListParm.addData("WARDNAME", this.getStationData(
					inparm.getValue("STATION_CODE"))
					.getValue("STATION_DESC", 0));
			// ������14
			drugListParm.addData("ROOMNO", this.getRoomDesc(inparm
					.getValue("ROOM_CODE")));
			// ������ 15
			drugListParm.addData("BEDNO", inparm.getValue("BED_NO_DESC"));
			// ҽʦ���� 16
			drugListParm.addData("DOCTORCD", inparm.getValue("ORDER_DR_CODE")
					.length() > 7 ? new String(inparm.getValue("ORDER_DR_CODE")
					.getBytes(), 0, 7) : inparm.getValue("ORDER_DR_CODE"));
			// ҽʦ���� 17
			drugListParm.addData("DOCTORNAME", getDrDesc(inparm
					.getValue("ORDER_DR_CODE")));
			// ����ʱ�� 18
			drugListParm.addData("PRESCRIPTIONDATE", inparm
					.getTimestamp("ORDER_DATE"));
			int day = inparm.getInt("TAKE_DAYS");
			int time = getFreqData(inparm.getValue("FREQ_CODE")).getInt(
					"FREQ_TIMES", 0);
			// ����ҽ��
			if (ioFlg.equals("2")) {
				String start = inparm.getValue("START_DTTM");
				TParm sysparm = new TParm(TJDODBTool.getInstance().select(
						"SELECT * FROM ODI_SYSPARM"));
				// �[ˎ�r�g
				String udDispTime = ("" + SystemTool.getInstance().getDate())
						.substring(0, 10).replaceAll("-", "")
						+ sysparm.getValue("DSPN_TIME", 0);
				if ((StringTool.getTimestamp(start.substring(0, 12),
						"yyyyMMddHHmm").getTime() < StringTool.getTimestamp(
						udDispTime, "yyyyMMddHHmm").getTime())
						&& inparm.getValue("DSPN_KIND").equals("UD")) {
					System.out.println("�Ͱ�ҩ������ҽ�������쳣�t���_ʼ�r�gС춮�ǰ�[ˎ�r�g: ������ "
							+ inparm.getValue("MR_NO") + " ���� "
							+ inparm.getValue("PAT_NAME") + "ҽ��  "
							+ inparm.getValue("ORDER_CODE") + " �_ʼ�r�g��" + start);
				}
				String end = inparm.getValue("END_DTTM");
				// ��ѯϸ���SQL
				String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
						+ "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_DATE_REAL FROM ODI_DSPND "
						+ "WHERE CASE_NO='"
						+ inparm.getValue("CASE_NO")
						+ "' AND ORDER_NO='"
						+ inparm.getValue("ORDER_NO")
						+ "' AND ORDER_SEQ='"
						+ inparm.getValue("ORDER_SEQ")
						+ "' "
						+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
						+ start
						+ "','YYYYMMDDHH24MISS') "
						+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
						+ end
						+ "','YYYYMMDDHH24MISS')"
						+ " ORDER BY ORDER_DATE||ORDER_DATETIME";

				// ����ϸ���TDS,����������
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if (result.getCount() <= 0) {
					System.out.println("�Ͱ�ҩ������ҽ���쳣����]�Д���: ������ "
							+ inparm.getValue("MR_NO") + " ���� "
							+ inparm.getValue("PAT_NAME") + "ҽ��  "
							+ inparm.getValue("ORDER_CODE"));
					// ��һ����ҩʱ�� 19
					drugListParm.addData("TAKEDATE", "");
					// ��ʼ���õ�ʱ����� 20
					drugListParm.addData("TAKETIME", "");
					// �����õ�ʱ����� 21
					drugListParm.addData("LASTTIME", "");
				} else {
					// ��һ����ҩʱ�� 19
					drugListParm.addData("TAKEDATE", StringTool.getTimestamp(
							result.getValue("ORDER_DATE", 0), "yyyyMMdd"));
					// ��ʼ���õ�ʱ����� 20
					drugListParm.addData("TAKETIME", 
							result.getValue("ORDER_DATETIME", 0));
					// �����õ�ʱ����� 21
					drugListParm.addData("LASTTIME",
							result.getValue("ORDER_DATETIME", result.getCount() - 1));
				}
				int datecount=result.getCount()<=0?1:result.getCount();
				day=(int) Math.ceil((double)datecount/time);
				// �������� 33
				drugListParm.addData("DISPENSE_DAYS", day);
			}
			// סԺ��ʱ
			else if (ioFlg.equals("3")) {
				// ��һ����ҩʱ�� 19
				drugListParm.addData("TAKEDATE", SystemTool.getInstance()
						.getDate());
				// ��ʼ���õ�ʱ����� 20
				drugListParm.addData("TAKETIME", "2355");
				// �����õ�ʱ����� 21
				drugListParm.addData("LASTTIME", "2355");
				// �������� 33
				drugListParm.addData("DISPENSE_DAYS", day);
			}
			// ��Ժ��ҩ
			else if (ioFlg.equals("1")) {
				// ��һ����ҩʱ�� 19
				drugListParm.addData("TAKEDATE", SystemTool.getInstance()
						.getDate());
				// ��ʼ���õ�ʱ����� 20
				drugListParm.addData("TAKETIME", "");
				// �����õ�ʱ����� 21
				drugListParm.addData("LASTTIME", "");
				// �������� 33
				drugListParm.addData("DISPENSE_DAYS", day);

			}
			// �������Ĭ��Ϊ1�� 22
			drugListParm.addData("PRESC_CLASS", 0);
			// ҩƷ���� 23
			drugListParm.addData("DRUGCD", inparm.getValue("ORDER_CODE"));
			TParm order = getOrderData(inparm.getValue("ORDER_CODE"));
			// ҩƷ�� 24
			drugListParm.addData("DRUGNAME", order.getValue("ORDER_DESC", 0)
					+ "" + order.getValue("SPECIFICATION", 0));
			// ҩƷ����(Ĭ��Ϊ��) 25
			drugListParm.addData("DRUGSHAPE", "");
			double mediQty = inparm.getDouble("MEDI_QTY");
			// ��ҩ���� 26
			drugListParm.addData("PRESCRIPTIONDOSE", mediQty);
			// ��ҩ��λ 27
			drugListParm.addData("PRESCRIPTIONUNIT", getUnitDesc(inparm
					.getValue("MEDI_UNIT")));
			double dispenQty = inparm.getDouble("DISPENSE_QTY");
			double qty = (double) (mediQty / this.getPHAOrderTransRate(
					inparm.getValue("ORDER_CODE")).getDouble("MEDI_QTY", 0));
			BigDecimal sf = new BigDecimal(String.valueOf(qty));
			BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
			// System.out.println("---------------------------------------------4");
			// ��ҩ���� 28
			drugListParm.addData("DISPENSEDDOSE", data.doubleValue());
			// ��ҩ������ 29
			drugListParm.addData("DISPENSEDTOTALDOSE", dispenQty);
			// ��ҩ��λ 30
			drugListParm.addData("DISPENSEDUNIT", getUnitDesc(inparm
					.getValue("DISPENSE_UNIT")));
			// ÿ�������� 31
			drugListParm.addData("AMOUNT_PER_PACKAGE", this
					.getPHAOrderTransRate(inparm.getValue("ORDER_CODE"))
					.getDouble("MEDI_QTY", 0));
			String mandesc = this.getManDesc(getOrderData(
					inparm.getValue("ORDER_CODE")).getValue("MAN_CODE", 0));
			// ������ 32
			drugListParm.addData("FIRM_ID",
					mandesc.getBytes().length > 20 ? new String(mandesc
							.getBytes(), 0, 20) : mandesc);
			// Ƶ�� 34
			// drugListParm
			// .setData("FREQ_DESC_CODE", inparm.getValue("FREQ_CODE"));
			drugListParm.addData("FREQ_DESC_CODE", "");
			// Ƶ������ 35
			drugListParm.addData("FREQ_DESC", getFreqData(
					inparm.getValue("FREQ_CODE")).getValue("FREQ_CHN_DESC", 0));
			// һ����ô������գ� 36
			drugListParm.addData("FREQ_COUNTER", "");
			String timeCode = TXNewATCTool.getTimeLine(inparm
					.getValue("FREQ_CODE"));
			// סԺ���� �� ��Ժ��ҩ
			if (ioFlg.equals("2") || ioFlg.equals("1")) {
				// ����ʱ����� 37
				drugListParm.addData("FREQ_DESC_DETAIL_CODE", timeCode);
				String timeDetail = TXNewATCTool.getTimeDetail(inparm
						.getValue("FREQ_CODE"));
				// ����ʱ����ϸ 38
				drugListParm.addData("FREQ_DESC_DETAIL", timeDetail);
			}
			// סԺ��ʱ
			else if (ioFlg.equals("3")) {
				drugListParm.addData("FREQ_DESC_DETAIL_CODE", "2355");
				drugListParm.addData("FREQ_DESC_DETAIL", "����");
			}
			// ��ҩ˵������ 39
			drugListParm.addData("EXPLANATION_CODE", "");
			// ��ҩ˵�� 40
			drugListParm.addData("EXPLANATION", "");
			// ��ҩ;�� 41
			drugListParm.addData("ADMINISTRATION_NAME", this
					.getRouteDesc(inparm.getValue("ROUTE_CODE")));
			// ��ע 42
			drugListParm.addData("DOCTORCOMMENT", "");
			// ��ҩ˳�� 43
			drugListParm.addData("BAGORDERBY", "");
			// ����ʱ�� 44
			drugListParm.addData("MAKERECTIME", ("" + SystemTool.getInstance()
					.getDate()).substring(0, 19));
			// �Է�����ʱ�� 45
			drugListParm.addData("UPDATERECTIME", "");
			// Ԥ�� 46
			drugListParm.addData("FILLER", "");
			// ҽ���� 47
			drugListParm.addData("ORDER_NO", Long.parseLong(inparm
					.getValue("ORDER_NO")));
			// ˳��� 48
			drugListParm.addData("ORDER_SUB_NO", inparm.getValue("ORDER_SEQ"));
			// ������ʱ����
			// �����ӡ��ʽ 49
			drugListParm.addData("BAGPRINTFMT", "");
			// �ߴ� 50
			drugListParm.addData("BAGLEN", "");
			// ��ҩ�� 51
			drugListParm.addData("TICKETNO", "");
			// ҩ����ӡ�ò����� 52
			drugListParm.addData("BAGPRINTPATIENTNM", "");
			// Ԥ���ô�ӡ���ݣ��������� 53
			drugListParm.addData("FREEPRINTITEM_PRESC1", "");
			// Ԥ���ô�ӡ���ݣ�����2�� 54
			drugListParm.addData("FREEPRINTITEM_PRESC2", "");
			// Ԥ���ô�ӡ���ݣ�����3�� 55
			drugListParm.addData("FREEPRINTITEM_PRESC3", "");
			// Ԥ���ô�ӡ���ݣ�����4�� 56
			drugListParm.addData("FREEPRINTITEM_PRESC4", "");
			// Ԥ���ô�ӡ���ݣ�����5�� 57
			drugListParm.addData("FREEPRINTITEM_PRESC5", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ1�� 58
			drugListParm.addData("FREEPRINTITEM_DRUG1", inparm
					.getValue("ORDER_NO"));
			// Ԥ���ô�ӡ���ݣ�ҩƷ2�� 59
			drugListParm.addData("FREEPRINTITEM_DRUG2", inparm
					.getValue("ORDER_SEQ"));
			// Ԥ���ô�ӡ���ݣ�ҩƷ3�� 60
			drugListParm.addData("FREEPRINTITEM_DRUG3", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ4�� 61
			drugListParm.addData("FREEPRINTITEM_DRUG4", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ5�� 62
			drugListParm.addData("FREEPRINTITEM_DRUG5", "");
			// �ۺϰ�ҩ�ñ�־λ(���й���ʹ�� 63
			drugListParm.addData("SYNTHETICFLG", "");
			// 0:����ֽ��1:�ڴ˴�����׷��һ������ֽ 64
			drugListParm.addData("CUTFLG", "");
			// ����ʱ�䣨number�͡��޷����뺺�֣� 65
			drugListParm.addData("PHARMACYTIME", "");
			// ҩƷ�ϵĿ�ӡ 66
			drugListParm.addData("CARVEDSEAL", "");
			// ҩƷ��ӡ��� 67
			drugListParm.addData("CARVEDSEALABB", "");
			// ������Ϣ�����룱 68
			drugListParm.addData("PREBARCODE1", "");
			// ������Ϣ�����룲 69
			drugListParm.addData("PREBARCODE2", "");
			// ҩƷ��Ϣ������ 70
			drugListParm.addData("PREDRUGBARCODE", "");
			// �������ʽ 71
			drugListParm.addData("PREBARCODEFMT", "");
			// ===========================================================
			drugListParm.addData("CASE_NO", inparm.getValue("CASE_NO"));
			drugListParm.addData("HISORDER_NO", inparm.getValue("ORDER_NO"));
			drugListParm.addData("ORDER_SEQ", inparm.getValue("ORDER_SEQ"));
			drugListParm.addData("START_DTTM", inparm.getValue("START_DTTM"));
			drugListParm.addData("BAR_CODE", inparm.getValue("BAR_CODE"));
			drugListParm.addData("END_DTTM", inparm.getValue("END_DTTM"));
			drugListParm.addData("OPT_USER", Operator.getID());
			drugListParm.addData("OPT_TERM", Operator.getIP());
			count++;
		}
		drugListParm.setCount(count);
		if (count > 0) {
			parm.setData("DRUG_LIST_PARM", drugListParm.getData());
			parm.setData("TYPE", "2");
			parm = TIOM_AppServer.executeAction("action.pha.PHAATCAction",
					"onATCI", parm);
			if (parm.getErrCode() == -1) {
				messageBox("�Ͱ�ҩ��ʧ��");
				return;
			} else if (parm.getErrCode() == -2) {
				messageBox("��������ʧ��");
				return;
			} else {
				messageBox("�Ͱ�ҩ���ɹ�");
			}
		}
	}

	/**
	 * ȡ��ҩƷ��ҩ��λ�Ϳ�浥λת����
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getPHAOrderTransRate(String orderCode) {
		return new TParm(getDBTool().select(
				" SELECT DOSAGE_QTY/STOCK_QTY TRANS_RATE,MEDI_QTY,DOSAGE_UNIT "
						+ " FROM PHA_TRANSUNIT " + " WHERE ORDER_CODE='"
						+ orderCode + "'"));
	}

	/**
	 * ȡ�ò�������
	 * 
	 * @param stationCode
	 * @return
	 */
	public TParm getStationData(String stationCode) {
		return new TParm(getDBTool().select(
				" SELECT STATION_DESC,MACHINENO,ATC_TYPE "
						+ " FROM SYS_STATION " + " WHERE STATION_CODE='"
						+ stationCode + "'"));
	}

	/**
	 * ȡ��ҩƷ����
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderData(String orderCode) {
		return new TParm(
				getDBTool()
						.select(
								" SELECT ORDER_DESC,GOODS_DESC,ALIAS_DESC,TRADE_ENG_DESC,DESCRIPTION,MAN_CODE,SPECIFICATION"
										+ " FROM SYS_FEE"
										+ " WHERE ORDER_CODE='"
										+ orderCode
										+ "'"));
	}

	/**
	 * ȡ�ÿ�������
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT DEPT_CHN_DESC" + " FROM SYS_DEPT "
						+ " WHERE DEPT_CODE='" + deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * ȡ����Ա����
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getDrDesc(String userId) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT USER_NAME " + " FROM SYS_OPERATOR "
						+ " WHERE USER_ID='" + userId + "'"));
		String userName = "";
		if (parm.getCount() > 0)
			userName = parm.getValue("USER_NAME", 0);
		return userName;
	}

	/**
	 * ȡ�õ�λ����
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getUnitDesc(String unitCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT UNIT_CHN_DESC " + " FROM SYS_UNIT "
						+ " WHERE UNIT_CODE='" + unitCode + "'"));
		String unitDesc = "";
		if (parm.getCount() > 0)
			unitDesc = parm.getValue("UNIT_CHN_DESC", 0);
		return unitDesc;
	}

	/**
	 * ȡ����ҩ;������
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getRouteDesc(String routeCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT ROUTE_CHN_DESC " + " FROM SYS_PHAROUTE "
						+ " WHERE ROUTE_CODE='" + routeCode + "'"));
		String routeDesc = "";
		if (parm.getCount() > 0)
			routeDesc = parm.getValue("ROUTE_CHN_DESC", 0);
		return routeDesc;
	}

	/**
	 * ȡ��Ƶ������
	 * 
	 * @param freqCode
	 * @return
	 */
	public TParm getFreqData(String freqCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT FREQ_CHN_DESC,FREQ_TIMES " + " FROM SYS_PHAFREQ "
						+ " WHERE FREQ_CODE='" + freqCode + "'"));
		return parm;
	}

	/**
	 * ȡ��������������
	 * 
	 * @param freqCode
	 * @return
	 */
	public String getManDesc(String manCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT MAN_CHN_DESC " + " FROM SYS_MANUFACTURER "
						+ " WHERE MAN_CODE='" + manCode + "'"));
		String manDesc = "";
		if (parm.getCount() > 0)
			manDesc = parm.getValue("MAN_CHN_DESC", 0);
		return manDesc;
	}

	/**
	 * ȡ�÷�������
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getRoomDesc(String roomCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT ROOM_DESC" + " FROM SYS_ROOM " + " WHERE ROOM_CODE='"
						+ roomCode + "'"));
		return parm.getValue("ROOM_DESC", 0);
	}

	/**
	 * ��������table���޸������¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDownTableCheckBoxChangeValue(Object obj) {

		// ��õ����table����
		TTable tableDown = (TTable) obj;
		// ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
		tableDown.acceptText();
		// ���ѡ�е���/��
		int col = tableDown.getSelectedColumn();
		int row = tableDown.getSelectedRow();
		// ��������table�ϵ��
		// ���ѡ�е��ǵ����оͼ���ִ�ж���--�Ͱ�ҩ��
		if (col == 0) {
			boolean exeFlg;
			// ��õ��ʱ��ֵ
			exeFlg = TCM_Transform.getBoolean(tableDown.getValueAt(row, col));
			saveParm.setData("EXEC", row, exeFlg);
		}
		if (col == 4) {
			String orderNo = saveParm.getValue("ORDER_NO", row);
			int orderSeq = saveParm.getInt("ORDER_SEQ", row);
			String caseNo = saveParm.getValue("CASE_NO", row);
			String orderCode = saveParm.getValue("ORDER_CODE", row);
			String ATCFlg = getATCFlgFromSYSFee(orderCode);
			String boxFlg = getBoxFlgFromOdiorder(orderNo, caseNo, orderSeq);
			if (tableDown.getValueAt(row, col).equals("Y")
					&& (ATCFlg.length() == 0 || ATCFlg.equals("N"))) {
				callFunction("UI|TBL_DTL|setValueAt", "N", row, 0);
				tableDown.acceptText();
				this.tblDtl.setItem(row, "SENDATC_FLG", "N");
				messageBox("��ҩƷ�޷��Ͱ�ҩ��");
				return;
			}
			if (tableDown.getValueAt(row, col).equals("Y")
					&& boxFlg.equals("Y")) {
				callFunction("UI|TBL_DTL|setValueAt", "N", row, 0);
				tableDown.acceptText();
				this.tblDtl.setItem(row, "SENDATC_FLG", "N");
				messageBox("�м�ҩƷ�޷��Ͱ�ҩ��");
				return;
			}
			// ��õ��ʱ��ֵ
			ATCFlg = TCM_Transform.getString(tableDown.getValueAt(row, col));
			// ����ִ�б��
			saveParm.setData("SENDATC_FLG", row, TypeTool.getBoolean(ATCFlg));
		}
		// ===zhangp 20120802 start  
		TTabbedPane TTabbedPane = (TTabbedPane) getComponent("TTabbedPane");
		if (TTabbedPane.getSelectedIndex() == 1) {
			TParm tableParm = tableDown.getParmValue();
			String link_no = tableParm.getValue("LINK_NO", row);
			String case_no = tableParm.getValue("CASE_NO", row);
			String exec = tableParm.getValue("EXEC", row);
			for (int i = 0; i < tableParm.getCount("EXEC"); i++) {
				if (tableParm.getValue("LINK_NO", i).equals(link_no)
						&& !link_no.equals("")
						&& tableParm.getValue("CASE_NO", i).equals(case_no)) {
					tableParm.setData("EXEC", i, exec);
				}
			}
			tblDtl.setParmValue(tableParm);
		}
		// ===zhangp 20120802 end
	}
	
	public boolean onDownTableBatchNoChangeValue(TTableNode tNode) {
		int row = tNode.getRow();
		int column = tNode.getColumn();
		String batchNo = "";
		String colName = tblDtl.getParmMap(column);
		if(column==23) {
			if ("BATCH_NO".equalsIgnoreCase(colName)) {
				 batchNo = (String) tNode.getValue();			
			}
			saveParm.setData("BATCH_NO",row,batchNo);	
		}	
		/*TTable tableDown = (TTable) obj;
		int col = tableDown.getSelectedColumn();
		int row = tableDown.getSelectedRow();
		this.messageBox(col+"=="+row);		*/
		return false;
	}

	// /**
	// * ��������table���޸������¼�
	// *
	// * @param obj
	// * Object
	// */
	// public void onPatTableCheckBoxChangeValue(Object obj) {
	//
	// // ��õ����table����
	// TTable tableDown = (TTable) obj;
	// // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
	// tableDown.acceptText();
	// int col = tableDown.getSelectedColumn();
	// int row=this.tblPat.getSelectedRow();
	// // ��������table�ϵ��
	// if (col == 0) {
	// if ("DOSAGE".equalsIgnoreCase(controlName)) {
	// // this.messageBox_("fafa");
	// // this.messageBox_(tblPat.getValueAt(row, col)+"");
	// if (TCM_Transform.getBoolean(tblPat.getValueAt(row, col))) {
	// String stationCode = getTableSelectRowData("STATION_CODE",
	// "TBL_PAT");
	// // this.messageBox_(stationCode);
	// String machineNo = this.getStationData(stationCode)
	// .getValue("MACHINENO", 0);
	// String atcType = this.getStationData(stationCode).getValue(
	// "ATC_TYPE", 0);
	// // this.messageBox_(atcType);
	// callFunction("UI|ALLATCDO|isSelected",false);
	// this.setValue("ATC_MACHINENO", machineNo);
	// this.setValue("ATC_TYPE", atcType);
	// } else {
	// // this.messageBox_(tblPat.getValueAt(row, col)+"");
	// callFunction("UI|ALLATCDO|isSelected",false);
	// this.setValue("ATC_MACHINENO", "");
	// this.setValue("ATC_TYPE", "");
	// }
	// }
	// }
	// }

	/**
	 * סԺ��ҩ��ע��
	 * 
	 * @param orderCode
	 * @return
	 */
	private String getATCFlgFromSYSFee(String orderCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT ATC_FLG_I " + " FROM SYS_FEE" + " WHERE ORDER_CODE='"
						+ orderCode + "'"));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("ATC_FLG_I", 0);
	}

	/**
	 * ��ҩ��װע��
	 * 
	 * @param orderNo
	 * @param caseNo
	 * @param Seq
	 * @return
	 */
	private String getBoxFlgFromOdiorder(String orderNo, String caseNo, int Seq) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT GIVEBOX_FLG " + " FROM ODI_ORDER"
						+ " WHERE ORDER_NO='" + orderNo + "' AND CASE_NO='"
						+ caseNo + "' AND ORDER_SEQ=" + Seq));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("GIVEBOX_FLG", 0);
	}

	/**
	 * ȡ��ѡ��ļ��ͷ���
	 * 
	 * @return String
	 */
	private String getDoseType() {
		String getDoseType = "";
		List list = new ArrayList();
		if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
			list.add("O");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
			list.add("E");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
			list.add("I");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
			list.add("F");
		}

		if (list == null || list.size() == 0) {
			return "";
		} else {
			getDoseType = " AND F.CLASSIFY_TYPE IN (";
			for (int i = 0; i < list.size(); i++) {
				getDoseType = getDoseType + "'" + list.get(i) + "' ,";
			}
			getDoseType = getDoseType.substring(0, getDoseType.length() - 1)
					+ ")";
		}
		return getDoseType;
	}

	/**
	 * ȡ��ѡ��ļ��ͷ���
	 * 
	 * @return String
	 */
	private String getDoseTypeByWhere() {
		String getDoseType = "";
		List list = new ArrayList();
		if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
			list.add("O");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
			list.add("E");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
			list.add("I");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
			list.add("F");
		}

		if (list == null || list.size() == 0) {
			return "";
		} else {
			for (int i = 0; i < list.size(); i++) {
				getDoseType = getDoseType + "'" + list.get(i) + "' ,";
			}
			getDoseType = getDoseType.substring(0, getDoseType.length() - 1);
		}
		return getDoseType;
	}

	/**
	 * ȡ��ѡ��ļ��ͷ������ڱ�����ʾ
	 * 
	 * @return String
	 */
	private String getDoseTypeText() {
		String getDoseType = "";
		if ("N".equals(this.getValueString("DOSE_TYPEO"))
				|| "N".equals(this.getValueString("DOSE_TYPEE"))
				|| "N".equals(this.getValueString("DOSE_TYPEI"))
				|| "N".equals(this.getValueString("DOSE_TYPEF"))) {
			List list = new ArrayList();
			if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
				list.add("�ڷ�");
			}
			if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
				list.add("����");
			}
			if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
				list.add("���");
			}
			if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
				list.add("���");
			}

			if (list == null || list.size() == 0) {
				return "";
			} else {
				for (int i = 0; i < list.size(); i++) {
					getDoseType = getDoseType + list.get(i) + " ,";
				}
				getDoseType = "�÷�: "
						+ getDoseType.substring(0, getDoseType.length() - 1);
			}
		} else {
			getDoseType = "�÷�: ȫ��";
		}
		return getDoseType;

	}

	/**
	 * �õ����ݿ����Tool
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ȡ�ñ��ؼ�
	 * 
	 * @param tableName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableName) {
		return (TTable) getComponent(tableName);
	}

	// ***************************
	// luhai 2012-3-7 add ƿǩ��ӡ
	// ***************************
	/**
	 * 
	 * ƿǩ��ӡ luhai 2012-2-24
	 */
	public void onPrintPasterBottle(int abc) {
		Vector vct = new Vector();
		TParm parm = this.tblDtl.getParmValue();
		System.out.println("oooooo"+parm);
		for (int i = 0; i < 26; i++) {//  24���25  ����һ������  machao
			vct.add(new Vector());
		}
		String cat1Type = "";
		String orderCode = "";
		String orderDesc = "";
		String Dosetype = "";
		String birthdate = "";
		// System.out.println("=====���ݣ�"+parm);
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			cat1Type = parm.getData("CAT1_TYPE", i) + "";
			orderCode = (String) parm.getData("ORDER_CODE", i);
			orderDesc = (String) parm.getData("ORDER_DESC", i);
			if (TypeTool.getBoolean(tblDtl.getValueAt(i, 0))) {
				if (cat1Type.equals("PHA")) {
					Dosetype = SysPhaBarTool.getInstance().getDoseType(
							orderCode);
					if (!Dosetype.equals("I") && !Dosetype.equals("F")) {
						this.messageBox(orderDesc + "����������Σ����ܴ�ӡ��");
						return;
					}			
				}
			}
			if (!("true".equals(this.tblDtl.getValueAt(i, 0) + "") || ("Y"
					.equals(this.tblDtl.getValueAt(i, 0) + "")))) {
				continue;
			}
			//���ҩƷ�����Ѵ�ӡȷ��xiongwg20150128 start
//			 String orderDate = parm.getValue("ORDER_DATE",i).substring(0,4)+
//			 parm.getValue("ORDER_DATE",i).substring(5,7)+
//			 parm.getValue("ORDER_DATE",i).substring(8,10);
			 if(parm.getValue("EXEC", i).equals("Y")){
				String sqlbar = "SELECT BAR_CODE FROM ODI_DSPND " +
				"WHERE CASE_NO='"+parm.getValue("CASE_NO",i)	+
				"' AND ORDER_NO='"+parm.getValue("ORDER_NO",i)	+
				"' AND ORDER_SEQ='"+parm.getValue("ORDER_SEQ",i)	+
//				"' AND ORDER_DATE='"+orderDate	+
				"' AND ORDER_DATE||ORDER_DATETIME BETWEEN '"	+
				parm.getValue("START_DTTM",i)+"' AND '"	+
				parm.getValue("END_DTTM",i)	+	"'";
				TParm bar = new TParm(TJDODBTool.getInstance().select(sqlbar));
				if(bar.getCount()<0 || bar.getValue("BAR_CODE",0).equals("")){
					this.messageBox("��������ҩƷ����");
					return;
				}
			}
			//���ҩƷ�����Ѵ�ӡȷ��xiongwg20150128 end
			((Vector) vct.get(0)).add(parm.getData("BED_NO", i));
			((Vector) vct.get(1)).add(parm.getData("MR_NO", i));
			((Vector) vct.get(2)).add(parm.getData("PAT_NAME", i));
			((Vector) vct.get(3)).add(parm.getData("LINKMAIN_FLG", i));
			((Vector) vct.get(4)).add(parm.getData("LINK_NO", i));
			((Vector) vct.get(5)).add(parm.getData("ORDER_DESC", i));
			((Vector) vct.get(6)).add(parm.getData("MEDI_QTY", i));
			((Vector) vct.get(7)).add(parm.getData("MEDI_UNIT", i));
			((Vector) vct.get(8)).add(parm.getData("ORDER_CODE", i));
			((Vector) vct.get(9)).add(parm.getData("ORDER_NO", i));
			((Vector) vct.get(10)).add(parm.getData("ORDER_SEQ", i));
			((Vector) vct.get(11)).add(parm.getData("START_DTTM", i));
			((Vector) vct.get(12)).add(parm.getData("FREQ_CODE", i));
			((Vector) vct.get(13)).add(getStationDesc(parm.getValue(
					"STATION_CODE", i)));
			TParm patParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT *" + " FROM SYS_PATINFO A" + " WHERE MR_NO ='"
							+ parm.getData("MR_NO", i) + "'"));
			TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT CHN_DESC" + " FROM SYS_DICTIONARY A "
							+ " WHERE GROUP_ID = 'SYS_SEX'" + " AND  ID = '"
							+ patParm.getData("SEX_CODE", 0) + "'"));
			((Vector) vct.get(14)).add(sexDescParm.getData("CHN_DESC", 0));
			((Vector) vct.get(15)).add(StringUtil.getInstance().showAge(
					(Timestamp) patParm.getData("BIRTH_DATE", 0),
					SystemTool.getInstance().getDate()));
			String udSt = "";
			if (parm.getData("DSPN_KIND", i).equals("UD")
					|| parm.getData("DSPN_KIND", i).equals("F"))
				udSt = "����ҽ��";
			else if (parm.getData("DSPN_KIND", i).equals("ST"))
				udSt = "��ʱҽ��";
			TParm routeParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT * FROM SYS_PHAROUTE WHERE ROUTE_CODE='"
							+ parm.getData("ROUTE_CODE", i) + "'"));
			((Vector) vct.get(16)).add(udSt);
			((Vector) vct.get(17)).add(routeParm.getValue("ROUTE_CHN_DESC", 0));
			((Vector) vct.get(18)).add(getOperatorName(parm.getValue(
					"ORDER_DR_CODE", i)));
			((Vector) vct.get(19)).add(parm.getData("DISPENSE_QTY", i));
			((Vector) vct.get(20)).add(parm.getData("DISPENSE_UNIT", i));
			// �������
			((Vector) vct.get(21)).add(parm.getData("CLASSIFY_TYPE", i));// ------------------------------
			// ((Vector) vct.get(21)).add("I");//------------------------------
			// ����CASE_NO
			((Vector) vct.get(22)).add(parm.getData("CASE_NO", i));
			// ���� END_DTTM
			((Vector) vct.get(23)).add(parm.getData("END_DTTM", i));
			// ��������  machao
			((Vector) vct.get(24)).add(parm.getData("INFLUTION_RATE", i));
			// ��������
			birthdate = patParm.getValue("BIRTH_DATE", 0);
			if (null != birthdate && birthdate.length() >= 10) {
				birthdate = birthdate.substring(0, 4) + "��"
						+ birthdate.substring(5, 7) + "��"
						+ birthdate.substring(8, 10) + "��";
			}
			((Vector) vct.get(25)).add(birthdate);
		}
		vct.add(getUnitMap());
		// openWindow("%ROOT%\\config\\inw\\INWPrintBottonUI.x", vct);
		// ��ӡƿǩ��ֱ�Ӵ�ӡ��
		printBottle(vct);
	}

	/**
	 * 
	 * ��ӡƿǩ���� luhai 2012-2-28
	 * 
	 * @param buttonVct
	 */
	public void printBottle(Vector buttonVct) {
		parm = initPageData((Vector) buttonVct); 
		System.out.println("22222:"+parm);
		Object objPha = (buttonVct).get((buttonVct).size() - 1);
		if (objPha != null) {
			phaMap = (Map) (buttonVct).get((buttonVct).size() - 1);
		}
		// ��ӡƿǩ
		// ѡ����
		int row = 0;
		// ѡ����
		int column = 0;

		int count = parm.getCount("BED_NO");
		if (count <= 0) {
			this.messageBox_("û��Ҫ��ӡ��ҽ����");
			return;
		}
		TParm actionParm = creatPrintData();
		System.out.println("actionParm:"+actionParm);
		int rowCount = actionParm.getCount("PRINT_DATAPQ");
		if (rowCount <= 0) {
			this.messageBox_("��ӡ���ݴ���");
			return;
		}
		// ***************************************************
		// ���´����ӡƿǩ���������������ϵ�һ���н��д�ӡ luhai 2012-2-29 begin
		// ***************************************************
		TParm printDataPQParm = new TParm();
		int pRow = row;
		int pColumn = column;
		int cnt = 0;
		int rowNull = 0;
		for (int i = 0; i < 15; i++) {
			if (i % 3 == 0 && i != 0) {
				// cnt = 0 ;
				rowNull++;
			}
			if (i < pRow * 3 + pColumn) {
				printDataPQParm.addData("ORDER_DATE_" + (cnt + 1), "");
				printDataPQParm.addData("BED_" + (cnt + 1), "");
				printDataPQParm.addData("PAT_NAME_" + (cnt + 1), "");
				//add by wukai ������
				printDataPQParm.addData("LINK_NO_" + (cnt + 1), "");
				//add by wukai ������
				printDataPQParm.addData("BAR_CODE_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_1_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_1_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_1_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_2_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_2_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_2_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_3_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_3_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_3_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_4_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_4_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_4_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_5_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_5_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_5_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_6_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_6_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_6_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_7_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_7_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_7_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_8_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_8_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_8_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_9_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_9_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_9_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_10_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_10_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_10_" + (cnt + 1), "");
				printDataPQParm.addData("STATION_DESC_" + (cnt + 1), "");
				printDataPQParm.addData("MR_NO_" + (cnt + 1), "");
				printDataPQParm.addData("SEX_" + (cnt + 1), "");
				printDataPQParm.addData("AGE_" + (cnt + 1), "");
				printDataPQParm.addData("RX_TYPE_" + (cnt + 1), "");
				printDataPQParm.addData("FREQ_CODE_" + (cnt + 1), "");
				printDataPQParm.addData("DOCTOR_" + (cnt + 1), "");
				printDataPQParm.addData("ROUT_" + (cnt + 1), "");
				printDataPQParm.addData("PAGE_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_NAME_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_QTY_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_TOT_" + (cnt + 1), "");
				// add by liyh 20121217������ҩʦ
				printDataPQParm.addData("TITLE_CHECK_DR_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_CHECK_DATE_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_DR_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_CHECK_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_EXE_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_PAGEF_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_PAGEB_" + (cnt + 1), "");
				
				printDataPQParm.addData("RUNTE_1_" + (cnt + 1), "");//��������  machao
				printDataPQParm.addData("RUNTE_2_" + (cnt + 1), "");//��������  machao
				printDataPQParm.addData("RUNTE_3_" + (cnt + 1), "");//��������  machao
				printDataPQParm.addData("RUNTE_4_" + (cnt + 1), "");//��������  machao
				printDataPQParm.addData("RUNTE_5_" + (cnt + 1), "");//��������  machao
				printDataPQParm.addData("BIRTHDATE", "");// ���ӳ�������
				// cnt++;
				continue;
			} else {
				break;
			}
		}
		// ���ñ�������ִ��ʱ��ʵ��������Ŀ
		int realtotCount = 0;
		for (int i = 0; i < rowCount; i++) {
			TParm temp = (TParm) actionParm.getData("PRINT_DATAPQ", i);
			printDataPQParm.addData("ORDER_DATE_" + (pColumn + 1), temp
					.getData("DATETIME"));
			printDataPQParm.addData("BED_" + (pColumn + 1), temp
					.getData("BED_NO"));
			printDataPQParm.addData("PAT_NAME_" + (pColumn + 1), temp
					.getData("PAT_NAME"));
			// this.messageBox( temp.getData("BAR_CODE")+"");
			//add by wukai ������
			
			printDataPQParm.addData("LINK_NO_" + (pColumn + 1), getLinkNo(temp.getValue("LINK_NO")));
			//this.messageBox(temp.getValue("LINK_NO"));
			//add by wukai ������
			printDataPQParm.addData("BAR_CODE_" + (pColumn + 1), temp
					.getData("BAR_CODE"));
			printDataPQParm.addData("STATION_DESC_" + (pColumn + 1), temp
					.getData("STATION_DESC"));
			printDataPQParm.addData("MR_NO_" + (pColumn + 1), temp
					.getData("MR_NO"));
			printDataPQParm
					.addData("SEX_" + (pColumn + 1), temp.getData("SEX"));
			printDataPQParm
					.addData("AGE_" + (pColumn + 1), temp.getData("AGE"));
			printDataPQParm.addData("BIRTHDATE", temp.getData("BIRTHDATE"));
			printDataPQParm.addData("RX_TYPE_" + (pColumn + 1), temp
					.getData("RX_TYPE"));
			
			String freqDesc = getFreqDesc(temp.getData("FREQ"));
			printDataPQParm.addData("FREQ_CODE_" + (pColumn + 1), freqDesc);
			printDataPQParm.addData("DOCTOR_" + (pColumn + 1), temp
					.getData("DOCTOR"));
			printDataPQParm.addData("ROUT_" + (pColumn + 1), temp
					.getData("ROUTE"));
			printDataPQParm.addData("PAGE_" + (pColumn + 1), temp
					.getData("PAGE"));

			printDataPQParm.addData("TITLE_NAME_" + (pColumn + 1), "ҩ��");
			printDataPQParm.addData("TITLE_QTY_" + (pColumn + 1), "����");
			printDataPQParm.addData("TITLE_TOT_" + (pColumn + 1), "����");
			// modify by lim 2012/04/29 begin
//			printDataPQParm.addData("TITLE_CHECK_DR_" + (pColumn + 1), "���ҩʦ:"
//					+ Operator.getName());
//			printDataPQParm.addData("TITLE_CHECK_DATE_" + (pColumn + 1),
//					"����ʱ��:");
//			printDataPQParm.addData("TITLE_DR_" + (pColumn + 1), "����ҩʦ:");
			printDataPQParm.addData("TITLE_DR_" + (pColumn + 1), "ҽ��:");
//			printDataPQParm.addData("TITLE_CHECK_" + (pColumn + 1), "�˶�ҩʦ:");
			printDataPQParm.addData("TITLE_CHECK_" + (pColumn + 1), "���:");
			// printDataPQParm.addData("TITLE_DR_" + (pColumn + 1), "ҽ��:");
			// printDataPQParm.addData("TITLE_CHECK_" + (pColumn + 1), "���:");
			printDataPQParm.addData("TITLE_EXE_" + (pColumn + 1), "ִ��:");
			// modify by lim 2012/04/29 end
			printDataPQParm.addData("TITLE_PAGEF_" + (pColumn + 1), "��");
			printDataPQParm.addData("TITLE_PAGEB_" + (pColumn + 1), "ҳ");
			int rowOrderCount = temp.getCount("ORDER_DESC");
			
			for (int j = 0; j < 5; j++) {
				if (j > rowOrderCount - 1) {
					printDataPQParm.addData("ORDER_" + (j + 1) + "_"
							+ (pColumn + 1), "");
					printDataPQParm.addData("QTY_" + (j + 1) + "_"
							+ (pColumn + 1), "");
					printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_"
							+ (pColumn + 1), "");
				//  ��������  machao
					printDataPQParm.addData("RUNTE_" + (j + 1) + "_"
							+ (pColumn + 1), "");
					continue;
				}
				printDataPQParm.addData("ORDER_" + (j + 1) + "_"
						+ (pColumn + 1), temp.getData("ORDER_DESC", j));
				printDataPQParm.addData("QTY_" + (j + 1) + "_" + (pColumn + 1),
						numDot(temp.getDouble("QTY", j)) + ""
								+ temp.getData("UNIT_CODE", j));
				printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_"
						+ (pColumn + 1),
						numDot(temp.getDouble("DOSAGE_QTY", j)) + ""
								+ temp.getData("DOSAGE_UNIT", j));
				//  ��������  machao
				if(numDot(temp.getDouble("RUNTE", j)).length()>0){
					printDataPQParm.addData("RUNTE_" + (j + 1) + "_" + (pColumn + 1),
							numDot(temp.getDouble("RUNTE", j))+"ml/h");
				}else{
					printDataPQParm.addData("RUNTE_" + (j + 1) + "_" + (pColumn + 1),
							numDot(temp.getDouble("RUNTE", j)));
				}
			}
			System.out.println("111111"+printDataPQParm);
//			for (int j = 0; j < 6; j++) {
//				if (j > rowOrderCount - 1) {
//					printDataPQParm.addData("ORDER_" + (j + 1) + "_"
//							+ (pColumn + 1), "");
//					printDataPQParm.addData("QTY_" + (j + 1) + "_"
//							+ (pColumn + 1), "");
//					printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_"
//							+ (pColumn + 1), "");
//					continue;
//				}
//				// modify by lim 2012/04/29 begin
//				String order = "";
//				if (null != temp.getValue("ORDER_DESC", j)
//						&& !"".equals(temp.getValue("ORDER_DESC", j))) {
//					String[] orderDescArray = temp.getValue("ORDER_DESC", j)
//							.trim().split(" ");
//
//					/* end update by guoyi 20120504 for ƿǩ���ֵ��� */
//					if (orderDescArray.length == 1) {
//						order = orderDescArray[0];
//					} else if (orderDescArray.length == 2) {
//						order = orderDescArray[0] + orderDescArray[1] + " "
//								+ numDot(temp.getDouble("QTY", j))
//								+ temp.getData("UNIT_CODE", j);
//					} else if (orderDescArray.length == 3) {
//						order = orderDescArray[0] + orderDescArray[2] + ""
//								+ numDot(temp.getDouble("QTY", j))
//								+ temp.getData("UNIT_CODE", j);
//					}
//					/* end update by guoyi 20120511 for ƿǩȥ�������������͹���������ո������ڽ�ȡ */
//				}
//
//				printDataPQParm.addData("ORDER_" + (j + 1) + "_"
//						+ (pColumn + 1), order);
//				// printDataPQParm.addData("QTY_" + (j + 1) + "_" + (pColumn +
//				// 1),numDot(temp.getDouble("QTY", j)) + ""+
//				// temp.getData("UNIT_CODE", j));
//				// printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_" + (pColumn
//				// + 1),numDot(temp.getDouble("DOSAGE_QTY", j)) + ""+
//				// temp.getData("DOSAGE_UNIT", j));
//				printDataPQParm.addData("QTY_" + (j + 1) + "_" + (pColumn + 1),
//						"");
//				printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_"
//						+ (pColumn + 1), "");
//				// modify by lim 2012/04/29 end
//
//			}
			// pColumn++;
			if (pColumn == 3) {
				// pColumn = 0;
				pRow++;
			}
		}
		// printDataPQParm.setCount(pRow+rowNull+1);
		printDataPQParm.setCount(rowCount);
		// --------modify
		printDataPQParm.addData("SYSTEM", "COLUMNS", "A1");
		printDataPQParm.addData("SYSTEM", "COLUMNS", "A2");
		printDataPQParm.addData("SYSTEM", "COLUMNS", "A3");
		TParm parmForPrint = new TParm();
		parmForPrint.setData("PRINT_PQ", printDataPQParm.getData());
		// System.out.println("======="+parmForPrint);
		// luhai 2012-2-13 modify ��ƿǩ���ݲ𿪣�ÿҳ�����д�ӡ
		// ����ͼ�㷽ʽ�������⣬��ȡ����ӡԤ�������÷�������ÿҳ���ݷ�ʽʵ�֣�
//		System.out.println("printDataPQParm ::: " + printDataPQParm);
		TParm pqParm = new TParm((Map) parmForPrint.getData("PRINT_PQ"));
		int abc=0;
		for (int i = 0; i < pqParm.getCount(); i++) {//����  ��Һǩ���������Һǩ machao
//			if((!StringUtil.isNullString(pqParm.getValue("LINK_NO_1",i))) && 
//					!(pqParm.getValue("LINK_NO_1",i).equals("[]"))){
//				//����
//				printBottleForEach2(pqParm, i,abc);
//			}else{
//				//������
//				printBottleForEach(pqParm, i,abc);
//			}
			printBottleForEach2(pqParm, i,abc);
		}
//		for (int i = 0; i < pqParm.getCount(); i++) {
//			printBottleForEach(pqParm, i,abc);
//		}
		// luhai 2012-2-13 modify modify ��ƿǩ���ݲ𿪣�ÿҳ�����д�ӡ
		// ***************************************************
		// ���´����ӡƿǩ���������������ϵ�һ���н��д�ӡ luhai 2012-2-29 end
		// ***************************************************
	}
	public String getLinkNo(String str){
		String temp = "";
		if(str.indexOf(",")>0){
//			str = str.replaceFirst("\\[", "");
//			str = str.substring(0, str.length()-1);
//			System.out.println(str);
//			
//			String[] s = str.split(",");
//			
//			
//			for(int i = 0 ;i<s.length;i++){
//				String ss = s[i].trim();
//				if(!(ss.equals(""))){
//					temp += ss.trim()+","; 
//				}
//				//System.out.println();
//			}
//			if(temp.length()>0){
//				temp = temp.substring(0, temp.length()-1);
//			}
			temp = "[]";
		}else {
			temp = str;
		}
		return temp;
		
	} 
	public String getFreqDesc(Object o){
		String sql = "SELECT FREQ_CHN_DESC "+
					 "FROM SYS_PHAFREQ "+
					 "WHERE FREQ_CODE = '"+o+"'";
		String desc = new TParm(TJDODBTool.getInstance().select(sql)).getValue("FREQ_CHN_DESC",0);
		return desc;
	}
	/**
	 * 
	 * ����ƿǩTparm ��ӡ����
	 * 
	 * @param pqTParm
	 *            ƿǩTParm
	 * @param index
	 *            ��ӡ���� luhai 2012-3-13
	 */
	private void printBottleForEach(TParm pqTParm, int index) {
		
		TParm printTParm = new TParm();
		TParm newPqTParm = new TParm();
		String[] names = pqTParm.getNames();
		for (String key : names) {
			newPqTParm.addData(key, pqTParm.getValue(key, index));
		}
		newPqTParm.setCount(1);
		printTParm.setData("PRINT_PQ", newPqTParm.getData());
		// openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",parmForPrint);
		openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",
				printTParm, false);
	}

	// ȫ������
	TParm parm = new TParm();
	// ȫ�ֵ�λ
	Map phaMap;

	/**
	 * �����ӡ����
	 * 
	 * @return TParm
	 */
	public TParm creatPrintData() {
		TParm result = new TParm();
		Set linkSet = new HashSet();
		Map linkMap = new HashMap();
		int rowCount = parm.getCount("PAT_NAME");
		// ��ӡ���ٸ�ƿǩ
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
			if (!"".equals(temp.getValue("LINK_NO"))) {
				String tempStr = temp.getValue("MR_NO")
						+ temp.getValue("LINK_NO")
						+ temp.getValue("START_DTTM") + temp.getValue("BAR_CODE")
						+ temp.getValue("ORDER_NO");
				linkSet.add(tempStr);
			}
		}
		Iterator linkIterator = linkSet.iterator();
		// ÿ��ƿǩ�Ļ�����Ϣ
		while (linkIterator.hasNext()) {
			String tempLinkStr = "" + linkIterator.next();
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				// if(tempLinkStr.equals(tempParm.getValue("MR_NO")+tempParm.getValue("LINK_NO")+tempParm.getValue("START_DTTM")+tempParm.getValue("SEQ_NO")+
				// tempParm.getValue("ORDER_NO"))){
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))
						&& !"".equals(tempParm.getValue("LINK_NO"))) {
					TParm temp = new TParm();
					// String dateTime = tempParm.getValue("START_DTTM")
					// .substring(4, 6)
					// + "/"
					// + tempParm.getValue("START_DTTM").substring(6, 8);
					String dateTime = "";
					try {
						dateTime = tempParm.getValue("ORDER_DATE").substring(4,
								6)
								+ "/"
								+ tempParm.getValue("ORDER_DATE").substring(6,
										8)
								+ " "
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(0, 2)
								+ ":"
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(2, 4);
					} catch (Exception e) {
						e.printStackTrace();
					}
					temp.setData("DATETIME", dateTime);
					temp.setData("BED_NO", tempParm.getValue("BED_NO"));
					temp.setData("PAT_NAME", tempParm.getValue("PAT_NAME"));
					temp.setData("MR_NO", tempParm.getValue("MR_NO"));
					temp.setData("FREQ", tempParm.getValue("FREQ"));
					temp.setData("STATION_DESC", tempParm
							.getValue("STATION_DESC"));
					temp.setData("SEX", tempParm.getValue("SEX"));
					temp.setData("AGE", tempParm.getValue("AGE"));
					temp.setData("RX_TYPE", tempParm.getValue("RX_TYPE"));
					temp.setData("ROUTE", tempParm.getValue("ROUTE"));
					temp.setData("DOCTOR", tempParm.getValue("DOCTOR"));
					temp.setData("BAR_CODE", tempParm.getValue("BAR_CODE"));
					temp.setData("LINK_NO", tempParm.getValue("LINK_NO")); //add by wukai 20170320
					temp.setData("BIRTHDATE", tempParm.getValue("BIRTHDATE"));
					// fux modify 20150804
					linkMap.put(tempLinkStr, temp);
					break;
				}
			}
			TParm temp = (TParm) linkMap.get(tempLinkStr);
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")  
						+ tempParm.getValue("ORDER_NO"))) {
					String orderDesc = tempParm.getValue("ORDER_DESC");
					/*
					 * begin update by guoyi 20120511 for
					 * ƿǩȥ�������������͹���������ո������ڽ�ȡ
					 */
					String desc[] = breakNFixRow(orderDesc, 30, 1);
					/* end update by guoyi 20120511 for ƿǩȥ�������������͹���������ո������ڽ�ȡ */
					for (int k = 0; k < desc.length; k++) {
						//System.out.println("tempParm:"+tempParm);
						if (k == 0) {
							temp.addData("ORDER_DESC", desc[k]);
							// ����
							temp.addData("QTY", tempParm.getValue("QTY"));
							// ��λ
							temp.addData("UNIT_CODE", phaMap.get(tempParm
									.getValue("UNIT_CODE")));
							// ����
							temp.addData("DOSAGE_QTY", tempParm
									.getValue("DOSAGE_QTY"));
							// ������λ
							temp.addData("DOSAGE_UNIT", phaMap.get(tempParm
									.getValue("DOSAGE_UNIT")));
							// ������
							temp.addData("LINK_MAIN_FLG", tempParm
									.getValue("LINK_MAIN_FLG"));
							// ����   machao
							temp.addData("RUNTE", tempParm
									.getValue("RUNTE"));
							continue;
						}
						temp.addData("ORDER_DESC", desc[k]);
						// ����
						temp.addData("QTY", "");
						// ��λ
						temp.addData("UNIT_CODE", "");
						// ����
						temp.addData("DOSAGE_QTY", "");
						// ������λ
						temp.addData("DOSAGE_UNIT", "");
						// ������
						temp.addData("LINK_MAIN_FLG", "");
						// ����   machao
						temp.addData("RUNTE", "");
					}
				}  
			}
			linkMap.put(tempLinkStr, temp);
			result.addData("PRINT_DATAPQ", linkMap.get(tempLinkStr));
		}
		Set onlySet = new HashSet();
		for (int i = 0; i < result.getCount("PRINT_DATAPQ"); i++) {
			onlySet.add(((TParm) result.getRow(i).getData("PRINT_DATAPQ"))
					.getValue("BED_NO"));
		}
		TParm resultTemp = new TParm();
		Iterator iter = onlySet.iterator();
		while (iter.hasNext()) {
			String temp = iter.next().toString();
			for (int j = 0; j < result.getCount("PRINT_DATAPQ"); j++) {
				if (temp.equals(((TParm) result.getRow(j).getData(
						"PRINT_DATAPQ")).getValue("BED_NO"))) {
					resultTemp.addData("PRINT_DATAPQ", result.getRow(j)
							.getData("PRINT_DATAPQ"));
				}
			}
		}
		// **************************************************************************************************
		// ���������ҽ�������Ҳ��ʾ����begin
		// **************************************************************************************************
		result = new TParm();
		linkSet = new HashSet();
		linkMap = new HashMap();
		rowCount = parm.getCount("PAT_NAME");
		// ��ӡ���ٸ�ƿǩ
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
			boolean classifyFlg = "F".equals(temp.getValue("CLASSIFY_TYPE"))
					|| "I".equals(temp.getValue("CLASSIFY_TYPE"));
			if ("".equals(temp.getValue("LINK_NO")) && (classifyFlg)) {
				String tempStr = temp.getValue("MR_NO")
						+ temp.getValue("LINK_NO")
						+ temp.getValue("START_DTTM")
						+ temp.getValue("BAR_CODE")
						+ temp.getValue("ORDER_NO");
				linkSet.add(tempStr);
			}
		}
		linkIterator = linkSet.iterator();
		// ÿ��ƿǩ�Ļ�����Ϣ
		while (linkIterator.hasNext()) {
			String tempLinkStr = "" + linkIterator.next();
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))
						&& "".equals(tempParm.getValue("LINK_NO"))) {
					TParm temp = new TParm();
					// String dateTime = tempParm.getValue("START_DTTM")
					// .substring(4, 6)
					// + "/"
					// + tempParm.getValue("START_DTTM").substring(6, 8);
					String dateTime = "";
					try {
						dateTime = tempParm.getValue("ORDER_DATE").substring(4,
								6)
								+ "/"
								+ tempParm.getValue("ORDER_DATE").substring(6,
										8)
								+ " "
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(0, 2)
								+ ":"
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(2, 4);
					} catch (Exception e) {
						e.printStackTrace();
					}
					temp.setData("DATETIME", dateTime);
					temp.setData("BED_NO", tempParm.getValue("BED_NO"));
					temp.setData("PAT_NAME", tempParm.getValue("PAT_NAME"));
					temp.setData("MR_NO", tempParm.getValue("MR_NO"));
					temp.setData("FREQ", tempParm.getValue("FREQ"));
					temp.setData("STATION_DESC", tempParm
							.getValue("STATION_DESC"));
					temp.setData("SEX", tempParm.getValue("SEX"));
					temp.setData("AGE", tempParm.getValue("AGE"));
					temp.setData("RX_TYPE", tempParm.getValue("RX_TYPE"));
					temp.setData("ROUTE", tempParm.getValue("ROUTE"));
					temp.setData("DOCTOR", tempParm.getValue("DOCTOR"));
					temp.setData("BAR_CODE", tempParm.getValue("BAR_CODE"));
					temp.setData("LINK_NO", tempParm.getValue("LINK_NO"));
					temp.setData("BIRTHDATE", tempParm.getValue("BIRTHDATE"));
					linkMap.put(tempLinkStr, temp);
					break;
				}
			}
			TParm temp = (TParm) linkMap.get(tempLinkStr);
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				//System.out.println("tempParm:"+tempParm);
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))) {
					String orderDesc = tempParm.getValue("ORDER_DESC");
					/*
					 * begin update by guoyi 20120511 for
					 * ƿǩȥ�������������͹���������ո������ڽ�ȡ
					 */
					String desc[] = breakNFixRow(orderDesc, 30, 1);
					/* end update by guoyi 20120511 for ƿǩȥ�������������͹���������ո������ڽ�ȡ */
					for (int k = 0; k < desc.length; k++) {
						if (k == 0) {
							temp.addData("ORDER_DESC", desc[k]);
							// ����
							temp.addData("QTY", tempParm.getValue("QTY"));
							// ��λ
							temp.addData("UNIT_CODE", phaMap.get(tempParm
									.getValue("UNIT_CODE")));
							// ����
							temp.addData("DOSAGE_QTY", tempParm
									.getValue("DOSAGE_QTY"));
							// ������λ
							temp.addData("DOSAGE_UNIT", phaMap.get(tempParm
									.getValue("DOSAGE_UNIT")));
							// ������
							temp.addData("LINK_MAIN_FLG", tempParm
									.getValue("LINK_MAIN_FLG"));
							// ������
							temp.addData("LINK_NO", tempParm
									.getValue("LINK_NO"));
							// ����   machao
							temp.addData("RUNTE", tempParm
									.getValue("RUNTE"));
							continue;
						}
						temp.addData("ORDER_DESC", desc[k]);
						// ����
						temp.addData("QTY", "");
						// ��λ
						temp.addData("UNIT_CODE", "");
						// ����
						temp.addData("DOSAGE_QTY", "");
						// ������λ
						temp.addData("DOSAGE_UNIT", "");
						// ������
						temp.addData("LINK_MAIN_FLG", "");
						// ������
						temp.addData("LINK_NO", "");
						// ����  machao
						temp.addData("RUNTE", "");
					}
				}
			}
			linkMap.put(tempLinkStr, temp);  
			result.addData("PRINT_DATAPQ", linkMap.get(tempLinkStr));
		}
		onlySet = new HashSet();
		for (int i = 0; i < result.getCount("PRINT_DATAPQ"); i++) {
			onlySet.add(((TParm) result.getRow(i).getData("PRINT_DATAPQ"))
					.getValue("BED_NO"));
		}
		// TParm resultTemp = new TParm();
		iter = onlySet.iterator();
		while (iter.hasNext()) {  
			String temp = iter.next().toString();
			for (int j = 0; j < result.getCount("PRINT_DATAPQ"); j++) {
				if (temp.equals(((TParm) result.getRow(j).getData(
						"PRINT_DATAPQ")).getValue("BED_NO"))) {
					resultTemp.addData("PRINT_DATAPQ", result.getRow(j)
							.getData("PRINT_DATAPQ"));
				}
			}
		}
		// **************************************************************************************************
		// ���������ҽ�������Ҳ��ʾ����end
		// **************************************************************************************************
		
		return configParm(resultTemp);
	}

	/**
	 * ��ʼ��ҳ���ӡ���� luhai 2012-2-28
	 * 
	 * @param parm
	 *            Vector
	 * @return TParm
	 */
	public TParm initPageData(Vector parm) {
		TParm result = new TParm();
		int rowCount = ((Vector) parm.get(0)).size();
		for (int i = 0; i < rowCount; i++) {
			// luhai 2012-2-29 modify ����Ƶ��Ϊ�յ���� begin
			String freqCode = "";
			if (((Vector) (parm.get(12))).get(i) == null) {
				freqCode = "STAT";
			} else {
				freqCode = ((Vector) parm.get(12)).get(i).toString();
			}
			// luhai 2012-2-29 modify ����Ƶ��Ϊ�յ���� end
			TParm freqParm = new TParm(this.getDBTool().select(
					"SELECT FREQ_TIMES FROM SYS_PHAFREQ WHERE FREQ_CODE='"
							+ freqCode + "'"));
			int countFreq = freqParm.getInt("FREQ_TIMES", 0);
			// luhai 2012-3-1 ����ִ�д����ļ��㣬֮ǰ�߼�����Ƶ�μ���������������ִ��������������Ҫÿ��ִ�ж���ӡƿǩ begin
			String caseNo = ((Vector) parm.get(22)).get(i) + "";
			String orderNo = ((Vector) parm.get(9)).get(i) + "";
			String orderSeq = ((Vector) parm.get(10)).get(i) + "";
			String startDttm = ((Vector) parm.get(11)).get(i) + "";
			String endDttm = ((Vector) parm.get(23)).get(i) + "";
			// ��ѯϸ���SQL
			String sql = " SELECT "
					+ " BAR_CODE,ORDER_DATE,ORDER_DATETIME "
					+ " FROM ODI_DSPND  WHERE CASE_NO='"
					+ caseNo
					+ "' AND ORDER_NO='"
					+ orderNo
					+ "' AND ORDER_SEQ='"
					+ orderSeq
					+ "' "
					+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
					+ startDttm
					+ "','YYYYMMDDHH24MISS') "
					+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
					+ endDttm + "','YYYYMMDDHH24MISS')";
			// + " GROUP BY BAR_CODE  ";
			// + " ORDER BY ORDER_DATE||ORDER_DATETIME ";
			// System.out.println("sql=========="+sql);
			// System.out.println(":::::::::::::::::::::::::" + sql);
			TParm resultDspnCnt = new TParm(TJDODBTool.getInstance()
					.select(sql));
			int totCount = resultDspnCnt.getCount();
			String barCode = "";
			// System.out.println("BAR_CODE-------"+barCode);
			// luhai 2012-3-1 ����ִ�д����ļ��㣬֮ǰ�߼�����Ƶ�μ���������������ִ��������������Ҫÿ��ִ�ж���ӡƿǩ end
			// ����1��
			int seqNo = 1;
			for (int j = 0; j < totCount; j++) {
				result.addData("BED_NO", getbedDesc((String) ((Vector) parm
						.get(0)).get(i)));
				result.addData("MR_NO", ((Vector) parm.get(1)).get(i));
				result.addData("PAT_NAME", ((Vector) parm.get(2)).get(i));
				result.addData("LINK_MAIN_FLG", ((Vector) parm.get(3)).get(i));
				result.addData("LINK_NO", ((Vector) parm.get(4)).get(i));
				result.addData("ORDER_DESC", ((Vector) parm.get(5)).get(i));
				result.addData("QTY", ((Vector) parm.get(6)).get(i));
				result.addData("UNIT_CODE", ((Vector) parm.get(7)).get(i));
				result.addData("ORDER_CODE", ((Vector) parm.get(8)).get(i));
				result.addData("ORDER_NO", ((Vector) parm.get(9)).get(i));
				result.addData("ORDER_SEQ", ((Vector) parm.get(10)).get(i));
				result.addData("START_DTTM", ((Vector) parm.get(11)).get(i));
				result.addData("SEQ_NO", seqNo);

				result.addData("FREQ", ((Vector) parm.get(12)).get(i));
				result.addData("STATION_DESC", ((Vector) parm.get(13)).get(i));
				result.addData("SEX", ((Vector) parm.get(14)).get(i));
				result.addData("AGE", ((Vector) parm.get(15)).get(i));
				result.addData("RX_TYPE", ((Vector) parm.get(16)).get(i));
				result.addData("ROUTE", ((Vector) parm.get(17)).get(i));
				result.addData("DOCTOR", ((Vector) parm.get(18)).get(i));
				result.addData("DOSAGE_QTY", ((Vector) parm.get(19)).get(i));
				result.addData("DOSAGE_UNIT", ((Vector) parm.get(20)).get(i));
				// �������
				result.addData("CLASSIFY_TYPE", ((Vector) parm.get(21)).get(i));
				// ����case_no
				result.addData("CASE_NO", ((Vector) parm.get(22)).get(i));
				// ����END_DTTM
				result.addData("END_DTTM", ((Vector) parm.get(23)).get(i));
				// ��������   machao
				result.addData("RUNTE", ((Vector) parm.get(24)).get(i));
				// ��������
				result.addData("BIRTHDATE", ((Vector) parm.get(25)).get(i));
				// shibl 20120415 add
				barCode = resultDspnCnt.getValue("BAR_CODE", j);
				// barCode
				result.addData("BAR_CODE", barCode);
				result.addData("ORDER_DATE", resultDspnCnt.getValue(
						"ORDER_DATE", j));
				result.addData("ORDER_DATETIME", resultDspnCnt.getValue(
						"ORDER_DATETIME", j));
				seqNo++;
			}
			// }
		}
		return result;
	}

	public String getStationDesc(String stationCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT STATION_DESC " + " FROM SYS_STATION "
						+ " WHERE STATION_CODE = '" + stationCode + "'"));
		return parm.getValue("STATION_DESC", 0);
	}

	/**
	 * ȡ�õ�λ�ֵ�
	 * 
	 * @return Map
	 */
	public Map getUnitMap() {
		Map map = new HashMap();
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT UNIT_CODE,UNIT_CHN_DESC FROM SYS_UNIT"));
		for (int i = 0; i < parm.getCount(); i++) {
			map.put(parm.getData("UNIT_CODE", i), parm.getData("UNIT_CHN_DESC",
					i));
		}
		return map;
	}

	/**
	 * 
	 * 
	 */
	public String getbedDesc(String bedNo) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT BED_NO,BED_NO_DESC FROM SYS_BED WHERE BED_NO = '"
						+ bedNo + "'"));
		return parm.getValue("BED_NO_DESC", 0);
	}

	public String getOperatorName(String userID) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT USER_NAME " + " FROM SYS_OPERATOR "
						+ " WHERE USER_ID = '" + userID + "'"));
		return parm.getValue("USER_NAME", 0);
	}

	private String numDot(double medQty) {
		if (medQty == 0)
			return "";
		if ((int) medQty == medQty)
			return "" + (int) medQty;
		else
			return "" + medQty;
	}

	public String[] breakNFixRow(String src, int bre, int fix) {
		return fixRow(breakRow(src, bre), fix);
	}

	public String[] fixRow(String string, int size) {
		Vector splitVector = new Vector();
		int index = 0;
		int separatorCount = 0;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if ("\n".equals(String.valueOf(c))) {
				if (++separatorCount >= size) {
					splitVector.add(string.substring(index, i));
					index = i + 1;
					separatorCount = 0;
				}
			}
		}

		splitVector.add(string.substring(index, string.length()));
		String splitArray[] = new String[splitVector.size()];
		for (int j = 0; j < splitVector.size(); j++)
			splitArray[j] = (String) splitVector.get(j);

		return splitArray;
	}

	public String breakRow(String src, int size) {
		return breakRow(src, size, 0);
	}

	public String breakRow(String src, int size, int shift) {
		StringBuffer tmp = new StringBuffer("");
		tmp.append(space(shift));
		int i = 0;
		int len = 0;
		for (; i < src.length(); i++) {
			char c = src.charAt(i);
			len += getCharSize(c);
			/* begin update by guoyi 20120504 for ƿǩ���ֵ��� */
			/*
			 * if ("\n".equals(String.valueOf(c))) { tmp.append(c);
			 * tmp.append(space(shift)); len = 0; } else if (size >= len) {
			 * tmp.append(c); } else { tmp.append("\n");
			 * tmp.append(space(shift)); tmp.append(c); len = getCharSize(c); }
			 */
			/* begin update by guoyi 20120511 for ƿǩȥ�������������͹���������ո������ڽ�ȡ */
			/*
			 * if ("\n".equals(String.valueOf(c))) { tmp.append(c);
			 * tmp.append(space(shift)); len = 0; } if (size >= len) {
			 * tmp.append(c); if(size == len){ break; } }else if
			 * ("\n".equals(String.valueOf(c))) { tmp.append(c);
			 * tmp.append(space(shift)); len = 0; }
			 */
			if ("\n".equals(String.valueOf(c))) {
				tmp.append(c);
				tmp.append(space(shift));
				len = 0;
			}
			if (size > len) {
				tmp.append(c);
			} else if (size == len) {
				
				tmp.append("\n");
				tmp.append(space(shift));
				tmp.append(c);
				len = getCharSize(c);
				
//				tmp.append(")");
//				break;
			}
			/* end update by guoyi 20120511 for ƿǩȥ�������������͹���������ո������ڽ�ȡ */
			/* begin update by guoyi 20120504 for ƿǩ���ֵ��� */
		}

		return tmp.toString();
	}

	public int getCharSize(char c) {
		return (new String(new char[] { c })).getBytes().length;
	}

	public String space(int n) {
		StringBuffer tmp = new StringBuffer("");
		for (int i = 0; i < n; i++)
			tmp.append(' ');

		return tmp.toString();
	}

	private TParm configParm(TParm parm) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("PRINT_DATAPQ"); i++) {
			TParm parmI = (TParm) parm.getData("PRINT_DATAPQ", i);
			;
			int rowCount = parmI.getCount("ORDER_DESC");
			int pageCount = 1;
			/* begin update by guoyi 20120504 for ƿǩ���ֵ�����ҳ */
			int pageSize = 6;
			if (rowCount % pageSize == 0)
				pageCount = rowCount / pageSize;
			else
				pageCount = rowCount / pageSize + 1;
			int page = 1;
			for (int j = 0; j < rowCount; j++) {
				if ((j + 1) % pageSize == 0) {
					result.addData("PRINT_DATAPQ", cloneParm(parmI, j
							- pageSize + 1, j));
					((TParm) result.getData("PRINT_DATAPQ", result
							.getCount("PRINT_DATAPQ") - 1)).setData("PAGE",
							page + "/" + pageCount);
					page++;
				} else if ((j + 1) == rowCount) {
					result.addData("PRINT_DATAPQ", cloneParm(parmI, rowCount
							- rowCount % pageSize, j));
					((TParm) result.getData("PRINT_DATAPQ", result
							.getCount("PRINT_DATAPQ") - 1)).setData("PAGE",
							page + "/" + pageCount);
					page++;
				}
			}
			/* end update by guoyi 20120504 for ƿǩ���ֵ�����ҳ */
		}
		return result;
	}

	private TParm cloneParm(TParm parm, int startIndex, int endIndex) {
		TParm result = new TParm();
		String[] names = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			if (parm.getData(names[i]) instanceof String)
				result.setData(names[i], parm.getData(names[i]));
			else if (parm.getData(names[i]) instanceof Vector) {
				for (int j = startIndex; j <= endIndex; j++)
					result.addData(names[i], parm.getData(names[i], j));
			}
		}
		return result;
	}

	// ***************************
	// luhai 2012-3-7 end ƿǩ��ӡ
	// ***************************
	/**
	 * ������Һ����
	 */
	public void GeneratPhaBarcode() {
		TParm dspndParm = new TParm();
		TParm tablValue = tblDtl.getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int rowCount = tblDtl.getRowCount();
		int count = 0;
		TParm linkparm = new TParm();
		Map mapBarCode = new HashMap();
		Map linkmap = new HashMap();
		String caseNo = "";
		String orderNo = "";
		String orderSeq = "";
		String cat1Type = "";
		String orderCode = "";
		String startDttm = "";
		String endDttm = "";
		String orderDesc = "";
		String Dosetype = "";
		String barCode = "";
		String linkNo = "";
		String dspnKind = "";
		String linkStr = "";
		String dcFlg = "";
		String routeCode = "";
		// ���������
		for (int i = 0; i < rowCount; i++) {
			caseNo = (String) tablValue.getData("CASE_NO", i);
			orderNo = (String) tablValue.getData("ORDER_NO", i);
			orderSeq = tablValue.getData("ORDER_SEQ", i) + "";
			cat1Type = tablValue.getData("CAT1_TYPE", i) + "";
			orderCode = (String) tablValue.getData("ORDER_CODE", i);
			startDttm = (String) tablValue.getData("START_DTTM", i);
			endDttm = (String) tablValue.getData("END_DTTM", i);
			orderDesc = (String) tablValue.getData("ORDER_DESC", i);
			Dosetype = "";
			linkNo = tablValue.getValue("LINK_NO", i);
			dspnKind = (String) tablValue.getData("DSPN_KIND", i);
			routeCode = (String) tablValue.getData("ROUTE_CODE", i);
			if (TypeTool.getBoolean(tblDtl.getValueAt(i, 0))) {
				if (cat1Type.equals("PHA")) {
					if (routeCode.equals("")) {
						this.messageBox(orderDesc + "�÷�Ϊ�գ������������룡");
						return;
					}
					Dosetype = SysPhaBarTool.getInstance().getClassifyType(
							routeCode);
					// ====zhangp 20121120 start
					TRadioButton dc = (TRadioButton) getComponent("DC");
					if ((!Dosetype.equals("I") && !Dosetype.equals("F"))
							|| dc.isSelected()) {
						this.messageBox(orderDesc + "����������Σ������������룡");
						return;
					}
					// ====zhangp 20121120 end
					// �ж�����ҽ����һ��һ�룩
					if (!linkNo.equals("")) {
						linkStr = caseNo + orderNo + dspnKind + startDttm
								+ linkNo;
						if (linkmap.get(linkStr) == null) {
							// ȡ��
							barCode = SysPhaBarTool.getInstance().getBarCode();
							mapBarCode.put(linkStr, barCode);
						}
						linkmap.put(linkStr, linkStr);
						// ��ѯϸ���SQL
						String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
								+ "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_CODE_REAL,BAR_CODE FROM ODI_DSPND "
								+ "WHERE CASE_NO='"
								+ caseNo
								+ "' AND ORDER_NO='"
								+ orderNo
								+ "' AND ORDER_SEQ='"
								+ orderSeq
								+ "' "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
								+ startDttm
								+ "','YYYYMMDDHH24MISS') "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
								+ endDttm
								+ "','YYYYMMDDHH24MISS')"
								+ " ORDER BY ORDER_DATE||ORDER_DATETIME";
						// ����ϸ���TDS,����������
						TParm result = new TParm(TJDODBTool.getInstance()
								.select(sql));
						if (result.getCount() <= 0)
							continue;
						for (int j = 0; j < result.getCount(); j++) {
							if (!result.getValue("BAR_CODE", j).equals(""))
								continue;
							dspndParm.addData("CASE_NO", result.getValue(
									"CASE_NO", j));
							dspndParm.addData("ORDER_NO", result.getValue(
									"ORDER_NO", j));
							dspndParm.addData("ORDER_SEQ", result.getValue(
									"ORDER_SEQ", j));
							dspndParm.addData("ORDER_DATE", result.getValue(
									"ORDER_DATE", j));
							dspndParm.addData("ORDER_DATETIME", result
									.getValue("ORDER_DATETIME", j));
							dspndParm.addData("BAR_CODE", (String) mapBarCode
									.get(linkStr)
									+ j);
							dspndParm.addData("OPT_USER", Operator.getID());
							dspndParm.addData("OPT_DATE", now);
							dspndParm.addData("OPT_TERM", Operator.getIP());
							count++;
						}
					} else {
						// ȡ��
						barCode = SysPhaBarTool.getInstance().getBarCode();
						// ��ѯϸ���SQL
						String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
								+ "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_CODE_REAL,BAR_CODE FROM ODI_DSPND "
								+ "WHERE CASE_NO='"
								+ caseNo
								+ "' AND ORDER_NO='"
								+ orderNo
								+ "' AND ORDER_SEQ='"
								+ orderSeq
								+ "' "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
								+ startDttm
								+ "','YYYYMMDDHH24MISS') "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
								+ endDttm
								+ "','YYYYMMDDHH24MISS')"
								+ " ORDER BY ORDER_DATE||ORDER_DATETIME";
						// ����ϸ���TDS,����������
						TParm result = new TParm(TJDODBTool.getInstance()
								.select(sql));
						if (result.getCount() <= 0)
							continue;
						for (int j = 0; j < result.getCount(); j++) {
							if (!result.getValue("BAR_CODE", j).equals(""))
								continue;
							dspndParm.addData("CASE_NO", result.getValue(
									"CASE_NO", j));
							dspndParm.addData("ORDER_NO", result.getValue(
									"ORDER_NO", j));
							dspndParm.addData("ORDER_SEQ", result.getValue(
									"ORDER_SEQ", j));
							dspndParm.addData("ORDER_DATE", result.getValue(
									"ORDER_DATE", j));
							dspndParm.addData("ORDER_DATETIME", result
									.getValue("ORDER_DATETIME", j));
							dspndParm.addData("BAR_CODE", barCode + j);
							dspndParm.addData("OPT_USER", Operator.getID());
							dspndParm.addData("OPT_DATE", now);
							dspndParm.addData("OPT_TERM", Operator.getIP());
							count++;
						}
					}
				}
			}
		}
		dspndParm.setCount(count);
		if (count > 0) {
			TParm result = InwOrderExecTool.getInstance().GeneratIFBarcode(
					dspndParm);
			if (result.getErrCode() < 0) {
				this.messageBox("��������ʧ�ܣ�");
				return;
			}

			this.messageBox("��������ɹ���");
		} else {
			this.messageBox("������������������������");
		}
	}

	/**
	 * �龫��ѯ ===zhangp 20120807
	 */
	public void onQueryTable() {
		int count = tblPat.getParmValue().getCount("EXEC");
		for (int i = 0; i < count; i++) {
			if (tblPat.getParmValue().getValue("EXEC", i).equals("Y")) {
				onQueryDtl();
				onQueryMed();
				// onLackStore();
				break;
			}
		}

	}

	/**
	 * ��ӡ�龫��ҩ�� ===zhangp 20121118
	 */
	public void CtrlDispenseSheet() {
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("") && this.getRadioButton("ST").isSelected()) {
			onDispenseSheet();
		}
	}
	
	/**
	 * ��ӡ��ҩȷ�ϵ�  
	 */
	public void onDispenseSheetRe(String no) {
		TParm inParm = new TParm();
		// luhai delete 2012-2-23 ɾ�� ���е���ҩȷ�ϵ���Ϊ��һ�δ�ӡ begin
		inParm.setData("FIRST_PRINT", Boolean.valueOf(true));
		// luhai delete 2012-2-23 ɾ�� ���е���ҩȷ�ϵ���Ϊ��һ�δ�ӡ end
		TTextFormat station = (TTextFormat) getComponent("COMBO");
		String stationName = station.getText();
		// String stationCode = getValueString("COMBO");
		if (StringUtil.isNullString(stationName))
			stationName = "ȫԺ";
		inParm.setData("STATION_NAME","TEXT", stationName);
		inParm.setData("START_DATE", TypeTool
				.getTimestamp(getValue("START_DATE")));          
		String flg = "";
		if (getRadioButton("IN_STATION_TWO").isSelected()) {
			inParm.setData("DISPENSE_ORG_TWO","TEXT","����");               
			inParm.setData("WHERE_5", "'1'");
			flg = "'1'";
		}else {
			inParm.setData("WHERE_5", "'2'");				
			inParm.setData("DISPENSE_ORG_TWO","TEXT","סԺҩ��");
			flg= "'2'";
		}
        inParm.setData("DISPENSE_ORG_TWO", "TEXT", this.getText("EXEC_DEPT_CODE"));//wanglong add 20140812
		inParm.setData("END_DATE", TypeTool.getTimestamp(getValue("END_DATE")));
		inParm.setData("DONE", Boolean.valueOf(TypeTool
				.getBoolean(getValue("UNCHECK"))));
		boolean isStation = TypeTool.getBoolean(getValue("STA"));
		/*if (!isStation) {
			messageBox_("������ҩƷ��Ϣ���ܰ�������ʾ");
			return;
		}*/
		inParm.setData("IS_STATION", Boolean.valueOf(isStation));
		String caseNos = getCaseNos();
		inParm.setData("WHERE_1", caseNos);
		String phaDispenseNo = getPhaDispenseNoWhere2();
		inParm.setData("WHERE_2", phaDispenseNo);
		// ====zhangp 20121118 start
		String ctrl = "";
		String bar_code = "";
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("") && this.getRadioButton("ST").isSelected()) {
			ctrl = "�龫";
			// ===zhangp 20130225 start
			tblDtl.acceptText();
			TParm parm = tblDtl.getParmValue();
			if (parm.getCount() > 0) {
				int count = parm.getCount();
				for (int i = 0; i < count; i++) {
					if (StringTool.getBoolean(parm.getValue("EXEC", i)) && parm.getValue("TAKEMED_ORG", i).equals("2"))
						if(phaDispenseNo.length()>0){
							bar_code = phaDispenseNo.replace("'", "");
						}
					break;
				}
			}
		}
		// ===zhangp 20130225 end
		String type = "";
		if (this.getRadioButton("ST").isSelected()) {
			inParm.setData("TYPE_T","TEXT", "��ʱҽ��" + ctrl + "��ҩȷ�ϵ�");//����ʱ����ҩȷ�ϵ���Ϊ��ҩȷ�ϵ�--xiongwg20150129
			inParm.setData("WHERE_3", " 'ST','F'");
			type=  " 'ST','F'";
		} else if (this.getRadioButton("UD").isSelected()) {
			inParm.setData("TYPE_T","TEXT", "����ҽ��" + ctrl + "��ҩȷ�ϵ�");//����ʱ����ҩȷ�ϵ���Ϊ��ҩȷ�ϵ�--xiongwg20150129
			inParm.setData("WHERE_3", " 'UD'");
			type =" 'UD'";
		} else {
			inParm.setData("TYPE_T","TEXT", "��Ժ��ҩ" + ctrl + "��ҩȷ�ϵ�");//����ʱ����ҩȷ�ϵ���Ϊ��ҩȷ�ϵ�--xiongwg20150129
			inParm.setData("WHERE_3", " 'DS'");
			type = " 'DS'";
		}
		// ====zhangp 20121118 end
		if ("''".equalsIgnoreCase(caseNos)) {
			messageBox_("û������");
			return;
		}
		// ���ͷ���
		inParm.setData("WHERE_4", getDoseTypeByWhere());
		// �÷�
		inParm.setData("DOSE_TYPE", getDoseTypeText());
		
		String datetime = TJDODBTool.getInstance().getDBTime().toString()
		.substring(0, 19).replace("-", "/");
		inParm.setData("CUR_DATE", "TEXT",datetime + "");			
		

		TParm parmData = tblPat.getParmValue();
		TParm parmRowData = new TParm();
		for (int i = 0; i < parmData.getCount("EXEC"); i++) {
			if (StringTool.getBoolean(parmData.getValue("EXEC", i))) {
				parmRowData = parmData.getRow(i);
				break;
			}
		}
		//fux modify 20150311
		String bsql = "SELECT  A.CASE_NO,A.ORDER_CODE,D.BED_NO_DESC,A.MR_NO,C.PAT_NAME,A.LINK_NO," +
		"B.ORDER_DESC||' '||B.SPECIFICATION ORDER_DESC, SUM(A.MEDI_QTY) AS MEDI_QTY," +
		"H.UNIT_CHN_DESC AS MEDI_UNIT,I.ROUTE_CHN_DESC,J.FREQ_CHN_DESC,A.TAKE_DAYS," +
		"SUM (A.DISPENSE_QTY) AS DISPENSE_QTY,G.UNIT_CHN_DESC," +
		"SUM (A.DOSAGE_QTY) *B.OWN_PRICE AS OWN_AMT,W.USER_NAME AS USER_NAME1 FROM ODI_DSPNM A,SYS_FEE B,SYS_PATINFO C, " +
		"SYS_BED D, SYS_PHAROUTE F,SYS_UNIT G,SYS_UNIT H,SYS_PHAROUTE I,SYS_PHAFREQ J,SYS_OPERATOR W " +
		"WHERE A.FREQ_CODE=J.FREQ_CODE AND A.ROUTE_CODE=I.ROUTE_CODE AND H.UNIT_CODE=A.MEDI_UNIT AND " +
		"A.DISPENSE_UNIT=G.UNIT_CODE AND    A.ORDER_CODE = B.ORDER_CODE AND A.BED_NO = D.BED_NO " +
		"AND A.MR_NO=C.MR_NO AND A.ROUTE_CODE = F.ROUTE_CODE AND A.ORDER_DR_CODE = W.USER_ID AND " +
		"(A.ORDER_CAT1_CODE = 'PHA_W'  OR A.ORDER_CAT1_CODE = 'PHA_C') " +
		"AND A.DISPENSE_FLG = 'N' AND A.CASE_NO IN ("+caseNos+") AND A.PHA_DISPENSE_NO IN" +
				" ("+phaDispenseNo+") AND A.DSPN_KIND IN ( "+type+") " +		  
						"AND F.CLASSIFY_TYPE IN ("+getDoseTypeByWhere()+" )  " +  
								" GROUP BY A.CASE_NO,A.ORDER_CODE,D.BED_NO_DESC,A.MR_NO,A.BED_NO," +
										"C.PAT_NAME,A.LINK_NO,B.ORDER_DESC, H.UNIT_CHN_DESC,I.ROUTE_CHN_DESC," +
										"J.FREQ_CHN_DESC,G.UNIT_CHN_DESC,B.OWN_PRICE,B.SPECIFICATION,A.LINKMAIN_FLG," +
										"A.DSPN_KIND,A.ORDER_NO,A.ORDER_SEQ,A.TAKE_DAYS,W.USER_NAME ORDER BY A.BED_NO,A.MR_NO," +   
										"A.DSPN_KIND DESC,A.ORDER_NO,A.ORDER_SEQ,A.LINK_NO,A.LINKMAIN_FLG,W.USER_NAME DESC";  
//		String bsql = "SELECT  A.CASE_NO,A.ORDER_CODE,D.BED_NO_DESC,A.MR_NO,C.PAT_NAME,A.LINK_NO," +
//				"B.ORDER_DESC||' '||B.SPECIFICATION ORDER_DESC, SUM(A.MEDI_QTY) AS MEDI_QTY," +
//				"H.UNIT_CHN_DESC AS MEDI_UNIT,I.ROUTE_CHN_DESC,J.FREQ_CHN_DESC,A.TAKE_DAYS," +
//				"SUM (A.DISPENSE_QTY) AS DISPENSE_QTY,G.UNIT_CHN_DESC," +
//				"SUM (A.DOSAGE_QTY) *B.OWN_PRICE AS OWN_AMT FROM ODI_DSPNM A,SYS_FEE B,SYS_PATINFO C, " +
//				"SYS_BED D, SYS_PHAROUTE F,SYS_UNIT G,SYS_UNIT H,SYS_PHAROUTE I,SYS_PHAFREQ J " +
//				"WHERE A.FREQ_CODE=J.FREQ_CODE AND A.ROUTE_CODE=I.ROUTE_CODE AND H.UNIT_CODE=A.MEDI_UNIT AND " +
//				"A.DISPENSE_UNIT=G.UNIT_CODE AND    A.ORDER_CODE = B.ORDER_CODE AND A.BED_NO = D.BED_NO " +
//				"AND A.MR_NO=C.MR_NO AND A.ROUTE_CODE = F.ROUTE_CODE AND " +
//				"(A.ORDER_CAT1_CODE = 'PHA_W'  OR A.ORDER_CAT1_CODE = 'PHA_C') " +
//				"AND A.DISPENSE_FLG = 'N' AND A.CASE_NO IN ("+caseNos+") AND A.PHA_DISPENSE_NO IN" +
//						" ("+phaDispenseNo+") AND A.DSPN_KIND IN ( "+type+") " +		
//								"AND F.CLASSIFY_TYPE IN ("+getDoseTypeByWhere()+" )  " +
//										" GROUP BY A.CASE_NO,A.ORDER_CODE,D.BED_NO_DESC,A.MR_NO,A.BED_NO," +
//												"C.PAT_NAME,A.LINK_NO,B.ORDER_DESC, H.UNIT_CHN_DESC,I.ROUTE_CHN_DESC," +
//												"J.FREQ_CHN_DESC,G.UNIT_CHN_DESC,B.OWN_PRICE,B.SPECIFICATION,A.LINKMAIN_FLG," +
//												"A.DSPN_KIND,A.ORDER_NO,A.ORDER_SEQ,A.TAKE_DAYS ORDER BY A.BED_NO,A.MR_NO," +
//												"A.DSPN_KIND DESC,A.ORDER_NO,A.ORDER_SEQ,A.LINK_NO,A.LINKMAIN_FLG DESC";
		System.out.println("bsql:"+bsql);
		TParm  searchprintParm = new TParm(TJDODBTool.getInstance()
				.select(bsql));												
		if (searchprintParm.getCount() <=0) {
			this.messageBox("�޴�ӡ����");  
			return;
		}
		TParm printParm = new TParm();
		for(int i=0;i<searchprintParm.getCount();i++) {
			String orderCode = searchprintParm.getValue("ORDER_CODE",i);
			String caseNo = searchprintParm.getValue("CASE_NO",i);
			printParm.addData("BED", searchprintParm.getValue("BED_NO_DESC",i));
			printParm.addData("MR_NO", searchprintParm.getValue("MR_NO",i));
			printParm.addData("NAME", searchprintParm.getValue("PAT_NAME",i));
			printParm.addData("LIAN", searchprintParm.getValue("LINK_NO",i));
			printParm.addData("ORDER_DESC", searchprintParm.getValue("ORDER_DESC",i));
			printParm.addData("YL1", searchprintParm.getValue("MEDI_QTY",i));
			printParm.addData("YL2", searchprintParm.getValue("MEDI_UNIT",i));
			printParm.addData("YF", searchprintParm.getValue("ROUTE_CHN_DESC",i));
			printParm.addData("PC", searchprintParm.getValue("FREQ_CHN_DESC",i));
			printParm.addData("DAYS", searchprintParm.getValue("TAKE_DAYS",i));
			printParm.addData("NUM1", searchprintParm.getValue("DISPENSE_QTY",i));
			printParm.addData("NUM2", searchprintParm.getValue("UNIT_CHN_DESC",i));
			String sql = "SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"' ORDER BY OPT_DATE DESC";
			TParm  batchParm = new TParm(TJDODBTool.getInstance()
					.select(sql));	 
			if (batchParm.getCount() <=0) {
				printParm.addData("BATCH_NO", "");
			}else {
				printParm.addData("BATCH_NO", batchParm.getValue("BATCH_NO",i));										
			}			
			printParm.addData("TOT_PRICE", searchprintParm.getValue("OWN_AMT",i));  
			//fux modify 20150320
			//messageBox(""+searchprintParm.getValue("USER_NAME1",i));  //zhaolingling  modify
			printParm.addData("DR_NAME", searchprintParm.getValue("USER_NAME1",i));
		}
		printParm.setCount(searchprintParm.getCount());
		printParm.addData("SYSTEM", "COLUMNS", "BED");
		printParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		printParm.addData("SYSTEM", "COLUMNS", "NAME");
		printParm.addData("SYSTEM", "COLUMNS", "LIAN");
		printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		printParm.addData("SYSTEM", "COLUMNS", "YL1");
		printParm.addData("SYSTEM", "COLUMNS", "YL2");
		printParm.addData("SYSTEM", "COLUMNS", "YF");
		printParm.addData("SYSTEM", "COLUMNS", "PC");
		printParm.addData("SYSTEM", "COLUMNS", "DAYS");
		printParm.addData("SYSTEM", "COLUMNS", "NUM1");
		printParm.addData("SYSTEM", "COLUMNS", "NUM2");
		printParm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
		printParm.addData("SYSTEM", "COLUMNS", "TOT_PRICE");
		printParm.addData("SYSTEM", "COLUMNS", "DR_NAME");
		inParm.setData("TABLE", printParm.getData());
		

		inParm.setData("USER_NAME","TEXT", Operator.getName());
		// ===zhangp 20121120 start
		inParm.setData("BAR_CODE", "TEXT",phaDispenseNo.replace("'", ""));
		// ===zhangp 20121120 end
		//fux modify 20150320 
		//inParm.setData("DR_NAME","TEXT" ,parmRowData.getData("USER_NAME1")); // lirui
		// 2012-6-8  
		// �ڱ�����չʾ����ҽʦ
		// luhai modify 2012-05-09 add begin ��ҩ��Ա����ҩʱ��ֿ����� end				
		if ("''".equalsIgnoreCase(phaDispenseNo)) {
			messageBox_("û������");
			return;
		} else {
			 if (!pha_ctrlcode.equals("") && this.getRadioButton("ST").isSelected()) {
					String barCodes = inParm.getValue("BAR_CODE");
					Set<String> set= distinctPhaDispenseNo(phaDispenseNo);
					if (null != set && set.size()>1) {
						this.messageBox("�龫��ҩ����ӡ��һ��ֻ�ܴ�ӡһ����ҩ��");
						return;
					}	
					if (null != barCodes && barCodes.length()>12) {  
						barCodes = barCodes.substring(0, 12);
					}
					inParm.setData("BAR_CODE",barCodes);				 
					openPrintWindow(
							"%ROOT%\\config\\prt\\UDD\\UddDispenseConfirmListOfDrug.jhw",
							inParm, false);
					return;   
			 }else {   
					openPrintWindow(
							"%ROOT%\\config\\prt\\UDD\\UddDispenseConfirmListOfPs.jhw",			
							inParm, false);
					return;
			}
	
		}

	}

	/**
	 * ��ӡ��ҩҩǩ-������
	 * 
	 */
	public void onPrintSwab() {
		
		String sqlbuilder = " SELECT A.ORDER_DESC ,A.DR_NOTE,A.SPECIFICATION  , DECODE(SUBSTR(A.MEDI_QTY,1,1),'.','0'||A.MEDI_QTY,A.MEDI_QTY) || " +
				" G.UNIT_CHN_DESC AS MEDI_QTY,B.MR_NO,G.UNIT_CHN_DESC,DECODE(SUBSTR(A.DISPENSE_QTY,1,1),'.','0'||A.DISPENSE_QTY,A.DISPENSE_QTY)|| " +
				" O.UNIT_CHN_DESC AS DISPENSE_QTY,"  
			+"	B.PAT_NAME,C.ROUTE_CHN_DESC AS ROUTE_CODE,D.FREQ_CHN_DESC AS FREQ_CODE,E.DEPT_CHN_DESC,J.USER_NAME AS DOSAGE_NAME, "
			+"	F.BED_NO_DESC,H.CHN_DESC,A.TAKE_DAYS,A.LINK_NO , "    
			//modify by kangy ȥ�������������㣬��ѯ��������ü������䷽��
			//+"     FLOOR(MONTHS_BETWEEN (SYSDATE, BIRTH_DATE)/12)||'��'|| "
	       // +"    ABS(TO_NUMBER(EXTRACT(MONTH FROM SYSDATE)-EXTRACT(MONTH FROM BIRTH_DATE)))||'��'|| "
	       // +"    ABS(TO_NUMBER(EXTRACT(DAY FROM SYSDATE)-EXTRACT(DAY FROM BIRTH_DATE)))||'��'
			 +"'' AS AGE,B.BIRTH_DATE,I.USER_NAME " +
	        		" ,A.DOSE_TYPE ,A.DISPENSE_QTY AS QTY ,A.CASE_NO ,A.ORDER_CODE,N.UNIT_CHN_DESC  AS DOSAGE_UNIT,A.DOSAGE_QTY " 
			+" ,K.MATERIAL_LOC_CODE,M.ORDER_DATE,O.UNIT_CHN_DESC AS DISPENSE_UNIT  , " +
					" A.DOSAGE_QTY/P.DOSAGE_QTY AS DISPENSE_QTYNEW " +
					" ,MOD(A.DOSAGE_QTY,P.DOSAGE_QTY) AS MOD_DOSAGE,MOD(A.MEDI_QTY,P.MEDI_QTY) AS MOD_MEDI" +
					" ,A.DOSAGE_QTY AS DOSAGE1,P.DOSAGE_QTY AS DOSAGE2, "+
					" A.MEDI_QTY AS MEDI1,P.MEDI_QTY AS MEDI2,O.UNIT_CHN_DESC AS UNIT_STOCK" +
					" ,REGEXP_REPLACE(A.MEDI_QTY,'^\\D','0.')/REGEXP_REPLACE(P.MEDI_QTY,'^\\D','0.') AS DOSAGE_QTYNEW "
			+" FROM ODI_DSPNM A,SYS_PATINFO B,SYS_PHAROUTE C,SYS_PHAFREQ  D,  SYS_DEPT E,SYS_BED F,SYS_UNIT G,SYS_UNIT N,SYS_UNIT O," +
					" SYS_DICTIONARY H,SYS_OPERATOR I,SYS_OPERATOR J,IND_STOCKM K ,ODI_ORDER M,PHA_TRANSUNIT P "
			+" WHERE    "     
			+"      A.MR_NO = B.MR_NO "  
			+"    AND A.ROUTE_CODE = C.ROUTE_CODE "   
			+"     AND A.FREQ_CODE=D.FREQ_CODE "  
			+"     AND A.DEPT_CODE=E.DEPT_CODE "     
			+"     AND A.BED_NO=F.BED_NO "
			+"     AND A.MEDI_UNIT = G.UNIT_CODE "  
			+"     AND A.DOSAGE_UNIT = N.UNIT_CODE "  
			+"     AND P.STOCK_UNIT = O.UNIT_CODE " +
			 "     AND A.ORDER_CODE = P.ORDER_CODE "
			+"    AND B.SEX_CODE = H.ID "
			+"    AND  H.GROUP_ID = 'SYS_SEX' "
			+"     AND A.ORDER_DR_CODE=I.USER_ID" +

			//fux modify 20151009
			 " AND A.ORDER_CODE = K.ORDER_CODE(+)" +
			 " AND A.EXEC_DEPT_CODE = K.ORG_CODE(+)" +
			 " AND A.CASE_NO = M.CASE_NO(+)" +
			 " AND A.ORDER_NO  = M.ORDER_NO(+)" +
			 " AND A.ORDER_SEQ = M.ORDER_SEQ(+) " 
			+"     AND A.PHA_DOSAGE_CODE=J.USER_ID(+) " //wanglong modify 20140812 
			+"    AND (A.ORDER_CAT1_CODE = 'PHA_W' OR A.ORDER_CAT1_CODE = 'PHA_C') "
			+"    AND A.DISPENSE_FLG = 'N' ";  
		
			boolean ds = false;
			if (TypeTool.getBoolean(getValue("ST"))){
				sqlbuilder +=" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F') ";
			}else if (TypeTool.getBoolean(getValue("UD"))){
				sqlbuilder += " AND A.DSPN_KIND='UD' ";
			}else{  
				sqlbuilder += " AND A.DSPN_KIND='DS'"  ;
				ds =  true;
			}
//			if ("Y".equals(this.getValueString("LINK_NO"))) {//wanglong delete 20140812 ֻ�пڷ��������ܴ�ӡҩǩ��������Һ������
//				sqlbuilder += " AND A.LINK_NO IS NOT NULL ";//������Һ
//			}
			if (TypeTool.getBoolean(getValue("UNCHECK"))) {
				if ("DOSAGE".equalsIgnoreCase(controlName)) {   
					sqlbuilder +=" AND A.PHA_DOSAGE_CODE IS NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL  ";
				} else {
					sqlbuilder += " AND A.PHA_DISPENSE_CODE IS  NULL AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL ";
					String dispense_no = getPhaDispenseNo();
					if (!"".equals(dispense_no) && !"''".equals(dispense_no))
						sqlbuilder +="  AND A.PHA_DISPENSE_NO IN ("+	dispense_no+ " )" ;
				}
			} else if ("DOSAGE".equalsIgnoreCase(controlName)) {  
				sqlbuilder += " AND  A.PHA_DOSAGE_CODE IS NOT NULL  AND (A.PHA_DOSAGE_DATE IS NULL OR (A.PHA_DOSAGE_DATE>=TO_DATE('" +
												StringTool.getString(TypeTool.getTimestamp(getValue("START_DATE")),"yyyyMMdd")+
												"','YYYYMMDD') AND A.PHA_DOSAGE_DATE<=TO_DATE('" +StringTool.getString(TypeTool.getTimestamp(getValue("END_DATE")),
																"yyyyMMdd")+"','YYYYMMDD')))" ;
				String dispense_no = getPhaDispenseNo();
				if (!"".equals(dispense_no) && !"''".equals(dispense_no))
					sqlbuilder +="  AND A.PHA_DISPENSE_NO IN ("+dispense_no+")" ;
			} else {
				sqlbuilder +=" AND A.PHA_DISPENSE_CODE IS NOT NULL AND (A.PHA_DISPENSE_DATE IS NULL OR (A.PHA_DISPENSE_DATE>=TO_DATE('"+
												StringTool.getString(TypeTool.getTimestamp(getValue("START_DATE")),"yyyyMMdd")+
												"','YYYYMMDD') AND A.PHA_DISPENSE_DATE<=TO_DATE('"+
												StringTool.getString(TypeTool.getTimestamp(getValue("END_DATE")),"yyyyMMdd")+"','YYYYMMDD')))";
				String dispense_no = getPhaDispenseNo();
				if (!"".equals(dispense_no) && !"''".equals(dispense_no))
					sqlbuilder +=
							"  AND A.PHA_DISPENSE_NO IN ("+dispense_no+")"  ;
			}
			 
			String getDoseType = "";
			List list = new ArrayList();
			if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
				list.add("O");
			}
			if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
				list.add("E");
			}
//			if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {//wanglong delete 20140812 ֻ�пڷ��������ܴ�ӡҩǩ
//				list.add("I");
//			}
//			if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
//				list.add("F");
//			}

			if (list == null || list.size() == 0) {
				 
			} else {
				getDoseType = " AND C.CLASSIFY_TYPE IN (";
				for (int i = 0; i < list.size(); i++) {
					getDoseType = getDoseType + "'" + list.get(i) + "' ,";
				}
				getDoseType = getDoseType.substring(0, getDoseType.length() - 1)
						+ ")";
			}                      
		sqlbuilder += getDoseType;  
		sqlbuilder +=  "     AND A.CASE_NO IN ("+getCaseNos()+") "
			+"		ORDER BY A.TAKEMED_ORG, A.ORDER_CODE";
 
		TParm tparmPHA = new TParm(TJDODBTool.getInstance().select(sqlbuilder));
		if(tparmPHA.getCount() > 0){
			//==start=add by kangy 20161111 ���¼�������
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			for(int i=0;i<tparmPHA.getCount();i++){
				ts=Timestamp.valueOf(tparmPHA.getValue("BIRTH_DATE",i).toString().substring(0, 19));
				tparmPHA.setData("AGE",i,setBirth(ts));
			}
			//===end=== add by kangy 20161111 ���¼�������
			String mrNo = "";  
			String dos_name = "";
			String drName = "";
			String sexCode = "";
			String patName =  "";		
			String linkNo = "";
			String deptName = "";
			String bedNoDesc = "";
			// add caoy  start
			Timestamp date1 = SystemTool.getInstance().getDate();

	        //add caoy end 

			String doseTypeChn = "";
			
			//���� machao start

			
			//���� machao end
			Set set = new HashSet();
			for(int i = 0 ; i < tparmPHA.getCount();i++){
				set.add(tparmPHA.getValue("CASE_NO",i));
			}

			
			if(ds){ 
				//��ʱ					
				for( int i = 0 ; i < tparmPHA.getCount() ; i++ ){  
					TParm date = new TParm();
					    
					TParm newTParm = new TParm();  
						String doseType = "";
						String orderCode=tparmPHA.getValue("ORDER_CODE",i);
						TParm phaData=getPHAOrderTransRate(orderCode);
						String line=phaData.getValue("MEDI_QTY",0)+tparmPHA.getValue("UNIT_CHN_DESC", i)+"/"+getUnitDesc(phaData.getValue("DOSAGE_UNIT", 0));
						doseType = tparmPHA.getValue("ROUTE_CODE",i);
						doseTypeChn= doseType;
						//fux modify 20171212 id:6038
						mrNo  = tparmPHA.getValue("MR_NO",i);
						Pat pat = Pat.onQueryByMrNo(mrNo);
				        String age= DateUtil.showAge(pat.getBirthday(), date1);
						String birthdate = pat.getBirthday().toString();
						if (null != birthdate && birthdate.length() >= 10) {
							birthdate = birthdate.substring(0, 4) + "��"
									+ birthdate.substring(5, 7) + "��"
									+ birthdate.substring(8, 10) + "��";
						}
						bedNoDesc = tparmPHA.getValue("BED_NO_DESC",i);
						drName = tparmPHA.getValue("USER_NAME",i);
						sexCode = tparmPHA.getValue("CHN_DESC",i);
						patName =  tparmPHA.getValue("PAT_NAME",i);
						age = tparmPHA.getValue("AGE",i);
						deptName = tparmPHA.getValue("DEPT_CHN_DESC",i);
					String doseChn = "��Ժ��ҩ";
					date.setData("TABLE", newTParm.getData());    
					date.setData("LINK_NO", "TEXT", linkNo);
					date.setData("DEPT_TYPE", "TEXT", "��"+doseTypeChn+"��");   
					date.setData("MR_NO", "TEXT", mrNo);
					date.setData("DEPT_CODE", "TEXT", deptName); 
					date.setData("BED_CODE", "TEXT", "(��)"+bedNoDesc);  
					date.setData("PAT_CODE", "TEXT", patName);    
					date.setData("SEX_CODE", "TEXT",sexCode );      
					date.setData("AGE", "TEXT",age);     
					date.setData("BIRTHDATE","TEXT",birthdate);
					//fux modify 20151009  
					date.setData("DOSECHN","TEXT",doseChn);    
					date.setData("REGION","TEXT",Operator.getHospitalCHNShortName());
					date.setData("REMARK","TEXT","��ҩǰ���Ķ�ҩƷ˵����");  
					date.setData("ADM_DATE","TEXT",tparmPHA.getValue("ORDER_DATE",i).substring(0, 10).replace('/', '-'));
					date.setData("SEND_CODE","TEXT",dos_name);      
					date.setData("DOCTOR_CODE","TEXT",drName);
					date.setData("MATERIAL_LOC_CODE","TEXT",tparmPHA.getValue("MATERIAL_LOC_CODE",i).toString());  
					date.setData("GOODS_DESC","TEXT",tparmPHA.getData("ORDER_DESC",i).toString());           
					date.setData("SPECIFICATION","TEXT","���: "+ tparmPHA.getData("SPECIFICATION",i).toString() );    
					  
					if( tparmPHA.getData("MOD_DOSAGE",i).toString().equals("0")){	
						date.setData("DISPENSE_QTY","TEXT","����: "+ tparmPHA.getData("DOSAGE_QTY",i).toString()+tparmPHA.getData("DOSAGE_UNIT",i).toString());
					}else{
						date.setData("DISPENSE_QTY","TEXT","����: "+ tparmPHA.getData("DOSAGE1",i).toString()+"/"+   
								tparmPHA.getData("DOSAGE2",i).toString()+tparmPHA.getData("DOSAGE_UNIT",i).toString()); 
					}  
					  
					
					if( tparmPHA.getData("MOD_MEDI",i).toString().equals("0")){ 
	            	    date.setData("FREQ_CODE","TEXT", "�÷�: "+tparmPHA.getData("FREQ_CODE", i).toString()+"  "+"" +
	              				"ÿ�� "+tparmPHA.getData("MEDI_QTY", i).toString()
	            				);
					}
	                  else{			    
	    					date.setData("FREQ_CODE","TEXT", "�÷�: "+tparmPHA.getData("FREQ_CODE", i).toString()+"  "+"" +
	    							"ÿ�� "+tparmPHA.getData("MEDI_QTY", i).toString()
	    							);
						  
					}      
					//modify by wangjc 20171226 סԺ&��Ժ��ҩҩǩ��עץȡҩƷ�ֵ䲡��ע������ start
					String drugNotesPatientSql = "SELECT DRUG_NOTES_PATIENT FROM PHA_BASE WHERE ORDER_CODE = '"+orderCode+"'";
					TParm drugNotesPatientParm = new TParm(TJDODBTool.getInstance().select(drugNotesPatientSql));
					date.setData("DRUG_NOTES_PATIENT","TEXT","��ע: "+ drugNotesPatientParm.getValue("DRUG_NOTES_PATIENT", 0)); 
//					date.setData("DRUG_NOTES_PATIENT","TEXT","��ע: "+ tparmPHA.getData("DR_NOTE",i)+"");
					//modify by wangjc 20171226 סԺ&��Ժ��ҩҩǩ��עץȡҩƷ�ֵ䲡��ע������ end
					this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\PHAPersralnew.jhw", date,
							true);	      
				}        					  
			}else{ 
				//��ʱ					
					for( int i = 0 ; i < tparmPHA.getCount() ; i++ ){  
						TParm date = new TParm();
						    
						TParm newTParm = new TParm();  
							String doseType = "";
							String orderCode=tparmPHA.getValue("ORDER_CODE",i);
							TParm phaData=getPHAOrderTransRate(orderCode);
							String line=phaData.getValue("MEDI_QTY",0)+tparmPHA.getValue("UNIT_CHN_DESC", i)+"/"+getUnitDesc(phaData.getValue("DOSAGE_UNIT", 0));
							doseType = tparmPHA.getValue("ROUTE_CODE",i);
							doseTypeChn= doseType;
							//fux modify 20171212 id:6038
							mrNo  = tparmPHA.getValue("MR_NO",i);
							Pat pat = Pat.onQueryByMrNo(mrNo);
					        String age= DateUtil.showAge(pat.getBirthday(), date1);
							String birthdate = pat.getBirthday().toString();
							if (null != birthdate && birthdate.length() >= 10) {
								birthdate = birthdate.substring(0, 4) + "��"
										+ birthdate.substring(5, 7) + "��"
										+ birthdate.substring(8, 10) + "��";
							}
							bedNoDesc = tparmPHA.getValue("BED_NO_DESC",i);
							drName = tparmPHA.getValue("USER_NAME",i);
							sexCode = tparmPHA.getValue("CHN_DESC",i);
							patName =  tparmPHA.getValue("PAT_NAME",i);
							age = tparmPHA.getValue("AGE",i);
							deptName = tparmPHA.getValue("DEPT_CHN_DESC",i);
						String doseChn = "סԺ";
						date.setData("TABLE", newTParm.getData());    
						date.setData("LINK_NO", "TEXT", linkNo);
						date.setData("DEPT_TYPE", "TEXT", "��"+doseTypeChn+"��");   
						date.setData("MR_NO", "TEXT", mrNo);
						date.setData("DEPT_CODE", "TEXT", deptName); 
						date.setData("BED_CODE", "TEXT", "(��)"+bedNoDesc);  
						date.setData("PAT_CODE", "TEXT", patName);    
						date.setData("SEX_CODE", "TEXT",sexCode );      
						date.setData("AGE", "TEXT",age);     
						date.setData("BIRTHDATE","TEXT",birthdate);
						//fux modify 20151009  
						date.setData("DOSECHN","TEXT",doseChn);    
						date.setData("REGION","TEXT",Operator.getHospitalCHNShortName());
						date.setData("REMARK","TEXT","��ҩǰ���Ķ�ҩƷ˵����");  
						date.setData("ADM_DATE","TEXT",tparmPHA.getValue("ORDER_DATE",i).substring(0, 10).replace('/', '-'));
						date.setData("SEND_CODE","TEXT",dos_name);      
						date.setData("DOCTOR_CODE","TEXT",drName);
						date.setData("MATERIAL_LOC_CODE","TEXT",tparmPHA.getValue("MATERIAL_LOC_CODE",i).toString());  
						date.setData("GOODS_DESC","TEXT",tparmPHA.getData("ORDER_DESC",i).toString());           
						date.setData("SPECIFICATION","TEXT","���: "+ tparmPHA.getData("SPECIFICATION",i).toString() );    
						  
						if( tparmPHA.getData("MOD_DOSAGE",i).toString().equals("0")){	
							date.setData("DISPENSE_QTY","TEXT","����: "+ tparmPHA.getData("DOSAGE_QTY",i).toString()+tparmPHA.getData("DOSAGE_UNIT",i).toString());
						}else{
							date.setData("DISPENSE_QTY","TEXT","����: "+ tparmPHA.getData("DOSAGE1",i).toString()+"/"+   
									tparmPHA.getData("DOSAGE2",i).toString()+tparmPHA.getData("DOSAGE_UNIT",i).toString()); 
						}  
						  
						
						if( tparmPHA.getData("MOD_MEDI",i).toString().equals("0")){ 
		            	    date.setData("FREQ_CODE","TEXT", "�÷�: "+tparmPHA.getData("FREQ_CODE", i).toString()+"  "+"" +
		              				"ÿ�� "+tparmPHA.getData("MEDI_QTY", i).toString()
		            				);
						}
		                  else{			    
		    					date.setData("FREQ_CODE","TEXT", "�÷�: "+tparmPHA.getData("FREQ_CODE", i).toString()+"  "+"" +
		    							"ÿ�� "+tparmPHA.getData("MEDI_QTY", i).toString()
		    							);
							  
						}
						//modify by wangjc 20171226 סԺ&��Ժ��ҩҩǩ��עץȡҩƷ�ֵ䲡��ע������ start
						String drugNotesPatientSql = "SELECT DRUG_NOTES_PATIENT FROM PHA_BASE WHERE ORDER_CODE = '"+orderCode+"'";
						TParm drugNotesPatientParm = new TParm(TJDODBTool.getInstance().select(drugNotesPatientSql));
						date.setData("DRUG_NOTES_PATIENT","TEXT","��ע: "+ drugNotesPatientParm.getValue("DRUG_NOTES_PATIENT", 0)); 
//						date.setData("DRUG_NOTES_PATIENT","TEXT","��ע: "+ tparmPHA.getData("DR_NOTE",i)+"");
						//modify by wangjc 20171226 סԺ&��Ժ��ҩҩǩ��עץȡҩƷ�ֵ䲡��ע������ end
						this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\PHAPersralnew.jhw", date,
								true);	      
					}        					  
				}   
		}else{                            
			this.messageBox("�޴�ӡ����");
		}
		
		
	}
	  
	/**
	 * ��Һǩ
	 */    
	public void  onPrintOld(){

		String sqlbuilder = getSqlBuilder();  
		   
		//System.out.println("sqlbuilder---------------:"+sqlbuilder);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sqlbuilder));
		String linkNo = parm.getValue("LINK_NO",0);
		String link = parm.getValue("LINK_NO",0);
		String mrNo = parm.getValue("MR_NO",0); 
		String rxNo = parm.getValue("RX_NO",0);
		String dosageName = parm.getValue("DISPENSE_NAME",0);
		String drName = parm.getValue("USER_NAME",0);
		String sexCode = parm.getValue("CHN_DESC",0); 
		String patName =  parm.getValue("PAT_NAME",0);
		//String age = parm.getValue("AGE",0);
		String deptName = parm.getValue("DEPT_CHN_DESC",0);
		String bedNoDesc = parm.getValue("BED_NO_DESC",0);
		String barCode = SysPhaBarTool.getInstance().getBarCode();
		// add caoy  start
		 Timestamp date1 = SystemTool.getInstance().getDate();
		 Pat pat = Pat.onQueryByMrNo(mrNo);
        String age= StringUtil.getInstance().showAge(pat.getBirthday(), date1);
		


		boolean isInfusion = true;
		Set<String>  set=new HashSet<String>();  
		//fux modify 20150804
		for(int i = 0 ; i < parm.getCount() ; i++){
			linkNo = parm.getValue("LINK_NO",i);
			//String caseNo = parm.getValue("CASE_NO",i);
			//set.add(linkNo+caseNo);
			set.add(linkNo);
		}
		 
		for(int i = 0 ; i < parm.getCount() ; i++){
			String doseType = parm.getValue("DOSE_TYPE",0);
			if("O".equals(doseType) || "E".equals(doseType)){
				isInfusion = false;
				break;
			}
		}
		if(isInfusion){
			for( Iterator <String>  it = set.iterator();  it.hasNext(); )  {
				linkNo = (String)it.next();
				TParm tparmPHA  = new TParm();
				TParm date = new TParm();
				int count = 0;
				for(int i = 0 ; i < parm.getCount() ; i++){
					String linkNoNew = parm.getValue("LINK_NO",i);   
					if(linkNo.equals(linkNoNew)){  
						tparmPHA.addData("GOODS_DESC", parm.getData("ORDER_DESC", i)+" "+parm.getData("SPECIFICATION",i));
//						tparmPHA.addData("SPECIFICATION", parm.getData("SPECIFICATION",i));
						tparmPHA.addData("QTY", parm.getData("MEDI_QTY", i));
//						tparmPHA.addData("ROUTE_CODE", parm.getData("ROUTE_CODE", i));
//						tparmPHA.addData("FREQ_CODE", parm.getData("FREQ_CODE", i));
						count++ ;  
					}   
				}        
				
				tparmPHA.setCount(count);
				tparmPHA.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
//				tparmPHA.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
				tparmPHA.addData("SYSTEM", "COLUMNS", "QTY");
//				tparmPHA.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
//				tparmPHA.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
				 
				date.setData("TABLE", tparmPHA.getData());
				// ��ͷ���� MR_NO  
				date.setData("LINK_NO", "TEXT", linkNo);
				date.setData("DEPT_TYPE", "TEXT", "סԺ��ע�䡿ҩ");
				date.setData("MR_NO", "TEXT", mrNo);
				date.setData("DEPT_CODE", "TEXT", deptName);  
				date.setData("BED_CODE", "TEXT", "");  
				date.setData("PAT_CODE", "TEXT", patName);  
				date.setData("SEX_CODE", "TEXT",sexCode );  
				date.setData("AGE", "TEXT",age);
				date.setData("ROUTE_CODE", "TEXT",parm.getData("ROUTE_CODE", 0));
				date.setData("FREQ_CODE", "TEXT",parm.getData("FREQ_CODE", 0));
//				String sql = " SELECT "
//					+ " BAR_CODE,ORDER_DATE,ORDER_DATETIME "
//					+ " FROM ODI_DSPND  WHERE CASE_NO='"
//					+ caseNo
//					+ "' AND ORDER_NO='"
//					+ orderNo
//					+ "' AND ORDER_SEQ='"
//					+ orderSeq
//					+ "' "
//					+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
//					+ startDttm
//					+ "','YYYYMMDDHH24MISS') "
//					+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
//					+ endDttm + "','YYYYMMDDHH24MISS')";
//			// + " GROUP BY BAR_CODE  ";
//			// + " ORDER BY ORDER_DATE||ORDER_DATETIME ";
//			// System.out.println("sql=========="+sql);
//			// System.out.println(":::::::::::::::::::::::::" + sql);
//			TParm resultDspnCnt = new TParm(TJDODBTool.getInstance()
//					.select(sql));
				date.setData("BAR_CODE", "TEXT",getBarCode(rxNo,linkNo));
				date.setData("SEND_CODE","TEXT",dosageName);
				date.setData("DOCTOR_CODE","TEXT",drName);  
				
				this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\phaInfusion.jhw", date,
						false);	
			}
		
			
			
//			int count = 0;
//			String linkNoStr = "";
//			TParm tparmPHA  = new TParm();
//			for( Iterator <String>  it = set.iterator();  it.hasNext(); )  {
//				linkNo = (String)it.next();
////				TParm tparmPHA  = new TParm();
//				TParm date = new TParm();
//				count = 0;
//				linkNoStr = "";
//				for(int i = 0 ; i < parm.getCount() ; i++){
//					String linkNoNew = parm.getValue("LINK_NO",i);
//					
//					String caseNoNew = parm.getValue("CASE_NO",i);
//					if(linkNo.equals(linkNoNew+caseNoNew)){
//						tparmPHA.addData("GOODS_DESC", parm.getValue("ORDER_DESC", i)+" "+parm.getValue("SPECIFICATION",i));
////						tparmPHA.addData("SPECIFICATION", parm.getData("SPECIFICATION",i));
//						tparmPHA.addData("QTY", parm.getData("MEDI_QTY", i));
////						tparmPHA.addData("ROUTE_CODE", parm.getData("ROUTE_CODE", i));
////						tparmPHA.addData("FREQ_CODE", parm.getData("FREQ_CODE", i));
//						linkNoStr = linkNoNew;
//						
//						mrNo = parm.getValue("MR_NO",i);
//					    dosageName = parm.getValue("DISPENSE_NAME",i);
//						drName = parm.getValue("USER_NAME",i);
//						sexCode = parm.getValue("CHN_DESC",i);
//						patName =  parm.getValue("PAT_NAME",i);
////						age = parm.getValue("AGE",i);
//						deptName = parm.getValue("DEPT_CHN_DESC",i);
//						bedNoDesc = parm.getValue("BED_NO_DESC",i);
//						
//						count++ ;
//					}
//				}
//				tparmPHA.setCount(count);
//				tparmPHA.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
////				tparmPHA.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//				tparmPHA.addData("SYSTEM", "COLUMNS", "QTY");
////				tparmPHA.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
////				tparmPHA.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
//				 
//				date.setData("TABLE", tparmPHA.getData());
//				// ��ͷ���� MR_NO
//				date.setData("LINK_NO", "TEXT", linkNoStr);
//				date.setData("DEPT_TYPE", "TEXT", "סԺ��ע�䡿ҩ");
//				date.setData("MR_NO", "TEXT", mrNo);
//				date.setData("DEPT_CODE", "TEXT", deptName);
//				date.setData("BED_CODE", "TEXT", bedNoDesc);
//				date.setData("PAT_CODE", "TEXT", patName);
//				date.setData("SEX_CODE", "TEXT",sexCode );
//				date.setData("AGE", "TEXT",age);
//				date.setData("ROUTE_CODE", "TEXT",parm.getData("ROUTE_CODE", 0));
//				date.setData("FREQ_CODE", "TEXT",parm.getData("FREQ_CODE", 0));
//				date.setData("BAR_CODE", "TEXT",barCode+count);
//				date.setData("SEND_CODE","TEXT",dosageName);
//				date.setData("DOCTOR_CODE","TEXT",drName);
//				
////				this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\phaInfusion.jhw", date,
////						true);	
//			}	
				
		}
		else{
			this.messageBox("����ҺҩƷ���ܴ�ӡ��Һ��ǩ!");
		}
		 
	}
	
	
	
	
	/**
	 * ��Һǩ
	 */  
	public void  onPrint(){

		String sqlbuilder = getSqlBuilder();  
		   
		//System.out.println("sqlbuilder---------------:"+sqlbuilder);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sqlbuilder));
		String linkNo = "";
		String link = "";
		String mrNo = ""; 
		String rxNo = "";
		String dosageName = "";
		String drName = "";
		String sexCode =""; 
		String patName =  "";
		//String age = parm.getValue("AGE",0);
		String deptName = "";
		String bedNoDesc = "";
		String caseNo = "";
		String age = "";
		String routeCode ="";
		String freqCode = "";
		String barCode = SysPhaBarTool.getInstance().getBarCode();
		// add caoy  start
		Timestamp date1 = SystemTool.getInstance().getDate();

		


		boolean isInfusion = true;
		Set<String>  set=new HashSet<String>();  
		//fux modify 20150804
		for(int i = 0 ; i < parm.getCount() ; i++){
			linkNo = parm.getValue("LINK_NO",i);
			caseNo = parm.getValue("CASE_NO",i);
			set.add(linkNo+caseNo);
			//set.add(linkNo);
		}
		 
		for(int i = 0 ; i < parm.getCount() ; i++){
			String doseType = parm.getValue("DOSE_TYPE",0);
			if("O".equals(doseType) || "E".equals(doseType)){
				isInfusion = false;
				break;
			}
		}
		if(isInfusion){			
			int count = 0;
			String linkNoStr = "";
			TParm tparmPHA  = new TParm();
			for( Iterator <String>  it = set.iterator();  it.hasNext(); )  {
				linkNo = (String)it.next();
//				TParm tparmPHA  = new TParm();
				TParm date = new TParm();
				count = 0;
				linkNoStr = "";
				for(int i = 0 ; i < parm.getCount() ; i++){

					String linkNoNew = parm.getValue("LINK_NO",i);
					String caseNoNew = parm.getValue("CASE_NO",i);
					if(linkNo.equals(linkNoNew+caseNoNew)){
						tparmPHA.addData("GOODS_DESC", parm.getValue("ORDER_DESC", i)+" "+parm.getValue("SPECIFICATION",i));
//						tparmPHA.addData("SPECIFICATION", parm.getData("SPECIFICATION",i));
						tparmPHA.addData("QTY", parm.getData("MEDI_QTY", i));
//						tparmPHA.addData("ROUTE_CODE", parm.getData("ROUTE_CODE", i));
//						tparmPHA.addData("FREQ_CODE", parm.getData("FREQ_CODE", i));
						linkNoStr = linkNoNew;
						
						mrNo = parm.getValue("MR_NO",i);
						Pat pat = Pat.onQueryByMrNo(mrNo);
				        age= StringUtil.getInstance().showAge(pat.getBirthday(), date1);
					    dosageName = parm.getValue("DISPENSE_NAME",i);
						drName = parm.getValue("USER_NAME",i);
						sexCode = parm.getValue("CHN_DESC",i);
						patName =  parm.getValue("PAT_NAME",i);
//						age = parm.getValue("AGE",i);
						deptName = parm.getValue("DEPT_CHN_DESC",i);
						bedNoDesc = parm.getValue("BED_NO_DESC",i);
						routeCode = parm.getValue("ROUTE_CODE",i);
						freqCode = parm.getValue("FREQ_CODE",i);
						count++ ;
					}
				}
				tparmPHA.setCount(count);
				tparmPHA.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
//				tparmPHA.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
				tparmPHA.addData("SYSTEM", "COLUMNS", "QTY");
//				tparmPHA.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
//				tparmPHA.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
				date.setData("TABLE", tparmPHA.getData());
				// ��ͷ���� MR_NO
				date.setData("LINK_NO", "TEXT", linkNoStr);
				date.setData("DEPT_TYPE", "TEXT", "סԺ��ע�䡿ҩ");
				date.setData("MR_NO", "TEXT", mrNo);
				date.setData("DEPT_CODE", "TEXT", deptName);
				date.setData("BED_CODE", "TEXT", bedNoDesc);
				date.setData("PAT_CODE", "TEXT", patName);
				date.setData("SEX_CODE", "TEXT",sexCode );  
				date.setData("AGE", "TEXT",age);
				date.setData("ROUTE_CODE", "TEXT",routeCode);
				date.setData("FREQ_CODE", "TEXT",freqCode);
				date.setData("BAR_CODE", "TEXT",getBarCode(rxNo,linkNo));
				date.setData("SEND_CODE","TEXT",dosageName);
				date.setData("DOCTOR_CODE","TEXT",drName);  
				this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\phaInfusion.jhw", date,
						false);	
			}	  
		}
		else{
			this.messageBox("����ҺҩƷ���ܴ�ӡ��Һ��ǩ!");  
		}
		 
	}
	
	
	/**
	 * ��ȡ����
	 * @param orderNo
	 * @param linkNo
	 * @return
	 */
	private String getBarCode(String orderNo,String linkNo){
		if ("".equals(linkNo)) {
			return "";
		}
		linkNo = "00".substring(0,2-linkNo.length())+linkNo.trim();
		return orderNo+linkNo;
	}

	private String getSqlBuilder() {
		String sqlbuilder = " SELECT A.ORDER_DESC ,A.SPECIFICATION  , A.MEDI_QTY ||  G.UNIT_CHN_DESC AS MEDI_QTY,B.MR_NO,A.RX_NO, "
					+"	B.PAT_NAME,C.ROUTE_CHN_DESC AS ROUTE_CODE,D.FREQ_CHN_DESC AS FREQ_CODE,E.DEPT_CHN_DESC,"
					+"	F.BED_NO_DESC,H.CHN_DESC,A.TAKE_DAYS,A.LINK_NO , "
					+"     FLOOR(MONTHS_BETWEEN (SYSDATE, BIRTH_DATE)/12)||'��'|| "
			        +"    ABS(TO_NUMBER(EXTRACT(MONTH FROM SYSDATE)-EXTRACT(MONTH FROM BIRTH_DATE)))||'��'|| "
			        +"    ABS(TO_NUMBER(EXTRACT(DAY FROM SYSDATE)-EXTRACT(DAY FROM BIRTH_DATE)))||'��' AS AGE,I.USER_NAME  ,A.DOSE_TYPE ,A.DISPENSE_QTY AS QTY ,A.CASE_NO ,A.DOSE_TYPE,J.USER_NAME AS DISPENSE_NAME  " 
					+"FROM ODI_DSPNM A,SYS_PATINFO B,SYS_PHAROUTE C,SYS_PHAFREQ  D, "
					+"      SYS_DEPT E,SYS_BED F,SYS_UNIT G,SYS_DICTIONARY H,SYS_OPERATOR I ,SYS_OPERATOR J "
					+" WHERE    "
					+"      A.MR_NO = B.MR_NO "
					+"    AND A.ROUTE_CODE = C.ROUTE_CODE " 
					+"     AND A.FREQ_CODE=D.FREQ_CODE "
					+"     AND A.DEPT_CODE=E.DEPT_CODE "
					+"     AND A.BED_NO=F.BED_NO "
					+"     AND A.MEDI_UNIT = G.UNIT_CODE "
					+"    AND B.SEX_CODE = H.ID "
					+"    AND  H.GROUP_ID = 'SYS_SEX' "
					+"     AND A.ORDER_DR_CODE=I.USER_ID "
					+"     AND A.PHA_DISPENSE_CODE=J.USER_ID(+) "
					+"    AND (A.ORDER_CAT1_CODE = 'PHA_W' OR A.ORDER_CAT1_CODE = 'PHA_C') "
					+"    AND A.DISPENSE_FLG = 'N' ";
					if (TypeTool.getBoolean(getValue("ST"))){
						sqlbuilder +=" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F') ";
					}else if (TypeTool.getBoolean(getValue("UD"))){
						sqlbuilder += " AND A.DSPN_KIND='UD' ";
					}else{
						sqlbuilder += " AND A.DSPN_KIND='DS'"  ;
					}
					if ("Y".equals(this.getValueString("LINK_NO"))) {
						sqlbuilder += " AND A.LINK_NO IS NOT NULL ";
					}
					if (TypeTool.getBoolean(getValue("UNCHECK"))) {
						if ("DOSAGE".equalsIgnoreCase(controlName)) {
							sqlbuilder +=" AND A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL  ";
						} else {
							sqlbuilder += " AND A.PHA_DISPENSE_CODE IS  NULL AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.DC_DATE IS NULL AND A.DC_NS_CHECK_DATE IS NULL ";
							String dispense_no = getPhaDispenseNo();
							if (!"".equals(dispense_no) && !"''".equals(dispense_no))
								sqlbuilder +="  AND A.PHA_DISPENSE_NO IN ("+	dispense_no+ " )" ;
						}
					} else if ("DOSAGE".equalsIgnoreCase(controlName)) {
						sqlbuilder += " AND  A.PHA_DOSAGE_CODE IS NOT NULL  AND (A.PHA_DOSAGE_DATE IS NULL OR (A.PHA_DOSAGE_DATE>=TO_DATE('" +
														StringTool.getString(TypeTool.getTimestamp(getValue("START_DATE")),"yyyyMMdd")+
														"','YYYYMMDD') AND A.PHA_DOSAGE_DATE<=TO_DATE('" +StringTool.getString(TypeTool.getTimestamp(getValue("END_DATE")),
																		"yyyyMMdd")+"','YYYYMMDD')))" ;
						String dispense_no = getPhaDispenseNo();
						if (!"".equals(dispense_no) && !"''".equals(dispense_no))
							sqlbuilder +="  AND A.PHA_DISPENSE_NO IN ("+dispense_no+")" ;
					} else {
						sqlbuilder +=" AND A.PHA_DISPENSE_CODE IS NOT NULL AND (A.PHA_DISPENSE_DATE IS NULL OR (A.PHA_DISPENSE_DATE>=TO_DATE('"+
														StringTool.getString(TypeTool.getTimestamp(getValue("START_DATE")),"yyyyMMdd")+
														"','YYYYMMDD') AND A.PHA_DISPENSE_DATE<=TO_DATE('"+
														StringTool.getString(TypeTool.getTimestamp(getValue("END_DATE")),"yyyyMMdd")+"','YYYYMMDD')))";
						String dispense_no = getPhaDispenseNo();
						if (!"".equals(dispense_no) && !"''".equals(dispense_no))
							sqlbuilder +=
									"  AND A.PHA_DISPENSE_NO IN ("+dispense_no+")"  ;
					}
					 
					String getDoseType = "";
					List list = new ArrayList();
//					if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {//wanglong delete 20140812 O�ڷ���E���ò�������Һ
//						list.add("O");
//					}
//					if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
//						list.add("E");
//					}
//					if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
						list.add("I");
//					}
//					if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
						list.add("F");
//					}

					if (list == null || list.size() == 0) {
						 
					} else {
						getDoseType = " AND C.CLASSIFY_TYPE IN (";
						for (int i = 0; i < list.size(); i++) {
							getDoseType = getDoseType + "'" + list.get(i) + "' ,";
						}
						getDoseType = getDoseType.substring(0, getDoseType.length() - 1)
								+ ")";
					}
				sqlbuilder += getDoseType;
				sqlbuilder +=  "     AND A.CASE_NO IN ("+getCaseNos()+") "
					+"		ORDER BY A.TAKEMED_ORG, A.ORDER_CODE";
		//System.out.println("SQLBUILDER-------------:"+sqlbuilder);
		return sqlbuilder;
	}
	private void printBottleForEach(TParm pqTParm, int index,int abc) {
		//System.out.println("llll"+pqTParm);
		TParm printTParm = new TParm();
		TParm newPqTParm = new TParm();
		String[] names = pqTParm.getNames();
		for (String key : names) {
			newPqTParm.addData(key, pqTParm.getValue(key, index));
		}
		newPqTParm.setCount(1);
		printTParm.setData("PRINT_PQ", newPqTParm.getData());
		
		// openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",parmForPrint);
		//fux modify 20150803
//		openPrintWindow("%ROOT%\\config\\prt\\PHA\\phaInfusion.jhw",
//				printTParm, false);    
		//д���ӡ������Ա��ʱ�� wangjingchun 20150319 add
		TParm resultD = UddDispatchTool.getInstance().updatePrintBottleUser(
				new TParm((Map) printTParm.getData("PRINT_PQ")).getValue("BAR_CODE_1",0));
    	if (resultD.getErrCode()<0) {
			return;
		}   
		openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",  
				printTParm, false);
	}
	private void printBottleForEach2(TParm pqTParm, int index,int abc) {
		//this.messageBox("3");
		TParm printTParm = new TParm();
		TParm newPqTParm = new TParm();
		String[] names = pqTParm.getNames();
		for (String key : names) {
			if(key.startsWith("RUNTE_")){
				if(!StringUtil.isNullString(pqTParm.getValue(key, index))){
					newPqTParm.addData("RUNTE", pqTParm.getValue(key, index));
				}
			}else{
				newPqTParm.addData(key, pqTParm.getValue(key, index));
			}
		}
		System.out.println("55555555"+newPqTParm);
		newPqTParm.setCount(1);
		printTParm.setData("PRINT_PQ", newPqTParm.getData());
		TParm resultD = UddDispatchTool.getInstance().updatePrintBottleUser(
				new TParm((Map) printTParm.getData("PRINT_PQ")).getValue("BAR_CODE_1",0));
    	if (resultD.getErrCode()<0) {
			return;
		} 
		// openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",parmForPrint);
		openPrintWindow("%ROOT%\\config\\prt\\INW\\INWPrintBottle.jhw",
				printTParm, false);
	}
	 /**
     * ��������
     */
    public String setBirth(Timestamp birthDay) {
        Timestamp sysDate = SystemTool.getInstance().getDate();
        Timestamp temp = birthDay == null ? sysDate : birthDay;
        String age = "0";
        age = DateUtil.showAge(temp, sysDate);
       return age;
    }
    
    /**
	 * 
	* @Title: onUpdateBatchNo
	* @Description: TODO(˫������ҩƷ��ϸ�������Ƥ��ҩƷ�����޸�����)
	* @author pangben
	* @throws
	 */
	public void onDoubleClickUpdateBatchNo(int row){
		TParm parm = this.tblDtl.getParmValue();
		if (this.tblDtl.getSelectedRow()<0) {
			this.messageBox("��ѡ����Ҫ�޸ĵ�����");
			return;
		}
		//����ҽ��δ��ɿ����޸�Ƥ������
		if(TypeTool.getBoolean(getValue("ST"))&&TypeTool.getBoolean(getValue("UNCHECK"))){   
			TParm result = new TParm();
			// ��ѯѡ��ҩ���Ƿ�ΪƤ��ҩƷ
			String sql = "SELECT A.SKINTEST_FLG, A.ANTIBIOTIC_CODE,MAX(B.OPT_DATE),"
					+ "B.BATCH_NO,B.SKINTEST_NOTE"
					+ " FROM PHA_BASE A,PHA_ANTI B  WHERE A.ORDER_CODE = B.ORDER_CODE "
					+ "AND A.ORDER_CODE = '"
					+ parm.getValue("ORDER_CODE",this.tblDtl.getSelectedRow())
					+ "' AND B.CASE_NO = '"
					+ parm.getValue("CASE_NO",this.tblDtl.getSelectedRow())
					+ "' "
					+ "GROUP BY B.BATCH_NO ,B.SKINTEST_NOTE,B.OPT_DATE,A.SKINTEST_FLG, A.ANTIBIOTIC_CODE "
					+ "ORDER BY B.OPT_DATE DESC";
			TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
			TParm parmValue=new TParm();
			if (result1.getCount() <= 0) {
				this.messageBox("��ҩƷ���ǿ���ҩƷ��");
				return;
			} else if (result1.getValue("SKINTEST_FLG", 0).equals("N")) {
				this.messageBox("��Ƥ��ҩƷ��");
				return;
			} else if (result1.getValue("BATCH_NO", 0).equals(null)  
					|| "".equals(result1.getValue("BATCH_NO", 0))) {
				parmValue.setData("BATCH_NO", "");// ����
				//parmValue.setData("SKINTEST_NOTE", "");// Ƥ�Խ��
			} else {
				parmValue.setData("BATCH_NO", result1.getValue("BATCH_NO", 0));// ����
				//parmValue.setData("SKINTEST_FLG", result1.getValue("SKINTEST_NOTE", 0));// Ƥ�Խ��
			}
			parmValue.setData("CASE_NO", parm.getValue("CASE_NO",this.tblDtl.getSelectedRow()));// �����
			parmValue.setData("ORDER_CODE", parm.getValue("ORDER_CODE",this.tblDtl.getSelectedRow()));// ҽ������
			parmValue.setData("ORDER_NO", parm.getValue("ORDER_NO",this.tblDtl.getSelectedRow()));
			parmValue.setData("ORDER_SEQ", parm.getValue("ORDER_SEQ",this.tblDtl.getSelectedRow()));
			parmValue.setData("OPT_USER", Operator.getID());//
			parmValue.setData("OPT_TERM", Operator.getIP());//
			parmValue.setData("ORG_CODE", parm.getValue("EXEC_DEPT_CODE", this.tblDtl.getSelectedRow()));//add by wukai on 20170412 ��ӿ���
			result = (TParm) this.openDialog("%ROOT%\\config\\UDD\\UDDSkiResult.x",
					parmValue, true);
			if (result!=null) {
				parm.setData("BATCH_NO",this.tblDtl.getSelectedRow(),result.getData("BATCH_NO",0));
				this.tblDtl.setParmValue(parm);
			}
			
		}else{
			this.messageBox("״̬������,�����Բ���");
		}
	}
}