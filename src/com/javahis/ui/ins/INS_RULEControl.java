package com.javahis.ui.ins;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.ins.INSRuleTXTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:��Ŀ�ֵ�
 * </p>
 * 
 * <p>
 * Description: ҽ����Ŀ�ֵ��Ӧ��ҩƷ
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS 2.0 (c) 2011
 * </p>
 * 
 */

public class INS_RULEControl extends TControl {
	/**
	 * ҽ��TABLE
	 */
	private String type; //��Ŀ�ֵ�����:ORDERA ҩƷ,ORDERC ����
	private TTable tableFee;// SYS_FEE����
	private TParm regionParm;// ҽ���������
	private TTable tableRule;// ҽ������
	private TTable tableHistory;// ��ʷ��¼
	private TTable tableShare;// ���ô���
	// ���һ���������� ����ƥ���ѯ���ݲ���ʹ�� ��SYS_FEE��������һ����������
	private TParm tempSysFeeParm;
	private String kssj;//��ʼʱ��
	/**
	 * ҽ����ѯTABLEֵ�ı����
	 */
	private static String TABLE_FEE = "TABLE_FEE";
	
	// ����
	private Compare compare = new Compare();
	private Compare compareOne = new Compare();
	private int sortColumnOne = -1;
	private boolean ascendingOne = false;
	private int sortColumn = -1;
	private boolean ascending = false;

	public void onInit() { // ��ʼ������
		super.onInit();
		type = (String) this.getParameter();//��Ŀ�ֵ�����:ORDERA ҩƷ,ORDERC ����
		//��ʼ������title
		initParm();		
		tableFee = (TTable) this.getComponent("TABLE_FEE");//SYS_FEE�շ�����
		tableRule = (TTable) this.getComponent("TABLE_RULE");//ҽ������
		tableHistory = (TTable) this.getComponent("TABLE_HISTORY");//��ʷ��¼����
		tableShare = (TTable) this.getComponent("TABLE_SHARE");//���ô�������
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());//���ҽ���������
		//�����ӿ�������
		getTempSysParm();
		callFunction("UI|save|setEnabled", false);//���水ť�ûҲ���ʹ��
		callFunction("UI|query|setEnabled", false);//��ѯ��ť�ûҲ���ʹ��
		callFunction("UI|removeUpdate|setEnabled", false);//�Ƴ��޸�ҽ����ť�ûҲ���ʹ��
		callFunction("UI|SAVE_BUTTON|setEnabled",false );//����ѡ�в���ʹ��
		this.setValue("ADM_DATE", SystemTool.getInstance().getDate());//��ѯʱ�䣨������ʾ��
		//��ù��ô�������
		getsharedata();
		//���SYS_FEE����
		GetOrderInf("","");
		//���������ʷ����
		callFunction("UI|TABLE_FEE|addEventListener", "TABLE_FEE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		//˫����TABLE_RULE�л����Ŀ�����ҽ������
		callFunction("UI|TABLE_RULE|addEventListener", "TABLE_RULE->"
				+ TTableEvent.DOUBLE_CLICKED, this, "ondoubleTableClicked");
		//˫����TABLE_SHARE�л����Ŀ����
		callFunction("UI|TABLE_SHARE|addEventListener", "TABLE_SHARE->"
				+ TTableEvent.DOUBLE_CLICKED, this, "ondoubleshareTableClicked");
		addListener(tableFee);//SYS_FEE��������
		addListenerOne(tableRule);//SYS_RULE��������
	}
   //��ʼ������title
	private void initParm() {
		TParm parm = new TParm();
		if ("ORDERA".equalsIgnoreCase(type)) {
			this.setTitle("��Ŀ�ֵ�ҩƷ");
		} else if ("ORDERC".equalsIgnoreCase(type)) {
			this.setTitle("��Ŀ�ֵ�����");
		}
	}
	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	/**
	 * �����ӿ�������
	 */
	private void getTempSysParm() {
		String[] tempSysFee = {"ORDER_CODE", "ORDER_DESC",
				"NHI_CODE_I", "NHI_CODE_O", "NHI_CODE_E", "NHI_FEE_DESC",
				"NHI_PRICE", "OWN_PRICE", "DOSE_CHN_DESC","SPECIFICATION", "HYGIENE_TRADE_CODE","MAN_CODE" };
		tempSysFeeParm = new TParm();
		tempSysFeeParm.setCount(tempSysFee.length);
	}
	/**
	 * �Ƴ��������
	 */
	private void removeTable() {
		tableFee.setParmValue(new TParm());
		tableRule.setParmValue(new TParm());
		tableHistory.setParmValue(new TParm());
		tableFee.removeRowAll();
		tableRule.removeRowAll();
		tableHistory.removeRowAll();
	}

