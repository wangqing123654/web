package jdo.ope;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.jws.WebService;

import jdo.bil.String2TParmTool;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import action.bil.SPCINVRecordAction;

import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.util.OdiUtil;

/**
 * <p>
 * Title: �����Խ������� werbservice�ӿ�
 * </p>
 * 
 * <p>
 * Description: �����Խ������� werbservice�ӿ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author fux 2014-5-4
 * @version 4.0
 */
@WebService
public class OPEPackageWsToolImpl implements OPEPackageWsTool {

	// /**
	// * ʵ��
	// */
	// public static OPEPackageWsToolImpl instanceObject;
	// /**
	// * �õ�ʵ��
	// * @return OPEPackageWsToolImpl
	// */
	// public static OPEPackageWsToolImpl getInstance() {
	// if (instanceObject == null)
	// instanceObject = new OPEPackageWsToolImpl();
	// return instanceObject;
	// }

	// ֻ�ܷ���web\WEB-INF\classes\jdo\ope
	// ����Ӧ����his��,��Ϊ��hisȡֵ
	@Override
	public String onSaveOpePackage(String opCode, String supTypeCode,
			String opDateS, String opDateE, String state, String optUser,
			String optTerm) {
		System.out.println("------------onSaveOpe ��ʼ-----------------");
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("<OPE_SAVE_RESULT>");
		// ========================================================
		// ====================start=====================
		String sql = "";
		String why = "";
		// 0 ���룬 1 �ų���� ��2�������
		if ("0".equals(state)) {
			why = " AND STATE = '0' ";
		} else if ("1".equals(state)) {
			why = " AND STATE = '1' ";
		} else {
			why = " AND STATE IN ('0','1','2','3','4','5','6') ";
		}
		// OPBOOK_SEQ ; OP_CODE ; SUPTYPE_CODE; OP_DATE ; STATE
		// �ų����ʱ�� APROVE_DATE �ų������Ա APROVE_USER
		// 2014-05-27
		String opDateSYearS = opDateS.substring(0, 4);
		String opDateSMonthS = opDateS.substring(5, 7);  
		String opDateSDayS = opDateS.substring(8, 10);
		String opDateSYearE = opDateE.substring(0, 4);
		String opDateSMonthE = opDateE.substring(5, 7);
		String opDateSDayE = opDateE.substring(8, 10);
		//GDVAS_CODE 1:�ɶ���  2:�Ӷ��� ����Ϊ��  
		sql = " SELECT A.OPBOOK_SEQ,A.OP_CODE1 AS OP_CODE,A.TYPE_CODE AS SUPTYPE_CODE,A.REMARK,"
				+ " A.OP_DATE,A.STATE,A.MR_NO,B.PAT_NAME,A.GDVAS_CODE "
				+ " FROM  OPE_OPBOOK A,SYS_PATINFO B "  
				+ " WHERE A.OP_DATE BETWEEN TO_DATE('"
				+ opDateSYearS
				+ opDateSMonthS
				+ opDateSDayS
				+ "000000', 'YYYYMMDDHH24MISS') "
				+ " AND TO_DATE('"
				+ opDateSYearE
				+ opDateSMonthE
				+ opDateSDayE
				+ "235959', 'YYYYMMDDHH24MISS') " + " AND A.MR_NO = B.MR_NO " +
				// " WHERE OP_DATE BETWEEN TO_DATE('20140526000000', 'YYYYMMDDHH24MISS') "
				// +
				// " AND TO_DATE('20140527235959', 'YYYYMMDDHH24MISS') " +
				why + " ORDER BY A.OPBOOK_SEQ";
		System.out.println("��������---" + sql);
		TParm selParm = new TParm();    
		String2TParmTool tool = new String2TParmTool();
		String result = "";
		if (sql.length() != 0 && !sql.equals("")) {
			selParm = new TParm(TJDODBTool.getInstance().select(sql));
			System.out.println("selParm:" + selParm);
			result = tool.tparm2String(selParm);
		}
		// pda��ֵ
		// if(selParm.getCount()>0)
		// for(int i = 0;i<selParm.getCount();i++){
		// String opbookSeq = selParm.getData("OPBOOK_SEQ",i).toString();
		// opCode = selParm.getData("OP_CODE",i).toString();
		// supTypeCode = selParm.getData("SUPTYPE_CODE",i).toString();
		// String opDate = selParm.getData("OP_DATE",i).toString();
		// //0 ���룬 1 �ų���� ��2�������
		// state = selParm.getData("STATE",i).toString();
		//                 
		// Timestamp now = TJDODBTool.getInstance().getDBTime();
		// strBuf.append("<RESULT"+i+">");
		//
		// strBuf.append("<OPBOOK_SEQ"+i+">");
		// strBuf.append(opbookSeq);
		// strBuf.append("</OPBOOK_SEQ"+i+">");
		//    			
		// strBuf.append("<OP_CODE"+i+">");
		// strBuf.append(opCode);
		// strBuf.append("</OP_CODE"+i+">");
		//    			
		// strBuf.append("<SUPTYPE_CODE"+i+">");
		// strBuf.append(supTypeCode);
		// strBuf.append("</SUPTYPE_CODE"+i+">");
		//    			
		// strBuf.append("<OP_DATE"+i+">");
		// strBuf.append(opDate);
		// strBuf.append("</OP_DATE"+i+">");
		//    	
		// strBuf.append("<STATE"+i+">");
		// strBuf.append(state);
		// strBuf.append("</STATE"+i+">");
		//    			
		// strBuf.append("<OPT_USER>");
		// strBuf.append(optUser);
		// strBuf.append("</OPT_USER>");
		//    			
		// strBuf.append("<OPT_DATE"+i+">");
		// strBuf.append(now);
		// strBuf.append("</OPT_DATE"+i+">");
		//    			
		// strBuf.append("<OPT_TERM>");
		// strBuf.append(optTerm);
		// strBuf.append("</OPT_TERM>");
		//    			
		// strBuf.append("</RESULT"+i+">");
		// }
		// System.out.println("ѭ������");
		// strBuf.append("</OPE_SAVE_RESULT>");
		// System.out.println("������----��"+strBuf.toString());
		return result;

	}

}
