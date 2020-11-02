package jdo.mem;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 会员卡SQL封装
 * </p>
 *
 * <p>
 * Description: 会员卡SQL封装
 * </p>
 *

 * <p>
 * Company:bluecore
 * </p>
 *
 * @author huangtt 20140109
 * @version 1.0
 */
public class MEMSQL {
	public MEMSQL(){}
	
	/**
	 * 查询礼品卡种类
	 * @param price
	 * @return
	 */
	public static String getCaseCardInfo(String price){
		String sql1="";
		if(!"".equals(price)){
			sql1=" AND ( GIFTCARD_DESC LIKE '"+price+"%' OR PY1 LIKE '"+price+"%' )";
		}
		String sql="SELECT   GIFTCARD_CODE , GIFTCARD_DESC,RETAIL_PRICE,FACE_VALUE" +
				" FROM MEM_CASH_CARD_INFO" +
				" WHERE 1=1 " +
				sql1+
				" ORDER BY GIFTCARD_CODE";
		System.out.println(sql);
		return sql;
		
	}
	/**
	 * 根据会员卡类型查询身份
	 * @param memCode
	 * @return
	 */
	public static String getSysCtz(String memCode){
		String sql="SELECT CTZ_CODE,DEPT_CODE FROM SYS_CTZ WHERE MEM_CODE='"+memCode+"'";
		return sql;
	}
	
    /**
     * 根据身份和病案号查询费用
     * @param mrNo
     * @param ctzs
     * @return
     */
	public static String getOpdorderList(String mrNo,String ctzs){
		String sql="SELECT A.CASE_NO, A.MR_NO,D.PAT_NAME USER_NAME,D.IDNO ID_NO, B.ADM_DATE,A.ORDER_CODE," +
				" A.ORDER_DESC,A.OWN_AMT ORIGINAL_PRICE,A.AR_AMT DISCOUNT_PRICE,A.OWN_AMT-A.AR_AMT DIFFERENT_PRICE" +
				" FROM OPD_ORDER A,REG_PATADM B,SYS_PATINFO D" +
				" WHERE  B.CASE_NO=A.CASE_NO" +
				" AND B.MR_NO = D.MR_NO" +
				" AND A.MEM_PACKAGE_ID IS NULL " +
				" AND B.MR_NO='"+mrNo+"'" +
				" AND B.CTZ1_CODE IN("+ctzs+")";
		return sql;
	}
	/**
	 * 查询自费身份
	 * @return
	 */
	public static String getOwnPrice(){
		String sql = "SELECT CTZ_CODE FROM SYS_CTZ WHERE CTZ_CODE = '99'";
		return sql;
	}
	
	/**
	 * 查询交易表中是否有新建的会员记录
	 * @param mr_no
	 * @return
	 */
	public static String getMemTrade(String mr_no){
		String sql = "SELECT TRADE_NO, MR_NO, MEM_CODE, MEM_DESC, MEM_CARD_NO, MEM_FEE, START_DATE, END_DATE," +
				" DESCRIPTION, OPT_DATE, OPT_USER, OPT_TERM,REASON,INTRODUCER1,INTRODUCER2,INTRODUCER3" +
				" FROM MEM_TRADE" +
				" WHERE STATUS = '0' AND MR_NO = '"+mr_no+"'";
		return sql;
	}
	
	
	/**
	 * 查询交易表中会员记录
	 * @param mr_no
	 * @return
	 */
	public static String getMemTrade1(String mr_no){
		String sql = "SELECT TRADE_NO, MR_NO, MEM_CODE, MEM_DESC, MEM_CARD_NO, MEM_FEE, START_DATE, END_DATE," +
				" DESCRIPTION, OPT_DATE, OPT_USER, OPT_TERM, GATHER_TYPE, REASON, INTRODUCER1,INTRODUCER2, INTRODUCER3, " +
				" PAY_TYPE01, PAY_TYPE02, PAY_TYPE03, PAY_TYPE04, PAY_TYPE05, PAY_TYPE06, PAY_TYPE07, PAY_TYPE08, PAY_TYPE09, PAY_TYPE10 " +
				" FROM MEM_TRADE" +
				" WHERE REMOVE_FLG='N' AND STATUS = '1' AND END_DATE > SYSDATE AND MR_NO = '"+mr_no+"'" +
				" ORDER BY MEM_CARD_NO DESC";
		return sql;
	}
	
	public static String getMemTradeCrm(String mr_no){
		String sql = "SELECT   TRADE_NO, STATUS, MR_NO, MEM_CODE, MEM_DESC, MEM_CARD_NO, MEM_FEE," +
				" START_DATE, END_DATE, DESCRIPTION, OPT_DATE, OPT_USER, OPT_TERM," +
				" REASON, INTRODUCER1, INTRODUCER2, INTRODUCER3" +
				" FROM MEM_TRADE" +
				" WHERE STATUS IN ('0', '1') AND MR_NO = '"+mr_no + "'" +
				" ORDER BY STATUS, TRADE_NO DESC";
		return sql;
	}
	
	
	
