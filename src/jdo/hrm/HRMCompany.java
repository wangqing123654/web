package jdo.hrm;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;
/**
*
* <p>Title: ��������������</p>
*
* <p>Description: ��������������</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMCompany extends TDataStore{
	//��ʼ��SQL
	private static final String INIT="SELECT * FROM HRM_COMPANY ORDER BY COMPANY_CODE";
	//����PY����NAMEģ����ѯCODE
	private static final String GET_CODE_BY_NAME_OR_PY="SELECT COMPANY_CODE FROM HRM_COMPANY WHERE PY1 LIKE '%#%' OR COMPANY_DESC LIKE '%#%'";
	private String id=Operator.getID();
	private String ip=Operator.getIP();
	/**
	 * ��ѯ�¼�
	 * @return
	 */
	public int onQuery(){
		this.setSQL(INIT);
		return this.retrieve();
	}
	/**
	 * �½�һ����¼
	 * @return
	 */
	public boolean newCompany(TParm parm){
		String comCode = SystemTool.getInstance().getNo("ALL", "ODO", "COMPANY_NO","COMPANY_NO");
		if(StringUtil.isNullString(comCode)){
			return false;
		}
		int row=this.insertRow(-1);
		this.setItem(row, "COMPANY_CODE", comCode);
		this.setItem(row, "COMPANY_DESC", parm.getValue("COMPANY_DESC"));
		this.setItem(row, "PY1", parm.getValue("PY1"));
		this.setItem(row, "PY2", parm.getValue("PY2"));
		this.setItem(row, "DESCRIPTION", parm.getValue("DESCRIPTION"));
		this.setItem(row, "ADMINISTRATOR", parm.getValue("ADMINISTRATOR"));
		this.setItem(row, "TEL", parm.getValue("TEL"));
		this.setItem(row, "IND_TYPE_CODE", parm.getValue("IND_TYPE_CODE"));
		this.setItem(row, "FAX_NO", parm.getValue("FAX_NO"));
		this.setItem(row, "CONTACTS_NAME", parm.getValue("CONTACTS_NAME"));
		this.setItem(row, "CONTACTS_TEL", parm.getValue("CONTACTS_TEL"));
		this.setItem(row, "POST_CODE", parm.getValue("POST_CODE"));
		this.setItem(row, "ADDRESS", parm.getValue("ADDRESS"));
		this.setItem(row, "E_MAIL", parm.getValue("E_MAIL"));
		this.setItem(row, "COUNT", parm.getInt("COUNT"));
		this.setItem(row, "OPT_USER", id);
		this.setItem(row, "OPT_TERM", ip);
		this.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
		return true;
	}
	/**
	 * ����������Ϣ
	 * @param parm
	 * @return
	 */
	public boolean updateCompany(TParm parm){
		int row=parm.getInt("ROW");
		this.setItem(row, "COMPANY_DESC", parm.getValue("COMPANY_DESC"));
		this.setItem(row, "PY1", parm.getValue("PY1"));
		this.setItem(row, "PY2", parm.getValue("PY2"));
		this.setItem(row, "DESCRIPTION", parm.getValue("DESCRIPTION"));
		this.setItem(row, "ADMINISTRATOR", parm.getValue("ADMINISTRATOR"));
		this.setItem(row, "TEL", parm.getValue("TEL"));
		this.setItem(row, "IND_TYPE_CODE", parm.getValue("IND_TYPE_CODE"));
		this.setItem(row, "FAX_NO", parm.getValue("FAX_NO"));
		this.setItem(row, "CONTACTS_NAME", parm.getValue("CONTACTS_NAME"));
		this.setItem(row, "CONTACTS_TEL", parm.getValue("CONTACTS_TEL"));
		this.setItem(row, "POST_CODE", parm.getValue("POST_CODE"));
		this.setItem(row, "ADDRESS", parm.getValue("ADDRESS"));
		this.setItem(row, "E_MAIL", parm.getValue("E_MAIL"));
		this.setItem(row, "COUNT", parm.getInt("COUNT"));
		this.setItem(row, "OPT_USER", id);
		this.setItem(row, "OPT_TERM", ip);
		if(this.isModified()){
			this.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());	
		}
		
		return true;
	}
	/**
	 * ���ݸ�������������������Ϣ
	 * @param companyCode
	 * @return
	 */
	public boolean filt(String companyCode){
		if(StringUtil.isNullString(companyCode)){
			return false;
		}
		this.setFilter("COMPANY_CODE='" +companyCode+ "'");
		return this.filter();
	}
	/**
	 * ���ݸ������������ж��Ƿ�����������
	 * @param desc
	 * @return
	 */
	public boolean isHaveSaveDesc(String desc){
		if(StringUtil.isNullString(desc)){
			return true;
		}
		int count=this.rowCount();
		for(int i=0;i<count;i++){
			if(desc.equalsIgnoreCase(this.getItemString(i, "COMPANY_DESC"))){
				return true;
			}
		}
		return false;
	}
	/**
	 * �������ư�PY����COMPANY_CODEģ����ѯCODE
	 * @param companyCode
	 * @return
	 */
	public TParm queryCodeByName(String companyCode){
		//caowl 20130308 start �޸�ģ����ѯֻ��ѯһ��������
		TParm parm = new TParm();
		if(StringUtil.isNullString(companyCode)){
			return parm;
		}
		String sql=GET_CODE_BY_NAME_OR_PY.replaceFirst("#", companyCode).replaceFirst("#", companyCode);		
		parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getErrCode()!=0){
			return parm;
		}	
		//caowl 20130308 end
		return parm;
	}

}
