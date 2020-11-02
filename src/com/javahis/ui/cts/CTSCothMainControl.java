package com.javahis.ui.cts;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.hibernate.event.def.OnReplicateVisitor;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.device.RFIDMidUtils;
import com.javahis.device.Uitltool;
import com.javahis.system.textFormat.TextFormatSYSOperator;



/**
 * 20130808 ����
 * @author Administrator
 *
 */
public class CTSCothMainControl extends TControl{
	private static String CLOTH_NO = "";
	private static String INV_CODE ="";
	private static String OWNER = "";
	private static String OWNER_CODE= "";
//	private static String DEPT_CODE = "";
	private static String STATION_CODE = "";
	private static String optIp = "";
	private static String optId = "";
	private static String ACTIVE_FLG = "N";
	private static String PAT_FLG = "N";
	private static String WRITE_FLG = "";
	private static String TURN_POINT = "";
//	private static String STATE = "";
	private static TCheckBox active_flg;
	private static TTable table;
	private static int row = -1;
	private static TParm rowParm = null;
	private static TTextField cloth_no;
	private static TTextField owner_code;
//	private static TTextField turn_point;
	private static TTable rfid_table;
//	private static TTextField inv_code;
	private static TRadioButton tRadioButton_1;
	private static TRadioButton tRadioButton_0;
	private static TComboBox write_flg;
	private static boolean rfid_flg; 
	private static TParm RfidParm;
	private static boolean rfid_write_flg;  //ÿд��һ�α�Ϊtrue������Ϊfalse
	
	private static String houseman;  //ʵϰҽ������
	
	private boolean ascending = false;
	private int sortColumn =-1;
	private Compare compare = new Compare();

	
    /**
	 * ��ʼ������
	 */
	public void onInit() {
		
		
		optIp = Operator.getIP();
		optId = Operator.getID();
		active_flg = (TCheckBox) getComponent("ACTIVE_FLG");
		cloth_no = (TTextField) getComponent("CLOTH_NO");
		owner_code = (TTextField) getComponent("OWNER_CODE");
//		turn_point = (TTextField) getComponent("TURN_POINT");
		table = (TTable) getComponent("TABLE");
		rfid_table = (TTable) getComponent("RFID_TABLE");
//		inv_code = (TTextField) getComponent("INV_CODE");
		tRadioButton_1 =  (TRadioButton) getComponent("tRadioButton_1");
		tRadioButton_0 =  (TRadioButton) getComponent("tRadioButton_0");
		write_flg = (TComboBox) getComponent("WRITE_FLG");
		rfid_flg=false;
		rfid_write_flg=false;
		
		
		RfidParm = new TParm();
		TParm parm = new TParm();
//        // ���õ����˵�
//		inv_code.setPopupMenuParameter("UI", getConfigParm().newConfig(
//            "%ROOT%\\config\\inv\\INVBasePopup.x"), parm);
//        // ������ܷ���ֵ����
//		inv_code.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
//                                    "popReturn");
		
		TextFormatSYSOperator ss = new TextFormatSYSOperator();
//		ss.setAttribute("EndDateFlg", "1");
		
		//����ʵϰҽ������
		String sql="SELECT USER_ID FROM SYS_OPERATOR WHERE HOUSEMAN_FLG='Y'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		houseman=selParm.getValue("USER_ID", 0);
		
//		onQuery();
	}
	
	public void popReturn(String tag, Object obj) {
        if (obj == null)
            return;
        //invBase���ص����ݰ�
        TParm parm = (TParm) obj;
        //System.out.println("parm==="+parm);
        String inv_chn_desc = parm.getValue("INV_CHN_DESC");
        INV_CODE = parm.getValue("INV_CODE");
        setValue("INV_CODE", inv_chn_desc);
	}
	public void selStation(){
		OWNER = getValueString("OWNER");
		if(OWNER.equals(houseman)){
			clearValue(OWNER_CODE);
			owner_code.setEnabled(true);
		}else{
			String sql="SELECT USER_ID, USER_NAME, COST_CENTER_CODE FROM SYS_OPERATOR WHERE USER_ID = '"+OWNER+"'";
			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			this.setValue("STATION_CODE", selParm.getValue("COST_CENTER_CODE", 0));
			this.setValue("OWNER_CODE", selParm.getValue("USER_NAME", 0));
			owner_code.setEnabled(false);
		}
		
	}
	
