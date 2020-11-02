package com.javahis.ui.aci;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import jdo.aci.ACIADRName;
import jdo.sys.Operator;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ҩƷ�����¼������ֵ�ά�� </p>
 *
 * <p>Description: ҩƷ�����¼������ֵ�ά�� </p>
 *
 * <p>Copyright: Copyright (c) 2013 </p>
 *
 * <p>Company: BlueCore </p>
 *
 * @author WangLong 2013.09.30
 * @version 1.0
 */
public class ACIADRDictionaryControl
        extends TControl {

    private TTable table;
    private ACIADRName adrName;
    private boolean NEW_FLG = true;// ������ǣ�true����;false���£�
    private int SELECTED_ROW = -1;// ��ѡ�е���
    private boolean ascending = false;// ���������������
    private int sortColumn = -1;// ��������(�������)

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        addSortListener(table);
        callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        initData();
    }

    /**
     * ��ʼ���������
     */
    public void initData() {
        adrName = new ACIADRName();
        adrName.setSQL("SELECT * FROM ACI_ADRNAME ORDER BY ADR_CODE,ORGAN_CODE1");
        adrName.retrieve();
        table.setDataStore(adrName);
        table.setDSValue();
    }

    /**
     * ����
     */
    public void onSave() {
        if (this.getValueString("ADR_CODE").length() > 0
                || this.getValueString("ADR_DESC").length() > 0
                || this.getValueString("ORGAN_CODE").length() > 0) {
            if (!(this.getValueString("ADR_CODE").length() > 0
                    && this.getValueString("ADR_DESC").length() > 0 && this
                    .getValueString("ORGAN_CODE").length() > 0)) {
                if (this.getValueString("ADR_CODE").length() == 0) {
                    this.messageBox("����дADR����");
                    return;
                }
                if (this.getValueString("ADR_DESC").length() == 0) {
                    this.messageBox("����д������Ӧ����");
                    return;
                }
                if (this.getValueString("ORGAN_CODE1").length() == 0) {
                    this.messageBox("����д���ٴ���1");
                    return;
                }
            }
        }
        int row = 0;
        if (NEW_FLG) {
            row = adrName.insertRow();
        } else {
            row = SELECTED_ROW;
        }
        adrName.setItem(row, "ADR_CODE", this.getValue("ADR_CODE"));
        adrName.setItem(row, "ADR_DESC", this.getValue("ADR_DESC"));
        adrName.setItem(row, "ADR_ENG_DESC", this.getValue("ADR_ENG_DESC"));
        adrName.setItem(row, "PY", StringUtil.onCode(this.getValueString("ADR_DESC")));
        adrName.setItem(row, "ORGAN_CODE1", this.getValue("ORGAN_CODE1"));
        adrName.setItem(row, "ORGAN_CODE2", this.getValue("ORGAN_CODE2"));
        adrName.setItem(row, "ORGAN_CODE3", this.getValue("ORGAN_CODE3"));
        adrName.setItem(row, "OPT_USER", Operator.getID());
        adrName.setItem(row, "OPT_DATE", adrName.getDBTime());
        adrName.setItem(row, "OPT_TERM", Operator.getIP());
        if (NEW_FLG) {
            String adrID = adrName.getNewId(row);
            if (StringTool.getInt(adrID) < 0) {
                this.messageBox("E0034");// ȡ�����ݴ���
                return;
            }
            adrName.setItem(row, "ADR_ID", adrID);
        }
        adrName.setActive(row, true);
        if (adrName.update()) {
            this.messageBox("P0001");
        } else {
            this.messageBox("E0001");
        }
        table.setDSValue();
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        table.acceptText();
        adrName.deleteRow(table.getSelectedRow());
        if (adrName.update()) {
            this.messageBox("P0003");
        } else {
            this.messageBox("E0003");
        }
        table.setDSValue();
    }