	/**
	 * 得到会费和卡的有效期
	 * @param memCode
	 * @return
	 */
	public static String getMemFee(String memCode){
		String sql = "SELECT MEM_FEE,VALID_DAYS FROM MEM_MEMBERSHIP_INFO WHERE  MEM_CODE='"+memCode+"'";
		return sql;
	}
	
	public static String getMemFee(String memCard,String mrNo){
		String sql = "SELECT B.MEM_FEE FROM MEM_TRADE A , MEM_MEMBERSHIP_INFO B WHERE A.MEM_CODE=B.MEM_CODE AND B.MEM_CARD='"+memCard+"' AND A.MR_NO='"+mrNo+"' AND A.END_DATE > SYSDATE";
		return sql;
	}
	
	public static String getMemInfo(String mrNo){
		String sql = "SELECT MR_NO, MEM_CODE, MEM_DESC, START_DATE, END_DATE, FAMILY_DOCTOR, " +
				" ACCOUNT_MANAGER_NAME, ACCOUNT_MANAGER_CODE, INSURANCE_COMPANY1_CODE," +
				" INSURANCE_COMPANY1_NAME, INSURANCE_COMPANY2_CODE," +
				" INSURANCE_COMPANY2_NAME, INSURANCE_COMPANY3_CODE," +
				" INSURANCE_COMPANY3_NAME, INSURANCE_NUMBER1, INSURANCE_NUMBER2," +
				" INSURANCE_NUMBER3, SCHOOL_NAME, SCHOOL_TEL, BIRTH_HOSPITAL," +
				" WEIXIN_ACCOUNT, SOURCE, GUARDIAN1_NAME, GUARDIAN1_RELATION," +
				" GUARDIAN1_TEL, GUARDIAN1_PHONE, GUARDIAN1_COM, GUARDIAN1_ID_TYPE," +
				" GUARDIAN1_ID_CODE, GUARDIAN1_EMAIL, GUARDIAN2_NAME, GUARDIAN2_RELATION," +
				" GUARDIAN2_TEL, GUARDIAN2_PHONE, GUARDIAN2_COM, GUARDIAN2_ID_TYPE," +
				" GUARDIAN2_ID_CODE, GUARDIAN2_EMAIL, DEFAULT_GUARDIAN,REG_CTZ1_CODE,REG_CTZ2_CODE,REASON " +
				" FROM MEM_PATINFO WHERE  MR_NO = '"+mrNo+"' AND END_DATE > SYSDATE";
		return sql;
	}
	
	public static String getMemInfoAll(String mrNo){
		String sql = "SELECT MR_NO, MEM_CODE, MEM_DESC, START_DATE, END_DATE, FAMILY_DOCTOR, " +
				" ACCOUNT_MANAGER_NAME, ACCOUNT_MANAGER_CODE, INSURANCE_COMPANY1_CODE," +
				" INSURANCE_COMPANY1_NAME, INSURANCE_COMPANY2_CODE," +
				" INSURANCE_COMPANY2_NAME, INSURANCE_COMPANY3_CODE," +
				" INSURANCE_COMPANY3_NAME, INSURANCE_NUMBER1, INSURANCE_NUMBER2," +
				" INSURANCE_NUMBER3, SCHOOL_NAME, SCHOOL_TEL, BIRTH_HOSPITAL," +
				" WEIXIN_ACCOUNT, SOURCE, GUARDIAN1_NAME, GUARDIAN1_RELATION," +
				" GUARDIAN1_TEL, GUARDIAN1_PHONE, GUARDIAN1_COM, GUARDIAN1_ID_TYPE," +
				" GUARDIAN1_ID_CODE, GUARDIAN1_EMAIL, GUARDIAN2_NAME, GUARDIAN2_RELATION," +
				" GUARDIAN2_TEL, GUARDIAN2_PHONE, GUARDIAN2_COM, GUARDIAN2_ID_TYPE," +
				" GUARDIAN2_ID_CODE, GUARDIAN2_EMAIL, DEFAULT_GUARDIAN,REG_CTZ1_CODE,REG_CTZ2_CODE,REASON,CUSTOMER_SOURCE FROM MEM_PATINFO WHERE  MR_NO = '"+mrNo+"'";
		return sql;
	}
	
	public static String getMemEktIssuelog(String mrNo){
		String sql = "SELECT MR_NO,CARD_NO FROM EKT_ISSUELOG WHERE WRITE_FLG='Y' AND MR_NO = '"+mrNo+"'";
		return sql;
	}
	
