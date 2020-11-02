package com.javahis.ui.bil;

import com.javahis.util.ExportExcelUtil;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import com.dongyang.util.Log;

/**
 * <p>Title: 在院患者医疗费用总表</p>
 *
 * <p>Description: 在院患者医疗费用总表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.11.20
 * @version 1.0
 */
public class BILInHospTotalWordControl extends TControl {
    String[] chargName = {"CHARGE01", "CHARGE02", "CHARGE03", "CHARGE04",
            "CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08",
            "CHARGE09",
            "CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13",
            "CHARGE14",
            //===zhangp 20120307 modify start
            "CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18",
            "CHARGE19", "CHARGE20", "CHARGE21"};
    //===zhangp 20120307 modify end
    private TParm parmName; //费用名称
	private TParm parmCode; //费用代码
	// ==========modify-begin (by wanglong 20120710)===============
	private TTable table;//获取单档表单
	// 以下作为表单排序的辅助
	//private Compare compare = new Compare();
	private BILComparator comparator=new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	// ==========modify-end========================================
	
    public void onInit() {
        super.onInit();
        initPage();
    }


    /**
     * 日志输出
     * @param text String 日志内容
     */
    public void out(String text) {
        Log.getInstance().out(text);
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        out("begin");
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_DATE", yesterday);
        setValue("E_DATE", SystemTool.getInstance().getDate());
        String sql = SYSSQL.getBillRecpparm(); //获得费用代码
        sql += " WHERE ADM_TYPE='I'";
        this.callFunction("UI|Table|removeRowAll");
        parmCode = new TParm(TJDODBTool.getInstance().select(sql));
        if (parmCode.getErrCode() < 0 || parmCode.getCount() <= 0) {
            this.messageBox("设置费用字典有问题");
            return;
        }
        //获得费用名称
        sql =
                "SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
        parmName = new TParm(TJDODBTool.getInstance().select(sql));
		// ==========modify-begin (by wanglong 20120710)===============
        //TTable table = (TTable)this.getComponent("Table");
		 table = (TTable) this.getComponent("Table");
		// 为表单添加监听器，为排序做准备。
		addListener(table);
		// ==========modify-end========================================
        table.setParmValue(getHeard());
    }

    /**
     * 添加表头
     * @return TParm
     */
    private TParm getHeard() {
        TParm heardParm = new TParm();
        heardParm.addData("DEPT_CHN_DESC", "科室名称");
        heardParm.addData("STATION_DESC", "病区");
        heardParm.addData("TOT_AMT", "合计金额");
        heardParm.addData("LUMPWORK_IN", "套餐内金额");
        heardParm.addData("LUMPWORK_OUT", "套餐外金额");
        for (int i = 0; i < chargName.length; i++) {
            heardParm.addData(chargName[i],
                              getChargeName(parmName,
                                            parmCode.getValue(chargName[i], 0)));
        }
        heardParm.setCount(1);
//        System.out.println("--heardParm--"+heardParm);
        return heardParm;
    }

    /**
     * 获得费用名称
     * @param parmName TParm
     * @param chargeCode String
     * @return String
     */
    private String getChargeName(TParm parmName, String chargeCode) {
        for (int i = 0; i < parmName.getCount(); i++) {
            if (parmName.getValue("ID", i).equals(chargeCode)) {
                return parmName.getValue("CHN_DESC", i);
            }
        }
        return "";
    }

    /**
     * 打印
     */
    public void onPrint() {
        print();
    }

    /**
     * 调用报表打印预览界面
     */
    private void print() {
        TTable table = (TTable)this.getComponent("Table");
        int row = table.getRowCount();
        if (row < 2) {
            this.messageBox_("先查询数据!");
            return;
        }
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyyMMdd");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd HH:mm:ss");
        TParm printData = this.getPrintDate(startTime, endTime);
        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyy/MM/dd");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyy/MM/dd");
        TParm parm = new TParm();
