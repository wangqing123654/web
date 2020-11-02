package com.javahis.ui.ibs;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import jdo.ibs.IBSPreciousMedicalMaterialTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
//import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

import java.text.DecimalFormat;
import java.util.Vector;
/**
 * <p>
 * Title:��������ͳ����ϸ
 * </p>
 * 
 * <p>
 * Description:��������ͳ����ϸ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author wangzl 2012.08.09
 * @version 1.0
 */
public class IBSPreciousMedicalMaterialControl extends TControl {
	private TTable table;//��ϸ��
	private TTable table1;//���ܱ�
	private int pageFlg = 0;//ҳǩ���
	private String admType = null;//�ż�ס��
	int selectedrow;//���ܱ���ѡ�����
	private Compare compare = new Compare();
	private int sortColumn = -1;
	private boolean ascending = false;
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.setValue("ADM_TYPE", "");
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				"-", "/")
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		this.table = this.getTable("TABLE");
		this.table1 = this.getTable("TABLE1");
		// ���õ����˵�
		TParm parmIn = new TParm();
		parmIn.setData("CAT1_TYPE", "OTH");
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// Ϊ����Ӽ�������Ϊ������׼����
		addListener(table);
		addListener(table1);
	}
	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
	}
	/**
	 * ��ȡ������
	 * 
	 * @param tagName
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}
	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTTabbedPane() {
		TTabbedPane tabbedPane = ( (TTabbedPane)this.getComponent(
	            "tTabbedPane_1"));
		 if (tabbedPane.getSelectedIndex() == 0) {
	         pageFlg = 0;
	     } else {
	    	 pageFlg = 1;
	     }
	}
	/**
	 * �õ� Vector ֵ
	 * @param parm TParm
	 * @param group String
	 * @param names String
	 * @param size int
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
	 * ת��parm�е���
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
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
	 * vectoryת��param
	 * @param vectorTable Vector
	 * @param parmTable TParm
	 * @param columnNames String
	 */
	    //================start===============
//	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
//			String columnNames) {
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
    	//================end=================
		// ������->��
		// System.out.println("========names==========="+columnNames);
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
		//================start===============
		//table.setParmValue(parmTable);
		return parmTable;
		//================end=================
		// System.out.println("�����===="+parmTable);
	}
	// ==========modify-end========================================
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
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
//				TParm tableData = CLP_BSCINFO.getParmValue();
				TParm tableData = table.getShowParmValue();
				//=====������ �� "�ܼ�"��  �����봦����======
//				TParm titRowParm=new TParm();//��¼������
//				titRowParm.addRowData(table.getParmValue(), 0);
				TParm totRowParm=new TParm();//��¼���ܼơ���
				totRowParm.addRowData(table.getParmValue(), tableData.getCount()-1);
				int rowCount=tableData.getCount();//���ݵ�������������С���к��ܼ��У�
//				tableData.removeRow(0);//ȥ����һ�У������У�
				tableData.removeRow(tableData.getCount()-1);//ȥ�����һ��(�ܼ���)
				//=========================================
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
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				TParm lastResultParm=new TParm();//��¼���ս��
				TParm tmpParm=cloneVectoryParam(vct, new TParm(), strNames);
//				lastResultParm.addRowData(titRowParm, 0);//���������
				for (int k = 0; k < tmpParm.getCount(); k++) {
					lastResultParm.addRowData(tmpParm, k);//�����м�����
				}
				lastResultParm.addRowData(totRowParm, 0);//�����ܼ���
				lastResultParm.setCount(rowCount);
