package com.javahis.ui.inv;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.inv.INVValidAndQtyWarnTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ��Ч�ڼ��������ʾ</p>
 *
 * <p>Description: ��Ч�ڼ��������ʾ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangh 2013.7.12
 * @version 1.0
 */
public class INVValidAndQtyWarnControl
    extends TControl {

    private TPanel panel_0; 

    private TTable tableValid;

    private TTable tableQty;
    
  //===========������==================add by wanglong 20121212
    private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

    public INVValidAndQtyWarnControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        panel_0 = getPanel("TPanel_0");
        tableValid = (TTable) this.getComponent("TABLE_VALID");
        tableQty = (TTable) this.getComponent("TABLE_QTY");
        addSortListener(tableValid);//===�����������=====add by wanglong 20121212
        addSortListener(tableQty);//===�����������=====add by wanglong 20121212
		TParm parmValid = new TParm();
		parmValid.setData("CAT1_TYPE", "OTH");
		// ���õ����˵�
        getTextField("INV_CODE_VALID").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parmValid);
		// ������ܷ���ֵ����
        getTextField("INV_CODE_VALID").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturnValid");
        TParm parmQty = new TParm();
        parmQty.setData("CAT1_TYPE", "OTH");
		// ���õ����˵�
        getTextField("INV_CODE_QTY").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parmQty);
		// ������ܷ���ֵ����
        getTextField("INV_CODE_QTY").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturnQTY");
    }
    
    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturnValid(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE_VALID").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC_VALID").setValue(order_desc);
    }
    
    /**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturnQTY(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE_QTY").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC_QTY").setValue(order_desc);
    }
    

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        TParm result = new TParm();
        String inv_code = "";

        if (panel_0.isShowing()) {
            String date = StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyyMMdd");
            //��������
            if (getRadioButton("VALID_DATE_A").isSelected()) {
                parm.setData("VALID_DATE",
                             rollMonth(date.substring(0, 6), date.substring(6, 8), 1));
            }
            else if (getRadioButton("VALID_DATE_B").isSelected()) {
                parm.setData("VALID_DATE",
                             rollMonth(date.substring(0, 6), date.substring(6, 8), 3));
            }
            else {
                String valid_date = getValueString("VALID_DATE");
                parm.setData("VALID_DATE", valid_date.substring(0, 4) +
                             valid_date.substring(5, 7) + valid_date.substring(8, 10));
            }
            //���ʴ���
            inv_code = getValueString("INV_CODE_VALID");
            if (inv_code != null && inv_code.length() > 0) {
                parm.setData("INV_CODE", inv_code);
            }
            //��Ч�ڲ�ѯ
            result = INVValidAndQtyWarnTool.getInstance().onQueryValid(parm);
            if (result == null || result.getCount("INV_CODE") <= 0) {
                this.messageBox("û�в�ѯ����");
                tableValid.removeRowAll();
                return;
            }
            tableValid.setParmValue(result);
        }
        else {
            //ҩƷ����
            inv_code = getValueString("INV_CODE_QTY");
            if (inv_code != null && inv_code.length() > 0) {
                parm.setData("INV_CODE", inv_code);
            }
            //�����
            if (getRadioButton("STOCK_QTY_A").isSelected()) {
                parm.setData("STOCK_QTY_A", "STOCK_QTY_A");
            }
            else if (getRadioButton("STOCK_QTY_B").isSelected()) {
                parm.setData("STOCK_QTY_B", "STOCK_QTY_B");
            }
            else {
                parm.setData("STOCK_QTY_C", getValue("STOCK_QTY_C"));
            }
            //�������ѯ
            result = INVValidAndQtyWarnTool.getInstance().onQueryQty(parm);
            if (result == null || result.getCount("INV_CODE") <= 0) {
                this.messageBox("û�в�ѯ����");
                tableQty.removeRowAll();
                return;
            }
            tableQty.setParmValue(result);
        }
    }

    /**
     * ��շ���
     */
    public void onClear() {
        if (panel_0.isShowing()) {
            getRadioButton("VALID_DATE_A").setSelected(true);
            getTextFormat("VALID_DATE").setEnabled(false);
            this.clearValue("VALID_DATE;INV_CODE_VALID;INV_DESC_VALID");
            tableValid.removeRowAll();
        }
        else {
            getRadioButton("STOCK_QTY_C").setSelected(true);
            this.clearValue("INV_CODE_QTY;INV_DESC_QTY");
            tableQty.removeRowAll();
        }
    }

    /**
     * �����ѡ��
     */
    public void onChangeRadioButton() {
        if (getRadioButton("VALID_DATE_C").isSelected()) {
            getTextFormat("VALID_DATE").setEnabled(true);
        }
        else {
            getTextFormat("VALID_DATE").setEnabled(false);
            this.clearValue("VALID_DATE");
        }
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
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }


    /**
     * �õ�TPanel����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TPanel getPanel(String tagName) {
        return (TPanel) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * ����ָ�����·ݺ������Ӽ�������Ҫ���·ݺ�����
     * @param Month String �ƶ��·� ��ʽ:yyyyMM
     * @param Day String �ƶ��·� ��ʽ:dd
     * @param num String �Ӽ������� ����Ϊ��λ
     * @return String
     */
    public String rollMonth(String Month, String Day,int num){
        if(Month.trim().length()<=0){
            return "";
        }
        Timestamp time = StringTool.getTimestamp(Month,"yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // ��ǰ�£�num
        cal.add(cal.MONTH, num);
        // ���¸���1����Ϊ���ڳ�ʼֵ
        cal.set(cal.DATE, 1);
        Timestamp month = new Timestamp(cal.getTimeInMillis());
        String result = StringTool.getString(month, "yyyyMM");
        String lastDayOfMonth = getLastDayOfMonth(result);
        if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
            result += lastDayOfMonth;
        }
        else {
            result += Day;
        }
        return result;
    }

    /**
     * ��ȡָ���·ݵ����һ�������
     * @param date String ��ʽ YYYYMM
     * @return Timestamp
     */
    public String getLastDayOfMonth(String date) {
        if (date.trim().length() <= 0) {
            return "";
        }
        Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // ��ǰ�£�1�����¸���
        cal.add(cal.MONTH, 1);
        // ���¸���1����Ϊ���ڳ�ʼֵ
        cal.set(cal.DATE, 1);
        // �¸���1�ż�ȥһ�죬���õ���ǰ�����һ��
        cal.add(cal.DATE, -1);
        Timestamp result = new Timestamp(cal.getTimeInMillis());
        return StringTool.getString(result, "dd");
    }
    
    /*
     * ����excel����
     */
    public void onExecl(){
    	if (panel_0.isShowing()) {
    		if(tableValid.getRowCount() <= 0){
    			this.messageBox("û�����ݣ�");
    			return;
    		}
    		ExportExcelUtil.getInstance().exportExcel(tableValid, "���ʽ�Ч��ͳ��");
    	}else {
    		if(tableQty.getRowCount() <= 0){
    			this.messageBox("û�����ݣ�");
    			return;
    		}
    		ExportExcelUtil.getInstance().exportExcel(tableQty, "���ʰ�ȫ�����ͳ��");
    	}
    }
    
 // ====================������begin======================add by wanglong 20121212
	/**
	 * �����������������
	 * @param table
	 */
	public void addSortListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				if (j == sortColumn) {
					ascending = !ascending;// �����ͬ�У���ת����
				} else {
					ascending = true;
					sortColumn = j;
				}
				TParm tableData = table.getParmValue();// ȡ�ñ��е�����
				String columnName[] = tableData.getNames("Data");// �������
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				String tblColumnName = table.getParmMap(sortColumn); // ������������;
				int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames, table);
			}
		});
	}

	/**
	 * �����������ݣ���TParmתΪVector
	 * @param parm
	 * @param group
	 * @param names
	 * @param size
	 * @return
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
	 * ����ָ���������������е�index
	 * @param columnName
	 * @param tblColumnName
	 * @return int
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
	 * �����������ݣ���Vectorת��Parm
	 * @param vectorTable
	 * @param parmTable
	 * @param columnNames
	 * @param table
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
	}
	// ====================������end======================

}