	//查询会员折旧的总钱数
	public static String getMemFeeDepreciation(String mrNo,String memCardNo){
		String sql="SELECT SUM(DEPRECIATION_FEE) DEPRECIATION_FEE FROM MEM_FEE_DEPRECIATION WHERE MEM_CARD_NO='"+memCardNo+"' AND MR_NO='"+mrNo+"'";
		return sql;
	}
	//查询会员折旧的开始时间
	public static String getMemFeeDepreciationSdate(String mrNo,String memCardNo){
		String sql = "SELECT DEPRECIATION_START_DATE FROM MEM_FEE_DEPRECIATION " +
				"WHERE MEM_CARD_NO='"+memCardNo+"' AND MR_NO='"+mrNo+"' " +
				"ORDER BY DEPRECIATION_START_DATE ";
		return sql;
	}
	//查询会员折旧的结束时间
	public static String getMemFeeDepreciationEdate(String mrNo,String memCardNo){
		String sql = "SELECT DEPRECIATION_END_DATE FROM MEM_FEE_DEPRECIATION " +
				"WHERE MEM_CARD_NO='"+memCardNo+"' AND MR_NO='"+mrNo+"' " +
				"ORDER BY DEPRECIATION_END_DATE DESC";
		return sql;
	}
	
	//查询交易表中状态为0，1，3的记录，0  新建 1  缴费 3停卡
	
	public static String getRevokeMemInfo(String mrNo){
		//MR_NO;PAT_NAME;MEM_CODE;MEM_FEE;GATHER_TYPE;START_DATE;END_DATE;DESCRIPTION
		String sql="SELECT A.TRADE_NO,A.MR_NO, B.PAT_NAME, A.MEM_CODE, A.MEM_FEE, A.GATHER_TYPE," +
				" A.START_DATE, A.END_DATE,  A.REMOVE_FLG,A.STATUS,A.MEM_CARD_NO, 0 REVOKE_FEE, '' DESCRIPTION, '' STOP_CARD_DESCRIPTION," +
				" A.PAY_TYPE01, A.PAY_TYPE02, A.PAY_TYPE03, A.PAY_TYPE04, A.PAY_TYPE05, A.PAY_TYPE06, " +
				" A.PAY_TYPE07, A.PAY_TYPE08, A.PAY_TYPE09, A.PAY_TYPE10" +
				" FROM MEM_TRADE A, SYS_PATINFO B" +
				" WHERE A.MR_NO = B.MR_NO" +
				" AND A.END_DATE > SYSDATE" +
//				" AND A.START_DATE < SYSDATE" +
				" AND A.STATUS IN ('1')" +
				" AND A.MR_NO = '"+mrNo+"'";
		return sql;
	}
	
	public static String getRevoke(String mrNo){
		String sql = "SELECT MR_NO,MEM_CARD_NO,MEM_FEE,RETURN_TRADE_NO,DESCRIPTION,GATHER_TYPE,STOP_CARD_DESCRIPTION FROM MEM_TRADE WHERE STATUS = '3' AND REMOVE_FLG = 'N' AND MR_NO = '"+mrNo+"'";
		return sql;
	}
	
	/**
	 * 得到支付方式的类型
	 * @param gatherType
	 * @return
	 */
	public static String getPayType(String gatherType){
		String sql = "SELECT PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE WHERE GATHER_TYPE='"+gatherType+"'";
		return sql;
	}
	
	/**
	 * 得到某些支付方式的钱数
	 * @param payType
	 * @param memCardNo
	 * @return
	 */
	public static String getPayTypeFee(String payType,String memCardNo){
		String sql = "SELECT SUM (" + payType + ") PAY_TYPE" + " FROM MEM_TRADE"
				+ " WHERE MEM_CARD_NO = '" + memCardNo
				+ "' AND STATUS = '1' AND REMOVE_FLG = 'N'";
		return sql;

	}
	
	/**
	 * 得到会员售卡时各种支付方式的记录
	 * @param parm
	 * @return
	 */
	public static TParm getMemType(TParm parm){
		TParm tableParm = new TParm();
		String sql = "SELECT A.GATHER_TYPE,A.PAYTYPE,B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A,SYS_DICTIONARY B WHERE B.GROUP_ID = 'GATHER_TYPE' AND A.GATHER_TYPE=B.ID";
		TParm patType = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < patType.getCount(); i++) {
			if(Math.abs(parm.getDouble(patType.getValue("PAYTYPE", i))) > 0){
				tableParm.addData("PAYTYPE_CODE", patType.getValue("GATHER_TYPE", i));
				tableParm.addData("PAYTYPE_DESC", patType.getValue("CHN_DESC", i));
				tableParm.addData("PAYTYPE_AMT", Math.abs(parm.getDouble(patType.getValue("PAYTYPE", i))));	
				int row =Integer.parseInt(patType.getValue("PAYTYPE", i).substring(patType.getValue("PAYTYPE", i).length()-2)) ;
				tableParm.addData("REMARKS", parm.getValue("MEMO"+row));	
				if("刷卡".equals(patType.getValue("CHN_DESC", i))){
					tableParm.addData("CARD_TYPE", parm.getValue("CARD_TYPE"));	
				}else{
					tableParm.addData("CARD_TYPE", "");	
				}
				
			}
		}
		tableParm.setCount(tableParm.getCount("PAYTYPE_AMT"));
		return tableParm;
		
	}

}
