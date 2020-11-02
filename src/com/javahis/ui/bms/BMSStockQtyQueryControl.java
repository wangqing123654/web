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
 * <p>Title: ѪƷ����ѯ</p>
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
     * ��ʼ������
     */
    public void onInit() {
		// �������
		addListener(getTTable(TABLE));        

    }	

    /**
     * ��ѯ����
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
    	//   ��ô�����Ϣ��Ϣ
    	if (result.getErrCode() < 0) {
    	    messageBox(result.getErrText());
    	    return;      
    	}    	
        if (result == null || result.getCount() <= 0) {
            this.messageBox("û�в�ѯ����");
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
     * ��շ���
     */
    public void onClear() {
        String clearStr = "BLOOD_NO;BLD_CODE;BLD_TYPE;SUBCAT_CODE;END_DATE;STATE_CODE";
        this.clearValue(clearStr);
        getTable("TABLE").removeRowAll();
    }

    /**
     * ���ѪƷ
     */
    public void onChangeBld() {
        String bld_code = getComboBox("BLD_CODE").getSelectedID();
        ((TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).setBldCode(bld_code);
        ((TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).onQuery();
    }

    /**
     * �õ�ComboBox����
     * @param tagName Ԫ��TAG����
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * �õ�Table����
     * @param tagName Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
	 /**
	 * �����������������
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = getTTable(TABLE).getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				//getTMenuItem("save").setEnabled(false);
			}
		});
	}
	
	/**
	 * �õ� Vector ֵ
	 * @param group String ����
	 * @param names String "ID;NAME"
	 * @param size int �������
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
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// System.out.println("===vectorTable==="+vectorTable);
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
		getTTable(TABLE).setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

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
	 * �õ�TABLE
	 * @param tag String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}	    
}
