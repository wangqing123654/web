package jdo.sys;

import jdo.opd.ODO;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOObject;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.JavaHisDebug;

/**
*
* <p>Title: Operator���ṹ
*
* <p>Description: Operator���ṹ</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2008</p>
*
* <p>Company: javahis
*
* @author ehui 20081112
* @version 1.0
*/
public class OperatorDAO  extends TJDOObject  {
	/**
	 * �����б�
	 */
	private JDODeptList deptList;
	/**
	 * ֤���б�
	 */
	private JDOLicenceList licenseList;
	/**
	 * �û��б�
	 */
	private JDOOperatorList operatorList;
	/**
	 * ����վ�б�
	 */
	private JDOStationList stationList;
	public JDODeptList getDeptList(){
		return this.deptList;
	}
	public void setDeptList(JDODeptList deptList){
		this.deptList=deptList;
	}
	public JDOLicenceList getLicenseList(){
		return this.licenseList;
	}
	public void setLicenseList(JDOLicenceList licenseList){
		this.licenseList=licenseList;
	}
	public JDOOperatorList getOperatorList(){
		return this.operatorList;
	}
	public void setOperatorList(JDOOperatorList operatorList){
		this.operatorList=operatorList;
	}
	public JDOStationList getStationList(){
		return this.stationList;
	}
	public void setStationList(JDOStationList stationList){
		this.stationList=stationList;
	}
	public OperatorDAO(){
		this.setDeptList(new JDODeptList());
		this.setLicenseList(new JDOLicenceList());
		this.setOperatorList(new JDOOperatorList());
		this.setStationList(new JDOStationList());
	}
	/**
	 * ��ʼ������
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initJDODeptList(TParm parm) {
		if (parm == null)
			return false;
		return this.getDeptList().initParm(parm);
	}
	/**
	 * ��ʼ��֤��
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initJDOLicenceList(TParm parm) {
		if (parm == null)
			return false;
		return this.getLicenseList().initParm(parm);
	}
	/**
	 * ��ʼ���û�
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initJDOOperatorList(TParm parm) {
		if (parm == null)
			return false;

		return this.getOperatorList().initParm(parm);
	}
	/**
	 * ��ʼ������վ
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initJDOStationList(TParm parm) {
		if (parm == null)
			return false;
		return this.getStationList().initParm(parm);
	}
	/**
	 * ��ʼ������
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initParm(TParm parm) {
		if (parm == null)
			return false;
		//��ʼ���û�
		if (!initJDOOperatorList(parm.getParm("Operator")))
			return false;
		return true;
	}
	/**
	 * ��ʼ������
	 * @param parm TParm
	 * @return boolean �棺�ɹ����٣�ʧ��
	 */
	public boolean initParmForOperator(TParm parm){
		if (parm == null)
			return false;
		//��ʼ������
		if (!initJDODeptList(parm.getParm("Dept")))
			return false;
		//��ʼ��֤��
		if (!initJDOLicenceList(parm.getParm("License")))
			return false;
		//��ʼ������վ
		if (!initJDOStationList(parm.getParm("Station")))
			return false;
		return true;
	}
	/**
	 * �õ�ȫ������
	 * @return TParm
	 */
	public TParm getParm() {
		TParm result = new TParm();
		//�������߿�������
		result.setData("Dept", this.getDeptList().getParm().getData());
		//����ҽ������
		result.setData("License", this.getLicenseList().getParm().getData());
		//���ع���ʷ����
		result.setData("Operator",this.getOperatorList().getParm().getData());
		//���ؼ���ʷ����
		result.setData("Station", this.getStationList().getParm()
				.getData());
		return result;
	}
	/**
	 * ����
	 * @return TParm
	 */
	public TParm onSave() {
		TParm parm = getParm();
		TParm result = TIOM_AppServer.executeAction("action.sys.SYSOperatorAction",
				"onSave", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		reset();
		return result;
	}
	/**
	 * ��ѯ
	 * @param caseNo String
	 */
	public boolean onQuery(TParm parm) {
		reset();
		TParm result = TIOM_AppServer.executeAction("action.sys.SYSOperatorAction",
				"onQuery", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		if (!initParm(result)) {
			err("��������ʧ��");
			return false;
		}
		return true;
	}
	/**
	 * Ϊ��֪���û���ѯ�ܱ���Ϣ
	 * @param caseNo String
	 */
	public boolean onQueryForOperator(TParm parm){
		this.setDeptList(new JDODeptList());
		this.setLicenseList(new JDOLicenceList());
		this.setStationList(new JDOStationList());
		TParm result = TIOM_AppServer.executeAction("action.sys.SYSOperatorAction",
				"onQueryForOperator", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		if (!initParmForOperator(result)) {
			err("��������ʧ��");
			return false;
		}
		return true;
	}
	public void reset()
	{
		this.getDeptList().reset();
		this.getLicenseList().reset();
		this.getOperatorList().reset();
		this.getStationList().reset();
	}
	public static void main(String args[]) {
		JavaHisDebug.initClient();
		OperatorDAO dao=new OperatorDAO();
		TParm parm=new TParm();

		dao.onQuery(parm);
		parm.setData("USER_ID","admin");
		dao.onQueryForOperator(parm);
		//System.out.println(dao.getDeptList().getParm(dao.getDeptList().PRIMARY));
		//System.out.println(dao.getStationList().getParm(dao.getStationList().PRIMARY));
		//System.out.println(dao.getLicenseList().getParm(dao.getLicenseList().PRIMARY));
		JDOOperatorList op=dao.getOperatorList();
		JDODeptList list=dao.getDeptList();
		JDOStationList slist=dao.getStationList();
		//System.out.println(slist.getParm(dao.getStationList().PRIMARY));
		for(int i=0;i<slist.size();i++){
			JDOStation opl=slist.getJDOStation(i);
//			System.out.println(opl.getDeptCode()+"i"+i+"");
		}
	}

}
