package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 公告栏数据层实现类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011-04-21</p>
 *
 * <p>Company: </p>
 *
 * @author li.xiang790130@gmail.com
 * @version 1.0
 */
public class SYSPublishBoardTool
extends TJDOTool {
	private static final SimpleDateFormat ORA_DATE_FORMAT = new
			SimpleDateFormat(
					"yyyyMMdd");
	/**
	 * 实例
	 */
	private static SYSPublishBoardTool instanceObject;
	/**
	 * 得到实例
	 * @return SYSPublishBoardTool
	 */
	public static SYSPublishBoardTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new SYSPublishBoardTool();
		}
		return instanceObject;
	}

	private SYSPublishBoardTool() {
		//加载Module文件
		setModuleName("sys\\SYSPublishBoardModule.x");
		onInit();
	}

	/**
	 * 获得收件人
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm getReceiveUsers(TParm parm) {
		parm.setData("CURRENT_DATE", ORA_DATE_FORMAT.format(new Date()));
		TParm result = new TParm();
		String receiveType = (String) parm.getData("RECEIVE_TYPE");
		//System.out.println("===receiveType===" + receiveType);
		//个人
		if (receiveType.equals("P")) {
			// modified by wangqing 20171113 start
			//            result = this.query("queryByUserID", parm);
			String sql = " SELECT USER_ID,ROLE_ID,E_MAIL "
					+ " FROM SYS_OPERATOR WHERE USER_ID IN "+parm.getData("RECIPIENTS")
					+ " AND REGION_CODE='"+parm.getData("REGION")
					+"' AND ACTIVE_DATE<=SYSDATE and END_DATE>=SYSDATE ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			System.out.println("======sql="+sql);
			// modified by wangqing 20171113 end
		}
		//部门
		else if (receiveType.equals("D")) {
			// modified by wangqing 20171113 start
			//          result = this.query("queryByDept", parm);
			String sql = "SELECT distinct a.USER_ID,a.ROLE_ID,E_MAIL "
					+ " FROM SYS_OPERATOR a LEFT JOIN SYS_OPERATOR_DEPT b on a.USER_ID=b.USER_ID "
					+ " WHERE DEPT_CODE IN "+parm.getData("RECIPIENTS")
					+" and REGION_CODE='"+parm.getData("REGION")
					+"' and ACTIVE_DATE<=SYSDATE and END_DATE>=SYSDATE ";
			
				System.out.println("3333333333sql="+sql);
			
			result = new TParm(TJDODBTool.getInstance().select(sql));
			// modified by wangqing 20171113 end
		}
		//角色
		else if (receiveType.equals("R")) {
			// modified by wangqing 20171113 start
			//          result = this.query("queryByRole", parm);
			String sql = "SELECT USER_ID,ROLE_ID,E_MAIL "
					+ " FROM SYS_OPERATOR WHERE ROLE_ID IN "+parm.getData("RECIPIENTS")
					+" and REGION_CODE='"+parm.getData("REGION")
					+"' and ACTIVE_DATE<=SYSDATE and END_DATE>=SYSDATE ";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			// modified by wangqing 20171113 end
		}
		//所有
		else if (receiveType.equals("A")) {
			result = this.query("queryAll", parm);
		}

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
			+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * 接收公告总数
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm getReceiveMessageCount(TParm parm) {
		String chkHistoryFlag = (String) parm.getData("chkHistoryFlag");
		String optUser = (String) parm.getData("OPT_USER");
		String oldSqlStr = "SELECT count(*)";
		oldSqlStr +=
				" FROM SYS_POSTRCV a LEFT JOIN SYS_BOARD b ON a.MESSAGE_NO=b.MESSAGE_NO";
		oldSqlStr += " LEFT JOIN SYS_OPERATOR c on b.POST_ID=c.USER_ID";
		oldSqlStr += " WHERE a.USER_ID='" + optUser + "'";
		
		// add by wangqing 201712214
		oldSqlStr += " AND a.MESSAGE_NO IS NOT NULL AND b.MESSAGE_NO IS NOT NULL ";
		
		
		//不显示历史记
		if (!chkHistoryFlag.equalsIgnoreCase("Y")) {
			oldSqlStr += " AND READ_FLG='N'";
		}		
		System.out.println("======== sql ====="+oldSqlStr);
		TParm result = new TParm(this.getDBTool().select(oldSqlStr));
		return result;
	}


	/**
	 * 获得接收消息分页列有；
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm getReceiveMessageList(TParm parm) {
		//是否显示历资料
		String chkHistoryFlag = (String) parm.getData("chkHistoryFlag");
		int startIndex = (int) parm.getInt("startIndex");
		int endIndex = (int) parm.getInt("endIndex");
		String optUser = (String) parm.getData("OPT_USER");
		// modified by wangqing 20171114 新增START_READ_TIME字段
		String oldSqlStr = "SELECT TO_CHAR(a.START_READ_TIME, 'yyyy/MM/dd HH24:MI:SS') AS START_READ_TIME, a.READ_FLG,(CASE WHEN b.URG_FLG='Y' THEN '!' WHEN b.URG_FLG='N' THEN '' END) as URG_FLG ,b.POST_TIME,b.POST_SUBJECT,c.USER_NAME,a.MESSAGE_NO,a.USER_ID";
		oldSqlStr +=
				" FROM SYS_POSTRCV a LEFT JOIN SYS_BOARD b ON a.MESSAGE_NO=b.MESSAGE_NO";
		oldSqlStr += " LEFT JOIN SYS_OPERATOR c on b.POST_ID=c.USER_ID";
		oldSqlStr += " WHERE a.USER_ID='" + optUser + "'";
		
		// add by wangqing 201712214
		oldSqlStr += " AND a.MESSAGE_NO IS NOT NULL AND b.MESSAGE_NO IS NOT NULL ";
		
		//不显示历史记
		if (!chkHistoryFlag.equalsIgnoreCase("Y")) {
			oldSqlStr += " AND READ_FLG='N'";

		}
		oldSqlStr += " ORDER BY READ_FLG,b.POST_TIME DESC";
		
		System.out.println("oldSqlStr="+oldSqlStr);
		
		//oracle 分页
		int low = startIndex + 1;
		int up = startIndex + endIndex;

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM(");
		sb.append("SELECT A.*, ROWNUM RN");
		sb.append(" FROM (");
		sb.append(oldSqlStr);
		sb.append(" ) A)");
		sb.append("WHERE RN BETWEEN ");
		sb.append(low);
		sb.append(" AND ");
		sb.append(up);
		//System.out.println("getReceiveMessageList sql======="+sb.toString());

		TParm result = new TParm(this.getDBTool().select(sb.toString()));
		return result;
	}

	/**
	 * 发送公告消息
	 * @param parm TParm
	 * @param conn TConnection
	 * @return TParm
	 */
	public TParm sendPublishMessage(TParm parm, TConnection conn) {
		TParm result = new TParm();
		//1.取得公告流水号；
		String messageNo = this.getMessageNo();
		parm.setData("MESSAGE_NO", messageNo);
		//System.out.println("====messageNo====="+messageNo);
		TParm receiveUsers = (TParm) parm.getData("RECEIVE_USERS");
		//System.out.println("user count++++++"+receiveUsers);
		//2.插入接收档；
		if (receiveUsers != null && receiveUsers.getCount() > 0) {
			TParm insertReceiveParm = new TParm();
			insertReceiveParm.setData("MESSAGE_NO", messageNo);
			insertReceiveParm.setData("POST_TYPE", parm.getData("RECEIVE_TYPE"));
			insertReceiveParm.setData("READ_FLG", "N");
			insertReceiveParm.setData("OPT_USER", parm.getData("OPT_USER"));
			insertReceiveParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			insertReceiveParm.setData("OPT_TERM", parm.getData("OPT_TERM"));

			for (int i = 0; i < receiveUsers.getCount(); i++) {
				insertReceiveParm.setData("USER_ID",
						receiveUsers.getData("USER_ID", i));
				if (receiveUsers.getData("ROLE_ID", i) == null) {
					insertReceiveParm.setData("POST_GROUP", "");
				}
				else {
					insertReceiveParm.setData("POST_GROUP",
							receiveUsers.getData("ROLE_ID", i));
				}
				//
				result = this.update("inserPostRCV", insertReceiveParm, conn);
				//插入接收档操作是否失败；
				if (result.getErrCode() < 0) {
					err(result.getErrName() + " " +
							result.getErrText());
					return result;
				}

			}
		}
		//3.插入公告档；
		result = this.update("inserBoard", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 删除公告
	 * @param parm TParm
	 * @param conn TConnection
	 * @return TParm
	 */
	public TParm deletePublishMessage(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = this.update("deletePostRCV", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}

		result = this.update("deleteBoard", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 批量删除公告
	 * @param parm TParm
	 * @param connection TConnection
	 * @return TParm
	 */
	public TParm batchDeletePublishMessage(TParm parm,
			TConnection conn) {
		final String delMessageNos = parm.getValue("DEL_MESSAGE_NOS");

		TParm result = new TParm();
		//1.删除接收档；
		String delPostRCVSQL = "DELETE FROM SYS_POSTRCV WHERE MESSAGE_NO IN (" +
				delMessageNos + ")";
		result = new TParm(this.getDBTool().update(delPostRCVSQL, conn));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		//2.删除公告档；
		String delBoardSQL = "DELETE FROM SYS_BOARD WHERE MESSAGE_NO IN (" +
				delMessageNos + ")";
		result = new TParm(this.getDBTool().update(delBoardSQL, conn));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 删除接收档消息
	 * @return TParm
	 */
	public TParm deleteReceiveMessage(TParm parm) {
		TParm result = new TParm();
		result = this.update("deleteRCVByUserIDMessNo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 存档，更新公告内容;
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm updateMessageByMessageNo(TParm parm) {
		TParm result = new TParm();
		result = this.update("updateBoardByMessageNo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 读公告详细内容；
	 * @param parm TParm
	 * @param conn TConnection
	 * @return TParm
	 */
	public TParm readMessage(TParm parm, TConnection conn) {
		TParm result = new TParm();
		//取主题,内容,响应数；
		result = this.query("getBoardByMessageNo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		//System.out.println("RESPONSE_NO==="+result.getLong("RESPONSE_NO",0));
		//String.valueOf(Long.valueOf((String)result.getData("RESPONSE_NO",0))+1)
		parm.setData("RESPONSE_NO", result.getLong("RESPONSE_NO", 0) + 1);

		//更新响应数加1，并更改为已读;
		TParm updateRead = this.update("updateReadFlag", parm, conn);
		if (updateRead.getErrCode() < 0) {
			err(updateRead.getErrName() + " " + updateRead.getErrText());
			return updateRead;
		}

		//只要是读过，更新响应数加1，并更改为已读;
		TParm updateRespose = this.update("updateResposeNo", parm, conn);
		if (updateRespose.getErrCode() < 0) {
			err(updateRespose.getErrName() + " " + updateRespose.getErrText());
			return updateRespose;
		}

		return result;
	}


	/**
	 * 取得流水号
	 * @return String
	 */
	private synchronized String getMessageNo() {
		String messageNo = "";
		/**String currentDate = ORA_DATE_FORMAT.format(new Date());
                 //取最流水号加1
                 StringBuffer bf = new StringBuffer();
                 bf.append(
            "select (to_number(MESSAGE_NO)+1) as messageNo from SYS_BOARD");
                 bf.append(" where MESSAGE_NO=(Select Max(MESSAGE_NO) from SYS_BOARD WHERE Substr(MESSAGE_NO,1,8)='" +
                  currentDate + "')");
         TParm query = new TParm(this.getDBTool().select(bf.toString()));
                 Object objMessageNo = query.getData(0, 0);
                 if (objMessageNo == null) {
            messageNo = currentDate + "0001";
                 }
                 else {
            messageNo = objMessageNo.toString();
                 }**/
		//利用取号器取值；
		messageNo = SystemTool.getInstance().getNo("ALL", "PUB", "MESSAGE_NO",
				"MESSAGE_NO");

		return messageNo;
	}

	/**
	 * 返回数据库操作工具
	 * @return TJDODBTool
	 */
	private TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 *
	 * @param parm TParm
	 * @param conn TConnection
	 * @return TParm
	 */
	public TParm insertPostRCV(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = this.update("inserPostRCV", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 *
	 * @param parm TParm
	 * @param conn TConnection
	 * @return TParm
	 */
	public TParm insertBoard(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = this.update("inserBoard", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询某人的接收数据 add by wangqing 20171114
	 * @param parm
	 * @return
	 */
	public TParm selectReceiveData(TParm parm){
		TParm result = this.query("selectReceiveData", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

}
