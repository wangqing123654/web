package action.pha;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import jdo.pha.PHACHMTool;

import com.dongyang.action.TAction;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title:�ջ��в�ҩ���ݽ����ӿ�
 * </p>
 * 
 * <p>
 * Description:�ջ��в�ҩ���ݽ����ӿ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author wangb
 * @version 1.0
 */
public class PHACHMAction extends TAction {
	/**
	 * �����������
	 * 
	 * @return Connection ���Ӷ���
	 */
	public Connection getMDConnection() {
		Connection conn = null;
		String dbDriver = "org.firebirdsql.jdbc.FBDriver";
		TConfig prop = this.getProp();
		String dbPort = prop.getString("CHM.DB.Port");
		// System.out.println("CHM.DB.Port======================");
		// System.out.println(dbPort);
		String database = prop.getString("CHM.DB.DataBase");
		// System.out.println("CHM.DB.DataBase======================");
		// System.out.println(database);
		String userName = prop.getString("CHM.DB.UserName");
		// System.out.println("CHM.DB.UserName======================");
		// System.out.println(userName);
		String password = prop.getString("CHM.DB.Password");
		// System.out.println("CHM.DB.Password======================");
		// System.out.println(password);
		String address = prop.getString("CHM.DB.Address");
		// System.out.println("CHM.DB.Address======================");
		// System.out.println(address);
		// String url = "jdbc:firebirdsql:" + address + ":" + dbPort + "/"+
		// database;
		String url = "jdbc:firebirdsql:" + address + "/" + dbPort + ":"
				+ database;
		try {
			// System.out.println("driver=========================1");
			Class.forName(dbDriver);
			// System.out.println("driver=========================2");
//			System.out.println("url:"+url);
//			System.out.println("userName:"+userName);
//			System.out.println("password:"+password);
			conn = DriverManager.getConnection(url, userName, password);
//			System.out.println("conn:");
//			System.out.println(conn);
			// conn = DriverManager.getConnection(
			// "jdbc:firebirdsql:localhost/3050:D:/database/MDDB.FDB",
			// "SYSDBA", "masterkey");
			// jdbc:firebirdsql:localhost/3050:x:/database/myTest.fdb
			// "jdbc:firebirdsql:embedded:E:/Project/test.fdb";
			// System.out.println("conn======================");
			// System.out.println(conn);
			return conn;
		} catch (ClassNotFoundException cnf) {
			System.out.println("driver not find:" + cnf);
			return null;
		} catch (SQLException sqle) {
			System.out.println("can't connection db:" + sqle);
			return null;
		} catch (Exception e) {
			System.out.println("Failed to load JDBC/ODBC driver.");
			return null;
		}
	}

	/**
	 * ���в�ҩ�ӿڷ�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertIntoDataPrescription(TParm parm) {
		// System.out.println("action========================1");
		Connection conn = this.getMDConnection();
		// �в�ҩ�ӿ�_���ݲ���
		TParm result = PHACHMTool.getInstance().executeInsert(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}

		return result;
	}

	/**
	 * ��ȡ TConfig.x
	 *
	 * @return TConfig
	 */
	private TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		return config;
	}
}
