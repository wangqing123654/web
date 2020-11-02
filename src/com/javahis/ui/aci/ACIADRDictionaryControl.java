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
 * <p>Title: 药品不良事件名称字典维护 </p>
 *
 * <p>Description: 药品不良事件名称字典维护 </p>
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
    private boolean NEW_FLG = true;// 新增标记（true新增;false更新）
    private int SELECTED_ROW = -1;// 被选中的行
    private boolean ascending = false;// 用于排序（升序或降序）
    private int sortColumn = -1;// 用于排序(排序的列)

    /**
     * 初始化
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
     * 初始化表格数据
     */
    public void initData() {
        adrName = new ACIADRName();
        adrName.setSQL("SELECT * FROM ACI_ADRNAME ORDER BY ADR_CODE,ORGAN_CODE1");
        adrName.retrieve();
        table.setDataStore(adrName);
        table.setDSValue();
    }

    /**
     * 保存
     */
    public void onSave() {
        if (this.getValueString("ADR_CODE").length() > 0
                || this.getValueString("ADR_DESC").length() > 0
                || this.getValueString("ORGAN_CODE").length() > 0) {
            if (!(this.getValueString("ADR_CODE").length() > 0
                    && this.getValueString("ADR_DESC").length() > 0 && this
                    .getValueString("ORGAN_CODE").length() > 0)) {
                if (this.getValueString("ADR_CODE").length() == 0) {
                    this.messageBox("请填写ADR代码");
                    return;
                }
                if (this.getValueString("ADR_DESC").length() == 0) {
                    this.messageBox("请填写不良反应名称");
                    return;
                }
                if (this.getValueString("ORGAN_CODE1").length() == 0) {
                    this.messageBox("请填写器官代码1");
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
                this.messageBox("E0034");// 取得数据错误
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
     * 删除
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
//     * 新增
//     */
//    public void onNew() {
//        table.acceptText();
//        int row = table.getDataStore().insertRow();
//        table.getDataStore().setActive(row, false);
//        table.setDSValue();
//    }

    /**
     * 通过excel批量新增
     * excel第一行为表头，应包含：ADR代码；不良反应名称；不良反应英文名称；器官代码1；器官代码2；器官代码3
     * 各列的顺序可变
     * 所有信息必须存在excel的第一个sheet页中
     */
    public void onInsertByExl() {
        table.acceptText();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilter() {// 过滤xls文件

                    public boolean accept(File f) {
                        if (f.isDirectory()) {// 忽略文件夹
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
                    this.messageBox("excel中没有数据");
                    return;
                }
                String[] title = new String[column];
                for (int j = 0; j < column; j++) {
                    String cell = st.getCell(j, 0).getContents();
                    if (cell.indexOf("ADR代码") != -1) {
                        title[j] = "ADR_CODE";
                        continue;
                    }
                    if (cell.indexOf("不良反应名称") != -1) {
                        title[j] = "ADR_DESC";
                        continue;
                    }
                    if (cell.indexOf("不良反应英文名称") != -1) {
                        title[j] = "ADR_ENG_DESC";
                        continue;
                    }
                    if (cell.indexOf("器官代码1") != -1) {
                        title[j] = "ORGAN_CODE1";
                        continue;
                    }
                    if (cell.indexOf("器官代码2") != -1) {
                        title[j] = "ORGAN_CODE2";
                        continue;
                    }
                    if (cell.indexOf("器官代码3") != -1) {
                        title[j] = "ORGAN_CODE3";
                        continue;
                    }
                }
                List<String> titleList = Arrays.asList(title);
                if (!titleList.contains("ADR_CODE")) {
                    this.messageBox("缺少“ADR代码”列");
                    return;
                }
                if (!titleList.contains("ADR_DESC")) {
                    this.messageBox("缺少“不良反应名称”列");
                    return;
                }
                if (!titleList.contains("ORGAN_CODE1")) {
                    this.messageBox("缺少“器官代码1”列");
                    return;
                }
                int count = 0;
                for (int i = 1; i < row; i++) {// 一行一行加入excel中的数据
                    for (int j = 0; j < column; j++) {// 每次导入一行的所有列
                        String cell = st.getCell(j, i).getContents();
                        parm.addData(title[j], cell.trim());
                    }
                    count = parm.getCount("ADR_CODE");
                    parm.setCount(count);
                    // ===================数据检查开始
                    if (StringUtil.isNullString(parm.getValue("ADR_CODE"))) {
                        this.messageBox("EXCEL " + (i + 1) + "行缺少“ADR代码”字段");
                        continue;
                    }
                    if (StringUtil.isNullString(parm.getValue("ADR_DESC"))) {
                        this.messageBox("EXCEL " + (i + 1) + "行缺少“不良反应名称”字段");
                        continue;
                    }
                    if (StringUtil.isNullString(parm.getValue("ORGAN_CODE1"))) {
                        this.messageBox("EXCEL " + (i + 1) + "行缺少“器官代码1”字段");
                        continue;
                    }
//                    if (!parm.getValue("ADR_CODE").matches("[0-9]+[ ]*[0-9]+")) {
//                        this.messageBox("EXCEL " + (i + 1) + "行“ADR代码”列的数值不正确");
//                        continue;
//                    }
//                    if (!parm.getValue("ORGAN_CODE1").matches("[0-9]+[ ]*[0-9]+")) {
//                        this.messageBox("EXCEL " + (i + 1) + "行“器官代码1”列的数值不正确");
//                        continue;
//                    }
                    // ===================数据检查结束
                }
                parm.setCount(count);
                if (count < 1) {
                    this.messageBox_("有效数据少于一行，导入操作终止");
                    return;
                }
            }
            catch (BiffException e) {
                this.messageBox_("excel文件操作出错");
                e.printStackTrace();
                return;
            }
            catch (IOException e) {
                this.messageBox_("打开文件出错");
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
                this.messageBox("E0034");// 取得数据错误
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
                this.messageBox_("导入成功,共导入" + count + "行数据");
            } else {
                this.messageBox_("导入失败");
                initData();
            }
        }
    }

    /**
     * 不良反应名称回车事件(生成拼音)
     */
    public void onCreatePY() {
        String adrDesc = this.getValueString("ADR_DESC");
        String py = TMessage.getPy(adrDesc);
        this.setValue("PY", py);
        ((TTextField)this.getComponent("PY")).grabFocus();
    }
    
    /**
     *增加对Table的监听
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
     * 清空
     */
    public void onClear() {
        this.clearValue("ADR_CODE;ADR_DESC;PY;ADR_ENG_DESC;ORGAN_CODE1;ORGAN_CODE2;ORGAN_CODE3");
        table.clearSelection();
        NEW_FLG = true;
        SELECTED_ROW = -1;
    }
    
    
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                int i = table.getTable().columnAtPoint(e.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// 点击相同列，翻转排序
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
