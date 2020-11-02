package com.javahis.ui.med;

import java.sql.*;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.dongyang.ui.TTextField;
import com.javahis.system.textFormat.TextFormatRegClinicRoom;
import com.javahis.system.textFormat.TextFormatRegClinicArea;
import com.javahis.system.combo.TComboADMType;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TRadioButton;
import jdo.sys.PatTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.event.TTableEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jdo.hl7.Hl7Communications;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class MEDReSendControl extends TControl {
	/**
	 * �ż�ס��
	 */
	private String admType;
	/**
	 * ����
	 */
	private String deptCode;
	/**
	 * ����
	 */
	private String stationCode;
	/**
	 * ����
	 */
	private String clinicarea;
	/**
	 * ����
	 */
	private String clinicRoom;
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * סԺ��
	 */
	private String ipdNo;
	/**
	 * ҽ�����
	 */
	private String cat1Type;
	/**
	 * ҽ��ϸ����
	 */
	private String orderCat1Code;
	/**
	 * �����
	 */
	private String applayNo;
	/**
	 * ��ʼʱ��
	 */
	private Timestamp startDate;
	/**
	 * ��ֹʱ��
	 */
	private Timestamp endDate;
	/**
	 * ��ʼ״̬����
	 */
	private int initTypeStatus = -1;
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		Object obj = this.getParameter();
		// this.messageBox_(obj);
		if (obj != null) {
			// ����
			if (obj instanceof TParm) {
				initTypeStatus = 0;
				TParm parm = (TParm) obj;
				this.setAdmType(parm.getValue("ADM_TYPE"));
				this.setValue("ADM_TYPE", parm.getValue("ADM_TYPE"));
				this.setDeptCode(parm.getValue("DEPT_CODE"));
				this.setValue("DEPT_CODE", parm.getValue("DEPT_CODE"));
				this.setMrNo(parm.getValue("MR_NO"));
				this.setValue("MR_NO", parm.getValue("MR_NO"));
				this.setApplayNo(parm.getValue("LAB_NO"));
				this.setValue("LAB_NO", parm.getValue("LAB_NO"));
			} else {
				initTypeStatus = 1;
				/**
				 * �ż�ס��
				 */
				this.setValue("ADM_TYPE", "" + obj);
				this.setAdmType("" + obj);
			}
		}
		// else{
		// initTypeStatus = 1;
		// this.setValue("ADM_TYPE","I");
		// this.setAdmType("I");
		// }
		// ��ʼ��ҳ��
		initPage();
		/**
		 * ��ʼ���¼�
		 */
		initEvent();
	}

	/**
	 * ��ʼ���¼�
	 */
	public void initEvent() {
		getTTable(TABLE).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
	}

	/**
	 * ��ѡ�¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onCheckBoxValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		TParm tableParm = parm.getRow(row);
		String applicationNo = tableParm.getValue("APPLICATION_NO");
		if ("FLG".equals(columnName)) {
			int rowCount = parm.getCount("ORDER_DESC");
			for (int i = 0; i < rowCount; i++) {
				if (i == row)
					continue;
				if (applicationNo.equals(parm.getValue("APPLICATION_NO", i))) {
					parm.setData("FLG", i, parm.getBoolean("FLG", i) ? "N"
							: "Y");
				}
			}
			table.setParmValue(parm);
		}
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void initPage() {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp toDate = StringTool.getTimestamp(
				StringTool.getString(sysDate, "yyyy-MM-dd 00:00:00"),
				"yyyy-MM-dd HH:mm:ss");
		if (initTypeStatus == 0) {
			((TComboADMType) this.getComponent("ADM_TYPE")).setEnabled(false);
			// ��ʼʱ��
			this.setValue("START_DATE", toDate);
			// ��ֹʱ��
			this.setValue("END_DATE", StringTool.rollDate(toDate, 1));
			if ("O".equals(this.getAdmType())) {
				// ����
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// סԺ��
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
			if ("E".equals(this.getAdmType())) {
				// ����
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// סԺ��
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
			if ("I".equals(this.getAdmType())) {
				// ����
				((TextFormatRegClinicArea) this.getComponent("CLINICAREA_CODE"))
						.setEnabled(false);
				// ���
				((TextFormatRegClinicRoom) this.getComponent("CLINICROOM_NO"))
						.setEnabled(false);
			}
			if ("H".equals(this.getAdmType())) {
				// ����
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// סԺ��
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
		}
		if (initTypeStatus == 1) {
			((TComboADMType) this.getComponent("ADM_TYPE")).setEnabled(false);
			// ��ʼʱ��
			this.setValue("START_DATE", toDate);
			// ��ֹʱ��
			this.setValue("END_DATE", StringTool.rollDate(toDate, 1));
			if ("O".equals(this.getAdmType())) {
				// ����
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// סԺ��
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
			if ("E".equals(this.getAdmType())) {
				// ����
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// סԺ��
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
			if ("I".equals(this.getAdmType())) {
				// ����
				((TextFormatRegClinicArea) this.getComponent("CLINICAREA_CODE"))
						.setEnabled(false);
				// ���
				((TextFormatRegClinicRoom) this.getComponent("CLINICROOM_NO"))
						.setEnabled(false);
			}
			if ("H".equals(this.getAdmType())) {
				// ����
				((TextFormatSYSStation) this.getComponent("STATION_CODE"))
						.setEnabled(false);
				// ����
				((TextFormatRegClinicArea) this.getComponent("CLINICAREA_CODE"))
						.setEnabled(false);
				// ���
				((TextFormatRegClinicRoom) this.getComponent("CLINICROOM_NO"))
						.setEnabled(false);
				// סԺ��
				((TTextField) this.getComponent("IPD_NO")).setEditable(false);
			}
		}
	}

	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		if (this.getValueString("MR_NO").trim().length() != 0) {
			this.setValue(
					"MR_NO",
					PatTool.getInstance().checkMrno(
							this.getValueString("MR_NO")));
		}
		if (this.getValueString("IPD_NO").trim().length() != 0) {
			this.setValue(
					"IPD_NO",
					PatTool.getInstance().checkIpdno(
							this.getValueString("IPD_NO")));
		}
		String sqlStr = getSQL();
		// System.out.println(""+sqlStr);
		TParm tableParm = new TParm(this.getDBTool().select(sqlStr));
		TParm parm = new TParm();
		int count=0;
		if (this.admType.equals("I")) {
			for (int i = 0; i < tableParm.getCount(); i++) {
				TParm parmRow = tableParm.getRow(i);
				if (!this.getBillFlg(parmRow))
					continue;
				parm.addData("FLG", parmRow.getData("FLG"));
				parm.addData("PAT_NAME", parmRow.getData("PAT_NAME"));
				parm.addData("ORDER_DESC", parmRow.getData("ORDER_DESC"));
				parm.addData("ORDER_CAT1_CODE", parmRow.getData("ORDER_CAT1_CODE"));
				parm.addData("EXEC_DEPT_CODE", parmRow.getData("EXEC_DEPT_CODE"));
				parm.addData("ORDER_DR_CODE", parmRow.getData("ORDER_DR_CODE"));
				parm.addData("MR_NO", parmRow.getData("MR_NO"));
				parm.addData("IPD_NO", parmRow.getData("IPD_NO"));
				parm.addData("CASE_NO", parmRow.getData("CASE_NO"));
				parm.addData("APPLICATION_NO", parmRow.getData("APPLICATION_NO"));
				parm.addData("SEQ_NO", parmRow.getData("SEQ_NO"));
				parm.addData("ORDER_NO", parmRow.getData("ORDER_NO"));
				parm.addData("ADM_TYPE", parmRow.getData("ADM_TYPE"));
				parm.addData("CAT1_TYPE", parmRow.getData("CAT1_TYPE"));
			}
		}else{
			parm=tableParm;
		}
		// this.messageBox_(tableParm);
		this.getTTable(TABLE).setParmValue(parm);
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * �õ���ѯ���
	 * 
	 * @return String
	 */
	public String getSQL() {
		int count = 0;
		String sql = "SELECT 'N' AS FLG,PAT_NAME,ORDER_DESC,ORDER_CAT1_CODE,EXEC_DEPT_CODE,ORDER_DR_CODE,MR_NO,IPD_NO,CASE_NO,APPLICATION_NO,ORDER_NO,SEQ_NO,ADM_TYPE,CAT1_TYPE FROM MED_APPLY";
		// ҽ�����
		if (this.getValueString("CAT1_TYPE").length() != 0) {
			sql += " WHERE CAT1_TYPE='" + this.getValueString("CAT1_TYPE")
					+ "' ";
			count++;
		}
		// �ż�ס��
		if (this.getValueString("ADM_TYPE").length() != 0) {
			if (count > 0) {
				sql += " AND ADM_TYPE='" + this.getValueString("ADM_TYPE")
						+ "' ";
			} else {
				sql += " WHERE ADM_TYPE='" + this.getValueString("ADM_TYPE")
						+ "' ";
			}
			count++;
		}
		// ҽ��ϸ����
		if (this.getValueString("ORDER_CAT1_CODE").length() != 0) {
			if (count > 0) {
				sql += " AND ORDER_CAT1_CODE='"
						+ this.getValueString("ORDER_CAT1_CODE") + "' ";
			} else {
				sql += " WHERE ORDER_CAT1_CODE='"
						+ this.getValueString("ORDER_CAT1_CODE") + "' ";
			}
			count++;
		}
		// סԺ��
		if (this.getValueString("MR_NO").length() != 0) {
			if (count > 0) {
				sql += " AND MR_NO='" + this.getValueString("MR_NO") + "' ";
			} else {
				sql += " WHERE MR_NO='" + this.getValueString("MR_NO") + "' ";
			}
			count++;
		}
		// �����
		if (this.getValueString("LAB_NO").length() != 0) {
			if (count > 0) {
				sql += " AND APPLICATION_NO='" + this.getValueString("LAB_NO")
						+ "' ";
			} else {
				sql += " WHERE APPLICATION_NO='"
						+ this.getValueString("LAB_NO") + "' ";
			}
			count++;
		}
		// ����
		if (this.getValueString("DEPT_CODE").length() != 0) {
			if (count > 0) {
				sql += " AND DEPT_CODE='" + this.getValueString("DEPT_CODE")
						+ "' ";
			} else {
				sql += " WHERE DEPT_CODE='" + this.getValueString("DEPT_CODE")
						+ "' ";
			}
			count++;
		}
		// ��ʼʱ��
		if (this.getValueString("START_DATE").length() != 0
				&& this.getValueString("END_DATE").length() != 0) {
			String sDate = StringTool.getString(
					(Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss");
			String eDate = StringTool.getString(
					(Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss");
			if (count > 0) {
				sql += " AND ORDER_DATE BETWEEN TO_DATE('" + sDate
						+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate
						+ "','YYYYMMDDHH24MISS') ";
			} else {
				sql += " WHERE ORDER_DATE BETWEEN TO_DATE('" + sDate
						+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate
						+ "','YYYYMMDDHH24MISS') ";
			}
			count++;
		}
		// �ż�����
		if ("O".equals(this.getValueString("ADM_TYPE"))) {
			// ����
			if (this.getValueString("CLINICAREA_CODE").length() != 0) {
				if (count > 0) {
					sql += " AND CLINICAREA_CODE='"
							+ this.getValueString("CLINICAREA_CODE") + "' ";
				} else {
					sql += " WHERE CLINICAREA_CODE='"
							+ this.getValueString("CLINICAREA_CODE") + "' ";
				}
				count++;
			}
			// ���
			if (this.getValueString("CLINICROOM_NO").length() != 0) {
				if (count > 0) {
					sql += " AND CLINICROOM_NO='"
							+ this.getValueString("CLINICROOM_NO") + "' ";
				} else {
					sql += " WHERE CLINICROOM_NO='"
							+ this.getValueString("CLINICROOM_NO") + "' ";
				}
				count++;
			}
		}
		// סԺ����
		if ("I".equals(this.getValueString("ADM_TYPE"))) {
			// סԺ��
			if (this.getValueString("IPD_NO").length() != 0) {
				if (count > 0) {
					sql += " AND IPD_NO='" + this.getValueString("IPD_NO")
							+ "' ";
				} else {
					sql += " WHERE IPD_NO='" + this.getValueString("IPD_NO")
							+ "' ";
				}
				count++;
			}
			// ����
			if (this.getValueString("STATION_CODE").length() != 0) {
				if (count > 0) {
					sql += " AND STATION_CODE='"
							+ this.getValueString("STATION_CODE") + "' ";
				} else {
					sql += " WHERE STATION_CODE='"
							+ this.getValueString("STATION_CODE") + "' ";
				}
				count++;
			}
		}
		// �ѷ���
		if (this.getTRadioButton("SENDTRUE").isSelected()) {
			if (count > 0) {
				sql += " AND SEND_FLG >= 2 AND STATUS <> 9";
			} else {
				sql += " WHERE SEND_FLG >= 2 AND STATUS <> 9";
			}
			count++;
		}
		// δ����
		if (this.getTRadioButton("SENDFALSE").isSelected()) {
			if (count > 0) {
				sql += " AND SEND_FLG < 2 AND STATUS <> 9";
			} else {
				sql += " WHERE SEND_FLG < 2 AND STATUS <> 9";
			}

			count++;
		}
		if (!admType.equals("I")) {
			if (count > 0) {
				sql += " AND BILL_FLG='Y'";
			} else {
				sql += " WHERE  BILL_FLG='Y' ";
			}
		}
		// System.out.println("SQL=="+sql);
		// System.out.println("SQLSEND"+sql);
		return sql;
	}

	private boolean getBillFlg(TParm parm) {
		boolean flg = false;
		String caseNo = parm.getValue("CASE_NO");
		String orderNo = parm.getValue("ORDER_NO");
		String orderSeq = parm.getValue("SEQ_NO");
		String sql = " SELECT BILL_FLG FROM ODI_DSPNM WHERE CASE_NO='" + caseNo
				+ "' AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='" + orderSeq
				+ "'";
		TParm inparm = new TParm(this.getDBTool().select(sql));
		flg = inparm.getBoolean("BILL_FLG", 0);
		return flg;
	}

	/**
	 * �õ�TRdioButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	/**
	 * ����
	 */
	public void onSendRe() {
		List orderList = new ArrayList();
		TParm parm = this.getTTable(TABLE).getParmValue();
		if (parm == null)
			return;
        // ======================// add by wanglong 20130311
        Set<String> mrSet = new HashSet<String>();
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            mrSet.add(parm.getValue("CASE_NO", i));
        }
        TParm coverTparm = new TParm();
        if ("H".equals(this.getAdmType())) {
            String mrList = "";
            String sql =
                    "SELECT B.CASE_NO, A.MR_NO, A.PAT_NAME FROM HRM_CONTRACTD A, HRM_PATADM B "
                            + " WHERE A.MR_NO = B.MR_NO AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                            + " AND A.COVER_FLG <> 'Y' AND B.CASE_NO IN (#)";
            for (String string : mrSet) {
                mrList += "'" + string + "',";
            }
            mrList = mrList.substring(0, mrList.length() - 1);
            sql = sql.replaceFirst("#", mrList);
            // System.out.println("==============sql============" + sql);
            coverTparm = new TParm(TJDODBTool.getInstance().select(sql));
            if (coverTparm.getErrCode() != 0) {
                this.messageBox("��ѯ���������������");
                return;
            }
            if (coverTparm.getCount() > 0) {
                for (int i = 0; i < coverTparm.getCount(); i++) {
                    this.messageBox(coverTparm.getValue("PAT_NAME", i) + " δ�����������벻������");
                    String caseNo = coverTparm.getValue("CASE_NO", i);
                    for (int j = parm.getCount() - 1; j > -1; j--) {
                        if (parm.getValue("CASE_NO", j).equals(caseNo)) {
                            parm.removeRow(j);
                        }
                    }
                }
                parm.setCount(parm.getCount("CASE_NO"));
            }
        }
        // ===============add end
		int rowCount = parm.getCount("CASE_NO");
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
			if (!temp.getBoolean("FLG"))
				continue;
			TParm action = new TParm();
			action.setData("ADM_TYPE", temp.getValue("ADM_TYPE"));
			action.setData("CAT1_TYPE", temp.getValue("CAT1_TYPE"));
			action.setData("PAT_NAME", temp.getValue("PAT_NAME"));
			action.setData("CASE_NO", temp.getValue("CASE_NO"));
			action.setData("LAB_NO", temp.getValue("APPLICATION_NO"));
			action.setData("ORDER_NO", temp.getValue("ORDER_NO"));
			action.setData("SEQ_NO", temp.getValue("SEQ_NO"));
			action.setData("FLG", "0");
			orderList.add(action);
		}
		if (orderList.size() <= 0) {
			this.messageBox("û����Ҫ���͵���Ϣ��");
			return;
		}
		TParm result = Hl7Communications.getInstance().Hl7Message(orderList);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		} else {
			this.messageBox("���ͳɹ���");
		}
	}

	/**
	 * ���
	 */
	public void onClear() {
		clearValue("CAT1_TYPE;ORDER_CAT1_CODE;MR_NO;IPD_NO;DEPT_CODE;STATION_CODE;CLINICAREA_CODE;CLINICROOM_NO;LAB_NO;ALLCHECK");
		onInit();
		this.getTRadioButton("SENDFALSE").setSelected(true);
		this.getTTable(TABLE).removeRowAll();
	}

	/**
	 * �õ�TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * ȫѡ
	 */
	public void onAll() {
		TParm parm = this.getTTable(TABLE).getParmValue();
		int rowCount = parm.getCount();
		for (int i = 0; i < rowCount; i++) {
			if (this.getTCheckBox("ALLCHECK").isSelected())
				parm.setData("FLG", i, "Y");
			else
				parm.setData("FLG", i, "N");
		}
		this.getTTable(TABLE).setParmValue(parm);

	}

	public String getAdmType() {
		return admType;
	}

	public String getApplayNo() {
		return applayNo;
	}

	public String getCat1Type() {
		return cat1Type;
	}

	public String getClinicarea() {
		return clinicarea;
	}

	public String getClinicRoom() {
		return clinicRoom;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public String getOrderCat1Code() {
		return orderCat1Code;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public String getStationCode() {
		return stationCode;
	}

	public void setAdmType(String admType) {
		this.admType = admType;
	}

	public void setApplayNo(String applayNo) {
		this.applayNo = applayNo;
	}

	public void setCat1Type(String cat1Type) {
		this.cat1Type = cat1Type;
	}

	public void setClinicarea(String clinicarea) {
		this.clinicarea = clinicarea;
	}

	public void setClinicRoom(String clinicRoom) {
		this.clinicRoom = clinicRoom;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public void setOrderCat1Code(String orderCat1Code) {
		this.orderCat1Code = orderCat1Code;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}
}
