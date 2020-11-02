package com.javahis.ui.mro;

import com.dongyang.control.*;
import jdo.bil.BILComparator;
import jdo.mro.MRORT003TypeTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;

import jdo.sys.SYSRegionTool;
/**
 * <p>Title: ҽʦ��������ͳ�Ʊ�</p>
 *
 * <p>Description: ҽʦ��������ͳ�Ʊ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-13
 * @version 4.0
 */
public class MRORT003TypeControl
    extends TControl {
	
    private String DATE_S="";//��ʼʱ��
    private String DATE_E="";//��ֹʱ��
    private BILComparator compare = new BILComparator();//modify by wanglong 20130909
    private boolean ascending = false;
    private int sortColumn =-1;
    /**
     * ��ʼ��
     */
    public void onInit(){
        super.onInit();
        comboInit();//ʱ���ʼ��
        this.setValue("REGION_CODE",Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        addListener((TTable) this.getComponent("Table"));
    }
    
    /**
     * ʱ���ʼ��
     */
    private void comboInit(){
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("DATE_S",StringTool.rollDate(date,-7));
        this.setValue("DATE_E",date);
    }
    
    /**
     * ��ѯ
     */
    public void onQuery(){
        DATE_S = this.getText("DATE_S");
        DATE_E = this.getText("DATE_E");
        if(DATE_S.trim().length()<=0){
            this.messageBox_("����д��ѯ����");
            callFunction("UI|DATE_S|grabFocus");//��ȡ����
            return;
        }
        if(DATE_E.trim().length()<=0){
            this.messageBox_("����д��ѯ����");
            callFunction("UI|DATE_E|grabFocus");//��ȡ����
            return;
        }
        TParm parm = new TParm();
        parm.setData("DATE_S",DATE_S.replace("/",""));
        parm.setData("DATE_E",DATE_E.replace("/",""));
        String station = this.getValueString("STATION");
        if(station.length()>0){
            parm.setData("STATION",station);
        }
        if(this.getValueString("DEPT").length()>0){
            parm.setData("DEPT",this.getValueString("DEPT"));
        }
        String vs_code = this.getValueString("VS_CODE");
        if(vs_code.length()>0){
            parm.setData("VS_CODE",vs_code);
        }
        TParm result = new TParm();
        if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()>0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));

        if("Y".equals(this.getValueString("OUT"))){
            result = MRORT003TypeTool.getInstance().selectOUT(parm);
        }else if("Y".equals(this.getValueString("IN"))){
            result = MRORT003TypeTool.getInstance().selectIN(parm);
        }
        if(result.getErrCode()<0){
            this.messageBox("E0005");//ִ��ʧ��
            return;
        }
        if (result.getCount() <= 0) {
            this.messageBox("û��Ҫ��ѯ������");
        }
        int sumCount = 0; //ͳ��������
        for (int i = 0; i < result.getCount(); i++) {
            sumCount += result.getInt("EXAMINE_COUNT", i);
        }
        result.setData("REGION_CODE",result.getCount(), "");
        result.setData("DEPT_CODE", result.getCount(), "");
        result.setData("STATION_CODE",result.getCount(), "");
        result.setData("DR_CODE",result.getCount(), "");
        result.setData("TYPE_CODE",result.getCount(), "");
        result.setData("EXAMINE_CODE",result.getCount(), "�ܼ�");
        result.setData("EXAMINE_COUNT",result.getCount(),sumCount);
        result.setCount(result.getCount() + 1);
        TTable table = (TTable)this.getComponent("Table");
        table.setParmValue(result);
    }
    
    /**
     * ��ӡ
     */
    public void onPrint(){
        TTable table = (TTable)this.getComponent("Table");
        TParm data = table.getShowParmValue();
        if(null==data||data.getCount()<=0){
            this.messageBox("û����Ҫ��ӡ������");
            return;
        }
        data.setCount(data.getCount()+1);
        data.addData("SYSTEM", "COLUMNS", "REGION_CODE");
        data.addData("SYSTEM","COLUMNS","DEPT_CODE");
        data.addData("SYSTEM", "COLUMNS", "STATION_CODE");
        data.addData("SYSTEM", "COLUMNS", "DR_CODE");
        data.addData("SYSTEM", "COLUMNS", "TYPE_CODE");
        data.addData("SYSTEM", "COLUMNS", "EXAMINE_CODE");
        data.addData("SYSTEM", "COLUMNS", "EXAMINE_COUNT");
        TParm printData = new TParm();
        printData.setData("T1", data.getData());//��ӡ����
        //��ֹ����
        printData.setData("date", "TEXT",
                          DATE_S.substring(0, 4) + "��" +
                          DATE_S.subSequence(5, 7) + "��" +
                          DATE_S.substring(8, 10) + "�� �� " +
                          DATE_E.substring(0, 4) + "��" +
                          DATE_E.subSequence(5, 7) + "��" +
                          DATE_E.substring(8, 10) + "��");
        TParm basic = new TParm();
        basic.addData("PrintUser",Operator.getName());//�Ʊ���
        printData.setData("basic",basic.getData());
        
        String region = data.getValue("REGION_CODE",0);
        printData.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "����ҽԺ") + "ҽʦ��������ͳ�Ʊ�");//��ӱ�ͷ

        //�Ʊ�ʱ��
        printData.setData("printDate","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy��MM��dd��"));
        this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MRORT003_Type.jhw",
                             printData);
    }
    
	/**
	 * ���Excel
	 */
	public void onExport() {//add by wanglong 20121108
		TTable table = (TTable) callFunction("UI|Table|getThis");
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "ҽʦ��������ͳ�Ʊ�");
	}
	
    /**
     * ����ѡ���¼�
     */
    public void onSTATION(){
        this.clearValue("STATION;VS_CODE");
        this.callFunction("UI|STATION|onQuery");
        this.callFunction("UI|VS_CODE|onQuery");
    }
    
    /**
     * ���
     */
    public void onClear(){
        this.clearValue("STATION;DEPT;VS_CODE");
        //comboInit();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.setValue("OUT",true);
        this.setValue("REGION_CODE",Operator.getRegion());
    }
    
    // ====================������begin======================//add by wanglong 20130909
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
                // ת�����û���������к͵ײ����ݵ��У�Ȼ���ж�
                if (j == sortColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();
                TParm totAmtRow = tableData.getRow(tableData.getCount() - 1);// add by wanglong 20130108
                tableData.removeRow(tableData.getCount() - 1);// add by wanglong 20130108
                tableData.removeGroupData("SYSTEM");
                String columnName[] = tableData.getNames("Data");
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn);
                int col = tranParmColIndex(columnName, tblColumnName);
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                TParm lastResultParm = new TParm();// ��¼���ս��
                lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// �����м�����
                for (int k = 0; k < columnName.length; k++) {// add by wanglong 20130108
                    lastResultParm.addData(columnName[k], totAmtRow.getData(columnName[k]));
                }
                lastResultParm.setCount(lastResultParm.getCount(columnName[0]));// add by wanglong 20130108
                table.setParmValue(lastResultParm);
            }
        });
    }

    /**
     * ����ת������ֵ
     * 
     * @param columnName
     * @param tblColumnName
     * @return
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
     * �õ� Vector ֵ
     * 
     * @param group
     *            String ����
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int �������
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size) count = size;
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
     * vectoryת��param
     */
    private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
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
        return parmTable;
    }
    // ====================������end======================
}
