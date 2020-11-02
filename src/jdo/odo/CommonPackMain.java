package jdo.odo;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;

/**
 * 
 * <p>Title: ����ҽ������վ������������</p>
 * 
 * <p>Description:����ҽ������վ������������</p>
 * 
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * 
 * <p>Company:Javahis</p>
 * 
 * @author ehui 20090429
 * @version 1.0
 */
public class CommonPackMain extends StoreBase {
	/**
	 * ���ҡ�ҽʦ����
	 */
	private String deptOrDr;
	/**
	 * ���һ�ҽʦ����
	 */
	private String deptOrDrCode;
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
	 * Contructor
	 * @param deptOrdr String
	 * @param deptOrDrCode String
	 */
	public CommonPackMain(String deptOrdr,String deptOrDrCode){
		this.setDeptOrDr(deptOrdr);
		this.setDeptOrDrCode(deptOrDrCode);

	}
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
    	TParm parm=new TParm();
    	parm.setData("DEPT_OR_DR",this.getDeptOrDr());
    	parm.setData("DEPTORDR_CODE",this.getDeptOrDrCode());
        return OpdComPackQuoteTool.getInstance().initQuote(parm);
    }
    /**
     * �����ﲡ����ʼ��
     * @param odo ODO
     * @return boolean true:��ʼ���ɹ�,false:��ʼ��ʧ��
     */
    public boolean initOdo(ODO odo){
    	if(!this.onQuery())
    		return false;
    	int row=this.insertRow();
    	if(row<0)
    		return false;
    	if(odo==null)
    		return false;
    	this.setItem(row, "SUBJ_TEXT", odo.getSubjrec().getItemData(0, "SUBJ_TEXT"));
    	this.setItem(row, "OBJ_TEXT", odo.getSubjrec().getItemData(0, "OBJ_TEXT"));
    	this.setItem(row, "PHYSEXAM_REC", odo.getSubjrec().getItemData(0, "PHYSEXAM_REC"));
    	return true;
    }
    /**
     * ��������
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
//        if(newRow==-1)
//            return -1;
        //�����ż�ס��
        setItem(newRow,"DEPT_OR_DR",getDeptOrDr());
        setItem(newRow,"DEPTORDR_CODE",getDeptOrDrCode());
        setItem(newRow,"PACK_CODE",getNo());
        setActive(newRow, false);
//        setItem(newRow,"ORDER_DATE",super.getDBTime());
        return newRow;
    }
    /**
     * ͨ������ԭ��ȡ���µ�PACK_CODE
     * @return packCode String
     */
    public String getNo(){
		String packCode = SystemTool.getInstance().getNo("ALL", "ODO", "PACK",
		"PACK_CODE");
		return packCode;
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
        int rows[] = getModifiedRows(storeName);
        for(int i = 0;i < rows.length;i++)
        {
            setItem(rows[i],"OPT_USER",optUser,storeName);
            setItem(rows[i],"OPT_DATE",optDate,storeName);
            setItem(rows[i],"OPT_TERM",optTerm,storeName);
        }
        return true;
    }
}
