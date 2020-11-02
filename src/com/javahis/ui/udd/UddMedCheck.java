package com.javahis.ui.udd;

import java.awt.Color;
import java.io.IOException;
import java.sql.Timestamp;

import javax.swing.JOptionPane;











import jdo.ekt.EKTIO;
import jdo.inw.InwUtil;
import jdo.pha.PassTool;
import jdo.sys.Operator; 
import jdo.sys.PatTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UddChnCheckTool;
  









import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.pha.PhaMainControl;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;
 
/**
 * <p>
 * Title: 住院药房西药审核
 * </p>
 * 
 * <p>
 * Description: 住院药房西药审核
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
public class UddMedCheck extends TControl {

	public static final String Y = "Y"; 
	public static final String N = "N";
	public static final String NULL = "";
	private String odiOrdercat = "";
	private TTextFormat queryBed;
	
	/**
	 * 初始化病患列表的SQL
	 */
	private static final String PAT_SQL = "SELECT 'N' AS EXEC,A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,"
			+ "       A.MR_NO,A.IPD_NO, '' AS AGE, B.BIRTH_DATE, "
			+ "       D.USER_NAME AS PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.ORDER_NO, " 
//			+ "		  H.WEIGHT,"// modify by wangjc 20180810 若体温单中获取不到体重，则从新生儿登记信息中抓取出生体重(6046)
			+ "		  CASE "
			+ "        WHEN H.WEIGHT IS NULL OR H.WEIGHT = '' OR H.WEIGHT = 0 "
	        + "        THEN "
	        + "         B.NEW_BODY_WEIGHT/1000 "
	        + "        ELSE "
	        + "         H.WEIGHT "
	        + "       END AS WEIGHT, "
			+ "G.ICD_CHN_DESC "//add 增加体重与入院住诊断  machao
			+ "	FROM ODI_ORDER A , SYS_PATINFO B ,SYS_BED C, SYS_OPERATOR D "
			+ " ,(SELECT * FROM ADM_INPDIAG WHERE IO_TYPE = 'M' AND MAINDIAG_FLG = 'Y') F,SYS_DIAGNOSIS G,ADM_INP H " //add 增加体重与入院住诊断  machao
			+ "	WHERE  B.MR_NO=A.MR_NO "
			+ "		  AND C.BED_NO=A.BED_NO "
			+
			// "		  AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') " +
			// "		  AND (C.BED_OCCU_FLG IS NULL OR C.BED_OCCU_FLG='N')  " +
			"		  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') "
		  + "       AND A.PHA_CHECK_CODE = D.USER_ID(+)   "
		  + "       AND A.OPBOOK_SEQ IS NULL "//wanglong add 20141114 过滤掉术中医嘱
	      + " 		AND A.CASE_NO = F.CASE_NO(+) "//add 增加体重与入院住诊断  machao
	      + "       AND F.ICD_CODE = G.ICD_CODE(+) "//add 增加体重与入院住诊断  machao
	      + "       AND H.MR_NO = A.MR_NO"//add 增加体重与入院住诊断  machao
	      + "       AND A.CASE_NO = H.CASE_NO(+)";//add 增加体重与入院住诊断  machao
	TTable tblPat, tblDtl;
	/**
	 * 保存需要的集合
	 */
	TParm saveParm = new TParm();
	/**
	 * 是否需要护士审核
	 */
	boolean isNsCheck = false;
	/**
	 * 合理用药
	 */
	boolean passIsReady = false;
	private boolean enforcementFlg = false;
	private int warnFlg;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		TParm stationParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_STATION WHERE ORG_CODE='"
						+ Operator.getDept() + "' "));
		// System.out.println("SELECT * FROM SYS_STATION WHERE ORG_CODE='"+Operator.getDept()+"' ");
		// System.out.println(stationParm);
		tblPat = (TTable) this.getComponent("TBL_PAT");
		tblDtl = (TTable) this.getComponent("TBL_MED");
		passIsReady = SYSNewRegionTool.getInstance().isIREASONABLEMED(
				Operator.getRegion());
		enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		isNsCheck = InwUtil.getInstance().getNsCheckEXEFlg();
		odiOrdercat = "(A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')";
		queryBed = (TTextFormat) getComponent("QUERY_BED");
		
		onClear();
		// 合理用药
		if (passIsReady) {
			if (!PassTool.getInstance().init()) {
				this.messageBox("合理用药初始化失败！");
			}
		}
	}

	/**
	 * 清空
	 */
	public void onClear() {
		Timestamp t = TJDODBTool.getInstance().getDBTime();
		this.setValue("START_DATE", StringTool.rollDate(t, -7));
		this.setValue("END_DATE", t);
		//this.setValue("UDST", Y);
		this.setValue("ST", Y);
		this.setValue("UD", Y);
		this.setValue("EXEC_DEPT_CODE", Operator.getDept());
		// this.setValue("EXEC_DEPT_CODE", "308003");
		this.setValue("AGENCY_ORG_CODE", NULL);
		this.setValue("STA", Y);
		this.setValue("UNCHECK", Y);		
		this.setValue("NO", NULL);
		this.setValue("NAME", NULL);
		this.setValue("PHA_CTRLCODE", NULL);
		this.setValue("STATIONCOMBOL", NULL);	
		queryBed.setVisible(false);
		callFunction("UI|NO|setVisible", new Object[] { Boolean.valueOf(true) });
		tblPat.removeRowAll();
		tblDtl.removeRowAll();
		setEnableMenu();
	}

	/**
	 * 查询
	 */
	public void onQuery() { 
		StringBuffer sql = new StringBuffer();
		sql.append(PAT_SQL).append(getWhere());
		/*
		 * 
		 * 执,40,boolean;床号,80;姓名,80;处方号,100;护士站,120;病案号,120;住院号,120
		 * EXEC;BED_NO;PAT_NAME;RX_NO;STATION_CODE;MR_NO;IPD_NO
		 */
		tblPat.removeRowAll();
		String sqlStr = sql
				.append(" GROUP BY A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO, B.BIRTH_DATE, D.USER_NAME, A.PHA_CHECK_DATE, A.ORDER_NO ")
				.append(" ,B.WEIGHT,G.ICD_CHN_DESC,H.WEIGHT ")//add 增加体重与入院住诊断  machao
				.append(" ,B.NEW_BODY_WEIGHT ")//add by wangjc 20180813
				.append(" ORDER BY A.CASE_NO").toString();	
		System.out.println("sqlStr--------------:"+sqlStr);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sqlStr));
		Timestamp date = SystemTool.getInstance().getDate();
		for (int i = 0; i < parm.getCount("AGE"); i++) {
			parm.setData(
					"AGE",
					i,
					StringUtil.getInstance().showAge(
							parm.getTimestamp("BIRTH_DATE", i), date));
		}
		sql = null;
		tblPat.setParmValue(parm);
		if (StringTool.getBoolean(this.getValueString("MR"))
				|| StringTool.getBoolean(this.getValueString("BED"))) {
			this.setValue("NAME", parm.getValue("PAT_NAME", 0));
		}
		if (tblDtl != null) {
			tblDtl.removeRowAll();
		}
		// onQueryDtl();
		setEnableMenu();
	}

	/**
	 * 查询药品详细
	 */
	public void onQueryDtl() {
		TParm parm = tblPat.getParmValue();
		StringBuffer caseNos = new StringBuffer();
		// System.out.println("tableParm=========="+parm);
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			// String exec=parm.getValue("EXEC",i);
			if (parm.getBoolean("EXEC", i))
				caseNos.append("'").append(parm.getValue("CASE_NO", i))
						.append("'");
		}
		// System.out.println("sql========="+caseNos);
		if (StringUtil.isNullString(caseNos.toString())) {
			this.messageBox_("查无数据");
			tblDtl.removeRowAll();
			return;
		}
		String order_no = " AND A.ORDER_NO = '"
				+ parm.getValue("ORDER_NO", tblPat.getSelectedRow()) + "'";
		// String bed_no = " AND A.BED_NO = '" +
		// parm.getValue("BED_NO", tblPat.getSelectedRow()) + "'";
		// ===========pangben modify 20110511 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110511 stop
		String pnFlgSql = "(SELECT PN_FLG FROM ODI_DSPNM B WHERE B.CASE_NO=A.CASE_NO AND B.ORDER_NO=A.ORDER_NO AND B.ORDER_SEQ=A.ORDER_SEQ AND B.DSPN_KIND <> 'UD')";
		String sql = " SELECT CASE_NO , ORDER_NO,ORDER_SEQ,ORDER_CODE,ORDER_DESC||' '||GOODS_DESC|| '('|| SPECIFICATION ||')' ORDER_DESC, "  //lirui  2012-6-6   药嘱加规格
				+ "	 LINKMAIN_FLG,LINK_NO,MEDI_QTY,MEDI_UNIT,FREQ_CODE, "
				+ "	 ROUTE_CODE,TAKE_DAYS,DR_NOTE,TO_CHAR(EFF_DATE,'YYYY/MM/DD HH24:MI') AS EFF_DATE, "  //lirui  2012-6-6    明细加天数
				+ "	 (CASE WHEN TO_CHAR(sysdate,'YYYY/MM/DD') >= TO_CHAR(DC_DATE,'YYYY/MM/DD') THEN TO_CHAR(DC_DATE,'YYYY/MM/DD HH24:MI') ELSE '' END) AS DC_DATE ,GOODS_DESC,ORDER_DR_CODE "
				//add by wukai on 20170323 PN字段
				+ "  , A.RX_KIND, " + pnFlgSql + " AS PN_FLG " 
				+ "  , DISPENSE_FLG "//20150803 wangjc add 自备药
				+ " FROM ODI_ORDER A"
				+ " WHERE CASE_NO ="
				+ caseNos.toString()
				+ "	AND (ORDER_CAT1_CODE='PHA_W' OR ORDER_CAT1_CODE='PHA_C') "// luhai 2012-3-6 add 备药不用走审配发流程  AND A.DISPENSE_FLG='N'
				+ getWhere() + order_no + region + // bed_no +