	/**
	 * �������
	 */
	public void onSave() {
		String type = "";//�����ж���ʷ������β���
		int Row = tableFee.getSelectedRow();//����
		//��û�����ݷ���
		if (Row < 0) 
		    return;
		//ҽ������
		if(checkNhiCode(Row))
		    return;
		TParm parm = tableFee.getParmValue().getRow(Row);//���SYS_FEE����
		String startdate = getTableHistorystartdate(parm.getValue("ORDER_CODE"));
		startdate = startdate.substring(0,8);//�����ʷ��Ч���ݵĿ�ʼʱ��
		//���ҽ����ʼʱ�䣨˫��TABLE_RULE�л�ý�TABLE_RULE�еĿ�ʼʱ�丳ֵ���趨�޸���������
		String kssj = parm.getValue("DATE").substring(0, 10).replace("/", "");
//		System.out.println("enddate==="+startdate);
//		System.out.println("kssj==="+kssj);
		String date  =kssj.substring(0, 4)+"-"+kssj.substring(4, 6)
		              +"-"+kssj.substring(6, 8)+" 00:00:00";//ת�����ͳ�YYYY-MM-DD 00:00:00
		Timestamp yesterday = StringTool.rollDate(
				StringTool.getTimestamp(date, "yyyy-MM-dd HH:mm:ss"), -1);
		String kssjyesterday = StringTool.getString(yesterday, "yyyyMMdd");//ҽ����ʼʱ��ǰһ��
//		System.out.println("kssjyesterday==="+kssjyesterday);
//    	System.out.println("parm===0"+parm);
		//����ʷ��Ч���ݵĿ�ʼʱ��>=ҽ��ʱ��ִ�и��²�����ִ֮�в������
    	if(Double.parseDouble(startdate)>=Double.parseDouble(kssj))
    		type ="update";	
    	else
    		type ="insert";	
    	parm.setData("OPT_USER", Operator.getID());//������Ա
    	parm.setData("OPT_TERM", Operator.getIP());//������ַ
    	parm.setData("INSDATE", kssj);//ҽ����ʼʱ��
    	parm.setData("YESTERDAYDATE", kssjyesterday);//ҽ����ʼʱ��ǰһ��
    	parm.setData("TYPE", type);//�ж���ʷ������β���
//    	System.out.println("parm===1"+parm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSRuleTXAction", "Save", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0001");//����ʧ��
		} else {
			this.messageBox("P0001");//����ɹ�
		}
		String smrj = this.getValueString("XMRJ");//��ý���ƴ����ѯ����	
		String datejm = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "ADM_DATE")), "yyyyMMdd"); //�õ�����Ĳ�ѯʱ��	
		//���sys_fee����
		GetOrderInf(smrj,datejm);
		//�����ʷ����
		getTableHistory(parm.getValue("ORDER_CODE"));
		
	}
	/**
	 * ҽ������
	 */
	private boolean checkNhiCode(int row){
		TParm feeParm = tableFee.getParmValue();
	    if(feeParm.getValue("NHI_CODE_I",row).trim().length() == 0 ||
	       feeParm.getValue("NHI_CODE_O",row).trim().length() == 0 ||
	       feeParm.getValue("NHI_CODE_E",row).trim().length() == 0){
	      messageBox("ҽ���벻��Ϊ��");
	      return true;
	    }
	    if(feeParm.getValue("NHI_CODE_I",row).trim().length() > 6 ||
	       feeParm.getValue("NHI_CODE_O",row).trim().length() > 6 ||
	       feeParm.getValue("NHI_CODE_E",row).trim().length() > 6){
	      messageBox("ҽ���볤�Ȳ��ܳ�����λ");
	      return true;	      
	    }
	    return false; 
	}
	/**
	 * �����ʷ����
	 * @param order
	 */
	public void onTableClicked(int row){
		TParm sysFeeParm = tableFee.getParmValue();// SYS_FEEҽ��
		//�����ʷ����
		getTableHistory(sysFeeParm.getValue("ORDER_CODE", row));
	}
	/**
	 * ��TABLE_RULE�л����Ŀ�����ҽ������
	 * @param order
	 */
	public void ondoubleTableClicked(int row){
		int rowFee = tableFee.getSelectedRow();// SYS_FEEҽ�����ݻ��ѡ����	
		if (rowFee < 0) {
			this.messageBox("��ѡ��һ����Ҫ�޸ĵ�ҽ��");
			return;
		}
		TParm ruleParm = tableRule.getParmValue();// ҽ������
		TParm sysFeeParm = tableFee.getParmValue();// SYS_FEEҽ��
		//���»��ҽ�����ҽ������
		sysFeeParm.setData("NHI_CODE_I", rowFee, ruleParm.getValue("SFXMBM",
				row));//סԺҽ����
		sysFeeParm.setData("NHI_CODE_O", rowFee, ruleParm.getValue("SFXMBM",
				row));//����ҽ����
		sysFeeParm.setData("NHI_CODE_E", rowFee, ruleParm.getValue("SFXMBM",
				row));//����ҽ����
		sysFeeParm.setData("NHI_FEE_DESC", rowFee, ruleParm.getValue("XMMC",
				row));//ҽ������
		String kssj = ruleParm.getValue("KSSJ",row).substring(0, 10); //ҽ����ʼʱ��
		sysFeeParm.setData("DATE",rowFee,kssj);//��ҽ����ʼʱ�丳ֵ���趨�޸�������λ	
		tableFee.setParmValue(sysFeeParm);// ���¸�ֵ
		tableFee.setSelectedRow(rowFee);// ѡ�е�ǰ��
		tableFee.setLockColumns("0,1,6,7,8,9,10,11,12,13");//ҽ��������޸�
		callFunction("UI|SAVE_BUTTON|setEnabled", true);// ����ѡ�п���ʹ��
	}
	/**
	 * ��TABLE_SHARE�л����Ŀ����
	 * @param order
	 */
	public void ondoubleshareTableClicked(int row){
		int rowFee = tableFee.getSelectedRow();// SYS_FEEҽ�����ݻ��ѡ����	
		if (rowFee < 0) {
			this.messageBox("��ѡ��һ����Ҫ�޸ĵ�ҽ��");
			return;
		}
		TParm ruleParm = tableShare.getParmValue();// ��������
		TParm sysFeeParm = tableFee.getParmValue();// SYS_FEEҽ��
		//���»��ҽ����
		sysFeeParm.setData("NHI_CODE_I", rowFee, ruleParm.getValue("SFXMBM",
				row));//סԺҽ����
		sysFeeParm.setData("NHI_CODE_O", rowFee, ruleParm.getValue("SFXMBM",
				row));//����ҽ����
		sysFeeParm.setData("NHI_CODE_E", rowFee, ruleParm.getValue("SFXMBM",
				row));//����ҽ����	
		 String now = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy/MM/dd"); //�õ���ǰ��ʱ��
		 sysFeeParm.setData("DATE",rowFee,now);//����ǰ��ʱ�丳ֵ���趨�޸�������λ
		tableFee.setParmValue(sysFeeParm);// ���¸�ֵ
		tableFee.setSelectedRow(rowFee);// ѡ�е�ǰ��
		callFunction("UI|SAVE_BUTTON|setEnabled", true);// ����ѡ�п���ʹ��
		tableFee.setLockColumns("0,1,2,3,4,6,7,8,9,10,11,12,13");//ҽ���벻���޸�
	}
	/**
	 * ��ʷ���ݲ�ѯ
	 * 
	 * @param order
	 */
	private void getTableHistory(String order) {
		String sql = " SELECT ORDER_CODE, ORDER_DESC, "
				+ " DESCRIPTION, SPECIFICATION,MAN_CODE, "
				+ " OWN_PRICE, NHI_PRICE,HYGIENE_TRADE_CODE, "
				+ " UNIT_CODE,NHI_FEE_DESC,  "
				+ " IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG, "
				+ " EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, "
				+ " ADDPAY_AMT, NHI_CODE_O, NHI_CODE_E, " + " NHI_CODE_I, "
				+ " SYS_GRUG_CLASS, NOADDTION_FLG, "
				+ " SYS_PHA_CLASS,START_DATE,END_DATE "
				+ " FROM SYS_FEE_HISTORY  WHERE  " + " ORDER_CODE = '" + order
				+ "' ORDER BY START_DATE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0116");//û������
			return;
		}
		tableHistory.setParmValue(result);
	}
	/**
	 * ��ѯ��ʷ������Ч����
	 * ��99991231125959��һ�ʣ�
	 * @param order
	 */
	private String getTableHistorystartdate(String order) {
		//��õ�ǰʱ��
		String sysdate =StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMddHHmmss");
//		System.out.println("sysdate"+sysdate);
		String enddate = "";
			String sql = " SELECT START_DATE "+ 
			             " FROM SYS_FEE_HISTORY " +
					     " WHERE ORDER_CODE = '" + order+ "' " +
					     " AND '" + sysdate+ "'BETWEEN START_DATE AND END_DATE "+
					     " ORDER BY START_DATE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		enddate = result.getValue("START_DATE",0);
		if (result.getErrCode() < 0) {
			this.messageBox("E0116");//û������
			return null;
		}
		return enddate;
	}
	/**
	 * �Ƴ�ѡ�е�ҽ�� ҽ������
	 */
	public void onRemoveUpdate() {
		TParm parm = tableFee.getParmValue();// ��ƥ��ҽ������
		if (null == parm) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			return;
		}
		boolean flg = false;
		// TParm exeParm=new TParm();//ִ�е�����
		StringBuffer exeOrderCode = new StringBuffer();// ִ�е�����
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getBoolean("FLG", i)) {// ѡ������
				flg = true;
				exeOrderCode
						.append("'" + parm.getValue("ORDER_CODE", i) + "',");
			}
		}
		if (!flg) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			return;
		}
		if (this.messageBox("��ʾ", "�Ƿ�ִ���Ƴ�����", 2) != 0) {
			return;
		}
		String exeOrder = exeOrderCode.toString().substring(0,
				exeOrderCode.toString().lastIndexOf(","));// ִ�е�����ȥ��ĩβ","
		// ִ��sql
		String sql = " UPDATE SYS_FEE SET NHI_CODE_O=NULL,NHI_CODE_E=NULL,NHI_CODE_I=NULL,NHI_FEE_DESC=NULL,"
				+ "NHI_PRICE = NULL,INSPAY_TYPE = NULL, OPT_USER='"
				+ Operator.getID()
				+ "',OPT_DATE=SYSDATE"
				+ ", OPT_TERM='"
				+ Operator.getIP()
				+ "' WHERE  ORDER_CODE IN ("
				+ exeOrder
				+ ")";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));// �޸��Ƴ�����
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
		} else {
			this.messageBox("P0005");// ִ�гɹ�
//			onQuery();
		}
	}

	/**
	 * ��ղ���
	 */
	public void onClear() {
		removeTable();
		callFunction("UI|removeUpdate|setEnabled", true);// ��������
		this.setValue("XMRJ", "");//������ƴ����ѯ����Ϊ��
		callFunction("UI|XMRJ|setEnabled", true);//������ƴ����ѯ�ɲ���
		this.setValue("ADM_DATE", SystemTool.getInstance().getDate());
		callFunction("UI|SAVE_BUTTON|setEnabled",false );// ����ѡ�в���ʹ��
		tableFee.setLockColumns("0,1,2,3,4,5,6,7,8,9,10,11,12,13");//ҽ���벻���޸�
		
	}

	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}

				// �����parmֵһ��
				// 1.ȡparamwֵ;
				TParm tableData = tableFee.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// 3.���ݵ������,��vector����
				// ������������;
				String tblColumnName = tableFee.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
			}
		});
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParamOne(Vector vectorTable, TParm parmTable,
			String columnNames) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		tableRule.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVectorOne(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndexOne(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListenerOne(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumnOne) {
					ascendingOne = !ascendingOne;
				} else {
					ascendingOne = true;
					sortColumnOne = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = tableRule.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVectorOne(tableData, "Data", strNames, 0);
				// 3.���ݵ������,��vector����
				// ������������;
				String tblColumnName = tableRule.getParmMap(sortColumnOne);
				// ת��parm�е���
				int col = tranParmColIndexOne(columnName, tblColumnName);
				compareOne.setDes(ascendingOne);
				compareOne.setCol(col);
				java.util.Collections.sort(vct, compareOne);
				// ��������vectorת��parm;
				cloneVectoryParamOne(vct, new TParm(), strNames);
			}
		});
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		tableFee.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}

		return index;
	}
	
	/**
	 * 
	 * @param ���sys_fee����
	 * @param ��ť
	 * @return
	 */
	public void onQuerySysfee(){
		//�ж��Ƿ�Ϊ��	
		if(checkQuery())
		   return;		
		String smrj = this.getValueString("XMRJ");//��ý���ƴ����ѯ����	
		String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "ADM_DATE")), "yyyyMMdd"); //�õ������ʱ��	
		//���sys_fee����
		GetOrderInf(smrj,date);	
	}
	/**
	 * 
	 * @param ���ins_rule����
	 * @param ��ť
	 * @return
	 */
	public void onQueryInsrule(){
		//�ж��Ƿ�Ϊ��	
		if(checkQuery())
		   return;		
		String smrj = this.getValueString("XMRJ");//��ý���ƴ����ѯ����		
		String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "ADM_DATE")), "yyyyMMdd"); //�õ������ʱ��	
		//���ins_rule������
		GetNhiOrderInf(smrj,date);	
	}
	/**
	 * 
	 * @param ��õ�ǰ��Ӧ����
	 * @param ��ť
	 * @return
	 */
	public void onQueryCorresonp(){
		String date = StringTool.getString(TCM_Transform.
				getTimestamp(getValue("ADM_DATE")), "yyyyMMdd"); //�õ������ʱ��	
		int row = tableFee.getSelectedRow();// SYS_FEEҽ�����ݻ��ѡ����
		TParm ruleParm = tableRule.getParmValue();// ҽ������
		if (row < 0) {
			this.messageBox("��ѡ��һ�ʶ�Ӧҽ��");
			tableRule.setParmValue(ruleParm);
			return;
		}
		TParm feeParm = tableFee.getParmValue();//���YS_FEEҽ������
		String nhicodeI =feeParm.getValue("NHI_CODE_I",row);//���סԺҽ����
		String nhicodeO =feeParm.getValue("NHI_CODE_O",row);//�������ҽ����
		String nhicodeE =feeParm.getValue("NHI_CODE_E",row);//��ü���ҽ����
//    	System.out.println("nhicodeI==="+nhicodeI);	
		if (nhicodeI.equals("") && nhicodeO.equals("") && nhicodeE.equals("")) {
			this.messageBox("E0116");//û������
			return;
		}
		tableRule.setParmValue(new TParm());
		tableRule.removeRowAll();
		//��õ�ǰ��Ӧ����
		onPresent(nhicodeI,nhicodeO,nhicodeE,date);
	}
	//��õ�ǰ��Ӧ����
	public void onPresent(String nhicodeI,String nhicodeO,
			String nhicodeE,String date){
		String sql =  
			" SELECT SFXMBM, XMMC, BZJG, " +
			"CASE MZYYBZ WHEN '1' THEN 'Y' ELSE 'N' END AS MZYYBZ,"+
	        "CASE ETYYBZ WHEN '1' THEN 'Y'  ELSE 'N' END AS ETYYBZ,"+
	        "CASE YKD242 WHEN '1' THEN 'Y' ELSE 'N' END AS YKD242,"+
	        "JX, GG,PZWH, SCQY, TO_CHAR(KSSJ,'yyyy/mm/dd HH:mm:ss') AS KSSJ, " +
	        "TO_CHAR(JSSJ,'yyyy/mm/dd HH:mm:ss') AS JSSJ" +
	        " FROM INS_RULE" +
	        " WHERE SFXMBM IN ('" + nhicodeI + "','" + nhicodeO + "'," +
	        "'" + nhicodeE + "') " +
	        " AND   TO_DATE('" + date +
	        "','YYYYMMDDHH24MISS') BETWEEN KSSJ AND JSSJ" +
	        " ORDER BY SFXMBM";	
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() < 0) {
				this.messageBox("E0116");//û������
				return;
			}
			//����ʼʱ��ͽ���ʱ������ת��
			for (int i = 0; i < result.getCount(); i++) {
				String Sdate = result.getValue("KSSJ", i).substring(0, 10);
				Sdate += " 00:00:00";
				result.setData("KSSJ", i, Sdate);
				String Edate = result.getValue("JSSJ", i).substring(0, 10);
				Edate += " 23:59:59";
				result.setData("JSSJ", i, Edate);
			}
			tableRule.setParmValue(result);					
		}
	//��ù��ô��������
	public void getsharedata() {
    String now = StringTool.getString(SystemTool.getInstance().getDate(),
                                      "yyyyMMdd"); //�õ���ǰ��ʱ��
    
		String sql =  " SELECT SFXMBM, XMMC, BZJG, " +
        "JX, GG,PZWH, SCQY, TO_CHAR(KSSJ,'yyyy/mm/dd HH:mm:ss') AS KSSJ, " +
        "TO_CHAR(JSSJ,'yyyy/mm/dd HH:mm:ss') AS JSSJ" +
        " FROM INS_RULE" +
        " WHERE (XMMC LIKE '%�Է�%' OR XMMC LIKE '%����%' OR XMMC LIKE '%�ۼ�����%')" +
        " AND   TO_DATE('" + now +
        "','YYYYMMDDHH24MISS') BETWEEN KSSJ AND JSSJ" +
        " ORDER BY SFXMBM";
	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	
	if (result.getErrCode() < 0) {
		this.messageBox("E0116");//û������
		return;
	}
	//����ʼʱ��ͽ���ʱ������ת��
	for (int i = 0; i < result.getCount(); i++) {
		String Sdate = result.getValue("KSSJ", i).substring(0, 10);
		Sdate += " 00:00:00";
		result.setData("KSSJ", i, Sdate);
		String Edate = result.getValue("JSSJ", i).substring(0, 10);
		Edate += " 23:59:59";
		result.setData("JSSJ", i, Edate);
	}
	tableShare.setParmValue(result);
	}
	//�ֱ���sys_fee��ins_rule������
	public void onQutryData() {
	//�ж��Ƿ�Ϊ��	
	if(checkQuery())
	   return;	
	String smrj = this.getValueString("XMRJ");//��ý���ƴ����ѯ����			
	String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
     "ADM_DATE")), "yyyyMMdd"); //�õ������ʱ��	
	//���sys_fee����
	GetOrderInf(smrj,date);
	//���ins_rule������
	GetNhiOrderInf(smrj,date);	
	}
	//���sys_fee����
	public void GetOrderInf(String smrj,String date) {
		 String now = StringTool.getString(SystemTool.getInstance().getDate(),
         "yyyy/MM/dd"); //�õ���ǰ��ʱ��
		String sql1="";
		String py1 ="";
		if ("ORDERA".equalsIgnoreCase(type)) {
			sql1=" AND A.ORDER_CAT1_CODE LIKE '%PHA%'";//��ѯҩƷ����	
		}
		if ("ORDERC".equalsIgnoreCase(type)) {
			sql1=" AND A.ORDER_CAT1_CODE NOT LIKE '%PHA%'";//��ѯ��������	
		}
		if(!smrj.equals(""))
		py1 =smrj.toUpperCase();//ת���ɴ�д��ʽ	
		String sql = 
		"SELECT A.ORDER_CODE,A.ORDER_DESC,A.NHI_CODE_I,A.NHI_CODE_O,A.NHI_CODE_E," +
		"A.NHI_FEE_DESC,A.NHI_PRICE,A.OWN_PRICE,C.DOSE_CHN_DESC," +
		"A.SPECIFICATION,A.HYGIENE_TRADE_CODE,A.MAN_CODE" +
	    " FROM SYS_FEE A LEFT JOIN PHA_BASE B " +
	    " ON A.ORDER_CODE = B.ORDER_CODE" +
	    " LEFT JOIN PHA_DOSE C ON B.DOSE_CODE = C.DOSE_CODE " +
	    " WHERE A.PY1 LIKE '%" + py1 + "%'" +
	    " AND A.OWN_PRICE !=0"
		+ sql1;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0116");//û������
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			result.setData("DATE",i,now);	
			
		}
		tableFee.setParmValue(result);
	}
	//���ins_rule������
	public void GetNhiOrderInf(String smrj,String date) {
		String sql =  
		" SELECT SFXMBM, XMMC, BZJG, " +
		"CASE MZYYBZ WHEN '1' THEN 'Y' ELSE 'N' END AS MZYYBZ,"+
        "CASE ETYYBZ WHEN '1' THEN 'Y'  ELSE 'N' END AS ETYYBZ,"+
        "CASE YKD242 WHEN '1' THEN 'Y' ELSE 'N' END AS YKD242,"+
        "JX, GG,PZWH, SCQY, TO_CHAR(KSSJ,'yyyy/mm/dd HH:mm:ss') AS KSSJ, " +
        "TO_CHAR(JSSJ,'yyyy/mm/dd HH:mm:ss') AS JSSJ" +
        " FROM INS_RULE" +
        " WHERE (XMRJ  LIKE  '%" + smrj.toLowerCase() + "%' OR SFXMBM = '" + smrj + "')"+
        " AND   TO_DATE('" + date +
        "','YYYYMMDDHH24MISS') BETWEEN KSSJ AND JSSJ" +
        " ORDER BY SFXMBM";	
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0116");//û������
			return;
		}
		//����ʼʱ��ͽ���ʱ������ת��
		for (int i = 0; i < result.getCount(); i++) {
			String Sdate = result.getValue("KSSJ", i).substring(0, 10);
			Sdate += " 00:00:00";
			result.setData("KSSJ", i, Sdate);
			String Edate = result.getValue("JSSJ", i).substring(0, 10);
			Edate += " 23:59:59";
			result.setData("JSSJ", i, Edate);
		}
		tableRule.setParmValue(result);	
	}
	//�ж��Ƿ�Ϊ��
	private boolean checkQuery(){
	   if(this.getValueString("XMRJ").length() == 0){
		messageBox("ƴ����ѯ����Ϊ��");
	    return true;
    }
	   if(StringTool.getString(TCM_Transform.getTimestamp(getValue(
		"ADM_DATE")), "yyyyMMdd").length() == 0){
		messageBox("��ѯʱ�䲻��Ϊ��");
		return true;
	}
	return false;
 }
	
}
