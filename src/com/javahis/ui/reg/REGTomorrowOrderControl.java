package com.javahis.ui.reg;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;


/**
 * <p>Title: ҽ��ԤԼ�ҺŲ�ѯ</p>
 *
 * <p>Description:ҽ��ԤԼ�ҺŲ�ѯ </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author huangtt 20131104
 * @version 1.0
 */

public class REGTomorrowOrderControl extends TControl{
	
	//�������
	private boolean ascending = false;
	private int sortColumn =-1;
	private Compare compare = new Compare();
	
	private static TTable table; 
	private static TCheckBox telAll;
	
	public void onInit(){
		
		//��ʼ��REGION��½Ĭ�ϵ�¼����
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE"))); 
        
        TComboBox admType = (TComboBox)this.getComponent("ADM_TYPE");
        admType.setValue("O");
        
        callFunction("UI|DEPT_CODE|onQuery");
        table = (TTable) getComponent("table");        
        Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE",
				StringTool.rollDate(date, +1).toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("END_DATE", StringTool.rollDate(date, +1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		
		telAll = (TCheckBox) getComponent("TEL_ALL");
		
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onSelect");
	
     
	}
	
	public void onQuery(){
		table.removeRowAll();
		this.setValue("SUM", "");
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		
		String sql="SELECT 'N' FLG,A.REGION_CODE, A.ADM_TYPE, B.MR_NO, B.PAT_NAME, B.TEL_HOME, A.ADM_DATE, A.REG_DATE," +
				" A.SESSION_CODE, A.CLINICROOM_NO, A.DEPT_CODE, A.DR_CODE, A.REALDEPT_CODE," +
				" A.REALDR_CODE, A.CLINICTYPE_CODE, A.QUE_NO, C.USER_NAME, A.OPT_DATE" +
				" FROM REG_PATADM A, SYS_PATINFO B, SYS_OPERATOR C" +
				" WHERE A.MR_NO=B.MR_NO  AND A.APPT_CODE='Y' AND A.REGCAN_USER IS NULL AND A.REGCAN_DATE IS NULL" +
				" AND A.ADM_DATE BETWEEN TO_DATE ('" + date_s + "', 'YYYYMMDDHH24MISS') " +
			    " AND TO_DATE ('" + date_e + "', 'YYYYMMDDHH24MISS')"+
			    " AND A.OPT_USER=C.USER_ID";
		
//		if (!getValueString("ADM_TYPE").equals("")) {
			sql += " AND A.ADM_TYPE = 'O'";
//		}
		if (!getValueString("SESSION_CODE").equals("")) {
			sql += " AND A.SESSION_CODE = '" + getValueString("SESSION_CODE") + "'";
		}
		if (!getValueString("CLINICROOM_NO").equals("")) {
			sql += " AND A.CLINICROOM_NO = '" + getValueString("CLINICROOM_NO") + "'";
		}
		if (!getValueString("DEPT_CODE").equals("")) {
			sql += " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE") + "'";
		}
		if (!getValueString("DR_CODE").equals("")) {
			sql += " AND A.DR_CODE = '" + getValueString("DR_CODE") + "'";
		}
		sql += " ORDER BY A.REG_DATE";
//		System.out.println(sql);
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount()<0){
			this.messageBox("û��Ҫ��ѯ������");
			return;
		}
		table.setParmValue(result);
		this.setValue("SUM", result.getCount()+"");
		//����������
		addListener(this.getTable("TABLE")); 
	}
	/**
	 * �ж��Ƿ�Ϊ�ֻ���
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles){
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	public void onTelAll(){
		table.acceptText();
		String flg= "N";
		if(telAll.isSelected()){
			flg = "Y";
		}else{
			flg = "N";
		}
		TParm tableParm = table.getParmValue();
		int count =0;
		for(int i=0;i<tableParm.getCount("FLG");i++ ){
			String tel=tableParm.getValue("TEL_HOME", i);
			if(this.isMobileNO(tel)){
				tableParm.setData("FLG", i, flg);
			}
		}
		table.setParmValue(tableParm);
		for(int i=0;i<tableParm.getCount("FLG");i++){
			if(tableParm.getBoolean("FLG", i)){
				count++;
			}
		}
		if(telAll.isSelected()){
			this.setValue("TELNUM", count+"");
		}else{
			this.setValue("TELNUM", "");
		}
		
	}
	
	
	
	public void onSelect(Object obj){
		TTable table = (TTable) obj;
		table.acceptText();
		TParm tableP = table.getParmValue();
		int count = 0;
		for(int i=0;i<tableP.getCount("FLG");i++){
			if(tableP.getBoolean("FLG", i)){
				count++;
			}
		}
		this.setValue("TELNUM", count+"");
	}
	
	
	public void onMessage(){
		table.acceptText();
		TParm tableParm = table.getShowParmValue();
		TParm parm = new TParm();
		for(int i=0;i<tableParm.getCount("MR_NO");i++){
			if(tableParm.getBoolean("FLG", i)){
				parm.addData("MrNo", tableParm.getValue("MR_NO", i));
				parm.addData("Name", tableParm.getValue("PAT_NAME", i));
				 String content = "��ԤԼ��̩��ҽԺ"+
				 	tableParm.getValue("ADM_DATE", i)+" "+
				 	tableParm.getValue("SESSION_CODE", i) +
					"��"+tableParm.getValue("QUE_NO", i)+"��"+
					tableParm.getValue("DR_CODE", i) +"ҽ�����������"+tableParm.getValue("PAT_NAME", i)+"���ˣ�����ˬԼ������ȡ��������ǰһ�첦�����绰4001568568��Ϊ�˱�֤��׼ʱ���������ǰ����Һ�����";
				 parm.addData("Content", content);
				 parm.addData("TEL1", tableParm.getValue("TEL_HOME", i));
			}
			
		}
		 TIOM_AppServer.executeAction(
					"action.reg.REGAction", "orderMessage", parm);
		 this.messageBox("���ͳɹ���");
	}
	
	/**
	 * ����Excel
	 * */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = (TTable) callFunction("UI|table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "����Ԥ���������ϸ");
	}
	
