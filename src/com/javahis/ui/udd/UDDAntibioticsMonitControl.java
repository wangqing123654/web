package com.javahis.ui.udd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.sys.Pat;
import jdo.udd.UDDAntibioticsMonitTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p> Title:抗菌药物临床应用监测 </p>
 * 
 * <p> Description:TODO </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author Yuanxm
 * @version 1.0
 */
public class UDDAntibioticsMonitControl extends TControl {

    //TPanel PAT_PANEL;
    //TPanel MEDI_PANEL;
    TTable PAT_TABLE; // 患者表格
    TTable MEDI_TABLE; // 用药表格

    private TParm data;
    
    private BILComparator compare=new BILComparator();
    private boolean ascending = false;
    private int sortColumn = -1;
    private TTabbedPane TabbedPane;

    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        addListener(getTTable("PAT_TABLE"));
        initComponent();
        onClear();// 清空界面
    }

    /**
     * 初始化组件
     */
    public void initComponent() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("START_DATE", StringTool.rollDate(date, -30).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        //PAT_PANEL = (TPanel) this.getComponent("PAT_PANEL");
        //MEDI_PANEL = (TPanel) this.getComponent("MEDI_PANEL");
        TabbedPane = (TTabbedPane) this.getComponent("TabbedPane");   
        PAT_TABLE = (TTable) this.getComponent("PAT_TABLE");
        MEDI_TABLE = (TTable) this.getComponent("MEDI_TABLE");
        this.setValue("ANTIBIOTIC_WAY", "01");//预防使用
        onTableShowHead();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm selectCondition =
                getParmForTag("START_DATE;END_DATE;DEPT_CODE;MR_NO;PAT_NAME;ALL;SURGERY;UNSURGERY;DEAD;HEAL_LEVEL;USE_ANTIBIOTIC;ANTIBIOTIC_WAY",
                              true);// modify by wanglong 20130801
//        System.out.println("--------入参------"+selectCondition);
        data = UDDAntibioticsMonitTool.getInstance().onQuery(selectCondition);
        if (data.getErrCode() < 0) {
            this.messageBox(data.getErrText());
        }
        System.out.println("data::::"+data);
        if (data.getCount()<=0) {
			this.messageBox("没有查询的数据");
			PAT_TABLE.setParmValue(new TParm());
			return;
		}
        // 患者
        if (TabbedPane.getSelectedIndex()==0) {
            int count = data.getCount();
            if (count > 0) {
                double totCharge03 = 0;
                double totCharge03Fee = 0;
                double sumDDD = 0;// 抗生素累积量
                double totdays = 0;// 住院总天数
                int coCount = 0;// 联合使用总数 add by wanglong 20130802
                int preCount = 0; // 预防使用总数
                int timeCount = 0;// 时机合理总数
                int cureCount = 0;// 治疗合理总数
                // 抗生素费用不为0的的总数/查询出的所有记录数=住院患者抗菌药物使用率统计值
                TParm result=null;
                for (int i = 0; i < count; i++) {
                    TParm rowParm = data.getRow(i);
                    double charge03 = rowParm.getDouble("CHARGE_03");
                    result=new TParm(TJDODBTool.getInstance().select(" SELECT B.CASE_NO " +
                    " FROM ODI_ORDER B,PHA_BASE C WHERE B.CAT1_TYPE = 'PHA' AND B.CASE_NO='"+rowParm.getValue("CASE_NO")+
                    "' AND B.ORDER_CODE=C.ORDER_CODE AND B.ANTIBIOTIC_WAY IS NOT NULL GROUP BY B.CASE_NO"));//预防使用"));
                    if (result.getCount()>0) {
                    	data.setData("PRE_USE", i, "Y");
                    	totCharge03 += 1;
                        data.setData("IS_USE", i, "使用");
                    	preCount++;
					}else{
						data.setData("PRE_USE", i, "N");
						data.setData("IS_USE", i, "");
					}
                    totCharge03Fee += charge03;
                    result=new TParm(TJDODBTool.getInstance().select("SELECT SUM(B.DOSAGE_QTY * C.MEDI_QTY / D.DDD) DDD_SUM FROM IBS_ORDD B, PHA_TRANSUNIT C, PHA_BASE D " +
                    "  WHERE B.CAT1_TYPE = 'PHA' AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE " +
                    "    AND D.ANTIBIOTIC_CODE IS NOT NULL AND B.CASE_NO='"+rowParm.getValue("CASE_NO")+"'"));
                    if (result.getErrCode()<0) {
						this.messageBox("查询出现问题");
						return;
					}
                    data.setData("DDD_SUM",i,StringTool.round(result.getDouble("DDD_SUM",0), 2));
                    
                    sumDDD += result.getDouble("DDD_SUM",0);
                    int days = rowParm.getInt("REAL_STAY_DAYS");
                    if (days > 0) {
                        totdays += days;
                    }
                    if (rowParm.getBoolean("CO_USE")) {// 联合使用
                        coCount++;
                    }
                    if (rowParm.getBoolean("REASONABLE_TIME")) {// 时机合理
                        timeCount++;
                    }
                    if (rowParm.getBoolean("REASONABLE_CURE")) {// 治疗合理
                        cureCount++;
                    }
                }
                //抗菌药物使用强度(抗菌药物消耗量(总DDD)/查询的总病患住院天数/查询出的病患总数)
                sumDDD = StringTool.round(sumDDD, 2);
                double ddd = sumDDD / totdays;// 抗生素强度add by wanglong 20130807
                ddd = StringTool.round(ddd, 2);
                //抗生素费用总数大于0
                int newRow = count + 1;
                data.setData("CHOOSE", newRow, "");
                data.setData("IN_DATE", newRow, "");
                data.setData("OUT_DATE", newRow, "");
                data.setData("OUT_DEPT", newRow, "");
                data.setData("MR_NO", newRow, "");
                data.setData("PAT_NAME", newRow, "");
                data.setData("SEX", newRow, "");
                data.setData("AGE", newRow, "");
                data.setData("NB_WEIGHT", newRow, "");
                data.setData("INTE_DIAG_CODE", newRow, "");
                data.setData("OUT_DIAG_CODE1", newRow, "");
                data.setData("OUT_DIAG_CODE2", newRow, "");
                data.setData("OUT_DIAG_CODE4", newRow, "");
                data.setData("ALLEGIC", newRow, "");
                data.setData("SUM_TOT", newRow, "");
                data.setData("MEDICAL_TOT", newRow, "");
                data.setData("OUT_DIAG_CODE3", newRow, "");
                double average = totCharge03 / count;
                if (totCharge03 > 0) {
                    DecimalFormat df = new DecimalFormat("####0.####");
                    data.setData("CHARGE_03", newRow, df.format(totCharge03Fee));
                    data.setData("IS_USE", newRow, df.format(average*100) + "%");
                } else {
                    data.setData("CHARGE_03", newRow, "");
                    data.setData("IS_USE", newRow, "0.0000%");
                }
                data.setData("OP_CODE", newRow, "");
                data.setData("HEAL_LV", newRow, "");
                data.setData("CASE_NO", newRow, "");
                data.setData("REAL_STAY_DAYS", newRow, totdays);// add by wanglong 20130801
                data.setData("DDD_SUM", newRow, sumDDD);
                data.setData("CO_USE", newRow, coCount + "人");
                data.setData("PRE_USE", newRow, preCount + "人");
                data.setData("RT_FLG", newRow, timeCount);
                data.setData("RC_FLG", newRow, cureCount);
                this.setValue("DDD", ddd + "");// add by wanglong 20130807
            }
//            System.out.println("----------data-------------"+data);
            PAT_TABLE.setParmValue(data);
        }
    }
    public void onTableShowHead(){
    	  if (this.getValue("ANTIBIOTIC_WAY").equals("02")) {
              PAT_TABLE.setHeader("选,30,boolean;入院日期,80,Timestamp;出院日期,80,Timestamp;出院科室,80,DEPT_CODE;病案号,100;姓名,80;性别,50,SEX_CODE;年龄,100;体重,50;诊断1,160;诊断2,160;诊断3,160;诊断4,160;诊断5,160;过敏史,160;住院总费用,100,double,#########0.00;药品总费用,100,double,#########0.00;抗生素费用,100,double,#########0.00;DDD累积量,100;在院天数,80;使用抗菌药品,100;联合使用,80,YES_NO1;治疗使用,80,YES_NO2;时机合理,80,boolean;合理治疗,80,boolean;手术名称,160;切口类别,160;就诊号,100");
          }else {
              PAT_TABLE.setHeader("选,30,boolean;入院日期,80,Timestamp;出院日期,80,Timestamp;出院科室,80,DEPT_CODE;病案号,100;姓名,80;性别,50,SEX_CODE;年龄,100;体重,50;诊断1,160;诊断2,160;诊断3,160;诊断4,160;诊断5,160;过敏史,160;住院总费用,100,double,#########0.00;药品总费用,100,double,#########0.00;抗生素费用,100,double,#########0.00;DDD累积量,100;在院天数,80;使用抗菌药品,100;联合使用,80,YES_NO1;预防使用,80,YES_NO2;时机合理,80,boolean;合理治疗,80,boolean;手术名称,160;切口类别,160;就诊号,100"); 
          }
    }
    public void onQueryMedical() {
        PAT_TABLE.acceptText();
        //PAT_PANEL = (TPanel) this.getComponent("PAT_PANEL");
        String caseNo = "";
        TParm parm = new TParm();
        PAT_TABLE = (TTable) this.getComponent("PAT_TABLE");
        TParm patParm = PAT_TABLE.getParmValue();
        int count = patParm.getCount();
        int num = 0;
        // 多选行
        for (int i = 0; i < count; i++) {
            boolean choose = patParm.getBoolean("CHOOSE", i);
            if (choose) {
                caseNo = patParm.getValue("CASE_NO", i);
                if (caseNo.length()<=0) {
					continue;
				}
                parm.addData("CASE_NO", caseNo);
                num++;
            }
        }
        parm.setCount(num);
        if (TabbedPane.getSelectedIndex()==1) {
        	if (parm.getCount("CASE_NO")<=0) {
				this.messageBox("请选择需要查询的数据");
				TabbedPane.setSelectedIndex(0);
				return;
			}
		}
        if (parm.getCount() > 0) {
            //MEDI_TABLE = (TTable) this.getComponent("MEDI_TABLE");
            TParm result = UDDAntibioticsMonitTool.getInstance().onQueryMedical(parm);
            if (result.getErrCode()<0) {
				this.messageBox("查询失败");
				TabbedPane.setSelectedIndex(0);
				return;
			}
            if (result.getCount()<=0) {
            	this.messageBox("没有需要查询的数据");
				TabbedPane.setSelectedIndex(0);
				return;
			}
            MEDI_TABLE.setParmValue(result);
        }
    }
    
    /**
     * 保存
     */
    public void onSave() {//add by wanglong 20130802
        if (TabbedPane.getSelectedIndex()==0) {
            PAT_TABLE.acceptText();
            TParm parm = PAT_TABLE.getShowParmValue();
            if (parm.getCount("MR_NO") < 1) {
                return;
            }
            TParm result = UDDAntibioticsMonitTool.getInstance().onSave(parm);
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
            }
            this.messageBox("P0001");// 保存成功
        }
    }
    
    /**
     * 汇出
     */
    public void onExport() {
        if (TabbedPane.getSelectedIndex()==0) {
            PAT_TABLE = (TTable) this.getComponent("PAT_TABLE");
            if (PAT_TABLE.getRowCount() <= 0) {
                this.messageBox("没有汇出数据");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(PAT_TABLE, "患者名单");
        }
        if (TabbedPane.getSelectedIndex()==1) {
            MEDI_TABLE = (TTable) this.getComponent("MEDI_TABLE");
            if (MEDI_TABLE.getRowCount() <= 0) {
                this.messageBox("没有汇出数据");
                return;
            }
            ExportExcelUtil.getInstance().exportExcel(MEDI_TABLE, "用药情况");
        }
    }

    /**
     * 病案号回车事件
     */
    public void onMrNo() {
        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO").trim());
        String mrNo = pat.getMrNo();
        this.setValue("MR_NO", mrNo);
        this.setValue("PAT_NAME", pat.getName());
        this.onQuery();
    }
    
    /**
     * 清空
     */
    public void onClear() {
        initComponent();
        this.clearValue("DEPT_CODE;MR_NO;PAT_NAME;DEAD;HEAL_LEVEL;ANTIBIOTIC_WAY");
        PAT_TABLE.setParmValue(new TParm());
        MEDI_TABLE.removeRowAll();
        this.setValue("ALL", "Y");
        this.setValue("SURGERY", "N");
        this.setValue("UNSURGERY", "N");
        this.callFunction("UI|HEAL_LEVEL|setEnabled", false);//add by wanglong 20130802
        this.setValue("USE_ANTIBIOTIC", "Y");
        this.callFunction("UI|ANTIBIOTIC_WAY|setEnabled", true);
        this.setValue("ANTIBIOTIC_WAY", "01");//预防使用
        this.setValue("DDD", "");// add by wanglong 20130807
        this.setValue("DEPT_CODE", "");
        this.setValue("DEAD", "");
        this.setValue("MR_NO", "");
        this.setValue("PAT_NAME", "");
        this.setValue("CHECK_ALL", "");
        TabbedPane.setSelectedIndex(0);
        onTableShowHead();
    }

    /**
     * 得到TTable
     * @param tag
     * @return
     */
    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }
    
    /**
     * 表格单击事件
     * @param row
     */
    public void onTableClicked(int row) {
        PAT_TABLE.acceptText();
        if (row > 0) {
            setValueForParm("MR_NO;PAT_NAME",data, PAT_TABLE.getSelectedRow());
        }
    }
    
    /**
     * 全选按钮
     */
    public void onSelectAll() {
        PAT_TABLE.acceptText();
        if (TabbedPane.getSelectedIndex()==0) {
            TParm parm = PAT_TABLE.getParmValue();
            if (parm.getCount() <= 0) {
                return;
            }
            // 全选勾选
            if (this.getValueBoolean("CHECK_ALL")) {
                for (int i = 0; i < parm.getCount(); i++) {
                    parm.setData("CHOOSE", i, "Y");
                }
            } else {
                for (int i = 0; i < parm.getCount(); i++) {
                    parm.setData("CHOOSE", i, "N");
                }
            }
            PAT_TABLE.setParmValue(parm);
        }
    }

    /**
     * 手术单选按钮切换事件
     */
    public void onChangeOpRadio() {
        if (this.getValueString("ALL").equals("Y") || this.getValueString("UNSURGERY").equals("Y")) {
            this.callFunction("UI|HEAL_LEVEL|setEnabled", false);
        } else {
            this.callFunction("UI|HEAL_LEVEL|setEnabled", true);
        }
    }
    
    /**
     * 使用抗菌药物CheckBox状态变化事件
     */
    public void onChangeAntibioticState() {
        if (this.getValueString("USE_ANTIBIOTIC").equals("Y")) {
            this.callFunction("UI|ANTIBIOTIC_WAY|setEnabled", true);
            onTableShowHead();
        } else {
            this.callFunction("UI|ANTIBIOTIC_WAY|setEnabled", false);
            PAT_TABLE.setHeader("选,30,boolean;入院日期,80,Timestamp;出院日期,80,Timestamp;出院科室,80,DEPT_CODE;病案号,100;姓名,80;性别,50,SEX_CODE;年龄,100;体重,50;诊断1,160;诊断2,160;诊断3,160;诊断4,160;诊断5,160;过敏史,160;住院总费用,100,double,#########0.00;药品总费用,100,double,#########0.00;抗生素费用,100,double,#########0.00;DDD累积量,100;在院天数,80;使用抗菌药品,100;联合使用,80,YES_NO1;使用状态,80,YES_NO2;时机合理,80,boolean;合理治疗,80,boolean;手术名称,160;切口类别,160;就诊号,100");
        }
    }
    // =================================排序功能开始==================================
    /**
     * 加入表格排序监听方法
     * 
     * @param table
     */
    public void addListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                // 调用排序方法;
                // 转换出用户想排序的列和底层数据的列，然后判断
                if (j == sortColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                // table.getModel().sort(ascending, sortColumn);
                // 表格中parm值一致,
                // 1.取paramw值;
                TParm tableData = table.getParmValue();
                TParm totAmtRow = tableData.getRow(tableData.getCount("CASE_NO") - 1);// add by wanglong 20130108
                tableData.removeRow(tableData.getCount("CASE_NO") - 1);// add by wanglong 20130108
                // System.out.println("tableData:"+tableData);
                tableData.removeGroupData("SYSTEM");
                // 2.转成 vector列名, 行vector ;
                String columnName[] = tableData.getNames("Data");
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                // System.out.println("==strNames=="+strNames);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                // System.out.println("==vct=="+vct);
                // 3.根据点击的列,对vector排序
                // System.out.println("sortColumn===="+sortColumn);
                // 表格排序的列名;
                String tblColumnName = table.getParmMap(sortColumn);
                // 转成parm中的列
                int col = tranParmColIndex(columnName, tblColumnName);
                // System.out.println("==col=="+col);
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                TParm lastResultParm = new TParm();// 记录最终结果
                lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// 加入中间数据
                for (int k = 0; k < columnName.length; k++) {// add by wanglong 20130108
                    lastResultParm.addData(columnName[k], totAmtRow.getData(columnName[k]));
                }
                lastResultParm.setCount(lastResultParm.getCount(columnName[0]));// add by wanglong 20130108
                table.setParmValue(lastResultParm);
            }
        });
    }

    /**
     * 列名转列索引值
     * 
     * @param columnName
     * @param tblColumnName
     * @return
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                // System.out.println("tmp相等");
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 得到 Vector 值
     * 
     * @param group
     *            String 组名
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int 最大行数
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
     * vectory转成param
     */
    private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        // 行数据;
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
    // ================================排序功能结束==================================
}
