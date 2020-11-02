package jdo.spc;

import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:��������HISͬ���� SQL�ܻ�
 * </p>
 * 
 * <p>
 * Description:��������HISͬ���� SQL�ܻ�
 * </p>
 * 
 * @author zhangyiwu 2013.7.10
 * @version 1.0
 */

public class SYSDictionaryPatchTool extends TJDODBTool {

	/** ʵ�� */
	public static SYSDictionaryPatchTool instanceObject;

	/** �õ�ʵ�� */
	public static SYSDictionaryPatchTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSDictionaryPatchTool();
		return instanceObject;
	}

	/*
	 * ���� PHA_DOSE��
	 */
	public TParm onSavePhaDose(TParm tparm) {
		String sql = "INSERT INTO PHA_DOSE (DOSE_CODE,DOSE_CHN_DESC,PY1,PY2,DESCRIPTION,DOSE_TYPE,ENG_DESC,SEQ,OPT_USER,"
				+ "OPT_DATE,OPT_TERM )values('"
				+ tparm.getValue("DOSE_CODE")
				+ "','"
				+ tparm.getValue("DOSE_CHN_DESC")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("DOSE_TYPE")
				+ "','"
				+ tparm.getValue("ENG_DESC")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','apserver',sysdate,'apserver')";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ���� PHA_DOSE��
	 */
	public TParm OnUpdatePhaDose(TParm tparm) {

		String sql = "UPDATE PHA_DOSE SET DOSE_CHN_DESC ='"
				+ tparm.getValue("DOSE_CHN_DESC") + "'," + "PY1='"
				+ tparm.getValue("PY1") + "'," + "PY2='"
				+ tparm.getValue("PY2") + "'," + "DESCRIPTION='"
				+ tparm.getValue("DESCRIPTION") + "'," + "DOSE_TYPE='"
				+ tparm.getValue("DOSE_TYPE") + "'," + "ENG_DESC='"
				+ tparm.getValue("ENG_DESC") + "'," + "SEQ='"
				+ tparm.getValue("SEQ") + "'" + " WHERE DOSE_CODE='"
				+ tparm.getValue("DOSE_CODE") + "'";

		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ɾ�� PHA_DOSE��
	 */
	public TParm OnDeletePhaDose(TParm tparm) {
		String sql = "DELETE FROM PHA_DOSE WHERE DOSE_CODE ='"
				+ tparm.getValue("DOSE_CODE") + "'";

		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ����SYS_BED��
	 */

	public TParm OnSaveSysBed(TParm tparm) {
		String sql = "INSERT INTO SYS_BED (BED_NO,BED_NO_DESC,PY1,PY2,DESCRIPTION,ROOM_CODE,STATION_CODE,"
				+ "SEQ,REGION_CODE,BED_CLASS_CODE,BED_TYPE_CODE,ACTIVE_FLG,APPT_FLG,ALLO_FLG,"
				+ "BED_OCCU_FLG,RESERVE_BED_FLG,SEX_CODE,OCCU_RATE_FLG,DR_APPROVE_FLG,BABY_BED_FLG,"
				+ "HTL_FLG,ADM_TYPE,MR_NO,CASE_NO,IPD_NO,DEPT_CODE,BED_STATUS,ENG_DESC,"
				+ "OPT_USER,OPT_DATE,OPT_TERM )values('"
				+ tparm.getValue("BED_NO")
				+ "','"
				+ tparm.getValue("BED_NO_DESC")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("ROOM_CODE")
				+ "','"
				+ tparm.getValue("STATION_CODE")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','"
				+ tparm.getValue("REGION_CODE")
				+ "','"
				+ tparm.getValue("BED_CLASS_CODE")
				+ "','"
				+ tparm.getValue("BED_TYPE_CODE")
				+ "','"
				+ tparm.getValue("ACTIVE_FLG")
				+ "','"
				+ tparm.getValue("APPT_FLG")
				+ "','"
				+ tparm.getValue("ALLO_FLG")
				+ "','"
				+ tparm.getValue("BED_OCCU_FLG")
				+ "','"
				+ tparm.getValue("RESERVE_BED_FLG")
				+ "','"
				+ tparm.getValue("SEX_CODE")
				+ "','"
				+ tparm.getValue("OCCU_RATE_FLG")
				+ "','"
				+ tparm.getValue("DR_APPROVE_FLG")
				+ "','"
				+ tparm.getValue("BABY_BED_FLG")
				+ "','"
				+ tparm.getValue("HTL_FLG")
				+ "','"
				+ tparm.getValue("ADM_TYPE")
				+ "','"
				+ tparm.getValue("MR_NO")
				+ "','"
				+ tparm.getValue("CASE_NO")
				+ "','"
				+ tparm.getValue("IPD_NO")
				+ "','"
				+ tparm.getValue("DEPT_CODE")
				+ "','"
				+ tparm.getValue("BED_STATUS")
				+ "','"
				+ tparm.getValue("ENG_DESC")
				+ "','apserver',sysdate,'apserver')";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ���� SYS_BED��
	 */
	public TParm OnUpdateSysBed(TParm tparm) {
		String sql = "UPDATE SYS_BED SET BED_NO_DESC ='"
				+ tparm.getValue("BED_NO_DESC") + "'," + "PY1='"
				+ tparm.getValue("PY1") + "'," + "PY2='"
				+ tparm.getValue("PY2") + "'," + "DESCRIPTION='"
				+ tparm.getValue("DESCRIPTION") + "'," + "ROOM_CODE='"
				+ tparm.getValue("ROOM_CODE") + "'," + "STATION_CODE='"
				+ tparm.getValue("STATION_CODE") + "'," + "SEQ='"
				+ tparm.getValue("SEQ") + "'," + "REGION_CODE='"
				+ tparm.getValue("REGION_CODE") + "'," + "BED_CLASS_CODE='"
				+ tparm.getValue("BED_CLASS_CODE") + "'," + "BED_TYPE_CODE='"
				+ tparm.getValue("BED_TYPE_CODE") + "'," + "ACTIVE_FLG='"
				+ tparm.getValue("ACTIVE_FLG") + "'," + "APPT_FLG='"
				+ tparm.getValue("APPT_FLG") + "'," + "ALLO_FLG='"
				+ tparm.getValue("ALLO_FLG") + "'," + "BED_OCCU_FLG='"
				+ tparm.getValue("BED_OCCU_FLG") + "'," + "RESERVE_BED_FLG='"
				+ tparm.getValue("RESERVE_BED_FLG") + "'," + "SEX_CODE='"
				+ tparm.getValue("SEX_CODE") + "'," + "OCCU_RATE_FLG='"
				+ tparm.getValue("OCCU_RATE_FLG") + "'," + "DR_APPROVE_FLG='"
				+ tparm.getValue("DR_APPROVE_FLG") + "'," + "BABY_BED_FLG='"
				+ tparm.getValue("BABY_BED_FLG") + "'," + "HTL_FLG='"
				+ tparm.getValue("HTL_FLG") + "'," + "ADM_TYPE='"
				+ tparm.getValue("ADM_TYPE") + "'," + "MR_NO='"
				+ tparm.getValue("MR_NO") + "'," + "CASE_NO='"
				+ tparm.getValue("CASE_NO") + "'," + "IPD_NO='"
				+ tparm.getValue("IPD_NO") + "'," + "DEPT_CODE='"
				+ tparm.getValue("DEPT_CODE") + "'," + "BED_STATUS='"
				+ tparm.getValue("BED_STATUS") + "'," + "ENG_DESC='"
				+ tparm.getValue("ENG_DESC") + "'" + " WHERE BED_NO='"
				+ tparm.getValue("BED_NO") + "'";

		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ɾ��SYS_BED��
	 */
	public TParm OnDeleteSysBed(TParm tparm) {
		String sql = "DELETE FROM SYS_BED WHERE BED_NO ='"
				+ tparm.getValue("BED_NO") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ����SYS_BED_TYPE��
	 */
	public TParm OnSaveSysBedType(TParm tparm) {
		String sql = "INSERT INTO SYS_BED_TYPE (BED_TYPE_CODE,BEDTYPE_DESC,ENNAME,PY1,PY2,"
				+ "SEQ,DESCRIPTION,LAB_DISCNT_FLG,ISOLATION_FLG,BURN_FLG,PEDIATRIC_FLG,OBSERVATION_FLG,"
				+ "TRANSPLANT_FLG,ICU_FLG,CCU_FLG,BC_FLG,"
				+ "OPT_USER,OPT_DATE,OPT_TERM )values('"
				+ tparm.getValue("BED_TYPE_CODE")
				+ "','"
				+ tparm.getValue("BEDTYPE_DESC")
				+ "','"
				+ tparm.getValue("ENNAME")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("LAB_DISCNT_FLG")
				+ "','"
				+ tparm.getValue("ISOLATION_FLG")
				+ "','"
				+ tparm.getValue("BURN_FLG")
				+ "','"
				+ tparm.getValue("PEDIATRIC_FLG")
				+ "','"
				+ tparm.getValue("OBSERVATION_FLG")
				+ "','"
				+ tparm.getValue("TRANSPLANT_FLG")
				+ "','"
				+ tparm.getValue("ICU_FLG")
				+ "','"
				+ tparm.getValue("CCU_FLG")
				+ "','"
				+ tparm.getValue("BC_FLG")
				+ "','apserver',sysdate,'apserver')";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_BED_TYPE��
	 */
	public TParm OnUpdateSysBedType(TParm tparm) {
		String sql = "UPDATE SYS_BED_TYPE SET BEDTYPE_DESC='"
				+ tparm.getValue("BEDTYPE_DESC") + "',ENNAME='"
				+ tparm.getValue("ENNAME") + "',PY1='" + tparm.getValue("PY1")
				+ "',PY2='" + tparm.getValue("PY2") + "',SEQ='"
				+ tparm.getValue("SEQ") + "',DESCRIPTION='"
				+ tparm.getValue("DESCRIPTION") + "',LAB_DISCNT_FLG='"
				+ tparm.getValue("LAB_DISCNT_FLG") + "',ISOLATION_FLG='"
				+ tparm.getValue("ISOLATION_FLG") + "',BURN_FLG='"
				+ tparm.getValue("BURN_FLG") + "',PEDIATRIC_FLG='"
				+ tparm.getValue("PEDIATRIC_FLG") + "',OBSERVATION_FLG='"
				+ tparm.getValue("OBSERVATION_FLG") + "',TRANSPLANT_FLG='"
				+ tparm.getValue("TRANSPLANT_FLG") + "',ICU_FLG='"
				+ tparm.getValue("ICU_FLG") + "',CCU_FLG='"
				+ tparm.getValue("CCU_FLG") +

				"' WHERE BED_TYPE_CODE='" + tparm.getValue("BED_TYPE_CODE")
				+ "'";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ɾ��SYS_BED_TYPE��
	 */
	public TParm OnDeleteSysBedType(TParm tparm) {
		String sql = "DELETE FROM SYS_BED_TYPE WHERE BED_TYPE_CODE='"
				+ tparm.getValue("BED_TYPE_CODE") + "'";

		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ����SYS_CATEGORY��
	 */
	public TParm OnSaveSysCategory(TParm tparm) {
		String sql = "INSERT INTO SYS_CATEGORY (RULE_TYPE,CATEGORY_CODE,CATEGORY_CHN_DESC,CATEGORY_ENG_DESC,DETAIL_FLG,PY1,PY2,SEQ,DESCRIPTION,"
				+ "OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ tparm.getValue("RULE_TYPE")
				+ "','"
				+ tparm.getValue("CATEGORY_CODE")
				+ "','"
				+ tparm.getValue("CATEGORY_CHN_DESC")
				+ "','"
				+ tparm.getValue("CATEGORY_ENG_DESC")
				+ "','"
				+ tparm.getValue("DETAIL_FLG")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','apserver',sysdate,'apserver')";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_CATEGORY��
	 */
	public TParm OnUpdateSysCategory(TParm tparm) {
		String sql = "UPDATE SYS_CATEGORY SET DESCRIPTION ='"
				+ tparm.getValue("DESCRIPTION") + "',RULE_TYPE ='"
				+ tparm.getValue("RULE_TYPE1") + "'," + "CATEGORY_CHN_DESC='"
				+ tparm.getValue("CATEGORY_CHN_DESC") + "',"
				+ "CATEGORY_ENG_DESC='" + tparm.getValue("CATEGORY_ENG_DESC")
				+ "'," + "DETAIL_FLG='" + tparm.getValue("DETAIL_FLG")
				+ "',CATEGORY_CODE='" + tparm.getValue("CATEGORY_CODE1")
				+ "',PY1='" + tparm.getValue("PY1") + "',PY2='"
				+ tparm.getValue("PY2") + "',SEQ='" + tparm.getValue("SEQ")
				+ "'" + " WHERE CATEGORY_CODE='"
				+ tparm.getValue("CATEGORY_CODE") + "' AND RULE_TYPE='"
				+ tparm.getValue("RULE_TYPE") + "'";

		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ɾ��SYS_CATEGORY��
	 */
	public TParm OnDeleteSysCategory(TParm tparm) {
		String sql = "DELETE FROM SYS_CATEGORY WHERE CATEGORY_CODE='"
				+ tparm.getValue("CATEGORY_CODE") + "' AND RULE_TYPE='"
				+ tparm.getValue("RULE_TYPE") + "'";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_COST_CENTER��
	 */
	public TParm OnSaveSysCostCenter(TParm tparm) {
		String sql = "INSERT INTO SYS_COST_CENTER (COST_CENTER_CODE,COST_CENTER_CHN_DESC,COST_CENTER_ENG_DESC,COST_CENTER_ABS_DESC,"
				+ "PY1,PY2,SEQ,DESCRIPTION,FINAL_FLG,REGION_CODE,DEPT_GRADE,CLASSIFY,"
				+ "DEPT_CAT1,OPD_FIT_FLG,EMG_FIT_FLG,IPD_FIT_FLG,HRM_FIT_FLG,"
				+ "DEFAULT_TERM_NO,DEFAULT_PRINTER_NO,STATISTICS_FLG,ACTIVE_FLG,"
				+ "OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ tparm.getValue("COST_CENTER_CODE")
				+ "','"
				+ tparm.getValue("COST_CENTER_CHN_DESC")
				+ "','"
				+ tparm.getValue("COST_CENTER_ENG_DESC")
				+ "','"
				+ tparm.getValue("COST_CENTER_ABS_DESC")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("FINAL_FLG")
				+ "','"
				+ tparm.getValue("REGION_CODE")
				+ "','"
				+ tparm.getValue("DEPT_GRADE")
				+ "','"
				+ tparm.getValue("CLASSIFY")
				+ "','"
				+ tparm.getValue("DEPT_CAT1")
				+ "','"
				+ tparm.getValue("OPD_FIT_FLG")
				+ "','"
				+ tparm.getValue("EMG_FIT_FLG")
				+ "','"
				+ tparm.getValue("IPD_FIT_FLG")
				+ "','"
				+ tparm.getValue("HRM_FIT_FLG")
				+ "','"
				+ tparm.getValue("DEFAULT_TERM_NO")
				+ "','"
				+ tparm.getValue("DEFAULT_PRINTER_NO")
				+ "','"
				+ tparm.getValue("STATISTICS_FLG")
				+ "','"
				+ tparm.getValue("ACTIVE_FLG")

				+ "','apserver',sysdate,'apserver')";
		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ����SYS_COST_CENTER��COST_CENTER_CODE
	 */
	public TParm OnUpdateSysCostCenter(TParm tparm) {
		String sql = "UPDATE SYS_COST_CENTER SET  COST_CENTER_CHN_DESC='"
				+ tparm.getValue("COST_CENTER_CHN_DESC") + "',"
				+ "COST_CENTER_ENG_DESC='"
				+ tparm.getValue("COST_CENTER_ENG_DESC") + "',"
				+ "COST_CENTER_ABS_DESC='"
				+ tparm.getValue("COST_CENTER_ABS_DESC")
				+ "',COST_CENTER_CODE='" + tparm.getValue("COST_CENTER_CODE1")
				+ "'," + "PY1='" + tparm.getValue("PY1") + "'," + "PY2='"
				+ tparm.getValue("PY2") + "'," + "SEQ='"
				+ tparm.getValue("SEQ") + "'," + "DESCRIPTION='"
				+ tparm.getValue("DESCRIPTION") + "'," + "FINAL_FLG='"
				+ tparm.getValue("FINAL_FLG") + "'," + "REGION_CODE='"
				+ tparm.getValue("REGION_CODE") + "'," + "DEPT_GRADE='"
				+ tparm.getValue("DEPT_GRADE") + "'," + "CLASSIFY='"
				+ tparm.getValue("CLASSIFY") + "'," + "DEPT_CAT1='"
				+ tparm.getValue("DEPT_CAT1") + "'," + "OPD_FIT_FLG='"
				+ tparm.getValue("OPD_FIT_FLG") + "'," + "EMG_FIT_FLG='"
				+ tparm.getValue("EMG_FIT_FLG") + "'," + "IPD_FIT_FLG='"
				+ tparm.getValue("IPD_FIT_FLG") + "'," + "HRM_FIT_FLG='"
				+ tparm.getValue("HRM_FIT_FLG") + "'," + "DEFAULT_TERM_NO='"
				+ tparm.getValue("DEFAULT_TERM_NO") + "',"
				+ "DEFAULT_PRINTER_NO='" + tparm.getValue("DEFAULT_PRINTER_NO")
				+ "'," + "STATISTICS_FLG='" + tparm.getValue("STATISTICS_FLG")
				+ "'," + "ACTIVE_FLG='" + tparm.getValue("ACTIVE_FLG") + "'"
				+ " WHERE COST_CENTER_CODE='"
				+ tparm.getValue("COST_CENTER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ɾ��SYS_COST_CENTER��
	 */
	public TParm OnDeleteSysCostCenter(TParm tparm) {
		String sql = "DELETE FROM SYS_COST_CENTER  WHERE COST_CENTER_CODE='"
				+ tparm.getValue("COST_CENTER_CODE") + "'";

		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ����SYS_CTZ��
	 */
	public TParm OnSaveSysCtz(TParm tparm) {
		String sql = "INSERT INTO SYS_CTZ (CTZ_CODE,CTZ_DESC,ENG_DESC,DESCRIPT,PY1,PY2,SEQ,"
				+ "NHI_COMPANY_CODE,MAIN_CTZ_FLG,NHI_CTZ_FLG,MRCTZ_UPD_FLG,DEF_CTZ_FLG,"
				+ "MRO_CTZ,STA2_CODE,STA1_CODE,NHI_NO,"
				+ "OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ tparm.getValue("CTZ_CODE")
				+ "','"
				+ tparm.getValue("CTZ_DESC")
				+ "','"
				+ tparm.getValue("ENG_DESC")
				+ "','"
				+ tparm.getValue("DESCRIPT")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','"
				+ tparm.getValue("NHI_COMPANY_CODE")
				+ "','"
				+ tparm.getValue("MAIN_CTZ_FLG")
				+ "','"
				+ tparm.getValue("NHI_CTZ_FLG")
				+ "','"
				+ tparm.getValue("MRCTZ_UPD_FLG")
				+ "','"
				+ tparm.getValue("DEF_CTZ_FLG")
				+ "','"
				+ tparm.getValue("MRO_CTZ")
				+ "','"
				+ tparm.getValue("STA2_CODE")
				+ "','"
				+ tparm.getValue("STA1_CODE")
				+ "','"
				+ tparm.getValue("NHI_NO")

				+ "','apserver',sysdate,'apserver')";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_CTZ��
	 */
	public TParm OnUpdateSysCtz(TParm tparm) {
		String sql = "UPDATE SYS_CTZ SET  CTZ_DESC='"
				+ tparm.getValue("CTZ_DESC") + "'," + "ENG_DESC='"
				+ tparm.getValue("ENG_DESC") + "'," + "DESCRIPT='"
				+ tparm.getValue("DESCRIPT") + "'," + "CTZ_CODE='"
				+ tparm.getValue("CTZ_CODE1") + "'," + "PY1='"
				+ tparm.getValue("PY1") + "'," + "PY2='"
				+ tparm.getValue("PY2") + "'," + "SEQ='"
				+ tparm.getValue("SEQ") + "'," + "NHI_COMPANY_CODE='"
				+ tparm.getValue("NHI_COMPANY_CODE") + "'," + "MAIN_CTZ_FLG='"
				+ tparm.getValue("MAIN_CTZ_FLG") + "'," + "NHI_CTZ_FLG='"
				+ tparm.getValue("NHI_CTZ_FLG") + "'," + "MRCTZ_UPD_FLG='"
				+ tparm.getValue("MRCTZ_UPD_FLG") + "'," + "DEF_CTZ_FLG='"
				+ tparm.getValue("DEF_CTZ_FLG") + "'," + "MRO_CTZ='"
				+ tparm.getValue("MRO_CTZ") + "'," + "STA2_CODE='"
				+ tparm.getValue("STA2_CODE") + "'," + "STA1_CODE='"
				+ tparm.getValue("STA1_CODE") + "'," + "NHI_NO='"
				+ tparm.getValue("NHI_NO") + "'" +

				" WHERE CTZ_CODE='" + tparm.getValue("CTZ_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ɾ��SYS_CTZ��
	 */
	public TParm OnDeleteSysCtz(TParm tparm) {
		String sql = "DELETE FROM SYS_CTZ WHERE CTZ_CODE='"
				+ tparm.getValue("CTZ_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_CTRLDRUGCLASS��
	 */
	public TParm OnSaveSysCtrldrugclass(TParm tparm) {
		String sql = "INSERT INTO SYS_CTRLDRUGCLASS (CTRLDRUGCLASS_CODE,CTRLDRUGCLASS_CHN_DESC,CTRLDRUGCLASS_ENG_DESC,TAKE_DAYS,"
				+ "PY1,PY2,SEQ,DESCRIPTION,PRN_TYPE_CODE,PRN_TYPE_DESC,PRNSPCFORM_FLG,CTRL_FLG,NARCOTIC_FLG,"
				+ "TOXICANT_FLG,PSYCHOPHA1_FLG,RADIA_FLG,TEST_DRUG_FLG,ANTISEPTIC_FLG,ANTIBIOTIC_FLG,TPN_FLG,PSYCHOPHA2_FLG,"
				+ "OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ tparm.getValue("CTRLDRUGCLASS_CODE")
				+ "','"
				+ tparm.getValue("CTRLDRUGCLASS_CHN_DESC")
				+ "','"
				+ tparm.getValue("CTRLDRUGCLASS_ENG_DESC")
				+ "','"
				+ tparm.getValue("TAKE_DAYS")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("PRN_TYPE_CODE")
				+ "','"
				+ tparm.getValue("PRN_TYPE_DESC")
				+ "','"
				+ tparm.getValue("PRNSPCFORM_FLG")
				+ "','"
				+ tparm.getValue("CTRL_FLG")
				+ "','"
				+ tparm.getValue("NARCOTIC_FLG")
				+ "','"
				+ tparm.getValue("TOXICANT_FLG")
				+ "','"
				+ tparm.getValue("PSYCHOPHA1_FLG")
				+ "','"
				+ tparm.getValue("RADIA_FLG")
				+ "','"
				+ tparm.getValue("TEST_DRUG_FLG")
				+ "','"
				+ tparm.getValue("ANTISEPTIC_FLG")
				+ "','"
				+ tparm.getValue("ANTIBIOTIC_FLG")
				+ "','"
				+ tparm.getValue("TPN_FLG")
				+ "','"
				+ tparm.getValue("PSYCHOPHA2_FLG")

				+ "','apserver',sysdate,'apserver')";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_CTRLDRUGCLASS��
	 */
	public TParm OnUpdateSysCtrldrugclass(TParm tparm) {
		String sql = "UPDATE SYS_CTRLDRUGCLASS SET  CTRLDRUGCLASS_CHN_DESC='"
				+ tparm.getValue("CTRLDRUGCLASS_CHN_DESC") + "',"
				+ "CTRLDRUGCLASS_ENG_DESC='"
				+ tparm.getValue("CTRLDRUGCLASS_ENG_DESC") + "',"
				+ "TAKE_DAYS='" + tparm.getValue("TAKE_DAYS")
				+ "',CTRLDRUGCLASS_CODE='"
				+ tparm.getValue("CTRLDRUGCLASS_CODE1") + "'," + "PY1='"
				+ tparm.getValue("PY1") + "'," + "PY2='"
				+ tparm.getValue("PY2") + "'," + "SEQ='"
				+ tparm.getValue("SEQ") + "'," + "DESCRIPTION='"
				+ tparm.getValue("DESCRIPTION") + "'," + "PRN_TYPE_CODE='"
				+ tparm.getValue("PRN_TYPE_CODE") + "'," + "PRN_TYPE_DESC='"
				+ tparm.getValue("PRN_TYPE_DESC") + "'," + "PRNSPCFORM_FLG='"
				+ tparm.getValue("PRNSPCFORM_FLG") + "'," + "CTRL_FLG='"
				+ tparm.getValue("CTRL_FLG") + "'," + "NARCOTIC_FLG='"
				+ tparm.getValue("NARCOTIC_FLG") + "'," + "TOXICANT_FLG='"
				+ tparm.getValue("TOXICANT_FLG") + "'," + "PSYCHOPHA1_FLG='"
				+ tparm.getValue("PSYCHOPHA1_FLG") + "'," + "RADIA_FLG='"
				+ tparm.getValue("RADIA_FLG") + "'," + "TEST_DRUG_FLG='"
				+ tparm.getValue("TEST_DRUG_FLG") + "'," + "ANTISEPTIC_FLG='"
				+ tparm.getValue("ANTISEPTIC_FLG") + "'," + "ANTIBIOTIC_FLG='"
				+ tparm.getValue("ANTIBIOTIC_FLG") + "'," + "TPN_FLG='"
				+ tparm.getValue("TPN_FLG") + "'," + "PSYCHOPHA2_FLG='"
				+ tparm.getValue("PSYCHOPHA2_FLG") + "'"
				+ " WHERE CTRLDRUGCLASS_CODE='"
				+ tparm.getValue("CTRLDRUGCLASS_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ɾ��SYS_CTRLDRUGCLASS��
	 */
	public TParm OnDeleteSysCtrldrugclass(TParm tparm) {
		String sql = "DELETE FROM SYS_CTRLDRUGCLASS WHERE CTRLDRUGCLASS_CODE='"
				+ tparm.getValue("CTRLDRUGCLASS_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_DIAGNOSIS��
	 */
	public TParm OnSaveSysDiagnosis(TParm tparm) {
		String sql = "INSERT INTO SYS_DIAGNOSIS (ICD_TYPE,ICD_CODE,ICD_CHN_DESC,ICD_ENG_DESC,MAIN_DIAG_FLG,"
				+ "PY1,PY2,SEQ,DESCRIPTION,SYNDROME_FLG,MDC_CODE,"
				+ "CCMD_CODE,CAT_FLG,STANDARD_DAYS,CHLR_FLG,DISEASETYPE_CODE,"
				+ "MR_CODE,CHRONIC_FLG,START_AGE,LIMIT_DEPT_CODE,LIMIT_SEX_CODE,END_AGE,AVERAGE_FEE,STA2_CODE,STA1_CODE,"
				+ "OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ tparm.getValue("ICD_TYPE")
				+ "','"
				+ tparm.getValue("ICD_CODE")
				+ "','"
				+ tparm.getValue("ICD_CHN_DESC")
				+ "','"
				+ tparm.getValue("ICD_ENG_DESC")
				+ "','"
				+ tparm.getValue("MAIN_DIAG_FLG")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("SYNDROME_FLG")
				+ "','"
				+ tparm.getValue("MDC_CODE")
				+ "','"
				+ tparm.getValue("CCMD_CODE")
				+ "','"
				+ tparm.getValue("CAT_FLG")
				+ "','"
				+ tparm.getValue("STANDARD_DAYS")
				+ "','"
				+ tparm.getValue("CHLR_FLG")
				+ "','"
				+ tparm.getValue("DISEASETYPE_CODE")
				+ "','"
				+ tparm.getValue("MR_CODE")
				+ "','"
				+ tparm.getValue("CHRONIC_FLG")
				+ "','"
				+ tparm.getValue("START_AGE")
				+ "','"
				+ tparm.getValue("LIMIT_DEPT_CODE")
				+ "','"
				+ tparm.getValue("LIMIT_SEX_CODE")
				+ "','"
				+ tparm.getValue("END_AGE")
				+ "','"
				+ tparm.getValue("AVERAGE_FEE")
				+ "','"
				+ tparm.getValue("STA2_CODE")
				+ "','"
				+ tparm.getValue("STA1_CODE")

				+ "','apserver',sysdate,'apserver')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_DIAGNOSIS��
	 */
	public TParm OnUpdateSysDiagnosis(TParm tparm) {
		String sql = "UPDATE SYS_DIAGNOSIS SET  ICD_TYPE='"
				+ tparm.getValue("ICD_TYPE1") + "'," + "ICD_CHN_DESC='"
				+ tparm.getValue("ICD_CHN_DESC") + "'," + "ICD_ENG_DESC='"
				+ tparm.getValue("ICD_ENG_DESC") + "'," + "MAIN_DIAG_FLG ='"
				+ tparm.getValue("MAIN_DIAG_FLG") + "',ICD_CODE='"
				+ tparm.getValue("ICD_CODE1") + "'," + "PY1 ='"
				+ tparm.getValue("PY1") + "'," + "PY2 ='"
				+ tparm.getValue("PY2") + "'," + "SEQ ='"
				+ tparm.getValue("SEQ") + "'," + "DESCRIPTION ='"
				+ tparm.getValue("DESCRIPTION") + "'," + "SYNDROME_FLG ='"
				+ tparm.getValue("SYNDROME_FLG") + "'," + "MDC_CODE ='"
				+ tparm.getValue("MDC_CODE") + "'," + "CCMD_CODE ='"
				+ tparm.getValue("CCMD_CODE") + "'," + "CAT_FLG ='"
				+ tparm.getValue("CAT_FLG") + "'," + "STANDARD_DAYS ='"
				+ tparm.getValue("STANDARD_DAYS") + "'," + "CHLR_FLG ='"
				+ tparm.getValue("CHLR_FLG") + "'," + "DISEASETYPE_CODE ='"
				+ tparm.getValue("DISEASETYPE_CODE") + "'," + "MR_CODE ='"
				+ tparm.getValue("MR_CODE") + "'," + "CHRONIC_FLG ='"
				+ tparm.getValue("CHRONIC_FLG") + "'," + "START_AGE ='"
				+ tparm.getValue("START_AGE") + "'," + "LIMIT_DEPT_CODE ='"
				+ tparm.getValue("LIMIT_DEPT_CODE") + "',"
				+ "LIMIT_SEX_CODE ='" + tparm.getValue("LIMIT_SEX_CODE") + "',"
				+ "END_AGE ='" + tparm.getValue("END_AGE") + "',"
				+ "AVERAGE_FEE ='" + tparm.getValue("AVERAGE_FEE") + "',"
				+ "STA2_CODE ='" + tparm.getValue("STA2_CODE") + "',"
				+ "STA1_CODE ='" + tparm.getValue("STA1_CODE") + "'"
				+ " WHERE ICD_CODE='" + tparm.getValue("ICD_CODE")
				+ "' AND ICD_TYPE='" + tparm.getValue("ICD_TYPE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ɾ��SYS_DIAGNOSIS��
	 */
	public TParm OnDeleteSysDiagnosis(TParm tparm) {
		String sql = "DELETE FROM SYS_DIAGNOSIS WHERE ICD_CODE='"
				+ tparm.getValue("ICD_CODE") + "' AND ICD_TYPE='"
				+ tparm.getValue("ICD_TYPE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_MANUFACTURER��
	 */
	public TParm OnSaveSysManufacturer(TParm tparm) {
		String sql = "INSERT INTO SYS_MANUFACTURER (MAN_CODE,MAN_CHN_DESC,MAN_ENG_DESC,MAN_ABS_DESC,"
				+ "PY1,PY2,SEQ,DESCRIPTION,NATIONAL_CODE,POST_CODE,"
				+ "ADDRESS,TEL,FAX,WEBSITE,E_MAIL,PHA_FLG,"
				+ "MAT_FLG,DEV_FLG,OTHER_FLG,"
				+ "OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ tparm.getValue("MAN_CODE")
				+ "','"
				+ tparm.getValue("MAN_CHN_DESC")
				+ "','"
				+ tparm.getValue("MAN_ENG_DESC")
				+ "','"
				+ tparm.getValue("MAN_ABS_DESC")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("NATIONAL_CODE")
				+ "','"
				+ tparm.getValue("POST_CODE")
				+ "','"
				+ tparm.getValue("ADDRESS")
				+ "','"
				+ tparm.getValue("TEL")
				+ "','"
				+ tparm.getValue("FAX")
				+ "','"
				+ tparm.getValue("WEBSITE")
				+ "','"
				+ tparm.getValue("E_MAIL")
				+ "','"
				+ tparm.getValue("PHA_FLG")
				+ "','"
				+ tparm.getValue("MAT_FLG")
				+ "','"
				+ tparm.getValue("DEV_FLG")
				+ "','" + tparm.getValue("OTHER_FLG")

				+ "','apserver',sysdate,'apserver')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_MANUFACTURER��
	 */
	public TParm OnUpdateSysManufacturer(TParm tparm) {
		String sql = "UPDATE SYS_MANUFACTURER SET  MAN_CHN_DESC='"
				+ tparm.getValue("MAN_CHN_DESC") + "'," + "MAN_ENG_DESC='"
				+ tparm.getValue("MAN_ENG_DESC") + "'," + "MAN_ABS_DESC='"
				+ tparm.getValue("MAN_ABS_DESC") + "',MAN_CODE='"
				+ tparm.getValue("MAN_CODE1") + "'," + "PY1='"
				+ tparm.getValue("PY1") + "'," + "PY2='"
				+ tparm.getValue("PY2") + "'," + "SEQ='"
				+ tparm.getValue("SEQ") + "'," + "DESCRIPTION='"
				+ tparm.getValue("DESCRIPTION") + "'," + "NATIONAL_CODE='"
				+ tparm.getValue("NATIONAL_CODE") + "'," + "POST_CODE='"
				+ tparm.getValue("POST_CODE") + "'," + "ADDRESS='"
				+ tparm.getValue("ADDRESS") + "'," + "TEL='"
				+ tparm.getValue("TEL") + "'," + "FAX='"
				+ tparm.getValue("FAX") + "'," + "WEBSITE='"
				+ tparm.getValue("WEBSITE") + "'," + "E_MAIL='"
				+ tparm.getValue("E_MAIL") + "'," + "PHA_FLG='"
				+ tparm.getValue("PHA_FLG") + "'," + "MAT_FLG='"
				+ tparm.getValue("MAT_FLG") + "'," + "DEV_FLG='"
				+ tparm.getValue("DEV_FLG") + "'," + "OTHER_FLG='"
				+ tparm.getValue("OTHER_FLG") + "'" +

				" WHERE MAN_CODE='" + tparm.getValue("MAN_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ɾ��SYS_MANUFACTURER��
	 */
	public TParm OnDeleteSysManufacturer(TParm tparm) {
		String sql = "DELETE FROM SYS_MANUFACTURER WHERE MAN_CODE='"
				+ tparm.getValue("MAN_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_POSITION��
	 */
	public TParm OnSaveSysPosition(TParm tparm) {
		String sql = "INSERT INTO SYS_POSITION (POS_CODE,POS_CHN_DESC,POS_ENG_DESC,DESCRIPTION,"
				+ "PY1,PY2,SEQ,POS_TYPE,"
				+ "OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ tparm.getValue("POS_CODE")
				+ "','"
				+ tparm.getValue("POS_CHN_DESC")
				+ "','"
				+ tparm.getValue("POS_ENG_DESC")
				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','" + tparm.getValue("POS_TYPE")

				+ "','apserver',sysdate,'apserver')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_POSITION��
	 */
	public TParm OnUpdateSysPosition(TParm tparm) {
		String sql = "UPDATE SYS_POSITION SET  POS_CHN_DESC='"
				+ tparm.getValue("POS_CHN_DESC") + "'," + "POS_ENG_DESC='"
				+ tparm.getValue("POS_ENG_DESC") + "'," + "DESCRIPTION='"
				+ tparm.getValue("DESCRIPTION") + "',POS_CODE='"
				+ tparm.getValue("POS_CODE1") + "'," + "PY1='"
				+ tparm.getValue("PY1") + "'," + "PY2='"
				+ tparm.getValue("PY2") + "'," + "SEQ='"
				+ tparm.getValue("SEQ") + "'," + "POS_TYPE='"
				+ tparm.getValue("POS_TYPE") + "'" + " WHERE POS_CODE='"
				+ tparm.getValue("POS_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ɾ��SYS_POSITION��
	 */
	public TParm OnDeleteSysPosition(TParm tparm) {
		String sql = "DELETE FROM SYS_POSITION WHERE POS_CODE='"
				+ tparm.getValue("POS_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_ROOM��
	 */
	public TParm OnSaveSysRoom(TParm tparm) {
		String sql = "INSERT INTO SYS_ROOM (ROOM_CODE,ROOM_DESC,STATION_CODE,REGION_CODE,SEX_LIMIT_FLG,ENG_DESC,"
				+ "PY1,PY2,SEQ,DESCRIPT,RED_SIGN,YELLOW_SIGN,"
				+ "OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ tparm.getValue("ROOM_CODE")
				+ "','"
				+ tparm.getValue("ROOM_DESC")
				+ "','"
				+ tparm.getValue("STATION_CODE")
				+ "','"
				+ tparm.getValue("REGION_CODE")
				+ "','"
				+ tparm.getValue("SEX_LIMIT_FLG")
				+ "','"
				+ tparm.getValue("ENG_DESC")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','"
				+ tparm.getValue("DESCRIPT")
				+ "','"
				+ tparm.getValue("RED_SIGN")
				+ "','"
				+ tparm.getValue("YELLOW_SIGN")

				+ "','apserver',sysdate,'apserver')";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_ROOM��
	 */

	public TParm OnUpdateSysRoom(TParm tparm) {
		String sql = "UPDATE SYS_ROOM SET ROOM_DESC='"
				+ tparm.getValue("ROOM_DESC") + "', STATION_CODE='"
				+ tparm.getValue("STATION_CODE") + "'," + "REGION_CODE='"
				+ tparm.getValue("REGION_CODE") + "'," + "SEX_LIMIT_FLG='"
				+ tparm.getValue("SEX_LIMIT_FLG") + "', ENG_DESC='"
				+ tparm.getValue("ENG_DESC") + "',ROOM_CODE='"
				+ tparm.getValue("ROOM_CODE1") + "'," + "PY1='"
				+ tparm.getValue("PY1") + "'," + "PY2='"
				+ tparm.getValue("PY2") + "'," + "SEQ='"
				+ tparm.getValue("SEQ") + "'," + "DESCRIPT='"
				+ tparm.getValue("DESCRIPT") + "'," + "RED_SIGN='"
				+ tparm.getValue("RED_SIGN") + "'," + "YELLOW_SIGN='"
				+ tparm.getValue("YELLOW_SIGN") + "'" + " WHERE ROOM_CODE='"
				+ tparm.getValue("ROOM_CODE") + "'";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ɾ��SYS_ROOM��
	 */
	public TParm OnDeleteSysRoom(TParm tparm) {
		String sql = "DELETE FROM SYS_ROOM WHERE ROOM_CODE ='"
				+ tparm.getValue("ROOM_CODE") + "'";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_STATION��
	 */
	public TParm OnSaveSysStation(TParm tparm) {
		String sql = "INSERT INTO SYS_STATION (STATION_CODE,STATION_DESC,ENG_DESC,DEPT_CODE,REGION_CODE,ORG_CODE,"
				+ "PY1,PY2,SEQ,DESCRIPTION,LOC_CODE,PRINTER_NO,"
				+ "TEL_EXT,COST_CENTER_CODE,STA2_CODE,STA1_CODE,"
				+ "MACHINENO,ATC_TYPE,"
				+ "OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ tparm.getValue("STATION_CODE")
				+ "','"
				+ tparm.getValue("STATION_DESC")
				+ "','"
				+ tparm.getValue("ENG_DESC")
				+ "','"
				+ tparm.getValue("DEPT_CODE")
				+ "','"
				+ tparm.getValue("REGION_CODE")
				+ "','"
				+ tparm.getValue("ORG_CODE")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "','"
				+ tparm.getValue("SEQ")
				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("LOC_CODE")
				+ "','"
				+ tparm.getValue("PRINTER_NO")
				+ "','"
				+ tparm.getValue("TEL_EXT")
				+ "','"
				+ tparm.getValue("COST_CENTER_CODE")
				+ "','"
				+ tparm.getValue("STA2_CODE")
				+ "','"
				+ tparm.getValue("STA1_CODE")
				+ "','"
				+ tparm.getValue("MACHINENO")
				+ "','"
				+ tparm.getValue("ATC_TYPE")

				+ "','apserver',sysdate,'apserver')";

		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ����SYS_STATION��
	 */
	public TParm OnUpdateSysStation(TParm tparm) {
		String sql = "UPDATE SYS_STATION SET  STATION_DESC='"
				+ tparm.getValue("STATION_DESC") + "'," + "ENG_DESC='"
				+ tparm.getValue("ENG_DESC") + "'," + "DEPT_CODE='"
				+ tparm.getValue("DEPT_CODE") + "', REGION_CODE='"
				+ tparm.getValue("REGION_CODE") + "',ORG_CODE='"
				+ tparm.getValue("ORG_CODE") + "'," + "PY1='"
				+ tparm.getValue("PY1") + "'," + "PY2='"
				+ tparm.getValue("PY2") + "'," + "SEQ='"
				+ tparm.getValue("SEQ") + "'," + "DESCRIPTION='"
				+ tparm.getValue("DESCRIPTION") + "'," + "LOC_CODE='"
				+ tparm.getValue("LOC_CODE") + "'," + "PRINTER_NO='"
				+ tparm.getValue("PRINTER_NO") + "'," + "TEL_EXT='"
				+ tparm.getValue("TEL_EXT") + "'," + "COST_CENTER_CODE='"
				+ tparm.getValue("COST_CENTER_CODE") + "'," + "STA2_CODE='"
				+ tparm.getValue("STA2_CODE") + "'," + "STA1_CODE='"
				+ tparm.getValue("STA1_CODE") + "'," + "MACHINENO='"
				+ tparm.getValue("MACHINENO") + "'," + "ATC_TYPE='"
				+ tparm.getValue("ATC_TYPE") + "'" + " WHERE STATION_CODE='"
				+ tparm.getValue("STATION_CODE")

				+ "'";

		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/*
	 * ɾ��SYS_STATION��
	 */
	public TParm OnDeleteSysStation(TParm tparm) {
		String sql = "DELETE FROM SYS_STATION WHERE STATION_CODE='"
				+ tparm.getValue("STATION_CODE") + "'";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_ANTIBIOTIC��
	 */
	public TParm OnSaveSysAntibiotic(TParm tparm) {
		String sql = "INSERT INTO SYS_ANTIBIOTIC (ANTIBIOTIC_CODE,ANTIBIOTIC_DESC,ENG_DESC,TAKE_DAYS,PY1,PY2,"
				+ "DESCRIPTION,MR_CODE,"
				+ "OPT_USER,OPT_DATE,OPT_TERM) VALUES('"
				+ tparm.getValue("ANTIBIOTIC_CODE")
				+ "','"
				+ tparm.getValue("ANTIBIOTIC_DESC")
				+ "','"
				+ tparm.getValue("ENG_DESC")
				+ "','"
				+ tparm.getValue("TAKE_DAYS")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")

				+ "','"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("MR_CODE")

				+ "','apserver',sysdate,'apserver')";

		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ����SYS_ANTIBIOTIC��
	 */

	public TParm OnUpdateSysAntibiotic(TParm tparm) {
		String sql = "UPDATE SYS_ANTIBIOTIC SET  PY1='" + tparm.getValue("PY1")
				+ "',ANTIBIOTIC_DESC='" + tparm.getValue("ANTIBIOTIC_DESC")
				+ "'," + "ENG_DESC='" + tparm.getValue("ENG_DESC")
				+ "', TAKE_DAYS='" + tparm.getValue("TAKE_DAYS") + "',PY2='"
				+ tparm.getValue("PY2") + "'," + "DESCRIPTION='"
				+ tparm.getValue("DESCRIPTION") + "'," + "MR_CODE='"
				+ tparm.getValue("MR_CODE")

				+ "'" + " WHERE ANTIBIOTIC_CODE='"
				+ tparm.getValue("ANTIBIOTIC_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ɾ��SYS_ANTIBIOTIC��
	 */
	public TParm OnDeleteSysAntibiotic(TParm tparm) {
		String sql = "DELETE FROM SYS_ANTIBIOTIC WHERE ANTIBIOTIC_CODE='"
				+ tparm.getValue("ANTIBIOTIC_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * IND_AGENT��ת��
	 */
	public TParm OnIndAgent(TParm parm) {
		String sql = "SELECT ORDER_CODE FROM SYS_FEE_SPC WHERE REGION_CODE='"
				+ parm.getValue("REGION_CODE") + "'" + "AND HIS_ORDER_CODE='"
				+ parm.getValue("HIS_ORDER_CODE", 0) + "' AND ACTIVE_FLG='Y'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * IND_AGENT���UPDATE
	 * 
	 * @author shendr
	 */
	public TParm OnUpdateInd(TParm parm) {
		String sql = "UPDATE IND_AGENT SET CONTRACT_PRICE="
				+ parm.getDouble("CONTRACT_PRICE") + ",LAST_VERIFY_PRICE ="
				+ parm.getDouble("LAST_VERIFY_PRICE") + ",MAIN_FLG='"
				+ parm.getValue("MAIN_FLG") + "',LAST_VERIFY_DATE="
				+ format(parm.getValue("LAST_VERIFY_DATE")) + ",OPT_USER='"
				+ parm.getValue("OPT_USER") + "',OPT_DATE="
				+ format(parm.getValue("OPT_DATE")) + ",OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "' WHERE  ORDER_CODE='"
				+ parm.getValue("ORDER_CODE", 0) + "' AND SUP_CODE='"
				+ parm.getValue("SUP_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * IND_AGENT���INSERT
	 * 
	 * @author shendr
	 */
	public TParm OnInsertInd(TParm parm) {
		String sql = "INSERT INTO IND_AGENT(SUP_CODE,ORDER_CODE,MAIN_FLG,CONTRACT_PRICE, "
				+ "LAST_VERIFY_DATE,LAST_VERIFY_PRICE,OPT_USER,OPT_DATE,OPT_TERM) "
				+ "VALUES('"
				+ parm.getValue("SUP_CODE")
				+ "','"
				+ parm.getValue("ORDER_CODE")
				+ "','"
				+ parm.getValue("MAIN_FLG")
				+ "',"
				+ parm.getDouble("CONTRACT_PRICE")
				+ ","
				+ format(parm.getValue("LAST_VERIFY_DATE"))
				+ ","
				+ parm.getDouble("LAST_VERIFY_PRICE")
				+ ",'"
				+ parm.getValue("OPT_USER")
				+ "',"
				+ format(parm.getValue("OPT_DATE"))
				+ ",'"
				+ parm.getValue("OPT_TERM") + "')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * IND_AGENT���DELETE
	 * 
	 * @author shendr
	 */
	public TParm OnDeleteInd(TParm parm) {
		String sql = "DELETE FROM IND_AGENT " + "WHERE SUP_CODE = '"
				+ parm.getValue("SUP_CODE") + "' " + "AND ORDER_CODE = '"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	//<------ SYS_FEE_SPC ������ identity by shendr 20131104
	/*
	 * ��ѯ���޶�Ӧ������������
	 */
	// public TParm queryOrderCode(TParm parm) {
	// String sql = "SELECT ORDER_CODE FROM SYS_FEE_SPC WHERE HIS_ORDER_CODE='"
	// + parm.getValue("ORDER_CODE") + "'";
	// return new TParm(TJDODBTool.getInstance().select(sql));
	// }
	// ----->

	/*
	 * ��ѯ��������������IND_AGENT���Ƿ����
	 */
	public TParm queryIndAgentByOrderCode(TParm parm) {
		String sql = "SELECT SUP_CODE FROM IND_AGENT WHERE SUP_CODE='"
				+ parm.getValue("SUP_CODE") + "' AND ORDER_CODE='"
				+ parm.getValue("ORDER_CODE", 0) + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * ��ѯ��������������PHA_TRANSUNIT���Ƿ����
	 */
	public TParm queryPhaTransunitByOrderCode(TParm parm) {
		String sql = "SELECT COUNT(ORDER_CODE) FROM PHA_TRANSUNIT "
				+ "WHERE ORDER_CODE = '" + parm.getValue("ORDER_CODE", 0) + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * SYS_FEE���UPDATE
	 * 
	 * @author shendr 2013.08.28
	 */
	public TParm OnUpdateFee(TParm parm) {
		String sql = "UPDATE SYS_FEE SET ORDER_DESC='"
				+ parm.getValue("ORDER_DESC") + "',PY1='"
				+ parm.getValue("PY1") + "',PY2='" + parm.getValue("PY2")
				+ "',SEQ=" + parm.getDouble("SEQ") + ", " + "TRADE_ENG_DESC='"
				+ parm.getValue("TRADE_ENG_DESC") + "',GOODS_DESC='"
				+ parm.getValue("GOODS_DESC") + "',GOODS_PYCODE='"
				+ parm.getValue("GOODS_PYCODE") + "',ALIAS_DESC='"
				+ parm.getValue("ALIAS_DESC") + "',ALIAS_PYCODE='"
				+ parm.getValue("ALIAS_PYCODE") + "', " + "SPECIFICATION='"
				+ parm.getValue("SPECIFICATION") + "',MAN_CODE='"
				+ parm.getValue("MAN_CODE") + "',ORDER_CAT1_CODE='"
				+ parm.getValue("ORDER_CAT1_CODE") + "',OWN_PRICE="
				+ parm.getDouble("OWN_PRICE") + ",CTRL_FLG='"
				+ parm.getValue("CTRL_FLG") + "', " + "CAT1_TYPE='"
				+ parm.getValue("CAT1_TYPE") + "',ACTIVE_FLG='"
				+ parm.getValue("ACTIVE_FLG") + "',OPT_USER='"
				+ parm.getValue("OPT_USER") + "',OPT_DATE="
				+ format(parm.getValue("OPT_DATE")) + ",OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "' " + "WHERE ORDER_CODE = '"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * SYS_FEE���INSERT
	 * 
	 * @author shendr 2013.08.28
	 */
	public TParm OnInsertFee(TParm parm) {
		String sql = "INSERT INTO SYS_FEE (ORDER_CODE,ORDER_DESC,PY1,PY2,SEQ, "
				+ "TRADE_ENG_DESC,GOODS_DESC,GOODS_PYCODE,ALIAS_DESC,ALIAS_PYCODE, "
				+ "SPECIFICATION,MAN_CODE,ORDER_CAT1_CODE,OWN_PRICE,CTRL_FLG, "
				+ "CAT1_TYPE,ACTIVE_FLG,OPT_USER,OPT_DATE,OPT_TERM) "
				+ "VALUES('"
				+ parm.getValue("ORDER_CODE")
				+ "','"
				+ parm.getValue("ORDER_DESC")
				+ "','"
				+ parm.getValue("PY1")
				+ "','"
				+ parm.getValue("PY2")
				+ "',"
				+ parm.getDouble("SEQ")
				+ ",'"
				+ parm.getValue("TRADE_ENG_DESC")
				+ "','"
				+ parm.getValue("GOODS_DESC")
				+ "','"
				+ parm.getValue("GOODS_PYCODE")
				+ "','"
				+ parm.getValue("ALIAS_DESC")
				+ "','"
				+ parm.getValue("ALIAS_PYCODE")
				+ "','"
				+ parm.getValue("SPECIFICATION")
				+ "','"
				+ parm.getValue("MAN_CODE")
				+ "','"
				+ parm.getValue("ORDER_CAT1_CODE")
				+ "',"
				+ parm.getDouble("OWN_PRICE")
				+ ",'"
				+ parm.getValue("CTRL_FLG")
				+ "','"
				+ parm.getValue("CAT1_TYPE")
				+ "','"
				+ parm.getValue("ACTIVE_FLG")
				+ "','"
				+ parm.getValue("OPT_USER")
				+ "',"
				+ format(parm.getValue("OPT_DATE"))
				+ ",'"
				+ parm.getValue("OPT_TERM") + "')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * SYS_FEE���DELETE
	 * 
	 * @author shendr 2013.08.28
	 */
	public TParm OnDeleteFee(TParm parm) {
		String sql = "DELETE FROM SYS_FEE WHERE ORDER_CODE = '"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ��ѯ��������������SYS_FEE���Ƿ����
	 */
	public TParm querySysFeeByOrderCode(TParm parm) {
		String sql = "SELECT ORDER_CODE FROM SYS_FEE WHERE ORDER_CODE='"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * PHA_BASE���UPDATE
	 * 
	 * @author shendr 2013.08.28
	 */
	public TParm OnUpdateBase(TParm parm) {
		String sql = "UPDATE PHA_BASE SET ORDER_DESC='"
				+ parm.getValue("ORDER_DESC") + "',GOODS_DESC='"
				+ parm.getValue("GOODS_DESC") + "',ALIAS_DESC='"
				+ parm.getValue("ALIAS_DESC") + "',SPECIFICATION='"
				+ parm.getValue("SPECIFICATION") + "',MAN_CHN_DESC='"
				+ parm.getValue("MAN_CHN_DESC") + "'," + "PHA_TYPE='"
				+ parm.getValue("PHA_TYPE") + "',TYPE_CODE='"
				+ parm.getValue("TYPE_CODE") + "',DOSE_CODE='"
				+ parm.getValue("DOSE_CODE") + "',FREQ_CODE='"
				+ parm.getValue("FREQ_CODE") + "',ROUTE_CODE='"
				+ parm.getValue("ROUTE_CODE") + "', " + "TAKE_DAYS="
				+ parm.getInt("TAKE_DAYS") + ",MEDI_QTY="
				+ parm.getDouble("MEDI_QTY") + ",MEDI_UNIT='"
				+ parm.getValue("MEDI_UNIT") + "',DOSAGE_UNIT='"
				+ parm.getValue("DOSAGE_UNIT") + "',STOCK_UNIT='"
				+ parm.getValue("STOCK_UNIT") + "', " + "PURCH_UNIT='"
				+ parm.getValue("PURCH_UNIT") + "',RETAIL_PRICE="
				+ parm.getDouble("RETAIL_PRICE") + ",CTRLDRUGCLASS_CODE='"
				+ parm.getValue("CTRLDRUGCLASS_CODE") + "',ANTIBIOTIC_CODE='"
				+ parm.getValue("ANTIBIOTIC_CODE") + "',SUP_CODE='"
				+ parm.getValue("SUP_CODE") + "', " + "OPT_USER='"
				+ parm.getValue("OPT_USER") + "',OPT_DATE="
				+ format(parm.getValue("OPT_DATE")) + ",OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',PACK_UNIT='"
				+ parm.getValue("PACK_UNIT") + "' "
				+ "WHERE ORDER_CODE = '" + parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * PHA_BASE���INSERT
	 * 
	 * @author shendr 2013.08.28
	 */
	public TParm OnInsertBase(TParm parm) {
		String sql = "INSERT INTO PHA_BASE(ORDER_CODE,ORDER_DESC,GOODS_DESC,ALIAS_DESC,SPECIFICATION, "
				+ "MAN_CHN_DESC,PHA_TYPE,TYPE_CODE,DOSE_CODE,FREQ_CODE, "
				+ "ROUTE_CODE,TAKE_DAYS,MEDI_QTY,MEDI_UNIT,DOSAGE_UNIT, "
				+ "STOCK_UNIT,PURCH_UNIT,RETAIL_PRICE,CTRLDRUGCLASS_CODE,ANTIBIOTIC_CODE, "
				+ "SUP_CODE,OPT_USER,OPT_DATE,OPT_TERM,PACK_UNIT) "
				+ "VALUES('"
				+ parm.getValue("ORDER_CODE")
				+ "','"
				+ parm.getValue("ORDER_DESC")
				+ "','"
				+ parm.getValue("GOODS_DESC")
				+ "','"
				+ parm.getValue("ALIAS_DESC")
				+ "','"
				+ parm.getValue("SPECIFICATION")
				+ "','"
				+ parm.getValue("MAN_CHN_DESC")
				+ "','"
				+ parm.getValue("PHA_TYPE")
				+ "','"
				+ parm.getValue("TYPE_CODE")
				+ "','"
				+ parm.getValue("DOSE_CODE")
				+ "','"
				+ parm.getValue("FREQ_CODE")
				+ "','"
				+ parm.getValue("ROUTE_CODE")
				+ "',"
				+ parm.getInt("TAKE_DAYS")
				+ ","
				+ parm.getDouble("MEDI_QTY")
				+ ",'"
				+ parm.getValue("MEDI_UNIT")
				+ "','"
				+ parm.getValue("DOSAGE_UNIT")
				+ "','"
				+ parm.getValue("STOCK_UNIT")
				+ "','"
				+ parm.getValue("PURCH_UNIT")
				+ "',"
				+ parm.getDouble("RETAIL_PRICE")
				+ ",'"
				+ parm.getValue("CTRLDRUGCLASS_CODE")
				+ "','"
				+ parm.getValue("ANTIBIOTIC_CODE")
				+ "','"
				+ parm.getValue("SUP_CODE")
				+ "','"
				+ parm.getValue("OPT_USER")
				+ "',"
				+ format(parm.getValue("OPT_DATE"))
				+ ",'"
				+ parm.getValue("OPT_TERM")
				+ "','"
				+ parm.getValue("PACK_UNIT")
				+ "')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * PHA_BASE���DELETE
	 * 
	 * @author shendr 2013.08.28
	 */
	public TParm OnDeleteBase(TParm parm) {
		String sql = "DELETE FROM PHA_BASE WHERE ORDER_CODE = '"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/*
	 * ��ѯ��������������PHA_BASE���Ƿ����
	 */
	public TParm queryPhaBaseByOrderCode(TParm parm) {
		String sql = "SELECT ORDER_CODE FROM PHA_BASE WHERE ORDER_CODE='"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * ��¼��־
	 */
	public TParm OnSaveIndBatchLog(TParm tparm) {
		SimpleDateFormat logFormater = new SimpleDateFormat("yyMMdd HHmmssSSS");

		String sql = "INSERT INTO IND_BATCH_LOG(BATCH_CODE,START_TIME,END_TIME,RESULT_DESC) "
				+ "VALUES('"
				+ tparm.getValue("BATCH_CODE")
				+ ""
				+ "','"
				+ logFormater.format(new Date())
				+ ""
				+ "','"
				+ logFormater.format(new Date())
				+ "','"
				+ tparm.getValue("RESULT_DESC") + "')";

		return new TParm(TJDODBTool.getInstance().update(sql));

	}

	/**
	 * SYS_OPERATOR���INSERT
	 * 
	 * @return
	 * @author shendr 2013.08.28
	 */
	public TParm onSaveSysOperator(TParm tparm) {
		String sql = "INSERT INTO SYS_OPERATOR( "
				+ "USER_ID,USER_NAME,PY1,PY2,SEQ, "
				+ "DESCRIPTION,ID_NO,SEX_CODE,USER_PASSWORD,POS_CODE, "
				+ "ROLE_ID,ACTIVE_DATE,END_DATE,PUB_FUNCTION,E_MAIL, "
				+ "LCS_NO,EFF_LCS_DATE,END_LCS_DATE,FULLTIME_FLG,CTRL_FLG, "
				+ "REGION_CODE,RCNT_LOGIN_DATE,RCNT_LOGOUT_DATE,RCNT_IP,OPT_USER, "
				+ "OPT_DATE,OPT_TERM,FOREIGNER_FLG,ABNORMAL_TIMES,POS_DESC, "
				+ "USER_ENG_NAME,PWD_ENDDATE,PWD_STARTDATE,UKEY_FLG,DR_QUALIFY_CODE, "
				+ "COST_CENTER_CODE,TEL1,TEL2) " + "VALUES('"
				+ tparm.getValue("USER_ID")
				+ "','"
				+ tparm.getValue("USER_NAME")
				+ "','"
				+ tparm.getValue("PY1")
				+ "','"
				+ tparm.getValue("PY2")
				+ "',"
				+ tparm.getInt("SEQ")
				+ ",'"
				+ tparm.getValue("DESCRIPTION")
				+ "','"
				+ tparm.getValue("ID_NO")
				+ "','"
				+ tparm.getValue("SEX_CODE")
				+ "','"
				+ tparm.getValue("USER_PASSWORD")
				+ "','"
				+ tparm.getValue("POS_CODE")
				+ "','"
				+ tparm.getValue("ROLE_ID")
				+ "',"
				+ format(tparm.getValue("ACTIVE_DATE"))
				+ ","
				+ format(tparm.getValue("END_DATE"))
				+ ",'"
				+ tparm.getValue("PUB_FUNCTION")
				+ "','"
				+ tparm.getValue("E_MAIL")
				+ "','"
				+ tparm.getValue("LCS_NO")
				+ "',"
				+ format(tparm.getValue("EFF_LCS_DATE"))
				+ ","
				+ format(tparm.getValue("END_LCS_DATE"))
				+ ",'"
				+ tparm.getValue("FULLTIME_FLG")
				+ "','"
				+ tparm.getValue("CTRL_FLG")
				+ "','"
				+ tparm.getValue("REGION_CODE")
				+ "',"
				+ format(tparm.getValue("RCNT_LOGIN_DATE"))
				+ ","
				+ format(tparm.getValue("RCNT_LOGOUT_DATE"))
				+ ",'"
				+ tparm.getValue("RCNT_IP")
				+ "','"
				+ tparm.getValue("OPT_USER")
				+ "',"
				+ format(tparm.getValue("OPT_DATE"))
				+ ",'"
				+ tparm.getValue("OPT_TERM")
				+ "','"
				+ tparm.getValue("FOREIGNER_FLG")
				+ "',"
				+ tparm.getInt("ABNORMAL_TIMES")
				+ ",'"
				+ tparm.getValue("POS_DESC")
				+ "','"
				+ tparm.getValue("USER_ENG_NAME")
				+ "',"
				+ format(tparm.getValue("PWD_ENDDATE"))
				+ ","
				+ format(tparm.getValue("PWD_STARTDATE"))
				+ ",'"
				+ tparm.getValue("UKEY_FLG")
				+ "','"
				+ tparm.getValue("DR_QUALIFY_CODE")
				+ "','"
				+ tparm.getValue("COST_CENTER_CODE")
				+ "','"
				+ tparm.getValue("TEL1")
				+ "','"
				+ tparm.getValue("TEL2")
				+ "')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * SYS_OPERATOR���UPDATE
	 * 
	 * @return
	 * @author shendr 2013.08.28
	 */
	public TParm onUpdateSysOperator(TParm tparm) {
		String sql = "UPDATE SYS_OPERATOR SET USER_NAME='"
				+ tparm.getValue("USER_NAME") + "',PY1='"
				+ tparm.getValue("PY1") + "',PY2='" + tparm.getValue("PY2")
				+ "',SEQ=" + tparm.getInt("SEQ") + ",DESCRIPTION='"
				+ tparm.getValue("DESCRIPTION") + "',ID_NO='"
				+ tparm.getValue("ID_NO") + "',SEX_CODE='"
				+ tparm.getValue("SEX_CODE") + "',USER_PASSWORD='"
				+ tparm.getValue("USER_PASSWORD") + "',POS_CODE='"
				+ tparm.getValue("POS_CODE") + "',ROLE_ID='"
				+ tparm.getValue("ROLE_ID") + "',ACTIVE_DATE="
				+ format(tparm.getValue("ACTIVE_DATE")) + ",END_DATE="
				+ format(tparm.getValue("END_DATE")) + ",PUB_FUNCTION='"
				+ tparm.getValue("PUB_FUNCTION") + "',E_MAIL='"
				+ tparm.getValue("E_MAIL") + "',LCS_NO='"
				+ tparm.getValue("LCS_NO") + "',EFF_LCS_DATE="
				+ format(tparm.getValue("EFF_LCS_DATE")) + ",END_LCS_DATE="
				+ format(tparm.getValue("END_LCS_DATE")) + ",FULLTIME_FLG='"
				+ tparm.getValue("FULLTIME_FLG") + "',CTRL_FLG='"
				+ tparm.getValue("CTRL_FLG") + "',REGION_CODE='"
				+ tparm.getValue("REGION_CODE") + "',RCNT_LOGIN_DATE="
				+ format(tparm.getValue("RCNT_LOGIN_DATE"))
				+ ",RCNT_LOGOUT_DATE="
				+ format(tparm.getValue("RCNT_LOGOUT_DATE")) + ",RCNT_IP='"
				+ tparm.getValue("RCNT_IP") + "',OPT_USER='"
				+ tparm.getValue("OPT_USER") + "',OPT_DATE="
				+ format(tparm.getValue("OPT_DATE")) + ",OPT_TERM='"
				+ tparm.getValue("OPT_TERM") + "',FOREIGNER_FLG='"
				+ tparm.getValue("FOREIGNER_FLG") + "',ABNORMAL_TIMES="
				+ tparm.getInt("ABNORMAL_TIMES") + ",POS_DESC='"
				+ tparm.getValue("POS_DESC") + "',USER_ENG_NAME='"
				+ tparm.getValue("USER_ENG_NAME") + "',PWD_ENDDATE="
				+ format(tparm.getValue("PWD_ENDDATE")) + ",PWD_STARTDATE="
				+ format(tparm.getValue("PWD_STARTDATE")) + ",UKEY_FLG='"
				+ tparm.getValue("UKEY_FLG") + "',DR_QUALIFY_CODE='"
				+ tparm.getValue("DR_QUALIFY_CODE") + "',COST_CENTER_CODE='"
				+ tparm.getValue("COST_CENTER_CODE") + "',TEL1='"
				+ tparm.getValue("TEL1") + "',TEL2='" + tparm.getValue("TEL2")
				+ "' WHERE USER_ID='" + tparm.getValue("USER_ID") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * SYS_OPERATOR���DELETE
	 * 
	 * @return
	 * @author shendr 2013.08.28
	 */
	public TParm onDeleteSysOperator(TParm tparm) {
		String sql = "DELETE FROM SYS_OPERATOR WHERE USER_ID='"
				+ tparm.getValue("USER_ID") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * ת������
	 * 
	 * @param data
	 * @author shendr
	 * @return
	 */
	public static String format(String data) {
		if (StringUtil.isNullString(data))
			return null;
		else {
			String str = "";
			str = data.substring(4, 5);
			if ("��".equals(str))
				data = "TO_DATE (REPLACE ('" + data
						+ "', '�� ', ''), 'dd-mm-yy')";
			else
				data = "TO_DATE (REPLACE ('"
						+ data.replace(data.substring(5, 6), data.substring(5,
								6)
								+ " ") + "', '�� ', ''), 'dd-mm-yy')";
		}
		return data;
	}

	/**
	 * SYS_PATINFO���INSERT
	 * 
	 * @return
	 * @author shendr 2013.09.10
	 */
	public TParm onSaveSysPatInfo(TParm tparm) {
		String sql = "INSERT INTO SYS_PATINFO (MR_NO,IPD_NO,PAT_NAME,IDNO,BIRTH_DATE, "
				+ "TEL_HOME,CELL_PHONE,BLOOD_TYPE,SEX_CODE,MARRIAGE_CODE, "
				+ "POST_CODE,ADDRESS,RESID_POST_CODE,RESID_ADDRESS,NATION_CODE,DEAD_DATE, "
				+ "HEIGHT,WEIGHT,OPT_USER,OPT_DATE,OPT_TERM) " + "VALUES ('"
				+ tparm.getValue("MR_NO")
				+ "','"
				+ tparm.getValue("IPD_NO")
				+ "','"
				+ tparm.getValue("PAT_NAME")
				+ "','"
				+ tparm.getValue("IDNO")
				+ "',"
				+ format(tparm.getValue("BIRTH_DATE"))
				+ ",'"
				+ tparm.getValue("TEL_HOME")
				+ "','"
				+ tparm.getValue("CELL_PHONE")
				+ "','"
				+ tparm.getValue("BLOOD_TYPE")
				+ "','"
				+ tparm.getValue("SEX_CODE")
				+ "','"
				+ tparm.getValue("MARRIAGE_CODE")
				+ "','"
				+ tparm.getValue("POST_CODE")
				+ "','"
				+ tparm.getValue("ADDRESS")
				+ "','"
				+ tparm.getValue("RESID_POST_CODE")
				+ "','"
				+ tparm.getValue("RESID_ADDRESS")
				+ "','"
				+ tparm.getValue("NATION_CODE")
				+ "',"
				+ format(tparm.getValue("DEAD_DATE"))
				+ ",'"
				+ tparm.getValue("HEIGHT")
				+ "','"
				+ tparm.getValue("WEIGHT")
				+ "','"
				+ tparm.getValue("OPT_USER")
				+ "',"
				+ format(tparm.getValue("OPT_DATE"))
				+ ",'"
				+ tparm.getValue("OPT_TERM") + "')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * SYS_PATINFO���UPDATE
	 * 
	 * @return
	 * @author shendr 2013.09.10
	 */
	public TParm onUpdateSysPatInfo(TParm tparm) {
		String sql = "UPDATE SYS_PATINFO SET IPD_NO='"
				+ tparm.getValue("IPD_NO") + "',PAT_NAME='"
				+ tparm.getValue("PAT_NAME") + "',IDNO='"
				+ tparm.getValue("IDNO") + "',BIRTH_DATE="
				+ format(tparm.getValue("BIRTH_DATE")) + ",TEL_HOME='"
				+ tparm.getValue("TEL_HOME") + "', " + "CELL_PHONE='"
				+ tparm.getValue("CELL_PHONE") + "',BLOOD_TYPE='"
				+ tparm.getValue("BLOOD_TYPE") + "',SEX_CODE='"
				+ tparm.getValue("SEX_CODE") + "',MARRIAGE_CODE='"
				+ tparm.getValue("MARRIAGE_CODE") + "',POST_CODE='"
				+ tparm.getValue("POST_CODE") + "', " + "ADDRESS='"
				+ tparm.getValue("ADDRESS") + "',RESID_POST_CODE='"
				+ tparm.getValue("RESID_POST_CODE") + "',RESID_ADDRESS='"
				+ tparm.getValue("RESID_ADDRESS") + "',NATION_CODE='"
				+ tparm.getValue("NATION_CODE") + "',DEAD_DATE="
				+ format(tparm.getValue("DEAD_DATE")) + ", " + "HEIGHT='"
				+ tparm.getValue("HEIGHT") + "',WEIGHT='"
				+ tparm.getValue("WEIGHT") + "' " + "WHERE MR_NO = '"
				+ tparm.getValue("MR_NO") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * SYS_PATINFO���DELETE
	 * 
	 * @return
	 * @author shendr 2013.09.10
	 */
	public TParm onDeleteSysPatInfo(TParm tparm) {
		String sql = "DELETE FROM SYS_PATINFO WHERE MR_NO='"
				+ tparm.getValue("MR_NO") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * PHA_TRANSUNIT���INSERT
	 * 
	 * @return
	 * @author shendr 2013.09.10
	 */
	public TParm onInsertTransunit(TParm parm) {
		String sql = "INSERT INTO PHA_TRANSUNIT(ORDER_CODE,PURCH_UNIT,PURCH_QTY,STOCK_UNIT,STOCK_QTY, "
				+ "DOSAGE_UNIT,DOSAGE_QTY,MEDI_UNIT,MEDI_QTY,OPT_USER, "
				+ "OPT_DATE,OPT_TERM) " + "VALUES('"
				+ parm.getValue("ORDER_CODE")
				+ "','"
				+ parm.getValue("PURCH_UNIT")
				+ "',"
				+ parm.getDouble("PURCH_QTY")
				+ ",'"
				+ parm.getValue("STOCK_UNIT")
				+ "',"
				+ parm.getDouble("STOCK_QTY")
				+ ",'"
				+ parm.getValue("DOSAGE_UNIT")
				+ "',"
				+ parm.getDouble("DOSAGE_QTY")
				+ ",'"
				+ parm.getValue("MEDI_UNIT")
				+ "',"
				+ parm.getDouble("MEDI_QTY")
				+ ",'"
				+ parm.getValue("OPT_USER")
				+ "',"
				+ format(parm.getValue("OPT_DATE"))
				+ ",'"
				+ parm.getValue("OPT_TERM") + "')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * PHA_TRANSUNIT���UPDATE
	 * 
	 * @return
	 * @author shendr 2013.09.10
	 */
	public TParm onUpdateTransunit(TParm parm) {
		String sql = "UPDATE PHA_TRANSUNIT SET PURCH_UNIT='"
				+ parm.getValue("PURCH_UNIT") + "',PURCH_QTY="
				+ parm.getDouble("PURCH_QTY") + ",STOCK_UNIT='"
				+ parm.getValue("STOCK_UNIT") + "',STOCK_QTY="
				+ parm.getDouble("STOCK_QTY") + ",DOSAGE_UNIT='"
				+ parm.getValue("DOSAGE_UNIT") + "', " + "DOSAGE_QTY="
				+ parm.getDouble("DOSAGE_QTY") + ",MEDI_UNIT='"
				+ parm.getValue("MEDI_UNIT") + "',MEDI_QTY="
				+ parm.getDouble("MEDI_QTY") + ",OPT_USER='"
				+ parm.getValue("OPT_USER") + "',OPT_DATE="
				+ format(parm.getValue("OPT_DATE")) + ", " + "OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "' " + "WHERE ORDER_CODE = '"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * PHA_TRANSUNIT���DELETE
	 * 
	 * @return
	 * @author shendr 2013.09.10
	 */
	public TParm onDeleteTransunit(TParm parm) {
		String sql = "DELETE FROM PHA_TRANSUNIT WHERE ORDER_CODE='"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * ����IND_STOCKM ACTIVE_FLG
	 * 
	 * @return
	 */
	public TParm updateActiveFlgM(TParm parm, TConnection conn) {
		String sql = "UPDATE IND_STOCKM SET ACTIVE_FLG='N' WHERE ORDER_CODE='"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * ����IND_STOCK ACTIVE_FLG
	 * 
	 * @return
	 */
	public TParm updateActiveFlgD(TParm parm, TConnection conn) {
		String sql = "UPDATE IND_STOCK SET ACTIVE_FLG='N' WHERE ORDER_CODE='"
				+ parm.getValue("ORDER_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

}