//				System.out.println("lastResultParm:\r\n"+lastResultParm+"\r\n\r\n");////////////////////
				table.setParmValue(lastResultParm);
				//getTMenuItem("save").setEnabled(false);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	
	/**
	 * �����Żس��¼�
	 */
	public void onMrNo() {
		if(getValue("MR_NO").equals("") || getValue("MR_NO") == null) {
			this.setValue("PAT_NAME", "");
			((TTextField)this.getComponent("PAT_NAME")).setEnabled(true);
		}
		Pat pat1 = Pat.onQueryByMrNo(this.getValueString("MR_NO").trim());
		String mrNo = pat1.getMrNo();
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		this.setValue("MR_NO", mrNo);
		this.setValue("PAT_NAME", pat1.getName());
		((TTextField)this.getComponent("PAT_NAME")).setEnabled(false);
	}
	
	/**
	 * ��ѯ����
	 * 
	 * @param tag
	 * @param obj
	 */ 
	public void onQuery() {
		int sumRow = -1;
		TTextFormat startDateComp = (TTextFormat) this
				.getComponent("START_DATE");
		TTextFormat endDateComp = (TTextFormat) this.getComponent("END_DATE");
		admType = this.getValue("ADM_TYPE").toString();
		TParm parm = new TParm();
		String startDate = StringTool.getString((Timestamp) startDateComp
				.getValue(), "yyyyMMddHHmmss");
		String endDate = StringTool.getString((Timestamp) endDateComp
				.getValue(), "yyyyMMddHHmmss");
		parm.setData("startDate", startDate);
		parm.setData("endDate", endDate);
		parm.setData("SUPPLIES_TYPE", this.getValueString("SUPPLIES_TYPE"));
		parm.setData("MR_NO", this.getValue("MR_NO"));
		parm.setData("EXE_DEPT_CODE", this.getValue("�ɱ�������������_0"));
		parm.setData("ADM_TYPE", admType);
		parm.setData("ORDER_CODE", this.getValue("ORDER_CODE"));
		TParm result = new TParm(); 
		//========yanjing modify 
		//��ϸ���ݲ�ѯ
		if(pageFlg == 0) {
			if(admType == null || admType.equals("")) {
				//�趨��ѯ�������ݵ�����
				int i_numCount = 0;//סԺ
				int e_numCount = 0;//�ż���
				int h_numCount = 0;//����
				parm.setData("ADM_TYPE","E");//�ż���
				TParm e_result = IBSPreciousMedicalMaterialTool.getInstance().onQuery(parm);
				if(!(e_result.getCount() < 0)){
					e_numCount = e_result.getCount();
				}
				for(int i = 0;i<e_numCount;i++){
                	result.setRowData(i, e_result.getRow(i));
//                	result.addRowData(e_result.getRow(i), i);
                }
				parm.setData("ADM_TYPE","I");//סԺ
				TParm i_result = IBSPreciousMedicalMaterialTool.getInstance().onQuery(parm);
				if(!(i_result.getCount() < 0)){
					i_numCount = i_result.getCount();
				}
				
				for(int i = e_numCount;i<e_numCount+i_numCount;i++){
                	result.setRowData(i, i_result.getRow(i-e_numCount));
//                	result.addRowData(i_result.getRow(i-e_result.getCount()), i);
                }
				parm.setData("ADM_TYPE","H");//����
				TParm h_result = IBSPreciousMedicalMaterialTool.getInstance().onQuery(parm);
				if(!(h_result.getCount() < 0)){
					h_numCount = h_result.getCount();
				}
				for(int i = e_numCount+i_numCount;i<e_numCount+i_numCount+h_numCount;i++){
                	result.setRowData(i, h_result.getRow(i-e_numCount-i_numCount));
//                	result.addRowData(h_result.getRow(i-e_result.getCount()-i_result.getCount()),i);
                	if (result.getCount("ORDER_CODE") < 0 && e_result.getCount()<0 && i_result.getCount()<0 && h_result.getCount()<0) {
            			messageBox("��������");
            			TParm resultparm = new TParm();
            			this.table.setParmValue(resultparm);
            			this.table1.setParmValue(resultparm);
            			return;
            		}
                }
			}else{	
				//����ѡ����ż�ס���ѯ��Ӧ����ϸ
			parm.setData("ADM_TYPE", admType);
			result = IBSPreciousMedicalMaterialTool.getInstance().onQuery(parm);
			if (result.getCount("ORDER_CODE") < 0 ) {
				messageBox("��������");
				TParm resultparm = new TParm();
				this.table.setParmValue(resultparm);
				this.table1.setParmValue(resultparm);
				return;
			}
			}
			//�������ݲ�ѯ
		} else if(pageFlg == 1){
			//�ż�ס��Ϊ�գ����ż��סԺ�ͽ���ȫ��
			if(admType == null || admType.equals("")) {
				//�趨��ѯ�������ݵ�����
				int i_numCount = 0;//סԺ
				int e_numCount = 0;//�ż���
				int h_numCount = 0;//����
				parm.setData("ADM_TYPE","E");//�ż���
				TParm e_result = IBSPreciousMedicalMaterialTool.getInstance().onQuery1(parm);
				if(!(e_result.getCount() < 0)){
					e_numCount = e_result.getCount();
				}
				for(int i = 0;i<e_numCount;i++){
                	result.setRowData(i, e_result.getRow(i));
//                	result.addRowData(e_result.getRow(i), i);
                }
				parm.setData("ADM_TYPE","I");//סԺ
				TParm i_result = IBSPreciousMedicalMaterialTool.getInstance().onQuery1(parm);
				if(!(i_result.getCount() < 0)){
					i_numCount = i_result.getCount();
				}
				for(int i = e_numCount;i<e_numCount+i_numCount;i++){
                	result.setRowData(i, i_result.getRow(i-e_numCount));
//                	result.addRowData(i_result.getRow(i-e_result.getCount()), i);
                }
				parm.setData("ADM_TYPE","H");//����
				TParm h_result = IBSPreciousMedicalMaterialTool.getInstance().onQuery1(parm);
				if(!(h_result.getCount() < 0)){
					h_numCount = h_result.getCount();
				}
				for(int i = e_numCount+i_numCount;i<e_numCount+i_numCount+h_numCount;i++){
                	result.setRowData(i, h_result.getRow(i-e_numCount-i_numCount));
//                	result.addRowData(h_result.getRow(i-e_result.getCount()-i_result.getCount()),i);
                	if (result.getCount("ORDER_CODE") < 0 && e_result.getCount()<0 && i_result.getCount()<0 && h_result.getCount()<0) {
            			messageBox("��������");
            			TParm resultparm = new TParm();
            			this.table.setParmValue(resultparm);
            			this.table1.setParmValue(resultparm);
            			return;
            		}
                }
			}else{	
				//����ѡ����ż�ס���ѯ��Ӧ�Ļ�������
				parm.setData("ADM_TYPE", admType);
				result = IBSPreciousMedicalMaterialTool.getInstance().onQuery1(parm);
				if (result.getCount("ORDER_CODE") < 0 ) {
					messageBox("��������");
					TParm resultparm = new TParm();
					this.table.setParmValue(resultparm);
					this.table1.setParmValue(resultparm);
					return;
				}
			}
			
		}
//		if (result.getCount("ORDER_CODE") < 0 ) {
//			messageBox("��������");
//			TParm resultparm = new TParm();
//			this.table.setParmValue(resultparm);
//			this.table1.setParmValue(resultparm);
//			return;
//		}
		//����ܼ���
		if(pageFlg == 0) {
			//��ϸ������ܼ���
			 double totalAmt = 0.0;//���
		      int count = result.getCount("ORDER_CODE");
//		      int sumQty=0;//����
		      //ѭ���ۼ�
		      for (int i = 0; i < count; i++) {
		    	  //����������
		    	  double temp = result.getDouble("OWN_AMT", i);
		          totalAmt += temp;
		          result.setData("DOSAGE_QTY",i,result.getInt("DOSAGE_QTY", i));
		      }
				  result.setData("MR_NO", count, "�ܼ�:");
			      result.setData("PAT_NAME", count, "");
			      result.setData("DEPT_DESC",count,"");
			      result.setData("DEPT_CHN_DESC",count,"");
			      result.setData("BILL_DATE",count,"");
			      result.setData("ORDER_CODE",count,"");
			      result.setData("ORDER_DESC", count,"");
			      result.setData("SPECIFICATION",count,"");
			      result.setData("UNIT_CHN_DESC",count,"");
			      result.setData("OWN_PRICE",count,"");
			      result.setData("DOSAGE_QTY", count,"");
			      result.setData("OWN_AMT",count,totalAmt);
			this.table.setParmValue(result);	
		} else if(pageFlg == 1){
			//���ܱ�����ܼ���
			 double totalAmt = 0.0;
		      int count = result.getCount("ORDER_CODE");
//		      int sumQty=0;//����
		      //ѭ���ۼ�
		      for (int i = 0; i < count; i++) {
		          double temp = result.getDouble("OWN_AMT", i);
		          totalAmt += temp;
//		          sumQty+=result.getInt("OWN_AMT", i);
//		          result.setData("SUM_QTY",i,result.getInt("SUM_QTY", i));
		      }
		      result.setData("DEPT_CHN_DESC", count, "�ܼ�:");
		      result.setData("ORDER_CODE", count, "");
		      result.setData("ORDER_DESC", count,"");
		      result.setData("SPECIFICATION", count,"");
		      result.setData("UNIT_CHN_DESC", count,"");
		      result.setData("OWN_PRICE", count,"");
		      result.setData("DOSAGE_QTY", count,"");
		      result.setData("OWN_AMT", count,totalAmt);
			this.table1.setParmValue(result);
			}
		}
	/**
	 * ���ܱ��(TABLE1)�����¼�
	 */
	public void onTableMClicked() {
	    TTable table = getTable("TABLE1");
	    int row = table.getSelectedRow();
	    selectedrow = row;
	    if (row != -1) {
	    	TParm tableParm = table.getParmValue();
	    	String dept_chn_desc = tableParm.getValue("DEPT_CHN_DESC", row);
			String order_code = tableParm.getValue("ORDER_CODE", row);
			String order_desc = tableParm.getValue("ORDER_DESC", row);
			setValue("DEPT_CHN_DESC", dept_chn_desc);
			setValue("ORDER_CODE", order_code);
			setValue("ORDER_DESC", order_desc);
	    
	    }
	}
	//caowl 20121212 ���ӻ������
	 /**
     * ���Excel
     */
    public void onExport() {

        //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
    	TTable table = null;
    	try {
    		table = pageFlg == 0?table = (TTable) callFunction("UI|TABLE|getThis"):(TTable) callFunction("UI|TABLE1|getThis");
    		if(pageFlg == 0){
    		ExportExcelUtil.getInstance().exportExcel(table, "�Ĳķ�����ϸ");
    		}else if(pageFlg == 1){
    			ExportExcelUtil.getInstance().exportExcel(table, "�Ĳķ��û���");
    		}
		} catch (NullPointerException e) {
			// TODO: handle exception
			messageBox("û�пɵ�������ݣ�");
			return;
		}
    }

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if(pageFlg == 0 ) {
			TParm data = this.table.getParmValue();
			if (data == null || data.getCount("MR_NO") <= 0) {
				this.messageBox("û��Ҫ��ӡ������");
				return;
			}
			TParm printData = new TParm();
			TParm T1 = new TParm();
			int tableDataCount = data.getCount("ORDER_CODE");
			double totalAmt1 = 0.0;//���
//		      int sumQty=0;//����
			for (int i = 0; i < tableDataCount; i++) {
				double temp = data.getDouble("OWN_AMT", i);
		          totalAmt1 += temp;
//		          sumQty+=data.getInt("DOSAGE_QTY", i);
//		          data.setData("DOSAGE_QTY",i,data.getInt("DOSAGE_QTY", i));
				T1.addData("MR_NO", data.getValue("MR_NO", i));
				T1.addData("PAT_NAME", data.getValue("PAT_NAME", i));
				T1.addData("DEPT_DESC", data.getValue("DEPT_DESC", i));
				T1.addData("DEPT_CHN_DESC", data.getValue("DEPT_CHN_DESC", i));	
				T1.addData("BILL_DATE", data.getValue("BILL_DATE", i));	
				T1.addData("ORDER_CODE", data.getValue("ORDER_CODE", i));	
				T1.addData("ORDER_DESC", data.getValue("ORDER_DESC", i));
				T1.addData("SPECIFICATION", data.getValue("SPECIFICATION", i));
				T1.addData("UNIT_CHN_DESC", data.getValue("UNIT_CHN_DESC", i));
				T1.addData("OWN_PRICE", data.getValue("OWN_PRICE", i));
				T1.addData("DOSAGE_QTY", data.getValue("DOSAGE_QTY", i));
				T1.addData("OWN_AMT", data.getValue("OWN_AMT", i));
			}
			DecimalFormat df = new DecimalFormat("######0.00");
			String totalAmt = df.format(totalAmt1);
			T1.addData("MR_NO", "�ܼƣ�");
			T1.addData("PAT_NAME", "");
			T1.addData("DEPT_DESC", "");
			T1.addData("DEPT_CHN_DESC", "");	
			T1.addData("BILL_DATE", "");	
			T1.addData("ORDER_CODE", "");	
			T1.addData("ORDER_DESC", "");
			T1.addData("SPECIFICATION", "");
			T1.addData("UNIT_CHN_DESC", "");
			T1.addData("OWN_PRICE", "");
			T1.addData("DOSAGE_QTY","");
			T1.addData("OWN_AMT",totalAmt);
			T1.setCount(tableDataCount);
			TTextFormat startDateComp = (TTextFormat) this
					.getComponent("START_DATE");
			TTextFormat endDateComp = (TTextFormat) this.getComponent("END_DATE");
			String startDate = StringTool.getString((Timestamp) startDateComp
					.getValue(), "yyyy/MM/dd HH:mm:ss");
			String endDate = StringTool.getString((Timestamp) endDateComp
					.getValue(), "yyyy/MM/dd HH:mm:ss");
			String dataArea = startDate + " �� " + endDate;
			printData.setData("DATE_AREA", "TEXT", dataArea);
			T1.addData("SYSTEM", "COLUMNS", "MR_NO");
			T1.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			T1.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
			T1.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
			T1.addData("SYSTEM", "COLUMNS", "BILL_DATE");
			T1.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			T1.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			T1.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			T1.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			T1.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			T1.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
			T1.addData("SYSTEM", "COLUMNS", "OWN_AMT");
			printData.setData("T1", T1.getData());
			printData.setData("printDate", "TEXT", StringTool.getString(SystemTool
					.getInstance().getDate(), "yyyy/MM/dd"));
			String supplies_type = this.getValueString("SUPPLIES_TYPE"); // �õ��Ĳķ���
			if (supplies_type.equals("1")) { // ��ֵ
				printData.setData("SUPPLIES_TYPE", "TEXT", "ҽ�ø�ֵ�Ĳ�");
			}
			if (supplies_type.equals("2")) {// ��ֵ
				printData.setData("SUPPLIES_TYPE", "TEXT", "ҽ�õ�ֵ�Ĳ�");
			}
			if (supplies_type.equals("3")) {// ��ҽ��
				printData.setData("SUPPLIES_TYPE", "TEXT", "��ҽ�õ�ֵ�Ĳ�");
			}
			printData.setData("printUser", "TEXT", Operator.getName());
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\IBS\\IBSPreciousMedicalMaterial.jhw",
					printData);
		} else if(pageFlg == 1) {
			TParm data = this.table1.getParmValue();
			if (data == null || data.getCount("ORDER_CODE") <= 0) {
				this.messageBox("û��Ҫ��ӡ������");
				return;
			}
			TParm printData = new TParm();
			TParm T2 = new TParm();
			int tableDataCount = data.getCount("ORDER_CODE");
			double sumQty1 = 0.00;
			for (int i = 0; i < tableDataCount; i++) {
				sumQty1+=data.getDouble("OWN_AMT", i);
				  T2.addData("DEPT_CHN_DESC", data.getValue("DEPT_CHN_DESC", i));
		          T2.addData("ORDER_CODE", data.getValue("ORDER_CODE", i));
		          T2.addData("ORDER_DESC", data.getValue("ORDER_DESC", i));
		          T2.addData("SPECIFICATION", data.getValue("SPECIFICATION", i));
		          T2.addData("UNIT_CHN_DESC", data.getValue("UNIT_CHN_DESC", i));
				  T2.addData("OWN_PRICE", data.getValue("OWN_PRICE", i));
				  T2.addData("DOSAGE_QTY", data.getValue("DOSAGE_QTY", i));
				  T2.addData("OWN_AMT", data.getValue("OWN_AMT", i));
			}
			  T2.setCount(tableDataCount);
			TTextFormat startDateComp = (TTextFormat) this
					.getComponent("START_DATE");
			TTextFormat endDateComp = (TTextFormat) this.getComponent("END_DATE");
			String startDate = StringTool.getString((Timestamp) startDateComp
					.getValue(), "yyyy/MM/dd HH:mm:ss");
			String endDate = StringTool.getString((Timestamp) endDateComp
					.getValue(), "yyyy/MM/dd HH:mm:ss");
			String dataArea = startDate + "��" + endDate;
			printData.setData("DATE_AREA", "TEXT", dataArea);
			T2.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
			T2.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			T2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			T2.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			T2.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			T2.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			T2.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
			T2.addData("SYSTEM", "COLUMNS", "OWN_AMT");
			printData.setData("T2", T2.getData());
			printData.setData("printDate", "TEXT", StringTool.getString(SystemTool
					.getInstance().getDate(), "yyyy/MM/dd"));
			String supplies_type = this.getValueString("SUPPLIES_TYPE"); // �õ��Ĳķ���
			if (supplies_type.equals("1")) { // ��ֵ
				printData.setData("SUPPLIES_TYPE", "TEXT", "ҽ�ø�ֵ�Ĳ�");
			}
			if (supplies_type.equals("2")) {// ��ֵ
				printData.setData("SUPPLIES_TYPE", "TEXT", "ҽ�õ�ֵ�Ĳ�");
			}
			if (supplies_type.equals("3")) {// ��ҽ��
				printData.setData("SUPPLIES_TYPE", "TEXT", "��ҽ�õ�ֵ�Ĳ�");
			}
			printData.setData("printUser", "TEXT", Operator.getName());
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\IBS\\IBSPreciousMM.jhw",
					printData);
		}
	}
	
	
	/**
	 * ��շ���
	 */
	public void onClear() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				"-", "/")
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		if(pageFlg == 0) {
			 table.removeRowAll();
		} else if(pageFlg == 1) {
			table1.removeRowAll();
		}
		this.clearValue("SUPPLIES_TYPE;MR_NO;PAT_NAME;�ɱ�������������_0;ADM_TYPE;ORDER_CODE;ORDER_DESC");
	}
}

