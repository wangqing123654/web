package jdo.sys;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import action.reg.REGCRMAction;

import com.dongyang.config.TConfigParm;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;


public class SYSUnifiedCodeTask {
	private String configDir = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private TConnection connection = null;
	
	ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
	@SuppressWarnings("unchecked")
	ScheduledFuture taskHandle = null;

	public SYSUnifiedCodeTask() {

	}

	public SYSUnifiedCodeTask(String configDir) {
		this.configDir = configDir;
	}

	public TConnection getTConnection() {

		if (connection == null) {
			TDBPoolManager.getInstance().init(
					configDir + "WEB-INF\\config\\system\\");
			TConfigParm parm1 = new TConfigParm();
			parm1.setSystemGroup("");
			if (!configDir.endsWith("\\"))
				configDir += "\\";
			configDir += "WEB-INF\\config\\";
			String dir = configDir + "system\\";
			parm1.setConfig(dir + "TDBInfo.x");
			parm1.setConfigClass(dir + "TClass.x");
			TDBPoolManager m = new TDBPoolManager();
			m.init(parm1);
			connection = m.getConnection("javahis");
		}
		return connection;
	}

	/**
	 * 定时扫描SYS_IO_LOG表
	 */
	public void doJob() {
		taskHandle = exec.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
						String sql="SELECT IO_CODE,PASSWORD FROM SYS_IO_INF WHERE IO_CODE = 'CRM'";
						TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
						String[] info = SYSDictionaryServiceTool.getInstance().getModifyTable("CRM", parm.getValue("PASSWORD", 0), "NEW");
	
						List<String> tableName = new ArrayList<String>();						
						tableName.add("SYS_IDTYPE");
						tableName.add("SYS_SEX");
						tableName.add("SYS_NATION");
						tableName.add("SYS_SPECIES");
						tableName.add("SYS_RELIGION");
						tableName.add("SYS_MARRIAGE");
						tableName.add("SYS_RELATIONSHIP");
						tableName.add("SYS_CTZ");
						tableName.add("OPERATOR_INF");
						tableName.add("MEM_MEMBERSHIP_INFO");	
						tableName.add("SYS_POSTCODE");
						tableName.add("SYS_HOMEPLACE");
						tableName.add("SYS_SPECIALDIET");
						tableName.add("EKT_ISSUERSN");
						tableName.add("DEPT_INF");
						tableName.add("OPERATOR_DEPT_INF");
						tableName.add("SYS_POSITION");
						int count = info.length;
						for (int i = 0; i < info.length; i++) {
							if(!tableName.contains(info[i])){
								String sqlDel = "DELETE FROM SYS_IO_LOG WHERE IO_CODE='CRM' " +
										"AND STATUS='NEW' AND  TABLE_NAME='"+info[i]+"'";
								TParm parmDel = new TParm(TJDODBTool.getInstance().update(sqlDel));
								count--;
							}
								
						}
//						System.out.println("aaaaaaaaaa=="+info.length);
//						System.out.println("aaaaaaaaaa==count===="+count);
						if(count>0){
							REGCRMAction action = new REGCRMAction();
							TParm order = action.updateCode();
							System.out.println("order=返回结果=="+order.getBoolean("flg"));
						
							
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 10 * 1000, 60 * 60 * 1000, TimeUnit.MILLISECONDS);
//		}, 10 * 1000, 60 * 1000, TimeUnit.MILLISECONDS);
	}

	public void close() {
		exec.schedule(new Runnable() {
			public void run() {
				System.out.println("取消任务");
				taskHandle.cancel(true);
			}
		}, 60, TimeUnit.SECONDS);
	}
}
