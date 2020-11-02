package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p> Title: </p>
 * 
 * <p> Description: </p>
 * 
 * <p> Copyright: Copyright (c) 2008 </p>
 * 
 * <p> Company: </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class EMRMEDDataControl extends TControl {
	private String caseNo;
	private static String TABLE = "TABLE";
	private static String TABLE1 = "TABLE1";
	private static String TABLE2 = "TABLE2";
	private TParm medDaParm = new TParm();
	private static String TABLEPANLE = "TABLEPANE";

	/**
	 * ��ʼ��
	 */
	public void onInit() {
	    medDaParm = this.getInputParm();
		if (medDaParm != null) {
			this.setCaseNo(medDaParm.getValue("CASE_NO"));
		}
        callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        callFunction("UI|" + TABLE1 + "|addEventListener", TABLE1 + "->" + TTableEvent.CLICKED,
                     this, "onTableClickedLis");
        callFunction("UI|" + TABLE2 + "|addEventListener", TABLE2 + "->" + TTableEvent.CLICKED,
                     this, "onTableClickedRis");
        
        //add by huangtt 20151109 start ����߳�
        boolean isDedug=true; //add by huangtt 20160505 ��־���
        try {
			
		
        class TableThread extends Thread{
        	
        	EMRMEDDataControl t;
        	
        	public TableThread(EMRMEDDataControl t){
        		this.t = t;
        	}
        	@Override
        	public void run(){
        		t.initPage();// ��ʼ��ҳ��
        	}
        }
        
        TableThread tableThread = new TableThread(this);
        tableThread.start();
        
        } catch (Exception e) {
			// TODO: handle exception
        	if(isDedug){  
				System.out.println(" come in class: EMRMEDDataControl ��method ��onInit-TableThread");
				e.printStackTrace();
			}
		}
      //add by huangtt 20151109 start ����߳�
	}
	
	/**
     * ��ʼ������
     */
    public void initPage() {
    	this.getTComboBox("YX").setSelectedID("1");
        // ��ѯTABLE1
        queryData();
            
    }

    /**
     * ��ѯ
     */
    public void queryData() {
        String medSql = "SELECT * FROM MED_APPLY WHERE CASE_NO='" + this.getCaseNo() + "' ";
        if (getRadioButtonFlg("LIS")) {
            medSql += " AND CAT1_TYPE='LIS'";
            getTTabbedPane(TABLEPANLE).setSelectedIndex(0);
            getTTabbedPane(TABLEPANLE).setEnabledAt(0, true);
            getTTabbedPane(TABLEPANLE).setEnabledAt(1, false);
            this.getTComboBox("YX").setSelectedID("1");
            this.getTTable(TABLE1).removeRowAll();
            this.setValue("TEXT", "");
        } else {
            medSql += " AND CAT1_TYPE='RIS'";
            getTTabbedPane(TABLEPANLE).setSelectedIndex(1);
            getTTabbedPane(TABLEPANLE).setEnabledAt(0, false);
            getTTabbedPane(TABLEPANLE).setEnabledAt(1, true);
            this.getTComboBox("YX").setSelectedID("1");
            this.getTTable(TABLE2).removeRowAll();
            this.setValue("TEXT", "");
        }
        if (medDaParm.getValue("ADM_TYPE").equals("O") || medDaParm.getValue("ADM_TYPE").equals("E")) {//wanglong add 20140908
            medSql +=
                    " AND APPLICATION_NO IN(SELECT DISTINCT MED_APPLY_NO FROM OPD_ORDER WHERE CASE_NO = '#' AND MED_APPLY_NO IS NOT NULL) ";
            medSql = medSql.replaceFirst("#", this.getCaseNo());
        }
        if (medDaParm.getValue("ADM_TYPE").equals("I")) {
            medSql +=
                    " AND APPLICATION_NO IN(SELECT DISTINCT MED_APPLY_NO FROM ODI_ORDER WHERE CASE_NO = '#' AND MED_APPLY_NO IS NOT NULL) ";
            medSql = medSql.replaceFirst("#", this.getCaseNo());
        } else if (medDaParm.getValue("ADM_TYPE").equals("H")) {
            medSql +=
                    " AND APPLICATION_NO IN(SELECT DISTINCT MED_APPLY_NO FROM HRM_ORDER WHERE CASE_NO = '#' AND MED_APPLY_NO IS NOT NULL) ";
            medSql = medSql.replaceFirst("#", this.getCaseNo());
        }
        TParm parm = new TParm(TJDODBTool.getInstance().select(medSql));
        this.getTTable(TABLE).setParmValue(new TParm());        
        this.getTTable(TABLE).setParmValue(parm);
        
    }

	/**
	 * ҽ������
	 * 
	 * @param row
	 *            int
	 */
	public void onSelectCombo() {
		TTable table = this.getTTable(TABLE);
		int index = getTTabbedPane(TABLEPANLE).getSelectedIndex();
		switch (index) {
		case 0:
			TParm inparm = new TParm();
			String medSql = "SELECT * FROM MED_APPLY WHERE CASE_NO='"
					+ this.getCaseNo() + "' AND CAT1_TYPE='LIS' ";
			TParm parm = new TParm(TJDODBTool.getInstance().select(medSql));
			// ����
			if ("2".equals(this.getValueString("YX"))) {
				for (int i = 0; i < parm.getCount(); i++) {
					String cat1Type = parm.getValue("CAT1_TYPE", i);
					String applyNo = parm.getValue("APPLICATION_NO", i);
					if (this.getflg(applyNo, cat1Type))
						continue;
					inparm.addRowData(parm, i);
				}
				inparm.setCount(inparm.getCount("CASE_NO"));
			}
			// �쳣
			if ("3".equals(this.getValueString("YX"))) {
				for (int i = 0; i < parm.getCount(); i++) {
					String cat1Type = parm.getValue("CAT1_TYPE", i);
					String applyNo = parm.getValue("APPLICATION_NO", i);
					if (!this.getflg(applyNo, cat1Type))
						continue;
					inparm.addRowData(parm, i);
				}
				inparm.setCount(inparm.getCount("CASE_NO"));
			}
			if ("1".equals(this.getValueString("YX")))
				inparm.setData(parm.getData());
			this.getTTable(TABLE).setParmValue(inparm);
			this.getTTable(TABLE1).removeRowAll();
			this.setValue("TEXT", "");
			break;
		case 1:
			TParm Rinparm = new TParm();
			String RmedSql = "SELECT * FROM MED_APPLY WHERE CASE_NO='"
					+ this.getCaseNo() + "' AND CAT1_TYPE='RIS' ";
			TParm Rparm = new TParm(TJDODBTool.getInstance().select(RmedSql));
			// ����
			if ("2".equals(this.getValueString("YX"))) {
				for (int i = 0; i < Rparm.getCount(); i++) {
					String cat1Type = Rparm.getValue("CAT1_TYPE", i);
					String applyNo = Rparm.getValue("APPLICATION_NO", i);
					if (this.getflg(applyNo, cat1Type))
						continue;
					Rinparm.addRowData(Rparm, i);
				}
				Rinparm.setCount(Rinparm.getCount("CASE_NO"));
			}
			// �쳣
			if ("3".equals(this.getValueString("YX"))) {
				for (int i = 0; i < Rparm.getCount(); i++) {
					String cat1Type = Rparm.getValue("CAT1_TYPE", i);
					String applyNo = Rparm.getValue("APPLICATION_NO", i);
					if (!this.getflg(applyNo, cat1Type))
						continue;
					Rinparm.addRowData(Rparm, i);
				}
				Rinparm.setCount(Rinparm.getCount("CASE_NO"));
			}
			if ("1".equals(this.getValueString("YX"))) {
				Rinparm.setData(Rparm.getData());
			}
			this.getTTable(TABLE).setParmValue(Rinparm);
			this.getTTable(TABLE2).removeRowAll();
			this.setValue("TEXT", "");
			break;
		}
	}

	/**
	 * ҽ������
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		TTable table = this.getTTable(TABLE);
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		int index = getTTabbedPane(TABLEPANLE).getSelectedIndex();
		// ҽ��ϸ����
		String orderCat1Type = parm.getValue("CAT1_TYPE");
		// ���뵥��
		String applicationNo = parm.getValue("APPLICATION_NO");
		switch (index) {
		case 0:
			TDataStore tableLis = new TDataStore();
			tableLis.setSQL("SELECT * FROM MED_LIS_RPT WHERE CAT1_TYPE='"
					+ orderCat1Type + "' AND APPLICATION_NO='" + applicationNo
					+ "'");
			tableLis.retrieve();
			this.getTTable(TABLE1).setDataStore(tableLis);
			this.getTTable(TABLE1).setDSValue();
			break;
		case 1:
			TDataStore tableRis = new TDataStore();
			tableRis.setSQL("SELECT * FROM MED_RPTDTL WHERE CAT1_TYPE='"
					+ orderCat1Type + "' AND APPLICATION_NO='" + applicationNo
					+ "'");
			tableRis.retrieve();
			this.getTTable(TABLE2).setDataStore(tableRis);
			this.getTTable(TABLE2).setDSValue();
			break;
		}
	}

	/**
	 * 
	 * @param applyNo
	 * @return
	 */
	public boolean getflg(String applyNo, String cat1Type) {
		String sql = "";
		if (cat1Type.equals("LIS")) {
			sql = "SELECT * FROM MED_LIS_RPT WHERE CAT1_TYPE='" + cat1Type
					+ "' AND APPLICATION_NO='" + applyNo
					+ "' AND CRTCLLWLMT<>'NM'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm.getCount() > 0) {
				return true;
			}
			return false;
		} else if (cat1Type.equals("RIS")) {
			sql = "SELECT * FROM MED_RPTDTL WHERE CAT1_TYPE='" + cat1Type
					+ "' AND APPLICATION_NO='" + applyNo
					+ "' AND OUTCOME_TYPE = 'H'";//����
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm.getCount() > 0) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * ���鵥��
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedLis(int row) {
		TTable table = this.getTTable(TABLE1);
		TParm parm = table.getDataStore().getRowParm(row);
		StringBuffer str = new StringBuffer();
		if (this.getValueString("TEXT").length() != 0) {
			str.append("\n" + parm.getValue("TESTITEM_CHN_DESC") + ":");
			str.append("����ֵ:" + parm.getValue("TEST_VALUE") + " "
					+ parm.getValue("TEST_UNIT"));
		} else {
			str.append(parm.getValue("TESTITEM_CHN_DESC") + ":");
			str.append("����ֵ:" + parm.getValue("TEST_VALUE") + " "
					+ parm.getValue("TEST_UNIT"));
		}
		this.setValue("TEXT", this.getValueString("TEXT") + str);
	}

	/**
	 * ��鵥��
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedRis(int row) {
		TTable table = this.getTTable(TABLE2);
		TParm parm = table.getDataStore().getRowParm(row);
		StringBuffer str = new StringBuffer();
		if (this.getValueString("TEXT").length() != 0) {
			if (parm.getValue("OUTCOME_TYPE").length() != 0) {
				str.append("\n" + "������:" + (parm.getValue("OUTCOME_TYPE").equals("T")?"����":"����")+"\n");
			} else {
				str.append("\n");
			}
			str.append("�����ӡ��:" + parm.getValue("OUTCOME_DESCRIBE")+"\n");
			str.append("���������:" + parm.getValue("OUTCOME_CONCLUSION"));
		} else {
			if (parm.getValue("OUTCOME_TYPE").length() != 0) {
				str.append("������:" + (parm.getValue("OUTCOME_TYPE").equals("T")?"����":"����")+"\n");
			} else {
				str.append("");
			}
			str.append("�����ӡ��:" + parm.getValue("OUTCOME_DESCRIBE")+"\n");
			str.append("���������:" + parm.getValue("OUTCOME_CONCLUSION"));
		}
		this.setValue("TEXT", this.getValueString("TEXT") + str);
	}

	
	/**
	 * �иı��¼�
	 */
	public void onSelRow() {
		onTableClicked(this.getTTable(TABLE).getSelectedRow());
	}

	/**
	 * ����
	 */
	public void onFetchBack() {
		medDaParm.runListener("onReturnContent", this.getValue("TEXT"));
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("TEXT");
	}

	/**
	 * �õ�TTabbedPane
	 * 
	 * @param tag
	 *            String
	 * @return TTabbedPane
	 */
	public TTabbedPane getTTabbedPane(String tag) {
		return (TTabbedPane) this.getComponent(tag);
	}

	/**
	 * �õ��Ƿ�ѡ��
	 * 
	 * @param tag
	 *            String
	 * @return boolean
	 */
	public boolean getRadioButtonFlg(String tag) {
		return getTRadioButton(tag).isSelected();
	}

	/**
	 * �õ�TComboBox
	 * 
	 * @param tag
	 *            String
	 * @return TComboBox
	 */
	public TComboBox getTComboBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}

	/**
	 * ����TRadioButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
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

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
}
