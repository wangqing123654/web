package com.javahis.ui.bms;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.dongyang.control.TControl;
import jdo.bms.BMSBloodTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboBMSBldsubcat;

/**
 * <p>Title: 血品库存查询</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.10.8
 * @version 1.0
 */
public class BMSStockQtyQueryControl extends TControl {
	
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;	
	
	private static String TABLE = "TABLE";	
	
	/**
     * 初始化方法
     */
    public void onInit() {
		// 排序监听
		addListener(getTTable(TABLE));        

    }	

    /**
     * 查询方法
     */
    public void onQuery() {
//        TParm parm = new TParm();
//        String bood_no = this.getValueString("BLOOD_NO");
//        if (bood_no != null && bood_no.length() > 0) {
//            parm.setData("BLOOD_NO", bood_no);
//        }
//        String bld_code = this.getValueString("BLD_CODE");
//        if (bld_code != null && bld_code.length() > 0) {
//            parm.setData("BLD_CODE", bld_code);
//        }
//        String bld_type = this.getValueString("BLD_TYPE");
//        if (bld_type != null && bld_type.length() > 0) {
//            parm.setData("BLD_TYPE", bld_type);
//        }
//        String subcat_code = this.getValueString("SUBCAT_CODE");
//        if (subcat_code != null && subcat_code.length() > 0) {
//            parm.setData("SUBCAT_CODE", subcat_code);
//        }
//        String end_date = this.getValueString("END_DATE");
//        if (end_date != null && end_date.length() > 0) {
//            parm.setData("END_DATE", end_date);
//        }
//        String state_code = this.getValueString("STATE_CODE");
//        if (state_code != null && state_code.length() > 0) {
//            parm.setData("STATE_CODE", state_code);
//        }

//        TParm result = BMSBloodTool.getInstance().onQueryBloodQtyStock(parm);
    	String sql=" SELECT A.BLOOD_NO, A.RH_FLG, A.BLD_CODE, A.SUBCAT_CODE,"+
	       "        A.IN_DATE, A.BLD_TYPE, A.SHIT_FLG, A.END_DATE, A.IN_PRICE, A.BLOOD_VOL,"+ 
	       "        A.ORG_BARCODE, A.STATE_CODE, A.APPLY_NO, A.MR_NO, A.IPD_NO,"+ 
	       "        A.CASE_NO, A.ID_NO, A.USE_DATE, A.CROSS_MATCH_L, A.CROSS_MATCH_S,"+ 
	       "        A.ANTI_A, A.ANTI_B, A.RESULT, A.TEST_DATE, A.TEST_USER,"+ 
	       "        A.PRE_U, A.PRE_D, A.T, A.P, A.R,"+ 
	       "        A.WORK_USER, A.OUT_NO, A.OUT_DATE, A.OUT_USER,"+ 
	       "        A.TRAN_RESN,A.TRAN_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.DEPT_CODE ,B.PAT_NAME,D.UNIT_CHN_DESC,C.UNIT_CODE "+
	       " FROM BMS_BLOOD A ,SYS_PATINFO B ,BMS_BLDSUBCAT C,SYS_UNIT D "+ 
	       " WHERE A.MR_NO = B.MR_NO(+) " +
	       "   AND A.BLD_CODE = C.BLD_CODE " +
	       "   AND A.SUBCAT_CODE = C.SUBCAT_CODE " +
	       "   AND C.UNIT_CODE = D.UNIT_CODE " ;  
    	StringBuilder sbuilder = new StringBuilder(sql) ;
    	
        String bood_no = this.getValueString("BLOOD_NO");
        if (bood_no != null && bood_no.length() > 0) {
            sbuilder.append(" AND A.BLOOD_NO= '"+bood_no+"'") ;
        }
        String bld_code = this.getValueString("BLD_CODE");
        if (bld_code != null && bld_code.length() > 0) {
            sbuilder.append(" AND A.BLD_CODE= '"+bld_code+"'") ;
        }
        String bld_type = this.getValueString("BLD_TYPE");
        if (bld_type != null && bld_type.length() > 0) {
            sbuilder.append(" AND A.BLD_TYPE= '"+bld_type+"'") ;
        }
        String subcat_code = this.getValueString("SUBCAT_CODE");
        if (subcat_code != null && subcat_code.length() > 0) {
            sbuilder.append(" AND A.SUBCAT_CODE= '"+subcat_code+"'") ;
        }
        String end_date = this.getValueString("END_DATE");
        if (end_date != null && end_date.length() > 0) {
    		String sDate = end_date.substring(0, 19) ;
    		sbuilder.append(" AND TO_CHAR(END_DATE,'YYYY-MM-DD HH:mm:ss') <= '"+sDate+"'") ;            
        }
        String state_code = this.getValueString("STATE_CODE");
        if (state_code != null && state_code.length() > 0) {
            sbuilder.append(" AND A.STATE_CODE= '"+state_code+"'") ;
        }  
        sbuilder.append(" ORDER BY SUBCAT_CODE,BLD_TYPE,RH_FLG,END_DATE,STATE_CODE"); //add by wanglong 20121212
    	TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.toString()));
    	//   获得错误信息消息
    	if (result.getErrCode() < 0) {
    	    messageBox(result.getErrText());
    	    return;      
    	}    	
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            this.callFunction("UI|TABLE|setParmValue", new TParm());
            return;
        }
    	Set<String>  unitSet = new HashSet<String>() ;
    	double bldVol = 0.0 ;
    	for (int i = 0; i < result.getCount(); i++) {
    		unitSet.add(result.getValue("UNIT_CHN_DESC", i)) ;
    		bldVol+=result.getDouble("BLOOD_VOL", i) ;			
		}
    	if(unitSet.size()==1){
    		this.setValue("BLD_VOL", bldVol) ;
    		this.setValue("UNIT_LABEL", unitSet.iterator().next() ) ;
    	}    	
        this.getTable("TABLE").setParmValue(result);
    }


    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "BLOOD_NO;BLD_CODE;BLD_TYPE;SUBCAT_CODE;END_DATE;STATE_CODE";
        this.clearValue(clearStr);
        getTable("TABLE").removeRowAll();
    }

    /**
     * 变更血品
     */
    public void onChangeBld() {
        String bld_code = getComboBox("BLD_CODE").getSelectedID();
        ((TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).setBldCode(bld_code);
        ((TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).onQuery();
    }

    /**
     * 得到ComboBox对象
     * @param tagName 元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到Table对象
     * @param tagName 元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
	 /**
	 * 加入表格排序监听方法
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTTable(TABLE).getParmValue();
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
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				//getTMenuItem("save").setEnabled(false);
			}
		});
	}
	
	/**
	 * 得到 Vector 值
	 * @param group String 组名
	 * @param names String "ID;NAME"
	 * @param size int 最大行数
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
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// System.out.println("===vectorTable==="+vectorTable);
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
		getTTable(TABLE).setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

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
	 * 拿到TABLE
	 * @param tag String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}	    
}
