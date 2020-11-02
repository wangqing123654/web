package jdo.pha;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jdo.hl7.Hl7Tool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.OdiUtil;

/**
 * <p>
 * Title:�ջ��в�ҩ���ݽ����ӿ�
 * </p>
 * 
 * <p>
 * Description:�ջ��в�ҩ���ݽ����ӿ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author wangb
 * @version 1.0
 */
public class PHACHMTool extends TJDOTool {
	
	/**
	 * ʵ��
	 */
	public static PHACHMTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PHACHMTool
	 */
	public static PHACHMTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new PHACHMTool();
		}
		return instanceObject;
	}
	
    /**
     * �в�ҩ���ӿ�_����
     * 
     * @param parm TParm
     * @return TParm
     */
	public TParm executeInsert(TParm parm, Connection conn) {
		PreparedStatement pstmM = null;
		PreparedStatement pstmD = null;
		TParm result = new TParm();
		TParm parmM = parm.getParm("PARM_M");
		TParm parmD = parm.getParm("PARM_D");
		if (parmM == null) {
			result.setErr(-1, "����ʧ��", "ҽ�������ʧ��");
			return result;
		}
		
		if (parmD == null) {
			result.setErr(-1, "����ʧ��", "ҽ��ϸ���ʧ��");
			return result;
		}
		
		try {
			conn.setAutoCommit(false);
			
			// ��������
			StringBuffer sbSql = new StringBuffer();
			sbSql.append("INSERT INTO DATA_PRESCRIPTION(ID,REGISTER_ID,NAME,SEX,AGE,TELE,EMAIL,");
			sbSql.append("DEPARTMENT_NAME,DOCTOR_NAME,PRESCRIPTION_NAME,PRESCRIBE_TIME,CREATOR_NAME,CREATION_TIME,");
			sbSql.append("VALUE_SN,VALUER_NAME,VALUATION_TIME,PRICE,QUANTITY,QUANTITY_DAY,PRICE_TOTAL,");
			sbSql.append("PAYMENT_TYPE,PAYMENT_STATUS,DATA_SOURCE,DEVICE_ID,PROCESS_STATUS,DESCRIPTION) ");
			sbSql.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pstmM = conn.prepareStatement(sbSql.toString());
			pstmM.setString(1, parmM.getValue("ID"));
			pstmM.setString(2, parmM.getValue("REGISTER_ID"));
			pstmM.setString(3, parmM.getValue("NAME"));
			pstmM.setString(4, parmM.getValue("SEX"));
			pstmM.setInt(5, parmM.getInt("AGE"));
			pstmM.setString(6, parmM.getValue("TELE"));
			pstmM.setString(7, parmM.getValue("EMAIL"));
			pstmM.setString(8, parmM.getValue("DEPARTMENT_NAME"));
			pstmM.setString(9, parmM.getValue("DOCTOR_NAME"));
			pstmM.setString(10, parmM.getValue("PRESCRIPTION_NAME"));
			pstmM.setTimestamp(11, parmM.getTimestamp("PRESCRIBE_TIME"));
			pstmM.setString(12, parmM.getValue("CREATOR_NAME"));
			pstmM.setTimestamp(13, parmM.getTimestamp("CREATION_TIME"));
			pstmM.setString(14, parmM.getValue("VALUE_SN"));
			pstmM.setString(15, parmM.getValue("VALUER_NAME"));
			pstmM.setTimestamp(16, parmM.getTimestamp("VALUATION_TIME"));
			pstmM.setDouble(17, parmM.getDouble("PRICE"));
			pstmM.setFloat(18, parmM.getFloat("QUANTITY"));
			pstmM.setFloat(19, parmM.getFloat("QUANTITY_DAY"));
			pstmM.setDouble(20, parmM.getDouble("PRICE_TOTAL"));
			pstmM.setString(21, parmM.getValue("PAYMENT_TYPE"));
			pstmM.setString(22, parmM.getValue("PAYMENT_STATUS"));
			pstmM.setString(23, parmM.getValue("DATA_SOURCE"));
			pstmM.setString(24, null);
			pstmM.setString(25, parmM.getValue("PROCESS_STATUS"));
			pstmM.setString(26, parmM.getValue("DESCRIPTION"));
			
			pstmM.executeUpdate();
			
			
			// ������ϸ��
			sbSql = new StringBuffer();
			sbSql.append("INSERT INTO DATA_PRESCRIPTION_DETAIL(ID,\"NO\",GRANULE_ID,GRANULE_NAME,DOSE_HERB,EQUIVALENT,DOSE,PRICE) ");
			sbSql.append("VALUES(?,?,?,?,?,?,?,?)");
			System.out.println("==================================================");
			System.out.println(sbSql.toString());
			System.out.println(pstmD);
			pstmD = conn.prepareStatement(sbSql.toString());
			
			for (int i = 0; i < parmD.getCount("NO"); i++) {
				pstmD.setString(1, parmD.getValue("ID", i));
				pstmD.setInt(2, parmD.getInt("NO", i));
				pstmD.setString(3, parmD.getValue("GRANULE_ID", i));
				pstmD.setString(4, parmD.getValue("GRANULE_NAME", i));
				pstmD.setDouble(5, parmD.getDouble("DOSE_HERB", i));
				pstmD.setDouble(6, parmD.getDouble("EQUIVALENT", i));
				pstmD.setDouble(7, parmD.getDouble("DOSE", i));
				pstmD.setDouble(8, parmD.getDouble("PRICE", i));
				
				pstmD.addBatch();
				
			}
			pstmD.executeBatch();
			
			conn.commit();
			conn.setAutoCommit(true);
			pstmM.close();
			pstmD.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			result.setErr(-1, "����ʧ��", e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				result.setErr(-1, "����ع�ʧ��", e.getMessage());
				e1.printStackTrace();
			}
			
			// �ر�����
            if (pstmM != null) {
                try {
                	pstmM.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
            
            // �ر�����
            if (pstmD != null) {
                try {
                	pstmD.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
 
            // �ر����Ӷ���
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
            
            return result;
		} finally {
			// �ر�����
            if (pstmM != null) {
                try {
                	pstmM.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
            
            // �ر�����
            if (pstmD != null) {
                try {
                	pstmD.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
 
            // �ر����Ӷ���
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException se) {
                	se.printStackTrace();
                }
            }
		}
		
		return result;
	}
	
	/**
	 * ��ѯҽ����������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryOrderInfo(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT A.*,B.PAT_NAME AS PATNAME,");
		sbSql.append(" CASE B.SEX_CODE WHEN '1' THEN '��' WHEN '2' THEN 'Ů' ELSE '��' END AS SEX,");
		sbSql.append(" B.BIRTH_DATE AS BIRTHDATE,B.TEL_HOME,B.E_MAIL ");
		sbSql.append(" FROM OPD_ORDER A, SYS_PATINFO B WHERE 1 = 1  ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}

		if (StringUtils.isNotEmpty(parm.getValue("RX_NO"))) {
			sbSql.append(" AND RX_NO = '");
			sbSql.append(parm.getValue("RX_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("SEQ_NO"))) {
			sbSql.append(" AND SEQ_NO IN (");
			sbSql.append(parm.getValue("SEQ_NO"));
			sbSql.append(") ");
		}

		sbSql.append(" AND A.MR_NO = B.MR_NO ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	
	/**
     * ��װ�в�ҩ��������
     * 
     * @param parm TParm
     * @return TParm
     */
	public TParm getChmInsertData(TParm parm) {
		TParm chmParm = new TParm();
		TParm parmM = new TParm();
		TParm parmD = new TParm();
		// ������С�������λ
		DecimalFormat df = new DecimalFormat("0.00");
		TParm queryParm = new TParm();
		queryParm.setData("NHI_CTZ_FLG", "Y");
		// ��ѯ�������
		TParm ctzParm = this.querySysCtz(queryParm);
		// �ô���ǩ�µ�����ҩƷ����
		String orderCodes = "'"
				+ parm.getValue("ORDER_CODE").replaceAll("\\[", "").replaceAll(
						"\\]", "").replaceAll(",", "','").replaceAll(" ", "") + "'";
		queryParm.setData("ORDER_CODE", orderCodes);
		// ��ѯҩƷת����
		TParm phaTransUnitParm = this.queryPhaTransunit(queryParm);
		
		// ʵʱ����������������
		parmM.setData("AUTOID", null); // �Զ����
		parmM.setData("ID", parm.getValue("RX_NO", 0)); // ������
		parmM.setData("REGISTER_ID", ""); // �Һŵ���
		parmM.setData("NAME", parm.getValue("PATNAME", 0)); // ��������
		parmM.setData("SEX", parm.getValue("SEX", 0)); // �����Ա�
		
		// ���ݳ������ڼ��㵱ǰ����
		String age = OdiUtil.showAge(parm.getTimestamp("BIRTHDATE", 0), SystemTool
				.getInstance().getDate());
		parmM.setData("AGE", age.split("��")[0]); // ��������
		parmM.setData("TELE", parm.getValue("TEL_HOME", 0)); // ��ϵ�绰
		parmM.setData("EMAIL", parm.getValue("E_MAIL", 0)); // ����
		parmM.setData("DEPARTMENT_NAME", Hl7Tool.getInstance().getDeptDesc(
				parm.getValue("DEPT_CODE", 0))); // ����
		parmM.setData("DOCTOR_NAME", Hl7Tool.getInstance().getDrName(
				parm.getValue("DR_CODE", 0))); // ҽ��
		parmM.setData("PRESCRIPTION_NAME", ""); // Э����������
		parmM.setData("PRESCRIBE_TIME", parm.getTimestamp("ORDER_DATE", 0)); // ���ߴ���ʱ��
		parmM.setData("CREATOR_NAME", null); // ¼��Ա
		parmM.setData("CREATION_TIME", null); // ¼��ʱ��
		parmM.setData("VALUE_SN", ""); // ���۵���
		parmM.setData("VALUER_NAME", ""); // ����Ա����
		parmM.setData("VALUATION_TIME", null); // ����ʱ��
		parmM.setData("PRICE", null); // ÿ������
		parmM.setData("QUANTITY", parm.getInt("TAKE_DAYS", 0)*2); // ������������
		parmM.setData("QUANTITY_DAY", parm.getInt("TAKE_DAYS", 0)); // ������������
		
		double sumPrice = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			sumPrice = sumPrice + parm.getDouble("AR_AMT", i);
		}
		parmM.setData("PRICE_TOTAL", df.format(sumPrice)); // �����ܼ�
		
		String paymentType = "�Է�";
		// OPD_ORDER�����������ֻҪ��һ��Ϊҽ�������֧����ʽΪҽ��
		List<String> nhiCtzList = new ArrayList<String>();
		for (int i = 0; i < ctzParm.getCount("CTZ_CODE"); i++) {
			nhiCtzList.add(ctzParm.getValue("CTZ_CODE", i));
		}
		if (nhiCtzList.contains(parm.getValue("CTZ1_CODE", 0))
				|| nhiCtzList.contains(parm.getValue("CTZ2_CODE", 0))
				|| nhiCtzList.contains(parm.getValue("CTZ3_CODE", 0))) {
			paymentType = "ҽ��";
		}
		
		parmM.setData("PAYMENT_TYPE", paymentType); // ֧����ʽ
		parmM.setData("PAYMENT_STATUS", "PAYED"); // ֧��״̬
		parmM.setData("DATA_SOURCE", "HIS"); // ����������Դ
		parmM.setData("DEVICE_ID", null); // �����豸
		parmM.setData("PROCESS_STATUS", "NEW"); // ����״̬
		parmM.setData("DESCRIPTION", ""); // ˵��

		// ����
		double equivalent = 1;
		for (int i = 0; i < parm.getCount(); i++) {
			equivalent = 1;
			// ʵʱ������ϸ
			parmD.addData("ID", parm.getValue("RX_NO", i)); // ������
			parmD.addData("NO", parm.getValue("SEQ_NO", i)); // ���
			parmD.addData("GRANULE_ID", parm.getValue("ORDER_CODE", i)); // ����ID
			parmD.addData("GRANULE_NAME", parm.getValue("ORDER_DESC", i)); // ��������
			parmD.addData("DOSE_HERB", parm.getDouble("MEDI_QTY", i)); // ��Ƭ����
			
			for (int j = 0; j < phaTransUnitParm.getCount(); j++) {
				if (StringUtils.equals(parm.getValue("ORDER_CODE", i),
						phaTransUnitParm.getValue("ORDER_CODE", j))) {
					// ���ݸ�ҩƷ�ֵ�Ŀ�ҩ��ҩת���ʻ�ø�ҩƷ����
					equivalent = phaTransUnitParm.getDouble("MEDI_QTY", j);
					break;
				}
			}
			
			parmD.addData("EQUIVALENT", equivalent); // ����
			parmD.addData("DOSE", parm.getDouble("MEDI_QTY", i) / equivalent); // ����
			parmD.addData("PRICE", df.format(parm.getDouble("OWN_PRICE", i)
					* (parm.getDouble("MEDI_QTY", i) / equivalent))); // �۸�
		}
		
		chmParm.setData("PARM_M", parmM.getData());
		chmParm.setData("PARM_D", parmD.getData());
		return chmParm;
	}
	
	/**
	 * ��ѯ�������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm querySysCtz(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT * FROM SYS_CTZ WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("NHI_CTZ_FLG"))) {
			sbSql.append(" AND NHI_CTZ_FLG = '");
			sbSql.append(parm.getValue("NHI_CTZ_FLG"));
			sbSql.append("' ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
	
	/**
	 * ��ѯҩƷת����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryPhaTransunit(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT * FROM PHA_TRANSUNIT WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CODE"))) {
			sbSql.append(" AND ORDER_CODE IN (");
			sbSql.append(parm.getValue("ORDER_CODE"));
			sbSql.append(") ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}
}
