package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.javahis.util.StringUtil;
/**
 * 
 * <p>Title: ����ҽ������վ�����������</p>
 * 
 * <p>Description:����ҽ������վ�����������</p>
 * 
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * 
 * <p>Company:Javahis</p>
 * 
 * @author ehui 20090429
 * @version 1.0
 */
public class CommonPackDiag extends StoreBase {
	//����ڴ��
	TDataStore ICD = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
	/**
	 * ���ҡ�ҽʦ����
	 */
	private String deptOrDr;
	/**
	 * ���һ�ҽʦ����
	 */
	private String deptOrDrCode;
	/**
	 * ģ�����
	 */
	private String packCode;
	/**
	 * �õ����ҡ�ҽʦ����
	 * @return deptOrDr String
	 */
	public String getDeptOrDr() {
		return deptOrDr;
	}
	/**
	 * ���ÿ��ҡ�ҽʦ����
	 * @parm deptOrDr String
	 */
	public void setDeptOrDr(String deptOrDr) {
		this.deptOrDr=deptOrDr;
	}
	/**
	 * �õ����һ�ҽʦ����
	 * @return deptOrDrCode String
	 */
	public String getDeptOrDrCode() {
		return deptOrDrCode;
	}
	/**
	 * ���ÿ��һ�ҽʦ����
	 * @parm deptOrDrCode String
	 */
	public void setDeptOrDrCode(String deptOrDrCode) {
		this.deptOrDrCode=deptOrDrCode;
	}
	/**
	 * �õ�ģ�����
	 * @return packCode String
	 */
	public String getPackCode() {
		return packCode;
	}
	/**
	 * ����ģ�����
	 * @parm packCode String
	 */
	public void setPackCode(String packCode) {
		this.packCode=packCode;
	}
	/**
	 * Constructor
	 * @param deptOrDr
	 * @param deptOrDrCode
	 * @param packCode
	 */
	public CommonPackDiag(String deptOrDr,String deptOrDrCode,String packCode){
		this.setDeptOrDr(deptOrDr);
		this.setDeptOrDrCode(deptOrDrCode);
		this.setPackCode(packCode);

	}
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        return OpdComPackQuoteTool.INIT_DIAG_SQL+" WHERE PACK_CODE='" +packCode+"'";
    }
    /**
     * ��������
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
        //�����ż�ס��
        setItem(newRow,"DEPT_OR_DR",getDeptOrDr());
        setItem(newRow,"DEPTORDR_CODE",getDeptOrDrCode());
        setItem(newRow,"PACK_CODE",getPackCode());
//        setItem(newRow,"ORDER_DATE",super.getDBTime());
        return newRow;
    }
    /**
     * �õ�����������
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TParm parm,int row,String column)
    {
    	TParm icdParm=ICD.getBuffer(ICD.isFilter()?ICD.FILTER:ICD.PRIMARY);
    	Vector code=(Vector)icdParm.getData("ICD_CODE");
    	String icdCode=parm.getValue("ICD_CODE",row);
		int rowNow=code.indexOf(icdCode);
		if(rowNow<0)
			return "";
    	if("ICD_CHN_DESC".equalsIgnoreCase(column)){
    			return icdParm.getValue("ICD_CHN_DESC",rowNow);
    	}
    	if("ICD_ENG_DESC".equalsIgnoreCase(column)){
    		return	icdParm.getValue("ICD_ENG_DESC",rowNow);
    	}	
    	return "";
    }
    /**
     * ���ò����û�
     * @param optUser String �����û�
     * @param optDate Timestamp ����ʱ��
     * @param optTerm String ����IP
     * @return boolean true �ɹ� false ʧ��
     */
    public boolean setOperator(String optUser,Timestamp optDate,String optTerm)
    {
        String storeName = isFilter()?FILTER:PRIMARY;
        int rows[] = getNewRows(storeName);
        return super.setOperator(optUser,optDate,optTerm);
    }
    /**
     * �����Ƿ��и�������
     * @param code ����Ҫ���������
     * @return boolean true:���ڸ����;false:�����ڸ����
     */
    public boolean isHaveSameDiag(String code){
    	TParm parm=this.getBuffer(this.isFilter()?this.FILTER:this.PRIMARY);
    	Vector mainFlg=(Vector)parm.getData("ICD_CODE");
    	return mainFlg.indexOf(code)>-1;
    }
    /**
     * �ж��Ƿ��Ѿ��������
     * @return boolean ��true,�У�false,û��
     */
    public int isHavaMainDiag(){
    	TParm parm=this.getBuffer(this.isFilter()?this.FILTER:this.PRIMARY);
    	Vector mainFlg=(Vector)parm.getData("MAIN_DIAG_FLG");
    	return mainFlg.indexOf("Y");
    }
    /**
     * �����ﲡ����ʼ��
     * @param odo ODO
     * @return boolean true:��ʼ���ɹ�,false:��ʼ��ʧ��
     */
    public boolean initOdo(ODO odo){
    	if(!this.onQuery())
    		return false;
    	if(odo==null||odo.getDiagrec()==null||odo.getDiagrec().rowCount()<1)
    		return false;
    	Diagrec diag=odo.getDiagrec();
    	TParm diagParm=diag.getBuffer(diag.FILTER);
    	int count=diagParm.getCount();
    	for(int i=0;i<count;i++){
    		int row=this.insertRow();
    		if(row<0)
    			return false;
        	this.setItem(row, "ICD_TYPE", diagParm.getValue("ICD_TYPE",i));
        	this.setItem(row, "ICD_CODE", diagParm.getValue("ICD_CODE",i));
        	this.setItem(row, "MAIN_DIAG_FLG", diagParm.getValue("MAIN_DIAG_FLG",i));
        	this.setItem(row, "DIAG_NOTE", diagParm.getValue("DIAG_NOTE",i));
    	}
    	return true;
    }
    /**
     * ����ģ���ɾ��ģ��
     * @param packCode
     * @return
     */
    public boolean deletePack(String packCode){
    	if(StringUtil.isNullString(packCode)){
    		return false;
    	}
    	this.setFilter("PACK_CODE='" +packCode+
    			"'");
    	this.filter();
    	for(int i=this.rowCount()-1;i>-1;i--){
    		this.deleteRow(i);
    	}
    	return true;
    }
    /**
     * �����Ƿ��������
     * @return boolean true:�������,false:û�������
     */
    public boolean haveMainDiag(int[] i){
    	TParm parm=this.getBuffer(this.isFilter()?this.FILTER:this.PRIMARY);
    	Vector mainFlg=(Vector)parm.getData("MAIN_DIAG_FLG");
    	i[0]=mainFlg.indexOf("Y");
    	return i[0]>-1;
    }
}
