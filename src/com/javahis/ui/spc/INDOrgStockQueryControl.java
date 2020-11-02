package com.javahis.ui.spc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.spc.INDSQL;
import jdo.spc.IndStockDTool;
import jdo.sys.Operator;       
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ���ſ���ѯ
 * </p>
 *
 * <p>
 * Description: ���ſ���ѯ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.09.22
 * @version 1.0
 */
public class INDOrgStockQueryControl
    extends TControl {
    public INDOrgStockQueryControl() {
    }

    private TTable table_m;    

	private int sortColumn = -1;
	private boolean ascending = false;
	private BILComparator compare = new BILComparator();

	
    /**
     * ��ʼ������
     */
    public void onInit() {
        // ��ʾȫԺҩ�ⲿ��
        if (!this.getPopedem("deptAll")) {
            //((TextFormatINDOrg)this.getComponent("ORG_CODE")).o
            //getTextFormat("ORG_CODE")
//            if (parm.getCount("NAME") > 0) {
//                getComboBox("ORG_CODE").setSelectedIndex(1);
//            }
        }
        setValue("ACTIVE_FLG", "Y");
    	setValue("STOP_FLG", "N");    
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // ���õ����˵�
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // ������ܷ���ֵ����
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
                
        this.table_m = this.getTable("TABLE");
//		addListener(table_m);
		
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
//        if ("".equals(this.getValueString("ORG_CODE"))) {
//            this.messageBox("��ѯ���Ų���Ϊ��");
//            return;
//        }
        this.getTable("TABLE").removeRowAll();
        TParm parm = new TParm();  
        if (!"".equals(this.getValueString("ORG_CODE"))) {
            parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        }
        if (!"".equals(this.getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        }
        if (!"".equals(this.getValueString("BATCH_NO"))) {
            parm.setData("BATCH_NO", this.getValueString("BATCH_NO"));
        }
/*        if (!"".equals(this.getValueString("MAN_CODE"))) {
            parm.setData("MATERIAL_LOC_CODE", this.getValueString("MAN_CODE"));
        }*/
        if (!"".equals(this.getValueString("TYPE_CODE"))) {
            parm.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
        }
        if ("Y".equals(this.getValueString("SAFE_QTY"))) {
            parm.setData("SAFE_QTY", "SAFE_QTY");
        }  
        if ("Y".equals(this.getValueString("CHECK_NOT_ZERO"))) {
            parm.setData("STOCK_QTY", "CHECK_NOT_ZERO");
        }
       
        if ("Y".equals(this.getValueString("ORDER_TYPE_W")) &&
            "Y".equals(this.getValueString("ORDER_TYPE_C")) &&
            "Y".equals(this.getValueString("ORDER_TYPE_G"))) {  
            parm.setData("PHA_TYPE", "('W', 'C', 'G')");
        }
        if ("Y".equals(this.getValueString("ACTIVE_FLG"))){
        	parm.setData("ACTIVE_FLG","Y");
        }     
        else if  ("N".equals(this.getValueString("ACTIVE_FLG"))){
        	parm.setData("ACTIVE_FLG","N");  
        }   
        String mjFlg = this.getValueString("MJ_FLG");   
        parm.setData("MJ_FLG",mjFlg);		  
        TParm result = new TParm();
        //Ϊ�˻���������ʱ����
        TParm resultTemp = new TParm();
        //fux ����ȫ�� δ���� �����ж�(IND_STOCK�е�active_flg)
        if ("Y".equals(this.getValueString("ALL_FLG"))) {  
        	parm.setData("STOCK_ACTIVE", "ALL"); 
        }
        else if ("Y".equals(this.getValueString("Y_FLG"))) {  
            parm.setData("STOCK_ACTIVE", "Y");   
        }else if("Y".equals(this.getValueString("N_FLG"))) {
            parm.setData("STOCK_ACTIVE", "N");
        }    
        
        
        TParm indOrgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgByCode(getValueString("ORG_CODE"))));
        //fux modify 20141027  �ж��Ƿ�����������   ������������ҿ����Ϊ����  
        //fux modify 20141103 start 
        System.out.println("parm:"+parm);    
        if (!"Y".equals(this.getValueString("SHOW_BATCH"))) { 
          /*  result = IndStockDTool.getInstance().onQueryOrgStockQueryNotBatch(
                parm);*/
        	//�޸� ��ѯ ������Ϳ���� by liyh 20120808
        	//result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQL(parm)));
        	if("Y".equals(mjFlg)) {  
        		
        		if(indOrgParm.getCount() > 0 ){
        			String orgType = indOrgParm.getValue("ORG_TYPE",0);
        			parm.setData("ORG_TYPE",orgType);  
        		}   
        		
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQLNew(parm)));
        	}else {
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQueryNotBatchSQL(parm)));
        	}	  
        }     
        //�����ʾ����Ч�����ܲ�ѯ������ҩ     
        else {		            
           // result = IndStockDTool.getInstance().onQueryOrgStockQuery(parm);
        	//��ʾ����Ч��
        	if("Y".equals(mjFlg)) {
        		//TParm indOrgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgByCode(getValueString("ORG_CODE"))));
        		if(indOrgParm.getCount() > 0 ){                          
        			String orgType = indOrgParm.getValue("ORG_TYPE",0);      
        			parm.setData("ORG_TYPE",orgType);  
        		}   
        		result  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockDrugQuery(parm)));
        	}else {
        		//���ſ���ѯ(��ʾ���ź�Ч��) ---- IndStockDModule :  getOrgStockQuery(��������û����ֻ�ܴ�module��ĵ�INDSQL��,����ʵ��)
        		resultTemp  =  new TParm(TJDODBTool.getInstance().select(INDSQL.getOrgStockQuery(parm)));
        		//result = IndStockDTool.getInstance().onQueryOrgStockQuery(parm);
        	}
        }
        Map<String, Integer> map = new TreeMap<String, Integer>();
        String order_code="";
        for (int i = 0; i < resultTemp.getCount(); i++) { 
        	
//        	X.ORDER_CODE, X.ORDER_DESC, X.SPECIFICATION, X.CHN_DESC, X.STOCK_QTY," +
//			" X.UNIT_CHN_DESC,X.QTY,X.PRICE,X.STOCK_PRICE,X.STOCK_AMT,X.OWN_AMT," +  
//			" X.DIFF_AMT, X.BATCH_NO, X.VALID_DATE, X.STOCK_FLG," +
//			" X.SAFE_QTY, X.PHA_TYPE,X.MATERIAL_LOC_CODE, X.OWN_PRICE, X.ACTIVE_FLG ,X.DOSAGE_QTY, X.ORG_CHN_DESC,Y.BASE_QTY ,X.ORG_CODE" 
        	result.addData("ORDER_CODE", resultTemp.getValue("ORDER_CODE", i));
        	result.addData("ORDER_DESC", resultTemp.getValue("ORDER_DESC", i));
        	result.addData("SPECIFICATION", resultTemp.getValue("SPECIFICATION", i));
        	result.addData("CHN_DESC", resultTemp.getValue("CHN_DESC", i));
        	result.addData("STOCK_QTY", resultTemp.getValue("STOCK_QTY", i));
        	result.addData("UNIT_CHN_DESC", resultTemp.getValue("UNIT_CHN_DESC", i));
        	result.addData("QTY", resultTemp.getValue("QTY", i));
        	result.addData("PRICE", resultTemp.getValue("PRICE", i));
        	result.addData("STOCK_PRICE", resultTemp.getValue("STOCK_PRICE", i));
        	
        	result.addData("STOCK_AMT", resultTemp.getValue("STOCK_AMT", i));
        	result.addData("OWN_AMT", resultTemp.getValue("OWN_AMT", i));
        	result.addData("DIFF_AMT", resultTemp.getValue("DIFF_AMT", i));
        	result.addData("BATCH_NO", resultTemp.getValue("BATCH_NO", i));
        	result.addData("VALID_DATE", resultTemp.getValue("VALID_DATE", i));
        	result.addData("STOCK_FLG", resultTemp.getValue("STOCK_FLG", i));
        	result.addData("SAFE_QTY", resultTemp.getValue("SAFE_QTY", i));
        	result.addData("PHA_TYPE", resultTemp.getValue("PHA_TYPE", i));
        	result.addData("SAFE_QTY", resultTemp.getValue("SAFE_QTY", i));
        	result.addData("MATERIAL_LOC_CODE", resultTemp.getValue("MATERIAL_LOC_CODE", i));
        	result.addData("OWN_PRICE", resultTemp.getValue("OWN_PRICE", i));
        	result.addData("ACTIVE_FLG", resultTemp.getValue("ACTIVE_FLG", i));
        	result.addData("DOSAGE_QTY", resultTemp.getValue("DOSAGE_QTY", i));
        	result.addData("ORG_CHN_DESC", resultTemp.getValue("ORG_CHN_DESC", i));
        	
            order_code = resultTemp.getValue("ORDER_CODE", i);
            if (map.containsKey(order_code)) {
				map.remove(order_code);     
				map.put(order_code,        
						1);      
				result.addData("BASE_QTY", "");  
            }          
            else {     
				map.put(order_code,  
						0);        
				result.addData("BASE_QTY", resultTemp.getValue("BASE_QTY", i));
			}    
        }
        
        //fux modify 20141103 end
        if (result == null || result.getCount("ORDER_CODE") <= 0) {
            this.messageBox("û�в�ѯ����");    
            return;
        }  
