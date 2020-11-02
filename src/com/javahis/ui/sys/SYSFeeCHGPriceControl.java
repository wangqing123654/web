package com.javahis.ui.sys;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import jdo.sys.Operator;
import jdo.sys.SystemTool;


import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TLabel;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * @author lix
 * 
 */
public class SYSFeeCHGPriceControl extends TControl {
	TButton btnChgPrice;
	TLabel  labTip;
	String  type ;

	/**
	 * ��ʼ��
	 */
	public void onInit() { // ��ʼ������
		super.onInit();
		btnChgPrice = (TButton) this.getComponent("ACTIVE_Y");
		labTip = (TLabel) this.getComponent("tLabel_0");
		type = "N" ;

	}
	/**
	 * ͣ��/������
	 */
	public void onStopSysFee(){
		type ="START_STOP" ; 
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(null);
		TParm parm = new TParm();
		if (option == JFileChooser.APPROVE_OPTION) {//��ȡѡ�е��ļ�����
			File file = fileChooser.getSelectedFile();
			Workbook wb;
			try {
				wb = Workbook.getWorkbook(file);

				Sheet st = wb.getSheet(0);
				int row = st.getRows();
				int column = st.getColumns();
				labTip.setVisible(true);
				String id = "";
				for (int j = 0; j < column; j++) {

					for (int i = 1; i < row; i++) {
						Cell cell = st.getCell(j, i);
						// ==================  У�����������
						if(!check(cell.getContents().trim(), i,j)){
							labTip.setVisible(false);
							return   ;
						}
							
						switch (j) {
						case 0:
							id = cell.getContents().trim();
							if(id.length()<0){
								return ;
							}
							parm.addData("ORDER_CODE", id);
							break;
						case 1:
							id = cell.getContents().trim();
							parm.addData("START_DATE", id);
							break;
						case 4:
							id = cell.getContents().trim();
							parm.addData("ACTIVE_FLG", id);
							break;
						}
					}
					parm.setCount(row - 1);
				}
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// System.out.println("-----------excel�ļ���¼-------------"+parm);
			System.out.println("-----------ͣ�ü�¼����-------------"
					+ (parm.getCount()));
		TParm checkParm =	onRepeat(parm) ;
		if(checkParm.getErrCode()<0){
			this.messageBox("�ظ�ҽ���룺"+checkParm.getValue("ORDER_CODE")) ;
			labTip.setVisible(false);
			return ;
		}
			boolean flg=true ; 
			String sysfeesql ;
			String tmpSQL;
			String selSQL;
			String tmpInsSQL ;
			String now =SystemTool.getInstance().getDate().toString().substring(0,10).replace("-", "")+
            SystemTool.getInstance().getDate().toString().substring(11,19).replace(":", "");
			ArrayList<String>  list  ;
			// 1.����parmÿһ�м�¼
			for (int i = 0; i < parm.getCount(); i++) {
				//===========  ͣ��
				tmpSQL = "" ;
				list =  new ArrayList<String>() ;
				tmpSQL = "UPDATE SYS_FEE_HISTORY SET ACTIVE_FLG='N' ,END_DATE = '"+now+"'";
				tmpSQL += " WHERE ORDER_CODE='"
						+  parm.getValue("ORDER_CODE", i)+"' " +
								" AND ACTIVE_FLG ='Y' ";
				sysfeesql = "UPDATE SYS_FEE SET ACTIVE_FLG='N' ";
				sysfeesql += " WHERE ORDER_CODE='"
						+  parm.getValue("ORDER_CODE", i)+"' " +
								" AND  ACTIVE_FLG ='Y' ";
				TParm selectParm = new TParm(TJDODBTool.getInstance().select("SELECT *  FROM SYS_FEE_HISTORY " +
						" WHERE ORDER_CODE ='"+parm.getValue("ORDER_CODE", i)+"' " +
						" AND START_DATE = '"+parm.getValue("START_DATE", i)+"' ")) ;
				if(parm.getValue("ACTIVE_FLG", i).equals("Y") && selectParm.getCount()<=0){
					tmpSQL += " AND START_DATE = '"+parm.getValue("START_DATE", i)+"'"  ;
				}
				list.add(tmpSQL) ;
				if(parm.getValue("ACTIVE_FLG", i).equals("N")){
					list.add(sysfeesql) ;
				}
				 String[] allSql = (String[]) list.toArray(new String[] {});
				TParm uptParm = new TParm(this.getDBTool().update(allSql));
				if (uptParm.getErrCode() < 0) {
					System.out.println("----------��/ͣ��¼״̬����ORDER_CODE:"
							+ parm.getValue("ORDER_CODE", i));
					System.out.println("----------��/ͣ��¼״̬SQL:" + tmpSQL);
				}
				//===============����
				if(parm.getValue("ACTIVE_FLG", i).equals("Y")){
					selSQL = "SELECT *  FROM SYS_FEE_HISTORY WHERE ORDER_CODE ='"+parm.getValue("ORDER_CODE", i)+"' " +
							" AND START_DATE = '"+parm.getValue("START_DATE", i)+"' "  ;
					TParm selParm = new TParm(TJDODBTool.getInstance().select(selSQL)) ;
					if(selParm.getCount()<0)
						flg = false ;
					if(selParm.getErrCode()<0){
						System.out.println("----------��ѯҩƷ��Ϣ����ORDER_CODE:"
								+ parm.getValue("ORDER_CODE", i));
						System.out.println("----------��ѯҩƷ��Ϣ����SQL:" + selSQL);
					}
					if(flg){
						// �����¼�¼
						TParm newParm = selParm.getRow(0) ;
						newParm.setData("START_DATE", now);
						newParm.setData("END_DATE",   "99991231235959");	
						newParm.setData("ACTIVE_FLG", "Y");
						tmpInsSQL = "INSERT INTO SYS_FEE_HISTORY(ORDER_CODE,START_DATE,END_DATE,ORDER_DESC,ACTIVE_FLG,";
						tmpInsSQL += "LAST_FLG,PY1,PY2,SEQ,DESCRIPTION,TRADE_ENG_DESC,GOODS_DESC,GOODS_PYCODE,ALIAS_DESC,";
						tmpInsSQL += "ALIAS_PYCODE,SPECIFICATION,NHI_FEE_DESC,HABITAT_TYPE,MAN_CODE,HYGIENE_TRADE_CODE,";
						tmpInsSQL += "ORDER_CAT1_CODE,CHARGE_HOSP_CODE,OWN_PRICE,NHI_PRICE,GOV_PRICE,UNIT_CODE,LET_KEYIN_FLG,";
						tmpInsSQL += "DISCOUNT_FLG,EXPENSIVE_FLG,OPD_FIT_FLG,EMG_FIT_FLG,IPD_FIT_FLG,HRM_FIT_FLG,DR_ORDER_FLG,";
						tmpInsSQL += "INTV_ORDER_FLG,LCS_CLASS_CODE,TRANS_OUT_FLG,TRANS_HOSP_CODE,USEDEPT_CODE,EXEC_ORDER_FLG,EXEC_DEPT_CODE,";
						tmpInsSQL += "INSPAY_TYPE,ADDPAY_RATE,ADDPAY_AMT,NHI_CODE_O,NHI_CODE_E,NHI_CODE_I,CTRL_FLG,CLPGROUP_CODE,ORDERSET_FLG,";
						tmpInsSQL += "INDV_FLG,SUB_SYSTEM_CODE,RPTTYPE_CODE,DEV_CODE,OPTITEM_CODE,MR_CODE,DEGREE_CODE,CIS_FLG,OPT_USER,OPT_DATE,";
						tmpInsSQL += "OPT_TERM,RPP_CODE,ACTION_CODE,ATC_FLG,OWN_PRICE2,OWN_PRICE3,TUBE_TYPE,CAT1_TYPE,IS_REMARK,ATC_FLG_I,";
						tmpInsSQL += "REMARK_1,REMARK_2,REGION_CODE,SYS_GRUG_CLASS,NOADDTION_FLG,SYS_PHA_CLASS,SUPPLIES_TYPE"; // ��һ�����ϵĿ��ֶ�

						tmpInsSQL += ")";
						tmpInsSQL += " VALUES('" + newParm.getValue("ORDER_CODE")
								+ "',";
						tmpInsSQL += "'" + newParm.getValue("START_DATE") + "',";
						tmpInsSQL += "'" + newParm.getValue("END_DATE") + "',";
						tmpInsSQL += "'" + newParm.getValue("ORDER_DESC") + "',";
						tmpInsSQL += "'" + newParm.getValue("ACTIVE_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("LAST_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("PY1") + "',";
						tmpInsSQL += "'" + newParm.getValue("PY2") + "',";
						tmpInsSQL += "'" + newParm.getValue("SEQ") + "',";
						tmpInsSQL += "'" + newParm.getValue("DESCRIPTION") + "',";
						tmpInsSQL += "'" + newParm.getValue("TRADE_ENG_DESC") + "',";
						tmpInsSQL += "'" + newParm.getValue("GOODS_DESC") + "',";
						tmpInsSQL += "'" + newParm.getValue("GOODS_PYCODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("ALIAS_DESC") + "',";
						tmpInsSQL += "'" + newParm.getValue("ALIAS_PYCODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("SPECIFICATION") + "',";
						tmpInsSQL += "'" + newParm.getValue("NHI_FEE_DESC") + "',";
						tmpInsSQL += "'" + newParm.getValue("HABITAT_TYPE") + "',";
						tmpInsSQL += "'" + newParm.getValue("MAN_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("HYGIENE_TRADE_CODE")
								+ "',";
						tmpInsSQL += "'" + newParm.getValue("ORDER_CAT1_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("CHARGE_HOSP_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("OWN_PRICE") + "',";
						tmpInsSQL += "'" + newParm.getValue("NHI_PRICE") + "',";
						tmpInsSQL += "'" + newParm.getValue("GOV_PRICE") + "',";
						tmpInsSQL += "'" + newParm.getValue("UNIT_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("LET_KEYIN_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("DISCOUNT_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("EXPENSIVE_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("OPD_FIT_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("EMG_FIT_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("IPD_FIT_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("HRM_FIT_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("DR_ORDER_FLG") + "',";

						tmpInsSQL += "'" + newParm.getValue("INTV_ORDER_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("LCS_CLASS_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("TRANS_OUT_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("TRANS_HOSP_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("USEDEPT_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("EXEC_ORDER_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("EXEC_DEPT_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("INSPAY_TYPE") + "',";
						tmpInsSQL += "'" + newParm.getValue("ADDPAY_RATE") + "',";
						tmpInsSQL += "'" + newParm.getValue("ADDPAY_AMT") + "',";
						tmpInsSQL += "'" + newParm.getValue("NHI_CODE_O") + "',";
						tmpInsSQL += "'" + newParm.getValue("NHI_CODE_E") + "',";
						tmpInsSQL += "'" + newParm.getValue("NHI_CODE_I") + "',";
						tmpInsSQL += "'" + newParm.getValue("CTRL_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("CLPGROUP_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("ORDERSET_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("INDV_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("SUB_SYSTEM_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("RPTTYPE_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("DEV_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("OPTITEM_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("MR_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("DEGREE_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("CIS_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("OPT_USER") + "',";
						tmpInsSQL += "SYSDATE,";
						tmpInsSQL += "'" + newParm.getValue("OPT_TERM") + "',";
						tmpInsSQL += "'',"; //���ۼƻ���������д��Ӧ��Ϊ��
						tmpInsSQL += "'" + newParm.getValue("ACTION_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("ATC_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("OWN_PRICE2") + "',";
						tmpInsSQL += "'" + newParm.getValue("OWN_PRICE3") + "',";
						tmpInsSQL += "'" + newParm.getValue("TUBE_TYPE") + "',";
						tmpInsSQL += "'" + newParm.getValue("CAT1_TYPE") + "',";
						tmpInsSQL += "'" + newParm.getValue("IS_REMARK") + "',";
						tmpInsSQL += "'" + newParm.getValue("ATC_FLG_I") + "',";
						tmpInsSQL += "'" + newParm.getValue("REMARK_1") + "',";
						tmpInsSQL += "'" + newParm.getValue("REMARK_2") + "',";
						tmpInsSQL += "'" + newParm.getValue("REGION_CODE") + "',";
						tmpInsSQL += "'" + newParm.getValue("SYS_GRUG_CLASS") + "',";
						tmpInsSQL += "'" + newParm.getValue("NOADDTION_FLG") + "',";
						tmpInsSQL += "'" + newParm.getValue("SYS_PHA_CLASS") + "',";
						tmpInsSQL += "'" + newParm.getValue("SUPPLIES_TYPE") + "'";
						
						tmpInsSQL += ")";
//						System.out.println("---tmpInsSQL------" +i+"=========="+ tmpInsSQL);
						TParm insParm = new TParm(this.getDBTool().update(tmpInsSQL));
						if (insParm.getErrCode() < 0) {
							System.out.println("---------�����������ORDER_CODE:"
									+ parm.getValue("ORDER_CODE", i));
							System.out.println("---------SQL:" + tmpInsSQL);
						}
					}
				}
			}
//          if(parm.getValue("ACTIVE_FLG", 0).equals("N")){
//        	  TIOM_Database.logTableAction("SYS_FEE"); 
//        	  this.messageBox("����ɹ���");
//        	  labTip.setVisible(false);
//        	  return ;
//          }
			
			
		}
		labTip.setVisible(false);
	boolean banchFlg = 	getBanch() ;//ִ������
	if(!banchFlg) {
		return ;
	}
		this.messageBox("����ɹ���");
	
		
	}
	/**
	 * ����SYS_FEE
	 */
	public void onChgSysFeeClicked(){
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(null);
		TParm parm = new TParm();
		if (option == JFileChooser.APPROVE_OPTION) {//��ȡѡ�е��ļ�����
			File file = fileChooser.getSelectedFile();
			/*System.out.println();
			if (file == null) {
				return;
			}*/
			Workbook wb;
			try {
				wb = Workbook.getWorkbook(file);

				Sheet st = wb.getSheet(0);
				int row = st.getRows();
				int column = st.getColumns();
				// System.out.println("column==="+column);
				// StringBuffer wrongMsg = new StringBuffer();
				labTip.setVisible(true);
				String id = "";
				for (int j = 0; j < column; j++) {

					for (int i = 1; i < row; i++) {

						Cell cell = st.getCell(j, i);
						String name = st.getCell(1, i).getContents().trim();
						if (StringUtil.isNullString(name)) {
							// this.messageBox_("name is none");
							continue;
						}
						// if(!StringTool.isId(id)){
						// continue;
						// }
						switch (j) {
						case 0:
							id = cell.getContents().trim();
							parm.addData("ORDER_CODE", id);
							break;
						case 8:
							id = cell.getContents().trim();
							parm.addData("NHI_CODE_O", id);
							break;
						case 9:
							id = cell.getContents().trim();
							parm.addData("NHI_CODE_I", id);
							break;

						case 10:
							id = cell.getContents().trim();
							parm.addData("OWN_PRICE", id);
							break;

						case 12:
							id = cell.getContents().trim();
							parm.addData("HYGIENE_TRADE_CODE", id);
							break;

						}
					}
					parm.setCount(row - 1);
				}
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// System.out.println("-----------excel�ļ���¼-------------"+parm);
			System.out.println("-----------���¼�¼����-------------"
					+ (parm.getCount() + 1));
			TParm tmp;
			String tmpSQL;
			String tmpUptSQL;
			TParm newParm = new TParm();
			String tmpInsSQL;
			// 1.����parmÿһ�м�¼
			for (int i = 0; i < parm.getCount(); i++) {
				newParm.setData("NHI_CODE_O", parm.getValue("NHI_CODE_O", i));
				newParm.setData("NHI_CODE_E", parm.getValue("NHI_CODE_O", i));
				newParm.setData("NHI_CODE_I", parm.getValue("NHI_CODE_I", i));
				newParm.setData("HYGIENE_TRADE_CODE", parm.getValue(
						"HYGIENE_TRADE_CODE", i));
				newParm.setData("OWN_PRICE", parm.getValue("OWN_PRICE", i));
				
				
				tmpUptSQL = "UPDATE SYS_FEE SET NHI_CODE_O='"+parm.getValue("NHI_CODE_O", i)+"',";
				tmpUptSQL +=" NHI_CODE_E='"+parm.getValue("NHI_CODE_O", i)+"',";
				tmpUptSQL +=" NHI_CODE_I='"+parm.getValue("NHI_CODE_I", i)+"',";
				tmpUptSQL +=" HYGIENE_TRADE_CODE='"+parm.getValue("HYGIENE_TRADE_CODE", i)+"',";
				tmpUptSQL +=" OWN_PRICE='"+parm.getValue("OWN_PRICE", i)+"' ";
				
				tmpUptSQL += "WHERE ORDER_CODE='"
						+ parm.getValue("ORDER_CODE", i)+"'";

				//System.out.println("---tmpUptSQL------" + tmpUptSQL);
				TParm uptParm = new TParm(this.getDBTool().update(tmpUptSQL));
				if (uptParm.getErrCode() < 0) {
					System.out.println("----------����SYS_FEE��¼״̬����ORDER_CODE:"
							+ parm.getValue("ORDER_CODE", i));
					System.out.println("----------����SYS_FEE��¼״̬SQL:" + tmpUptSQL);
				}
				
			}
			
			
			
		}
		
		this.messageBox("�뿴������־,����ɹ���");
		labTip.setVisible(false);
		
		
	}
	

	/**
     * ����һ��SYS_FEE_HISTORY
     */
	public void onChgClicked() {
		type ="UPDATE" ; 
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(null);
		TParm parm = new TParm();
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			/*System.out.println();
			if (file == null) {
				return;
			}*/
			Workbook wb;
			try {
				wb = Workbook.getWorkbook(file);

				Sheet st = wb.getSheet(0);
				int row = st.getRows();
				int column = st.getColumns();
				// System.out.println("column==="+column);
				// StringBuffer wrongMsg = new StringBuffer();
				labTip.setVisible(true);
				String id = "";

				for (int j = 0; j < column; j++) {

					for (int i = 1; i < row; i++) {

						Cell cell = st.getCell(j, i);
						// ==================  У�����������
						if(!check(cell.getContents().trim(), i,j)){
							labTip.setVisible(false);
							return  ;	
						}
						switch (j) {
						case 0:
							id = cell.getContents().trim();
							parm.addData("ORDER_CODE", id);
							break;
						case 1:
							id = cell.getContents().trim();
							parm.addData("START_DATE", id);
							break;
						case 44:
							id = cell.getContents().trim();
							parm.addData("NHI_CODE_O", id);
							break;
						case 46:
							id = cell.getContents().trim();
							parm.addData("NHI_CODE_I", id);
							break;

						case 22:
							id = cell.getContents().trim();
							parm.addData("OWN_PRICE", id);
							break;

						case 19:
							id = cell.getContents().trim();
							parm.addData("HYGIENE_TRADE_CODE", id);
							break;

						}
					}
					parm.setCount(row - 1);
				}
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       

			// System.out.println("-----------excel�ļ���¼-------------"+parm);
			System.out.println("-----------���¼�¼����-------------"
					+ (parm.getCount()));
			TParm checkParm =	onRepeat(parm) ;
			if(checkParm.getErrCode()<0){
				this.messageBox("�ظ�ҽ���룺"+checkParm.getValue("ORDER_CODE")) ;
				labTip.setVisible(false);
				return ;
			}
			TParm tmp;
			String tmpSQL;
			String tmpUptSQL;
			TParm newParm = new TParm();
			String tmpInsSQL;
			
			// 1.����parmÿһ�м�¼
			labTip.setVisible(true);
			for (int i = 0; i < parm.getCount(); i++) {
				boolean flg=true;
				tmpSQL = "SELECT * FROM SYS_FEE_HISTORY WHERE ORDER_CODE='"
					+ parm.getValue("ORDER_CODE", i)
					+ "' AND ACTIVE_FLG='Y'" +
					" AND START_DATE = '"+parm.getValue("START_DATE", i)+"' " +
					" ORDER BY START_DATE DESC";
			tmp = new TParm(this.getDBTool().select(tmpSQL));
			//û�м�¼
			if(tmp.getCount()<=0){
				flg=false;
				System.out.println("----------û�м�¼ORDER_CODE:"+parm.getValue("ORDER_CODE", i));
				System.out.println("----------û�м�¼:" + tmpSQL);
				continue;
			}
				// 3.һ����¼������ORDER_CODE START_DATE,���¼�¼END_DATE 20121031235959
				// 20121031000000
				// �ж�����¼�����(Y)�����
			newParm = tmp.getRow(0) ;
				String now =SystemTool.getInstance().getDate().toString().substring(0,10).replace("-", "")+
				            SystemTool.getInstance().getDate().toString().substring(11,19).replace(":", "");
				tmpUptSQL = "UPDATE SYS_FEE_HISTORY SET END_DATE='"+now+"',ACTIVE_FLG='N' ";
				tmpUptSQL += "WHERE ORDER_CODE='"
						+ parm.getValue("ORDER_CODE", i)
				        + "' AND START_DATE='"+parm.getValue("START_DATE", i)+"' ";
				TParm uptParm = new TParm(this.getDBTool().update(tmpUptSQL));
				if (uptParm.getErrCode() < 0) {
					System.out.println("----------���ļ�¼״̬����ORDER_CODE:"
							+ parm.getValue("ORDER_CODE", i));
					System.out.println("----------���ļ�¼״̬SQL:" + tmpUptSQL);
				}		
				newParm.setData("START_DATE", now);
				newParm.setData("END_DATE",   "99991231235959");	
				newParm.setData("ACTIVE_FLG", "Y");
				newParm.setData("NHI_CODE_O", parm.getValue("NHI_CODE_O", i));
				newParm.setData("NHI_CODE_E", parm.getValue("NHI_CODE_O", i));
				newParm.setData("NHI_CODE_I", parm.getValue("NHI_CODE_I", i));
				newParm.setData("HYGIENE_TRADE_CODE", parm.getValue(
						"HYGIENE_TRADE_CODE", i));
				newParm.setData("OWN_PRICE", parm.getValue("OWN_PRICE", i));

				// �����¼�¼
				tmpInsSQL = "INSERT INTO SYS_FEE_HISTORY(ORDER_CODE,START_DATE,END_DATE,ORDER_DESC,ACTIVE_FLG,";
				tmpInsSQL += "LAST_FLG,PY1,PY2,SEQ,DESCRIPTION,TRADE_ENG_DESC,GOODS_DESC,GOODS_PYCODE,ALIAS_DESC,";
				tmpInsSQL += "ALIAS_PYCODE,SPECIFICATION,NHI_FEE_DESC,HABITAT_TYPE,MAN_CODE,HYGIENE_TRADE_CODE,";
				tmpInsSQL += "ORDER_CAT1_CODE,CHARGE_HOSP_CODE,OWN_PRICE,NHI_PRICE,GOV_PRICE,UNIT_CODE,LET_KEYIN_FLG,";
				tmpInsSQL += "DISCOUNT_FLG,EXPENSIVE_FLG,OPD_FIT_FLG,EMG_FIT_FLG,IPD_FIT_FLG,HRM_FIT_FLG,DR_ORDER_FLG,";
				tmpInsSQL += "INTV_ORDER_FLG,LCS_CLASS_CODE,TRANS_OUT_FLG,TRANS_HOSP_CODE,USEDEPT_CODE,EXEC_ORDER_FLG,EXEC_DEPT_CODE,";
				tmpInsSQL += "INSPAY_TYPE,ADDPAY_RATE,ADDPAY_AMT,NHI_CODE_O,NHI_CODE_E,NHI_CODE_I,CTRL_FLG,CLPGROUP_CODE,ORDERSET_FLG,";
				tmpInsSQL += "INDV_FLG,SUB_SYSTEM_CODE,RPTTYPE_CODE,DEV_CODE,OPTITEM_CODE,MR_CODE,DEGREE_CODE,CIS_FLG,OPT_USER,OPT_DATE,";
				tmpInsSQL += "OPT_TERM,RPP_CODE,ACTION_CODE,ATC_FLG,OWN_PRICE2,OWN_PRICE3,TUBE_TYPE,CAT1_TYPE,IS_REMARK,ATC_FLG_I,";
				tmpInsSQL += "REMARK_1,REMARK_2,REGION_CODE,SYS_GRUG_CLASS,NOADDTION_FLG,SYS_PHA_CLASS,SUPPLIES_TYPE"; // ��һ�����ϵĿ��ֶ�

				tmpInsSQL += ")";
				tmpInsSQL += " VALUES('" + newParm.getValue("ORDER_CODE")
						+ "',";
				tmpInsSQL += "'" + newParm.getValue("START_DATE") + "',";
				tmpInsSQL += "'" + newParm.getValue("END_DATE") + "',";
				tmpInsSQL += "'" + newParm.getValue("ORDER_DESC") + "',";
				tmpInsSQL += "'" + newParm.getValue("ACTIVE_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("LAST_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("PY1") + "',";
				tmpInsSQL += "'" + newParm.getValue("PY2") + "',";
				tmpInsSQL += "'" + newParm.getValue("SEQ") + "',";
				tmpInsSQL += "'" + newParm.getValue("DESCRIPTION") + "',";
				tmpInsSQL += "'" + newParm.getValue("TRADE_ENG_DESC") + "',";
				tmpInsSQL += "'" + newParm.getValue("GOODS_DESC") + "',";
				tmpInsSQL += "'" + newParm.getValue("GOODS_PYCODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("ALIAS_DESC") + "',";
				tmpInsSQL += "'" + newParm.getValue("ALIAS_PYCODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("SPECIFICATION") + "',";
				tmpInsSQL += "'" + newParm.getValue("NHI_FEE_DESC") + "',";
				tmpInsSQL += "'" + newParm.getValue("HABITAT_TYPE") + "',";
				tmpInsSQL += "'" + newParm.getValue("MAN_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("HYGIENE_TRADE_CODE")
						+ "',";
				tmpInsSQL += "'" + newParm.getValue("ORDER_CAT1_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("CHARGE_HOSP_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("OWN_PRICE") + "',";
				tmpInsSQL += "'" + newParm.getValue("NHI_PRICE") + "',";
				tmpInsSQL += "'" + newParm.getValue("GOV_PRICE") + "',";
				tmpInsSQL += "'" + newParm.getValue("UNIT_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("LET_KEYIN_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("DISCOUNT_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("EXPENSIVE_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("OPD_FIT_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("EMG_FIT_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("IPD_FIT_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("HRM_FIT_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("DR_ORDER_FLG") + "',";

				tmpInsSQL += "'" + newParm.getValue("INTV_ORDER_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("LCS_CLASS_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("TRANS_OUT_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("TRANS_HOSP_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("USEDEPT_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("EXEC_ORDER_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("EXEC_DEPT_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("INSPAY_TYPE") + "',";
				tmpInsSQL += "'" + newParm.getValue("ADDPAY_RATE") + "',";
				tmpInsSQL += "'" + newParm.getValue("ADDPAY_AMT") + "',";
				tmpInsSQL += "'" + newParm.getValue("NHI_CODE_O") + "',";
				tmpInsSQL += "'" + newParm.getValue("NHI_CODE_E") + "',";
				tmpInsSQL += "'" + newParm.getValue("NHI_CODE_I") + "',";
				tmpInsSQL += "'" + newParm.getValue("CTRL_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("CLPGROUP_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("ORDERSET_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("INDV_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("SUB_SYSTEM_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("RPTTYPE_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("DEV_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("OPTITEM_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("MR_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("DEGREE_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("CIS_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("OPT_USER") + "',";
				tmpInsSQL += "SYSDATE,";
				tmpInsSQL += "'" + newParm.getValue("OPT_TERM") + "',";
				tmpInsSQL += "'',"; //���ۼƻ���������д��Ӧ��Ϊ��
				tmpInsSQL += "'" + newParm.getValue("ACTION_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("ATC_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("OWN_PRICE2") + "',";
				tmpInsSQL += "'" + newParm.getValue("OWN_PRICE3") + "',";
				tmpInsSQL += "'" + newParm.getValue("TUBE_TYPE") + "',";
				tmpInsSQL += "'" + newParm.getValue("CAT1_TYPE") + "',";
				tmpInsSQL += "'" + newParm.getValue("IS_REMARK") + "',";
				tmpInsSQL += "'" + newParm.getValue("ATC_FLG_I") + "',";
				tmpInsSQL += "'" + newParm.getValue("REMARK_1") + "',";
				tmpInsSQL += "'" + newParm.getValue("REMARK_2") + "',";
				tmpInsSQL += "'" + newParm.getValue("REGION_CODE") + "',";
				tmpInsSQL += "'" + newParm.getValue("SYS_GRUG_CLASS") + "',";
				tmpInsSQL += "'" + newParm.getValue("NOADDTION_FLG") + "',";
				tmpInsSQL += "'" + newParm.getValue("SYS_PHA_CLASS") + "',";
				tmpInsSQL += "'" + newParm.getValue("SUPPLIES_TYPE") + "'";
				
				tmpInsSQL += ")";
				//System.out.println("---tmpInsSQL------" + tmpInsSQL);
				TParm insParm = new TParm(this.getDBTool().update(tmpInsSQL));
				if (insParm.getErrCode() < 0) {
					System.out.println("---------�����������ORDER_CODE:"
							+ parm.getValue("ORDER_CODE", i));
					System.out.println("---------SQL:" + tmpInsSQL);
				}
				// ���������
			}
			labTip.setVisible(false);
			boolean banchFlg = 	getBanch() ;//ִ������
			if(!banchFlg) {
				return  ;
			}
			this.messageBox("����ɹ���");       
			
		}

	}
	/**
     * ����һ��SYS_FEE_HISTORY
     */
	public void onInsertClicked() {
		type ="INSERT" ; 
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(null);
		TParm newParm = new TParm();
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			/*System.out.println();
			if (file == null) {
				return;
			}*/
			Workbook wb;
			try {
				wb = Workbook.getWorkbook(file);

				Sheet st = wb.getSheet(0);
				int row = st.getRows();
				int column = st.getColumns();
				// System.out.println("column==="+column);
				// StringBuffer wrongMsg = new StringBuffer();
				labTip.setVisible(true);
				String id = "";
				String idName = "" ;

				for (int j = 0; j < column; j++) {

					for (int i = 1; i < row; i++) {

						Cell cell = st.getCell(j, i);
						// ==================  У�����������
						if(!check(cell.getContents().trim(), i,j)){
							labTip.setVisible(false);
							return  ;	
						}
									
						idName = st.getCell(j, 0).getContents().trim() ;
							id = cell.getContents().trim();
							newParm.addData(idName, id);
					
					}
					newParm.setCount(row - 1);
				}
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       

		}
			// System.out.println("-----------excel�ļ���¼-------------"+parm);
			System.out.println("-----------���¼�¼����-------------"
					+ (newParm.getCount()));
			TParm checkParm =	onRepeat(newParm) ;
			if(checkParm.getErrCode()<0){
				this.messageBox("�ظ�ҽ���룺"+checkParm.getValue("ORDER_CODE")) ;
				labTip.setVisible(false);
				return ;
			}
			String tmpInsSQL;
			String selectSql;
			String orderCode = "" ;
			String now =SystemTool.getInstance().getDate().toString().substring(0,10).replace("-", "")+
            SystemTool.getInstance().getDate().toString().substring(11,19).replace(":", "");
			String activeFlg = "Y" ;
           for(int i=0;i<newParm.getCount();i++){
        	 //�ж��Ƿ����ظ�����
   			 selectSql = " SELECT ORDER_CODE FROM SYS_FEE_HISTORY WHERE ORDER_CODE = '"+newParm.getValue("ORDER_CODE", i)+"'"  ;
   			 TParm result = new TParm(TJDODBTool.getInstance().select(selectSql)) ;
   			 if(result.getCount()>0){
   				orderCode +=result.getValue("ORDER_CODE", 0)+";" ;
   				continue ;
   			 }
        	// �����¼�¼
				tmpInsSQL = "INSERT INTO SYS_FEE_HISTORY(ORDER_CODE,START_DATE,END_DATE,ORDER_DESC,ACTIVE_FLG,";
				tmpInsSQL += "LAST_FLG,PY1,PY2,SEQ,DESCRIPTION,TRADE_ENG_DESC,GOODS_DESC,GOODS_PYCODE,ALIAS_DESC,";
				tmpInsSQL += "ALIAS_PYCODE,SPECIFICATION,NHI_FEE_DESC,HABITAT_TYPE,MAN_CODE,HYGIENE_TRADE_CODE,";
				tmpInsSQL += "ORDER_CAT1_CODE,CHARGE_HOSP_CODE,OWN_PRICE,NHI_PRICE,GOV_PRICE,UNIT_CODE,LET_KEYIN_FLG,";
				tmpInsSQL += "DISCOUNT_FLG,EXPENSIVE_FLG,OPD_FIT_FLG,EMG_FIT_FLG,IPD_FIT_FLG,HRM_FIT_FLG,DR_ORDER_FLG,";
				tmpInsSQL += "INTV_ORDER_FLG,LCS_CLASS_CODE,TRANS_OUT_FLG,TRANS_HOSP_CODE,USEDEPT_CODE,EXEC_ORDER_FLG,EXEC_DEPT_CODE,";
				tmpInsSQL += "INSPAY_TYPE,ADDPAY_RATE,ADDPAY_AMT,NHI_CODE_O,NHI_CODE_E,NHI_CODE_I,CTRL_FLG,CLPGROUP_CODE,ORDERSET_FLG,";
				tmpInsSQL += "INDV_FLG,SUB_SYSTEM_CODE,RPTTYPE_CODE,DEV_CODE,OPTITEM_CODE,MR_CODE,DEGREE_CODE,CIS_FLG,OPT_USER,OPT_DATE,";
				tmpInsSQL += "OPT_TERM,RPP_CODE,ACTION_CODE,ATC_FLG,OWN_PRICE2,OWN_PRICE3,TUBE_TYPE,CAT1_TYPE,IS_REMARK,ATC_FLG_I,";
				tmpInsSQL += "REMARK_1,REMARK_2,REGION_CODE,SYS_GRUG_CLASS,NOADDTION_FLG,SYS_PHA_CLASS,SUPPLIES_TYPE"; // ��һ�����ϵĿ��ֶ�

				tmpInsSQL += ")";
				tmpInsSQL += " VALUES('" + newParm.getValue("ORDER_CODE",i)
						+ "',";
				tmpInsSQL += "'"+now+"',";
				tmpInsSQL += "'" + newParm.getValue("END_DATE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("ORDER_DESC",i) + "',";
				tmpInsSQL += "'" +activeFlg+ "',";
				tmpInsSQL += "'" + newParm.getValue("LAST_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("PY1",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("PY2",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("SEQ",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("DESCRIPTION",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("TRADE_ENG_DESC",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("GOODS_DESC",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("GOODS_PYCODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("ALIAS_DESC",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("ALIAS_PYCODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("SPECIFICATION",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("NHI_FEE_DESC",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("HABITAT_TYPE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("MAN_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("HYGIENE_TRADE_CODE",i)
						+ "',";
				tmpInsSQL += "'" + newParm.getValue("ORDER_CAT1_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("CHARGE_HOSP_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("OWN_PRICE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("NHI_PRICE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("GOV_PRICE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("UNIT_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("LET_KEYIN_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("DISCOUNT_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("EXPENSIVE_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("OPD_FIT_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("EMG_FIT_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("IPD_FIT_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("HRM_FIT_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("DR_ORDER_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("INTV_ORDER_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("LCS_CLASS_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("TRANS_OUT_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("TRANS_HOSP_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("USEDEPT_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("EXEC_ORDER_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("EXEC_DEPT_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("INSPAY_TYPE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("ADDPAY_RATE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("ADDPAY_AMT",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("NHI_CODE_O",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("NHI_CODE_E",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("NHI_CODE_I",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("CTRL_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("CLPGROUP_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("ORDERSET_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("INDV_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("SUB_SYSTEM_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("RPTTYPE_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("DEV_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("OPTITEM_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("MR_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("DEGREE_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("CIS_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("OPT_USER",i) + "',";
				tmpInsSQL += "SYSDATE,";
				tmpInsSQL += "'" + newParm.getValue("OPT_TERM",i) + "',";
				tmpInsSQL += "'',"; //���ۼƻ���������д��Ӧ��Ϊ��
				tmpInsSQL += "'" + newParm.getValue("ACTION_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("ATC_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("OWN_PRICE2",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("OWN_PRICE3",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("TUBE_TYPE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("CAT1_TYPE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("IS_REMARK",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("ATC_FLG_I",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("REMARK_1",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("REMARK_2",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("REGION_CODE",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("SYS_GRUG_CLASS",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("NOADDTION_FLG",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("SYS_PHA_CLASS",i) + "',";
				tmpInsSQL += "'" + newParm.getValue("SUPPLIES_TYPE",i) + "'";
				
				tmpInsSQL += ")";
				//System.out.println("---tmpInsSQL------" + tmpInsSQL);
				TParm insParm = new TParm(this.getDBTool().update(tmpInsSQL));
				if (insParm.getErrCode() < 0) {
					System.out.println("---------�����������ORDER_CODE:"
							+ newParm.getValue("ORDER_CODE", i));
					System.out.println("---------SQL:" + tmpInsSQL);
				}
				// ���������
			}   		
		labTip.setVisible(false);
		if(orderCode.length()>0){
			this.messageBox("�ظ�ҽ������=="+orderCode) ;
		}
		boolean banchFlg = 	getBanch() ;//ִ������
		if(!banchFlg) {
			return  ;
		}
		
		
			this.messageBox("����ɹ���");       
		
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
	 * ִ������
	 */
	public boolean getBanch(){
		if(this.getBanchData().getCount()<=0){
			 return false ;
		}
		TParm  parmToBanch = new TParm() ;
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		 parmToBanch.setData("OPT_USER", Operator.getID());
         parmToBanch.setData("OPT_TERM", Operator.getIP());
         parmToBanch.setData("OPT_DATE", now);
         parmToBanch.setData("ORDER", this.getBanchData().getData());
         TParm result = TIOM_AppServer.executeAction(
                 "action.sys.SYSFeeReadjustAction",
                 "onInsertSYSFeeReadjust", parmToBanch);
             if (result.getErrCode() < 0) {
                 this.messageBox_(result);
                 return false ;
             }
           TIOM_Database.logTableAction("SYS_FEE");
           return true ;
	}

	/**
	 * ��ѯ��ȡ��������
	 */
	public TParm getBanchData(){
		String now =StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyyMMdd");
		 String selSql =
	            " SELECT START_DATE,ORDER_DESC,OWN_PRICE,NHI_PRICE,GOV_PRICE,RPP_CODE,ORDER_CODE,END_DATE,OWN_PRICE2,OWN_PRICE3,ORDERSET_FLG " +
	            " FROM SYS_FEE_HISTORY " +
	            " WHERE ACTIVE_FLG='Y' " +//ֻ��ʾ��ǰ���õ�
	            " AND TO_DATE (START_DATE, 'YYYYMMDDHH24MISS') > TO_DATE (" + now +" , 'YYYYMMDD')"+
	            " AND RPP_CODE IS NULL" +  //���۵���Ϊ��
	            " ORDER BY START_DATE ";
		 TParm result = new TParm(TJDODBTool.getInstance().select(selSql));
	        if (result.getCount() <= 0) {
	            this.messageBox("û�д�����Ŀ");
	            return result;
	        }
	    for(int i=0;i<result.getCount();i++){
	    	 String startDate = checkDateTime( (String) result.getData(
	                 "START_DATE", i));
	    	result.setData("START_DATE", i,
	    			startDate.substring(0, 4) + "/" +
	    			startDate.substring(4, 6) + "/" +
	    			startDate.substring(6, 8) + " " +
	    			startDate.substring(8, 10) + ":" +
	    			startDate.substring(10, 12) + ":" +
                     startDate.substring(12));
	    }
	        return result ;
	}
	  /**
     * ����Ƿ�����14λ��yyyyMMddHHmmss��
     * @param dateTime String
     * @return String
     */
    private String checkDateTime(String dateTime) {

        if (dateTime.length() >= 14) {
            return dateTime.substring(0, 14);
        }
        //С��14λ
        int addZero = 14 - dateTime.length();
        for (int i = 0; i < addZero; i++)
            dateTime += "0";
        return dateTime;
    }
/**
 * У��
 */
    public boolean check(String date,int i,int j){
    	int n = i+1 ;
    	if (StringUtil.isNullString(date) && j==0) {
			
			this.messageBox("��"+n+"�е�"+1+"�и�ʽ�д���") ;
			return false ;
		}
		if (j==1 && (StringUtil.isNullString(date) || date.length()>14)) {
			this.messageBox("��"+n+"�е�"+2+"�и�ʽ�д���") ;
			return false ;
		}
		if (type.equals("START_STOP") && j==4 && StringUtil.isNullString(date)) {
			this.messageBox("��"+n+"�е�"+5+"�и�ʽ�д���,���ñ�־����Ϊ��") ;
			return false ;
		}
		return true ;
    }
    /**
     * �ж��Ƿ����ظ�����
     */
    public TParm  onRepeat(TParm parm){
    	TParm result = new TParm() ;
    	for(int i=0;i<parm.getCount();i++){
    		TParm rowParm = parm.getRow(i);
			String orderCode = rowParm.getValue("ORDER_CODE") ;
			for(int j = i+1;j < parm.getCount();j++){ 
				TParm rowParmNew = parm.getRow(j);
				String orderCodeNew = rowParmNew.getValue("ORDER_CODE") ;
			    if(orderCode.equals(orderCodeNew) ){ 
			   	    result.setErrCode(-1) ;
			   	    result.setData("ORDER_CODE", orderCode) ;
			   	    return result ;
			   }
    	}  	
    }
    	return result ;
}
}