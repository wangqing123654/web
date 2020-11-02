package action.reg;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.reg.REGCcbSchtool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_AppServer;
import action.ccb.CBCClientAction;

/**
 * <p>
 * Title: ҽ��ͨ��Ŀ���Ű�
 * </p>
 * 
 * <p>
 * Description:ҽ��ͨ��Ŀ���Ű�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:BuleCore
 * </p>
 * 
 * @author zhangp 20120901
 * @version 1.0
 */
public class REGCcbSchAction extends TAction {

	/**
	 * �ϴ�ԤԼ�Һ��ļ�
	 * 
	 * @param parm1
	 * @return
	 */
	public TParm upload(TParm parm1) {
		TParm resultData = new TParm();
		SimpleDateFormat sf = new SimpleDateFormat("HHmmss");
		String dataStr = sf.format(new Date());
		TParm qTparm = REGCcbSchtool.getInstance().query(dataStr);
		if (qTparm.getValue("BOOL").equals("Y")) {
			TSocket socket = new TSocket("127.0.0.1", 8080, "web");
			TParm parm = new TParm();
			Timestamp today = SystemTool.getInstance().getDate();
			String date = today.toString();
			String moth = date.substring(5, 7);
			String day = date.substring(8, 10);
			date = date.substring(0, 4) + "-" + moth + "-" + day;
			parm.setData("HIS_CODE", "000551");
			parm.setData("FTP_DATE", date);
			parm.setData("SEQ_NO", dataStr);
			parm.setData("FILE_TYPE", 4);
			String date2 = today.toString();
			date2 = date2.substring(0, 4) + date2.substring(5, 7)
					+ date2.substring(8, 10);
			parm.setData("FILE_NAME", "DoctorRegToday_000551_" + date2 + "_"
					+ dataStr);
			resultData = TIOM_AppServer.executeAction(socket,
					"action.ccb.CBCClientAction", "SendFileBtoB", parm);
		} else {
		}
		return resultData;
	}

	/**
	 * �ϴ����չҺ��ļ�
	 * 
	 * @param parm1
	 * @return
	 */
	public TParm upload2(TParm parm1) {
		TParm resultData = new TParm();
		SimpleDateFormat sf = new SimpleDateFormat("HHmmss");
		String dataStr = sf.format(new Date());
		TParm qTparm = REGCcbSchtool.getInstance().queryByDate(dataStr);
		if (qTparm.getValue("BOOL").equals("Y")) {
			TSocket socket = new TSocket("127.0.0.1", 8080, "web");
			TParm parm = new TParm();
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			now.set(Calendar.DATE, now.get(Calendar.DATE) + 1);
			Date today = now.getTime();
			SimpleDateFormat dateSf = new SimpleDateFormat("yyyy-MM-dd");
			String date = dateSf.format(today);
			parm.setData("HIS_CODE", "000551");
			parm.setData("FTP_DATE", date);
			parm.setData("SEQ_NO", dataStr);
			parm.setData("FILE_TYPE", 3);
			Date fDate = new Date();
			SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMdd");
			parm.setData("FILE_NAME", "DoctorReg_000551_" + sf1.format(fDate)
					+ "_" + dataStr);
			resultData = TIOM_AppServer.executeAction(socket,
					"action.ccb.CBCClientAction", "SendFileBtoB", parm);
		} else {
		}
		return resultData;
	}

	/**
	 * �ϴ������ļ�
	 * 
	 * @param parm1
	 * @return
	 */
	public TParm uploadDept(TParm parm1) {
		TParm resultData = new TParm();
		SimpleDateFormat sf = new SimpleDateFormat("HHmmss");
		String dataStr = sf.format(new Date());
		TParm qTparm = REGCcbSchtool.getInstance().queryDept(dataStr);
		if (qTparm.getValue("BOOL").equals("Y")) {
			TSocket socket = new TSocket("127.0.0.1", 8080, "web");
			TParm parm = new TParm();
			Timestamp today = SystemTool.getInstance().getDate();
			String date = today.toString();
			String moth = date.substring(5, 7);
			String day = date.substring(8, 10);
			date = date.substring(0, 4) + "-" + moth + "-" + day;
			parm.setData("HIS_CODE", "000551");
			parm.setData("FTP_DATE", date);
			parm.setData("SEQ_NO", dataStr);
			parm.setData("FILE_TYPE", 1);
			String date2 = today.toString();
			date2 = date2.substring(0, 4) + date2.substring(5, 7)
					+ date2.substring(8, 10);
			parm.setData("FILE_NAME", "Item_000551_" + date2 + "_" + dataStr);
			resultData = TIOM_AppServer.executeAction(socket,
					"action.ccb.CBCClientAction", "SendFileBtoB", parm);
		} else {
		}
		return resultData;
	}

	/**
	 * �ϴ�ҽʦ�ļ�
	 * 
	 * @param parm1
	 * @return
	 */
	public TParm uploadDr(TParm parm1) {
		TParm resultData = new TParm();
		SimpleDateFormat sf = new SimpleDateFormat("HHmmss");
		String dataStr = sf.format(new Date());
		TParm qTparm = REGCcbSchtool.getInstance().queryDr(dataStr);
		if (qTparm.getValue("BOOL").equals("Y")) {
			TSocket socket = new TSocket("127.0.0.1", 8080, "web");
			TParm parm = new TParm();
			Timestamp today = SystemTool.getInstance().getDate();
			String date = today.toString();
			String moth = date.substring(5, 7);
			String day = date.substring(8, 10);
			date = date.substring(0, 4) + "-" + moth + "-" + day;
			parm.setData("HIS_CODE", "000551");
			parm.setData("FTP_DATE", date);
			parm.setData("SEQ_NO", dataStr);
			parm.setData("FILE_TYPE", 2);
			String date2 = today.toString();
			date2 = date2.substring(0, 4) + date2.substring(5, 7)
					+ date2.substring(8, 10);
			parm.setData("FILE_NAME", "Doctor_000551_" + date2 + "_" + dataStr);
			resultData = TIOM_AppServer.executeAction(socket,
					"action.ccb.CBCClientAction", "SendFileBtoB", parm);
		} else {
		}
		return resultData;
	}

}