//        Map map = new HashMap();
//        map.put("01", "��ҩ");
//        map.put("02", "�г�ҩ");
//        map.put("03", "�в�ҩ");
//        map.put("04", "������");
//        map.put("05", "��Һ");  
//        map.put("06", "�Ƽ�");
//        map.put("07", "�Լ�");
//        map.put("08", "ԭ��");
        double stock_amt = 0;
        double own_amt = 0;
        for (int i = result.getCount() - 1; i >= 0; i--) {
        	 if ("N".equals(this.getValueString("ORDER_TYPE_W")) &&
                     "W".equals(result.getValue("PHA_TYPE", i))&&!"Y".equals(result.getValue("CTRL_FLG", i))) {							
                     result.removeRow(i);
                     continue;
             }
            if ("N".equals(this.getValueString("ORDER_TYPE_C")) &&
                "C".equals(result.getValue("PHA_TYPE", i))) {
                result.removeRow(i);
                continue;
            }
            if ("N".equals(this.getValueString("ORDER_TYPE_G")) &&  
                "G".equals(result.getValue("PHA_TYPE", i))) {
                result.removeRow(i);
                continue;
            }
//            result.setData("TYPE_CODE", i,
//                           map.get(result.getData("TYPE_CODE", i))); 
            stock_amt += result.getDouble("STOCK_AMT", i);
            own_amt += result.getDouble("OWN_AMT", i);
        }
        if (result == null || result.getCount("ORDER_CODE") <= 0) {
            this.messageBox("û�в�ѯ����");
            return;     
        }
        this.getTable("TABLE").setParmValue(new TParm());  
        this.getTable("TABLE").setParmValue(result);  
        this.setValue("STOCK_AMT", StringTool.round(stock_amt, 2));
        this.setValue("OWN_AMT", StringTool.round(own_amt, 2));
        this.setValue("DIFF_AMT", StringTool.round(own_amt - stock_amt, 2));
        this.setValue("SUM_TOT", this.getTable("TABLE").getRowCount());
    }

    /**
     * ��շ���
     */
    public void onClear() {
    	  this.getTable("TABLE").removeRowAll();
        String clearStr = "ORG_CODE;ORDER_CODE;ORDER_DESC;BATCH_NO;TYPE_CODE;"
            + "SAFE_QTY;CHECK_NOT_ZERO;SHOW_BATCH;MAN_CODE;STOCK_AMT;OWN_AMT;"
            + "DIFF_AMT;SUM_TOT";
        this.clearValue(clearStr);
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setOrgCode("");
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).onQuery();
        this.setValue("ORDER_TYPE_W", "Y");
        this.setValue("ORDER_TYPE_C", "Y");
        this.setValue("ORDER_TYPE_G", "Y");
      
        
       
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        TTable table = getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        // ��ӡ����
        TParm date = new TParm();
        // ��ͷ����      
        date.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "ҩƷ���ͳ�Ʊ�");
        date.setData("ORG_CODE", "TEXT",  
                     "ͳ�Ʋ���: " +
                     this.getTextFormat("ORG_CODE").getText());
        date.setData("DATE", "TEXT",
                     "ͳ��ʱ��: " +
                     SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace('-', '/'));
        date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());              
        // �������
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();
        for (int i = 0; i < table.getRowCount(); i++) {
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION", i));
            parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
            parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
            parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
            parm.addData("OWN_AMT", tableParm.getValue("OWN_AMT", i));
            parm.addData("MATERIAL_CHN_DESC",
                         tableParm.getValue("MATERIAL_CHN_DESC", i));
        }
        parm.setCount(parm.getCount("ORDER_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");  
        parm.addData("SYSTEM", "COLUMNS", "MATERIAL_CHN_DESC");
        date.setData("TABLE", parm.getData());
        // ��β����
        date.setData("TOT", "TEXT", "�ϼƣ� "+getValueDouble("OWN_AMT"));
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDOrgStockQuery.jhw",
                             date);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        TTable table = this.getTable("TABLE");  
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "���ſ���ѯ");
    }

    /**
     * ����ѡ���¼�
     */
    public void onOrgAction() {
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setOrgCode(
            TypeTool.getString(getTextFormat("ORG_CODE").getValue()));
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).onQuery();
        ( (TComboINDMaterialloc)this.getComponent("MAN_CODE")).setSelectedIndex(
            0);
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
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
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
     * �õ�TextFormat����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }
    
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) { 
		table.getTable().
			getTableHeader().
				addMouseListener(
					new MouseAdapter() {
							public void mouseClicked(MouseEvent mouseevent) {
									int i = table.getTable().columnAtPoint(mouseevent.getPoint());
									int j = table.getTable().convertColumnIndexToModel(i);
									if (j == sortColumn) {
										ascending = !ascending;
									} else {
										ascending = true;
										sortColumn = j;
									}
															
									TParm tableData = table.getParmValue();// �����parmֵһ��,// 1.ȡparamwֵ;
									// 2.ת�� vector����, ��vector ;
									String columnName[] = tableData.getNames("Data");
									String strNames = "";
									for (String tmp : columnName) {
										strNames += tmp + ";";
									}
									strNames = strNames.substring(0, strNames.length() - 1);
									Vector vct = getVector(tableData, "Data", strNames, 0);
			
									String tblColumnName = table.getParmMap(sortColumn);// 3.���ݵ������,��vector����// ������������;
									
									int col = tranParmColIndex(columnName, tblColumnName);// ת��parm�е���
									compare.setDes(ascending);
									compare.setCol(col);
									java.util.Collections.sort(vct, compare);
									// ��������vectorת��parm;
									// Ϊ��ʹ�˷���ͨ�ã�����Ҫ��cloneVectoryParam()Ҳ��ͨ�ã����ܽ�����deptFeeTable
									cloneVectoryParam(vct, new TParm(), strNames, table);
								}
							}
				);
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
	@SuppressWarnings( { "rawtypes", "unchecked" })
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
	 * vectoryת��param
	 */
	@SuppressWarnings("rawtypes")
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames, final TTable table) {
		// ������->��
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
		table.setParmValue(parmTable);
	}
	
    /**
     * �龫����¼�			
     */
    public void onClick() {
    	String mjFlg = this.getValueString("MJ_FLG");
    	if("Y".equals(mjFlg)) {
    		this.setValue("ORDER_TYPE_W", "N");
    		this.setValue("ORDER_TYPE_C", "N");
    		this.setValue("ORDER_TYPE_G", "N");
    	}else {
    		this.setValue("ORDER_TYPE_W", "Y");
    		this.setValue("ORDER_TYPE_C", "Y");
    		this.setValue("ORDER_TYPE_G", "Y");
    	}
    }
                          
    public void onClickW() {
    	String flg = this.getValueString("ORDER_TYPE_W");
    	if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}
    }
    
    public void onClickC() {
    	String flg = this.getValueString("ORDER_TYPE_C");
    	if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}
    }
    
    public void onClickG() {			
    	String flg = this.getValueString("ORDER_TYPE_G");
    	if("Y".equals(flg)) {
    		this.setValue("MJ_FLG", "N");
    	}
    }



}
