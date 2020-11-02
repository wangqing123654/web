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
 * <p>Copyright: 病案借阅
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
	 * 新增中间表
	 * @param parm
	 * @return
	 */
	public TParm insertMroReg(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		int dataLen = parm.getCount();
		String mroRegNo = "";
		int seqNum = 1;
		
		// 先从临时表查询验证该数据是否存在
		for (int i = 0; i < dataLen; i++) {
			result = MROBorrowTool.getInstance().queryMroRegAppointment(
					parm.getRow(i));

			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}

			// modify by wangb 2017/11/24
			// 根据CRM给出的回复，调用接口提取到的数据全部为有效预约，且相同预约号存在给不同人重复使用的情况
			// 临时表中没有该条数据则进行插入操作
			if (result.getCount() == 0) {
				// 判断预约状态(预约状态 0:未预约,1：已预约 ,2：不可预约,3：预约停诊,4：预约确认,5: 预约报到,6：不出诊)
//				if ("1,4,5".contains(parm.getValue("STATUS", i))) {
					// 如果当前病患是复诊，这说明存在病历，可以插入临时表
					if ("".equals(mroRegNo)) {
						// 取号原则
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
				// 如果临时表中存在该数据，则验证当前该数据是否取消预约，如果取消则更新取消注记(假删除)，否则不进行操作
//				if ("0,2,3,6".contains(parm.getValue("STATUS", i))) {
					if (StringUtils.isEmpty(parm.getValue("CASE_NO", i))) {
						parm.setData("CASE_NO", i, null);
					}
	
					// add by wangb 2017/11/24
					// 如果历史表中存在该预约号，验证病案号是否一致，一致的不做处理
					// 如果不一致，将库里原有病案号的数据取消，将本次提取到的数据插入数据库
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
						
						// 如果当前病患是复诊，这说明存在病历，可以插入临时表
						if ("".equals(mroRegNo)) {
							// 取号原则
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
	 * 将确认借阅的病历插入到借阅表中
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
	 * 借阅的出库入库，将数据插入到历史记录表中
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
	 * 案卷合并后删除多余数据
	 * 
	 * @param parm
	 * @return 案卷信息
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
