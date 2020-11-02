package action.ins;

import java.sql.Timestamp;
import java.util.Map;

import jdo.ins.INSRuleTXTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ҽ������Ŀ�ֵ�:֧����׼��
 * </p>
 * 
 * <p>
 * Description:ҽ������Ŀ�ֵ�:֧����׼��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author pangben 2011-12-08
 * @version 2.0
 */
public class INSRuleTXAction extends TAction {

	public TParm insertINSRule(TParm parm) {
		String regionCode = parm.getValue("REGION_CODE");
		String XMLB = parm.getValue("XMLB");
		TParm result = null;
		TConnection connection = getConnection();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			result = INSRuleTXTool.getInstance().insertINSRule(
					getTemp(parm, regionCode, i, XMLB), connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	private TParm getTemp(TParm orderParm, String regionCode, int i, String XMLB) {
		TParm parm = new TParm();
		parm.setData("SFDLBM", regionCode);// ����
		parm.setData("SFXMBM", orderParm.getValue("ORDER_CODE", i));// ��Ŀ����
		parm.setData("XMBM", SystemTool.getInstance().getNo("ALL", "INS",
				"INSRULE_NO", "INSRULE_NO"));// �������
		parm.setData("XMMC", orderParm.getValue("ORDER_DESC", i));// ��������
		parm.setData("XMRJ", orderParm.getValue("PY1", i));// ƴ��
		parm.setData("BZJG", orderParm.getDouble("OWN_PRICE", i));// ��׼���
		parm.setData("SJJG", orderParm.getDouble("OWN_PRICE", i));// ʵ�ս��
		parm.setData("XMLB", XMLB);// ��Ŀ���
		parm.setData("JX", orderParm.getValue("DOSE_DESC", i)); // ����
		parm.setData("GG", orderParm.getValue("SPECIFICATION", i)); // ���
		parm.setData("DW", orderParm.getValue("MEDI_UNIT_DESC", i)); // ��λ:��ҩ��λ
		parm.setData("YF", orderParm.getValue("ROUTE_DESC", i)); // �÷�
		// result.setData("YL",updateNull(parmReturn.getValue("DOSE_CODE")));
		// //����
		parm.setData("SL", 1);
		parm.setData("BZ", orderParm.getValue("BZ", i));
		return parm;
	}

	/**
	 * ��ȫƥ�����ݵ�ѡ��ťѡ�в���
	 * 
	 * @return
	 */
	public TParm updateSameSave(TParm executParm) {
		// TParm ruleParm =new TParm((Map)executParm.getData("ruleParm"));
		TParm result = null;
		TParm tempParm = null;
		TParm tempHisParm = null;
		String optUser = executParm.getValue("OPT_USER");
		String optTerm = executParm.getValue("OPT_TERM");
		String kssj = executParm.getValue("START_DAY");
		String kssjIn=executParm.getValue("END_DATE_IN");//��������ݵĿ�ʼʱ��
		String insCode=executParm.getValue("INS_CODE");//��ҽ����
		String nhiFeeDesc=executParm.getValue("NHI_FEE_DESC",0);//ҽ������
		double nhiPrice=executParm.getDouble("NHI_PRICE",0);//ҽ�����
		String jssj = "99991231125959";
		// ��ʷ��¼
		TParm tableHistoryParm = executParm.getParm("tableHistoryParm");
		TConnection connection = null;
		connection = getConnection();
		//����SYS_FEE������
		
		for (int i = 0; i < executParm.getCount("ORDER_CODE"); i++) {
			tempParm = executParm.getRow(i);
			tempParm.setData("OPT_USER", optUser);
			tempParm.setData("OPT_TERM", optTerm);
			if (tempParm.getBoolean("FLG")) {
				result = INSRuleTXTool.getInstance().updateSysFeeNhi(tempParm,
						connection);
				if (result.getErrCode() < 0) {
					connection.close();
					return result;
				}

			}
		}
		//�޸�SYS_FEE_HISTORY ������
		for (int i = 0; i < tableHistoryParm.getCount("ORDER_CODE"); i++) {
			tempHisParm = tableHistoryParm.getRow(i);
			if (tempHisParm.getBoolean("FLG")) {
				String sql = "UPDATE SYS_FEE_HISTORY SET END_DATE='"
						+ tempHisParm.getValue("END_DATE") + "'"
						+ " WHERE ORDER_CODE='"
						+ tempHisParm.getValue("ORDER_CODE")
						+ "' AND START_DATE='"
						+ tempHisParm.getValue("START_DATE") + "'";
				result = new TParm(TJDODBTool.getInstance().update(sql,
						connection));
				if (result.getErrCode() < 0) {
					connection.close();
					return result;
				}
			}
		}
		//���SYS_FEE_HISTORY ������
		for (int i = 0; i < tableHistoryParm.getCount("ORDER_CODE"); i++) {
			tempHisParm = tableHistoryParm.getRow(i);
			if (tempHisParm.getBoolean("MAIN_FLG")) {
				// ������һ���Ľ���ʱ�䲻���޸ĵĿ�ʼʱ������,�� (��ʼʱ��:START_DATE :20110101
				// �޸ĵĿ�ʼʱ��20101101)
				// �����߼����һ���Ľ���ʱ��ĳɽ����235959 �����µĿ�ʼʱ��Ϊת������
				String feeSql = "SELECT  S.ORDER_CODE,  "
						+ "  S.ORDER_DESC, S.ACTIVE_FLG, S.PY1, S.PY2, S.SEQ, "
						+ "  S.DESCRIPTION, S.TRADE_ENG_DESC, S.GOODS_DESC, "
						+ "  S.GOODS_PYCODE, S.ALIAS_DESC, S.ALIAS_PYCODE, "
						+ "  S.SPECIFICATION, S.NHI_FEE_DESC, S.HABITAT_TYPE, "
						+ "  S.MAN_CODE, S.HYGIENE_TRADE_CODE, S.ORDER_CAT1_CODE, "
						+ "  S.CHARGE_HOSP_CODE, S.OWN_PRICE, S.NHI_PRICE, "
						+ "  S.GOV_PRICE, S.UNIT_CODE, S.LET_KEYIN_FLG, "
						+ "  S.DISCOUNT_FLG, S.EXPENSIVE_FLG, S.OPD_FIT_FLG, "
						+ "  S.EMG_FIT_FLG, S.IPD_FIT_FLG, S.HRM_FIT_FLG, "
						+ "  S.DR_ORDER_FLG, S.INTV_ORDER_FLG, S.LCS_CLASS_CODE, "
						+ "  S.TRANS_OUT_FLG, S.TRANS_HOSP_CODE, S.USEDEPT_CODE, "
						+ "  S.EXEC_ORDER_FLG, S.EXEC_DEPT_CODE, S.INSPAY_TYPE, "
						+ "  S.ADDPAY_RATE, S.ADDPAY_AMT, S.NHI_CODE_O, "
						+ "  S.NHI_CODE_E, S.NHI_CODE_I, S.CTRL_FLG,  S.CLPGROUP_CODE, S.ORDERSET_FLG, S.INDV_FLG, "
						+ "  S.SUB_SYSTEM_CODE, S.RPTTYPE_CODE, S.DEV_CODE,  S.OPTITEM_CODE, S.MR_CODE, S.DEGREE_CODE, "
						+ "  S.CIS_FLG, S.OPT_USER, S.OPT_DATE,  S.OPT_TERM,  "
						+ "  S.ACTION_CODE,  S.ATC_FLG, S.OWN_PRICE2, S.OWN_PRICE3, "
						+ "  S.TUBE_TYPE, S.CAT1_TYPE, S.IS_REMARK, "
						+ "  S.ATC_FLG_I, S.REMARK_1, S.REMARK_2, "
						+ "  S.REGION_CODE, S.SYS_GRUG_CLASS, S.NOADDTION_FLG,   S.SYS_PHA_CLASS"
						+ " FROM  SYS_FEE S    " + " WHERE  S.ORDER_CODE='"
						+ tempHisParm.getValue("ORDER_CODE") + "' ";
				TParm feeParm = new TParm(TJDODBTool.getInstance().select(
						feeSql));
				feeParm = feeParm.getRow(0);
				feeParm.setData("OPT_USER", optUser);
				feeParm.setData("OPT_TERM", optTerm);
				if (tempHisParm.getDouble("START_DATE") > Double
						.parseDouble(kssj)) {
					//����ʱ��Ϊ�����235959
					String sql = "UPDATE SYS_FEE_HISTORY SET END_DATE='"
							+ StringTool.getString(SystemTool.getInstance()
									.getDate(), "yyyyMMDD") + "235959" + "'"
							+ " WHERE ORDER_CODE='"
							+ tempHisParm.getValue("ORDER_CODE")
							+ "' AND START_DATE='"
							+ tempHisParm.getValue("START_DATE") + "'";
					result = new TParm(TJDODBTool.getInstance().update(sql,
							connection));

					String start = StringTool.getString(StringTool.rollDate(
							SystemTool.getInstance().getDate(), 1), "yyyyMMDD")
							+ "000000";
					feeParm.setData("START_DATE", start);//��ӵĿ�ʼʱ��Ϊת����賿���
					feeParm.setData("END_DATE", jssj);
					result = INSRuleTXTool.getInstance().saveFeeHistory(
							feeParm, connection);
					if (result.getErrCode() < 0) {
						connection.close();
						return result;
					}
				} else {
					//����������� ���һ�����ݽ���ʱ��Ϊҽ����ʼʱ����һ���235959
					String sql = "UPDATE SYS_FEE_HISTORY SET END_DATE='"
						+  kssjIn + "'"
						+ " WHERE ORDER_CODE='"
						+ tempHisParm.getValue("ORDER_CODE")
						+ "' AND START_DATE='"
						+ tempHisParm.getValue("START_DATE") + "'";
					result = new TParm(TJDODBTool.getInstance().update(sql,
							connection));
					if (result.getErrCode() < 0) {
						connection.close();
						return result;
					}
					feeParm.setData("START_DATE", kssj);//������ݿ�ʼʱ��Ϊҽ����ʼʱ��
					feeParm.setData("END_DATE", jssj);
					feeParm.setData("NHI_CODE_O", insCode);
					feeParm.setData("NHI_CODE_I", insCode);
					feeParm.setData("NHI_CODE_E", insCode);
					feeParm.setData("NHI_FEE_DESC", nhiFeeDesc);//ҽ������
					feeParm.setData("NHI_PRICE", nhiPrice);//ҽ�����
					result = INSRuleTXTool.getInstance().saveFeeHistory(
							feeParm, connection);
					if (result.getErrCode() < 0) {
						connection.close();
						return result;
					}
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * �������* 
	 * @return
	 */
	public TParm Save(TParm Parm) {
//		System.out.println("parm======action"+Parm);
		TParm result = new TParm();
		TParm tempParm = new TParm();
	    String type =Parm.getValue("TYPE");	
	    TConnection connection = null;
		connection = getConnection();
		tempParm.setData("OPT_USER", Parm.getValue("OPT_USER"));
		tempParm.setData("OPT_TERM", Parm.getValue("OPT_TERM"));
		tempParm.setData("ORDER_CODE", Parm.getValue("ORDER_CODE"));
		tempParm.setData("NHI_CODE_O", Parm.getValue("NHI_CODE_O"));
		tempParm.setData("NHI_CODE_I", Parm.getValue("NHI_CODE_I"));
		tempParm.setData("NHI_CODE_E", Parm.getValue("NHI_CODE_E"));
		tempParm.setData("NHI_FEE_DESC",Parm.getValue("NHI_FEE_DESC"));//ҽ������
//		System.out.println("tempParm======"+tempParm);
		//����SYS_FEE������
		result = INSRuleTXTool.getInstance().updateSysFee(tempParm,
				connection);
//		System.out.println("result:"+result);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
//		 System.out.println("result======0000"+result); 
		//�޸�SYS_FEE_HISTORY ������
		//��������޸�һ����Ч����
		 if(type.equals("update")){
		//����SYS_FEE_HISTORY������
		result = INSRuleTXTool.getInstance().updateSysFeeHistory(tempParm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		    }   
	     }else if(type.equals("insert")){
//	    	 System.out.println("parm======1"); 
	    	 result= insert(Parm,connection,result);
	     }	
		connection.commit();
		connection.close();
		return result;	
	}
	/**
	 * ��������޸�һ����Ч����* 
	 * @return
	 */
	public TParm insert(TParm Parm,TConnection connection,TParm result) {
//		 System.out.println("parm======2");
	    String jssj = "99991231125959";
		String optUser =Parm.getValue("OPT_USER");
		String optTerm =Parm.getValue("OPT_TERM");
		String kssj =Parm.getValue("INSDATE");//ҽ����ʼʱ��
		kssj+="000000";
//		 System.out.println("kssj======"+kssj);		
        String kssjyesterday =Parm.getValue("YESTERDAYDATE");//ҽ����ʼʱ���ǰһ��
        kssjyesterday+="235959";
//        System.out.println("kssjyesterday======"+kssjyesterday);
	    //����SYS_FEE_HISTORY�����ݲ����һ����Ч����	 
   	 String feeSql = "SELECT  S.ORDER_CODE,  "
				+ "  S.ORDER_DESC, S.ACTIVE_FLG, S.PY1, S.PY2, S.SEQ, "
				+ "  S.DESCRIPTION, S.TRADE_ENG_DESC, S.GOODS_DESC, "
				+ "  S.GOODS_PYCODE, S.ALIAS_DESC, S.ALIAS_PYCODE, "
				+ "  S.SPECIFICATION, S.NHI_FEE_DESC, S.HABITAT_TYPE, "
				+ "  S.MAN_CODE, S.HYGIENE_TRADE_CODE, S.ORDER_CAT1_CODE, "
				+ "  S.CHARGE_HOSP_CODE, S.OWN_PRICE, S.NHI_PRICE, "
				+ "  S.GOV_PRICE, S.UNIT_CODE, S.LET_KEYIN_FLG, "
				+ "  S.DISCOUNT_FLG, S.EXPENSIVE_FLG, S.OPD_FIT_FLG, "
				+ "  S.EMG_FIT_FLG, S.IPD_FIT_FLG, S.HRM_FIT_FLG, "
				+ "  S.DR_ORDER_FLG, S.INTV_ORDER_FLG, S.LCS_CLASS_CODE, "
				+ "  S.TRANS_OUT_FLG, S.TRANS_HOSP_CODE, S.USEDEPT_CODE, "
				+ "  S.EXEC_ORDER_FLG, S.EXEC_DEPT_CODE, S.INSPAY_TYPE, "
				+ "  S.ADDPAY_RATE, S.ADDPAY_AMT, S.NHI_CODE_O, "
				+ "  S.NHI_CODE_E, S.NHI_CODE_I, S.CTRL_FLG,  S.CLPGROUP_CODE, S.ORDERSET_FLG, S.INDV_FLG, "
				+ "  S.SUB_SYSTEM_CODE, S.RPTTYPE_CODE, S.DEV_CODE,  S.OPTITEM_CODE, S.MR_CODE, S.DEGREE_CODE, "
				+ "  S.CIS_FLG, S.OPT_USER, S.OPT_DATE,  S.OPT_TERM,  "
				+ "  S.ACTION_CODE,  S.ATC_FLG, S.OWN_PRICE2, S.OWN_PRICE3, "
				+ "  S.TUBE_TYPE, S.CAT1_TYPE, S.IS_REMARK, "
				+ "  S.ATC_FLG_I, S.REMARK_1, S.REMARK_2, "
				+ "  S.REGION_CODE, S.SYS_GRUG_CLASS, S.NOADDTION_FLG,   S.SYS_PHA_CLASS"
				+ " FROM  SYS_FEE S    " + " WHERE  S.ORDER_CODE='"
				+ Parm.getValue("ORDER_CODE") + "' ";
		TParm feeParm = new TParm(TJDODBTool.getInstance().select(
				feeSql));
		feeParm = feeParm.getRow(0);
//		 System.out.println("feeParm======333333333"+feeParm); 
		feeParm.setData("OPT_USER", optUser);
		feeParm.setData("OPT_TERM", optTerm);
		//����������� ���һ�����ݽ���ʱ��Ϊҽ����ʼʱ����һ���235959
		String sql = "UPDATE SYS_FEE_HISTORY SET END_DATE='" +kssjyesterday +"'"
			+ " WHERE ORDER_CODE='"
			+ Parm.getValue("ORDER_CODE")
			+ "' AND END_DATE IN ('99991231125959','99991231235959')";
		result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
//		 System.out.println("result======111"+result); 
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		feeParm.setData("START_DATE", kssj);//������ݿ�ʼʱ��Ϊҽ����ʼʱ��
		feeParm.setData("END_DATE", jssj);
		feeParm.setData("NHI_CODE_O", Parm.getValue("NHI_CODE_O"));
		feeParm.setData("NHI_CODE_I", Parm.getValue("NHI_CODE_I"));
		feeParm.setData("NHI_CODE_E", Parm.getValue("NHI_CODE_E"));
		feeParm.setData("NHI_FEE_DESC",Parm.getValue("NHI_FEE_DESC"));//ҽ������
//		 System.out.println("feeParm======5555555555"+feeParm); 
		result = INSRuleTXTool.getInstance().saveFeeHistory(
				feeParm, connection);
//		 System.out.println("result======2222"+result); 
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		return result;
	}
}
