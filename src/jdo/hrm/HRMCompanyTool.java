package jdo.hrm;

import java.util.HashMap;
import java.util.Map;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
*
* <p>Title: 健康检查合同动作类</p>
*
* <p>Description: 健康检查合同动作类</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMCompanyTool extends TJDOTool {
	 /**
     * 实例
     */
    public static HRMCompanyTool instanceObject;
    /**
     * 初始化套餐COMBOSQL
     */
    private static final String INIT_PACKAGE_COMBO_SQL="SELECT PACKAGE_CODE ID,PACKAGE_DESC NAME FROM HRM_PACKAGEM ORDER BY PACKAGE_CODE";
    /**
     * 团体COMBO数据SQL
     */
    private static final String COMBO_SQL="SELECT COMPANY_CODE ID,COMPANY_DESC NAME,PY1 PY1 FROM HRM_COMPANY ORDER BY COMPANY_CODE";
    /**
     * 当日检查的团体、合同
     */
    private static final String COMPANY_TODAY="SELECT DISTINCT B.CONTRACT_CODE,B.CONTRACT_DESC ,A.COMPANY_CODE ID,A.COMPANY_DESC NAME FROM HRM_CONTRACTD B,HRM_COMPANY A WHERE B.PRE_CHK_DATE<=TO_DATE('#','YYYYMMDDHH24MI') AND B.COMPANY_CODE=A.COMPANY_CODE";
    /**
     * 取得健检部门
     */
    private static final String GET_DEPT="SELECT DEPT_CODE ID,DEPT_ABS_DESC NAME FROM SYS_DEPT WHERE FINAL_FLG='Y' AND HRM_FIT_FLG='Y' ORDER BY SEQ";
    /**
     * 查询团体的所有报到信息
     */
    private static final String GET_CONTRACTD=
    	"SELECT   'N' CHOOSE,B.COMPANY_DESC,A.CONTRACT_DESC,D.PACKAGE_DESC,A.PAT_NAME," +
    	"         A.PACKAGE_CODE,C.SEX_CODE,C.BIRTH_DATE,A.MR_NO,A.COMPANY_CODE,A.CONTRACT_CODE,C.IDNO" +
    	"  FROM   HRM_CONTRACTD A, HRM_COMPANY B, SYS_PATINFO C,HRM_PACKAGEM D" +
    	"  WHERE       A.COMPANY_CODE = '#'" +
    	"         AND A.CONTRACT_CODE = '#'" +
    	"         AND A.COMPANY_CODE = B.COMPANY_CODE" +
    	"		  AND A.PACKAGE_CODE=D.PACKAGE_CODE(+)" +
    	"		  AND A.COVER_FLG='#'" +
    	"         AND A.MR_NO = C.MR_NO" +
    	"  ORDER BY A.MR_NO";
    /**
     * 根据团体代码、合同代码、病案号查询报到详情
     */
    private static final String GET_CONTRACTD_BY_MR=//modify by wanglong 20121217
		"SELECT 'Y' AS CHOOSE,A.* ,A.BIRTHDAY AS BIRTH_DATE,D.DEPT_CODE,D.CASE_NO,D.REPORTLIST,D.INTRO_USER,D.PAT_DEPT,D.DISCNT,D.BILL_FLG  "
			+ "	 FROM HRM_CONTRACTD A,HRM_PATADM D "
			+ "  WHERE A.COMPANY_CODE = '#' "
			+ "    AND A.CONTRACT_CODE = '#' "
			+ "    AND A.MR_NO='#' "
			+ "    AND A.COMPANY_CODE=D.COMPANY_CODE(+) "
			+ "    AND A.CONTRACT_CODE=D.CONTRACT_CODE(+) "
			+ "    AND A.MR_NO=D.MR_NO(+) " 
			+ "	ORDER BY A.SEQ_NO,A.STAFF_NO";
    /**
     * 只根据病案号（不使用团体代码、合同代码）查询报到详情
     */
	private static final String GET_CONTRACTD_BY_MR_WITHOUT_COMPANY = // add by wanglong 20121217
		"SELECT 'Y' AS CHOOSE,A.* ,A.BIRTHDAY AS BIRTH_DATE,FLOOR(MONTHS_BETWEEN(SYSDATE,A.BIRTHDAY)/12) AS AGE,'#' DEPT_CODE,D.CASE_NO,D.REPORTLIST,D.INTRO_USER  "
			+ "	 FROM HRM_CONTRACTD A,HRM_PATADM D "
			+ "	WHERE A.MR_NO='#' "
			+ "   AND A.COMPANY_CODE=D.COMPANY_CODE(+) "
			+ "   AND A.CONTRACT_CODE=D.CONTRACT_CODE(+) "
			+ "   AND A.MR_NO=D.MR_NO(+) " 
			+ "	ORDER BY A.SEQ_NO,A.STAFF_NO";
    /**
     * 根据团体代码、合同代码、身份证号查询报到详情
     */
    private static final String GET_CONTRACTD_BY_ID=
    	"SELECT   A.COVER_FLG CHOOSE,A.COMPANY_CODE,A.CONTRACT_CODE,A.PACKAGE_CODE,A.PAT_NAME,A.SEQ_NO,A.STAFF_NO,A.TEL,B.PACKAGE_DESC," +//add by wanglong 20121214
    	"         C.SEX_CODE,C.BIRTH_DATE,A.MR_NO,A.CONTRACT_CODE,A.IDNO,'' DEPT_CODE" +
    	"  FROM   HRM_CONTRACTD A,HRM_PACKAGEM B,SYS_PATINFO C" +
    	"  WHERE       A.COMPANY_CODE = '#'" +
    	"         AND A.CONTRACT_CODE = '#'" +
    	"		  AND A.IDNO='#'" +
    	"		  AND A.MR_NO=C.MR_NO" +
    	"		  AND A.COVER_FLG='#'" +
    	"		  AND A.PACKAGE_CODE=B.PACKAGE_CODE";
    	
    /**
     * 查询团体的详细信息
     */
    private static final String GET_COMPANY_BY_CODE="SELECT * FROM HRM_COMPANY WHERE COMPANY_CODE='#'";
    /**
     * 得到实例
     * @return HRMCompanyTool
     */
	public static HRMCompanyTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new HRMCompanyTool();
		}
		return instanceObject;
	}
    
    /**
     * 保存
     * @param parm
     * @param conn
     * @return
     */
	public TParm onSaveContract(TParm parm,TConnection conn){
		TParm result=new TParm();
		Map inMap=(HashMap)parm.getData("IN_MAP");
		String[] sql=(String[])inMap.get("SQL");
		if(sql==null){
			return result;
		}
		if(sql.length<1){
			return result;
		}
		for(String tempSql:sql){
			result=new TParm(TJDODBTool.getInstance().update(tempSql, conn));
			if(result.getErrCode()!=0){
//				// System.out.println("wrong sql="+tempSql);
				return result;
			}
		}
		return result;
	}
	
	/**
	 * 得到套餐COMBO数据
	 * @return
	 */
	public TParm getPackageComboParm(){
	TParm result=new TParm(TJDODBTool.getInstance().select(INIT_PACKAGE_COMBO_SQL));
	return result;
	}
	
	/**
	 * 得到团体COMBO数据
	 * @return
	 */
	public TParm getCompanyComboParm(){
		TParm result=new TParm(TJDODBTool.getInstance().select(COMBO_SQL));
		return result;
	}
	
	/**
	 * 当日检查的团体和合同信息
	 * @return
	 */
	public TParm getCompanyToday(){
		String sql=this.COMPANY_TODAY.replace("#", StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMddHHmm"));
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * 根据团体代码和合同代码查询健检报到信息
	 * @param companyCode
	 * @param contractCode
	 * @param isCovered
	 * @return
	 */
	public TParm getContractD(String companyCode,String contractCode,String isCovered){
		TParm result=new TParm();
		if(StringUtil.isNullString(companyCode)||StringUtil.isNullString(contractCode)){
			return result;
		}
		//CHOOSE;COMPANY_DESC;CONTRACT_DESC;PACKAGE_DESC;PAT_NAME;SEX_CODE;BIRTH_DATE
		String sql=this.GET_CONTRACTD.replaceFirst("#",companyCode).replaceFirst("#", contractCode).replaceFirst("#", isCovered);
		result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * 根据团体代码、合同代码以及病案号查询健检报到信息
	 * @param companyCode
	 * @param contractCode
	 * @param mrNo
	 * @return
	 */
	public TParm getContractDByMr(String companyCode,String contractCode,String mrNo){
		TParm result=new TParm();
		if(StringUtil.isNullString(companyCode)||StringUtil.isNullString(contractCode)||StringUtil.isNullString(mrNo)){
			return result;
		}
		//CHOOSE;COMPANY_DESC;CONTRACT_DESC;PACKAGE_DESC;PAT_NAME;SEX_CODE;BIRTH_DATE
		String sql=this.GET_CONTRACTD_BY_MR.replaceFirst("#",companyCode).replaceFirst("#", contractCode).replaceFirst("#", mrNo);
//        System.out.println("getContractDByMr.sql=========================="+sql);
		result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * 根据病案号查询健检报到信息
	 * @param mrNo
	 * @return
	 */
	public TParm getContractDByMr(String mrNo){
		TParm result=new TParm();
		if(StringUtil.isNullString(mrNo)){
			return result;
		}
		//CHOOSE;COMPANY_DESC;CONTRACT_DESC;PACKAGE_DESC;PAT_NAME;SEX_CODE;BIRTH_DATE
		String sql=this.GET_CONTRACTD_BY_MR_WITHOUT_COMPANY.replaceFirst("#", Operator.getDept()).replaceFirst("#", mrNo);
//		System.out.println("getContractDByMr.sql=========================="+sql);
		result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * 根据团体代码、合同代码以及身份证号查询健检报到信息
	 * @param companyCode
	 * @param contractCode
	 * @param idNo
	 * @param isReport
	 * @return
	 */
	public TParm getContractDById(String companyCode,String contractCode,String idNo,String isReport){
		TParm result=new TParm();
		if(StringUtil.isNullString(companyCode)||StringUtil.isNullString(contractCode)||StringUtil.isNullString(idNo)){
			return result;
		}
		//CHOOSE;COMPANY_DESC;CONTRACT_DESC;PACKAGE_DESC;PAT_NAME;SEX_CODE;BIRTH_DATE
		String sql=this.GET_CONTRACTD_BY_ID.replaceFirst("#",companyCode).replaceFirst("#", contractCode).replaceFirst("#", idNo).replaceFirst("#", isReport);
		result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * 根据团体代码取得团体详细信息
	 * @param companyCode
	 * @return
	 */
	public TParm getCompanyByCode(String companyCode){
		TParm result=new TParm();
		if(StringUtil.isNullString(companyCode)){
			return result;
		}
		result=new TParm(TJDODBTool.getInstance().select(GET_COMPANY_BY_CODE.replace("#", companyCode)));
		return result;
	}
	
	/**
	 * 取得健检部门
	 * @return
	 */
	public TParm getDept(){
		TParm result=new TParm(TJDODBTool.getInstance().select(GET_DEPT));
		return result;
	}
}