//			     " AND  A.DC_DATE IS NULL " +//by liyh 20120905 已经停用的医嘱不用审核       因线上出现问题应急注释   shibl 20121017 modidfy
				" ORDER BY ORDER_NO,LINK_NO,LINKMAIN_FLG DESC,EFF_DATE";
				
		//System.out.println("审核详细查询----------------------"+sql);	
		saveParm = new TParm(TJDODBTool.getInstance().select(sql));
		tblDtl.setParmValue(saveParm);
		//===============  modify  by  chenxi  20120703  特殊药品颜色提示
		TParm tableParm = tblDtl.getParmValue() ;
		Color normalColor = new Color(0, 0, 0);
		Color blueColor  =  new Color(0, 0, 255);
		for (int i = 0; i < tableParm.getCount(); i++) {
			//=========药品提示信息      modify  by  chenxi 
			String orderCode = tableParm.getValue("ORDER_CODE",i) ;
			String  sql1 = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '" +orderCode+ "'" ;
			TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql1)) ;
			sqlparm = sqlparm.getRow(0) ;
			//先判断高危药品 然后是普通药品 最后是普通颜色  
			//fux modify 20150820
			 if (this.getColorByHighRiskOrderCode(orderCode)) {
				 tblDtl.setRowTextColor(i, Color.RED);//高危药品，显示为红色
			 }
			 else if(sqlparm.getValue("DRUG_NOTES_DR").length()==0){
				 tblDtl.setRowTextColor(i, normalColor);
				 //continue ;
			}
			 else{  
			     tblDtl.setRowTextColor(i, blueColor) ;
			 }
			 
	
			
		}
	}
	
	/**
	 * 判断是否为高危药品
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

	/**
	 * 按照界面的查询条件是否输入拼装WHERE条件
	 * 
	 * @return where String
	 */
	public String getWhere() {
		StringBuffer result = new StringBuffer();
		String startDate = StringTool.getString(
				TCM_Transform.getTimestamp(this.getValue("START_DATE")),
				"yyyyMMddHHmmss");
		String endDate = StringTool.getString(
				TCM_Transform.getTimestamp(this.getValue("END_DATE")),
				"yyyyMMddHHmmss").substring(0, 8)
				+ "235959";
		// ===========pangben modify 20110511 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			result.append(" AND A.REGION_CODE='" + Operator.getRegion() + "'");
		}
		// ===========pangben modify 20110511 stop
		if (isNsCheck) {
			result.append(" AND A.NS_CHECK_CODE IS NOT NULL ");
		} else {
			result.append(" AND (A.DC_DATE >= A.START_DTTM OR A.DC_DATE IS NULL) ");
		}
		if (StringTool.getBoolean(this.getValueString("UNCHECK"))) {
			result.append(" AND A.EFF_DATE >=TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') AND A.EFF_DATE<=TO_DATE('"
					+ endDate
					+ "','YYYYMMDDHH24MISS') AND A.PHA_CHECK_DATE IS NULL "
			
					);
		} else {
			result.append(" AND A.PHA_CHECK_DATE >=TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') AND A.PHA_CHECK_DATE<=TO_DATE('"
					+ endDate + "','YYYYMMDDHH24MISS')");
		}
		result.append(" AND A.EXEC_DEPT_CODE='"
				+ this.getValueString("EXEC_DEPT_CODE") + "'");
	/*	if (TypeTool.getBoolean(this.getValue("UDST"))) {
			result.append(" AND RX_KIND IN ('UD','ST') ");
		} else {
			result.append(" AND RX_KIND='DS'");
		}*/
		if(TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("UD"))&&!TypeTool.getBoolean(this.getValue("DS"))) {
			result.append(" AND RX_KIND='ST'");
		}
		else if(TypeTool.getBoolean(this.getValue("UD"))&&!TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("DS"))) {
			result.append(" AND RX_KIND='UD'");
		}
		else if(TypeTool.getBoolean(this.getValue("DS"))&&!TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("UD"))) {
			result.append(" AND RX_KIND='DS'");
		}
		else if(TypeTool.getBoolean(this.getValue("ST"))&&TypeTool.getBoolean(this.getValue("UD"))&&!TypeTool.getBoolean(this.getValue("DS"))) {
			result.append(" AND RX_KIND IN ('UD','ST')");
		}
		else if(TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("UD"))&&TypeTool.getBoolean(this.getValue("DS"))) {
			result.append(" AND RX_KIND IN ('DS','ST')");
		}
		else if(!TypeTool.getBoolean(this.getValue("ST"))&&TypeTool.getBoolean(this.getValue("UD"))&&TypeTool.getBoolean(this.getValue("DS"))) {
			result.append(" AND RX_KIND IN ('UD','DS')");
		}else {
			result.append(" AND RX_KIND IN ('UD','ST','DS')");
		}
		if (!StringUtil.isNullString(this.getValueString("AGENCY_ORG_CODE"))) {
			result.append(" AND A.AGENCY_ORG_CODE='"
					+ this.getValueString("AGENCY_ORG_CODE") + "'");
		}
		if (StringTool.getBoolean(this.getValueString("STA"))) {
			if (!StringUtil.isNullString(this.getValueString("STATIONCOMBOL"))) {
				result.append(" AND A.STATION_CODE='"
						+ this.getValueString("STATIONCOMBOL") + "'");
			}
		} else if (StringTool.getBoolean(this.getValueString("MR"))) {
			String mr = this.getValueString("NO");
			int length = PatTool.getInstance().getMrNoLength();
			if(mr.contains("-")) {
				length= length+2;
			}
			String mrNo = StringTool.fill0(this.getValueString("NO"), length);//===cehnxi
			this.setValue("NO", mrNo);
			result.append(" AND A.MR_NO='" + mrNo + "'");
		} else {
			result.append(" AND A.BED_NO='" + getValueString("QUERY_BED") + "' ");
		}

		return result.toString();
	}

	/**
	 * 病患TABLE点击事件，整个TABLE只能有一个被点选
	 */
	public void onTblPatClick() {
		boolean value = TCM_Transform.getBoolean(tblPat.getValueAt(
				tblPat.getSelectedRow(), 0));
		int allRow = tblPat.getRowCount();

		for (int i = 0; i < allRow; i++) {
			tblPat.setValueAt(false, i, 0);
			tblPat.getParmValue().setData("EXEC", i, false);
		}
		tblPat.setValueAt(true, tblPat.getSelectedRow(), 0);
		tblPat.getParmValue().setData("EXEC", tblPat.getSelectedRow(), true);
		// System.out.println("click parm======"+tblPat.getParmValue());
		tblDtl.removeRowAll();
		onQueryDtl();
		
		//药品明细表上方增加的过敏史信息
		//设置初始过敏史信息框为不可见
		this.callFunction("UI|GMS|setVisible", false);
		this.callFunction("UI|DRUGORINGRD_CODE|setVisible", false);
		//获取选中病患清单TBL_PAT中的病案号
		//String mrNo = getTableSelectRowData("MR_NO", "TBL_PAT");
		//爱育华要求显示本次就诊过敏记录
		String caseNo = getTableSelectRowData("CASE_NO", "TBL_PAT");
		//根据病案号查出对应的过敏信息
		String drugoringdSql = "SELECT DISTINCT ORDER_DESC, ALLERGY_NOTE FROM ( "
				+ "SELECT (CASE WHEN OPD_DRUGALLERGY.DRUG_TYPE = 'D' THEN '' ELSE (SELECT CHN_DESC" +
				" FROM SYS_DICTIONARY" +
				" WHERE     GROUP_ID = 'SYS_ALLERGY'" +
				" AND ID = OPD_DRUGALLERGY.DRUG_TYPE) END)" +
				" AS DRUG_TYPE,(SELECT ORDER_DESC FROM SYS_FEE" +
				" WHERE     ORDER_CODE = OPD_DRUGALLERGY.DRUGORINGRD_CODE" +
				" AND OPD_DRUGALLERGY.drug_type = 'B')" +
				" || (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'PHA_INGREDIENT'" +
				" AND ID = OPD_DRUGALLERGY.DRUGORINGRD_CODE AND OPD_DRUGALLERGY.drug_type = 'A')" +
				" || (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_ALLERGYTYPE'" +
				" AND ID = OPD_DRUGALLERGY.DRUGORINGRD_CODE AND OPD_DRUGALLERGY.drug_type = 'C')" +
				" || (CASE WHEN  OPD_DRUGALLERGY.DRUG_TYPE = 'D' THEN DRUGORINGRD_CODE END )" +
				" AS ORDER_DESC, OPD_DRUGALLERGY.ALLERGY_NOTE FROM OPD_DRUGALLERGY, SYS_DEPT, SYS_OPERATOR" +
				" WHERE OPD_DRUGALLERGY.CASE_NO = '"+caseNo+"'" +
				" AND OPD_DRUGALLERGY.DEPT_CODE = SYS_DEPT.DEPT_CODE" +
				" AND OPD_DRUGALLERGY.DR_CODE = SYS_OPERATOR.USER_ID" +
				" ) WHERE TRIM(ORDER_DESC) <> '-'";
		TParm drugorTparm = new TParm(TJDODBTool.getInstance().select(drugoringdSql));
		//定义一个存放字符串的临时器皿
		String temp = "";
		for(int i = 0 ;i < drugorTparm.getCount(); i++){
			if(StringUtils.isEmpty(drugorTparm.getValue("ORDER_DESC",i))){
				if(StringUtils.isNotEmpty(drugorTparm.getValue("ALLERGY_NOTE",i))){
					temp += drugorTparm.getValue("ALLERGY_NOTE",i)+"；";
				}
			}else{
				temp += drugorTparm.getValue("ORDER_DESC",i);
				if(StringUtils.isNotEmpty(drugorTparm.getValue("ALLERGY_NOTE",i))){
					temp += ","+drugorTparm.getValue("ALLERGY_NOTE",i)+"；";
				}else{
					temp += "；";
				}
			}
			//判断字段信息为无字时，赋值为空
//			if(drugorTparm.getValue("ORDER_DESC",i).equals("无")){
//				temp = temp + "";
//			}else{
//				if(StringUtils.isEmpty(drugorTparm.getValue("ORDER_DESC",i))){
//					if(StringUtils.isNotEmpty(drugorTparm.getValue("ALLERGY_NOTE",i))){
//						temp += drugorTparm.getValue("ALLERGY_NOTE",i)+"；";
//					}
//				}else{
//					temp += drugorTparm.getValue("ORDER_DESC",i);
//					if(StringUtils.isNotEmpty(drugorTparm.getValue("ALLERGY_NOTE",i))){
//						temp += ","+drugorTparm.getValue("ALLERGY_NOTE",i)+"；";
//					}else{
//						temp += "；";
//					}
//				}
//			}
		}
		//去掉末尾的逗号
		temp = temp.length()>0?temp.substring(0, temp.length()-1):""; 
		this.setValue("DRUGORINGRD_CODE", temp);
		if(temp.length() > 0){
			this.callFunction("UI|GMS|setVisible", true);
			this.callFunction("UI|DRUGORINGRD_CODE|setVisible", true);
		}
	}
	
	/**
	 * 取得表格选中行数据
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
	 * 按行号取得该行数据
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
	 * 保存
	 */
	public void onSave() {
		String userID = Operator.getID();
		
		if (TypeTool.getBoolean(this.getValue("CHECK"))) {
			this.messageBox_("无保存数据");
			return;
		}
		TParm parm = new TParm();
		/*
		 * UPDATE ODI_ORDER SET
		 * PHA_CHECK_CODE=<OPT_USER>,PHA_CHECK_DATE=SYSDATE,
		 * OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE
		 * CASE_NO=<CASE_NO> AND ORDER_NO=<ORDER_NO> AND ORDER_SEQ=<ORDER_SEQ>
		 */
		/*
		 * UPDATE ODI_DSPNM SET
		 * PHA_DOSAGE_CODE=<OPT_USER>,PHA_DOSAGE_DATE=SYSDATE,
		 * OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE
		 * CASE_NO=<CASE_NO> AND ORDER_NO=<ORDER_NO> AND ORDER_SEQ=<ORDER_SEQ>
		 */
		// System.out.println("savePaarm==="+saveParm);
		boolean drugFlg = false;
		for (int i = 0; i < saveParm.getCount("ORDER_SEQ"); i++) {
			String orderCode = saveParm.getValue("ORDER_CODE", i);
			String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='"+orderCode+"' AND CTRL_FLG='Y'";
			TParm drugParm = new TParm(TJDODBTool.getInstance().select(sql)) ;
			if(drugParm.getCount()>0) {
				drugFlg = true;
			}
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("CASE_NO", saveParm.getValue("CASE_NO", i));
			parm.addData("ORDER_NO", saveParm.getValue("ORDER_NO", i));
			parm.addData("ORDER_SEQ", saveParm.getInt("ORDER_SEQ", i));
			parm.addData("DCTAGENT_FLG", "N");
			parm.addData("RX_KIND", saveParm.getValue("RX_KIND", i));//20150505 wangjingchun add 静脉营养用
			if(StringUtils.isEmpty(saveParm.getValue("PN_FLG", i)))
				parm.addData("PN_FLG", "N");//20150505 wangjingchun add 静脉营养用
			else
				parm.addData("PN_FLG", saveParm.getValue("PN_FLG", i));//20150505 wangjingchun add 静脉营养用
		}  
		if(drugFlg) {
			String sql = "SELECT * FROM SYS_OPERATOR WHERE USER_ID='"+userID+"' AND CTRL_FLG='Y'";  
			TParm drugParm = new TParm(TJDODBTool.getInstance().select(sql)) ;   
			if(drugParm.getCount()<=0) {
				this.messageBox("无麻精药品审核权限");			
				return;
			}
		}
		if (parm.getCount("OPT_USER") < 1) {
			this.messageBox_("无可保存数据");
			return;
		}
		// 合理用药
		if (!checkDrugAuto()) {
			return;
		}
		parm = TIOM_AppServer.executeAction("action.udd.UddAction",
				"onUpdateMedCheck", parm);
		if (parm.getErrCode() != 0) {
			// this.messageBox_(parm.getErrText());
			this.messageBox("E0001");
		} else {
			this.messageBox("P0001");
		}
//		onClear();
//		Timestamp t = TJDODBTool.getInstance().getDBTime();
//		this.setValue("START_DATE", StringTool.rollDate(t, -7));
//		this.setValue("END_DATE", t);
//		this.setValue("UDST", Y);
//		this.setValue("EXEC_DEPT_CODE", Operator.getDept());
//		// this.setValue("EXEC_DEPT_CODE", "308003");
//		this.setValue("AGENCY_ORG_CODE", NULL);
//		this.setValue("STA", Y);
//		this.setValue("UNCHECK", Y);
//		this.setValue("NO", NULL);
//		this.setValue("NAME", NULL);
		//luhai modify 2012-3-19 保存后不清空查询状态 begin
		//onClear();
		tblPat.removeRowAll();
		tblDtl.removeRowAll();
		setEnableMenu();
		this.onQuery();
		//luhai modify 2012-3-19 保存后不清空查询状态 end
	}

	/**
	 * 取消审核
	 */
	public void onDelete() {
		if (!TypeTool.getBoolean(this.getValue("CHECK"))) {
			this.messageBox_("无保存数据");
			return;
		}
		TParm parm = new TParm();
		// System.out.println("savePaarm==="+saveParm);
		for (int i = 0; i < saveParm.getCount("ORDER_SEQ"); i++) {
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("CASE_NO", saveParm.getValue("CASE_NO", i));
			parm.addData("ORDER_NO", saveParm.getValue("ORDER_NO", i));
			parm.addData("ORDER_SEQ", saveParm.getInt("ORDER_SEQ", i));
		}
		// System.out.println("parm========"+parm);
		if (parm.getCount("OPT_USER") < 1) {
			this.messageBox_("无可保存数据");
			return;
		}
		if (!isDosage()) {
			return;
		}
		parm = TIOM_AppServer.executeAction("action.udd.UddAction",
				"onUpdateUnCheck", parm);
		if (parm.getErrCode() != 0) {
			// this.messageBox_(parm.getErrText());
			this.messageBox("E0001");
		} else {
			this.messageBox("P0001");
		}
		onClear();
	}

	/**
	 * 取消
	 * 
	 * @return
	 */
	private boolean isDosage() {
		if (saveParm == null) {
			return false;
		}
		int count = saveParm.getCount();
		if (count < 1) {
			return false;
		}
		for (int i = 0; i < count; i++) {
			if (!UddChnCheckTool.getInstance().isDosage(saveParm.getRow(i))) {
				this.messageBox_("药品已配药,不能取消审核");
				return false;
			}
		}
		return true;
	}

	/**
	 * 病案号/床号输入框的回车查询事件
	 */
	public void onNo() {
		onQuery();
	}

	/**
	 * 护士站combo点击查询事件
	 */
	public void onQueryStation() {
		onQuery();
	}

	public TParm MedtableClick() {
		TParm parm = new TParm();
		int row = tblDtl.getSelectedRow();
		if (row < 0) {
		}
		String value = saveParm.getValue("ORDER_CODE", row);
		String orderNO = saveParm.getValue("ORDER_NO", row);
		int seq = saveParm.getInt("ORDER_SEQ", row);
		parm.setData("ORDER_CODE", value);
		parm.setData("ORDER_NO", orderNO);
		parm.setData("ORDER_SEQ", seq);
		//============== chenxi   modify  20120703 
		TParm  action = tblDtl.getParmValue() ;
		String orderCode =  action.getValue("ORDER_CODE", row) ;
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC," +
		"DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE" +
		" WHERE ORDER_CODE = '" +orderCode+ "'" ;
          TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql)) ;
         sqlparm = sqlparm.getRow(0);
         //状态栏显示医嘱提示
         //PN字段点击事件
       //20150505 wangjingchun add start
 		//选取静脉营养
 		int column = tblDtl.getSelectedColumn();
 		if (getRadioButton("UNCHECK").isSelected()) {
 			if ("N".equals(tblDtl.getItemString(row, "PN_FLG"))
 					&& column == 9 
// 					&& "ST".equals(tblDtl.getParmValue().getValue("RX_KIND", row))
 					&& !"".equals(tblDtl.getParmValue().getValue("LINK_NO", row))) {
// 				tblDtl.setItem(row, "PN_FLG", "Y");
 				for(int i=0;i<tblDtl.getParmValue().getCount("CASE_NO");i++){
 					if(tblDtl.getParmValue().getValue("LINK_NO", i).equals(tblDtl.getParmValue().getValue("LINK_NO", row))){
 						tblDtl.setItem(i, "PN_FLG", "Y");
 					}
 				}
 			} else if ("Y".equals(tblDtl.getItemString(row, "PN_FLG"))
 					&& column == 9
// 					&& "ST".equals(tblDtl.getParmValue().getValue("RX_KIND", row))
 					&& !"".equals(tblDtl.getParmValue().getValue("LINK_NO", row))) {
 				for(int i=0; i < tblDtl.getParmValue().getCount("CASE_NO");i++) {
 					if(tblDtl.getParmValue().getValue("LINK_NO", i).equals(tblDtl.getParmValue().getValue("LINK_NO", row))){
 						tblDtl.setItem(i, "PN_FLG", "N");
 					}
 				}
// 				tblDtl.setItem(row, "PN_FLG", "N");
 			} else if (column == 9){
 				tblDtl.setItem(row, "PN_FLG", "N");
 			}
 		}else{
 			if (column == 9
 					&& !"ST".equals(tblDtl.getParmValue().getValue("RX_KIND", row))) {
 				tblDtl.setItem(row, "PN_FLG", "N");
 			}
 		}
         
         
         callFunction(
  				"UI|setSysStatus",
  				sqlparm.getValue("ORDER_CODE") + " " + sqlparm.getValue("ORDER_DESC")
  						+ " " + sqlparm.getValue("GOODS_DESC") + " "
  						+ sqlparm.getValue("DESCRIPTION") + " "
  						+ sqlparm.getValue("SPECIFICATION") + " "
  						+ sqlparm.getValue("REMARK_1") + " "
  						+ sqlparm.getValue("REMARK_2") + " "
  						+ sqlparm.getValue("DRUG_NOTES_DR"));
         // ===============  modify  by   chenxi   20120703
		return parm;
	}

	/**
	 * 药品信息查询
	 */
	public void queryDrug() {
		if (!passIsReady) {
			messageBox("合理用药未启用");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("合理用药初始化失败，此功能不能使用！");
			return;
		}
		int row = getTable("TBL_MED").getSelectedRow();
		if (row < 0) {
			return;
		}
		String value = (String) this
				.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
		if (value == null || value.length() == 0) {
			return;
		}
		int conmmand = Integer.parseInt(value);
		if (conmmand != 6) {
			PassTool.getInstance().setQueryDrug(
					MedtableClick().getValue("ORDER_CODE"), conmmand);
		} else {
			PassTool.getInstance().setWarnDrug2(
					MedtableClick().getValue("ORDER_NO"),
					" " + MedtableClick().getValue("ORDER_SEQ"));
		}

	}

	/**
	 * 手动检测合理用药
	 */
	public void checkDrugHand() {
		if (!passIsReady) {
			messageBox("合理用药未启用");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("合理用药初始化失败，此功能不能使用！");
			return;
		}
		if (saveParm.getValue("CASE_NO", 0) == null) {
			return;
		}
		String type="";
		/*if (TypeTool.getBoolean(this.getValue("UDST"))) {
			type="UDST";
		} else {
			type="DS";
		}*/
		if(TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("UD"))&&!TypeTool.getBoolean(this.getValue("DS"))) {
			type=" AND A.RX_KIND='ST'";
		}
		else if(TypeTool.getBoolean(this.getValue("UD"))&&!TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("DS"))) {
			type=" AND A.RX_KIND='UD'";
		}
		else if(TypeTool.getBoolean(this.getValue("DS"))&&!TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("UD"))) {
			type=" AND A.RX_KIND='DS'";
		}
		else if(TypeTool.getBoolean(this.getValue("ST"))&&TypeTool.getBoolean(this.getValue("UD"))&&!TypeTool.getBoolean(this.getValue("DS"))) {
			type=" AND (A.RX_KIND='ST' OR A.RX_KIND='UD')";
		}
		else if(TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("UD"))&&TypeTool.getBoolean(this.getValue("DS"))) {
			type=" AND (A.RX_KIND='ST' OR A.RX_KIND='DS')";
		}
		else if(!TypeTool.getBoolean(this.getValue("ST"))&&TypeTool.getBoolean(this.getValue("UD"))&&TypeTool.getBoolean(this.getValue("DS"))) {
			type=" AND (A.RX_KIND='UD' OR A.RX_KIND='DS')";
		}else {
			type=" AND (A.RX_KIND='UD' OR A.RX_KIND='DS' OR A.RX_KIND='ST')";
		}
		
		PassTool.getInstance().init();
		PassTool.getInstance().setadmPatientInfo(
				saveParm.getValue("CASE_NO", 0));
		PassTool.getInstance().setAllergenInfo(saveParm.getValue("MR_NO", 0));
		PassTool.getInstance().setadmMedCond(saveParm.getValue("CASE_NO", 0));
		TParm parm = PassTool.getInstance().setadmRecipeInfoHand(
				saveParm.getValue("CASE_NO", 0), odiOrdercat,type);
		isWarn(parm);
	}
	/**
	 * 医疗卡读卡
	 */
	public void onEKT() {
		TParm parm = EKTIO.getInstance().TXreadEKT();
        //System.out.println("parm==="+parm);
    	if (null == parm || parm.getValue("MR_NO").length() <= 0) {
            this.messageBox("请查看医疗卡是否正确使用");
            return;
        } 
    	//zhangp 20120130
    	if(parm.getErrCode()<0){
    		messageBox(parm.getErrText());
    	}
		setValue("NO", parm.getValue("MR_NO"));
		TRadioButton td=(TRadioButton)this.getComponent("MR");
		td.setSelected(true);
		this.onQuery();
		//修改读医疗卡功能  end luhai 2012-2-27 
	}
	/**
	 * 自动检测合理用药
	 */
	private boolean checkDrugAuto() {
		if (!passIsReady) {
			return true;
		}
		if (!PassTool.getInstance().init()) {
			return true;
		}
		String type="";
		/*if (TypeTool.getBoolean(this.getValue("UDST"))) {
			type="UDST";
		} else {
			type="DS";
		}*/
		if(TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("UD"))&&!TypeTool.getBoolean(this.getValue("DS"))) {
			type=" AND A.RX_KIND='ST'";
		}
		else if(TypeTool.getBoolean(this.getValue("UD"))&&!TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("DS"))) {
			type=" AND A.RX_KIND='UD'";
		}
		else if(TypeTool.getBoolean(this.getValue("DS"))&&!TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("UD"))) {
			type=" AND A.RX_KIND='DS'";
		}
		else if(TypeTool.getBoolean(this.getValue("ST"))&&TypeTool.getBoolean(this.getValue("UD"))&&!TypeTool.getBoolean(this.getValue("DS"))) {
			type=" AND (A.RX_KIND='ST' OR A.RX_KIND='UD')";
		}
		else if(TypeTool.getBoolean(this.getValue("ST"))&&!TypeTool.getBoolean(this.getValue("UD"))&&TypeTool.getBoolean(this.getValue("DS"))) {
			type=" AND (A.RX_KIND='ST' OR A.RX_KIND='DS')";
		}
		else if(!TypeTool.getBoolean(this.getValue("ST"))&&TypeTool.getBoolean(this.getValue("UD"))&&TypeTool.getBoolean(this.getValue("DS"))) {
			type=" AND (A.RX_KIND='UD' OR A.RX_KIND='DS')";
		}else {
			type=" AND (A.RX_KIND='UD' OR A.RX_KIND='DS' OR A.RX_KIND='ST')";
		}
		PassTool.getInstance().init();
		PassTool.getInstance().setadmPatientInfo(
				saveParm.getValue("CASE_NO", 0));
		PassTool.getInstance().setAllergenInfo(saveParm.getValue("MR_NO", 0));
		PassTool.getInstance().setadmMedCond(saveParm.getValue("CASE_NO", 0));
		TParm parm = PassTool.getInstance().setadmRecipeInfoAuto(
				saveParm.getValue("CASE_NO", 0), odiOrdercat,type);
		if (!isWarn(parm)) {
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

	private boolean isWarn(TParm parm) {
		boolean warnFlg = false;
		for (int i = 0; i < parm.getCount("ORDER_NO"); i++) {
			int flg = parm.getInt("FLG", i);
			if (!warnFlg) {
				if (getWarn(flg)) {
					warnFlg = true;
				} else {
					warnFlg = false;
				}
			}
		}
		return warnFlg;
	}

	private boolean getWarn(int flg) {
		if (warnFlg != 3 && flg != 3) {
			if (warnFlg != 2 && flg != 2) {
				if (flg >= warnFlg) {
					return true;
				} else {
					return false;
				}
			} else if (warnFlg == 2 && flg != 2) {
				return false;
			} else if (warnFlg != 2 && flg == 2) {
				return true;
			} else if (warnFlg == 2 && flg == 2) {
				return true;
			}
		} else if (warnFlg == 3 && flg != 3) {
			return false;
		} else if (warnFlg != 3 && flg == 3) {
			return true;
		} else if (warnFlg == 3 && flg == 3) {
			return true;
		}
		return false;
	}

	/**
	 * 显示商品名
	 */
	public void onShowGoodsName() {
		if (tblDtl == null)
			return;
		TParm parm = tblDtl.getParmValue();
		// System.out.println("before goods=============="+parm);
		int count = parm.getCount();
		if (count < 1)
			return;
		// LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;EFF_DATE;DC_DATE;DR_NOTE;ORDER_DR_CODE
		boolean showGoods = TypeTool.getBoolean(this.getValue("SHOW_DESC"));
		// tblDtl.removeRowAll();
		if (showGoods) {
			tblDtl.setParmMap("LINKMAIN_FLG;LINK_NO;GOODS_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;EFF_DATE;DC_DATE;DR_NOTE;ORDER_DR_CODE");
		} else {
			tblDtl.setParmMap("LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;EFF_DATE;DC_DATE;DR_NOTE;ORDER_DR_CODE");
		}
		tblDtl.setParmValue(parm);
		// System.out.println("goods parm======="+parm);
	}

	public void setEnableMenu() {
		if (TypeTool.getBoolean(this.getValue("UNCHECK"))) {
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|delete|setEnabled", false);
		} else {
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|delete|setEnabled", true);
		}
	}

	/**
	 * 取得表格控件
	 * 
	 * @param tableName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableName) {
		return (TTable) getComponent(tableName);
	}

	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	/**
	 * 右击MENU弹出事件-duzhw-20131121
	 * 
	 * @param tableName
	 */
	public void showPopMenu(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		table.setPopupMenuSyntax("给药原因|Show Drug Use Desc,useDrugMenu"); 
	}
	/**
	 * 使用说明-duzhw-20131121
	 */
	public void useDrugMenu(){
		int selectedIndx=this.getTTable("TBL_MED").getSelectedRow();
    	if(selectedIndx<0){
    		return;
    	}

    	TParm tableparm=this.getTTable("TBL_MED").getParmValue();
    	String caseNo = tableparm.getValue("CASE_NO", selectedIndx);
    	String orderNo = tableparm.getValue("ORDER_NO", selectedIndx);
    	String orderSeq = tableparm.getValue("ORDER_SEQ", selectedIndx);
    	String orderCode = tableparm.getValue("ORDER_CODE", selectedIndx);
    	TParm parm = new TParm();
    	parm.setData("CASE_NO", caseNo);
    	parm.setData("ORDER_NO", orderNo);
    	parm.setData("ORDER_SEQ", orderSeq);
    	parm.setData("ORDER_CODE", orderCode);
		
		this.openDialog("%ROOT%\\config\\odi\\ODIOrderDrugDesc.x", parm);
	}
	/**
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	
	/**
	 * 打印电子病历
	 */
	public void onElecCaseHistory() {
		TParm parm = tblPat.getParmValue();
		if(parm.getCount("MR_NO")==0) {
			this.messageBox("请选择病患");
			return;
		}
		String mrNo = "";
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			// String exec=parm.getValue("EXEC",i);
			if (parm.getBoolean("EXEC", i))
				mrNo = parm.getValue("MR_NO",i);
		}
		if("".equals(mrNo)) {
			this.messageBox("请选择病患");
			return;
		}
	/*	if (null == this.getValueString("MR_NO")
				|| this.getValueString("MR_NO").length() <= 0) {
			this.messageBox("请选择一个病人");
			return;
		}*/
		Runtime run = Runtime.getRuntime();
		try {
			// 得到当前使用的ip地址
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("EMRWebInitServlet?Mr_No=");
			// 连接网页方法
			run.exec("IEXPLORE.EXE " + ip + mrNo);
		} catch (IOException ex) {			
			ex.printStackTrace();
		}		

	}
	
	/**
	 * 病床点选事件
	 */
	public void onBedNo() {
		setValue("QUERY_BED", "");
		queryBed.setVisible(true);
		callFunction("UI|NO|setVisible",
				new Object[] { Boolean.valueOf(false) });
		 
	}
	
	/**
	 * 病案号点选事件
	 */
	public void onMrNo() {
		setValue("QUERY_BED", "");
		queryBed.setVisible(false);
		callFunction("UI|NO|setVisible", new Object[] { Boolean.valueOf(true) });
		 
		
	}
	
	/**
	 * 病区点选事件
	 */
	public void onStation() {
		setValue("QUERY_BED", "");
		queryBed.setVisible(false);
		callFunction("UI|NO|setVisible", new Object[] { Boolean.valueOf(true) });
	}
	
	public TRadioButton getRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}
	
}
