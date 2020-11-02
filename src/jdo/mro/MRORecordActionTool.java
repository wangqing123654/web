package jdo.mro;

import jdo.sta.StaGenMroDataTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ������ҳ ��Tool
 * </p>
 * 
 * <p>
 * Description: ������ҳ ��Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-5-5
 * @version 1.0
 */
public class MRORecordActionTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	public static MRORecordActionTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static MRORecordActionTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MRORecordActionTool();
		return instanceObject;
	}

	public MRORecordActionTool() {
	}

	/**
	 * �޸���ҳ��������
	 * 
	 * @param Tparm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateRecordDate(TParm parm, TConnection connection) {
		TParm result = new TParm();
		String ADMCHK_FLG = "";// סԺ�ύ
		String DIAGCHK_FLG = "";// ҽʦ�ύ
		String BILCHK_FLG = "";// �����ύ
		String QTYCHK_FLG = "";// �����ύ
		String ctzCode="";
		TParm page;
		if (parm.getData("Page1") != null) {
			page = parm.getParm("Page1"); // ȡ��һҳǩ����
			ADMCHK_FLG = page.getValue("ADMCHK_FLG");
			ctzCode=page.getValue("CTZ1_CODE");
			result = MRORecordTool.getInstance().saveFirst(page, connection);// �޸ĵ�һҳǩ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		if (parm.getData("Page2") != null) {
			page = parm.getParm("Page2"); // ȡ�ڶ�ҳǩ����
			DIAGCHK_FLG = page.getValue("DIAGCHK_FLG");
			result = MRORecordTool.getInstance().saveSecend(page, connection);// �޸ĵڶ�ҳǩ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			String flg=page.getValue("ISMODIFY_DSDATE");//ͬ����Ժ����    shibl 20130108 add 
			if(flg.equals("Y")){
				page.setData("CTZ1_CODE", ctzCode);
				result = MRORecordTool.getInstance().modifydsDate(page, connection);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		if (parm.getData("Page4") != null) {
			page = parm.getParm("Page4"); // ȡ����ҳǩ����
			BILCHK_FLG = page.getValue("BILCHK_FLG");
			QTYCHK_FLG = page.getValue("QTYCHK_FLG");
			result = MRORecordTool.getInstance().saveFour(page, connection);// �޸ĵ���ҳǩ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		// ȡ������Ϣ���б���
		if (parm.getData("PageOP") != null) {
			page = parm.getParm("PageOP");
//			String[] sql = (String[]) parm.getData("PageOP"); // ȡ��������
			result = MRORecordTool.getInstance().saveOP(page, connection);// ����������Ϣ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		if (parm.getData("PageICD") != null) {
			TParm diag = parm.getParm("PageICD"); // ȡ�������
			result = MRORecordTool.getInstance()
					.updateMRODiag(diag, connection);// �޸ĵ���ҳǩ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		
		
		//add by yangjj 20150701
		if (parm.getData("ChildBirth") != null) {
			TParm childBirthParm = parm.getParm("ChildBirth"); 
			result = MRORecordTool.getInstance()
					.saveChildBirth(childBirthParm, connection);// �޸Ĳ������ҳǩ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		
		if (ADMCHK_FLG.equals("Y") && DIAGCHK_FLG.equals("Y")
				&& BILCHK_FLG.equals("Y") && QTYCHK_FLG.equals("Y")) {//ͬʱ�ύʱ�޸�ҽ�Ƽ���м��     shibl 20130108 add
			// �޸���ҳ����ҽ��ͳ�Ʊ��
			result = StaGenMroDataTool.getInstance().updateSTAFlg(parm,"","0",connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		
		if (parm.getData("PagePatInfo") != null) {
			page = parm.getParm("PagePatInfo"); // ȡ�벡����ҳ��صĲ�����Ϣҳǩ���
			result = MRORecordTool.getInstance().savePatToMro(page, connection);// �޸ĵ�һҳǩ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		
		return result;
	}
}
