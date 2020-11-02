package jdo.dev;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title:��������Ϣ��ѯ������</p>
 *
 * <p>Description:��������Ϣ��ѯ������ </p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company:bluecore </p>
 * 
 * @author wukai
 * @version 4.5
 */
public class DEVChildAppointInfoTool extends TJDOTool {
	
	private static DEVChildAppointInfoTool mInstance;
	
	public DEVChildAppointInfoTool () {
		setModuleName("dev\\DevChildAppointInfoModule.x");
		onInit();
	}
	
	public static DEVChildAppointInfoTool getNewInstance() {
		if(mInstance == null) {
			mInstance = new DEVChildAppointInfoTool();
		}
		return mInstance;
	}
	
	public TParm onQueryAll() {
		//System.out.println("=========onQueryAll Begin=========");
		TParm allParm = query("queryAll");
		//System.out.println("**AllParm : " + allParm);
		//System.out.println("=========onQueryAll Begin=========");
		return allParm;
	}
	
}