	public void onPrint(){
		String date_s = getValueString("START_DATE");
		String date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "/").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "/").replace(" ", "");
		
		TTable table = this.getTable("TABLE");
    	if (table.getRowCount() <= 0) {
            this.messageBox("û�д�ӡ����");
            return;
        }
        TParm data = new TParm();
        data.setData("TITLE", "TEXT", "����Ԥ��������ϸ����");
        data.setData("DATE", "TEXT", date_s+"~"+date_e);
        data.setData("USER", "TEXT", "�Ʊ��ˣ�"+Operator.getName());
		TParm tableParm = table.getShowParmValue();
		TParm parm = new TParm();
//REGION_CODE;ADM_TYPE;;;TEL_HOME;;;;;;;REALDEPT_CODE;REALDR_CODE;;
		for(int i=0;i<table.getRowCount();i++){
    		parm.addData("MR_NO", tableParm.getValue("MR_NO",i));
    		parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME",i));
    		parm.addData("TEL_HOME", tableParm.getValue("TEL_HOME",i));
    		parm.addData("ADM_DATE", tableParm.getValue("ADM_DATE",i));
    		parm.addData("SESSION_CODE", tableParm.getValue("SESSION_CODE",i));
    		parm.addData("CLINICROOM_NO", tableParm.getValue("CLINICROOM_NO",i));
    		parm.addData("DEPT_CODE", tableParm.getValue("DEPT_CODE",i));
    		parm.addData("DR_CODE", tableParm.getValue("DR_CODE",i));
    		parm.addData("CLINICTYPE_CODE", tableParm.getValue("CLINICTYPE_CODE",i));
    		parm.addData("QUE_NO", tableParm.getValue("QUE_NO",i));
    		
    	}
    	
    
    	parm.setCount(parm.getCount("MR_NO"));
    	parm.addData("SYSTEM", "COLUMNS", "MR_NO");
    	parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
    	parm.addData("SYSTEM", "COLUMNS", "TEL_HOME");
    	parm.addData("SYSTEM", "COLUMNS", "ADM_DATE");
    	parm.addData("SYSTEM", "COLUMNS", "SESSION_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "CLINICROOM_NO");
    	parm.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "DR_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "CLINICTYPE_CODE");
    	parm.addData("SYSTEM", "COLUMNS", "QUE_NO");
    	
    	data.setData("TABLE",parm.getData());
    	this.openPrintWindow("%ROOT%\\config\\prt\\reg\\REGTomorrowOrderPrint.jhw", data);
	}
	
	public void onClear(){
		table.removeRowAll();
		clearValue("START_DATE;END_DATE;ADM_TYPE;SESSION_CODE;CLINICROOM_NO;DEPT_CODE;DR_COD;SUM;TELNUM");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE",
				StringTool.rollDate(date, +1).toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("END_DATE", StringTool.rollDate(date, +1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");

		telAll.setSelected(false);
		TComboBox admType = (TComboBox)this.getComponent("ADM_TYPE");
        admType.setValue("O");
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
	 * �����������������
	 * 
	 * @param table
	 */
	public  void addListener(final TTable table) {
//		 System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		 TParm tableDate = table.getParmValue();
		 
//		 System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
					int rowCount = table.getRowCount();
//					Map m1=new HashMap();
//					for (int i = 0; i < rowCount; i++) {
//						m1.put(i,new Color(255,255,0));
//					}
//					 table.setRowColorMap(m1);
//				 System.out.println("===��rrrrrrrǰ===");		
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
//				 System.out.println("+i+"+i);
//				 System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);
				TParm tableData = table.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
//				 System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);

				// 3.���ݵ������,��vector����
//				 System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				
				java.util.Collections.sort(vct, compare);
				cloneVectoryParam(vct, new TParm(), strNames,table);
				// getTMenuItem("save").setEnabled(false);
			}
		});
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
	 * vectorת��TParm
	 */
	 void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames,TTable table) {
		//
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
		table.setParmValue(parmTable);
		
		// System.out.println("�����===="+parmTable);

	}
	 

}