//        parm.setData("TITLE", "TEXT","在院患者医疗费用总表");
        parm.setData("DATE", "TEXT", sDate + " 至 " + eDate);
        parm.setData("E_DATE", eDate);
        parm.setData("OPT_USER", "TEXT", Operator.getName());
        parm.setData("printDate", "TEXT", sysDate);
        parm.setData("T1", printData.getData());
        parm.setData("TOT_AMT", "TEXT", "合计:  " + table.getShowParmValue().getValue("TOT_AMT", table.getRowCount()-1));//===zhangp 20120824
        // System.out.println("报表数据" + printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\IBS\\BILInHospPatTotFee.jhw",
                             parm);

    }


    /**
     * 整理打印数据
     * @param startTime String
     * @param endTime String
     * @return TParm
     */
    private TParm getPrintDate(String startTime, String endTime) {
        TParm selParm = new TParm();
        String deptWhere = "";
        if (getValue("DEPT_CODE") != null) {
            if (getValue("DEPT_CODE").toString().length() != 0)
                deptWhere = " AND A.DEPT_CODE = '" + getValue("DEPT_CODE") +
                            "'  ";
        }
        if (this.getValue("STATION_CODE") != null &&
            this.getValueString("STATION_CODE").length() > 0) {
            deptWhere += " AND A.STATION_CODE = '" +
                    this.getValueString("STATION_CODE") + "'";
        }
        //自费
        if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_1"))) {
            deptWhere += " AND E.NHI_CTZ_FLG='N' ";
        }
        //医保
        if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_2"))) {
            deptWhere += " AND E.NHI_CTZ_FLG='Y' ";
            if (this.getValueString("CTZ_CODE").length() > 0) {
                deptWhere += " AND E.CTZ_CODE='" +
                        this.getValueString("CTZ_CODE") + "' ";
            }
        }
        if ("Y".equalsIgnoreCase(this.getValueString("tCheckBox_0"))){
        	//套餐
            if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_3"))) {
            	if(this.getValue("LUMPWORK_CODE") != null &&
            			this.getValueString("LUMPWORK_CODE").length() > 0){
            		deptWhere += " AND F.LUMPWORK_CODE ='"+this.getValue("LUMPWORK_CODE")+"'";
            	}else{
            		deptWhere += " AND F.LUMPWORK_CODE IS NOT NULL ";
            	}              
            }
            //非套餐
            if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_4"))) {
                deptWhere += " AND F.LUMPWORK_CODE IS NULL  ";
            }
        }
       
        String regionWhere = "";
        if (!"".equals(Operator.getRegion()))
            regionWhere = " AND C.REGION_CODE = '" + Operator.getRegion() +
                          "' ";
        String sql =
                "SELECT A.DEPT_CODE,A.STATION_CODE,SUM(B.AR_AMT) AS TOT_AMT,B.REXP_CODE,C.DEPT_CHN_DESC,D.STATION_DESC " +
                "  FROM IBS_BILLM A,IBS_BILLD B,SYS_DEPT C ,SYS_STATION D,SYS_CTZ E,ADM_INP F " +
                " WHERE A.BILL_NO=B.BILL_NO " +
                "   AND A.BILL_SEQ=B.BILL_SEQ " +
                "   AND A.DEPT_CODE=C.DEPT_CODE(+) " +
                "   AND A.STATION_CODE=D.STATION_CODE(+) " +
                "   AND A.CASE_NO=F.CASE_NO " +
                "   AND A.CTZ1_CODE=E.CTZ_CODE(+)" +
                "   AND A.BILL_DATE BETWEEN TO_DATE ('" + startTime +
                "000000', 'yyyyMMddhh24miss') " +
                "   AND TO_DATE ('" + endTime + "235959', 'yyyyMMddhh24miss') " +
