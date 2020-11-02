package jdo.ope;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jws.WebService;

import com.dongyang.Service.Server;

import com.dongyang.data.TParm;

import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 手术PDA werbservice接口
 * </p>
 * 
 * <p>
 * Description: 手术PDA werbservice接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author liuzhen 2012-6-27
 * @version 4.0
 */

@WebService
public class OPEPdaWsToolImpl implements OPEPdaWsTool {
	/** 用户登录 */
	public String login(String userID, String password) {

		String enPass = encrypt(password);

		// System.out.println("-------password---"+password);
		// System.out.println("-------enPass---"+enPass);

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String dstr = format.format(new Date());

		String sql = "SELECT count(1) AS COUNT " + "FROM SYS_OPERATOR "
				+ "WHERE USER_ID = '" + userID + "' " + "AND USER_PASSWORD='"
				+ enPass + "' " + "AND END_DATE>TO_DATE('" + dstr
				+ "','yy-mm-dd hh24:mi:ss')";

		// System.out.println("-------sql---"+sql);

		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int count = Integer.parseInt(result.getValue("COUNT", 0));
		/** 密码错误 */
		if (0 == count) {
			return "1";
		} else {
			return "0";
		}

	}

	/**
	 * 加密字串
	 * 
	 * @param text
	 *            String 源字串
	 * @return String 加密后字串
	 */
	private String encrypt(String text) {
		String av_str = "";
		try {
			byte aa[] = text.getBytes("UTF-16BE");

			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < aa.length; i++) {
				aa[i] = (byte) (~aa[i]);
				sb.append(Integer.toHexString(aa[i]).substring(6));
			}
			av_str = sb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return av_str;
	}

	/** 获取系统术间信息 */
	public String getHisRooms() {

		StringBuffer strBff = new StringBuffer();
		strBff.append("<OPE_PDA_ROOMS>");
		strBff.append("");

		String sql = "select ID,CHN_DESC from SYS_DICTIONARY where GROUP_ID = 'OPE_OPROOM'";
		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < result.getCount(); i++) {
			String id = result.getValue("ID", i);
			String chnDesc = result.getValue("CHN_DESC", i);
			strBff.append("<ROOM>");
			strBff.append("<ID>");
			strBff.append(id);
			strBff.append("</ID>");
			strBff.append("<CHN_DESC>");
			strBff.append(chnDesc);
			strBff.append("</CHN_DESC>");
			strBff.append("</ROOM>");
		}
		strBff.append("</OPE_PDA_ROOMS>");

