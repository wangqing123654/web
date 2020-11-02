package com.javahis.ui.odi;

import java.util.List;
import java.util.Map;

import jdo.adm.ADMInpTool;
import jdo.opd.TotQtyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ����ҩ��ҽ��Խ�����
 * </p>
 * 
 * <p>
 * Description: ����ҩ��ҽ��Խ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2014��2��
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author Miracle
 * @version JavaHis 1.0
 */
public class ODIOverRideCheckControl extends TControl {
	private String caseNo = "";
	private String mrNo = "";
	private String ipdNo = "";
	private String bedNo = "";
	private String patname = "";
	private String admDate = "";
	private String stationNo = "";
	private String deptCode = "";
	private String vsDcCode = "";
	private TTable table;
	private String orgCode = "";

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE1");
		// //���ܴ���Ĳ���
		TParm outsideParm = (TParm) this.getParameter();
		mrNo = outsideParm.getValue("INW", "MR_NO");// ��������
		caseNo = outsideParm.getValue("INW", "CASE_NO");// �����
		this.onQuery();
		ipdNo = outsideParm.getValue("INW", "IPD_NO");// סԺ��
		bedNo = outsideParm.getValue("INW", "BED_NO");// ����
		patname = outsideParm.getValue("INW", "PAT_NAME");// ����
		admDate = outsideParm.getValue("INW", "ADM_DATE");// ��Ժ����       yanmm
		stationNo = outsideParm.getValue("INW", "STATION_CODE");// ����
		deptCode = outsideParm.getValue("INW", "DEPT_CODE");// ����
		vsDcCode = outsideParm.getValue("INW", "VS_DR_CODE");// ����ҽ��
		orgCode = (((TParm) this.getParameter()).getData("ODI", "ORG_CODE")
				.toString());
		addEventListener("TABLE1" + "->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableValue");
		// ����ҽ��CHECK_BOX�����¼�
		getTable("TABLE1").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onCheckBoxValue");

	}

	
	/**
	 * �޸��¼�
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onChangeTableValue(Object obj) {
		TTableNode tNode = (TTableNode) obj;
		if (tNode == null)
			return true;
		TParm tableParm = table.getParmValue();
		// �жϵ�ǰ���Ƿ���ҽ��
		int row = tNode.getRow();
		// �õ�table�ϵ�parmmap������
		String columnName = tNode.getTable().getDataStoreColumnName(
				tNode.getColumn());
		if (columnName.equals("CHOOSE_FLG")) {
			tableParm.setData("CHOOSE_FLG", row,tableParm.getBoolean("CHOOSE_FLG", row) ? "N" : "Y");
			String flg = tableParm.getValue(columnName, row);
			// �����ҽ�������Ա��� yanmm
			if (flg.equals("Y")) {
				if (tableParm.getValue("CHECK_FLG", row).equals("Y")) {
					tableParm.setData("CHOOSE_FLG", row, "N");
					this.messageBox("�����ҽ������ѡ��!");
					table.setParmValue(tableParm);
					return true;
				}
			}
			int rowCount = tableParm.getCount("ORDER_DESC");
			String linkNo = tableParm.getValue("LINK_NO", row);
			for (int i = 0; i < rowCount; i++) {

				if (!"".equals(linkNo) && !linkNo.equals(null)
						&& linkNo.equals(tableParm.getValue("LINK_NO", i))
						&& i != row) {
					tableParm
							.setData("CHOOSE_FLG", i, tableParm.getBoolean(
									"CHOOSE_FLG", row) ? "N" : "Y");
				}
			}
//			tableParm.setData("CHOOSE_FLG", row,
//					tableParm.getBoolean("CHOOSE_FLG", row) ? "N" : "Y");
			table.setParmValue(tableParm);
			int newRow = row;
			if (flg.equals("Y")) {
				if (!tableParm.getValue("LCS_CLASS_CODE", row).equals(null)
						&& !"".equals(tableParm.getValue("LCS_CLASS_CODE", row))) {// ����ҩ��ʱ
					newRow = row;
				} else {
					// �ǿ���ҩ��ʱȡ�����ҩ���ͣ��ʱ��
					// ȡ��link_no�ҳ���Ӧ������
					String fLinkNo = tableParm.getValue("LINK_NO", row);
					for (int m = 0; m < tableParm.getCount(); m++) {
						String kLinkNo = tableParm.getValue("LINK_NO", m);
						if (fLinkNo.equals(kLinkNo)) {
							String lcsClassCode = tableParm.getValue(
									"LCS_CLASS_CODE", m);
							if (!"".equals(lcsClassCode)
									&& !lcsClassCode.equals(null)) {// �ҳ����ȡ��ͣ��ʱ��
								newRow = m;
							}
						}
					}
				}
				TParm lcsParm = new TParm(
						getDBTool()
								.select("SELECT LCS_CLASS_CODE FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
										+ Operator.getID()
										+ "' AND SYSDATE BETWEEN EFF_LCS_DATE AND END_LCS_DATE"));
				String lcsClassCode = table.getParmValue().getValue(
						"LCS_CLASS_CODE", newRow);
				if (!OrderUtil.getInstance().checkLcsClassCode(lcsParm,
						Operator.getID(), "" + lcsClassCode)) {
					lcsParm.setErrCode(-1);
					// ��û�д�ҽ����֤�գ�
					this.messageBox("E0166");
					for (int i = 0; i < rowCount; i++) {
						if (linkNo.equals(tableParm.getValue("LINK_NO", i))
								&& i != row) {
							tableParm.setData("CHOOSE_FLG", i, tableParm
									.getBoolean("CHOOSE_FLG", row) ? "N" : "Y");
						}
					}
					tableParm.setData("CHOOSE_FLG", row, "N");
					table.setParmValue(tableParm);
					return true;
				}
				if (tableParm.getValue("RX_KIND", row).equals("ST")) {// ��ʱ
					tableParm.setData("DC_DATE", row, "");// ͣ��ʱ��
					tableParm.setData("DC_DR_CODE", row, "");// ͣ��ҽ��
					tableParm.setData("ANTIBIOTIC_WAY", row, "01");// ������ʶ
					tableParm.setData("DC_DEPT_CODE", row,"");// ͣ�ÿ���
					tableParm.setData("TAKE_DAYS", row, 1);

				} else {
					tableParm.setData("DC_DATE", row, StringTool.rollDate(
							SystemTool.getInstance().getDate(),
							tableParm.getInt("ANTI_TAKE_DAYS", newRow)));// ͣ��ʱ��
					tableParm.setData("DC_DR_CODE", row, Operator.getID());// ͣ��ҽ��
					tableParm.setData("DC_DEPT_CODE", row,deptCode);// ͣ�ÿ���
					tableParm.setData("ANTIBIOTIC_WAY", row, "02");// ������ʶ
				}

				tableParm.setData("ORDER_DR_CODE", row, Operator.getID());
				tableParm.setData("CHOOSE_FLG", newRow, "Y");
				for (int i = 0; i < rowCount; i++) {
					if (linkNo.length()>0 &&  linkNo.equals(tableParm.getValue("LINK_NO", i))) {
						setValueTableParm(tableParm, i, columnName, tNode);
						if (tableParm.getValue("RX_KIND", i).equals("ST")) {// ��ʱ
							tableParm.setData("DC_DATE", i, "");// ͣ��ʱ��
							tableParm.setData("DC_DR_CODE", i, "");// ͣ��ҽ��
							tableParm.setData("DC_DEPT_CODE", i,"");// ͣ�ÿ���
							tableParm.setData("ANTIBIOTIC_WAY", i, "01");// ������ʶ
							tableParm.setData("TAKE_DAYS", row, 1);
						} else {
							tableParm.setData("DC_DATE", i, StringTool
									.rollDate(SystemTool.getInstance()
											.getDate(), tableParm.getInt(
											"ANTI_TAKE_DAYS", newRow)));// ͣ��ʱ��
							tableParm.setData("DC_DR_CODE", i, Operator.getID());// ͣ��ҽ��
							tableParm.setData("DC_DEPT_CODE", i,deptCode);// ͣ�ÿ���
							tableParm.setData("ANTIBIOTIC_WAY", i, "02");// ������ʶ
						}
						tableParm.setData("ORDER_DR_CODE", i, Operator.getID());
						tableParm.setData("CHOOSE_FLG", i, "Y");
					}
				}
			} else {
				tableParm.setData("DC_DATE", row, "");// ͣ��ʱ��
				tableParm.setData("DC_DR_CODE", row, "");// ͣ��ҽ��
				tableParm.setData("DC_DEPT_CODE", row,"");// ͣ�ÿ���
				tableParm.setData("ANTIBIOTIC_WAY", row, "");// ������ʶ
				tableParm.setData("ORDER_DR_CODE", row, vsDcCode);
				tableParm.setData("CHOOSE_FLG", newRow, "N");
				for (int i = 0; i < rowCount; i++) {
					if (linkNo.length()>0 && linkNo.equals(tableParm.getValue("LINK_NO", i))) {
						tableParm.setData("DC_DATE", i, "");// ͣ��ʱ��
						tableParm.setData("DC_DR_CODE", i, "");// ͣ��ҽ��
						tableParm.setData("DC_DEPT_CODE", i,"");// ͣ�ÿ���
						tableParm.setData("ANTIBIOTIC_WAY", i, "");// ������ʶ
						tableParm.setData("ORDER_DR_CODE", i, vsDcCode);
						tableParm.setData("CHOOSE_FLG", i, "N");
					}
				}
			}
			setValueTableParm(tableParm, row, columnName, tNode);
			table.setParmValue(tableParm);
		}
		if (columnName.equals("MEDI_QTY") || columnName.equals("FREQ_CODE")) {
			if (setValueTableParm(tableParm, row, columnName, tNode))
				return true;
			table.setParmValue(tableParm);
		}
		// this.messageBox("===============888888888===================");
		// EXEC_DEPT_CODE
		return false;
	}
	
	
	private boolean setValueTableParm(TParm tableParm,int row,String columnName,TTableNode tNode){
		if (tableParm.getValue("ORDER_CODE", row).length() == 0) {
			// ��¼��ҽ����
			// ============xueyf modify 20120217 start
			if (Float.valueOf(tableParm.getValue("MEDI_QTY", row)) > 0) {
				this.messageBox("E0157");
			}
			// ============xueyf modify 20120217 stop
			return true;
		}
		if (tableParm.getValue("CAT1_TYPE", row).equals("PHA")) {
			// ����������
			if (columnName.equals("MEDI_QTY")) {
				tableParm.setData("MEDI_QTY", row, tNode.getValue());
			}
			if (columnName.equals("FREQ_CODE")) {
				// �ж�Ƶ���Ƿ��������ʱʹ��
				tableParm.setData("FREQ_CODE", row, tNode.getValue());
			}
			TParm action = this.getTempStartQty(tableParm.getRow(row));
			if (action.getErrCode() < 0) {
				// ����������
				if (action.getErrCode() == -2) {
					if (messageBox("��ʾ��Ϣ Tips",
							"����ҩƷ����������׼�Ƿ��մ���������? \n Qty Overproof",
							this.YES_NO_OPTION) != 0)
						return true;
				} else {//  ������δ�ش�
					this.messageBox(action.getErrText());
					return true;
				}
			}
			tableParm.setData("DISPENSE_QTY",row,action.getDouble("DISPENSE_QTY"));
			tableParm.setData("DOSAGE_UNIT",row,action.getValue("DOSAGE_UNIT"));
			tableParm.setData("DISPENSE_UNIT",row,action.getValue("DISPENSE_UNIT"));
			tableParm.setData("ACUMMEDI_QTY",row,action.getDouble("ACUMMEDI_QTY"));
			tableParm.setData("ACUMDSPN_QTY",row,action.getDouble("ACUMDSPN_QTY"));
			tableParm.setData("DOSAGE_QTY",row,action.getDouble("DOSAGE_QTY"));
			tableParm.setData("LASTDSPN_QTY",row,action.getDouble("LASTDSPN_QTY"));
			tableParm.setData("START_DTTM",row,action.getTimestamp("START_DTTM"));
			tableParm.setData("FRST_QTY",row,action.getDouble("ACUMMEDI_QTY"));
		}
		return false;
	}
	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		
		String selSql = "SELECT 'N' AS MODIFY_FLG,'N' AS CHOOSE_FLG,A.LINKMAIN_FLG,A.CHECK_FLG,A.LINK_NO,A.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,"
				+ "A.FREQ_CODE,A.ROUTE_CODE,'N' AS URGENT_FLG,'N' AS DISPENSE_FLG,"
				+ "'' AS DR_NOTE,SYSDATE AS EFF_DATEDAY,'' AS DC_DATE,'' AS NS_NOTE,"
				+ "B.INSPAY_TYPE,A.ORDER_DATE,C.DOSAGE_QTY AS DISPENSE_QTY,C.DOSAGE_UNIT,A.SPECIFICATION,"
				+ "C.DOSAGE_UNIT AS DISPENSE_UNIT,A.RX_KIND,'' DC_DEPT_CODE,A.INFLUTION_RATE,"
				+ "ANTI_MAX_DAYS AS ANTI_TAKE_DAYS,ANTI_MAX_DAYS AS TAKE_DAYS,A.PHA_SEQ," 
				+ "A.SEQ_NO,A.ORDER_CODE,B.LCS_CLASS_CODE,C.DOSAGE_QTY,'PHA' CAT1_TYPE,'' START_DTTM "
				+ " FROM PHA_ANTI A ,SYS_FEE B ,PHA_TRANSUNIT C " 
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDER_CODE=C.ORDER_CODE "
				+ "AND A.CASE_NO = '"
				+ caseNo							//����ע��CHECK_FLG ====yanminming
				+ "' AND A.OVERRIDE_FLG = 'Y' ORDER BY LINK_NO DESC,LINKMAIN_FLG DESC ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(selSql));
		if (parm.getErrCode()<0) {
			this.messageBox(parm.getErrText());
			return;
		}
		if (parm.getCount()<=0) {
			this.messageBox("δ�������");
			table.setParmValue(new TParm());
			return;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			// ȡ������ҩ���ʹ������
			parm.setData("ANTIBIOTIC_WAY", i, "");// ������ʶ
			parm.setData("EXEC_DEPT_CODE", i, orgCode);//ִ�п���
			parm.setData("ORDER_DR_CODE", i, Operator.getID());// ����ҽ��
			parm.setData("NS_CHECK_CODE", i, "");//ȷ�ϻ�ʿ
			parm.setData("NS_CHECK_DATE", i, "");// ȷ��ʱ��
			parm.setData("DC_RSN_CODE", i, "");// ͣ��ԭ��
			parm.setData("DC_NS_CHECK_CODE", i, "");// ͣ��ȷ�ϻ�ʿ
			parm.setData("DC_NS_CHECK_DATE", i, "");// ͣ��ȷ��ʱ��
			parm.setData("ORDER_STATE", i, "N");// ҽ��״̬��N��
			parm.setData("ACUMMEDI_QTY", i, 0);// �ۻ�����
			parm.setData("ACUMDSPN_QTY", i, 0);// �ۻ���ҩ
			parm.setData("DC_DATE", i, "");// ͣ��ʱ��
			parm.setData("DC_DR_CODE", i, "");// ͣ��ҽ��
			parm.setData("ANTIBIOTIC_WAY", i, "");// ������ʶ
		}
		// Ϊ���ֵ
		table.setParmValue(parm);
	}

	/**
	 * �õ�TABLE����
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		// ȡ�ñ���ֵ
		TTable table1 = (TTable) getComponent("TABLE1");
		table1.acceptText();
		TParm tableParm = table1.getParmValue();
		TParm saveParm = new TParm();
		// ���ô洢�����õ�ORDER_NO
		String orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
				"ORDER_NO", "ORDER_NO");
		int orderSeq = 0;
		//=====�����ŵ�����   yanjing 20140619
//		String linkNo = "9999";
//		String linkNoSql = "SELECT LINK_NO FROM ODI_ORDER "
//			+ "WHERE CASE_NO = '" +caseNo + "' AND RX_KIND = 'UD' AND LINK_NO IS NOT NULL "
//			+ " ORDER BY LINK_NO DESC  ";
////		System.out.println("action sql is :"+linkNoSql);
//	    TParm linkNoResult = new TParm(TJDODBTool.getInstance().select(linkNoSql));
//	    if (linkNoResult.getErrCode() < 0) {
//		    return ;
//	    }
//	    if(linkNoResult.getCount()<=0){
//	    	linkNo = "0";
//	    	
//	    }else{
////	    	 linkNo =  String.valueOf(Integer.parseInt(linkNoResult.getValue("LINK_NO",0))+1);
//	    	linkNo = linkNoResult.getValue("LINK_NO",0);
//	    }
	  
		String linkNoSql = "SELECT MAX(LINK_NO) LINK_NO FROM ODI_ORDER "
				+ "WHERE CASE_NO = '" +caseNo + "' AND LINK_NO IS NOT NULL ";
			
//			System.out.println("action sql is :"+linkNoSql);
			
	    TParm odiLinkNoResult = new TParm(TJDODBTool.getInstance().select(linkNoSql));
	    if (odiLinkNoResult.getErrCode() < 0) {
	    	this.messageBox("����ʧ��");
			return;
	    }
	    String linkNo ="-1";
	    boolean flg = false;
	    if(odiLinkNoResult.getCount()>0 && null!=odiLinkNoResult.getValue("LINK_NO", 0)
	    		&& !"".equals(odiLinkNoResult.getValue("LINK_NO", 0))){
	    	linkNo =odiLinkNoResult.getValue("LINK_NO", 0);
	    	linkNoSql = "SELECT MAX(LINK_NO) LINK_NO FROM PHA_ANTI "
					+ "WHERE CASE_NO = '" +caseNo + "' AND LINK_NO IS NOT NULL ";
	    	odiLinkNoResult = new TParm(TJDODBTool.getInstance().select(linkNoSql));
		    if (odiLinkNoResult.getErrCode() < 0) {
		    	this.messageBox("����ʧ��");
				return;
		    }
		    
	    	if(null!=odiLinkNoResult.getValue("LINK_NO", 0)
		    		&& odiLinkNoResult.getValue("LINK_NO", 0).length()>0){
				if(Integer.parseInt(linkNo)>=odiLinkNoResult.getInt("LINK_NO", 0)){
					flg = true;
					linkNo = String.valueOf(Integer.parseInt(linkNo)+1);
				}
			}
	    }
	    
	    if(flg){
	    	 //=====�����ŵ�����   yanjing 20140619  END
		    for(int m = 0;m < tableParm.getCount();m++){//������Ĳ���
		    	String rLinkNo = tableParm.getValue("LINK_NO", m);
		    	if(null==rLinkNo || "".equals(rLinkNo) || !tableParm.getValue("CHOOSE_FLG", m).equals("Y")){
					continue;
					
				}
		    	if(tableParm.getValue("CHOOSE_FLG", m).equals("Y")&&
		    			tableParm.getValue("LINKMAIN_FLG", m).equals("Y")){//�����������޸��估��ϸ���������
					//=====ѭ�������ҳ���������ͬ��ҽ�������޸�������
					for(int j = 0;j<tableParm.getCount();j++){
						String nLinkNo = tableParm.getValue("LINK_NO", j);
						if(rLinkNo.equals(nLinkNo)){//�޸�������
							tableParm.setData("LINK_NO", j,linkNo);
						}
					}
					tableParm.setData("LINK_NO", m,linkNo);
				}
		    	linkNo = String.valueOf(Integer.parseInt(linkNo)+1);
		    }
	    }
	   
	    
	    
		for (int i = 0; i < tableParm.getCount(); i++) {
			// ȡ���Ƿ�ѡ��״̬
			String isSave = tableParm.getValue("CHOOSE_FLG", i);
			if (isSave.equals("Y")) {
				
				String bedSql = "SELECT BED_NO FROM SYS_BED WHERE BED_NO_DESC = '"
						+ bedNo + "'";
				TParm bedNoParm = new TParm(TJDODBTool.getInstance().select(
						bedSql));
				String catSql = "SELECT A.CAT1_TYPE,A.ORDER_CAT1_CODE,B.ANTIBIOTIC_CODE " +
						"FROM SYS_FEE A ,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.ORDER_CODE = '"
						+ tableParm.getValue("ORDER_CODE", i) + "'";
				TParm catTypeParm = new TParm(TJDODBTool.getInstance().select(
						catSql));
				flg = true;
				// ȡ�����е�����
				saveParm = tableParm.getRow(i);
				saveParm.setData("REGION_CODE", Operator.getRegion());
				saveParm.setData("ORDER_NO", orderNo);
				saveParm.setData("ANTIBIOTIC_CODE", catTypeParm.getValue(
						"ANTIBIOTIC_CODE", 0));
				saveParm.setData("ORDER_SEQ", ++orderSeq);
				saveParm.setData("CASE_NO", caseNo);//
				saveParm.setData("MR_NO", mrNo);//
				saveParm.setData("IPD_NO", ipdNo);// סԺ��
				saveParm.setData("BED_NO", bedNoParm.getValue("BED_NO", 0));// ����
				saveParm.setData("STATION_CODE", stationNo);// ����
				//saveParm.setData("RX_KIND", "UD");//
				saveParm.setData("DEPT_CODE", deptCode);//
				saveParm.setData("VS_DR_CODE", vsDcCode);//
				saveParm.setData("ORDER_DEPT_CODE", deptCode);//
				saveParm.setData("INDV_FLG", "Y");//
				saveParm.setData("HIDE_FLG", "N");//
				saveParm.setData("ORDER_CAT1_CODE", catTypeParm.getValue(
						"ORDER_CAT1_CODE", 0));//
				saveParm.setData("CAT1_TYPE", catTypeParm.getValue("CAT1_TYPE",
						0));//
				saveParm.setData("ORDERSET_GROUP_NO", 0);//
				saveParm.setData("EXEC_DEPT_CODE", orgCode);// ִ�п���
				saveParm.setData("SETMAIN_FLG", "N");//
				saveParm.setData("CHECK_FLG", "Y");//
				saveParm.setData("USE_FLG", "Y");//
				saveParm.setData("OPT_DATE", SystemTool.getInstance().getDate());//
				saveParm.setData("OPT_TERM", Operator.getIP());//
				saveParm.setData("OPT_USER", Operator.getID());
				//saveParm.setData("FRST_QTY", 1);
				if (null == saveParm.getValue("FRST_QTY") || saveParm.getValue("FRST_QTY").equals("null") ||saveParm.getValue("FRST_QTY").length()<=0) {
					saveParm.setData("FRST_QTY", 1);
				}
				// ��odi_order����д���ݣ��޸�pha_anti���е�״̬λaction��
				//System.out.println("��� ���saveParm saveParm  is����"+saveParm);
				TParm result = TIOM_AppServer.executeAction(
						"action.odi.ODIAction", "onSaveToOdi", saveParm);
				if (result.getErrCode() < 0) {
					this.messageBox("����ʧ��");
					return;
				}
			}
		}
		if (!flg) {
			this.messageBox("��ѡ��Ҫ��������ݡ�");
			return;
		} else {
			this.messageBox("����ɹ���");
			onQuery() ;
			this.messageBox("�뼰ʱ���俹��ҩ����ﱨ��!"); //yanmm
			onConsApply();
			return;
		}
		

	}
	
	
	/**
	 * �����������yanmm
	 */
	public void onConsApply() {
			String flg = "ODI";
			TParm parm = new TParm();
			parm.setData("INW", "CASE_NO", caseNo);
			parm.setData("INW", "PAT_NAME", patname);
			parm.setData("INW", "ADM_DATE", admDate);
			parm.setData("INW", "MR_NO", mrNo);
			parm.setData("INW", "ODI_FLG", flg);
			parm.setData("INW", "IPD_NO", ipdNo);
			parm.setData("INW", "ADM_TYPE", "I");
			parm.setData("INW", "KIND_CODE", "01");
			parm = (TParm) openDialog(
					"%ROOT%\\config\\inp\\INPConsApplication.x", parm);
		
	}
	

	/**
	 * checkBoxֵ�ı��¼�
	 * 
	 * @param obj
	 */
	public boolean onCheckBoxValue(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		return true;
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.onQuery();
	}

	/**
	 * ������ʱ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getTempStartQty(TParm parm) {

		TParm result = new TParm();
		// ������
		parm.setData("ACUMDSPN_QTY", 0);
		// �ۼƿ�ҩ��
		parm.setData("ACUMMEDI_QTY", 0);
		String effDate = StringTool.getString(parm.getTimestamp("ORDER_DATE"),
				"yyyyMMddHHmmss");
		// �õ���ҩ���պ�����
		// List dispenseDttm =
		// TotQtyTool.getInstance().getDispenseDttmArrange(effDate);
		List dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(
				parm.getTimestamp("ORDER_DATE"));
		// System.out.println("===dispenseDttm==="+dispenseDttm);
		if (StringUtil.isNullList(dispenseDttm)) {
			result.setErrCode(-1);
			// �����д���
			result.setErrText("E0024");
			return result;
		}
		// this.messageBox("��ҩʱ������:"+dispenseDttm.get(0)+"��ҩʱ������:"+dispenseDttm.get(1));
		// ����������
		// this.messageBox_(parm);
		TParm selLevelParm = new TParm();
		selLevelParm.setData("CASE_NO", this.caseNo);
		// System.out.println(""+this.caseNo);
		// =============pangben modify 20110512 start ��Ӳ���
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			selLevelParm.setData("REGION_CODE", Operator.getRegion());
		// =============pangben modify 20110512 stop

		// System.out.println("==selLevelParm=="+selLevelParm);
		TParm selLevel = ADMInpTool.getInstance().selectall(selLevelParm);
		// System.out.println("selLevel==="+selLevel+"====="+selLevel.getValue("SERVICE_LEVEL",
		// 0));
		String level = selLevel.getValue("SERVICE_LEVEL", 0);
		String dcDate="";
		if (null!=parm.getValue("DC_DATE") && parm.getValue("DC_DATE").length()>0) {
			dcDate =StringTool.getString(parm.getTimestamp("DC_DATE"), "yyyyMMddHHmm");
		}
		List startQty = TotQtyTool.getInstance().getOdiStQty(effDate,
				dcDate, dispenseDttm.get(0).toString(),
				dispenseDttm.get(1).toString(), parm, level);
		// this.messageBox_(startQty);
		// System.out.println(""+startQty);
		// �ײ�ʱ�� START_DTTM
		List startDate = (List) startQty.get(0);
		// System.out.println("======startDate====="+startDate);
		// ������Ҫ����//order���LASTDSPN_QTY ORDER_LASTDSPN_QTY
		// order���ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
		// order���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
		// M���dispenseQty M_DISPENSE_QTY
		// M���dispenseUnit M_DISPENSE_UNIT
		// M���dosageQty M_DOSAGE_QTY
		// M���dosageUnit M_DOSAGE_UNIT
		// D���MediQty D_MEDI_QTY
		// D���MediUnit D_MEDI_UNIT
		// D���dosageQty D_DOSAGE_QTY
		// D���dosageUnit D_DOSAGE_UNIT
		Map otherData = (Map) startQty.get(1);
		// System.out.println("===otherData==="+otherData);
		if (StringUtil.isNullList(startDate)
				&& (otherData == null || otherData.isEmpty())) {
			result.setErrCode(-1);
			result.setErrText("E0024");
			return result;
		}
		//TParm mediQTY = getMediQty(parm.getValue("ORDER_CODE"));
		// �ײ�ʱ���
		result.setData("START_DTTM_LIST", startDate);
		// �ײ�ʱ��
		// this.messageBox_(startDate.get(0).toString()+":"+startDate.get(0).getClass());
		if (null== startDate  || startDate.size() ==0 || startDate.get(0).toString().length()<=0) {
			result.setData("START_DTTM",  SystemTool.getInstance().getDate());
		}else{
			result.setData("START_DTTM", StringTool.getTimestamp(startDate.get(0)
					.toString(), "yyyyMMddHHmm"));
		}

		result.setData("FRST_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
		// �����ҩ��
		result.setData("LASTDSPN_QTY", otherData.get("ORDER_LASTDSPN_QTY"));
		// ������
		result.setData("ACUMDSPN_QTY", otherData.get("ORDER_ACUMDSPN_QTY"));
		// �ۼƿ�ҩ��
		result.setData("ACUMMEDI_QTY", otherData.get("ORDER_ACUMMEDI_QTY"));
		// ��ҩ���� / ʵ����ҩ��������л���Ƭ��
		result.setData("DISPENSE_QTY", otherData.get("M_DISPENSE_QTY"));
		// ������λ
		result.setData("DISPENSE_UNIT", otherData.get("M_DISPENSE_UNIT"));
		if (parm.getValue("RX_KIND").equals("ST")) {

			// ��ҩ����
			result.setData("DOSAGE_QTY", otherData.get("D_DOSAGE_QTY"));
			// ��ҩ��λ�����䵥λ
			result.setData("DOSAGE_UNIT", otherData.get("D_DOSAGE_UNIT"));

		}else if(parm.getValue("RX_KIND").equals("UD")){
			// ��ҩ������ʵ�ʿۿ�����
			result.setData("DOSAGE_QTY", otherData.get("M_DOSAGE_QTY"));
			// ��ҩ��λ�����䵥λ
			result.setData("DOSAGE_UNIT", otherData.get("D_DOSAGE_UNIT"));
		}
		// ����ҩ�Ƿ���
		if (!OrderUtil.getInstance().checkKssPhaQty(parm)) { // shibl 20130123
			// modify ������δ�ش�
			result.setErrCode(-2);
			return result;
		}
		return result;
	}
	/**
	 * ��õ�ǰҩƷ��Ĭ������ ==============pangben modify 20110609
	 *
	 * @return TParm
	 */
	public TParm getMediQty(String orderCode) {
		TParm mediQTY = null;
		String mediQtySQL = "SELECT MEDI_QTY,MEDI_UNIT,DOSAGE_UNIT,FREQ_CODE,ROUTE_CODE FROM PHA_BASE WHERE ORDER_CODE='"
				+ orderCode + "'";
		mediQTY = new TParm(TJDODBTool.getInstance().select(mediQtySQL));
		return mediQTY;
	}
}
