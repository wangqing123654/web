package jdo.hrm;

import jdo.reg.SessionTool;
import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
*
* <p>Title: ���������������</p>
*
* <p>Description: ���������������</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: javahis</p>
*
* @author ehui 20090922
* @version 1.0
*/
public class HRMClinicRoom extends TDataStore {
	private String clinicAreaCode;
	private String regionCode;
	private static TJDODBTool tool;
	private StringUtil util;
	private String id=Operator.getID();
	private String ip=Operator.getIP();
	//��ʼ������
	private static final String INIT_SQL="SELECT * FROM REG_CLINICROOM WHERE   REGION_CODE='#' AND CLINICAREA_CODE='#'  ORDER BY CLINICROOM_NO";
	private static final String INIT_HRM_SQL="SELECT * FROM REG_CLINICROOM WHERE REGION_CODE='#' AND CLINICAREA_CODE='#' AND ADM_TYPE='H'  ORDER BY CLINICROOM_NO";
	/**
	 *
	 */
	private static final String COMBO_SQL="SELECT CLINICROOM_NO ID,CLINICROOM_DESC NAME,PY1,PY2 FROM REG_CLINICROOM ORDER BY SEQ";
	private static final String GET_CLINICROOM=
		"SELECT CLINICROOM_NO " +
			" FROM HRM_SCHDAY " +
			" WHERE REGION_CODE='#' " +
			" AND ADM_DATE=TO_DATE('#','YYYYMMDD')" +
			" AND SESSION_CODE='#'" +
			" AND CLINIC_AREA='#'" +
			" AND DEPT_CODE='#'";
	//ȡ�ý���ʹ�������Ϣ
	private static final String GET_CLINIC_DATA=
		"SELECT A.* , B.CLINIC_DESC " +
			"FROM REG_CLINICROOM A,REG_CLINICAREA B " +
		"WHERE A.REGION_CODE='#' " +
		"	   AND A.ADM_TYPE='H' " +
		"	   AND A.CLINICAREA_CODE=B.CLINICAREA_CODE " +
		"	   AND A.ACTIVE_FLG='Y'" +
		"ORDER BY A.SEQ";
	/**
	 * ��ѯ
	 * @return
	 */
	public boolean onQuery(String clinicAreaCode,String regionCode){
		if(StringUtil.isNullString(clinicAreaCode)){
			return false;
		}
		String sql=INIT_SQL.replaceFirst("#", regionCode).replaceFirst("#", clinicAreaCode);
//		// System.out.println("room.sql"+sql);
		this.setSQL(sql);
		this.clinicAreaCode=clinicAreaCode;
		this.regionCode=regionCode;
		return (this.retrieve()<0);
	}
	/**
	 * ����
	 * @return
	 */
	public TParm onSave(){
		TParm result=new TParm();
		if(!this.isModified()){
			result.setErrCode(0);
			return result;
		}
		String sql[]=this.getUpdateSQL();
//		for(String temp:sql){
//			// System.out.println("sql="+temp);
//		}
		result=new TParm(tool.getInstance().update(sql));
		if(result.getErrCode()==0){
			this.resetModify();
		}
		return result;
	}
	/**
	 * ɾ�������µ��������
	 * @return
	 */
	public TParm deleteByAreaCode(){
		TParm result=new TParm();
		if(util.isNullString(this.clinicAreaCode)){
			return result;
		}
		int count=this.rowCount()-1;
		for(int i=count;i>-1;i--){
			this.deleteRow(i);
		}
		result=onSave();
		return result;
	}
	/**
	 * ����һ������
	 */
	public int newRow(int row){
		if(row==-1){
			row=super.insertRow();
			this.setItem(row, "OPT_USER", id);
			this.setItem(row, "OPT_TERM", ip);
			this.setItem(row, "OPT_DATE", this.getDBTime());
			this.setItem(row, "CLINICAREA_CODE", this.clinicAreaCode);
			this.setItem(row, "ADM_TYPE", "H");
			this.setItem(row, "ACTIVE_FLG", "Y");
			this.setItem(row, "REGION_CODE", Operator.getRegion());
			this.setItem(row, "SEQ", StringTool.getInt(this.getItemData(row, "#ID#")+"")+1);
			this.setActive(row, false);
			return row;
		}else{
			return -1;
		}
	}
	/**
	 * �ṩCOMBO����
	 * @return
	 */
	public static TParm getComboParm(){
		TParm result=new TParm(tool.getInstance().select(COMBO_SQL));
		return result;
	}
	/**
	 * ȡ������
	 * @return
	 */
	public String getClinicRoom(){
		String clinicRoom="";
		String sessionCode = SessionTool.getInstance().getDrSessionNow("O",Operator.getRegion());
		String sql=GET_CLINICROOM
		.replaceFirst("#", Operator.getRegion())
		.replaceFirst("#", StringTool.getString(this.getDBTime(),"yyyyMMdd"))
		.replaceFirst("#", sessionCode)
		.replaceFirst("#", Operator.getStation())
		.replaceFirst("#", Operator.getDept());
//		// System.out.println("getClinicRoom.sql=========="+sql);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		clinicRoom=result.getValue("CLINICROOM_NO",0);
		return clinicRoom;
	}
	/**
	 * ���ݵ�ǰ��¼���򣬵�¼������õ�ǰ��������б�
	 * @return
	 */
	public boolean onQuery(){
		String regionCode=Operator.getRegion();
		String areaCode=Operator.getStation();
		String sql=this.INIT_HRM_SQL.replaceFirst("#", regionCode).replaceFirst("#", areaCode);
//		// System.out.println("sql="+sql);
		this.setSQL(sql);
		this.retrieve();
		return true;
	}
	/**
	 * ȡ�ý���ʹ�������Ϣ
	 * @return
	 */
	public TParm getClinicRoomData(){
		TParm result=new TParm();
		String sql=GET_CLINIC_DATA.replaceFirst("#", Operator.getRegion());
		// System.out.println("getClinicRoomData.sql="+sql);
		result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			return null;
		}
		return result;
	}
}
