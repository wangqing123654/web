package action.ins;

import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.action.TAction;

import jdo.ins.INSBlackListTool;
import jdo.ins.INSDownloadPayTool;
import jdo.ins.INSNoticeTool;
import jdo.ins.INSTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * 
 * <p>
 * Title: ����֧����Ϣ
 * </p>
 * 
 * <p>
 * Description:����֧����Ϣ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2012.02.11
 * @version 1.0
 */
public class INSDownloadPayAction extends TAction {
	/**
	 * ��ѯȡ���걨����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveData(TParm tableData) {
		TParm result = new TParm();
		if (tableData == null)
			return result.newErrParm(-1, "����Ϊ��");
		TParm parm = null;
		TConnection connection = null;
		try {
			connection = getConnection();
			//------------------------------------------------------------------
			parm = tableData.getParm("tab4Download_G_J");
			parm.setData("NHI_HOSP_CODE", tableData.getValue("NHI_HOSP_CODE"));
			parm.setData("OPT_USER", tableData.getValue("OPT_USER"));
			parm.setData("OPT_TERM", tableData.getValue("OPT_TERM"));
			parm.setData("ADM_SEQ", tableData.getValue("ADM_SEQ"));
			result = INSDownloadPayTool.getInstance().updateIBS(parm,connection);
			
			if (result == null || result.getErrCode() < 0) {
				throw new Exception("�������ݿ�δ�ɹ����������£�" + parm);
			}
			
			//------------------------------------------------------------------
			parm = tableData.getParm("adm_Confirm");
			parm.setData("OPT_USER", tableData.getValue("OPT_USER"));
			parm.setData("OPT_TERM", tableData.getValue("OPT_TERM"));
			result = INSDownloadPayTool.getInstance().updateConfirmSet(
					parm,connection);
			if (result == null || result.getErrCode() < 0) {
				throw new Exception("�������ݿ�δ�ɹ����������£�" + parm); 
			}
			//------------------------------------------------------------------
			TParm tab4Download_I_E = tableData.getParm("tab4Download_I_E");
			for (int i = 0; i < tab4Download_I_E.getCount("CONFIRM_NO"); i++) {
				parm = tab4Download_I_E.getRow(i);
				parm.setData("NHI_HOSP_CODE", tableData.getValue("NHI_HOSP_CODE"));
				parm.setData("OPT_USER", tableData.getValue("OPT_USER"));
				parm.setData("OPT_TERM", tableData.getValue("OPT_TERM"));
				parm.setData("ADM_SEQ", tableData.getValue("ADM_SEQ"));
				parm.setData("REGION_CODE", tableData.getValue("REGION_CODE"));
				parm.setData("REPORT_CODE", tableData.getValue("REPORT_CODE"));
				parm.setData("CHARGE_DATE", SystemTool.getInstance().getDateReplace(parm.getValue("CHARGE_DATE"), true));
				result = INSDownloadPayTool.getInstance().insertIBSDownload(
						parm,connection);
				if (result == null || result.getErrCode() < 0) {
					throw new Exception("�������ݿ�δ�ɹ����������£�" + parm);					
				}	
			}
			
			//------------------------------------------------------------------
			connection.commit();
		} catch (Exception ex) {
			connection.rollback();
			ex.printStackTrace();
			return result;
		} finally {
			if (connection != null) {
				connection.close();
			}
			
		}
		return result;
	}
	/**
	 * ֧����Ϣ����
	 * @return
	 */
	public TParm saveDataPay(TParm tableData){
		TParm DataDown_rs_K = tableData.getParm("DataDown_rs_K");
		TConnection connection = getConnection();
		
		TParm parm = null;	
		TParm result=null;
		//------------------------------------------------------------------
		for (int i = 0; i < DataDown_rs_K.getCount("HOSP_NHI_NO"); i++) {
			parm = DataDown_rs_K.getRow(i);
			parm.setData("NHI_HOSP_CODE", tableData.getValue("NHI_HOSP_CODE"));
			parm.setData("OPT_USER", tableData.getValue("OPT_USER"));
			parm.setData("OPT_TERM", tableData.getValue("OPT_TERM"));
			parm.setData("REGION_CODE", tableData.getValue("REGION_CODE"));
			parm.setData("REPORT_CODE", tableData.getValue("REPORT_CODE"));
			result = INSDownloadPayTool.getInstance().insertIBSPay(parm,connection);
			if (result == null || result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}	
		}
		connection.commit();
		connection.close();
		return result;
	}
	public TParm saveData1(TParm tableData) {
		TParm result = new TParm();
		
		if (tableData == null)
			return result.newErrParm(-1, "����Ϊ��");
		TConnection connection = null;
		try {
			connection = getConnection();
			TParm parm = null;
			for (int i = 0; i < tableData.getCount("HOSP_NHI_NO"); i++) {
				
				parm = tableData.getRow(i);
				parm.setData("NHI_HOSP_CODE", tableData.getValue("NHI_HOSP_CODE"));
				parm.setData("OPT_USER", tableData.getValue("OPT_USER"));
				parm.setData("OPT_TERM", tableData.getValue("OPT_TERM"));
				parm.setData("REGION_CODE", tableData.getValue("REGION_CODE"));
				result = INSDownloadPayTool.getInstance().insertIBSPay(parm,connection);
				if (result == null || result.getErrCode() < 0) {
					throw new Exception("�������ݿ�δ�ɹ����������£�" + parm);
					
				}
				result = INSDownloadPayTool.getInstance().updateIBS(parm,connection);
				if (result == null || result.getErrCode() < 0) {
					throw new Exception("�������ݿ�δ�ɹ����������£�" + parm);
				}
				result = INSDownloadPayTool.getInstance().updateConfirmSet(
						parm,connection);
				if (result == null || result.getErrCode() < 0) {
					throw new Exception("�������ݿ�δ�ɹ����������£�" + parm);
				}
				
				result = INSDownloadPayTool.getInstance().insertIBSDownload(
						parm,connection);
				if (result == null || result.getErrCode() < 0) {
					throw new Exception("�������ݿ�δ�ɹ����������£�" + parm);
				}
			}
			connection.commit();
		} catch (Exception ex) {
			connection.rollback();
			ex.printStackTrace();
			return result;
		} finally {
			if (connection != null) {
				connection.close();
			}
			
		}
		return result;
		
	}


}
