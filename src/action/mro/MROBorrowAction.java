package action.mro;

import org.apache.commons.lang.StringUtils;

import jdo.med.MedNodifyTool;
import jdo.mro.MROBorrowTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title:
 *
 * <p>Description: 
 *
 * <p>Copyright: ��������
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 
 * @version 4.0
 */
public class MROBorrowAction extends TAction{
	
	
	/**
	 * 
	 * @param parm
	 * @return
	 */
	public TParm query(TParm parm) {
		TParm result = MedNodifyTool.getInstance().query(parm);
		return result;
	}
	
	/**
	 * �����м��
	 * @param parm
	 * @return
	 */
	public TParm insertMroReg(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		int dataLen = parm.getCount();
		String mroRegNo = "";
		int seqNum = 1;
		
		// �ȴ���ʱ���ѯ��֤�������Ƿ����
		for (int i = 0; i < dataLen; i++) {
			result = MROBorrowTool.getInstance().queryMroRegAppointment(
					parm.getRow(i));

			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}

			// modify by wangb 2017/11/24
			// ����CRM�����Ļظ������ýӿ���ȡ��������ȫ��Ϊ��ЧԤԼ������ͬԤԼ�Ŵ��ڸ���ͬ���ظ�ʹ�õ����
			// ��ʱ����û�и�����������в������
			if (result.getCount() == 0) {
				// �ж�ԤԼ״̬(ԤԼ״̬ 0:δԤԼ,1����ԤԼ ,2������ԤԼ,3��ԤԼͣ��,4��ԤԼȷ��,5: ԤԼ����,6��������)
//				if ("1,4,5".contains(parm.getValue("STATUS", i))) {
					// �����ǰ�����Ǹ����˵�����ڲ��������Բ�����ʱ��
					if ("".equals(mroRegNo)) {
						// ȡ��ԭ��
						mroRegNo = SystemTool.getInstance().getNo("ALL", "MRO",
								"MRO_REGNO", "MRO_REGNO");
					}

					parm.setData("MRO_REGNO", i, mroRegNo);
					parm.setData("SEQ", i, seqNum);
					result = MROBorrowTool.getInstance().insertMroReg(
							parm.getRow(i), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrName() + " " + result.getErrText());
						connection.rollback();
						connection.close();
						return result;
					}
					seqNum = seqNum + 1;
//				}
			} else {
				// �����ʱ���д��ڸ����ݣ�����֤��ǰ�������Ƿ�ȡ��ԤԼ�����ȡ�������ȡ��ע��(��ɾ��)�����򲻽��в���
//				if ("0,2,3,6".contains(parm.getValue("STATUS", i))) {
					if (StringUtils.isEmpty(parm.getValue("CASE_NO", i))) {
						parm.setData("CASE_NO", i, null);
					}
	
					// add by wangb 2017/11/24
					// �����ʷ���д��ڸ�ԤԼ�ţ���֤�������Ƿ�һ�£�һ�µĲ�������
					// �����һ�£�������ԭ�в����ŵ�����ȡ������������ȡ�������ݲ������ݿ�
					if (!result.getValue("MR_NO", 0).equals(
							parm.getValue("MR_NO", i))) {
						result = MROBorrowTool
								.getInstance()
								.cancelMroRegAppointment(result.getRow(0), connection);
						if (result.getErrCode() < 0) {
							err(result.getErrName() + " " + result.getErrText());
							connection.rollback();
							connection.close();
							return result;
						}
						
						// �����ǰ�����Ǹ����˵�����ڲ��������Բ�����ʱ��
						if ("".equals(mroRegNo)) {
							// ȡ��ԭ��
							mroRegNo = SystemTool.getInstance().getNo("ALL", "MRO",
									"MRO_REGNO", "MRO_REGNO");
						}

						parm.setData("MRO_REGNO", i, mroRegNo);
						parm.setData("SEQ", i, seqNum);
						parm.setData("CASE_NO", i, "");
						result = MROBorrowTool.getInstance().insertMroReg(
								parm.getRow(i), connection);
						if (result.getErrCode() < 0) {
							err(result.getErrName() + " " + result.getErrText());
							connection.rollback();
							connection.close();
							return result;
						}
						seqNum = seqNum + 1;
						
					}
//				}
			}
		}
		
		connection.commit();
		connection.close();

		return result;
	}

	/**
	 * ��ȷ�Ͻ��ĵĲ������뵽���ı���
	 * @param parm
	 * @return
	 */
	public TParm  insertQueue(TParm parm){
		  TConnection connection = getConnection();
		  TParm result = new TParm();
	        result = MROBorrowTool.getInstance().insertQueue(parm, connection) ;
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        return result;
	}

	/**
	 * ���ĵĳ�����⣬�����ݲ��뵽��ʷ��¼����
	 * @param parm
	 * @return
	 */
	public TParm  insertTRANHIS(TParm parm){
		  TConnection connection = getConnection();
		  TParm result = new TParm();
	        result = MROBorrowTool.getInstance().insertTRANHIS(parm, connection) ;
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        return result;
	}
	
	/**
	 * ����ϲ���ɾ����������
	 * 
	 * @param parm
	 * @return ������Ϣ
	 * @author wangbin 2014/09/01
	 */
	public TParm deleteMroMrv(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = MROBorrowTool.getInstance().deleteMroMrv(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
}