		return strBff.toString();
	}

	/** 查询接口 */
	public String getOP_Data(String mr_No, String dateStr) {
		// 1.输入参数不合法（两个参数，具体的参数要求包括，长度，合法性。日期格式，日期限制是否是今天）
		// 2.查询过程中出错，具体出错原因javahis是否能出来
		// 3.返回状态：0成功返回，1查询出错, 2 病案号校验不通过，3日期校验不通过，4查无结果，5查出多条记录
		// 4.日志记录

		/** 校验病案号格式 */
		if (1 == this.checkStr(mr_No, "")) {
			return this.returnIncorrectMsg(2);
		}

		/** 校验日期格式 */
		if (1 == this.checkStr(dateStr, "")) {
			return this.returnIncorrectMsg(3);
		}
		String sql = "SELECT A.ROOM_NO, A.OP_DATE, A.MR_NO, A.MAIN_SURGEON AS MAIN_SURGEON_ID, "
				+ "A.BOOK_AST_1 AS BOOK_AST_1_ID, A.CIRCULE_USER1 AS CIRCULE_USER1_ID, "
				+ "A.CIRCULE_USER2 AS CIRCULE_USER2, A.ANA_USER1 AS ANA_USER1_ID, "
				+ "A.EXTRA_USER1 AS EXTRA_USER1_ID,A.REMARK,A.OPBOOK_SEQ, A.ANA_CODE,"
				+ "B.CHN_DESC AS OP_ROOM,C.BIRTH_DATE, C.HEIGHT,C.WEIGHT,C.PAT_NAME, "
				+ "F.CHN_DESC AS SEX,G.ICD_CHN_DESC,H.OPT_CHN_DESC,I.USER_NAME AS MAIN_SURGEON ,"
				+ "J.USER_NAME AS BOOK_AST_1,K.USER_NAME AS CIRCULE_USER1,L.USER_NAME AS CIRCULE_USER2,"
				+ "M.USER_NAME AS ANA_USER1,N.USER_NAME AS EXTRA_USER1 "
				+ "FROM OPE_OPBOOK A,SYS_DICTIONARY B,SYS_PATINFO C,SYS_DICTIONARY F,"
				+ "SYS_DIAGNOSIS G,SYS_OPERATIONICD H,SYS_OPERATOR  I,SYS_OPERATOR J,"
				+ "SYS_OPERATOR K,SYS_OPERATOR L,SYS_OPERATOR M,SYS_OPERATOR N  "
				+ "WHERE B.GROUP_ID = 'OPE_OPROOM'AND A.ROOM_NO = B.ID(+) "
				+ "AND A.MR_NO = C.MR_NO (+) "
				+ "AND F.GROUP_ID = 'SYS_SEX' AND C.SEX_CODE = F.ID(+) "
				+ "AND A.DIAG_CODE1 = G.ICD_CODE(+)   "
				+ "AND A.OP_CODE1 = H.OPERATION_ICD(+)  "
				+ "AND A.MAIN_SURGEON = I.USER_ID(+) "
				+ "AND A.BOOK_AST_1 = J.USER_ID(+) "
				+ "AND A.CIRCULE_USER1 = K.USER_ID(+) "
				+ "AND A.CIRCULE_USER2 = L.USER_ID(+) "
				+ "AND A.ANA_USER1 = M.USER_ID(+) "
				+ "AND A.EXTRA_USER1 = N.USER_ID(+) "
				+ "AND A.MR_NO='"
				+ mr_No
				+ "' "
				+ "AND OP_DATE BETWEEN "
				+ "TO_DATE('"
				+ dateStr
				+ " 00:00:00','yyyymmdd hh24:mi:ss')"
				+ "AND "
				+ "TO_DATE('"
				+ dateStr
				+ " 23:59:59','yyyymmdd hh24:mi:ss')"
				+ "AND A.CANCEL_FLG <> 'Y' " + "ORDER BY OPBOOK_SEQ";

		Server.autoInit(this);

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// System.out.println("---sql--"+sql);
		/** 查询中出错 */
		if (result.getErrCode() < 0) {
			return this.returnIncorrectMsg(1);
		}
		/** 查无结果 */
		if (result.getCount() < 1) {
			return this.returnIncorrectMsg(4);
		}

		/** 手术单号 */
		String opbookSeq = result.getValue("OPBOOK_SEQ", 0);
		/** 术间 */
		String opRoom = result.getValue("ROOM_NO", 0);
		/** 时间 */
		String opDate = result.getValue("OP_DATE", 0);
		/** 姓名 */
		String patName = result.getValue("PAT_NAME", 0);
		/** 性别 */
		String sex = result.getValue("SEX", 0);
		/** 生日 */
		Date birthDate = (Date) result.getData("BIRTH_DATE", 0);
		Date today = new Date();
		int age = today.getYear() - birthDate.getYear();// 年龄

		/** 身高 */
		String height = result.getValue("HEIGHT", 0);
		/** 体重 */
		String weight = result.getValue("WEIGHT", 0);
		/** 病案号 */
		String mrNo = result.getValue("MR_NO", 0);
		/** 诊断ICD */
		String icdChnDesc = result.getValue("ICD_CHN_DESC", 0);
		/** 手术ICD */
		String optChnDesc = result.getValue("OPT_CHN_DESC", 0);
		/** 备注 */
		String remark = result.getValue("REMARK", 0);

		/** 术者 */
		String mainSurgeon = result.getValue("MAIN_SURGEON", 0);
		/** 一助 */
		String bookAst1 = result.getValue("BOOK_AST_1", 0);
		/** 护士1 */
		String circuleUser1 = result.getValue("CIRCULE_USER1", 0);
		/** 护士2 */
		String circuleUser2 = result.getValue("CIRCULE_USER2", 0);
		/** 麻醉 */
		String anaUser1 = result.getValue("ANA_USER1", 0);
		/** 体外 */
		String extraUser1 = result.getValue("EXTRA_USER1", 0);

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<OPE_PDA_SEARCH_RESULT>");

		strBuf.append("<STATUS>");
		if (result.getCount() == 1) {
			strBuf.append(0);
		} else {
			strBuf.append(5);
		}
		strBuf.append("</STATUS>");

		strBuf.append("<COUNT>");
		strBuf.append(result.getCount());
		strBuf.append("</COUNT>");

		strBuf.append("<RESULT>");
		strBuf.append("<OPBOOK_SEQ>");
		strBuf.append(opbookSeq);
		strBuf.append("</OPBOOK_SEQ>");

		strBuf.append("<OP_ROOM>");
		strBuf.append(opRoom);
		strBuf.append("</OP_ROOM>");

		strBuf.append("<OP_DATE>");
		strBuf.append(opDate);
		strBuf.append("</OP_DATE>");

		strBuf.append("<PAT_NAME>");
		strBuf.append(patName);
		strBuf.append("</PAT_NAME>");

		strBuf.append("<SEX>");
		strBuf.append(sex);
		strBuf.append("</SEX>");

		strBuf.append("<AGE>");
		strBuf.append(age);
		strBuf.append("</AGE>");

		strBuf.append("<HEIGHT>");
		strBuf.append(height);
		strBuf.append("</HEIGHT>");

		strBuf.append("<WEIGHT>");
		strBuf.append(weight);
		strBuf.append("</WEIGHT>");

		strBuf.append("<MR_NO>");
		strBuf.append(mrNo);
		strBuf.append("</MR_NO>");

		strBuf.append("<ICD_CHN_DESC>");
		strBuf.append(icdChnDesc);
		strBuf.append("</ICD_CHN_DESC>");

		strBuf.append("<OPT_CHN_DESC>");
		strBuf.append(optChnDesc);
		strBuf.append("</OPT_CHN_DESC>");

		strBuf.append("<REMARK>");
		strBuf.append(remark);
		strBuf.append("</REMARK>");

		strBuf.append("<MAIN_SURGEON>");
		strBuf.append(mainSurgeon);
		strBuf.append("</MAIN_SURGEON>");

		strBuf.append("<BOOK_AST_1>");
		strBuf.append(bookAst1);
		strBuf.append("</BOOK_AST_1>");

		strBuf.append("<CIRCULE_USER1>");
		strBuf.append(circuleUser1);
		strBuf.append("</CIRCULE_USER1>");

		strBuf.append("<CIRCULE_USER2>");
		strBuf.append(circuleUser2);
		strBuf.append("</CIRCULE_USER2>");

		strBuf.append("<ANA_USER1>");
		strBuf.append(anaUser1);
		strBuf.append("</ANA_USER1>");

		strBuf.append("<EXTRA_USER1>");
		strBuf.append(extraUser1);
		strBuf.append("</EXTRA_USER1>");

		strBuf.append("</RESULT>");
		strBuf.append("</OPE_PDA_SEARCH_RESULT>");
		return strBuf.toString();
	}

	/**
	 * 查询不成功的报文 。如校验不通过时，或者查询数据库出错时 入参：返回的状态码 出参：int类型：0通过，1不通过
	 */
	private String returnIncorrectMsg(int status) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("<OPE_PDA_SEARCH_RESULT>");
		strBuf.append("<STATUS>");
		strBuf.append(String.valueOf(status));
		strBuf.append("</STATUS>");
		strBuf.append("<COUNT>");
		strBuf.append(String.valueOf(0));
		strBuf.append("</COUNT>");
		strBuf.append("<RESULT>");
		strBuf.append("<OPBOOK_SEQ></OPBOOK_SEQ>");
		strBuf.append("<OP_ROOM></OP_ROOM>");
		strBuf.append("<OP_DATE></OP_DATE>");
		strBuf.append("<PAT_NAME></PAT_NAME>");
		strBuf.append("<SEX></SEX>");
		strBuf.append("<AGE></AGE>");
		strBuf.append("<HEIGHT></HEIGHT>");
		strBuf.append("<WEIGHT></WEIGHT>");
		strBuf.append("<MR_NO></MR_NO>");
		strBuf.append("<ICD_CHN_DESC></ICD_CHN_DESC>");
		strBuf.append("<OPT_CHN_DESC></OPT_CHN_DESC>");
		strBuf.append("<REMARK></REMARK>");
		strBuf.append("<MAIN_SURGEON></MAIN_SURGEON>");
		strBuf.append("<BOOK_AST_1></BOOK_AST_1>");
		strBuf.append("<CIRCULE_USER1></CIRCULE_USER1>");
		strBuf.append("<CIRCULE_USER2></CIRCULE_USER2>");
		strBuf.append("<ANA_USER1></ANA_USER1>");
		strBuf.append("<EXTRA_USER1></EXTRA_USER1>");
		strBuf.append("</RESULT>");
		strBuf.append("</OPE_PDA_SEARCH_RESULT>");
		return strBuf.toString();
	}

	/** 保存接口 */
	// 校验参数
	// 返回状态：0 保存成功，1 保存数据库失败，2 OPBOOK_SEQ校验不通过，3 ROOM_NO校验不通过，4
	// TRANSFER_USER校验不通过，
	// 5 TRANSFER_DATE校验不通过，6 TIMEOUT_USER校验不通过，7 TIMEOUT_DATE校验不通过，
	// 8 DR_CONFORM_FLG校验不通过，9 ANA_CONFORM_FLG校验不通过
	// 日志
	public String saveOP_Data(String opeBookSeq, String roomNo,
			String transferUser, String transfer_Date, String timeoutUser,
			String timeoutDate, String drConformFlg, String anaConformFlg) {
		/** 校验opeBookSeq */
		if (this.isBlank(opeBookSeq) || 1 == this.checkStr(opeBookSeq, "")) {
			return this.returnSaveMsg(2);
		}
		/** 校验roomNo */
		if (!this.isBlank(roomNo) && 1 == this.checkStr(roomNo, "")) {
			return this.returnSaveMsg(3);
		}
		/** 校验transferUser */
		if (!this.isBlank(transferUser) && 1 == this.checkStr(transferUser, "")) {
			return this.returnSaveMsg(4);
		}
		/** 校验transfer_Date */
		if (!this.isBlank(transfer_Date)
				&& 1 == this.checkStr(transfer_Date, "")) {
			return this.returnSaveMsg(5);
		}
		/** 校验timeoutUser */
		if (!this.isBlank(timeoutUser) && 1 == this.checkStr(timeoutUser, "")) {
			return this.returnSaveMsg(6);
		}
		/** 校验timeoutDate */
		if (!this.isBlank(timeoutDate) && 1 == this.checkStr(timeoutDate, "")) {
			return this.returnSaveMsg(7);
		}
		/** 校验drConformFlg */
		if (!this.isBlank(drConformFlg) && 1 == this.checkStr(drConformFlg, "")) {
			return this.returnSaveMsg(8);
		}
		/** 校验anaConformFlg */
		if (!this.isBlank(anaConformFlg)
				&& 1 == this.checkStr(anaConformFlg, "")) {
			return this.returnSaveMsg(9);
		}

		StringBuffer strBuf = new StringBuffer();
		strBuf.append("UPDATE OPE_OPBOOK SET ");

		/** roomNo */
		if (!this.isBlank(roomNo)) {
			strBuf.append("ROOM_NO='");
			strBuf.append(roomNo);
			strBuf.append("',");
		}
		/** transferUser */
		if (!this.isBlank(transferUser)) {
			strBuf.append("TRANSFER_USER='");
			strBuf.append(transferUser);
			strBuf.append("', ");
		}
		/** transfer_Date */
		if (!this.isBlank(transfer_Date)) {
			strBuf.append("TRANSFER_DATE=TO_DATE('");
			strBuf.append(transfer_Date);
			strBuf.append("','yy-mm-dd hh24:mi:ss'),");
		}

		/** timeoutUser */
		if (!this.isBlank(timeoutUser)) {
			strBuf.append("TIMEOUT_USER='");
			strBuf.append(timeoutUser);
			strBuf.append("', ");
		}
		/** timeoutDate */
		if (!this.isBlank(timeoutDate)) {
			strBuf.append(" TIMEOUT_DATE=TO_DATE('");
			strBuf.append(timeoutDate);
			strBuf.append("','yy-mm-dd hh24:mi:ss'),");
		}
		/** drConformFlg */
		if (!this.isBlank(drConformFlg)) {
			strBuf.append("DR_CONFORM_FLG='");
			strBuf.append(drConformFlg);
			strBuf.append("',");
		}
		/** anaConformFlg */
		if (!this.isBlank(anaConformFlg)) {
			strBuf.append("ANA_CONFORM_FLG='");
			strBuf.append(anaConformFlg);
			strBuf.append("', ");
		}
		strBuf.append("OPBOOK_SEQ='");
		strBuf.append(opeBookSeq);
		strBuf.append("' ");

		strBuf.append(" WHERE OPBOOK_SEQ='");
		strBuf.append(opeBookSeq);
		strBuf.append("'");

		Server.autoInit(this);

		// System.out.println("---strBuf.toString()--:"+strBuf.toString());

		TParm result = new TParm(TJDODBTool.getInstance().update(
				strBuf.toString()));

		if (result.getErrCode() < 0) {
			return this.returnSaveMsg(1);
		}

		return this.returnSaveMsg(0);
	}

	/**
	 * 保存接口返回报文 入参：返回的状态码 出参：int类型：0通过，1不通过
	 */
	private String returnSaveMsg(int status) {

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<OPE_PAD_SAVE_RESULT>");
		strBuf.append("<STATUS>");
		strBuf.append(String.valueOf(status));
		strBuf.append("</STATUS>");
		strBuf.append("</OPE_PAD_SAVE_RESULT>");

		return strBuf.toString();
	}

	/**
	 * 校验字符串 入参：正则表达式 出参：int类型：0通过，1不通过
	 */
	private int checkStr(String str, String regMath) {
		// if(!str.matches(regMath)){
		// return 1;
		// }
		return 0;
	}

	/** 校验是否为空 */
	private boolean isBlank(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}

}