	 /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
	
    
	
	/**
	 * ����
	 */
	public void onSave(){
		getThisValue();
		String clothno=this.getValueString("CLOTH_NO");
//		System.out.println("aaaa======"+clothno);
		TParm result = null;
//		if(!OWNER.equals("")){
//			String sql="SELECT USER_ID, USER_NAME, COST_CENTER_CODE FROM SYS_OPERATOR  WHERE USER_ID = '"+OWNER+"'";
//			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
//			STATION_CODE=selParm.getValue("COST_CENTER_CODE", 0);
//		}
		result = update();
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		
		if(!rfid_write_flg){
			messageBox("�����ɹ�");
		}
		
		
//		onClear();
//		onQuery();
//		onTableSelectRow(clothno);
	}
	
	public void onTableSelectRow(String cloth){
		TParm parm = table.getParmValue();
//		System.out.println("CLOTH_NO==="+cloth);
		for(int i=0;i<parm.getCount();i++){
			if(parm.getValue("CLOTH_NO", i).equals(cloth)){
//				System.out.println("aaaaaa");
				
//				TParm parmW = table.getParmValue().getRow(i);
//				parmW.setData("WRITE_FLG", "T");
//				table.setRowParmValue(i, parmW);
				table.setSelectedRow(i);
				return;
			}
		}
	}
	
	
	
	
//	/**
//	 * ����
//	 */
//	public TParm save(){
//		String sql =
//			" INSERT INTO CTS_CLOTH " +
//			" (CLOTH_NO, INV_CODE, OWNER, DEPT_CODE, STATION_CODE, ACTIVE_FLG," +
//			" PAT_FLG, STATE, OPT_USER," +
//			" OPT_DATE, OPT_TERM" +
//			" )" +
//			" VALUES ('" + CLOTH_NO + "', '" + INV_CODE + "', '" + OWNER + "', '" + DEPT_CODE + "', '" + STATION_CODE + "', '" + ACTIVE_FLG + "'," +
//			" '" + PAT_FLG + "', '" + STATE + "', '" + optId + "'," +
//			" SYSDATE, '" + optIp + "'" +
//			" )";
//		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//		return result;
//	}
	
	
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		
//		TTable table4 = this.getTTable("TABLE");
//		int rowCount4 = table.getRowCount();
//		Map m4=new HashMap();
//		for (int i = 0; i < rowCount4; i++) {
//			m4.put(i,new Color(255,255,0));
//		}
//		 table.setRowColorMap(m4); 
//        this.getTTable("TABLE").removeRowAll();
//		
        
        
		getThisValue();
//		System.out.println("INV_CODE=="+INV_CODE);
		StringBuilder sql = new StringBuilder();
		sql.append(
//				" SELECT CLOTH_NO, INV_CODE, OWNER, DEPT_CODE, STATION_CODE, ACTIVE_FLG," +
//				" PAT_FLG, STATE, OPT_USER, OPT_DATE, OPT_TERM" +
//				" FROM CTS_CLOTH" +
//				" WHERE PAT_FLG = '" + PAT_FLG + "' ");
				" SELECT   A.RFID CLOTH_NO, A.CTS_COST_CENTRE STATION_CODE, A.INV_CODE, B.INV_CHN_DESC," +
				" A.ACTIVE_FLG, A.PAT_FLG, A.OWNER, B.DESCRIPTION, B.COST_PRICE ,A.WRITE_FLG,A.OWNER_CODE,A.TURN_POINT" +
				" FROM INV_STOCKDD A, INV_BASE B" +
				" WHERE A.INV_CODE = B.INV_CODE " +
				" AND A.RFID IS NOT NULL " +
				" AND B.INV_KIND = '08'" );
		

