package action.hrm;

import java.util.ArrayList;
import java.util.List;
import jdo.hl7.Hl7Communications;
import jdo.hrm.HRMCompanyReportTool;
import jdo.hrm.HRMOrderTool;
import jdo.hrm.HRMPatadMTool;
import jdo.med.MEDApplyTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
/**
 * <p>Title: ����������屨������</p>
 * 
 * <p>Description: ����������屨������</p>
 * 
 * <p>Copyright: javahis 20090922</p>
 * 
 * <p>Company:JavaHis</p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMCompanyReportAction extends TAction {
	/**
	 * ����
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm){
		TParm result = new TParm();
		if (parm == null) {
			result.setErrCode(-1);
			result.setErrText("��������");
			return result;
		}
		// ȡ������
		TConnection conn = getConnection();
//		System.out.println("here in action");
		result = HRMCompanyReportTool.getInstance().onSave(parm, conn);
		if(result.getErrCode()!=0){
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
	
	   /**
     * ���½��첡���ĵ绰 add by wanglong 20130110
     * @param mrNo
     * @param caseNo
     * @return
     */
    public TParm updateHRMPatTEL(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErrCode(-1);
            result.setErrText("��������");
            return result;
        }
        TConnection conn = getConnection();
        for (int i = 0; i < parm.getCount(); i++) {
            if(parm.getValue("TEL", i).trim().equals("")){
                continue;
            }else {
                if(!parm.getValue("CASE_NO", i).equals("")){
                    result = HRMCompanyReportTool.getInstance().updateTELFromHRMPatAdm(parm.getValue("TEL", i), parm.getValue("CASE_NO", i), conn);
                    if(result.getErrCode()!=0){
                        conn.rollback();
                        result.addData("MR_NO", parm.getData("MR_NO", i));
                        result.addData("PAT_NAME", parm.getData("PAT_NAME", i));
                        continue;
                    }
                } 
                result = HRMCompanyReportTool.getInstance().updateTELFromHRMContractd(parm.getValue("TEL", i), parm.getValue("MR_NO", i), conn);
                if (result.getErrCode() != 0) {
                    conn.rollback();
                    result.addData("MR_NO", parm.getData("MR_NO", i));
                    result.addData("PAT_NAME", parm.getData("PAT_NAME", i));
                    continue;
                }
                result = HRMCompanyReportTool.getInstance().updateTELFromSYSPatInfo(parm.getValue("TEL", i), parm.getValue("MR_NO", i), conn);
                if (result.getErrCode() != 0) {
                    conn.rollback();
                    result.addData("MR_NO", parm.getData("MR_NO", i));
                    result.addData("PAT_NAME", parm.getData("PAT_NAME", i));
                    continue;
                }
                conn.commit();
            }
        }
        conn.commit();
        conn.close();
        result.setCount(result.getCount("MR_NO"));
        return result;
    }
    
    /**
     * ȡ��չ������
     * =============pangben 2013-3-10 
     */
    public TParm onDeleteOrder(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErrCode(-1);
            result.setErrText("��������");
            return result;
        }
        TConnection conn = getConnection();
        TParm tempParm = new TParm();
        StringBuffer patMmessage = new StringBuffer();
        StringBuffer hl7Mmessage = new StringBuffer();
        int index = 0;
        for (int i = 0; i < parm.getCount(); i++) {
            tempParm = parm.getRow(i);
            // ��ѯ�Ƿ��������
            result = HRMPatadMTool.getInstance().selectHrmPatadm(tempParm);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
            if (result.getCount() <= 0) {
                index++;
                if (i % 5 == 0 && i != 0) {
                    patMmessage.append("\n").append(tempParm.getValue("PAT_NAME")).append(",");
                } else {
                    patMmessage.append(tempParm.getValue("PAT_NAME")).append(",");
                }
                continue;
            }
            // // ɾ��������ҽ����¼ ���ݾ����
            // result = MEDApplyTool.getInstance().deleteMedApply(result.getRow(0), conn);
            // // ʹʧЧ������ҽ����¼ ���ݾ����
            // result = MEDApplyTool.getInstance().disableMedApply(result.getRow(0), conn);
            // if (result.getErrCode() < 0) {
            // conn.close();
            // return result;
            // }
            // ��ѯ�û��ļ���ҽ��
            result = HRMOrderTool.getInstance().selectMedApplyOrder(tempParm);
            if (result.getErrCode() < 0) {
                return result;
            }
            List hl7ParmDel = new ArrayList();
            for (int j = 0; j < result.getCount(); j++) {//modify by wanglong 20130321
                TParm delTemp = new TParm();
                TParm tempDel = result.getRow(j);
                delTemp.setData("ADM_TYPE", "H");
                delTemp.setData("PAT_NAME", tempDel.getValue("PAT_NAME", j));
                delTemp.setData("CAT1_TYPE", tempDel.getValue("CAT1_TYPE"));
                delTemp.setData("CASE_NO", tempDel.getValue("CASE_NO"));
                delTemp.setData("LAB_NO", tempDel.getValue("APPLICATION_NO"));
                delTemp.setData("ORDER_NO", tempDel.getValue("ORDER_NO"));
                delTemp.setData("SEQ_NO", tempDel.getValue("SEQ_NO"));
                delTemp.setData("FLG", "1");
                try {
                    if (Hl7Communications.getInstance().IsExeOrder(delTemp, "H")) {
                        continue;
                    }
                }
                catch (Exception ex) {
                    System.err.print("�����ִ���ж�ʧ�ܡ�");
                    ex.printStackTrace();
                }
                hl7ParmDel.add(delTemp);
            }
            if (hl7ParmDel.size() > 0) {// modify by wanglong 20130509
                TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(hl7ParmDel);
                if (hl7Parm.getErrCode() < 0) {
                    // index++;
                    if (i % 5 == 0 && i != 0) {
                        hl7Mmessage.append("\n").append(tempParm.getValue("PAT_NAME")).append(",");
                    } else {
                        hl7Mmessage.append(tempParm.getValue("PAT_NAME")).append(",");
                    }
                    continue;
                }
            }
            // ɾ��ҽ��,���ݲ�����,��λ���
            result = HRMOrderTool.getInstance().deleteHrmOrder(tempParm, conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
            // ɾ������������Ϣ��¼,���ݲ�����,��λ���
            result = HRMPatadMTool.getInstance().deleteHrmPatadm(tempParm, conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
        }
        if (patMmessage.toString().length() > 0) {
            result.setData("PAT_MESSAGE", patMmessage);
        }
        if (hl7Mmessage.toString().length() > 0) {
            result.setData("HL7_MESSAGE", hl7Mmessage);
        }
        if (index == parm.getCount()) result.setData("INDEX_MESSAGE", "û����Ҫȡ��������");
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * �����־
     * 
     * @param logParm
     */
    public TParm writeLog(TParm logParm) {// add by wanglong 20130412
        System.out.println(logParm.getValue("LOG"));
        return new TParm();
    }
}