//                "   AND A.CHARGE_DATE NOT BETWEEN TO_DATE ('" + startTime +
//                "000000', 'yyyyMMddhh24miss') " +
//                "   AND TO_DATE ('" + endTime + "235959', 'yyyyMMddhh24miss') " +
                "   AND (A.CHARGE_DATE >= TO_DATE ('" + endTime + "235959', 'yyyyMMddhh24miss') OR A.CHARGE_DATE IS NULL) " +
                deptWhere +
                regionWhere +
                " GROUP BY A.DEPT_CODE,A.STATION_CODE,B.REXP_CODE,C.DEPT_CHN_DESC,D.STATION_DESC" +
                " ORDER BY A.DEPT_CODE";
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount("DEPT_CODE") < 1) {
            //查无数据
            this.messageBox("E0008");
            this.initPage();
//            TTable table = (TTable)this.getComponent("Table");
//            table.removeRowAll();
            return selParm;
        }
        BILRecpChargeForPrint endData = new BILRecpChargeForPrint();
        TParm endParm = endData.getValue(selParm);
        TParm resultParm = getHeard(); //表头
        int count = resultParm.getCount();
        for (int i = 0; i < endParm.getCount(); i++) {
            resultParm.setRowData(count, endParm, i);
            count++;
        }
        resultParm.setCount(count);
        resultParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        resultParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
        resultParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        for (int i = 0; i < chargName.length; i++) {
            resultParm.addData("SYSTEM", "COLUMNS", chargName[i]);
        }
        this.callFunction("UI|Table|setParmValue", resultParm);
        return resultParm;
    }

    /**
     * 查询
     */
    public void onQuery() {
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyyMMdd");
        TParm printData = this.getPrintDate(startTime, endTime);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {

        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "在院患者医疗费用总表");
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
        // TTable table = (TTable)this.getComponent("Table");
        //  table.removeRowAll();
        this.clearValue("STATION_CODE;DEPT_CODE;LUMPWORK_CODE");
        this.callFunction("UI|tCheckBox_0|setSelected",false);
        this.callFunction("UI|tRadioButton_3|setEnabled", false);
        this.callFunction("UI|tRadioButton_4|setEnabled", false);
        //this.callFunction("UI|tRadioButton_5|setSelected",true);
        this.callFunction("UI|tRadioButton_4|setSelected",true);
        this.callFunction("UI|LUMPWORK_CODE|setEnabled", false);
    }

    /**
     * 科室combo事件
     */
    public void onDEPT() {
        this.clearValue("STATION_CODE");
        this.callFunction("UI|STATION_CODE|onQuery");
    }

    /**
     * 单选按钮事件
     * @param type String
     */
    public void onRodio(String type) {
        if ("CTZ".equalsIgnoreCase(type)) {
            this.callFunction("UI|CTZ_CODE|setEnabled", true);
        } else {
            this.callFunction("UI|CTZ_CODE|setEnabled", false);
        }
    }
    
    /**
     * 有无套餐复选框按钮事件
     * @param type String
     */
    public void onCheckBox(String type) {
        if ("Y".equalsIgnoreCase(this.getValueString("tCheckBox_0"))) {
            this.callFunction("UI|tRadioButton_3|setEnabled", true);
            this.callFunction("UI|tRadioButton_4|setEnabled", true);
            if ("LUMPWORK".equalsIgnoreCase(type)) {
                this.callFunction("UI|LUMPWORK_CODE|setEnabled", true);
            } else {
                this.callFunction("UI|LUMPWORK_CODE|setEnabled", false);
                this.clearValue("LUMPWORK_CODE");
            }
        } else {
            this.callFunction("UI|tRadioButton_3|setEnabled", false);
            this.callFunction("UI|tRadioButton_4|setEnabled", false);
            this.callFunction("UI|LUMPWORK_CODE|setEnabled", false);
            this.clearValue("LUMPWORK_CODE");
        }
    }
    
	// ==========modify-begin (by wanglong 20120710)===============
	// 以下为响应鼠标单击事件的方法：用于获取全部单元格的值，并按某列排序。以及相关辅助方法。
	/**
	 * 加入表格排序监听方法
	 * @param table TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int i = table.getTable().columnAtPoint(me.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = table.getParmValue();
				//=====标题行 和 "总计"行  不参与处理处理======
				TParm titRowParm=new TParm();//记录标题行
				titRowParm.addRowData(table.getParmValue(), 0);
				TParm totRowParm=new TParm();//记录“总计”行
				totRowParm.addRowData(table.getParmValue(), tableData.getCount()-1);
				
				int rowCount=tableData.getCount();//数据的总行数（包括小计行和总计行）
				tableData.removeRow(0);//去除第一行（标题行）
				tableData.removeRow(tableData.getCount()-1);//去除最后一行(总计行)
				//=========================================
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
				comparator.setDes(ascending);
				comparator.setCol(col);
				java.util.Collections.sort(vct, comparator);
				// 将排序后的vector转成parm;
				TParm lastResultParm=new TParm();//记录最终结果
				TParm tmpParm=cloneVectoryParam(vct, new TParm(), strNames);
				lastResultParm.addRowData(titRowParm, 0);//加入标题行
				for (int k = 0; k < tmpParm.getCount(); k++) {
					lastResultParm.addRowData(tmpParm, k);//加入中间数据
				}
				lastResultParm.addRowData(totRowParm, 0);//加入总计行
				lastResultParm.setCount(rowCount);
				table.setParmValue(lastResultParm);
				//getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * 得到 Vector 值
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
	 * 转换parm中的列
	 * @param columnName String[]
	 * @param tblColumnName String
	 * @return int
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
	 * vectory转成param
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
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
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
		//================start===============
		//table.setParmValue(parmTable);
		return parmTable;
		//================end=================
		// System.out.println("排序后===="+parmTable);
	}
	// ==========modify-end========================================
}
