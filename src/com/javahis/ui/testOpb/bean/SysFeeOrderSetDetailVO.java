package com.javahis.ui.testOpb.bean;

import com.javahis.ui.testOpb.annotation.Column;
import com.javahis.ui.testOpb.tools.Type;

/**
 * 
 * @author whao
 * @author zhangp
 */
public class SysFeeOrderSetDetailVO extends BasePOJO {

	@Column(name = "ORDERSET_CODE", type = Type.CHAR)
	public java.lang.String ordersetCode = null;
	
	@Column(name = "HIDE_FLG", type = Type.CHAR)
	public java.lang.String hideFlg = null;
	
	@Column(name = "ORDERSETDESC", type = Type.CHAR)
	public java.lang.String ordersetdesc = null;
	
	@Column(name = "TOTQTY", type = Type.NUM)
	public java.math.BigDecimal totqty = null;

	@Column(name = "ORDER_CODE", type = Type.CHAR)
	public java.lang.String orderCode = null;

	@Column(name = "USE_CAT", type = Type.CHAR)
	public java.lang.String useCat = null;

	@Column(name = "USEDEPT_CODE", type = Type.CHAR)
	public java.lang.String usedeptCode = null;

	@Column(name = "UNIT_CODE", type = Type.CHAR)
	public java.lang.String unitCode = null;

	@Column(name = "TUBE_TYPE", type = Type.CHAR)
	public java.lang.String tubeType = null;

	@Column(name = "TRANS_OUT_FLG", type = Type.CHAR)
	public java.lang.String transOutFlg = null;

	@Column(name = "TRANS_HOSP_CODE", type = Type.CHAR)
	public java.lang.String transHospCode = null;

	@Column(name = "TRADE_ENG_DESC", type = Type.CHAR)
	public java.lang.String tradeEngDesc = null;

	@Column(name = "TIME_LIMIT", type = Type.CHAR)
	public java.lang.String timeLimit = null;

	@Column(name = "SYS_PHA_CLASS", type = Type.CHAR)
	public java.lang.String sysPhaClass = null;

	@Column(name = "SYS_GRUG_CLASS", type = Type.CHAR)
	public java.lang.String sysGrugClass = null;

	@Column(name = "SUPPLIES_TYPE", type = Type.CHAR)
	public java.lang.String suppliesType = null;

	@Column(name = "SUB_SYSTEM_CODE", type = Type.CHAR)
	public java.lang.String subSystemCode = null;

	@Column(name = "SPECIFICATION", type = Type.CHAR)
	public java.lang.String specification = null;

	@Column(name = "SEQ", type = Type.NUM)
	public java.math.BigDecimal seq = null;

	@Column(name = "RPTTYPE_CODE", type = Type.CHAR)
	public java.lang.String rpttypeCode = null;

	@Column(name = "REMARK_2", type = Type.CHAR)
	public java.lang.String remark2 = null;

	@Column(name = "REMARK_1", type = Type.CHAR)
	public java.lang.String remark1 = null;

	@Column(name = "REGION_CODE", type = Type.CHAR)
	public java.lang.String regionCode = null;

	@Column(name = "PY2", type = Type.CHAR)
	public java.lang.String py2 = null;

	@Column(name = "PY1", type = Type.CHAR)
	public java.lang.String py1 = null;

	@Column(name = "OWN_PRICE3", type = Type.NUM)
	public java.math.BigDecimal ownPrice3 = null;

	@Column(name = "OWN_PRICE2", type = Type.NUM)
	public java.math.BigDecimal ownPrice2 = null;

	@Column(name = "OWN_PRICE", type = Type.NUM)
	public java.math.BigDecimal ownPrice = null;

	@Column(name = "ORD_SUPERVISION", type = Type.CHAR)
	public java.lang.String ordSupervision = null;

	@Column(name = "ORDER_DESC", type = Type.CHAR)
	public java.lang.String orderDesc = null;

	@Column(name = "ORDER_CAT1_CODE", type = Type.CHAR)
	public java.lang.String orderCat1Code = null;

	@Column(name = "SET_FLG", type = Type.CHAR)
	public java.lang.String setFlg = null;

	@Column(name = "OPT_USER", type = Type.CHAR)
	public java.lang.String optUser = null;

	@Column(name = "OPT_TERM", type = Type.CHAR)
	public java.lang.String optTerm = null;

	@Column(name = "OPT_DATE", type = Type.DATE)
	public java.lang.String optDate = null;

	@Column(name = "OPTITEM_CODE", type = Type.CHAR)
	public java.lang.String optitemCode = null;

	@Column(name = "OPD_FIT_FLG", type = Type.CHAR)
	public java.lang.String opdFitFlg = null;

	@Column(name = "NOADDTION_FLG", type = Type.CHAR)
	public java.lang.String noaddtionFlg = null;

	@Column(name = "NHI_PRICE", type = Type.NUM)
	public java.math.BigDecimal nhiPrice = null;

	@Column(name = "NHI_FEE_DESC", type = Type.CHAR)
	public java.lang.String nhiFeeDesc = null;

	@Column(name = "NHI_CODE_O", type = Type.CHAR)
	public java.lang.String nhiCodeO = null;

	@Column(name = "NHI_CODE_I", type = Type.CHAR)
	public java.lang.String nhiCodeI = null;

	@Column(name = "NHI_CODE_E", type = Type.CHAR)
	public java.lang.String nhiCodeE = null;

	@Column(name = "MR_CODE", type = Type.CHAR)
	public java.lang.String mrCode = null;

