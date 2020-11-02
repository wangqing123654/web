package action.clp;

import java.sql.Timestamp;
import com.dongyang.action.TAction;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import jdo.clp.CLPSingleDiseTool;

/**
 * <p>Title: ������action</p>
 *
 * <p>Description: ������action</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author WangLong 20120926
 * @version 1.0
 */
public class CLPSingleDiseAction extends TAction {
	
	/**
	 * ���뵥��������
	 * @param saveParm
	 * @return
	 */
	public TParm insertSDData(TParm parm) {
		TConnection connection = getConnection();
		TParm sdParm = new TParm();
		sdParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));// �ż���
		sdParm.setData("DISE_CODE", parm.getValue("DISE_CODE"));// �����ַ���
		sdParm.setData("CASE_NO", parm.getValue("CASE_NO"));// �����
		sdParm.setData("MR_NO", parm.getValue("MR_NO"));// ������
		sdParm.setData("IPD_NO", parm.getValue("IPD_NO"));// סԺ��
		sdParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));// ����
		sdParm.setData("SEX_CODE", parm.getValue("SEX_CODE"));// �Ա�
		sdParm.setData("AGE", parm.getValue("AGE"));// ����
		//Timestamp inDate = parm.getTimestamp("IN_DATE");
		if (parm.getData("IN_DATE") == null) {
			sdParm.setData("IN_DATE", new TNull(Timestamp.class));// ��Ժ����
		} else{
			sdParm.setData("IN_DATE", parm.getTimestamp("IN_DATE"));// ��Ժ����
		}
		// Timestamp outDate = parm.getTimestamp("OUT_DATE");
		if (parm.getData("OUT_DATE") == null) {
			sdParm.setData("OUT_DATE", new TNull(Timestamp.class));// ��Ժ����
		} else {
			sdParm.setData("OUT_DATE", parm.getTimestamp("OUT_DATE"));// ��Ժ����
		}
		sdParm.setData("STAY_DAYS", parm.getValue("STAY_DAYS"));// סԺ����
		sdParm.setData("ICD_CODE", parm.getValue("ICD_CODE"));// ��ϴ���
		sdParm.setData("ICD_CHN_DESC", parm.getValue("ICD_CHN_DESC"));// �������
		sdParm.setData("TBYS", parm.getValue("TBYS"));// ���ҽʦ
		sdParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		sdParm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
		sdParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		// sdParm.setData("FILE_NO", parm.getValue("FILE_NO"));
		sdParm.setData("FILE_PATH", parm.getValue("FILE_PATH"));
		sdParm.setData("FILE_NAME", parm.getValue("FILE_NAME"));
		TParm result = new TParm();
		result = CLPSingleDiseTool.getInstance().deleteSDData(sdParm, connection);// ɾ��������
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		result = CLPSingleDiseTool.getInstance().insertSDData(sdParm, connection);// ����������
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	
	/**
	 * ��������ʷ�����ϲ�
	 */
	public TParm mergeEMRhistory(TParm actionParm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
//		result = CLPSingleDiseTool.getInstance().clearSDDBHistory(actionParm, connection);//ɾ���ɵĵ�����������¼
//		if (result.getErrCode() < 0) {
//			connection.rollback();
//			connection.close();
//			return result;
//		}
//        result = CLPSingleDiseTool.getInstance().clearSDFileHistory(actionParm, connection);// ɾ���ɵĲ����ļ���¼
//        if (result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
		result = CLPSingleDiseTool.getInstance().copySDDBHistory(actionParm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			result.setErrText("�����ݿ⸴�Ƶ����ּ�¼����");
			return result;
		}
		result = CLPSingleDiseTool.getInstance().copySDFileHistory(actionParm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			result.setErrText("�����ݿ⸴�Ʋ�����¼����");
			return result;
		}
		if(!CLPSingleDiseTool.getInstance().copySDEMRFile(actionParm)){
			connection.rollback();
			connection.close();
			result.setErrCode(-1);
			result.setErrText("���Ʋ����ļ�����");
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	
}
