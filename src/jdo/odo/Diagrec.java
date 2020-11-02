package jdo.odo;

import java.sql.Timestamp;
import java.util.Vector;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

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
 * @author lzk 2009.2.11
 * @version 1.0
 */
public class Diagrec extends StoreBase{
    /**
     * �ż�ס��
     */
    private String admType;
    /**
     * ҽʦ
     */
    private String drCode;
    /**
     * ������ͣ�����ҽ��
     */
    private String icdType;
    //ȡ�������ע��
    private String GET_MAIN_FLG="SELECT * FROM SYS_DIAGNOSIS WHERE ICD_CODE='#'";
    TDataStore ICD = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
    /**
     * �����ż�ס��
     * @param admType String
     */
    public void setAdmType(String admType)
    {
        this.admType = admType;
    }
    /**
     * �õ��ż�ס��
     * @return String
     */
    public String getAdmType()
    {
        return admType;
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
     * ����ҽʦ
     * @param drCode String
     */
    public void setDrCode(String drCode)
    {
        this.drCode = drCode;
    }
    /**
     * �õ�ҽʦ
     * @return String
     */
    public String getDrCode()
    {
        return drCode;
    }
    /**
     * �õ�SQL
     * @return String
     */
    protected String getQuerySQL(){
        return "SELECT * FROM OPD_DIAGREC WHERE CASE_NO = '" + getCaseNo() + "' ORDER BY MAIN_DIAG_FLG DESC,FILE_NO";//modify caoy ��ICD_CODE ��ΪFILE_NO 2014,5,29
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
        setItem(newRow,"ADM_TYPE",getAdmType());
        setItem(newRow,"DR_CODE",getDrCode());
        setItem(newRow,"ICD_TYPE",getIcdType());
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
        int rows[] = getNewRows(storeName);
        for(int i = 0;i < rows.length;i++)
            setItem(rows[i],"ORDER_DATE",optDate,storeName);
        return super.setOperator(optUser,optDate,optTerm);
    }
//    /**
//     * �е�ֵ�ı�
//     */
//    public boolean setItem(int row,String column,Object value){
//    	super.setItem(row, column, value);
////    	if(itemNow)
////    		return true;
//    	if("ICD_CODE".equalsIgnoreCase(column)){
//
//    		//System.out.println("ICD_CHN_DESC"+parm.getValue("ICD_CHN_DESC",0));
//    		this.setItem(row, "ICD_DESC", parm.getValue("ICD_CHN_DESC",0));
//    		return true;
//    	}
//    	if("MAIN_DIAG_FLG".equalsIgnoreCase(column)){
//
//    	}
//
//    	return true;
//    }
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
    	if(code==null||code.size()<=0){
    		System.out.println("getOtherColumnValue.code is null");
    		return "";
    	}
    	String icdCode=parm.getValue("ICD_CODE",row);
		int rowNow=code.indexOf(icdCode);
		if(rowNow<0)
			return "";
    	if("ICD_DESC".equalsIgnoreCase(column)){
    		if("en".equalsIgnoreCase(Operator.getLanguage())){
    			return icdParm.getValue("ICD_ENG_DESC",rowNow);
    		}else{
    			return icdParm.getValue("ICD_CHN_DESC",rowNow);
    		}
    	}
    	if("ICD_ENG_DESC".equalsIgnoreCase(column)){
    		return	icdParm.getValue("ICD_ENG_DESC",rowNow);
    	}
    	return "";
    }
    /**
     * ���ݸ������ϴ��뷵��ֵ
     * @param icdCode String
     * @param LagType String ��������
     * @return String
     */
    public String getIcdDesc(String icdCode,String LagType){
        TParm icdParm=ICD.getBuffer(ICD.isFilter()?ICD.FILTER:ICD.PRIMARY);
        Vector code=(Vector)icdParm.getData("ICD_CODE");
        int rowNow=code.indexOf(icdCode);
        if("en".equals(LagType))
            return icdParm.getValue("ICD_ENG_DESC",rowNow);
        else
            return icdParm.getValue("ICD_CHN_DESC",rowNow);
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
     * ����codeֵ+�������
     * @return String
     */
    public String mainIcd(){
    	String result="";
    	int[] mainDiag=new int[]{0};
    	if(this.haveMainDiag(mainDiag)){
    		String code=this.getItemString(mainDiag[0], "ICD_CODE");
    		String desc=ICD.getItemString(ICD.find(code), "ICD_CHN_DESC");
    		result=code+"	"+desc;
    	}
    	return result;
    }
    /**
     * ȡ�������������
     * @return
     */
    public int getMainDiag(){
    	int count=this.rowCount();
    	int mainRow=-1;
    	for(int i=0;i<count;i++){
    		StringTool d;
    		boolean isMain=StringTool.getBoolean(this.getItemString(i, "MAIN_DIAG_FLG"));
    		if(isMain){
    			return i;
    		}
    	}
    	return -1;
    }
    /**
     * �жϸ����кŵ�����Ƿ�Ⱦ��
     * @param row
     * @return
     */
    public boolean isContagion(int row){
    	String sql="SELECT CHLR_FLG FROM SYS_DIAGNOSIS WHERE ICD_CODE='#'";
    	if(row<0){
    		return false;
    	}
    	String icdCode=this.getItemString(row, "ICD_CODE");
    	if(icdCode==null){
    		return false;
    	}
    	if(icdCode.trim().length()<1){
    		return false;
    	}
    	sql=sql.replaceFirst("#", icdCode);
    	//System.out.println("sql="+sql);
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	//System.out.println("result="+result);
    	if(result.getErrCode()!=0){
    		return false;
    	}
    	//System.out.println("chlr_flg="+result.getBoolean("CHLR_FLG",0));
    	return result.getBoolean("CHLR_FLG",0);
    }
    /**
     * ����ʱ������еĴ�Ⱦ����ϱ��TParm����
     * @return
     */
    public TParm getContagionParm(){
    	TParm result=new TParm();
    	int count=this.rowCount();
    	if(count<1){
    		return result;
    	}

    	return result;
    }
    /**
     * ���ݸ���ICD_CODE�õ������ע��
     * @param icdCode
     * @return
     */
    public boolean isMainFlg(String icdCode){
    	if(StringUtil.isNullString(icdCode)){
    		System.out.println("isMainFlg para is null");
    		return false;
    	}
    	String sql=GET_MAIN_FLG.replaceFirst("#", icdCode);
//    	System.out.println("sql="+sql);
    	TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getErrCode()!=0){
    		System.out.println("isMainFlg.err"+parm.getErrText());
    		return false;
    	}
//    	System.out.println("parm=="+parm);
    	return parm.getBoolean("MAIN_DIAG_FLG",0);
    }
    /**
     * ȡ��֢��ע��
     * @param icdCode
     * @return
     */
    public boolean isSyndromFlg(String icdCode){
    	if(StringUtil.isNullString(icdCode)){
    		return true;
    	}
    	TParm parm=new TParm(TJDODBTool.getInstance().select(GET_MAIN_FLG.replaceFirst("#", icdCode)));
    	if(parm.getErrCode()!=0){
    		System.out.println("isMainFlg.err"+parm.getErrText());
    		return true;
    	}
    	return parm.getBoolean("SYNDROME_FLG",0);
    }
}