		if(!"".equals(CLOTH_NO)){
			sql.append(" AND A.RFID = '"+CLOTH_NO+"' ");
		}
		
		
		if(!"".equals(INV_CODE)){
			sql.append(" AND B.INV_CODE = '"+INV_CODE+"' ");
		}
		
		if(!"".equals(OWNER)){
			sql.append(" AND A.OWNER = '"+OWNER+"' ");
		}
//		if(!"".equals(DEPT_CODE)){
//			sql.append(" AND DEPT_CODE = '"+DEPT_CODE+"' ");
//		}
//		if(!"".equals(STATION_CODE)){
//			sql.append(" AND A.CTS_COST_CENTRE = '"+STATION_CODE+"' ");
//		}
		if(!"".equals(WRITE_FLG)){
			sql.append(" AND WRITE_FLG = '"+WRITE_FLG+"' ");
		}
		if(WRITE_FLG.equals("Y")){
			sql.append(" ORDER BY  A.RFID DESC, A.INV_CODE, A.CTS_COST_CENTRE");
		}else{
			sql.append(" ORDER BY  A.RFID, A.INV_CODE, A.CTS_COST_CENTRE");
		}

//		System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if(result.getCount() <0){
			this.messageBox("û��Ҫ��ѯ������");
			return;
		}
		if(rfid_flg){
			rfid_flg=false;
//			RfidParm = new TParm();
			RfidParm.setData(result.getData());
//			RfidParm = result;
			
			
		}else{
			table.setParmValue(result);
		}
		// �������	
		addListener(this.getTTable("TABLE"));
		
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
	 * table������
	 */
	public void onClickTable(){
//		cloth_no.setEnabled(false);
//		inv_code.setEnabled(false);
		row = table.getSelectedRow();
		TParm tableParm = table.getParmValue();
		rowParm = tableParm.getRow(row);
		setValue("CLOTH_NO", rowParm.getValue("CLOTH_NO"));
		setValue("INV_CODE", rowParm.getValue("INV_CODE"));
		setValue("OWNER", rowParm.getValue("OWNER"));
		setValue("OWNER_CODE", rowParm.getValue("OWNER_CODE"));
		setValue("STATION_CODE", rowParm.getValue("STATION_CODE"));
		setValue("STATE", rowParm.getValue("STATE"));
//		setValue("TURN_POINT", rowParm.getValue("TURN_POINT"));
		if(rowParm.getValue("ACTIVE_FLG").equals("Y")){
			active_flg.setSelected(true);
		}else{
			active_flg.setSelected(false);
		}
		if(rowParm.getValue("PAT_FLG").equals("Y")){
			tRadioButton_0.setSelected(true);
			PAT_FLG = "Y";
		}
		if(rowParm.getValue("PAT_FLG").equals("N")){
			tRadioButton_1.setSelected(true);
			PAT_FLG = "N";
		}
		setValue("WRITE_FLG", rowParm.getValue("WRITE_FLG"));
	}
	/**
	 * ���
	 * 
	 */
	public void onClear(){
		clearData();
		table.removeRowAll();
		
	}
	
	public void clearData(){
		cloth_no.setEnabled(true);
//		inv_code.setEnabled(true);
		owner_code.setEnabled(true);
		clearValue("CLOTH_NO;WRITE_FLG;OWNER;DEPT_CODE;STATION_CODE;STATE;OWNER_CODE;TURN_POINT");
		active_flg.setSelected(true);
		row = -1;
		ACTIVE_FLG = "N";
//		INV_CODE="";
		rfid_table.removeRowAll();
		
		onNoPat();
		tRadioButton_1.setSelected(true);
//		onQuery();
	}
	
	
	/**
	 * ɾ��
	 */
	public void onDelete(){
//		if(row < 0){
//			messageBox("����ѡ��");
//			return;
//		}
//		getThisValue();
//		String sql = 
//			"DELETE CTS_CLOTH WHERE CLOTH_NO = '"+CLOTH_NO+"'";
//		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//		if(result.getErrCode()<0){
//			messageBox(result.getErrText());
//			return;
//		}
//		messageBox("�����ɹ�");
//		onClear();
//		onQuery(); 
		
		int i = messageBox("�Ƿ�ɾ��", "�Ƿ�ɾ����", this.YES_NO_OPTION);
		if(i == 1){
			return;
		}

		
		table.acceptText();
		int row = table.getSelectedRow();
		String rfid=table.getParmValue().getValue("CLOTH_NO", row);
		String sql =
			" UPDATE INV_STOCKDD" +
			" SET " +
			" OWNER = ''," +
			" OWNER_CODE = '',"+
			" CTS_COST_CENTRE = ''," +
			" WRITE_FLG = 'N'," +
			" PAT_FLG = 'N'," +
//			" STATE = '"+STATE+"'," +
			" OPT_USER = '"+optId+"'," +
			" OPT_DATE = SYSDATE," +
			" OPT_TERM = '"+optIp+"'" +
			" WHERE RFID = '"+rfid+"'";
//		System.out.println("update==="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		messageBox("�����ɹ�");
		onClear();
	}
	/**
	 * �޸�    
	 */
	public TParm update(){
		String sql =
			" UPDATE INV_STOCKDD" +
			" SET " +
			" OWNER = '"+OWNER+"'," +
			" CTS_COST_CENTRE = '"+STATION_CODE+"'," +
			" WRITE_FLG = 'Y'," +
			" PAT_FLG = '"+PAT_FLG+"'," +
			" OWNER_CODE = '"+OWNER_CODE+"',"+
			" TURN_POINT = '"+TURN_POINT+"',"+
//			" STATE = '"+STATE+"'," +
			" OPT_USER = '"+optId+"'," +
			" OPT_DATE = SYSDATE," +
			" OPT_TERM = '"+optIp+"'" +
			" WHERE RFID = '"+CLOTH_NO+"'";
//		System.out.println("update==="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	/**
	 * ȡ��ֵ
	 */
	public void getThisValue(){
		CLOTH_NO = getValueString("CLOTH_NO");
		INV_CODE = getValueString("INV_CODE");
		OWNER = getValueString("OWNER");
		OWNER_CODE = getValueString("OWNER_CODE");
		TURN_POINT = getValueString("TURN_POINT");
		STATION_CODE = getValueString("STATION_CODE");
//		STATE = getValueString("STATE");
		if(active_flg.isSelected()){
			ACTIVE_FLG = "Y";
		}else{
			ACTIVE_FLG = "N";
		}
		WRITE_FLG = getValueString("WRITE_FLG");
		
	}
	
	public void onSpreadout(){
		this.openWindow("%ROOT%\\config\\cts\\CTSUnfold.x");
	}
	
	
	
	public void onPat(){
//		onClear();
		PAT_FLG = "Y";
//		this.callFunction("UI|DEPT_CODE|setEnabled", true);
//		this.callFunction("UI|STATION_CODE|setEnabled", true);
//		this.callFunction("UI|OWNER|setEnabled", false);
	}
	
	public void onNoPat(){
//		onClear();
		PAT_FLG = "N";
//		this.callFunction("UI|DEPT_CODE|setEnabled", false);
//		this.callFunction("UI|STATION_CODE|setEnabled", false);
//		this.callFunction("UI|OWNER|setEnabled", true);
	}
	
	public void onOperator(){
//		String sql =
//			" SELECT DEPT_CODE" +
//			" FROM SYS_OPERATOR_DEPT" +
//			" WHERE USER_ID = '" + getValue("OWNER") + "' AND MAIN_FLG = 'Y'";
//		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		if(result.getCount()>0){
//			setValue("DEPT_CODE", result.getValue("DEPT_CODE", 0));
//		}
//		sql =
//			" SELECT STATION_CLINIC_CODE" +
//			" FROM SYS_OPERATOR_STATION" +
//			" WHERE USER_ID = '" + getValue("OWNER") + "' AND MAIN_FLG = 'Y'";
//		result = new TParm(TJDODBTool.getInstance().select(sql));
//		if(result.getCount()>0){
//			setValue("STATION_CODE", result.getValue("STATION_CLINIC_CODE", 0));
//		}
	}
	
	/**
	 * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	 */
	public void creat() {
		try {
			RFIDMidUtils.connect();
			this.callFunction("UI|OPEN|setEnabled", false);
			this.callFunction("UI|DISCONN|setEnabled", true);
		} catch (Exception e) {
			e.printStackTrace();
			this.callFunction("UI|OPEN|setEnabled", true);
			this.callFunction("UI|DISCONN|setEnabled", false);
			messageBox("ʧ��");
		}
		
	}

	public void disConn() {
		try {
			
			RFIDMidUtils.disconnect();
			this.callFunction("UI|DISCONN|setEnabled", false);
			this.callFunction("UI|OPEN|setEnabled", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.callFunction("UI|DISCONN|setEnabled", true);
			this.callFunction("UI|OPEN|setEnabled", false);
			messageBox("ʧ��");
		}
	}
	
	public void read(){
		read("1");
	}
	
	public void read(String flg) {
		rfid_table.removeRowAll();
		try {
//			RFIDMidUtils.connect();
//			System.out.println("ssssss==="+RFIDMidUtils.readTags());
//			System.out.println("sssdddddsss==="+RFIDMidUtils.readTags().length);
			String[] s = RFIDMidUtils.readTags();
			TParm parm = new TParm();
			for (String a : s) {
//				System.out.println("sssssaaa===="+a);
				String rfid = Uitltool.decode(a);
				parm.addData("CODE", a);
				parm.addData("RFID", rfid);
				//�ж��Ƿ�ǩ�Ƿ�д��
				String sql="SELECT RFID FROM INV_STOCKDD WHERE WRITE_FLG='Y' AND RFID='"+rfid+"'";
				TParm write = new TParm(TJDODBTool.getInstance().select(sql));
				if(write.getCount()>0){
					int i = messageBox("�Ƿ���д��ǩ", "��ǩ�Ѿ�д�����Ƿ���д��", this.YES_NO_OPTION);
					if(i == 1){
						return;
					}
				}
				if(flg.length()>0){
					//�ж��Ƿ�д�ɹ�
					if(rfid_write_flg){
						rfid_write_flg=false;
						if(!rfid.equals(getValue("CLOTH_NO"))){
							this.messageBox("û��д�ɹ���");
							
							return;
						}
						
						
					}
					
					clearData();
					rfid_flg=true;
					setValue("CLOTH_NO", rfid);
					clearValue(INV_CODE);
					onQuery();
					parm.addData("OWNER", RfidParm.getValue("OWNER", 0));
					parm.addData("OWNER_CODE", RfidParm.getValue("OWNER_CODE", 0));
					parm.addData("STATION_CODE", RfidParm.getValue("STATION_CODE", 0));
					parm.addData("PAT_FLG", RfidParm.getValue("PAT_FLG", 0));
//					TParm tableParm = table.getParmValue();
					this.setValue("INV_CODE", RfidParm.getValue("INV_CODE", 0));
					this.setValue("TURN_POINT", RfidParm.getValue("TURN_POINT", 0));
//					INV_CODE=RfidParm.getValue("INV_CODE", 0);
						
//					
					
				}
			}
			
//			System.out.println("parm1111====="+parm);
			rfid_table.setParmValue(parm);
			rfid_table.setSelectedRow(0);
//			RFIDMidUtils.disconnect();
			this.callFunction("UI|WRITE|setEnabled", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void write(){
		this.read("");
		onWrite();
		onSave();
		this.read("1");
		
	}
	
	public void onWrite(){
		try {
			int row = rfid_table.getSelectedRow();
			String oldValue = rfid_table.getParmValue().getValue("CODE", row);
			String data = getValueString("CLOTH_NO");
//			RFIDMidUtils.connect();
			RFIDMidUtils.writeTag(oldValue, Uitltool.encode(data));
//			RFIDMidUtils.disconnect();
			rfid_table.removeRowAll();
			rfid_write_flg=true;
//			messageBox("�ɹ�");
		} catch (Exception e) { // TODO Auto-generated catch block
			//e.printStackTrace();
//			System.out.println("error==="+e.getMessage());
		}
	}
	
	/**
	 * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	 */
	
	
	 public boolean onClosing(){
		 disConn();
		 return true;
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
