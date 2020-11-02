package jdo.inw;

import java.sql.Timestamp;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title: סԺ��ʿվ����ODI����Tool
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH
 * @version 1.0
 */
public class InwForOdiTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	private static InwForOdiTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return PatTool
	 */
	public static InwForOdiTool getInstance() {
		if (instanceObject == null)
			instanceObject = new InwForOdiTool();
		return instanceObject;
	}

	public InwForOdiTool() {

		// ����Module�ļ�
		this.setModuleName("inw\\INWForOdiModule.x");

		onInit();

	}

	/**
	 * ���������жϿ��
	 * 
	 * @param org_code
	 *            ҩ������
	 * @param order_code
	 *            ҩƷ����
	 * @param dosage_qty
	 *            ����
	 * @return boolean
	 */
	public boolean inspectIndStock(String org_code, String order_code,
			double dosage_qty) {
		if (dosage_qty < 0)
			return false;
		if (getStockQTY(org_code, order_code) >= dosage_qty)
			return true;
		return false;
	}

	/**
	 * ����ҩ���ż�ҩƷ�����ѯҩ������
	 * 
	 * @param org_code
	 *            ҩ����
	 * @param order_code
	 *            ҩƷ����
	 * @return QTY �����
	 */
	public double getStockQTY(String org_code, String order_code) {
		if ("".equals(org_code) || "".equals(order_code)) {
			return -1;
		}
		TParm parm = new TParm();
		parm.setData("ORG_CODE", org_code);
		parm.setData("ORDER_CODE", order_code);
		TParm result = this.query("getIndStockQty", parm);
		if (result == null || result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return -1;
		}
		return result.getDouble("INDSTOCKQTY", 0);
	}

	/**
	 * ����һ���µĳ��ڴ�����ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertOdiDspnd(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("insertOdiDspnd", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����һ���µĳ��ڴ�����ODI_DSPNM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertOdiDspnm(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("insertOdiDspnM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ���ݲ�ѯ������ѯODI_ORDER������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOdiOrder(TParm parm) {
		TParm result = new TParm();
		result = query("selectOdiOrder", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���ݲ�ѯ������ѯODI_ORDER������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOdiDspnm(TParm parm) {
		TParm result = new TParm();
		result = query("selectOdiDspnm", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
     * ���ݲ�ѯ������ѯODI_DSPND������
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm selectOdiDspnD(TParm parm) {// add by wanglong 20130626
        TParm result = new TParm();
        result = query("selectOdiDspnD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
	/**
	 * ����ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * 
	 *         public TParm updateOdiOrder(TParm parm, TConnection connection) {
	 * 
	 *         TParm result = new TParm(); //ִ��module�ϵ�insert update
	 *         delete��update result = update("updateOdiOrder", parm,
	 *         connection); if (result.getErrCode() < 0) { err("ERR:" +
	 *         result.getErrCode() + result.getErrText() + result.getErrName());
	 *         return result; } return result; }
	 */
	/**
	 * ����ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiOrder(TParm parm, TConnection connection) {
		TParm result = new TParm();
		String user = parm.getValue("OPT_USER").equals("<TNULL>")?"":parm.getValue("OPT_USER");
		String ip = parm.getValue("OPT_TERM").equals("<TNULL>")?"":parm.getValue("OPT_TERM");
		double lastqty = parm.getDouble("LASTDSPN_QTY");
		double acudqty = parm.getDouble("ACUMDSPN_QTY");
		double acumqty = parm.getDouble("ACUMMEDI_QTY");
		double dosqty = parm.getDouble("DOSAGE_QTY");
		double disqty = parm.getDouble("DISPENSE_QTY");
		String dosunit = parm.getValue("DOSAGE_UNIT");
		String disunit = parm.getValue("DISPENSE_UNIT");
		String nscheckcode = parm.getValue("NS_CHECK_CODE").equals("<TNULL>")?"":parm.getValue("NS_CHECK_CODE");
		String dcnscheckcode = parm.getValue("DC_NS_CHECK_CODE").equals("<TNULL>")?"":parm.getValue("DC_NS_CHECK_CODE");
		String takemedorg = parm.getValue("TAKEMED_ORG").equals("<TNULL>")?"":parm.getValue("TAKEMED_ORG");
		String caseNo = parm.getValue("CASE_NO");
		String orderNo = parm.getValue("ORDER_NO");
		int orderSeq = parm.getInt("ORDER_SEQ");
		String optdate = "NULL";
		String lastdate = "NULL";
		String nscheckdate = "NULL";
		String dcnscheckdate = "NULL";
		if (!parm.getValue("OPT_DATE").equals(
				String.valueOf(new TNull(Timestamp.class))))
			optdate = "TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("OPT_DATE"),
							"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		if (!parm.getValue("LAST_DSPN_DATE").equals(
				String.valueOf(new TNull(Timestamp.class))))
			lastdate = "TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("LAST_DSPN_DATE"),
							"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		if (!parm.getValue("NS_CHECK_DATE").equals(
				String.valueOf(new TNull(Timestamp.class))))
			nscheckdate = "TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("NS_CHECK_DATE"),
							"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		if (!parm.getValue("DC_NS_CHECK_DATE").equals(
				String.valueOf(new TNull(Timestamp.class))))
			dcnscheckdate = "TO_DATE('"
					+ StringTool
							.getString(parm.getTimestamp("DC_NS_CHECK_DATE"),
									"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		String sql = "UPDATE ODI_ORDER SET " + "OPT_DATE=" + optdate
				+ ",OPT_USER='" + user + "',OPT_TERM='" + ip + "',"
				+ "LASTDSPN_QTY=" + lastqty + ",LAST_DSPN_DATE=" + lastdate
				+ ",ACUMDSPN_QTY=" + acudqty + ",ACUMMEDI_QTY=" + acumqty + ","
				+ "DOSAGE_QTY=" + dosqty + ",DOSAGE_UNIT='" + dosunit
				+ "',DISPENSE_QTY=" + disqty + ",DISPENSE_UNIT='" + disunit
				+ "'," + "NS_CHECK_DATE=" + nscheckdate + ",NS_CHECK_CODE='"
				+ nscheckcode + "',DC_NS_CHECK_CODE='" + dcnscheckcode
				+ "',DC_NS_CHECK_DATE=" + dcnscheckdate + ","
				+ "TEMPORARY_FLG='Y',TAKEMED_ORG='" + takemedorg + "'"
				+ " WHERE CASE_NO='" + caseNo + "' AND ORDER_NO='" + orderNo
				+ "' AND ORDER_SEQ=" + orderSeq;
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateDCToOrder(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateDCToOrder", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_DSPNM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateDCToDspnM(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateDCToDspnM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateDCToDspnD(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateDCToDspnD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_DSPNM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspnmForExec(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiDspnmForExec", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_DSPNM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspnmByIBS(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiDspnmByIBS", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspndByIBS(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiDspndByIBS", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspndForExec(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiDspndForExec", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ����ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspndForExec1(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiDspndForExec1", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_ORDERȡ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiOrderForUndoCk(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiOrderForUndoCk", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ��ODI_DSPNMȡ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm delOdiDspnmForUndoCk(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("delOdiDspnmForUndoCk", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ��ODI_DSPNDȡ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm delOdiDspndForUndoCk(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("delOdiDspndForUndoCk", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspndToNsNot(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiDspndToNsNot", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiOrderForNote(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiOrderForNote", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ����ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiOrderForSkin(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiOrderForSkin", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ����MED_NODIFY
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * yanjing 20140714
	 */
	public TParm insertIntoMedNodify(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("insertIntoMedNodify", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm updateOdiOrderU(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiOrderU", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���¼����ͼ���Ա��ʱ��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateOdidspnmLisData(TParm parm) {
		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdidspnmLisData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ������Һ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateOdidspnDBarCode(TParm parm) {
		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiDspndBarCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiOrdertakeMedOrg(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiOrdertakeMedOrg", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_DSPNM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspnmtakeMedOrg(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiDspnmtakeMedOrg", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspnDtakeMedOrg(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiDspnDtakeMedOrg", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

    /**
     * ��ѯȡҩ�������Ƿ������ҩ
     * 
     * @param parm
     * @return
     */
    public boolean existCommonOrder(TParm parm) {// add by wanglong 20130607
        if (parm == null) {
            return false;
        }
        String sql =
                "SELECT A.* FROM ODI_ORDER A, ODI_DSPNM B WHERE A.CASE_NO = B.CASE_NO "
                        + " AND A.ORDER_NO = B.ORDER_NO AND A.ORDER_SEQ = B.ORDER_SEQ "
                        + " AND B.TAKEMED_NO = '#' AND A.CASE_NO IN(#) AND A.CTRLDRUGCLASS_CODE IS NULL";
        sql = sql.replaceFirst("#", parm.getValue("TAKEMED_NO"));
        sql = sql.replaceFirst("#", parm.getValue("WHERE_1"));//add by wanglong 20130620
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            return false;
        }
        if (result.getCount() < 1) {
            return false;
        }
        return true;
    }
    
    /**
     * ��ѯȡҩ�������Ƿ�����龫ҩ��
     * 
     * @param parm
     * @return
     */
    public boolean existDrugOrder(TParm parm) {// add by wanglong 20130607
        if (parm == null) {
            return false;
        }
        String sql =
                "SELECT A.* FROM ODI_ORDER A, ODI_DSPNM B WHERE A.CASE_NO = B.CASE_NO "
                        + " AND A.ORDER_NO = B.ORDER_NO AND A.ORDER_SEQ = B.ORDER_SEQ "
                        + " AND B.TAKEMED_NO = '#' AND A.CASE_NO IN(#) AND A.CTRLDRUGCLASS_CODE IS NOT NULL";
        sql = sql.replaceFirst("#", parm.getValue("TAKEMED_NO"));
        sql = sql.replaceFirst("#", parm.getValue("WHERE_1"));//add by wanglong 20130620
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            return false;
        }
        if (result.getCount() < 1) {
            return false;
        }
        return true;
    }
    
    /**
     * ��ѯȡҩ���ŵ�ҽ������ҽʦ
     * 
     * @param parm
     * @return
     */
    public String queryOrderDrCode(TParm parm) {// add by wanglong 20130607
        if (parm == null) {
            return "";
        }
        String sql =
                "SELECT DISTINCT A.ORDER_DR_CODE FROM ODI_ORDER A, ODI_DSPNM B WHERE A.CASE_NO = B.CASE_NO "
                        + " AND A.ORDER_NO = B.ORDER_NO AND A.ORDER_SEQ = B.ORDER_SEQ "
                        + " AND B.TAKEMED_NO = '#' AND A.CASE_NO IN(#) ORDER BY ORDER_DR_CODE";
        sql = sql.replaceFirst("#", parm.getValue("TAKEMED_NO"));
        sql = sql.replaceFirst("#", parm.getValue("WHERE_1"));//add by wanglong 20130620
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            return "";
        }
        if (result.getCount() < 1) {
            return "";
        }
        return result.getValue("ORDER_DR_CODE", 0);
    }
    
    /**
     * ��ѯ����ţ�FROM ODI_ORDER��
     * @param parm
     * @return
     */
    public TParm queryMedNo(TParm parm) {// add by wanglong 20130809
        TParm result = new TParm();
        result = query("selectOdiOrderMedNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݲ����Ų�ѯ�˻��߰�������ȷ����
     * @param value ������
     * @return ����
     */
	public String selectBedNoByMrNo(String mrNo) {
		String bedNo = "";
		String sql = " SELECT BED_NO_DESC FROM SYS_BED " +
					 " WHERE MR_NO IN (SELECT MR_NO FROM SYS_BED GROUP BY MR_NO HAVING COUNT(MR_NO) > 1) " +
					 " AND BED_OCCU_FLG='Y' AND MR_NO = '" + mrNo + "'" ;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return bedNo;
        }
		bedNo = result.getValue("BED_NO_DESC", 0);
		return bedNo;
	}
	
	/**
	 * ���벹��ҽ�� add caoyong 20131107
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm getInsetDetailedAll(TParm parm) {
		
		TParm result = new TParm();
	
		result = update("insertDetail", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm updateOdiDspndForExecSkin(TParm execData,
			TConnection connection) {
		TParm result = new TParm();
		// ִ��module�ϵ�insert update delete��update
		result = update("updateOdiDspndForExecSkin", execData, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

    
}
