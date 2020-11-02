package jdo.adm.ws;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * @author shibl
 *
 */
public class AdmServiceTool extends TJDOTool{
	/**
	 * 实例
	 */
	public static AdmServiceTool instanceObject;
	
	private static final String sql=" SELECT D.PAT_NAME,E.BED_NO_DESC,C.CONTACTS_TEL,F.CHN_DESC SEX,A.DS_DATE "+
	" FROM ADM_INP A,ADM_INP B,SYS_PATINFO C,SYS_PATINFO D,SYS_BED E,SYS_DICTIONARY F "+
	" WHERE A.M_CASE_NO=B.CASE_NO(+) "+
	" AND A.MR_NO=C.MR_NO(+) "+
	" AND A.BED_NO=E.BED_NO(+) "+
	" AND B.MR_NO=D.MR_NO "+
	" AND A.CANCEL_FLG<>'Y' "+
	" AND F.GROUP_ID='SYS_SEX' "+
	" AND C.SEX_CODE=F.ID "+
	" AND A.MR_NO='<MR_NO>' "+
	" ORDER BY A.IN_DATE DESC ";

	/**
	 * 得到实例
	 * 
	 * @return JDO
	 */
	public static AdmServiceTool getInstance() {
		if (instanceObject == null)
			instanceObject = new AdmServiceTool();
		return instanceObject;
	}
	/**
	 * 构造器
	 */
	public AdmServiceTool() {
        onInit();	
	}
	/**
	 * 
	 * @param mrNo
	 * @return
	 */
	public TParm getAdmPatInfo(String mrNo){
		TParm parm=new TParm();
		TParm inParm=new TParm();
		inParm.setData("MR_NO", mrNo);
		String selsql=buildSQL(this.sql,inParm);
		TParm result=new TParm(TJDODBTool.getInstance().select(selsql));
		if(result.getCount()<=0){
			parm.setErr(-1, "未查询此病案号数据");
			return parm;
		}
		if(!result.getValue("DS_DATE", 0).equals("")){
			parm.setErr(-1, "此病案号病患已出院");
			return parm;
		}
		return result;
	}
	
	/**
	 * 建造SQL语句
	 * 
	 * @param SQL
	 *            原始语句
	 * @param obj
	 *            替换语句中?的数值数组
	 * @return SQL语句
	 */
	public String buildSQL(String SQL, TParm parm) {
		SQL = SQL.trim();
		Object[] names = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			String name = (String) names[i];
			if (SQL.indexOf("<" + name.trim() + ">") == -1)
				continue;
			SQL = replace(SQL, "<" + name.trim() + ">", parm
					.getValue((String) names[i]));
		}
		return SQL;
	}

	public String replace(String s, String name, String value) {
		int index = s.indexOf(name);
		while (index >= 0) {
			s = s.substring(0, index) + value
					+ s.substring(index + name.length(), s.length());
			index = s.indexOf(name);
		}
		return s;
	}
}
