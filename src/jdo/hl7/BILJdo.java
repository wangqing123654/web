package jdo.hl7;

import java.sql.Timestamp;

import jdo.adm.ADMInpTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.ibs.IBSTool;
import jdo.inw.InwOrderExecTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.exception.HL7Exception;

/**
 * <p>
 * Title: ҽ���Ʒѹ�����
 * </p>
 * 
 * <p>
 * Description:ҽ���Ʒѹ�����
 * </p>
 * 
 * <p>
 * Copyright: BLUECORE
 * </p>
 * 
 * <p>
 * Company:BLUECORE
 * </p>
 * 
 * @author SHIBL
 * @version 1.0
 */
public class BILJdo extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static BILJdo instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return JDO
	 */
	public static BILJdo getInstance() {
		if (instanceObject == null)
			instanceObject = new BILJdo();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public BILJdo() {
		onInit();
	}

	/**
	 * סԺҽ���ƷѲ���
	 * 
	 * @param conn
	 * @param parm
	 * @param Cat1Type
	 * @param flg
	 * @return
	 */
	public TParm onIBilOperate(TConnection conn, TParm parm, String Cat1Type,
			String flg)throws HL7Exception{
		TParm result = new TParm();
		String applyNo = "";
		if (Cat1Type.equals("LIS")) {
			applyNo = parm.getValue("LAB_NUMBER");
		} else {
			applyNo = parm.getValue("MED_APPLY_NO");
		}
		TParm dataparm = Hl7Tool.getInstance().getOdiOrder(applyNo, Cat1Type,flg);
		if (dataparm.getCount() <= 0) {
			result.setErr(-2, "û�в�ѯ��סԺִ�еļƷ�ҽ��");
			return result;
		}
		result = onIBilFee(conn, dataparm, flg);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * סԺҽ���Ʒ�
	 * 
	 * @param conn
	 * @param parm
	 * @return
	 */
	public TParm onIBilFee(TConnection conn, TParm dataparm, String flg){
		TParm result = new TParm();
		String SelSql = "SELECT CTZ1_CODE,CTZ2_CODE,CTZ3_CODE "
				+ " FROM ADM_INP WHERE CASE_NO='"
				+ dataparm.getValue("CASE_NO", 0) + "'";
		// �õ��ò������и�ִ��չ���Ĵ���
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(SelSql));
		if (dataparm.getCount() <= 0) {
			result.setErr(-3, "û�в�ѯ���Ʒ����");
			return result;
		}
		// ����IBS�ӿڷ��ط�������
		TParm forIBSParm = new TParm();
		forIBSParm.setData("M", dataparm.getData());
		forIBSParm.setData("CTZ1_CODE", ctzParm.getData("CTZ1_CODE", 0));
		forIBSParm.setData("CTZ2_CODE", ctzParm.getData("CTZ2_CODE", 0));
		forIBSParm.setData("CTZ3_CODE", ctzParm.getData("CTZ3_CODE", 0));
		forIBSParm.setData("FLG", flg);
		TParm resultFromIBS = IBSTool.getInstance().getIBSOrderData(forIBSParm);
		if (resultFromIBS.getErrCode() < 0) {
			System.out.println(resultFromIBS.getErrText());
			conn.rollback();
			return resultFromIBS;
		}
		if (resultFromIBS.getCount() <= 0) {
			result.setErr(-4, "�Ʒ�ҽ�����ʧ��");
			return result;
		}
		TParm backWriteOdiParm = new TParm();
		Timestamp now = SystemTool.getInstance().getDate();
		// �ǵ�����
		int count = resultFromIBS.getCount("ORDER_DATE");
		int rows = 0;
		for (int i = 0; i < count; i++) {
			backWriteOdiParm.addData("CASE_NO", resultFromIBS.getData(
					"CASE_NO", i));
			backWriteOdiParm.addData("ORDER_NO", resultFromIBS.getData(
					"ORDER_NO", i));
			backWriteOdiParm.addData("ORDER_SEQ", resultFromIBS.getData(
					"ORDER_SEQ", i));
			backWriteOdiParm.addData("START_DTTM", resultFromIBS.getData(
					"START_DTTM", i));
			backWriteOdiParm.addData("END_DTTM", resultFromIBS.getData(
					"END_DTTM", i));
			backWriteOdiParm.addData("OPT_DATE", now);
			backWriteOdiParm.addData("OPT_USER", "MEDWEBSERVICE");
			backWriteOdiParm.addData("OPT_TERM", "127.0.0.1");
			// ������Ա���ǼƷ���Ա M/D�����ֶ�
			backWriteOdiParm.addData("CASHIER_CODE", "MEDWEBSERVICE");
			backWriteOdiParm.addData("CASHIER_USER", "MEDWEBSERVICE");
			backWriteOdiParm.addData("CASHIER_DATE", now);
			backWriteOdiParm.addData("BILL_FLG", resultFromIBS.getData(
					"BILL_FLG", i));
			backWriteOdiParm.addData("IBS_CASE_NO_SEQ", resultFromIBS.getData(
					"CASE_NO_SEQ", i));
			// M/D�����ֶ�
			backWriteOdiParm.addData("IBS_SEQ_NO", resultFromIBS.getData(
					"SEQ_NO", i));
			backWriteOdiParm.addData("IBS_CASE_NO", resultFromIBS.getData(
					"SEQ_NO", i));
			//��ֵOPT_USER
			resultFromIBS.setData("OPT_USER", i, "MEDWEBSERVICE");
			rows++;
		}
		backWriteOdiParm.setCount(rows);
		// ��дODI��M/D����
		result = InwOrderExecTool.getInstance().updateOdiDspndByIBS(
				backWriteOdiParm, conn);
		if (result.getErrCode() < 0) {
			result.setErr(-5, "�Ʒ�ҽ������ODI_DSPND��ʧ��");
			conn.rollback();
			return result;
		}
		result = InwOrderExecTool.getInstance().updateOdiDspnmByIBS(
				backWriteOdiParm, conn);
		if (result.getErrCode() < 0) {
			result.setErr(-5, "�Ʒ�ҽ������ODI_DSPNM��ʧ��");
			conn.rollback();
			return result;
		}
		 TParm lumpParm=new TParm();
         lumpParm.setData("CASE_NO",dataparm.getValue("CASE_NO", 0));
         result=ADMInpTool.getInstance().selectall(lumpParm);//�ײͲ���ҽ�������������ҽ���˷���Ҫ�ֶ��˷�
         if (null!=result.getValue("LUMPWORK_CODE",0)&&result.getValue("LUMPWORK_CODE",0).length()>0) {
         	 if(null!=dataparm.getValue("MED_APPLY_LUMP_FLG")&&dataparm.getValue("MED_APPLY_LUMP_FLG").equals("Y")){
         		 return result;
         	 }
		}
		// �����̨ʹ�õ�����
		TParm forIBSParm2 = new TParm();
		forIBSParm2.setData("DATA_TYPE", "6"); // ����webserviceҽ�����ñ��
		forIBSParm2.setData("M", resultFromIBS.getData());
		forIBSParm2.setData("FLG", flg);
		// ����IBS�ṩ��Tool����ִ��
		result = IBSTool.getInstance().insertIBSOrder(forIBSParm2, conn);
		if (result.getErrCode() < 0) {
			result.setErr(-5, "�Ʒ�ҽ������Ʒѵ�ʧ��");
			conn.rollback();
			return result;
		}
		return result;
	}

	/**
	 * �ż�������ж�
	 * 
	 * @param parm
	 * @param Cat1Type
	 * @return
	 */
	public TParm onOEBilCheck(TParm parm, String Cat1Type) {
		TParm result = new TParm();
		String applyNo = "";
		if (Cat1Type.equals("LIS")) {
			applyNo = parm.getValue("LAB_NUMBER");
		} else {
			applyNo = parm.getValue("MED_APPLY_NO");
		}
		TParm dataparm = Hl7Tool.getInstance().getOpdOrder(applyNo, Cat1Type);
		if (dataparm.getCount() <= 0) {
			result.setErr(-2, "û�в�ѯ���Ʒ�ҽ��");
			return result;
		}
		//����������
		if (EKTIO.getInstance().ektAyhSwitch()) {
			dataparm.setData("CASE_NO", dataparm.getValue("CASE_NO", 0));
			dataparm.setData("MR_NO", dataparm.getValue("MR_NO", 0));
			result = EKTpreDebtTool.getInstance().checkMasterForExe(dataparm);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}
}
