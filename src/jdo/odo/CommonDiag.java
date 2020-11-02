package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;

/**
*
* <p>Title: ��ϴ洢����</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author ehui 2009.2.11
* @version 1.0
*/
public class CommonDiag extends StoreBase{

    /**
     * ����ҽʦ
     */
    private String deptOrDr;
    /**
     * ����
     */
    private String deptOrdrCode;
    /**
     * ������ͣ�����ҽ��
     */
    private String icdType;
    
    TDataStore ICD = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
    
    public CommonDiag(String deptOrDr,String deptOrdrCode){
    	this.setDeptOrDr(deptOrDr);
    	this.setDeptOrdrCode(deptOrdrCode);
    	this.onQuery();
    }
    /**
     * ���ÿ��ҡ�ҽʦ���
     * @param deptOrDr
     */
    public void setDeptOrDr(String deptOrDr){
    	this.deptOrDr=deptOrDr;
    }
    /**
     * �õ����ҡ�ҽʦ���
     * @return deptOrDr String
     */
    public String getDeptOrDr(){
    	return this.deptOrDr;
    }
    /**
     * ���ô���
     * @param deptOrdrCode
     */
    public void setDeptOrdrCode(String deptOrdrCode){
    	this.deptOrdrCode=deptOrdrCode;
    }
    /**
     * �õ�����
     * @return
     */
    public String getDeptOrdrCode(){
    	return this.deptOrdrCode;
    }
    /**
     * �����������
     * @param icdType �������
     */
    public void setIcdType(String icdType){
    	this.icdType=icdType;
    }
    /**
     * �õ��������
     */
    public String getIcdType(){
    	return this.icdType;
    }
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM OPD_COMDIAG WHERE DEPT_OR_DR = '" + this.getDeptOrDr() + "' AND DEPTORDR_CODE='" +this.getDeptOrdrCode()+	"' ORDER BY SEQ";
    }
    /**
     * ��������
     * @param row int
     * @return int
     */
    public int insertRow(int row){
        int newRow = super.insertRow(row);
        if(newRow==-1)
            return -1;
        //�����ż�ס��
        setItem(newRow,"DEPT_OR_DR",getDeptOrDr());
        setItem(newRow,"DEPTORDR_CODE",getDeptOrdrCode());
        setItem(newRow,"ICD_TYPE",getIcdType());
        setItem(newRow,"SEQ",getMaxSeq());
        setActive(newRow, false);
//        setItem(newRow,"ORDER_DATE",super.getDBTime());
        return newRow;
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
        return super.setOperator(optUser,optDate,optTerm);
    }
    /**
     * �������SEQ_NO
     * @return int
     */
    public int getMaxSeq(){
    	
    	if(this.rowCount()<=1){
    		return 1;
    	}
    	return this.getItemInt(this.rowCount()-2, "SEQ")+1;
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
		if(rowNow<0){
			return "";
		}
			
    	if("ICD_CHN_DESC".equalsIgnoreCase(column)){
    		Operator d;
    		if("en".equalsIgnoreCase(Operator.getLanguage())){
    			return icdCode+"  "+ icdParm.getValue("ICD_ENG_DESC",rowNow);
    		}else{
    			return icdCode+"  "+ icdParm.getValue("ICD_CHN_DESC",rowNow);
    		}
    			
    			
    	}
    	return "";
    }
//    public setOtherColumnValue(TParm parm,int row,String column , Objecdt value)
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


}