	@Column(name = "MEDANT_FLG", type = Type.CHAR)
	public java.lang.String medantFlg = null;

	@Column(name = "MAN_CODE", type = Type.CHAR)
	public java.lang.String manCode = null;

	@Column(name = "LET_KEYIN_FLG", type = Type.CHAR)
	public java.lang.String letKeyinFlg = null;

	@Column(name = "LCS_CLASS_CODE", type = Type.CHAR)
	public java.lang.String lcsClassCode = null;

	@Column(name = "IS_REMARK", type = Type.CHAR)
	public java.lang.String isRemark = null;

	@Column(name = "IPD_FIT_FLG", type = Type.CHAR)
	public java.lang.String ipdFitFlg = null;

	@Column(name = "IO_CODE", type = Type.CHAR)
	public java.lang.String ioCode = null;

	@Column(name = "IN_OPFLG", type = Type.CHAR)
	public java.lang.String inOpflg = null;

	@Column(name = "INF_ORDER_FLG", type = Type.CHAR)
	public java.lang.String infOrderFlg = null;

	@Column(name = "INSPAY_TYPE", type = Type.CHAR)
	public java.lang.String inspayType = null;

	@Column(name = "INDV_FLG", type = Type.CHAR)
	public java.lang.String indvFlg = null;

	@Column(name = "HYGIENE_TRADE_CODE", type = Type.CHAR)
	public java.lang.String hygieneTradeCode = null;

	@Column(name = "HRM_FIT_FLG", type = Type.CHAR)
	public java.lang.String hrmFitFlg = null;

	@Column(name = "HABITAT_TYPE", type = Type.CHAR)
	public java.lang.String habitatType = null;

	@Column(name = "GOV_PRICE", type = Type.NUM)
	public java.math.BigDecimal govPrice = null;

	@Column(name = "GOODS_PYCODE", type = Type.CHAR)
	public java.lang.String goodsPycode = null;

	@Column(name = "GOODS_DESC", type = Type.CHAR)
	public java.lang.String goodsDesc = null;

	@Column(name = "EXPENSIVE_FLG", type = Type.CHAR)
	public java.lang.String expensiveFlg = null;

	@Column(name = "EXE_PLACE", type = Type.CHAR)
	public java.lang.String exePlace = null;

	@Column(name = "RBORDER_FLG", type = Type.CHAR)
	public java.lang.String rborderFlg = null;

	@Column(name = "RBORDER_DEPT_CODE", type = Type.CHAR)
	public java.lang.String rborderDeptCode = null;

	@Column(name = "EMG_FIT_FLG", type = Type.CHAR)
	public java.lang.String emgFitFlg = null;

	@Column(name = "DR_ORDER_FLG", type = Type.CHAR)
	public java.lang.String drOrderFlg = null;

	@Column(name = "DRUG_NOTES_PATIENT", type = Type.CHAR)
	public java.lang.String drugNotesPatient = null;

	@Column(name = "DRUG_NOTES_DR", type = Type.CHAR)
	public java.lang.String drugNotesDr = null;

	@Column(name = "DRUG_NOTE", type = Type.CHAR)
	public java.lang.String drugNote = null;

	@Column(name = "DRUG_FORM", type = Type.CHAR)
	public java.lang.String drugForm = null;

	@Column(name = "DISCOUNT_FLG", type = Type.CHAR)
	public java.lang.String discountFlg = null;

	@Column(name = "DEV_CODE", type = Type.CHAR)
	public java.lang.String devCode = null;

	@Column(name = "DESCRIPTION", type = Type.CHAR)
	public java.lang.String description = null;

	@Column(name = "DEGREE_CODE", type = Type.CHAR)
	public java.lang.String degreeCode = null;

	@Column(name = "DDD", type = Type.CHAR)
	public java.lang.String ddd = null;

	@Column(name = "CTRL_FLG", type = Type.CHAR)
	public java.lang.String ctrlFlg = null;

	@Column(name = "CRT_FLG", type = Type.CHAR)
	public java.lang.String crtFlg = null;

	@Column(name = "CLPGROUP_CODE", type = Type.CHAR)
	public java.lang.String clpgroupCode = null;

	@Column(name = "CIS_FLG", type = Type.CHAR)
	public java.lang.String cisFlg = null;

	@Column(name = "CHARGE_HOSP_CODE", type = Type.CHAR)
	public java.lang.String chargeHospCode = null;

	@Column(name = "CAT1_TYPE", type = Type.CHAR)
	public java.lang.String cat1Type = null;

	@Column(name = "ATC_FLG_I", type = Type.CHAR)
	public java.lang.String atcFlgI = null;

	@Column(name = "ATC_FLG", type = Type.CHAR)
	public java.lang.String atcFlg = null;

	@Column(name = "ALIAS_PYCODE", type = Type.CHAR)
	public java.lang.String aliasPycode = null;

	@Column(name = "ALIAS_DESC", type = Type.CHAR)
	public java.lang.String aliasDesc = null;

	@Column(name = "ADDPAY_RATE", type = Type.NUM)
	public java.math.BigDecimal addpayRate = null;

	@Column(name = "ADDPAY_AMT", type = Type.NUM)
	public java.math.BigDecimal addpayAmt = null;

	@Column(name = "ACTIVE_FLG", type = Type.CHAR)
	public java.lang.String activeFlg = null;

	@Column(name = "ACTION_CODE", type = Type.CHAR)
	public java.lang.String actionCode = null;

}