//    /**
//     * ����
//     */
//    public void onNew() {
//        table.acceptText();
//        int row = table.getDataStore().insertRow();
//        table.getDataStore().setActive(row, false);
//        table.setDSValue();
//    }

    /**
     * ͨ��excel��������
     * excel��һ��Ϊ��ͷ��Ӧ������ADR���룻������Ӧ���ƣ�������ӦӢ�����ƣ����ٴ���1�����ٴ���2�����ٴ���3
     * ���е�˳��ɱ�
     * ������Ϣ�������excel�ĵ�һ��sheetҳ��
     */
    public void onInsertByExl() {
        table.acceptText();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilter() {// ����xls�ļ�

                    public boolean accept(File f) {
                        if (f.isDirectory()) {// �����ļ���
                            return true;
                        }
                        return f.getName().endsWith(".xls");
                    }

                    public String getDescription() {
                        return ".xls";
                    }
                });
        int option = fileChooser.showOpenDialog(null);
        TParm parm = new TParm();
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Workbook wb = Workbook.getWorkbook(file);
                Sheet st = wb.getSheet(0);
                int row = st.getRows();
                int column = st.getColumns();
                if (row <= 1 || column <= 0) {
                    this.messageBox("excel��û������");
                    return;
                }
                String[] title = new String[column];
                for (int j = 0; j < column; j++) {
                    String cell = st.getCell(j, 0).getContents();
                    if (cell.indexOf("ADR����") != -1) {
                        title[j] = "ADR_CODE";
                        continue;
                    }
                    if (cell.indexOf("������Ӧ����") != -1) {
                        title[j] = "ADR_DESC";
                        continue;
                    }
                    if (cell.indexOf("������ӦӢ������") != -1) {
                        title[j] = "ADR_ENG_DESC";
                        continue;
                    }
                    if (cell.indexOf("���ٴ���1") != -1) {
                        title[j] = "ORGAN_CODE1";
                        continue;
                    }
                    if (cell.indexOf("���ٴ���2") != -1) {
                        title[j] = "ORGAN_CODE2";
                        continue;
                    }
                    if (cell.indexOf("���ٴ���3") != -1) {
                        title[j] = "ORGAN_CODE3";
                        continue;
                    }
                }
                List<String> titleList = Arrays.asList(title);
                if (!titleList.contains("ADR_CODE")) {
                    this.messageBox("ȱ�١�ADR���롱��");
                    return;
                }
                if (!titleList.contains("ADR_DESC")) {
                    this.messageBox("ȱ�١�������Ӧ���ơ���");
                    return;
                }
                if (!titleList.contains("ORGAN_CODE1")) {
                    this.messageBox("ȱ�١����ٴ���1����");
                    return;
                }
                int count = 0;
                for (int i = 1; i < row; i++) {// һ��һ�м���excel�е�����
                    for (int j = 0; j < column; j++) {// ÿ�ε���һ�е�������
                        String cell = st.getCell(j, i).getContents();
                        parm.addData(title[j], cell.trim());
                    }
                    count = parm.getCount("ADR_CODE");
                    parm.setCount(count);
                    // ===================���ݼ�鿪ʼ
                    if (StringUtil.isNullString(parm.getValue("ADR_CODE"))) {
                        this.messageBox("EXCEL " + (i + 1) + "��ȱ�١�ADR���롱�ֶ�");
                        continue;
                    }
                    if (StringUtil.isNullString(parm.getValue("ADR_DESC"))) {
                        this.messageBox("EXCEL " + (i + 1) + "��ȱ�١�������Ӧ���ơ��ֶ�");
                        continue;
                    }
                    if (StringUtil.isNullString(parm.getValue("ORGAN_CODE1"))) {
                        this.messageBox("EXCEL " + (i + 1) + "��ȱ�١����ٴ���1���ֶ�");
                        continue;
                    }
//                    if (!parm.getValue("ADR_CODE").matches("[0-9]+[ ]*[0-9]+")) {
//                        this.messageBox("EXCEL " + (i + 1) + "�С�ADR���롱�е���ֵ����ȷ");
//                        continue;
//                    }
//                    if (!parm.getValue("ORGAN_CODE1").matches("[0-9]+[ ]*[0-9]+")) {
//                        this.messageBox("EXCEL " + (i + 1) + "�С����ٴ���1���е���ֵ����ȷ");
//                        continue;
//                    }
                    // ===================���ݼ�����
                }
                parm.setCount(count);
                if (count < 1) {
                    this.messageBox_("��Ч��������һ�У����������ֹ");
                    return;
                }
            }
            catch (BiffException e) {
                this.messageBox_("excel�ļ���������");
                e.printStackTrace();
                return;
            }
            catch (IOException e) {
                this.messageBox_("���ļ�����");
                e.printStackTrace();
                return;
            }
        } else return;
  
        
        TParm adrParm = adrName.getBuffer(adrName.PRIMARY);
        for (int i = adrParm.getCount()-1; i >=0 ; i--) {
            if (!adrParm.getBoolean("#ACTIVE#", i)) {
                adrParm.removeRow(i);
            }
        }
        Vector adrVector = adrParm.getVectorValue("ADR_CODE");
        int count = 0;
        for (int i = 0; i < parm.getCount(); i++) {
            TParm parmRow = parm.getRow(i);
            int newRow = adrName.insertRow();
            if (adrVector.contains(parmRow.getValue("ADR_CODE"))) {
                continue;
            }
            adrName.setItem(newRow, "ADR_CODE", parmRow.getValue("ADR_CODE"));
            adrName.setItem(newRow, "ADR_DESC", parmRow.getValue("ADR_DESC"));
            adrName.setItem(newRow, "ADR_ENG_DESC", parmRow.getValue("ADR_ENG_DESC"));
            adrName.setItem(newRow, "PY", TMessage.getPy(parmRow.getValue("ADR_DESC")));
            adrName.setItem(newRow, "ORGAN_CODE1", parmRow.getValue("ORGAN_CODE1"));
            adrName.setItem(newRow, "ORGAN_CODE2", parmRow.getValue("ORGAN_CODE2"));
            adrName.setItem(newRow, "ORGAN_CODE3", parmRow.getValue("ORGAN_CODE3"));
            adrName.setItem(newRow, "OPT_USER", Operator.getID());
            adrName.setItem(newRow, "OPT_TERM", adrName.getDBTime());
            adrName.setItem(newRow, "OPT_DATE", Operator.getIP());
            String adrID = adrName.getNewId(newRow);
            if (StringTool.getInt(adrID) < 0) {
                this.messageBox("E0034");// ȡ�����ݴ���
                adrName.setActive(newRow, false);
            }else{
                adrName.setItem(newRow, "ADR_ID", adrID);
                adrName.setActive(newRow, true);
                count++;
            }
        }
        table.setDSValue();
        if (count > 0) {
            if (adrName.update()) {
                this.messageBox_("����ɹ�,������" + count + "������");
            } else {
                this.messageBox_("����ʧ��");
                initData();
            }
        }
    }

    /**
     * ������Ӧ���ƻس��¼�(����ƴ��)
     */
    public void onCreatePY() {
        String adrDesc = this.getValueString("ADR_DESC");
        String py = TMessage.getPy(adrDesc);
        this.setValue("PY", py);
        ((TTextField)this.getComponent("PY")).grabFocus();
    }
    
    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) return;
        TParm data = adrName.getRowParm(row);
        setValueForParm("ADR_CODE;ADR_DESC;PY;ADR_ENG_DESC;ORGAN_CODE1;ORGAN_CODE2;ORGAN_CODE3",
                        data);
        NEW_FLG = false;
        SELECTED_ROW = row;
    }
    
    /**
     * ���
     */
    public void onClear() {
        this.clearValue("ADR_CODE;ADR_DESC;PY;ADR_ENG_DESC;ORGAN_CODE1;ORGAN_CODE2;ORGAN_CODE3");
        table.clearSelection();
        NEW_FLG = true;
        SELECTED_ROW = -1;
    }
    
    
    /**
     * �����������������
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                int i = table.getTable().columnAtPoint(e.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// �����ͬ�У���ת����
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                String asc = "ASC";
                if (ascending == false) {
                    asc = "DESC";
                }
                adrName.setSort(table.getParmMap(j) + " " + asc);
                adrName.sort();
                table.setDataStore(adrName);
                table.setDSValue();
            }
        });
    }
}
