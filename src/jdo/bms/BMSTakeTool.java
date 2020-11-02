package jdo.bms;

import jdo.bms.ws.BmsTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * @author shibl
 * 
 */
public class BMSTakeTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static BMSTakeTool instanceObject;
	/**
     * 
     */
	private String MQuerysql = "SELECT *  FROM  BMS_BLDTAKEM WHERE BLOOD_TANO='<BLOOD_TANO>'";
	/**
     * 
     */
	private String DQuerysql = "SELECT *  FROM  BMS_BLDTAKED WHERE BLOOD_TANO='<BLOOD_TANO>' ORDER BY SEQ ";
	/**
     * 
     */
	private String Mdelsql = "DELETE FROM BMS_BLDTAKEM WHERE BLOOD_TANO='<BLOOD_TANO>'";
	/**
     * 
     */
	private String Ddelsql = "DELETE FROM BMS_BLDTAKED WHERE BLOOD_TANO='<BLOOD_TANO>'";
	/**
     * 
     */
	private String Minsertsql = "INSERT INTO BMS_BLDTAKEM(BLOOD_TANO,ADM_TYPE,APPLY_NO,"
			+ " BLOOD_DATE,BLOOD_USER,CASE_NO,MR_NO,IPD_NO,BED_NO,"
			+ " DEPT_CODE,STATION_CODE,OPT_USER,"
			+ " OPT_DATE,OPT_TERM,BLOOD_TYPE,BLOOD_RH_TYPE)"
			+ " VALUES ('<BLOOD_TANO>','<ADM_TYPE>','<APPLY_NO>',"
			+ " TO_DATE('<BLOOD_DATE>','YYYYMMDDHH24MISS'),'<BLOOD_USER>','<CASE_NO>','<MR_NO>',"
			+ "'<IPD_NO>','<BED_NO>','<DEPT_CODE>',"
			+ "'<STATION_CODE>','<OPT_USER>',SYSDATE,'<OPT_TERM>',"
			+ "'<BLOOD_TYPE>','<BLOOD_RH_TYPE>')";
	/**
     * 
     */
	private String Dinsertsql = "INSERT INTO BMS_BLDTAKED(BLOOD_TANO, SEQ,"
			+ " BLD_CODE,APPLY_QTY,UNIT_CODE, OPT_USER,"
			+ " OPT_DATE, OPT_TERM , BLOOD_TYPE ,RH ,IRRADIATION)VALUES('<BLOOD_TANO>','<SEQ>','<BLD_CODE>',"
			+ " '<APPLY_QTY>','<UNIT_CODE>',"
			+ " '<OPT_USER>',SYSDATE,'<OPT_TERM>','<BLOOD_TYPE>','<RH>','<IRRADIATION>')";

	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static BMSTakeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new BMSTakeTool();
		return instanceObject;
	}
    /**
     * 
     * @return
     */
	public static String getNo() {
		return SystemTool.getInstance().getNo("ALL", "BMS", "BMS_TAKENO", "BMS_TAKENO");
	}

	/**
	 * ������
	 */
	public BMSTakeTool() {
		onInit();
	}

	/**
	 * �õ�����sql
	 * 
	 * @param parm
	 * @return
	 */
	public String getMInsertsql(TParm parm) {
		String sql = BmsTool.getInstance().buildSQL(Minsertsql, parm);
		return sql;
	}

	/**
	 * �õ�ϸ��sql
	 * 
	 * @param parm
	 * @return
	 */
	public String getDInsertsql(TParm parm) {
		String sql = BmsTool.getInstance().buildSQL(Dinsertsql, parm);
		return sql;
	}

	/**
	 * �õ�����ɾ��
	 * 
	 * @param parm
	 * @return
	 */
	public String getMdelsql(TParm parm) {
		String sql = BmsTool.getInstance().buildSQL(Mdelsql, parm);
		return sql;
	}

	/**
	 * �õ�ϸ��ɾ��
	 * 
	 * @param parm
	 * @return
	 */
	public String getDdelsql(TParm parm) {
		String sql = BmsTool.getInstance().buildSQL(Ddelsql, parm);
		return sql;
	}

	/**
	 * �õ������ѯ
	 * 
	 * @param parm
	 * @return
	 */
	public String getMQuerysql(TParm parm) {
		String sql = BmsTool.getInstance().buildSQL(MQuerysql, parm);
		return sql;
	}

	/**
	 * �õ�ϸ���ѯ
	 * 
	 * @param parm
	 * @return
	 */
	public String getDQuerysql(TParm parm) {
		String sql = BmsTool.getInstance().buildSQL(DQuerysql, parm);
		return sql;
	}
}
